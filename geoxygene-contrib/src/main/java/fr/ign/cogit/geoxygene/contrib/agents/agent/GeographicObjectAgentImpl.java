/**
 * 
 */
package fr.ign.cogit.geoxygene.contrib.agents.agent;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.agents.action.Action;
import fr.ign.cogit.geoxygene.contrib.agents.state.AgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicObjectAgentState;
import fr.ign.cogit.geoxygene.contrib.agents.state.GeographicObjectAgentStateImpl;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

/**
 * A default implementation of GeographicObjectAgent
 * @author JGaffuri
 */
public abstract class GeographicObjectAgentImpl extends GeographicAgent
    implements GeographicObjectAgent {

  /**
   * The feature the agent is on
   */
  private IFeature feature = null;

  /**
   * @return
   */
  @Override
  public IFeature getFeature() {
    return this.feature;
  }

  /**
   * @param feature
   */
  @Override
  public void setFeature(IFeature feature) {
    this.feature = feature;
  }

  /**
   * The initial geometry
   */
  private IGeometry initialGeom;

  /**
   * @return
   */
  @Override
  public IGeometry getInitialGeom() {
    return this.initialGeom;
  }

  /**
   * @param geomInitiale
   */
  @Override
  public void setInitialGeom(IGeometry geomInitiale) {
    this.initialGeom = geomInitiale;
  }

  /**
   * The meso agent the agent possibly belong to
   */
  private MesoAgent<? extends GeographicObjectAgent> mesoAgent = null;

  /**
   * @return
   */
  @Override
  public MesoAgent<? extends GeographicObjectAgent> getMesoAgent() {
    return this.mesoAgent;
  }

  /**
   * @param agentMesoControleur
   */
  @Override
  public void setMesoAgent(
      MesoAgent<? extends GeographicObjectAgent> agentMesoControleur) {
    this.mesoAgent = agentMesoControleur;
  }

  /**
   * The internal structures the agent possibly belong to
   */
  private List<InternStructureAgent> structureAgents = new ArrayList<InternStructureAgent>();

  @Override
  public List<InternStructureAgent> getStructureAgents() {
    return this.structureAgents;
  }

  @Override
  public void setStructureAgents(List<InternStructureAgent> structureAgents) {
    this.structureAgents = structureAgents;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.GeographicAgentImpl#buildCurrentState
   * (fr.ign.cogit.agentgeoxygene.state.AgentState,
   * fr.ign.cogit.agentgeoxygene.action.Action)
   */
  @Override
  public GeographicObjectAgentState buildCurrentState(AgentState previousState,
      Action action) {
    return new GeographicObjectAgentStateImpl(this,
        (GeographicObjectAgentState) previousState, action);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.agentgeoxygene.agent.GeographicAgentImpl#goBackToState(fr.
   * ign.cogit.agentgeoxygene.state.AgentState)
   */
  @Override
  public void goBackToState(AgentState state) {
    super.goBackToState(state);
    GeographicObjectAgentState state_ = (GeographicObjectAgentState) state;
    this.getFeature().setGeom(state_.getGeometry());
    this.getFeature().setDeleted(state_.isDeleted());
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.GeographicObjectAgent#
   * goBackToInitialState ()
   */
  @Override
  public void goBackToInitialState() {
    this.getFeature().setDeleted(false);
    this.getFeature().setGeom(this.getInitialGeom());

    if (this instanceof MesoAgent<?>) {
      MesoAgent<?> meso = (MesoAgent<?>) this;
      for (GeographicObjectAgent ag : meso.getComponents()) {
        ag.goBackToInitialState();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.GeographicObjectAgent#delete()
   */
  @Override
  public void deleteAndRegister() {
    // mark the agent as deleted
    this.getFeature().setDeleted(true);
    // delete its geometry
    this.getFeature().setGeom(null);
    // delete its components, if it is a meso
    if (this instanceof MesoAgent<?>) {
      for (GeographicObjectAgent ag : ((MesoAgent<?>) this).getComponents()) {
        ag.deleteAndRegister();
      }
    }
  }

  @Override
  public void displaceAndRegister(double dx, double dy) {
    // displace the agent's geometry
    this.getFeature().setGeom(
        CommonAlgorithms.translation(this.getFeature().getGeom(), dx, dy));
    // displace the components' geometries, if the agent is a meso
    if (this instanceof MesoAgent<?>) {
      for (GeographicObjectAgent ag : ((MesoAgent<?>) this).getComponents()) {
        ag.getFeature().setGeom(
            CommonAlgorithms.translation(ag.getFeature().getGeom(), dx, dy));
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.agentgeoxygene.agent.AgentImpl#printInfosConsole()
   */
  @Override
  public void printInfosConsole() {
    super.printInfosConsole();
    System.out
        .println("Feature: " + this.getFeature().getClass().getSimpleName());
    System.out.println("Geometry: " + this.getFeature().getGeom());
    System.out.println("Initial geometry: " + this.getInitialGeom());
    System.out.println("Deletion: " + this.getFeature().isDeleted());
    if (this.getMesoAgent() != null) {
      System.out
          .println("Meso: " + this.getMesoAgent().getClass().getSimpleName()
              + " - id =" + this.getMesoAgent().getId());
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getGeom()
   */
  @Override
  public IGeometry getGeom() {
    return this.feature.getGeom();
  }

  @Override
  public IGeometry getSymbolGeom() {
    return this.getGeom();
  }

  @Override
  public double getSymbolArea() {
    return this.getSymbolGeom().area();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#isDeleted()
   */
  @Override
  public boolean isDeleted() {
    return this.feature.isDeleted();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getId()
   */
  @Override
  public int getId() {
    return this.feature.getId();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#getPopulation()
   */
  @Override
  public IPopulation<? extends IFeature> getPopulation() {
    return this.feature.getPopulation();
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#setEstSupprime(boolean)
   */
  @Override
  public void setDeleted(boolean deleted) {
    this.feature.setDeleted(deleted);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * fr.ign.cogit.geoxygene.api.feature.IFeature#setGeom(fr.ign.cogit.geoxygene
   * .api.spatial.geomroot.IGeometry)
   */
  @Override
  public void setGeom(IGeometry g) {
    this.feature.setGeom(g);
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.ign.cogit.geoxygene.api.feature.IFeature#setId(int)
   */
  @Override
  public void setId(int Id) {
    this.feature.setId(Id);
  }

}
