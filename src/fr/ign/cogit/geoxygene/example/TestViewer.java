/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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

package fr.ign.cogit.geoxygene.example;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbOracle;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.viewer.ObjectViewer;

/**
 * It is a simple test program to know how to use the (Geo)ObjectViewer.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class TestViewer {
	
	// choisir les noms de classe de test ici
	private static String featureClassName1 = "donnees.defaut.Departement";
	private static String featureClassName2 = "donnees.defaut.Bdc38_canton";	
	// choisir les noms de themes a apparaitre dans le viewer ici
	private static String nomTheme1 = "departement";
	private static String nomTheme2 = "canton 38";
	
	private static Class class1, class2;
	
	public TestViewer() {
	}


	public static void main(String args[]) throws Exception {
		
		Geodatabase db = new GeodatabaseOjbOracle();		
		
		// init viewer with Geodatabase connection
		ObjectViewer vwr = new ObjectViewer(db);
		
		// init viewer without Geodatabase connection
		//ObjectViewer vwr = new ObjectViewer();	
		
		try {
			class1 = Class.forName(featureClassName1);
			class2 = Class.forName(featureClassName2);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// load and display all features from a class
		FT_FeatureCollection collection1 = db.loadAllFeatures(class1);
		System.out.println("size of collection 1 : "+collection1.size());
		vwr.addFeatureCollection(collection1, nomTheme1);
		
		// load and display all features from a class
		FT_FeatureCollection collection2 = db.loadAllFeatures(class2);	
		vwr.addFeatureCollection(collection2, nomTheme2);	
		System.out.println("size of collection 2 : "+collection2.size());
		
		// add a new feature to a collection and refresh viewer
		System.out.println("Adding new object ...");
		FT_Feature feat = collection1.get(83);	
		GM_Object newGeom = feat.getGeom().translate(150000,150000,0);
		FT_Feature newFeature = (FT_Feature) class1.newInstance();
		newFeature.setGeom(newGeom);
		collection1.add(newFeature);
		System.out.println("size of collection 1 : "+collection1.size());
		vwr.refreshFeatureCollection(collection1,nomTheme1); 	// -> refresh all collection
		//vwr.refreshFeatureCollection(newFeature,nomTheme1); 	// -> refresh only object
	
		// modifiy a geometry and refresh viewer
		System.out.println("Computing buffer ...");
		newGeom = feat.getGeom().buffer(-10000);
		feat.setGeom(newGeom);
		vwr.refreshFeatureCollection(feat,nomTheme1);
	
		// add an image with coordinates (Xmin, Ymin, width, height)
		// vwr.addAnImage(new URL ("file:///home/braun/geotools1/dataset/SCAN25/scan25_extrait.jpg"),848120,124836,30000,21000);
		// add a Shapefile
		//vwr.addAShapefile( new URL ("file:///home/braun/geotools1/dataset/departement"));
			
		System.out.println(" #### OK #### ");

	}

}
