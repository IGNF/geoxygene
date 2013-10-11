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

/**
 * @author JeT
 * This operator takes a list of drawing primitives as input. It computes an homogeneous dilation of their points
 * relative to the homothetic center. If no center is given, the gravity center is used
 *  
 * 
 * input line:           *------C---*
 * output line:   *-------------C--------*         (x1.3)
 * 
 * Parameters :
 * - homothetic factor: 1: no change. <1 size reduction. >1 size widening
 * - homothetic center (optional)
 */
public class ExpandOperator extends AbstractDrawingPrimitiveOperator {

  private List<DrawingPrimitive> inputs = new ArrayList<DrawingPrimitive>(); //
  private double homotheticFactor = 2.;
  private Point2d homotheticCenter = null;

  /**
   * Constructor
   * @param parameters
   */
  public ExpandOperator() {
    this(2., null);
  }

  /**
   * Constructor
   * @param homotheticFactor
   * @param homotheticCenter
   */
  public ExpandOperator(final double homotheticFactor) {
    this(homotheticFactor, null);
    this.homotheticFactor = homotheticFactor;
  }

  /**
   * Constructor
   * @param homotheticFactor
   * @param homotheticCenter
   */
  public ExpandOperator(final double homotheticFactor, final Point2d homotheticCenter) {
    super();
    this.homotheticFactor = homotheticFactor;
    this.homotheticCenter = homotheticCenter;
  }

  @Override
  public void addInput(final DrawingPrimitive input) throws InvalidOperatorInputException {
    if (!input.isLeaf()) {
      for (DrawingPrimitive primitive : input.getPrimitives()) {
        this.addInput(primitive);
      }
    } else {
      // check input type
      this.inputs.add(input);
    }
  }

  @Override
  public void removeAllInputs() {
    this.inputs.clear();
  }

  @Override
  public DrawingPrimitive apply() {

    MultiDrawingPrimitive transformedPrimitives = new MultiDrawingPrimitive(); // resampled poly lines
    for (DrawingPrimitive input : this.inputs) {
      transformedPrimitives.addPrimitive(homothety(input, this.homotheticFactor, this.homotheticCenter));
    }
    return transformedPrimitives;
  }

  /**
   * Compute the homothetic transformation of one primitive.
   * The primitive is directly modified in its content. No copy is done
   * @return the given primitive
   */
  public static DrawingPrimitive homothety(final DrawingPrimitive primitive, final double factor, Point2d center) {
    if (!primitive.isLeaf()) {
      for (DrawingPrimitive child : primitive.getPrimitives()) {
        homothety(child, factor, center);
      }
      return primitive;
    }

    if (center == null) {
      // compute gravity center
      center = DrawingPrimitiveUtil.computeGravityCenter(primitive);
    }

    for (int n = 0; n < primitive.getPointCount(); n++) {
      double x = primitive.getPoint(n).x;
      double y = primitive.getPoint(n).y;
      primitive.getPoint(n).x = (x - center.x) * factor + center.x;
      primitive.getPoint(n).y = (y - center.y) * factor + center.y;
    }
    return primitive;
  }

}
