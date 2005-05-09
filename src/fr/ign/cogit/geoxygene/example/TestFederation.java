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
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Federation de donnees.
 * Exemple d'utilisation de plusieurs "Geodatabase" simultanees.
 * 
 * Remarque : l'utilisation de plusieurs transactions simultanees plante, 
 * a etudier.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 *
 */

public class TestFederation {


	public static void main(String args[]) {
		
    	Geodatabase db1,db2,db3; 
    	Class featureClass1=null; 
		Class featureClass2=null; 
        long t1,t2;
    
        System.out.println("coucou");
    
		db1 = GeodatabaseOjbFactory.newInstance();
		db2 = GeodatabaseOjbFactory.newInstance("thales");
		db3 = GeodatabaseOjbFactory.newInstance("chenipan");
		
		System.out.println(db1);
		System.out.println(db2);
		System.out.println(db3);		

    	
    	try {
    		featureClass1 = Class.forName("donnees.defaut.Bdc38_troncon_route");
			featureClass2 = Class.forName("donnees.defaut.Noeud_routier");
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
       
        
        System.out.println("nombre d'objets : "+db1.countObjects(featureClass1));    
		System.out.println("nombre d'objets : "+db2.countObjects(featureClass2));        
         
                
        System.out.print("chargement ... "+featureClass1.getName()+"   ");
        t1 = System.currentTimeMillis();
        FT_FeatureCollection coll1 = db1.loadAllFeatures(featureClass1);
        t2 = System.currentTimeMillis();
        System.out.println( (t2-t1) / 1000. );
        
		System.out.print("chargement ... "+featureClass2.getName()+"  ");
		t1 = System.currentTimeMillis();
		FT_FeatureCollection coll2 = db2.loadAllFeatures(featureClass2);
		t2 = System.currentTimeMillis();
		System.out.println( (t2-t1) / 1000. );
		
		
		System.out.println("Calcul buffer 1 ...");
		int i = 0;
		GM_Aggregate aggr = new GM_Aggregate();
		coll1.initIterator();
		while (coll1.hasNext()) {
			i++;
			GM_Object geom = coll1.next().getGeom();
			geom = geom.buffer(100.);
			aggr.add(geom);
			if (i > 1000) break;
		}
		
		
		System.out.println("Calcul buffer 2 ...");
		i = 0;
		coll2.initIterator();
		while (coll2.hasNext()) {
			i++;
			GM_Object geom = coll2.next().getGeom();
			geom = geom.buffer(1000.);
			aggr.add(geom);
			if (i > 1000) break;
		}
    
		db3.begin();
		System.out.println("begin db3");
        
        System.out.print("ecriture ... ");
        t1 = System.currentTimeMillis();
        aggr.initIterator();
        while (aggr.hasNext()) {
            Resultat res = new  Resultat();
			db3.makePersistent(res);    
			res.setGeom(aggr.next());  			
        }    
        t2 = System.currentTimeMillis();
        System.out.println( (t2-t1) / 1000. );     
                                     
        System.out.print("commit db3... ");
        t1 = System.currentTimeMillis();
        db3.commit();
        t2 = System.currentTimeMillis();
        System.out.println( (t2-t1) / 1000. );        
        System.out.println("OK");
        
		
	}


}
