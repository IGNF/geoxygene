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

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.LienReseaux;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.ElementCarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;

/**
 * Noeud du reseau à apparier.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

public class NoeudApp extends Noeud {

  /**
   * Rayon maximal sur le tarrain de l'objet correpondant au noeud (rayon de
   * recherche pour l'appariement).
   */
  private double taille = 0.0;

  public double getTaille() {
    return this.taille;
  }

  public void setTaille(double taille) {
    this.taille = taille;
  }

  /** Evaluation du résultat de l'appariement sur la face. */
  private String resultatAppariement;

  public String getResultatAppariement() {
    return this.resultatAppariement;
  }

  public void setResultatAppariement(String resultat) {
    this.resultatAppariement = resultat;
  }

  /**
   * Liens qui référencent les objets auquel l'arc est apparié dans un autre
   * réseau.
   */
  private List<LienReseaux> liens = new ArrayList<LienReseaux>(0);

  public List<LienReseaux> getLiens() {
    return this.liens;
  }

  public void setLiens(List<LienReseaux> liens) {
    this.liens = liens;
  }

  public void addLiens(LienReseaux liensReseaux) {
    this.liens.add(liensReseaux);
  }

  // //////////////////////////////////////////////////
  // POUR MANIPULER LES LIENS
  // //////////////////////////////////////////////////

  /** Renvoie les liens de l'objet qui appartiennent à la liste liensPertinents */
  public List<LienReseaux> getLiens(List<Lien> liensPertinents) {
    List<LienReseaux> listeTmp = new ArrayList<LienReseaux>(this.getLiens());
    listeTmp.retainAll(liensPertinents);
    return listeTmp;
  }

  /**
   * Renvoie les liens concernant l'objet et portant le nom passé en paramètre.
   * NB: renvoie une liste vide (et non "Null") si il n'y a pas de tels liens.
   */
  public List<LienReseaux> retrouveLiens(String nom) {
    List<LienReseaux> liensReseaux = new ArrayList<LienReseaux>(0);
    List<LienReseaux> tousLiens = this.getLiens();
    for (LienReseaux lien : tousLiens) {
      if (lien.getNom().compareToIgnoreCase(nom) == 0) {
        liensReseaux.add(lien);
      }
    }
    return liensReseaux;
  }

  /**
   * Noeuds reliés à this par l'appariement passé en paramètre. La liste
   * contient des NoeudComp.
   */
  public List<Noeud> noeudsCompEnCorrespondance(EnsembleDeLiens ensembleDeLiens) {
    List<Noeud> noeuds = new ArrayList<Noeud>(0);
    List<LienReseaux> liensOK = new ArrayList<LienReseaux>(this.getLiens());
    liensOK.retainAll(ensembleDeLiens.getElements());
    for (LienReseaux lien : liensOK) {
      noeuds.addAll(lien.getNoeuds2());
    }
    return noeuds;
  }

  /**
   * Groupes reliés à this par l'appariement passé en paramètre La liste
   * contient des GroupeComp.
   */
  public List<Groupe> groupesCompEnCorrespondance(
      EnsembleDeLiens ensembleDeLiens) {
    List<Groupe> groupes = new ArrayList<Groupe>(0);
    List<LienReseaux> liensOK = new ArrayList<LienReseaux>(this.getLiens());
    liensOK.retainAll(ensembleDeLiens.getElements());
    for (LienReseaux lien : liensOK) {
      groupes.addAll(lien.getGroupes2());
    }
    return groupes;
  }

  // /////////////////////////////////////////////////
  // DIVERS
  // /////////////////////////////////////////////////

  /** Noeud d'un groupe le plus proche d'un noeud donné */
  public NoeudApp noeudLePlusProche(Groupe groupe) {
    if (groupe.getListeNoeuds().isEmpty()) {
      return null;
    }
    Iterator<Noeud> itNoeuds = groupe.getListeNoeuds().iterator();
    NoeudApp noeudLePlusProche = (NoeudApp) itNoeuds.next();
    double distmin = this.distance(noeudLePlusProche);
    while (itNoeuds.hasNext()) {
      NoeudApp noeud = (NoeudApp) itNoeuds.next();
      double dist = this.distance(noeud);
      if (distmin > dist) {
        distmin = dist;
        noeudLePlusProche = noeud;
      }
    }
    return noeudLePlusProche;
  }

  // //////////////////////////////////////////////////
  // POUR ETUDIER LES CORRESPONDANCES DES ARCS
  // COEUR DE L'APPARIEMENT DES NOEUDS
  // //////////////////////////////////////////////////

  /**
   * Teste la correspondance des arcs de self avec les arcs entrants et sortants
   * des noeuds
   * 
   * @return 1 si ca correspond bien 0 si ca correspond en partie seulement -1
   *         si rien ne correspond du tout
   */
  @SuppressWarnings("unchecked")
  public int correspCommunicants(NoeudApp noeudcomp,
      EnsembleDeLiens liensPreappArcs) {
    List<Arc> inRef, inComp, outRef, outComp;
    List<Arc> arcsRef, arcsComp;
    List<Object> arcsCompClasses;
    List<Object> arcsRefClasses;
    List<Arc> arcsRefClassesArcs;
    List<Boolean> arcsRefClassesOrientations;
    List<Arc> arcsCompClassesArcs;
    List<Boolean> arcsCompClassesOrientations;
    int nbCorresp, i;
    ArcApp arc;
    boolean entrantGeom, inOut = false;

    // 1ers tests sur le nombre
    arcsRef = this.arcs();
    arcsComp = noeudcomp.arcs();

    // 1: est-ce que chaque arc ref a au moins un correspondant autour du noeud
    // comp ?
    nbCorresp = this.nbArcsRefAvecCorrespondant(arcsRef, arcsComp,
        liensPreappArcs);
    if (nbCorresp == 0) {
      return -1;
    }
    if (nbCorresp != arcsRef.size()) {
      return 0;
    }

    // 2: est-ce que chaque arc ref a un correspondant pour lui tout seul ?
    // NB: 1er filtrage pour gérer les cas faciles plus vite,
    // mais ne gère pas bien tous les cas
    Iterator<Arc> itArcsRef = arcsRef.iterator();
    Collection<Arc> arcsCompCandidats = new HashSet<Arc>(0);
    while (itArcsRef.hasNext()) {
      arc = (ArcApp) itArcsRef.next();
      arcsCompCandidats.addAll(arc.arcsCompEnCorrespondance(liensPreappArcs));
    }
    arcsCompCandidats.retainAll(arcsComp);
    if (arcsCompCandidats.size() < arcsRef.size()) {
      return 0;
    }

    // 3 : plus fin : est-ce qu'on trouve bien des correspondances 1-1 ?

    // On crée les listes d'arcs in et out (au sens de la circulation),
    // en tournant autour des noeuds dans le bon sens.
    inRef = new ArrayList<Arc>(0);
    inComp = new ArrayList<Arc>(0);
    outRef = new ArrayList<Arc>(0);
    outComp = new ArrayList<Arc>(0);

    arcsRefClasses = this.arcsClasses();
    arcsRefClassesArcs = (List<Arc>) arcsRefClasses.get(0);
    arcsRefClassesOrientations = (List<Boolean>) arcsRefClasses.get(1);

    for (i = 0; i < arcsRefClassesArcs.size(); i++) {
      arc = (ArcApp) arcsRefClassesArcs.get(i);
      entrantGeom = (arcsRefClassesOrientations.get(i)).booleanValue();
      if (entrantGeom) {
        if ((arc.getOrientation() == 1) || (arc.getOrientation() == 2)) {
          inRef.add(arc);
        }
        if ((arc.getOrientation() == -1) || (arc.getOrientation() == 2)) {
          outRef.add(arc);
        }
      } else {
        if ((arc.getOrientation() == 1) || (arc.getOrientation() == 2)) {
          outRef.add(arc);
        }
        if ((arc.getOrientation() == -1) || (arc.getOrientation() == 2)) {
          inRef.add(arc);
        }
      }
    }
    arcsCompClasses = noeudcomp.arcsClasses();
    arcsCompClassesArcs = (List<Arc>) arcsCompClasses.get(0);
    arcsCompClassesOrientations = (List<Boolean>) arcsCompClasses.get(1);

    for (i = 0; i < arcsCompClassesArcs.size(); i++) {
      arc = (ArcApp) arcsCompClassesArcs.get(i);
      entrantGeom = (arcsCompClassesOrientations.get(i)).booleanValue();
      if (entrantGeom) {
        if ((arc.getOrientation() == 1) || (arc.getOrientation() == 2)) {
          inComp.add(arc);
        }
        if ((arc.getOrientation() == -1) || (arc.getOrientation() == 2)) {
          outComp.add(arc);
        }
      } else {
        if ((arc.getOrientation() == 1) || (arc.getOrientation() == 2)) {
          outComp.add(arc);
        }
        if ((arc.getOrientation() == -1) || (arc.getOrientation() == 2)) {
          inComp.add(arc);
        }
      }
    }

    // c'est la même chose en in et out ?
    if (inRef.size() == outRef.size() && inRef.size() == arcsRef.size()) {
      inOut = true;
    }

    // // on double les liste pour pouvoir tourner comme on veut
    // incomp.addAll(incomp);
    // if ( incomp.size() != 0 ) incomp.remove(incomp.size()-1);
    // outcomp.addAll(outcomp);
    // if ( outcomp.size() != 0 ) outcomp.remove(outcomp.size()-1);

    // on teste si chaque arc entrant a au moins un correspondant,
    // sans compter le même correspondant deux fois,
    // et en respectant le sens de rotation autour des noeuds
    if (inRef.size() != 0) {
      if (!this.correspondantsArcsClasses(inRef, inComp, 0, liensPreappArcs)) {
        return 0;
      }
    }

    // si tous les arcs sont entrants et sortant, on ne refait pas 2 fois la
    // même chose
    if (inOut) {
      return 1;
    }

    // sinon, on refait la même chose sur les sortants
    if (outRef.size() != 0) {
      if (!this.correspondantsArcsClasses(outRef, outComp, 0, liensPreappArcs)) {
        return 0;
      }
    }
    return 1;
  }

  /**
   * Teste la correspondance des arcs de self avec les arcs entrants et sortants
   * des groupes EQUIVALENT DE LA METHODE SUR LES NOEUDS, D'UN POINT DE VUE
   * GROUPE = HYPER-NOEUD.
   * 
   * @return 1 si ca correspond bien 0 si ca correspond en partie seulement -1
   *         si rien ne correspond du tout
   */
  @SuppressWarnings("unchecked")
  public int correspCommunicants(GroupeApp groupecomp,
      EnsembleDeLiens liensPreappArcs) {
    List<Arc> inRef, inComp, outRef, outComp;
    List<Arc> arcsRef, arcsComp;
    List<Object> arcsRefClasses, arcsCompClasses;
    List<Arc> arcsRefClassesArcs;
    List<Boolean> arcsRefClassesOrientations;
    List<Arc> arcsCompClassesArcs;
    List<Boolean> arcsCompClassesOrientations;
    int nbCorresp, i;
    Arc arc;
    boolean entrantGeom, inOut = false;

    // 1ers tests sur le nombre
    arcsRef = this.arcs();
    arcsComp = groupecomp.getAdjacents();
    nbCorresp = this.nbArcsRefAvecCorrespondant(arcsRef, arcsComp,
        liensPreappArcs);
    if (nbCorresp == 0) {
      return -1;
    }
    if (nbCorresp != arcsRef.size()) {
      return 0;
    }

    // On crée les listes d'arcs in et out (au sens de la circulation),
    // en tournant autour des noeuds dans le bon sens.
    inRef = new ArrayList<Arc>();
    inComp = new ArrayList<Arc>();
    outRef = new ArrayList<Arc>();
    outComp = new ArrayList<Arc>();

    arcsRefClasses = this.arcsClasses();
    arcsRefClassesArcs = (List<Arc>) arcsRefClasses.get(0);
    arcsRefClassesOrientations = (List<Boolean>) arcsRefClasses.get(1);

    for (i = 0; i < arcsRefClassesArcs.size(); i++) {
      arc = arcsRefClassesArcs.get(i);
      entrantGeom = (arcsRefClassesOrientations.get(i)).booleanValue();
      if (entrantGeom) {
        if ((arc.getOrientation() == 1) || (arc.getOrientation() == 2)) {
          inRef.add(arc);
        }
        if ((arc.getOrientation() == -1) || (arc.getOrientation() == 2)) {
          outRef.add(arc);
        }
      } else {
        if ((arc.getOrientation() == 1) || (arc.getOrientation() == 2)) {
          outRef.add(arc);
        }
        if ((arc.getOrientation() == -1) || (arc.getOrientation() == 2)) {
          inRef.add(arc);
        }
      }
    }

    arcsCompClasses = groupecomp.arcsClasses();
    arcsCompClassesArcs = (List<Arc>) arcsCompClasses.get(0);
    arcsCompClassesOrientations = (List<Boolean>) arcsCompClasses.get(1);

    for (i = 0; i < arcsCompClassesArcs.size(); i++) {
      arc = arcsCompClassesArcs.get(i);
      entrantGeom = (arcsCompClassesOrientations.get(i)).booleanValue();
      if (entrantGeom) {
        if ((arc.getOrientation() == 1) || (arc.getOrientation() == 2)) {
          inComp.add(arc);
        }
        if ((arc.getOrientation() == -1) || (arc.getOrientation() == 2)) {
          outComp.add(arc);
        }
      } else {
        if ((arc.getOrientation() == 1) || (arc.getOrientation() == 2)) {
          outComp.add(arc);
        }
        if ((arc.getOrientation() == -1) || (arc.getOrientation() == 2)) {
          inComp.add(arc);
        }
      }
    }

    // c'est la même chose en in et out ?
    if (inRef.size() == outRef.size() && inRef.size() == arcsRef.size()) {
      inOut = true;
    }

    // on teste si chaque arc entrant a au moins un correspondant, sans compter
    // le même correspondant deux fois
    if (inRef.size() != 0) {
      if (!this.correspondantsArcsClasses(inRef, inComp, 0, liensPreappArcs)) {
        return 0;
      }
    }

    // si tous les arcs sont entrants et sortant, on ne refait pas 2 fois la
    // même chose
    if (inOut) {
      return 1;
    }

    // sinon, on refait la même chose sur les sortants
    if (outRef.size() != 0) {
      if (!this.correspondantsArcsClasses(outRef, outComp, 0, liensPreappArcs)) {
        return 0;
      }
    }
    return 1;
  }

  /**
   * Methode utile à correspCommunicants (pour les noeuds et les groupes)
   * Renvoie le nb d'éléments de ref ayant au moins un correspondant dans comp
   * par liens
   */
  private int nbArcsRefAvecCorrespondant(List<Arc> ref, List<Arc> comp,
      EnsembleDeLiens ensembleDeLiens) {
    int nb = 0;
    List<Arc> corresp;
    ArcApp arcRef;
    Iterator<Arc> itRef = ref.iterator();
    while (itRef.hasNext()) {
      arcRef = (ArcApp) itRef.next();
      corresp = arcRef.arcsCompEnCorrespondance(ensembleDeLiens);
      corresp.retainAll(comp);
      if (corresp.size() != 0) {
        nb = nb + 1;
      }
    }
    return nb;
  }

  /**
   * Methode utile à correspCommunicants (pour les noeuds et les groupes)
   * renvoie OK quand tout est bon
   * @param ref : les arcs du noeud ref qui n'ont pas encore de correspondant
   * @param comp : les arcs du noeud comp qui n'ont pas encore de correspondant
   * @param rangRef : rang de l'arc ref en cours de traitement
   */
  private boolean correspondantsArcsClasses(List<Arc> ref, List<Arc> comp,
      int rangRef, EnsembleDeLiens ensembleDeLiens) {
    ArcApp arcRef, arcComp;
    List<LienReseaux> liensArcRef;
    List<Arc> arcsCompCandidats, compPourProchain;
    boolean OK;
    // si on n'a plus d'arc à traiter, c'est gagné
    if (rangRef == ref.size()) {
      return true;
    }
    arcRef = (ArcApp) ref.get(rangRef); // arc en cours de traitement
    // on cherche les candidats à l'appariement de arcRef
    liensArcRef = new ArrayList<LienReseaux>(arcRef.getLiens(ensembleDeLiens
        .getElements()));
    arcsCompCandidats = new ArrayList<Arc>();
    for (int i = 0; i < liensArcRef.size(); i++) {
      arcsCompCandidats.addAll(liensArcRef.get(i).getArcs2());
    }
    arcsCompCandidats.retainAll(comp);
    // si la liste des candidats est vide, c'est foutu, il faut revenir en
    // arrière
    if (arcsCompCandidats.size() == 0) {
      return false;
    }
    // on teste toutes les combinaisons de correspondance possibles
    for (int i = 0; i < comp.size(); i++) {
      arcComp = (ArcApp) comp.get(i);
      if (!arcsCompCandidats.contains(arcComp)) {
        continue; // cet arc n'est pas candidat, on essaye avec le suivant
      }
      // on a un candidat sous la main
      compPourProchain = new ArrayList<Arc>();
      if (rangRef == 0) {
        for (int j = i + 1; j < comp.size(); j++) {
          compPourProchain.add(comp.get(j));
        }
        for (int j = 0; j < i; j++) {
          compPourProchain.add(comp.get(j));
        }
      } else {
        for (int j = i + 1; j < comp.size(); j++) {
          compPourProchain.add(comp.get(j));
        }
      }
      if (compPourProchain.size() < ref.size() - rangRef - 1) {
        continue;
      }
      OK = this.correspondantsArcsClasses(ref, compPourProchain, rangRef + 1,
          ensembleDeLiens);
      if (OK) {
        return true; // une correspondance possible : on continue
      }
    }
    return false; // aucune correspondance possible : on remonte d'un cran
  }

  /**
   * Renvoie la liste des objets géo initaux reliés à un arc ref ou un noeud ref
   * qui est en correspondance avec this (un arc_comp) à travers liens, soit
   * directement, soit par l'intermédiaire d'un groupe.
   */
  public List<IFeature> objetsGeoRefEnCorrespondance(EnsembleDeLiens liensArc) {
    List<ElementCarteTopo> objetsCtEnCorrespondance = new ArrayList<ElementCarteTopo>(
        0);
    List<IFeature> objetsGeoEnCorrespondance = new ArrayList<IFeature>(0);
    List<LienReseaux> liensOK;
    LienReseaux lien;
    Iterator<Groupe> itGroupes;
    Iterator<LienReseaux> itLiens;
    Iterator<ElementCarteTopo> itObjetsCT;

    // objets de reseauRef en correspondance directe avec this.
    liensOK = new ArrayList<LienReseaux>(this.getLiens());
    liensOK.retainAll(liensArc.getElements());
    itLiens = liensOK.iterator();
    while (itLiens.hasNext()) {
      lien = itLiens.next();
      objetsCtEnCorrespondance.addAll(lien.getArcs1());
      objetsCtEnCorrespondance.addAll(lien.getNoeuds1());
    }
    // objets de reseauRef en correspondance avec this à travers un groupe
    itGroupes = this.getListeGroupes().iterator();
    while (itGroupes.hasNext()) {
      GroupeApp groupe = (GroupeApp) itGroupes.next();
      liensOK = new ArrayList<LienReseaux>(groupe.getLiens());
      liensOK.retainAll(liensArc.getElements());
      itLiens = liensOK.iterator();
      while (itLiens.hasNext()) {
        lien = itLiens.next();
        objetsCtEnCorrespondance.addAll(lien.getArcs1());
        objetsCtEnCorrespondance.addAll(lien.getNoeuds1());
      }
    }
    // objets geo correspondants
    itObjetsCT = objetsCtEnCorrespondance.iterator();
    while (itObjetsCT.hasNext()) {
      IFeature objetCT = itObjetsCT.next();
      objetsGeoEnCorrespondance.addAll(objetCT.getCorrespondants());
    }
    return objetsGeoEnCorrespondance;
  }
}
