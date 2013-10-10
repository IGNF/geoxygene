package fr.ign.cogit.evidence.configuration;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import fr.ign.cogit.evidence.variable.Variable;
import fr.ign.cogit.evidence.variable.VariableSet;

public class ConfigurationSet<T> {
  private VariableSet<T> variableSet;
  private BitSet configurations;

  public ConfigurationSet(VariableSet<T> vs) {
    this.variableSet = new VariableSet<T>(vs);
    this.configurations = new BitSet(vs.getNumberOfConfigurations());
  }

  public ConfigurationSet(VariableSet<T> vs, BitSet confs) {
    this.variableSet = new VariableSet<T>(vs);
    this.configurations = confs;
  }

  public ConfigurationSet(ConfigurationSet<T> focalElement) {
    this.variableSet = new VariableSet<T>(focalElement.variableSet);
    this.configurations = (BitSet) focalElement.configurations.clone();
  }

  public VariableSet<T> getVariableSet() {
    return this.variableSet;
  }

  public void add(Configuration<T> config) {
    this.configurations.set(this.getIndex(config));
  }

  public void remove(Configuration<T> config) {
    this.configurations.clear(this.getIndex(config));
  }

  private int getIndex(Configuration<T> config) {
    int res = 0;
    int currentVariable = 0;
    for (Integer value : config.getValues()) {
      int pi = 1;
      for (int i = currentVariable + 1; i < config.getValues().size(); i++) {
        pi *= this.variableSet.getNthVariable(i).size();
      }
      res += value.doubleValue() * pi;
      currentVariable++;
    }
    return res;
  }

  public void addAllConfigurations() {
    this.configurations.set(0, this.variableSet.getNumberOfConfigurations());
  }

  public ConfigurationSet<T> and(ConfigurationSet<T> f) {
    assert (variableSet.equals(f.variableSet));
    BitSet confs = (BitSet) this.configurations.clone();
    confs.and(f.configurations);
    return new ConfigurationSet<T>(this.variableSet, confs);
  }

  public ConfigurationSet<T> or(ConfigurationSet<T> f) {
    assert (variableSet.equals(f.variableSet));
    BitSet confs = (BitSet) this.configurations.clone();
    confs.or(f.configurations);
    return new ConfigurationSet<T>(this.variableSet, confs);
  }

  public ConfigurationSet<T> getExtended(VariableSet<T> s) {
    assert (this.variableSet.isSubsetOf(s));
    if (this.variableSet.equals(s)) {
      return this;
    }
    ConfigurationSet<T> result = new ConfigurationSet<T>(this);
    for (int u = 0; u < s.size(); u++) {
      if (!this.variableSet.hasVariable(u) && s.hasVariable(u)) {
        result.applyExtension(this.variableSet.getVariable(u));
      }
    }
    return result;
  }

  private ConfigurationSet<T> applyExtension(Variable<T> variable) {
    if (this.variableSet.hasVariable(variable)) {
      return this;
    }
    VariableSet<T> vs = new VariableSet<T>(this.variableSet);
    vs.add(variable);
    ConfigurationSet<T> result = new ConfigurationSet<T>(vs);
    Integer r = variable.getRank();
    int u = (r.intValue() > 0) ? this.variableSet.getNumberOfConfigurations(0, r - 1) : 1;
    int v = this.variableSet.getNumberOfConfigurations(r + 1, this.variableSet.getMaxRank());
    for (int k = 0; k < u; k++) {
      result.deposit(v, k * variable.size() * v, this.extractAndReset(v, k * v));
    }
    for (int k = 0; k < variable.size() - 1; k++) {
      BitSet shiftLeft = new BitSet(result.configurations.size() + 1);
      for (int i = 0; i < result.configurations.size(); i++) {
        shiftLeft.set(i + v, result.configurations.get(i));
      }
      result.configurations.or(shiftLeft);
    }
    this.variableSet.add(variable);
    this.configurations = result.configurations;
    return this;
  }

  private void deposit(int nb, int pos, BitSet rhs) {
    for (int i = 0; i < nb; i++) {
      this.configurations.set(i + pos, rhs.get(i));
    }
  }

  private BitSet extractAndReset(int nb, int pos) {
    BitSet mask = new BitSet(this.configurations.size());
    for (int i = pos; i < pos + nb; i++) {
      mask.set(i);
    }
    BitSet result = (BitSet) mask.clone();
    result.and(this.configurations);
    mask.flip(0, mask.size());
    this.configurations.and(mask);
    return result.get(pos, result.size());
  }

  public boolean isSubSetOf(final ConfigurationSet<T> f) {
    BitSet copy = (BitSet) f.configurations.clone();
    copy.and(this.configurations);
    return this.configurations.equals(copy);
  }

  public boolean intersects(final ConfigurationSet<T> f) {
    return this.configurations.intersects(f.configurations);
  }

  public int sizeIntersection(final ConfigurationSet<T> f) {
    BitSet copy = (BitSet) f.configurations.clone();
    copy.and(this.configurations);
    return copy.cardinality();
  }

  public int size() {
    return this.configurations.size();
  }

  public int getNumberOfConfiguration() {
    return this.configurations.cardinality();
  }
  public boolean hasConfiguration(int index) {
    return this.configurations.get(index);
  }
  
  public Configuration<T> getConfiguration(int index) {
    return new Configuration<T>(this.variableSet, this.getValues(index));
  }

  public boolean has(Configuration<T> config) {
    return this.configurations.get(this.getIndex(config));
  }

  private List<T> getValues(int index) {
    List<T> result = new ArrayList<T>();
    for (int counter = this.variableSet.size(); counter > 0; counter--) {
      if (this.variableSet.hasVariable(counter - 1)) {
        Variable<T> v = this.variableSet.getVariable(counter - 1);
        int newValue = index % v.size();
        index /= v.size();
        result.add(0, v.getValue(newValue));
      }
    }
    return result;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((configurations == null) ? 0 : configurations.hashCode());
    result = prime * result + ((variableSet == null) ? 0 : variableSet.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ConfigurationSet<?> other = (ConfigurationSet<?>) obj;
    if (configurations == null) {
      if (other.configurations != null)
        return false;
    } else
      if (!configurations.equals(other.configurations))
        return false;
    if (variableSet == null) {
      if (other.variableSet != null)
        return false;
    } else
      if (!variableSet.equals(other.variableSet))
        return false;
    return true;
  }

  @Override
  public String toString() {
    String result = "{";
    int counter = 0;
    for (int i = 0; i < this.size(); i++) {
      if (this.configurations.get(i)) {
        result += this.getConfiguration(i);
        if (counter < this.getNumberOfConfiguration() - 1) {
          result += ", ";
        }
        counter++;
      }
    }
    result += "}";
    return result;
  }

  public void clear() {
    this.configurations.clear();
  }
}
