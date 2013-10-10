package fr.ign.cogit.evidence.variable;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory to create new variables and give them a proper rank.
 * All creations of variables have to be made via the factory.
 * @author Julien Perret
 * @param <T>
 *        the type of values the variables can take.
 *        //TODO rethink that: the variables could actually take any number of different types
 */
public class VariableFactory<T> {
  private int rank;
  private Map<Integer, Variable<T>> variables;

  /**
   * A new empty factory.
   */
  public VariableFactory() {
    this.rank = new Integer(0);
    variables = new HashMap<Integer, Variable<T>>();
  }

  /**
   * @return a brand new variable.
   */
  public Variable<T> newVariable() {
    Variable<T> v = new Variable<T>(this.rank);
    this.variables.put(this.rank, v);
    this.rank++;
    return v;
  }

  /**
   * @return number of variables the factory holds.
   */
  public int size() {
    return this.rank;
  }

  /**
   * @param r
   *        a rank
   * @return the variable with the given rank
   */
  public Variable<T> getVariable(Integer r) {
    return this.variables.get(r);
  }
}
