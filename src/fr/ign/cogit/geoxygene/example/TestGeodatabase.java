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

import java.math.BigDecimal;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.Metadata;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;


/**
 * Exemple et test d'utilisation de l'interface Geodatabase.
 * On suppose qu'il existe une classe persistante "donnees.defaut.Troncon_route".
 * (sinon changer le nom de la classe dans le code).
 * Si la classe a charger contient beaucoup d'objet, lancer le programme avec l'option '-Xmx512M'
 * (java -Xmx512M exemple.FirstExample) .
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */


public class TestGeodatabase {

	//la bd
	private Geodatabase db;

	//classe de troncons
	private Class<? extends FT_Feature> tronconClass;
	//nom de la classe de troncons
	private String nomClasse = "donnees.defaut.Bdc38_troncon_route";


	@SuppressWarnings("unchecked")
	public TestGeodatabase() {

		//iniatilsation de la Geodatabase
		db=GeodatabaseOjbFactory.newInstance();

		try {
			tronconClass = (Class<? extends FT_Feature>) Class.forName(nomClasse);
		} catch (ClassNotFoundException e) {
			System.out.println(nomClasse+" : non trouvee");
			System.exit(0);
		}

	}


	public static void main(String args[]) {
		TestGeodatabase test = new TestGeodatabase();
		test.testJDO();
		test.testMetadata();
		test.testSpatial();
		test.testSQL();
	}



	//Test des methodes JDO (ou ODMG)
	public void testJDO()  {

		// identifiant
		Integer gid = new Integer ( db.maxId(tronconClass) );
		// distance buffer
		int seuil = 100;

		// ouvre une transaction
		db.begin();
		System.out.println("debut transaction");

		// teste l'ouverture de la transaction
		System.out.println("transaction ouverte ? : "+db.isOpen());

		// charge un objet par son identifiant
		FT_Feature feature = db.load(tronconClass, new Integer(gid) );
		if (feature != null) System.out.println("objet charge : "+feature.getClass()+" - id : "+feature.getId());

		// chargement de tous les FT_Feature d'une classe
		FT_FeatureCollection<?> featList = db.loadAllFeatures(tronconClass) ;
		System.out.println("nombre de feature charges : "+featList.size());

		feature = featList.get(0);
		GM_Object geom = feature.getGeom();
		System.out.println(geom);
		Resultat result = new Resultat();
		result.setGeom(geom);
		db.makePersistent(result);
		System.out.println("objet resultat cree - id : "+result.getId());

		// commit intermediaire
		db.checkpoint();
		System.out.println("checkpoint");

		// re-chargement de tous les FT_Feature d'une classe
		// ils sont en fait deja charges, donc c'est instantane !
		featList = db.loadAllFeatures(tronconClass) ;
		System.out.println("nombre de feature charges : "+featList.size());

		// chargement de tous les troncons intersectant la geometrie "geom"
		featList = db.loadAllFeatures(tronconClass, geom) ;
		System.out.println("nombre de feature charges : "+featList.size());

		// chargement de tous les troncons dans un buffer autour de "geom"
		featList = db.loadAllFeatures(tronconClass, geom, seuil) ;
		System.out.println("nombre de feature charges : "+featList.size());

		// chargement d'objets par une requete OQL simple
		String query = "select x from "+Resultat.class.getName()+" where int1 = $0";
		System.out.println(query);
		List<?> list = db.loadOQL(query, new Integer(0) );
		System.out.println("nombre d'objets trouves par la requete : "+list.size());

		// destruction d'un objet
		db.deletePersistent(result);
		System.out.println("objet result detruit - id : "+result.getId());

		// Commite et ferme la transaction. */
		db.commit() ;
		System.out.println("fin transaction");

		// teste l'ouverture de la transaction
		System.out.println("transaction ouverte ? : "+db.isOpen());

	}



	//test des metadonnees
	public void testMetadata() {

		// liste des metadonnees issues du mapping
		List<Metadata> metadataList = db.getMetadata();
		for(Metadata metadata:metadataList) {
			if (metadata.getClassName() != null) System.out.println(metadata.getClassName());
			if (metadata.getJavaClass() != null) System.out.println(metadata.getJavaClass());
			if (metadata.getTableName() != null) System.out.println(metadata.getTableName());
			if (metadata.getGeomColumnName() != null) System.out.println(metadata.getGeomColumnName());
			if (metadata.getIdColumnName() != null) System.out.println(metadata.getIdColumnName());
			if (metadata.getSRID() != 0) System.out.println(metadata.getSRID());
			if (metadata.getEnvelope() != null) System.out.println(metadata.getEnvelope());
			if (metadata.getTolerance() != null) System.out.println(metadata.getTolerance(0));
			if (metadata.getDimension() != 0) System.out.println(metadata.getDimension());
			System.out.println("");
		}

		// acces direct aux metadonnees
		String tableName = db.getMetadata(tronconClass).getTableName();
		System.out.println(tableName);
		System.out.println("");
		System.out.println(db.getMetadata(tableName).getJavaClass().getName());
		System.out.println("");
	}


	//Test des fonctionnalites spatiales
	public void testSpatial() {

		// affectation d'une emprise
		db.mbr(tronconClass);
		System.out.println("emprise ok");

		// calcul d'un index spatial
		db.spatialIndex(tronconClass);
		System.out.println("index spatial ok");
	}



	//Teste les fonctionnalites SQL
	public void testSQL() {

		// execution directe d'une requete SQL
		List<?> list = db.exeSQLQuery("SELECT COGITID FROM RESULTAT");
		for (Object obj : list) {
			int featureId = ( (BigDecimal) ((Object[]) obj)[0] ).intValue();
			System.out.println("feature : "+featureId);
		}
		System.out.println("Requete directe SQL ok");

		// execution d'une commande SQL
		db.exeSQL("DELETE FROM RESULTAT");
		System.out.println("delete ok");

		// nombre d'objets d'une classe
		System.out.println("nombre d'objets : "+db.countObjects(tronconClass) );

		// identifiant minimum
		System.out.println("min id : "+db.minId(tronconClass) );

		// identifiant maximum
		System.out.println("max id : "+db.maxId(tronconClass) );

	}

}
