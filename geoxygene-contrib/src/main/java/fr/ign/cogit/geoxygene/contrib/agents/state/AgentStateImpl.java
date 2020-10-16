package fr.ign.cogit.geoxygene.contrib.agents.state;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

/**
 * An agent state
 * @author julien Gaffuri 7 juil. 2007
 */
public class AgentStateImpl implements AgentState {
  private static Logger logger = LogManager.getLogger(AgentState.class.getName());

  /**
   * The agent the state is linked to
   */
  private IAgent agent;

  /**
   * @return
   */
  public IAgent getAgent() {
    return this.agent;
  }

  /**
   * The state satisfaction value
   */
  private double satisfaction;

  /**
   * @return
   */
  public double getSatisfaction() {
    return this.satisfaction;
  }

  /**
   * @param satisfaction
   */
  public void setSatisfaction(double satisfaction) {
    this.satisfaction = satisfaction;
  }

  /**
   * The previous state
   */
  private AgentState previousState = null;

  /**
   * @return
   */
  public AgentState getPreviousState() {
    return this.previousState;
  }

  /**
   * The child states
   */
  private ArrayList<AgentState> childStates = new ArrayList<AgentState>();

  /**
   * @return
   */
  public ArrayList<AgentState> getChildStates() {
    return this.childStates;
  }

  /**
   * The action triggered on the agent to get the state
   */
  private Action action;

  /**
   * @return
   */
  public Action getAction() {
    return this.action;
  }

  /**
   * The collection of actions proposed by the constraint to the geographic
   * agent in its current state
   */
  private Set<ActionProposal> actionsToTry = null;

  /**
   * @return
   */
  public Set<ActionProposal> getActionsToTry() {
    return this.actionsToTry;
  }

  /**
   * @param actionsToTry
   */
  public void setActionsToTry(Set<ActionProposal> actionsToTry) {
    this.actionsToTry = actionsToTry;
  }

  /**
   * Build an agent state
   * 
   * @param agent
   */
  public AgentStateImpl(IAgent agent, AgentState previousState, Action action) {
    this.agent = agent;
    this.satisfaction = agent.getSatisfaction();
    this.actionsToTry = agent.getActionProposals();
    this.action = action;
    this.previousState = previousState;
    if (previousState != null) {
      previousState.getChildStates().add(this);
    }
  }

  public boolean isValid(double treshold) {
    AgentStateImpl.logger.warn("Non implementes method - " + treshold);
    return false;
  }

  /**
   * Clean the state
   */
  public void clean() {
    this.agent = null;
    if (this.valeursMesures != null) {
      this.valeursMesures.clear();
    }
    this.valeursMesures = null;
    if (this.applicationsActions != null) {
      this.applicationsActions.clear();
    }
    this.applicationsActions = null;

    if (this.action != null) {
      this.action.clean();
      this.action = null;
    }

    if (this.actionsToTry != null) {
      for (ActionProposal act : this.actionsToTry) {
        act.clean();
      }
      this.actionsToTry = null;
    }

    this.previousState = null;
    this.childStates = null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.getAction() + " S=" + this.getSatisfaction() + " ("
        + this.getActionsToTry().size() + ")";
  }

  /* DEBUT AJOUT PATRICK */

  // Agent connaissance priorité lié à la classe de la contrainte traitée
  /**
     */
  protected Map<String, Object> valeursMesures = new Hashtable<String, Object>();

  /**
   * @return
   */
  public Map<String, Object> getValeursMesures() {
    return this.valeursMesures;
  }

  /**
     */
  protected Map<String, Integer> applicationsActions = new Hashtable<String, Integer>();

  /**
   * @return
   */
  public Map<String, Integer> getApplicationsActions() {
    return this.applicationsActions;
  }

  public void ajouteApplicationAction(String nomAction) {
    Integer nbAppli = this.applicationsActions.get(nomAction);
    if (nbAppli == null) {
      this.applicationsActions.put(nomAction, new Integer(1));
    } else {
      this.applicationsActions.put(nomAction,
          new Integer(nbAppli.intValue() + 1));
    }
  }

  /* FIN AJOUT PATRICK */

}
