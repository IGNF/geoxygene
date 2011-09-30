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

package fr.ign.cogit.geoxygene.util.conversion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;
import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * @author Julien Perret
 * 
 */
public class RoadNetworkTextfileReader {
  private final static Logger logger = Logger.getLogger(RoadNetworkTextfileReader.class
      .getName());
  String shapefileName;
  String populationName;
  DataSet dataset;
  SchemaDefaultFeature schemaDefaultFeature;
  FeatureSource<SimpleFeatureType, SimpleFeature> source;
  Population<DefaultFeature> population;

  /**
   * Renvoie la population dans laquelle les objets sont chargés.
   * @return la population dans laquelle les objets sont chargés
   */
  public Population<DefaultFeature> getPopulation() {
    return this.population;
  }

  /**
   * Lit les features contenus dans le fichier en paramètre. Ce chargement est
   * synchrone
   * <p>
   * Pour utiliser le chargement asynchrone, utiliser le constructeur.
   * 
   * @see #read(String, String, DataSet, boolean)
   * 
   * @param shapefileName un shapefile
   * @return une population contenant les features contenues dans le fichier.
   */
  public static Population<DefaultFeature> read(String shapefileName) {
    return RoadNetworkTextfileReader.read(shapefileName, shapefileName.substring(
        shapefileName.lastIndexOf(File.pathSeparator) + 1,
        shapefileName.lastIndexOf(".")), null, false);} //$NON-NLS-1$

  /**
   * Lit les features contenus dans le fichier en paramètre et ajoute la
   * population chargée à un dataset. Ce chargement est synchrone Pour utiliser
   * le chargement asynchrone, utiliser le constructeur. Si le paramètre
   * initSpatialIndex est vrai, alors on initialise aussi l'index spatial de la
   * population.
   * 
   * @see #read(String)
   * 
   * @param shapefileName un shapefile
   * @param populationName non de la population
   * @param dataset jeu de données auquel ajouter la population
   * @param initSpatialIndex si ce boolean est vrai, alors on initialise la
   *          population.
   * @return une population contenant les features contenues dans le fichier.
   */
  public static Population<DefaultFeature> read(String shapefileName,
      String populationName, DataSet dataset, boolean initSpatialIndex) {
    // creation de la collection de features
    Population<DefaultFeature> population = new Population<DefaultFeature>(
        populationName);
    if (dataset != null) {
      dataset.addPopulation(population);
    }
    SchemaDefaultFeature schemaDefaultFeature = new SchemaDefaultFeature();
    schemaDefaultFeature.setNom(populationName);
    schemaDefaultFeature.setNomSchema(populationName);
    /** créer un featuretype de jeu correspondant */
    fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType newFeatureType = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType();
    newFeatureType.setTypeName(population.getNom());
    Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>();
    //"Edge ID"
    {
      AttributeType type = new AttributeType();
      String nomField = "id"; //$NON-NLS-1$
      String memberName = "id"; //$NON-NLS-1$
      String valueType = "String"; //$NON-NLS-1$
      type.setNomField(nomField);
      type.setMemberName(memberName);
      type.setValueType(valueType);
      newFeatureType.addFeatureAttribute(type);
      attLookup.put(new Integer(0), new String[] { nomField, memberName });
    }
    //"From Node ID"
    {
      AttributeType type = new AttributeType();
      String nomField = "Initial"; //$NON-NLS-1$
      String memberName = "Initial"; //$NON-NLS-1$
      String valueType = "String"; //$NON-NLS-1$
      type.setNomField(nomField);
      type.setMemberName(memberName);
      type.setValueType(valueType);
      newFeatureType.addFeatureAttribute(type);
      attLookup.put(new Integer(1), new String[] { nomField, memberName });
    }
    //"To Node ID"
    {
      AttributeType type = new AttributeType();
      String nomField = "Final"; //$NON-NLS-1$
      String memberName = "Final"; //$NON-NLS-1$
      String valueType = "String"; //$NON-NLS-1$
      type.setNomField(nomField);
      type.setMemberName(memberName);
      type.setValueType(valueType);
      newFeatureType.addFeatureAttribute(type);
      attLookup.put(new Integer(2), new String[] { nomField, memberName });
    }
    //"Two Way"
    {
      AttributeType type = new AttributeType();
      String nomField = "TwoWay"; //$NON-NLS-1$
      String memberName = "TwoWay"; //$NON-NLS-1$
      String valueType = "Boolean"; //$NON-NLS-1$
      type.setNomField(nomField);
      type.setMemberName(memberName);
      type.setValueType(valueType);
      newFeatureType.addFeatureAttribute(type);
      attLookup.put(new Integer(3), new String[] { nomField, memberName });
    }
    //"Speed (m/s)"
    {
      AttributeType type = new AttributeType();
      String nomField = "Speed"; //$NON-NLS-1$
      String memberName = "Speed"; //$NON-NLS-1$
      String valueType = "Double"; //$NON-NLS-1$
      type.setNomField(nomField);
      type.setMemberName(memberName);
      type.setValueType(valueType);
      newFeatureType.addFeatureAttribute(type);
      attLookup.put(new Integer(4), new String[] { nomField, memberName });
    }
    //"Vertex Count"
    /** création d'un schéma associé au featureType */
    newFeatureType.setGeometryType(GM_LineString.class);
    schemaDefaultFeature.setFeatureType(newFeatureType);
    newFeatureType.setSchema(schemaDefaultFeature);
    schemaDefaultFeature.setAttLookup(attLookup);
    population.setFeatureType(newFeatureType);
    if (RoadNetworkTextfileReader.logger.isDebugEnabled()) {
      for (GF_AttributeType fa : newFeatureType.getFeatureAttributes()) {
        RoadNetworkTextfileReader.logger.debug("FeatureAttibute = " //$NON-NLS-1$
            + fa.getMemberName() + "-" + fa.getValueType()); //$NON-NLS-1$
      }
    }
    /**
     * Parcours de features du fichier et création de Default features
     * équivalents
     */
    try {
      Scanner scanner = new Scanner(new FileInputStream(shapefileName), "UTF-8"); //$NON-NLS-1$
      scanner.nextLine(); // header
      double minX = Double.POSITIVE_INFINITY;
      double maxX = Double.NEGATIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;
      try {
        while (scanner.hasNextLine()) {
          String[] line = scanner.nextLine().split("\t"); //$NON-NLS-1$
          String id = line[0];
          String initialNodeId = line[1];
          String finalNodeId = line[2];
          Boolean twoWay = new Boolean(Integer.parseInt(line[3]) == 1);
          Double speed = new Double(Double.parseDouble(line[4]));
          //line[5] -> vertex count
          IGeometry geometry = WktGeOxygene.makeGeOxygene(line[6]);
          DefaultFeature feature = new DefaultFeature(geometry);
          feature.setAttributes(new Object[]{id, initialNodeId, finalNodeId, twoWay, speed});
          feature.setSchema(schemaDefaultFeature);
          population.add(feature);
          maxX = Math.max(maxX, geometry.envelope().maxX());
          minX = Math.min(minX, geometry.envelope().minX());
          maxY = Math.max(maxY, geometry.envelope().maxY());
          minY = Math.min(minY, geometry.envelope().minY());
        }
      } finally {
        scanner.close();
      }
      if (initSpatialIndex) {
        population.initSpatialIndex(Tiling.class, true, new GM_Envelope(minX,
            maxX, minY, maxY), 10);
      }
      population.setCenter(new DirectPosition((maxX + minX) / 2,
          (maxY + minY) / 2));
      if (RoadNetworkTextfileReader.logger.isTraceEnabled()) {
        RoadNetworkTextfileReader.logger.trace(I18N
            .getString("ShapefileReader.SpatialIndexInitialised") //$NON-NLS-1$
            + minX + "," + maxX + "," //$NON-NLS-1$ //$NON-NLS-2$
            + minY + "," + maxY); //$NON-NLS-1$
      }
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return population;
  }

  protected static EventListenerList listenerList = new EventListenerList();

  /**
   * Adds an <code>ActionListener</code>.
   * @param l the <code>ActionListener</code> to be added
   */
  public static void addActionListener(ActionListener l) {
    RoadNetworkTextfileReader.listenerList.add(ActionListener.class, l);
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is lazily created.
   * @see EventListenerList
   */
  protected static void fireActionPerformed(ActionEvent event) {
    // Guaranteed to return a non-null array
    Object[] listeners = RoadNetworkTextfileReader.listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ActionListener.class) {
        // Lazily create the event:
        ((ActionListener) listeners[i + 1]).actionPerformed(event);
      }
    }
  }
}
