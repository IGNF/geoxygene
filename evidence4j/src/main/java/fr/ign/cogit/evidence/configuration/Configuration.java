package fr.ign.cogit.evidence.configuration;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.evidence.variable.VariableSet;

/**
 * A configuration represents a realisation of all the variables in the variable set.
 * @author Julien Perret
 * @param <T>
 */
public class Configuration<T> {
  private List<Integer> values;
  private VariableSet<T> variableSet;

  public Configuration(VariableSet<T> d, List<T> l) {
    assert (l.size() == d.getNumberOfVariables());
    this.variableSet = d;
    this.initFromList(l);
  }

  private void initFromList(List<T> l) {
    // System.out.println("initFromList " + (l.size() == this.variableSet.getNumberOfVariables()));
    assert (l.size() == this.variableSet.getNumberOfVariables());
    this.values = new ArrayList<Integer>(l.size());
    int counter = 0;
    for (T element : l) {
      // System.out.println("T " + element);
      assert (variableSet.getNthVariable(counter).hasValue(element));
      this.values.add(this.variableSet.getNthVariable(counter).getValueId(element));
      counter++;
    }
  }

  public List<Integer> getValues() {
    return this.values;
  }

  public List<T> getAsList() {
    List<T> result = new ArrayList<T>();
    int counter = 0;
    for (Integer index : this.values) {
      result.add(this.variableSet.getNthVariable(counter).getValue(index));
      counter++;
    }
    return result;
  }

  @Override
  public String toString() {
    String result = "{";
    int counter = 0;
    for (Integer index : this.values) {
      result += this.variableSet.getNthVariable(counter).getValue(index);
      if (counter < this.values.size() - 1) {
        result += ", ";
      }
      counter++;
    }
    result += "}";
    return result;
  }
}
