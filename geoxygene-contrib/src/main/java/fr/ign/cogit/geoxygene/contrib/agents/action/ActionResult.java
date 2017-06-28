package fr.ign.cogit.geoxygene.contrib.agents.action;

/**
 * Describes the consequences an actions had on an agent.
 * @author CDuchene
 */
public enum ActionResult {
  /**
   * The agent is unchanged (geometry and other attributes)
   */
  UNCHANGED,
  /**
   * The agent has been modified (geometry and/or other attributes)
   */
  MODIFIED,
  /**
   * The agent has been eliminated by the action
   */
  ELIMINATED,
  /**
   * Unknown result
   */
  UNKNOWN
}
