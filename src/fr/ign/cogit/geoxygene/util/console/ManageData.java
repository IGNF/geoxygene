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

package fr.ign.cogit.geoxygene.util.console;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.ojb.broker.util.configuration.impl.OjbConfiguration;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.util.loader.GenerateIds;
import fr.ign.cogit.geoxygene.util.loader.MetadataReader;
import fr.ign.cogit.geoxygene.util.loader.TypeGeom;
import fr.ign.cogit.geoxygene.util.loader.gui.GUIManageData;



/**
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.1
  * 
  */

class ManageData extends JPanel {

	// liste des noms de tables geographiques a traiter
	private static List allTables = new ArrayList();    

	private static boolean genereIds = false; 
	private static boolean genereSimple = false;
	private static boolean genereUnique = false;
	private static boolean homogeneise = false;
	private static boolean spatialIndex = false;
	private static boolean emprise = false;
		
	protected static void action() {
		
		// initialisation
		Geodatabase data = GeodatabaseOjbFactory.newInstance();
		
		// on recharge les fichiers de mapping (utile si on vient de generer le mapping)
		OjbConfiguration config = new OjbConfiguration();
		File fileMapping = new File(config.getRepositoryFilename());
		try {
			data.refreshRepository(fileMapping);     
		} catch (Exception ee) {
			System.out.println("## ATTENTION les fichiers de mapping sont incorrects");
			ee.printStackTrace();
		}     
		               	
		// choix des tables a charger
		MetadataReader theMetadataReader = new MetadataReader(data);
		allTables = theMetadataReader.getSelectedTables();
		
		if (allTables.size() == 0) {
			System.out.println("Aucune table selectionnee ...");
			return;
		}
			
		// on demande a l'utilisateur ce qu'il veut faire
		GUIManageData configuration = new GUIManageData();
		boolean[] selectedActions = configuration.showDialog();
		
		// le passage des parametres de l'IHM se fait par passage d'un tableau de boolean ...
		// code a la va-vite comme un portc !
		if (selectedActions[0]) genereIds = true;
		     else genereIds = false;
		if (selectedActions[1]) homogeneise = true;
		     else homogeneise = false;
		if (selectedActions[2]) spatialIndex = true;
		     else spatialIndex = false;
		if (selectedActions[3]) emprise = true;
		     else emprise = false;
		if (selectedActions[4]) genereSimple = true;
		     else genereSimple = false;
		if (selectedActions[5]) genereUnique = true;
		     else genereUnique = false;
		
		if (genereIds) {
			System.out.println("Generation des COGITID ...");
			for (int i=0; i< allTables.size(); i++) {
				String table = (String) allTables.get(i);
				GenerateIds generator = new GenerateIds(data,table,genereUnique);
				generator.genere();
			}
			System.out.println("Fin de la generation des COGITID ...");			
		}
		
		if (homogeneise) {
			System.out.println("Homogeneisation des geometries ...");
			for (int i=0; i<allTables.size(); i++) {
				String tableName = (String)allTables.get(i);
				System.out.println(tableName);
				Class theClass = data.getMetadata(tableName).getJavaClass();
				TypeGeom  theTypeGeom = new TypeGeom(data,theClass);
				theTypeGeom.multi();
			}
			System.out.println("Fin de l'homogeneisation des geometries ...");			
		}
		
		if (emprise) {
			System.out.println("Calcul de l'emprise des geometries dans le SGBD");
			if (data.getDBMS() == Geodatabase.ORACLE) {
				for (int i=0; i<allTables.size(); i++) {
					String tableName = (String)allTables.get(i);
				 	System.out.println(tableName);
					Class theClass = data.getMetadata(tableName).getJavaClass();
					data.mbr(theClass);    
					System.out.println("OK");   
				}
			} else {
				System.out.println("Le calcul de l'emprise des geometries dans le SGBD ne fonctionne que pour Oracle");
			}
			System.out.println("Fin du calcul de l'emprise des geometries dans le SGBD");			
		}
		
		if (spatialIndex) {
			System.out.println("Calcul des index spatiaux dans le SGBD");
			for (int i=0; i<allTables.size(); i++) {
				String tableName = (String)allTables.get(i);
				System.out.println(tableName);
				Class theClass = data.getMetadata(tableName).getJavaClass();
				data.spatialIndex(theClass);   
				System.out.println("OK");    
			}
			System.out.println("Fin du calcul des index spatiaux dans le SGBD");
		}
		
		
		System.out.println("#### FINI !! ####");
					
	}

}
