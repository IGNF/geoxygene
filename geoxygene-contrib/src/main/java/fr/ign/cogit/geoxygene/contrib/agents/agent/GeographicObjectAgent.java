package fr.ign.cogit.geoxygene.contrib.agents.agent;

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicObjectAgentState;

/**
 * An object geographic agent (a building, a road, a block, etc.). Object
 * geographic agents are linked to a geographic object. They can belong to a
 * meso agent.
 * @author JGaffuri
 */
public interface GeographicObjectAgent extends IGeographicAgent {

  /**
   * @return The feature the agent is on
   */
  IFeature getFeature();

  /**
   * @param feature
   */
  void setFeature(IFeature feature);

  /**
   * @return The initial geometry
   */
  IGeometry getInitialGeom();

  /**
   * @param initialGeom
   */
  void setInitialGeom(IGeometry initialGeom);

  /**
   * @return The meso agent the agent possibly belong to
   */
  MesoAgent<? extends GeographicObjectAgent> getMesoAgent();

  /**
   * @param meso
   */
  void setMesoAgent(MesoAgent<? extends GeographicObjectAgent> meso);

  /**
   * @return The internal structures the agent possibly belong to
   */
  List<InternStructureAgent> getStructureAgents();

  /**
   * @param meso
   */
  void setStructureAgents(List<InternStructureAgent> structureAgents);

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.GeographicAgent#buildCurrentState(fr.
   * ign.cogit.agentgeoxygene.state.GeographicAgentState,
   * fr.ign.cogit.agentgeoxygene.action.Action)
   */
  @Override
  GeographicObjectAgentState buildCurrentState(AgentState previousState,
      Action action);

  /**
   * Go back to the initial state
   */
  void goBackToInitialState();

  /**
   * Delete the agent
   */
  void deleteAndRegister();

  /**
   * Displace the agent
   * 
   * @param dx
   * @param dy
   */
  void displaceAndRegister(double dx, double dy);

  /**
   * gets the geometry to the feature related to the agent
   * @return
   */
  IGeometry getGeom();

  /**
   * gets the geometry of the symbol to the feature related to the agent
   * @return
   */
  IGeometry getSymbolGeom();

  /**
   * gets the area of the symbol geometry
   */
  double getSymbolArea();

  /**
   * tests if the feature related to the agent is deleted
   * @return
   */
  boolean isDeleted();

  /**
   * gets the id of the agent
   */
  @Override
  int getId();

  /**
   * gets the population of the feature related to the agent
   * @return
   */
  IPopulation<? extends IFeature> getPopulation();

  /**
   * deletes the feature related to the agent
   * @param deleted
   */
  void setDeleted(boolean deleted);

  /**
   * sets a geometry to the feature related to the agent
   * @param g
   */
  void setGeom(IGeometry g);

  /**
   * sets an id to the agent
   */
  @Override
  void setId(int Id);

}
