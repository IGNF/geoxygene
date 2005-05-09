/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.Metadata;

/**
 * Encapsulation d'appels a Postgis.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 *
 */
public class SpatialQuery {
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	/// calcul d'index spatial ///////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	public static void spatialIndex(Geodatabase data, Class clazz) {
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
	public static void initGeomMetadata (List metadataList, Connection conn) {    
		try {
			int n = metadataList.size();
			Statement stm = conn.createStatement();
			String query = "SELECT F_TABLE_NAME, F_GEOMETRY_COLUMN FROM GEOMETRY_COLUMNS";
			ResultSet rs = (ResultSet)stm.executeQuery(query);
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

						// sortie de boucle quand on a trouve une egalite entre tableName de user_sdo_geom_metadata et tableName du mapping
						break;              
					}           
				}
			}
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
