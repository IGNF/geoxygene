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


/**
 * Ecarts de distance autorisés.
 * CE SONT LES PARAMETRES PRINCIPAUX.
 * 
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "distanceNoeudsMax",
    "distanceArcsMax",
    "distanceArcsMin",
    "distanceNoeudsImpassesMax"
})
@XmlRootElement(name = "ParamDistanceNetworkDataMatching")
public class ParamDistanceNetworkDataMatching {
  
  /**
   * Distance maximale autorisée entre deux noeuds appariés.
   */
  @XmlElement(name = "DistanceNoeudsMax")
  private float distanceNoeudsMax = 150;

  /**
   * Distance maximale autorisée entre deux noeuds appariés, quand le noeud du
   * réseau 1 est une impasse uniquement. Ce paramètre permet de prendre en
   * compte le fait que la localisation exacte des extrémités d'impasse est
   * assez imprécise dans certains réseaux (où commence exactement une rivière
   * par exemple?).
   * 
   * Si cette distance est strictement négative, alors ce paramètre n'est pas
   * pris en compte, et la distance maximale est la même pour tous les noeuds,
   * y-compris aux extrémités.
   */
  @XmlElement(name = "DistanceNoeudsImpassesMax")
  private float distanceNoeudsImpassesMax = -1;

  /**
   * Distance maximum autorisée entre les arcs des deux réseaux. La distance est
   * définie au sens de la "demi-distance de Hausdorf" des arcs du réseau 2 vers
   * les arcs du réseau 1.
   */
  @XmlElement(name = "DistanceArcsMax")
  private float distanceArcsMax = 100;

  /**
   * Distance minimum sous laquelle l'écart de distance pour divers arcs du
   * réseaux 2 (distance vers les arcs du réseau 1) n'a plus aucun sens. Cette
   * distance est typiquement de l'ordre de la précision géométrique du réseau
   * le moins précis.
   */
  @XmlElement(name = "DistanceArcsMin")
  private float distanceArcsMin = 30; 
  
  /**
   * Default constructor.
   */
  public ParamDistanceNetworkDataMatching() {
    distanceNoeudsMax = 150;
    distanceNoeudsImpassesMax = -1;
    distanceArcsMax = 100;
    distanceArcsMin = 30; 
  }
  
  public float getDistanceNoeudsMax() {
    return distanceNoeudsMax;
  }
  
  public void setDistanceNoeudsMax(float distanceNoeudsMax) {
    this.distanceNoeudsMax = distanceNoeudsMax;
  }

  public float getDistanceNoeudsImpassesMax() {
    return distanceNoeudsImpassesMax;
  }
  
  public void setDistanceNoeudsImpassesMax(float distanceNoeudsImpassesMax) {
    this.distanceNoeudsImpassesMax = distanceNoeudsImpassesMax;
  }
  
  public float getDistanceArcsMax() {
    return distanceArcsMax;
  }
  
  public void setDistanceArcsMax(float distanceArcsMax) {
    this.distanceArcsMax = distanceArcsMax;
  }
  
  public float getDistanceArcsMin() {
    return distanceArcsMin;
  }
  
  public void setDistanceArcsMin(float distanceArcsMin) {
    this.distanceArcsMin = distanceArcsMin;
  }
  
  /**
   * 
   */
  public String toString() {
      
      StringBuffer buffer = new StringBuffer();
      
      buffer.append("Écarts de distance autorisés : " + "<br/>");
      buffer.append("   - Distance maximale autorisée entre deux noeuds appariés = " + getDistanceNoeudsMax() + "<br/>");
      buffer.append("   - Distance maximum autorisée entre les arcs des deux réseaux = " + getDistanceArcsMax() + "<br/>");
      buffer.append("   - Distance minimum sous laquelle l'écart de distance pour divers arcs du réseaux 2 n'a plus aucun sens = " 
          + getDistanceArcsMin() + "<br/>");
      buffer.append("   - Distance maximale autorisée entre deux noeuds appariés, quand le noeud du réseau 1 est une impasse uniquement = " 
          + getDistanceNoeudsImpassesMax() + "<br/>");
  
      return buffer.toString();
  }
  
}
