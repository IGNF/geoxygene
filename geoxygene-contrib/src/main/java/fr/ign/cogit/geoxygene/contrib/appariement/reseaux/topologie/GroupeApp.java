/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.LienReseaux;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Un Groupe est un ensemble d'arcs et de noeuds d'un reseau. L'appariement de
 * réseaux à des échelles différentes abouti à de nombreux appariements 1-n,
 * d'un objet vers un groupe.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0
 */

public class GroupeApp extends Groupe {

  private String resultatAppariement;

  public String getResultatAppariement() {
    return this.resultatAppariement;
  }

  public void setResultatAppariement(String resultat) {
    this.resultatAppariement = resultat;
  }

  /** affecte le resultat de l'appariement sur le groupe et ses composants */
  public void setResultatAppariementGlobal(String resultat) {
    this.setResultatAppariement(resultat);
    Iterator<Arc> itComposantsArcs;
    Iterator<Noeud> itComposantsNoeuds;
    itComposantsArcs = this.getListeArcs().iterator();
    while (itComposantsArcs.hasNext()) {
      ArcApp arc = (ArcApp) itComposantsArcs.next();
      arc.setResultatAppariement(resultat);
    }
    itComposantsNoeuds = this.getListeNoeuds().iterator();
    while (itComposantsNoeuds.hasNext()) {
      NoeudApp noeud = (NoeudApp) itComposantsNoeuds.next();
      noeud.setResultatAppariement(resultat);
    }
  }

  /** Liens qui référencent l'objet apparié */
  private List<LienReseaux> liens = new ArrayList<LienReseaux>();

  public List<LienReseaux> getLiens() {
    return this.liens;
  }

  public void setLiens(List<LienReseaux> liens) {
    this.liens = liens;
  }

  public void addLiens(LienReseaux liensReseaux) {
    this.liens.add(liensReseaux);
  }

  /**
   * Renvoie les liens de l'objet qui appartiennent à la liste liensPertinents
   */
  public List<LienReseaux> getLiens(List<Lien> liensPertinents) {
    List<LienReseaux> listeTmp = new ArrayList<LienReseaux>(this.getLiens());
    listeTmp.retainAll(liensPertinents);
    return listeTmp;
  }

  /**
   * Les noeuds d'entree dans le groupe comp (au sens de la communication). La
   * notion d'entrée / sortie est relative au préappariement d'arcs et au noeud
   * ref passé en paramètre. La liste en sortie contient des NoeudApp
   */
  public List<Noeud> noeudsEntree(NoeudApp noeudref, EnsembleDeLiens liensPreappArcs) {
    List<Arc> arcsRef = new ArrayList<Arc>();
    List<Arc> arcs = new ArrayList<Arc>();
    List<Arc> arcsOK = new ArrayList<Arc>();
    List<Noeud> noeuds = new ArrayList<Noeud>();
    ArcApp arccomp;
    int i, j;

    arcsRef = noeudref.entrantsOrientes();
    for (i = 0; i < this.getListeNoeuds().size(); i++) {
      arcs = ((NoeudApp) this.getListeNoeuds().get(i)).entrantsOrientes();
      for (j = 0; j < arcs.size(); j++) {
        arccomp = (ArcApp) arcs.get(j);
        if (this.getListeArcs().contains(arccomp)) {
          continue;
        }
        arcsOK = arccomp.arcsRefEnCorrespondance(liensPreappArcs);
        arcsOK.retainAll(arcsRef);
        if (arcsOK.size() != 0) {
          noeuds.add(this.getListeNoeuds().get(i));
          break;
        }
      }
    }
    return noeuds;
  }

  /**
   * Les noeuds de sortie dans le groupe (au sens de la communication). La
   * notion d'entrée / sortie est relative au préappariement d'arcs et au noeud
   * ref passé en paramètre. La liste en sortie contient des Noeud
   */
  public List<Noeud> noeudsSortie(NoeudApp noeudref,
      EnsembleDeLiens liensPreappArcs) {
    List<Arc> arcsRef = new ArrayList<Arc>();
    List<Arc> arcs = new ArrayList<Arc>();
    List<Arc> arcsOK = new ArrayList<Arc>();
    List<Noeud> noeuds = new ArrayList<Noeud>();
    ArcApp arcComp;
    int i, j;

    arcsRef = noeudref.sortantsOrientes();
    for (i = 0; i < this.getListeNoeuds().size(); i++) {
      arcs = ((NoeudApp) this.getListeNoeuds().get(i)).sortantsOrientes();
      for (j = 0; j < arcs.size(); j++) {
        arcComp = (ArcApp) arcs.get(j);
        if (this.getListeArcs().contains(arcComp)) {
          continue;
        }
        arcsOK = arcComp.arcsRefEnCorrespondance(liensPreappArcs);
        arcsOK.retainAll(arcsRef);
        if (arcsOK.size() != 0) {
          noeuds.add(this.getListeNoeuds().get(i));
          break;
        }
      }
    }
    return noeuds;
  }

  /**
   * Enleve les premier et dernier noeuds du groupe. Utile pour traiter les plus
   * courts chemins.
   */
  public void enleveExtremites() {
    NoeudApp ext;
    if (this.getListeNoeuds().size() == 0) {
      return;
    }
    ext = (NoeudApp) this.getListeNoeuds().get(0);
    this.getListeNoeuds().remove(ext);
    ext.getListeGroupes().remove(this);
    if (this.getListeNoeuds().size() == 0) {
      return;
    }
    ext = (NoeudApp) this.getListeNoeuds()
        .get(this.getListeNoeuds().size() - 1);
    this.getListeNoeuds().remove(ext);
    ext.getListeGroupes().remove(this);
  }

  /**
   * Plus court chemin dans this pour relier les noeudsDepart aux noeudsArrivee.
   * <p>
   * NB: le pcc renvoyé ici NE CONTIENT PAS les noeuds initiaux et finaux
   */
  public GroupeApp plusCourtChemin(List<Noeud> noeudsDepart, List<Noeud> noeudsArrivee,
      double longMax) {
    GroupeApp pccMin = null, pcc;
    double longueur, longMin = longMax + 1;
    Noeud iniComp, finComp;
    for (int j = 0; j < noeudsDepart.size(); j++) {
      for (int k = 0; k < noeudsArrivee.size(); k++) {
        iniComp = noeudsDepart.get(j);
        finComp = noeudsArrivee.get(k);
        pcc = (GroupeApp) iniComp.plusCourtChemin(finComp, this, longMax);
        if (pcc == null) {
          continue;
        }
        longueur = pcc.longueur();
        if (longueur < longMin) {
          longMin = longueur;
          if (pccMin != null) {
            pccMin.vide();
          }
          pccMin = pcc;
          continue;
        }
        pcc.vide();
      }
    }
    return pccMin;
  }

  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  // FILTRAGES DES GROUPES LORS DE L'APPARIEMENT
  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Méthode permettant de filtrer un groupe du graphe de comparaison, supposé
   * apparié avec un noeud du graphe de référence. D'après thèse de Thomas
   * Devogèle (1997), avec ajouts de Sébastien Mustière (2002). S'appuie
   * principalement sur des recherches d'impasses et des calculs de plus courts
   * chemins. NB: le groupe en sortie peut être vide (filtrage trop fort)
   */
  public void filtrageGroupePendantAppariementDesNoeuds(NoeudApp noeudRef,
      EnsembleDeLiens liensPreappArcs) {
    int nbArcs;
    boolean complet;

    // le traitement diffère pour les groupes complets à l'origine et les autres
    if (noeudRef.correspCommunicants(this, liensPreappArcs) == 1) {
      complet = true;
    } else {
      complet = false;
    }

    // 1. Elimination des impasses de BD comp. Récursif
    while (true) {
      nbArcs = this.getListeArcs().size();
      if (nbArcs == 1) {
        break; // ajout seb mars 2005
      }
      this.filtrageImpasses();
      if (this.getListeArcs().size() == nbArcs) {
        break;
      }
    }

    // 2. Elimination des impasses au sein du groupe (sans tenir compte des
    // autres arcs de BDcomp)
    // ET dont l'extrémité (à l'extérieure du groupe) n'a pas d'autre troncon
    // apparié avec des arcs
    // de la BDRef par le préappariement. Recursif tant qu'on en enleve encore
    while (true) {
      nbArcs = this.getListeArcs().size();
      if (nbArcs == 1) {
        break; // ajout seb mars 2005
      }
      this.filtrageImpassesNonApparie(liensPreappArcs);
      if (this.getListeArcs().size() == nbArcs) {
        break;
      }
    }

    // 3. Pour les groupes incomplets :
    // Elimination des impasses au sein du groupe (sans tenir compte des autres
    // arcs de BDcomp)
    // ET dont l'extrémité (à l'extérieure du groupe) a tous ses troncons
    // adjacents (y compris l'impasse)
    // appariés avec le même arc de BDcomp. Recursif.
    if (!complet) {
      while (true) {
        nbArcs = this.getListeArcs().size();
        if (nbArcs == 1) {
          break; // ajout seb mars 2005
        }
        // modif Seb par rapport à Thomas
        this.filtrageImpassesTousApparies(liensPreappArcs);
        if (this.getListeArcs().size() == nbArcs) {
          break;
        }
      }
    }

    // 3. Pour les groupes complets :
    // Elimination des impasses au sein du groupe (sans tenir compte des autres
    // arcs de BDcomp)
    // qui laissent le groupe complet si on les enlève
    if (complet) {
      while (true) {
        nbArcs = this.getListeArcs().size();
        if (nbArcs == 1) {
          break; // ajout seb mars 2005
        }
        // modif Seb par rapport à Thomas
        this.filtrageImpassesComplets(noeudRef, liensPreappArcs);
        if (this.getListeArcs().size() == nbArcs) {
          break;
        }
      }
    }

    // 4. Filtrage par plus court chemin
    this.filtragePlusCourtChemin(noeudRef, liensPreappArcs);

    // 5. (idem 3)
    if (!complet) {
      while (true) {
        nbArcs = this.getListeArcs().size();
        if (nbArcs == 1) {
          break; // ajout seb mars 2005
        }
        // modif Seb par rapport à Thomas
        this.filtrageImpassesTousApparies(liensPreappArcs);
        if (this.getListeArcs().size() == nbArcs) {
          break;
        }
      }
    }

    if (complet) {
      while (true) {
        nbArcs = this.getListeArcs().size();
        if (nbArcs == 1) {
          break; // ajout seb mars 2005
        }
        // modif Seb par rapport à Thomas
        this.filtrageImpassesComplets(noeudRef, liensPreappArcs);
        if (this.getListeArcs().size() == nbArcs) {
          break;
        }
      }
    }
  }

  /**
   * Methode utile pour le filtrage des groupes Elimination des impasses de BD
   * comp
   */
  private void filtrageImpasses() {
    int i;
    ArcApp arcComp;
    List<ArcApp> impassesDebut = new ArrayList<ArcApp>();
    List<ArcApp> impassesFin = new ArrayList<ArcApp>();

    // Recherche des arcs à éliminer
    for (i = 0; i < this.getListeArcs().size(); i++) {
      arcComp = (ArcApp) this.getListeArcs().get(i);
      if (arcComp.impasseDebut()) {
        impassesDebut.add(arcComp);
      }
      if (arcComp.impasseFin()) {
        impassesFin.add(arcComp);
      }
    }
    // Elimination des arcs à éliminer ainsi que de leurs extrémités libres
    for (i = 0; i < impassesDebut.size(); i++) {
      arcComp = impassesDebut.get(i);
      this.getListeArcs().remove(arcComp);
      arcComp.getListeGroupes().remove(this);
      this.getListeNoeuds().remove(arcComp.getNoeudIni());
      ((NoeudApp) arcComp.getNoeudIni()).getListeGroupes().remove(this);
    }
    for (i = 0; i < impassesFin.size(); i++) {
      arcComp = impassesFin.get(i);
      this.getListeArcs().remove(arcComp);
      arcComp.getListeGroupes().remove(this);
      this.getListeNoeuds().remove(arcComp.getNoeudFin());
      ((NoeudApp) arcComp.getNoeudFin()).getListeGroupes().remove(this);
    }
  }

  /**
   * Methode utile pour le filtrage des groupes Elimination des impasses AU SEIN
   * du groupe et dont l'extrémité (à l'extérieure du groupe) n'a pas d'autre
   * troncon apparié avec des arcs de la BD Ref (par le préappariement)
   */
  private void filtrageImpassesNonApparie(EnsembleDeLiens liensPreappArcs) {
    int i, j;
    boolean match;
    ArcApp arcComp, arcAdjacent;
    List<Arc> adjacentsComp = new ArrayList<Arc>();
    List<ArcApp> impassesDebut = new ArrayList<ArcApp>();
    List<ArcApp> impassesFin = new ArrayList<ArcApp>();

    // Recherche des arcs à éliminer
    for (i = 0; i < this.getListeArcs().size(); i++) {
      arcComp = (ArcApp) this.getListeArcs().get(i);
      match = false;
      if (arcComp.impasseDebut(this)) {
        adjacentsComp = arcComp.getNoeudIni().arcs();
        for (j = 0; j < adjacentsComp.size(); j++) {
          arcAdjacent = (ArcApp) adjacentsComp.get(j);
          if (arcAdjacent == arcComp) {
            continue;
          }
          if (arcAdjacent.aUnCorrespondant(liensPreappArcs)) {
            match = true;
            break;
          }
        }
        if (!match) {
          impassesDebut.add(arcComp);
        }
      }
      if (arcComp.impasseFin(this)) {
        adjacentsComp = arcComp.getNoeudFin().arcs();
        for (j = 0; j < adjacentsComp.size(); j++) {
          arcAdjacent = (ArcApp) adjacentsComp.get(j);
          if (arcAdjacent == arcComp) {
            continue;
          }
          if (arcAdjacent.aUnCorrespondant(liensPreappArcs)) {
            match = true;
            break;
          }
        }
        if (!match) {
          impassesFin.add(arcComp);
        }
      }
    }

    // Elimination des arcs à éliminer ainsi que de leurs extrémités libres
    for (i = 0; i < impassesDebut.size(); i++) {
      arcComp = impassesDebut.get(i);
      this.getListeArcs().remove(arcComp);
      arcComp.getListeGroupes().remove(this);
      this.getListeNoeuds().remove(arcComp.getNoeudIni());
      ((NoeudApp) arcComp.getNoeudIni()).getListeGroupes().remove(this);
    }
    for (i = 0; i < impassesFin.size(); i++) {
      arcComp = impassesFin.get(i);
      this.getListeArcs().remove(arcComp);
      arcComp.getListeGroupes().remove(this);
      this.getListeNoeuds().remove(arcComp.getNoeudFin());
      ((NoeudApp) arcComp.getNoeudFin()).getListeGroupes().remove(this);
    }
  }

  /**
   * Methode utile pour le filtrage des groupes Filtrage par "réduction"
   * Elimination des impasses au sein du groupe et dont l'extrémité (à
   * l'extérieure du groupe) a tous ses troncons appariés avec le même arc de
   * BDcomp.
   */
  private void filtrageImpassesTousApparies(EnsembleDeLiens liensPreappArcs) {
    int i, j;
    List<Arc> arcsCommuns = new ArrayList<Arc>();
    List<Arc> arcsNouveaux = new ArrayList<Arc>();
    List<Arc> arcsCompCommuniquants = new ArrayList<Arc>();
    ArcApp arcComp;
    boolean communs;
    List<ArcApp> impassesDebut = new ArrayList<ArcApp>();
    List<ArcApp> impassesFin = new ArrayList<ArcApp>();

    // Recherche des arcs à éliminer
    for (i = 0; i < this.getListeArcs().size(); i++) {
      arcComp = (ArcApp) this.getListeArcs().get(i);
      if (arcComp.impasseDebut(this)) {
        // on cherche si tous les entrants/sortants du noeud ini de Arc_Comp
        // sont appariés avec au moins un même arc de BDref commun
        communs = true;
        arcsCompCommuniquants = arcComp.getNoeudIni().arcs();
        arcsCommuns = arcComp.arcsRefEnCorrespondance(liensPreappArcs);
        for (j = 0; j < arcsCompCommuniquants.size(); j++) {
          arcsNouveaux = ((ArcApp) arcsCompCommuniquants.get(j))
              .arcsRefEnCorrespondance(liensPreappArcs);
          if (arcsNouveaux.size() == 0) {
            continue;
          }
          arcsCommuns.retainAll(arcsNouveaux);
          if (arcsCommuns.size() == 0) {
            communs = false;
            break;
          }
        }
        if (communs) {
          impassesDebut.add(arcComp);
        }
      }
      if (arcComp.impasseFin(this)) {
        // on cherche si tous les entrants/sortants du noeud fin de arcComp
        // sont appariés avec au moins un même arc de BDref commun
        communs = true;
        arcsCompCommuniquants = arcComp.getNoeudFin().arcs();
        arcsCommuns = arcComp.arcsRefEnCorrespondance(liensPreappArcs);
        for (j = 0; j < arcsCompCommuniquants.size(); j++) {
          arcsNouveaux = ((ArcApp) arcsCompCommuniquants.get(j))
              .arcsRefEnCorrespondance(liensPreappArcs);
          if (arcsNouveaux.size() == 0) {
            continue;
          }
          arcsCommuns.retainAll(arcsNouveaux);
          if (arcsCommuns.size() == 0) {
            communs = false;
            break;
          }
        }
        if (communs) {
          impassesFin.add(arcComp);
        }
      }
    }

    // Elimination des arcs à éliminer ainsi que de leurs extrémités libres
    for (i = 0; i < impassesDebut.size(); i++) {
      arcComp = impassesDebut.get(i);
      this.getListeArcs().remove(arcComp);
      arcComp.getListeGroupes().remove(this);
      this.getListeNoeuds().remove(arcComp.getNoeudIni());
      ((NoeudApp) arcComp.getNoeudIni()).getListeGroupes().remove(this);
    }
    for (i = 0; i < impassesFin.size(); i++) {
      arcComp = impassesFin.get(i);
      this.getListeArcs().remove(arcComp);
      arcComp.getListeGroupes().remove(this);
      this.getListeNoeuds().remove(arcComp.getNoeudFin());
      ((NoeudApp) arcComp.getNoeudFin()).getListeGroupes().remove(this);
    }
  }

  /**
   * Methode utile pour le filtrage des groupes Elimination des impasses de BD
   * comp
   */
  private void filtrageImpassesComplets(NoeudApp noeudRef,
      EnsembleDeLiens liensPreappArcs) {
    int i;
    ArcApp arccomp;
    int correspondance;
    List<IFeature> arcsATester = new ArrayList<IFeature>();

    // Recherche des arcs à éliminer
    arcsATester.addAll(this.getListeArcs());
    for (i = 0; i < arcsATester.size(); i++) {
      arccomp = (ArcApp) arcsATester.get(i);
      if (arccomp.impasseDebut(this)) {
        // on enlève l'arc, et on vérifie si le goupe est toujours complet,
        // sinon on remet l'arc
        this.getListeArcs().remove(arccomp);
        arccomp.getListeGroupes().remove(this);
        this.getListeNoeuds().remove(arccomp.getNoeudIni());
        ((NoeudApp) arccomp.getNoeudIni()).getListeGroupes().remove(this);
        correspondance = noeudRef.correspCommunicants(this, liensPreappArcs);
        if (correspondance == 1) {
          return;
        }
        this.getListeArcs().add(arccomp);
        arccomp.getListeGroupes().add(this);
        this.getListeNoeuds().add(arccomp.getNoeudIni());
        ((NoeudApp) arccomp.getNoeudIni()).getListeGroupes().add(this);
      }
      if (arccomp.impasseFin(this)) {
        // on enlève l'arc, et on vérifie si le goupe est toujours complet,
        // sinon on remet l'arc
        this.getListeArcs().remove(arccomp);
        arccomp.getListeGroupes().remove(this);
        this.getListeNoeuds().remove(arccomp.getNoeudFin());
        ((NoeudApp) arccomp.getNoeudFin()).getListeGroupes().remove(this);
        correspondance = noeudRef.correspCommunicants(this, liensPreappArcs);
        if (correspondance == 1) {
          return;
        }
        this.getListeArcs().add(arccomp);
        arccomp.getListeGroupes().add(this);
        this.getListeNoeuds().add(arccomp.getNoeudFin());
        ((NoeudApp) arccomp.getNoeudFin()).getListeGroupes().add(this);
      }
    }
  }

  /** Méthode utile pour le filtrage des groupes */
  private void filtragePlusCourtChemin(NoeudApp noeudRef,
      EnsembleDeLiens liensPreappArcs) {

    List<Noeud> noeudsIn = new ArrayList<Noeud>();
    List<Noeud> noeudsOut = new ArrayList<Noeud>();
    List<Arc> arcsOK = new ArrayList<Arc>();
    List<Noeud> noeudsOK = new ArrayList<Noeud>();
    List<Noeud> noeudsAEnlever = new ArrayList<Noeud>();
    List<Arc> arcsAEnlever = new ArrayList<Arc>();
    GroupeApp pcc;
    NoeudApp in, out, noeudComp;
    ArcApp arcComp;
    int i, j, k;

    noeudsIn = this.noeudsEntree(noeudRef, liensPreappArcs);
    noeudsOut = this.noeudsSortie(noeudRef, liensPreappArcs);

    // Quand il n'y que des entrants ou que des sortants, on les garde ainsi que
    // les arcs qui les relient (bidouille, j'en conviens)
    if (noeudsIn.size() == 0) {
      noeudsIn.addAll(noeudsOut);
    }
    if (noeudsOut.size() == 0) {
      noeudsOut.addAll(noeudsIn);
    }

    for (i = 0; i < noeudsIn.size(); i++) {
      for (j = 0; j < noeudsOut.size(); j++) {
        in = (NoeudApp) noeudsIn.get(i);
        out = (NoeudApp) noeudsOut.get(j);
        // if ( in == out ) continue;
        // plus court chemin dans le groupe
        pcc = (GroupeApp) in.plusCourtChemin(out, this, 0);
        if (pcc == null) {
          continue;
        }
        for (k = 0; k < pcc.getListeArcs().size(); k++) {
          arcsOK.add(pcc.getListeArcs().get(k));
        }
        for (k = 0; k < pcc.getListeNoeuds().size(); k++) {
          noeudsOK.add(pcc.getListeNoeuds().get(k));
        }
        // A faire ? vider le pcc pour faire propre ?
      }
    }

    for (i = 0; i < this.getListeNoeuds().size(); i++) {
      if (!noeudsOK.contains(this.getListeNoeuds().get(i))) {
        noeudsAEnlever.add(this.getListeNoeuds().get(i));
      }
    }
    for (i = 0; i < this.getListeArcs().size(); i++) {
      if (!arcsOK.contains(this.getListeArcs().get(i))) {
        arcsAEnlever.add(this.getListeArcs().get(i));
      }
    }
    for (i = 0; i < noeudsAEnlever.size(); i++) {
      noeudComp = (NoeudApp) noeudsAEnlever.get(i);
      this.getListeNoeuds().remove(noeudComp);
      noeudComp.getListeGroupes().remove(this);
    }
    for (i = 0; i < arcsAEnlever.size(); i++) {
      arcComp = (ArcApp) arcsAEnlever.get(i);
      this.getListeArcs().remove(arcComp);
      arcComp.getListeGroupes().remove(this);
    }
  }

  // ////////////////////////////////////////////////////////////////////////////////////
  // POUR EXPORT UNIQUEMENT
  // ////////////////////////////////////////////////////////////////////////////////////
  /**
   * Pour l'export : mise bout à bout des arcs du groupe, dans le bon ordre. NB:
   * n'est valable que pour un groupe où les arcs sont bout à bout, et dans le
   * bon ordre, comme ceux issus du plus_court_chemin Cas particuliers : si le
   * goupe est vide, renvoie null; si le goupe ne contient que des points,
   * renvoie une ligne dégénérée contenant 2 fois le premier point.
   */
  public GM_LineString compileArcs(Arc arcRef) {
    Arc arc, premierArc;
    Noeud premierNoeud;
    List<IDirectPosition> points1 = new ArrayList<IDirectPosition>();
    List<IDirectPosition> points2 = new ArrayList<IDirectPosition>();
    Iterator<Arc> itArcs = this.getListeArcs().iterator();
    if (!(itArcs.hasNext())) {
      if (this.getListeNoeuds().isEmpty()) {
        return null;
      }
      points1.add(((NoeudApp) this.getListeNoeuds().get(0)).getCoord());
      points1.add(((NoeudApp) this.getListeNoeuds().get(0)).getCoord());
      return new GM_LineString(points1);
    }
    // on met bout à bout les arcs
    premierArc = this.getListeArcs().get(0);
    if (this.getListeNoeuds().contains(premierArc.getNoeudIni())) {
      premierNoeud = premierArc.getNoeudFin();
    } else {
      premierNoeud = premierArc.getNoeudIni();
    }
    while (itArcs.hasNext()) {
      arc = itArcs.next();
      if (arc.getNoeudIni() == premierNoeud) {
        for (int i = 0; i < arc.getCoord().size(); i++) {
          points1.add(arc.getCoord().get(i));
          premierNoeud = arc.getNoeudFin();
        }
      } else {
        for (int i = 0; i < arc.getCoord().size(); i++) {
          points1.add(arc.getCoord().get(arc.getCoord().size() - i - 1));
          premierNoeud = arc.getNoeudIni();
        }
      }
    }
    GM_LineString chemin = new GM_LineString(points1);
    // on inverse l'arc au besoin.
    double d1 = chemin.startPoint().distance(arcRef.getGeometrie().startPoint())
        + chemin.endPoint().distance(arcRef.getGeometrie().endPoint());
    double d2 = chemin.startPoint().distance(arcRef.getGeometrie().endPoint())
        + chemin.endPoint().distance(arcRef.getGeometrie().startPoint());
    if (d1 < d2) {
      return chemin;
    } else {
      for (int i = 0; i < points1.size(); i++) {
        points2.add(points1.get(points1.size() - i - 1));
      }
    }
    return new GM_LineString(points2);
  }

}
