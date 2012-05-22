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

package fr.ign.cogit.geoxygene.example;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.util.viewer.ObjectViewer;

/**
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */

public class TestViewer {

  // noms des classes a visualiser
  private static String featureClassName1 = "geoxygene.geodata.Departement";
  private static String featureClassName2 = "geoxygene.geodata.Bdc38_canton";
  // noms des themes a afficher dans le viewer
  private static String nomTheme1 = "departement";
  private static String nomTheme2 = "canton 38";

  private static Class<?> class1, class2;

  public TestViewer() {
  }

  public static void main(String args[]) throws Exception {

    Geodatabase db = GeodatabaseOjbFactory.newInstance();

    // init viewer with Geodatabase connection
    ObjectViewer vwr = new ObjectViewer(db);

    try {
      TestViewer.class1 = Class.forName(TestViewer.featureClassName1);
      TestViewer.class2 = Class.forName(TestViewer.featureClassName2);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    // load and display all features from a class
    IFeatureCollection<IFeature> collection1 = db
        .loadAllFeatures(TestViewer.class1);
    System.out.println("size of collection 1 : " + collection1.size());
    vwr.addFeatureCollection(collection1, TestViewer.nomTheme1);

    // load and display all features from a class
    IFeatureCollection<IFeature> collection2 = db
        .loadAllFeatures(TestViewer.class2);
    vwr.addFeatureCollection(collection2, TestViewer.nomTheme2);
    System.out.println("size of collection 2 : " + collection2.size());

    // add a new feature to a collection and refresh viewer
    System.out.println("Adding new object ...");
    IFeature feat = collection1.get(83);
    IGeometry newGeom = feat.getGeom().translate(150000, 150000, 0);
    IFeature newFeature = (FT_Feature) TestViewer.class1.newInstance();
    newFeature.setGeom(newGeom);
    collection1.add(newFeature);
    System.out.println("size of collection 1 : " + collection1.size());
    vwr.refreshFeatureCollection(collection1, TestViewer.nomTheme1); // ->
                                                                     // refresh
                                                                     // all
                                                                     // collection
    // vwr.refreshFeatureCollection(newFeature,nomTheme1); // -> refresh only
    // object

    // modifiy a geometry and refresh viewer
    System.out.println("Computing buffer ...");
    newGeom = feat.getGeom().buffer(-10000);
    feat.setGeom(newGeom);
    vwr.refreshFeatureCollection(feat, TestViewer.nomTheme1);

    // add an image with coordinates (Xmin, Ymin, width, height)
    // vwr.addAnImage(new URL
    // ("file:///home/braun/geotools1/dataset/SCAN25/scan25_extrait.jpg"),848120,124836,30000,21000);
    // add a Shapefile
    // vwr.addAShapefile( new URL
    // ("file:///home/braun/geotools1/dataset/departement"));

  }

}
