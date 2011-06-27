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

/**
 * Cubic Splines.
 * <p>
 * Gerald Farin and Dianne Hansford, The Essentials of CAGD, A K Peters, 2000. 
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Julien Perret
 */
public class GM_CubicSpline extends GM_PolynomialSpline {
  @Override
  public GM_CubicSpline reverse() {
    return new GM_CubicSpline(this.controlPoints.reverse(), this.vectorAtEnd[0], this.vectorAtStart[0]);
  }
  @Override
  public int getDegree() {
    return 3;
  }
  @Override
  public String getInterpolation() {
    return "cubicSpline"; //$NON-NLS-1$
  }
  public GM_CubicSpline(DirectPositionList points, double[] vectorAtStart, double[] vectorAtEnd) {
    this.controlPoints = points;
    this.vectorAtStart = new double[][] { vectorAtStart };
    this.vectorAtEnd = new double[][] { vectorAtEnd };
  }
  public GM_CubicSpline(DirectPositionList points) {
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
//    logger.info("using " + this.tangentMethod);
    // for each segment, generate tangents
    List<DirectPosition> list = new ArrayList<DirectPosition>();
    for (int i = 0; i < this.controlPoints.size() - 1; i++) {
      DirectPosition p0 = this.controlPoints.get(i);
      DirectPosition p1 = this.controlPoints.get(i + 1);
      double[] m0 = this.outgoingTangent(i);
      double[] m1 = this.incomingTangent(i + 1);
      List<DirectPosition> l = new ArrayList<DirectPosition>(4);
      l.add(p0);
      DirectPosition pt0 = new DirectPosition(p0);
      pt0.move(m0, 1.0d / 3.0d);
      l.add(pt0);
      DirectPosition pt1 = new DirectPosition(p1);
      pt1.move(m1, -1.0d / 3.0d);
      l.add(pt1);
      l.add(p1);
      GM_Bezier bezier = new GM_Bezier(l);
      GM_LineString line = bezier.asLineString(spacing, offset);
      if (i > 0) {
        line.getControlPoint().remove(0);
      }
      list.addAll(line.getControlPoint());
    }
    return new GM_LineString(list);
  }
  private double[] outgoingTangent(int i) {
    if (this.tangentMethod.equalsIgnoreCase("finiteDifference")) { //$NON-NLS-1$
      double[] v = {0.0d, 0.0d, 0.0d};
      if (i < this.controlPoints.size() - 1) {
        DirectPosition p0 = this.controlPoints.get(i);
        DirectPosition p1 = this.controlPoints.get(i + 1);
        double interval = 1.0d / (this.controlPoints.size() - 1);
        if (this.knot != null) {
          interval = 1.0d / (this.knot.get(i + 1).getValue() - this.knot.get(i).getValue());
        }
        v = p1.minus(p0, interval / 2);
      }
      if (i > 0) {
        DirectPosition p0 = this.controlPoints.get(i - 1);
        DirectPosition p1 = this.controlPoints.get(i);
        double interval = 1.0d / (this.controlPoints.size() - 1);
        if (this.knot != null) {
          interval = 1.0d / (this.knot.get(i).getValue() - this.knot.get(i - 1).getValue());
        }
        double[] v1 = p1.minus(p0, interval / 2);
        for (int j = 0; j < v1.length; j++) {
          v[j] += v1[j];
        }
      }
      return v;
    }
    if (this.tangentMethod.equalsIgnoreCase("cardinalSpline")) { //$NON-NLS-1$
      if (i > 0 && i < this.controlPoints.size() - 1) {
        DirectPosition p0 = this.controlPoints.get(i - 1);
        DirectPosition p1 = this.controlPoints.get(i + 1);
        double interval = 1.0d / ( 2.0d * (this.controlPoints.size() - 1));
        if (this.knot != null) {
          interval = 1.0d / (this.knot.get(i + 1).getValue() - this.knot.get(i - 1).getValue());
        }
        double[] v = p1.minus(p0, (1 - this.tension) * interval);
        return v;
      } else {
        if (i > 0) {
          DirectPosition p0 = this.controlPoints.get(i - 1);
          DirectPosition p1 = this.controlPoints.get(i);
          double interval = 1.0d / (this.controlPoints.size() - 1);
          if (this.knot != null) {
            interval = 1.0d / (this.knot.get(i).getValue() - this.knot.get(i - 1).getValue());
          }
          double[] v = p1.minus(p0, (1 - this.tension) * interval);
          return v;
        } else {
          DirectPosition p0 = this.controlPoints.get(i);
          DirectPosition p1 = this.controlPoints.get(i + 1);
          double interval = 1.0d / (this.controlPoints.size() - 1);
          if (this.knot != null) {
            interval = 1.0d / (this.knot.get(i + 1).getValue() - this.knot.get(i).getValue());
          }
          double[] v = p1.minus(p0, (1.0d - this.tension) * interval);
          return v;
        }
      }
    }
    if (this.tangentMethod.equalsIgnoreCase("kochanekBartels")) { //$NON-NLS-1$
      double[] v = {0.0d, 0.0d, 0.0d};
      if (i < this.controlPoints.size() - 1) {
        DirectPosition p0 = this.controlPoints.get(i);
        DirectPosition p1 = this.controlPoints.get(i + 1);
        double interval = (1.0d - this.tension) * (1.0d - this.bias) * (1.0d - this.continuity);
        v = p1.minus(p0, interval / 2.0d);
      }
      if (i > 0) {
        DirectPosition p0 = this.controlPoints.get(i - 1);
        DirectPosition p1 = this.controlPoints.get(i);
        double interval = (1.0d - this.tension) * (1.0d + this.bias) * (1.0d + this.continuity);
        double[] v1 = p1.minus(p0, interval / 2.0d);
        for (int j = 0; j < v1.length; j++) {
          v[j] += v1[j];
        }
      }
      return v;
    }
    return null;
  }
  private double[] incomingTangent(int i) {
    if (this.tangentMethod.equalsIgnoreCase("finiteDifference")) { //$NON-NLS-1$
      return this.outgoingTangent(i);
    }
    if (this.tangentMethod.equalsIgnoreCase("cardinalSpline")) { //$NON-NLS-1$
      return this.outgoingTangent(i);
    }
    if (this.tangentMethod.equalsIgnoreCase("kochanekBartels")) { //$NON-NLS-1$
      double[] v = {0.0d, 0.0d, 0.0d};
      if (i < this.controlPoints.size() - 1) {
        DirectPosition p0 = this.controlPoints.get(i);
        DirectPosition p1 = this.controlPoints.get(i + 1);
        double interval = (1.0d - this.tension) * (1.0d - this.bias) * (1.0d + this.continuity);
        v = p1.minus(p0, interval / 2.0d);
      }
      if (i > 0) {
        DirectPosition p0 = this.controlPoints.get(i - 1);
        DirectPosition p1 = this.controlPoints.get(i);
        double interval = (1.0d - this.tension) * (1.0d + this.bias) * (1.0d - this.continuity);
        double[] v1 = p1.minus(p0, interval / 2.0d);
        for (int j = 0; j < v1.length; j++) {
          v[j] += v1[j];
        }
      }
      return v;
    }
    return null;
  }
}
