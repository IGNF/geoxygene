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

import java.awt.BasicStroke;
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
    private static final double DEFAULT_AWT_MITERLIMIT = 10.;    // 10 is the default miter limit defined by java.awt.BasiStroke

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
            tesselateRing(complex, polygon.getExterior(), new ConstantFunction(stroke.getStrokeWidth()), stroke.getStrokeLineJoin(), stroke.getStrokeLineCap(),
                    DEFAULT_AWT_MITERLIMIT, minX, minY);
        } catch (FunctionEvaluationException e) {
            logger.error(e);
        }
        for (IRing interior : polygon.getInterior()) {
            try {
                tesselateRing(complex, interior, new ConstantFunction(stroke.getStrokeWidth()), stroke.getStrokeLineJoin(), stroke.getStrokeLineCap(),
                        DEFAULT_AWT_MITERLIMIT, minX, minY);
            } catch (FunctionEvaluationException e) {
                logger.error(e);
            }
        }
        return complex;
    }

    public static double length(Point2D p) {
        return Math.sqrt(p.getX() * p.getX() + p.getY() * p.getY());
    }

    public static double distance(Point2D p1, Point2D p2) {
        return Math.sqrt((p2.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY()) * (p2.getY() - p1.getY()));
    }

    public static Point2D mulDoublePoint2D(double d, Point2D p) {
        return new Point2D.Double(d * p.getX(), d * p.getY());
    }

    public static Point2D mulPoint2D(Point2D p1, Point2D p2) {
        return new Point2D.Double(p1.getX() * p2.getX(), p1.getY() * p2.getY());
    }

    public static Point2D addPoint2D(Point2D p1, Point2D p2) {
        return new Point2D.Double(p1.getX() + p2.getX(), p1.getY() + p2.getY());
    }

    public static Point2D rotatePoint2D(Point2D center, Point2D p, double angle) {
        return addPoint2D(center, rotateVector(vector(center, p), angle));
    }

    public static Point2D rotateVector(Point2D v, double angle) {
        double c = Math.cos(angle);
        double s = Math.sin(angle);
        return new Point2D.Double(v.getX() * c - v.getY() * s, v.getX() * s + v.getY() * c);
    }

    public static Point2D toPoint2D(IDirectPosition i) {
        return new Point2D.Double(i.getX(), i.getY());
    }

    public static Point2D vector(Point2D p1, Point2D p2) {
        return new Point2D.Double(p2.getX() - p1.getX(), p2.getY() - p1.getY());
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
    public static void tesselateRing(GLComplex complex, IRing ring, Function1D getWidth, int join, int cap, double miterLimit, double minX, double minY)
            throws FunctionEvaluationException {
        IDirectPositionList dlist = ring.coord();
        int pointCount = dlist.size();
        Point2D[] polyline = new Point2D.Double[pointCount];
        for (int i = 0; i < pointCount; ++i) {
            polyline[i] = new Point2D.Double(dlist.get(i).getX() - minX, dlist.get(i).getY() - minY);
        }

        tesselateThickLine(complex, getWidth, polyline, join, cap, miterLimit, true);
    }

    /**
     * @param complex
     * @param getWidth
     * @param polyline
     * @throws FunctionEvaluationException
     */
    private static void tesselateThickLine(GLComplex complex, Function1D getWidth, Point2D[] polyline, int join, int cap, double miterLimit, boolean closedLine)
            throws FunctionEvaluationException {
        final double anglePrecision = Math.PI / 40;
        final double epsilon = 1E-6;
        complex.setMayOverlap(true);
        //Algo tessellation

        int size = polyline.length;
        int edgeCount = closedLine ? size : size - 1;
        if (polyline[0].equals(polyline[size - 1])) {
            edgeCount -= 1;
            size -= 1;
        }
        float arcLength = 0;
        Point2D[] edges = new Point2D.Double[edgeCount];
        Point2D[] normals = new Point2D.Double[edgeCount];

        for (int edgeIndex = 0; edgeIndex < edgeCount; edgeIndex++) {
            Point2D p0 = polyline[edgeIndex];
            Point2D p1 = polyline[(edgeIndex + 1) % size];
            edges[edgeIndex] = new Point2D.Double(p1.getX() - p0.getX(), p1.getY() - p0.getY());
            normals[edgeIndex] = normalize(new Point2D.Double(-edges[edgeIndex].getY(), edges[edgeIndex].getX()));
            arcLength += length(edges[edgeIndex]);
        }

        GLMesh mesh = complex.addGLMesh(GL11.GL_TRIANGLES);
        float currentLength = 0;
        int vertexIndex0 = 0;
        int vertexIndex1 = 0;
        int splitVertexIndex = 0;

        Point2D edge0 = edges[0];
        Point2D normal0 = normals[0];
        Point2D vertex0 = addPoint2D(polyline[0], mulDoublePoint2D(-getWidth.evaluate(currentLength / arcLength) / 2, normal0));
        Point2D vertex1 = addPoint2D(polyline[0], mulDoublePoint2D(getWidth.evaluate(currentLength / arcLength) / 2, normal0));
        if (!closedLine) { // initial cap

            double l = getWidth.evaluate(currentLength / arcLength) / 2;
            // start Cap
            if (!closedLine) {
                switch (cap) {
                case BasicStroke.CAP_BUTT:
                    // BUTT is the default computation of start points
                    break;
                case BasicStroke.CAP_SQUARE:
                    Point2D dec = mulDoublePoint2D(-l, normalize(edge0));
                    vertex0 = addPoint2D(vertex0, dec);
                    vertex1 = addPoint2D(vertex1, dec);
                    break;
                case BasicStroke.CAP_ROUND:
                    GLMesh capMesh = complex.addGLMesh(GL11.GL_TRIANGLE_FAN);
                    Point2D splitVertex = new Point2D.Double(polyline[0].getX(), polyline[0].getY());
                    capMesh.addIndex(splitVertexIndex);
                    splitVertexIndex = complex.addVertex(new GLVertex(splitVertex));
                    int nbCapPoints = (int) (Math.PI / anglePrecision);
                    Point2D sideVector = mulDoublePoint2D(getWidth.evaluate(currentLength / arcLength) / 2, normal0);
                    for (int n = 0; n <= nbCapPoints; n++) {
                        double angle = n * Math.PI / nbCapPoints;
                        capMesh.addIndex(complex.addVertex(new GLVertex(addPoint2D(splitVertex, rotateVector(sideVector, angle)))));
                    }
                    break;
                }
            }
        }
        vertexIndex0 = complex.addVertex(new GLVertex(vertex0));
        vertexIndex1 = complex.addVertex(new GLVertex(vertex1));

        // treat all segments (draw segment + join to the next segment)
        for (int currentEdgeIndex = 0; currentEdgeIndex < edgeCount; currentEdgeIndex++) {
            int nextEdgeIndex = (currentEdgeIndex + 1) % edgeCount;
            Point2D endPoint0 = polyline[nextEdgeIndex];
            edge0 = edges[currentEdgeIndex];
            normal0 = normals[currentEdgeIndex];
            Point2D startPoint1 = polyline[nextEdgeIndex];
            Point2D normal1 = normals[nextEdgeIndex];
            Point2D edge1 = edges[nextEdgeIndex];

            // side to cap is in the same direction than normals ? then 1, else -1
            int sideToCap = -1;
            double turnDirection = dot(edge0, normal1);
            double segmentLength = length(edge0);
            if (segmentLength < epsilon) {
                // if two points are at the same position, skip it
                continue;
            }
            currentLength += segmentLength;

            if (turnDirection > epsilon) {
                sideToCap = 1;
            } else if (turnDirection < -epsilon) {
                sideToCap = -1;
            } else {
                // if points are exactly aligned, we can go directly to the next point. This one is useless
                continue;
            }

            // join between lines
            Point2D midPoint = null;
            //                  midPoint = addPoint2D(endPoint0, mulDoublePoint2D(-sideToCap*getWidth.evaluate(currentLength/arcLength)/2, addPoint2D(normal0, normal1)));
            midPoint = lineIntersection(addPoint2D(endPoint0, mulDoublePoint2D(-sideToCap * getWidth.evaluate(currentLength / arcLength) / 2, normal0)), edge0,
                    addPoint2D(endPoint0, mulDoublePoint2D(-sideToCap * getWidth.evaluate(currentLength / arcLength) / 2, normal1)), edge1);
            if (currentEdgeIndex < edgeCount - 1 || ((currentEdgeIndex == edgeCount - 1) && closedLine)) {

                // in AWT if the angle is too small, miter join is switched to bevel join to avoid quite infinite miter point
                switch (join) {
                case BasicStroke.JOIN_MITER: {
                    Point2D miterPoint = lineIntersection(
                            addPoint2D(endPoint0, mulDoublePoint2D(sideToCap * getWidth.evaluate(currentLength / arcLength) / 2, normal0)), edge0,
                            addPoint2D(endPoint0, mulDoublePoint2D(sideToCap * getWidth.evaluate(currentLength / arcLength) / 2, normal1)), edge1);
                    if (distance(miterPoint, midPoint) < miterLimit * getWidth.evaluate(currentLength / arcLength)) {
                        // miter should be represented as bevel due to too small angle between lines
                        int miterVertexIndex = complex.addVertex(new GLVertex(miterPoint));
                        int midVertexIndex = complex.addVertex(new GLVertex(midPoint));
                        if (sideToCap == 1) {
                            mesh.addIndices(vertexIndex0, vertexIndex1, midVertexIndex);
                            mesh.addIndices(vertexIndex1, midVertexIndex, miterVertexIndex);
                            vertexIndex0 = midVertexIndex;
                            vertexIndex1 = miterVertexIndex;
                        } else {
                            mesh.addIndices(vertexIndex0, vertexIndex1, miterVertexIndex);
                            mesh.addIndices(vertexIndex1, miterVertexIndex, midVertexIndex);
                            vertexIndex0 = miterVertexIndex;
                            vertexIndex1 = midVertexIndex;
                        }
                        break;
                    }
                }
                case BasicStroke.JOIN_BEVEL: {
                    Point2D vertex2 = (sideToCap == 1) ? midPoint : addPoint2D(endPoint0,
                            mulDoublePoint2D(-getWidth.evaluate(currentLength / arcLength) / 2, normal0));
                    int vertexIndex2 = complex.addVertex(new GLVertex(vertex2));
                    Point2D vertex3 = (sideToCap == -1) ? midPoint : addPoint2D(endPoint0,
                            mulDoublePoint2D(getWidth.evaluate(currentLength / arcLength) / 2, normal0));
                    Point2D vertex4 = (sideToCap == 1) ? addPoint2D(startPoint1, mulDoublePoint2D(getWidth.evaluate(currentLength / arcLength) / 2, normal1))
                            : addPoint2D(startPoint1, mulDoublePoint2D(-getWidth.evaluate(currentLength / arcLength) / 2, normal1));
                    int vertexIndex3 = complex.addVertex(new GLVertex(vertex3));
                    int vertexIndex4 = complex.addVertex(new GLVertex(vertex4));
                    // add triangles 
                    mesh.addIndices(vertexIndex0, vertexIndex1, vertexIndex2);
                    mesh.addIndices(vertexIndex1, vertexIndex2, vertexIndex3);
                    mesh.addIndices(vertexIndex2, vertexIndex4, vertexIndex3);
                    vertexIndex0 = (sideToCap == 1) ? vertexIndex2 : vertexIndex4;
                    vertexIndex1 = (sideToCap == 1) ? vertexIndex4 : vertexIndex3;
                }
                    break;
                case BasicStroke.JOIN_ROUND: {
                    Point2D centerPoint = endPoint0; // edges intersection
                    Point2D vertex2 = (sideToCap == 1) ? midPoint : addPoint2D(endPoint0,
                            mulDoublePoint2D(-getWidth.evaluate(currentLength / arcLength) / 2, normal0));
                    Point2D vertex3 = (sideToCap == -1) ? midPoint : addPoint2D(endPoint0,
                            mulDoublePoint2D(getWidth.evaluate(currentLength / arcLength) / 2, normal0));
                    Point2D vertex4 = (sideToCap == 1) ? addPoint2D(startPoint1, mulDoublePoint2D(getWidth.evaluate(currentLength / arcLength) / 2, normal1))
                            : addPoint2D(startPoint1, mulDoublePoint2D(-getWidth.evaluate(currentLength / arcLength) / 2, normal1));
                    int vertexIndex2 = complex.addVertex(new GLVertex(vertex2));
                    int vertexIndex3 = complex.addVertex(new GLVertex(vertex3));
                    int vertexIndex4 = complex.addVertex(new GLVertex(vertex4));
                    int midVertexIndex = complex.addVertex(new GLVertex(midPoint));
                    // add triangles 
                    mesh.addIndices(vertexIndex0, vertexIndex1, vertexIndex2);
                    mesh.addIndices(vertexIndex1, vertexIndex2, vertexIndex3);

                    Point2D border1 = vector(centerPoint, (sideToCap == 1) ? vertex3 : vertex2);
                    Point2D border2 = vector(centerPoint, vertex4);
                    double alpha1 = Math.atan2(border1.getY(), border1.getX());
                    double alpha2 = Math.atan2(border2.getY(), border2.getX());
                    if (alpha1 < 0) {
                        alpha1 += 2 * Math.PI;
                    }
                    if (alpha2 < 0) {
                        alpha2 += 2 * Math.PI;
                    }
                    if (sideToCap == -1 && alpha2 < alpha1) {
                        alpha2 += Math.PI * 2;
                    } else if (sideToCap == 1 && alpha1 < alpha2) {
                        alpha2 -= Math.PI * 2;
                    }
                    int nbJoinPoints = (int) Math.abs((alpha2 - alpha1) / anglePrecision);

                    GLMesh roundJoinMesh = complex.addGLMesh(GL11.GL_TRIANGLE_FAN);
                    roundJoinMesh.addIndices(midVertexIndex); // triangle fan center
                    if (sideToCap == 1) { // from vertex3 to vertex 4
                        roundJoinMesh.addIndices(vertexIndex3); // first corner
                        for (int n = 1; n < nbJoinPoints; n++) { // round part
                            double angle = n * (alpha2 - alpha1) / nbJoinPoints;
                            roundJoinMesh.addIndices(complex.addVertex(new GLVertex(addPoint2D(centerPoint, rotateVector(border1, angle)))));
                        }
                        roundJoinMesh.addIndices(vertexIndex4); // last corner
                    } else {// from vertex2 to vertex 4
                        roundJoinMesh.addIndices(vertexIndex2); // first corner
                        for (int n = 1; n < nbJoinPoints; n++) { // round part
                            double angle = alpha1 + n * (alpha2 - alpha1) / nbJoinPoints - Math.PI;
                            roundJoinMesh.addIndices(complex.addVertex(new GLVertex(addPoint2D(centerPoint, rotateVector(border1, angle)))));
                        }
                        roundJoinMesh.addIndices(vertexIndex4); // last corner
                    }

                    vertexIndex0 = (sideToCap == 1) ? vertexIndex2 : vertexIndex4;
                    vertexIndex1 = (sideToCap == 1) ? vertexIndex4 : vertexIndex3;
                }
                    break;
                }
            } else {
                // last point
                endPoint0 = polyline[size - 1];
                Point2D vertex2 = addPoint2D(endPoint0, mulDoublePoint2D(-getWidth.evaluate(currentLength / arcLength) / 2, normal0));
                Point2D vertex3 = addPoint2D(endPoint0, mulDoublePoint2D(getWidth.evaluate(currentLength / arcLength) / 2, normal0));
                int vertexIndex2 = complex.addVertex(new GLVertex(vertex2));
                int vertexIndex3 = complex.addVertex(new GLVertex(vertex3));
                // add triangles 
                mesh.addIndices(vertexIndex0, vertexIndex1, vertexIndex2);
                mesh.addIndices(vertexIndex1, vertexIndex2, vertexIndex3);
                vertexIndex0 = vertexIndex2;
                vertexIndex1 = vertexIndex3;
            }
        }

        // end Cap
        if (!closedLine) {
            // treat last point (manage cap)
            edge0 = edges[edgeCount - 1];
            normal0 = normalize(new Point2D.Double(-edge0.getY(), edge0.getX()));
            Point2D splitVertex = polyline[size - 1];
            double l = getWidth.evaluate(currentLength / arcLength) / 2;
            switch (cap) {
            case BasicStroke.CAP_BUTT:
                // BUTT is the default computation of start points
                break;
            case BasicStroke.CAP_SQUARE:
                Point2D dec = mulDoublePoint2D(l, normalize(edge0));
                GLVertex v0 = complex.getVertices().get(vertexIndex0);
                GLVertex v1 = complex.getVertices().get(vertexIndex1);
                v0.setXYZ(addPoint2D(new Point2D.Double(v0.getXYZ()[0], v0.getXYZ()[1]), dec));
                v1.setXYZ(addPoint2D(new Point2D.Double(v1.getXYZ()[0], v1.getXYZ()[1]), dec));
                break;
            case BasicStroke.CAP_ROUND:
                GLMesh capMesh = complex.addGLMesh(GL11.GL_TRIANGLE_FAN);
                splitVertexIndex = complex.addVertex(new GLVertex(splitVertex));
                capMesh.addIndex(splitVertexIndex);
                int nbCapPoints = (int) (Math.PI / anglePrecision);
                Point2D sideVector = mulDoublePoint2D(-getWidth.evaluate(currentLength / arcLength) / 2, normal0);
                for (int n = 0; n <= nbCapPoints; n++) {
                    double angle = n * Math.PI / nbCapPoints;
                    capMesh.addIndex(complex.addVertex(new GLVertex(addPoint2D(splitVertex, rotateVector(sideVector, angle)))));
                }
                break;
            }
        }
    }

    //                    // compute control points to estimate at best a circle with a cubic interpolation
    //                    // http://d.krauss.free.fr/documents/Transverses/Bezier/Arc_cercle/Arc_Cercle.htm
    //                    double alpha = Math.abs(Math.atan2(edge1.getY() - edge0.getY(), edge1.getX() - edge0.getX()));
    //                    double k1 = 4. / 3. * Math.tan(alpha / 4.);
    //                    k1 *= getWidth.evaluate(currentLength / arcLength) / 2;
    //                    System.err.println("k=" + k1);
    //                    float t = n / (float) nbJoinPoints;
    //                    float px, py;
    //                    if (sideToCap == 1) {
    //                        Point2D c1 = addPoint2D(vertex3, mulDoublePoint2D(k1, normalize(edge0)));
    //                        Point2D c2 = addPoint2D(vertex4, mulDoublePoint2D(-k1, normalize(edge1)));
    //                        px = (float) GLComplexFactory.interpolateCubic(vertex3.getX(), c1.getX(), c2.getX(), vertex4.getX(), t);
    //                        py = (float) GLComplexFactory.interpolateCubic(vertex3.getY(), c1.getY(), c2.getY(), vertex4.getY(), t);
    //                    } else {
    //                        Point2D c1 = addPoint2D(vertex2, mulDoublePoint2D(k1, normalize(edge0)));
    //                        Point2D c2 = addPoint2D(vertex4, mulDoublePoint2D(-k1, normalize(edge1)));
    //                        px = (float) GLComplexFactory.interpolateCubic(vertex2.getX(), c1.getX(), c2.getX(), vertex4.getX(), t);
    //                        py = (float) GLComplexFactory.interpolateCubic(vertex2.getY(), c1.getY(), c2.getY(), vertex4.getY(), t);
    //
    //                    }
    //                    int fanVertexIndex = complex.addVertex(new GLVertex(px, py, 0f));
    //                    roundJoinMesh.addIndices(fanVertexIndex); // triangle fan center

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
    private static void tesselateThickLine(GLComplex complex, ICurve line, Function1D getWidth, int join, int cap, double miterLimit, double minX, double minY)
            throws FunctionEvaluationException {
        IDirectPositionList dlist = line.coord();
        int pointCount = dlist.size();
        Point2D[] polyline = new Point2D.Double[pointCount];
        for (int i = 0; i < pointCount; ++i) {
            polyline[i] = new Point2D.Double(dlist.get(i).getX() - minX, dlist.get(i).getY() - minY);
        }

        tesselateThickLine(complex, getWidth, polyline, join, cap, miterLimit, false);
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
                tesselateThickLine(complex, curve, new ConstantFunction(stroke.getStrokeWidth()), stroke.getStrokeLineJoin(), stroke.getStrokeLineCap(),
                        DEFAULT_AWT_MITERLIMIT, minX, minY);
            } catch (FunctionEvaluationException e) {
                logger.error(e);
            }
        }
        return complex;
    }
}
