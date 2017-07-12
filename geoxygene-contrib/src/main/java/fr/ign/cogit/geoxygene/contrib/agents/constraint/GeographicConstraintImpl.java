package fr.ign.cogit.geoxygene.contrib.agents.constraint;

import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;

/**
 * A geographic constraint implementation
 * @author julien Gaffuri
 */
public abstract class GeographicConstraintImpl extends ConstraintImpl
    implements GeographicConstraint {

  /**
   * @return The agent the constraint is on.
   */
  private IGeographicAgent agent;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.constraint.GeographicConstraint#getAgent()
   */
  /**
   * @return
   */
  @Override
  public IGeographicAgent getAgent() {
    return this.agent;
  }

  /**
   * @param agent
   */
  private void setAgent(IGeographicAgent agent) {
    this.agent = agent;
  }

  /**
   * Build a constraint on a geographic agent with an importance
   * 
   * @param agent
   * @param importance
   */
  public GeographicConstraintImpl(IGeographicAgent agent, double importance) {
    super(importance);
    this.setAgent(agent);
    agent.getConstraints().add(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.constraint.GeographicConstraint#
   * computeCurrentValue ()
   */
  @Override
  public void computeCurrentValue() {
    // default: do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.constraint.GeographicConstraint#
   * computeGoalValue ()
   */
  @Override
  public void computeGoalValue() {
    // default: do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.constraint.GeographicConstraint#
   * computeSatisfaction ()
   */
  @Override
  public void computeSatisfaction() {
    // default: do nothing
  }

  /**
   * the constraint priority
   */
  private int priority = 1;

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.constraint.IGeographicConstraint#getPriority()
   */
  /**
   * @return
   * @author CDuchene
   */
  @Override
  public double getPriority() {
    return this.priority;
  }

  /**
   * @param priority
   */
  public void setPriority(int priority) {
    this.priority = priority;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.constraint.GeographicConstraint#computePriority
   * ()
   */
  @Override
  public void computePriority() {
    // default: do nothing
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.constraint.ConstraintImpl#printInfosConsole()
   */
  @Override
  public void printInfosConsole() {
    this.computeSatisfaction();
    this.computePriority();
    System.out.println("Constraint: " + this.getClass().getSimpleName()
        + " - Imp=" + this.getImportance() + " - Sat=" + this.getSatisfaction()
        + " - Prior=" + this.getPriority());
  }

  @Override
  public boolean equals(Object obj) {
    if (!this.getClass().getSimpleName()
        .equals(obj.getClass().getSimpleName())) {
      return false;
    }
    return (((GeographicConstraintImpl) obj).getAgent().equals(this.agent));
  }

  @Override
  public int hashCode() {
    return this.getClass().getSimpleName().hashCode();
  }

}
