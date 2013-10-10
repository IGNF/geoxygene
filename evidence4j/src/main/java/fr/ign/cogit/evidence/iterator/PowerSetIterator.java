package fr.ign.cogit.evidence.iterator;

import java.util.BitSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import fr.ign.cogit.evidence.configuration.ConfigurationSet;
import fr.ign.cogit.evidence.variable.VariableSet;

public class PowerSetIterator<T> implements Iterator<ConfigurationSet<T>> {
  VariableSet<T> variableSet;
  BitSet configurations;

  public PowerSetIterator(VariableSet<T> vs) {
    this.variableSet = vs;
    this.configurations = new BitSet(this.variableSet.getNumberOfConfigurations());
  }

  @Override
  public boolean hasNext() {
    return this.configurations.cardinality() < this.variableSet.getNumberOfConfigurations();
  }

  @Override
  public ConfigurationSet<T> next() {
    if (!this.hasNext()) {
      throw (new NoSuchElementException("No more ConfigurationSet to return."));
    }
    boolean carryOn = true;
    for (int i = 0; i < this.variableSet.getNumberOfConfigurations() && carryOn; i++) {
      carryOn = this.configurations.get(i);
      this.configurations.flip(i);
    }
    ConfigurationSet<T> confSet = new ConfigurationSet<T>(this.variableSet);
    for (int i = 0; i < this.variableSet.getNumberOfConfigurations(); i++)
      if (this.configurations.get(i)) {
        confSet.add(confSet.getConfiguration(i));
      }
    return confSet;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("This iterator does not support the remove method.");
  }
}
