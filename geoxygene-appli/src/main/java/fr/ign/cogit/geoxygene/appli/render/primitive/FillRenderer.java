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

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2d;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.util.glu.GLU.GLU_TESS_BEGIN;
import static org.lwjgl.util.glu.GLU.GLU_TESS_COMBINE;
import static org.lwjgl.util.glu.GLU.GLU_TESS_END;
import static org.lwjgl.util.glu.GLU.GLU_TESS_VERTEX;
import static org.lwjgl.util.glu.GLU.gluNewTess;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.GLUtessellator;
import org.lwjgl.util.glu.GLUtessellatorCallbackAdapter;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLTools;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;

/**
 * @author JeT
 * This renderer fills polygons with a texture or a solid color
 */
public class FillRenderer extends AbstractPrimitiveRenderer {

  private static Logger logger = Logger.getLogger(FillRenderer.class.getName());

  private int textureId = -1;
  private String textureFilename = null;
  private Color fillColor = Color.white;

  /**
   * Constructor
   */
  public FillRenderer() {

  }

  /**
   * Constructor
   * @param fillColor
   * @param foregroundColor
   */
  public FillRenderer(final Color fillColor) {
    super();
    this.fillColor = fillColor;
  }

  private Integer getTextureId() {
    if (this.textureFilename == null) {
      return null;
    }
    if (this.textureId < 0) {
      try {
        this.textureId = GLTools.loadTexture(this.textureFilename);
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
    return this.textureId;
  }

  /**
   * @return the fillColor
   */
  public Color getFillColor() {
    return this.fillColor;
  }

  /**
   * @param fillColor the fill color to set
   */
  public void setFillColor(final Color fillColor) {
    this.fillColor = fillColor;
  }

  /**
   * @return the textureFilename
   */
  public String getTextureFilename() {
    return this.textureFilename;
  }

  /**
   * @param textureFilename the textureFilename to set
   */
  public void setTextureFilename(final String textureFilename) {
    this.textureFilename = textureFilename;
    this.textureId = -1;
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
      this.renderSurface(viewport, new ParameterizedPolygon((ParameterizedPolyline) primitive), this.getTextureId());
      // this renderer converts lines to polygons
      return;
    } else if (primitive instanceof ParameterizedPolygon) {
      this.renderSurface(viewport, (ParameterizedPolygon) primitive, this.getTextureId());
      return;
    }
    logger.warn(this.getClass().getSimpleName() + " do not know how to paint primitives " + primitive.getClass().getSimpleName());
  }

  /**
   * Draw a filled shape with open GL
   */
  private void renderSurface(final Viewport viewport, final ParameterizedPolygon polygon, final Integer texIndex) {

    if (texIndex != null && texIndex > 0) {
      GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
      glEnable(GL_TEXTURE_2D);
      glBindTexture(GL_TEXTURE_2D, texIndex);
    } else {
      GLTools.glColor(this.getFillColor());
      GL11.glDisable(GL_TEXTURE_2D);
    }
    boolean texCoord = polygon.hasTextureCoordinates();

    // tesselation
    GLUtessellator tesselator = gluNewTess();
    // Set callback functions
    TessCallback callback = new TessCallback();
    tesselator.gluTessCallback(GLU_TESS_VERTEX, callback);
    tesselator.gluTessCallback(GLU_TESS_BEGIN, callback);
    tesselator.gluTessCallback(GLU_TESS_END, callback);
    tesselator.gluTessCallback(GLU_TESS_COMBINE, callback);
    tesselator.gluTessProperty(GLU.GLU_TESS_WINDING_RULE, GLU.GLU_TESS_WINDING_POSITIVE);

    tesselator.gluTessBeginPolygon(null);

    // outer frontier
    tesselator.gluTessBeginContour();
    for (int outerFrontierPointIndex = 0; outerFrontierPointIndex < polygon.getOuterFrontier().size(); outerFrontierPointIndex++) {
      Point2d outerFrontierPoint = polygon.getOuterFrontier().get(outerFrontierPointIndex);
      Point2d outerFrontierTextureCoordinates = polygon.getOuterFrontierTextureCoordinates(outerFrontierPointIndex);
      // point coordinates
      double coords[] = new double[6];
      coords[0] = outerFrontierPoint.x;
      coords[1] = outerFrontierPoint.y;
      coords[2] = 0;
      // texture coordinates
      coords[3] = texCoord ? outerFrontierTextureCoordinates.x / 100. : outerFrontierPoint.x;
      coords[4] = texCoord ? outerFrontierTextureCoordinates.y / 100. : outerFrontierPoint.y;
      coords[5] = 0;
      //      System.err.println("------------------------------------------------ " + outerFrontierTextureCoordinates);
      tesselator.gluTessVertex(coords, 0, coords);
    }
    tesselator.gluTessEndContour();

    //    for (int innerFrontierIndex = 0; innerFrontierIndex < polygon.getInnerFrontierCount(); innerFrontierIndex++) {
    //
    //      List<Point2d> innerFrontier = polygon.getInnerFrontier(innerFrontierIndex);
    //      tesselator.gluTessBeginContour();
    //
    //      for (int innerFrontierPointIndex = 0; innerFrontierPointIndex < innerFrontier.size(); innerFrontierPointIndex++) {
    //        Point2d innerFrontierPoint = innerFrontier.get(innerFrontierPointIndex);
    //        Point2d innerFrontierTextureCoordinates = polygon.getInnerFrontierTextureCoordinates(innerFrontierIndex, innerFrontierPointIndex);
    //
    //        double[] coords = new double[6];
    //        // point coordinates
    //        coords[0] = innerFrontierPoint.x;
    //        coords[1] = innerFrontierPoint.y;
    //        coords[2] = 0;
    //        // texture coordinates
    //        coords[3] = texCoord ? innerFrontierTextureCoordinates.x / 100. : innerFrontierPoint.x;
    //        coords[4] = texCoord ? innerFrontierTextureCoordinates.y / 100. : innerFrontierPoint.y;
    //        //        coords[3] = texCoord ? innerFrontierTextureCoordinates.x / 1000. : innerFrontierPoint.x;
    //        //        coords[4] = texCoord ? innerFrontierTextureCoordinates.y / 1000. : innerFrontierPoint.y;
    //        coords[5] = 0;
    //        tesselator.gluTessVertex(coords, 0, coords);
    //      }
    //
    //      tesselator.gluTessEndContour();
    //    }

    tesselator.gluTessEndPolygon();

  }

  /**
   * Callback class used in gl tesselation process
   * @author JeT
   *
   */
  private static class TessCallback extends GLUtessellatorCallbackAdapter {
    @Override
    public void edgeFlag(final boolean boundaryEdge) {
      //      System.err.println("edgeFlag " + boundaryEdge);
      super.edgeFlag(boundaryEdge);
    }

    @Override
    public void combine(final double[] coords, final Object[] data, final float[] weight, final Object[] outData) {
      double[] vertex = new double[6];
      vertex[0] = coords[0];
      vertex[1] = coords[1];
      vertex[2] = coords[2];
      for (int i = 3; i < 6; i++) {
        int nb = 0;
        vertex[i] = 0;
        for (int j = 0; j < data.length; j++) {
          if (data[j] != null) {
            vertex[i] += weight[j] * ((double[]) data[j])[i];
            nb++;
          }
        }
        if (nb != 0) {
          vertex[i] /= nb;
        }
      }
      outData[0] = vertex;
    }

    @Override
    public void beginData(final int type, final Object polygonData) {
      //      System.err.println("beginData " + type + " " + polygonData);
      super.beginData(type, polygonData);
    }

    @Override
    public void edgeFlagData(final boolean boundaryEdge, final Object polygonData) {
      //      System.err.println("edgeFlagData " + boundaryEdge + " " + polygonData);
      super.edgeFlagData(boundaryEdge, polygonData);
    }

    @Override
    public void vertexData(final Object vertexData, final Object polygonData) {
      //      System.err.println("vertexData " + vertexData + " " + polygonData);
      super.vertexData(vertexData, polygonData);
    }

    @Override
    public void endData(final Object polygonData) {
      //      System.err.println("endData " + polygonData);
      super.endData(polygonData);
    }

    @Override
    public void combineData(final double[] coords, final Object[] data, final float[] weight, final Object[] outData, final Object polygonData) {
      glTexCoord2d((Double) data[3], (Double) data[4]);
      glVertex2d(coords[0], coords[1]);
    }

    @Override
    public void begin(final int type) {
      //      System.err.println("begin " + type);
      //      glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
      glBegin(type);
    }

    @Override
    public void end() {
      //      System.err.println("end");
      glEnd();
      //      glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    @Override
    public void vertex(final Object coords) {
      double[] vertex = (double[]) coords;
      //      System.err.println("vertex " + vertex + " size = " + vertex.length);
      //      System.out.println("vertex tex : " + ((double[]) coords)[3] + "x" + ((double[]) coords)[4]);
      glTexCoord2d(vertex[3], vertex[4]);
      glVertex2d(vertex[0], vertex[1]);
    }

    @Override
    public void error(final int errnum) {
      String estring;
      estring = GLU.gluErrorString(errnum);
      logger.error("Tessellation Error Number: " + errnum);
      logger.error("Tessellation Error: " + estring);
      super.error(errnum);
    }

    @Override
    public void errorData(final int errnum, final Object polygonData) {
      logger.error("Tesselation error : " + errnum + " + " + polygonData.toString());
      super.errorData(errnum, polygonData);
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
