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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * @author Julien Perret
 * 
 */
public class GPSTextfileReader {
  private final static Logger logger = Logger.getLogger(GPSTextfileReader.class
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
   * @see #chooseAndReadShapefile()
   * 
   * @param shapefileName un shapefile
   * @return une population contenant les features contenues dans le fichier.
   */
  public static Population<DefaultFeature> read(String shapefileName) {
    return GPSTextfileReader.read(shapefileName, shapefileName.substring(
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
   * @see #chooseAndReadShapefile()
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
    {
      AttributeType type = new AttributeType();
      String nomField = "date"; //$NON-NLS-1$
      String memberName = "date"; //$NON-NLS-1$
      String valueType = "Date"; //$NON-NLS-1$
      type.setNomField(nomField);
      type.setMemberName(memberName);
      type.setValueType(valueType);
      newFeatureType.addFeatureAttribute(type);
      attLookup.put(new Integer(0), new String[] { nomField, memberName });
    }
    {
      AttributeType type = new AttributeType();
      String nomField = "time"; //$NON-NLS-1$
      String memberName = "time"; //$NON-NLS-1$
      String valueType = "String"; //$NON-NLS-1$
      type.setNomField(nomField);
      type.setMemberName(memberName);
      type.setValueType(valueType);
      newFeatureType.addFeatureAttribute(type);
      attLookup.put(new Integer(1), new String[] { nomField, memberName });
    }
    /** création d'un schéma associé au featureType */
    newFeatureType.setGeometryType(GM_Point.class);
    schemaDefaultFeature.setFeatureType(newFeatureType);
    newFeatureType.setSchema(schemaDefaultFeature);
    schemaDefaultFeature.setAttLookup(attLookup);
    population.setFeatureType(newFeatureType);
    if (GPSTextfileReader.logger.isDebugEnabled()) {
      for (GF_AttributeType fa : newFeatureType.getFeatureAttributes()) {
        GPSTextfileReader.logger.debug("FeatureAttibute = " //$NON-NLS-1$
            + fa.getMemberName() + "-" + fa.getValueType()); //$NON-NLS-1$
      }
    }
    /**
     * Parcours de features du fichier et création de Default features
     * équivalents
     */
    String header = null;
    try {
      Scanner scanner = new Scanner(new FileInputStream(shapefileName), "UTF-8"); //$NON-NLS-1$
      header = scanner.nextLine();
      String[] headerParts = header.split("\t"); //$NON-NLS-1$
      int dateIndex = -1;
      int timeIndex = -1;
      int latitudeIndex = -1;
      int longitudeIndex = -1;
      for (int i = 0; i < headerParts.length; i++) {
        if (headerParts[i].matches(".*Date.*")) { //$NON-NLS-1$
          dateIndex = i;
        } else {
          if (headerParts[i].matches(".*Time.*")) { //$NON-NLS-1$
            timeIndex = i;
          } else {
            if (headerParts[i].matches(".*Latitude.*")) { //$NON-NLS-1$
              latitudeIndex = i;
            } else {
              if (headerParts[i].matches(".*Longitude.*")) { //$NON-NLS-1$
                longitudeIndex = i;
              }
            }
          }
        }
      }
      double minX = Double.POSITIVE_INFINITY;
      double maxX = Double.NEGATIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;
      List<DefaultFeature> list = new ArrayList<DefaultFeature>(0);
      try {
        while (scanner.hasNextLine()) {
          String[] line = scanner.nextLine().split("\t"); //$NON-NLS-1$
          String[] dateString = line[dateIndex].split("-"); //$NON-NLS-1$
          String[] timeString = line[timeIndex].split(":"); //$NON-NLS-1$
          Calendar calendar = Calendar.getInstance();
          calendar.set(Integer.parseInt(dateString[2]),
              parseMonth(dateString[1]), Integer.parseInt(dateString[0]),
              Integer.parseInt(timeString[0]), Integer.parseInt(timeString[1]),
              Integer.parseInt(timeString[2]));
          Date date = calendar.getTime();
          double x = Double.parseDouble(line[longitudeIndex]);
          double y = Double.parseDouble(line[latitudeIndex]);
          GM_Point point = new GM_Point(new DirectPosition(x, y));
          DefaultFeature feature = new DefaultFeature(point);
          feature.setAttributes(new Object[]{date, line[timeIndex]});
          feature.setSchema(schemaDefaultFeature);
          list.add(feature);
          maxX = Math.max(maxX, x);
          minX = Math.min(minX, x);
          maxY = Math.max(maxY, y);
          minY = Math.min(minY, y);
        }
      } finally {
        scanner.close();
      }
      Collections.sort(list, new Comparator<DefaultFeature>() {
        @Override
        public int compare(DefaultFeature o1, DefaultFeature o2) {
          String t1 = (String) o1.getAttribute(1);
          String t2 = (String) o2.getAttribute(1);
          String[] timeString1 = t1.split(":"); //$NON-NLS-1$
          String[] timeString2 = t2.split(":"); //$NON-NLS-1$
          Calendar calendar = Calendar.getInstance();
          calendar.set(0, 0,
              Integer.parseInt(timeString1[0]), Integer.parseInt(timeString1[1]),
              Integer.parseInt(timeString1[2]));
          Date date1 = calendar.getTime();
          calendar.set(0, 0,
              Integer.parseInt(timeString2[0]), Integer.parseInt(timeString2[1]),
              Integer.parseInt(timeString2[2]));
          Date date2 = calendar.getTime();
          return date1.compareTo(date2);
        }
      });
      population.addAll(list);
      if (initSpatialIndex) {
        population.initSpatialIndex(Tiling.class, true, new GM_Envelope(minX,
            maxX, minY, maxY), 10);
      }
      population.setCenter(new DirectPosition((maxX + minX) / 2,
          (maxY + minY) / 2));
      if (GPSTextfileReader.logger.isDebugEnabled()) {
        GPSTextfileReader.logger.debug(I18N
            .getString("ShapefileReader.SpatialIndexInitialised") //$NON-NLS-1$
            + minX + "," + maxX + "," //$NON-NLS-1$ //$NON-NLS-2$
            + minY + "," + maxY); //$NON-NLS-1$
      }
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    }
    return population;
  }

  private static int parseMonth(String string) {
    if (string.equalsIgnoreCase("jan")) { //$NON-NLS-1$
      return Calendar.JANUARY;
    }
    if (string.equalsIgnoreCase("feb")) { //$NON-NLS-1$
      return Calendar.FEBRUARY;
    }
    if (string.equalsIgnoreCase("mar")) { //$NON-NLS-1$
      return Calendar.MARCH;
    }
    if (string.equalsIgnoreCase("apr")) { //$NON-NLS-1$
      return Calendar.APRIL;
    }
    if (string.equalsIgnoreCase("may")) { //$NON-NLS-1$
      return Calendar.MAY;
    }
    if (string.equalsIgnoreCase("jun")) { //$NON-NLS-1$
      return Calendar.JUNE;
    }
    if (string.equalsIgnoreCase("jul")) { //$NON-NLS-1$
      return Calendar.JULY;
    }
    if (string.equalsIgnoreCase("aug")) { //$NON-NLS-1$
      return Calendar.AUGUST;
    }
    if (string.equalsIgnoreCase("sep")) { //$NON-NLS-1$
      return Calendar.SEPTEMBER;
    }
    if (string.equalsIgnoreCase("oct")) { //$NON-NLS-1$
      return Calendar.OCTOBER;
    }
    if (string.equalsIgnoreCase("nov")) { //$NON-NLS-1$
      return Calendar.NOVEMBER;
    }
    if (string.equalsIgnoreCase("dec")) { //$NON-NLS-1$
      return Calendar.DECEMBER;
    }
    return -1;
  }

  /**
   * Ouvre une fenetre (JFileChooser) afin de choisir le fichier et le charge.
   * Ce chargement est synchrone. Pour utiliser le chargement asynchrone,
   * utiliser le constructeur.
   * 
   * @see #read(String)
   * @see #read(String, String, DataSet, boolean)
   * 
   * @return une population contenant les features contenues dans le fichier.
   */
  public static Population<DefaultFeature> chooseAndReadShapefile() {
    JFileChooser choixFichierShape = new JFileChooser();
    /**
     * crée un filtre qui n'accepte que les fichier shp ou les répertoires
     */
    choixFichierShape.setFileFilter(new FileFilter() {
      @Override
      public boolean accept(File f) {
        return (f.isFile() && (f.getAbsolutePath().endsWith(".shp") //$NON-NLS-1$
            || f.getAbsolutePath().endsWith(".SHP")) //$NON-NLS-1$
        || f.isDirectory());
      }

      @Override
      public String getDescription() {
        return I18N.getString("ShapefileReader.ESRIShapefiles"); //$NON-NLS-1$
      }
    });
    choixFichierShape.setFileSelectionMode(JFileChooser.FILES_ONLY);
    choixFichierShape.setMultiSelectionEnabled(false);
    JFrame frame = new JFrame();
    frame.setVisible(true);
    int returnVal = choixFichierShape.showOpenDialog(frame);
    frame.dispose();
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      if (GPSTextfileReader.logger.isDebugEnabled()) {
        GPSTextfileReader.logger.debug(I18N
            .getString("ShapefileReader.YouChoseThisFile") //$NON-NLS-1$
            + choixFichierShape.getSelectedFile().getAbsolutePath());
      }
      return GPSTextfileReader.read(choixFichierShape.getSelectedFile()
          .getAbsolutePath());
    }
    return null;
  }

  protected static EventListenerList listenerList = new EventListenerList();

  /**
   * Adds an <code>ActionListener</code>.
   * @param l the <code>ActionListener</code> to be added
   */
  public static void addActionListener(ActionListener l) {
    GPSTextfileReader.listenerList.add(ActionListener.class, l);
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is lazily created.
   * @see EventListenerList
   */
  protected static void fireActionPerformed(ActionEvent event) {
    // Guaranteed to return a non-null array
    Object[] listeners = GPSTextfileReader.listenerList.getListenerList();
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
