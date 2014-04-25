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

package test.App;

import static fr.ign.cogit.geoxygene.util.math.VectorUtil.addPoint2D;
import static fr.ign.cogit.geoxygene.util.math.VectorUtil.distance;
import static fr.ign.cogit.geoxygene.util.math.VectorUtil.dot;
import static fr.ign.cogit.geoxygene.util.math.VectorUtil.length;
import static fr.ign.cogit.geoxygene.util.math.VectorUtil.lineIntersection;
import static fr.ign.cogit.geoxygene.util.math.VectorUtil.mulDoublePoint2D;
import static fr.ign.cogit.geoxygene.util.math.VectorUtil.normalize;
import static fr.ign.cogit.geoxygene.util.math.VectorUtil.opposite;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.function.FunctionEvaluationException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.math.VectorUtil;

/**
 * @author JeT Utility method to generate line painting geometry
 */
public class LinePaintingTesselator {

    private static final double anglePrecision = Math.PI / 40;
    private static final double epsilon = 1E-6;

    /**
     * Private constructor for utility class
     */
    private LinePaintingTesselator() {
    }

    /**
     * @param complex
     * @param dlist
     * @param getWidth
     * @param join
     * @param cap
     * @param miterLimit
     * @param minX
     * @param minY
     * @throws FunctionEvaluationException
     */
    public static void tesselateThickLine(GLPaintingComplex complex,
            IDirectPositionList dlist, Function1D getWidth, double miterLimit,
            double minX, double minY) throws FunctionEvaluationException {
        int pointCount = dlist.size();
        Point2D[] polyline = new Point2D.Double[pointCount];
        for (int i = 0; i < pointCount; ++i) {
            polyline[i] = new Point2D.Double(dlist.get(i).getX() - minX, dlist
                    .get(i).getY() - minY);
        }

        tesselateThickLine(complex, getWidth, polyline, miterLimit, false);
    }

    /**
     * @param complex
     * @param getWidth
     * @param polyline
     * @throws FunctionEvaluationException
     */
    public static void tesselateThickLine(GLPaintingComplex complex,
            Function1D getWidth, Point2D[] polyline, double miterLimit,
            boolean closedLine) throws FunctionEvaluationException {
        complex.setMayOverlap(true);

        // Algo tessellation
        int size = polyline.length;
        int edgeCount = closedLine ? size : size - 1;
        if (polyline[0].equals(polyline[size - 1])) {
            // if the last point is the same as the first one, remove the last
            // from the list of points
            edgeCount -= 1;
            size -= 1;
        }
        float arcLength = 0;
        Point2D[] edges = new Point2D.Double[edgeCount];
        Point2D[] normals = new Point2D.Double[edgeCount];

        for (int edgeIndex = 0; edgeIndex < edgeCount; edgeIndex++) {
            Point2D p0 = polyline[edgeIndex];
            Point2D p1 = polyline[(edgeIndex + 1) % size];
            edges[edgeIndex] = new Point2D.Double(p1.getX() - p0.getX(),
                    p1.getY() - p0.getY());
            normals[edgeIndex] = normalize(new Point2D.Double(
                    -edges[edgeIndex].getY(), edges[edgeIndex].getX()));
            arcLength += length(edges[edgeIndex]);
        }

        GLMesh mesh = complex.addGLMesh(GL11.GL_TRIANGLES);
        float currentLength = 0;
        int vertexIndex0 = 0;
        int vertexIndex1 = 0;

        Point2D edge0 = edges[0];
        Point2D normal0 = normals[0];
        double l = getWidth.evaluate(currentLength / arcLength) / 2;
        Point2D sideVector = mulDoublePoint2D(l, normal0);
        Point2D vertex0 = addPoint2D(polyline[0], opposite(sideVector));
        Point2D vertex1 = addPoint2D(polyline[0], sideVector);
        vertexIndex0 = complex.addVertex(new GLPaintingVertex(vertex0));
        vertexIndex1 = complex.addVertex(new GLPaintingVertex(vertex1));

        // treat all segments (draw segment + join to the next segment)
        for (int currentEdgeIndex = 0; currentEdgeIndex < edgeCount; currentEdgeIndex++) {
            int nextEdgeIndex = (currentEdgeIndex + 1) % edgeCount;
            Point2D endPoint0 = polyline[(currentEdgeIndex + 1) % size];
            edge0 = edges[currentEdgeIndex];
            normal0 = normals[currentEdgeIndex];
            Point2D startPoint1 = endPoint0;
            Point2D normal1 = normals[nextEdgeIndex];
            Point2D edge1 = edges[nextEdgeIndex];

            // side to cap is in the same direction than normals ? then 1, else
            // -1
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
                // if points are exactly aligned, we can go directly to the next
                // point.
                // if there is only two points in the line, add triangles
                if (edgeCount == 1) {
                    // last point
                    Point2D vertex2 = addPoint2D(endPoint0,
                            mulDoublePoint2D(-l, normal0));
                    Point2D vertex3 = addPoint2D(endPoint0,
                            mulDoublePoint2D(l, normal0));
                    int vertexIndex2 = complex.addVertex(new GLPaintingVertex(
                            vertex2));
                    int vertexIndex3 = complex.addVertex(new GLPaintingVertex(
                            vertex3));
                    // add triangles
                    mesh.addIndices(vertexIndex0, vertexIndex1, vertexIndex2);
                    mesh.addIndices(vertexIndex1, vertexIndex2, vertexIndex3);
                    vertexIndex0 = vertexIndex2;
                    vertexIndex1 = vertexIndex3;
                }
                continue;
            }
            l = getWidth.evaluate(currentLength / arcLength) / 2;

            // join between lines
            Point2D midPoint = null;
            // midPoint = addPoint2D(endPoint0, mulDoublePoint2D(-sideToCap*l,
            // addPoint2D(normal0, normal1)));
            midPoint = lineIntersection(
                    addPoint2D(endPoint0,
                            mulDoublePoint2D(-sideToCap * l, normal0)),
                    edge0,
                    addPoint2D(endPoint0,
                            mulDoublePoint2D(-sideToCap * l, normal1)), edge1);

            if (currentEdgeIndex == edgeCount - 1 && !closedLine) {
                // last point
                Point2D vertex2 = addPoint2D(endPoint0,
                        mulDoublePoint2D(-l, normal0));
                Point2D vertex3 = addPoint2D(endPoint0,
                        mulDoublePoint2D(l, normal0));
                int vertexIndex2 = complex.addVertex(new GLPaintingVertex(
                        vertex2));
                int vertexIndex3 = complex.addVertex(new GLPaintingVertex(
                        vertex3));
                // add triangles
                mesh.addIndices(vertexIndex0, vertexIndex1, vertexIndex2);
                mesh.addIndices(vertexIndex1, vertexIndex2, vertexIndex3);
                vertexIndex0 = vertexIndex2;
                vertexIndex1 = vertexIndex3;
            } else {

                Point2D miterPoint = lineIntersection(
                        addPoint2D(endPoint0,
                                mulDoublePoint2D(sideToCap * l, normal0)),
                        edge0,
                        addPoint2D(endPoint0,
                                mulDoublePoint2D(sideToCap * l, normal1)),
                        edge1);
                if (distance(miterPoint, midPoint) < miterLimit * l * 2) {
                    // miter should be represented as bevel due to too small
                    // angle between lines
                    int miterVertexIndex = complex
                            .addVertex(new GLPaintingVertex(miterPoint));
                    int midVertexIndex = complex
                            .addVertex(new GLPaintingVertex(midPoint));
                    if (sideToCap == 1) {
                        mesh.addIndices(vertexIndex0, vertexIndex1,
                                midVertexIndex);
                        mesh.addIndices(vertexIndex1, midVertexIndex,
                                miterVertexIndex);
                        vertexIndex0 = midVertexIndex;
                        vertexIndex1 = miterVertexIndex;
                    } else {
                        mesh.addIndices(vertexIndex0, vertexIndex1,
                                miterVertexIndex);
                        mesh.addIndices(vertexIndex1, miterVertexIndex,
                                midVertexIndex);
                        vertexIndex0 = miterVertexIndex;
                        vertexIndex1 = midVertexIndex;
                    }
                }
            }
        }

    }

    /**
     * @param complex
     * @param dlist
     * @param getWidth
     * @param join
     * @param cap
     * @param miterLimit
     * @param dMax
     * @param minX
     * @param minY
     * @throws FunctionEvaluationException
     */
    public static void tesselateThickLine(GLPaintingComplex complex,
            IDirectPositionList dlist, Function1D getWidth, double miterLimit,
            double dMax, double minX, double minY)
            throws FunctionEvaluationException {
        int pointCount = dlist.size();
        List<Point2D> polyline = new ArrayList<Point2D>();

        for (int i = 1; i < pointCount; i++) {

            IDirectPosition dp0 = dlist.get(i - 1);
            IDirectPosition dp1 = dlist.get(i);
            Point2D p0 = new Point2D.Double(dp0.getX() - minX, dp0.getY()
                    - minY);
            Point2D p1 = new Point2D.Double(dp1.getX() - minX, dp1.getY()
                    - minY);
            double segmentLength = VectorUtil.distance(p0, p1);
            int segmentPointCount = (int) Math.floor(segmentLength / dMax);
            for (int n = 0; n < segmentPointCount; n++) {
                double alpha = n / (double) segmentPointCount;
                polyline.add(new Point2D.Double(p0.getX() * (1 - alpha)
                        + p1.getX() * alpha, p0.getY() * (1 - alpha)
                        + p1.getY() * alpha));
            }
        }
        polyline.add(new Point2D.Double(dlist.get(pointCount - 1).getX(), dlist
                .get(pointCount - 1).getY()));

        // Point2D[] polyline = new Point2D.Double[pointCount];
        tesselateThickLine(complex, getWidth,
                polyline.toArray(new Point2D[] {}), miterLimit, false);
    }
}
