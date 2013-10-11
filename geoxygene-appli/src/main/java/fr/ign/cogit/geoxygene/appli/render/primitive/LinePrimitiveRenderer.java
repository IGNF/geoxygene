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

import javax.swing.JDialog;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLTools;
import fr.ign.cogit.geoxygene.appli.gl.TiledTexture;
import fr.ign.cogit.geoxygene.appli.gl.Texture;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;
import fr.ign.cogit.geoxygene.math.MathUtil;
import fr.ign.cogit.geoxygene.math.ui.GraphPanel;

/**
 * @author JeT
 * This renderer writes GL Code to perform GL rendering
 * It draws line using textures
 */
public class LinePrimitiveRenderer extends AbstractPrimitiveRenderer {

  private static Logger logger = Logger.getLogger(LinePrimitiveRenderer.class.getName());

  private static final double defaultLineWidth = 4.;
  private double lineWidth = defaultLineWidth;
  private Texture texture = null;

  /**
   * Constructor
   * @param backgroundColor
   * @param foregroundColor
   */
  public LinePrimitiveRenderer() {
    this(defaultLineWidth);
  }

  /**
   * Constructor
   * @param backgroundColor
   * @param foregroundColor
   */
  public LinePrimitiveRenderer(final double lineWidth) {
    super();
    this.lineWidth = lineWidth;
    TiledTexture tiledTexture = new TiledTexture("./src/main/resources/textures/cell02.png", 4, 256, 150, 210);
    tiledTexture.setTextureSizeInWorldCoordinates(400);
    this.texture = tiledTexture;
  }

  /**
   * @return the lineWidth
   */
  public double getLineWidth() {
    return this.lineWidth;
  }

  /**
   * @param lineWidth the lineWidth to set
   */
  public void setLineWidth(final double lineWidth) {
    this.lineWidth = lineWidth;
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
    }
    logger.warn(this.getClass().getSimpleName() + " do not know how to paint primitives " + primitive.getClass().getSimpleName());
  }

  /**
   * Render simple line.
   * @param primitive primitive to paint
   */
  private void renderLine(final ParameterizedPolyline line) {
    if (line.getPointCount() < 2) {
      logger.warn("Line has " + line.getPointCount() + " points and cannot be rendered");
      return;
    }
    this.texture.initializeRendering();
    this.texture.setRange(0., 0, line.getParameter(line.getPointCount() - 1), 1);
    //    System.err.println("set range to " + this.texture.getMinX() + "x" + this.texture.getMinY() + " - " + this.texture.getMaxX() + "x"
    //        + this.texture.getMaxY());
    /**
     * p1 is the nth point, n1 the segment normal at p1 point 
     * p2 is the (n+1)th point, n2 the segment normal at p2 point
     * 
     * pXa, pXb are the 
     */
    Point2d p1 = line.getPoint(0); // nth point
    double p1t = line.getParameter(0);
    Point2d p2 = line.getPoint(1); // n+1 th point
    double p2t = line.getParameter(1);
    Point2d p3 = null; // n+2 th point
    Vector2d v1 = MathUtil.vector(p1, p2); // line direction at p1
    Vector2d v2 = null; // line direction at p2 
    Vector2d n1 = MathUtil.computeNormal(v1); // line normal at p1 (perpendicular to segment direction)
    Vector2d n2 = null; // line normal at p2 (perpendicular to segment direction)
    Point2d p1a = MathUtil.pointOfLine(p1, n1, -this.getLineWidth() / 2); // first stretched point at p1 (p1 + lineWidth/2 * n1)
    Point2d p1b = MathUtil.pointOfLine(p1, n1, this.getLineWidth() / 2); // second stretched point at p1 (p1 - lineWidth/2 * n1)
    Point2d p2a = null; // first stretched point at p2 (p2 + lineWidth/2 * n2)
    Point2d p2b = null; // second stretched point at p2 (p2 - lineWidth/2 * n2)

    if (line.getPointCount() <= 2) {
      p3 = p2;
      n2 = n1;
    }

    GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    GL11.glBegin(GL11.GL_QUAD_STRIP);
    try {
      GL11.glTexCoord2d(p1t, 0);
      GLTools.glTexCoord(this.texture.vertexCoordinates(p1t, 0));
      GL11.glVertex2d(p1a.x, p1a.y);
      GLTools.glTexCoord(this.texture.vertexCoordinates(p1t, 1));
      GL11.glVertex2d(p1b.x, p1b.y);
      //      System.err.println("set first point coordinates = " + p1t + "x" + "0" + " => " + this.texture.vertexCoordinates(p1t, 0));
      //      System.err.println("set first point coordinates = " + p1t + "x" + "1" + " => " + this.texture.vertexCoordinates(p1t, 1));

      for (int nPoint = 0; nPoint < line.getPointCount() - 2; nPoint++) {

        p3 = line.getPoint(nPoint + 2);
        v2 = MathUtil.vector(p2, p3);
        n2 = MathUtil.computeNormal(v2);
        p2a = MathUtil.pointOfLine(p2, n2, -this.getLineWidth() / 2);
        p2b = MathUtil.pointOfLine(p2, n2, this.getLineWidth() / 2);
        p2t = line.getParameter(nPoint);

        Point2d Ia = MathUtil.intersectionPoint(p1a, v1, p2a, v2);
        Point2d Ib = MathUtil.intersectionPoint(p1b, v1, p2b, v2);
        if (Ia == null || Ib == null) {
          Ia = MathUtil.mean(p1a, p2a);
          Ib = MathUtil.mean(p1b, p2b);
        }

        GLTools.glTexCoord(this.texture.vertexCoordinates(p2t, 0));
        GL11.glVertex2d(Ia.x, Ia.y);
        GLTools.glTexCoord(this.texture.vertexCoordinates(p2t, 1));
        GL11.glVertex2d(Ib.x, Ib.y);

        //        System.err.println("set #" + nPoint + " point coordinates = " + p2t + "x" + "0" + " => " + this.texture.vertexCoordinates(p2t, 0));
        //        System.err.println("set #" + nPoint + " point coordinates = " + p2t + "x" + "1" + " => " + this.texture.vertexCoordinates(p2t, 1));
        // shift context to the next point
        p1 = p2;
        p1t = p2t;
        n1 = n2;
        v1 = v2;
        p1a = Ia;
        p1b = Ib;
        p2 = p3;

      }
      // draw the last point
      Point2d p3a = MathUtil.pointOfLine(p3, n2, -this.getLineWidth() / 2);
      Point2d p3b = MathUtil.pointOfLine(p3, n2, this.getLineWidth() / 2);
      double p3t = line.getParameter(line.getPointCount() - 1);
      GLTools.glTexCoord(this.texture.vertexCoordinates(p3t, 0));
      GL11.glVertex2d(p3a.x, p3a.y);
      GLTools.glTexCoord(this.texture.vertexCoordinates(p3t, 1));
      GL11.glVertex2d(p3b.x, p3b.y);
      //      System.err.println("set last point coordinates = " + p3t + "x" + "0" + " => " + this.texture.vertexCoordinates(p3t, 0));
      //      System.err.println("set last point coordinates = " + p3t + "x" + "1" + " => " + this.texture.vertexCoordinates(p3t, 1));
    } finally {
      GL11.glEnd();
    }

    this.texture.initializeRendering();

  }

  static boolean first = true; // debug purpose only

  /**
   * Render simple line.
   * @param primitive primitive to paint
   */
  private void renderLineDebug(final ParameterizedPolyline line) {
    if (line.getPointCount() < 2) {
      logger.warn("Line has " + line.getPointCount() + " points and cannot be rendered");
      return;
    }
    final double epsilon = 1E-1;
    /**
     * p1 is the nth point, n1 the segment normal at p1 point 
     * p2 is the (n+1)th point, n2 the segment normal at p2 point
     * 
     * pXa, pXb are the 
     */
    Point2d p1 = line.getPoint(0); // nth point
    Point2d p2 = line.getPoint(1); // n+1 th point
    Point2d p3 = null; // n+2 th point
    Vector2d v1 = MathUtil.vector(p1, p2); // line direction at p1
    Vector2d v2 = null; // line direction at p2 
    Vector2d n1 = MathUtil.computeNormal(v1); // line normal at p1 (perpendicular to segment direction)
    Vector2d n2 = null; // line normal at p2 (perpendicular to segment direction)
    Point2d p1a = MathUtil.pointOfLine(p1, n1, -this.getLineWidth() / 2); // first stretched point at p1 (p1 + lineWidth/2 * n1)
    Point2d p1b = MathUtil.pointOfLine(p1, n1, this.getLineWidth() / 2); // second stretched point at p1 (p1 - lineWidth/2 * n1)
    Point2d p2a = null; // first stretched point at p2 (p2 + lineWidth/2 * n2)
    Point2d p2b = null; // second stretched point at p2 (p2 - lineWidth/2 * n2)

    if (line.getPointCount() <= 2) {
      p3 = p2;
      n2 = n1;
    }

    double s = 1;
    GL11.glLineWidth(2.f);
    GL11.glBegin(GL11.GL_LINES);
    // first point
    GL11.glColor3d(1, 0, 0);
    GL11.glVertex2d(p1a.x, p1a.y);
    GL11.glVertex2d(p1b.x, p1b.y);
    try {

      for (int nPoint = 0; nPoint < line.getPointCount() - 2; nPoint++) {

        p3 = line.getPoint(nPoint + 2);
        v2 = MathUtil.vector(p2, p3);
        n2 = MathUtil.computeNormal(v2);
        p2a = MathUtil.pointOfLine(p2, n2, -this.getLineWidth() / 2);
        p2b = MathUtil.pointOfLine(p2, n2, this.getLineWidth() / 2);

        GL11.glColor3d(.5, .5, .5);
        GL11.glVertex2d(p1a.x, p1a.y);
        GL11.glVertex2d(p1b.x, p1b.y);

        /*        GL11.glColor3d(.5, .5, .5);
                GL11.glVertex2d(p1.x, p1.y);
                GL11.glVertex2d(p1.x + s * v1.x, p1.y + s * v1.y);

                GL11.glColor3d(.5, .5, .5);
                GL11.glVertex2d(p1.x, p1.y);
                GL11.glVertex2d(p1.x + s * n1.x, p1.y + s * n1.y);
        */
        Point2d Ia = MathUtil.intersectionPoint(p1a, v1, p2a, v2, epsilon);
        Point2d Ib = MathUtil.intersectionPoint(p1b, v1, p2b, v2, epsilon);

        // debug
        if (first && Ia != null && Ib != null && MathUtil.norm(MathUtil.vector(Ia, Ib)) > 100) {
          first = false;
          JDialog dialog = new JDialog();
          GraphPanel graphPanel = new GraphPanel();
          dialog.getContentPane().add(graphPanel);
          dialog.pack();
          dialog.setVisible(true);

          graphPanel.addPoint("p1", p1);
          graphPanel.addPoint("p2", p2);
          graphPanel.addPoint("p3", p3);

          //          graphPanel.addVector("n1", p1, n1);
          //          graphPanel.addVector("n2", p2, n2);
          graphPanel.addVector("v1", p1, v1);
          graphPanel.addVector("v2", p1, v2);
          graphPanel.addVector(".", p1a, v1);
          graphPanel.addVector(",", p1b, v1);
          graphPanel.addVector("`", p2a, v2);
          graphPanel.addVector("'", p2b, v2);
          graphPanel.addPoint("p1a", p1a);
          graphPanel.addPoint("p1b", p1b);
          graphPanel.addPoint("p2a", p2a);
          graphPanel.addPoint("p2b", p2b);

          System.out.println("v1.v2 = " + MathUtil.cross(v1, v2));
          //          graphPanel.addPoint("Ia", Ia);
          //          graphPanel.addPoint("Ib", Ib);

          System.out.println(nPoint + " on " + line.getPointCount() + " =>");
          System.out.println(" p1   = " + p1);
          System.out.println(" p2   = " + p2);
          System.out.println(" p3   = " + p3);
          System.out.println(" n1   = " + n1);
          System.out.println(" n2   = " + n2);
          System.out.println(" v1   = " + v1);
          System.out.println(" v2   = " + v2);
          System.out.println(" p1a  = " + p1a);
          System.out.println(" p1b  = " + p1b);
          System.out.println(" p2a  = " + p2a);
          System.out.println(" p2b  = " + p2b);
          System.out.println(" Ia   = " + Ia);
          System.out.println(" Ib   = " + Ib);
          p3 = line.getPoint(nPoint + 2);
          v2 = MathUtil.vector(p2, p3);
          n2 = MathUtil.computeNormal(v2);
          p2a = MathUtil.pointOfLine(p2, n2, -this.getLineWidth() / 2);
          p2b = MathUtil.pointOfLine(p2, n2, this.getLineWidth() / 2);
          Ia = MathUtil.intersectionPoint(p1a, v1, p2a, v2, epsilon);
          Ib = MathUtil.intersectionPoint(p1b, v1, p2b, v2, epsilon);

        }
        if (Ia == null || Ib == null) {
          Ia = MathUtil.mean(p1a, p2a);
          Ib = MathUtil.mean(p1b, p2b);
        }
        // if no intersection, use p2a & p2b
        if (Ia == null) {
          GL11.glColor3d(.5, .5, .5);
          GL11.glVertex2d(p2a.x, p2b.y);
        } else {
          GL11.glColor3d(0, 0, 1);
          GL11.glVertex2d(Ia.x, Ia.y);
        }
        if (Ib == null) {
          GL11.glColor3d(.5, .5, .5);
          GL11.glVertex2d(p2b.x, p2b.y);
        } else {
          GL11.glColor3d(0, 1, 0);
          GL11.glVertex2d(Ib.x, Ib.y);
        }
        // shift context to the next point
        p1 = p2;
        n1 = n2;
        v1 = v2;
        p1a = p2a;
        p1b = p2b;
        p2 = p3;

      }
      if (p3 == null || n2 == null) {
        System.err.println("p3 = " + p3 + " n2 = " + n2 + " nbpt = " + line.getPointCount());
      }
      // draw the last point
      Point2d p3a = MathUtil.pointOfLine(p3, n2, -this.getLineWidth() / 2);
      Point2d p3b = MathUtil.pointOfLine(p3, n2, this.getLineWidth() / 2);
      GL11.glColor3d(1, 1, 0);
      GL11.glVertex2d(p3a.x, p3a.y);
      GL11.glVertex2d(p3b.x, p3b.y);
    } finally {
      GL11.glEnd();
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
