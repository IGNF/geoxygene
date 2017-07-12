package fr.ign.cogit.geoxygene.contrib.agents.agent;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.InternStructureAgentState;

/**
 * The meso agent interface. A meso agent is a group of geographic object
 * agents.
 * 
 * @author JGaffuri
 * 
 * @param <ComponentClass> The components class
 */
public interface InternStructureAgent extends GeographicObjectAgent {

  /**
   * @return The components of the meso.
   */
  List<? extends GeographicObjectAgent> getComponents();

  void setComponents(List<? extends GeographicObjectAgent> components);

  /**
   * @return The components satisfaction.
   */
  double getComponentsSatisfaction();

  /**
   * Return the best component to activate among a list of components.
   * 
   * @param componentsList
   * @return
   */
  GeographicObjectAgent getBestComponentToActivate(
      ArrayList<GeographicObjectAgent> componentsList);

  /**
   * manages the side effects inside the structure if necessary
   * @param geoObj: the last modified internal micro
   */
  void manageInternalSideEffects(GeographicObjectAgent geoObj);

  /**
   * @return The number of non deleted components of the structure
   */
  int getNonDeletedComponentsNumber();

  /**
   * @return The maximum area of the components.
   */
  double getComponentsMaximumArea();

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.GeographicObjectAgent#buildCurrentState
   * (fr.ign.cogit.agentgeoxygene.state.GeographicAgentState,
   * fr.ign.cogit.agentgeoxygene.action.Action)
   */
  @Override
  InternStructureAgentState buildCurrentState(AgentState previousState,
      Action action);

}
