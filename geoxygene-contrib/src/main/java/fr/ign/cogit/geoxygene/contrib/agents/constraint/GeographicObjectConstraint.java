package fr.ign.cogit.geoxygene.contrib.agents.constraint;

import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;

/**
 * A geographic constraint on an object.
 * @author JGaffuri
 */
public interface GeographicObjectConstraint extends GeographicConstraint {

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.constraint.GeographicConstraint#getAgent()
   */
  /**
     */
  @Override
  GeographicObjectAgent getAgent();

}
