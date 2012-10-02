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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.ojb.broker.util.configuration.impl.OjbConfiguration;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.util.loader.MetadataReader;
import fr.ign.cogit.geoxygene.util.loader.XMLJavaDicoGenerator;
import fr.ign.cogit.geoxygene.util.loader.gui.GUICompileMessage;
import fr.ign.cogit.geoxygene.util.loader.gui.GUIConfigOJBXMLJava;

/**
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */

public class SqlToJava extends JPanel {

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  // liste des noms de tables geographiques a traiter
  private static List<String> allTables = new ArrayList<String>();
  // nom du fichier de mapping
  private static String mappingFileName = "repository_geo.xml";
  // classe mere des classes chargees
  private static String extentClassName = "fr.ign.cogit.geoxygene.feature.FT_Feature";
  // fichier de mapping classe mere (pour OJB)
  private static String extentMappingFileName = null;
  // generation automatique ou pas ?
  // private static boolean flagInterroTable = false;
  // nom du package
  private static String packageName = "geoxygene.geodata";
  // repertoire d'accueil des packages Java
  private static String geOxygeneData;
  // repertoire d'accueil des fichiers de mapping
  private static String geOxygeneMapping;

  public static void action(int mapping) {

    System.out.println("Generation de mapping XML  et de classes java ... ");

    if (mapping == MappingTool.MAPPING_CASTOR) {
      System.out.println("CASTOR : marche pas !");
      return;
    }

    try {

      // repertoire d'accueil des fichiers de mapping
      OjbConfiguration config = new OjbConfiguration();
      File fileMapping = new File(config.getRepositoryFilename());

      // initialisation
      Geodatabase data = GeodatabaseOjbFactory.newInstance();

      // choix des tables a charger
      MetadataReader theMetadataReader = new MetadataReader(data);
      SqlToJava.allTables = theMetadataReader.getSelectedTables();

      if (SqlToJava.allTables.size() == 0) {
        System.out.println("Aucune table selectionnee ...");
        return;
      }

      System.out.println("Generation du mapping, des classes java et du dico ...");

      // determine valeur par defaut de geOxygeneData
      File tryFileData = new File(fileMapping.getParentFile().getParentFile().getParentFile(), "data");
      if (tryFileData.exists()) {
        SqlToJava.geOxygeneData = tryFileData.getPath();
      } else {
        tryFileData = new File(fileMapping.getParentFile().getParentFile().getParentFile().getParentFile(), "data");
        if (tryFileData.exists()) {
          SqlToJava.geOxygeneData = tryFileData.getPath();
        }
      }

      // determine valeur par defaut de geOxygeneMapping
      SqlToJava.geOxygeneMapping = fileMapping.getParentFile().getPath();

      GUIConfigOJBXMLJava configuration = new GUIConfigOJBXMLJava(SqlToJava.packageName, SqlToJava.geOxygeneData, SqlToJava.geOxygeneMapping,
          SqlToJava.extentClassName, SqlToJava.mappingFileName, SqlToJava.extentMappingFileName);
      String[] selectedValues = configuration.showDialog();

      // ceci permet d'utiliser le cancel
      if (selectedValues[2] == null) {
        return;
      }

      XMLJavaDicoGenerator generator = new XMLJavaDicoGenerator(null, data, false, SqlToJava.allTables, selectedValues[0], selectedValues[1],
          selectedValues[2], selectedValues[3], selectedValues[4], selectedValues[5]);
      generator.writeAll();

      GUICompileMessage message = new GUICompileMessage();
      message.showDialog();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
