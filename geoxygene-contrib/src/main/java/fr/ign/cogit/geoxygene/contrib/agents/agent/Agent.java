/*
 * Créé le 18 sept. 2006
 */
package fr.ign.cogit.geoxygene.contrib.agents.agent;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.action.ActionProposal;
import fr.ign.cogit.geoxygene.contrib.agents.constraint.Constraint;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.AgentLifeCycle;
import fr.ign.cogit.geoxygene.contrib.agents.lifecycle.TreeExplorationLifeCycle;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentStateImpl;

/**
 * An implementation of the Agent interface
 * @author JGaffuri
 */
public class Agent implements IAgent {
  private static Logger logger = Logger.getLogger(Agent.class.getName());

  /**
   * Default constructor that defines a default Border Strategy
   */
  public Agent() {
    super();
  }

  /**
   * the agent beeing activated
   */
  private static IAgent activatedAgent = null;

  /**
   * @return
   */
  public static IAgent getActivatedAgent() {
    return Agent.activatedAgent;
  }

  /**
   * @param agent
   */
  public static void setActivatedAgent(IAgent agent) {
    Agent.activatedAgent = agent;
  }

  /**
   * The agent's id
   */
  private int id = -1;

  /**
   * @return
   */
  @Override
  public int getId() {
    return this.id;
  }

  /**
   * @param id
   */
  @Override
  public void setId(int id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.Agent#activate()
   */
  @Override
  public AgentSatisfactionState activate() throws InterruptedException {
    IAgent a = Agent.getActivatedAgent();
    Agent.setActivatedAgent(this);

    // activation of the agent
    logger
        .debug("Agent " + this + " activate LifeCycle " + this.getLifeCycle());
    AgentSatisfactionState out = this.getLifeCycle().compute(this);
    Agent.setActivatedAgent(a);
    // for (LayerViewPanel lvp : CartAGenPlugin.getInstance().getApplication()
    // .getMainFrame().getSelectedProjectFrame().getLayerViewPanel()
    // .getViewport().getLayerViewPanels()) {
    // lvp.repaint();
    // }

    return out;
  }

  /**
   * The agent's life cycle
   */
  private AgentLifeCycle lifeCycle = TreeExplorationLifeCycle.getInstance();

  /**
   * @return
   */
  @Override
  public AgentLifeCycle getLifeCycle() {
    return this.lifeCycle;
  }

  /**
   * @param lifeCycle
   */
  @Override
  public void setLifeCycle(AgentLifeCycle lifeCycle) {
    this.lifeCycle = lifeCycle;
  }

  // statisfaction

  /**
   * the agent satisfaction value
   */
  private double satisfaction;

  /**
   * @return
   */
  @Override
  public double getSatisfaction() {
    return this.satisfaction;
  }

  /**
   * @param s
   */
  @Override
  public void setSatisfaction(double s) {
    this.satisfaction = s;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.Agent#computeSatisfaction()
   */
  @Override
  public void computeSatisfaction() {
    Agent.logger
        .warn("Non implemented method in " + this.getClass().getSimpleName());
  }

  // constraints

  /**
   * the agent constraints
   */
  private HashSet<Constraint> constraints = new HashSet<Constraint>();

  /**
   * @return
   */
  @Override
  public HashSet<Constraint> getConstraints() {
    return this.constraints;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.Agent#getActionsFromConstraints()
   */
  @Override
  public void updateActionProposals() {
    Agent.logger
        .warn("Non implemented method in " + this.getClass().getSimpleName());
  }

  // actions

  /**
   * The actions to try
   */
  private Set<ActionProposal> actionsToTry = new HashSet<ActionProposal>();

  /**
   * @return
   */
  @Override
  public Set<ActionProposal> getActionProposals() {
    return this.actionsToTry;
  }

  /**
   * @param actionsToTry
   */
  @Override
  public void setActionsToTry(Set<ActionProposal> actionsToTry) {
    this.actionsToTry = actionsToTry;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.Agent#getBestActionToTry()
   */
  @Override
  public ActionProposal getBestActionProposal() {
    Agent.logger.warn("Non implemented method");
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.Agent#cleanActionsToTry()
   */
  @Override
  public void cleanActionsToTry() {
    if (this.getActionProposals().isEmpty()) {
      return;
    }
    for (ActionProposal a : this.getActionProposals()) {
      a.clean();
    }
    this.getActionProposals().clear();
  }

  // states

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.Agent#buildCurrentState(fr.ign.cogit.
   * agentgeoxygene.state.AgentState, fr.ign.cogit.agentgeoxygene.action.Action)
   */
  @Override
  public AgentState buildCurrentState(AgentState previousState, Action action) {
    return new AgentStateImpl(this, previousState, action);
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.agentgeoxygene.agent.Agent#goBackToState(fr.ign.cogit.
   * agentgeoxygene.state.AgentState)
   */
  @Override
  public void goBackToState(AgentState state) {
    this.setSatisfaction(state.getSatisfaction());
    this.setActionsToTry(state.getActionsToTry());
  }

  // states tree

  /**
   * The agent's root state. The whole states tree can be retrieved by using the
   * getPreviousState and getChildStates methods.
   */
  private AgentState rootState;

  /**
   * @return
   */
  @Override
  public AgentState getRootState() {
    return this.rootState;
  }

  /**
   * @param rootState
   */
  @Override
  public void setRootState(AgentState rootState) {
    this.rootState = rootState;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.Agent#getStatesNumber()
   */
  @Override
  public int getStatesNumber() {
    return this.getStatesNumber(this.getRootState());
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.cogit.agentgeoxygene.agent.Agent#getStatesNumber(fr.ign.cogit.
   * agentgeoxygene.state.AgentState)
   */
  @Override
  public int getStatesNumber(AgentState state) {
    if (state == null) {
      return 0;
    }
    if (state.getChildStates() == null || state.getChildStates().size() == 0) {
      return 1;
    }

    // recursive call
    int nb = 1;
    for (AgentState e : state.getChildStates()) {
      nb += this.getStatesNumber(e);
    }
    return nb;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.Agent#cleanStates()
   */
  @Override
  public void cleanStates() {
    if (this.getRootState() == null) {
      return;
    }
    this.cleanSubTree(this.getRootState());
    this.rootState = null;
  }

  /**
   * @param state
   */
  @Override
  public void cleanSubTree(AgentState state) {
    // clean the state
    state.clean();

    // if there are no child states, go out
    if (state.getChildStates() == null || state.getChildStates().size() == 0) {
      return;
    }

    // clean the child sub trees (recursive call)
    for (AgentState childState : state.getChildStates()) {
      this.cleanSubTree(childState);
    }
  }

  // other

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.Agent#clean()
   */
  @Override
  public void clean() {
    if (this.getConstraints() != null) {
      this.getConstraints().clear();
    }
    if (this.getActionProposals() != null) {
      for (ActionProposal a : this.getActionProposals()) {
        a.clean();
      }
      this.getActionProposals().clear();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.Agent#printInfosConsole()
   */
  @Override
  public void printInfosConsole() {
    this.computeSatisfaction();
    System.out.println(
        "Agent: " + this.getClass().getSimpleName() + " - Id=" + this.getId());
    System.out.println("Satisfaction: " + this.getSatisfaction());
    System.out.println(
        "Life cycle: " + this.getLifeCycle().getClass().getSimpleName());
    System.out.println("Constraints: nb=" + this.getConstraints().size());
    for (Constraint c : this.getConstraints()) {
      System.out.print("   ");
      c.printInfosConsole();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run() {
    try {
      this.getLifeCycle().compute(this);
    } catch (InterruptedException e) {
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + Integer.toString(this.hashCode());
  }

}
