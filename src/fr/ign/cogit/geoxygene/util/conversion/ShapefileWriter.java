/**
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package fr.ign.cogit.geoxygene.util.conversion;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.type.GF_AttributeType;

/**
 * Classe permettant d'�crire des shapefiles à partir d'une collection de Features.
 * 
 * @author Julien Perret
 *
 */
public class ShapefileWriter {
	private final static Logger logger=Logger.getLogger(ShapefileWriter.class.getName());
	/**
	 * Sauve une collection de features dans un fichier.
	 * @param <Feature> type des features contenu dans la collection
	 * @param featureCollection collection de features à sauver dans le fichier shape
	 * @param shapefileName nom du fichier dans lequel sauver les shapes
	 */
	@SuppressWarnings("unchecked")
	public static <Feature extends FT_Feature> void write(FT_FeatureCollection<Feature> featureCollection, String shapefileName) {
		if (featureCollection.isEmpty()) return;
		try {
			ShapefileDataStore store = new ShapefileDataStore(new File(shapefileName).toURI().toURL());
			String specs="geom:"; //$NON-NLS-1$
			if (featureCollection.getFeatureType()!=null) {
				specs+=AdapterFactory.toJTSGeometryType(featureCollection.getFeatureType().getGeometryType()).getSimpleName();
				for(GF_AttributeType attributeType:featureCollection.getFeatureType().getFeatureAttributes()) {
					specs+=","+attributeType.getMemberName()+":"+valueType2Class(attributeType.getValueType()).getSimpleName(); //$NON-NLS-1$ //$NON-NLS-2$
				}
			} else {
				specs+=AdapterFactory.toJTSGeometryType(featureCollection.get(0).getGeom().getClass()).getSimpleName();
				if (featureCollection.get(0).getFeatureType()!=null)
					for(GF_AttributeType attributeType:featureCollection.get(0).getFeatureType().getFeatureAttributes()) {
						specs+=","+attributeType.getMemberName()+":"+valueType2Class(attributeType.getValueType()).getSimpleName();  //$NON-NLS-1$//$NON-NLS-2$
					}
			}
			String featureTypeName = shapefileName.substring(shapefileName.lastIndexOf("/")+1,shapefileName.lastIndexOf(".")); //$NON-NLS-1$ //$NON-NLS-2$
			featureTypeName=featureTypeName.replace('.', '_');
			SimpleFeatureType type = DataUtilities.createType(featureTypeName, specs);
			store.createSchema(type);
			FeatureStore featureStore = (FeatureStore) store.getFeatureSource(featureTypeName);
			Transaction t = new DefaultTransaction();
			FeatureCollection collection = FeatureCollections.newCollection();
			int i = 1;
			for(Feature feature:featureCollection) {
				List<Object> liste = new ArrayList<Object>();
				liste.add(AdapterFactory.toGeometry(new GeometryFactory(), feature.getGeom()));
				if (feature.getFeatureType()!=null)
					for(GF_AttributeType attributeType:feature.getFeatureType().getFeatureAttributes()) {
						liste.add(feature.getAttribute(attributeType.getMemberName()));
					}
				SimpleFeature simpleFeature = SimpleFeatureBuilder.build(type, liste.toArray(), String.valueOf(i++));
				collection.add(simpleFeature);
			}
			featureStore.addFeatures(collection);
			t.commit();
			t.close();			
			store.dispose();
		} catch (MalformedURLException e) {
			logger.error("Le nom du fichier "+shapefileName+" est mal form�");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Erreur pendant l'�criture du fichier "+shapefileName);
			e.printStackTrace();
		} catch (SchemaException e) {
			logger.error("Le schéma utilisé pour l'�criture du fichier "+shapefileName+" est incorrect");
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("Erreur pendant l'�criture du fichier "+shapefileName);
			e.printStackTrace();
		}
	}
	/**
	 * Renvoie la classe correspondant au nom d'un type primitif, 
	 * null si le paramètre ne correspond pas à un type primitif ou 
	 * s'il n'est pas géré.
	 * @param valueType nom d'un type primitif
	 * @return la classe correspondant au nom d'un type primitif ou 
	 * null si le paramètre ne correspond pas à un type primitif ou 
	 * s'il n'est pas géré. <b>Attention : les bool�ans sont convertis en strings car
	 * les format ESRI shapefile ne les gère pas</b>
	 */
	public static Class<?> valueType2Class(String valueType) {
		if(valueType.equalsIgnoreCase("string")) {return String.class;} //$NON-NLS-1$
		if(valueType.equalsIgnoreCase("integer")) {return Integer.class;} //$NON-NLS-1$
		if(valueType.equalsIgnoreCase("double")) {return Double.class;} //$NON-NLS-1$
		if(valueType.equalsIgnoreCase("long")) {return Integer.class;} //$NON-NLS-1$
		if(valueType.equalsIgnoreCase("boolean")) {return String.class;} //$NON-NLS-1$
		return null;
	}
	/**
	 * Ouvre une fenêtre permettant à l'utilisateur de choisir le fichier
	 * dans lequel il souhaite sauver ses features.
	 * @param <Feature> type des features contenu dans la collection
	 * @param featureCollection collection de features à sauver dans un fichier shape.
	 */
	public static <Feature extends FT_Feature> void chooseAndWriteShapefile(FT_FeatureCollection<Feature> featureCollection) {
		JFileChooser choixFichierShape = new JFileChooser();
		choixFichierShape.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File f) {return (f.isFile()&&f.getAbsolutePath().endsWith(".shp")||f.isDirectory());} //$NON-NLS-1$
			@Override
			public String getDescription() {return "fichiers ESRI shapefile";}
		});
		choixFichierShape.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		choixFichierShape.setMultiSelectionEnabled(false);
		JFrame frame = new JFrame();
		frame.setVisible(true);
		int returnVal = choixFichierShape.showSaveDialog(frame);
		frame.dispose();
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			if (logger.isDebugEnabled())
				logger.debug("You chose to save this file: "+choixFichierShape.getSelectedFile().getAbsolutePath());
			write(featureCollection,choixFichierShape.getSelectedFile().getAbsolutePath());
		}
	}
}