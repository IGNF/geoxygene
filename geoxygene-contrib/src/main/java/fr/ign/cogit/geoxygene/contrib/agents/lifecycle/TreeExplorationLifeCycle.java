/**
 * 
 */
package fr.ign.cogit.geoxygene.contrib.agents.lifecycle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.agents.AgentObservationSubject;
import fr.ign.cogit.geoxygene.contrib.agents.AgentObserver;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.AgentSatisfactionState;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;

/**
 * Life cycle with tree exploration
 * @author jgaffuri
 */
public class TreeExplorationLifeCycle
    implements AgentLifeCycle, AgentObservationSubject {
  private static Logger logger = Logger
      .getLogger(TreeExplorationLifeCycle.class.getName());

  /**
   * The maximum number of states an agent can encouter. After that value is
   * reached, the life cycle stops. A negative value means there is no limit.
   */
  private int statesMaxNumber = -1;

  /**
   * @return
   */
  public int getStatesMaxNumber() {
    return this.statesMaxNumber;
  }

  /**
   * @param statesMaxNumber
   */
  public void setStatesMaxNumber(int statesMaxNumber) {
    this.statesMaxNumber = statesMaxNumber;
  }

  /**
     */
  private boolean storeStates = false;

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.lifecycle.AgentLifeCycle#isStoreStates()
   */
  /**
   * @return
   */
  @Override
  public boolean isStoreStates() {
    return this.storeStates;
  }

  /**
   * @param storeStates
   */
  @Override
  public void setStoreStates(boolean storeStates) {
    this.storeStates = storeStates;
  }

  /**
     */
  private static TreeExplorationLifeCycle instance = null;

  /**
   * @return The unique instance.
   */
  public static TreeExplorationLifeCycle getInstance() {
    if (TreeExplorationLifeCycle.instance == null) {
      TreeExplorationLifeCycle.instance = new TreeExplorationLifeCycle();
    }
    return TreeExplorationLifeCycle.instance;
  }

  private TreeExplorationLifeCycle() {
  }

  /**
   * The validity satisfaction treshold: a satisfaction is said better than
   * another if its value, plus this treshold value, is greater.
   */
  private static double validitySatisfactionTreshold = 0.5;

  /**
   * @return
   */
  public static double getValiditySatisfactionTreshold() {
    return TreeExplorationLifeCycle.validitySatisfactionTreshold;
  }

  /**
   * @param validitySatisfactionTreshold
   */
  public static void setValiditySatisfactionTreshold(
      double validitySatisfactionTreshold) {
    TreeExplorationLifeCycle.validitySatisfactionTreshold = validitySatisfactionTreshold;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.lifecycle.AgentLifeCycle#compute(fr.ign.cogit
   * .agentgeoxygene.agent.Agent)
   */
  @Override
  public AgentSatisfactionState compute(IAgent agent)
      throws InterruptedException {
    if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
      TreeExplorationLifeCycle.logger.debug("******* Activation of " + agent
          + " (" + agent.getConstraints().size() + " constraint(s) )");

      // DEBUGAGE PAS BEAU TEMPORAIRE SEULEMENT
      // List<IFeatureCollection<IFeature>> fcList =
      // ((GeographicObjectAgentGeneralisation) agent)
      // .getFeature().getFeatureCollections();
      // if (fcList == null) {
      // TreeExplorationLifeCycle.logger
      // .debug("Liste des FeatureCollections null sur feature associé à l'agent
      // "
      // + agent);
      // } else {
      // TreeExplorationLifeCycle.logger
      // .debug("Liste des FeatureCollections sur feature associé à l'agent "
      // + agent + " : " + fcList.toString());
      // }
      // CecileGUIComponent.DisplayGeomAndPause(
      // ((GeographicObjectAgentGeneralisation) agent).getGeom(), Color.BLUE,
      // 3, "Prochain agent généralisé : " + agent.toString()
      // + " (visible en bleu)");
      // FIN DEBUGAGE PAS BEAU

    }

    // clean the possibly previously created and stored states
    agent.cleanStates();

    // satisfaction computation
    agent.computeSatisfaction();

    // get actions from constraints
    agent.updateActionProposals();
    if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
      TreeExplorationLifeCycle.logger.debug(" S=" + agent.getSatisfaction()
          + "   " + agent.getActionProposals().size() + " actions(s) to try");
    }

    // store the current state, and the best encountered state
    AgentState currentState = agent.buildCurrentState(null, null);
    AgentState bestEncounteredState = currentState;

    // mark the current state as the root state
    agent.setRootState(currentState);

    // test if the satisfaction is perfect
    if (agent.getSatisfaction() >= 100.0
        - TreeExplorationLifeCycle.getValiditySatisfactionTreshold()) {

      // perfect satisfaction
      if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
        TreeExplorationLifeCycle.logger.debug(
            " perfect initial state (S=" + agent.getSatisfaction() + ")");
      }

      // clean stored states
      if (!this.isStoreStates()) {
        agent.cleanStates();
      }

      // clean actions list
      agent.cleanActionsToTry();

      // end
      return AgentSatisfactionState.PERFECTLY_SATISFIED_INITIALY;
    }

    // a map to store the number of times an action has been applied to agent
    HashMap<Action, Integer> actionApplications = new HashMap<Action, Integer>();

    // the loop
    AgentSatisfactionState out = AgentSatisfactionState.SATISFACTION_UNCHANGED;
    while (true) {
      // test if no more actions to try
      if (agent.getActionProposals().size() == 0) {

        // test if there is a previous state
        if (currentState.getPreviousState() != null) {

          // there is a previous state: go back to this state, and try to
          // compute new actions
          if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
            TreeExplorationLifeCycle.logger.debug(
                "   No more action to try in current state: back to the previous state");
          }

          currentState = currentState.getPreviousState();
          agent.goBackToState(currentState);
          continue;
        }

        // there is no previous state: the current state is the initial one. The
        // whole tree has been explored.
        // all possible actions have been tried. The best possible state has
        // been reached.

        // back to the best encountered state
        agent.goBackToState(bestEncounteredState);
        if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
          TreeExplorationLifeCycle.logger
              .debug("******* End of activation of " + agent
                  + "; no more action to try. Back to the best encountered state (S="
                  + agent.getSatisfaction() + ")");
        }

        // clean stored states
        if (!this.isStoreStates()) {
          agent.cleanStates();
        }

        // clean actions list
        agent.cleanActionsToTry();

        return out;
      }

      // test if not too much states have been encountered
      if (this.getStatesMaxNumber() >= 0
          && agent.getStatesNumber() > this.getStatesMaxNumber()) {
        // too many states have been encountered: it is enougth! We stop here.

        // back to the best encountered state
        agent.goBackToState(bestEncounteredState);
        if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
          TreeExplorationLifeCycle.logger.debug("******* End of activation of "
              + agent + "; too many encountered states (S="
              + agent.getSatisfaction() + ")");
        }

        // clean stored states
        if (!this.isStoreStates()) {
          agent.cleanStates();
        }

        // clean actions list
        agent.cleanActionsToTry();

        return out;
      }

      // Debug - Log actions to try
      if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
        TreeExplorationLifeCycle.logger.debug("Actions to try :");
        for (ActionProposal debActionProp : agent.getActionProposals()) {
          TreeExplorationLifeCycle.logger.debug(debActionProp);
        }
      }
      // End debug

      // take the next action to try from the list
      ActionProposal bestActionProposal = agent.getBestActionProposal();
      Action actionToTry = bestActionProposal.getAction();
      agent.getActionProposals().remove(bestActionProposal);
      bestActionProposal.clean();
      // Debug
      if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
        TreeExplorationLifeCycle.logger
            .debug("Best action to try: " + actionToTry);
      }
      // End debug
      agent.getActionProposals().remove(actionToTry);

      // Debug - Log actions to try
      if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
        TreeExplorationLifeCycle.logger
            .debug("Actions to try after removing best action to try :");
        for (ActionProposal debActionProp : agent.getActionProposals()) {
          TreeExplorationLifeCycle.logger.debug(debActionProp);
        }
      }
      // End debug

      // test if the action has exceeded its restriction
      if (actionToTry.getRestriction() != -1) {
        int restriction = actionToTry.getRestriction();
        if (actionApplications.containsKey(actionToTry)) {
          if (actionApplications.get(actionToTry).intValue() >= restriction) {
            continue;
          }
        }
      }

      // increment the action applications
      if (!actionApplications.containsKey(actionToTry)) {
        actionApplications.put(actionToTry, new Integer(1));
      } else {
        actionApplications.put(actionToTry,
            new Integer(actionApplications.get(actionToTry).intValue() + 1));
      }

      if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
        TreeExplorationLifeCycle.logger.debug("   " + actionToTry.toString()
            + ", proposed by constraint "
            + actionToTry.getConstraint().getClass().getSimpleName() + " (S="
            + agent.getSatisfaction() + ", " + agent.getActionProposals().size()
            + " remaining action(s) to try)");
      }

      // trigger the action
      actionToTry.compute();

      // compute the new satisfaction
      agent.computeSatisfaction();

      // get the new actions from constraints
      agent.updateActionProposals();

      // build the new state
      currentState = agent.buildCurrentState(currentState, actionToTry);

      // notify the change to observers
      notifyChange();

      // perfect satisfaction
      if (agent.getSatisfaction() >= 100.0
          - TreeExplorationLifeCycle.getValiditySatisfactionTreshold()) {
        if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
          TreeExplorationLifeCycle.logger
              .debug("   SUCCESS -> perfect state reached (S="
                  + agent.getSatisfaction() + ")");
        }

        // clean stored states
        if (!this.isStoreStates()) {
          agent.cleanStates();
        }

        // clean actions list
        agent.cleanActionsToTry();

        return AgentSatisfactionState.PERFECTLY_SATISFIED_AFTER_TRANSFORMATION;
      }

      // valid state
      if (currentState.isValid(
          TreeExplorationLifeCycle.getValiditySatisfactionTreshold())) {
        if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
          TreeExplorationLifeCycle.logger.debug(
              "   SUCCESS -> valid state (S=" + agent.getSatisfaction() + ")");
        }
        if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
          TreeExplorationLifeCycle.logger.debug("   "
              + agent.getActionProposals().size() + " new action(s) to try");
        }
        out = AgentSatisfactionState.SATISFACTION_IMPROVED_BUT_NOT_PERFECT;

        // if the current state satisfaction is better than the best one, mark
        // the current state as the best encountered one
        if (currentState.getSatisfaction() > bestEncounteredState
            .getSatisfaction()) {
          if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
            TreeExplorationLifeCycle.logger
                .debug("   best encountered state !");
          }
          bestEncounteredState = currentState;
        }
      }

      // non valid state
      else {
        if (TreeExplorationLifeCycle.logger.isDebugEnabled()) {
          TreeExplorationLifeCycle.logger.debug(
              "   FAILURE -> non valid state (satisfaction worst or unchanged, S="
                  + agent.getSatisfaction() + ")");
        }

        // go back to the previous state
        currentState = currentState.getPreviousState();
        agent.goBackToState(currentState);
      }
    }
  }

  private Set<AgentObserver> observers = new HashSet<>();

  @Override
  public void attach(AgentObserver observer) {
    observers.add(observer);
  }

  @Override
  public void detach(AgentObserver observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyChange() {
    for (AgentObserver observer : observers) {
      observer.update();
    }
  }
}
