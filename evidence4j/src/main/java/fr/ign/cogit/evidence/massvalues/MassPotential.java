package fr.ign.cogit.evidence.massvalues;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import fr.ign.cogit.evidence.configuration.Configuration;
import fr.ign.cogit.evidence.configuration.ConfigurationSet;
import fr.ign.cogit.evidence.iterator.PowerSetIterator;
import fr.ign.cogit.evidence.variable.VariableSet;

/**
 * A Potential represents a source of information.
 * <p>
 * TODO make the tolerance configurable.
 * @author Julien Perret
 * @param <T>
 */
public class MassPotential<T> {

  private VariableSet<T> variableSet;
  private Map<ConfigurationSet<T>, Double> focalElements;
  private int conflict;
  private double tolerance = 0.001;

  /**
   * @return number of focal elements
   */
  public int size() {
    return this.focalElements.size();
  }

  /**
   * Construct an empty mass potential.
   * @param d
   *        a variable set
   */
  public MassPotential(VariableSet<T> d) {
    this.variableSet = d;
    this.focalElements = new HashMap<ConfigurationSet<T>, Double>();
    this.conflict = 0;
  }

  /**
   * Add a mass value for the given configuration set. If the focal element already exists, mass
   * values are summed and the number of conflicts is incremented.
   * @param f
   *        a configuration set
   * @param m
   *        a mass value
   */
  public void add(ConfigurationSet<T> f, double m) {
    // System.out.println("add " + (f.getVariableSet() == this.variableSet));
    assert (f.getVariableSet().equals(this.variableSet));
    Double v = this.focalElements.get(f);
    // System.out.println("ADD " + f);
    if (v != null) {
      ++conflict;
      // System.out.println("FOUND");
      this.focalElements.put(f, v.doubleValue() + m);
    } else {
      // System.out.println("NOT FOUND");
      this.focalElements.put(f, m);
    }
  }

  /**
   * Check if the sum of the mass values is equal to one (with a certain tolerance).
   */
  public void check() {
    double cumul = 0;
    // System.out.println("ckeck = " + cumul);
    for (Entry<ConfigurationSet<T>, Double> foc : this.focalElements.entrySet()) {
      cumul += foc.getValue().doubleValue();
    }
    // System.out.println("ckeck = " + cumul);
    // FIXME define a tolerance
    assert (Math.abs(cumul - 1.0) < this.tolerance);
  }

  /**
   * Combine 2 mass potentials. By default, the result will be normalized.
   * @param p
   *        another mass potential
   * @return the combination of the 2 mass potentials
   */
  public MassPotential<T> combination(MassPotential<T> p) {
    return this.combination(p, true);
  }

  /**
   * Combine 2 mass potentials.
   * <p>
   * TODO make the tolerance used for the focal elements not to grow too much configurable.
   * @param p
   *        another mass potential
   * @param normalize
   *        if true, normalize the result
   * @return the combination of the 2 mass potentials
   */
  public MassPotential<T> combination(MassPotential<T> p, boolean normalize) {
    Map<ConfigurationSet<T>, Double> focal1 = new HashMap<ConfigurationSet<T>, Double>();
    Map<ConfigurationSet<T>, Double> focal2 = new HashMap<ConfigurationSet<T>, Double>();
    VariableSet<T> varSet = (this.variableSet == p.variableSet) ? this.variableSet
        : this.variableSet.union(p.variableSet);
    MassPotential<T> result = new MassPotential<T>(varSet);
    for (Entry<ConfigurationSet<T>, Double> foc : this.focalElements.entrySet()) {
      focal1.put(foc.getKey().getExtended(varSet), foc.getValue());
    }
    for (Entry<ConfigurationSet<T>, Double> foc : p.focalElements.entrySet()) {
      focal2.put(foc.getKey().getExtended(varSet), foc.getValue());
    }
    // System.out.println("combination");
    for (Entry<ConfigurationSet<T>, Double> foc1 : focal1.entrySet()) {
      for (Entry<ConfigurationSet<T>, Double> foc2 : focal2.entrySet()) {
        // System.out.println(foc1.getKey().and(foc2.getKey()) + " = " + foc1.getValue() *
        // foc2.getValue());
        double comb = foc1.getValue() * foc2.getValue();
        if (comb > 0.00001) { // 0.0001
          result.add(foc1.getKey().and(foc2.getKey()), comb);
        }
      }
    }
    focal1.clear();
    focal2.clear();
    return normalize ? result.norm() : result;
  }

  /**
   * Combine a set of mass potentials.
   * @param potentialSet
   *        a set of mass potentials
   * @param normalize
   *        if true, normalize the results
   * @return a combination of all mass potentials
   */
  public static <T> MassPotential<T> combination(Set<MassPotential<T>> potentialSet,
      boolean normalize) {
    assert (!potentialSet.isEmpty());
    MassPotential<T> result = null;
    // int i = 0;
    for (MassPotential<T> p : potentialSet) {
      // System.out.println("combining " + (i++));
      result = (result == null) ? p : result.combination(p, normalize);
      // System.out.println("MP\n" + result);
    }
    return result;
  }

  public MassPotential<T> combinationForPatiallyOverlappingFrames(MassPotential<T> p,
      boolean normalize) {
    Map<ConfigurationSet<T>, Double> focal1 = new HashMap<ConfigurationSet<T>, Double>();
    Map<ConfigurationSet<T>, Double> focal2 = new HashMap<ConfigurationSet<T>, Double>();
    VariableSet<T> varSet = this.variableSet.union(p.variableSet);
    MassPotential<T> result = new MassPotential<T>(varSet);
    for (Entry<ConfigurationSet<T>, Double> foc : this.focalElements.entrySet()) {
      focal1.put(foc.getKey().getExtended(varSet), foc.getValue());
    }
    for (Entry<ConfigurationSet<T>, Double> foc : p.focalElements.entrySet()) {
      focal2.put(foc.getKey().getExtended(varSet), foc.getValue());
    }
    // System.out.println("combination");
    for (Entry<ConfigurationSet<T>, Double> foc1 : focal1.entrySet()) {
      for (Entry<ConfigurationSet<T>, Double> foc2 : focal2.entrySet()) {
        // System.out.println(foc1.getKey() + " + " + foc2.getKey());
        double comb = foc1.getValue() * foc2.getValue();
        if (comb > 0) {
          result.add(foc1.getKey().and(foc2.getKey()), comb);
        }
      }
    }
    ConfigurationSet<T> sigma1 = new ConfigurationSet<T>(varSet);
    for (Entry<ConfigurationSet<T>, Double> foc1 : focal1.entrySet()) {
      sigma1 = sigma1.or(foc1.getKey());
    }
    ConfigurationSet<T> sigma2 = new ConfigurationSet<T>(varSet);
    for (Entry<ConfigurationSet<T>, Double> foc2 : focal2.entrySet()) {
      sigma2 = sigma2.or(foc2.getKey());
    }
    ConfigurationSet<T> sigma = new ConfigurationSet<T>(varSet);
    sigma = sigma1.and(sigma2);
    // ConfigurationSet<T> sigmaUnion = new ConfigurationSet<T>(varSet);
    // sigmaUnion = sigma1.or(sigma2);
    // System.out.println("SIGMA  = " + sigma);
    // System.out.println("SIGMA1 = " + sigma1);
    // System.out.println("SIGMA2 = " + sigma2);
    // System.out.println("SIGMAUNION  = " + sigmaUnion);
    MassPotential<T> conditioned1 = new MassPotential<T>(varSet);
    MassPotential<T> conditioned2 = new MassPotential<T>(varSet);
    for (Entry<ConfigurationSet<T>, Double> foc : focal1.entrySet()) {
      conditioned1.add(foc.getKey().and(sigma), foc.getValue());
    }
    for (Entry<ConfigurationSet<T>, Double> foc : focal2.entrySet()) {
      conditioned2.add(foc.getKey().and(sigma), foc.getValue());
    }
    MassPotential<T> combination = new MassPotential<T>(varSet);
    for (Entry<ConfigurationSet<T>, Double> foc1 : focal1.entrySet()) {
      for (Entry<ConfigurationSet<T>, Double> foc2 : focal2.entrySet()) {
        // System.out.println(foc1.getKey() + " + " + foc2.getKey());
        ConfigurationSet<T> a = foc1.getKey().or(foc2.getKey());
        if (combination.focalElements.get(a) != null) {
          continue;
        }
        // System.out.println("a = " + a);
        ConfigurationSet<T> a1 = a.and(sigma1);
        // System.out.println("a1 = " + a1);
        ConfigurationSet<T> a2 = a.and(sigma2);
        // System.out.println("a2 = " + a2);
        ConfigurationSet<T> a0 = a.and(sigma);
        // System.out.println("a0 = " + a0);
        ConfigurationSet<T> a12 = a1.and(a2);
        // System.out.println("a12 = " + a12);
        Double m1a1 = focal1.get(a1);
        // if (m1a1 == null)
        // continue;
        Double m2a2 = focal2.get(a2);
        // if (m2a2 == null)
        // continue;
        double m1a0 = conditioned1.mass(a0);
        // if (m1a0 == null)
        // continue;
        double m2a0 = conditioned2.mass(a0);
        // if (m2a0 == null)
        // continue;
        double m12a12 = result.mass(a12);
        // System.out
        // .println("Masses = " + m1a1 + " " + m2a2 + " " + m1a0 + " " + m2a0 + " " + m12a12);
        if (m12a12 == 0 || m1a1 == null || m2a2 == null || m1a0 == 0 || m2a0 == 0)
          continue;
        double comb = m1a1 * m2a2 * m12a12 / (m1a0 * m2a0);
        // System.out.println("comb = " + comb);
        if (comb > 0) {
          combination.add(a, comb);
        }
      }
    }
    return normalize ? combination.norm() : combination;
  }

  /**
   * Normalize the mass potential.
   * @return the normalized mass potential
   */
  public MassPotential<T> norm() {
    ConfigurationSet<T> empty = new ConfigurationSet<T>(this.variableSet);
    Double v = this.focalElements.get(empty);
    // if (v == null) {
    // return this;
    // }
    if (v != null) {
      double complement = 1 - v.doubleValue();
      for (Entry<ConfigurationSet<T>, Double> foc : this.focalElements.entrySet()) {
        foc.setValue(foc.getValue().doubleValue() / complement);
      }
      this.focalElements.remove(empty);
    }
    double sum = 0;
    for (Entry<ConfigurationSet<T>, Double> foc : this.focalElements.entrySet()) {
      sum += foc.getValue().doubleValue();
    }
    for (Entry<ConfigurationSet<T>, Double> foc : this.focalElements.entrySet()) {
      foc.setValue(foc.getValue().doubleValue() / sum);
    }
    return this;
  }

  /**
   * Weaken the mass potential.
   * @param confidence
   *        a confidence value
   * @return the weakened mass potential
   */
  public MassPotential<T> weaken(double confidence) {
    for (Entry<ConfigurationSet<T>, Double> foc : this.focalElements.entrySet()) {
      foc.setValue(foc.getValue().doubleValue() * confidence);
    }
    ConfigurationSet<T> frame = new ConfigurationSet<T>(this.variableSet);
    frame.addAllConfigurations();
    Double v = this.focalElements.get(frame);
    this.focalElements.put(frame, ((v == null) ? 0 : v.doubleValue()) + (1 - confidence));
    return this;
  }

  public double bel(ConfigurationSet<T> f) {
    double result = 0;
    for (Entry<ConfigurationSet<T>, Double> foc : this.focalElements.entrySet()) {
      if (foc.getKey().getNumberOfConfiguration() > 0 && foc.getKey().isSubSetOf(f)) {
        result += foc.getValue().doubleValue();
      }
    }
    assert (result <= 1);
    return result;
  }

  /**
   * Plausibility.
   * @param f
   * @return
   */
  public double pls(ConfigurationSet<T> f) {
    double result = 0;
    for (Entry<ConfigurationSet<T>, Double> foc : this.focalElements.entrySet()) {
      if (foc.getKey().getNumberOfConfiguration() > 0 && foc.getKey().intersects(f)) {
        result += foc.getValue().doubleValue();
      }
    }
    assert (result <= 1);
    return result;
  }

  /**
   * Doubt.
   * @param f
   * @return
   */
  public double dou(ConfigurationSet<T> f) {
    return 1. - this.pls(f);
  }

  /**
   * Commonality.
   * @param f
   * @return
   */
  public double com(ConfigurationSet<T> f) {
    double result = 0;
    for (Entry<ConfigurationSet<T>, Double> foc : this.focalElements.entrySet()) {
      if (foc.getKey().getNumberOfConfiguration() > 0 && f.isSubSetOf(foc.getKey())) {
        result += foc.getValue().doubleValue();
      }
    }
    assert (result <= 1);
    return result;
  }

  /**
   * Ignorance.
   * @param f
   * @return
   */
  public double ign(ConfigurationSet<T> f) {
    double result = this.pls(f) - this.bel(f);
    assert (result >= 0);
    return result;
  }

  /**
   * Mass value for the given configuration set.
   * @param f
   * @return
   */
  public double mass(ConfigurationSet<T> f) {
    Double result = this.focalElements.get(f);
    if (result == null) {
      return 0;
    }
    assert (result.doubleValue() >= 0);
    return result.doubleValue();
  }

  /**
   * Pignistic probability.
   * @param ffinal
   * @return
   */
  public double pignistic(ConfigurationSet<T> f) {
    double result = 0d;
    for (int i = 0; i < f.size(); i++) {
      if (f.hasConfiguration(i)) {
        result += this.pignistic(f.getConfiguration(i));
      }
    }
    assert (result <= 1.0);
    return result;
  }

  /**
   * Pignistic probability.
   * @param f
   * @return
   */
  public double pignistic(Configuration<T> f) {
    double result = 0d;
    ConfigurationSet<T> empty = new ConfigurationSet<T>(this.variableSet);
    Double v = this.focalElements.get(empty);
    double mempty = (v == null) ? 0d : v.doubleValue();
    for (Entry<ConfigurationSet<T>, Double> foc : this.focalElements.entrySet()) {
      if (foc.getKey().has(f)) {
        int cardinal = foc.getKey().getNumberOfConfiguration();
        result += foc.getValue().doubleValue() / (cardinal * (1d - mempty));
      }
    }
    assert (result <= 1.0);
    return result;
  }

  @Override
  public String toString() {
    String result = "MP WITH " + this.focalElements.size() + " FOCAL ELEMENTS\n";
    for (Entry<ConfigurationSet<T>, Double> foc : this.focalElements.entrySet()) {
      result += foc.getValue().toString() + " FOR " + foc.getKey().toString() + "\n";
    }
    return result;
  }

  /**
   * Make a decision.
   * @param onSingles
   *        if true, only chose among singles
   * @return
   */
  public ConfigurationSet<T> decide(boolean onSingles) {
    return this.maxPignistic(onSingles);
  }

  /**
   * Choose the solution with the maximum pignistic probability.
   * @param onSingles
   * @return
   */
  public ConfigurationSet<T> maxPignistic(boolean onSingles) {
    double maxpig = 0d;
    // for (ConfigurationSet<T> conf : this.focalElements.keySet()) {
    // if (!onSingles || (onSingles && conf.getNumberOfConfiguration() == 1)) {
    // double pignistic = this.pignistic(conf);
    // System.out.println("pignistic = " + pignistic + " FOR " + conf);
    // if (pignistic > maxpig) {
    // maxpig = pignistic;
    // maxpignistic = conf;
    // }
    // }
    // }
    if (onSingles) {
      Configuration<T> maxpignistic = null;
      ConfigurationSet<T> conf = new ConfigurationSet<T>(this.variableSet);
      conf.addAllConfigurations();
      for (int i = 0; i < conf.getNumberOfConfiguration(); i++) {
        Configuration<T> c = conf.getConfiguration(i);
        double pig = this.pignistic(c);
        // System.out.println("pignistic = " + pig + " FOR " + c);
        if (pig > maxpig) {
          maxpig = pig;
          maxpignistic = c;
        }
      }
      conf.clear();
      if (maxpignistic != null) {
        conf.add(maxpignistic);
      }
      return conf;
    }
    ConfigurationSet<T> maxpignistic = null;
    PowerSetIterator<T> iterator = new PowerSetIterator<T>(this.variableSet);
    while (iterator.hasNext()) {
      ConfigurationSet<T> conf = iterator.next();
      double pig = this.pignistic(conf);
      if (pig > maxpig) {
        maxpig = pig;
        maxpignistic = conf;
      }
      // System.out.println("pignistic = " + pig + " FOR " + conf);
    }
    return maxpignistic;
  }
}