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

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive;
import fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitiveUtil;
import fr.ign.cogit.geoxygene.appli.render.primitive.MultiDrawingPrimitive;
import fr.ign.cogit.geoxygene.appli.render.primitive.ParameterizedPolygon;
import fr.ign.cogit.geoxygene.appli.render.primitive.ParameterizedPolyline;
import fr.ign.cogit.geoxygene.appli.render.primitive.ParameterizedSegment;
import fr.ign.cogit.geoxygene.function.ConstantFunction;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.function.FunctionEvaluationException;

/**
 * @author JeT
 *         This operator takes a list of Polyline as input. It blows thin lines
 *         into thick ones
 * 
 *         input line: *--------------*--------------*-----------------*
 * 
 *         output lines: *--------------*--------------*-----------------*
 *         *--------------*--------------*-----------------*
 * 
 *         Parameters :
 *         -thickness: a function that varies depending on the parameterization
 *         -scaleFactor value multiplied to the thickness function
 */
public class InflateOperator extends AbstractDrawingPrimitiveOperator {

    private static Logger logger = Logger.getLogger(InflateOperator.class.getName());
    private Function1D shiftFunction = null;
    private Function1D widthFunction = null;
    private double samplingRate = 10.;
    private final List<ParameterizedPolyline> lines = new ArrayList<ParameterizedPolyline>(); // poly lines to inflate
    private Viewport viewport = null;

    /**
     * Default constructor
     */
    public InflateOperator() {
        this(new ConstantFunction(5.), 10.);
    }

    /**
     * Constructor
     * 
     * @param parameters
     */
    public InflateOperator(final Function1D widthFunction, final double samplingRate) {
        this(widthFunction, new ConstantFunction(0.), samplingRate);
    }

    /**
     * Constructor
     * 
     * @param parameters
     */
    public InflateOperator(final Function1D widthFunction, final Function1D shiftFunction, final double samplingRate) {
        super();
        this.widthFunction = widthFunction;
        this.shiftFunction = shiftFunction;
        this.samplingRate = samplingRate;
    }

    @Override
    public void addInput(final DrawingPrimitive input) throws InvalidOperatorInputException {
        if (!input.isLeaf()) {
            for (DrawingPrimitive primitive : input.getPrimitives()) {
                this.addInput(primitive);
            }
        } else {
            // check input type
            if (!(input instanceof ParameterizedPolyline)) {
                throw new InvalidOperatorInputException(this.getClass().getSimpleName() + " can only handle ParameterizedPolyline drawing primitive, not "
                        + input.getClass().getSimpleName());
            }
            this.lines.add((ParameterizedPolyline) input);
        }
    }

    /**
     * @return the viewport
     */
    public Viewport getViewport() {
        return this.viewport;
    }

    /**
     * @param viewport
     *            the viewport to set
     */
    public void setViewport(final Viewport viewport) {
        this.viewport = viewport;
    }

    public List<ParameterizedPolyline> getLines() {
        return this.lines;
    }

    @Override
    public DrawingPrimitive apply() {
        MultiDrawingPrimitive relines = new MultiDrawingPrimitive(); // resampled poly lines
        for (ParameterizedPolyline line : this.lines) {
            relines.addPrimitive(inflater(line, this.samplingRate, this.shiftFunction, this.widthFunction, this.viewport));
        }
        return relines;
    }

    /**
     * @param line
     *            line to inflate
     * @param samplingRate
     *            mean size of samples draw on screen (in pixels)
     * @param shift
     *            distance from the initial line (0 is on the line)
     * @param width
     *            distance between the two points around the shifted point
     * @return a newly re-parameterized polyline
     */
    private static DrawingPrimitive inflater(final ParameterizedPolyline line, final double samplingRate, final Function1D shiftFunction,
            final Function1D widthFunction, final Viewport viewport) {

        ParameterizedPolyline reline1 = new ParameterizedPolyline();
        ParameterizedPolyline reline2 = new ParameterizedPolyline();

        ParameterizedSegment[] segments = DrawingPrimitiveUtil.segmentize(line);
        if (segments.length == 0) {
            throw new IllegalStateException("there cannot be zero segments in a line... #line points=" + line.getPointCount() + " #segments=" + segments.length);
        }
        double length = segments[segments.length - 1].getEndDistance(); // length = distance to the last end point

        int nb = (int) Math.round(length / samplingRate);
        if (nb < 1) {
            nb = 1;
        }
        double dInc = length / nb;
        //    System.err.println();
        //    System.err.println("line to inflate : " + line.getPointCount() + " from " + line.getFirstPoint() + " (" + line.getFirstParameter() + " ) to "
        //        + line.getLastPoint() + " (" + line.getLastParameter() + ")");
        //    System.err.println("Resampled nb points = " + nb);
        //    System.err.println("line length = " + length + " inc = " + dInc + "   nb * dInc = " + nb * dInc);
        double d = 0;
        int currentSegmentIndex = 0;
        ParameterizedSegment currentSegment = segments[currentSegmentIndex];
        Vector2d normal = null, prevNormal = null;

        for (int n = 0; n <= nb; n++) {
            // compute line & segment interpolations factors
            //      double lineInterpolationFactor = d / length; // distance rescaled between 0 and 1 from the beginning of the line to it's end
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
            double interpolatedParameter = currentSegment.getInterpolatedParameter(segmentInterpolationFactor);

            //      System.err.println("------------ Interpolation #" + n + " on " + nb);
            //      System.err.println("             current segment " + currentSegment + " " + d + " C? [" + currentSegment.getStartDistance() + ","
            //          + currentSegment.getEndDistance() + "]");
            //      System.err.println("factor = " + segmentInterpolationFactor + " p = " + interpolatedPoint + "  t = " + interpolatedParameter);

            // compute normal
            prevNormal = normal; // store previous normal
            normal = computeNormal(currentSegment, prevNormal);

            try {
                //        System.out.println("evaluate width at x = " + interpolatedParameter);
                Double width = widthFunction.evaluate(interpolatedParameter);
                //        System.out.println("width = " + width);
                //        System.out.println("evaluate shift at x = " + interpolatedParameter);
                //        System.err.println(interpolatedParameter + " => " + width);
                Double shift = shiftFunction.evaluate(interpolatedParameter);
                //        System.out.println("shift = " + width);
                if (shift == null) {
                    logger.warn("Function Shift (" + shiftFunction.help() + ") is not defined for x = " + interpolatedParameter);
                    shift = 0.;
                }
                if (width == null) {
                    logger.warn("Function Width (" + widthFunction.help() + ") is not defined for x = " + interpolatedParameter);
                    width = 0.;
                }
                Point2d p1 = new Point2d(interpolatedPoint.x + (shift + width) * normal.x, interpolatedPoint.y + (shift + width) * normal.y);
                Point2d p2 = new Point2d(interpolatedPoint.x + (shift - width) * normal.x, interpolatedPoint.y + (shift - width) * normal.y);
                //        System.err.println("interpolated parameter = " + currentSegment.getInterpolatedParameter(segmentInterpolationFactor));
                reline1.addPoint(p1, currentSegment.getInterpolatedParameter(segmentInterpolationFactor));
                reline2.addPoint(p2, currentSegment.getInterpolatedParameter(segmentInterpolationFactor));
            } catch (FunctionEvaluationException e) {
                logger.error(e.getMessage());
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

        //    System.out.println("generate polygon");
        ParameterizedPolygon primitive = new ParameterizedPolygon();
        List<Point2d> outerFrontier = new ArrayList<Point2d>();
        List<Point2d> outerTextureFrontier = new ArrayList<Point2d>();
        for (int n = 0; n < reline1.getPointCount(); n++) {
            outerFrontier.add(reline1.getPoint(n));
            outerTextureFrontier.add(new Point2d(reline1.getParameter(n), -1));
            //      System.err.println("line1 n = " + n + " => " + reline1.getPoint(n) + " t = " + reline1.getParameter(n));
        }
        for (int n = reline2.getPointCount() - 1; n >= 0; n--) {
            outerFrontier.add(reline2.getPoint(n));
            outerTextureFrontier.add(new Point2d(reline2.getParameter(n), 1));
            //      System.err.println("line2 n = " + n + " => " + reline2.getPoint(n) + " t = " + reline2.getParameter(n));
        }
        logger.error("parameterization is lost in Inflate Operator since the parameterizer has been moved");
        //    primitive.setOuterFrontier(outerFrontier, outerTextureFrontier);
        return primitive;
    }

    /**
     * Compute the normal between two points.
     * 
     * @param prev
     * @param current
     * @param next
     * @param prevNormal
     * @return
     */
    private static Vector2d computeNormal(final ParameterizedSegment segment, final Vector2d prevNormal) {
        return segment.getNormal();
    }

    @Override
    public void removeAllInputs() {
        this.lines.clear();
    }

}
