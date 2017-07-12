/**
 * @author julien Gaffuri 11 sept. 2008
 */
package fr.ign.cogit.geoxygene.contrib.agents.state;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.agent.GeographicObjectAgent;

/**
 * A geographic object agent state
 * 
 * @author julien Gaffuri 11 sept. 2008
 * 
 */
public class GeographicObjectAgentStateImpl extends GeographicAgentStateImpl
    implements GeographicObjectAgentState {

  /**
   * The geometry of the object
   */
  private IGeometry geometry;

  /**
   * @return
   */
  public IGeometry getGeometry() {
    return this.geometry;
  }

  /**
   * If the agent is deleted in this state
   */
  private boolean deleted;

  /**
   * @return
   */
  public boolean isDeleted() {
    return this.deleted;
  }

  /**
   * The constructor
   * 
   * @param ag
   * @param previousState
   * @param action
   */
  public GeographicObjectAgentStateImpl(GeographicObjectAgent ag,
      GeographicObjectAgentState previousState, Action action) {
    super(ag, previousState, action);
    this.deleted = ag.getFeature().isDeleted();
    if (ag.getFeature().getGeom() == null) {
      this.geometry = null;
    } else {
      this.geometry = (IGeometry) ag.getFeature().getGeom().clone();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.state.GeographicAgentState#clean()
   */
  @Override
  public void clean() {
    super.clean();
    this.geometry = null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.state.GeographicAgentState#getAgent()
   */
  @Override
  public GeographicObjectAgent getAgent() {
    return (GeographicObjectAgent) super.getAgent();
  }

}
