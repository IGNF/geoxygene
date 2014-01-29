/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package fr.ign.cogit.geoxygene.appli.gl;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.function.ConstantFunction;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.function.FunctionEvaluationException;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLVertex;

/**
 * Tesselator class specialized in tesselating lines (closed or opened)
 * 
 * @author David
 * 
 */
public class LineTesselator {

    private static final Logger logger = Logger.getLogger(LineTesselator.class.getName()); // logger

    /**
     * generate the outline of a collection of polygons
     * 
     * @param polygons
     * @param stroke
     * @param minX
     * @param minY
     * @return
     */
    public static GLComplex createPolygonOutlines(List<? extends IPolygon> polygons, Stroke stroke, double minX, double minY) {
        GLComplex primitive = new GLComplex(minX, minY);

        for (IPolygon polygon : polygons) {
            GLComplex subComplex = createPolygonOutline(polygon, stroke, minX, minY);

            primitive.addGLComplex(subComplex);

        }
        return primitive;
    }

    public static GLComplex createPolygonOutline(IPolygon polygon, Stroke stroke, double minX, double minY) {
        GLComplex complex = new GLComplex(minX, minY);

        try {
            tesselateRing(complex, polygon.getExterior(), new ConstantFunction(stroke.getStrokeWidth()), minX, minY);
        } catch (FunctionEvaluationException e) {
            logger.error(e);
        }
        for (IRing interior : polygon.getInterior()) {
            try {
                tesselateRing(complex, interior, new ConstantFunction(stroke.getStrokeWidth()), minX, minY);
            } catch (FunctionEvaluationException e) {
                logger.error(e);
            }
        }
        return complex;
    }

    public static double length(Point2D p) {
        return Math.sqrt(p.getX() * p.getX() + p.getY() * p.getY());
    }

    public static Point2D mulDoublePoint2D(double d, Point2D p) {
        return new Point2D.Double(d * p.getX(), d * p.getY());
    }

    public static Point2D addPoint2D(Point2D p1, Point2D p2) {
        return new Point2D.Double(p1.getX() + p2.getX(), p1.getY() + p2.getY());
    }

    public static Point2D toPoint2D(IDirectPosition i) {
        return new Point2D.Double(i.getX(), i.getY());
    }

    public static double dot(Point2D a, Point2D b) {
        return a.getX() * b.getX() + a.getY() * b.getY();
    }

    public static Point2D normalize(Point2D p) {
        double l = length(p);
        return new Point2D.Double(p.getX() / l, p.getY() / l);
    }

    public static Point2D lineIntersection(Point2D p0, Point2D e0, Point2D p1, Point2D e1) {

        double dx = p1.getX() - p0.getX();
        double dy = p1.getY() - p0.getY();
        double det = e1.getX() * e0.getY() - e1.getY() * e0.getX();
        double u = (dy * e1.getX() - dx * e1.getY()) / det;
        //v = (dy * ad.getX() - dx * ad.getY()) / det
        return new Point2D.Double(p0.getX() + u * e0.getX(), p0.getY() + u * e0.getY());
    }

    /**
     * Tesselation of an IRing (closed polyline)
     * 
     * @param complex
     * @param ring
     * @param getWidth
     * @param minX
     * @param minY
     * @throws FunctionEvaluationException
     */
    public static void tesselateRing(GLComplex complex, IRing ring, Function1D getWidth, double minX, double minY) throws FunctionEvaluationException {
        IDirectPositionList dlist = ring.coord();
        int pointCount = dlist.size();
        Point2D[] polyline = new Point2D.Double[pointCount];
        for (int i = 0; i < pointCount; ++i) {
            polyline[i] = new Point2D.Double(dlist.get(i).getX() - minX, dlist.get(i).getY() - minY);
        }

        tesselateThickLine(complex, getWidth, polyline);
    }

    /**
     * @param complex
     * @param getWidth
     * @param polyline
     * @throws FunctionEvaluationException
     */
    private static void tesselateThickLine(GLComplex complex, Function1D getWidth, Point2D[] polyline) throws FunctionEvaluationException {
        //Algo tessellation

        int size = polyline.length;
        float arcLength = 0;
        Point2D[] edges = new Point2D.Double[size - 1];
        Point2D[] normals = new Point2D.Double[size - 1];

        for (int i = 1; i < size; ++i) {
            edges[i - 1] = new Point2D.Double(polyline[i].getX() - polyline[i - 1].getX(), polyline[i].getY() - polyline[i - 1].getY());
            normals[i - 1] = normalize(new Point2D.Double(-edges[i - 1].getY(), edges[i - 1].getX()));
            arcLength += length(edges[i - 1]);
        }
        GLMesh mesh = complex.addGLMesh(GL11.GL_TRIANGLES);
        float currentLength = 0;
        int vertexIndex0 = 0;
        int vertexIndex1 = 0;
        int splitVertexIndex = 0;
        for (int i = 0; i < size - 2; ++i) {
            if (i == 0) {
                // TODO add loopback and cap
                Point2D edge0 = edges[i];
                Point2D normal0 = normalize(new Point2D.Double(-edge0.getY(), edge0.getX()));
                Point2D splitVertex = new Point2D.Double(polyline[0].getX(), polyline[0].getY());
                Point2D vertex0 = addPoint2D(polyline[0], mulDoublePoint2D(-getWidth.evaluate(currentLength / arcLength) / 2, normal0));
                Point2D vertex1 = addPoint2D(polyline[0], mulDoublePoint2D(getWidth.evaluate(currentLength / arcLength) / 2, normal0));
                splitVertexIndex = complex.addVertex(new GLVertex(splitVertex, Color.black));
                vertexIndex0 = complex.addVertex(new GLVertex(vertex0, Color.black));
                vertexIndex1 = complex.addVertex(new GLVertex(vertex1, Color.black));
                //add startCap
            }

            Point2D endPoint0 = polyline[i + 1];
            Point2D edge0 = edges[i];
            Point2D normal0 = normals[i];
            Point2D startPoint1 = polyline[i + 1];
            Point2D normal1 = normals[i + 1];
            Point2D edge1 = edges[i + 1];

            // side to cap is in the same direction than normals ? then 1, else -1
            int sideToCap = -1;
            if (dot(edge0, normal1) > 0) {
                sideToCap = 1;
            }

            currentLength += length(edge0);
            Point2D midPoint = null;
            //			midPoint = addPoint2D(endPoint0, mulDoublePoint2D(-sideToCap*getWidth.evaluate(currentLength/arcLength)/2, addPoint2D(normal0, normal1)));
            midPoint = lineIntersection(addPoint2D(endPoint0, mulDoublePoint2D(-sideToCap * getWidth.evaluate(currentLength / arcLength) / 2, normal0)), edge0,
                    addPoint2D(endPoint0, mulDoublePoint2D(-sideToCap * getWidth.evaluate(currentLength / arcLength) / 2, normal1)), edge1);

            // triangle 0
            Point2D vertex2 = (sideToCap == 1) ? midPoint : addPoint2D(endPoint0, mulDoublePoint2D(-getWidth.evaluate(currentLength / arcLength) / 2, normal0));
            Point2D vertex3 = (sideToCap == -1) ? midPoint : addPoint2D(endPoint0, mulDoublePoint2D(getWidth.evaluate(currentLength / arcLength) / 2, normal0));
            Point2D vertex4 = (sideToCap == 1) ? addPoint2D(startPoint1, mulDoublePoint2D(getWidth.evaluate(currentLength / arcLength) / 2, normal1))
                    : addPoint2D(startPoint1, mulDoublePoint2D(-getWidth.evaluate(currentLength / arcLength) / 2, normal1));
            int vertexIndex2 = complex.addVertex(new GLVertex(vertex2, Color.black));
            int vertexIndex3 = complex.addVertex(new GLVertex(vertex3, Color.black));
            int vertexIndex4 = complex.addVertex(new GLVertex(vertex4, Color.black));

            // add triangles 
            if (i == 0) {
                mesh.addIndices(vertexIndex0, vertexIndex2, splitVertexIndex);
                mesh.addIndices(vertexIndex2, vertexIndex3, splitVertexIndex);
                mesh.addIndices(splitVertexIndex, vertexIndex3, vertexIndex1);
            } else {
                mesh.addIndices(vertexIndex0, vertexIndex1, vertexIndex2);
                mesh.addIndices(vertexIndex1, vertexIndex2, vertexIndex3);
            }
            // TODO use cap information
            // add cap
            if (sideToCap == 1) {
                mesh.addIndices(vertexIndex2, vertexIndex4, vertexIndex3);
                vertexIndex0 = vertexIndex2;
                vertexIndex1 = vertexIndex4;
            } else {
                mesh.addIndices(vertexIndex2, vertexIndex4, vertexIndex3);
                vertexIndex0 = vertexIndex4;
                vertexIndex1 = vertexIndex3;
            }
        }
        // TODO loopback if needed, add end information
        Point2D edge0 = edges[size - 2];
        Point2D normal0 = normalize(new Point2D.Double(-edge0.getY(), edge0.getX()));
        Point2D splitVertex = polyline[size - 1];
        Point2D vertex2 = addPoint2D(splitVertex, mulDoublePoint2D(-getWidth.evaluate(currentLength / arcLength) / 2, normal0));
        Point2D vertex3 = addPoint2D(splitVertex, mulDoublePoint2D(getWidth.evaluate(currentLength / arcLength) / 2, normal0));
        splitVertexIndex = complex.addVertex(new GLVertex(splitVertex, Color.black));
        int vertexIndex2 = complex.addVertex(new GLVertex(vertex2, Color.black));
        int vertexIndex3 = complex.addVertex(new GLVertex(vertex3, Color.black));
        mesh.addIndices(vertexIndex0, vertexIndex2, splitVertexIndex);
        mesh.addIndices(vertexIndex0, splitVertexIndex, vertexIndex1);
        mesh.addIndices(vertexIndex1, splitVertexIndex, vertexIndex3);
        //add endCap
    }

    /**
     * Tesselation of an ICurve (closed or open polyline or polycurve)
     * 
     * @param complex
     * @param ring
     * @param getWidth
     * @param minX
     * @param minY
     * @throws FunctionEvaluationException
     */
    private static void tesselateThickLine(GLComplex complex, ICurve line, Function1D getWidth, double minX, double minY) throws FunctionEvaluationException {
        IDirectPositionList dlist = line.coord();
        int pointCount = dlist.size();
        Point2D[] polyline = new Point2D.Double[pointCount];
        for (int i = 0; i < pointCount; ++i) {
            polyline[i] = new Point2D.Double(dlist.get(i).getX() - minX, dlist.get(i).getY() - minY);
        }

        tesselateThickLine(complex, getWidth, polyline);
    }

    /**
     * Curve tesselation
     * 
     * @param curves
     * @param stroke
     * @param minX
     * @param minY
     * @return
     */
    public static GLComplex createThickLine(List<? extends ICurve> curves, Stroke stroke, double minX, double minY) {
        GLComplex complex = new GLComplex(minX, minY);

        for (ICurve curve : curves) {
            try {
                tesselateThickLine(complex, curve, new ConstantFunction(stroke.getStrokeWidth()), minX, minY);
            } catch (FunctionEvaluationException e) {
                logger.error(e);
            }
        }
        return complex;
    }
}
