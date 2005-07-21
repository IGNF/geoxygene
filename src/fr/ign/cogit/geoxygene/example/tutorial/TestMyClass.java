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

package fr.ign.cogit.geoxygene.example.tutorial;

import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;


/**
 * Exemple.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


public class TestMyClass {
    
      
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    /* represente une connection au SGBD via le mappeur objet-relationnel */
    private Geodatabase db;
     
     
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    /* constructeur : initialise l'attribut Geodatabse*/
    public TestMyClass() {
        // SGBD = oracle ; mappeur = OJB
        // initialise la connection au SGBD et le mapping
		db = GeodatabaseOjbFactory.newInstance();
    }
     
     
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    /* main */
    public static void main (String args[]) {
    
       // on construit un nouvel objet "TestMaclasse"
       TestMyClass test = new TestMyClass();
     
       // on appelle les methodes de TestMaclasse
       test.cree();
       test.charge();
       test.interroge();  
    }
     
     
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    /* methode cree() : on cree de nouveaux objets et on les rends persistants */
    public void cree() {
     
       // declaration des variables
       MyClass monobjet;
     
       // ouverture d'une transaction
       db.begin();
       System.out.println("begin");
     
       // creation de 2 objets
       // le "db.makePersistent" les rend persistents
       monobjet = new MyClass();
       monobjet.bonjour();
       db.makePersistent(monobjet);
       monobjet.setField0(123);
       monobjet.setField1("M. et Mme. PIC ont une fille");
       monobjet.setField2(true);
       monobjet.setField3(3.14159);
     
       monobjet = new MyClass();
       monobjet.bonjour();
       db.makePersistent(monobjet);
       monobjet.setField0(4567890);
       monobjet.setField1("Aïcha");
       monobjet.setField2(false);
       monobjet.setField3(0.123456);
     
       // remarque : on affecte aucun identifiant
       // il est affecte automatiquement par OJB
     
       // commit et fermeture de la transaction
       // on rend valide toutes les creations et modifications
       // une modif meme apres le "db.makePersistent" est repercutee
       db.commit();
       System.out.println("commit ok");
    }
     
        
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    /* methode charge() : on va charger des objets de la base */
    public void charge() {
    
        // declaration des variables
        MyClass monobjet;
         
        // ouverture d'une transaction
        db.begin();
        System.out.println("begin");
        
        // on charge tous les objets de Maclasse
        List list = db.loadAll(MyClass.class);
        
        // on passe en revue ces objets
        System.out.println("nombre d'objets : "+list.size());
        for (int i=0; i<list.size(); i++) {
           monobjet = (MyClass)list.get(i);
           System.out.println("id : "+monobjet.getId());
           System.out.println("classe : "+monobjet.getClass().getName());
           System.out.println("field0 : "+monobjet.getField0());
           System.out.println("field1 : "+monobjet.getField1());
           System.out.println("field2 : "+monobjet.getField2());
           System.out.println("field3 : "+monobjet.getField3());
        }
         
        // commit et fermeture de la transaction
        db.commit();
        System.out.println("commit ok");
    }
     
     
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    /* methode interroge() : exemple de requete oriente-objet OQL */
    public void interroge() {
     
       // declaration des variables
       MyClass monobjet;
     
       // ouverture d'une transaction
       db.begin();
       System.out.println("begin");
     
       // creation de la requete
       String query = "select x from fr.ign.cogit.geoxygene.example.tutorial.MyClass where field0 > $1";
       // $1 represente un parametre (200 ici)
       List list = db.loadOQL(query, new Integer(200));
     
       // on passe en revue ces objets
       System.out.println("nombre d'objets : "+list.size());
       for (int i=0; i<list.size(); i++) {
           monobjet = (MyClass)list.get(i);
           System.out.println("id : "+monobjet.getId());
       }
     
       // commit et fermeture de la transaction
       db.commit();
       System.out.println("commit ok");
    }
 
}
 
