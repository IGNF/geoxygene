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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.xml.OrientationAdapter;
import fr.ign.cogit.geoxygene.contrib.cartetopo.OrientationInterface;

/**
 * Prise en compte de l'orientation des arcs sur le terrain (sens de circulation). 
 * Si true : on suppose tous les arcs en double sens. 
 * Si false: on suppose tous les arcs en sens unique, celui défini par la géométrie. 
 * 
 * NB: ne pas confondre cette orientation 'géographique réelle', avec l'orientation de la géométrie.
 * 
 * Utile ensuite pour l'appariement des arcs.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "orientationDouble",
    "attributOrientation",
    "orientationMap"
})
@XmlRootElement(name = "ParamDirectionNetworkDataMatching")
public class ParamDirectionNetworkDataMatching {
  
  /** populationsArcsAvecOrientationDouble. */
  @XmlElement(name = "OrientationDouble")
  private boolean orientationDouble;
  
  /** Attribute of orientation. */
  @XmlElement(name = "AttributOrientation")
  private String attributOrientation;
  
  /** Table Attribute-Value for DIRECT, INVERSE, DOUBLE. */
  @XmlElement(name = "OrientationMap")
  @XmlJavaTypeAdapter(OrientationAdapter.class) 
  private Map<Integer, String> orientationMap;

  /**
   * Default constructor.
   */
  public ParamDirectionNetworkDataMatching() {
    orientationDouble = true;
    attributOrientation = null;
    orientationMap = null;
    /*"sens_de_circulation";
    orientationMap = new HashMap<Integer, String>();
    orientationMap.put(OrientationInterface.SENS_DIRECT, "direct");
    orientationMap.put(OrientationInterface.SENS_INVERSE, "inverse");
    orientationMap.put(OrientationInterface.DOUBLE_SENS, "double");*/
  }
  
  /**
   * Return vrai si la population des arcs est à double sens.
   * @return boolean
   */
  public boolean getOrientationDouble() {
    return orientationDouble;
  }
  
  /**
   * @param b
   *          Is orientation double to set.
   */
  public void setOrientationDouble(boolean b) {
    orientationDouble = b;
  }
  
  /**
   * Return attribut orientation.
   * @return string
   */
  public String getAttributOrientation() {
    return attributOrientation;
  }
  
  /**
   * @param ao
   *          Attribut orientation to set.
   */
  public void setAttributOrientation(String ao) {
    attributOrientation = ao;
  }
  
  /**
   * Return table des valeurs de l'attribut orientation.
   * @return Map<Integer, String>
   */
  public Map<Integer, String> getOrientationMap() {
    return orientationMap;
  }
  
  /**
   * @param mo
   *          Table des valeurs de l'attribut orientation to set.
   */
  public void setOrientationMap (Map<Integer, String> mo) {
    orientationMap = mo;
  }
  
}
