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

import org.geotools.data.simple.SimpleFeatureCollection;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;

/**
 * Network data matching results.
 * <ul>
 *   <li>- Link data set</li>
 *   <li>- Statistics results</li>
 *   <li>- Network matched</li>   
 * </ul>
 * 
 * @version 1.6
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "resultStat"
})
@XmlRootElement(name = "NetworkDataMatchingResult")
public class ResultatAppariement {
  
  /** Carte topo 1. */
  
  /** Carte topo 2. */
  
  /** EnsembleDeLiens. */
  private EnsembleDeLiens linkDataSet;
  
  /** Stat result. */
  @XmlElement(required = true)
  private ResultNetwork resultStat;
  
  /** Network matched. */
  private SimpleFeatureCollection networkMatched;
    
  /**
   * Default constructor.
   */
  public ResultatAppariement() {
    linkDataSet = null;
    networkMatched = null;
    resultStat = new ResultNetwork();
  }
  
  /**
   * Constructor.
   * @param edl
   * @param sfc  
   */
  public ResultatAppariement(EnsembleDeLiens edl, SimpleFeatureCollection sfc) {
    linkDataSet = edl;
    networkMatched = sfc;
    resultStat = new ResultNetwork();
  }
  
  /**
   * Return link data set.
   * @return EnsembleDeLiens
   */
  public EnsembleDeLiens getLinkDataSet() {
    return linkDataSet;
  }
  
  /**
   * @param EnsembleDeLiens
   *          link data set to set.
   */
  public void setLinkDataSet(EnsembleDeLiens edl) {
    linkDataSet = edl;
  }
  
  /**
   * Return statistics results.
   * @return resultStat
   */
  public ResultNetwork getResultStat() {
    return resultStat;
  }
  
  /**
   * @param rsa
   *          Stat result to set.
   */
  public void setResultStat (ResultNetwork rsa) {
    resultStat = rsa;
  }
  
  /**
   * Return Network matched.
   * @return SimpleFeatureCollection
   */
  public SimpleFeatureCollection getNetworkMatched() {
    return networkMatched;
  }
  
  /**
   * @param SimpleFeatureCollection
   *          Network matched to set.
   */
  public void setNetworkMatched(SimpleFeatureCollection sfc) {
    networkMatched = sfc;
  }

}
