package fr.ign.cogit.geoxygene.contrib.agents.agent;

import java.util.HashSet;
import java.util.Set;

import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.AgentLifeCycle;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

/**
 * An agent has a satisfaction value that depends on some constraints attached
 * to it. It can be activated to compute a lifecycle that improves its
 * satisfaction by trying some actions.
 * @author JGaffuri
 */
public interface IAgent extends Runnable {

  /**
   * @return The agent's id
   */
  int getId();

  /**
   * @param id
   */
  void setId(int id);

  /**
   * Activate the agent (usually, compute the lifeCycle method)
   * 
   * @return the AgentSatisfactionState returned
   * @throws InterruptedException
   */
  AgentSatisfactionState activate() throws InterruptedException;

  /**
   * @return the agent lifecycle
   */
  AgentLifeCycle getLifeCycle();

  /**
   * @param lifeCycle
   */
  void setLifeCycle(AgentLifeCycle lifeCycle);

  // satisfaction

  /**
   * @return the agent's satisfaction value
   */
  double getSatisfaction();

  /**
   * @param s
   */
  void setSatisfaction(double s);

  /**
   * compute the agent's satisfaction
   */
  void computeSatisfaction();

  // constraints

  /**
   * @return the constraints of the agent
   */
  HashSet<Constraint> getConstraints();

  /**
   * Fill the actions to try list with the ones proposed by the non satisfied
   * constraints. Supposes that the constraints satisfaction values have been
   * computed previously.
   */
  void updateActionProposals();

  // actions

  /**
   * @return the collection of actions to try. These actions are the one
   *         proposed by the contraints.
   */
  Set<ActionProposal> getActionProposals();

  /**
   * @param actionsToTry
   */
  void setActionsToTry(Set<ActionProposal> actionsToTry);

  /**
   * @return The best action to try by the agent, among the ones returned by the
   *         getActionsToTry() method.
   */
  ActionProposal getBestActionProposal();

  /**
   * Clean the actions to try list
   */
  void cleanActionsToTry();

  // states

  /**
   * Build the state object of an agent
   * 
   * @param previousState The previous state (to link the built one the its
   *          father. Is null for the root state)
   * @param action The action used to get the current state (is null for the
   *          root state)
   * @return
   */
  AgentState buildCurrentState(AgentState previousState, Action action);

  /**
   * Change an agent to go back to a given state
   * 
   * @param state
   */
  void goBackToState(AgentState state);

  // states tree

  /**
   * @return The agent's root state. The whole states tree (if used by the
   *         lifecycle) can be retrieved by using the getPreviousState and
   *         getChildStates methods.
   */
  AgentState getRootState();

  /**
   * @param rootState
   */
  void setRootState(AgentState rootState);

  /**
   * @return The number of states encountered by the agent (in its states tree)
   */
  int getStatesNumber();

  /**
   * @param state
   * @return The number of states in the states tree under a given state
   */
  int getStatesNumber(AgentState state);

  /**
   * Clean the agent's states tree
   */
  void cleanStates();

  /**
   * Clean a stored state and all its children (the whole sub-tree)
   * @param state
   */
  void cleanSubTree(AgentState state);

  // other

  /**
   * Clean the agent
   */
  void clean();

  /**
   * Print info on the agent in the console
   */
  void printInfosConsole();

}
