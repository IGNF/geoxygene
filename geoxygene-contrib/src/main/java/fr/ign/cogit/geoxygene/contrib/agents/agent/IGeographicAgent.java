/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.agents.agent;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicAgentState;

/**
 * An agent that has a geographical meaning.
 * 
 * @author JGaffuri
 * 
 */
public interface IGeographicAgent extends IAgent {

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.Agent#buildCurrentState(fr.ign.cogit.
   * agentgeoxygene.state.AgentState, fr.ign.cogit.agentgeoxygene.action.Action)
   */
  @Override
  GeographicAgentState buildCurrentState(AgentState previousState,
      Action action);

  public IFeature getFeature();
}
