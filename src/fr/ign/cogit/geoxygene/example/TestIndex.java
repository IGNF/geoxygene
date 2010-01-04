/*
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

package fr.ign.cogit.geoxygene.example;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Exemple et test d'utilisation d'un index spatial (dallage) sur FT_FeatureCollection.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */

@SuppressWarnings({"unchecked","unqualified-field-access"})
public class TestIndex {

	private Geodatabase db;
	private Class<FT_Feature> featureClass;
	private String nomClasse = "geoxygene.geodata.Troncon_route";
	private FT_FeatureCollection< FT_Feature> featureList;
	private FT_FeatureCollection<?> sublist;
	private GM_Envelope emprise;
	private long t1, t2;


	@SuppressWarnings("unchecked")
	private TestIndex() {

		// Classe geographique a charger
		try {
			featureClass = (Class<FT_Feature>) Class.forName(nomClasse);
		} catch (ClassNotFoundException e) {
			System.out.println(nomClasse + "classe geographique non trouvee");
			System.exit(0);
		}

		// Initialisation connexion a la BD
		db = GeodatabaseOjbFactory.newInstance();
		db.begin();

		// Recherche du nombre d'objets a traiter
		int n = db.countObjects(featureClass);
		System.out.println("nombre d'objets a traiter : " + n);

		// Chargement de tous les objets
		System.out.print("chargement ... ");
		t1 = System.currentTimeMillis();
		featureList = db.loadAllFeatures(featureClass);
		t2 = System.currentTimeMillis();
		System.out.println( (t2-t1) / 1000.0 +" sec.");

		//Fermeture transaction
		db.commit();

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
		t1 = System.currentTimeMillis();
		featureList.initSpatialIndex( Tiling.class, false, 15 );
		t2 = System.currentTimeMillis();
		System.out.println( (t2-t1) / 1000.0 +" sec.");

		// emprise de la couche
		//		GM_Envelope empriseIni = db.getMetadata(featureClass).getEnvelope();  // lecture de l'emprise dans le SGBD
		GM_Envelope empriseIni = featureList.envelope(); // calcul direct

		// boucle pour regler la taille de l'emprise par homotheties successives
		for (double h=0.1; h<0.5; h += 0.1) {
			System.out.println("### coefficient d'extension de l'enveloppe "+h);
			emprise = (GM_Envelope) empriseIni.clone();
			emprise.expandBy(h);
			testGM_Envelope();
			testGM_Polygon();
			testOracle();
		}
	}


	// ======= selection index memoire avec une GM_Envelope ========
	private void testGM_Envelope() {
		System.out.print("extraction GM_Envelope ... ");
		t1 = System.currentTimeMillis();
		sublist =featureList.select(emprise);
		t2 = System.currentTimeMillis();
		System.out.print("taille de la selection : "+sublist.size()+"\t");
		System.out.println("temps de calcul : "+ ((t2-t1)/1000.0) +" sec.");
		sublist.clear();
	}


	// ======= selection index memoire avec un GM_Polygon ==========
	private void testGM_Polygon() {
		System.out.print("extraction GM_Polygon  ... ");
		t1 = System.currentTimeMillis();
		sublist =featureList.select(new GM_Polygon(emprise));
		t2 = System.currentTimeMillis();
		System.out.print("taille de la selection : "+sublist.size()+"\t");
		System.out.println("temps de calcul : "+ ((t2-t1)/1000.0) +" sec.");
		sublist.clear();
	}


	// ======= selection avec Oracle =============================
	private void testOracle() {
		System.out.print("extraction oracle ...      ");
		t1 = System.currentTimeMillis();
		sublist =db.loadAllFeatures(featureClass, new GM_Polygon(emprise));
		t2 = System.currentTimeMillis();
		System.out.print("taille de la selection : "+sublist.size()+"\t");
		System.out.println("temps de calcul : "+ ((t2-t1)/1000.0) +" sec.");
	}


	// ======= test des fonctions de mise a jour =================
	private void testMiseAJour() {

		featureList.initSpatialIndex( Tiling.class, true, 15 );
		Tiling<?> dallage = (Tiling<?>) featureList.getSpatialIndex();
		GM_Envelope env;

		// avant mise a jour
		FT_Feature feature =  featureList.get(10);
		System.out.println("ancienne taille : "+featureList.size());
		env = dallage.getDallage(feature) [0];
		System.out.println("dalle contenant le feature : "+env.hashCode());
		System.out.println("nombre d'objets dans cette dalle : "+featureList.select(env).size());

		// ajout
		try {
			FT_Feature newFeature = feature.cloneGeom();
			newFeature.setGeom( (newFeature.getGeom().translate(1., 1., 0.)));
			featureList.add(newFeature);
			System.out.println("nouvelle taille : "+featureList.size());
			env = dallage.getDallage(feature) [0];
			System.out.println("dalle contenant le feature : "+env.hashCode());
			System.out.println("nombre d'objets dans cette dalle : "+featureList.select(env).size());

			// suppression
			featureList.remove(newFeature);
			System.out.println("nouvelle taille : "+featureList.size());
			env = dallage.getDallage(feature) [0];
			System.out.println("dalle contenant le feature : "+env.hashCode());
			System.out.println("nombre d'objets dans cette dalle : "+featureList.select(env).size());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}


}
