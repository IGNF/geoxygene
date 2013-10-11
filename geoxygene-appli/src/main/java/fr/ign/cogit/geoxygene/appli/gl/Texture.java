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
package fr.ign.cogit.geoxygene.appli.gl;

import javax.vecmath.Point2d;

/**
 * Texture management. This class returns texture coordinates depending on the kind of texture type
 * It maps 2D points to 2D texture coordinates
 * @author JeT
 *
 */
public interface Texture {

  /**
   * Get 2D texture coordinates depending on a given 2D point
   * @param p 2D point to retrieve texture coordinates
   * @return 2D texture coordinates at the given p point
   */
  public Point2d vertexCoordinates(final Point2d p);

  /**
   * Get 2D texture coordinates depending on a given 2D point
   * @param p 2D point to retrieve texture coordinates
   * @return 2D texture coordinates at the given p point
   */
  public Point2d vertexCoordinates(final double x, final double y);

  /**
   * Texture initialization. This method must be called before vertexCoordinates() method calls
   * @return true if texture is valid
   */
  boolean initializeRendering();

  /**
   * Finalize rendering. After this call, vertexCoordinates() method calls returns unpredictive results
   */
  void finalizeRendering();

  /**
   * Set the valid range of points that can be mapped to texture coordinates
   * @param xmin min x value
   * @param ymin min Y value
   * @param xmax max X value
   * @param ymax max Y value
   */
  public void setRange(double xmin, double ymin, double xmax, double ymax);

  /**
   * @return the min X point coordinate that can be mapped to texture coordinates
   */
  double getMinX();

  /**
   * @return the max X point coordinate that can be mapped to texture coordinates
   */
  double getMaxX();

  /**
   * @return the min Y point coordinate that can be mapped to texture coordinates
   */
  double getMinY();

  /**
   * @return the max Y point coordinate that can be mapped to texture coordinates
   */
  double getMaxY();

  /**
   * @return the texture image width (in pixels)
   */
  int getTextureWidth();

  /**
   * @return the texture image height (in pixels)
   */
  int getTextureHeight();

}
