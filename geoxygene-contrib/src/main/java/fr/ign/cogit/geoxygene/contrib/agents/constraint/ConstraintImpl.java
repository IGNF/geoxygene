/**
 * 
 */
package fr.ign.cogit.geoxygene.contrib.agents.constraint;

/**
 * A default constraint implementation.
 * 
 * @author julien Gaffuri
 * 
 */
public abstract class ConstraintImpl implements Constraint {

  /**
   * @return the constraint's importance
   */
  private double importance;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.constraint.Constraint#getImportance()
   */
  /**
   * @return
   */
  @Override
public double getImportance() {
    return this.importance;
  }

  /**
   * @param importance
   */
  @Override
public void setImportance(double importance) {
    this.importance = importance;
  }

  /**
   * Build a constraint with an importance value
   * 
   * @param importance
   */
  public ConstraintImpl(double importance) {
    this.setImportance(importance);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.constraint.Constraint#printInfosConsole()
   */
  @Override
  public void printInfosConsole() {
    System.out.println("Constraint: " + this.getClass().getSimpleName()
        + " - Imp=" + this.getImportance());
  }

  @Override
  public boolean equals(Object obj) {
    return this.getClass().getSimpleName().equals(
        obj.getClass().getSimpleName());
  }

  @Override
  public int hashCode() {
    return this.getClass().getSimpleName().hashCode();
  }

}
