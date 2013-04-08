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

import fr.ign.cogit.geoxygene.api.feature.IPopulation;

/**
 * Traitements topologiques à l'import.
 * 
 *
 */
public class ParamTopoTreatmentNetworkDataMatching {
  
  private double topologieSeuilFusionNoeuds1 = -1;
  private double topologieSeuilFusionNoeuds2 = -1;
  private IPopulation<?> topologieSurfacesFusionNoeuds1;
  private IPopulation<?> topologieSurfacesFusionNoeuds2;
  private boolean topologieElimineNoeudsAvecDeuxArcs1;
  private boolean topologieElimineNoeudsAvecDeuxArcs2;
  private boolean topologieGraphePlanaire1;
  private boolean topologieGraphePlanaire2;
  private boolean topologieFusionArcsDoubles1;
  private boolean topologieFusionArcsDoubles2;
  
  /**
   * Constructor.
   */
  public ParamTopoTreatmentNetworkDataMatching() {
    topologieSeuilFusionNoeuds1 = -1;
    topologieSeuilFusionNoeuds2 = -1;
    topologieSurfacesFusionNoeuds1 = null;
    topologieSurfacesFusionNoeuds2 = null;
    
    topologieElimineNoeudsAvecDeuxArcs1 = false;
    topologieElimineNoeudsAvecDeuxArcs2 = false;
    topologieGraphePlanaire1 = false;
    topologieGraphePlanaire2 = false;
    
    topologieFusionArcsDoubles1 = false;
    topologieFusionArcsDoubles2 = false;
  }
  
  
  public double getTopologieSeuilFusionNoeuds1() {
    return topologieSeuilFusionNoeuds1;
  }
  
  public double getTopologieSeuilFusionNoeuds2() {
    return topologieSeuilFusionNoeuds2;
  }
  
  public IPopulation<?> getTopologieSurfacesFusionNoeuds1() {
    return topologieSurfacesFusionNoeuds1;
  }
  
  public IPopulation<?> getTopologieSurfacesFusionNoeuds2() {
    return topologieSurfacesFusionNoeuds2;
  }
  
  public boolean getTopologieElimineNoeudsAvecDeuxArcs1() {
    return topologieElimineNoeudsAvecDeuxArcs1;
  }
  
  public boolean getTopologieElimineNoeudsAvecDeuxArcs2() {
    return topologieElimineNoeudsAvecDeuxArcs2;
  }
  
  public boolean getTopologieGraphePlanaire1() {
    return topologieGraphePlanaire1;
  }
  
  public boolean getTopologieGraphePlanaire2() {
    return topologieGraphePlanaire2;
  }
  
  public boolean getTopologieFusionArcsDoubles1() {
    return topologieFusionArcsDoubles1;
  }
  
  public boolean getTopologieFusionArcsDoubles2() {
    return topologieFusionArcsDoubles2;
  }

}
