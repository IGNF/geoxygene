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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
 * Utility method to generate line painting geometry
 * @author JeT
 * @author Bertrand Dumenieu
 */
public class BezierTesselator {

  private static final Logger logger = Logger.getLogger(BezierTesselator.class
      .getName()); // logger

  /**
   * Private constructor for utility class
   */
  private BezierTesselator() {
  }

  /**
     */
  public static double quadraticValue(double p0, double p1, double p2, double t) {
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
    return p0 * (1 - t) * (1 - t) * (1 - t) + 3 * p1 * t * (1 - t) * (1 - t)
        + 3 * p2 * t * t * (1 - t) + p3 * t * t * t;
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
      Colorizer c, int paperWidthInPixels, int paperHeightInPixels,
      double paperHeightInCm, double mapScale)
      throws FunctionEvaluationException {
    complex.setMayOverlap(true);
    int pointCount = dlist.size();
    Point2d[] polyline = new Point2d[pointCount];
    for (int i = 0; i < pointCount; ++i) {
      polyline[i] = new Point2d(dlist.get(i).getX() - minX, dlist.get(i).getY()
          - minY);
    }

    return tesselateThickLine(name, complex, polyline, lineWidth,
        transitionSize, c, paperWidthInPixels, paperHeightInPixels,
        paperHeightInCm, mapScale);
  }

  public static Task tesselateThickLine(String name,
      GLBezierShadingComplex complex, Point2d[] polyline, double lineWidth,
      double transitionSize, Colorizer colorizer, int paperWidthInPixels,
      int paperHeightInPixels, double paperHeightInCm, double mapScale)
      throws FunctionEvaluationException {

    if (polyline.length < 2) {
      System.err.println("line tesselation does not handle " + polyline.length
          + " points counts");
      return null;
    }
    return new BezierTesselatorTask(name, polyline, complex, lineWidth,
        transitionSize, colorizer, paperWidthInPixels, paperHeightInPixels,
        paperHeightInCm, mapScale);
  }

  private static class BezierTesselatorTask extends AbstractTask {
    private final Point2d[] polyline;
    private final boolean polyline_closed;
    private final GLBezierShadingComplex complex;
    private final double lineWidth;
    private final double transitionSize;
    private final Colorizer colorizer;
    public double paperWidthInWorldCoordinates = 1.;
    public double paperHeightInWorldCoordinates = 1.;

    private BezierTesselatorTask(String name, Point2d[] polyline,
        GLBezierShadingComplex complex, double lineWidth,
        double transitionSize, Colorizer c, int paperWidthInPixels,
        int paperHeightInPixels, double paperHeightInCm, double mapScale) {
      super(name + "-tesselation");

      this.polyline_closed = polyline[0].x == polyline[polyline.length - 1].x
          && polyline[0].y == polyline[polyline.length - 1].y;
      // Add an artificial segment if the line is closed to create the final
      // bezier turn.
      if (this.polyline_closed) {
        this.polyline = Arrays.copyOf(polyline, polyline.length + 1);
        this.polyline[polyline.length] = this.polyline[1];
      } else {
        this.polyline = polyline;
      }
      this.complex = complex;
      this.lineWidth = lineWidth;
      this.transitionSize = transitionSize;
      this.colorizer = c;

      double psf = paperHeightInCm * mapScale / (100. * paperHeightInPixels);
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
        int len = edges.length * 2;
        Point2d[] bottombounds = new Point2d[len];
        Point2d[] topbounds = new Point2d[len];
        this.computeSegmentsBounds(edges, normals, bottombounds, topbounds);
        boolean tessellation_success = this.tessellateSegments(edges, normals,
            bottombounds, topbounds, uParams, uMax);
        if (!tessellation_success) {
          return;
        }
        this.setState(TaskState.FINALIZING);
        this.colorizer.finalizeColorization();
        this.setState(TaskState.FINISHED);
      } catch (Exception e) {
        e.printStackTrace();
        this.setError(e);
        this.setState(TaskState.ERROR);
      }
    }

    /**
     * Compute the adjusted straight segments coordinates for each of the
     * linestring segments
     * @param edges Linestring edges as vectors
     * @param normals The edges normals
     * @param bottombounds Store the bottom coordinates created for each segment
     * @param topbounds Store the top coordinates created for each segment
     * @return true if the all segments were correctly created.
     */
    private void computeSegmentsBounds(Point2d[] edges, Point2d[] normals,
        Point2d[] bottombounds, Point2d[] topbounds) {
      boolean[] bottombounds_locks = new boolean[bottombounds.length];
      boolean[] topbounds_locks = new boolean[bottombounds.length];
      int nbuilds = edges.length;
      for (int i = 0; i < nbuilds; i++) {
        double shift;
        Point2d p1 = this.polyline[i];
        Point2d p2 = this.polyline[i + 1];
        // compute the left and the right (eventually shifted) bounds
        if (!bottombounds_locks[i * 2] && !topbounds_locks[i * 2]) {
          shift = (i == 0 && !this.polyline_closed) ? 0. : this.transitionSize;
          Point2d[] bounds = computeShiftedBounds(p1, normals[i], edges[i],
              shift, 1);
          bottombounds[i * 2] = bounds[0];
          topbounds[i * 2] = bounds[1];
        }
        shift = (i < nbuilds - 1 || this.polyline_closed) ? this.transitionSize
            : 0.;
        Point2d[] bounds = computeShiftedBounds(p2, normals[i], edges[i],
            shift, -1);
        bottombounds[i * 2 + 1] = bounds[0];
        topbounds[i * 2 + 1] = bounds[1];
        // pre-compute the left bound of the next segment and check for
        // intersections. If so, adjust the bounds to avoid intersection.
        if (i < nbuilds - 1) {
          Point2d[] nextbounds = computeShiftedBounds(p2, normals[i + 1],
              edges[i + 1], this.transitionSize, 1);
          bottombounds[i * 2 + 2] = nextbounds[0];
          topbounds[i * 2 + 2] = nextbounds[1];
          // Artificially extends the next segment bounds to compute the segment
          // intersection.
          Point2d xtendedbottom = new Point2d(nextbounds[0].x + edges[i + 1].x,
              nextbounds[0].y + edges[i + 1].y);
          Point2d xtendedtop = new Point2d(nextbounds[1].x + edges[i + 1].x,
              nextbounds[1].y + edges[i + 1].y);
          Point2d bottomintersect = new Point2d(-1, -1);
          Point2d topintersect = new Point2d(-1, -1);
          boolean intersect_bottom = segmentintersection(bottombounds[i * 2],
              bottombounds[i * 2 + 1], bottombounds[i * 2 + 2], xtendedbottom,
              bottomintersect);
          boolean intersect_top = segmentintersection(topbounds[i * 2],
              topbounds[i * 2 + 1], topbounds[i * 2 + 2], xtendedtop,
              topintersect);
          if (intersect_bottom) {
            bottombounds[i * 2 + 1] = bottomintersect;
            bottombounds[i * 2 + 2] = bottomintersect;
            topbounds[i * 2 + 1].set(bottomintersect.x + normals[i].x
                * this.lineWidth, bottomintersect.y + normals[i].y
                * this.lineWidth);
            topbounds[i * 2 + 2].set(bottomintersect.x + normals[i + 1].x
                * this.lineWidth, bottomintersect.y + normals[i + 1].y
                * this.lineWidth);
            topbounds_locks[i * 2 + 2] = bottombounds_locks[i * 2 + 2] = true;
          } else if (intersect_top) {
            topbounds[i * 2 + 1] = topintersect;
            topbounds[i * 2 + 2] = topintersect;
            bottombounds[i * 2 + 1].set(topintersect.x - normals[i].x
                * this.lineWidth, topintersect.y - normals[i].y
                * this.lineWidth);
            bottombounds[i * 2 + 2].set(topintersect.x - normals[i + 1].x
                * this.lineWidth, topintersect.y - normals[i + 1].y
                * this.lineWidth);
            topbounds_locks[i * 2 + 2] = bottombounds_locks[i * 2 + 2] = true;
          }
        }
        topbounds_locks[i * 2 + 1] = bottombounds_locks[i * 2 + 1] = true;

        if (this.isStopRequested()) {
          this.setState(TaskState.STOPPED);
          return;
        }
      }
      // In a closed polyline, we don't want to draw the last (artificial)
      // segment so we put its width to 0.
      if (this.polyline_closed) {
        bottombounds[bottombounds.length - 1] = bottombounds[bottombounds.length - 2];
        topbounds[bottombounds.length - 1] = topbounds[bottombounds.length - 2];
      }
    }

    /**
     * Build the triangles for each segment of the linestring. Uses the
     * pre-calculated bounds.
     * @param edges
     * @param normals
     * @param bottombounds
     * @param topbounds
     */
    private boolean tessellateSegments(Point2d[] edges, Point2d[] normals,
        Point2d[] bottombounds, Point2d[] topbounds, float[] uParams, float uMax) {
      float u0 = uParams[0];
      int ntessellate = edges.length;
      for (int i = 0; i < ntessellate; i++) {
        if (this.isStopRequested()) {
          this.setState(TaskState.STOPPED);
          return false;
        }
        // Build the triangles for the straight part of the rendered segment.
        GLMesh mesh = this.complex.addGLMesh(GL11.GL_TRIANGLES);
        Point2d p0 = new Point2d(
            (bottombounds[i * 2].x + topbounds[i * 2].x) / 2,
            (bottombounds[i * 2].y + topbounds[i * 2].y) / 2);
        Point2d p1 = new Point2d(
            (bottombounds[i * 2 + 1].x + topbounds[i * 2 + 1].x) / 2,
            (bottombounds[i * 2 + 1].y + topbounds[i * 2 + 1].y) / 2);
        Point2d v1 = new Point2d(p1.x - p0.x, p1.y - p0.y);
        float u1 = u0 + (float) VectorUtil.length(v1);
        if (u1 - u0 > 0) {
          addPolygon(mesh, p0, p1, bottombounds[i * 2],
              bottombounds[i * 2 + 1], topbounds[i * 2 + 1], topbounds[i * 2],
              u0, u1, uMax);
        }
        // Build rounded turns with bezier curves.
        if (i + 1 < ntessellate) {
          Point2d nextpoint = this.polyline[i + 1];
          u0 = u1;
          Point2d outputlow = new Point2d();
          Point2d outputhigh = new Point2d();
          p0.set(p1.x, p1.y);
          p1.set((bottombounds[i * 2 + 2].x + topbounds[i * 2 + 2].x) / 2,
              (bottombounds[i * 2 + 2].y + topbounds[i * 2 + 2].y) / 2);
          v1.set(nextpoint.x - p0.x, nextpoint.y - p0.y);
          u1 += VectorUtil.length(v1);
          v1.set(p1.x - nextpoint.x, p1.y - nextpoint.y);
          u1 += VectorUtil.length(v1);
          this.createBezierTurn(mesh, p0, p1, edges, normals, u0, u1, uMax, i,
              i + 1, outputlow, outputhigh);
          // TODO : adjust the bounds with the Bezier curves positions?
          // bottombounds[i * 2 + 2] = bottombounds[i * 2 + 3] = outputlow;
          // topbounds[i * 2 + 2] = topbounds[i * 2 + 3] =outputhigh;
          u0 = u1;
        }
      }
      return true;
    }

    /**
     * Shift a given point on a segment of a distance and compute the
     * coordinates of the bounds at this point. If the distance is greater than
     * the edge length the method shift the point of half the edge length.
     * @param p2shift The point to shift.
     * @param normal Normal vector to the edge containing {@code p2shift}.
     * @param edge The edge containing {@code p2shift}
     * @param shift the shifting distance from the beginning or the end of the
     *          edge.
     * @param direction 1 to shift {@code p2shift} from the beginning of the
     *          edge and -1 to shift from the end.
     * @return
     */
    private Point2d[] computeShiftedBounds(Point2d p2shift, Point2d normal,
        Point2d edge, double shift, double direction) {
      double edgelen = VectorUtil.length(edge);
      double leftshift = shift / edgelen;
      if (shift > 0.5 * edgelen) {
        leftshift = 0.5;
      }
      Point2d[] bound = new Point2d[2];
      // Bottom point
      double x0 = p2shift.x + edge.x * direction * leftshift;
      double y0 = p2shift.y + edge.y * direction * leftshift;
      bound[0] = new Point2d(x0 - normal.x * this.lineWidth / 2, y0 - normal.y
          * this.lineWidth / 2);
      // Top point
      bound[1] = new Point2d(x0 + normal.x * this.lineWidth / 2, y0 + normal.y
          * this.lineWidth / 2);
      return bound;
    }

    private Point2d intersection(Point2d p0, Point2d v0, Point2d p1, Point2d v1) {
      Point2d inter = new Point2d();
      VectorUtil.lineIntersection(inter, p0, v0, p1, v1);
      return inter;
    }

    private boolean segmentintersection(Point2d p1, Point2d p2, Point2d p3,
        Point2d p4, Point2d intersection) {
      double Sx;
      double Sy;
      if (p1.x == p2.x) {
        if (p3.x == p4.x)
          return false;
        else {
          double p34 = (p3.y - p4.y) / (p3.x - p4.y);
          Sx = p1.x;
          Sy = p34 * (p1.x - p3.x) + p3.y;
        }
      } else {
        if (p3.x == p4.x) {
          double p12 = (p1.y - p2.y) / (p1.x - p2.y);
          Sx = p3.x;
          Sy = p12 * (p3.x - p1.x) + p1.y;
        } else {
          double p34 = (p3.y - p4.y) / (p3.x - p4.x);
          double p12 = (p1.y - p2.y) / (p1.x - p2.x);
          double o34 = p3.y - p34 * p3.x;
          double o12 = p1.y - p12 * p1.x;
          Sx = (o12 - o34) / (p34 - p12);
          Sy = p34 * Sx + o34;
        }
      }
      if ((Sx < p1.x && Sx < p2.x) | (Sx > p1.x && Sx > p2.x)
          | (Sx < p3.x && Sx < p4.x) | (Sx > p3.x && Sx > p4.x)
          || (Sy < p1.y && Sy < p2.y) | (Sy > p1.y && Sy > p2.y)
          | (Sy < p3.y && Sy < p4.y) | (Sy > p3.y && Sy > p4.y))
        return false;
      intersection.set(Sx, Sy);
      return true;
    }

    private Point2d normal(Point2d p0, Point2d p1) {
      Point2d v = new Point2d(p1.x - p0.x, p1.y - p0.y);
      VectorUtil.normalize(v, v);
      return v;
    }

    private void createBezierTurn(GLMesh mesh, Point2d p0, Point2d p2,
        Point2d[] edges, Point2d[] normals, double u0, double u2, float uMax,
        int index0, int index2, Point2d outputLow, Point2d outputHigh) {
      Point2d p1 = new Point2d();
      VectorUtil.lineIntersection(p1, p0, edges[index0], p2, edges[index2]);
      Point2d n0 = this.normal(p0, p1);
      Point2d n2 = this.normal(p1, p2);
      double angle = n0.x * n2.y - n0.y * n2.x;
      int sign = angle > 0 ? 1 : -1;
      Point2d p0low = new Point2d(p0.x + sign * n0.y * this.lineWidth / 2, p0.y
          - sign * n0.x * this.lineWidth / 2);
      Point2d p0high = new Point2d(p0.x - sign * n0.y * this.lineWidth / 2,
          p0.y + sign * n0.x * this.lineWidth / 2);
      Point2d p2low = new Point2d(p2.x + sign * n2.y * this.lineWidth / 2, p2.y
          - sign * n2.x * this.lineWidth / 2);
      Point2d p2high = new Point2d(p2.x - sign * n2.y * this.lineWidth / 2,
          p2.y + sign * n2.x * this.lineWidth / 2);
      Point2d p1low = this.intersection(p0low, n0, p2low, n2);
      Point2d p1high = this.intersection(p0high, n0, p2high, n2);
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

      Point2d centerHigh = new Point2d(px - sign * tangent.y * this.lineWidth
          / 2, py + sign * tangent.x * this.lineWidth / 2);
      Point2d centerLow = new Point2d(px + sign * tangent.y * this.lineWidth
          / 2, py - sign * tangent.x * this.lineWidth / 2);
      VectorUtil.lineIntersection(B, centerLow, tangent, p0low, n0);
      VectorUtil.lineIntersection(C, centerLow, tangent, p2low, n2);

      VectorUtil.copy(A, p0low);
      VectorUtil.copy(D, p2low);
      VectorUtil.copy(E, p2high);
      F.x = centerHigh.x;
      F.y = centerHigh.y;
      VectorUtil.copy(G, p0high);
      Point2d I = new Point2d();
      boolean selfIntersect = VectorUtil.segmentIntersection(I, A, G, D, E) != null;
      Point2d uv = new Point2d(u0, u2);
      Color col = this.colorizer.getColor(uv.x, uv.y);
      Point2d paperUV = this.computePaperUV(A, this.complex.getMinX(),
          this.complex.getMinY());

      GLBezierShadingVertex vertexA = new GLBezierShadingVertex(A, uv, col,
          (float) this.lineWidth, uMax, p0, p1, p2, n0, n2, paperUV);
      int aIndex = this.complex.addVertex(vertexA);
      // uv = new Point2d((u0 + (u2 - u0) / 3), 0);
      col = this.colorizer.getColor(uv.x, uv.y);
      paperUV = this.computePaperUV(B, this.complex.getMinX(),
          this.complex.getMinY());
      GLBezierShadingVertex vertexB = new GLBezierShadingVertex(B, uv, col,
          (float) this.lineWidth, uMax, p0, p1, p2, n0, n2, paperUV);
      int bIndex = this.complex.addVertex(vertexB);

      // uv = new Point2d((u0 + 2 * (u2 - u0) / 3), 0);
      col = this.colorizer.getColor(uv.x, uv.y);
      paperUV = this.computePaperUV(C, this.complex.getMinX(),
          this.complex.getMinY());

      GLBezierShadingVertex vertexC = new GLBezierShadingVertex(C, uv, col,
          (float) this.lineWidth, uMax, p0, p1, p2, n0, n2, paperUV);
      int cIndex = this.complex.addVertex(vertexC);

      // uv = new Point2d(u2, 0);
      col = this.colorizer.getColor(uv.x, uv.y);
      paperUV = this.computePaperUV(D, this.complex.getMinX(),
          this.complex.getMinY());
      GLBezierShadingVertex vertexD = new GLBezierShadingVertex(D, uv, col,
          (float) this.lineWidth, uMax, p0, p1, p2, n0, n2, paperUV);
      int dIndex = this.complex.addVertex(vertexD);

      selfIntersect = false;

      if (selfIntersect) {
        // self intersection
        // uv = new Point2d(u2, 1);
        col = this.colorizer.getColor(uv.x, uv.y);
        paperUV = this.computePaperUV(I, this.complex.getMinX(),
            this.complex.getMinY());
        GLBezierShadingVertex vertexI = new GLBezierShadingVertex(I, uv, col,
            (float) this.lineWidth, uMax, p0, p1, p2, n0, n2, paperUV);
        int iIndex = this.complex.addVertex(vertexI);

        mesh.addIndices(aIndex, bIndex, iIndex);
        mesh.addIndices(bIndex, cIndex, iIndex);
        mesh.addIndices(cIndex, dIndex, iIndex);
        if (sign < 0) {
          VectorUtil.copy(outputLow, D);
          VectorUtil.copy(outputHigh, I);
        } else {
          VectorUtil.copy(outputLow, I);
          VectorUtil.copy(outputHigh, D);
        }
      } else {
        // uv = new Point2d(u2, 1);
        col = this.colorizer.getColor(uv.x, uv.y);
        paperUV = this.computePaperUV(E, this.complex.getMinX(),
            this.complex.getMinY());

        GLBezierShadingVertex vertexE = new GLBezierShadingVertex(E, uv, col,
            (float) this.lineWidth, uMax, p0, p1, p2, n0, n2, paperUV);
        int eIndex = this.complex.addVertex(vertexE);

        // uv = new Point2d((u0 + u2) / 2., 1);
        col = this.colorizer.getColor(uv.x, uv.y);
        paperUV = this.computePaperUV(F, this.complex.getMinX(),
            this.complex.getMinY());
        GLBezierShadingVertex vertexF = new GLBezierShadingVertex(F, uv, col,
            (float) this.lineWidth, uMax, p0, p1, p2, n0, n2, paperUV);
        int fIndex = this.complex.addVertex(vertexF);

        // uv = new Point2d(u0, 1);
        col = this.colorizer.getColor(uv.x, uv.y);
        paperUV = this.computePaperUV(G, this.complex.getMinX(),
            this.complex.getMinY());
        GLBezierShadingVertex vertexG = new GLBezierShadingVertex(G, uv, col,
            (float) this.lineWidth, uMax, p0, p1, p2, n0, n2, paperUV);
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
    private void addPolygon(GLMesh mesh, Point2d p0, Point2d p1, Point2d p0low,
        Point2d p1low, Point2d p1high, Point2d p0high, double u0, double u1,
        float uMax) {
      Point2d bezier0 = new Point2d(p0.x, p0.y);
      Point2d bezier1 = new Point2d((p0.x + p1.x) / 2, (p0.y + p1.y) / 2);
      Point2d bezier2 = new Point2d(p1.x, p1.y);
      Point2d n0 = new Point2d(p0low.x - p0.x, p0low.y - p0.y);
      Point2d n2 = new Point2d(p1low.x - p1.x, p1low.y - p1.y);
      VectorUtil.normalize(n0, n0);
      VectorUtil.normalize(n2, n2);
      Point2d us = new Point2d(u0, u1);
      Color c = this.colorizer.getColor(us.x, 0);
      Point2d paperUV = this.computePaperUV(p0low, this.complex.getMinX(),
          this.complex.getMinY());
      GLBezierShadingVertex vertex0low = new GLBezierShadingVertex(p0low, us,
          c, (float) this.lineWidth, uMax, bezier0, bezier1, bezier2, n0, n2,
          paperUV);
      c = this.colorizer.getColor(us.x, 1);
      paperUV = this.computePaperUV(p0high, this.complex.getMinX(),
          this.complex.getMinY());

      GLBezierShadingVertex vertex0high = new GLBezierShadingVertex(p0high, us,
          c, (float) this.lineWidth, uMax, bezier0, bezier1, bezier2, n0, n2,
          paperUV);
      c = this.colorizer.getColor(us.y, 1);
      paperUV = this.computePaperUV(p1low, this.complex.getMinX(),
          this.complex.getMinY());
      GLBezierShadingVertex vertex1low = new GLBezierShadingVertex(p1low, us,
          c, (float) this.lineWidth, uMax, bezier0, bezier1, bezier2, n0, n2,
          paperUV);
      c = this.colorizer.getColor(us.y, 0);
      paperUV = this.computePaperUV(p1high, this.complex.getMinX(),
          this.complex.getMinY());

      GLBezierShadingVertex vertex1high = new GLBezierShadingVertex(p1high, us,
          c, (float) this.lineWidth, uMax, bezier0, bezier1, bezier2, n0, n2,
          paperUV);
      int p0lowIndex = this.complex.addVertex(vertex0low);
      int p0highIndex = this.complex.addVertex(vertex0high);
      int p1lowIndex = this.complex.addVertex(vertex1low);
      int p1highIndex = this.complex.addVertex(vertex1high);
      mesh.addIndices(p0lowIndex, p1lowIndex, p1highIndex);
      mesh.addIndices(p0lowIndex, p1highIndex, p0highIndex);
    }

    /**
     * Compute texture coordinates for the given paper texture
     * 
     * @param p point in world coordinates
     * @return texture coordinates in paper coordinates
     */
    private Point2d computePaperUV(Point2d p, double minX, double minY) {
      // TODO: may be we can use minX, minY to computed reduced texture //
      // coordinates... (p.xy can be large...)
      double x = ((p.x + minX % this.paperWidthInWorldCoordinates))
          / this.paperWidthInWorldCoordinates;
      double y = ((p.y + minY % this.paperHeightInWorldCoordinates))
          / this.paperHeightInWorldCoordinates;
      return new Point2d(x, y);
    }
  }
}
