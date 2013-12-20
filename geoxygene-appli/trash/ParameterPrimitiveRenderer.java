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

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.awt.Color;
import java.text.DecimalFormat;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/**
 * @author JeT
 * This renderer writes GL Code to perform GL rendering
 * It displays the primitives parameters values as text for each points
 */
public class ParameterPrimitiveRenderer extends AbstractPrimitiveRenderer {

  private static final int crossSize = 4;

  private static Logger logger = Logger.getLogger(ParameterPrimitiveRenderer.class.getName());

  private Color textColor = Color.black;
  private DecimalFormat df = new DecimalFormat(".##");
  private int nbParametersToskip = 0; // number of parameter skipped (not displayed)

  /**
   * Constructor
   * @param backgroundColor
   * @param foregroundColor
   */
  public ParameterPrimitiveRenderer() {
    this(Color.black);
  }

  /**
   * Constructor
   * @param backgroundColor
   * @param foregroundColor
   */
  public ParameterPrimitiveRenderer(final Color textColor) {
    this(textColor, new DecimalFormat(".##"));
  }

  /**
   * Constructor
   * @param backgroundColor
   * @param foregroundColor
   */
  public ParameterPrimitiveRenderer(final Color textColor, final DecimalFormat df) {
    super();
    this.textColor = textColor;
    this.df = df;
    ;
  }

  /**
   * @return the skippedParameterCount
   */
  public int getNbParametersToSkip() {
    return this.nbParametersToskip;
  }

  /**
   * @param nbParametersToskip the skippedParameterCount to set
   */
  public void setNbParametersToSkip(final int nbParametersToskip) {
    this.nbParametersToskip = nbParametersToskip;
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
      this.render(primitive, this.getViewport());
    }

  }

  /**
   * Render one drawing primitive
   * @param primitive
   * @throws RenderingException
   */
  private void render(final DrawingPrimitive primitive, final Viewport viewport) throws RenderingException {
    if (!primitive.isLeaf()) {
      MultiDrawingPrimitive multiPrimitive = (MultiDrawingPrimitive) primitive;
      for (DrawingPrimitive childPrimitive : multiPrimitive.getPrimitives()) {
        this.render(childPrimitive, viewport);
      }
      return;
    } else if (primitive instanceof ParameterizedPolyline) {
      this.renderLine((ParameterizedPolyline) primitive);
      return;
    } else if (primitive instanceof ParameterizedPolygon) {
      this.renderPolygon((ParameterizedPolygon) primitive);
      return;
    }
    logger.warn(this.getClass().getSimpleName() + " do not know how to paint primitives " + primitive.getClass().getSimpleName());
  }

  /**
   * Render the parameter value as text and a line to show precisely the point position
   * @param polygon primitive containing parameter values
   */
  private void renderPolygon(final ParameterizedPolygon polygon) {
    int nbSkipppedParameters = this.getNbParametersToSkip();
    GLTools.glColor(this.textColor);
    glDisable(GL_BLEND);
    glDisable(GL_DEPTH_TEST);
    glLineWidth(1.f);
    for (int outerFrontierPointIndex = 0; outerFrontierPointIndex < polygon.getOuterFrontier().size(); outerFrontierPointIndex++) {
      if (nbSkipppedParameters >= this.getNbParametersToSkip()) {
        Point2d p = polygon.getOuterFrontier().get(outerFrontierPointIndex);
        Point2d t = polygon.getOuterFrontierTextureCoordinates(outerFrontierPointIndex);
        //        System.err.println("outer frontier parameter = " + t);
        this.displayParameterPoint(p, this.df.format(t.x) + "x" + this.df.format(t.y));
        nbSkipppedParameters = 0;
      } else {
        nbSkipppedParameters++;
      }
    }
  }

  /**
   * Render the parameter value as text and a line to show precisely the point position
   * @param primitive primitive to paint
   */
  private void renderLine(final ParameterizedPolyline line) {
    int nbSkipppedParameters = this.getNbParametersToSkip();
    GLTools.glColor(this.textColor);
    glDisable(GL_BLEND);
    glDisable(GL_DEPTH_TEST);
    for (int nPoint = 0; nPoint < line.getPointCount(); nPoint++) {
      if (nbSkipppedParameters >= this.getNbParametersToSkip()) {
        Point2d p = line.getPoint(nPoint);
        Double t = line.getParameter(nPoint);
        this.displayParameterPoint(p, this.df.format(t));
        nbSkipppedParameters = 0;
      } else {
        nbSkipppedParameters++;
      }
    }
  }

  /**
   * display a parameter value at a given point
   * @param p point where the parameter lies
   * @param value parameter value (can be null)
   */
  private void displayParameterPoint(final Point2d p, final String value) {
    Point2d p2 = new Point2d(p.x + 10, p.y - 10);
    glBegin(GL_LINES);
    GLTools.glVertex(p);
    GLTools.glVertex(p2);
    glVertex2d(p.x - crossSize, p.y);
    glVertex2d(p.x + crossSize, p.y);
    glVertex2d(p.x, p.y - crossSize);
    glVertex2d(p.x, p.y + crossSize);
    glEnd();
    if (value == null) {
      GLTools.glColor(Color.red);
      glBegin(GL_LINES);
      glVertex2d(p2.x + 2, p2.y + 2);
      glVertex2d(p2.x + 12, p2.y + 12);
      glVertex2d(p2.x + 2, p2.y + 12);
      glVertex2d(p2.x + 12, p2.y + 2);

      glEnd();
      GLTools.glColor(this.textColor);
    } else {
      GLTools.glDrawString(value, p2.x, p2.y);
    }
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
