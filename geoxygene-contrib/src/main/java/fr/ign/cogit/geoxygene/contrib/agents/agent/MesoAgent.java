package fr.ign.cogit.geoxygene.contrib.agents.agent;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.MesoAgentState;

/**
 * The meso agent interface. A meso agent is a group of geographic object
 * agents.
 * 
 * @author JGaffuri
 * 
 * @param <ComponentClass> The components class
 */
public interface MesoAgent<ComponentClass extends GeographicObjectAgent>
    extends GeographicObjectAgent {

  /**
   * @return The components of the meso.
   */
  List<ComponentClass> getComponents();

  void setComponents(List<ComponentClass> components);

  /**
   * @return The components satisfaction.
   */
  double getComponentsSatisfaction();

  /**
   * @return The intern structure satisfaction.
   */
  double getInternStructuresSatisfaction();

  /**
   * Return the best component to activate among a list of components.
   * 
   * @param componentsList
   * @return
   */
  ComponentClass getBestComponentToActivate(
      ArrayList<ComponentClass> componentsList);

  /**
   * Return the best structure to activate among a list of intern structures
   * 
   * @param structuresList
   * @return
   */
  InternStructureAgent getBestInternStructureToActivate(
      ArrayList<InternStructureAgent> structuresList);

  /**
   * manages the side effects inside the meso if necessary
   * @param geoObj: the last modified internal micro
   */
  void manageInternalSideEffects(GeographicObjectAgent geoObj);

  /**
   * @return The number of non deleted components of the meso.
   */
  int getNonDeletedComponentsNumber();

  /**
   * @return The maximum area of the components.
   */
  double getComponentsMaximumArea();

  /**
   * @return The potential intermediate structures of the meso
   */
  List<InternStructureAgent> getInternStructures();

  void setInternStructures(List<InternStructureAgent> internStructures);

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.GeographicObjectAgent#buildCurrentState
   * (fr.ign.cogit.agentgeoxygene.state.GeographicAgentState,
   * fr.ign.cogit.agentgeoxygene.action.Action)
   */
  @Override
  MesoAgentState buildCurrentState(AgentState previousState, Action action);
}
