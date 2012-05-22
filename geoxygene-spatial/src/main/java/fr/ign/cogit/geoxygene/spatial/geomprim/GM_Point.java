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

package fr.ign.cogit.geoxygene.spatial.geomprim;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IBoundary;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * Classe pour un objet géométrique constitué de un point, et seulement un.
 * 
 * @author Thierry Badard & Arnaud Braun
 * 
 *         19.02.2007 : correction de bug constructeur à partir d'une
 *         DirectPosition.
 * 
 * @version 1.1
 * 
 */
public class GM_Point extends GM_Primitive implements IPoint {
  /**
   * DirectPosition du point (DirectPosition étant la classe stockant les
   * coordonnées).
   */
  protected IDirectPosition position;

  @Override
  public IDirectPosition getPosition() {
    return this.position;
  }

  @Override
  public void setPosition(IDirectPosition pos) {
    this.position = pos;
  }

  /**
   * NON IMPLEMENTE (renvoie null). Direction entre self et le GM_Point passé en
   * paramètre, en suivant une courbe qui dépend du système de coordonnées
   * (courbe géodésique par exemple). Le bearing retourné est un vecteur.
   */
  /*
   * public Bearing bearing(GM_Point toPoint) { return null; }
   */
  /**
   * NON IMPLEMENTE (renvoie null). Direction entre self et le DirectPosition
   * passé en paramètre, en suivant une courbe qui dépend du système de
   * coordonnées (courbe géodésique par exemple). Le bearing retourné est un
   * vecteur.
   */
  /*
   * public Bearing bearing(DirectPosition toPoint) { return null; }
   */

  /** Constructeur par défaut. */
  public GM_Point() {
    this(new DirectPosition());
  }

  /**
   * Constructeur à partir de coordonnées.
   * @param pos DirectPosition : coordonnées du point
   */
  public GM_Point(IDirectPosition pos) {
    this.position = (IDirectPosition) pos.clone();
  }

  /** Affiche les coordonnées du point (2D et 3D). */
  /*
   * public String toString () { if (position != null) return
   * getPosition().toString(); else return "GM_Point : geometrie vide"; }
   */

  @Override
  public IDirectPositionList coord() {
    DirectPositionList dpl = new DirectPositionList();
    if (this.position != null) {
      dpl.add(this.position);
    }
    return dpl;
  }

  @Override
  public Object clone() {
    return new GM_Point((IDirectPosition) this.position.clone());
  }

  @Override
  public boolean isPoint() {
    return true;
  }

  @Override
  public IBoundary boundary() {
    return null;
  }

  @Override
  public IDirectPosition centroid() {
    return this.position;
  }
}
