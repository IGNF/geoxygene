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

package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.awt.Color;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import static org.lwjgl.opengl.GL11.*;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;
import fr.ign.cogit.geoxygene.appli.render.operator.InflateOperator;
import fr.ign.cogit.geoxygene.appli.render.operator.InvalidOperatorInputException;
import fr.ign.cogit.geoxygene.function.ConstantFunction;
import fr.ign.cogit.geoxygene.function.Function1D;

/**
 * @author JeT
 * This renderer writes GL Code to perform GL rendering
 */
public class DisplacementPrimitiveRenderer extends AbstractPrimitiveRenderer {

  private static Logger logger = Logger.getLogger(DisplacementPrimitiveRenderer.class.getName());
  private InflateOperator inflater = null;
  private GLPrimitiveRenderer glRenderer = new GLPrimitiveRenderer();

  /**
   * Default constructor
   */
  public DisplacementPrimitiveRenderer() {
    this(new ConstantFunction(5.), 10.);
  }

  /**
   * Constructor
   * @param parameters
   */
  public DisplacementPrimitiveRenderer(final Function1D widthFunction, final double samplingRate) {
    this(widthFunction, new ConstantFunction(0.), samplingRate);
  }

  /**
   * Constructor
   * @param parameters
   */
  public DisplacementPrimitiveRenderer(final Function1D widthFunction, final Function1D shiftFunction, final double samplingRate) {
    super();
    this.inflater = new InflateOperator(widthFunction, shiftFunction, samplingRate);
  }

  /**
   * Constructor
   * @param parameters
   */
  public DisplacementPrimitiveRenderer(final InflateOperator inflater) {
    super();
    this.inflater = inflater;
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.PrimitiveRenderer#render()
   */
  @Override
  public void render() throws RenderingException {
    if (this.getViewport() == null) {
      throw new RenderingException("viewport is not set");
    }
    for (DrawingPrimitive primitive : this.getPrimitives()) {
      this.render(primitive);
    }
  }

  /**
   * Render one drawing primitive
   * @param primitive
   * @throws RenderingException
   */
  private void render(final DrawingPrimitive primitive) throws RenderingException {
    if (primitive == null) {
      return;
    }
    if (!primitive.isLeaf()) {
      for (DrawingPrimitive childPrimitive : primitive.getPrimitives()) {
        this.render(childPrimitive);
      }
      return;
    } else if (primitive instanceof ParameterizedPolyline) {
      this.renderLine((ParameterizedPolyline) primitive);
      return;
    } else if (primitive instanceof ParameterizedPolygon) {
      this.renderSurface((ParameterizedPolygon) primitive);
      return;
    }

    logger.warn(this.getClass().getSimpleName() + " do not know how to paint primitives " + primitive.getClass().getSimpleName());
  }

  /**
   * Render simple polygon
   * @param primitive primitive to paint
   */
  private void renderSurface(final ParameterizedPolygon polygon) {
    logger.error(this.getClass().getSimpleName() + "::renderSurface() Not yet implemented");
  }

  /**
   * Render simple line
   * @param primitive primitive to paint
   * @throws RenderingException 
   */
  private void renderLine(final ParameterizedPolyline line) throws RenderingException {
    System.out.println("displacement 1");
    try {
      System.out.println("displacement 2");
      this.inflater.setViewport(this.getViewport());
      System.out.println("displacement 3");
      this.inflater.setInput(line);
      System.out.println("displacement 4");
    } catch (InvalidOperatorInputException e) {
      logger.error("This case is not possible ! Inflater must accept lines " + e.getMessage());
    }
    System.out.println("displacement 5");
    DrawingPrimitive inflatedLine = this.inflater.apply();
    System.out.println("displacement 6");
    this.glRenderer.setPrimitive(inflatedLine);
    System.out.println("displacement 7");
    this.glRenderer.setViewport(this.getViewport());
    System.out.println("displacement 8");
    this.glRenderer.render();
    System.out.println("displacement 9");
  }

  @Override
  public void initializeRendering() throws RenderingException {
    // nothing to initialize

  }

  @Override
  public void finalizeRendering() throws RenderingException {
    // nothing to finalize
  }

}
