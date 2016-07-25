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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;

/**
 * Classe permettant d'écrire des shapefiles à partir d'une collection de
 * Features.
 * 
 * @author Julien Perret
 * @author Bertrand Dumenieu
 */
public class ShapefileWriter {

	/** LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(ShapefileWriter.class.getName());

	/**
	 * Sauve une collection de features dans un fichier.
	 * 
	 * @param <Feature>
	 *            type des features contenu dans la collection
	 * @param featureCollection
	 *            collection de features à sauver dans le fichier shape
	 * @param shapefileName
	 *            nom du fichier dans lequel sauver les shapes
	 * @see #write(IFeatureCollection, String, CoordinateReferenceSystem)
	 */
	public static <Feature extends IFeature> void write(IFeatureCollection<Feature> featureCollection,
			String shapefileName) {
		ShapefileWriter.write(featureCollection, shapefileName, null);
	}

	/**
	 * Sauve une collection de features dans un fichier.
	 * 
	 * @param <Feature>
	 *            type des features contenu dans la collection
	 * @param featureCollection
	 *            collection de features à sauver dans le fichier shape
	 * @param shapefileName
	 *            nom du fichier dans lequel sauver les shapes
	 * @param crs
	 *            système de coordonnées
	 */
	@SuppressWarnings({ "unchecked" })
	public static <Feature extends IFeature> void write(IFeatureCollection<Feature> featureCollection,
			String shapefileName, CoordinateReferenceSystem crs) {

		// Test null case
		if (featureCollection.isEmpty()) {
			return;
		}

		// If not null
		try {
			if (!shapefileName.contains(".shp")) { //$NON-NLS-1$
				shapefileName = shapefileName + ".shp"; //$NON-NLS-1$
			}
			ShapefileDataStore store = new ShapefileDataStore(new File(shapefileName).toURI().toURL());

			String specs = "the_geom:"; //$NON-NLS-1$

			GF_FeatureType featureType = featureCollection.getFeatureType();
			if (featureType != null) {
				LOGGER.log(Level.FINE, "Using the collection's featureType"); //$NON-NLS-1$
				specs += AdapterFactory.toJTSGeometryType(featureType.getGeometryType()).getSimpleName();
				for (GF_AttributeType attributeType : featureType.getFeatureAttributes()) {
					Class<?> attributeClass = ShapefileWriter.valueType2Class(attributeType.getValueType());
					String attributeName = attributeType.getMemberName();
					if (AttributeType.class.isAssignableFrom(attributeType.getClass())) {
						attributeName = ((AttributeType) attributeType).getNomField();
					}
					specs += "," + attributeName //$NON-NLS-1$
							+ ":" //$NON-NLS-1$
							+ attributeClass.getName();
				}
			} else {
				LOGGER.log(Level.FINE, "Using the features' featureType"); //$NON-NLS-1$
				specs += AdapterFactory.toJTSGeometryType(featureCollection.get(0).getGeom().getClass())
						.getSimpleName();
				if (featureCollection.get(0).getFeatureType() != null) {
					featureType = featureCollection.get(0).getFeatureType();
					for (GF_AttributeType attributeType : featureType.getFeatureAttributes()) {
						Class<?> attributeClass = ShapefileWriter.valueType2Class(attributeType.getValueType());
						String attributeName = attributeType.getMemberName();
						if (AttributeType.class.isAssignableFrom(attributeType.getClass())) {
							attributeName = ((AttributeType) attributeType).getNomField();
						}
						specs += "," //$NON-NLS-1$
								+ attributeName + ":" //$NON-NLS-1$
								+ attributeClass.getName();
					}
				}
			}
			LOGGER.log(Level.FINE, "Specs = " + specs); //$NON-NLS-1$
			String featureTypeName = shapefileName.substring(shapefileName.lastIndexOf("/") + 1, //$NON-NLS-1$
					shapefileName.lastIndexOf(".")); //$NON-NLS-1$
			featureTypeName = featureTypeName.replace('.', '_');
			SimpleFeatureType type = DataUtilities.createType(featureTypeName, specs);
			store.createSchema(type);
			FeatureStore<SimpleFeatureType, SimpleFeature> featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>)store.getFeatureSource(featureTypeName);
			Transaction t = new DefaultTransaction();
			// FeatureCollection collection =
			// FeatureCollections.newCollection();
			DefaultFeatureCollection collection = new DefaultFeatureCollection("coll", type);
			int i = 1;
			for (Feature feature : featureCollection) {
				List<Object> liste = new ArrayList<Object>(0);
				liste.add(AdapterFactory.toGeometry(new GeometryFactory(), feature.getGeom()));
				if (featureType != null) {
					for (GF_AttributeType attributeType : featureType.getFeatureAttributes()) {
						liste.add(feature.getAttribute(attributeType.getMemberName()));
						LOGGER.log(Level.FINE,
								"Attribute " //$NON-NLS-1$
										+ attributeType.getMemberName() + " = " //$NON-NLS-1$
										+ feature.getAttribute(attributeType.getMemberName()));
					}
				}
				SimpleFeature simpleFeature = SimpleFeatureBuilder.build(type, liste.toArray(), String.valueOf(i++));
				collection.add(simpleFeature);
			}
			featureStore.addFeatures(collection);
			if (crs != null) {
				store.forceSchemaCRS(crs);
			}
			t.commit();
			t.close();
			store.dispose();
		} catch (MalformedURLException e) {
			LOGGER.log(Level.SEVERE, I18N.getString("ShapefileWriter.FileName") //$NON-NLS-1$
					+ shapefileName + I18N.getString("ShapefileWriter.Malformed")); //$NON-NLS-1$
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, I18N.getString("ShapefileWriter.ErrorWritingFile") //$NON-NLS-1$
					+ shapefileName);
			e.printStackTrace();
		} catch (SchemaException e) {
			LOGGER.log(Level.SEVERE, I18N.getString("ShapefileWriter.SchemeUsedForWritingFile") //$NON-NLS-1$
					+ shapefileName + I18N.getString("ShapefileWriter.Incorrect")); //$NON-NLS-1$
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, I18N.getString("ShapefileWriter.ErrorWritingFile") //$NON-NLS-1$
					+ shapefileName);
			e.printStackTrace();
		}
	}

	/**
	 * Renvoie la classe correspondant au nom d'un type primitif, null si le
	 * paramètre ne correspond pas à un type primitif ou s'il n'est pas géré.
	 * 
	 * @param valueType
	 *            nom d'un type primitif
	 * @return la classe correspondant au nom d'un type primitif ou null si le
	 *         paramètre ne correspond pas à un type primitif ou s'il n'est pas
	 *         géré. <b>Attention : les booléans sont convertis en strings car
	 *         les format ESRI shapefile ne les gère pas</b>
	 */
	public static Class<?> valueType2Class(String valueType) {
		if (valueType.equalsIgnoreCase("string")) { //$NON-NLS-1$
			return String.class;
		}
		if (valueType.equalsIgnoreCase("int") || valueType.equalsIgnoreCase("integer")) { //$NON-NLS-1$ //$NON-NLS-2$
			return Integer.class;
		}
		if (valueType.equalsIgnoreCase("double")) { //$NON-NLS-1$
			return Double.class;
		}
		if (valueType.equalsIgnoreCase("float")) { //$NON-NLS-1$
			return Float.class;
		}
		if (valueType.equalsIgnoreCase("long")) { //$NON-NLS-1$
			return Integer.class;
		}
		if (valueType.equalsIgnoreCase("boolean")) { //$NON-NLS-1$
			return String.class;
		}
		if (valueType.equalsIgnoreCase("date")) { //$NON-NLS-1$
			return Date.class;
		}
		return null;
	}

	/**
	 * Ouvre une fenêtre permettant à l'utilisateur de choisir le fichier dans
	 * lequel il souhaite sauver ses features. TODO faire en sorte que
	 * l'utilisateur puisse récupérer des fichiers sans extensions fichier
	 * shape.
	 * 
	 * @param <Feature>
	 *            type des features contenu dans la collection
	 * @param featureCollection
	 *            collection de features à sauver dans un
	 */
	public static <Feature extends IFeature> void chooseAndWriteShapefile(
			IFeatureCollection<Feature> featureCollection) {
		ShapefileWriter.chooseAndWriteShapefile(featureCollection, null);
	}

	/**
	 * Ouvre une fenêtre permettant à l'utilisateur de choisir le fichier dans
	 * lequel il souhaite sauver ses features. TODO faire en sorte que
	 * l'utilisateur puisse récupérer des fichiers sans extensions fichier
	 * shape.
	 * 
	 * @param <Feature>
	 *            type des features contenu dans la collection
	 * @param featureCollection
	 *            collection de features à sauver dans un
	 * @param crs
	 *            système de coordonnées
	 */
	public static <Feature extends IFeature> void chooseAndWriteShapefile(IFeatureCollection<Feature> featureCollection,
			CoordinateReferenceSystem crs) {
		JFileChooser choixFichierShape = new JFileChooser();
		choixFichierShape.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return (f.isFile() && f.getAbsolutePath().endsWith(".shp") //$NON-NLS-1$
						|| f.isDirectory());
			}

			@Override
			public String getDescription() {
				return I18N.getString("ShapefileWriter.ESRIShapefiles"); //$NON-NLS-1$
			}
		});
		choixFichierShape.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		choixFichierShape.setMultiSelectionEnabled(false);
		JFrame frame = new JFrame();
		frame.setVisible(true);
		int returnVal = choixFichierShape.showSaveDialog(frame);
		frame.dispose();
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String shapefileName = choixFichierShape.getSelectedFile().getAbsolutePath();
			if (!shapefileName.contains(".shp")) { //$NON-NLS-1$
				shapefileName = shapefileName + ".shp"; //$NON-NLS-1$
			}
			LOGGER.log(Level.FINE, I18N.getString("ShapefileWriter.YouChoseToSaveThisFile") //$NON-NLS-1$
					+ shapefileName);
			ShapefileWriter.write(featureCollection, shapefileName, crs);
		}
	}
}
