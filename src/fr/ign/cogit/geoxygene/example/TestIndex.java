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

package fr.ign.cogit.geoxygene.example;

import java.util.Collection;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Exemple et test d'utilisation d'un index spatial (dallage) sur
 * FT_FeatureCollection.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */
@SuppressWarnings( { "unchecked", "unqualified-field-access" })
public class TestIndex {

  private Geodatabase db;
  private Class<FT_Feature> featureClass;
  private String nomClasse = "geoxygene.geodata.Troncon_route"; //$NON-NLS-1$
  private FT_FeatureCollection<FT_Feature> featureList;
  private Collection<? extends FT_Feature> sublist;
  private GM_Envelope emprise;
  private long t1, t2;

  private TestIndex() {

    // Classe geographique a charger
    try {
      this.featureClass = (Class<FT_Feature>) Class.forName(this.nomClasse);
    } catch (ClassNotFoundException e) {
      System.out.println(this.nomClasse + "classe geographique non trouvee");
      System.exit(0);
    }

    // Initialisation connexion a la BD
    this.db = GeodatabaseOjbFactory.newInstance();
    this.db.begin();

    // Recherche du nombre d'objets a traiter
    int n = this.db.countObjects(this.featureClass);
    System.out.println("nombre d'objets a traiter : " + n);

    // Chargement de tous les objets
    System.out.print("chargement ... ");
    this.t1 = System.currentTimeMillis();
    this.featureList = this.db.loadAllFeatures(this.featureClass);
    this.t2 = System.currentTimeMillis();
    System.out.println((this.t2 - this.t1) / 1000.0 + " sec.");

    // Fermeture transaction
    this.db.commit();

  }

  public static void main(String args[]) {
    TestIndex test = new TestIndex();
    test.testPerf();
    test.testMiseAJour();
  }

  // test des performances compare a Oracle
  private void testPerf() {

    // Calcul de l'index
    System.out.print("calcul index ... ");
    this.t1 = System.currentTimeMillis();
    this.featureList.initSpatialIndex(Tiling.class, false, 15);
    this.t2 = System.currentTimeMillis();
    System.out.println((this.t2 - this.t1) / 1000.0 + " sec.");

    // emprise de la couche
    // GM_Envelope empriseIni = db.getMetadata(featureClass).getEnvelope(); //
    // lecture de l'emprise dans le SGBD
    GM_Envelope empriseIni = this.featureList.envelope(); // calcul direct

    // boucle pour regler la taille de l'emprise par homotheties successives
    for (double h = 0.1; h < 0.5; h += 0.1) {
      System.out.println("### coefficient d'extension de l'enveloppe " + h);
      this.emprise = (GM_Envelope) empriseIni.clone();
      this.emprise.expandBy(h);
      this.testGM_Envelope();
      this.testGM_Polygon();
      this.testOracle();
    }
  }

  // ======= selection index memoire avec une GM_Envelope ========
  private void testGM_Envelope() {
    System.out.print("extraction GM_Envelope ... ");
    this.t1 = System.currentTimeMillis();
    this.sublist = this.featureList.select(this.emprise);
    this.t2 = System.currentTimeMillis();
    System.out.print("taille de la selection : " + this.sublist.size() + "\t");
    System.out.println("temps de calcul : " + ((this.t2 - this.t1) / 1000.0)
        + " sec.");
    this.sublist.clear();
  }

  // ======= selection index memoire avec un GM_Polygon ==========
  private void testGM_Polygon() {
    System.out.print("extraction GM_Polygon  ... ");
    this.t1 = System.currentTimeMillis();
    this.sublist = this.featureList.select(new GM_Polygon(this.emprise));
    this.t2 = System.currentTimeMillis();
    System.out.print("taille de la selection : " + this.sublist.size() + "\t");
    System.out.println("temps de calcul : " + ((this.t2 - this.t1) / 1000.0)
        + " sec.");
    this.sublist.clear();
  }

  // ======= selection avec Oracle =============================
  private void testOracle() {
    System.out.print("extraction oracle ...      ");
    this.t1 = System.currentTimeMillis();
    this.sublist = this.db.loadAllFeatures(this.featureClass, new GM_Polygon(
        this.emprise));
    this.t2 = System.currentTimeMillis();
    System.out.print("taille de la selection : " + this.sublist.size() + "\t");
    System.out.println("temps de calcul : " + ((this.t2 - this.t1) / 1000.0)
        + " sec.");
  }

  // ======= test des fonctions de mise a jour =================
  private void testMiseAJour() {

    this.featureList.initSpatialIndex(Tiling.class, true, 15);
    Tiling<?> dallage = (Tiling<?>) this.featureList.getSpatialIndex();
    GM_Envelope env;

    // avant mise a jour
    FT_Feature feature = this.featureList.get(10);
    System.out.println("ancienne taille : " + this.featureList.size());
    env = dallage.getDallage(feature)[0];
    System.out.println("dalle contenant le feature : " + env.hashCode());
    System.out.println("nombre d'objets dans cette dalle : "
        + this.featureList.select(env).size());

    // ajout
    try {
      FT_Feature newFeature = feature.cloneGeom();
      newFeature.setGeom((newFeature.getGeom().translate(1., 1., 0.)));
      this.featureList.add(newFeature);
      System.out.println("nouvelle taille : " + this.featureList.size());
      env = dallage.getDallage(feature)[0];
      System.out.println("dalle contenant le feature : " + env.hashCode());
      System.out.println("nombre d'objets dans cette dalle : "
          + this.featureList.select(env).size());

      // suppression
      this.featureList.remove(newFeature);
      System.out.println("nouvelle taille : " + this.featureList.size());
      env = dallage.getDallage(feature)[0];
      System.out.println("dalle contenant le feature : " + env.hashCode());
      System.out.println("nombre d'objets dans cette dalle : "
          + this.featureList.select(env).size());
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
  }
}
