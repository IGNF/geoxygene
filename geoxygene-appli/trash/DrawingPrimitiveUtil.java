/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.util.List;

import javax.vecmath.Point2d;

/**
 * @author JeT
 * Utility class for DrawingPrimitives
 */
public final class DrawingPrimitiveUtil {

  /**
   * Compute the length of a line as the sum of parameters differences
   * @param line line to compute length
   * @return the computedLength
   */
  public static double parameterizedLength(final ParameterizedPolyline line) {
    double sumDistance = 0.;
    double prevParameter = line.getParameter(0);
    for (int n = 1; n < line.getPointCount(); n++) {
      double parameter = line.getParameter(n);
      sumDistance += Math.abs(parameter - prevParameter);
      prevParameter = parameter;
    }
    return sumDistance;
  }

  /**
   * Compute the length of a line as the sum of its segments length in pixels
   * (in points coordinates)
   * @param line line to compute length
   * @return the computedLength
   */
  public static double pointLength(final ParameterizedPolyline line) {
    double sumDistance = 0.;
    Point2d prevPoint = line.getPoint(0);
    for (int n = 1; n < line.getPointCount(); n++) {
      Point2d point = line.getPoint(n);
      sumDistance += point.distance(prevPoint);
      prevPoint = point;
    }
    return sumDistance;
  }

  /**
   * Generate all segments of a polyline
   * @param line line to segmentize
   * @return
   */
  public static ParameterizedSegment[] segmentize(final ParameterizedPolyline line) {
    ParameterizedSegment[] segments = new ParameterizedSegment[line.getPointCount() - 1];
    Point2d startPoint = line.getPoint(0);
    double startParameter = line.getParameter(0);
    double sumDistance = 0.;
    for (int n = 0; n < line.getPointCount() - 1; n++) {
      Point2d endPoint = line.getPoint(n + 1);
      double endParameter = line.getParameter(n + 1);
      segments[n] = new ParameterizedSegment(sumDistance, startPoint, startParameter, endPoint, endParameter);
      sumDistance += endPoint.distance(startPoint);
      startPoint = endPoint;
      startParameter = endParameter;
    }

    return segments;
  }

  /**
   * "Gravity center" computation is simply the sum of all points divided by the number of points
   * @param inputs
   * @return
   */
  public static Point2d computeGravityCenter(final List<DrawingPrimitive> inputs) {
    double sumX = 0.;
    double sumY = 0.;
    int nb = 0;
    for (DrawingPrimitive primitive : inputs) {
      for (int n = 0; n < primitive.getPointCount(); n++) {
        sumX += primitive.getPoint(n).x;
        sumY += primitive.getPoint(n).y;
        nb++;
      }
    }
    if (nb > 0) {
      sumX /= nb;
      sumY /= nb;
    }
    return new Point2d(sumX / nb, sumY / nb);
  }

  /**
   * "Gravity center" computation is simply the sum of all points divided by the number of points
   * @param inputs
   * @return
   */
  public static Point2d computeGravityCenter(final DrawingPrimitive primitive) {
    double sumX = 0.;
    double sumY = 0.;
    int nb = 0;
    for (int n = 0; n < primitive.getPointCount(); n++) {
      sumX += primitive.getPoint(n).x;
      sumY += primitive.getPoint(n).y;
      nb++;
    }
    if (nb > 0) {
      sumX /= nb;
      sumY /= nb;
    }
    // sumXY are no longer sums but means
    return new Point2d(sumX, sumY);
  }

}
