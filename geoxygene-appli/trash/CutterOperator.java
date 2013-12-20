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
package fr.ign.cogit.geoxygene.appli.render.operator;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive;
import fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitiveUtil;
import fr.ign.cogit.geoxygene.appli.render.primitive.MultiDrawingPrimitive;
import fr.ign.cogit.geoxygene.appli.render.primitive.ParameterizedPolyline;
import fr.ign.cogit.geoxygene.appli.render.primitive.ParameterizedSegment;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.function.FunctionEvaluationException;

/**
 * @author JeT
 * This operator takes a list of Polyline as input. It cuts lines into smaller lines (every meanDistance) and add holes.
 * Aspect keeps unchanged if no holes are inserted.
 * 
 * input line:      *------------------------------------------------*
 * output line:     *---  ---  ---  ---  ---  ---  ---  ---  ---  ---*
 * 
 * Parameters : 
 * -StrokeSizeParameterId size of every strokes
 * -HoleSizeParameterId size of every holes between strokes (real hole size will be adjusted as close as possible to this value)
 */
public class CutterOperator extends AbstractDrawingPrimitiveOperator {

  private static Logger logger = Logger.getLogger(CutterOperator.class.getName()); // logger
  public static final double StrokeSizeParameterDefaultValue = 1.;
  public static final double HoleSizeParameterDefaultValue = 1.;
  private double strokeSize = StrokeSizeParameterDefaultValue;
  public double holeSize = HoleSizeParameterDefaultValue;
  private List<ParameterizedPolyline> lines = new ArrayList<ParameterizedPolyline>(); // poly lines to resample

  /**
   * Default constructor
   */
  public CutterOperator() {
    this(StrokeSizeParameterDefaultValue, HoleSizeParameterDefaultValue);
  }

  /**
   * Constructor
   * @param parameters
   */
  public CutterOperator(final double strokeSizeParameterValue, final double holeSizeParameterValue) {
    super();
    this.strokeSize = strokeSizeParameterValue;
    this.holeSize = holeSizeParameterValue;
  }

  @Override
  public void addInput(final DrawingPrimitive input) throws InvalidOperatorInputException {
    // check input type
    if (!input.isLeaf()) {
      for (DrawingPrimitive primitive : input.getPrimitives()) {
        this.addInput(primitive);
      }
    } else {
      if (!(input instanceof ParameterizedPolyline)) {
        throw new InvalidOperatorInputException(this.getClass().getSimpleName() + " can only handle ParameterizedPolyline drawing primitive, not "
            + input.getClass().getSimpleName());
      }
      this.lines.add((ParameterizedPolyline) input);
    }
  }

  @Override
  public void removeAllInputs() {
    this.lines.clear();
  }

  public List<ParameterizedPolyline> getLines() {
    return this.lines;
  }

  @Override
  public DrawingPrimitive apply() {

    MultiDrawingPrimitive cuttedLines = new MultiDrawingPrimitive(); // resampled poly lines
    for (ParameterizedPolyline line : this.lines) {
      cuttedLines.addPrimitives(cut(line, this.strokeSize, this.holeSize));
    }
    return cuttedLines;
  }

  /**
   * Cut One polyline
   */
  public static List<ParameterizedPolyline> cut(final ParameterizedPolyline line, final double strokeSize, final double holeSize) {
    List<ParameterizedPolyline> cuttedLine = new ArrayList<ParameterizedPolyline>();

    for (int n = 0; n < line.getPointCount() - 1; n++) {
      Point2d p1 = line.getPoint(n);
      Point2d p2 = line.getPoint(n + 1);
      double t1 = line.getParameter(n);
      double t2 = line.getParameter(n + 1);
      cuttedLine.addAll(cut(p1, p2, t1, t2, strokeSize, holeSize));
    }

    return cuttedLine;
  }

  /**
   * Cut one segment of a polyline
   *        *-------------------------------------------------------------------------------------*
   *         \p1                                                                                    \p2
   *
   *        *-----------------*                *-----------------*                *---------------*
   *         \p1               \p1p                                                \p2p            \p2
   *
   * @param p1 first point
   * @param p2 second point
   * @param t1 first parameter
   * @param t2 second parameter
   * @return a set of polylines
   */
  private static List<ParameterizedPolyline> cut(final Point2d p1, final Point2d p2, final double t1, final double t2, final double strokeSize,
      final double holeSize) {

    List<ParameterizedPolyline> cutSegments = new ArrayList<ParameterizedPolyline>();
    double lSegment = p1.distance(p2);
    if (lSegment < 1E-6) {
      return cutSegments;
    }
    ///                            lSegment - strokeSize : distance(p1,p2p)
    int nbHole = (int) Math.floor((lSegment - strokeSize) / (strokeSize + holeSize)); // number of holes ( #holes = #stroke - 1 )
    if (nbHole <= 0) {
      // no enough space to add a hole. return the complete segment
      ParameterizedPolyline line = new ParameterizedPolyline();
      line.addPoint(p1, t1);
      line.addPoint(p2, t2);
      cutSegments.add(line);
    } else {
      double xLineNormal = (p2.x - p1.x) / lSegment;
      double yLineNormal = (p2.y - p1.y) / lSegment;
      double tLineNormal = (t2 - t1) / lSegment;
      double adjustedHoleSize = (lSegment - (1 + nbHole) * strokeSize) / nbHole;
      //      System.err.println("nbHoles = " + nbHole + " l = " + lSegment + " hole size = " + holeSize + " adjusted hole size = " + adjustedHoleSize);
      double strokeHoleVectorX = xLineNormal * (strokeSize + adjustedHoleSize);
      double strokeHoleVectorY = yLineNormal * (strokeSize + adjustedHoleSize);
      double strokeVectorX = xLineNormal * strokeSize;
      double strokeVectorY = yLineNormal * strokeSize;
      double strokeHoleDiffT = tLineNormal * (strokeSize + adjustedHoleSize);
      double strokeDiffT = tLineNormal * strokeSize;
      //      DecimalFormat dc = new DecimalFormat("#.##");
      // cut the segment (keep the last stroke out of the process)
      for (int nHole = 0; nHole < nbHole; nHole++) {
        ParameterizedPolyline stroke = new ParameterizedPolyline();
        stroke.addPoint(new Point2d(p1.x + strokeHoleVectorX * nHole, p1.y + strokeHoleVectorY * nHole), t1 + nHole * strokeHoleDiffT);
        stroke.addPoint(new Point2d(p1.x + strokeHoleVectorX * nHole + strokeVectorX, p1.y + strokeHoleVectorY * nHole + strokeVectorY), t1
            + strokeHoleDiffT * nHole + strokeDiffT);
        cutSegments.add(stroke);
        //        System.err.print("  " + dc.format(p1.x + strokeHoleVectorX * nHole) + "x" + dc.format(p1.y + strokeHoleVectorY * nHole));
        //        System.err.print("  " + dc.format(p1.x + strokeHoleVectorX * nHole + strokeVectorX) + "x"
        //            + dc.format(p1.y + strokeHoleVectorY * nHole + strokeVectorY));
      }
      ParameterizedPolyline stroke = new ParameterizedPolyline();
      stroke.addPoint(new Point2d(p1.x + strokeHoleVectorX * nbHole, p1.y + strokeHoleVectorY * nbHole), t1 + nbHole * strokeHoleDiffT);
      stroke.addPoint(new Point2d(p2), t2);
      //      System.err.print("  " + dc.format(p1.x + strokeHoleVectorX * nbHole) + "x" + dc.format(p1.y + strokeHoleVectorY * nbHole));
      //      System.err.print("  " + dc.format(p2.x) + "x" + dc.format(p2.y));
      //      System.err.println();
      cutSegments.add(stroke);

      // add the last stroke

      cutSegments.add(stroke);
    }
    return cutSegments;
  }

  /**
   * @param line line to reparameterize
   * @param samplingRate mean size of samples draw on screen (in pixels)
   * @param shift distance from the initial line (0 is on the line)
   * @param width distance between the two points around the shifted point
   * @return a newly re-parameterized polyline
   */
  private static DrawingPrimitive resample(final ParameterizedPolyline line, final double samplingRate, final Function1D shiftFunction,
      final Function1D widthFunction) {
    ParameterizedPolyline reline1 = new ParameterizedPolyline();
    ParameterizedPolyline reline2 = new ParameterizedPolyline();

    ParameterizedSegment[] segments = DrawingPrimitiveUtil.segmentize(line);
    if (segments.length == 0) {
      throw new IllegalStateException("there cannot be zero segments in a line... #line points=" + line.getPointCount() + " #segments="
          + segments.length);
    }
    double length = segments[segments.length - 1].getEndDistance(); // length = distance to the last end point

    int nb = (int) Math.round(length / samplingRate);
    if (nb < 1) {
      nb = 1;
    }
    double dInc = length / nb;

    double d = 0;
    int currentSegmentIndex = 0;
    ParameterizedSegment currentSegment = segments[currentSegmentIndex];
    Vector2d normal = null, prevNormal = null;
    for (int n = 0; n <= nb; n++) {
      // compute line & segment interpolations factors
      double lineInterpolationFactor = d / length; // distance rescaled between 0 and 1 from the beginning of the line to it's end
      //      if (lineInterpolationFactor < 0 || lineInterpolationFactor > 1) {
      //        logger.error("line interpolation factor = " + lineInterpolationFactor + " d = " + d + " should be >= " + 0 + " && <= " + length + " n = " + n
      //            + " on " + nb);
      //      }

      // compute point
      double segmentInterpolationFactor = (d - currentSegment.getStartDistance()) / currentSegment.getLength(); // 0..1 factor in the current segment
      //      if (segmentInterpolationFactor < 0 || segmentInterpolationFactor > 1) {
      //        logger.error("segment interpolation factor = " + segmentInterpolationFactor + " d = " + d + " should be >= "
      //            + currentSegment.getStartDistance() + " && <= " + currentSegment.getEndDistance());
      //      }
      Point2d interpolatedPoint = currentSegment.getInterpolatedPoint(segmentInterpolationFactor);

      // compute normal
      prevNormal = normal; // store previous normal
      normal = computeNormal(currentSegment, prevNormal);

      try {
        double width = widthFunction.evaluate(lineInterpolationFactor);
        double shift = shiftFunction.evaluate(lineInterpolationFactor);
        Point2d p1 = new Point2d(interpolatedPoint.x + (shift + width) * normal.x, interpolatedPoint.y + (shift + width) * normal.y);
        Point2d p2 = new Point2d(interpolatedPoint.x + (shift - width) * normal.x, interpolatedPoint.y + (shift - width) * normal.y);
        reline1.addPoint(p1, currentSegment.getInterpolatedParameter(segmentInterpolationFactor));
        reline2.addPoint(p2, currentSegment.getInterpolatedParameter(segmentInterpolationFactor));
      } catch (FunctionEvaluationException e) {
        logger.error(e);
      }
      // inc distance and change segment if needed
      d += dInc;
      if (d > currentSegment.getEndDistance()) {
        while (currentSegmentIndex < segments.length - 1 && d > segments[currentSegmentIndex].getEndDistance()) {
          currentSegmentIndex++;
        }
        currentSegment = segments[currentSegmentIndex];
      }
    }

    MultiDrawingPrimitive primitive = new MultiDrawingPrimitive();
    if (reline1.getPointCount() > 1) {
      primitive.addPrimitive(reline1);
    }
    if (reline2.getPointCount() > 1) {
      primitive.addPrimitive(reline2);
    }
    return primitive;
  }

  /**
   * Compute the normal between two points. 
   * @param prev
   * @param current
   * @param next
   * @param prevNormal
   * @return
   */
  private static Vector2d computeNormal(final ParameterizedSegment segment, final Vector2d prevNormal) {
    return segment.getNormal();
  }

}
