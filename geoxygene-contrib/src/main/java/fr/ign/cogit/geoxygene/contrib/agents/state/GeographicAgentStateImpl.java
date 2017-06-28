package fr.ign.cogit.geoxygene.contrib.agents.state;

import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IGeographicAgent;

/**
 * A geographic object state
 * 
 * @author JGaffuri
 * 
 */
public class GeographicAgentStateImpl extends AgentStateImpl
    implements GeographicAgentState {

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.state.AgentState#getAgent()
   */
  @Override
  public IGeographicAgent getAgent() {
    return (IGeographicAgent) super.getAgent();
  }

  /**
   * The constructor
   * 
   * @param agent
   * @param previousState
   * @param action
   */
  public GeographicAgentStateImpl(IGeographicAgent agent,
      GeographicAgentState previousState, Action action) {
    super(agent, previousState, action);
  }

  /**
   * @return True if the state is valid, else false
   */
  @Override
  public boolean isValid(double treshold) {
    // the state is valid if the satisfaction is bigger enough than the one of
    // its previous state
    if (this.getPreviousState() == null) {
      return true;
    }
    return this.getSatisfaction()
        - this.getPreviousState().getSatisfaction() >= treshold;
  }

}
