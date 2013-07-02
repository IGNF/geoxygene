package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;

/**
 * Jeux de données : lineaires et ponctuels
 * 
 *
 */
public class DatasetNetworkDataMatching {
  
  /** Classes d'arcs de la BD concernés par l'appariement. */
  private List<IFeatureCollection<? extends IFeature>> populationsArcs = null;
  
  /** Classes de noeuds de la BD concernés par l'appariement. */
  private List<IFeatureCollection<? extends IFeature>> populationsNoeuds = null;
  
  /**
   * Constructor.
   */
  public DatasetNetworkDataMatching() {
    populationsArcs = new ArrayList<IFeatureCollection<? extends IFeature>>();
    populationsNoeuds = new ArrayList<IFeatureCollection<? extends IFeature>>();
  }
  
  /**
   * 
   * @return
   */
  public List<IFeatureCollection<? extends IFeature>> getPopulationsArcs() {
    return populationsArcs;
  }
  
  /**
   * @param pop
   */
  public void setPopulationsArcs(List<IFeatureCollection<? extends IFeature>> pop) {
    populationsArcs = pop;
  }
  
  public void addPopulationsArcs(IPopulation<IFeature> popArc) {
    populationsArcs.add(popArc);
  }
  
  /**
   * 
   * @return
   */
  public List<IFeatureCollection<? extends IFeature>> getPopulationsNoeuds() {
    return populationsNoeuds;
  }
  
  /**
   * 
   * @param pop
   */
  public void setPopulationsNoeuds(List<IFeatureCollection<? extends IFeature>> pop) {
    populationsNoeuds = pop;
  }

}
