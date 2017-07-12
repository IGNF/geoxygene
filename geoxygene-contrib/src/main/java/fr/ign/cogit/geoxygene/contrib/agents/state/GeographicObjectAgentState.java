/**
 * @author julien Gaffuri 11 sept. 2008
 */
package fr.ign.cogit.geoxygene.contrib.agents.state;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;

/**
 * A geographic object agent state
 * 
 * @author julien Gaffuri 11 sept. 2008
 * 
 */
public interface GeographicObjectAgentState extends GeographicAgentState {

  /**
   * @return
   */
  public IGeometry getGeometry();

  /**
   * @return
   */
  public boolean isDeleted();

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.state.GeographicAgentState#getAgent()
   */
  @Override
  public GeographicObjectAgent getAgent();

}
