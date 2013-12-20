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

import static org.lwjgl.opengl.GL11.GL_POINTS;
import static org.lwjgl.opengl.GL11.GL_POINT_SMOOTH;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.awt.Color;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.render.RenderGL11Util;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;

/**
 * @author JeT
 * This renderer writes GL Code to perform GL rendering
 */
public class GLPrimitivePointRenderer extends AbstractPrimitiveRenderer {

  private static Logger logger = Logger.getLogger(GLPrimitivePointRenderer.class.getName());
  private double extremitiesSize = 3.; // width of the first and last points
  private boolean extremitiesRound = false; // round or square points of the first and last points (true => round)
  private Color extremitiesColor = Color.red; // color of the first and last points
  private double innersSize = 2.; // width of all points but first and last 
  private boolean innersRound = true; // round or square points of all points but first and last  (true => round)
  private Color innersColor = Color.black; // color of all points but first and last 

  /**
   * Constructor
   */
  public GLPrimitivePointRenderer() {
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
   * Constructor defining all points parameters (extremities and inner points not differentiated)
   * @param size points size (in pixels)
   * @param round points roundness (true = round, false = square)
   * @param color points color
   */
  public GLPrimitivePointRenderer(final double size, final boolean round, final Color color) {
    super();
    this.extremitiesSize = size;
    this.extremitiesRound = round;
    this.extremitiesColor = color;
    this.innersSize = size;
    this.innersRound = round;
    this.innersColor = color;
  }

  /**
   * Constructor
   * @param extremitiesSize extremities points size (in pixels)
   * @param extremitiesRound extremities  points roundness (true = round, false = square)
   * @param extremitiesColor extremities points color
   * @param innersSize inner  points size (in pixels)
   * @param innersRound inner points roundness (true = round, false = square)
   * @param innersColor inner points color
   */
  public GLPrimitivePointRenderer(final double extremitiesSize, final boolean extremitiesRound, final Color extremitiesColor,
      final double innersSize, final boolean innersRound, final Color innersColor) {
    super();
    this.extremitiesSize = extremitiesSize;
    this.extremitiesRound = extremitiesRound;
    this.extremitiesColor = extremitiesColor;
    this.innersSize = innersSize;
    this.innersRound = innersRound;
    this.innersColor = innersColor;
  }

  /**
   * @return the extremitiesSize
   */
  public double getExtremitiesSize() {
    return this.extremitiesSize;
  }

  /**
   * @param extremitiesSize the extremitiesSize to set
   */
  public void setExtremitiesSize(final double extremitiesSize) {
    this.extremitiesSize = extremitiesSize;
  }

  /**
   * @return the extremitiesRound
   */
  public boolean isExtremitiesRound() {
    return this.extremitiesRound;
  }

  /**
   * @param extremitiesRound the extremitiesRound to set
   */
  public void setExtremitiesRound(final boolean extremitiesRound) {
    this.extremitiesRound = extremitiesRound;
  }

  /**
   * @return the extremitiesColor
   */
  public Color getExtremitiesColor() {
    return this.extremitiesColor;
  }

  /**
   * @param extremitiesColor the extremitiesColor to set
   */
  public void setExtremitiesColor(final Color extremitiesColor) {
    this.extremitiesColor = extremitiesColor;
  }

  /**
   * @return the innersSize
   */
  public double getInnersSize() {
    return this.innersSize;
  }

  /**
   * @param innersSize the innersSize to set
   */
  public void setInnersSize(final double innersSize) {
    this.innersSize = innersSize;
  }

  /**
   * @return the innersRound
   */
  public boolean isInnersRound() {
    return this.innersRound;
  }

  /**
   * @param innersRound the innersRound to set
   */
  public void setInnersRound(final boolean innersRound) {
    this.innersRound = innersRound;
  }

  /**
   * @return the innersColor
   */
  public Color getInnersColor() {
    return this.innersColor;
  }

  /**
   * @param innersColor the innersColor to set
   */
  public void setInnersColor(final Color innersColor) {
    this.innersColor = innersColor;
  }

  /**
   * Render one drawing primitive
   * @param primitive
   * @throws RenderingException
   */
  private void render(final DrawingPrimitive primitive) throws RenderingException {
    if (!primitive.isLeaf()) {
      for (DrawingPrimitive child : primitive.getPrimitives()) {
        this.render(child);
      }
      return;
    }
    if (primitive.getPointCount() == 0) {
      return;
    }
    if (this.isExtremitiesRound()) {
      glEnable(GL_POINT_SMOOTH);
    } else {
      glDisable(GL_POINT_SMOOTH);
    }
    glPointSize((float) this.getExtremitiesSize());
    RenderGL11Util.glColor(this.getExtremitiesColor());
    glBegin(GL_POINTS);
    Point2d p = primitive.getPoint(0);
    glVertex2d(p.x, p.y);
    p = primitive.getPoint(primitive.getPointCount() - 1);
    glVertex2d(p.x, p.y);
    glEnd();

    if (this.isInnersRound()) {
      glEnable(GL_POINT_SMOOTH);
    } else {
      glDisable(GL_POINT_SMOOTH);
    }
    glBegin(GL_POINTS);
    RenderGL11Util.glColor(this.getInnersColor());
    glPointSize((float) this.getInnersSize());
    for (int n = 1; n < primitive.getPointCount() - 1; n++) {
      p = primitive.getPoint(n);
      glVertex2d(p.x, p.y);
    }
    glEnd();
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
