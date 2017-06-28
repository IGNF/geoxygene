/**
 * 
 */
package fr.ign.cogit.geoxygene.contrib.agents.lifecycle;

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
 * The life cycle with only one backward (according to [Ruas and Plazanet 1996])
 * @author jgaffuri
 */
public class BasicLifeCycle implements AgentLifeCycle, AgentObservationSubject {
  private static Logger logger = Logger
      .getLogger(BasicLifeCycle.class.getName());

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
  private static BasicLifeCycle instance = null;

  /**
   * @return The unique instance.
   */
  public static BasicLifeCycle getInstance() {
    if (BasicLifeCycle.instance == null) {
      BasicLifeCycle.instance = new BasicLifeCycle();
    }
    return BasicLifeCycle.instance;
  }

  private BasicLifeCycle() {
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
    return BasicLifeCycle.validitySatisfactionTreshold;
  }

  /**
   * @param validitySatisfactionTreshold
   */
  public static void setValiditySatisfactionTreshold(
      double validitySatisfactionTreshold) {
    BasicLifeCycle.validitySatisfactionTreshold = validitySatisfactionTreshold;
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
    if (BasicLifeCycle.logger.isDebugEnabled()) {
      BasicLifeCycle.logger.debug("******* Activation of " + agent + " ("
          + agent.getConstraints().size() + " constraints )");
    }

    // clean the possibly previously created and stored states
    agent.cleanStates();

    // satisfaction computation
    agent.computeSatisfaction();

    // get actions from constraints
    agent.updateActionProposals();

    // store current state
    AgentState currentState = agent.buildCurrentState(null, null);

    // if states storage required, store the current state as a root state
    if (this.isStoreStates()) {
      agent.setRootState(currentState);
    }

    if (BasicLifeCycle.logger.isDebugEnabled()) {
      BasicLifeCycle.logger.debug(" S=" + agent.getSatisfaction() + "   "
          + agent.getActionProposals().size() + " action(s) to try");
    }

    // test if the satisfaction is perfect
    if (agent.getSatisfaction() >= 100.0
        - BasicLifeCycle.getValiditySatisfactionTreshold()) {

      // perfect satisfaction
      if (BasicLifeCycle.logger.isDebugEnabled()) {
        BasicLifeCycle.logger.debug(
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

    // the agent is not satisfied: trie to improve its satisfaction by
    // triggering some actions
    AgentSatisfactionState out = AgentSatisfactionState.SATISFACTION_UNCHANGED;
    while (true) {

      // test if there are actions to try
      if (agent.getActionProposals().size() == 0) {
        // all possible action have been tried: the best possible state as been
        // reached.
        if (BasicLifeCycle.logger.isDebugEnabled()) {
          BasicLifeCycle.logger.debug("******* End of activation of " + agent
              + "; No more action to try. Best possible state reached (S="
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
        if (BasicLifeCycle.logger.isDebugEnabled()) {
          BasicLifeCycle.logger.debug("******* End of activation of " + agent
              + "; too many encountered states (S=" + agent.getSatisfaction()
              + ")");
        }

        // clean stored states
        if (!this.isStoreStates()) {
          agent.cleanStates();
        }

        // clean actions list
        agent.cleanActionsToTry();

        return out;
      }

      // take the next action to try from the list
      ActionProposal bestActionProposal = agent.getBestActionProposal();
      Action actionToTry = bestActionProposal.getAction();
      agent.getActionProposals().remove(bestActionProposal);
      bestActionProposal.clean();

      if (BasicLifeCycle.logger.isDebugEnabled()) {
        BasicLifeCycle.logger.debug("   " + actionToTry.toString()
            + ", proposed by constraint "
            + actionToTry.getConstraint().getClass().getSimpleName() + " (S="
            + agent.getSatisfaction() + ", " + agent.getActionProposals().size()
            + " remaining action(s) to try)");
      }

      // trigger the action
      actionToTry.compute();
      actionToTry.clean();

      // compute the new satisfaction
      agent.computeSatisfaction();

      // get the new actions from constraints
      agent.updateActionProposals();

      // build the new state
      currentState = agent.buildCurrentState(currentState, actionToTry);

      // notify change to observers
      notifyChange();

      // perfect satisfaction
      if (agent.getSatisfaction() >= 100.0
          - BasicLifeCycle.getValiditySatisfactionTreshold()) {
        if (BasicLifeCycle.logger.isDebugEnabled()) {
          BasicLifeCycle.logger.debug("   SUCCESS -> perfect state reached (S="
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
      if (currentState
          .isValid(BasicLifeCycle.getValiditySatisfactionTreshold())) {
        if (BasicLifeCycle.logger.isDebugEnabled()) {
          BasicLifeCycle.logger.debug(
              "   SUCCESS -> valid state (S=" + agent.getSatisfaction() + ")");
        }
        if (BasicLifeCycle.logger.isDebugEnabled()) {
          BasicLifeCycle.logger.debug("   " + agent.getActionProposals().size()
              + " new action(s) to try");
        }
        out = AgentSatisfactionState.SATISFACTION_IMPROVED_BUT_NOT_PERFECT;
      }

      // non valid state
      else {
        if (BasicLifeCycle.logger.isDebugEnabled()) {
          BasicLifeCycle.logger.debug(
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
      if (observer.isSlowMotion()) {
        try {
          this.wait(100);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
