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

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive;
import fr.ign.cogit.geoxygene.appli.render.primitive.MultiDrawingPrimitive;
import fr.ign.cogit.geoxygene.appli.render.primitive.ParameterizedPolyline;

/**
 * @author JeT
 * This operator takes a list of Polyline as input. It rotates every points of the ligne around its center.
 * The center line is computed as the average point between the first and the last point. We may add
 * some others center computation methods
 * 
 * Parameters : 
 * -RotationAngleParameterId rotation angle (in rad)
 */
public class RotateOperator extends AbstractDrawingPrimitiveOperator {

  public static final double RotationAngleParameterDefaultValue = Math.PI / 2.;
  public double rotationAngle = RotationAngleParameterDefaultValue;
  private List<DrawingPrimitive> primitives = new ArrayList<DrawingPrimitive>(); // primitives to rotate

  private static Logger logger = Logger.getLogger(RotateOperator.class.getName()); // logger

  /**
   * Default constructor
   */
  public RotateOperator() {
    this(RotationAngleParameterDefaultValue);
  }

  /**
   * Constructor
   * @param parameters
   */
  public RotateOperator(final double rotationAngleParameterValue) {
    super();
    this.rotationAngle = rotationAngleParameterValue;
  }

  @Override
  public void addInput(final DrawingPrimitive input) throws InvalidOperatorInputException {
    this.primitives.add(input);
  }

  @Override
  public void removeAllInputs() {
    this.primitives.clear();
  }

  public List<DrawingPrimitive> getPrimitives() {
    return this.primitives;
  }

  @Override
  public DrawingPrimitive apply() {
    double angle = this.rotationAngle;
    double ca = Math.cos(angle);
    double sa = Math.sin(angle);

    MultiDrawingPrimitive rotatedPrimitives = new MultiDrawingPrimitive(); // rotate poly lines
    for (DrawingPrimitive primitive : this.primitives) {
      this.apply(ca, sa, rotatedPrimitives, primitive);
    }
    return rotatedPrimitives;
  }

  /**
   * apply rotation to one primitive
   * @param ca cosine
   * @param sa sine
   * @param rotatedPrimitives list of rotated primitives
   * @param primitive primitive to rotate
   */
  private void apply(final double ca, final double sa, final MultiDrawingPrimitive rotatedPrimitives, final DrawingPrimitive primitive) {
    if (primitive instanceof MultiDrawingPrimitive) {
      for (DrawingPrimitive childPrimitive : primitive.getPrimitives()) {
        this.apply(ca, sa, rotatedPrimitives, childPrimitive);
      }
    } else if (primitive instanceof ParameterizedPolyline) {
      ParameterizedPolyline line = (ParameterizedPolyline) primitive;
      rotatedPrimitives.addPrimitives(rotate(line, ca, sa));
    } else {
      // do not rotate this kind of primitives, just keep them as it
      rotatedPrimitives.addPrimitive(primitive);
      logger.warn("do not know how to rotate " + primitive.getClass().getSimpleName());
    }

  }

  /**
   * Cut One polyline
   */
  public static List<ParameterizedPolyline> rotate(final ParameterizedPolyline line, final double ca, final double sa) {
    List<ParameterizedPolyline> rotatedLines = new ArrayList<ParameterizedPolyline>();
    ParameterizedPolyline rotatedLine = new ParameterizedPolyline();

    Point2d firstPoint = line.getPoint(0);
    Point2d lastPoint = line.getPoint(line.getPointCount() - 1);
    Point2d c = new Point2d((firstPoint.x + lastPoint.x) / 2., (firstPoint.y + lastPoint.y) / 2.);

    for (int n = 0; n < line.getPointCount(); n++) {
      Point2d p = line.getPoint(n);
      double t = line.getParameter(n);
      rotatedLine.addPoint(rotate(p, c, ca, sa), t);
    }

    rotatedLines.add(rotatedLine);
    return rotatedLines;
  }

  /**
   * Rotate a point 'p' around center 'c' with an angle defined by cos 'ca' & sin 'sa' values
   * @param p point to rotate
   * @param c center point
   * @param ca cos angle
   * @param sa sin angle
   * @return
   */
  private static Point2d rotate(final Point2d p, final Point2d c, final double ca, final double sa) {
    return new Point2d((p.x - c.x) * ca - (p.y - c.y) * sa + c.x, (p.x - c.x) * sa + (p.y - c.y) * ca + c.y);
  }
}
