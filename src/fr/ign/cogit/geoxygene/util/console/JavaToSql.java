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

package fr.ign.cogit.geoxygene.util.console;

import java.io.File;

import javax.swing.JPanel;

import org.apache.ojb.broker.util.configuration.impl.OjbConfiguration;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.util.loader.SQLXMLGenerator;
import fr.ign.cogit.geoxygene.util.loader.gui.GUIConfigSqlOJBXML;

/**
 * permet à partir d'une classe java choisie par l'utilisateur de Génèrer
 * automatiquement la table correspondante dans le sgbd (postgis ou oracle) et
 * le fichier de mapping correspondant
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

class JavaToSql extends JPanel {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  // nom du fichier de mapping
  private static String mappingFileName = "repository_geo.xml";
  // repertoire de la racine du projet geoxygene
  private static String geoxygeneDirectory = "";
  // repertoire d'accueil des fichiers de mapping
  private static String geOxygeneMapping;

  protected static void action() {

    System.out
        .println("Generation de tables dans le SGBD et du mapping XML correspondant ... ");

    try {

      // initialisation
      Geodatabase data = GeodatabaseOjbFactory.newInstance();

      // determine valeur par defaut de la racine du projet geoxygene
      OjbConfiguration config = new OjbConfiguration();
      File fileMapping = new File(config.getRepositoryFilename());

      try {
        File tryFileData = new File(fileMapping.getParentFile().getParentFile()
            .getParentFile(), "data");
        if (tryFileData.exists()) {
          JavaToSql.geoxygeneDirectory = tryFileData.getParentFile().getPath();
        } else {
          tryFileData = new File(fileMapping.getParentFile().getParentFile()
              .getParentFile().getParentFile(), "data");
          if (tryFileData.exists()) {
            JavaToSql.geoxygeneDirectory = tryFileData.getParentFile()
                .getPath();
          }
        }
        if (!tryFileData.exists()) {
          tryFileData = new File(fileMapping.getParentFile().getParentFile()
              .getParentFile().getParentFile().getParentFile(), "data");
          if (tryFileData.exists()) {
            JavaToSql.geoxygeneDirectory = tryFileData.getParentFile()
                .getPath();
          }
        }
      } catch (Exception e) {
      }

      // determine valeur par defaut de geoxygeneMapping
      JavaToSql.geOxygeneMapping = fileMapping.getParentFile().getPath();

      GUIConfigSqlOJBXML configuration = new GUIConfigSqlOJBXML(
          JavaToSql.geoxygeneDirectory, JavaToSql.geOxygeneMapping,
          JavaToSql.mappingFileName);
      String[] selectedValues = configuration.showDialog();

      String javaFilePath = selectedValues[0];
      String mappingDirectory = selectedValues[1];
      String tableName = selectedValues[2];
      JavaToSql.mappingFileName = selectedValues[3];

      if (javaFilePath == null || mappingDirectory == null || tableName == null
          || JavaToSql.mappingFileName == null) {
        return;
      }
      SQLXMLGenerator generator = new SQLXMLGenerator(data, javaFilePath,
          mappingDirectory, tableName, JavaToSql.mappingFileName);
      generator.writeAll();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
