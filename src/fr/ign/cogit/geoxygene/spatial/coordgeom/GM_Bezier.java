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
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;


/**
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Julien Perret
 */

public class GM_Bezier extends GM_BSplineCurve {
  public GM_Bezier(List<DirectPosition> points) {
    this(new DirectPositionList(points));
  }
  public GM_Bezier(DirectPositionList points) {
    this.interpolation = "polynomial"; //$NON-NLS-1$
    this.controlPoints = points;
    this.degree = this.controlPoints.size() - 1;
  }
  /**
   * Use De Casteljau's algorithm.
   * <p>
   * The offset is not used.
   * <p>
   * Gerald Farin and Dianne Hansford, The Essentials of CAGD, A K Peters, 2000. 
   * @param spacing max distance between 2 points when subdividing
   * @param offset distance between the linestring and the curve
   * @return a linestring representing the curve
   */
  public GM_LineString asLineString(double spacing, double offset) {
    if (offset != 0) {
      return null;
    }
    if (this.controlPoints.size() > 1) {
      DirectPositionList list = piecewiseBezier(this.controlPoints.getList(), spacing);
      return new GM_LineString(list);
    }
    return new GM_LineString(this.controlPoints);
  }
  DirectPositionList piecewiseBezier(List<DirectPosition> p, double spacing) {
    double length = new GM_LineString(p).length();
    if (length <= spacing) {
      return new DirectPositionList(p.get(0), p.get(p.size() - 1));
    } else {
      DirectPosition[][] T = new DirectPosition[p.size()][p.size()];
      for (int i = 0; i < p.size(); i++) {
        T[0][i] = p.get(i);
      }
      for (int i = 1; i <  p.size(); i++) {
        for (int j = 0; j < p.size() - i; j++) {
          T[i][j] = Operateurs.milieu(T[i - 1][j], T[i -1][j + 1]);
        }
      }
      List<DirectPosition> left = new ArrayList<DirectPosition> (p.size());
      List<DirectPosition> right = new ArrayList<DirectPosition> (p.size());
      for (int i = 0; i < p.size(); i++) {
        left.add(T[i][0]);
        right.add(T[p.size() - 1 - i][i]);
      }
      DirectPositionList leftList = piecewiseBezier(left, spacing);
      DirectPositionList rightList = piecewiseBezier(right, spacing);
      DirectPositionList list = new DirectPositionList();
      list.addAll(leftList);
      list.remove(list.size() - 1);
      list.addAll(rightList);
      return list;
    }
  }
  public GM_LineString asLineString(int numberOfPoints) {
    if (this.controlPoints.size() > 1) {
      DirectPositionList list = piecewiseBezier(this.controlPoints.getList(), numberOfPoints);
      return new GM_LineString(list);
    }
    return new GM_LineString(this.controlPoints);
  }

  DirectPositionList piecewiseBezier(List<DirectPosition> p, int numberOfPoints) {
    if (numberOfPoints <= 2) {
      return new DirectPositionList(p.get(0), p.get(p.size() - 1));
    } else {
      DirectPosition[][] T = new DirectPosition[p.size()][p.size()];
      for (int i = 0; i < p.size(); i++) {
        T[0][i] = p.get(i);
      }
      for (int i = 1; i <  p.size(); i++) {
        for (int j = 0; j < p.size() - i; j++) {
          T[i][j] = Operateurs.milieu(T[i - 1][j], T[i -1][j + 1]);
        }
      }
      List<DirectPosition> left = new ArrayList<DirectPosition> (p.size());
      List<DirectPosition> right = new ArrayList<DirectPosition> (p.size());
      for (int i = 0; i < p.size(); i++) {
        left.add(T[i][0]);
        right.add(T[p.size() - 1 - i][i]);
      }
      DirectPositionList leftList = piecewiseBezier(left, (numberOfPoints + 1) / 2);
      DirectPositionList rightList = piecewiseBezier(right, (numberOfPoints + 1) / 2);
      DirectPositionList list = new DirectPositionList();
      list.addAll(leftList);
      list.remove(list.size() - 1);
      list.addAll(rightList);
      return list;
    }
  }
}
