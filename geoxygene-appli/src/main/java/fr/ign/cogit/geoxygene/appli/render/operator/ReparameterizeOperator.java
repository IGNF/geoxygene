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

/**
 * @author JeT
 * This operator takes a list of Polyline as input. It recalculate the parameterization of the lines
 * 
 * 
 * input line:         *--------------*--------------*-----------------*
 *                     t1             t2             t3                t4
 * 
 * output lines:       *--------------*--------------*-----------------*
 *                     0              x2             x3                1/length
 * 
 * Parameters : 
 * - reparameterization method
 */
public class ReparameterizeOperator extends AbstractDrawingPrimitiveOperator {

  public enum ParameterizationType {
    UNIT, // 0 .. 1
    LENGTH, // use points coordinates to compute length. 0..length
  }

  private static Logger logger = Logger.getLogger(ReparameterizeOperator.class.getName());
  private ParameterizationType parameterizationMethod = ParameterizationType.UNIT;
  private List<ParameterizedPolyline> lines = new ArrayList<ParameterizedPolyline>(); // poly lines to inflate

  /**
   * Default constructor
   */
  public ReparameterizeOperator() {
    this(ParameterizationType.UNIT);
  }

  /**
   * Constructor
   * @param parameters
   */
  public ReparameterizeOperator(final ParameterizationType parameterizationMethod) {
    super();
    this.parameterizationMethod = parameterizationMethod;
  }

  @Override
  public void addInput(final DrawingPrimitive input) throws InvalidOperatorInputException {
    if (!input.isLeaf()) {
      for (DrawingPrimitive primitive : input.getPrimitives()) {
        this.addInput(primitive);
      }
    } else {
      if (!(input instanceof ParameterizedPolyline)) {
        // check input type    if (!(input instanceof ParameterizedPolyline)) {
        throw new InvalidOperatorInputException(this.getClass().getSimpleName() + " can only handle ParameterizedPolyline drawing primitive, not "
            + input.getClass().getSimpleName());
      }
      this.lines.add((ParameterizedPolyline) input);
    }
  }

  public List<ParameterizedPolyline> getLines() {
    return this.lines;
  }

  @Override
  public DrawingPrimitive apply() {
    MultiDrawingPrimitive relines = new MultiDrawingPrimitive(); // resampled poly lines
    for (ParameterizedPolyline line : this.lines) {
      switch (this.parameterizationMethod) {
      case UNIT:
        relines.addPrimitive(reparameterizeUnit(line));
        break;
      case LENGTH:
        relines.addPrimitive(reparameterizeLength(line));
        break;
      }
    }
    return relines;
  }

  /**
   * This method deos not create a new Polyline, it modifies the existing one (and return it)
   * resulting parameters are set to 0 .. 1 length
   * @param line line to reparameterize
   * @return the given re-parameterized polyline
   */
  private static ParameterizedPolyline reparameterizeUnit(final ParameterizedPolyline line) {
    double lineLength = DrawingPrimitiveUtil.pointLength(line);
    double currentLength = 0.;
    line.setParameter(0, 0);
    Point2d previousPoint = line.getPoint(0);
    for (int nPoint = 1; nPoint < line.getPointCount(); nPoint++) {
      Point2d currenPoint = line.getPoint(nPoint);
      currentLength += previousPoint.distance(currenPoint);
      line.setParameter(nPoint, currentLength / lineLength);
    }
    return line;
  }

  /**
   * This method deos not create a new Polyline, it modifies the existing one (and return it)
   * resulting parameters are set to 0 .. line length
   * @param line line to reparameterize
   * @return the given re-parameterized polyline
   */
  private static ParameterizedPolyline reparameterizeLength(final ParameterizedPolyline line) {
    double currentLength = 0.;
    line.setParameter(0, 0);
    Point2d previousPoint = line.getPoint(0);
    for (int nPoint = 1; nPoint < line.getPointCount(); nPoint++) {
      Point2d currenPoint = line.getPoint(nPoint);
      currentLength += previousPoint.distance(currenPoint);
      line.setParameter(nPoint, currentLength);
    }
    return line;
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

  @Override
  public void removeAllInputs() {
    this.lines.clear();
  }

}
