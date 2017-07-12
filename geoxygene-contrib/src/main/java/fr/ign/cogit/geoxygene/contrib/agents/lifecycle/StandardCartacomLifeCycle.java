/**
 * 
 */
package fr.ign.cogit.geoxygene.contrib.agents.lifecycle;

import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

/**
 * Stereotype singleton. Life-cycle for CartACom agents.
 * 
 * @author CDuchene
 * 
 */
public class StandardCartacomLifeCycle implements AgentLifeCycle {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  // Very private fields (no public getter) //

  // //////////////////////////////////////
  // All constructors //
  // //////////////////////////////////////

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////////////////////
  // Public methods - Getters and setters //
  // //////////////////////////////////////////////////////////

  /**
   * {@inheritDoc} (This is the behaviour inherited from the super class). Here
   * not used (until here).
   * 
   */
  @Override
  public boolean isStoreStates() {
    // TODO Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc} (This is the behaviour inherited from the super class). Here
   * not used (until here).
   * 
   */
  @Override
  public void setStoreStates(boolean storeStates) {
    // TODO Auto-generated method stub

  }

  // /////////////////////////////////////////////
  // Public methods - Others //
  // /////////////////////////////////////////////

  /**
   * Standard life cycle method for CartACom agents.
   * @param agent the agnet on which to execute the life-cycle
   * 
   */
  @Override
  public AgentSatisfactionState compute(IAgent agent)
      throws InterruptedException {
    // TODO Auto-generated method stub
    return null;
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
