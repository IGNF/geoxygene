/**
 * 
 */
package fr.ign.cogit.geoxygene.contrib.agents.agent;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.GeographicConstraint;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicAgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicAgentStateImpl;

/**
 * @author JGaffuri
 * 
 */
public abstract class GeographicAgent extends Agent
    implements IGeographicAgent {

  private static Logger logger = LogManager
      .getLogger(GeographicAgent.class.getName());

  /**
   * Default constructor that defines a default Border Strategy
   */
  public GeographicAgent() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.AgentImpl#buildCurrentState(fr.ign.cogit
   * .agentgeoxygene.state.AgentState,
   * fr.ign.cogit.agentgeoxygene.action.Action)
   */
  @Override
  public GeographicAgentState buildCurrentState(AgentState previousState,
      Action action) {
    return new GeographicAgentStateImpl(this,
        (GeographicAgentState) previousState, action);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.AgentImpl#getActionsFromConstraints()
   */
  @Override
  public void updateActionProposals() {
    if (GeographicAgent.logger.isDebugEnabled()) {
      GeographicAgent.logger
          .debug("get actions from non satified constraints of " + this);
    }

    if (this instanceof GeographicObjectAgent
        && ((GeographicObjectAgent) this).getFeature().isDeleted()) {
      return;
    }

    // get action proposals from non satified constraints and compute priority
    // values
    this.setActionsToTry(new HashSet<ActionProposal>());

    for (Constraint cont : this.getConstraints()) {
      GeographicConstraint contGeo = (GeographicConstraint) cont;

      // if the constraint is satisfied, continue
      if (contGeo.getSatisfaction() >= 100.0) {
        if (GeographicAgent.logger.isDebugEnabled()) {
          GeographicAgent.logger.debug(
              "	" + contGeo.getClass().getSimpleName() + " (satisfied)");
        }
        continue;
      }

      if (GeographicAgent.logger.isDebugEnabled()) {
        GeographicAgent.logger.debug("	" + contGeo.getClass().getSimpleName()
            + " sat: " + contGeo.getSatisfaction());
      }

      // else, compute proirity and propose actions
      contGeo.computePriority();
      Set<ActionProposal> actionProposals = contGeo.getActions();

      if (GeographicAgent.logger.isDebugEnabled()) {
        GeographicAgent.logger.debug("  " + contGeo.getClass().getSimpleName()
            + " action proposals : " + actionProposals);
      }

      if (actionProposals != null) {
        this.getActionProposals().addAll(actionProposals);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.AgentImpl#getBestActionToTry()
   */
  @Override
  public ActionProposal getBestActionProposal() {
    if (GeographicAgent.logger.isDebugEnabled()) {
      GeographicAgent.logger
          .debug("Choose the best action to try by the agent: " + this);
    }

    // the best action to try is the one whose proposing constraint has 1/ the
    // higher priority and 2/ the higher weight

    double maxPriority = Double.NEGATIVE_INFINITY;
    double maxWeight = Double.NEGATIVE_INFINITY;
    ActionProposal bestActionProposal = null;

    for (ActionProposal actionProposal : this.getActionProposals()) {

      // check the priority
      if (GeographicAgent.logger.isTraceEnabled()) {
        GeographicAgent.logger
            .trace("   action: " + actionProposal + " priority= "
                + ((GeographicConstraint) actionProposal.getHandledConstraint())
                    .getPriority());
      }
      if (((GeographicConstraint) actionProposal.getHandledConstraint())
          .getPriority() < maxPriority) {
        continue;
      }

      // check the weight
      if (GeographicAgent.logger.isTraceEnabled()) {
        GeographicAgent.logger
            .trace("   weight= " + actionProposal.getWeight());
      }
      // edit Guillaume: gros bug car une contrainte avec une plus grande
      // priorité mais une importance égale n'est pas choisie à cause du test
      // sur l'importance. Ce test n'est valable que si les priorités sont
      // égales
      if (((GeographicConstraint) actionProposal.getHandledConstraint())
          .getPriority() == maxPriority
          && actionProposal.getWeight() <= maxWeight) {
        continue;
      }

      if (GeographicAgent.logger.isTraceEnabled()) {
        GeographicAgent.logger.trace("      best action!");
      }
      bestActionProposal = actionProposal;
      maxPriority = ((GeographicConstraint) actionProposal
          .getHandledConstraint()).getPriority();
      maxWeight = actionProposal.getWeight();
    }

    if (GeographicAgent.logger.isTraceEnabled() && bestActionProposal != null) {
      GeographicAgent.logger
          .trace("action: " + bestActionProposal + " priority= "
              + ((GeographicConstraint) bestActionProposal
                  .getHandledConstraint()).getPriority()
              + " weight= " + bestActionProposal.getWeight());
    }
    return bestActionProposal;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.AgentImpl#computeSatisfaction()
   */
  @Override
  public void computeSatisfaction() {
    // the mean of the constraints satisfactions, weighted by the importance

    int n = this.getConstraints().size();
    if (GeographicAgent.logger.isDebugEnabled()) {
      GeographicAgent.logger.debug("satisfaction computation of the agent "
          + this + " (constraints nb=" + n + ")");
    }

    // if the agent has no constraint or is deleted, so it is perfectly
    // satisfied
    if (n == 0 || this instanceof GeographicObjectAgent
        && ((GeographicObjectAgent) this).getFeature().isDeleted()) {
      this.setSatisfaction(100.0);
      if (GeographicAgent.logger.isDebugEnabled()) {
        GeographicAgent.logger.debug("   S=100");
      }
      return;
    }

    double sum = 0.0;
    double impSum = 0.0;
    for (Constraint cont : this.getConstraints()) {
      GeographicConstraint cont_ = (GeographicConstraint) cont;
      cont_.computeSatisfaction();
      sum += cont_.getImportance() * cont_.getSatisfaction();
      impSum += cont_.getImportance();
      if (GeographicAgent.logger.isTraceEnabled()) {
        GeographicAgent.logger
            .trace("   Cont: " + cont_.getClass().getSimpleName() + " imp="
                + cont_.getImportance() + " s=" + cont_.getSatisfaction());
      }
    }

    if (impSum == 0) {
      this.setSatisfaction(100.0);
    } else {
      this.setSatisfaction(sum / impSum);
    }

    if (this.getSatisfaction() > 100) {
      GeographicAgent.logger.warn(
          "problem during the satisfaction computation: number greater than 100 :"
              + this.getSatisfaction());
    }

    if (GeographicAgent.logger.isDebugEnabled()) {
      GeographicAgent.logger.debug("   S=" + this.getSatisfaction());
    }
  }

}
