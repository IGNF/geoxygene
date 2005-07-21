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

package fr.ign.cogit.geoxygene.example;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.viewer.ObjectViewer;



/**
 * Simple exemple de code pour l'utilisation de GeOxygene.
 * On suppose qu'il existe une classe persistante "donnees.defaut.Troncon_route" 
 * (sinon changer le nom de la classe dans le code).
 * Si la classe a charger contient beaucoup d'objet, lancer le programme avec l'option '-Xmx512M'
 * (java -Xmx512M exemple.FirstExample) .
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 *
 */


public class FirstExample {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /* Attributs */
    private Geodatabase db;     // connection a la base de donnees
    private Class tronconClasse;	// classe de troncons
 
	private String nomClasse = "geoxygene.geodata.Troncon_route"; // nom de la classe a charger

    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /* Constructeur */
    public FirstExample() {    
        
        // initialisation de la connection et lecture des fichiers de mapping
        System.out.println("Debut initialisation");    
		db = GeodatabaseOjbFactory.newInstance();		
        System.out.println("Initialisation OK");
        try {
            tronconClasse = Class.forName(nomClasse);
        } catch (ClassNotFoundException e) {
            System.out.println(nomClasse+" : non trouvee");  
            System.exit(0);    	
        }
    }

 
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /* méthode main */
    public static void main (String args[]) {
        FirstExample test = new FirstExample();     
        
        test.exemple1();
        test.exemple2();
            
    }
        
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /* méthode exemple1 : exemple de traitement sur un objet.*/
    public void exemple1 ()  {
        
        // Declaration des variables
        FT_Feature troncon;
		Resultat resultat;
		Resultat buffer;
        GM_LineString polyligne;
        GM_Polygon polygone;
        int rayon = 20;                // rayon de buffer
                          
        // Debut d'une transaction
        System.out.println("Debut transaction");
        db.begin();
        
        // Recherche du plus petit identifiant
        Integer gid = new Integer ( db.maxId(tronconClasse) );
                
        // Chargement d'un objet par son identifiant
        troncon = (FT_Feature)db.load(tronconClasse,gid);
        System.out.println("identifiant de l'objet charge : "+troncon.getId());
        
        // Sa géométrie
        polyligne = (GM_LineString)troncon.getGeom();
        System.out.println(polyligne);
        
        // Creation d'un objet Resultat auquel on affecte la geometrie du Troncon_route
        resultat = new Resultat();
        resultat.setGeom(polyligne);
                
        // Creation d'un objet Resultat auquel on affecte la geometrie d'un buffer autour du Troncon
        System.out.println("Traitement");
        polygone = (GM_Polygon)polyligne.buffer(rayon);
        buffer = new Resultat();
        buffer.setGeom(polygone);
        
        // Ecriture des résultats dans la base
        db.makePersistent(resultat);
        db.makePersistent(buffer);
        
        //Fermeture transaction et commit ( = validation des ecritures dans la base)
        System.out.println("commit");
        db.commit();
        
        // Visualisation dans le viewer
        ObjectViewer viewer = new ObjectViewer(db);
        FT_FeatureCollection iniColl = new FT_FeatureCollection();
		iniColl.add(troncon);
		FT_FeatureCollection bufferColl = new FT_FeatureCollection();
		bufferColl.add(buffer);
		viewer.addFeatureCollection(iniColl,"Tronçons");
        viewer.addFeatureCollection(bufferColl,"Résultats");
        
    }     

    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /* méthode exemple2 : exemple de traitement sur une couche entiere.*/
    public void exemple2 ()  {
        
        // Declaration des variables
        FT_Feature troncon;
        FT_FeatureCollection tronconList;
        Resultat resultat;
        GM_LineString polyligne1, polyligne2;
        double seuil = 80.0;                    // seuil pour le Douglas-Peucker
        int compteur = 0;                   // pour voir l'avancement
                
        // Debut d'une transaction
        System.out.println("Debut transaction");
        db.begin();
                     
        // Recherche du nombre d'objets a traiter
        int n = db.countObjects(tronconClasse);
        System.out.println("Nombre d'objets a traiter :"+n);
        
        // Chargement de tous les objets 
        tronconList = db.loadAllFeatures(tronconClasse);
        System.out.println("chargement termine");    
        
        // Création d'une nouvelle collection pour stocker les résultats
        FT_FeatureCollection allResults = new FT_FeatureCollection();    
        
        // Traitement des les objets        
        tronconList.initIterator();
        while (tronconList.hasNext()) {
            troncon = tronconList.next();          
            polyligne1 = (GM_LineString)troncon.getGeom();
            polyligne2 = (GM_LineString)Filtering.DouglasPeucker(polyligne1,seuil);
            resultat = new Resultat();
            resultat.setGeom(polyligne2);
            db.makePersistent(resultat);
            compteur++;
            if (compteur % 100 == 0)
                System.out.println(compteur);
            allResults.add(resultat);
        }
        
        //Fermeture transaction et commit ( = validation des ecritures dans la base)
        System.out.println("commit");
        db.commit();
        
		// Visualisation dans le viewer
		ObjectViewer viewer = new ObjectViewer(db);
		viewer.addFeatureCollection(tronconList,"Tronçons");        
		viewer.addFeatureCollection(allResults,"Résultats");              
    }
                
}
