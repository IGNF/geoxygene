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
    "topologieSeuilFusionNoeuds",
    "topologieSurfacesFusionNoeuds",
    "topologieElimineNoeudsAvecDeuxArcs",
    "topologieGraphePlanaire",
    "topologieFusionArcsDoubles"
})
@XmlRootElement(name = "ParamTopoTreatmentNetworkDataMatching")
public class ParamTopoTreatmentNetworkDataMatching {
  
  @XmlElement(name = "TopologieSeuilFusionNoeuds")
  private double topologieSeuilFusionNoeuds = -1;
  
  @XmlElement(name = "TopologieSurfacesFusionNoeuds")
  private IPopulation<?> topologieSurfacesFusionNoeuds;
  
  @XmlElement(name = "TopologieElimineNoeudsAvecDeuxArcs")
  private boolean topologieElimineNoeudsAvecDeuxArcs;
  
  @XmlElement(name = "TopologieGraphePlanaire")
  private boolean topologieGraphePlanaire;
  
  @XmlElement(name = "TopologieFusionArcsDoubles")
  private boolean topologieFusionArcsDoubles;
  
  /**
   * Constructor.
   */
  public ParamTopoTreatmentNetworkDataMatching() {
    topologieSeuilFusionNoeuds = -1;
    topologieSurfacesFusionNoeuds = null;
    topologieElimineNoeudsAvecDeuxArcs = false;
    topologieGraphePlanaire = false;
    topologieFusionArcsDoubles = false;
  }
  
  
  public double getTopologieSeuilFusionNoeuds() {
    return topologieSeuilFusionNoeuds;
  }
  
  public void setTopologieSeuilFusionNoeuds(double d) {
    topologieSeuilFusionNoeuds = d;
  }
  
  public IPopulation<?> getTopologieSurfacesFusionNoeuds() {
    return topologieSurfacesFusionNoeuds;
  }
  
  public void settopologieSurfacesFusionNoeuds(IPopulation<?> pop) {
    topologieSurfacesFusionNoeuds = pop;
  }
  
  public boolean getTopologieElimineNoeudsAvecDeuxArcs() {
    return topologieElimineNoeudsAvecDeuxArcs;
  }
  
  public void setTopologieElimineNoeudsAvecDeuxArcs(boolean b) {
    topologieElimineNoeudsAvecDeuxArcs = b;
  }
  
  public boolean getTopologieGraphePlanaire() {
    return topologieGraphePlanaire;
  }
  
  public void setTopologieGraphePlanaire(boolean b) {
    topologieGraphePlanaire = b;
  }
  
  public boolean getTopologieFusionArcsDoubles() {
    return topologieFusionArcsDoubles;
  }
  
  public void setTopologieFusionArcsDoubles(boolean b) {
    topologieFusionArcsDoubles = b;
  }
  
  

}
