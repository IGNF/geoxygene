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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;

/**
 * Traitements topologiques à l'import.
 * 
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "seuilFusionNoeuds",
    "surfacesFusionNoeuds",
    "elimineNoeudsAvecDeuxArcs",
    "graphePlanaire",
    "fusionArcsDoubles"
})
@XmlRootElement(name = "ParamTopoTreatmentNetworkDataMatching")
public class ParamTopologyTreatmentNetwork {
  
  /**
   * Les noeuds proches du réseau sont fusionés en un seul noeud (proches =
   * éloignés de moins que ce paramètre). Si ce paramètre est > 0, les noeuds
   * sont fusionés à une position moyenne, et les arcs sont déformés pour
   * suivre. Si ce paramètre est = 0, seul les noeuds strictement superposés sont
   * fusionés. Si ce paramètre est < 0 (défaut), aucune fusion n'est faite.
   */
  @XmlElement(name = "SeuilFusionNoeuds")
  private double seuilFusionNoeuds = -1;
  
  /**
   * Les noeuds du réseau contenus dans une même surface de la population en
   * paramètre seront fusionés en un seul noeud pour l'appariement. Ce paramètre
   * peut être null (défaut), auquel il est sans influence. Exemple typique: on
   * fusionne toutes les extrémités de lignes ferrés arrivant dans une même aire
   * de triage, si les aires de triage sont définies par des surfaces dans les
   * données.
   */
  @XmlElement(name = "SurfacesFusionNoeuds")
  private IPopulation<?> surfacesFusionNoeuds;
  
  /**
   * Doit-on eliminer pour l'appariement les noeuds du réseau qui n'ont que 2
   * arcs incidents et fusionner ces arcs ?
   */
  @XmlElement(name = "ElimineNoeudsAvecDeuxArcs")
  private boolean elimineNoeudsAvecDeuxArcs;
  
  /**
   * Doit-on rendre le réseau planaire ? 
   *   i.e. créer des noeuds aux intersections d'arcs
   */
  @XmlElement(name = "GraphePlanaire")
  private boolean graphePlanaire;
  
  /**
   * Fusion des arcs doubles. Si true: si le réseau contient des arcs en
   * double (même géométrie exactement), alors on les fusionne en un seul arc
   * pour l'appariement. Si false: rien n'est fait.
   */
  @XmlElement(name = "FusionArcsDoubles")
  private boolean fusionArcsDoubles;
  
  /**
   * Constructor.
   */
  public ParamTopologyTreatmentNetwork() {
    seuilFusionNoeuds = -1;
    surfacesFusionNoeuds = null;
    elimineNoeudsAvecDeuxArcs = false;
    graphePlanaire = false;
    fusionArcsDoubles = false;
  }
  
  
  public double getSeuilFusionNoeuds() {
    return seuilFusionNoeuds;
  }
  
  public void setSeuilFusionNoeuds(double d) {
    seuilFusionNoeuds = d;
  }
  
  public IPopulation<?> getSurfacesFusionNoeuds() {
    return surfacesFusionNoeuds;
  }
  
  public void setSurfacesFusionNoeuds(IPopulation<?> pop) {
    surfacesFusionNoeuds = pop;
  }
  
  public boolean getElimineNoeudsAvecDeuxArcs() {
    return elimineNoeudsAvecDeuxArcs;
  }
  
  public void setElimineNoeudsAvecDeuxArcs(boolean b) {
    elimineNoeudsAvecDeuxArcs = b;
  }
  
  public boolean getGraphePlanaire() {
    return graphePlanaire;
  }
  
  public void setGraphePlanaire(boolean b) {
    graphePlanaire = b;
  }
  
  public boolean getFusionArcsDoubles() {
    return fusionArcsDoubles;
  }
  
  public void setFusionArcsDoubles(boolean b) {
    fusionArcsDoubles = b;
  }
  
  

}
