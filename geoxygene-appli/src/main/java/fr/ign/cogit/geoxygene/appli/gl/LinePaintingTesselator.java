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

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.appli.render.primitive.Colorizer;
import fr.ign.cogit.geoxygene.appli.task.AbstractTask;
import fr.ign.cogit.geoxygene.appli.task.Task;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.function.FunctionEvaluationException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.math.VectorUtil;

/**
 * @author JeT Utility method to generate line painting geometry
 */
public class LinePaintingTesselator {

    private static final Logger logger = Logger
            .getLogger(LinePaintingTesselator.class.getName()); // logger

    // private static final double anglePrecision = Math.PI / 40;
    // private static final double epsilon = 1E-6;
    // private static final Function1D identityLineWidth = new
    // ConstantFunction(1);
    // private static final Function1D identityLineShift = new
    // ConstantFunction(0);

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
    public static Task tesselateThickLine(String name,
            GLPaintingComplex complex, IDirectPositionList dlist,
            Function1D lineWidth, Function1D lineShift, double maxLength,
            double minAngle, double minX, double minY, Colorizer c,
            int paperWidthInPixels, int paperHeightInPixels,
            double paperHeightInCm, double mapScale)
            throws FunctionEvaluationException {
        complex.setMayOverlap(true);
        int pointCount = dlist.size();
        Point2d[] polyline = new Point2d[pointCount];
        for (int i = 0; i < pointCount; ++i) {
            polyline[i] = new Point2d(dlist.get(i).getX() - minX, dlist.get(i)
                    .getY() - minY);
        }

        return tesselateThickLine(name, complex, lineWidth, lineShift,
                polyline, maxLength, minAngle, false, c, paperWidthInPixels,
                paperHeightInPixels, paperHeightInCm, mapScale);
    }

    public static Task tesselateThickLine(String name,
            GLPaintingComplex complex, Function1D lineWidth,
            Function1D lineShift, Point2d[] polyline, double maxLength,
            double minAngle, boolean closedLine, Colorizer colorizer,
            int paperWidthInPixels, int paperHeightInPixels,
            double paperHeightInCm, double mapScale)
            throws FunctionEvaluationException {

        if (polyline.length < 2) {
            System.err.println("line tesselation does not handle "
                    + polyline.length + " points counts");
            return null;
        }
        return new LinePaintingTesselatorTask(name, polyline, complex,
                lineWidth, lineShift, maxLength, minAngle, colorizer,
                paperWidthInPixels, paperHeightInPixels, paperHeightInCm,
                mapScale);
    }

    // public static Task tesselateThickLine(String name,
    // GLPaintingComplex complex, Function1D lineWidth,
    // Function1D lineShift, Point2d[] polyline, double maxLength,
    // double minAngle, boolean closedLine, Colorizer colorizer)
    // throws FunctionEvaluationException {
    //
    // if (polyline.length < 2) {
    // System.err.println("line tesselation does not handle "
    // + polyline.length + " points counts");
    // return null;
    // }
    // return new LinePaintingTesselatorTask(name, polyline, complex,
    // lineWidth, lineShift, maxLength, minAngle, colorizer);
    // }

    private static class LinePaintingTesselatorTask extends AbstractTask {

        private final Point2d[] polyline;
        private final GLPaintingComplex complex;
        private final Function1D lineWidth;
        private final Function1D lineShift;
        private final double maxLength;
        private final double minAngle;
        private final Colorizer colorizer;
        public double paperWidthInWorldCoordinates = 1.;
        public double paperHeightInWorldCoordinates = 1.;

        private LinePaintingTesselatorTask(String name, Point2d[] polyline,
                GLPaintingComplex complex, Function1D lineWidth,
                Function1D lineShift, double maxLength, double minAngle,
                Colorizer c, int paperWidthInPixels, int paperHeightInPixels,
                double paperHeightInCm, double mapScale) {
            super(name + "-tesselation");
            this.polyline = polyline;
            this.complex = complex;
            this.lineWidth = lineWidth;
            this.lineShift = lineShift;
            this.maxLength = maxLength;
            this.minAngle = minAngle;
            this.colorizer = c;

            double psf = paperHeightInCm * mapScale
                    / (100. * paperHeightInPixels);
            this.paperWidthInWorldCoordinates = psf * paperWidthInPixels;
            this.paperHeightInWorldCoordinates = psf * paperHeightInPixels;
        }

        @Override
        public boolean isProgressable() {
            return true;
        }

        @Override
        public boolean isPausable() {
            return false;
        }

        @Override
        public boolean isStoppable() {
            return true;
        }

        @Override
        public void run() {
            try {
                this.setState(TaskState.INITIALIZING);
                double arcLength = 0;
                double minCosAngle = Math.cos(this.minAngle);
                this.colorizer.initializeColorization();
                int pointCount = this.polyline.length;
                int edgeCount = this.polyline.length - 1;
                Point2d[] edges = new Point2d[edgeCount];
                Point2d[] normals = new Point2d[edgeCount];
                Double[] params = new Double[pointCount];
                for (int edgeIndex = 0; edgeIndex < edgeCount; edgeIndex++) {
                    Point2d p0 = this.polyline[edgeIndex];
                    Point2d p1 = this.polyline[edgeIndex + 1];
                    edges[edgeIndex] = new Point2d();
                    VectorUtil.vector(edges[edgeIndex], p0, p1);
                    normals[edgeIndex] = new Point2d();
                    normalize(normals[edgeIndex],
                            new Point2d(-edges[edgeIndex].getY(),
                                    edges[edgeIndex].getX()));
                    params[edgeIndex] = new Double(0);
                    params[edgeIndex] = arcLength;
                    arcLength += length(edges[edgeIndex]);
                    // System.err.println("segment #" + edgeIndex + "  p0 " + p0
                    // +
                    // "  p1 "
                    // + p1 + "  edge : " + edges[edgeIndex] + "  normal : "
                    // + normals[edgeIndex]);
                }
                if (this.isStopRequested()) {
                    this.setState(TaskState.STOPPED);
                    return;
                }
                this.setState(TaskState.RUNNING);
                params[pointCount - 1] = arcLength;

                int currentEdgeAndPointIndex = 0;
                Point2d edge = edges[currentEdgeAndPointIndex];
                Point2d normal = normals[currentEdgeAndPointIndex];
                Double param = params[currentEdgeAndPointIndex];
                Double nextParam = new Double(0);
                Point2d p = new Point2d(this.polyline[currentEdgeAndPointIndex]);
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
                // identity points are interpolated to keep a maximum segment
                // size
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
                double width = this.lineWidth.evaluate(0);
                double shift = this.lineShift.evaluate(0);
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
                Color cLow = this.colorizer.getColor(uvLow.x, uvLow.y);
                Color cHigh = this.colorizer.getColor(uvHigh.x, uvHigh.y);
                Point2d paperUV = this.computePaperUV(lowPoint0,
                        this.complex.getMinX(), this.complex.getMinY());
                int p0LowIndex = this.complex.addVertex(new GLPaintingVertex(
                        (float) lowPoint0.x, (float) lowPoint0.y,
                        (float) uvLow.x, (float) uvLow.y, (float) paperUV.x,
                        (float) paperUV.y, (float) arcLength, 0f, 0f, 0f, 0f,
                        cLow.getRed() / 255f, cLow.getGreen() / 255f, cLow
                                .getBlue() / 255f, cLow.getAlpha() / 255f));
                paperUV = this.computePaperUV(highPoint0,
                        this.complex.getMinX(), this.complex.getMinY());
                int p0HighIndex = this.complex.addVertex(new GLPaintingVertex(
                        (float) highPoint0.x, (float) highPoint0.y,
                        (float) uvHigh.x, (float) uvHigh.y, (float) paperUV.x,
                        (float) paperUV.y, (float) arcLength, 0f, 0f, 0f, 0f,
                        cHigh.getRed() / 255f, cHigh.getGreen() / 255f, cHigh
                                .getBlue() / 255f, cHigh.getAlpha() / 255f));
                // System.err.println("Add vertex with uv = " + uvLow + " / " +
                // uvHigh + " on " + arcLength);

                GLMesh mesh = this.complex.addGLMesh(GL11.GL_TRIANGLES);
                while (currentEdgeAndPointIndex < edgeCount) {
                    if (this.isStopRequested()) {
                        this.setState(TaskState.STOPPED);
                        return;
                    }
                    this.setProgress(currentEdgeAndPointIndex
                            / (double) edgeCount);
                    int nextEdgeAndPointIndex = currentEdgeAndPointIndex + 1;
                    // Point2d nextNormal = normals[nextEdgeAndPointIndex];
                    // Point2d nextEdge = edges[nextEdgeAndPointIndex];
                    param = params[currentEdgeAndPointIndex];
                    nextParam = params[nextEdgeAndPointIndex];
                    Point2d p0 = this.polyline[currentEdgeAndPointIndex];
                    Point2d p1 = this.polyline[nextEdgeAndPointIndex];
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
                    } else {
                        alpha = Math.acos(cosAlpha);
                    }
                    double factor = Math.tan(alpha / 2.);
                    factor = Math.min(5, 1 + factor * factor); // 1 + tan2 ( a /
                                                               // 2 )

                    Point2d middleNormal = new Point2d(factor * (n0.x + n1.x)
                            / 2., factor * (n0.y + n1.y) / 2.);
                    // compute end-of-edge identity expansion
                    identityLowPoint1.x = p1.x + middleNormal.x;
                    identityLowPoint1.y = p1.y + middleNormal.y;
                    identityHighPoint1.x = p1.x - middleNormal.x;
                    identityHighPoint1.y = p1.y - middleNormal.y;

                    // System.err.println("low0 " + identityLowPoint0 + " low1 "
                    // + identityLowPoint1);
                    // System.err.println("high0 " + identityHighPoint0 +
                    // " high1 "
                    // + identityHighPoint1);
                    // System.err.println("middle normal = " + middleNormal);
                    // interpolate between identity low/highPoints0 & 1

                    int nbSegments = (int) Math.max(1,
                            Math.ceil(edgeLength / this.maxLength));
                    Point2d deltaIdentityLow = new Point2d(
                            (identityLowPoint1.x - identityLowPoint0.x)
                                    / nbSegments,
                            (identityLowPoint1.y - identityLowPoint0.y)
                                    / nbSegments);
                    Point2d deltaIdentityHigh = new Point2d(
                            (identityHighPoint1.x - identityHighPoint0.x)
                                    / nbSegments,
                            (identityHighPoint1.y - identityHighPoint0.y)
                                    / nbSegments);
                    double deltaParam = (nextParam - param) / nbSegments;

                    for (int nSegment = 1; nSegment <= nbSegments; nSegment++) {
                        // interpolated Low & High identity points
                        interpolatedIdentityLowPoint.x = identityLowPoint0.x
                                + nSegment * deltaIdentityLow.x;
                        interpolatedIdentityLowPoint.y = identityLowPoint0.y
                                + nSegment * deltaIdentityLow.y;
                        interpolatedIdentityHighPoint.x = identityHighPoint0.x
                                + nSegment * deltaIdentityHigh.x;
                        interpolatedIdentityHighPoint.y = identityHighPoint0.y
                                + nSegment * deltaIdentityHigh.y;
                        double t = param + nSegment * deltaParam;
                        // compute mid point and normal using interpolated Low &
                        // High
                        // Points
                        p.x = (interpolatedIdentityLowPoint.x + interpolatedIdentityHighPoint.x) / 2;
                        p.y = (interpolatedIdentityLowPoint.y + interpolatedIdentityHighPoint.y) / 2;
                        n.x = (interpolatedIdentityLowPoint.x - interpolatedIdentityHighPoint.x) / 2;
                        n.y = (interpolatedIdentityLowPoint.y - interpolatedIdentityHighPoint.y) / 2;
                        shift = this.lineShift.evaluate(t);
                        width = this.lineWidth.evaluate(t);
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
                        VectorUtil
                                .normalize(currentLowVector, currentLowVector);
                        VectorUtil.normalize(currentHighVector,
                                currentHighVector);
                        // System.err.println("segment #" + nSegment);
                        // System.err.println(" previous LowPoint" +
                        // previousLowPoint
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
                            lowCosAngle = VectorUtil
                                    .dot(currentLowVector, edge) / edgeLength;
                            highCosAngle = VectorUtil.dot(currentHighVector,
                                    edge) / edgeLength;
                        }

                        if (nSegment == nbSegments
                                || lowCosAngle <= minCosAngle
                                || highCosAngle <= minCosAngle) {

                            uvLow.x = t;
                            uvLow.y = 0;
                            uvHigh.x = t;
                            uvHigh.y = 1;
                            cLow = this.colorizer.getColor(uvLow.x, uvLow.y);
                            cHigh = this.colorizer.getColor(uvHigh.x, uvHigh.y);

                            paperUV = this.computePaperUV(interpolatedLowPoint,
                                    this.complex.getMinX(),
                                    this.complex.getMinY());
                            int p1LowIndex = this.complex
                                    .addVertex(new GLPaintingVertex(
                                            (float) interpolatedLowPoint.x,
                                            (float) interpolatedLowPoint.y,
                                            (float) uvLow.x, (float) uvLow.y,
                                            (float) paperUV.x,
                                            (float) paperUV.y,
                                            (float) arcLength, (float) n.x,
                                            (float) n.y, 0f, 0f,
                                            cLow.getRed() / 255f, cLow
                                                    .getGreen() / 255f, cLow
                                                    .getBlue() / 255f, cLow
                                                    .getAlpha() / 255f));
                            paperUV = this.computePaperUV(
                                    interpolatedHighPoint,
                                    this.complex.getMinX(),
                                    this.complex.getMinY());
                            int p1HighIndex = this.complex
                                    .addVertex(new GLPaintingVertex(
                                            (float) interpolatedHighPoint.x,
                                            (float) interpolatedHighPoint.y,
                                            (float) uvHigh.x, (float) uvHigh.y,
                                            (float) paperUV.x,
                                            (float) paperUV.y,
                                            (float) arcLength, (float) n.x,
                                            (float) n.y, 0f, 0f,
                                            cHigh.getRed() / 255f, cHigh
                                                    .getGreen() / 255f, cHigh
                                                    .getBlue() / 255f, cHigh
                                                    .getAlpha() / 255f));
                            // System.err.println("Add vertex with uv = " +
                            // uvLow +
                            // " / "
                            // + uvHigh + " on " + arcLength);
                            // + highPoint1 + " " + lowPoint);
                            // System.err.println("create triangle " + lowPoint
                            // + " "
                            // + highPoint1 + " " + lowPoint1);
                            mesh.addIndices(p0HighIndex, p1HighIndex,
                                    p0LowIndex);
                            mesh.addIndices(p0LowIndex, p1HighIndex, p1LowIndex);

                            p0LowIndex = p1LowIndex;
                            p0HighIndex = p1HighIndex;

                            VectorUtil.copy(previousLowPoint,
                                    interpolatedLowPoint);
                            VectorUtil.copy(previousHighPoint,
                                    interpolatedHighPoint);
                            VectorUtil
                                    .copy(previousLowVector, currentLowVector);
                            VectorUtil.copy(previousHighVector,
                                    currentHighVector);
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

                this.setState(TaskState.FINALIZING);
                this.colorizer.finalizeColorization();
                this.setState(TaskState.FINISHED);
            } catch (Exception e) {
                this.setError(e);
                this.setState(TaskState.ERROR);
            }
        }

        /**
         * Compute texture coordinates for the given paper texture
         * 
         * @param p
         *            point in world coordinates
         * @return texture coordinates in paper coordinates
         */
        private Point2d computePaperUV(Point2d p, double minX, double minY) {
            // TODO: may be we can use minX, minY to computed reduced texture
            // coordinates... (p.xy can be large...)
            double x = ((p.x + minX % this.paperWidthInWorldCoordinates))
                    / this.paperWidthInWorldCoordinates;
            double y = ((p.y + minY % this.paperHeightInWorldCoordinates))
                    / this.paperHeightInWorldCoordinates;
            return new Point2d(x, y);
        }
    }
}
