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

import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.Viewport;

/**
 * @author JeT
 *
 */
public class WorldCoordinatesLineParameterizer implements Parameterizer {

  private static Logger logger = Logger.getLogger(WorldCoordinatesLineParameterizer.class.getName());
  private Viewport viewport = null;
  private Double previousX = null;
  private Double previousY = null;
  private double distance = 0.;

  /**
   * Constructor
   * @param shape shape representing a line
   * @param viewport viewport in which the shape has been generated
   */
  public WorldCoordinatesLineParameterizer(final Viewport viewport) {
    this.viewport = viewport;
  }

  /**
   * @return the viewport
   */
  public Viewport getViewport() {
    return this.viewport;
  }

  /**
   * @param viewport the viewport to set
   */
  public void setViewport(final Viewport viewport) {
    this.viewport = viewport;
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer#initialize()
   */
  @Override
  public void initializeParameterization() {
    this.previousX = null;
    this.previousY = null;
    this.distance = 0;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#finalize()
   */
  @Override
  public void finalizeParameterization() {
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer#getTextureCoordinates(double, double)
   */
  @Override
  public Point2d getTextureCoordinates(final double x, final double y) {
    Point2D modelPoint;
    try {
      modelPoint = this.viewport.toModelPoint(new Point2D.Double(x, y));
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
      return new Point2d();
    }
    return new Point2d(modelPoint.getX(), modelPoint.getY());
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer#getLinearParameter(float, float)
   */
  @Override
  public double getLinearParameter(final float x, final float y) {
    try {
      Point2D modelView = this.viewport.toModelPoint(new Point2D.Double(x, y));
      if (this.previousX != null && this.previousY != null) {
        this.distance += Math.sqrt((this.previousX - modelView.getX()) * (this.previousX - modelView.getX()) + (this.previousY - modelView.getY())
            * (this.previousY - modelView.getY()));
      }
      this.previousX = modelView.getX();
      this.previousY = modelView.getY();
    } catch (NoninvertibleTransformException e) {
      logger.error(e);
      return this.distance;
    }
    return this.distance;
  }

}
