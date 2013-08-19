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

package fr.ign.cogit.geoxygene.util.loader;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;

/**
 * Usage interne. Remplit les tables GF_FeatureType et GF_AttributeType, quand
 * elles existent, au moment de la generation des classes Java.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

class DicoGenerator {

  private Connection conn;
  private int dbms;

  DicoGenerator(Geodatabase data) {
    this.conn = data.getConnection();
    this.dbms = data.getDBMS();
  }

  void writeFeature(String typeName) {
    int max = 1;
    try {

      Statement stm = this.conn.createStatement();

      String query = "SELECT MAX (GF_FeatureTypeID) FROM GF_FEATURETYPE";
      ResultSet rs = stm.executeQuery(query);
      while (rs.next()) {
        if (this.dbms == Geodatabase.ORACLE) {
          if (rs.getObject(1) != null) {
            max = ((BigDecimal) rs.getObject(1)).intValue() + 1;
          }
        } else if (this.dbms == Geodatabase.POSTGIS) {
          max = rs.getInt(1) + 1;
        }
      }

      String update = "INSERT INTO GF_FEATURETYPE VALUES (" + max + ",'"
          + typeName + "',null,'0')";
      stm.executeUpdate(update);
      stm.close();
    } catch (Exception e) {
      // e.printStackTrace();
      // mieux de pas afficher de message : si les tables n'existent pas ou s'il
      // y a des doublons
    }
  }

  void writeAttribute(String featureName, String memberName, String typeName) {
    int id = 0;
    int max = 1;
    try {
      Statement stm = this.conn.createStatement();

      String query = "SELECT GF_FEATURETYPEID FROM GF_FEATURETYPE WHERE TYPENAME = '"
          + featureName + "'";
      ResultSet rs = stm.executeQuery(query);
      while (rs.next()) {
        if (this.dbms == Geodatabase.ORACLE) {
          id = ((BigDecimal) rs.getObject(1)).intValue();
        } else if (this.dbms == Geodatabase.POSTGIS) {
          id = rs.getInt(1);
        }
      }

      query = "SELECT MAX (GF_PropertyTypeID) FROM GF_ATTRIBUTETYPE";
      rs = stm.executeQuery(query);
      while (rs.next()) {
        if (this.dbms == Geodatabase.ORACLE) {
          if (rs.getObject(1) != null) {
            max = ((BigDecimal) rs.getObject(1)).intValue() + 1;
          }
        } else if (this.dbms == Geodatabase.POSTGIS) {
          max = rs.getInt(1) + 1;
        }
      }

      String update = "INSERT INTO GF_ATTRIBUTETYPE VALUES (" + max + "," + id
          + ",'" + memberName + "',null,'" + typeName + "',null,1,1,null)";
      stm.executeUpdate(update);
      stm.close();
    } catch (Exception e) {
      // e.printStackTrace();
      // mieux de pas afficher de message : si les tables n'existent pas ou s'il
      // y a des doublons
    }
  }

}
