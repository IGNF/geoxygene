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

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ICurveSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
/**
 * Polyligne. L'attribut "interpolation" est egal à "linear".
 *
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Julien Perret
 */

public class GM_LineString extends GM_CurveSegment implements ILineString {

  /**
   * Points pour le dessin de la polyligne : séquence de DirectPosition. Le
   * premier point est le startPoint de la polyligne.
   */
  protected IDirectPositionList controlPoint;

  @Override
  public IDirectPositionList getControlPoint() {
    return this.controlPoint;
  }

  @Override
  public IDirectPosition getControlPoint(int i) {
    return this.controlPoint.get(i);
  }

  @Override
  public void setControlPoint(int i, IDirectPosition value) {
    this.controlPoint.set(i, value);
  }

  @Override
  public void addControlPoint(IDirectPosition value) {
    this.controlPoint.add(value);
  }

  @Override
  public void addControlPoint(int i, IDirectPosition value) {
    this.controlPoint.add(i, value);
  }

  @Override
  public void removeControlPoint(IDirectPosition value) {
    this.controlPoint.remove(value);
  }

  @Override
  public void removeControlPoint(int i) {
    this.controlPoint.remove(i);
  }

  @Override
  public int sizeControlPoint() {
    return this.controlPoint.size();
  }

  /** Constructeur par défaut. */
  public GM_LineString() {
    this(new DirectPositionList());
  }
  
  public GM_LineString(List<IDirectPosition> list) {
    this(new DirectPositionList(list));
  }

  /** Constructeur par defaut. */
  public GM_LineString(IDirectPosition... list) {
    this(Arrays.asList(list));
  }

  /** Constructeur à partir d'une liste de DirectPosition. */
  public GM_LineString(IDirectPositionList points) {
    super();
    this.segment.add(this);
    this.controlPoint = new DirectPositionList();
    this.controlPoint.addAll(points);
//    this.interpolation = "linear"; //$NON-NLS-1$
  }


  /**
   * TODO Renvoie null. Decompose une polyligne en une sequence de segments.
   */
  @Override
  public List<ILineSegment> asGM_LineSegment() {
    return null;
  }

  @Override
  public IDirectPositionList coord() {
    return this.controlPoint;
  }

  @Override
  public ICurveSegment reverse() {
    GM_LineString result = new GM_LineString();
    int n = this.controlPoint.size();
    for (int i = 0; i < n; i++) {
      result.getControlPoint().add(this.controlPoint.get(n - 1 - i));
    }
    return result;
  }

  @Override
  public boolean isClosed(double tolerance) {
    if (this.isEmpty()) {
      return false;
    }
    return this.coord().get(0).equals2D(
        this.coord().get(this.coord().size() - 1), tolerance);
  }


  @Override
  public boolean isClosed() {
    return this.isClosed(0);
  }

  @Override
  public Object clone() {
    return new GM_LineString((IDirectPositionList) this.controlPoint.clone());
  }
  @Override
  public GM_LineString getNegative() {
    List<IDirectPosition> list = new ArrayList<IDirectPosition>(this.controlPoint.getList());
    Collections.reverse(list);
    return new GM_LineString(new DirectPositionList(list));
  }

  @Override
  public boolean isLineString() {
    return true;
  }

  @Override
  public String getInterpolation() {
    return "linear"; //$NON-NLS-1$
  }
}
