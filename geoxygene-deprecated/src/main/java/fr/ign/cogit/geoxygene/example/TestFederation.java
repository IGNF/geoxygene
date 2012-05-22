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

import java.util.Iterator;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;

/**
 * Federation de donnees. Exemple d'utilisation de plusieurs "Geodatabase"
 * simultanees.
 * 
 * Remarque : l'utilisation de plusieurs transactions simultanees plante, a
 * etudier.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */

public class TestFederation {

  @SuppressWarnings("unchecked")
  public static void main(String args[]) {

    Geodatabase db1, db2, db3;
    Class<? extends FT_Feature> featureClass1 = null;
    Class<? extends FT_Feature> featureClass2 = null;
    long t1, t2;

    System.out.println("coucou");

    db1 = GeodatabaseOjbFactory.newInstance();
    db2 = GeodatabaseOjbFactory.newInstance("ORACLE_ALIAS_NCDB");
    db3 = GeodatabaseOjbFactory.newInstance("POSTGRES_ALIAS");

    System.out.println(db1);
    System.out.println(db2);
    System.out.println(db3);

    try {
      featureClass1 = (Class<? extends FT_Feature>) Class
          .forName("geoxygene.geodata.Troncon_voie_ferree");
      featureClass2 = (Class<? extends FT_Feature>) Class
          .forName("geoxygene.geodata.Troncon_voie_ferree_50");
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    System.out.println("nombre d'objets : " + db1.countObjects(featureClass1));
    System.out.println("nombre d'objets : " + db2.countObjects(featureClass2));

    System.out.print("chargement ... " + featureClass1.getName() + "   ");
    t1 = System.currentTimeMillis();
    IFeatureCollection<? extends IFeature> coll1 = db1
        .loadAllFeatures(featureClass1);
    t2 = System.currentTimeMillis();
    System.out.println((t2 - t1) / 1000.);

    System.out.print("chargement ... " + featureClass2.getName() + "  ");
    t1 = System.currentTimeMillis();
    IFeatureCollection<? extends IFeature> coll2 = db2
        .loadAllFeatures(featureClass2);
    t2 = System.currentTimeMillis();
    System.out.println((t2 - t1) / 1000.);

    System.out.println("Calcul buffer 1 ...");
    int i = 0;
    GM_Aggregate<IGeometry> aggr = new GM_Aggregate<IGeometry>();
    Iterator<? extends IFeature> iterator = coll1.iterator();
    while (iterator.hasNext()) {
      i++;
      IGeometry geom = iterator.next().getGeom();
      geom = geom.buffer(100.);
      aggr.add(geom);
      if (i > 1000) {
        break;
      }
    }

    System.out.println("Calcul buffer 2 ...");
    i = 0;
    iterator = coll2.iterator();
    while (iterator.hasNext()) {
      i++;
      IGeometry geom = iterator.next().getGeom();
      geom = geom.buffer(50.);
      aggr.add(geom);
      if (i > 1000) {
        break;
      }
    }

    db3.begin();
    System.out.println("begin db3");

    System.out.print("ecriture ... ");
    t1 = System.currentTimeMillis();
    for (IGeometry o : aggr) {
      Resultat res = new Resultat();
      db3.makePersistent(res);
      res.setGeom(o);
    }
    t2 = System.currentTimeMillis();
    System.out.println((t2 - t1) / 1000.);

    System.out.print("commit db3... ");
    t1 = System.currentTimeMillis();
    db3.commit();
    t2 = System.currentTimeMillis();
    System.out.println((t2 - t1) / 1000.);
    System.out.println("OK");

  }

}
