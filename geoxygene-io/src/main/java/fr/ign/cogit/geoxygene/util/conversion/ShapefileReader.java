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
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
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
  
    private final static Logger logger = Logger.getLogger(ShapefileReader.class.getName());
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
   * @see #ShapefileReader(String, String, IDataSet, boolean)
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
      ShapefileReader.logger.log(
          Level.SEVERE,
          I18N.getString("ShapefileReader.FileURL") //$NON-NLS-1$
              + shapefileName
              + I18N.getString("ShapefileReader.MalformedNullResult")); //$NON-NLS-1$
      return null;
    } catch (IOException e) {
      ShapefileReader.logger
          .log(
              Level.SEVERE,
              I18N.getString("ShapefileReader.ProblemReadingFile") //$NON-NLS-1$
                  + shapefileName
                  + I18N.getString("ShapefileReader.FileNotLoaded")); //$NON-NLS-1$
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
      ShapefileReader.logger.log(Level.FINE,
          I18N.getString("ShapefileReader.YouChoseThisFile") //$NON-NLS-1$
              + choixFichierShape.getSelectedFile().getAbsolutePath());
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
    ShapefileReader.logger.log(Level.FINE, "INIT SCHEMA"); //$NON-NLS-1$
    try {
      reader = new Reader(shapefileName);
    } catch (MalformedURLException e) {
      ShapefileReader.logger.log(Level.SEVERE, "URL " + shapefileName //$NON-NLS-1$
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
    ShapefileReader.logger.log(Level.FINE,
        I18N.getString("ShapefileReader.SpatialIndexInitialised") //$NON-NLS-1$
            + minX + "," + maxX + "," //$NON-NLS-1$ //$NON-NLS-2$
            + minY + "," + maxY); //$NON-NLS-1$
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
      attLookup.put(i, new String[] { nomField, memberName });
      ShapefileReader.logger.log(Level.FINE,
          I18N.getString("ShapefileReader.AddingAttribute") + i //$NON-NLS-1$
              + " = " + nomField); //$NON-NLS-1$
    }
    /** Création d'un schéma associé au featureType */
    newFeatureType
        .setGeometryType((reader.getShapeType() == null) ? GM_Object.class
            : reader.getShapeType());
    ShapefileReader.logger.log(Level.FINE,
        "shapeType = " + reader.getShapeType() //$NON-NLS-1$
            + I18N.getString("ShapefileReader.GeometryType") //$NON-NLS-1$
            + newFeatureType.getGeometryType());
    schemaDefaultFeature.setFeatureType(newFeatureType);
    newFeatureType.setSchema(schemaDefaultFeature);
    schemaDefaultFeature.setAttLookup(attLookup);
    population.setFeatureType(newFeatureType);
    for (GF_AttributeType fa : newFeatureType.getFeatureAttributes()) {
      ShapefileReader.logger.log(Level.FINE, "FeatureAttibute = " //$NON-NLS-1$
          + fa.getMemberName() + "-" + fa.getValueType()); //$NON-NLS-1$
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
    	  	IGeometry geometry = null ;
			if (reader.geometries[indexFeature] == null) {
				// support for empty geometries added
				logger.log(
					Level.WARNING, 
					"null geometry for object "+ indexFeature+ " (considered EMPTY)"
				);
				geometry = new GM_Aggregate<IGeometry>();
			} else {
				geometry = AdapterFactory.toGM_Object(reader.geometries[indexFeature]);
			}
			
			if (!geometryType.isAssignableFrom(geometry.getClass())) {
				ShapefileReader.logger.log(Level.FINE,
						"Geometry of type " //$NON-NLS-1$
								+ geometry.getClass().getSimpleName() + " instead of " //$NON-NLS-1$
								+ geometryType.getSimpleName());
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
			ShapefileReader.fireActionPerformed(new ActionEvent(population, 1, "Read", indexFeature)); //$NON-NLS-1$
      } catch (Exception e) {
        ShapefileReader.logger.log(Level.SEVERE, I18N
            .getString("ShapefileReader" + //$NON-NLS-1$
                ".ProblemWhileConvertingGeometry") //$NON-NLS-1$
            + I18N.getString("ShapefileReader.ObjectIgnored")); //$NON-NLS-1$
      }
    }
    ShapefileReader.logger.log(Level.FINE, population.size()
        + " features created for " //$NON-NLS-1$
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
      ShapefileReader.logger.log(
          Level.SEVERE,
          I18N.getString("ShapefileReader.ProblemReadingFile") //$NON-NLS-1$
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



