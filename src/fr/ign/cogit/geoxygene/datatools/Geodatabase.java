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

package fr.ign.cogit.geoxygene.datatools;


import java.io.File;
import java.sql.Connection;
import java.util.List;

import org.odmg.OQLQuery;

import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;


/// A revoir plus tard : compatiblite avec la norme JDO...

/**
 * Represente une connection a une base de donnees geographique.
 * Gere la manipulation des donnees via un mappeur objet-relationnel (Castor, OJB).
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */



public interface Geodatabase  {


	public static final int ORACLE = 1;
	public static final int POSTGIS = 2;

	/////////////////////////////////////////////////////////////////////////////////////////
	///// gestion des transactions //////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** Ouvre une transaction. */
	public void begin() ;

	/** Commit la transaction sans la fermer. */
	public void checkpoint() ;

	/** Commite et ferme la transaction. */
	public void commit() ;

	/** Annule et ferme la transaction. */
	public void abort() ;

	/** Renvoie true si la transaction est ouverte. */
	public boolean isOpen() ;

	/** Ferme la connection (libere les ressources). */
	public void close() ;

	/** Vide le cache de la transaction.
     A appeler a l'interieur d'une transaction ouverte. */
	public void clearCache() ;



	/////////////////////////////////////////////////////////////////////////////////////////
	///// gestion de la persistance /////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** Rend persistant un objet.
        A appeler a l'interieur d'une transaction ouverte.*/
	public void makePersistent(Object obj) ;

	/** Detruit un objet persistant.
        A appeler a l'interieur d'une transaction ouverte. */
	public void deletePersistent(Object obj) ;



	/////////////////////////////////////////////////////////////////////////////////////////
	///// chargement d'objets ///////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** Charge l'objet d'identifiant id.
        A utilisser avec precaution, car ne charge pas forcement toutes les relations.
        Passer un Integer pour id, si l'identifiant est un int.
        Renvoie null si l'objet d'identifiant id n'existe pas.
        A appeler a l'interieur d'une transaction ouverte. */
	public <T> T load(Class<T> clazz, Object id) ;

	/** Charge tous les objets persistants de la classe featureClass et les met dans une liste.
        A appeler a l'interieur d'une transaction ouverte. */
	public <T> List<T> loadAll(Class<T> featureClass) ;

	/** Charge tous les objets persistants de la classe featureClass dans la classe featureListClass.
    A appeler a l'interieur d'une transaction ouverte. */
	public <T> T loadAll(Class<?> featureClass, Class<T> featureListClass) ;

	/** Charge tous les FT_Feature de la classe featureClass dans la classe FT_FeatureCollection.
		A appeler a l'interieur d'une transaction ouverte.
		La classe featureClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide. */
	public <T extends FT_Feature> FT_FeatureCollection <T> loadAllFeatures(Class<?> featureClass) ;

	/** Charge tous les FT_Feature de la classe featureClass dans la classe featureListClass.
        A appeler a l'interieur d'une transaction ouverte.
        La classe featureClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide.
        La classe featureListClass doit etre un sous classe de FT_FeatureCollection.*/
	public <T> T loadAllFeatures(Class<?> featureClass, Class<T> featureListClass) ;

	/**
	 * Charge tous les FT_Feature de la classe featureClass avec une certaine valeur pour un paramètre dans la classe featureListClass.
	 * A appeler a l'interieur d'une transaction ouverte.
	 * @param featureClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide.
	 * @param featureListClass doit etre un sous classe de FT_FeatureCollection.
	 * @param param nom du paramètre
	 * @param value valeur du paramètre
	 * @return  tous les FT_Feature de la classe featureClass avec une certaine valeur pour un paramètre dans la classe featureListClass.
	 */
	public <T> T loadAllFeatures(Class<?> featureClass, Class<T> featureListClass, String param, String value) ;

	/** Charge tous les FT_Feature de la classe featureClass intersectant le GM_Object geom, dans la classe FT_FeatureCollection.
		 A appeler a l'interieur d'une transaction ouverte.
		 La classe featureClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide. */
	public <T extends FT_Feature> FT_FeatureCollection<T> loadAllFeatures(Class<T> featureClass, GM_Object geom) ;

	/** Charge tous les FT_Feature de la classe featureClass intersectant le GM_Object geom, dans la classe featureListClass.
         A appeler a l'interieur d'une transaction ouverte.
         La classe featureClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide.
         La classe featureListClass doit etre un sous classe de FT_FeatureCollection.*/
	public <T> T loadAllFeatures(Class<?> featureClass, Class<T> featureListClass, GM_Object geom) ;

	/** Charge tous les FT_Feature de la classe theClass a une distance dist du GM_Object geom, dans la classe FT_FeatureCollection.
		Si geom est la geometrie d'un FT_Feature de theClass, alors ce FT_Feature appartiendra au resultat.
		 A appeler a l'interieur d'une transaction ouverte.
		 La classe featureClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide.*/
	public <T extends FT_Feature> FT_FeatureCollection<T> loadAllFeatures(Class<T> featureClass, GM_Object geom, double dist) ;

	/** Charge tous les FT_Feature de la classe featureClass a une distance dist du GM_Object geom, dans la classe featureListClass.
        Si geom est la geometrie d'un FT_Feature de theClass, alors ce FT_Feature appartiendra au resultat.
         A appeler a l'interieur d'une transaction ouverte.
         La classe featureClass doit etre une sous-classe de FT_Feature, sinon renvoie une liste vide.
        La classe featureListClass doit etre un sous classe de FT_FeatureCollection.*/
	public <T> T loadAllFeatures(Class<?> featureClass, Class<T> featureListClass, GM_Object geom, double dist) ;

	/**
	 * Charge tous les features correspondant à une même classe de schéma
	 * conceptuel (il n'est pas nécessaire de connaêtre la classe Java
	 * d'implémentation). Si on est dans le contexte d'un MdDataSet, les
	 * MdFeatures sont en même temps affect�s à une MdPopulation propre à leur
	 * classe de schéma conceptuel.
	 */
	public <T extends FT_Feature> FT_FeatureCollection<T> loadAllFeatures(FeatureType featureType);

	/** Execute la requete OQL query, la lie avec le parametre param, et met le resultat dans une liste.
        A appeler a l'interieur d'une transaction ouverte.
        On peut passer null pour param, si on ne souhaite lier la requete a aucune variable. */
	public  List<?> loadOQL(String query, Object param) ;



	/////////////////////////////////////////////////////////////////////////////////////////
	///// OQL ///////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** Cree une une nouvelle requete OQL (ODMG). */
	public OQLQuery newOQLQuery() ;



	/////////////////////////////////////////////////////////////////////////////////////////
	///// Gestion de l'information spatiale /////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** 
	 * Renvoie la liste des metadonnees.
	 * @return la liste des metadonnees.
	 */
	public List<Metadata> getMetadata();

	/** 
	 * Renvoie les metadonnees de la classe theClass.
	 * @param theClass doit etre une classe definie dans le mapping. 
	 * @return les metadonnees de la classe theClass.
	 */
	public Metadata getMetadata(Class<?> theClass);

	/** 
	 * Renvoie les metadonnees de la classe mappee avec la table theTable.
	 * Si theTable est mappee avec plusieurs classes, en renvoie une.
	 * @param theTable doit etre une table definie dans le mapping.
	 * @return les metadonnees de la classe mappee avec la table theTable.
	 */
	public Metadata getMetadata(String theTable);

	/**
	 * Calcule DANS LE SGBD l'emprise la table mappee avec la classe (utile pour Oracle ...).
	 * @param clazz Cette classe doit heriter de FT_Feature, la table doit contenir une geometrie.
	 */
	public void mbr(Class<?> clazz);

	/**
	 * Calcule DANS LE SGBD un index spatial sur la table mappee avec la classe (R-Tree).
	 * @param clazz Cette classe doit heriter de FT_Feature, la table doit contenir une geometrie.
	 */
	public void spatialIndex(Class<?> clazz);

	/////////////////////////////////////////////////////////////////////////////////////////
	///// SQL ///////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** 
	 * Renvoie la connection JDBC sous-jacente.
	 * @return la connection JDBC sous-jacente.
	 */
	public Connection getConnection();

	/**
	 * Renvoie le type de SGBD.
	 * @return le type de SGBD.
	 */
	public int getDBMS();

	/**
	 * Execute une commande SQL.
	 * Cette commande ne doit pas renvoyer de resultat : INSERT, UPDATE, DELETE, mais pas SELECT.
	 * Utile uniquement pour debugger : on ne doit pas utiliser de SQL direct avec GeOxygene !
	 * @param query commande SQL
	 */
	public void exeSQL(String query) ;
	
	/** 
	 * Execute les commandes SQL contenues dans un fichier.
	 * @param fileName fichier contenant des commandes SQL
	 */
	public void exeSQLFile(String fileName) ;


	/**
	 * Execute une requete et met les resultats dans une liste de tableau d'objets.
	 * Les tableaux ont la taille du nombre d'objets demandes dans le SELECT.
	 * Exemple d'utilisation du resultat :
	 * <tt> List edges = db.exeSQLQuery("SELECT edgeID FROM tableName WHERE ..."). </tt>
	 * Pour recuperer le premier resultat :
	 * <tt> edgeId = ( (BigDecimal) ((Object[]) (edges.get(0)) )[0] ).intValue(); </tt>
	 * Utile uniquement pour debugger : on ne doit pas utiliser de SQL direct avec GeOxygene !
	 * @param query requete SQL à ex�cuter
	 * @return liste contenant le resultat de la requ�te SQL
	 */
	public List<?> exeSQLQuery(String query) ;



	/////////////////////////////////////////////////////////////////////////////////////////
	///// divers ////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////
	/** Renvoie le nombre d'objets persistants de la classe theClass.
        A appeler a l'interieur d'une transaction ouverte. */
	public int countObjects(Class<?> theClass)  ;

	/** Renvoie l'identifiant maximum de la classe theClass.
        ATTENTION : La classe passee en parametre doit avoir un champ "id" de type int (marche pour les FT_Feature).
        A appeler a l'interieur d'une transaction ouverte. */
	public int maxId(Class<?> theClass)  ;

	/** Renvoie l'identifiant minimum de la classe theClass.
       ATTENTION : La classe passee en parametre doit avoir un champ "id" de type int (marche pour les FT_Feature).
       A appeler a l'interieur d'une transaction ouverte. */
	public int minId(Class<?> theClass)  ;

	/** Recharger un fichier de mapping qui a ete modifie. */
	public void refreshRepository(File newRepository) throws Exception ;


}
