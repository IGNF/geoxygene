package fr.ign.cogit.geoxygene.contrib.agents.action;

import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;

/**
 * interface of an action an action is proposed by a constraint, with a weight,
 * and is used on an agent
 * @author JGaffuri
 */
public interface Action {

  /**
   * @return The action weight
   */
  public abstract double getWeight();

  /**
   * @param weight
   */
  public abstract void setWeight(double weight);

  /**
   * @return The constraint that proposed the action
   */
  public abstract Constraint getConstraint();

  /**
   * @param constraint
   */
  public abstract void setConstraint(Constraint constraint);

  /**
   * @return The agent the action is proposed to
   */
  public abstract IAgent getAgent();

  /**
   * @param agent
   */
  public abstract void setAgent(IAgent agent);

  /**
   * computes the action
   * @return an {@link ActionResult} describing if the agent has been modified,
   *         eliminated or is unchanged.
   * @throws InterruptedException The thread in which this action is executed
   *           has been interrupted (??? to be confirmed) .
   */
  // TODO Check the real explanation of the InterruptedException
  public abstract ActionResult compute() throws InterruptedException;

  /**
   * Clean the object to be sure it is well erased by the GC
   */
  public abstract void clean();

  /**
   * Get the restriction on the action: the maximum number of times the action
   * can be computed on the same agent.
   * @return the restriction number. -1 for unrestricted actions.
   * @author GTouya
   */
  public abstract int getRestriction();

  /**
   * @param restriction
   */
  public abstract void setRestriction(int restriction);

}
