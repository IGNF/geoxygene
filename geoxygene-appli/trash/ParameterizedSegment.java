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

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import fr.ign.cogit.geoxygene.math.MathUtil;

/**
 * @author JeT
 * A parameterized segment is two points associated with two parameter values
 */
public class ParameterizedSegment {

  private Point2d startPoint = null;
  private Point2d endPoint = null;
  private double startParameter = 0.;
  private double endParameter = 0.;
  private double startDistance = 0.; // distance to the polyline origin (or 0)
  private double endDistance = 0.; // distance to the polyline origin (or segment length)
  private Vector2d normal = null;

  /**
   * Constructor
   * @param startPoint
   * @param endPoint
   * @param startParameter
   * @param endParameter
   * @param startDistance
   */
  public ParameterizedSegment(final double startDistance, final Point2d startPoint, final double startParameter, final Point2d endPoint,
      final double endParameter) {
    super();
    this.startPoint = startPoint;
    this.endPoint = endPoint;
    this.startParameter = startParameter;
    this.endParameter = endParameter;
    this.startDistance = startDistance;
    this.endDistance = endPoint.distance(startPoint) + startDistance;
    this.normal = MathUtil.computeNormal(startPoint, endPoint);
  }

  /**
   * @return the startPoint
   */
  public final Point2d getStartPoint() {
    return this.startPoint;
  }

  /**
   * @param startPoint the startPoint to set
   */
  public final void setStartPoint(final Point2d startPoint) {
    this.startPoint = startPoint;
  }

  /**
   * @return the endPoint
   */
  public final Point2d getEndPoint() {
    return this.endPoint;
  }

  /**
   * @return the startParameter
   */
  public final double getStartParameter() {
    return this.startParameter;
  }

  /**
   * @return the endParameter
   */
  public final double getEndParameter() {
    return this.endParameter;
  }

  /**
   * @return the startDistance
   */
  public double getStartDistance() {
    return this.startDistance;
  }

  /**
   * @return the endDistance
   */
  public final double getEndDistance() {
    return this.endDistance;
  }

  public final double getLength() {
    return this.endDistance - this.startDistance;
  }

  /**
   * @return the normal
   */
  public Vector2d getNormal() {
    return this.normal;
  }

  /**
   * get an interpolated point in this segment as ( Start * (1-a) + End * a )
   * @param a interpolation factor (0..1) 0 = start, 1 = end
   * @return the interpolated point
   */
  public Point2d getInterpolatedPoint(final double a) {
    return new Point2d(this.startPoint.x * (1 - a) + this.endPoint.x * a, this.startPoint.y * (1 - a) + this.endPoint.y * a);
  }

  /**
   * get an interpolated parameter in this segment as ( Start * (1-a) + End * a )
   * @param a interpolation factor (0..1) 0 = start, 1 = end
   * @return the interpolated parameter
   */
  public double getInterpolatedParameter(final double a) {
    return this.startParameter * (1 - a) + this.endParameter * a;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + this.startPoint + "<->" + this.endPoint + ", " + this.startParameter + "<->" + this.endParameter + "]";
  }

}
