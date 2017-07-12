package fr.ign.cogit.geoxygene.contrib.agents.state;

import java.util.HashSet;

import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;
import fr.ign.cogit.geoxygene.contrib.agents.agent.MesoAgent;

/**
 * A meso agent state. This state does not concern only the state of the meso
 * agent, but a list of states of its components too, in a recursive way.
 * @author JGaffuri
 */
public class MesoAgentStateImpl extends GeographicObjectAgentStateImpl
    implements MesoAgentState {

  /**
   * the states of the components
   */
  private HashSet<GeographicObjectAgentState> componentStates = new HashSet<GeographicObjectAgentState>();

  /**
   * @return
   */
  public HashSet<GeographicObjectAgentState> getComponentStates() {
    return this.componentStates;
  }

  /**
   * @param agent
   * @param previousState
   * @param action
   */
  public MesoAgentStateImpl(MesoAgent<? extends GeographicObjectAgent> agent,
      MesoAgentState previousState, Action action) {
    super(agent, previousState, action);

    // store the components states too
    for (GeographicObjectAgent ag : agent.getComponents()) {
      // this.getComponentStates().add(((MicroAgent) ag));
      // if (ag instanceof MesoAgent<?>) {
      // this.getComponentStates().add(
      // new MesoAgentStateImpl((MesoAgent<?>) ag, null, action));
      // } else {
      // this.getComponentStates().add(
      // new MicroAgentStateImpl((MicroAgent) ag, null, action));
      // }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.state.GeographicObjectAgentState#clean()
   */
  @Override
  public void clean() {
    super.clean();
    for (GeographicObjectAgentState eago : this.getComponentStates()) {
      eago.clean();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.state.GeographicObjectAgentState#getAgent()
   */
  @Override
  public MesoAgent<?> getAgent() {
    return (MesoAgent<?>) super.getAgent();
  }

}
