package fr.ign.cogit.evidence.variable;

import java.util.BitSet;

/**
 * A set of variables.
 * @author Julien Perret
 * @param <T>
 *        the type of values the variables hold
 */
public class VariableSet<T> {
  private VariableFactory<T> variableFactory;

  /**
   * @return the factory used by this set of variables.
   */
  public VariableFactory<T> getVariableFactory() {
    return this.variableFactory;
  }

  private BitSet variables;
  private int numberOfConfigurations;
  private int numberOfVariables;

  /**
   * Create a new variable set by copy.
   * @param variableSet
   *        a variable set
   */
  public VariableSet(VariableSet<T> variableSet) {
    this.variableFactory = variableSet.variableFactory;
    this.variables = (BitSet) variableSet.variables.clone();
    this.numberOfVariables = variableSet.numberOfVariables;
    this.numberOfConfigurations = variableSet.numberOfConfigurations;
  }

  /**
   * A new variable set using the given factory.
   * @param vf
   *        a variable factory
   */
  public VariableSet(VariableFactory<T> vf) {
    this.variableFactory = vf;
    this.variables = new BitSet(vf.size());
    this.numberOfConfigurations = 1;
    this.numberOfVariables = 0;
  }

  /**
   * A new variable set using the given factory and a set of variables.
   * @param vf
   *        a variable factory
   * @param vars
   *        a bitset representing a set of variables
   */
  public VariableSet(VariableFactory<T> vf, BitSet vars) {
    this.variableFactory = vf;
    this.variables = (BitSet) vars.clone();
    this.numberOfConfigurations = 1;
    this.numberOfVariables = 0;
    for (int rank = 0; rank < this.variables.length(); rank++) {
      if (this.variables.get(rank)) {
        ++this.numberOfVariables;
        this.numberOfConfigurations *= this.getVariable(rank).size();
      }
    }
  }

  /**
   * Add a new variable to the set.
   * @param v
   *        a variable
   */
  public void add(Variable<T> v) {
    // assert (v.getRank() < this.size());
    this.variables.set(v.getRank());
    this.numberOfConfigurations *= v.size();
    ++this.numberOfVariables;
    // System.out.println(this.numberOfVariables + " vars " + this.numberOfConfigurations +
    // " confs");
  }

  /**
   * @param vs
   *        another variable set
   * @return the union of the 2 variable sets
   */
  public VariableSet<T> union(VariableSet<T> vs) {
    assert (this.variableFactory == vs.variableFactory);
    BitSet vars = (BitSet) this.variables.clone();
    vars.or(vs.variables);
    return new VariableSet<T>(this.variableFactory, vars);
  }

  /**
   * @return the size of the variable set, ie the maximum rank of its variables + 1. 
   */
  public int size() {
    return this.variables.length();//this.variables.size();
  }

  /**
   * @param rank
   *        a rank
   * @return true if the variable set has the variable with the given rank
   */
  public boolean hasVariable(Integer rank) {
    return this.variables.get(rank);
  }

  /**
   * @param v
   *        a variable
   * @return true if the variable set has the given variable
   */
  public boolean hasVariable(Variable<T> v) {
    return this.hasVariable(v.getRank());
  }

  /**
   * @param rank
   *        a rank
   * @return returns the variable with the given rank
   */
  public Variable<T> getVariable(int rank) {
    return this.variableFactory.getVariable(rank);
  }

  /**
   * @return the number of configurations the variable set holds
   */
  public int getNumberOfConfigurations() {
    return this.numberOfConfigurations;
  }

  /**
   * @param i
   * @param j
   * @return the number of configurations one can make using the variables between rank i and j
   *         (included)
   */
  public int getNumberOfConfigurations(int i, int j) {
    int res = 1;
    for (int counter = i; counter < this.variables.size() && counter <= j; ++counter) {
      if (this.variables.get(counter)) {
        res *= this.variableFactory.getVariable(counter).size();
      }
    }
    return res;
  }

  public int getMaxRank() {
    int res = 0;
    for (int counter = 0; counter < this.variables.size(); ++counter) {
      if (this.variables.get(counter)) {
        int rank = this.variableFactory.getVariable(counter).getRank();
        if (rank > res) {
          res = rank;
        }
      }
    }
    return res;
  }

  /**
   * Return the nth variable of the set. Be careful: this is not necessarily the variable with rank
   * n.
   * @param n
   * @return the nth variable of the variable set
   */
  public Variable<T> getNthVariable(int n) {
    assert (n < this.getNumberOfVariables());
    int currentVariableNumber = 0;
    int currentVariableRank = 0;
    for (; currentVariableRank < this.size(); ++currentVariableRank) {
      if (this.variables.get(currentVariableRank)) {
        if (currentVariableNumber == n) {
          break;
        }
        ++currentVariableNumber;
      }
    }
    assert (currentVariableRank < this.size());
    return this.variableFactory.getVariable(currentVariableRank);
  }

  /**
   * @return the number of variables the set holds.
   */
  public int getNumberOfVariables() {
    return this.numberOfVariables;
  }

  /**
   * @param v another variable set
   * @return true is this variable set is a subset of the given variable set
   */
  public boolean isSubsetOf(VariableSet<T> v) {
    BitSet copy = (BitSet) v.variables.clone();
    copy.and(this.variables);
    return this.variables.equals(copy);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + numberOfConfigurations;
    result = prime * result + numberOfVariables;
    result = prime * result + ((variables == null) ? 0 : variables.hashCode());
    result = prime * result + ((variableFactory == null) ? 0 : variableFactory.hashCode());
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
    VariableSet<?> other = (VariableSet<?>) obj;
    if (numberOfConfigurations != other.numberOfConfigurations)
      return false;
    if (numberOfVariables != other.numberOfVariables)
      return false;
    if (variables == null) {
      if (other.variables != null)
        return false;
    } else
      if (!variables.equals(other.variables))
        return false;
    if (variableFactory == null) {
      if (other.variableFactory != null)
        return false;
    } else
      if (!variableFactory.equals(other.variableFactory))
        return false;
    return true;
  }

  @Override
  public String toString() {
    String result = "" + this.variables + " - " + this.numberOfVariables + " variables - "
        + this.numberOfConfigurations + " configurations";
    return result;
  }
}
