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

import java.util.Collection;
import java.util.List;

import org.lwjgl.opengl.GL11;

import fr.ign.cogit.geoxygene.appli.render.RenderingException;
import fr.ign.cogit.geoxygene.appli.render.operator.CutterOperator;
import fr.ign.cogit.geoxygene.appli.render.operator.InvalidOperatorInputException;
import fr.ign.cogit.geoxygene.appli.render.operator.RotateOperator;

/**
 * @author JeT
 * This renderer uses operators to modify primitives and then draw the resulting primitives
 */
public class MillPrimitiveRenderer extends AbstractPrimitiveRenderer {

  private PrimitiveRenderer chainedRenderer = null; // renderer used after operators apply 

  /**
   * Constructor
   */
  public MillPrimitiveRenderer(final PrimitiveRenderer renderer) {
    this.chainedRenderer = renderer;
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.PrimitiveRenderer#render()
   */
  @Override
  public void render() throws RenderingException {
    if (this.getViewport() == null) {
      throw new RenderingException("viewport is not set");
    }
    this.chainedRenderer.setViewport(this.getViewport());
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
    // TODO: create a class PrimitiveManipulatorEngine that do the job

    // compute parameter value to cut in world space coordinates
    double stroke = 5 * this.getViewport().getScale();
    //    ResamplerOperator resampler = new ResamplerOperator(0.5);
    CutterOperator cutter = new CutterOperator(15, 10);
    try {
      cutter.addInput(primitive);
    } catch (InvalidOperatorInputException e) {
      throw new RenderingException(e);
    }
    DrawingPrimitive resultingPrimitive = cutter.apply();

    RotateOperator rotator = new RotateOperator(Math.PI / 4.);
    try {
      rotator.addInput(resultingPrimitive);
    } catch (InvalidOperatorInputException e) {
      throw new RenderingException(e);
    }
    resultingPrimitive = rotator.apply();

    GL11.glColor3f(0.f, 0.f, 0.f);
    GL11.glLineWidth(1.f);
    this.chainedRenderer.removeAllPrimitives();
    this.chainedRenderer.addPrimitive(resultingPrimitive);
    this.chainedRenderer.render();

    //    GLPrimitivePointRenderer pointRenderer = new GLPrimitivePointRenderer();
    //    pointRenderer.setViewport(this.getViewport());
    //
    //    pointRenderer.removeAllPrimitive();
    //    pointRenderer.addPrimitive(primitive);
    //    pointRenderer.render();
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
