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

import java.util.Map;

/**
 * Prise en compte de l'orientation des arcs sur le terrain (sens de
 * circulation). Si true : on suppose tous les arcs en double sens. Si false:
 * on suppose tous les arcs en sens unique, celui défini par la géométrie. NB:
 * ne pas confondre cette orientation 'géographique réelle', avec
 * l'orientation de la géométrie.
 * 
 * Utile ensuite pour l'appariement des arcs.
 */
public class ParamDirectionNetworkDataMatching {
  
  private boolean populationsArcsAvecOrientationDouble;
  private String attributOrientation1;
  private String attributOrientation2;
  private Map<Integer, String> orientationMap1;
  private Map<Integer, String> orientationMap2;

  /**
   * Constructor
   */
  public ParamDirectionNetworkDataMatching() {
    populationsArcsAvecOrientationDouble = true;
    attributOrientation1 = "orientation";
    attributOrientation2 = "orientation";
    orientationMap1 = null;
    orientationMap2 = null;
  }
  
  public boolean getPopulationsArcsAvecOrientationDouble() {
    return populationsArcsAvecOrientationDouble;
  }
  
  public void setPopulationsArcsAvecOrientationDouble(boolean b) {
    populationsArcsAvecOrientationDouble = b;
  }
  
  public String getAttributOrientation1() {
    return attributOrientation1;
  }
  
  public void setAttributOrientation1(String attributOrientation) {
    attributOrientation1 = attributOrientation;
  }
  
  public String getAttributOrientation2() {
    return attributOrientation2;
  }
  
  public void setAttributOrientation2(String attributOrientation) {
    attributOrientation2 = attributOrientation;
  }
  
  
  public Map<Integer, String> getOrientationMap1() {
    return orientationMap1;
  }
  
  public void setOrientationMap1 (Map<Integer, String> mapOrientation) {
    orientationMap1 = mapOrientation;
  }
  
  public Map<Integer, String> getOrientationMap2() {
    return orientationMap2;
  }
  
  public void setOrientationMap2 (Map<Integer, String> mapOrientation) {
    orientationMap2 = mapOrientation;
  }
}
