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

import java.io.File;
import java.io.FileWriter;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;

/**
 * Usage interne. Generateur de fichier XML pour OJB.
 * 
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */

class OjbXMLGenerator {

  private FileWriter fw;
  private String mappingFilePath; // GEOXYGENE_MAPPING/repository_geo.xml
  private String extentMappingFilePath = null; // GEOXYGENE_MAPPING/repository_feature.xml
  // private String plateformePath;
  private int i = 1; // identifiant du field dans le fichier de mapping
  private String extentClassName;

  private String mappingString = ""; // chaine de caractere qu'on va ecrire dans le fichier de mapping //$NON-NLS-1$
  private String classeMereString = "";// chaine de caractere qu'on va ecrire dans le fichier de mapping de la classe mere //$NON-NLS-1$

  private Geodatabase data;

  private String keyColumnName;
  private final static String KEY_COLUMN_NAME_ORACLE = "COGITID"; //$NON-NLS-1$
  private final static String KEY_COLUMN_NAME_POSTGIS = "COGITID"; //$NON-NLS-1$

  OjbXMLGenerator(Geodatabase Data, String path, String mappingFileName,
      String ExtentClassName, String extentMappingFileName) {
    try {
      this.data = Data;
      // plateformePath = path;
      File thePath = new File(path);
      File mappingFile = new File(thePath, mappingFileName);
      this.mappingFilePath = mappingFile.getPath();
      this.extentClassName = ExtentClassName;
      if (extentMappingFileName != null) {
        File extentMappingFile = new File(thePath, extentMappingFileName);
        this.extentMappingFilePath = extentMappingFile.getPath();
      }
      if (this.data.getDBMS() == Geodatabase.ORACLE) {
        this.keyColumnName = OjbXMLGenerator.KEY_COLUMN_NAME_ORACLE;
      } else if (this.data.getDBMS() == Geodatabase.POSTGIS) {
        this.keyColumnName = OjbXMLGenerator.KEY_COLUMN_NAME_POSTGIS;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  void writeFileHeader() {
    // fichier de mapping des classes filles
    String line1 = "<!-- fichier de mapping OJB pour GeOxygene : classes geographiques -->\n";
    String line2 = "<!-- fichier genere automatiquement par le chargeur de la plate-forme GeOxygene -->\n";
    String line3 = "\n"; //$NON-NLS-1$

    // fichier de mapping classe mere
    if (this.extentMappingFilePath != null) {
      String line4 = "<class-descriptor class=\"" + this.extentClassName + "\"  >\n"; //$NON-NLS-1$ //$NON-NLS-2$
      this.classeMereString = line1 + line2 + line3 + line4;
    }
  }

  void writeFileBottom() {
    // fichier de mapping de la classe mere
    if (this.extentMappingFilePath != null) {
      String line0 = "</class-descriptor>\n"; //$NON-NLS-1$
      String line1 = "\n"; //$NON-NLS-1$
      this.classeMereString += line0 + line1;
    }
  }

  void writeClassHeader(String className, String tableName) {
    // fichier de mapping des classes filles
    this.i = 1; // remise a 1 du compteur
    String str1 = "<class-descriptor class=\"" + className + "\" table=\"" + tableName + "\" >\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    String str2 = "  <field-descriptor name=\"id\"  column=\"" + this.keyColumnName + "\" jdbc-type=\"INTEGER\" primarykey=\"true\" autoincrement=\"true\"/>\n"; //$NON-NLS-1$ //$NON-NLS-2$
    this.mappingString += str1 + str2;

    // fichier de mapping de la classe mere
    if (this.extentMappingFilePath != null) {
      str1 = "  <extent-class class-ref=\"" + className + "\" />\n"; //$NON-NLS-1$ //$NON-NLS-2$
      this.classeMereString += str1;
    }
  }

  void writeClassBottom() {
    // fichier de mapping des classes filles
    String line0 = "</class-descriptor>\n"; //$NON-NLS-1$
    String line1 = "\n"; //$NON-NLS-1$
    this.mappingString += line0 + line1;
  }

  void writeField(String javaName, String sqlName, String sqlType) {
    try {
      this.i++;
      String str1 = "  <field-descriptor name=\"" + javaName + "\" column=\"" + sqlName + "\" jdbc-type=\"" + this.getJdbcType(sqlType) + "\" />\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
      this.mappingString += str1;
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  void writeInFile() {
    try {
      if (this.extentMappingFilePath != null) {
        if (this.extentMappingFilePath.equals(this.mappingFilePath)) {
          this.fw = new FileWriter(this.mappingFilePath);
          this.fw.write(this.classeMereString + "\n" + this.mappingString); //$NON-NLS-1$
          this.fw.close();
        } else {
          this.fw = new FileWriter(this.extentMappingFilePath);
          this.fw.write(this.classeMereString);
          this.fw.close();
          this.fw = new FileWriter(this.mappingFilePath);
          this.fw.write(this.mappingString);
          this.fw.close();
        }
      } else {
        this.fw = new FileWriter(this.mappingFilePath);
        this.fw.write(this.mappingString);
        this.fw.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // /////////////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////////////
  private String getJdbcType(String sqlType) {
    try {
      if (this.data.getDBMS() == Geodatabase.ORACLE) {
        return this.oracleType2JdbcType(sqlType);
      } else if (this.data.getDBMS() == Geodatabase.POSTGIS) {
        return this.postgisType2JdbcType(sqlType);
      }
      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  // /////////////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////////////
  private String oracleType2JdbcType(String oracle) throws Exception {
    if (oracle.compareToIgnoreCase("VARCHAR2") == 0) {
      return "VARCHAR"; //$NON-NLS-1$ //$NON-NLS-2$
    } else if (oracle.compareToIgnoreCase("VARCHAR") == 0) {
      return "VARCHAR"; //$NON-NLS-1$ //$NON-NLS-2$
    } else if (oracle.compareToIgnoreCase("CHAR") == 0) {
      return "VARCHAR"; //$NON-NLS-1$//$NON-NLS-2$
    } else if (oracle.compareToIgnoreCase("NUMBER") == 0) {
      return "DOUBLE"; //$NON-NLS-1$ //$NON-NLS-2$
    } else if (oracle.compareToIgnoreCase("FLOAT") == 0) {
      return "DOUBLE"; //$NON-NLS-1$//$NON-NLS-2$
    } else if (oracle.compareToIgnoreCase("INTEGER") == 0) {
      return "INTEGER"; //$NON-NLS-1$ //$NON-NLS-2$
    } else if (oracle.compareToIgnoreCase("BOOLEAN") == 0) {
      return "BIT"; //$NON-NLS-1$//$NON-NLS-2$
    } else if (oracle.compareToIgnoreCase("SDO_GEOMETRY") == 0) {
      return "STRUCT\" conversion=\"fr.ign.cogit.geoxygene.datatools.ojb.GeomGeOxygene2Dbms"; //$NON-NLS-1$ //$NON-NLS-2$
    } else {
      throw new Exception("type non reconnu : " + oracle);
    }
  }

  // /////////////////////////////////////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////////////////////////////////////////
  private String postgisType2JdbcType(String postgis) throws Exception {
    if (postgis.compareToIgnoreCase("varchar") == 0) {
      return "VARCHAR"; //$NON-NLS-1$ //$NON-NLS-2$
    } else if (postgis.compareToIgnoreCase("bpchar") == 0) {
      return "VARCHAR"; //$NON-NLS-1$ //$NON-NLS-2$
    } else if (postgis.compareToIgnoreCase("float8") == 0) {
      return "DOUBLE"; //$NON-NLS-1$//$NON-NLS-2$
    } else if (postgis.compareToIgnoreCase("float4") == 0) {
      return "FLOAT"; //$NON-NLS-1$//$NON-NLS-2$
    } else if (postgis.compareToIgnoreCase("int2") == 0) {
        return "INTEGER"; //$NON-NLS-1$ //$NON-NLS-2$
    } else if (postgis.compareToIgnoreCase("int4") == 0) {
      return "INTEGER"; //$NON-NLS-1$ //$NON-NLS-2$
    } else if (postgis.compareToIgnoreCase("int8") == 0) {
      return "BIGINT"; //$NON-NLS-1$ //$NON-NLS-2$
    } else if (postgis.compareToIgnoreCase("bool") == 0) {
      return "BIT"; //$NON-NLS-1$//$NON-NLS-2$
    } else if (postgis.compareToIgnoreCase("geometry") == 0) {
      return "STRUCT\" conversion=\"fr.ign.cogit.geoxygene.datatools.ojb.GeomGeOxygene2Dbms"; //$NON-NLS-1$ //$NON-NLS-2$
    } else {
      throw new Exception("type non reconnu : " + postgis);
    }
  }

}
