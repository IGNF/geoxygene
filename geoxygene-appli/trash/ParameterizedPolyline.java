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
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.RenderUtil;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;

/**
 * This class represents a polyline composed of 2D points and each point is
 * associated with a parameter
 * The default parameter is the distance to the line start
 * The order of insertion is kept. Parameter values are not necessarily in
 * asc/desc order...
 * 
 * @author JeT
 * 
 */
public class ParameterizedPolyline implements DrawingPrimitive {

    private static Logger logger = Logger.getLogger(ParameterizedPolyline.class.getName()); // logger
    private final List<Point2d> points = new ArrayList<Point2d>(); // list of points composing the poly line
    private final List<Double> parameters = new ArrayList<Double>(); // list of parameters associated to each point
    private final List<Shape> shapes = new ArrayList<Shape>();
    private static final List<DrawingPrimitive> primitives = new ArrayList<DrawingPrimitive>(); // empty children list
    private IFeature feature = null;
    private LineSymbolizer lineSymbolizer = null;

    private IGeometry geometry = null;

    /**
     * Default constructor
     */
    public ParameterizedPolyline() {
    }

    public ParameterizedPolyline(LineSymbolizer lineSymbolizer, IFeature feature) {
        this.lineSymbolizer = lineSymbolizer;
        this.geometry = RenderUtil.getGeometry(this.lineSymbolizer.getGeometryPropertyName(), this.feature);
        this.feature = feature;
    }

    private IGeometry getLineGeometry() {
        return this.geometry;
    }

    //    /**
    //     * constructor
    //     */
    //    public ParameterizedPolyline(final Shape shape, final Parameterizer parameterizer) {
    //        PathIterator pathIterator = shape.getPathIterator(null);
    //        double distance = 0; // distance from the line start
    //        Point2d point = new Point2d(); // current point position
    //        while (!pathIterator.isDone()) {
    //            float[] coords = new float[6];
    //
    //            int segmentType = pathIterator.currentSegment(coords);
    //            switch (segmentType) {
    //            case PathIterator.SEG_CLOSE:
    //                break;
    //            case PathIterator.SEG_LINETO:
    //            case PathIterator.SEG_MOVETO:
    //                point.x = coords[0];
    //                point.y = coords[1];
    //
    //                if (parameterizer != null) {
    //                    distance = parameterizer.getLinearParameter(coords[0], coords[1]);
    //                }
    //
    //                this.addPoint(point, distance);
    //                break;
    //            case PathIterator.SEG_CUBICTO:
    //                point.x = coords[4];
    //                point.y = coords[5];
    //                if (parameterizer != null) {
    //                    distance = parameterizer.getLinearParameter(coords[0], coords[1]);
    //                }
    //                this.addPoint(point, distance);
    //                break;
    //            case PathIterator.SEG_QUADTO:
    //                point.x = coords[2];
    //                point.y = coords[3];
    //                if (parameterizer != null) {
    //                    distance = parameterizer.getLinearParameter(coords[0], coords[1]);
    //                }
    //                this.addPoint(point, distance);
    //                break;
    //            default:
    //                logger.warn("Draw GL shape do not know how to handle segment type " + segmentType);
    //            }
    //
    //            pathIterator.next();
    //        }
    //        this.shape = shape;
    //    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive#getShape()
     */
    @Override
    public List<Shape> getShapes() {
        if (this.shapes == null) {
            boolean firstPoint = true;
            GeneralPath path = new GeneralPath(Path2D.WIND_EVEN_ODD, this.getPointCount());
            for (int n = 0; n < this.getPointCount(); n++) {
                Point2d p = this.getPoint(n);
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

    /**
     * Add a parameterized point to the point list. Points are kept in order
     * 
     * @param p
     *            point to add
     * @param parameter
     *            parameter associated with the added point
     */
    public void addPoint(final Point2d p, final double parameter) {
        this.points.add(new Point2d(p));
        this.parameters.add(parameter);
    }

    /**
     * Add a parameterized point to the point list. Points are kept in order
     * 
     * @param p
     *            point to add
     * @param parameter
     *            parameter associated with the added point
     */
    public void addPoint(final double x, final double y, final double parameter) {
        this.points.add(new Point2d(x, y));
        this.parameters.add(parameter);
    }

    /**
     * @param n
     *            point index to retrieve point coordinates
     * @return the Nth point
     */
    @Override
    public Point2d getPoint(final int n) {
        try {
            return this.points.get(n);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Set a point value. The stored point instance stay the same, only it's
     * content (x & y values) are modified
     * 
     * @param n
     *            point index to set point
     * @param p
     *            point values to set
     */
    public void setPoint(final int n, final Point2d p) {
        this.setPoint(n, p.x, p.y);
    }

    /**
     * Set a point value. The stored point instance stay the same, only it's
     * content (x & y values) are modified
     * 
     * @param n
     *            point index to set point
     * @param x
     *            x point value to set
     * @param y
     *            y point value to set
     */
    public void setPoint(final int n, final double x, final double y) {
        this.getPoint(n).x = x;
        this.getPoint(n).y = y;
    }

    /**
     * @param n
     *            point index to retrieve parameter
     * @return the Nth parameter point (0. on error)
     */
    public Double getParameter(final int n) {
        try {
            return this.parameters.get(n);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Set a parameter value
     * 
     * @param n
     *            point index to set parameter
     */
    public void setParameter(final int n, final double parameterValue) {
        this.parameters.set(n, parameterValue);
    }

    /**
     * get the number of points in this poly line
     * 
     * @return
     */
    @Override
    public int getPointCount() {
        return this.points.size();
    }

    @Override
    public List<DrawingPrimitive> getPrimitives() {
        return ParameterizedPolyline.primitives;
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

    //    /**
    //     * This method is the exact copy of RenderUtil.getShapeList() method
    //     * 
    //     * @param geometry
    //     *            a geometry
    //     * @param viewport
    //     *            the viewport in which to view it
    //     * @param fill
    //     *            true if the stroke width should be used to build the shapes,
    //     *            ie if they will be used for graphic fill
    //     * @return the list of awt shapes corresponding to the given geometry
    //     */
    //    @SuppressWarnings("unchecked")
    //    public static List<Shape> getShapeList(final LineSymbolizer symbolizer, final IGeometry geometry, final Viewport viewport, final boolean fill) {
    //        double scaleSymbolizerUOMToDataUOM = 1;
    //        if (symbolizer.getUnitOfMeasure().equalsIgnoreCase(Symbolizer.PIXEL)) {
    //            try {
    //                scaleSymbolizerUOMToDataUOM = 1 / viewport.getModelToViewTransform().getScaleX();
    //            } catch (NoninvertibleTransformException e) {
    //                e.printStackTrace();
    //            }
    //        }
    //        if (ICurve.class.isAssignableFrom(geometry.getClass()) || IPolygon.class.isAssignableFrom(geometry.getClass())) {
    //            ICurve curve = ICurve.class.isAssignableFrom(geometry.getClass()) ? (ICurve) geometry : ((IPolygon) geometry).exteriorLineString();
    //            if (symbolizer.getPerpendicularOffset() != 0) {
    //                IMultiCurve<ILineString> offsetCurve = JtsAlgorithms.offsetCurve(curve, symbolizer.getPerpendicularOffset() * scaleSymbolizerUOMToDataUOM);
    //                List<Shape> shapes = new ArrayList<Shape>();
    //                for (ILineString l : offsetCurve) {
    //                    shapes.addAll(getLineStringShapeList(symbolizer, l, viewport, fill, scaleSymbolizerUOMToDataUOM));
    //                }
    //                return shapes;
    //            }
    //            return getLineStringShapeList(symbolizer, curve, viewport, fill, scaleSymbolizerUOMToDataUOM);
    //        }
    //        if (geometry.isMultiCurve()) {
    //            List<Shape> shapes = new ArrayList<Shape>();
    //            for (IOrientableCurve line : (IMultiCurve<IOrientableCurve>) geometry) {
    //                if (symbolizer.getPerpendicularOffset() != 0) {
    //                    IMultiCurve<ILineString> offsetCurve = JtsAlgorithms.offsetCurve((ILineString) line, symbolizer.getPerpendicularOffset()
    //                            * scaleSymbolizerUOMToDataUOM);
    //                    for (ILineString l : offsetCurve) {
    //                        shapes.addAll(getLineStringShapeList(symbolizer, l, viewport, fill, scaleSymbolizerUOMToDataUOM));
    //                    }
    //                } else {
    //                    shapes.addAll(getLineStringShapeList(symbolizer, line, viewport, fill, scaleSymbolizerUOMToDataUOM));
    //                }
    //            }
    //            return shapes;
    //        }
    //        if (geometry.isMultiSurface()) {
    //            List<Shape> shapes = new ArrayList<Shape>();
    //            for (IOrientableSurface surface : ((IMultiSurface<IOrientableSurface>) geometry).getList()) {
    //                try {
    //                    Shape shape = viewport.toShape(fill ? surface.buffer(symbolizer.getStroke().getStrokeWidth() / 2) : surface);
    //                    if (shape != null) {
    //                        shapes.add(shape);
    //                    }
    //                } catch (NoninvertibleTransformException e) {
    //                    e.printStackTrace();
    //                }
    //            }
    //            return shapes;
    //        }
    //        return null;
    //    }
    //
    //    /**
    //     * This method is the exact copy of RenderUtil.getLineStringShapeList()
    //     * method
    //     * 
    //     * @param symbolizer
    //     *            a line symbolizer
    //     * @param line
    //     *            the geometry of the line
    //     * @param viewport
    //     *            the viewport used for rendering
    //     * @param fill
    //     *            true if the stroke width should be used to build the shapes,
    //     *            ie if they will be used for graphic fill
    //     * @param scale
    //     *            scale to go from the symbolizer's uom to the data uom
    //     * @return
    //     */
    //    public static List<Shape> getLineStringShapeList(final LineSymbolizer symbolizer, final IOrientableCurve line, final Viewport viewport, final boolean fill,
    //            final double scale) {
    //        List<Shape> shapes = new ArrayList<Shape>();
    //        try {
    //            Shape shape = viewport.toShape(fill ? line.buffer(symbolizer.getStroke().getStrokeWidth() * 0.5 * scale) : line);
    //            if (shape != null) {
    //                shapes.add(shape);
    //            }
    //        } catch (NoninvertibleTransformException e) {
    //            e.printStackTrace();
    //        }
    //        return shapes;
    //    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ParameterizedPolyline [points=" + this.points.size() + ", parameters=" + this.parameters.size() + "]";
    }

    @Override
    public void generateParameterization(Parameterizer parameterizer) {
        parameterizer.initializeParameterization();
        this.parameters.clear();
        for (Point2d p : this.points) {
            this.parameters.add(parameterizer.getLinearParameter(p.x, p.y));
        }
        parameterizer.finalizeParameterization();
    }

    @Override
    public void update(Viewport viewport) {
        this.shapes.clear();
        this.shapes.addAll(ParameterizedLineConverterUtil.getShapeList(this.lineSymbolizer, this.getLineGeometry(), viewport, false));
        for (Shape shape : this.shapes) {
            PathIterator pathIterator = shape.getPathIterator(null);
            this.points.clear();
            // TODO: this is static parameterization, it should be done in new Parameterization classes 
            // ( Parameterizer::getLinearParameter() )
            double distance = 0; // distance from the line start
            Point2d point = new Point2d(); // current point position
            while (!pathIterator.isDone()) {
                float[] coords = new float[6];

                int segmentType = pathIterator.currentSegment(coords);
                switch (segmentType) {
                case PathIterator.SEG_CLOSE:
                    break;
                case PathIterator.SEG_LINETO:
                case PathIterator.SEG_MOVETO:
                    point.x = coords[0];
                    point.y = coords[1];
                    this.addPoint(point, distance);
                    break;
                case PathIterator.SEG_CUBICTO:
                    point.x = coords[4];
                    point.y = coords[5];
                    this.addPoint(point, distance);
                    break;
                case PathIterator.SEG_QUADTO:
                    point.x = coords[2];
                    point.y = coords[3];
                    this.addPoint(point, distance);
                    break;
                default:
                    logger.warn("update polyline do not know how to handle segment type " + segmentType);
                }

                pathIterator.next();
            }
        }
    }

}
