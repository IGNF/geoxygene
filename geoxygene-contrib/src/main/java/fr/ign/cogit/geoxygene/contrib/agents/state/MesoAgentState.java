package fr.ign.cogit.geoxygene.contrib.agents.state;

import java.util.Set;

import fr.ign.cogit.geoxygene.contrib.agents.agent.MesoAgent;

/**
 * A meso agent state. This state does not concern only the state of the meso
 * agent, but a list of states of its components too, in a recursive way.
 * @author JGaffuri
 */
public interface MesoAgentState extends GeographicObjectAgentState {

  /**
   * @return
   */
  public Set<GeographicObjectAgentState> getComponentStates();

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.state.GeographicObjectAgentState#getAgent()
   */
  @Override
  public MesoAgent<?> getAgent();

}
