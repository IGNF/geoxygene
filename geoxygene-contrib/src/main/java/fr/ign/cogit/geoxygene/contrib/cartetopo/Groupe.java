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
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;

/**
 * Classe des groupes de la carte topo. Un groupe est une composition de noeuds,
 * d'arcs et de faces.
 * <p>
 * English: a group is a set of nodes/arcs/faces of a topological map.
 * 
 * // ///////////////////////////////////////////////// // Pour les relations de
 * composition : // - un groupe contient PLUSIEURS noeuds, arcs et faces // - un
 * groupe appartient à UNE carte topo //
 * /////////////////////////////////////////////////
 * 
 * @author Sébastien Mustière
 * @author Olivier Bonin
 * @version 1.0
 */
public class Groupe extends ElementCarteTopo {

  /** Logger. */
  protected final static Logger LOGGER = LogManager
      .getLogger(Groupe.class
      .getName());

  private double length = 0.0;
  /** Noeuds composants du groupe. */
  private List<Noeud> listeNoeuds = new ArrayList<Noeud>(0);
  /** Arcs composants du groupe. */
  private List<Arc> listeArcs = new ArrayList<Arc>(0);
  /** Faces composants du groupe. */
  private List<Face> listeFaces = new ArrayList<Face>(0);

  /**
   * Default constructor
   */
  public Groupe() {
  }

  public double getLength() {
    return this.length;
  }

  public void setLength(double length) {
    this.length = length;
  }

  /** Renvoie la liste des noeuds de self. */
  public List<Noeud> getListeNoeuds() {
    return this.listeNoeuds;
  }

  /** Définit la liste des noeuds de self. */
  public void setListeNoeuds(List<Noeud> liste) {
    this.listeNoeuds = liste;
  }

  /** Ajoute un noeud à self. */
  public void addNoeud(Noeud noeud) {
    this.addNoeud(noeud, true);
  }

  /** Ajoute un noeud à self. */
  public void addNoeud(Noeud noeud, boolean addGroupToNode) {
    if (noeud != null && !this.listeNoeuds.contains(noeud)) {
      this.listeNoeuds.add(noeud);
      if (addGroupToNode && !noeud.getListeGroupes().contains(this)) {
        noeud.addGroupe(this);
      }
    }
  }

  /** Ajoute une liste de noeuds à self **/
  public void addAllNoeuds(List<Noeud> liste) {
    this.addAllNoeuds(liste, true);
  }

  /** Ajoute une liste de noeuds à self **/
  public void addAllNoeuds(List<Noeud> liste, boolean addGroupToNodes) {
    Iterator<Noeud> itObj = liste.iterator();
    while (itObj.hasNext()) {
      Noeud objet = itObj.next();
      this.addNoeud(objet, addGroupToNodes);
    }
  }

  /** Renvoie la liste des arcs de self */
  public List<Arc> getListeArcs() {
    return this.listeArcs;
  }

  /** Définit la liste des arcs de self */
  public void setListeArcs(List<Arc> liste) {
    this.listeArcs = liste;
  }

  /** Ajoute un arc de self */
  public void addArc(Arc arc) {
    this.addArc(arc, true);
  }

  /** Ajoute un arc de self */
  public void addArc(Arc arc, boolean addGroupToEdge) {
    if (arc != null && !this.listeArcs.contains(arc)) {
      this.listeArcs.add(arc);
      if (addGroupToEdge && !arc.getListeGroupes().contains(this)) {
        arc.addGroupe(this);
      }
    }
  }

  /** Ajoute une liste d'arcs à self **/
  public void addAllArcs(List<Arc> liste) {
    this.addAllArcs(liste, true);
  }

  /** Ajoute une liste d'arcs à self **/
  public void addAllArcs(List<Arc> liste, boolean addGroupToEdges) {
    for (Arc arc : liste) {
      this.addArc(arc, addGroupToEdges);
    }
  }

  /** Renvoie la liste des faces de self */
  public List<Face> getListeFaces() {
    return this.listeFaces;
  }

  /** Définit la liste des faces de self */
  public void setListeFaces(List<Face> liste) {
    this.listeFaces = liste;
  }

  /** Ajoute une face à self */
  public void addFace(Face face) {
    this.addFace(face, true);
  }

  /** Ajoute une face à self */
  public void addFace(Face face, boolean addGroupToFace) {
    if (face != null && !this.listeFaces.contains(face)) {
      this.listeFaces.add(face);
      if (addGroupToFace && !face.getListeGroupes().contains(this)) {
        face.addGroupe(this);
      }
    }
  }

  /** Ajoute une liste de faces à self **/
  public void addAllFaces(List<Face> liste) {
    this.addAllFaces(liste, true);
  }

  /** Ajoute une liste de faces à self **/
  public void addAllFaces(List<Face> liste, boolean addGroupToFaces) {
    for (Face face : liste) {
      this.addFace(face, addGroupToFaces);
    }
  }

  // /////////////////////////////////////////////////
  // Pour les relations topologiques dans une vision Groupe = Hyper Noeud
  // /////////////////////////////////////////////////

  /**
   * Arcs entrants dans le groupe, au sens de la géométrie (vision groupe =
   * hyper-noeud)
   */
  public List<Arc> getEntrants() {
    List<Arc> arcs = new ArrayList<Arc>(0);
    for (int i = 0; i < this.getListeNoeuds().size(); i++) {
      List<Arc> arcsDuNoeud = (this.getListeNoeuds().get(i)).getEntrants();
      for (int j = 0; j < arcsDuNoeud.size(); j++) {
        if (!this.getListeArcs().contains(arcsDuNoeud.get(j))) {
          arcs.add(arcsDuNoeud.get(j));
        }
      }
    }
    return arcs;
  }

  /**
   * Arcs sortants du groupe, au sens de la géométrie (vision groupe =
   * hyper-noeud)
   */
  public List<Arc> getSortants() {
    List<Arc> arcs = new ArrayList<Arc>(0);
    for (int i = 0; i < this.getListeNoeuds().size(); i++) {
      List<Arc> arcsDuNoeud = (this.getListeNoeuds().get(i)).getSortants();
      for (int j = 0; j < arcsDuNoeud.size(); j++) {
        if (!this.getListeArcs().contains(arcsDuNoeud.get(j))) {
          arcs.add(arcsDuNoeud.get(j));
        }
      }
    }
    return arcs;
  }

  /**
   * Arcs adjacents (entrants et sortants) de self (vision groupe =
   * hyper-noeud). NB : si un arc est à la fois entrant et sortant (boucle), il
   * est 2 fois dans la liste
   */
  public List<Arc> getAdjacents() {
    List<Arc> arcs = new ArrayList<Arc>(this.getSortants().size()
        + this.getEntrants().size());
    arcs.addAll(this.getSortants());
    arcs.addAll(this.getEntrants());
    return arcs;
  }

  // /////////////////////////////////////////////////
  // Pour les relations topologiques dans une vision Groupe = Hyper Noeud,
  // en tenant compte du sens de circulation
  // /////////////////////////////////////////////////
  /**
   * Arcs entrants dans le groupe, au sens de la géométrie (vision groupe =
   * hyper-noeud)
   */
  public List<Arc> entrantsOrientes() {
    List<Arc> arcs = new ArrayList<Arc>(0);
    for (int i = 0; i < this.getListeNoeuds().size(); i++) {
      List<Arc> arcsDuNoeud = (this.getListeNoeuds().get(i)).entrantsOrientes();
      for (int j = 0; j < arcsDuNoeud.size(); j++) {
        if (!this.getListeArcs().contains(arcsDuNoeud.get(j))) {
          arcs.add(arcsDuNoeud.get(j));
        }
      }
    }
    return arcs;
  }

  /**
   * Arcs sortants du groupe, au sens de la géométrie (vision groupe =
   * hyper-noeud)
   */
  public List<Arc> sortantsOrientes() {
    List<Arc> arcs = new ArrayList<Arc>(0);
    for (int i = 0; i < this.getListeNoeuds().size(); i++) {
      List<Arc> arcsDuNoeud = (this.getListeNoeuds().get(i)).sortantsOrientes();
      for (int j = 0; j < arcsDuNoeud.size(); j++) {
        if (!this.getListeArcs().contains(arcsDuNoeud.get(j))) {
          arcs.add(arcsDuNoeud.get(j));
        }
      }
    }
    return arcs;
  }

  /**
   * Arcs incidents à un noeuds, classés en tournant autour du noeud dans
   * l'ordre trigonométrique, et qualifiés d'entrants ou sortants, au sens de la
   * géoémtrie (utile particulièrement à la gestion des boucles). NB : renvoie
   * une liste de liste: Liste.get(0) = liste des arcs (de la classe 'Arc')
   * Liste.get(1) = liste des orientations de type Boolean, true = entrant,
   * false = sortant) NB : Classement effectué sur la direction donnée par le
   * premier point de l'arc après le noeud. NB : Le premier arc est celui dont
   * la direction est la plus proche de l'axe des X, en tournant dans le sens
   * trigo. NB : Ce classement est recalculé en fonction de la géométrie à
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
    Iterator<Arc> itArcs;
    // recherche de l'angle de départ de chaque arc sortant
    itArcs = arcsSortants.iterator();
    while (itArcs.hasNext()) {
      arc = itArcs.next();
      angle = new Angle(arc.getCoord().get(0), arc.getCoord().get(1));
      arcs.add(arc);
      angles.add(angle);
      orientations.add(Boolean.FALSE);
    }
    // recherche de l'angle de départ de chaque arc entrant
    itArcs = arcsEntrants.iterator();
    while (itArcs.hasNext()) {
      arc = itArcs.next();
      angle = new Angle(arc.getCoord().get(arc.getCoord().size() - 1), arc
          .getCoord().get(arc.getCoord().size() - 2));
      arcs.add(arc);
      angles.add(angle);
      orientations.add(Boolean.TRUE);
    }
    // classement des arcs
    while (!arcs.isEmpty()) {
      double angleMin = angles.get(0).getValeur();
      int imin = 0;
      for (int i = 1; i < arcs.size(); i++) {
        double angleCourant = angles.get(i).getValeur();
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

  // /////////////////////////////////////////////////
  // Méthodes de base pour manipuler un groupe
  // /////////////////////////////////////////////////
  /**
   * Pour vider un groupe, et mettre à jour les liens des objets simples vers ce
   * groupe. Vide mais ne détruit pas le groupe: i.e. ne l'enlève pas de la
   * carte topo.
   */
  public void vide() {
    for (Arc arc : this.getListeArcs()) {
      arc.getListeGroupes().remove(this);
    }
    for (Noeud noeud : this.getListeNoeuds()) {
      noeud.getListeGroupes().remove(this);
    }
    this.getListeArcs().clear();
    this.getListeNoeuds().clear();
  }

  /**
   * Pour vider un groupe, mettre à jour les liens des objets simples vers ce
   * groupe, et l'enlever des populations auxquelles il appartient. NB: ce
   * groupe n'est pas vraiment detruit, il n'est pas rendu null ; NB: rien n'est
   * géré au niveau de la persistance eventuelle.
   */
  public void videEtDetache() {
    this.vide();
    IPopulation<?> groupes = this.getPopulation();
    if (groupes != null) {
      groupes.remove(this);
    }
  }

  /**
   * Pour copier un groupe.
   * <ul>
   * <li>NB 1 : on crée un nouveau groupe pointant vers les mêmes objets
   * composants
   * <li>NB 2 : ce groupe n'est PAS ajouté à la carteTopo
   * </ul>
   */
  public Groupe copie() {
    return this.copie(true);
  }

  /**
   * Pour copier un groupe.
   * <ul>
   * <li>NB 1 : on crée un nouveau groupe pointant vers les mêmes objets
   * composants
   * <li>NB 2 : ce groupe n'est PAS ajouté à la carteTopo
   * </ul>
   */
  public Groupe copie(boolean addGroupToElements) {
    Groupe copie = new Groupe();
    // Groupe copie = (Groupe) this.getPopulation().nouvelElement();
    copie.addAllArcs(this.getListeArcs(), addGroupToElements);
    copie.addAllNoeuds(this.getListeNoeuds(), addGroupToElements);
    copie.addAllFaces(this.getListeFaces(), addGroupToElements);
    // copie.setPopulation(this.getPopulation());
    return copie;
  }

  // /////////////////////////////////////////////////
  // /////////////////////////////////////////////////
  // Opérateurs de calculs sur les groupes
  // /////////////////////////////////////////////////
  // /////////////////////////////////////////////////

  /**
   * Decompose un groupe en plusieurs groupes connexes, et vide le groupe self.
   * La liste en sortie contient des Groupes.
   * <p>
   * ATTENTION : LE GROUPE EN ENTREE EST VIDE AU COURS DE LA METHODE PUIS ENLEVE
   * DE LA CARTE TOPO.
   */
  public List<Groupe> decomposeConnexes() {
    return this.decomposeConnexes(true);
  }

  /**
   * Decompose un groupe en plusieurs groupes connexes, et vide le groupe self.
   * La liste en sortie contient des Groupes.
   * <p>
   * ATTENTION : LE GROUPE EN ENTREE EST VIDE AU COURS DE LA METHODE PUIS ENLEVE
   * DE LA CARTE TOPO.
   */
  public List<Groupe> decomposeConnexes(boolean addGroupToEdges) {
    List<Groupe> groupesConnexes = new ArrayList<Groupe>(0);
    try {
      // if (this.getPopulation() == null) {
      // Groupe.LOGGER.error("ATTENTION : le groupe " + this
      // + " n'a pas de population associée");
      // Groupe.LOGGER
      // .error("\tImpossible de le décomposer en groupes connexes");
      // return null;
      // }
      // if (this.getCarteTopo() == null) {
      // Groupe.LOGGER.error("ATTENTION : le groupe " + this
      // + " ne fait pas partie d'une carte topo");
      // Groupe.LOGGER
      // .error("\tImpossible de le décomposer en groupes connexes");
      // return null;
      // }
      // if (this.getCarteTopo().getPopArcs() == null) {
      // Groupe.LOGGER.error("ATTENTION : le groupe " + this
      // + " fait partie d'une carte topo sans population d'arcs");
      // Groupe.LOGGER
      // .error("\tImpossible de le décomposer en groupes connexes");
      // return null;
      // }
      // if (this.getCarteTopo().getPopNoeuds() == null) {
      // Groupe.LOGGER.error("ATTENTION : le groupe " + this
      // + " fait partie d'une carte topo sans population de noeuds");
      // Groupe.LOGGER
      // .error("\tImpossible de le décomposer en groupes connexes");
      // return null;
      // }
      while (!this.getListeNoeuds().isEmpty()) {
        Groupe groupeConnexe = null;
        if (this.getPopulation() == null) {
          groupeConnexe = new Groupe();
        } else {
          groupeConnexe = (Groupe) this.getPopulation().nouvelElement();
        }
        groupesConnexes.add(groupeConnexe);
        // le premier noeud de la liste des noeuds, vidée au fur et à mesure,
        // est l'amorce d'un nouveau groupe connexe
        Noeud amorce = this.getListeNoeuds().get(0);
        groupeConnexe.ajouteVoisins(amorce, this); // nb: méthode récursive
        // recherche des arcs du groupe, situés entre 2 noeuds du groupe connexe
        groupeConnexe.arcsDansGroupe(this, addGroupToEdges);
      }
      // vidage des arcs du groupe, pour faire propre (on a déjà vidé les noeuds
      // au fur et à mesure)
      for (int i = 0; i < this.getListeArcs().size(); i++) {
        Arc arc = this.getListeArcs().get(i);
        arc.getListeGroupes().remove(this);
      }
      this.getListeArcs().clear();
      if (this.getPopulation() != null) {
        this.getPopulation().enleveElement(this);
      }
      return groupesConnexes;
    } catch (Exception e) {
      Groupe.logger
          .error("----- ERREUR dans décomposition en groupes connxes: ");
      Groupe.logger
          .error("\tSource possible : Nom de la classe des groupes pas ou mal renseigné dans la carte topo");
      e.printStackTrace();
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public IPopulation<Groupe> getPopulation() {
    return (IPopulation<Groupe>) super.getPopulation();
  }

  /**
   * Methode nécessaire à DecomposeConnexe : ajoute le noeud au groupe connexe,
   * cherche ses voisins, puis l'enlève du goupe total.
   * @param noeud
   * @param groupeTotal
   */
  private void ajouteVoisins(Noeud noeud, Groupe groupeTotal) {
    if (this.getListeNoeuds().contains(noeud)) {
      return;
    }
    this.addNoeud(noeud, false);
    List<Noeud> noeudsVoisins = noeud.voisins(groupeTotal);
    groupeTotal.getListeNoeuds().remove(noeud);
    // noeud.getListeGroupes().remove(groupeTotal);
    for (int i = 0; i < noeudsVoisins.size(); i++) {
      this.ajouteVoisins(noeudsVoisins.get(i), groupeTotal);
    }
    return;
  }

  /**
   * Methode nécessaire à DecomposeConnexe : Recherche les arcs de groupeTotal
   * ayant pour extrémité des noeuds de this.
   * @param groupeTotal
   */
  @SuppressWarnings("unused")
  private void arcsDansGroupe(Groupe groupeTotal) {
    this.arcsDansGroupe(groupeTotal, true);
  }

  /**
   * Methode nécessaire à DecomposeConnexe : Recherche les arcs de groupeTotal
   * ayant pour extrémité des noeuds de this.
   * @param groupeTotal
   */
  private void arcsDansGroupe(Groupe groupeTotal, boolean addGroupToEdges) {
    for (int i = 0; i < groupeTotal.getListeArcs().size(); i++) {
      Arc arc = groupeTotal.getListeArcs().get(i);
      if (this.getListeNoeuds().contains(arc.getNoeudIni())
          || this.getListeNoeuds().contains(arc.getNoeudFin())) {
        this.addArc(arc, addGroupToEdges);
      }
    }
  }

  /** somme des longueurs des arcs du groupe. */
  public double longueur() {
    double longueur = 0;
    for (int i = 0; i < this.getListeArcs().size(); i++) {
      longueur += this.getListeArcs().get(i).longueur();
    }
    return longueur;
  }

  /**
   * Teste si le groupe contient exactement les mêmes arcs qu'un autre groupe.
   * NB: si des arcs sont en double dans un des groupes et pas dans l'autre,
   * renvoie true quand même
   */
  public boolean contientMemesArcs(Groupe groupe) {
    if (!groupe.getListeArcs().containsAll(this.getListeArcs())) {
      return false;
    }
    if (!this.getListeArcs().containsAll(groupe.getListeArcs())) {
      return false;
    }
    return true;
  }

  /**
   * Pour un groupe dont on ne connait que les arcs : ajoute les noeuds ini et
   * fin de ses arcs dans le groupe. La topologie doit avoir été instanciée.
   */
  public void ajouteNoeuds() {
    for (int i = 0; i < this.getListeArcs().size(); i++) {
      Arc arc = this.getListeArcs().get(i);
      Noeud ini = arc.getNoeudIni();
      Noeud fin = arc.getNoeudFin();
      if (ini != null) {
        if (!this.getListeNoeuds().contains(ini)) {
          this.addNoeud(ini);
          ini.addGroupe(this);
        }
      }
      if (fin != null) {
        if (!this.getListeNoeuds().contains(fin)) {
          this.addNoeud(fin);
          fin.addGroupe(this);
        }
      }
    }
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()
        + " " + this.getGeom() + " " + this.longueur(); //$NON-NLS-1$ //$NON-NLS-2$
  }
}
