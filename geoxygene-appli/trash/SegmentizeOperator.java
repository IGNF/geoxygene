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

import fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive;
import fr.ign.cogit.geoxygene.appli.render.primitive.MultiDrawingPrimitive;
import fr.ign.cogit.geoxygene.appli.render.primitive.ParameterizedPolyline;
import fr.ign.cogit.geoxygene.appli.render.primitive.ParameterizedSegment;

/**
 * @author JeT
 * This operator takes a list of Polyline as input. It cuts one line into segments (lines composed of only two points)
 * by duplicating the inner points
 *  
 * 
 * input line:      *---------*------------*---------------------------*
 * output line:     *---------**-----------**--------------------------*
 * 
 * Parameters : 
 */
public class SegmentizeOperator extends AbstractDrawingPrimitiveOperator {

  private List<ParameterizedPolyline> lines = new ArrayList<ParameterizedPolyline>(); // poly lines to resample

  /**
   * Constructor
   * @param parameters
   */
  public SegmentizeOperator() {
    super();
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

  @Override
  public void removeAllInputs() {
    this.lines.clear();
  }

  public List<ParameterizedPolyline> getLines() {
    return this.lines;
  }

  @Override
  public DrawingPrimitive apply() {
    MultiDrawingPrimitive segmentedLines = new MultiDrawingPrimitive(); // resampled poly lines
    for (ParameterizedPolyline line : this.lines) {
      segmentedLines.addPrimitives(segmentize(line));
    }
    return segmentedLines;
  }

  /**
   * Cut One polyline into multiple segments by duplicating inner points
   */
  public static List<ParameterizedPolyline> segmentize(final ParameterizedPolyline line) {
    List<ParameterizedPolyline> segmentedLines = new ArrayList<ParameterizedPolyline>();

    for (int n = 0; n < line.getPointCount() - 1; n++) {
      Point2d p1 = line.getPoint(n);
      Point2d p2 = line.getPoint(n + 1);
      double t1 = line.getParameter(n);
      double t2 = line.getParameter(n + 1);
      ParameterizedPolyline segment = new ParameterizedPolyline();
      segment.addPoint(p1, t1);
      segment.addPoint(p2, t2);
      segmentedLines.add(segment);
    }

    return segmentedLines;
  }

}
