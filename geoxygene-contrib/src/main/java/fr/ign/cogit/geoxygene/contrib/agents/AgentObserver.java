/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.agents;

/**
 * Observer in the sense of the Observer design pattern. Here, the observer
 * observes an agent process (i.e. the subject) and updates each time the
 * subject makes a notification, which is each time an agent has a new state.
 * This behaviour is useful to make an online observation of the agent processes
 * in the layer view panel (i.e. the geometries are updated online in the layer
 * view panel).
 * @author GTouya
 * 
 */
public interface AgentObserver {

  /**
   * update the GUI to consider the current state in the agent process.
   */
  public void update();

  /**
   * Tells if the agent observer is in slow motion, which requires the
   * observation subject to slow down and wait after update.
   * @return
   */
  public boolean isSlowMotion();

  /**
   * Set the Agent observation in slow motion.
   * @return
   */
  public void setSlowMotion(boolean isSlowMotion);
}
