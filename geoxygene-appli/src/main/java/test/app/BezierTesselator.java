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

package test.app;

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
import fr.ign.cogit.geoxygene.function.FunctionEvaluationException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.math.VectorUtil;

/**
 * @author JeT Utility method to generate line painting geometry
 */
public class BezierTesselator {

    private static final Logger logger = Logger
            .getLogger(BezierTesselator.class.getName()); // logger

    // private static final double anglePrecision = Math.PI / 40;
    // private static final double epsilon = 1E-6;
    // private static final Function1D identityLineWidth = new
    // ConstantFunction(1);
    // private static final Function1D identityLineShift = new
    // ConstantFunction(0);

    /**
     * Private constructor for utility class
     */
    private BezierTesselator() {
    }

    /**
     */
    public static double quadraticValue(double p0, double p1, double p2,
            double t) {
        return p0 * (1 - t) * (1 - t) + 2 * p1 * t * (1 - t) + p2 * t * t;
    }

    public static double quadraticDerivative(double p0, double p1, double p2,
            double t) {
        return -2 * p0 * (1 - t) + 2 * p1 * (1 - 2 * t) + 2 * p2 * t;
    }

    /**
     */
    public static double cubicValue(double p0, double p1, double p2, double p3,
            double t) {
        return p0 * (1 - t) * (1 - t) * (1 - t) + 3 * p1 * t * (1 - t)
                * (1 - t) + 3 * p2 * t * t * (1 - t) + p3 * t * t * t;
    }

    public static double cubicDerivative(double p0, double p1, double p2,
            double p3, double t) {
        return -3 * p0 * (1 - t) * (1 - t) + p1
                * (3 * (1 - t) * (1 - t) - 6 * (1 - t) * t) + p2
                * (6 * t * (1 - t) - 3 * t * t) + 3 * p3 * t * t;
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
            GLBezierShadingComplex complex, IDirectPositionList dlist,
            double lineWidth, double transitionSize, double minX, double minY,
            Colorizer c) throws FunctionEvaluationException {
        complex.setMayOverlap(true);
        int pointCount = dlist.size();
        Point2d[] polyline = new Point2d[pointCount];
        for (int i = 0; i < pointCount; ++i) {
            polyline[i] = new Point2d(dlist.get(i).getX() - minX, dlist.get(i)
                    .getY() - minY);
        }

        return tesselateThickLine(name, complex, polyline, lineWidth,
                transitionSize, c);
    }

    public static Task tesselateThickLine(String name,
            GLBezierShadingComplex complex, Point2d[] polyline,
            double lineWidth, double transitionSize, Colorizer colorizer)
            throws FunctionEvaluationException {

        if (polyline.length < 2) {
            System.err.println("line tesselation does not handle "
                    + polyline.length + " points counts");
            return null;
        }
        return new BezierTesselatorTask(name, polyline, complex, lineWidth,
                transitionSize, colorizer);
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

    private static class BezierTesselatorTask extends AbstractTask {

        private static final double EPSILON = 1E-3;
        private final Point2d[] polyline;
        private final GLBezierShadingComplex complex;
        private final double lineWidth;
        private final double transitionSize;
        private final Colorizer colorizer;

        private BezierTesselatorTask(String name, Point2d[] polyline,
                GLBezierShadingComplex complex, double lineWidth,
                double transitionSize, Colorizer c) {
            super(name + "-tesselation");
            this.polyline = polyline;
            this.complex = complex;
            this.lineWidth = lineWidth;
            this.transitionSize = transitionSize;
            this.colorizer = c;
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
                float currentLength = 0;
                this.colorizer.initializeColorization();
                int pointCount = this.polyline.length;
                int edgeCount = this.polyline.length - 1;
                Point2d[] edges = new Point2d[edgeCount];
                Point2d[] normals = new Point2d[edgeCount];
                float[] uParams = new float[pointCount];

                for (int edgeIndex = 0; edgeIndex < edgeCount; edgeIndex++) {
                    Point2d p0 = this.polyline[edgeIndex];
                    Point2d p1 = this.polyline[edgeIndex + 1];
                    Point2d v = new Point2d();
                    VectorUtil.vector(v, p0, p1);
                    edges[edgeIndex] = new Point2d(p1.x - p0.x, p1.y - p0.y);
                    uParams[edgeIndex] = currentLength;
                    currentLength += length(edges[edgeIndex]);
                    normals[edgeIndex] = new Point2d(p0.y - p1.y, p1.x - p0.x);
                    normalize(normals[edgeIndex], normals[edgeIndex]);
                }
                if (this.isStopRequested()) {
                    this.setState(TaskState.STOPPED);
                    return;
                }
                this.setState(TaskState.RUNNING);
                uParams[pointCount - 1] = currentLength;
                float uMax = currentLength;

                Point2d p0 = this.polyline[0];
                Point2d n0 = normals[0];
                Point2d inputLow = new Point2d(
                        p0.x + this.lineWidth / 2 * n0.x, p0.y + this.lineWidth
                                / 2 * n0.y);
                Point2d inputHigh = new Point2d(p0.x - this.lineWidth / 2
                        * n0.x, p0.y - this.lineWidth / 2 * n0.y);
                Point2d outputLow = new Point2d();
                Point2d outputHigh = new Point2d();

                GLMesh mesh = this.complex.addGLMesh(GL11.GL_TRIANGLES);
                float outputU = this.createSegment(mesh, inputLow, inputHigh,
                        uParams[0], this.polyline[0], edges, normals, uParams,
                        uMax, -1, 0, 1, outputLow, outputHigh);
                VectorUtil.copy(inputLow, outputLow);
                VectorUtil.copy(inputHigh, outputHigh);
                int currentEdgeAndPointIndex = 1; // get to second point
                while (currentEdgeAndPointIndex < edgeCount - 1) {
                    // System.err.println("p[" + currentEdgeAndPointIndex +
                    // "] = "
                    // + this.polyline[currentEdgeAndPointIndex]);
                    if (this.isStopRequested()) {
                        this.setState(TaskState.STOPPED);
                        return;
                    }
                    this.setProgress(currentEdgeAndPointIndex
                            / (double) edgeCount);

                    outputU = this
                            .createSegment(mesh, inputLow, inputHigh, outputU,
                                    this.polyline[currentEdgeAndPointIndex],
                                    edges, normals, uParams, uMax,
                                    currentEdgeAndPointIndex - 1,
                                    currentEdgeAndPointIndex,
                                    currentEdgeAndPointIndex + 1, outputLow,
                                    outputHigh);

                    VectorUtil.copy(inputLow, outputLow);
                    VectorUtil.copy(inputHigh, outputHigh);
                    currentEdgeAndPointIndex++;
                }
                this.createSegment(mesh, inputLow, inputHigh, outputU,
                        this.polyline[pointCount - 2], edges, normals, uParams,
                        uMax, edgeCount - 2, edgeCount - 1, -1, outputLow,
                        outputHigh);

                this.setState(TaskState.FINALIZING);
                this.colorizer.finalizeColorization();
                this.setState(TaskState.FINISHED);
            } catch (Exception e) {
                e.printStackTrace();
                this.setError(e);
                this.setState(TaskState.ERROR);
            }
        }

        private float createSegment(GLMesh mesh, Point2d inputLow,
                Point2d inputHigh, float u0, Point2d p0, Point2d[] edges,
                Point2d[] normals, float[] uParams, float uMax,
                int previousIndex, int currentIndex, int nextIndex,
                Point2d outputLow, Point2d outputHigh) {
            Point2d previousEdge = null;
            Point2d currentEdge = edges[currentIndex];
            Point2d nextEdge = null;
            Point2d previousNormal = null;
            Point2d currentNormal = normals[currentIndex];
            Point2d nextNormal = null;
            float u1 = uParams[currentIndex + 1];

            if (previousIndex >= 0) {
                previousEdge = edges[previousIndex];
                previousNormal = edges[previousIndex];
            }
            if (nextIndex >= 0 && nextIndex < edges.length) {
                nextEdge = edges[nextIndex];
                nextNormal = edges[nextIndex];
            }
            // isolated segment
            if (previousEdge == null && nextEdge == null) {
                Point2d p1 = new Point2d(p0.x + currentEdge.x, p0.y
                        + currentEdge.y);
                this.createStraightSegment(mesh, inputLow, inputHigh, p1,
                        currentNormal, uMax, u0, u1, outputLow, outputHigh);
                return u1;
            }

            if (nextEdge != null) {
                Point2d p1 = new Point2d(p0.x + currentEdge.x, p0.y
                        + currentEdge.y);
                double currentLength = VectorUtil.length(currentEdge);
                double nextLength = VectorUtil.length(nextEdge);
                if (currentLength > this.transitionSize + EPSILON
                        && nextLength > this.transitionSize * 2 + EPSILON) {
                    double factorA = (currentLength - this.transitionSize)
                            / VectorUtil.length(currentEdge);
                    double factorB = this.transitionSize
                            / VectorUtil.length(nextEdge);
                    Point2d pA = new Point2d(p0.x + factorA * currentEdge.x,
                            p0.y + factorA * currentEdge.y);
                    Point2d pB = new Point2d(p1.x + factorB * nextEdge.x, p1.y
                            + factorB * nextEdge.y);
                    double uA = u0 + (currentLength - this.transitionSize);
                    double uB = u1 + this.transitionSize;
                    this.createStraightSegment(mesh, inputLow, inputHigh, pA,
                            currentNormal, uMax, u0, uA, outputLow, outputHigh);
                    this.createBezierTurn(mesh, pA, pB, edges, normals, uA, uB,
                            uMax, currentIndex, nextIndex, outputLow,
                            outputHigh);
                    return (float) uB;
                } else {
                    this.createAngularSegment(mesh, inputLow, inputHigh, p1,
                            edges, normals, uParams, uMax, currentIndex,
                            nextIndex, outputLow, outputHigh);
                    return uParams[nextIndex];
                }

            }

            if (previousEdge != null && nextEdge == null) {
                Point2d p1 = new Point2d(p0.x + currentEdge.x, p0.y
                        + currentEdge.y);
                this.createStraightSegment(mesh, inputLow, inputHigh, p1,
                        currentNormal, uMax, u0, u1, outputLow, outputHigh);
                return u1;

            }
            throw new IllegalStateException("should not be here...");
        }

        private Point2d intersection(Point2d p0, Point2d v0, Point2d p1,
                Point2d v1) {
            Point2d inter = new Point2d();
            VectorUtil.lineIntersection(inter, p0, v0, p1, v1);
            return inter;
        }

        /**
         * @param p0
         * @param p1
         * @return
         */
        private Point2d normal(Point2d p0, Point2d p1) {
            Point2d v = new Point2d(p1.x - p0.x, p1.y - p0.y);
            VectorUtil.normalize(v, v);
            return v;
        }

        private void createBezierTurn(GLMesh mesh, Point2d p0, Point2d p2,
                Point2d[] edges, Point2d[] normals, double u0, double u2,
                float uMax, int index0, int index2, Point2d outputLow,
                Point2d outputHigh) {
            Point2d p1 = new Point2d();
            VectorUtil.lineIntersection(p1, p0, edges[index0], p2,
                    edges[index2]);
            Point2d n0 = this.normal(p0, p1);
            Point2d n2 = this.normal(p1, p2);
            double angle = n0.x * n2.y - n0.y * n2.x;
            int sign = angle > 0 ? 1 : -1;
            Point2d p0low = new Point2d(
                    p0.x + sign * n0.y * this.lineWidth / 2, p0.y - sign * n0.x
                            * this.lineWidth / 2);
            Point2d p0high = new Point2d(p0.x - sign * n0.y * this.lineWidth
                    / 2, p0.y + sign * n0.x * this.lineWidth / 2);
            Point2d p2low = new Point2d(
                    p2.x + sign * n2.y * this.lineWidth / 2, p2.y - sign * n2.x
                            * this.lineWidth / 2);
            Point2d p2high = new Point2d(p2.x - sign * n2.y * this.lineWidth
                    / 2, p2.y + sign * n2.x * this.lineWidth / 2);
            Point2d p1low = this.intersection(p0low, n0, p2low, n2);
            Point2d p1high = this.intersection(p0high, n0, p2high, n2);
            // System.err.println("angle = " + angle);
            // this.drawLine(g, p0, p0low);
            // this.drawLine(g, p0low, p1low);
            // this.drawLine(g, p1low, p2low);
            // this.drawLine(g, p2low, p2);
            // this.drawLine(g, p2, p2high);
            // this.drawLine(g, p2high, p1high);
            // this.drawLine(g, p1high, p0high);
            // this.drawLine(g, p0high, p0);

            Point2d A = p0low;
            Point2d B = new Point2d();
            Point2d C = new Point2d();
            Point2d D = p1low;
            Point2d E = p1high;
            Point2d F = new Point2d();
            Point2d G = p0high;

            double px = quadraticValue(p0.x, p1.x, p2.x, 0.5);
            double py = quadraticValue(p0.y, p1.y, p2.y, 0.5);
            double vx = quadraticDerivative(p0.x, p1.x, p2.x, 0.5);
            double vy = quadraticDerivative(p0.y, p1.y, p2.y, 0.5);
            Point2d tangent = new Point2d(vx, vy);
            VectorUtil.normalize(tangent, tangent);

            Point2d centerHigh = new Point2d(px - sign * tangent.y
                    * this.lineWidth / 2, py + sign * tangent.x
                    * this.lineWidth / 2);
            Point2d centerLow = new Point2d(px + sign * tangent.y
                    * this.lineWidth / 2, py - sign * tangent.x
                    * this.lineWidth / 2);
            VectorUtil.lineIntersection(B, centerLow, tangent, p0low, n0);
            VectorUtil.lineIntersection(C, centerLow, tangent, p2low, n2);

            VectorUtil.copy(A, p0low);
            VectorUtil.copy(D, p2low);
            VectorUtil.copy(E, p2high);
            F.x = centerHigh.x;
            F.y = centerHigh.y;
            VectorUtil.copy(G, p0high);

            Point2d uv = new Point2d(u0, 0);
            Color col = this.colorizer.getColor(uv.x, uv.y);
            GLBezierShadingVertex vertexA = new GLBezierShadingVertex(A, uv,
                    col, (float) this.lineWidth, uMax, p0, p1, p2);
            int aIndex = this.complex.addVertex(vertexA);
            uv = new Point2d((u0 + (u2 - u0) / 3), 0);
            col = this.colorizer.getColor(uv.x, uv.y);
            GLBezierShadingVertex vertexB = new GLBezierShadingVertex(B, uv,
                    col, (float) this.lineWidth, uMax, p0, p1, p2);
            int bIndex = this.complex.addVertex(vertexB);

            uv = new Point2d((u0 + 2 * (u2 - u0) / 3), 0);
            col = this.colorizer.getColor(uv.x, uv.y);
            GLBezierShadingVertex vertexC = new GLBezierShadingVertex(C, uv,
                    col, (float) this.lineWidth, uMax, p0, p1, p2);
            int cIndex = this.complex.addVertex(vertexC);

            uv = new Point2d(u2, 0);
            col = this.colorizer.getColor(uv.x, uv.y);
            GLBezierShadingVertex vertexD = new GLBezierShadingVertex(D, uv,
                    col, (float) this.lineWidth, uMax, p0, p1, p2);
            int dIndex = this.complex.addVertex(vertexD);

            uv = new Point2d(u2, 1);
            col = this.colorizer.getColor(uv.x, uv.y);
            GLBezierShadingVertex vertexE = new GLBezierShadingVertex(E, uv,
                    col, (float) this.lineWidth, uMax, p0, p1, p2);
            int eIndex = this.complex.addVertex(vertexE);

            uv = new Point2d((u0 + u2) / 2., 1);
            col = this.colorizer.getColor(uv.x, uv.y);
            GLBezierShadingVertex vertexF = new GLBezierShadingVertex(F, uv,
                    col, (float) this.lineWidth, uMax, p0, p1, p2);
            int fIndex = this.complex.addVertex(vertexF);

            uv = new Point2d(u0, 1);
            col = this.colorizer.getColor(uv.x, uv.y);
            GLBezierShadingVertex vertexG = new GLBezierShadingVertex(G, uv,
                    col, (float) this.lineWidth, uMax, p0, p1, p2);
            int gIndex = this.complex.addVertex(vertexG);

            mesh.addIndices(aIndex, bIndex, gIndex);
            mesh.addIndices(gIndex, bIndex, fIndex);
            mesh.addIndices(fIndex, bIndex, cIndex);
            mesh.addIndices(fIndex, cIndex, eIndex);
            mesh.addIndices(eIndex, cIndex, dIndex);
            if (sign < 0) {
                VectorUtil.copy(outputLow, D);
                VectorUtil.copy(outputHigh, E);
            } else {
                VectorUtil.copy(outputLow, E);
                VectorUtil.copy(outputHigh, D);
            }
        }

        private void createAngularSegment(GLMesh mesh, Point2d inputLow,
                Point2d inputHigh, Point2d p1, Point2d[] edges,
                Point2d[] normals, float[] uParams, float uMax,
                int currentIndex, int nextIndex, Point2d outputLow,
                Point2d outputHigh) {
            Point2d n0 = normals[currentIndex];
            Point2d n1 = normals[nextIndex];
            Point2d p0 = new Point2d((inputLow.x + inputHigh.x) / 2,
                    (inputLow.y + inputHigh.y) / 2);

            Point2d p1low = new Point2d();
            Point2d p1high = new Point2d();
            fillAngularPoints(p1low, p1high, p0, n0, p1, n1, this.lineWidth);
            this.addPolygon(mesh, p0, p1, inputLow, p1low, p1high, inputHigh,
                    uParams[currentIndex], uParams[nextIndex], uMax);
            VectorUtil.copy(outputLow, p1low);
            VectorUtil.copy(outputHigh, p1high);
        }

        /**
         * @param mesh
         * @param p0
         * @param uMax
         * @param currentEdge
         * @param currentNormal
         * @param u0
         * @param u1
         */
        private void createStraightSegment(GLMesh mesh, Point2d inputLow,
                Point2d inputHigh, Point2d p1, Point2d n1, float uMax,
                double u0, double u1, Point2d outputLow, Point2d outputHigh) {

            Point2d p1low = new Point2d(p1.x + n1.x * this.lineWidth / 2, p1.y
                    + n1.y * this.lineWidth / 2);
            Point2d p1high = new Point2d(p1.x - n1.x * this.lineWidth / 2, p1.y
                    - n1.y * this.lineWidth / 2);
            Point2d p0 = new Point2d((inputLow.x + inputHigh.x) / 2,
                    (inputLow.y + inputHigh.y) / 2);
            this.addPolygon(mesh, p0, p1, inputLow, p1low, p1high, inputHigh,
                    u0, u1, uMax);
            VectorUtil.copy(outputLow, p1low);
            VectorUtil.copy(outputHigh, p1high);
        }

        /**
         * Create a quadrangle composed of 2 triangles between p0(low/high) and
         * p1(low/high)
         * 
         * @param mesh
         * @param p0
         * @param p1
         * @param p0low
         * @param p1low
         * @param p1high
         * @param p0high
         * @param u0
         * @param u1
         * @param uMax
         */
        private void addPolygon(GLMesh mesh, Point2d p0, Point2d p1,
                Point2d p0low, Point2d p1low, Point2d p1high, Point2d p0high,
                double u0, double u1, float uMax) {
            Point2d bezier0 = new Point2d(p0.x, p0.y);
            Point2d bezier1 = new Point2d((p0.x + p1.x) / 2, (p0.y + p1.y) / 2);
            Point2d bezier2 = new Point2d(p1.x, p1.y);
            Point2d uv = new Point2d(u0, 0);
            Color c = this.colorizer.getColor(uv.x, uv.y);
            GLBezierShadingVertex vertex0low = new GLBezierShadingVertex(p0low,
                    uv, c, (float) this.lineWidth, uMax, bezier0, bezier1,
                    bezier2);
            uv = new Point2d(u0, 1);
            c = this.colorizer.getColor(uv.x, uv.y);
            GLBezierShadingVertex vertex0high = new GLBezierShadingVertex(
                    p0high, uv, c, (float) this.lineWidth, uMax, bezier0,
                    bezier1, bezier2);
            uv = new Point2d(u1, 0);
            c = this.colorizer.getColor(uv.x, uv.y);
            GLBezierShadingVertex vertex1low = new GLBezierShadingVertex(p1low,
                    uv, c, (float) this.lineWidth, uMax, bezier0, bezier1,
                    bezier2);
            uv = new Point2d(u1, 1);
            c = this.colorizer.getColor(uv.x, uv.y);
            GLBezierShadingVertex vertex1high = new GLBezierShadingVertex(
                    p1high, uv, c, (float) this.lineWidth, uMax, bezier0,
                    bezier1, bezier2);
            int p0lowIndex = this.complex.addVertex(vertex0low);
            int p0highIndex = this.complex.addVertex(vertex0high);
            int p1lowIndex = this.complex.addVertex(vertex1low);
            int p1highIndex = this.complex.addVertex(vertex1high);
            mesh.addIndices(p0lowIndex, p1lowIndex, p1highIndex);
            mesh.addIndices(p0lowIndex, p1highIndex, p0highIndex);
        }

        private static void fillAngularPoints(Point2d iLow, Point2d iHigh,
                Point2d p0, Point2d n0, Point2d p1, Point2d n1, double lineWidth) {
            double cosAlpha = VectorUtil.dot(n0, n1);
            int sign = (n0.x * n1.y - n0.y * n1.x) > 0 ? 1 : -1;
            sign = 1;
            double alpha = 0;
            if (cosAlpha >= 0.9999) {
                alpha = 0;
            } else {
                alpha = Math.acos(cosAlpha);
            }
            double factor = Math.tan(alpha / 2.);
            factor = Math.min(5, 1 + factor * factor); // 1 + tan2 ( a /
                                                       // 2 )

            Point2d middleNormal = new Point2d(factor * (n0.x + n1.x) / 2.,
                    factor * (n0.y + n1.y) / 2.);
            // compute low and high points
            iLow.x = p1.x + sign * middleNormal.x * lineWidth / 2;
            iLow.y = p1.y + sign * middleNormal.y * lineWidth / 2;
            iHigh.x = p1.x - sign * middleNormal.x * lineWidth / 2;
            iHigh.y = p1.y - sign * middleNormal.y * lineWidth / 2;

        }
    }
}
