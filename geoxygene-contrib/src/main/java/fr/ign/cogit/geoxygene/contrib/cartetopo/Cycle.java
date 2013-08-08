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
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

/**
 * Classe représentant un cycle dans une carte topologique.
 * 
 * @author Julien Perret
 */
public class Cycle {
  
    protected List<Arc> arcs;
    protected List<Boolean> orientationsArcs;
    protected ILineString geometrie;
    protected boolean aGauche;

  /**
   * Construit un cycle
   * @param arcs liste des arcs du cycle
   * @param orientationsArcs orientations respectives des arcs du cycle
   * @param geometrie géométrie du cycle (GM_LineString)
   * @param aGauche vrai si le cycle parcours son premier arcs par la gauche,
   *          faux sinon.
   */
  public Cycle(List<Arc> arcs, List<Boolean> orientationsArcs,
      ILineString geometrie, boolean aGauche) {
    this.arcs = arcs;
    this.orientationsArcs = orientationsArcs;
    this.geometrie = geometrie;
    this.aGauche = aGauche;
  }

  /**
   * Récupère la liste des arcs du cycle
   * @return the arcs la liste des arcs du cycle
   */
  public List<Arc> getArcs() {
    return this.arcs;
  }

  /**
   * définit la liste des arcs du cycle
   * @param arcs la liste des arcs du cycle
   */
  public void setArcs(List<Arc> arcs) {
    this.arcs = arcs;
  }

  /**
   * Récupère la liste des orientations du cycle
   * @return la liste des orientations du cycle
   */
  public List<Boolean> getOrientationsArcs() {
    return this.orientationsArcs;
  }

  /**
   * Définit la liste des orientations du cycle
   * @param orientationsArcs les orientations des arcs du cycle
   */
  public void setOrientationsArcs(List<Boolean> orientationsArcs) {
    this.orientationsArcs = orientationsArcs;
  }

  /**
   * Récupère la géométrie du cycle
   * @return la géométrie du cycle
   */
  public ILineString getGeometrie() {
    return this.geometrie;
  }

  /**
   * Définit la géométrie du cycle
   * @param geometrie la géométrie du cycle
   */
  public void setGeometrie(ILineString geometrie) {
    this.geometrie = geometrie;
  }

  /**
   * Récupère le sens de parcours du premier arc du cycle
   * @return le sens de parcours du premier arc du cycle : vrai s'il est
   *         parcours par la gauche, faux sinon.
   */
  public boolean isAGauche() {
    return this.aGauche;
  }

  /**
   * Définit le sens de parcours du premier arc du cycle
   * @param gauche le sens de parcours du premier arc du cycle : vrai s'il est
   *          parcours par la gauche, faux sinon.
   */
  public void setAGauche(boolean gauche) {
    this.aGauche = gauche;
  }

  /**
   * Construit la liste des faces à l'intérieur du cycle en parcourant ce cycle.
   * Attention : Seules les faces touchant l'extérieur du cycle sont renvoyées.
   * NB : cette méthode sert à optimiser un peu le calcul de la topologie des
   * faces en évitant des calculs d'intersection de géomtries.
   * @return la liste des faces à l'intérieur du cycle
   */
  public Collection<Face> getListeFacesInterieuresDuCycle() {
    Set<Face> listeFacesInterieuresDuCycle = new HashSet<Face>();
    for (int index = 0; index < this.getArcs().size(); index++) {
      Arc arc = this.getArcs().get(index);
      boolean orientation = this.getOrientationsArcs().get(index)
          .booleanValue();
      if (((orientation && !this.isAGauche()) || (!orientation && this
          .isAGauche())) && (arc.getFaceGauche() != null)) {
        listeFacesInterieuresDuCycle.add(arc.getFaceGauche());
      } else if (arc.getFaceDroite() != null) {
        listeFacesInterieuresDuCycle.add(arc.getFaceDroite());
      }
    }
    return listeFacesInterieuresDuCycle;
  }

  /**
   * Construit la liste des faces à l'extérieur du cycle en parcourant ce cycle.
   * Attention : Seules les faces touchant l'extérieur du cycle sont renvoyées.
   * Le résultat est donc différent des voisins du cycle.
   * @return la liste des faces à l'extérieur du cycle
   */
  public List<Face> getListeFacesExterieuresDuCycle() {
    List<Face> listeFacesExterieuresDuCycle = new ArrayList<Face>();
    for (Arc arc : this.getArcs()) {
      listeFacesExterieuresDuCycle.addAll(arc.getNoeudIni().faces());
    }
    listeFacesExterieuresDuCycle.remove(this.isAGauche() ? this.getArcs()
        .get(0).getFaceGauche() : this.getArcs().get(0).getFaceDroite());
    return listeFacesExterieuresDuCycle;
  }
  
  @Override
  public String toString() {
    String result = "Cycle " + (this.aGauche ? "(left) " : "(right) ") + this.arcs.size() + "\n";
    for (Arc a : this.arcs) {
      result += "\t " + a + "\n";
    }
    result += "\twith geom = " + this.geometrie;
    return result;
  }
}
