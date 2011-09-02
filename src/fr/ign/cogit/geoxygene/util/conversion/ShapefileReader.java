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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.prj.PrjFileReader;
import org.geotools.data.shapefile.shp.ShapeType;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.api.feature.IDataSet;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Classe permettant de lire des shapefiles et de créer une population de
 * DefautFeatures. Le schéma et le FeatureType associé sont créés au passage. Il
 * existe deux principales possibilités pour l'utiliser :
 * <ul>
 * <li>de façon <b>asynchrone</b>. Pour ce, il faut créer un objet
 * ShapefileReader et exécuter la méthode read dessus. Celà lance un nouveau
 * processus qui lit les features et ajoute les objets à la population au fur et
 * à mesure.
 * <li>de façon <b>synchrone</b>. Pour ce, utiliser une des méthodes statiques
 * read ou chooseAndReadShapefile.
 * </ul>
 * @author Julien Perret
 * @author Bertrand Dumenieu
 */
public class ShapefileReader implements Runnable {
  private final static Logger logger = Logger.getLogger(ShapefileReader.class
      .getName());
  String shapefileName;
  String populationName;
  IDataSet dataset;
  CoordinateReferenceSystem crs;
  SchemaDefaultFeature schemaDefaultFeature;
  FeatureSource<SimpleFeatureType, SimpleFeature> source;
  IPopulation<IFeature> population;
  Reader reader = null;

  public Reader getReader() {
    return this.reader;
  }

  /**
   * Renvoie la population dans laquelle les objets sont chargés.
   * @return la population dans laquelle les objets sont chargés
   */
  public IPopulation<IFeature> getPopulation() {
    return this.population;
  }

  /**
   * Constructeur de shapefileReader. L'utilisation de ce constructeur n'a de
   * sens que si l'on souhaite utiliser le chargement asynchrone. Pour utiliser
   * ce dernier, on contruit un objet et on lance le chargement en utilisant la
   * méthode <code> read </code>.
   * <p>
   * Pour utiliser le chargement synchrone, utiliser l'une des méthode statique
   * <code> read </code>.
   * 
   * @see #read()
   * @see #read(String)
   * @see #read(String, String, IDataSet, boolean)
   * @see #chooseAndReadShapefile()
   * @see #initSchema(String, SchemaDefaultFeature, IPopulation, boolean)
   * 
   * @param shapefileName nom du fichier à charger
   * @param populationName nom de la population à créer et à l'intérieur de
   *          laquelle les objets sont ajoutés
   * @param dataset nom du dataset auquel la population est ajoutée
   * @param initSpatialIndex vrai si l'on veut créer un index spatial sur la
   *          population et le mettre à jour pendant l'ajout des objets
   */
  public ShapefileReader(String shapefileName, String populationName,
      IDataSet dataset, boolean initSpatialIndex) {
    this.shapefileName = shapefileName;
    this.populationName = populationName;
    this.dataset = dataset;
    this.population = new Population<IFeature>(populationName);
    if (dataset != null) {
      dataset.addPopulation(this.population);
    }
    this.schemaDefaultFeature = new SchemaDefaultFeature();
    /** Initialise le schéma */
    this.reader = ShapefileReader.initSchema(shapefileName,
        this.schemaDefaultFeature, this.population, initSpatialIndex);
    this.crs = this.reader.getCRS();
    this.population.setEnvelope(new GM_Envelope(this.reader.minX,
            this.reader.maxX, this.reader.minY, this.reader.maxY));
  }

  /**
   * Utilisée pour lancer le chargement asynchrone.
   * @see #ShapefileReader(String, String, DataSet, boolean)
   */
  public void read() {

    new Thread(this).start();

  }

  /**
   * Lit les features contenus dans le fichier en paramètre. Ce chargement est
   * synchrone
   * <p>
   * Pour utiliser le chargement asynchrone, utiliser le constructeur.
   * 
   * @see #read()
   * @see #read(String, String, IDataSet, boolean)
   * @see #chooseAndReadShapefile()
   * 
   * @param shapefileName un shapefile
   * @return une population contenant les features contenues dans le fichier.
   */
  public static IPopulation<IFeature> read(String shapefileName) {
    return ShapefileReader.read(shapefileName, false);
  }

  /**
   * Lit les features contenus dans le fichier en paramètre. Ce chargement est
   * synchrone
   * <p>
   * Pour utiliser le chargement asynchrone, utiliser le constructeur.
   * 
   * @see #read()
   * @see #read(String, String, IDataSet, boolean)
   * @see #chooseAndReadShapefile()
   * 
   * @param shapefileName un shapefile
   * @return une population contenant les features contenues dans le fichier.
   */
  public static IPopulation<IFeature> read(String shapefileName,
      boolean initSpatialIndex) {
    return ShapefileReader.read(shapefileName, shapefileName.substring(
        shapefileName.lastIndexOf(File.pathSeparator) + 1,
        shapefileName.lastIndexOf(".")), null, initSpatialIndex); //$NON-NLS-1$
  }

  /**
   * Lit les features contenus dans le fichier en paramètre et ajoute la
   * population chargée à un dataset. Ce chargement est synchrone Pour utiliser
   * le chargement asynchrone, utiliser le constructeur. Si le paramètre
   * initSpatialIndex est vrai, alors on initialise aussi l'index spatial de la
   * population.
   * 
   * @see #read()
   * @see #read(String)
   * @see #chooseAndReadShapefile()
   * @see #initSchema(String, SchemaDefaultFeature, IPopulation, boolean)
   * @see #read(Reader, SchemaDefaultFeature, IPopulation)
   * 
   * @param shapefileName un shapefile
   * @param populationName non de la population
   * @param dataset jeu de données auquel ajouter la population
   * @param initSpatialIndex si ce boolean est vrai, alors on initialise la
   *          population.
   * @return une population contenant les features contenues dans le fichier.
   */
  public static IPopulation<IFeature> read(String shapefileName,
      String populationName, IDataSet dataset, boolean initSpatialIndex) {
    // creation de la collection de features
    Population<IFeature> population = new Population<IFeature>(populationName);
    if (dataset != null) {
      dataset.addPopulation(population);
    }
    try {
      SchemaDefaultFeature schemaDefaultFeature = new SchemaDefaultFeature();
      schemaDefaultFeature.setNom(populationName);
      schemaDefaultFeature.setNomSchema(populationName);
      /** Initialise le schéma */
      Reader reader = ShapefileReader.initSchema(shapefileName,
          schemaDefaultFeature, population, initSpatialIndex);
      if (reader == null) {
        return null;
      }
      /**
       * Parcours de features du fichier et création de Default features
       * équivalents
       */
      ShapefileReader.read(reader, schemaDefaultFeature, population);
    } catch (MalformedURLException e1) {
      ShapefileReader.logger.error(I18N.getString("ShapefileReader.FileURL") //$NON-NLS-1$
          + shapefileName
          + I18N.getString("ShapefileReader.MalformedNullResult")); //$NON-NLS-1$
      return null;
    } catch (IOException e) {
      ShapefileReader.logger.error(I18N
          .getString("ShapefileReader.ProblemReadingFile") //$NON-NLS-1$
          + shapefileName + I18N.getString("ShapefileReader.FileNotLoaded")); //$NON-NLS-1$
      return null;
    }
    return population;
  }

  /**
   * Ouvre une fenetre (JFileChooser) afin de choisir le fichier et le charge.
   * Ce chargement est synchrone. Pour utiliser le chargement asynchrone,
   * utiliser le constructeur.
   * 
   * @see #read()
   * @see #read(String)
   * @see #read(String, String, IDataSet, boolean)
   * 
   * @return une population contenant les features contenues dans le fichier.
   */
  public static IPopulation<IFeature> chooseAndReadShapefile() {
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
      if (ShapefileReader.logger.isDebugEnabled()) {
        ShapefileReader.logger.debug(I18N
            .getString("ShapefileReader.YouChoseThisFile") //$NON-NLS-1$
            + choixFichierShape.getSelectedFile().getAbsolutePath());
      }
      return ShapefileReader.read(choixFichierShape.getSelectedFile()
          .getAbsolutePath());
    }
    return null;
  }

  /**
   * Initialise le schéma utilisé pour les nouveaux features.
   * @see #read(Reader, SchemaDefaultFeature, IPopulation)
   * @param shapefileName nom du fichier à lire
   * @param schemaDefaultFeature schéma à initialiser
   * @param population population à peupler avec les features
   * @param initSpatialIndex vrai si on souhaite initialiser l'index spatial de
   *          la population
   */
  public static Reader initSchema(String shapefileName,
      SchemaDefaultFeature schemaDefaultFeature,
      IPopulation<IFeature> population, boolean initSpatialIndex) {
    Reader reader = null;
    try {
      reader = new Reader(shapefileName);
    } catch (MalformedURLException e) {
      ShapefileReader.logger.error("URL " + shapefileName //$NON-NLS-1$
          + I18N.getString("ShapefileReader.Malformed")); //$NON-NLS-1$
      return null;
    }
    double minX = reader.getMinX();
    double maxX = reader.getMaxX();
    double minY = reader.getMinY();
    double maxY = reader.getMaxY();
    if (initSpatialIndex) {
      population.initSpatialIndex(Tiling.class, true, new GM_Envelope(minX,
          maxX, minY, maxY), 10);
    }
    population.setCenter(new DirectPosition((maxX + minX) / 2,
        (maxY + minY) / 2));
    if (ShapefileReader.logger.isTraceEnabled()) {
      ShapefileReader.logger.trace(I18N
          .getString("ShapefileReader.SpatialIndexInitialised") //$NON-NLS-1$
          + minX + "," + maxX + "," //$NON-NLS-1$ //$NON-NLS-2$
          + minY + "," + maxY); //$NON-NLS-1$
    }
    /** Créer un featuretype de jeu correspondant */
    fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType newFeatureType = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType();
    newFeatureType.setTypeName(population.getNom());
    int nbFields = reader.getNbFields();
    Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);
    for (int i = 0; i < nbFields; i++) {
      AttributeType type = new AttributeType();
      String nomField = reader.getFieldName(i);
      String memberName = reader.getFieldName(i);
      String valueType = reader.getFieldClass(i).getSimpleName();
      type.setNomField(nomField);
      type.setMemberName(memberName);
      type.setValueType(valueType);
      newFeatureType.addFeatureAttribute(type);
      attLookup.put(new Integer(i), new String[] { nomField, memberName });
      if (ShapefileReader.logger.isDebugEnabled()) {
        ShapefileReader.logger.debug(I18N
            .getString("ShapefileReader.AddingAttribute") + i //$NON-NLS-1$
            + " = " + nomField); //$NON-NLS-1$
      }
    }
    /** Création d'un schéma associé au featureType */
    newFeatureType
        .setGeometryType((reader.getShapeType() == null) ? GM_Object.class
            : reader.getShapeType());
    if (ShapefileReader.logger.isDebugEnabled()) {
      ShapefileReader.logger.debug("shapeType = " + reader.getShapeType() //$NON-NLS-1$
          + I18N.getString("ShapefileReader.GeometryType") //$NON-NLS-1$
          + newFeatureType.getGeometryType());
    }
    schemaDefaultFeature.setFeatureType(newFeatureType);
    newFeatureType.setSchema(schemaDefaultFeature);
    schemaDefaultFeature.setAttLookup(attLookup);
    population.setFeatureType(newFeatureType);
    if (ShapefileReader.logger.isDebugEnabled()) {
      for (GF_AttributeType fa : newFeatureType.getFeatureAttributes()) {
        ShapefileReader.logger.debug("FeatureAttibute = " //$NON-NLS-1$
            + fa.getMemberName() + "-" + fa.getValueType()); //$NON-NLS-1$
      }
    }
    return reader;
  }

  protected static EventListenerList listenerList = new EventListenerList();

  /**
   * Adds an <code>ActionListener</code>.
   * @param l the <code>ActionListener</code> to be added
   */
  public static void addActionListener(ActionListener l) {
    ShapefileReader.listenerList.add(ActionListener.class, l);
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is lazily created.
   * @see EventListenerList
   */
  protected static void fireActionPerformed(ActionEvent event) {
    // Guaranteed to return a non-null array
    Object[] listeners = ShapefileReader.listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ActionListener.class) {
        // Lazily create the event:
        ((ActionListener) listeners[i + 1]).actionPerformed(event);
      }
    }
  }

  /**
   * Lit la collection de features GeoTools <code> source </code> et crée des
   * default features correspondant en utilisant le schéma <code> schema
   * </code> et les ajoute à la population <code> population </code>.
   * @param schema schéma des features à créer
   * @param population population à laquelle ajouter les features créés
   * @throws IOException renvoie une exception en cas d'erreur de lecture
   */
  public static void read(Reader reader, SchemaDefaultFeature schema,
      IPopulation<IFeature> population) throws IOException {
    ShapefileReader.fireActionPerformed(new ActionEvent(population, 0,
        "Read", reader.getNbFeatures())); //$NON-NLS-1$
    for (int indexFeature = 0; indexFeature < reader.getNbFeatures(); indexFeature++) {
      DefaultFeature defaultFeature = new DefaultFeature();
      defaultFeature.setFeatureType(schema.getFeatureType());
      defaultFeature.setSchema(schema);
      defaultFeature.setAttributes(reader.fieldValues[indexFeature]);
      Class<? extends IGeometry> geometryType = schema.getFeatureType()
          .getGeometryType();
      try {
        if (reader.geometries[indexFeature] == null) {
          ShapefileReader.logger.error("null geometry for object "
              + indexFeature);
          ShapefileReader.logger.error(I18N.getString("ShapefileReader" + //$NON-NLS-1$
              ".NullGeometryObjectIGnored")); //$NON-NLS-1$
        } else {
          IGeometry geometry = AdapterFactory
              .toGM_Object(reader.geometries[indexFeature]);
          if (!geometryType.isAssignableFrom(geometry.getClass())) {
            if (ShapefileReader.logger.isDebugEnabled()) {
              ShapefileReader.logger.debug("Geometry of type " //$NON-NLS-1$
                  + geometry.getClass().getSimpleName() + " instead of " //$NON-NLS-1$
                  + geometryType.getSimpleName());
            }
            // TODO make it more robust: a lot of assumptions here
            if (geometry instanceof GM_MultiSurface<?>) {
              geometry = ((GM_MultiSurface<?>) geometry).get(0);
            } else {
              if (geometry instanceof GM_MultiCurve<?>) {
                geometry = ((GM_MultiCurve<?>) geometry).get(0);
              } else {
                if (geometry instanceof GM_MultiPoint) {
                  geometry = ((GM_MultiPoint) geometry).get(0);
                }
              }
            }
          }
          defaultFeature.setGeom(geometry);
          defaultFeature.setId(indexFeature);
          population.add(defaultFeature);
          ShapefileReader.fireActionPerformed(new ActionEvent(population, 1,
              "Read", indexFeature)); //$NON-NLS-1$
        }
      } catch (Exception e) {
        ShapefileReader.logger.error(I18N.getString("ShapefileReader" + //$NON-NLS-1$
            ".ProblemWhileConvertingGeometry") //$NON-NLS-1$
            + I18N.getString("ShapefileReader.ObjectIgnored")); //$NON-NLS-1$
      }
    }
    ShapefileReader.logger.debug(population.size() + " features created for "
        + reader.getNbFeatures());
    ShapefileReader.fireActionPerformed(new ActionEvent(population, 2,
        "Finished", reader.getNbFeatures())); //$NON-NLS-1$
  }

  @Override
  public void run() {
    try {
      ShapefileReader.read(this.reader, this.schemaDefaultFeature,
          this.population);
    } catch (IOException e) {
      ShapefileReader.logger.error(I18N
          .getString("ShapefileReader.ProblemReadingFile") //$NON-NLS-1$
          + this.shapefileName
          + I18N.getString("ShapefileReader.FileNotLoaded")); //$NON-NLS-1$
    }
  }

  /**
   * @return maximum X value
   */
  public double getMaxX() {
    return this.reader.getMaxX();
  }

  public double getMinX() {
    return this.reader.getMinX();
  }

  public double getMaxY() {
    return this.reader.getMaxY();
  }

  public double getMinY() {
    return this.reader.getMinY();
  }

  public CoordinateReferenceSystem getCRS() {
    return this.crs;
  }

}

/**
 * @author Julien Perret
 * 
 */
class Reader {
  private final static Logger logger = Logger.getLogger(Reader.class.getName());
  String shapefileName;
  double minX;
  double maxX;
  double minY;
  double maxY;
  int nbFields;
  int nbFeatures;
  Object[][] fieldValues;
  String[] fieldNames;
  Class<?>[] fieldClasses;
  Geometry[] geometries;
  Class<? extends GM_Object> shapeType;
  CoordinateReferenceSystem localCRS;

  public Reader(String shapefileName) throws MalformedURLException {
    this.shapefileName = shapefileName;
    org.geotools.data.shapefile.shp.ShapefileReader shapefileReader = null;
    org.geotools.data.shapefile.dbf.DbaseFileReader dbaseFileReader = null;
    PrjFileReader prjFileReader = null;
    ShpFiles shpf;
    shpf = new ShpFiles(shapefileName);
    try {
      shapefileReader = new org.geotools.data.shapefile.shp.ShapefileReader(
          shpf, true, false, new GeometryFactory());
      dbaseFileReader = new org.geotools.data.shapefile.dbf.DbaseFileReader(
          shpf, true, Charset.forName("ISO-8859-1")); //$NON-NLS-1$
    } catch (FileNotFoundException e) {
      if (Reader.logger.isDebugEnabled()) {
        Reader.logger
            .debug(I18N.getString("ShapefileReader.File") + shapefileName //$NON-NLS-1$
                + I18N.getString("ShapefileReader.NotFound")); //$NON-NLS-1$
      }
      return;
    } catch (ShapefileException e) {
      Reader.logger.error(I18N
          .getString("ShapefileReader.ErrorReadingShapefile") //$NON-NLS-1$
          + shapefileName);
      return;
    } catch (IOException e) {
      Reader.logger.error(I18N.getString("ShapefileReader.ErrorReadingFile") //$NON-NLS-1$
          + shapefileName);
      e.printStackTrace();
      return;
    }
    try {
      prjFileReader = new PrjFileReader(shpf);
    } catch (FileNotFoundException e) {
      if (Reader.logger.isDebugEnabled()) {
        Reader.logger
            .debug(I18N.getString("ShapefileReader.PrjFile") + shapefileName //$NON-NLS-1$
                + I18N.getString("ShapefileReader.NotFound")); //$NON-NLS-1$
      }
    } catch (ShapefileException e) {
      if (Reader.logger.isDebugEnabled()) {
        Reader.logger.debug(I18N
            .getString("ShapefileReader.ErrorReadingPrjFile") //$NON-NLS-1$
            + shapefileName);
      }
    } catch (IOException e) {
      if (Reader.logger.isDebugEnabled()) {
        Reader.logger.debug(I18N
            .getString("ShapefileReader.ErrorReadingPrjFile") //$NON-NLS-1$
            + shapefileName);
      }
    }

    this.minX = shapefileReader.getHeader().minX();
    this.maxX = shapefileReader.getHeader().maxX();
    this.minY = shapefileReader.getHeader().minY();
    this.maxY = shapefileReader.getHeader().maxY();
    this.shapeType = Reader.geometryType(shapefileReader.getHeader()
        .getShapeType());
    this.nbFields = dbaseFileReader.getHeader().getNumFields();
    this.nbFeatures = dbaseFileReader.getHeader().getNumRecords();
    this.fieldValues = new Object[this.nbFeatures][this.nbFields];
    this.fieldNames = new String[this.nbFields];
    this.fieldClasses = new Class<?>[this.nbFields];
    for (int i = 0; i < this.nbFields; i++) {
      this.fieldNames[i] = dbaseFileReader.getHeader().getFieldName(i);
      this.fieldClasses[i] = dbaseFileReader.getHeader().getFieldClass(i);
      Reader.logger.debug("field " + i + " = " + this.fieldNames[i]);
    }
    /*
     * // FIXME gère le SRID String wkt =
     * "PROJCS[\"unnamed\",GEOGCS[\"DHDN\",DATUM[\"Deutsches_Hauptdreiecksnetz\",SPHEROID[\"Bessel 1841\",6377397.155,299.1528128,AUTHORITY[\"EPSG\",\"7004\"]],TOWGS84[606,23,413,0,0,0,0],AUTHORITY[\"EPSG\",\"6314\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4314\"]],PROJECTION[\"Cassini_Soldner\"],PARAMETER[\"latitude_of_origin\",52.41864827777778],PARAMETER[\"central_meridian\",13.62720366666667],PARAMETER[\"false_easting\",40000],PARAMETER[\"false_northing\",10000],UNIT[\"Meter\",1]]"
     * ; // String wkt =
     * "PROJCS[\"NAD_1983_StatePlane_Massachusetts_Mainland_FIPS_2001\",GEOGCS[\"GCS_North_American_1983\",DATUM[\"D_North_American_1983\",SPHEROID[\"GRS_1980\", 6378137.0, 298.257222101]],PRIMEM[\"Greenwich\", 0.0], UNIT[\"degree\", 0.017453292519943295],AXIS[\"Longitude\", EAST],AXIS[\"Latitude\", NORTH]],PROJECTION[\"Lambert_Conformal_Conic\"], PARAMETER[\"central_meridian\", -71.5],PARAMETER[\"latitude_of_origin\", 41.0],PARAMETER[\"standard_parallel_1\", 41.71666666666667],PARAMETER[\"scale_factor\", 1.0],PARAMETER[\"false_easting\", 200000.0],PARAMETER[\"false_northing\", 750000.0],PARAMETER[\"standard_parallel_2\", 42.68333333333334],UNIT[\"m\", 1.0],AXIS[\"x\", EAST],AXIS[\"y\", NORTH]] "
     * ; CoordinateReferenceSystem example; try {
     * System.out.println(GeoTools.getVersion()); example = CRS.parseWKT(wkt); }
     * catch (FactoryException e3) { // TODO Auto-generated catch block
     * e3.printStackTrace(); }
     * System.out.println(prjFileReader.getCoodinateSystem());
     * System.out.println("code = " +
     * prjFileReader.getCoodinateSystem().getName().getCode());
     * System.out.println("SRS=" +
     * CRS.toSRS(prjFileReader.getCoodinateSystem()));
     * 
     * try { System.out.println("SRS="+CRS.lookupIdentifier(prjFileReader
     * .getCoodinateSystem(),true));
     * System.out.println("SRS="+CRS.lookupEpsgCode(prjFileReader
     * .getCoodinateSystem(),true)); } catch (FactoryException e1) {
     * e1.printStackTrace(); }
     */
    if (prjFileReader != null) {
      this.localCRS = prjFileReader.getCoodinateSystem();
    }
    this.geometries = new Geometry[this.nbFeatures];
    int indexFeatures = 0;
    try {
      while (shapefileReader.hasNext() && dbaseFileReader.hasNext()) {
        Object[] entry = dbaseFileReader.readEntry();
        Record record = shapefileReader.nextRecord();
        try {
          this.geometries[indexFeatures] = (Geometry) record.shape();
        } catch (Exception e) {
          // logger.error("Error for geometry of object " + entry[2]);
          this.geometries[indexFeatures] = null;
        }
        for (int index = 0; index < this.nbFields; index++) {
          this.fieldValues[indexFeatures][index] = entry[index];
        }
        indexFeatures++;
      }
      Reader.logger.debug("Stopped at index " + indexFeatures + " with "
          + shapefileReader.hasNext() + " and " + dbaseFileReader.hasNext());
      shapefileReader.close();
      dbaseFileReader.close();
      if (prjFileReader != null) {
        prjFileReader.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Renvoie la valeur de l'attribut minX.
   * @return la valeur de l'attribut minX
   */
  public double getMinX() {
    return this.minX;
  }

  /**
   * Renvoie la valeur de l'attribut maxX.
   * @return la valeur de l'attribut maxX
   */
  public double getMaxX() {
    return this.maxX;
  }

  /**
   * Renvoie la valeur de l'attribut minY.
   * @return la valeur de l'attribut minY
   */
  public double getMinY() {
    return this.minY;
  }

  /**
   * Renvoie la valeur de l'attribut maxY.
   * @return la valeur de l'attribut maxY
   */
  public double getMaxY() {
    return this.maxY;
  }

  /**
   * Renvoie la valeur de l'attribut nbFields.
   * @return la valeur de l'attribut nbFields
   */
  public int getNbFields() {
    return this.nbFields;
  }

  /**
   * Renvoie la valeur de l'attribut nbFields.
   * @return la valeur de l'attribut nbFields
   */
  public int getNbFeatures() {
    return this.nbFeatures;
  }

  /**
   * @param i
   * @return the name of the given field
   */
  public String getFieldName(int i) {
    return this.fieldNames[i];
  }

  /**
   * @param i
   * @return the class of the given field
   */
  public Class<?> getFieldClass(int i) {
    return this.fieldClasses[i];
  }

  public Class<? extends GM_Object> getShapeType() {
    return this.shapeType;
  }

  /**
   * @param type
   * @return the class of the given geometry type
   */
  private static Class<? extends GM_Object> geometryType(ShapeType type) {
    if (Reader.logger.isDebugEnabled()) {
      Reader.logger.debug("shapeType = " + type); //$NON-NLS-1$
    }
    if (type.isPointType()) {
      return GM_Point.class;
    }
    if (type.isMultiPointType()) {
      return GM_MultiPoint.class;
    }
    if (type.isLineType()) {
      return GM_MultiCurve.class;
    }
    if (type.isPolygonType()) {
      return GM_MultiSurface.class;
    }
    return GM_MultiSurface.class;
  }

  public CoordinateReferenceSystem getCRS() {
    return this.localCRS;
  }
  
}
