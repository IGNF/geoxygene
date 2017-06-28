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
 * Interface for Subject classes in the sense of the Observer design pattern.
 * Here, the observer is a class implementing {@link AgentObserver}, and the
 * subject is an agent process/lifecycle that notifies the observer when some
 * agents have been modified.
 * @author GTouya
 * 
 */
public interface AgentObservationSubject {

  /**
   * Attach a new observer for this subject.
   * @param observer
   */
  public void attach(AgentObserver observer);

  /**
   * Detach one of the observers of this subject.
   * @param observer
   */
  public void detach(AgentObserver observer);

  /**
   * notify a change to all the observers, generally by calling the update
   * method of {@link AgentObserver}.
   */
  public void notifyChange();
}
