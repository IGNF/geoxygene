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

package fr.ign.cogit.geoxygene.datatools.postgis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.Metadata;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Encapsulation d'appels a Postgis.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.3
 * 21/07/2008 : modification de la définition de la requete dans la methode executeFeatureList (Nathalie, Seb)
 * 21/07/2008 : modification de initGeomMetadata : r�cup�ration du SRID dans postgis (Julien Perret)
 */
public class PostgisSpatialQuery {


	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/// calcul d'index spatial ///////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void spatialIndex(Geodatabase data, Class<?> clazz) {
		try {
			Connection conn = data.getConnection();
			Statement stm = conn.createStatement();
			String tableName = data.getMetadata(clazz).getTableName().toLowerCase();
			String columnName = data.getMetadata(clazz).getGeomColumnName().toLowerCase();

			// on est oblige de faire ceci, sinon message d'erreur d'Oracle : nom d'index trop long...
			String indexName;
			if (tableName.length()>24) indexName = tableName.substring(0,24)+"_spidx";
			else indexName = tableName+"_spidx";

			try {
				String query = "CREATE INDEX "+indexName+" ON "+tableName+" USING GIST ("+columnName+" GIST_GEOMETRY_OPS)";
				stm.executeUpdate(query);
				conn.commit();
			} catch (Exception ee) {	// l'index existe
				conn.commit();
				String query = "REINDEX INDEX "+indexName+" FORCE";
				stm.executeUpdate(query);
			}
			stm.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/// intialisation des metadonnees ////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void initGeomMetadata (List<?> metadataList, Connection conn) {
		try {
			int n = metadataList.size();
			Statement stm = conn.createStatement();
			String query = "SELECT F_TABLE_NAME, F_GEOMETRY_COLUMN, SRID FROM GEOMETRY_COLUMNS";
			ResultSet rs = stm.executeQuery(query);
			while (rs.next()) {
				String sqlTableName = rs.getString(1);
				for (int i=0; i<n; i++) {
					String arrayTableName = ((Metadata)metadataList.get(i)).getTableName();
					if (arrayTableName != null)  // ceci car pour les classes abstraites, pas de table name
						// On compare le nom de table de GEOMETRY_COLUMNS et le nom de table issu du mapping
						if (sqlTableName.compareToIgnoreCase(arrayTableName) == 0) {
							Metadata metadataElt = (Metadata)metadataList.get(i);

							// colonne portant la geometrie
							String sqlGeomcolumn = rs.getString(2);
							metadataElt.setGeomColumnName(sqlGeomcolumn);

							// colonne portant le SRID
							String sqlSridColumn = rs.getString(3);
							metadataElt.setSRID(Integer.parseInt(sqlSridColumn));

							// sortie de boucle quand on a trouve une egalite entre tableName de user_sdo_geom_metadata et tableName du mapping
							break;

							// et les emprises ??
						}
				}
			}
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/// chargement d'objets par zones ////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	public static List<?> loadAllFeatures(Geodatabase data, Class<?> theClass, GM_Object geom) {
		String query = "Intersects";
		return executeFeatureList(data,geom,theClass,query);
	}


	public static List<?> loadAllFeatures(Geodatabase data, Class<?> theClass, GM_Object geom, double dist) {
		// On crée un buffer autour de la géometrie
		GM_Object buffer = geom.buffer(dist);
		String query = "Intersects";
		return executeFeatureList(data,buffer,theClass,query);
	}


	/** Renvoie une liste d'identifiants résultats d'une requete spatiale.*/
	private static List<?> executeFeatureList(Geodatabase data, GM_Object geom, Class<?> theClass, String theQuery) {

		// ceci sera le resultat
		List<Object> idList = new ArrayList<Object>();

		try {
			// recherche du tableName et nom des colonnes
			String tableName = data.getMetadata(theClass).getTableName();
			String pkColumn = data.getMetadata(theClass).getIdColumnName();
			String geomColumn = data.getMetadata(theClass).getGeomColumnName();

			// Récupère la connection
			Connection conn = data.getConnection();

			// définition de la requ�te
			String geomString = "SRID="+geom.getCRS()+";"+geom.toString();
			String query = "SELECT t."+pkColumn+" FROM "+tableName+" t ";
			query = query + "WHERE t." +geomColumn + " && '" +geomString + "'";
			query = query + " AND " + theQuery + "('" + geomString + "',t." + geomColumn + ")";

			// execute la requete
			PreparedStatement ps = conn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next())
				idList.add(rs.getObject(1));
			rs.close();
			ps.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		// renvoi du resultat
		return idList;
	}


}
