/**
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
 * 
 */

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// import operateur.OpDirectPosition;

/**
 * Polyligne. L'attribut "interpolation" est egal à "linear".
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 *          19.02.2007 : correction de bug constructeur à partir d'une liste de
 *          DirectPosition
 * 
 */

public class GM_LineString extends GM_CurveSegment {

  // ////////////////////////////////////////////////////////////////////////
  // Attribut "controlPoint" et methodes pour le traiter ///////////////////
  // ////////////////////////////////////////////////////////////////////////
  /**
   * Points pour le dessin de la polyligne : sequence de DirectPosition. Le
   * premier point est le startPoint de la polyligne.
   */
  protected DirectPositionList controlPoint;

  /**
   * Renvoie la liste conbtrolPoint. Equivalent de samplePoint() et de coord().
   * A laisser ?
   */
  public DirectPositionList getControlPoint() {
    return this.controlPoint;
  }

  /** Renvoie le DirectPosition de rang i. */
  public DirectPosition getControlPoint(int i) {
    return this.controlPoint.get(i);
  }

  /** Affecte un DirectPosition au i-eme rang de la liste. */
  public void setControlPoint(int i, DirectPosition value) {
    this.controlPoint.set(i, value);
  }

  /** Ajoute un DirectPosition en fin de liste */
  public void addControlPoint(DirectPosition value) {
    this.controlPoint.add(value);
  }

  /** Ajoute un DirectPosition au i-eme rang de la liste. */
  public void addControlPoint(int i, DirectPosition value) {
    this.controlPoint.add(i, value);
  }

  /** Efface de la liste le DirectPosition passe en parametre. */
  public void removeControlPoint(DirectPosition value) {
    this.controlPoint.remove(value);
  }

  /** Efface le i-eme DirectPosition de la liste. */
  public void removeControlPoint(int i) {
    this.controlPoint.remove(i);
  }

  /** Renvoie le nombre de DirectPosition. */
  public int sizeControlPoint() {
    return this.controlPoint.size();
  }

  // ////////////////////////////////////////////////////////////////////////
  // Constructeurs /////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////
  /** Constructeur par defaut. */
  public GM_LineString() {
    this(new DirectPositionList());
  }

  /** Constructeur par defaut. */
  public GM_LineString(DirectPosition... list) {
    this(new DirectPositionList(Arrays.asList(list)));
  }

  /** Constructeur à partir d'une liste de DirectPosition. */
  public GM_LineString(DirectPositionList points) {
    super();
    this.segment.add(this);
    this.controlPoint = new DirectPositionList();
    this.controlPoint.addAll(points);
    this.interpolation = "linear"; //$NON-NLS-1$
  }

  // ////////////////////////////////////////////////////////////////////////
  // Methode de la norme ///////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////
  /**
   * TODO Renvoie null. Decompose une polyligne en une sequence de segments.
   */
  public List<GM_LineSegment> asGM_LineSegment() {
    return null;
  }

  // ////////////////////////////////////////////////////////////////////////
  // Implementation de methodes abstraites /////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////
  /** Renvoie la liste ordonnee des points de contrele (idem que coord()). */
  @Override
  public DirectPositionList coord() {
    return this.controlPoint;
  }

  /** Renvoie un GM_CurveSegment de sens oppose. */
  @Override
  public GM_CurveSegment reverse() {
    GM_LineString result = new GM_LineString();
    int n = this.controlPoint.size();
    for (int i = 0; i < n; i++) {
      result.getControlPoint().add(this.controlPoint.get(n - 1 - i));
    }
    return result;
  }

  /**
   * Verifie si la ligne est fermee ou non. La ligne est fermee lorsque les deux
   * points extremes ont la meme position
   * @return <code>true</code> if this LineString is closed with the given
   *         tolerance (<code>distance(startpoint,endpoint) < tolerance</code>);
   *         <code>false</code> otherwise.
   */
  public boolean isClosed(double tolerance) {
    if (this.isEmpty()) {
      return false;
    }
    return this.coord().get(0).equals2D(
        this.coord().get(this.coord().size() - 1), tolerance);
  }

  /**
   * @return <code>true</code> if this LineString is closed (start point=end
   *         point); <code>false</code> otherwise.
   */
  public boolean isClosed() {
    return this.isClosed(0);
  }

  @Override
  public Object clone() {
    return new GM_LineString((DirectPositionList) this.controlPoint.clone());
  }
  @Override
  public GM_LineString getNegative() {
    List<DirectPosition> list = new ArrayList<DirectPosition>(this.controlPoint.getList());
    Collections.reverse(list);
    return new GM_LineString(new DirectPositionList(list));
  }

  @Override
  public boolean isLineString() {
    return true;
  }

}
