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

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive;
import fr.ign.cogit.geoxygene.appli.render.primitive.MultiDrawingPrimitive;
import fr.ign.cogit.geoxygene.appli.render.primitive.ParameterizedLineConverterUtil;

/**
 * @author JeT
 * This operator outlines any drawing primitive
 * 
 * Parameters : 
 * -thickness: size of dilation
 */
public class OutlineOperator extends AbstractDrawingPrimitiveOperator {

  private static Logger logger = Logger.getLogger(OutlineOperator.class.getName());
  private List<DrawingPrimitive> primitives = new ArrayList<DrawingPrimitive>(); // primitives to outline
  private Viewport viewport = null;
  private Stroke awtStroke = null;
  private double outlineWidth = 10;
  private CapStyle capStyle = CapStyle.ROUND;
  private JoinStyle joinStyle = JoinStyle.ROUND;

  /**
   * Default constructor
   */
  public OutlineOperator() {
    this(10.);
  }

  /**
   * Constructor
   * @param parameters
   */
  public OutlineOperator(final double outlineWidth) {
    this(outlineWidth, CapStyle.ROUND, JoinStyle.ROUND);
  }

  /**
   * Constructor
   * @param outlineWidth
   * @param capStyle
   * @param joinStyle
   */
  public OutlineOperator(final double outlineWidth, final CapStyle capStyle, final JoinStyle joinStyle) {
    super();
    this.setOutlineWidth(outlineWidth);
    this.setCapStyle(capStyle);
    this.setJoinStyle(joinStyle);
  }

  /**
   * @return the capStyle
   */
  public CapStyle getCapStyle() {
    return this.capStyle;
  }

  /**
   * @param capStyle the capStyle to set
   */
  public void setCapStyle(final CapStyle capStyle) {
    this.capStyle = capStyle;
    this.invalidateStroke();
  }

  /**
   * @return the joinStyle
   */
  public JoinStyle getJoinStyle() {
    return this.joinStyle;
  }

  /**
   * @param joinStyle the joinStyle to set
   */
  public void setJoinStyle(final JoinStyle joinStyle) {
    this.joinStyle = joinStyle;
    this.invalidateStroke();
  }

  /**
   * @return the outlineWidth
   */
  public double getOutlineWidth() {
    return this.outlineWidth;
  }

  /**
   * @param outlineWidth the outlineWidth to set
   */
  public void setOutlineWidth(final double outlineWidth) {
    this.outlineWidth = outlineWidth;
    this.invalidateStroke();
  }

  /**
   * Set the awt stroke used to outline primitives. If set last, it do not use Join, Cap and Width
   * @param awtStroke the awtStroke to set
   */
  public void setAwtStroke(final Stroke awtStroke) {
    this.awtStroke = awtStroke;
  }

  @Override
  public void addInput(final DrawingPrimitive input) throws InvalidOperatorInputException {
    if (!input.isLeaf()) {
      for (DrawingPrimitive primitive : input.getPrimitives()) {
        this.addInput(primitive);
      }
    } else {
      this.primitives.add(input);
    }
  }

  /**
   * @return the viewport
   */
  public Viewport getViewport() {
    return this.viewport;
  }

  /**
   * @param viewport the viewport to set
   */
  public void setViewport(final Viewport viewport) {
    this.viewport = viewport;
  }

  public List<DrawingPrimitive> getPrimitives() {
    return this.primitives;
  }

  @Override
  public void removeAllInputs() {
    this.primitives.clear();
  }

  @Override
  public DrawingPrimitive apply() {
    MultiDrawingPrimitive dilatedPrimitives = new MultiDrawingPrimitive(); // resampled poly lines
    for (DrawingPrimitive primitive : this.getPrimitives()) {
      dilatedPrimitives.addPrimitive(outline(primitive, this.getAwtStroke()));
    }
    return dilatedPrimitives;
  }

  private static DrawingPrimitive outline(final DrawingPrimitive primitive, final Stroke awtStroke) {
    Shape shape = primitive.getShape();
    Shape outline = awtStroke.createStrokedShape(shape);
    DrawingPrimitive outlinePrimitive = ParameterizedLineConverterUtil.generateParameterizedPolyline(outline, null);
    return outlinePrimitive;
  }

  public class CompositeStroke implements Stroke {
    private Stroke stroke1, stroke2;

    public CompositeStroke(final Stroke stroke1, final Stroke stroke2) {
      this.stroke1 = stroke1;
      this.stroke2 = stroke2;
    }

    @Override
    public Shape createStrokedShape(final Shape shape) {
      return this.stroke2.createStrokedShape(this.stroke1.createStrokedShape(shape));
    }
  }

  /**
   * get the awt stroke with stored parameters
   * @return
   */
  private Stroke getAwtStroke() {
    if (this.awtStroke == null) {
      this.awtStroke = new BasicStroke((float) this.getOutlineWidth(), this.getCapStyle().getValue(), this.getJoinStyle().getValue());
      //this.awtStroke = new CompositeStroke(new BasicStroke(10f), new BasicStroke(0.5f));
    }
    return this.awtStroke;
  }

  private void invalidateStroke() {
    this.awtStroke = null;
  }

}
