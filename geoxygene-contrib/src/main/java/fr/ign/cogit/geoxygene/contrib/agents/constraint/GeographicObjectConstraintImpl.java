/**
 * 
 */
package fr.ign.cogit.geoxygene.contrib.agents.constraint;

import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;

/**
 * A geographic simple constraint - simple means on a single object. It can be
 * hold by a geographic object.
 * 
 * @author JGaffuri
 * 
 */
public abstract class GeographicObjectConstraintImpl
    extends GeographicConstraintImpl implements GeographicObjectConstraint {

  /**
   * Build a simple constraint on an agent with an importance value
   * 
   * @param agent
   * @param importance
   */
  public GeographicObjectConstraintImpl(IGeographicAgent agent,
      double importance) {
    super(agent, importance);
  }

  /**
   * the constraint's satisfaction
   */
  private double satisfaction;

  /**
   * @return
   */
  @Override
  public double getSatisfaction() {
    return this.satisfaction;
  }

  /**
   * @param satisfaction
   */
  protected void setSatisfaction(double satisfaction) {
    this.satisfaction = satisfaction;
  }

  @Override
  public GeographicObjectAgent getAgent() {
    return (GeographicObjectAgent) super.getAgent();
  }

}
