/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;


/**
 * Represents paramaters data to input for network data matching.
 * Settings are loaded from an XML file.
 * 
 * @author M.-D. Van Damme
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ParamNetworkDataMatching")
public class ParametresAppData {
  
  /** A classic logger. */
  static Logger logger = Logger.getLogger(ParametresAppData.class.getName());
  
  @XmlElements(@XmlElement(name = "NoeudsMax", type = Float.class))
  private float noeudsMax;

  /**
   * Return Distance maximale autorisée entre deux noeuds appariés.
   * @return NoeudsMax
   */
  public float getNoeudsMax() {
    return this.noeudsMax;
  }

  /**
   * @param noeudsMax
   *          Distance maximale autorisée entre deux noeuds appariés to set.
   */
  public void setNoeudsMax(float noeudsMax) {
    this.noeudsMax = noeudsMax;
  }
  
  @XmlElements(@XmlElement(name = "ArcsMax", type = Float.class))
  private float arcsMax;

  /**
   * Return Distance maximum autorisée entre les arcs des deux réseaux.
   * @return ArcsMax
   */
  public float getArcsMax() {
    return this.arcsMax;
  }

  /**
   * @param ArcsMax
   *          Distance maximum autorisée entre les arcs des deux réseaux to set.
   */
  public void setArcsMax(float arcsMax) {
    this.arcsMax = arcsMax;
  }
  
  @XmlElements(@XmlElement(name = "ArcsMin", type = Float.class))
  private float arcsMin;

  /**
   * Return Distance minimum sous laquelle l'écart de distance pour divers arcs du
   * réseaux 2 (distance vers les arcs du réseau 1) n'a plus aucun sens.
   * @return ArcsMin
   */
  public float getArcsMin() {
    return this.arcsMin;
  }

  /**
   * @param arcsMin
   *          Distance minimum sous laquelle l'écart de distance pour divers arcs du
   * réseaux 2 (distance vers les arcs du réseau 1) n'a plus aucun sens to set.
   */
  public void setArcsMin(float arcsMin) {
    this.arcsMin = arcsMin;
  }
  
  @XmlElements(@XmlElement(name = "NoeudsImpassesMax", type = Float.class))
  private float noeudsImpassesMax;

  /**
   * Return Distance maximale autorisée entre deux noeuds appariés, quand le noeud du
   * réseau 1 est une impasse uniquement.
   * @return NoeudsImpassesMax
   */
  public float getNoeudsImpassesMax() {
    return this.noeudsImpassesMax;
  }

  /**
   * @param NoeudsImpassesMax
   *          Distance maximale autorisée entre deux noeuds appariés, quand le noeud du
   * réseau 1 est une impasse uniquement to set.
   */
  public void setNoeudsImpassesMax(float noeudsImpassesMax) {
    this.noeudsImpassesMax = noeudsImpassesMax;
  }
  
  
  /**
   * Load the parameters from the specified stream.
   * 
   * @param stream stream to load the parameters from
   * @return the parameters loaded from the specified stream
   */
  public static ParametresAppData unmarshall(InputStream stream) {
    try {
      JAXBContext context = JAXBContext.newInstance(ParametresAppData.class);
      Unmarshaller m = context.createUnmarshaller();
      ParametresAppData parametresAppData = (ParametresAppData) m.unmarshal(stream);
      return parametresAppData;
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return new ParametresAppData();
  }
  
  /**
   * Load the parameters. 
   * If file does not exist, create new empty XML.
   * 
   * @param fileName XML parameter file to load
   * @return ParametresAppData loaded
   */
  public static ParametresAppData unmarshall(String fileName) {
    try {
      return ParametresAppData.unmarshall(new FileInputStream(fileName));
    } catch (FileNotFoundException e) {
      ParametresAppData.logger
          .error("File " + fileName + " could not be read"); //$NON-NLS-1$//$NON-NLS-2$
      return new ParametresAppData();
    }
  }

}
