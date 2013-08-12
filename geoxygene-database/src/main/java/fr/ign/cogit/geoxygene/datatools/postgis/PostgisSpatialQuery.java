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

package fr.ign.cogit.geoxygene.datatools.postgis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.Metadata;

/**
 * Encapsulation d'appels a Postgis.
 * 
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Sebastier Mustiere
 * @author Nathalie Abadie
 * @author Julien Perret
 * @version 1.3 21/07/2008 : modification de la définition de la requete dans la
 *          methode executeFeatureList 21/07/2008 : modification de
 *          initGeomMetadata : récupération du SRID dans postgis
 */
public class PostgisSpatialQuery {

  /**
   * creation d'index spatial
   * 
   * @param data
   * @param clazz
   */
  public static void spatialIndex(Geodatabase data, Class<?> clazz) {
    try {
      Connection conn = data.getConnection();
      Statement stm = conn.createStatement();
      String tableName = data.getMetadata(clazz).getTableName().toLowerCase();
      String columnName = data.getMetadata(clazz).getGeomColumnName()
          .toLowerCase();

      // on est oblige de faire ceci, sinon message d'erreur: nom d'index trop
      // long...
      String indexName;
      if (tableName.length() > 24) {
        indexName = tableName.substring(0, 24) + "_spidx";
      } else {
        indexName = tableName + "_spidx";
      }

      try {
        String query = "CREATE INDEX " + indexName + " ON " + tableName
            + " USING GIST (" + columnName + " GIST_GEOMETRY_OPS)";
        stm.executeUpdate(query);
        conn.commit();
      } catch (Exception ee) { // l'index existe deja
        conn.commit();
        String query = "REINDEX INDEX " + indexName + " FORCE";
        stm.executeUpdate(query);
      }
      stm.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * intialisation des metadonnees
   * 
   * @param metadataList
   * @param conn
   */
  public static void initGeomMetadata(List<?> metadataList, Connection conn) {
    try {
      // recupere les objets de la table geometrique
      Statement stm = conn.createStatement();
      ResultSet rs = stm
          .executeQuery("SELECT F_TABLE_NAME, F_GEOMETRY_COLUMN, SRID, COORD_DIMENSION FROM GEOMETRY_COLUMNS");

      // parcours des objets de la table geometrique
      while (rs.next()) {

        // recupere nom table geometrique
        String sqlTableName = rs.getString(1);

        // parcours des metadonnees
        for (int i = 0; i < metadataList.size(); i++) {
          // recupere nom de la table i
          String arrayTableName = ((Metadata) metadataList.get(i))
              .getTableName();

          // pour les classes abstraites, il n'y a pas de table name: ne rien a
          // faire
          if (arrayTableName == null) {
            continue;
          }

          // les deux noms de table sont differents: ne rien faire
          if (sqlTableName.compareToIgnoreCase(arrayTableName) != 0) {
            continue;
          }

          // renseigne les metadonnees geometriques
          Metadata metadataElt = (Metadata) metadataList.get(i);
          // le nom de colonne geometrie
          metadataElt.setGeomColumnName(rs.getString(2));
          // la dimension
          metadataElt.setDimension(Integer.parseInt(rs.getString(4)));
          // colonne portant le SRID
          String sqlSridColumn = rs.getString(3);
          metadataElt.setSRID(Integer.parseInt(sqlSridColumn));

          // sortie de boucle quand on a trouve une egalite
          // entre tableName de user_sdo_geom_metadata et
          // tableName du mapping
          break;
          // TODO: ajouter enveloppe et tolerances
        }
      }
      stm.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
    /**
     * intialisation des metadonnees
     * 
     * @param metadataList
     * @param conn
     */
    public static Map<Integer, Map<String, String>> getTables(Connection conn) {
    
        Map<Integer, Map<String, String>> resultat = null;
        
        try {
            // recupere les objets de la table geometrique
            Statement stm = conn.createStatement();
            ResultSet rs = stm
                    .executeQuery(" SELECT F_TABLE_SCHEMA as schema, F_TABLE_NAME as table, TYPE as type, "
                            + "            F_GEOMETRY_COLUMN as geometry_colum, SRID as srid "
                            + " FROM GEOMETRY_COLUMNS");

            int cpt = 0;
            resultat = new HashMap<Integer, Map<String, String>>();
            // Parcours des objets de la table geometrique
            while (rs.next()) {
                // recupere nom table geometrique
                Map<String, String> row = new HashMap<String, String>();
                row.put("schema", rs.getString("schema"));
                row.put("table", rs.getString("table"));
                row.put("type", rs.getString("type"));
                row.put("geometry_colum", rs.getString("geometry_colum"));
                row.put("srid", rs.getString("srid"));
                resultat.put(cpt, row);
                cpt++;
            }
            rs.close();
            stm.close();
        
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return resultat;
    }

  /**
   * chargement d'objets par zones
   * 
   * @param data
   * @param theClass
   * @param geom
   * @return
   */
  public static List<?> loadAllFeatures(Geodatabase data, Class<?> theClass,
      IGeometry geom) {
    return PostgisSpatialQuery.loadAllFeatures(data, theClass, geom, 0.0);
  }

  /**
   * @param data
   * @param theClass
   * @param geom
   * @param dist
   * @return
   */
  public static List<?> loadAllFeatures(Geodatabase data, Class<?> theClass,
      IGeometry geom, double dist) {
    if (dist == 0.0) {
      return PostgisSpatialQuery.executeFeatureList(data, geom, theClass,
          "Intersects");
    } else {
      return PostgisSpatialQuery.executeFeatureList(data, geom.buffer(dist),
          theClass, "Intersects");
    }
  }

  /** Renvoie une liste d'identifiants résultats d'une requete spatiale. */
  private static List<?> executeFeatureList(Geodatabase data, IGeometry geom,
      Class<?> theClass, String theQuery) {

    // ceci sera le resultat
    List<Object> idList = new ArrayList<Object>();

    try {
      // recherche du tableName et nom des colonnes
      String tableName = data.getMetadata(theClass).getTableName();
      String pkColumn = data.getMetadata(theClass).getIdColumnName();
      String geomColumn = data.getMetadata(theClass).getGeomColumnName();

      // recupere la connection
      Connection conn = data.getConnection();

      // definition de la requête
      String geomString = "SRID=" + geom.getCRS() + ";" + geom.toString();
      String query = "SELECT t." + pkColumn + " FROM " + tableName + " t ";
      query = query + "WHERE t." + geomColumn + " && '" + geomString + "'";
      query = query + " AND " + theQuery + "('" + geomString + "',t."
          + geomColumn + ")";

      // execute la requete
      PreparedStatement ps = conn.prepareStatement(query);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        idList.add(rs.getObject(1));
      }
      rs.close();
      ps.close();

    } catch (Exception e) {
      e.printStackTrace();
    }

    // renvoi du resultat
    return idList;
  }

}
