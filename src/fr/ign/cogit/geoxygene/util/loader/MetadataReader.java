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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.util.loader.gui.GUITableChoice;

/**
 * Usage interne. Le but est de remplir la liste des tables a charger.
 * 
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class MetadataReader {

  private Geodatabase data;
  private List<String> theList = new ArrayList<String>();
  private String query;

  private final String ORACLE_QUERY = "SELECT TABLE_NAME FROM USER_SDO_GEOM_METADATA"; //$NON-NLS-1$
  private final String POSTGIS_QUERY = "SELECT F_TABLE_NAME FROM GEOMETRY_COLUMNS"; //$NON-NLS-1$

  public MetadataReader(Geodatabase Data) {
    this.data = Data;
    if (this.data.getDBMS() == Geodatabase.ORACLE) {
      this.query = this.ORACLE_QUERY;
    } else if (this.data.getDBMS() == Geodatabase.POSTGIS) {
      this.query = this.POSTGIS_QUERY;
    }
  }

  public List<String> getSelectedTables() {
    this.getAllTables();
    this.ihm();
    return this.theList;
  }

  private void getAllTables() {
    this.theList.clear();
    try {
      Connection conn = this.data.getConnection();
      Statement stm = conn.createStatement();
      ResultSet rs = stm.executeQuery(this.query);
      while (rs.next()) {
        String sqlTableName = rs.getString(1);
        if (sqlTableName.compareToIgnoreCase("RESULT_POINT") == 0) { //$NON-NLS-1$
          continue;
        }
        if (sqlTableName.compareToIgnoreCase("RESULT_CURVE") == 0) { //$NON-NLS-1$
          continue;
        }
        if (sqlTableName.compareToIgnoreCase("RESULT_SURFACE") == 0) { //$NON-NLS-1$
          continue;
        }
        if (sqlTableName.compareToIgnoreCase("RESULTAT") == 0) { //$NON-NLS-1$
          continue;
        }
        if (sqlTableName.compareToIgnoreCase("TABLEAUX") == 0) { //$NON-NLS-1$
          continue;
        }

        this.theList.add(sqlTableName);
      }
      stm.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void ihm() {
    String user = null;
    try {
      user = this.data.getConnection().getMetaData().getUserName();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Selection des tables ...");
    GUITableChoice swing = new GUITableChoice(this.theList.toArray(), user);
    String[] selectedTables = swing.showDialog();
    this.theList = new ArrayList<String>();
    for (String selectedTable : selectedTables) {
      System.out.println(selectedTable);
      this.theList.add(selectedTable);
    }
  }

}
