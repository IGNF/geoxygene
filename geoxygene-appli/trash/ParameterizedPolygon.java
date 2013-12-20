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
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.RenderUtil;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;

/**
 * This class represents a complex polygon composed of 2D points describing the
 * outer frontier
 * and a list of inner frontiers for "holes"
 * The default parameter is the position of points in world coordinates
 * 
 * @author JeT
 * 
 */
public class ParameterizedPolygon implements DrawingPrimitive {

    private static Logger logger = Logger.getLogger(ParameterizedPolygon.class.getName()); // logger
    private final List<Point2d> outerFrontier = new ArrayList<Point2d>(); // list of points composing the outer polyline
    private final List<Point2d> outerTexturePoints = new ArrayList<Point2d>(); // list of texture coordinates composing the outer polyline
    private final List<List<Point2d>> innerFrontiers = new ArrayList<List<Point2d>>(); // list of points composing the inner polylines
    private final List<List<Point2d>> innerTexturePoints = new ArrayList<List<Point2d>>(); // list of texture coordinates composing the inner polylines
    private List<Point2d> points = null; // generated list of points containing all points from all frontiers
    private final List<DrawingPrimitive> primitives = new ArrayList<DrawingPrimitive>();
    private GM_Polygon polygon = null;
    private IFeature feature = null;
    private PolygonSymbolizer polygonSymbolizer = null;
    private final List<Shape> shapes = new ArrayList<Shape>();
    private Parameterizer parameterizer = null; // stored parameterizer used when updating polygon

    /**
     * Constructor
     */
    public ParameterizedPolygon() {
        super();
    }

    /**
     * Constructor from GM_Geometry
     * 
     * @param parameterizer
     * @param viewport
     * @param polygon
     */
    public ParameterizedPolygon(final PolygonSymbolizer polygonSymbolizer, final GM_Polygon polygon, final IFeature feature, final Viewport viewport) {
        this.feature = feature;
        this.polygonSymbolizer = polygonSymbolizer;
        this.polygon = polygon;
        this.update(viewport);

    }

    /**
     * update primitive content to match viewport
     * 
     * @param viewport
     */
    @Override
    public void update(final Viewport viewport) {
        if (this.getGM_Polygon() == null) {
            return;
        }
        //        System.err.println("update polygon " + this.hashCode() + " " + this.getGM_Polygon().area() + " " + this.parameterizer);
        this.innerFrontiers.clear();
        this.outerFrontier.clear();
        try {
            // put all "holes" in a list 
            for (IRing ring : this.getGM_Polygon().getInterior()) {
                Shape innerShape = viewport.toShape(ring);
                if (innerShape != null) {
                    this.addInnerFrontier(innerShape);
                }

            }
            // draw the outer & inner frontier
            Shape outerShape = viewport.toShape(this.getGM_Polygon().getExterior());
            this.setOuterFrontier(outerShape);

        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        try {
            this.shapes.clear();
            this.shapes.add(viewport.toShape(this.getGM_Polygon()));
        } catch (NoninvertibleTransformException e) {
            logger.error("Cannot convert polygon " + this.getGM_Polygon() + " to shape object");
            e.printStackTrace();
        }
        // if a parameterizer has been used, regenerate parameterization
        this.generateParameterization(this.parameterizer);
    }

    //    /**
    //     * Constructor from a shape
    //     * TODO : fill the outer and inner frontiers
    //     * 
    //     * @param shape
    //     * @param parameterizer
    //     */
    //    public ParameterizedPolygon(final Shape shape, final Parameterizer parameterizer) {
    //        this.shape = shape;
    //    }
    //
    //    /**
    //     * Constructor from a polyline
    //     */
    //    public ParameterizedPolygon(final ParameterizedPolyline line) {
    //        List<Point2d> outerFrontier = new ArrayList<Point2d>();
    //        for (int nPoint = 0; nPoint < line.getPointCount(); nPoint++) {
    //            outerFrontier.add(line.getPoint(nPoint));
    //        }
    //        this.setOuterFrontier(outerFrontier);
    //    }

    /**
     * @return the polygon
     */
    public final GM_Polygon getGM_Polygon() {
        return this.polygon;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive#getShape()
     */
    @Override
    public List<Shape> getShapes() {
        if (this.shapes.size() == 0) {
            GeneralPath path = new GeneralPath();
            boolean firstPoint = true;
            for (Point2d p : this.points) {
                if (firstPoint) {
                    path.moveTo(p.x, p.y);
                    firstPoint = false;
                } else {
                    path.lineTo(p.x, p.y);
                }
            }
            this.shapes.add(path);
        }
        return this.shapes;
    }

    private void invalidateShape() {
        this.shapes.clear();
    }

    /**
     * @param n
     *            point index to retrieve point coordinates
     * @return the Nth point
     */
    @Override
    public Point2d getPoint(final int n) {
        try {
            return this.getPoints().get(n);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * free the generated point list and force the point list reconstruction at
     * next getPoints() method call
     */
    public void invalidatePoints() {
        this.points = null;
    }

    /**
     * get the list of all points of this polygon. It is composed of the outer
     * frontier points and all
     * the inner frontier points. The list is reconstructed each time it is
     * needed, use invalidatePoints()
     * to force the list reconstruction
     * 
     * @return
     */
    private List<Point2d> getPoints() {
        if (this.points == null) {
            this.points = new ArrayList<Point2d>();
            this.points.addAll(this.outerFrontier);
            for (List<Point2d> innerFrontier : this.innerFrontiers) {
                this.points.addAll(innerFrontier);
            }
        }
        return this.points;
    }

    /**
     * @return the outerFrontier
     */
    public List<Point2d> getOuterFrontier() {
        return this.outerFrontier;
    }

    /**
     * @return the number of inner Frontier
     */
    public int getInnerFrontierCount() {
        return this.innerFrontiers.size();
    }

    /**
     * @return the Nth inner Frontier
     */
    public List<Point2d> getInnerFrontier(final int n) {
        return this.innerFrontiers.get(n);
    }

    //  /**
    //   * @param n point index to retrieve parameter
    //   * @return the Nth parameter point (0. on error)
    //   */
    //  public double getParameter(final int n) {
    //    return this.getPoints().get(n).getParameter();
    //  }
    //
    //  /**
    //   * Set a parameter value
    //   * @param n point index to set parameter
    //   */
    //  public void setParameter(final int n, final double parameterValue) {
    //    this.getPoints().get(n).setParameter(parameterValue);
    //  }
    //
    //  /**
    //   * @return the 1st parameter point (0. on error)
    //   */
    //  public double getFirstParameter() {
    //    return this.getParameter(0);
    //  }
    //
    //  /**
    //   * @return the last parameter point (0. on error)
    //   */
    //  public double getLastParameter() {
    //    return this.getParameter(this.getPoints().size() - 1);
    //  }

    /**
     * get the number of points in this poly line
     * 
     * @return
     */
    @Override
    public int getPointCount() {
        return this.getPoints().size();
    }

    @Override
    public List<DrawingPrimitive> getPrimitives() {
        return this.primitives;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive#isLeaf()
     */
    @Override
    public boolean isLeaf() {
        return true;
    }

    /**
     * Add an inner frontier (which define holes in the polygon).
     * 
     * @param innerShape
     *            Shape defining the inner frontier
     */
    public void addInnerFrontier(final Shape innerShape) {
        if (innerShape == null) {
            return;
        }
        final List<Point2d> frontier = new ArrayList<Point2d>(); // list of points composing the inner poly line

        PathIterator pathIterator = innerShape.getPathIterator(null);
        float[] pathCoords = new float[6];
        while (!pathIterator.isDone()) {
            int segmentType = pathIterator.currentSegment(pathCoords);
            if (segmentType == PathIterator.SEG_MOVETO || segmentType == PathIterator.SEG_LINETO) {
                frontier.add(new Point2d(pathCoords[0], pathCoords[1]));
            }
            pathIterator.next();
        }
        this.innerFrontiers.add(frontier);

        this.invalidateShape();
    }

    @Override
    public void generateParameterization(final Parameterizer parameterizer) {
        this.parameterizer = parameterizer;
        this.outerTexturePoints.clear();
        this.innerTexturePoints.clear();
        if (parameterizer == null) {
            return;
        }
        parameterizer.initializeParameterization();
        // generate coordinates for outer frontier points
        for (Point2d p : this.outerFrontier) {
            this.outerTexturePoints.add(parameterizer.getTextureCoordinates(p.x, p.y));
        }
        // generate coordinates for inner frontiers
        for (List<Point2d> innerFrontier : this.innerFrontiers) {
            final List<Point2d> textureCoordinates = new ArrayList<Point2d>(); // list of texture coordinates composing the inner poly line
            for (Point2d p : innerFrontier) {
                textureCoordinates.add(parameterizer.getTextureCoordinates(p.x, p.y));
            }
            this.innerTexturePoints.add(textureCoordinates);
        }
        parameterizer.finalizeParameterization();
    }

    /**
     * set the outer frontier .
     * 
     * @param outerShape
     *            Shape defining the outer frontier
     * @param viewport
     *            viewport containing the shape
     * @param setTextureCoordinates
     *            set texture coordinates if true
     */
    public void setOuterFrontier(final Shape outerShape) {
        this.outerFrontier.clear();
        if (outerShape == null) {
            return;
        }
        PathIterator pathIterator = outerShape.getPathIterator(null);
        float[] pathCoords = new float[6];
        while (!pathIterator.isDone()) {
            int segmentType = pathIterator.currentSegment(pathCoords);
            if (segmentType == PathIterator.SEG_MOVETO || segmentType == PathIterator.SEG_LINETO) {
                // point coordinates
                this.outerFrontier.add(new Point2d(pathCoords[0], pathCoords[1]));
            }
            pathIterator.next();
        }
        this.invalidateShape();
    }

    /**
     * set the outer frontier .
     * 
     * @param outerPoints
     *            points defining the outer frontier
     * @param outerTexturePoints
     *            texture coordinates associated with each point
     */
    private void setOuterFrontier(final List<Point2d> outerPoints, final List<Point2d> outerTexturePoints) {
        this.outerFrontier.clear();
        this.outerTexturePoints.clear();
        for (Point2d p : outerPoints) {
            this.outerFrontier.add(new Point2d(p.x, p.y));
        }
        if (outerTexturePoints == null) {
            return;
        }
        if (outerTexturePoints.size() != outerPoints.size()) {
            logger.error("Trying to set " + outerTexturePoints.size() + " outer texture coordinates on " + outerPoints.size() + "points");
        }
        for (Point2d p : outerTexturePoints) {
            this.outerTexturePoints.add(new Point2d(p.x, p.y));
        }
        this.invalidateShape();
    }

    /**
     * set the outer frontier .
     * 
     * @param outerPoints
     *            points defining the outer frontier
     */
    public void setOuterFrontier(final List<Point2d> outerPoints) {
        this.outerFrontier.clear();
        for (Point2d p : outerPoints) {
            this.outerFrontier.add(new Point2d(p.x, p.y));
        }
        this.invalidateShape();
    }

    /**
     * set the outer frontier .
     * 
     * @param outerTexturePoints
     *            texture coordinates associated with each point
     */
    public void setOuterTextureFrontier(final List<Point2d> outerTexturePoints) {
        this.outerTexturePoints.clear();
        for (Point2d p : outerTexturePoints) {
            this.outerTexturePoints.add(new Point2d(p.x, p.y));
        }
        this.invalidateShape();
    }

    /**
     * Get the texture coordinates associated with the points of the outer
     * frontier
     * 
     * @param index
     *            index of the outer frontier point
     * @return the texture coordinates or null
     */
    public Point2d getOuterFrontierTextureCoordinates(final int index) {
        try {
            return this.outerTexturePoints.get(index);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the texture coordinates associated with the points of an inner
     * frontier
     * 
     * @param innerFrontierIndex
     *            index of the inner frontier
     * @param innerFrontierPointIndex
     *            index of the inner frontier point
     * @return the texture coordinates or null
     */

    public Point2d getInnerFrontierTextureCoordinates(final int innerFrontierIndex, final int innerFrontierPointIndex) {
        try {
            return this.innerTexturePoints.get(innerFrontierIndex).get(innerFrontierPointIndex);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Check if the polygon has valid texture coordinates.
     * 
     * @return false if at least one point has no associated texture coordinates
     */
    public boolean hasTextureCoordinates() {
        //        System.err.println("polygon " + this.hashCode());
        //        System.err.println("out " + this.outerFrontier.size() + " =? " + this.outerTexturePoints.size());
        //        System.err.println("in " + this.innerFrontiers.size() + " =? " + this.innerTexturePoints.size());
        //        for (int n = 0; n < this.innerFrontiers.size(); n++) {
        //            System.err.println("in #" + n + ":  " + this.innerFrontiers.get(n).size() + " =? " + this.innerTexturePoints.get(n).size());
        //        }

        if (this.outerFrontier.size() != this.outerTexturePoints.size()) {
            return false;
        }

        if (this.innerFrontiers.size() != this.innerTexturePoints.size()) {

            return false;
        }
        for (int n = 0; n < this.innerFrontiers.size(); n++) {
            if (this.innerFrontiers.get(n).size() != this.innerTexturePoints.get(n).size()) {
                return false;
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ParameterizedPolygon [outerFrontier=" + this.outerFrontier.size() + ", innerFrontiers=" + this.innerFrontiers.size() + "]";
    }

}
