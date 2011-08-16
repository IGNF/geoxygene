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

package fr.ign.cogit.geoxygene.example.relations;

// Imports necessaires aux relations 1-n et n-m
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classe exemple pour les relations, mono ou bidirectionnelles, avec la classe
 * AAA.
 * 
 * @author Thierry Badard, Arnaud Braun & Sébastien Mustière
 * @version 1.0
 * 
 */

public class BBB extends ClasseMere {

  // ////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////
  // / /////
  // / R E L A T I O N S /////
  // / B I D I R E C T I O N N E L L E S /////
  // / /////
  // ////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////

  // ////////////////////////////////////////////////////////////////////////
  // relation BIDIRECTIONNELLE 1-1 //////////////////////
  // ////////////////////////////////////////////////////////////////////////

  /**
   * Lien bidirectionnel 1-1 vers BBB. 1 objet AAA est en relation avec 1 objet
   * BBB au plus. 1 objet BBB est en relation avec 1 objet AAA au plus.
   * 
   * Les méthodes get et set sont utiles pour assurer la bidirection.
   * 
   * NB : si il n'y a pas d'objet en relation, getObjet renvoie null. Pour
   * casser une relation: faire setObjet(null);
   */
  private AAA objetAAA_bi11;

  /** Récupère l'objet en relation */

  public AAA getObjetAAA_bi11() {
    return this.objetAAA_bi11;
  }

  /** Définit l'objet en relation */
  public void setObjetAAA_bi11(AAA O) {
    AAA old = this.objetAAA_bi11;
    this.objetAAA_bi11 = O;
    if (old != null) {
      old.setObjetBBB_bi11(null);
    }
    if (O != null) {
      if (O.getObjetBBB_bi11() != this) {
        O.setObjetBBB_bi11(this);
      }
    }
  }

  // ////////////////////////////////////////////////////////////////////////
  // relation BIDIRECTIONNELLE 1-n //////////////////////
  // ////////////////////////////////////////////////////////////////////////

  /**
   * Lien bidirectionnel 1-n vers BBB. 1 objet AAA est en relation avec n objets
   * BBB (n pouvant etre nul). 1 objet BBB est en relation avec 1 objet AAA au
   * plus.
   * 
   * Les méthodes get et set sont utiles pour assurer la bidirection.
   * 
   * NB : si il n'y a pas d'objet en relation, getObjet renvoie null. Pour
   * casser une relation: faire setObjet(null);
   */
  private AAA objetAAA_bi1N;

  /** Récupère l'objet en relation. */

  public AAA getObjetAAA_bi1N() {
    return this.objetAAA_bi1N;
  }

  /** Définit l'objet en relation, et met à jour la relation inverse. */
  public void setObjetAAA_bi1N(AAA O) {
    AAA old = this.objetAAA_bi1N;
    this.objetAAA_bi1N = O;
    if (old != null) {
      old.getListe_objetsBBB_bi1N().remove(this);
    }
    if (O != null) {
      if (!O.getListe_objetsBBB_bi1N().contains(this)) {
        O.getListe_objetsBBB_bi1N().add(this);
      }
    }
  }

  // ////////////////////////////////////////////////////////////////////////
  // relation BIDIRECTIONNELLE n-m /////////////////////
  // ////////////////////////////////////////////////////////////////////////

  /**
   * Lien bidirectionnel n-m vers BBB. 1 objet AAA est en relation avec n objets
   * BBB (n pouvant etre nul). 1 objet BBB est en relation avec m objets AAA (m
   * pouvant etre nul).
   * 
   * NB: Contrairement aux relation 1-n, on autorise ici qu'un objet soit en
   * relation plusieurs fois avec le même objet AAA
   * 
   * Les méthodes get (sans indice) et set sont nécessaires au mapping. Les
   * autres méthodes sont là seulement pour faciliter l'utilisation de la
   * relation. ATTENTION: Pour assurer la bidirection, il faut modifier les
   * listes uniquement avec ces methodes. NB: si il n'y a pas d'objet en
   * relation, la liste est vide mais n'est pas "null". Pour casser toutes les
   * relations, faire setListe(new ArrayList()) ou emptyListe().
   */
  private List<Object> liste_objetsAAA_biNM = new ArrayList<Object>();

  /** Récupère l'objet en relation */
  public List<Object> getListe_objetsAAA_biNM() {
    return this.liste_objetsAAA_biNM;
  }

  /** Définit l'objet en relation, et met à jour la relation inverse. */
  public void setListe_objetsAAA_biNM(List<Object> L) {
    List<Object> old = new ArrayList<Object>(this.liste_objetsAAA_biNM);
    Iterator<Object> it1 = old.iterator();
    while (it1.hasNext()) {
      AAA O = (AAA) it1.next();
      this.liste_objetsAAA_biNM.remove(O);
      O.getListe_objetsBBB_biNM().remove(this);
    }
    Iterator<Object> it2 = L.iterator();
    while (it2.hasNext()) {
      AAA O = (AAA) it2.next();
      this.liste_objetsAAA_biNM.add(O);
      O.getListe_objetsBBB_biNM().add(this);
    }
  }

  /** Récupère le ième élément de la liste des objets en relation. */
  public AAA getObjetAAA_biNM(int i) {
    return (AAA) this.liste_objetsAAA_biNM.get(i);
  }

  /**
   * Ajoute un objet à la liste des objets en relation, et met à jour la
   * relation inverse.
   */
  public void addObjetAAA_biNM(AAA O) {
    if (O == null) {
      return;
    }
    this.liste_objetsAAA_biNM.add(O);
    O.getListe_objetsBBB_biNM().add(this);
  }

  /**
   * Enlève un élément de la liste des objets en relation, et met à jour la
   * relation inverse.
   */
  public void removeObjetAAA_biNM(AAA O) {
    if (O == null) {
      return;
    }
    this.liste_objetsAAA_biNM.remove(O);
    O.getListe_objetsBBB_biNM().remove(this);
  }

  /** Vide la liste des objets en relation, et met à jour la relation inverse. */
  public void emptyListe_objetsAAA_biNM() {
    Iterator<Object> it = this.liste_objetsAAA_biNM.iterator();
    while (it.hasNext()) {
      AAA O = (AAA) it.next();
      O.getListe_objetsBBB_biNM().remove(this);
    }
    this.liste_objetsAAA_biNM.clear();
  }

  // ////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////
  // / /////
  // / LES EXEMPLES DE RELATIONS MONODIRECTIONNELLES /////
  // / SONT CODES SUR AAA UNIQUEMENT /////
  // / /////
  // ////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////

}
