/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;

/**
 * Dataset to match.
 * 
 * - populationsArcs1
 * - (populationsNoeuds1)
 * - populationsArcs2
 * - (populationsNoeuds2)
 *
 */
public class ParamDatasetNetworkDataMatching {
  
  /** Classes d'arcs de la BD 1 concernés par l'appariement. */
  private List<IFeatureCollection<? extends IFeature>> populationsArcs1 = null;

  /** Classes de noeuds de la BD 1 concernés par l'appariement. */
  // private List<IFeatureCollection<? extends IFeature>> populationsNoeuds1 = null;

  /** Classes d'arcs de la BD 2 concernés par l'appariement. */
  private List<IFeatureCollection<? extends IFeature>> populationsArcs2 = null;

  /** Classes de noeuds de la BD 2 (la plus détaillée) concernés par l'appariement. */
  // private List<IFeatureCollection<? extends IFeature>> populationsNoeuds2 = null;

  /**
   * Constructor.
   */
  public ParamDatasetNetworkDataMatching() {
    populationsArcs1 = new ArrayList<IFeatureCollection<? extends IFeature>>();
    // populationsNoeuds1 = new ArrayList<IFeatureCollection<? extends IFeature>>();
    populationsArcs2 = new ArrayList<IFeatureCollection<? extends IFeature>>();
    // populationsNoeuds2 = new ArrayList<IFeatureCollection<? extends IFeature>>();
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
  
  public void addPopulationsArcs1(IPopulation<IFeature> popArc) {
    populationsArcs1.add(popArc);
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
  
  public void addPopulationsArcs2(IPopulation<IFeature> popArc) {
    populationsArcs2.add(popArc);
  }
  
  /**
   * 
   * @return
   */
  //public List<IFeatureCollection<? extends IFeature>> getPopulationsNoeuds1() {
  //  return populationsNoeuds1;
  //}
  
  /**
   * 
   * @param pop
   */
  //public void setPopulationsNoeuds1(List<IFeatureCollection<? extends IFeature>> pop) {
  //  populationsNoeuds1 = pop;
  //}
  
  /**
   * 
   * @return
   */
  //public List<IFeatureCollection<? extends IFeature>> getPopulationsNoeuds2() {
  //  return populationsNoeuds2;
  //}
  
  /**
   * 
   * @param pop
   */
  //public void setPopulationsNoeuds2(List<IFeatureCollection<? extends IFeature>> pop) {
  //  populationsNoeuds2 = pop;
  //}
}
