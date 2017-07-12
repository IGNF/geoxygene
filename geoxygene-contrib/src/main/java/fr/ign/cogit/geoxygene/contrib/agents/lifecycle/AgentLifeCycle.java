/**
 * 
 */
package fr.ign.cogit.geoxygene.contrib.agents.lifecycle;

import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

/**
 * The agent lifecycle interface
 * @author julien Gaffuri
 */
public interface AgentLifeCycle {

  /**
   * Compute the lifecycle on an agent
   * 
   * @param agent
   * @return
   * @throws InterruptedException
   */
  public AgentSatisfactionState compute(IAgent agent)
      throws InterruptedException;

  /**
   * @return true if the encountered states have to be stored at the end of the
   *         lifecycle (for debug)
   */
  public boolean isStoreStates();

  /**
   * @param storeStates
   */
  public void setStoreStates(boolean storeStates);

}
