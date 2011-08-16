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

package fr.ign.cogit.geoxygene.util.algo;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.example.Resultat;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Test de toutes les methodes d'une bibliotheque de geometrie algorithmique.
 * Generique : on peut creer une nouvelle classe pour l'appel aux methodes, par
 * extension de util.GeomAlgorithms .
 * 
 * @author Thierry Badard, Arnaud Braun & Christophe Pele
 * @version 1.1
 * 
 */

public class TestLib {

  // private GeomAlgorithms algo; // les algorithmes a tester - generique
  private JtsAlgorithms algo; // typage JTS pour tester des algos specifiques a
                              // JTS

  private double seuil = 50.0; // seuil pour le buffer

  // Alias de Connection a Oracle (dans le fichier de mapping
  // repository_database.xml)
  private String ORACLE_ALIAS = "ORACLE_ALIAS";

  private Geodatabase db; // connection a la base de donnees
  private Class<?> featureClass1, featureClass2; // classes de FT_Feature a
                                                 // charger
  private List<? extends FT_Feature> featureList1, featureList2; // result du
                                                                 // chargement
  private GM_MultiSurface<GM_OrientableSurface> aggr1; // agregat geometrique
                                                       // issu du chargement
  private GM_MultiSurface<GM_OrientableSurface> aggr2; // agregat geometrique
                                                       // issu du chargement
  private IGeometry geom; // ceci sera le resultat geometrique des algos
  private Resultat result; // resultat a rendre persistant

  public TestLib() {

    this.db = GeodatabaseOjbFactory.newInstance(this.ORACLE_ALIAS);

    this.aggr1 = new GM_MultiSurface<GM_OrientableSurface>(); /*
                                                               * eventuellement
                                                               * new
                                                               * GM_MultiCurve
                                                               * ();
                                                               */
    this.aggr2 = new GM_MultiSurface<GM_OrientableSurface>(); /*
                                                               * eventuellement
                                                               * new
                                                               * GM_MultiCurve
                                                               * ();
                                                               */

    /* definir ici la bibliotheque a tester */
    this.algo = new JtsAlgorithms();
    // algos =new OracleAlgorithms(db,0.0000000005);

    /* definir ici les classe d'objets geographiques a charger */
    try {
      // featureClass1 = Class.forName("geoschema.feature.Batiment_surf");
      // featureClass1 = Class.forName("geoschema.feature.Troncon_route_bdc");
      this.featureClass1 = Class
          .forName("geoschema.feature.Topo_bati_extrait5");
      this.featureClass2 = Class
          .forName("geoschema.feature.Topo_bati_extrait5_translate");
    } catch (ClassNotFoundException e) {
      System.out.println("classe geographique non trouvee : "
          + this.featureClass1.getName());
      System.out.println("classe geographique non trouvee : "
          + this.featureClass2.getName());
    }
  }

  public static void main(String args[]) {
    TestLib test = new TestLib();
    test.testAll();
  }

  @SuppressWarnings("unchecked")
  public void testAll() {

    // Debut d'une transaction
    System.out.println("Debut transaction");
    this.db.begin();

    // Recherche du nombre d'objets a traiter
    int n = this.db.countObjects(this.featureClass1);
    System.out.println("Nombre d'objets a traiter classe 1:" + n);
    n = this.db.countObjects(this.featureClass2);
    System.out.println("Nombre d'objets a traiter classe 2:" + n);

    // Chargement de tous les objets
    this.featureList1 = (List<? extends FT_Feature>) this.db
        .loadAll(this.featureClass1);
    this.featureList2 = (List<? extends FT_Feature>) this.db
        .loadAll(this.featureClass2);
    System.out.println("chargement termine");

    // Constitution des agregats
    Iterator<? extends FT_Feature> iterator = this.featureList1.iterator();
    while (iterator.hasNext()) {
      this.aggr1.add((GM_OrientableSurface) iterator.next().getGeom());
    }
    iterator = this.featureList2.iterator();
    while (iterator.hasNext()) {
      this.aggr2.add((GM_OrientableSurface) iterator.next().getGeom());
    }

    // buffer
    System.out.println("buffer ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.geom = this.algo.buffer(this.aggr1, this.seuil);
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.result = new Resultat();
    this.result.setGeom(this.geom);
    this.db.makePersistent(this.result);
    this.db.checkpoint();

    // centroide
    System.out.println("centroide ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.geom = new GM_Point(this.algo.centroid(this.aggr1));
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.result = new Resultat();
    this.result.setGeom(this.geom);
    this.db.makePersistent(this.result);
    this.db.checkpoint();

    // enveloppe convexe
    System.out.println("enveloppe convexe ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.geom = this.algo.convexHull(this.aggr1);
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.result = new Resultat();
    this.result.setGeom(this.geom);
    this.db.makePersistent(this.result);
    this.db.checkpoint();

    // difference
    System.out.println("difference ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.geom = this.algo.difference(this.aggr1, this.aggr2);
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.result = new Resultat();
    this.result.setGeom(this.geom);
    this.db.makePersistent(this.result);
    this.db.checkpoint();

    // intersection
    System.out.println("intersection ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.geom = this.algo.intersection(this.aggr1, this.aggr2);
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.result = new Resultat();
    this.result.setGeom(this.geom);
    this.db.makePersistent(this.result);
    this.db.checkpoint();

    // union
    System.out.println("union ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.geom = this.algo.union(this.aggr1, this.aggr2);
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.result = new Resultat();
    this.result.setGeom(this.geom);
    this.db.makePersistent(this.result);
    this.db.checkpoint();

    // difference symetrique
    System.out.println("difference symetrique ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.geom = this.algo.symDifference(this.aggr1, this.aggr2);
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.result = new Resultat();
    this.result.setGeom(this.geom);
    this.db.makePersistent(this.result);
    this.db.checkpoint();

    // predicat contains
    System.out.println(" predicat contains ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.contains(this.aggr1, this.aggr2));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // predicat intersects
    System.out.println(" predicat intersects ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.intersects(this.aggr1, this.aggr2));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // predicat equals
    System.out.println(" predicat equals ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.equals(this.aggr1, this.aggr2));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // distance
    System.out.println(" distance ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.distance(this.aggr1, this.aggr2));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // longueur
    System.out.println(" longueur ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.length(this.aggr1));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // aire
    System.out.println(" aire ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.area(this.aggr1));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // ## ce qui suit est specifique JTS

    // predicat equals exact
    System.out.println(" predicat equals exact ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.equalsExact(this.aggr1, this.aggr2));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // predicat crosses
    System.out.println(" predicat crosses ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.crosses(this.aggr1, this.aggr2));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // predicat disjoint
    System.out.println(" predicat disjoint ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.disjoint(this.aggr1, this.aggr2));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // predicat within
    System.out.println(" predicat within ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.within(this.aggr1, this.aggr2));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // predicat overlaps
    System.out.println(" predicat overlaps ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.overlaps(this.aggr1, this.aggr2));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // predicat touches
    System.out.println(" predicat touches ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.touches(this.aggr1, this.aggr2));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // isValid
    System.out.println(" isValid ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.isValid(this.aggr1));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // isEmpty
    System.out.println(" isEmpty ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.isEmpty(this.aggr1));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // isSimple
    System.out.println(" isSimple ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.isSimple(this.aggr1));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // dimension
    System.out.println(" dimension ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.dimension(this.aggr1));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // numPoints
    System.out.println(" numPoints ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    System.out.println(this.algo.numPoints(this.aggr1));
    System.out.println("\t" + new Date(System.currentTimeMillis()));

    // translate
    System.out.println("translate ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.geom = this.algo.translate(this.aggr1, 50, 50, 0);
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.result = new Resultat();
    this.result.setGeom(this.geom);
    this.db.makePersistent(this.result);
    this.db.checkpoint();

    // boundary
    System.out.println("boundary ...");
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.geom = this.algo.boundary(this.aggr1);
    System.out.println("\t" + new Date(System.currentTimeMillis()));
    this.result = new Resultat();
    this.result.setGeom(this.geom);
    this.db.makePersistent(this.result);
    this.db.checkpoint();

    // Fermeture transaction
    System.out.println("commit");
    this.db.commit();

  }

}
