package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/**
 * Dataset to match.
 * 
 * - populationsArcs1
 * - populationsNoeuds1
 * - populationsArcs2
 * - populationsNoeuds2
 *
 */
public class ParamDatasetNetworkDataMatching {
  
  /** Classes d'arcs de la BD 1 concernés par l'appariement. */
  private List<IFeatureCollection<? extends IFeature>> populationsArcs1 = null;

  /** Classes de noeuds de la BD 1 concernés par l'appariement. */
  private List<IFeatureCollection<? extends IFeature>> populationsNoeuds1 = null;

  /** Classes d'arcs de la BD 2 concernés par l'appariement. */
  private List<IFeatureCollection<? extends IFeature>> populationsArcs2 = null;

  /** Classes de noeuds de la BD 2 (la plus détaillée) concernés par l'appariement. */
  private List<IFeatureCollection<? extends IFeature>> populationsNoeuds2 = null;

  /**
   * Constructor.
   */
  public ParamDatasetNetworkDataMatching() {
    populationsArcs1 = new ArrayList<IFeatureCollection<? extends IFeature>>();
    populationsNoeuds1 = new ArrayList<IFeatureCollection<? extends IFeature>>();
    populationsArcs2 = new ArrayList<IFeatureCollection<? extends IFeature>>();
    populationsNoeuds2 = new ArrayList<IFeatureCollection<? extends IFeature>>();
  }
  
  /**
   * 
   * @return
   */
  public List<IFeatureCollection<? extends IFeature>> getPopulationsArcs1() {
    return populationsArcs1;
  }
  
  /**
   * 
   * @param pop
   */
  public void setPopulationsArcs1(List<IFeatureCollection<? extends IFeature>> pop) {
    populationsArcs1 = pop;
  }
  
  /**
   * 
   * @return
   */
  public List<IFeatureCollection<? extends IFeature>> getPopulationsArcs2() {
    return populationsArcs2;
  }
  
  /**
   * 
   * @param pop
   */
  public void setPopulationsArcs2(List<IFeatureCollection<? extends IFeature>> pop) {
    populationsArcs2 = pop;
  }
  
  /**
   * 
   * @return
   */
  public List<IFeatureCollection<? extends IFeature>> getPopulationsNoeuds1() {
    return populationsNoeuds1;
  }
  
  /**
   * 
   * @param pop
   */
  public void setPopulationsNoeuds1(List<IFeatureCollection<? extends IFeature>> pop) {
    populationsNoeuds1 = pop;
  }
  
  /**
   * 
   * @return
   */
  public List<IFeatureCollection<? extends IFeature>> getPopulationsNoeuds2() {
    return populationsNoeuds2;
  }
  
  /**
   * 
   * @param pop
   */
  public void setPopulationsNoeuds2(List<IFeatureCollection<? extends IFeature>> pop) {
    populationsNoeuds2 = pop;
  }
}
