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

package fr.ign.cogit.geoxygene.util.viewer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Observable;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;


/**
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

public class ObjectViewer extends Observable {
	
	private static final String OBJECTVIEWER_TITLE = "GeOxygene Object Viewer";

	public ObjectViewerInterface objectViewerInterface;

	/** Creates a new ObjectViewer without connection to a Geodatabase.*/
	public ObjectViewer() {
		this (null);
	}
		
	/** Creates a new ObjectViewer with a connection to a Geodatabase. */
	public ObjectViewer (Geodatabase db) {
		objectViewerInterface = new ObjectViewerInterface(OBJECTVIEWER_TITLE, db);
		addObserver(objectViewerInterface);
		objectViewerInterface.pack();
		objectViewerInterface.setSize(600, 400);
		objectViewerInterface.show();
		objectViewerInterface.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				objectViewerInterface.dispose();
				//System.exit(0);
			}
		});
	}
	
	/** Display objects stored in the FeatureCollection as a Theme with the given name. */
	public void addFeatureCollection (FT_FeatureCollection fColl, String themeName) {
		objectViewerInterface.addAFeatureCollectionTheme(fColl,themeName);
	}
		
	/** Refresh fully the FeatureCollection displayed in the viewer with this given name. */
	public void refreshFeatureCollection (FT_FeatureCollection fColl, String themeName) {
		objectViewerInterface.refreshAFeatureCollectionTheme(fColl, themeName);		
	}
	
	/** Refresh the FeatureCollection displayed in the viewer with this given name with this feature.
	 * The feature must already belong to the collection. */
	public void refreshFeatureCollection (FT_Feature feature, String themeName) {
		objectViewerInterface.refreshAFeatureCollectionTheme (feature, themeName);		
	}	

	/** Display objects stored in the shapefile given by url (without .shp) in an ObjectViewer.	*/
	public void addAShapefile(URL url) {
		objectViewerInterface.addAShapefileTheme(url);
	}
	
	/** Display an image (.jpg or .gif) in an ObjectViewer.	*/
	public void addAnImage(URL url, int x, int y, int width, int height) {
		objectViewerInterface.addAnImageTheme(url.getFile(), x, y, width, height);
	}

}
