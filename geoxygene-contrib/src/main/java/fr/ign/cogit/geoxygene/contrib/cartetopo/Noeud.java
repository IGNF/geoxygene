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

package fr.ign.cogit.geoxygene.contrib.cartetopo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Nodes of topological map.
 * <p>
 * Classe des noeuds de la carte topo. Les noeuds ont pour géométrie un GM_Point.
 * </p>
 * 
 * @author Sébastien Mustière
 * @author Olivier Bonin
 * @version 1.0
 */
public class Noeud extends ElementCarteTopo {
  /**
   * Default Constructor.
   */
  public Noeud() {
  }

  /**
   * Constructor.
   * @param point a point
   */
  public Noeud(IPoint point) {
    this.setGeom(point);
  }

  /**
   * Constructor.
   * @param p a position
   */
  public Noeud(IDirectPosition p) {
    this.setCoord(p);
  }

  // ////////////////////////////////////////////////////////////////////////
  // géométrie
  // ////////////////////////////////////////////////////////////////////////
  /** Renvoie le IPoint qui définit la géométrie de self */
  public IPoint getGeometrie() {
    return (IPoint) this.geom;
  }

  /** définit le GM_Point qui définit la géométrie de self */
  public void setGeometrie(IPoint geometrie) {
    this.setGeom(geometrie);
  }

  /** Renvoie le DirectPosition qui définit les coordonnées de self */
  public IDirectPosition getCoord() {
    return this.getGeometrie().getPosition();
  }

  /** définit le DirectPosition qui définit les coordonnées de self */
  public void setCoord(IDirectPosition dp) {
    this.geom = new GM_Point(dp);
  }

  // ////////////////////////////////////////////////////////////////////////
  // Topologie de réseau arcs / noeuds
  // ////////////////////////////////////////////////////////////////////////
  private List<Arc> entrants = new ArrayList<Arc>(0);

  /**
   * Renvoie la liste (non ordonnée) des arcs entrants de self. La distinction
   * entrant/sortant s'entend au sens du codage de la géométrie. (et non au sens
   * de l'orientation du graphe, comme avec les attributs entrantsOrientation).
   */
  public List<Arc> getEntrants() {
    return this.entrants;
  }

  /** Ajoute un arc entrant à la liste des arcs entrants de self */
  public void addEntrant(Arc arc) {
    if (arc != null && !this.entrants.contains(arc)) {
      this.entrants.add(arc);
      if (arc.getNoeudFin() != this) {
        arc.setNoeudFin(this);
      }
    }
  }

  /** Enlève un arc entrant à la liste des arcs entrants de self */
  public void enleveEntrant(Arc arc) {
    if (arc == null) {
      return;
    }
    if (!this.entrants.contains(arc)) {
      return;
    }
    this.entrants.remove(arc);
    arc.setNoeudFin(null);
  }

  /**
   * Outgoing edges.
   */
  private List<Arc> sortants = new ArrayList<Arc>(0);

  /**
   * Renvoie la liste (non ordonnée) des arcs sortants de self La distinction
   * entrant/sortant s'entend au sens du codage de la géométrie (et non au sens
   * de l'orientation du graphe, comme avec les attributs entrantsOrientation).
   */
  public List<Arc> getSortants() {
    return this.sortants;
  }

  /** Ajoute un arc sortant à la liste des arcs sortants de self */
  public void addSortant(Arc arc) {
    if (arc != null && !this.sortants.contains(arc)) {
      this.sortants.add(arc);
      if (arc.getNoeudIni() != this) {
        arc.setNoeudIni(this);
      }
    }
  }

  /** Enlève un arc sortant à la liste des arcs entrants de self */
  public void enleveSortant(Arc arc) {
    if (arc == null) {
      return;
    }
    if (!this.sortants.contains(arc)) {
      return;
    }
    this.sortants.remove(arc);
    arc.setNoeudIni(null);
  }

  /**
   * Renvoie la liste (non ordonnée) de tous les arcs entrants et sortants de
   * self. NB : si un arc est à la fois entrant et sortant (boucle), il est 2
   * fois dans la liste.
   */
  public List<Arc> arcs() {
    List<Arc> Arcs = new ArrayList<Arc>();
    Arcs.addAll(this.getSortants());
    Arcs.addAll(this.getEntrants());
    return Arcs;
  }

  /**
   * Renvoie la liste des noeuds voisins de self dans le réseau sans tenir
   * compte de l'orientation (i.e. tous les arcs sont considérés en double
   * sens).
   */
  public List<Noeud> voisins() {
    List<Noeud> voisins = new ArrayList<Noeud>();
    for (Arc arc : this.getEntrants()) {
      voisins.add(arc.getNoeudIni());
    }
    for (Arc arc : this.getSortants()) {
      voisins.add(arc.getNoeudFin());
    }
    return voisins;
  }

  // ////////////////////////////////////////////////////////////////////////
  // Gestion de graphe noeuds / faces
  // ////////////////////////////////////////////////////////////////////////
  /** Renvoie la liste des faces s'appuyant sur self */
  public List<Face> faces() {
    HashSet<Face> faces = new HashSet<Face>();
    List<Arc> arcs = this.arcs();
    Arc arc;
    Iterator<Arc> iterarcs = arcs.iterator();
    while (iterarcs.hasNext()) {
      arc = iterarcs.next();
      faces.addAll(arc.faces());
    }
    return new ArrayList<Face>(faces);
  }

  // ////////////////////////////////////////////////////////////////////////
  // Gestion de réseau orienté
  // ////////////////////////////////////////////////////////////////////////

  /**
   * les entrants du noeud, au sens de l'orientation, (alors que pour
   * getEntrants c'est au sens de la géométrie)
   **/
  public List<Arc> entrantsOrientes() {
    List<Arc> arcsEntrants = this.getEntrants();
    List<Arc> arcsSortants = this.getSortants();
    List<Arc> arcs = new ArrayList<Arc>();
    Arc arc;
    int i;

    for (i = 0; i < arcsEntrants.size(); i++) {
      arc = arcsEntrants.get(i);
      if ((arc.getOrientation() == 1) || (arc.getOrientation() == 2)) {
        arcs.add(arc);
      }
    }
    for (i = 0; i < arcsSortants.size(); i++) {
      arc = arcsSortants.get(i);
      if ((arc.getOrientation() == -1) || (arc.getOrientation() == 2)) {
        arcs.add(arc);
      }
    }
    return arcs;
  }

  /**
   * les sortants du noeud, au sens de l'orientation, (alors que pour
   * getSortants c'est au sens de la géométrie)
   **/
  public List<Arc> sortantsOrientes() {
    List<Arc> arcsEntrants = this.getEntrants();
    List<Arc> arcsSortants = this.getSortants();
    List<Arc> arcs = new ArrayList<Arc>();
    Arc arc;
    int i;

    for (i = 0; i < arcsEntrants.size(); i++) {
      arc = arcsEntrants.get(i);
      if ((arc.getOrientation() == -1) || (arc.getOrientation() == 2)) {
        arcs.add(arc);
      }
    }
    for (i = 0; i < arcsSortants.size(); i++) {
      arc = arcsSortants.get(i);
      if ((arc.getOrientation() == 1) || (arc.getOrientation() == 2)) {
        arcs.add(arc);
      }
    }
    return arcs;
  }

  /**
   * Retourne les noeuds accessibles depuis ce noeud, au sens de l'orientation
   * et non de la géometrie,
   */
  public Map<Noeud, Arc> noeudsSortantsOrientes() {
    List<Arc> arcsEntrants = this.getEntrants();
    List<Arc> arcsSortants = this.getSortants();
    Map<Noeud, Arc> noeuds = new HashMap<Noeud, Arc>();
    Arc arc;
    int i;

    for (i = 0; i < arcsEntrants.size(); i++) {
      arc = arcsEntrants.get(i);
      if ((arc.getOrientation() == -1) || (arc.getOrientation() == 2)) {
        noeuds.put(arc.getOtherSide(this), arc);
      }
    }
    for (i = 0; i < arcsSortants.size(); i++) {
      arc = arcsSortants.get(i);
      if ((arc.getOrientation() == 1) || (arc.getOrientation() == 2)) {
        noeuds.put(arc.getOtherSide(this), arc);
      }
    }
    return noeuds;
  }

  // ////////////////////////////////////////////////////////////////////////
  // Gestion de type carte topologique
  // ////////////////////////////////////////////////////////////////////////
  // Les arcs sont classés autour d'un noeud en fonction de leur géométrie.
  // Ceci permet en particulier de parcourir facilement les cycles d'un
  // graphe.
  // ////////////////////////////////////////////////////////////////////////

  /**
   * Arcs incidents à un noeuds, classés en tournant autour du noeud dans
   * l'ordre trigonométrique, et qualifiés d'entrants ou sortants, au sens de la
   * géométrie (utile particulièrement à la gestion des boucles).
   * 
   * NB : renvoie une liste de liste : Liste.get(0) = liste des arcs (de la
   * classe 'Arc') Liste.get(1) = liste des orientations de type Boolean, true =
   * entrant, false = sortant) NB : Classement effectué sur la direction donnée
   * par le premier point de l'arc après le noeud. NB : Le premier arc est celui
   * dont la direction est la plus proche de l'axe des X, en tournant dans le
   * sens trigo. NB : Ce classement est recalculé en fonction de la géométrie à
   * chaque appel de la méthode.
   */
  public List<Object> arcsClasses() {
    List<Arc> arcsClasses = new ArrayList<Arc>(0);
    List<Boolean> arcsClassesOrientation = new ArrayList<Boolean>(0);
    List<Arc> arcsEntrants = new ArrayList<Arc>(this.getEntrants());
    List<Arc> arcsSortants = new ArrayList<Arc>(this.getSortants());
    List<Arc> arcs = new ArrayList<Arc>(0);
    List<Angle> angles = new ArrayList<Angle>(0);
    List<Boolean> orientations = new ArrayList<Boolean>(0);
    List<Object> resultat = new ArrayList<Object>(0);
    Arc arc;
    Angle angle;
    double angleMin, angleCourant;
    int imin;
    Iterator<Arc> itArcs;
    int i;

    // recherche de l'angle de départ de chaque arc sortant
    itArcs = arcsSortants.iterator();
    while (itArcs.hasNext()) {
      arc = itArcs.next();
      if (arc.getCoord().size() < 2)
        continue;
      angle = new Angle(arc.getCoord().get(0), arc.getCoord().get(1));
      arcs.add(arc);
      angles.add(angle);
      orientations.add(Boolean.FALSE);
    }
    // recherche de l'angle de départ de chaque arc entrant
    itArcs = arcsEntrants.iterator();
    while (itArcs.hasNext()) {
      arc = itArcs.next();
      if (arc.getCoord().size() < 2)
        continue;
      angle = new Angle(arc.getCoord().get(arc.getCoord().size() - 1), arc
          .getCoord().get(arc.getCoord().size() - 2));
      arcs.add(arc);
      angles.add(angle);
      orientations.add(Boolean.TRUE);
    }
    // classement des arcs
    while (!(arcs.isEmpty())) {
      angleMin = angles.get(0).getValeur();
      imin = 0;
      for (i = 1; i < arcs.size(); i++) {
        angleCourant = angles.get(i).getValeur();
        if (angleCourant < angleMin) {
          angleMin = angleCourant;
          imin = i;
        }
      }
      arcsClasses.add(arcs.get(imin));
      arcsClassesOrientation.add(orientations.get(imin));
      arcs.remove(imin);
      angles.remove(imin);
      orientations.remove(imin);
    }
    // retour du résultat
    resultat.add(arcsClasses);
    resultat.add(arcsClassesOrientation);
    return resultat;
  }

  // ////////////////////////////////////////////////////////////////////////
  // Gestion des groupes
  // ////////////////////////////////////////////////////////////////////////
  /** Groupes auquels self appartient */
  private List<Groupe> listeGroupes = new ArrayList<Groupe>(0);

  /** Renvoie la liste des groupes de self */
  public List<Groupe> getListeGroupes() {
    return this.listeGroupes;
  }

  /** définit la liste des groupes de self */
  public void setListeGroupes(List<Groupe> liste) {
    this.listeGroupes = liste;
  }

  /** Ajoute un groupe à self */
  public void addGroupe(Groupe groupe) {
    if (groupe != null && !this.listeGroupes.contains(groupe)) {
      this.listeGroupes.add(groupe);
      if (!groupe.getListeNoeuds().contains(this)) {
        groupe.addNoeud(this);
      }
    }
  }

  /**
   * Liste des noeuds voisins de self au sein d'un groupe. Renvoie une liste
   * vide si il n'y en a pas.
   */
  public List<Noeud> voisins(Groupe groupe) {
    List<Noeud> noeudsVoisins = new ArrayList<Noeud>();
    List<Noeud> noeudsDuGroupe = groupe.getListeNoeuds();
    List<Arc> arcsDuGroupe = groupe.getListeArcs();
    // gestion des arcs entrants
    List<Arc> arcsVoisins = new ArrayList<Arc>(this.getEntrants());
    arcsVoisins.retainAll(arcsDuGroupe);
    for (int i = 0; i < arcsVoisins.size(); i++) {
      Arc arcVoisin = arcsVoisins.get(i);
      Noeud noeudVoisin = arcVoisin.getNoeudIni();
      if (noeudVoisin == null) {
        continue;
      }
      if (noeudsDuGroupe.contains(noeudVoisin)
          && !noeudsVoisins.contains(noeudVoisin)) {
        noeudsVoisins.add(noeudVoisin);
      }
    }
    // gestion des arcs sortants
    arcsVoisins = new ArrayList<Arc>(this.getSortants());
    arcsVoisins.retainAll(arcsDuGroupe);
    for (int i = 0; i < arcsVoisins.size(); i++) {
      Arc arcVoisin = arcsVoisins.get(i);
      Noeud noeudVoisin = arcVoisin.getNoeudFin();
      if (noeudVoisin == null) {
        continue;
      }
      if (noeudsDuGroupe.contains(noeudVoisin)
          && !noeudsVoisins.contains(noeudVoisin)) {
        noeudsVoisins.add(noeudVoisin);
      }
    }
    return noeudsVoisins;
  }

  // ////////////////////////////////////////////////////////////////////////
  // opérateurs de calculs sur les noeuds
  // ////////////////////////////////////////////////////////////////////////
  /** Distance euclidienne. Valable pour des coordonnées en 2 ou 3D. */
  public double distance(Noeud noeud) {
    return this.getCoord().distance(noeud.getCoord());
  }

  /** Distance euclidienne dans le plan (x,y). */
  public double distance2D(Noeud noeud) {
    return this.getCoord().distance2D(noeud.getCoord());
  }

  /** Distance euclidienne à un arc. */
  public double distance(Arc arc) {
    return Distances.distance(this.getCoord(), arc.getGeometrie());
  }

  // ////////////////////////////////////////////////////////////////////////
  // différentes variantes du plus court chemin
  // ////////////////////////////////////////////////////////////////////////

  // attributs internes utiles pour les calculs de plus court chemin
  /** utilisation interne : ne pas utiliser */
  private double distance;
  /** utilisation interne : ne pas utiliser */
  private Arc arcPrecedent;
  /** utilisation interne : ne pas utiliser */
  private Noeud noeudPrecedent;

  /**
   * Plus court chemin de this vers arrivée, en tenant compte du sens de
   * circulation. Le pcc s'appuie sur l'attribut 'poids' des arcs, qui doit être
   * rempli auparavant.
   * 
   * @param maxLongueur Pour optimiser: on arrête de chercher et on renvoie null
   *          si il n'y a pas de pcc de taille inférieure à maxLongueur (inactif
   *          si maxLongueur = 0).
   * 
   * @return Renvoie un groupe, qui contient (dans l'ordre) les noeuds et arcs
   *         du plus court chemin. Cas particuliers : Si this = arrivée, renvoie
   *         un groupe contenant uniquement self; Si this et arrivée sont sur 2
   *         composantes connexes distinctes (pas de pcc), renvoie null.
   * 
   *         NB : l'attribut orientation DOIT etre renseigné. NB : ce groupe
   *         contient le noeud de départ et le noeud d'arrivée.
   */
  public Groupe plusCourtChemin(Noeud arrivee, double maxLongueur) {

    // Noeud.logger.debug("shortest path between " + this.getCoord() + " - " + arrivee.getCoord());

    List<Noeud> noeudsFinaux = new ArrayList<Noeud>(0);
    List<Arc> arcsFinaux = new ArrayList<Arc>(0);
    List<Noeud> noeudsVoisins = new ArrayList<Noeud>(0);
    List<Arc> arcsVoisins = new ArrayList<Arc>(0);
    List<Double> distancesVoisins = new ArrayList<Double>(0);
    List<Noeud> traites = new ArrayList<Noeud>(0);
    List<Noeud> aTraiter = new ArrayList<Noeud>(0);
    int i;
    Arc arcVoisin;
    Noeud noeudVoisin, plusProche, suivant;
    double dist;

    try {

      if (this.getCarteTopo() == null) {
        Noeud.logger.error("ATTENTION : le noeud " + this + " ne fait pas partie d'une carte topo");
        Noeud.logger.error("            Impossible de calculer un plus court chemin");
        return null;
      }

      if (this.getCarteTopo().getPopGroupes() == null) {
        Noeud.logger.error("ATTENTION : le noeud " + this
            + " fait partie d'une carte topo sans population de groupes");
        Noeud.logger.error("            Impossible de calculer un plus court chemin");
        return null;
      }

      Groupe plusCourtChemin = this.getCarteTopo().getPopGroupes().nouvelElement();
      
      // Schema ??? - Quels attributs ???
      plusCourtChemin.setSchema(this.getSchema());
      
      
      if (this == arrivee) {
          Noeud.logger.debug("node is arrival");
          plusCourtChemin.addNoeud(this);
          this.addGroupe(plusCourtChemin);
          return plusCourtChemin;
      }

      this.distance = 0;
      this.chercheArcsNoeudsVoisins(noeudsVoisins, distancesVoisins, arcsVoisins);
      // logger.info("voisins " + noeudsVoisins.size());

      for (i = 0; i < noeudsVoisins.size(); i++) {
        noeudVoisin = noeudsVoisins.get(i);
        arcVoisin = arcsVoisins.get(i);
        dist = distancesVoisins.get(i).doubleValue();
        noeudVoisin.distance = dist;
        noeudVoisin.arcPrecedent = arcVoisin;
        noeudVoisin.noeudPrecedent = this;
      }
      aTraiter.addAll(noeudsVoisins);

      // Phase "avant"
      while (!aTraiter.isEmpty()) {
        // choisi le noeud à marquer comme traité parmi les voisins
        plusProche = aTraiter.get(0);
        for (i = 1; i < aTraiter.size(); i++) {
          if (aTraiter.get(i).distance < plusProche.distance) {
            plusProche = aTraiter.get(i);
          }
        }
        // logger.info("plus proche " + plusProche);
        traites.add(plusProche);
        aTraiter.remove(plusProche);
        // il s'agit du noeud d'arrivée
        if (plusProche == arrivee) {
          // logger.info("arrivé !!!");
          break;
        }
        if (maxLongueur != 0) {
          if (plusProche.distance > maxLongueur) {
            // Noeud.logger.debug("Trop long, on s'arrête");
            return null; // heuristique pour stopper la recherche
          }
        }
        plusProche.chercheArcsNoeudsVoisins(noeudsVoisins, distancesVoisins, arcsVoisins);
        // logger.info("voisins " + noeudsVoisins.size());
        for (i = 0; i < noeudsVoisins.size(); i++) {
          noeudVoisin = noeudsVoisins.get(i);
          arcVoisin = arcsVoisins.get(i);
          dist = distancesVoisins.get(i).doubleValue();
          if (traites.contains(noeudVoisin)) {
            continue; // Noeud déjà traité
          }
          if (aTraiter.contains(noeudVoisin)) {
            // Noeud déjà atteint, on voit si on a trouvé un chemin
            // plus court pour y accèder
            if (noeudVoisin.distance > (plusProche.distance + dist)) {
              noeudVoisin.distance = plusProche.distance + dist;
              noeudVoisin.arcPrecedent = arcVoisin;
              noeudVoisin.noeudPrecedent = plusProche;
            }
            continue;
          }
          // Nouveau noeud atteint, on l'initialise
          noeudVoisin.distance = plusProche.distance + dist;
          noeudVoisin.arcPrecedent = arcVoisin;
          noeudVoisin.noeudPrecedent = plusProche;
          aTraiter.add(noeudVoisin);
        }
      }

      // Phase "arriere"
      if (!traites.contains(arrivee)) {
        Noeud.logger.debug("couldn't reach it");
        return null;
      }
      suivant = arrivee;
      while (true) {
        arcsFinaux.add(0, suivant.arcPrecedent);
        suivant.arcPrecedent.addGroupe(plusCourtChemin);
        suivant = suivant.noeudPrecedent;
        if (suivant == this) {
          break;
        }
        noeudsFinaux.add(0, suivant);
        suivant.addGroupe(plusCourtChemin);
      }
      noeudsFinaux.add(0, this);
      this.addGroupe(plusCourtChemin);
      noeudsFinaux.add(arrivee);
      arrivee.addGroupe(plusCourtChemin);
      plusCourtChemin.setListeArcs(arcsFinaux);
      plusCourtChemin.setListeNoeuds(noeudsFinaux);
      plusCourtChemin.setLength(arrivee.distance);
      return plusCourtChemin;
    } catch (Exception e) {
      Noeud.logger.error("----- ERREUR dans calcul de plus court chemin.");
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Méthode utile au plus court chemin.
   * @param noeudsVoisins
   * @param distancesVoisins
   * @param arcsVoisins
   */
  private void chercheArcsNoeudsVoisins(List<Noeud> noeudsVoisins,
      List<Double> distancesVoisins, List<Arc> arcsVoisins) {
    // au sens de la geometrie
    List<Arc> arcsEntrants = new ArrayList<Arc>(0);
    // au sens de la geometrie
    List<Arc> arcsSortants = new ArrayList<Arc>(0);
    // au sens de la circulation
    List<Arc> arcsSortants2 = new ArrayList<Arc>(0);
    // au sens de la circulation
    List<Noeud> noeudsSortants2 = new ArrayList<Noeud>(0);
    // au sens de la circulation
    List<Double> distancesSortants2 = new ArrayList<Double>(0);
    noeudsVoisins.clear();
    distancesVoisins.clear();
    arcsVoisins.clear();
    try {
      arcsEntrants = this.getEntrants();
      arcsSortants = this.getSortants();
    } catch (Exception e) {
      e.printStackTrace();
    }
    // transformation du sens géométrique au sens de circulation
    for (Arc arc : arcsEntrants) {
      if ((arc.getOrientation() == -1) || (arc.getOrientation() == 2)) {
        if (arc.getNoeudIni() != null) {
          arcsSortants2.add(arc);
          noeudsSortants2.add(arc.getNoeudIni());
          distancesSortants2.add(arc.getPoids());
        }
      }
    }
    for (Arc arc : arcsSortants) {
      if ((arc.getOrientation() == 1) || (arc.getOrientation() == 2)) {
        if (arc.getNoeudFin() != null) {
          arcsSortants2.add(arc);
          noeudsSortants2.add(arc.getNoeudFin());
          distancesSortants2.add(arc.getPoids());
        }
      }
    }
    // en choisissant l'arc le plus court, si il existe des arcs
    // parallèles (mêmes noeuds ini et fin)
    for (int i = 0; i < noeudsSortants2.size(); i++) {
      // choix du plus court arc menant au noeud sortant
      Noeud noeud = noeudsSortants2.get(i);
      if (noeudsVoisins.contains(noeud)) {
        continue;
      }
      Arc arc = arcsSortants2.get(i);
      Double distance = distancesSortants2.get(i);
      for (int j = i + 1; j < noeudsSortants2.size(); j++) {
        if (noeud == noeudsSortants2.get(j)) {
          if (distancesSortants2.get(j).doubleValue() < distance.doubleValue()) {
            distance = distancesSortants2.get(j);
            arc = arcsSortants2.get(j);
          }
        }
      }
      arcsVoisins.add(arc);
      noeudsVoisins.add(noeud);
      distancesVoisins.add(distance);
    }
    // logger.debug("neighboorhood for node " + this);
    // for (int i = 0; i < arcsVoisins.size(); i++) {
    // logger.debug("\t" + arcsVoisins.get(i));
    // logger.debug("\t" + noeudsVoisins.get(i));
    // logger.debug("\t" + distancesVoisins.get(i));
    // }
  }

  /**
   * Plus court chemin de this vers arrivée, en tenant compte du sens de
   * circulation, au sein d'un groupe d'arcs et de noeuds. Le pcc s'appuie sur
   * l'attribut 'poids' des arcs, qui doit être rempli auparavant.
   * 
   * @param maxLongueur Pour optimiser: on arrête de chercher et on renvoie null
   *          si il n'y a pas de pcc de taille inférieure à maxLongueur (inactif
   *          si maxLongueur = 0).
   * 
   * @return Renvoie un groupe, qui contient (dans l'ordre) les noeuds et arcs
   *         du plus court chemin. Cas particuliers : Si this = arrivée, renvoie
   *         un groupe contenant uniquement self; Si this et arrivée sont sur 2
   *         composantes connexes distinctes (pas de pcc), renvoie null.
   * 
   *         NB : l'attribut orientation DOIT etre renseigné. NB : ce groupe
   *         contient le noeud de départ et le noeud d'arrivée.
   */
  public Groupe plusCourtChemin(Noeud arrivee, Groupe groupe, double maxLongueur) {
    List<Noeud> noeudsFinaux = new ArrayList<Noeud>(0);
    List<Arc> arcsFinaux = new ArrayList<Arc>(0);
    List<Noeud> noeudsVoisins = new ArrayList<Noeud>(0);
    List<Arc> arcsVoisins = new ArrayList<Arc>(0);
    List<Double> distancesVoisins = new ArrayList<Double>(0);
    List<Noeud> traites = new ArrayList<Noeud>(0);
    List<Noeud> aTraiter = new ArrayList<Noeud>(0);
    int i;
    Arc arcVoisin;
    Noeud noeudVoisin, plusProche, suivant;
    double dist;

    try {
      if (this.getCarteTopo() == null) {
        System.out.println("ATTENTION : le noeud " + this
            + " ne fait pas partie d'une carte topo");
        System.out
            .println("            Impossible de calculer un plus court chemin");
        return null;
      }
      if (this.getCarteTopo().getPopGroupes() == null) {
        System.out.println("ATTENTION : le noeud " + this
            + " fait partie d'une carte topo sans population de groupes");
        System.out
            .println("            Impossible de calculer un plus court chemin");
        return null;
      }
      Groupe plusCourtChemin = this.getCarteTopo().getPopGroupes()
          .nouvelElement();

      if (this == arrivee) {
        plusCourtChemin.addNoeud(this);
        this.addGroupe(plusCourtChemin);
        return plusCourtChemin;
      }
      this.distance = 0;
      this.chercheArcsNoeudsVoisins(groupe, noeudsVoisins, distancesVoisins,
          arcsVoisins);
      for (i = 0; i < noeudsVoisins.size(); i++) {
        noeudVoisin = noeudsVoisins.get(i);
        arcVoisin = arcsVoisins.get(i);
        dist = distancesVoisins.get(i).doubleValue();
        noeudVoisin.distance = dist;
        noeudVoisin.arcPrecedent = arcVoisin;
        noeudVoisin.noeudPrecedent = this;
      }
      aTraiter.addAll(noeudsVoisins);

      // Phase "avant"
      while (aTraiter.size() != 0) {

        // choisi le noeud à marquer comme traité parmi les voisins
        plusProche = aTraiter.get(0);
        for (i = 1; i < aTraiter.size(); i++) {
          if (aTraiter.get(i).distance < plusProche.distance) {
            plusProche = aTraiter.get(i);
          }
        }

        traites.add(plusProche);
        aTraiter.remove(plusProche);
        if (plusProche == arrivee) {
          break; // il s'agit du noeud d'arrivée
        }
        if (maxLongueur != 0) {
          if (plusProche.distance > maxLongueur) {
            return null; // heuristique pour stopper la recherche
          }
        }

        plusProche.chercheArcsNoeudsVoisins(groupe, noeudsVoisins,
            distancesVoisins, arcsVoisins);
        for (i = 0; i < noeudsVoisins.size(); i++) {
          noeudVoisin = noeudsVoisins.get(i);
          arcVoisin = arcsVoisins.get(i);
          dist = distancesVoisins.get(i).doubleValue();
          if (traites.contains(noeudVoisin)) {
            continue; // Noeud déjà traité
          }
          if (aTraiter.contains(noeudVoisin)) {
            // Noeud déjà atteint, on voit si on a trouvé un chemin
            // plus court pour y accèder
            if (noeudVoisin.distance > plusProche.distance + dist) {
              noeudVoisin.distance = plusProche.distance + dist;
              noeudVoisin.arcPrecedent = arcVoisin;
              noeudVoisin.noeudPrecedent = plusProche;
            }
            continue;
          }
          // Nouveau noeud atteint, on l'initialise
          noeudVoisin.distance = plusProche.distance + dist;
          noeudVoisin.arcPrecedent = arcVoisin;
          noeudVoisin.noeudPrecedent = plusProche;
          aTraiter.add(noeudVoisin);
        }
      }

      // Phase "arriere"
      if (!traites.contains(arrivee)) {
        return null;
      }
      suivant = arrivee;
      while (true) {
        arcsFinaux.add(0, suivant.arcPrecedent);
        suivant.arcPrecedent.addGroupe(plusCourtChemin);
        suivant = suivant.noeudPrecedent;
        if (suivant == this) {
          break;
        }
        noeudsFinaux.add(0, suivant);
        suivant.addGroupe(plusCourtChemin);
      }

      noeudsFinaux.add(0, this);
      this.addGroupe(plusCourtChemin);
      noeudsFinaux.add(arrivee);
      arrivee.addGroupe(plusCourtChemin);

      plusCourtChemin.setListeArcs(arcsFinaux);
      plusCourtChemin.setListeNoeuds(noeudsFinaux);
      return plusCourtChemin;
    } catch (Exception e) {
      System.out.println("----- ERREUR dans calcul de plus court chemin.");
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Méthode utile au plus court chemin.
   * @param groupe
   * @param noeudsVoisins
   * @param distancesVoisins
   * @param arcsVoisins
   */
  private void chercheArcsNoeudsVoisins(Groupe groupe,
      List<Noeud> noeudsVoisins, List<Double> distancesVoisins,
      List<Arc> arcsVoisins) {
    // au sens de la geometrie
    List<Arc> arcsEntrants = new ArrayList<Arc>(0);
    // au sens de la geometrie
    List<Arc> arcsSortants = new ArrayList<Arc>(0);
    // au sens de la circulation
    List<Arc> arcsSortants2 = new ArrayList<Arc>(0);
    // au sens de la circulation
    List<Noeud> noeudsSortants2 = new ArrayList<Noeud>(0);
    // au sens de la circulation
    List<Double> distancesSortants2 = new ArrayList<Double>(0);
    Noeud noeud;
    Arc arc;
    Double distance;
    int i, j;

    noeudsVoisins.clear();
    distancesVoisins.clear();
    arcsVoisins.clear();

    arcsEntrants = this.getEntrants();
    arcsSortants = this.getSortants();

    // transformation du sens géométrique au sens de circulation
    for (i = 0; i < arcsEntrants.size(); i++) {
      arc = arcsEntrants.get(i);
      if (groupe.getListeArcs().contains(arc)) {
        if ((arc.getOrientation() == -1) || (arc.getOrientation() == 2)) {
          if (arc.getNoeudIni() != null) {
            arcsSortants2.add(arc);
            noeudsSortants2.add(arc.getNoeudIni());
            distancesSortants2.add(arc.getPoids());
          }
        }
      }
    }
    for (i = 0; i < arcsSortants.size(); i++) {
      arc = arcsSortants.get(i);
      if (groupe.getListeArcs().contains(arc)) {
        if ((arc.getOrientation() == 1) || (arc.getOrientation() == 2)) {
          if (arc.getNoeudFin() != null) {
            arcsSortants2.add(arc);
            noeudsSortants2.add(arc.getNoeudFin());
            distancesSortants2.add(arc.getPoids());
          }
        }
      }
    }

    // en choisissant l'arc le plus court, si il existe des arcs parallèles
    // (mêmes noeuds ini et fin)
    for (i = 0; i < noeudsSortants2.size(); i++) {
      // choix du plus court arc menant au noeud sortant
      noeud = noeudsSortants2.get(i);
      if (noeudsVoisins.contains(noeud)) {
        continue;
      }
      arc = arcsSortants2.get(i);
      distance = distancesSortants2.get(i);
      for (j = i + 1; j < noeudsSortants2.size(); j++) {
        if (noeud == noeudsSortants2.get(j)) {
          if (distancesSortants2.get(j).doubleValue() < distance.doubleValue()) {
            distance = distancesSortants2.get(j);
            arc = arcsSortants2.get(j);
          }
        }
      }
      arcsVoisins.add(arc);
      noeudsVoisins.add(noeud);
      distancesVoisins.add(distance);
    }
  }

  // /////////////////////////////////////////////////////
  // DIVERS
  // /////////////////////////////////////////////////////

  /**
   * Direction (Angle entre 0 et 2PI) de l'arc à la sortie du noeud this. Cette
   * direction est calculée à partir d'une partie de l'arc d'une certaine
   * longueur (paramètre), et en ré-échantillonant l'arc (paramètre). Si l'arc
   * n'a pas pour noeud initial ou final this: renvoie null.
   * 
   * @param longueurEspaceTravail Longueur curviligne qui détermine l'espace de
   *          travail autour du noeud, Si elle est égale à 0: les deux premiers
   *          points de l'arc sont considérés.
   * 
   * @param pasEchantillonage Avant le calcul de la direction moyenne des
   *          points, la ligne est ré-échantillonée à ce pas. Si égal à 0: aucun
   *          échantillonage n'est effectué
   * 
   */
  public Angle directionArc(Arc arc, double longueurEspaceTravail,
      double pasEchantillonage) {
    IDirectPositionList listePts, arcEchantillone;
    int nbPts;
    if (arc.getNoeudFin() == this) {
      listePts = Operateurs.derniersPoints(arc.getGeometrie(),
          longueurEspaceTravail);
      if (listePts.size() < 2) {
        nbPts = arc.getGeometrie().coord().size();
        listePts.add(arc.getGeometrie().coord().get(nbPts - 2));
      }
    } else {
      if (arc.getNoeudIni() == this) {
        listePts = Operateurs.premiersPoints(arc.getGeometrie(),
            longueurEspaceTravail);
        if (listePts.size() < 2) {
          listePts.add(arc.getGeometrie().coord().get(1));
        }
      } else {
        return null;
      }
    }

    if (pasEchantillonage == 0) {
      arcEchantillone = listePts;
    } else {
      arcEchantillone = Operateurs.echantillonePasVariable(
          new GM_LineString(listePts), pasEchantillonage).coord();
    }
    return Operateurs.directionPrincipaleOrientee(arcEchantillone);
  }

  @Override
  public String toString() {
    return "Noeud" + " " + this.getId() + " - " //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        + this.getGeometrie();
  }

  /**
   * @return the distance
   */
  public double getDistance() {
    return this.distance;
  }

  /**
   * @param distance the distance to set
   */
  public void setDistance(double distance) {
    this.distance = distance;
  }

  /**
   * Supprime un arc du noeud.
   * @param arc
   */
  public void enleveArc(Arc arc) {
    if (this.entrants.contains(arc)) {
      this.entrants.remove(arc);
    } else {
      this.sortants.remove(arc);
    }
  }

  /**
   * Copie un noeud
   * @return
   */
  public Noeud copy() {
    Noeud noeud = new Noeud(this.getCoord());
    for (Arc a : this.getEntrants()) {
      noeud.addEntrant(a);
    }
    for (Arc a : this.getSortants()) {
      noeud.addSortant(a);
    }
    noeud.setGeometrie(new GM_Point(this.getCoord()));
    return noeud;
  }
}
