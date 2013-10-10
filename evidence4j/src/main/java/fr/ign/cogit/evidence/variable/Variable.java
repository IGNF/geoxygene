package fr.ign.cogit.evidence.variable;

import java.util.HashMap;
import java.util.Map;

/**
 * A variable.
 * @author Julien Perret
 *
 * @param <T> type of value held
 */
public class Variable<T> {
  private Integer rank;
  /**
   * @return the rank of the variable
   */
  public Integer getRank() {
    return this.rank;
  }

  private Map<Integer, T> valueMap;
  private Map<T, Integer> valueReverseMap;

  /**
   * Create a new variable with the given rank;
   * @param r the rank of the variable.
   */
  Variable(Integer r) {
    this.rank = r;
    this.valueMap = new HashMap<Integer, T>();
    this.valueReverseMap = new HashMap<T, Integer>();
  }

  /**
   * Add a new value for the variable.
   * @param v a value
   */
  public void add(T v) {
    if (valueReverseMap.containsKey(v)) {
      // TODO Handle exception
      return;
    }
    Integer index = new Integer(this.size());
    this.valueMap.put(index, v);
    this.valueReverseMap.put(v, index);
  }

  /**
   * @return number of values the variable can take
   */
  public int size() {
    return this.valueMap.size();
  }

  /**
   * @param e a variable
   * @return the internal identifier for the given value
   */
  public Integer getValueId(T e) {
    Integer i = this.valueReverseMap.get(e);
    assert (i != null);
    return i;
  }

  /**
   * @param v a value
   * @return true if the variable contains the given value, false otherwise
   */
  public boolean hasValue (T v) {
    return this.valueReverseMap.containsKey(v);
  }
  /**
   * @param index an index
   * @return the value for the given index
   */
  public T getValue(int index) {
    return this.valueMap.get(index);
  }

  @Override
  public String toString() {
    String result = "" + this.rank + " (";
    int counter = 0;
    for (T e : this.valueReverseMap.keySet()) {
      result += e;
      if (counter < this.size() - 1) {
        result += ", ";
      }
      counter++;
    }
    result += ")";
    return result;
  }
}
