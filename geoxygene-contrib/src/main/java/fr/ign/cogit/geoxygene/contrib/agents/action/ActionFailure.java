/**
 * 
 */
package fr.ign.cogit.geoxygene.contrib.agents.action;

/**
 * An action that led to a failure
 * @author CDuchene
 * 
 */
public interface ActionFailure {

  /**
   * Gets the failing action
   * @return the failing action
   */
  public Action getAction();

  /**
   * Gets the validity of the failure (indicates whrn the failure expires)
   * @return the validity of the failure
   */
  public FailureValidity getValidity();

  /**
   * For an action that had been requested by another agent: indicates if this
   * other agent should be informed when the failure is removed.
   * @return {@code true} if the other agent is to be informed, {@code false}
   *         otherwise.
   */
  public boolean getInformOtherAgent();

}
