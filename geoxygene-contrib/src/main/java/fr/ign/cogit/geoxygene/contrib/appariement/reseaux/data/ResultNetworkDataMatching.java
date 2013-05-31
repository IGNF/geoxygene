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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.geotools.data.simple.SimpleFeatureCollection;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;

import org.apache.log4j.Logger;

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
    "resultStat",
    "networkMatched"
})
@XmlRootElement(name = "ResultNetworkDataMatching")
public class ResultNetworkDataMatching {
  
  /** Cartes topo */
  @XmlTransient
  private ReseauAppStat reseauStat1;
  @XmlTransient
  private ReseauAppStat reseauStat2;
  
  /** EnsembleDeLiens. */
  @XmlTransient
  private EnsembleDeLiens liens;
  @XmlTransient
  private EnsembleDeLiens liensGeneriques;
  
  /** Stat result. */
  @XmlElement(required = true)
  private ResultNetworkStat resultStat;
  
  /** Network matched. */
  @XmlElement(required = true)
  private SimpleFeatureCollection networkMatched; //  ???
  
  /** A classic logger. */
  static Logger logger = Logger.getLogger(ResultNetworkDataMatching.class.getName());
    
  /**
   * Default constructor.
   */
  public ResultNetworkDataMatching() {
    liens = null;
    liensGeneriques = null;
    networkMatched = null;
    resultStat = new ResultNetworkStat();
  }
  
  /**
   * Constructor.
   * @param edl
   * @param sfc  
   */
  /*public ResultNetworkDataMatching(EnsembleDeLiens edl, SimpleFeatureCollection sfc) {
    liens = edl;
    networkMatched = sfc;
    resultStat = new ResultNetworkStat();
  }*/
  
  /**
   * Return link data set.
   * @return EnsembleDeLiens
   */
  public EnsembleDeLiens getLiens() {
    return liens;
  }
  
  /**
   * @param EnsembleDeLiens
   *          link data set to set.
   */
  public void setLiens(EnsembleDeLiens edl) {
    liens = edl;
  }
  
  public EnsembleDeLiens getLiensGeneriques() {
    return liensGeneriques;
  }
  
  public void setLiensGeneriques(EnsembleDeLiens edl) {
    liensGeneriques = edl;
  }
  
  /**
   * Return statistics results.
   * @return resultStat
   */
  public ResultNetworkStat getResultStat() {
    return resultStat;
  }
  
  /**
   * @param rsa
   *          Stat result to set.
   */
  public void setResultStat (ResultNetworkStat rsa) {
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
  
  
  
  public void setReseauStat1(ReseauAppStat resStat) {
      reseauStat1 = resStat;
  }
  
  public ReseauAppStat getReseauStat1() {
    return reseauStat1;
  }
  
  public void setReseauStat2(ReseauAppStat resStat) {
      reseauStat2 = resStat;
  }
  
  public ReseauAppStat getReseauStat2() {
    return reseauStat2;
  }
  
  /**
   * Load the parameters from the specified stream.
   * 
   * @param stream stream to load the parameters from
   * @return the parameters loaded from the specified stream
   */
  public static ResultNetworkDataMatching unmarshall(InputStream stream) {
    try {
      JAXBContext context = JAXBContext.newInstance(ResultNetworkDataMatching.class);
      Unmarshaller m = context.createUnmarshaller();
      ResultNetworkDataMatching parametresAppData = (ResultNetworkDataMatching) m.unmarshal(stream);
      return parametresAppData;
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return new ResultNetworkDataMatching();
  }
  
  /**
   * Load the parameters. 
   * If file does not exist, create new empty XML.
   * 
   * @param fileName XML parameter file to load
   * @return ParametresAppData loaded
   */
  public static ResultNetworkDataMatching unmarshall(String fileName) {
    try {
      return ResultNetworkDataMatching.unmarshall(new FileInputStream(fileName));
    } catch (FileNotFoundException e) {
      /*ResultNetworkDataMatching.LOGGER
          .error("File " + fileName + " could not be read");*/
      return new ResultNetworkDataMatching();
    }
  }

}
