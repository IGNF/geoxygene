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

// Imports necessaires aux relations 1-n et n-m    
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/** 
 * Classe exemple pour les relations mono ou bidirectionnelles, avec la classe BBB.
 * Les relations peuvent etre persistantes ou non (cf. fichier de mapping repository_AAA_BBB.xml)
 * 
 * @author Thierry Badard, Arnaud Braun & Sébastien Mustière
 * @version 1.0
 * 
 */


public class AAA extends ClasseMere {



//////////////////////////////////////////////////////////////////////////    
//////////////////////////////////////////////////////////////////////////    
//////////////////////////////////////////////////////////////////////////    
///                                                                  /////
///                        R E L A T I O N S                         /////
///                 B I D I R E C T I O N N E L L E S                /////
///                                                                  /////
//////////////////////////////////////////////////////////////////////////    
//////////////////////////////////////////////////////////////////////////    
//////////////////////////////////////////////////////////////////////////    

//////////////////////////////////////////////////////////////////////////    
//      relation     BIDIRECTIONNELLE     1-1     ////////////////////////
//////////////////////////////////////////////////////////////////////////    
    
    /** Lien bidirectionnel  1-1 vers BBB. 
     *  1 objet AAA est en relation avec 1 objet BBB au plus.             
     *  1 objet BBB est en relation avec 1 objet AAA au plus.
     *             
     *  Les méthodes get et set sont utiles pour assurer la bidirection.
     *
     *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null. 
     *  Pour casser une relation: faire setObjet(null);
     */
    private BBB objetBBB_bi11;

    /** Récupère l'objet en relation */
    public BBB getObjetBBB_bi11() {return objetBBB_bi11;}
    
    /** Définit l'objet en relation */
    public void setObjetBBB_bi11(BBB O) {
        BBB old = objetBBB_bi11;
        objetBBB_bi11 = O;
        if ( old != null ) old.setObjetAAA_bi11(null);
        if ( O != null ) {
            if ( O.getObjetAAA_bi11() != this ) O.setObjetAAA_bi11(this);
        }
    }


//////////////////////////////////////////////////////////////////////////    
//      relation     BIDIRECTIONNELLE     1-n     ////////////////////////
//////////////////////////////////////////////////////////////////////////    

    /** Lien bidirectionnel 1-n vers BBB. 
     *  1 objet AAA est en relation avec n objets BBB (n pouvant etre nul).             
     *  1 objet BBB est en relation avec 1 objet AAA au plus.
     *
     *  NB: un objet AAA ne doit pas être en relation plusieurs fois avec le même objet BBB :
     *  il est impossible de bien gérer des relations 1-n bidirectionnelles avec doublons.
     *
     *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec 
     *  les methodes fournies.
     * 
     *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
     *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
     */
    private List liste_objetsBBB_bi1N = new ArrayList();   

    /** Récupère la liste des objets en relation. */
    public List getListe_objetsBBB_bi1N() {return liste_objetsBBB_bi1N ; } 
    
    /** Définit la liste des objets en relation, et met à jour la relation inverse. */
    public void setListe_objetsBBB_bi1N (List L) {
        List old = new ArrayList(liste_objetsBBB_bi1N);
        Iterator it1 = old.iterator();
        while ( it1.hasNext() ) {
            BBB O = (BBB)it1.next();
            O.setObjetAAA_bi1N(null);
        }
        Iterator it2 = L.iterator();
        while ( it2.hasNext() ) {
            BBB O = (BBB)it2.next();
            O.setObjetAAA_bi1N(this);
        }
    }
    
    /** Récupère le ième élément de la liste des objets en relation. */
    public BBB getObjetBBB_bi1N(int i) {return (BBB)liste_objetsBBB_bi1N.get(i) ; }  
    
    /** Ajoute un objet à la liste des objets en relation, et met à jour la relation inverse. */
    public void addObjetBBB_bi1N (BBB O) {
        if ( O == null ) return;
        liste_objetsBBB_bi1N.add(O) ;
        O.setObjetAAA_bi1N(this) ;
    }
    
    /** Enlève un élément de la liste des objets en relation, et met à jour la relation inverse. */
    public void removeObjetBBB_bi1N (BBB O) {
        if ( O == null ) return;
        liste_objetsBBB_bi1N.remove(O) ; 
        O.setObjetAAA_bi1N(null);
    }
    
    /** Vide la liste des objets en relation, et met à jour la relation inverse. */
    public void emptyListe_objetsBBB_bi1N () {
        List old = new ArrayList(liste_objetsBBB_bi1N);
        Iterator it = old.iterator(); 
        while ( it.hasNext() ) {
            BBB O = (BBB)it.next();
            O.setObjetAAA_bi1N(null);
        }
    }
   
    
//////////////////////////////////////////////////////////////////////////    
//      relation     BIDIRECTIONNELLE     n-m        /////////////////////
//////////////////////////////////////////////////////////////////////////    

    /** Lien bidirectionnel n-m vers BBB. 
     *  1 objet AAA est en relation avec n objets BBB (n pouvant etre nul).             
     *  1 objet BBB est en relation avec m objets AAA (m pouvant etre nul).
     *
     *  NB: Contrairement aux relation 1-n, on autorise ici qu'un objet soit en relation 
     *  plusieurs fois avec le même objet BBB 
     *
     *  Les méthodes get (sans indice) et set sont nécessaires au mapping. 
     *  Les autres méthodes sont là seulement pour faciliter l'utilisation de la relation.
     *  ATTENTION: Pour assurer la bidirection, il faut modifier les listes uniquement avec ces methodes. 
     *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
     *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
     */
    private List liste_objetsBBB_biNM = new ArrayList();
    
    /** Récupère l'objet en relation */
    public List getListe_objetsBBB_biNM() {return liste_objetsBBB_biNM ; }   
     
    /** Définit l'objet en relation, et met à jour la relation inverse. */
    public void setListe_objetsBBB_biNM (List L) {
        List old = new ArrayList(liste_objetsBBB_biNM);
        Iterator it1 = old.iterator();
        while ( it1.hasNext() ) {
            BBB O = (BBB)it1.next();
            liste_objetsBBB_biNM.remove(O);
            O.getListe_objetsAAA_biNM().remove(this);
        }
        Iterator it2 = L.iterator();
        while ( it2.hasNext() ) {
            BBB O = (BBB)it2.next();
            liste_objetsBBB_biNM.add(O);
            O.getListe_objetsAAA_biNM().add(this);
        }
    }
    
    /** Récupère le ième élément de la liste des objets en relation. */
    public BBB getObjetBBB_biNM(int i) {return (BBB)liste_objetsBBB_biNM.get(i) ; }    
    
    /** Ajoute un objet à la liste des objets en relation, et met à jour la relation inverse. */
    public void addObjetBBB_biNM (BBB O) {
        if ( O == null ) return;
        liste_objetsBBB_biNM.add(O) ;
        O.getListe_objetsAAA_biNM().add(this);
    }
    
    /** Enlève un élément de la liste des objets en relation, et met à jour la relation inverse. */
    public void removeObjetBBB_biNM (BBB O) {
        if ( O == null ) return;
        liste_objetsBBB_biNM.remove(O) ; 
        O.getListe_objetsAAA_biNM().remove(this);
    }
    
    /** Vide la liste des objets en relation, et met à jour la relation inverse. */
    public void emptyListe_objetsBBB_biNM () {
        Iterator it = liste_objetsBBB_biNM.iterator(); 
        while ( it.hasNext() ) {
            BBB O = (BBB)it.next();
            O.getListe_objetsAAA_biNM().remove(this);
        }
        liste_objetsBBB_biNM.clear();
    }

    
    
//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
///                                                                  /////
///                        R E L A T I O N S                         /////
///               M O N O D I R E C T I O N N E L L E S              /////
///                                                                  /////
//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
    
    
//////////////////////////////////////////////////////////////////////////    
//    relation   MONODIRECTIONNELLE     VERS 1 OBJET     /////////////////
//////////////////////////////////////////////////////////////////////////    

    /** Lien monodirectionnel vers 0 ou 1 objet BBB. 
     *
     *  NB : un objet BBB peut pointer vers 1 ou N objets AAA, cela ne change rien
     *
     *  NB : si il n'y a pas d'objet en relation, getObjet renvoie null. 
     *  Pour casser une relation: faire setObjet(null);
     */
    private BBB objetBBB_mono11;

    public BBB getObjetBBB_mono11() {return objetBBB_mono11;}
    
    public void setObjetBBB_mono11(BBB O) {objetBBB_mono11 = O;}
    

//////////////////////////////////////////////////////////////////////////    
//   relation   MONODIRECTIONNELLE  1-N  ////////////////////////////////
//////////////////////////////////////////////////////////////////////////    

    /** Lien monodirectionnel vers n objets BBB (n pouvant etre nul) 1-n. 
     *
     *  Les relations 1-N ou N-M sont codees identiquement en Java.
     *  Mais cela change le mapping : en cas de relation 1-N, c'est une cle etrangere sur BBB.
     *  En cas de relation N-M, c'est une table de liaison externe.
     *
     *  Les méthodes sont là seulement pour faciliter l'utilisation de la relation.
     *  Elle sont optionnelles et toutes les manipulations peuvent être faites directement
     *  à partir des get et set. Elles servent néanmoins: 
     *  1/ à encapsuler les méthodes de liste en harmonisant avec les relations 
     *      bidirectionnelles et/ou persistantes; 
     *  2/ à rendre le code plus solide en vérifiant la classe des objets ajoutés/enlevés dès la compilation.
     *
     *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
     *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
     */
    private List liste_objetsBBB_mono1N = new ArrayList();   

    public List getListe_objetsBBB_mono1N() {return liste_objetsBBB_mono1N ; } 
    
    public void setListe_objetsBBB_mono1N (List L) {liste_objetsBBB_mono1N = L; }
    
    public BBB getObjetBBB_mono1N(int i) {return (BBB)liste_objetsBBB_mono1N.get(i) ; }  
    
    public void addObjetBBB_mono1N (BBB O) {
        if ( O == null ) return;
        liste_objetsBBB_mono1N.add(O) ; 
    }
    
    public void removeObjetBBB_mono1N (BBB O) {
        if ( O == null ) return;
        liste_objetsBBB_mono1N.remove(O) ; 
    }
    
    public void emptyListe_objetsBBB_mono1N () {liste_objetsBBB_mono1N.clear();}


//////////////////////////////////////////////////////////////////////////    
//    relation   MONODIRECTIONNELLE  N-M  ////////////////////////////////
//////////////////////////////////////////////////////////////////////////    

        /** Lien monodirectionnel vers n objets BBB (n pouvant etre nul) n-m. 
         *
         *  Les relations 1-N ou N-M sont codees identiquement en Java.
         *  Mais cela change le mapping : en cas de relation 1-N, c'est une cle etrangere sur BBB.
         *  En cas de relation N-M, c'est une table de liaison externe.
         *
         *  Les méthodes sont là seulement pour faciliter l'utilisation de la relation.
         *  Elle sont optionnelles et toutes les manipulations peuvent être faites directement
         *  à partir des get et set. Elles servent néanmoins: 
         *  1/ à encapsuler les méthodes de liste en harmonisant avec les relations 
         *      bidirectionnelles et/ou persistantes; 
         *  2/ à rendre le code plus solide en vérifiant la classe des objets ajoutés/enlevés dès la compilation.
         *
         *  NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas "null".
         *  Pour casser toutes les relations, faire setListe(new ArrayList()) ou emptyListe().
         */
        private List liste_objetsBBB_monoNM = new ArrayList();   

        public List getListe_objetsBBB_monoNM() {return liste_objetsBBB_monoNM ; } 
    
        public void setListe_objetsBBB_monoNM (List L) {liste_objetsBBB_monoNM = L; }
    
        public BBB getObjetBBB_monoNM(int i) {return (BBB)liste_objetsBBB_monoNM.get(i) ; }  
    
        public void addObjetBBB_monoNM (BBB O) {
            if ( O == null ) return;
            liste_objetsBBB_monoNM.add(O) ; 
        }
    
        public void removeObjetBBB_monoNM (BBB O) {
            if ( O == null ) return;
            liste_objetsBBB_monoNM.remove(O) ; 
        }
    
        public void emptyListe_objetsBBB_monoNM () {liste_objetsBBB_monoNM.clear();}
       
}
