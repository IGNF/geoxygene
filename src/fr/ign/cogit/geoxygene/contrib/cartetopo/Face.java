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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * Classe des faces de la carte topo. Les arcs ont pour géométrie un GM_Polygon.
 * @author Sébastien Mustière
 * @author Olivier Bonin
 * @author Julien Perret
 */
public class Face extends ElementCarteTopo {
  static Logger logger = Logger.getLogger(Face.class.getName());

  public Face() {
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////////
  // géométrie
  // ///////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Vrai s'il s'agit d'une face infinie. True if the face is infinite.
   */
  private boolean infinite = false;

  /** Renvoie le GM_Polygon qui définit la géométrie de self. */
  public GM_Polygon getGeometrie() {
    return (GM_Polygon) this.geom;
  }

  /** définit le GM_Polygon qui définit la géométrie de self. */
  public void setGeometrie(GM_Polygon geometrie) {
    this.setGeom(geometrie);
  }

  /** Renvoie la liste de DirectPosition qui définit les coordonnées de self */
  public DirectPositionList getCoord() {
    return this.getGeometrie().exteriorCoord();
  }

  // On suppose que exteriorCoordList() donne deux fois le point de départ
  /** définit la liste de DirectPosition qui définit les coordonnées de self */
  public void setCoord(DirectPositionList dpl) {
    this.geom = new GM_Polygon(new GM_LineString(dpl));
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////////
  // Gestion des groupes
  // ///////////////////////////////////////////////////////////////////////////////////////////////
  /** Groupes auquels self appartient */
  private Collection<Groupe> listeGroupes = new ArrayList<Groupe>();

  /** Renvoie la liste des groupes de self */
  public Collection<Groupe> getListeGroupes() {
    return this.listeGroupes;
  }

  /** définit la liste des groupes de self */
  public void setListeGroupes(Collection<Groupe> liste) {
    this.listeGroupes = liste;
  }

  /** Ajoute un groupe à self */
  public void addGroupe(Groupe groupe) {
    if (groupe != null && !this.listeGroupes.contains(groupe)) {
      this.listeGroupes.add(groupe);
      if (!groupe.getListeFaces().contains(this)) {
        groupe.addFace(this);
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////////
  // Gestion de la topologie arcs / faces
  // ///////////////////////////////////////////////////////////////////////////////////////////////

  private List<Arc> arcsDirects = new ArrayList<Arc>();

  /** Renvoie la liste des arcs directs de self */
  public List<Arc> getArcsDirects() {
    return this.arcsDirects;
  }

  /** Ajoute un arc direct de self */
  public void addArcDirect(Arc arc) {
    if (arc != null && !this.arcsDirects.contains(arc)) {
      this.arcsDirects.add(arc);
      if (arc.getFaceGauche() != this) {
        arc.setFaceGauche(this);
      }
    }
  }

  /** enlève un arc direct de self */
  public void enleveArcDirect(Arc arc) {
    if (arc == null) {
      return;
    }
    if (!this.arcsDirects.contains(arc)) {
      return;
    }
    this.arcsDirects.remove(arc);
    arc.setFaceGauche(null);
  }

  private List<Arc> arcsIndirects = new ArrayList<Arc>();

  /** Renvoie la liste des arcs indirects de self */
  public List<Arc> getArcsIndirects() {
    return this.arcsIndirects;
  }

  /** Ajoute un arc indirect de self */
  public void addArcIndirect(Arc arc) {
    if (arc != null && !this.arcsIndirects.contains(arc)) {
      this.arcsIndirects.add(arc);
      if (arc.getFaceDroite() != this) {
        arc.setFaceDroite(this);
      }
    }
  }

  /** enlève un arc indirect de self */
  public void enleveArcIndirect(Arc arc) {
    if (arc == null) {
      return;
    }
    if (!this.arcsIndirects.contains(arc)) {
      return;
    }
    this.arcsIndirects.remove(arc);
    arc.setFaceDroite(null);
  }

  /**
   * Renvoie la liste (non classée) des arcs entourant self. NB: cette liste est
   * la concaténation des listes des arcs directs et indirects. Ce sont ces
   * listes qui doivent être manipulées pour la modification/l'instanciation des
   * relations topologiques sur les faces. NB2 codeur : A faire : coder une
   * méthode qui renvoie ces arcs dans le bon ordre de parcours
   */
  public List<Arc> arcs() {
    List<Arc> Arcs = new ArrayList<Arc>();
    Arcs.addAll(this.getArcsDirects());
    Arcs.addAll(this.getArcsIndirects());
    return Arcs;
  }

  /**
   * Liste de liste représentant les arcs incidents à une face (i.e. les arcs
   * des noeuds de la face, sauf les arcs de la face eux-mêmes). Dans l'esprit
   * de la méthode arcsorientés d'un noeud, les arcs sont classés en tournant
   * autour de la face dans l'ordre trigonométrique, et qualifiés d'entrants ou
   * sortants.
   * 
   * 
   * ATTENTION : renvoie une liste de liste: Liste.get(0) = liste des arcs (de
   * la classe 'Arc') Liste.get(1) = liste des orientations de type Boolean
   * (classe Boolean et non type boolean), true = entrant, false = sortant)
   * 
   * NB : Le classement est recalculé en fonction de la géométrie à chaque appel
   * de la méthode.
   */
  @SuppressWarnings("unchecked")
  public List<Object> arcsExterieursClasses() {
    List<Arc> arcsDeLaFace = this.arcs();
    Iterator<Noeud> itNoeudsDeLaFace = this.noeudsTrigo().iterator();
    List<Object> arcsDuNoeud;
    List<Object> resultat = new ArrayList<Object>();
    List<Arc> arcsExterieurs = new ArrayList<Arc>();
    List<Boolean> orientations = new ArrayList<Boolean>();
    Iterator<Arc> itArcs;
    Iterator<Boolean> itOrientations;
    Noeud noeud;
    Arc arc;
    Boolean orientation;

    while (itNoeudsDeLaFace.hasNext()) {
      noeud = itNoeudsDeLaFace.next();
      arcsDuNoeud = noeud.arcsClasses();
      itArcs = ((List<Arc>) arcsDuNoeud.get(0)).iterator();
      itOrientations = ((List<Boolean>) arcsDuNoeud.get(1)).iterator();
      while (itArcs.hasNext()) {
        arc = itArcs.next();
        orientation = itOrientations.next();
        if (arcsDeLaFace.contains(arc)) {
          continue;
        }
        arcsExterieurs.add(arc);
        orientations.add(orientation);
      }
    }
    resultat.add(arcsExterieurs);
    resultat.add(orientations);
    return resultat;
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////////
  // Topologie faces / noeuds
  // ///////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Renvoie la liste des noeuds entourant self. NB: cette liste n'est pas
   * modifiable directement. En effet, la topologie face/noeuds n'est pas gérée
   * directement, elle est déduite par calcul des topologies face/arcs et
   * arcs/noeuds
   */
  public List<Noeud> noeuds() {
    List<Arc> arcs = this.arcs();
    HashSet<Noeud> noeuds = new HashSet<Noeud>();
    Arc arc;
    Iterator<Arc> iterarcs = arcs.iterator();
    while (iterarcs.hasNext()) {
      arc = iterarcs.next();
      noeuds.addAll(arc.noeuds());
    }
    return new ArrayList<Noeud>(noeuds);
  }

  /**
   * Renvoie la liste des noeuds entourant self en parcourant la face dans le
   * sens trigonométrique. Le noeud de départ est choisi au hasard. NB : La
   * topologie arcs/noeuds ET faces doit avoir été instanciée. NB : On ne boucle
   * pas : le premier noeud n'est pas égal au dernier noeud (contrairement aux
   * géométries de polygone).
   */
  public List<Noeud> noeudsTrigo() {
    List<Arc> arcs;
    Cycle cycle = null;
    List<Noeud> noeuds = new ArrayList<Noeud>();
    Arc arc0, arc;
    Noeud noeud;
    boolean renverser = true, orientation;
    Iterator<Arc> itArcsEntourants;
    Iterator<Boolean> itOrientations;

    arcs = new ArrayList<Arc>(this.arcs());
    if (arcs.size() == 0) {
      // TODO: face infinie ???
      Face.logger
          .error("problème : gestion d'une face avec zéro arc entourant: ");
      Face.logger
          .error("           la topolgie de face a bien été instanciée?");
      return null;
    }

    arc0 = arcs.get(0);
    if (arc0.getFaceDroite() == this) {
      cycle = arc0.cycleADroite();
      renverser = true;
    } else if (arc0.getFaceGauche() == this) {
      cycle = arc0.cycleAGauche();
      renverser = false;
    } else {
      Face.logger
          .error("problème : incohérence dans la topologie arcs / faces");
      return null;
    }

    itArcsEntourants = cycle.getArcs().iterator();
    itOrientations = cycle.getOrientationsArcs().iterator();
    while (itArcsEntourants.hasNext()) {
      arc = itArcsEntourants.next();
      orientation = (itOrientations.next()).booleanValue();
      if (orientation) {
        noeud = arc.getNoeudIni();
      } else {
        noeud = arc.getNoeudFin();
      }
      if (renverser) {
        noeuds.add(0, noeud);
      } else {
        noeuds.add(noeud);
      }
    }
    return noeuds;
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////////
  // Topologie faces / faces
  // ///////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Renvoie la liste des faces voisines de self. NB: ceci est calculé en
   * passant par la topologie faces/arcs qui doit être instanciée.
   */
  public List<Face> voisins() {
    List<Arc> arcs = this.arcs();
    HashSet<Face> voisins = new HashSet<Face>();
    Arc arc;
    Iterator<Arc> iterarcs = arcs.iterator();
    while (iterarcs.hasNext()) {
      arc = iterarcs.next();
      voisins.addAll(arc.faces());
    }
    voisins.remove(this);
    return new ArrayList<Face>(voisins);
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////////
  // opérateurs de calcul sur les faces
  // ///////////////////////////////////////////////////////////////////////////////////////////////
  /** Surface d'un polygone. */
  // Le calcul est effectué dans un repère local centré sur le premier point
  // de la surface, ce qui est utile pour minimiser les erreurs de calcul
  // si on manipule de grandes coordonnées).
  public double surface() {
    return Operateurs.surface(this.getGeometrie());
  }

  protected String arcsUtilises = "";
  protected String arcsIgnores = "";

  public String getArcsUtilises() {
    return this.arcsUtilises;
  }

  public void setArcsUtilises(String arcsUtilises) {
    this.arcsUtilises = arcsUtilises;
  }

  public String getArcsIgnores() {
    return this.arcsIgnores;
  }

  public void setArcsIgnores(String arcsIgnores) {
    this.arcsIgnores = arcsIgnores;
  }

  protected List<Arc> arcsPendants = new ArrayList<Arc>();

  public List<Arc> getArcsPendants() {
    return this.arcsPendants;
  }

  public void setArcsPendants(List<Arc> arcsPendants) {
    this.arcsPendants = arcsPendants;
  }

  /**
   * Set a face as infinite or not.
   * @param infinite new infinite parameter value
   */
  public void setInfinite(final boolean infinite) {
    this.infinite = infinite;
  }

  /**
   * @return True if the face is the infinite face, false otherwise.
   */
  public boolean isInfinite() {
    return this.infinite;
  }

  @Override
  public String toString() {
    return "Face" + " " + this.getId() + " - " //$NON-NLS-2$ //$NON-NLS-3$
        + this.getGeometrie();
  }
}
