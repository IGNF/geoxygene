package fr.ign.cogit.geoxygene.contrib.agents.state;

import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;

/**
 * A geographic object state
 * 
 * @author JGaffuri
 * 
 */
public interface GeographicAgentState extends AgentState {

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.state.AgentState#getAgent()
   */
  @Override
  public IGeographicAgent getAgent();

  /**
   * @return True if the state is valid, else false
   */
  @Override
  public boolean isValid(double treshold);

}
