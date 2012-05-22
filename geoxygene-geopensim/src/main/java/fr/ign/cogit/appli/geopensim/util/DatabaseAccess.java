/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut géographique National
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
 *******************************************************************************/

package fr.ign.cogit.appli.geopensim.util;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;

/**
 * Cette classe contient des Méthodes qui permettent de manipuler des tables.
 * @author Ana-Maria Raimond
 */
public class DatabaseAccess {
	static Logger logger = Logger.getLogger(DatabaseAccess.class.getName());
	private static final long serialVersionUID = 1L;
	private Geodatabase data;
	private String nomTable;
	private final static String ORACLE_COLUMN_QUERY = "SELECT TABLE_NAME FROM USER_SDO_GEOM_METADATA";
	private final static String POSTGIS_COLUMN_QUERY = "SELECT F_TABLE_NAME FROM GEOMETRY_COLUMNS";

	private boolean unique;  // veut-on des identifiants uniques sur toute la base ?
	private int maxID = 0;
	public DatabaseAccess (Geodatabase Data, String TableName, boolean Unique) {
		data = Data;
		nomTable = TableName;
		unique = Unique;
	}
	public int recupereValeurDate(String tableName) {
		int valeurColonne = 0;
		try {
			Connection conn = data.getConnection();
			conn.commit();
			String query = "SELECT DATE_SOURCE_SAISIE FROM " + tableName;
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while (rs.next()) {
				valeurColonne = ((Number) rs.getObject(1)).intValue();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return valeurColonne;
	}
	public int recupereValeurCogitID(String tableName) {
		int valeurColonne = 0;
		try {
			Connection conn = data.getConnection();
			conn.commit();
			String query = "SELECT cogitid FROM " + tableName;
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while (rs.next()) {
				valeurColonne = ((Number)rs.getObject(1)).intValue();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return valeurColonne;
	}
	//remplissage de la colonne Date avec la date saisie par l'utilisateur
	public void remplissageColonneDate(int valeurDate) {
		dropColumnDate();
		addColumnDate();
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			stm.executeUpdate(" update "+nomTable+" set DATE_SOURCE_SAISIE=" + valeurDate);
			logger.info(nomTable + " : la valeur de la date saisie (" + valeurDate + " ) a été enregistrée");
			stm.close();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void remplissageColonneIDGeo(int valeurDate) {
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			stm.executeUpdate(" update " + nomTable + " set id_geo=" + valeurDate);
			stm.close();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//supprime la colonne Date, si elle existe déjà
	void dropColumnDate() {
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			try {
				String query = "ALTER TABLE " + nomTable + " DROP COLUMN DATE_SOURCE_SAISIE";
				stm.executeUpdate(query);
				logger.info(nomTable+" : colonne DATE_SOURCE_SAISIE effacee");
			} catch (Exception ee) { // pas de colonne Date !!
				conn.commit();
			}
			stm.close();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//ajoute la colonne Date 
	void addColumnDate () {
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			String query = "ALTER TABLE " + nomTable + " ADD DATE_SOURCE_SAISIE INTEGER";
			stm.executeUpdate(query);
			logger.info(nomTable + " : colonne DATE_SOURCE_SAISIE creee");
			stm.close();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//supprime la colonne ID_GEO si elle existe déjà
	void dropColonneIdGEO () {
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			try {
				String query = "ALTER TABLE " + nomTable + " DROP COLUMN ID_GEO";
				stm.executeUpdate(query);
				logger.info(nomTable + " : colonne IDGEO effacee");
			} catch (Exception ee) {
				conn.commit();
			}
			stm.close();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ajoute la colonne "ID_GEO" et appelle la methode genereID
	void addColonneIDGEO () {
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			String query = "ALTER TABLE " + nomTable + " ADD ID_GEO INTEGER";
			stm.executeUpdate(query);
			logger.info(nomTable + " : colonne ID_Geo creee");
			stm.close();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//génération des id_Geo
	public void generationIDGEO() {
		dropColonneIdGEO();
		addColonneIDGEO();
		if (unique) {
			if (data.getDBMS() == Geodatabase.ORACLE) {
				maxIDGeo(ORACLE_COLUMN_QUERY);
			} else {
				if (data.getDBMS() == Geodatabase.POSTGIS) {
					maxIDGeo(POSTGIS_COLUMN_QUERY);
				}
			}
		}
		if (data.getDBMS() == Geodatabase.ORACLE) {
			genereIDGeoPostgres();
		} else {
			if (data.getDBMS() == Geodatabase.POSTGIS) {
				genereIDGeoPostgres();
			}
		}
	}
	//génération des id_Geo
	public void generationIDGEOApresApp() {
		if (unique) {
			if (data.getDBMS() == Geodatabase.ORACLE) {
				maxIDGeo(ORACLE_COLUMN_QUERY);
			} else {
				if (data.getDBMS() == Geodatabase.POSTGIS) {
					maxIDGeo(POSTGIS_COLUMN_QUERY);
				}
			}
		}
		if (data.getDBMS() == Geodatabase.ORACLE) {
			genereIDGeoPostgresApresApp();
		} else {
			if (data.getDBMS() == Geodatabase.POSTGIS) {
				genereIDGeoPostgresApresApp();
			}
		}
	}
	//génération des id_Geo pour une Sélection d'objets
	public int generationIDGEOSelectionObjets(int cogitID) {
		if (unique) {
			if (data.getDBMS() == Geodatabase.ORACLE) {
				maxIDGeo(ORACLE_COLUMN_QUERY);
			} else {
				if (data.getDBMS() == Geodatabase.POSTGIS) {
					maxIDGeo(POSTGIS_COLUMN_QUERY);
				}
			}
		}
		if (data.getDBMS() == Geodatabase.ORACLE) {
			return genereIDGeoPostGIS(cogitID);
		} else {
			if (data.getDBMS() == Geodatabase.POSTGIS) {
				return genereIDGeoPostGIS(cogitID);
			}
		}
		return -1;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	// recherche du ID_GEO maximum parmi les tables géographiques (variable globale maxID)
	public void maxIDGeo(String query) {
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			List<String> listOfTables = new ArrayList<String>();
			while (rs.next()) {
				listOfTables.add(rs.getString(1));
			}
			Iterator<String> it = listOfTables.iterator();
			while (it.hasNext()) {
				String aTableName = it.next();
				try {
					query = "SELECT MAX(ID_GEO) FROM " + aTableName;
					rs= stm.executeQuery(query);
					int max = 0;
					while (rs.next()) {
						max = ((Number) rs.getObject(1)).intValue();
					}
					if (max > maxID) {
						maxID = max;
					}
				} catch (Exception ee) {    // pas de colonne idGeo
					conn.commit();
				}
			}
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	// genere les identifiants dans la colonne id_geo
	void genereIDGeoPostgres() {
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			String query = "SELECT COUNT(*) FROM " + nomTable;
			ResultSet rs = stm.executeQuery(query);
			while (rs.next()) {
				int nbCount = ((Number)rs.getObject(1)).intValue();
				logger.info(nbCount + " objets dans la table " + nomTable + " ... generation des identifiants ...");
			}
			// création d'une séquence
			try {
				String update = "create SEQUENCE seq_genere_id_geo";
				stm.executeUpdate(update);
			} catch (Exception ee) {
				// La séquence existe déjà !
				conn.commit();
			}
			conn.commit();
			// Si le maxID vaut 0 (il n'y a pas encore d'identifiant dans la base), on le force à 1
			if (maxID == 0) {
				maxID = 1;
			}
			// Affectation du maxID à la séquence
			// On a pas besoin de l'affecter à maxID+1 puisque l'on utilise toujours nextval pour affecter les identifiants
			query = "SELECT setval ('seq_genere_id_geo', " + maxID + ")";
			rs = stm.executeQuery(query);
			while (rs.next()) { }
			conn.commit();
			// Mise à jour de la table à l'aide de la sequence
			String update = "update " + nomTable + " set id_geo = nextval('seq_genere_id_geo')";
			stm.executeUpdate(update);
			conn.commit();
			// on enleve si ancienne cle primaire
			// Arnaud 28 oct : modif
			query = "select con.conname, con.contype from pg_constraint con, pg_class cl";
			query = query + " where con.conrelid = cl.oid";
			query = query + " and cl.relname='" + nomTable + "'";
			rs = stm.executeQuery(query);
			@SuppressWarnings("unused")
			String conName = "";
			while (rs.next()) {
				String conType = rs.getString(2);
				if (conType.compareToIgnoreCase("p") == 0) {
					conName = rs.getString(1);
				}
			}
			// fin
			stm.close();
			conn.commit();
		} catch (Exception e) {
			logger.info(nomTable);
			e.printStackTrace();
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	// genere les identifiants dans la colonne id_geo
	void genereIDGeoPostgresApresApp() {
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			String query = "SELECT COUNT(*) FROM " + nomTable;
			ResultSet rs = stm.executeQuery(query);
			while (rs.next()) {
				int nbCount = ((Number)rs.getObject(1)).intValue();
				logger.info(nbCount+" objets dans la table " + nomTable + " ... generation des identifiants ...");
			}
			// création d'une séquence
			try {
				String update = "create SEQUENCE seq_genere_id_geo";
				stm.executeUpdate(update);
			} catch (Exception ee) {
				// La séquence existe déjà !
				conn.commit();
			}
			conn.commit();
			// Si le maxID vaut 0 (il n'y a pas encore d'identifiant dans la base), on le force à 1
			if (maxID == 0) { maxID = 1; }
			// Affectation du maxID à la séquence
			// On a pas besoin de l'affecter à maxID+1 puisque l'on utilise toujours nextval pour affecter les identifiants
			query = "SELECT setval ('seq_genere_id_geo', " + maxID + ")";
			rs = stm.executeQuery(query);
			while (rs.next()) { }
			conn.commit();
			// Mise à jour de la table à l'aide de la sequence
			String update = "update " + nomTable + " set id_geo = nextval('seq_genere_id_geo')" + "WHERE id_geo is " + "NULL";
			stm.executeUpdate(update);
			conn.commit();
			// on enleve si ancienne cle primaire
			// Arnaud 28 oct : modif
			query = "select con.conname, con.contype from pg_constraint con, pg_class cl";
			query = query + " where con.conrelid = cl.oid";
			query = query + " and cl.relname='" + nomTable + "'";
			rs = stm.executeQuery(query);
			@SuppressWarnings("unused")
			String conName = "";
			while (rs.next()) {
				String conType = rs.getString(2);
				if (conType.compareToIgnoreCase("p") == 0) {
					conName = rs.getString(1);
				}
			}
			// fin
			stm.close();
			conn.commit();
		} catch (Exception e) {
			logger.info(nomTable);
			e.printStackTrace();
		}
	}

	void remplissageColonneIDGeo(int idGeo, int cogitID){
		logger.info("Affecting id: " + idGeo + " to object with cogitid: " + cogitID + " in table " + this.nomTable);
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			//ResultSet resultat =stm.executeQuery("SELECT * FROM "+nomTable);
			stm.executeUpdate(" update " + nomTable + " set ID_GEO=" + idGeo + "WHERE cogitid =" + cogitID);
			conn.commit();
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	// genere les identifiants id_geo dans la colonne id_geo pour les n objets fusionés
	int genereIDGeoPostGIS(int cogitID) {
		try {
			Connection conn = data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			String query;
			ResultSet rs;
			// création d'une séquence
			try {
				String update = "create SEQUENCE seq_genere_id_geo";
				stm.executeUpdate(update);
			} catch (Exception ee) {
				// La séquence existe déjà !
				conn.commit();
			}
			conn.commit();
			// Si le maxID vaut 0 (il n'y a pas encore d'identifiant dans la base), on le force à 1
			if (maxID == 0) { maxID = 1; }
			// Affectation du maxID à la séquence
			// On a pas besoin de l'affecter à maxID+1 puisque l'on utilise toujours nextval pour affecter les identifiants
			query = "SELECT setval ('seq_genere_id_geo', " + maxID+")";
			rs = stm.executeQuery(query);
			while (rs.next()) { }
			conn.commit();
			// Mise à jour de la table à l'aide de la sequence
			String update = "update " + nomTable + " set id_geo = nextval('seq_genere_id_geo')" + "WHERE cogitid =" + cogitID;
			stm.executeUpdate(update);
			conn.commit();
			// on enleve si ancienne cle primaire
			// Arnaud 28 oct : modif
			query = "select con.conname, con.contype from pg_constraint con, pg_class cl";
			query = query + " where con.conrelid = cl.oid";
			query = query + " and cl.relname='"+nomTable+"'";
			rs = stm.executeQuery(query);
			@SuppressWarnings("unused")
			String conName = "";
			while (rs.next()) {
				String conType = rs.getString(2);
				if (conType.compareToIgnoreCase("p") == 0) {
					conName = rs.getString(1);
				}
			}
			// fin
			stm.close();
			conn.commit();
		} catch (Exception e) {
			logger.info(nomTable);
			e.printStackTrace();
		}
		String query = "select max(id_geo) from " + nomTable;
		Number nn = null;
		try {
			Connection conn = data.getConnection();
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			while (rs.next()) { nn = (Number) rs.getObject(1); }
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (nn != null) { return nn.intValue(); }
		return -1;
	}
}
