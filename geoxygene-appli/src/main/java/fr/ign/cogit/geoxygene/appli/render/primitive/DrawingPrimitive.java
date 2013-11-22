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
import java.util.List;

import javax.vecmath.Point2d;

import fr.ign.cogit.geoxygene.appli.Viewport;

/**
 * @author JeT
 *         Base interface of all drawing primitive. These primitives are used in
 *         the GL rendering process.
 *         A DrawingPrimitive can contain multiple drawing primitives, it is
 *         then a non-leaf primitive.
 *         We can access points to non leaf primitive but we don't know where
 *         the "holes" between primitives are
 *         The safe way to access primitives data is:
 *         - if it is a leaf : access to points. getPrimitives() is null
 *         - if it is not a leaf: access to children using getPrimitives().
 *         access to points is managed but is not reflecting the exact reality
 */
public interface DrawingPrimitive {

    /**
     * Get the primitive as Java2D shape objects
     */
    public List<Shape> getShapes();

    /**
     * get all primitives contained in this primitive
     */
    public List<DrawingPrimitive> getPrimitives();

    /**
     * @param n
     *            point index to retrieve point coordinates
     * @return the Nth point
     */
    public Point2d getPoint(final int n);

    //  /**
    //   * @return the 1st point
    //   */
    //  public Point2d getFirstPoint();
    //
    //  /**
    //   * @return the last point
    //   */
    //  public Point2d getLastPoint();

    /**
     * get the number of points in this primitive
     */
    public int getPointCount();

    /**
     * if a primitive is a "leaf" primitive, it is a non composed single
     * primitive.
     * accessing it's points is safe
     */
    public boolean isLeaf();

    /**
     * Set primitive texture coordinates using the given parameterizer
     * 
     * @param parameterizer
     *            object computing texture coordinates
     */
    void generateParameterization(Parameterizer parameterizer);

    /**
     * update primitive content with given viewport
     * 
     * @param viewport
     *            viewport used to recompute primitive content
     */
    void update(Viewport viewport);

}
