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

package fr.ign.cogit.geoxygene.example.relations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbOracle;


/** 
 * Test des relations BIDIRECTIONNELLES entre les classes AAA et BBB. 
 * Ce test teste aussi la persistance.
 * C'est le meme que "TestRelationsBiNonPersistantes", 
 * avec en plus les fonctions d'ecriture dans le SGBD.  
 * On peut controler a tout moment l'etat de la base en activant des commit et en quittant l'application.
 * Pensez a activer le fichier de mapping "repository_AAA_BBB.xml" .
 * La base s'initialise avec le script "init_relations_AAA_BBB.sql" .
 * 
 * @author Thierry Badard, Arnaud Braun & Sébastien Mustière
 * @version 1.0
 * 
 */


public class TestRelationsBiPersistantes {
    
    private static Geodatabase db = new GeodatabaseOjbOracle();

    public static void main (String args[]) {
        System.out.println("DEBUT DES TESTS");
        test_bi11();
        test_bi1n();
        test_binn();
        charge();
    }


    /** Teste la relation 1-1 bi-directionnelle */
    public static void test_bi11() {
        
        db.begin();
        
        System.out.println("Creation des objets AAA, BBB");
        AAA a1 = new AAA(); a1.setNom("a1");
        AAA a2 = new AAA(); a2.setNom("a2");
        AAA a3 = new AAA(); a3.setNom("a3");
        BBB b1 = new BBB(); b1.setNom("b1");
        BBB b2 = new BBB(); b2.setNom("b2");
        BBB b3 = new BBB(); b3.setNom("b3");
        
        db.makePersistent(a1);
        db.makePersistent(a2);
        db.makePersistent(a3);
        db.makePersistent(b1);
        db.makePersistent(b2);
        db.makePersistent(b3);
        
        System.out.println("");
        System.out.println("TEST RELATION 1-1 BIDIRECTIONNELLE");
        System.out.println("objet BBB en relation avec a1 (null) : "+a1.getObjetBBB_bi11());
        System.out.println("objet BBB en relation avec a2 (null) : "+a2.getObjetBBB_bi11());
        System.out.println("objet AAA en relation avec b1 (null) : "+b1.getObjetAAA_bi11());
        System.out.println("objet AAA en relation avec b2 (null) : "+b2.getObjetAAA_bi11());
        System.out.println("--");
        System.out.println("instanciation sur a1 de a1 R b1 ");
        a1.setObjetBBB_bi11(b1);
        System.out.println("objet BBB en relation avec a1 (b1) : "+a1.getObjetBBB_bi11().getNom());
        System.out.println("objet BBB en relation avec a2 (null) : "+a2.getObjetBBB_bi11());
        System.out.println("objet AAA en relation avec b1 (a1) : "+b1.getObjetAAA_bi11().getNom());
        System.out.println("objet AAA en relation avec b2 (null) : "+b2.getObjetAAA_bi11());
        /* decommenter si on veut consulter l'etat de la base ici */
//         db.commit();
//         System.exit(0);
        
        System.out.println("--");
        System.out.println("instanciation sur a1 de a1 R b2 ");
        a1.setObjetBBB_bi11(b2);
        System.out.println("objet BBB en relation avec a1 (b2) : "+a1.getObjetBBB_bi11().getNom());
        System.out.println("objet BBB en relation avec a2 (null) : "+a2.getObjetBBB_bi11());
        System.out.println("objet AAA en relation avec b1 (null) : "+b1.getObjetAAA_bi11());
        System.out.println("objet AAA en relation avec b2 (a1) : "+b2.getObjetAAA_bi11().getNom());
        /* decommenter si on veut consulter l'etat de la base ici */
//         db.commit();
//         System.exit(0);
        
        System.out.println("--");
        System.out.println("instanciation sur a1 de a1 R b2 (2eme fois)");
        a1.setObjetBBB_bi11(b2);
        System.out.println("objet BBB en relation avec a1 (b2) : "+a1.getObjetBBB_bi11().getNom());
        System.out.println("objet BBB en relation avec a2 (null) : "+a2.getObjetBBB_bi11());
        System.out.println("objet AAA en relation avec b1 (null) : "+b1.getObjetAAA_bi11());
        System.out.println("objet AAA en relation avec b2 (a1) : "+b2.getObjetAAA_bi11().getNom());
        /* decommenter si on veut consulter l'etat de la base ici */
//         db.commit();
//         System.exit(0);        
        
        System.out.println("--");
        System.out.println("instanciation sur b2 de a2 R b2");
        b2.setObjetAAA_bi11(a2);
        System.out.println("objet BBB en relation avec a1 (null) : "+a1.getObjetBBB_bi11());
        System.out.println("objet BBB en relation avec a2 (b2) : "+a2.getObjetBBB_bi11().getNom());
        System.out.println("objet AAA en relation avec b1 (null) : "+b1.getObjetAAA_bi11());
        System.out.println("objet AAA en relation avec b2 (a2) : "+b2.getObjetAAA_bi11().getNom());
        /* decommenter si on veut consulter l'etat de la base ici */
//         db.commit();
//         System.exit(0);        
        
        System.out.println("--");
        System.out.println("instanciation sur b1 de a2 R b1");
        b1.setObjetAAA_bi11(a2);
        System.out.println("objet BBB en relation avec a1 (null) : "+a1.getObjetBBB_bi11());
        System.out.println("objet BBB en relation avec a2 (b1) : "+a2.getObjetBBB_bi11().getNom());
        System.out.println("objet AAA en relation avec b1 (a2) : "+b1.getObjetAAA_bi11().getNom());
        System.out.println("objet AAA en relation avec b2 (null) : "+b2.getObjetAAA_bi11());
        /* decommenter si on veut consulter l'etat de la base ici */
//         db.commit();
//         System.exit(0);
        
        /*System.out.println("--");
        System.out.println("vidage des relations sur b1");
        b1.setObjetAAA_bi11(null);
        System.out.println("objet BBB en relation avec a1 (null) : "+a1.getObjetBBB_bi11());
        System.out.println("objet BBB en relation avec a2 (null) : "+a2.getObjetBBB_bi11());
        System.out.println("objet AAA en relation avec b1 (null) : "+b1.getObjetAAA_bi11());
        System.out.println("objet AAA en relation avec b2 (null) : "+b2.getObjetAAA_bi11());*/
        /* decommenter si on veut consulter l'etat de la base ici */
        db.commit();
        
    }
    

    /** Teste la relation 1-n bi-directionnelle */    
    public static void test_bi1n() {
        
        db.begin();
        
        System.out.println("Creation des objets AAA, BBB");
        AAA a1 = new AAA(); a1.setNom("a1");
        AAA a2 = new AAA(); a2.setNom("a2");
        AAA a3 = new AAA(); a3.setNom("a3");
        BBB b1 = new BBB(); b1.setNom("b1");
        BBB b2 = new BBB(); b2.setNom("b2");
        BBB b3 = new BBB(); b3.setNom("b3");
        List L;
        
        db.makePersistent(a1);
        db.makePersistent(a2);
        db.makePersistent(a3);
        db.makePersistent(b1);
        db.makePersistent(b2);
        db.makePersistent(b3);
                
        System.out.println("");
        System.out.println("TEST RELATION 1-N BIDIRECTIONNELLE");
        System.out.println("objet BBB en relation avec a1 (vide) : ");
        affiche(a1.getListe_objetsBBB_bi1N());
        System.out.println("objet BBB en relation avec a2 (vide) : ");
        affiche(a2.getListe_objetsBBB_bi1N());
        System.out.println("objet BBB en relation avec a3 (vide) : ");
        affiche(a3.getListe_objetsBBB_bi1N());
        System.out.println("objet AAA en relation avec b1 (null) : "+b1.getObjetAAA_bi1N());
        System.out.println("objet AAA en relation avec b2 (null) : "+b2.getObjetAAA_bi1N());
        System.out.println("objet AAA en relation avec b3 (null) : "+b3.getObjetAAA_bi1N());
        /* decommenter si on veut consulter l'etat de la base ici */
//         db.commit();
//         System.exit(0);                

        System.out.println("--");
        System.out.println("ajout sur a1, de b1 aux objets en relation avec a1");
        System.out.println("set sur a2, de liste 'b2, b3' comme objets en relation avec a2");
        a1.addObjetBBB_bi1N(b1);
        L = new ArrayList();
        L.add(b2);L.add(b3);
        a2.setListe_objetsBBB_bi1N(L);
        System.out.println("objet BBB en relation avec a1 (b1) : ");
        affiche(a1.getListe_objetsBBB_bi1N());
        System.out.println("objet BBB en relation avec a2 (b2, b3) : ");
        affiche(a2.getListe_objetsBBB_bi1N());
        System.out.println("objet BBB en relation avec a3 (vide) : ");
        affiche(a3.getListe_objetsBBB_bi1N());
        System.out.println("objet AAA en relation avec b1 (a1) : "+b1.getObjetAAA_bi1N().getNom());
        System.out.println("objet AAA en relation avec b2 (a2) : "+b2.getObjetAAA_bi1N().getNom());
        System.out.println("objet AAA en relation avec b3 (a2) : "+b3.getObjetAAA_bi1N().getNom());
        /* decommenter si on veut consulter l'etat de la base ici */
//         db.commit();
//         System.exit(0);                

        System.out.println("--");
        System.out.println("set sur b2 de b2 en relation avec a1");
        b2.setObjetAAA_bi1N(a1);
        System.out.println("objet BBB en relation avec a1 (b1, b2) : ");
        affiche(a1.getListe_objetsBBB_bi1N());
        System.out.println("objet BBB en relation avec a2 (b3) : ");
        affiche(a2.getListe_objetsBBB_bi1N());
        System.out.println("objet BBB en relation avec a3 (vide) : ");
        affiche(a3.getListe_objetsBBB_bi1N());
        System.out.println("objet AAA en relation avec b1 (a1) : "+b1.getObjetAAA_bi1N().getNom());
        System.out.println("objet AAA en relation avec b2 (a1) : "+b2.getObjetAAA_bi1N().getNom());
        System.out.println("objet AAA en relation avec b3 (a2) : "+b3.getObjetAAA_bi1N().getNom());
        /* decommenter si on veut consulter l'etat de la base ici */
//         db.commit();
//         System.exit(0);                

        System.out.println("--");
        System.out.println("vidage sur a1 des objets en relation");
        System.out.println("destruction de la relation sur b3");
        a1.emptyListe_objetsBBB_bi1N();
        b3.setObjetAAA_bi1N(null);
        System.out.println("objet BBB en relation avec a1 (vide) : ");
        affiche(a1.getListe_objetsBBB_bi1N());
        System.out.println("objet BBB en relation avec a2 (vide) : ");
        affiche(a2.getListe_objetsBBB_bi1N());
        System.out.println("objet BBB en relation avec a3 (vide) : ");
        affiche(a3.getListe_objetsBBB_bi1N());
        System.out.println("objet AAA en relation avec b1 (null) : "+b1.getObjetAAA_bi1N());
        System.out.println("objet AAA en relation avec b2 (null) : "+b2.getObjetAAA_bi1N());
        System.out.println("objet AAA en relation avec b3 (null) : "+b3.getObjetAAA_bi1N());
        /* decommenter si on veut consulter l'etat de la base ici */
//         db.commit();
//         System.exit(0);                

        System.out.println("--");
        System.out.println("set sur a2, de liste 'b2, b3' comme objets en relation avec a2");
        System.out.println("set sur b2 de la relation avec a2 (redondant)");
        b2.setObjetAAA_bi1N(a2);
        L = new ArrayList();
        L.add(b2);L.add(b3);
        a2.setListe_objetsBBB_bi1N(L);
        System.out.println("objet BBB en relation avec a1 (vide) : ");
        affiche(a1.getListe_objetsBBB_bi1N());
        System.out.println("objet BBB en relation avec a2 (b2, b3) : ");
        affiche(a2.getListe_objetsBBB_bi1N());
        System.out.println("objet BBB en relation avec a3 (vide) : ");
        affiche(a3.getListe_objetsBBB_bi1N());
        System.out.println("objet AAA en relation avec b1 (null) : "+b1.getObjetAAA_bi1N());
        System.out.println("objet AAA en relation avec b2 (a2) : "+b2.getObjetAAA_bi1N().getNom());
        System.out.println("objet AAA en relation avec b3 (a2) : "+b3.getObjetAAA_bi1N().getNom());
        /* decommenter si on veut consulter l'etat de la base ici */
//         db.commit();
//         System.exit(0);                
        
        System.out.println("--");
        System.out.println("set sur a2, de liste 'b1, b3' comme objets en relation avec a2");
        L = new ArrayList();
        L.add(b1);L.add(b3);
        a2.setListe_objetsBBB_bi1N(L);
        System.out.println("objet BBB en relation avec a1 (vide) : ");
        affiche(a1.getListe_objetsBBB_bi1N());
        System.out.println("objet BBB en relation avec a2 (b1, b3) : ");
        affiche(a2.getListe_objetsBBB_bi1N());
        System.out.println("objet BBB en relation avec a3 (vide) : ");
        affiche(a3.getListe_objetsBBB_bi1N());
        System.out.println("objet AAA en relation avec b1 (a2) : "+b1.getObjetAAA_bi1N().getNom());
        System.out.println("objet AAA en relation avec b2 (null) : "+b2.getObjetAAA_bi1N());
        System.out.println("objet AAA en relation avec b3 (a2) : "+b3.getObjetAAA_bi1N().getNom());
        /* decommenter si on veut consulter l'etat de la base ici */
//         db.commit();
//         System.exit(0);                
        
        System.out.println("--");
        System.out.println("ajout sur a2, de liste b2 comme objets en relation avec a2");
        a2.addObjetBBB_bi1N(b2);
        System.out.println("objet BBB en relation avec a1 (vide) : ");
        affiche(a1.getListe_objetsBBB_bi1N());
        System.out.println("objet BBB en relation avec a2 (b1, b3, b2) : ");
        affiche(a2.getListe_objetsBBB_bi1N());
        System.out.println("objet BBB en relation avec a3 (vide) : ");
        affiche(a3.getListe_objetsBBB_bi1N());
        System.out.println("objet AAA en relation avec b1 (a2) : "+b1.getObjetAAA_bi1N().getNom());
        System.out.println("objet AAA en relation avec b2 (a2) : "+b2.getObjetAAA_bi1N().getNom());
        System.out.println("objet AAA en relation avec b3 (a2) : "+b3.getObjetAAA_bi1N().getNom());
        /* decommenter si on veut consulter l'etat de la base ici */
         db.commit();

    }
    
    
    /** Teste la relation n-n bi-directionnelle */    
    public static void test_binn() {
        
        db.begin();
        
        System.out.println("Creation des objets AAA, BBB");
        AAA a1 = new AAA(); a1.setNom("a1");
        AAA a2 = new AAA(); a2.setNom("a2");
        AAA a3 = new AAA(); a3.setNom("a3");
        BBB b1 = new BBB(); b1.setNom("b1");
        BBB b2 = new BBB(); b2.setNom("b2");
        BBB b3 = new BBB(); b3.setNom("b3");
        List L;
        
        db.makePersistent(a1);
        db.makePersistent(a2);
        db.makePersistent(a3);
        db.makePersistent(b1);
        db.makePersistent(b2);
        db.makePersistent(b3);        
        
        System.out.println("TEST RELATION N-M BIDIRECTIONNELLE");
        System.out.println("set sur a1, de liste b1 b2 comme objets en relation avec a2");
        System.out.println("add sur a2, de b2 comme objets en relation avec a2");
        System.out.println("add sur a3, de b3 comme objets en relation avec a3");
        L=new ArrayList(); L.add(b1); L.add(b2);
        a1.setListe_objetsBBB_biNM(L);
        a2.addObjetBBB_biNM(b2);
        a3.addObjetBBB_biNM(b3);
        System.out.println("objet BBB en relation avec a1 (b1, b2) : ");
        affiche(a1.getListe_objetsBBB_biNM());
        System.out.println("objet BBB en relation avec a2 (b2) : ");
        affiche(a2.getListe_objetsBBB_biNM());
        System.out.println("objet BBB en relation avec a3 (b3) : ");
        affiche(a3.getListe_objetsBBB_biNM());
        System.out.println("objet AAA en relation avec b1 (a1) : ");
        affiche(b1.getListe_objetsAAA_biNM());
        System.out.println("objet AAA en relation avec b2 (a1, a2) : ");
        affiche(b2.getListe_objetsAAA_biNM());
        System.out.println("objet AAA en relation avec b3 (a3) : ");
        affiche(b3.getListe_objetsAAA_biNM());
        /* decommenter si on veut consulter l'etat de la base ici */
//         db.commit();
//         System.exit(0);                        
        
        System.out.println("");
        System.out.println("-- (idem avant)");
        System.out.println("set sur a1, de liste b1 b2 comme objets en relation avec a2");
        System.out.println("add sur a2, de b2 comme objets en relation avec a2");
        System.out.println("add sur a3, de b3 comme objets en relation avec a3");
        L=new ArrayList(); L.add(b1); L.add(b2);
        a1.setListe_objetsBBB_biNM(L);
        a2.addObjetBBB_biNM(b2);
        a3.addObjetBBB_biNM(b3);
        System.out.println("objet BBB en relation avec a1 (b1, b2) : ");
        affiche(a1.getListe_objetsBBB_biNM());
        System.out.println("objet BBB en relation avec a2 (b2, b2) : ");
        affiche(a2.getListe_objetsBBB_biNM());
        System.out.println("objet BBB en relation avec a3 (b3, b3) : ");
        affiche(a3.getListe_objetsBBB_biNM());
        System.out.println("objet AAA en relation avec b1 (a1) : ");
        affiche(b1.getListe_objetsAAA_biNM());
        System.out.println("objet AAA en relation avec b2 (a1, a2, a2) : ");
        affiche(b2.getListe_objetsAAA_biNM());
        System.out.println("objet AAA en relation avec b3 (a3, a3) : ");
        affiche(b3.getListe_objetsAAA_biNM());
        /* decommenter si on veut consulter l'etat de la base ici */
//         db.commit();
//         System.exit(0);                        

        System.out.println("");
        System.out.println("-- ");
        System.out.println("remove sur b1 de a1");
        System.out.println("remove sur b2 de a2");
        System.out.println("vidage de a3");
        L=new ArrayList(); L.add(b1); L.add(b2);
        b1.removeObjetAAA_biNM(a1);
        b2.removeObjetAAA_biNM(a2);
        a3.emptyListe_objetsBBB_biNM ();
        System.out.println("objet BBB en relation avec a1 (b2) : ");
        affiche(a1.getListe_objetsBBB_biNM());
        System.out.println("objet BBB en relation avec a2 (b2) : ");
        affiche(a2.getListe_objetsBBB_biNM());
        System.out.println("objet BBB en relation avec a3 (vide) : ");
        affiche(a3.getListe_objetsBBB_biNM());
        System.out.println("objet AAA en relation avec b1 (vide) : ");
        affiche(b1.getListe_objetsAAA_biNM());
        System.out.println("objet AAA en relation avec b2 (a1, a2) : ");
        affiche(b2.getListe_objetsAAA_biNM());
        System.out.println("objet AAA en relation avec b3 (vide) : ");
        affiche(b3.getListe_objetsAAA_biNM());
        /* decommenter si on veut consulter l'etat de la base ici */
         db.commit();
    }
    
       
    /** Chargement des objets */
    public static void charge() {
        db.begin();
        List allBBB = db.loadAll(BBB.class);
        List allAAA = db.loadAll(AAA.class);        
        System.out.println("-- AAA ---");
        Iterator itA = allAAA.iterator();
        while (itA.hasNext()) {
            AAA a = (AAA) itA.next();
            System.out.println("##  objet - id = "+a.getId()+" - nom = "+a.getNom());
            System.out.print("        lien 1-1 : ");
            if (a.getObjetBBB_bi11() != null) 
                System.out.print("id = "+a.getObjetBBB_bi11().getId()+" - nom = "+a.getObjetBBB_bi11().getNom());
            System.out.println();                
            System.out.println("        lien 1-n : ");  
            affiche(a.getListe_objetsBBB_bi1N());
            System.out.println("        lien n-m : ");  
            affiche(a.getListe_objetsBBB_biNM());            
        }
        System.out.println();
        System.out.println("-- BBB ---");
        Iterator itB = allBBB.iterator();
        while (itB.hasNext()) {
            BBB b = (BBB) itB.next();
            System.out.println("##  objet - id = "+b.getId()+" - nom = "+b.getNom());
            System.out.print("        lien 1-1 : ");
            if (b.getObjetAAA_bi11() != null) 
                System.out.print("id = "+b.getObjetAAA_bi11().getId()+" - nom = "+b.getObjetAAA_bi11().getNom());
            System.out.println();                
            System.out.print("        lien 1-n : ");  
            if (b.getObjetAAA_bi1N() != null) 
                System.out.print("id = "+b.getObjetAAA_bi1N().getId()+" - nom = "+b.getObjetAAA_bi1N().getNom());
            System.out.println();                
            System.out.println("        lien n-m : ");  
            affiche(b.getListe_objetsAAA_biNM());            
        }        
    }
    
    
    public static void affiche(List L) {
        Iterator it = L.iterator();
        while ( it.hasNext() ) {
            ClasseMere o = (ClasseMere)it.next();
            if ( o == null ) System.out.println("   - null");
            else System.out.println("   - "+o.getNom());
        }
    }
    
   
}
