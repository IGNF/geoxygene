/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.util.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import fr.ign.cogit.geoxygene.api.feature.IDataSet;
import fr.ign.cogit.geoxygene.util.loader.gui.GUIChargementDonnees;

/**
 * Classe permettant de représenter un chargement de données.
 * 
 * @see GUIChargementDonnees
 * 
 * @author Julien Perret
 * 
 */
public class Chargement {

  private IDataSet dataSet = null;

  /**
   * Renvoie la valeur de l'attribut dataSet.
   * @return la valeur de l'attribut dataSet
   */
  public IDataSet getDataSet() {
    return this.dataSet;
  }

  /**
   * Affecte la valeur de l'attribut dataSet.
   * @param dataSet l'attribut dataSet à affecter
   */
  public void setDataSet(IDataSet dataSet) {
    this.dataSet = dataSet;
  }

  private Map<String, String> fichiers = new HashMap<String, String>();

  /**
   * Renvoie la valeur de l'attribut fichiers.
   * @return la valeur de l'attribut fichiers
   */
  public Map<String, String> getFichiers() {
    return this.fichiers;
  }

  /**
   * Affecte la valeur de l'attribut fichiers.
   * @param fichiers l'attribut fichiers à affecter
   */
  public void setFichiers(Map<String, String> fichiers) {
    this.fichiers = fichiers;
  }

  /**
   * Charge un objet {@link Chargement} depuis le fichier XML passé en
   * paramètre.
   * @param nomFichier fichier XML
   * @return l'objet {@link Chargement} chragé depuis le fichier XML passé en
   *         paramètre.
   */
  public static Chargement charge(String nomFichier) {
    try {
      return (Chargement) Chargement.getXStream().fromXML(
          new FileInputStream(new File(nomFichier)));
    } catch (FileNotFoundException e) {
      return null;
    }
  }

  /**
   * Sauve le {@link Chargement} dans le fichier en paramètre.
   * @param nomFichier fichier dans lequel on sauve le {@link Chargement}
   */
  public void toXml(String nomFichier) {
    try {
      Chargement.getXStream().toXML(this,
          new FileOutputStream(new File(nomFichier)));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Renvoie un objet {@link XStream} servant pour le chargement ou la
   * sauvegarde d'un objet de type {@link Chargement}
   * @return un objet {@link XStream} servant pour le chargement ou la
   *         sauvegarde d'un objet de type {@link Chargement}
   */
  private static XStream getXStream() {
    XStream xstream = new XStream(new DomDriver());
    xstream.alias("Chargement", Chargement.class);
    xstream.registerConverter(new ChargementConverter());
    return xstream;
  }
}
