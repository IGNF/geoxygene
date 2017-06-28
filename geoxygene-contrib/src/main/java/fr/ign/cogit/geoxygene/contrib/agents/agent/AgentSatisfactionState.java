/**
 * 
 */
package fr.ign.cogit.geoxygene.contrib.agents.agent;

/**
 * A possible state on the agent satisfaction after its activation
 * @author JGaffuri
 */
public enum AgentSatisfactionState {

  /**
	 */
  PERFECTLY_SATISFIED_INITIALY,

  /**
	 */
  PERFECTLY_SATISFIED_AFTER_TRANSFORMATION,

  /**
	 */
  SATISFACTION_IMPROVED_BUT_NOT_PERFECT,

  /**
	 */
  SATISFACTION_UNCHANGED,

  /**
	 */
  ERROR
}
