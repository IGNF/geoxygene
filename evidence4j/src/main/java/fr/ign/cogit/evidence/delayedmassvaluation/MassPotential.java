package fr.ign.cogit.evidence.delayedmassvaluation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fr.ign.cogit.evidence.configuration.ConfigurationSet;
import fr.ign.cogit.evidence.variable.VariableSet;

/**
 * Implementation of a mass potential using the delayed mass valuation principle.
 * @author Julien Perret
 * @param <T>
 * @param <E>
 */
public class MassPotential<T, E> {

  private VariableSet<T> variableSet;
  private Map<ConfigurationSet<T>, MassFunctor<E>> focalElements;

  public Set<ConfigurationSet<T>> getConfigurationSets() {
    return focalElements.keySet();
  }

  private int conflict;

  public MassPotential(VariableSet<T> d) {
    this.variableSet = d;
    this.focalElements = new HashMap<ConfigurationSet<T>, MassFunctor<E>>();
    this.conflict = 0;
  }

  public void add(ConfigurationSet<T> f, MassFunctor<E> m) {
    assert (f.getVariableSet().equals(this.variableSet));
    MassFunctor<E> v = this.focalElements.get(f);
    if (v != null) {
      ++conflict;
      this.focalElements.put(f, new MassPlus<E>(v, m));
    } else {
      this.focalElements.put(f, m);
    }
  }

  public void check(E event) {
    double cumul = 0;
    for (Entry<ConfigurationSet<T>, MassFunctor<E>> foc : this.focalElements.entrySet()) {
      cumul += foc.getValue().evaluate(event);
    }
    // FIXME define a tolerance
    assert (Math.abs(cumul - 1.0) < 0.001);
  }

  public MassPotential<T, E> combination(MassPotential<T, E> p) {
    Map<ConfigurationSet<T>, MassFunctor<E>> focal1 = new HashMap<ConfigurationSet<T>, MassFunctor<E>>();
    Map<ConfigurationSet<T>, MassFunctor<E>> focal2 = new HashMap<ConfigurationSet<T>, MassFunctor<E>>();
    VariableSet<T> varSet = this.variableSet.union(p.variableSet);
    MassPotential<T, E> result = new MassPotential<T, E>(varSet);
    for (Entry<ConfigurationSet<T>, MassFunctor<E>> foc : this.focalElements.entrySet()) {
      focal1.put(foc.getKey().getExtended(varSet), foc.getValue());
    }
    for (Entry<ConfigurationSet<T>, MassFunctor<E>> foc : p.focalElements.entrySet()) {
      focal2.put(foc.getKey().getExtended(varSet), foc.getValue());
    }
    for (Entry<ConfigurationSet<T>, MassFunctor<E>> foc1 : focal1.entrySet()) {
      for (Entry<ConfigurationSet<T>, MassFunctor<E>> foc2 : focal2.entrySet()) {
        result.add(foc1.getKey().and(foc2.getKey()),
            new MassTimes<E>(foc1.getValue(), foc2.getValue()));
      }
    }
    return result.norm();
  }

  private MassPotential<T, E> norm() {
    ConfigurationSet<T> empty = new ConfigurationSet<T>(this.variableSet);
    MassFunctor<E> v = this.focalElements.get(empty);
    if (v == null) {
      return this;
    }
    MassFunctor<E> complement = new MassComplement<E>(v);
    for (Entry<ConfigurationSet<T>, MassFunctor<E>> foc : this.focalElements.entrySet()) {
      foc.setValue(new MassDivide<E>(foc.getValue(), complement));
    }
    this.focalElements.remove(empty);
    return this;
  }

  public double bel(ConfigurationSet<T> f, E event) {
    double result = 0;
    for (Entry<ConfigurationSet<T>, MassFunctor<E>> foc : this.focalElements.entrySet()) {
      if (foc.getKey().isSubSetOf(f)) {
        result += foc.getValue().evaluate(event);
      }
    }
    assert (result <= 1);
    return result;
  }

  public double pls(ConfigurationSet<T> ffinal, E event) {
    double result = 0;
    for (Entry<ConfigurationSet<T>, MassFunctor<E>> foc : this.focalElements.entrySet()) {
      if (foc.getKey().intersects(ffinal)) {
        result += foc.getValue().evaluate(event);
      }
    }
    assert (result <= 1);
    return result;
  }

  public double dou(ConfigurationSet<T> ffinal, E event) {
    return 1. - this.pls(ffinal, event);
  }

  public double com(ConfigurationSet<T> ffinal, E event) {
    double result = 0;
    for (Entry<ConfigurationSet<T>, MassFunctor<E>> foc : this.focalElements.entrySet()) {
      if (ffinal.isSubSetOf(foc.getKey())) {
        result += foc.getValue().evaluate(event);
      }
    }
    assert (result <= 1);
    return result;
  }

  public double ign(ConfigurationSet<T> ffinal, E event) {
    double result = this.pls(ffinal, event) - this.bel(ffinal, event);
    assert (result >= 0);
    return result;
  }

  @Override
  public String toString() {
    String result = "";
    for (Entry<ConfigurationSet<T>, MassFunctor<E>> foc : this.focalElements.entrySet()) {
      result += foc.getKey() + " -> " + foc.getValue() + "\n";
    }
    return result;
  }
}
