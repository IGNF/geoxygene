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
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ICubicSpline;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

/**
 * Cubic Splines.
 * <p>
 * Gerald Farin and Dianne Hansford, The Essentials of CAGD, A K Peters, 2000.
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Julien Perret
 */

public class GM_CubicSpline extends GM_PolynomialSpline implements ICubicSpline {
  @Override
  public GM_CubicSpline reverse() {
    return new GM_CubicSpline(((DirectPositionList) this.controlPoints).reverse(),
        this.vectorAtEnd[0], this.vectorAtStart[0]);
  }

  @Override
  public int getDegree() {
    return 3;
  }

  @Override
  public String getInterpolation() {
    return "cubicSpline"; //$NON-NLS-1$
  }

  public GM_CubicSpline(IDirectPositionList points, double[] vectorAtStart,
      double[] vectorAtEnd) {
    this.controlPoints = points;
    this.vectorAtStart = new double[][] { vectorAtStart };
    this.vectorAtEnd = new double[][] { vectorAtEnd };
  }

  public GM_CubicSpline(IDirectPositionList points) {
    this.controlPoints = points;
    this.vectorAtStart = new double[][] {};
    this.vectorAtEnd = new double[][] {};
  }

  String tangentMethod = "finiteDifference"; //$NON-NLS-1$

  public String getTangentMethod() {
    return this.tangentMethod;
  }

  public void setTangentMethod(String tangentMethod) {
    this.tangentMethod = tangentMethod;
  }

  double tension = 0.0;

  public void setTension(double tension) {
    this.tension = tension;
  }

  double bias = 0.0;

  public void setBias(double bias) {
    this.bias = bias;
  }

  double continuity = 0.0;

  public void setContinuity(double continuity) {
    this.continuity = continuity;
  }

  /**
   * @param spacing max distance between 2 points when subdividing
   * @param offset distance between the linestring and the curve
   * @return a linestring representing the curve
   */
  public GM_LineString asLineString(double spacing, double offset) {
    // for each segment, generate tangents
    // if (this.knot == null) {
    // double length = new GM_LineString(this.controlPoints).length();
    // this.knot = new ArrayList<GM_Knot>(this.controlPoints.size());
    // double accumulatedLength = 0.0d;
    // this.knot.add(new GM_Knot(accumulatedLength, 1, 1));
    // for (int i = 0; i < this.controlPoints.size() - 1; i++) {
    // DirectPosition p0 = this.controlPoints.get(i);
    // DirectPosition p1 = this.controlPoints.get(i + 1);
    // accumulatedLength += p0.distance(p1);
    // this.knot.add(new GM_Knot(accumulatedLength / length, 1, 1));
    // }
    // }
    List<IDirectPosition> list = new ArrayList<IDirectPosition>();
    for (int i = 0; i < this.controlPoints.size() - 1; i++) {
      IDirectPosition p0 = this.controlPoints.get(i);
      IDirectPosition p1 = this.controlPoints.get(i + 1);
      double[] m0 = this.outgoingTangent(i);
      double[] m1 = this.incomingTangent(i + 1);
      List<IDirectPosition> l = new ArrayList<IDirectPosition>(4);
      l.add(p0);
      IDirectPosition pt0 = new DirectPosition(p0.getCoordinate());
      ((DirectPosition)pt0).move(m0, 1.0d / 3.0d);
      l.add(pt0);
      IDirectPosition pt1 = new DirectPosition(p1.getCoordinate());
      ((DirectPosition)pt1).move(m1, -1.0d / 3.0d);
      l.add(pt1);
      l.add(p1);
      GM_Bezier bezier = new GM_Bezier(new DirectPositionList(l));
      GM_LineString line = bezier.asLineString(spacing, offset);
      if (i > 0) {
        line.getControlPoint().remove(0);
      }
      list.addAll(line.getControlPoint());
    }
    return new GM_LineString(list);
  }

  public ILineString asLineString(int numberOfPoints) {
    List<IDirectPosition> list = new ArrayList<IDirectPosition>();
    for (int i = 0; i < this.controlPoints.size() - 1; i++) {
      IDirectPosition p0 = this.controlPoints.get(i);
      IDirectPosition p1 = this.controlPoints.get(i + 1);
      double[] m0 = this.outgoingTangent(i);
      double[] m1 = this.incomingTangent(i + 1);
      List<IDirectPosition> l = new ArrayList<IDirectPosition>(4);
      l.add(p0);
      DirectPosition pt0 = new DirectPosition(p0.getCoordinate());
      pt0.move(m0, 1.0d / 3.0d);
      l.add(pt0);
      DirectPosition pt1 = new DirectPosition(p1.getCoordinate());
      pt1.move(m1, -1.0d / 3.0d);
      l.add(pt1);
      l.add(p1);
      GM_Bezier bezier = new GM_Bezier(l);
      GM_LineString line = bezier.asLineString(numberOfPoints);
      if (i > 0) {
        line.getControlPoint().remove(0);
      }
      list.addAll(line.getControlPoint());
    }
    return new GM_LineString(list);
  }

  private double[] outgoingTangent(int i) {
    if (this.tangentMethod.equalsIgnoreCase("finiteDifference")) { //$NON-NLS-1$
      double[] v1 = this.twoPointsDifference(i + 1, i);
      double[] v2 = this.twoPointsDifference(i, i - 1);
      return avg(v1, v2);
    }
    if (this.tangentMethod.equalsIgnoreCase("cardinalSpline")) { //$NON-NLS-1$
      int i1 = Math.min(this.controlPoints.size() - 1, i + 1);
      int i2 = Math.max(0, i - 1);
      double[] v = this.twoPointsDifference(i1, i2);
      return mul(v, (1 - this.tension));
    }
    if (this.tangentMethod.equalsIgnoreCase("kochanekBartels")) { //$NON-NLS-1$
      double[] v1 = this.twoPointsDifference(i + 1, i);
      double numerator = (1.0d - this.tension) * (1.0d - this.bias)
          * (1.0d - this.continuity);
      v1 = mul(v1, numerator);
      double[] v2 = this.twoPointsDifference(i, i - 1);
      numerator = (1.0d - this.tension) * (1.0d + this.bias)
          * (1.0d + this.continuity);
      v2 = mul(v2, numerator);
      return avg(v1, v2);
    }
    return null;
  }
  private double[] incomingTangent(int i) {
    if (!this.tangentMethod.equalsIgnoreCase("kochanekBartels")) { //$NON-NLS-1$
      return this.outgoingTangent(i);
    }
    double[] v1 = this.twoPointsDifference(i + 1, i);
    double numerator = (1.0d - this.tension) * (1.0d - this.bias)
    * (1.0d + this.continuity);
    v1 = mul(v1, numerator);
    double[] v2 = this.twoPointsDifference(i, i - 1);
    numerator = (1.0d - this.tension) * (1.0d + this.bias)
    * (1.0d - this.continuity);
    v2 = mul(v2, numerator);
    return avg(v1, v2);
  }
  private double[] twoPointsDifference(int i1, int i2) {
    if (i2 < 0 || i1 > this.controlPoints.size() - 1) {
      return null;
    }
    IDirectPosition p1 = this.controlPoints.get(i1);
    IDirectPosition p2 = this.controlPoints.get(i2);
    double denominator = 1.0d;
    if (this.knot != null) {
      denominator = this.knot.get(i2).getValue() - this.knot.get(i1).getValue();
    }
    return ((DirectPosition) p1).minus(p2, 1.0d / denominator);
  }
  private double[] mul(double[] v, double d) {
    if (v == null) {
      return v;
    }
    for (int j = 0; j < v.length; j++) {
      v[j] *= d;
    }
    return v;
  }
  private double[] avg(double[] v1, double[] v2) {
    if (v1 == null) {
      return v2;
    }
    if (v2 == null) {
      return v1;
    }
    double[] result = new double[v1.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = (v1[i] + v2[i]) / 2;
    }
    return result;
  }
}
