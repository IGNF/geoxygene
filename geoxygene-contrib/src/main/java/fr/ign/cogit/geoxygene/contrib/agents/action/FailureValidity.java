/**
 * 
 */
package fr.ign.cogit.geoxygene.contrib.agents.action;

/**
 * Validity of a failing action
 * @author CDuchene
 */
/**
 * @author CDuchene
 * 
 */
public enum FailureValidity {

  /**
   * The failure never expires
   */
  NEVER_EXPIRES,
  /**
   * The failure expires at the next activation of the agent
   */
  NEXT_ACTIVATION,
  /**
   * Any neighbour of the agent has modified itself
   */
  ENVIRONMENT_MODIFIED,
  /**
   * The agent has modified itself
   */
  AGENT_MODIFIED,
  /**
   * The other agent sharing the constraint the action was trying to satisfied
   * has modified itself
   */
  OTHER_AGENT_MODIFIED,
  /**
   * The agent or any of its neighbours has modified itself
   */
  AGENT_OR_ENVIRONMENT_MODIFIED,
  /**
   * The other agent sharing the constraint the action was trying to satisfied
   * has sent a message indicating that on its side, it has suppressed the
   * failure.
   */
  INFO_FROM_OTHER
}
