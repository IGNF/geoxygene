package fr.ign.cogit.geoxygene.contrib.agents.state;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.agent.IAgent;

/**
 * An agent state
 * @author julien Gaffuri 7 juil. 2007
 */
public interface AgentState {

  /**
   * @return
   */
  public IAgent getAgent();

  /**
   * @return
   */
  public double getSatisfaction();

  /**
   * @param satisfaction
   */
  public void setSatisfaction(double satisfaction);

  /**
   * @return
   */
  public AgentState getPreviousState();

  /**
   * @return
   */
  public ArrayList<AgentState> getChildStates();

  /**
   * @return
   */
  public Action getAction();

  /**
   * @return
   */
  public Set<ActionProposal> getActionsToTry();

  /**
   * @param actionsToTry
   */
  public void setActionsToTry(Set<ActionProposal> actionsToTry);

  public boolean isValid(double treshold);

  /**
   * Clean the state
   */
  public void clean();

  /* DEBUT AJOUT PATRICK */

  /**
   * @return
   */
  public Map<String, Object> getValeursMesures();

  /**
   * @return
   */
  public Map<String, Integer> getApplicationsActions();

  public void ajouteApplicationAction(String nomAction);

  /* FIN AJOUT PATRICK */

}
