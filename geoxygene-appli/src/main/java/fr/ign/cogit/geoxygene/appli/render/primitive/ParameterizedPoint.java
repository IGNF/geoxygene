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

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

/**
 * This class represents a point associated with a parameter
 * @author JeT
 *
 */
public class ParameterizedPoint implements DrawingPrimitive {

  private final Point2d point = new Point2d(); // point pointer is never changed, only the content is modified
  private GeneralPath pointPath = null;
  private double parameter = 0; // point parameter
  private static final List<DrawingPrimitive> primitives = new ArrayList<DrawingPrimitive>(); // empty children list

  /**
   * Default constructor
   */
  public ParameterizedPoint() {
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive#getShape()
   */
  @Override
  public Shape getShape() {
    if (this.pointPath == null) {
      this.pointPath = new GeneralPath();
      this.pointPath.moveTo(this.point.x, this.point.y);
    }
    return this.pointPath;
  }

  private void invalidateShape() {
    this.pointPath = null;
  }

  /**
   * Set the parameterized point
   * @param p point value to set
   * @param parameter parameter associated with the point 
   */
  public void setPoint(final Point2d p, final double parameter) {
    this.point.set(p.x, p.y);
    this.parameter = parameter;
    this.invalidateShape();
  }

  /**
   * Set the parameterized point
   * @param x x coordinate to set
   * @param y y coordinate to set
   * @param parameter parameter associated with the point 
   */
  public void setPoint(final double x, final double y, final double parameter) {
    this.point.set(x, y);
    this.parameter = parameter;
    this.invalidateShape();
  }

  /**
   * @param n point index to retrieve point coordinates
   * @return the Nth point
   */
  @Override
  public Point2d getPoint(final int n) {
    if (n != 0) {
      throw new IndexOutOfBoundsException("ParameterizedPoint::getPoint() method can only be called with inedx = 0");
    }
    return this.point;
  }

  /**
   * @return the stored point
   */
  public Point2d getPoint() {
    return this.point;
  }

  /**
   * @return the parameter point 
   */
  public double getParameter() {
    return this.parameter;
  }

  /**
   * Set a parameter value
   */
  public void setParameter(final double parameterValue) {
    this.parameter = parameterValue;
  }

  /**
   * get the number of points in this poly line
   * @return
   */
  @Override
  public int getPointCount() {
    return 1;
  }

  @Override
  public List<DrawingPrimitive> getPrimitives() {
    return ParameterizedPoint.primitives;
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive#isLeaf()
   */
  @Override
  public boolean isLeaf() {
    return true;
  }

}
