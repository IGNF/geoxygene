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

import static fr.ign.cogit.geoxygene.util.math.VectorUtil.length;
import static fr.ign.cogit.geoxygene.util.math.VectorUtil.normalize;

import java.awt.Color;

import javax.vecmath.Point2d;

import org.lwjgl.opengl.GL11;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.appli.render.primitive.Colorizer;
import fr.ign.cogit.geoxygene.function.ConstantFunction;
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
    private static final Function1D identityLineWidth = new ConstantFunction(1);
    private static final Function1D identityLineShift = new ConstantFunction(0);

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
            IDirectPositionList dlist, Function1D lineWidth,
            Function1D lineShift, double maxLength, double minAngle,
            double minX, double minY, Colorizer c)
            throws FunctionEvaluationException {
        complex.setMayOverlap(true);
        int pointCount = dlist.size();
        Point2d[] polyline = new Point2d[pointCount];
        for (int i = 0; i < pointCount; ++i) {
            polyline[i] = new Point2d(dlist.get(i).getX() - minX, dlist.get(i)
                    .getY() - minY);
        }

        tesselateThickLine(complex, lineWidth, lineShift, polyline, maxLength,
                minAngle, false, c);
    }

    public static void tesselateThickLine(GLPaintingComplex complex,
            Function1D lineWidth, Function1D lineShift, Point2d[] polyline,
            double maxLength, double minAngle, boolean closedLine,
            Colorizer colorizer) throws FunctionEvaluationException {
        double arcLength = 0;
        double minCosAngle = Math.cos(minAngle);

        if (polyline.length < 2) {
            System.err.println("line tesselation does not handle "
                    + polyline.length + " points counts");
            return;
        }
        colorizer.initializeColorization();
        int pointCount = polyline.length;
        int edgeCount = polyline.length - 1;
        Point2d[] edges = new Point2d[edgeCount];
        Point2d[] normals = new Point2d[edgeCount];
        Double[] params = new Double[pointCount];
        for (int edgeIndex = 0; edgeIndex < edgeCount; edgeIndex++) {
            Point2d p0 = polyline[edgeIndex];
            Point2d p1 = polyline[edgeIndex + 1];
            edges[edgeIndex] = new Point2d();
            VectorUtil.vector(edges[edgeIndex], p0, p1);
            normals[edgeIndex] = new Point2d();
            normalize(normals[edgeIndex], new Point2d(-edges[edgeIndex].getY(),
                    edges[edgeIndex].getX()));
            params[edgeIndex] = new Double(0);
            params[edgeIndex] = arcLength;
            arcLength += length(edges[edgeIndex]);
            // System.err.println("segment #" + edgeIndex + "  p0 " + p0 +
            // "  p1 "
            // + p1 + "  edge : " + edges[edgeIndex] + "  normal : "
            // + normals[edgeIndex]);
        }
        params[pointCount - 1] = arcLength;

        int currentEdgeAndPointIndex = 0;
        Point2d edge = edges[currentEdgeAndPointIndex];
        Point2d normal = normals[currentEdgeAndPointIndex];
        Double param = params[currentEdgeAndPointIndex];
        Double nextParam = new Double(0);
        Point2d p = new Point2d(polyline[currentEdgeAndPointIndex]);
        Point2d uvLow = new Point2d(0, 0);
        Point2d uvHigh = new Point2d(0, 0);
        Point2d n = new Point2d();
        // 0 is the segment start expanded point
        Point2d lowPoint0 = new Point2d();
        Point2d highPoint0 = new Point2d();
        // identity is an expanded line of 1 with no center shift
        // 0 is the segment start identity point
        Point2d identityLowPoint0 = new Point2d();
        Point2d identityHighPoint0 = new Point2d();
        // 1 is the segment start identity point
        Point2d identityLowPoint1 = new Point2d();
        Point2d identityHighPoint1 = new Point2d();
        // identity points are interpolated to keep a maximum segment size
        Point2d interpolatedIdentityLowPoint = new Point2d();
        Point2d interpolatedIdentityHighPoint = new Point2d();
        Point2d interpolatedLowPoint = new Point2d();
        Point2d interpolatedHighPoint = new Point2d();
        // low & high vectors used to compute curvature
        Point2d previousLowVector = new Point2d();
        Point2d previousHighVector = new Point2d();
        Point2d currentLowVector = new Point2d();
        Point2d currentHighVector = new Point2d();
        Point2d previousLowPoint = new Point2d();
        Point2d previousHighPoint = new Point2d();

        // compute identity line expansion
        identityLowPoint0.x = p.x + normal.x;
        identityLowPoint0.y = p.y + normal.y;
        identityHighPoint0.x = p.x - normal.x;
        identityHighPoint0.y = p.y - normal.y;
        // compute deformed line expansion
        double width = lineWidth.evaluate(0);
        double shift = lineShift.evaluate(0);
        lowPoint0.x = p.x + normal.x * (shift + width);
        lowPoint0.y = p.y + normal.y * (shift + width);
        highPoint0.x = p.x + normal.x * (shift - width);
        highPoint0.y = p.y + normal.y * (shift - width);
        // compute first point for currentLow/HighVector
        VectorUtil.copy(previousLowPoint, lowPoint0);
        VectorUtil.copy(previousHighPoint, highPoint0);
        VectorUtil.normalize(previousLowVector, edge);
        VectorUtil.copy(previousHighVector, previousLowVector);

        uvLow.x = 0;
        uvLow.y = 0;
        uvHigh.x = 0;
        uvHigh.y = 1;
        Color cLow = colorizer.getColor(uvLow.x, uvLow.y);
        Color cHigh = colorizer.getColor(uvHigh.x, uvHigh.y);
        int p0LowIndex = complex.addVertex(new GLPaintingVertex(
                (float) lowPoint0.x, (float) lowPoint0.y, (float) uvLow.x,
                (float) uvLow.y, (float) arcLength, 0f, 0f, 0f, 0f, cLow
                        .getRed() / 255f, cLow.getGreen() / 255f, cLow
                        .getBlue() / 255f, cLow.getAlpha() / 255f));
        int p0HighIndex = complex.addVertex(new GLPaintingVertex(
                (float) highPoint0.x, (float) highPoint0.y, (float) uvHigh.x,
                (float) uvHigh.y, (float) arcLength, 0f, 0f, 0f, 0f, cHigh
                        .getRed() / 255f, cHigh.getGreen() / 255f, cHigh
                        .getBlue() / 255f, cHigh.getAlpha() / 255f));
        // System.err.println("Add vertex with uv = " + uvLow + " / " + uvHigh
        // + " on " + arcLength);
        while (currentEdgeAndPointIndex < edgeCount) {
            int nextEdgeAndPointIndex = currentEdgeAndPointIndex + 1;
            // Point2d nextNormal = normals[nextEdgeAndPointIndex];
            // Point2d nextEdge = edges[nextEdgeAndPointIndex];
            param = params[currentEdgeAndPointIndex];
            nextParam = params[nextEdgeAndPointIndex];
            Point2d p0 = polyline[currentEdgeAndPointIndex];
            Point2d p1 = polyline[nextEdgeAndPointIndex];
            Point2d n0 = normals[currentEdgeAndPointIndex];
            Point2d n1 = nextEdgeAndPointIndex >= normals.length ? n0
                    : normals[nextEdgeAndPointIndex];
            // System.err.println("---------------------------------");
            // System.err.println("p0 " + p0);
            // System.err.println("p1 " + p1);
            // System.err.println("n0 " + n0);
            // System.err.println("n1 " + n1);

            double edgeLength = VectorUtil.distance(p0, p1);
            double cosAlpha = VectorUtil.dot(n0, n1);
            double alpha = 0;
            if (cosAlpha >= 0.9999) {
                alpha = 0;
            } else if (Math.abs(cosAlpha) < 0.001) {
                throw new IllegalArgumentException(
                        "low cos case not implemented yet");
            } else {
                alpha = Math.acos(cosAlpha);
            }
            double factor = Math.tan(alpha / 2.);
            factor = 1 + factor * factor; // 1 + tan2 ( a / 2 )

            Point2d middleNormal = new Point2d(factor * (n0.x + n1.x) / 2.,
                    factor * (n0.y + n1.y) / 2.);
            // compute end-of-edge identity expansion
            identityLowPoint1.x = p1.x + middleNormal.x;
            identityLowPoint1.y = p1.y + middleNormal.y;
            identityHighPoint1.x = p1.x - middleNormal.x;
            identityHighPoint1.y = p1.y - middleNormal.y;

            // System.err.println("low0 " + identityLowPoint0 + " low1 "
            // + identityLowPoint1);
            // System.err.println("high0 " + identityHighPoint0 + " high1 "
            // + identityHighPoint1);
            // System.err.println("middle normal = " + middleNormal);
            // interpolate between identity low/highPoints0 & 1

            int nbSegments = (int) Math.max(1,
                    Math.ceil(edgeLength / maxLength));
            Point2d deltaIdentityLow = new Point2d(
                    (identityLowPoint1.x - identityLowPoint0.x) / nbSegments,
                    (identityLowPoint1.y - identityLowPoint0.y) / nbSegments);
            Point2d deltaIdentityHigh = new Point2d(
                    (identityHighPoint1.x - identityHighPoint0.x) / nbSegments,
                    (identityHighPoint1.y - identityHighPoint0.y) / nbSegments);
            double deltaParam = (nextParam - param) / nbSegments;

            GLMesh mesh = complex.addGLMesh(GL11.GL_TRIANGLES);
            for (int nSegment = 1; nSegment <= nbSegments; nSegment++) {
                // interpolated Low & High identity points
                interpolatedIdentityLowPoint.x = identityLowPoint0.x + nSegment
                        * deltaIdentityLow.x;
                interpolatedIdentityLowPoint.y = identityLowPoint0.y + nSegment
                        * deltaIdentityLow.y;
                interpolatedIdentityHighPoint.x = identityHighPoint0.x
                        + nSegment * deltaIdentityHigh.x;
                interpolatedIdentityHighPoint.y = identityHighPoint0.y
                        + nSegment * deltaIdentityHigh.y;
                double t = param + nSegment * deltaParam;
                // compute mid point and normal using interpolated Low & High
                // Points
                p.x = (interpolatedIdentityLowPoint.x + interpolatedIdentityHighPoint.x) / 2;
                p.y = (interpolatedIdentityLowPoint.y + interpolatedIdentityHighPoint.y) / 2;
                n.x = (interpolatedIdentityLowPoint.x - interpolatedIdentityHighPoint.x) / 2;
                n.y = (interpolatedIdentityLowPoint.y - interpolatedIdentityHighPoint.y) / 2;
                shift = lineShift.evaluate(t);
                width = lineWidth.evaluate(t);
                interpolatedLowPoint.x = p.x + n.x * (shift + width);
                interpolatedLowPoint.y = p.y + n.y * (shift + width);
                interpolatedHighPoint.x = p.x + n.x * (shift - width);
                interpolatedHighPoint.y = p.y + n.y * (shift - width);
                double lowCosAngle = 0;
                double highCosAngle = 0;
                VectorUtil.vector(currentLowVector, previousLowPoint,
                        interpolatedLowPoint);
                VectorUtil.vector(currentHighVector, previousHighPoint,
                        interpolatedHighPoint);
                VectorUtil.normalize(currentLowVector, currentLowVector);
                VectorUtil.normalize(currentHighVector, currentHighVector);
                // System.err.println("segment #" + nSegment);
                // System.err.println(" previous LowPoint" + previousLowPoint
                // + " current LowPoint " + interpolatedLowPoint);
                if (nSegment > 0) {
                    lowCosAngle = VectorUtil.dot(previousLowVector,
                            currentLowVector);
                    highCosAngle = VectorUtil.dot(previousHighVector,
                            currentHighVector);
                    // System.err.println(" previous LowVector"
                    // + previousLowVector + " current LowVector "
                    // + currentLowVector + " => cos = " + lowCosAngle);
                } else {
                    lowCosAngle = VectorUtil.dot(currentLowVector, edge)
                            / edgeLength;
                    highCosAngle = VectorUtil.dot(currentHighVector, edge)
                            / edgeLength;
                }

                if (nSegment == nbSegments || lowCosAngle <= minCosAngle
                        || highCosAngle <= minCosAngle) {

                    uvLow.x = t;
                    uvLow.y = 0;
                    uvHigh.x = t;
                    uvHigh.y = 1;
                    cLow = colorizer.getColor(uvLow.x, uvLow.y);
                    cHigh = colorizer.getColor(uvHigh.x, uvHigh.y);

                    int p1LowIndex = complex.addVertex(new GLPaintingVertex(
                            (float) interpolatedLowPoint.x,
                            (float) interpolatedLowPoint.y, (float) uvLow.x,
                            (float) uvLow.y, (float) arcLength, (float) n.x,
                            (float) n.y, 0f, 0f, cLow.getRed() / 255f, cLow
                                    .getGreen() / 255f, cLow.getBlue() / 255f,
                            cLow.getAlpha() / 255f));
                    int p1HighIndex = complex.addVertex(new GLPaintingVertex(
                            (float) interpolatedHighPoint.x,
                            (float) interpolatedHighPoint.y, (float) uvHigh.x,
                            (float) uvHigh.y, (float) arcLength, (float) n.x,
                            (float) n.y, 0f, 0f, cHigh.getRed() / 255f, cHigh
                                    .getGreen() / 255f, cHigh.getBlue() / 255f,
                            cHigh.getAlpha() / 255f));
                    // System.err.println("Add vertex with uv = " + uvLow +
                    // " / "
                    // + uvHigh + " on " + arcLength);
                    // + highPoint1 + " " + lowPoint);
                    // System.err.println("create triangle " + lowPoint + " "
                    // + highPoint1 + " " + lowPoint1);
                    mesh.addIndices(p0HighIndex, p1HighIndex, p0LowIndex);
                    mesh.addIndices(p0LowIndex, p1HighIndex, p1LowIndex);

                    p0LowIndex = p1LowIndex;
                    p0HighIndex = p1HighIndex;

                    VectorUtil.copy(previousLowPoint, interpolatedLowPoint);
                    VectorUtil.copy(previousHighPoint, interpolatedHighPoint);
                    VectorUtil.copy(previousLowVector, currentLowVector);
                    VectorUtil.copy(previousHighVector, currentHighVector);
                    // } else {
                    // System.err
                    // .println("skip triangle generation   low angle = "
                    // + lowCosAngle + " high = " + highCosAngle
                    // + " > " + minCosAngle);
                }
            }
            identityLowPoint0.x = identityLowPoint1.x;
            identityLowPoint0.y = identityLowPoint1.y;
            identityHighPoint0.x = identityHighPoint1.x;
            identityHighPoint0.y = identityHighPoint1.y;
            currentEdgeAndPointIndex++;
        }

        colorizer.finalizeColorization();
    }
    // /**
    // * @param complex
    // * @param getWidth
    // * @param polyline
    // * @throws FunctionEvaluationException
    // */
    // private static void tesselateThickLine(GLPaintingComplex complex,
    // Function1D getWidth, Point2D[] polyline, double miterLimit,
    // boolean closedLine) throws FunctionEvaluationException {
    // complex.setMayOverlap(false);
    //
    // // Algo tessellation
    // int edgeCount = polyline.length;
    // float arcLength = 0;
    // Point2D[] edges = new Point2D.Double[edgeCount];
    // Point2D[] normals = new Point2D.Double[edgeCount];
    //
    // for (int edgeIndex = 0; edgeIndex < edgeCount; edgeIndex++) {
    // Point2D p0 = polyline[edgeIndex];
    // Point2D p1 = polyline[(edgeIndex + 1) % edgeCount];
    // edges[edgeIndex] = new Point2D.Double(p1.getX() - p0.getX(),
    // p1.getY() - p0.getY());
    // normals[edgeIndex] = normalize(new Point2D.Double(
    // -edges[edgeIndex].getY(), edges[edgeIndex].getX()));
    // arcLength += length(edges[edgeIndex]);
    // }
    //
    // GLMesh mesh = complex.addGLMesh(GL11.GL_TRIANGLES);
    // float currentLength = 0;
    // int vertexIndex0 = 0;
    // int vertexIndex1 = 0;
    //
    // Point2D edge0 = edges[0];
    // Point2D normal0 = normals[0];
    // double l = getWidth.evaluate(currentLength / arcLength) / 2;
    // Point2D sideVector = mulDoublePoint2D(l, normal0);
    // Point2D vertex0 = addPoint2D(polyline[0], opposite(sideVector));
    // Point2D vertex1 = addPoint2D(polyline[0], sideVector);
    // vertexIndex0 = complex.addVertex(new GLPaintingVertex(vertex0));
    // vertexIndex1 = complex.addVertex(new GLPaintingVertex(vertex1));
    //
    // // treat all segments (draw segment + join to the next segment)
    // for (int currentEdgeIndex = 0; currentEdgeIndex < edgeCount;
    // currentEdgeIndex++) {
    // int nextEdgeIndex = (currentEdgeIndex + 1) % edgeCount;
    // Point2D endPoint0 = polyline[(currentEdgeIndex + 1) % edgeCount];
    // edge0 = edges[currentEdgeIndex];
    // normal0 = normals[currentEdgeIndex];
    // Point2D startPoint1 = endPoint0;
    // Point2D normal1 = normals[nextEdgeIndex];
    // Point2D edge1 = edges[nextEdgeIndex];
    //
    // // side to cap is in the same direction than normals ? then 1, else
    // // -1
    // int sideToCap = -1;
    // double turnDirection = dot(edge0, normal1);
    // double segmentLength = length(edge0);
    // if (segmentLength < epsilon) {
    // // if two points are at the same position, skip it
    // continue;
    // }
    // currentLength += segmentLength;
    //
    // if (turnDirection > epsilon) {
    // sideToCap = 1;
    // } else if (turnDirection < -epsilon) {
    // sideToCap = -1;
    // } else {
    // // if points are exactly aligned, we can go directly to the next
    // // point.
    // // if there is only two points in the line, add triangles
    // if (edgeCount == 1) {
    // // last point
    // Point2D vertex2 = addPoint2D(endPoint0,
    // mulDoublePoint2D(-l, normal0));
    // Point2D vertex3 = addPoint2D(endPoint0,
    // mulDoublePoint2D(l, normal0));
    // int vertexIndex2 = complex.addVertex(new GLPaintingVertex(
    // vertex2));
    // int vertexIndex3 = complex.addVertex(new GLPaintingVertex(
    // vertex3));
    // // add triangles
    // mesh.addIndices(vertexIndex0, vertexIndex1, vertexIndex2);
    // mesh.addIndices(vertexIndex1, vertexIndex2, vertexIndex3);
    // vertexIndex0 = vertexIndex2;
    // vertexIndex1 = vertexIndex3;
    // }
    // continue;
    // }
    // l = getWidth.evaluate(currentLength / arcLength) / 2;
    //
    // // join between lines
    // Point2D midPoint = null;
    // // midPoint = addPoint2D(endPoint0, mulDoublePoint2D(-sideToCap*l,
    // // addPoint2D(normal0, normal1)));
    // midPoint = lineIntersection(
    // addPoint2D(endPoint0,
    // mulDoublePoint2D(-sideToCap * l, normal0)),
    // edge0,
    // addPoint2D(endPoint0,
    // mulDoublePoint2D(-sideToCap * l, normal1)), edge1);
    //
    // if (currentEdgeIndex == edgeCount - 1 && !closedLine) {
    // // last point
    // Point2D vertex2 = addPoint2D(endPoint0,
    // mulDoublePoint2D(-l, normal0));
    // Point2D vertex3 = addPoint2D(endPoint0,
    // mulDoublePoint2D(l, normal0));
    // int vertexIndex2 = complex.addVertex(new GLPaintingVertex(
    // vertex2));
    // int vertexIndex3 = complex.addVertex(new GLPaintingVertex(
    // vertex3));
    // // add triangles
    // mesh.addIndices(vertexIndex0, vertexIndex1, vertexIndex2);
    // mesh.addIndices(vertexIndex1, vertexIndex2, vertexIndex3);
    // vertexIndex0 = vertexIndex2;
    // vertexIndex1 = vertexIndex3;
    // } else {
    //
    // Point2D miterPoint = lineIntersection(
    // addPoint2D(endPoint0,
    // mulDoublePoint2D(sideToCap * l, normal0)),
    // edge0,
    // addPoint2D(endPoint0,
    // mulDoublePoint2D(sideToCap * l, normal1)),
    // edge1);
    // if (distance(miterPoint, midPoint) < miterLimit * l * 2) {
    // // miter should be represented as bevel due to too small
    // // angle between lines
    // int miterVertexIndex = complex
    // .addVertex(new GLPaintingVertex(miterPoint));
    // int midVertexIndex = complex
    // .addVertex(new GLPaintingVertex(midPoint));
    // if (sideToCap == 1) {
    // mesh.addIndices(vertexIndex0, vertexIndex1,
    // midVertexIndex);
    // mesh.addIndices(vertexIndex1, midVertexIndex,
    // miterVertexIndex);
    // vertexIndex0 = midVertexIndex;
    // vertexIndex1 = miterVertexIndex;
    // } else {
    // mesh.addIndices(vertexIndex0, vertexIndex1,
    // miterVertexIndex);
    // mesh.addIndices(vertexIndex1, miterVertexIndex,
    // midVertexIndex);
    // vertexIndex0 = miterVertexIndex;
    // vertexIndex1 = midVertexIndex;
    // }
    // }
    // }
    // }
    //
    // }

    // /**
    // * @param complex
    // * @param dlist
    // * @param getWidth
    // * @param join
    // * @param cap
    // * @param miterLimit
    // * @param dMax
    // * @param minX
    // * @param minY
    // * @throws FunctionEvaluationException
    // */
    // public static void tesselateThickLine(GLPaintingComplex complex,
    // IDirectPositionList dlist, Function1D getWidth, double miterLimit,
    // double dMax, double minX, double minY)
    // throws FunctionEvaluationException {
    // int pointCount = dlist.size();
    // List<Point2D> polyline = new ArrayList<Point2D>();
    //
    // for (int i = 1; i < pointCount; i++) {
    //
    // IDirectPosition dp0 = dlist.get(i - 1);
    // IDirectPosition dp1 = dlist.get(i);
    // Point2D p0 = new Point2D.Double(dp0.getX() - minX, dp0.getY()
    // - minY);
    // Point2D p1 = new Point2D.Double(dp1.getX() - minX, dp1.getY()
    // - minY);
    // double segmentLength = VectorUtil.distance(p0, p1);
    // int segmentPointCount = (int) Math.floor(segmentLength / dMax);
    // for (int n = 0; n < segmentPointCount; n++) {
    // double alpha = n / (double) segmentPointCount;
    // polyline.add(new Point2D.Double(p0.getX() * (1 - alpha)
    // + p1.getX() * alpha, p0.getY() * (1 - alpha)
    // + p1.getY() * alpha));
    // }
    // }
    // polyline.add(new Point2D.Double(dlist.get(pointCount - 1).getX(), dlist
    // .get(pointCount - 1).getY()));
    //
    // // Point2D[] polyline = new Point2D.Double[pointCount];
    // tesselateThickLine(complex, getWidth,
    // polyline.toArray(new Point2D[] {}), miterLimit, false);
    // }
}
