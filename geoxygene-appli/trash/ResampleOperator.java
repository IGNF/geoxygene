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

import fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive;
import fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitiveUtil;
import fr.ign.cogit.geoxygene.appli.render.primitive.MultiDrawingPrimitive;
import fr.ign.cogit.geoxygene.appli.render.primitive.ParameterizedPolyline;
import fr.ign.cogit.geoxygene.appli.render.primitive.ParameterizedSegment;

/**
 * @author JeT
 * This operator takes a list of Polyline as input. It adds points to polylines every meanDistance
 * parameter value. Aspect keeps unchanged but 
 * 
 * input line:      *-----------------------------------------------*
 * output line:     *---*---*---*---*---*---*---*---*---*---*---*---*
 * 
 * Parameters : 
 * -SamplingDistance distance between two sample points (real size will be adjusted as close as possible to this value)
 */
public class ResampleOperator extends AbstractDrawingPrimitiveOperator {

  public static final double SamplingDistanceParameterDefaultValue = 1.;
  private double samplingDistance = SamplingDistanceParameterDefaultValue;
  private boolean keepInitialPoints = false;
  private List<ParameterizedPolyline> lines = new ArrayList<ParameterizedPolyline>(); // poly lines to resample

  /**
   * Default constructor
   */
  public ResampleOperator() {
    this(SamplingDistanceParameterDefaultValue, true);
  }

  /**
   * Constructor
   * @param parameters
   */
  public ResampleOperator(final double samplingParameterDistance, final boolean keepInitialPoints) {
    super();
    this.samplingDistance = samplingParameterDistance;
    this.keepInitialPoints = keepInitialPoints;
  }

  /**
   * @return the samplingDistance
   */
  public double getSamplingDistance() {
    return this.samplingDistance;
  }

  /**
   * @return the keepInitialPoints
   */
  public boolean isKeepInitialPoints() {
    return this.keepInitialPoints;
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
    }
    this.lines.add((ParameterizedPolyline) input);
  }

  public List<ParameterizedPolyline> getLines() {
    return this.lines;
  }

  @Override
  public DrawingPrimitive apply() {
    MultiDrawingPrimitive relines = new MultiDrawingPrimitive(); // resampled poly lines
    for (ParameterizedPolyline line : this.lines) {
      if (this.isKeepInitialPoints()) {
        relines.addPrimitive(constrainedLinearResample(line, this.samplingDistance));
      } else {
        relines.addPrimitive(freeLinearResample(line, this.samplingDistance));
      }
    }
    return relines;
  }

  /**
   * recompute a new polyline by adding intermediate points to generate a quite equal parameter distance,
   * all initial points are kept, some points are added
   * @param line line to reparameterize
   * @param samplingParameterDistance parameter distance between two points 
   * @return a newly re-parameterized polyline
   */
  private static ParameterizedPolyline constrainedLinearResample(final ParameterizedPolyline line, final double samplingParameterDistance) {
    if (line.getPointCount() <= 1) {
      return line;
    }
    ParameterizedPolyline reline = new ParameterizedPolyline();

    int lastIndex = line.getPointCount() - 1;
    for (int n = 0; n < lastIndex; n++) {
      Point2d p1 = line.getPoint(n);
      Point2d p2 = line.getPoint(n + 1);
      double t1 = line.getParameter(n);
      double t2 = line.getParameter(n + 1);
      int newNumberOfPoints = (int) Math.round(Math.abs(t2 - t1) / samplingParameterDistance);
      newNumberOfPoints = newNumberOfPoints < 1 ? 1 : newNumberOfPoints; // min is 1
      double tDiff = (t2 - t1) / newNumberOfPoints;
      double xDiff = (p2.x - p1.x) / newNumberOfPoints;
      double yDiff = (p2.y - p1.y) / newNumberOfPoints;
      for (int nNewPoint = 0; nNewPoint < newNumberOfPoints; nNewPoint++) {
        reline.addPoint(p1.x + xDiff * nNewPoint, p1.y + yDiff * nNewPoint, t1 + tDiff * nNewPoint);
      }

    }
    reline.addPoint(line.getPoint(lastIndex), line.getParameter(lastIndex));
    return reline;
  }

  /**
   * recompute a new polyline by adding intermediate points to generate a quite equal parameter distance,
   * all initial points are deleted but the first and last one, some points are added
   * @return a newly re-parameterized polyline
   */
  private static DrawingPrimitive freeLinearResample(final ParameterizedPolyline line, final double samplingParameterDistance) {
    ParameterizedPolyline reline1 = new ParameterizedPolyline();

    ParameterizedSegment[] segments = DrawingPrimitiveUtil.segmentize(line);
    if (segments.length == 0) {
      throw new IllegalStateException("there cannot be zero segments in a line... #line points=" + line.getPointCount() + " #segments="
          + segments.length);
    }
    double length = segments[segments.length - 1].getEndDistance(); // length = distance to the last end point

    int nb = (int) Math.round(length / samplingParameterDistance);
    if (nb < 1) {
      nb = 1;
    }
    double dInc = length / nb;

    double d = 0;
    int currentSegmentIndex = 0;
    ParameterizedSegment currentSegment = segments[currentSegmentIndex];
    for (int n = 0; n <= nb; n++) {

      // compute point
      double segmentInterpolationFactor = (d - currentSegment.getStartDistance()) / currentSegment.getLength(); // 0..1 factor in the current segment
      //      if (segmentInterpolationFactor < 0 || segmentInterpolationFactor > 1) {
      //        logger.error("segment interpolation factor = " + segmentInterpolationFactor + " d = " + d + " should be >= "
      //            + currentSegment.getStartDistance() + " && <= " + currentSegment.getEndDistance());
      //      }
      reline1.addPoint(currentSegment.getInterpolatedPoint(segmentInterpolationFactor), currentSegment
          .getInterpolatedParameter(segmentInterpolationFactor));
      // inc distance and change segment if needed
      d += dInc;
      if (d > currentSegment.getEndDistance()) {
        while (currentSegmentIndex < segments.length - 1 && d > segments[currentSegmentIndex].getEndDistance()) {
          currentSegmentIndex++;
        }
        currentSegment = segments[currentSegmentIndex];
      }
    }

    return reline1;
  }

  @Override
  public void removeAllInputs() {
    this.lines.clear();
  }

}
