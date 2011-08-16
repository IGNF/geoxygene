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
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.viewer.ObjectViewer;

/**
 * Simple exemple de code pour l'utilisation de GeOxygene. On suppose qu'il
 * existe une classe persistante "donnees.defaut.Troncon_route" (sinon changer
 * le nom de la classe dans le code). Si la classe a charger contient beaucoup
 * d'objet, lancer le programme avec l'option '-Xmx512M' (java -Xmx512M
 * exemple.FirstExample) .
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.2
 * 
 */

public class FirstExample {

  // connection a la base de donnees
  private Geodatabase db;

  // classe de troncons
  private Class<?> tronconClasse;
  // nom de la classe a charger
  private String nomClasse = "geoxygene.geodata.TronconRoute";

  // constructeur
  public FirstExample() {

    // initialisation de la connection et lecture des fichiers de mapping
    System.out.println("Debut initialisation");
    this.db = GeodatabaseOjbFactory.newInstance();
    System.out.println("Initialisation OK");
    try {
      this.tronconClasse = Class.forName(this.nomClasse);
    } catch (ClassNotFoundException e) {
      System.out.println(this.nomClasse + " : non trouvee");
      System.exit(0);
    }
  }

  public static void main(String args[]) {
    FirstExample test = new FirstExample();

    test.exemple1();
    test.exemple2();

  }

  // exemple1 : traitement sur un objet.
  public void exemple1() {

    // Debut d'une transaction
    System.out.println("Debut transaction");
    this.db.begin();

    // Recherche du plus grand identifiant
    Integer gid = new Integer(this.db.maxId(this.tronconClasse));

    // Chargement d'un objet par son identifiant
    FT_Feature troncon = (FT_Feature) this.db.load(this.tronconClasse, gid);
    System.out.println("identifiant de l'objet charge : " + troncon.getId());

    // geometrie
    GM_LineString polyligne = (GM_LineString) troncon.getGeom();
    System.out.println(polyligne);

    // Creation d'un objet Resultat auquel on affecte la geometrie du
    // Troncon_route
    Resultat resultat = new Resultat();
    this.db.makePersistent(resultat);
    resultat.setGeom(polyligne);

    // Creation d'un objet Resultat auquel on affecte la geometrie d'un buffer
    // autour du Troncon
    int rayon = 20;
    GM_Polygon polygone = (GM_Polygon) polyligne.buffer(rayon);
    Resultat buffer = new Resultat();
    this.db.makePersistent(buffer);
    buffer.setGeom(polygone);

    // Fermeture transaction et commit ( = validation des ecritures dans la
    // base)
    System.out.println("commit");
    this.db.commit();

    // Visualisation dans le viewer
    ObjectViewer viewer = new ObjectViewer(this.db);
    FT_FeatureCollection<FT_Feature> iniColl = new FT_FeatureCollection<FT_Feature>();
    iniColl.add(troncon);
    FT_FeatureCollection<FT_Feature> bufferColl = new FT_FeatureCollection<FT_Feature>();
    bufferColl.add(buffer);
    viewer.addFeatureCollection(iniColl, "Tronçons");
    viewer.addFeatureCollection(bufferColl, "Résultats");

  }

  // exemple2 : traitement sur une couche entiere
  public void exemple2() {

    // Debut d'une transaction
    System.out.println("Debut transaction");
    this.db.begin();

    // Recherche du nombre d'objets a traiter
    System.out.println("nb: " + this.db.countObjects(this.tronconClasse));

    // Chargement de tous les objets
    IFeatureCollection<? extends IFeature> tronconList = this.db
        .loadAllFeatures(this.tronconClasse);
    System.out.println("chargement termine");

    // Création d'une nouvelle collection pour stocker les résultats
    FT_FeatureCollection<FT_Feature> allResults = new FT_FeatureCollection<FT_Feature>();

    // seuil filtre Douglas-Peucker
    double seuil = 80.0;
    int compteur = 0;

    // Traitement des objets
    for (IFeature troncon : tronconList) {
      GM_LineString polyligne1 = (GM_LineString) troncon.getGeom();
      GM_LineString polyligne2 = (GM_LineString) Filtering.DouglasPeucker(
          polyligne1, seuil);
      Resultat resultat = new Resultat();
      resultat.setGeom(polyligne2);
      this.db.makePersistent(resultat);
      compteur++;
      if (compteur % 100 == 0) {
        System.out.println(compteur);
      }
      allResults.add(resultat);
    }

    // Fermeture transaction et commit ( = validation des ecritures dans la
    // base)
    System.out.println("commit");
    this.db.commit();

    // Visualisation dans le viewer
    ObjectViewer viewer = new ObjectViewer(this.db);
    viewer.addFeatureCollection(tronconList, "Tronçons");
    viewer.addFeatureCollection(allResults, "Résultats");
  }

}
