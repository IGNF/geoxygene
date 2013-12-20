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

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
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

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.TessCallback;

/**
 * @author JeT
 *         This renderer writes GL Code to perform GL rendering
 */
public class GLPrimitiveRenderer extends AbstractPrimitiveRenderer {

    private static Logger logger = Logger.getLogger(GLPrimitiveRenderer.class.getName());

    private int textureId = -1;
    private String textureFilename = null;
    private Color backgroundColor = Color.white;
    private Color foregroundColor = Color.black;
    private float lineWidth = 2.f;
    private float pointWidth = 2.f;

    /**
     * Constructor
     */
    public GLPrimitiveRenderer() {

    }

    /**
     * Constructor
     * 
     * @param backgroundColor
     * @param foregroundColor
     */
    public GLPrimitiveRenderer(final Color backgroundColor, final Color foregroundColor) {
        super();
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
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
     * @return the lineWidth
     */
    public float getLineWidth() {
        return this.lineWidth;
    }

    /**
     * @param lineWidth
     *            the lineWidth to set
     */
    public void setLineWidth(final float lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * @return the pointWidth
     */
    public float getPointWidth() {
        return this.pointWidth;
    }

    /**
     * @param pointWidth
     *            the pointWidth to set
     */
    public void setPointWidth(final float pointWidth) {
        this.pointWidth = pointWidth;
    }

    /**
     * @return the backgroundColor
     */
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    /**
     * @param backgroundColor
     *            the backgroundColor to set
     */
    public void setBackgroundColor(final Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * @return the foregroundColor
     */
    public Color getForegroundColor() {
        return this.foregroundColor;
    }

    /**
     * @param foregroundColor
     *            the foregroundColor to set
     */
    public void setForegroundColor(final Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    /**
     * @return the textureFilename
     */
    public String getTextureFilename() {
        return this.textureFilename;
    }

    /**
     * @param textureFilename
     *            the textureFilename to set
     */
    public void setTextureFilename(final String textureFilename) {
        this.textureFilename = textureFilename;
        this.textureId = -1;
    }

    /*
     * (non-Javadoc)
     * 
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
     * 
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
            this.renderSurface(viewport, (ParameterizedPolygon) primitive, this.getTextureId());
            return;
        }
        logger.warn(this.getClass().getSimpleName() + " do not know how to paint primitives " + primitive.getClass().getSimpleName());
    }

    /**
     * Render simple line
     * 
     * @param primitive
     *            primitive to paint
     */
    private void renderLine(final ParameterizedPolyline line) {
        GLTools.glColor(this.getForegroundColor());
        glLineWidth(this.getLineWidth());
        glBegin(GL_LINE_STRIP);
        for (int n = 0; n < line.getPointCount(); n++) {
            Point2d p2 = line.getPoint(n);
            glVertex2d(p2.x, p2.y);
            //      System.err.println("point #" + n + " = " + p2);
        }
        glEnd();
    }

    /**
     * Draw a filled shape with open GL
     */
    private void renderSurface(final Viewport viewport, final ParameterizedPolygon polygon, final Integer texIndex) {

        // TODO: [JeT] Those lines should not be here. parameterization should be part of the SLD description
        // As I don't already have modified the SLD, I just had to find a place where to compute parameterization
        // comparable lines can be found in DensityFieldPrimitiveRenderer

        // TODO: create a hashmap to avoid recomputing texture coordinates each time
        WorldCoordinatesParameterizer parameterizer = new WorldCoordinatesParameterizer(viewport);
        polygon.generateParameterization(parameterizer);

        if (texIndex != null && texIndex > 0) {
            GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, texIndex);
        } else {
            GLTools.glColor(this.getBackgroundColor());
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

        for (int innerFrontierIndex = 0; innerFrontierIndex < polygon.getInnerFrontierCount(); innerFrontierIndex++) {

            List<Point2d> innerFrontier = polygon.getInnerFrontier(innerFrontierIndex);
            tesselator.gluTessBeginContour();

            for (int innerFrontierPointIndex = 0; innerFrontierPointIndex < innerFrontier.size(); innerFrontierPointIndex++) {
                Point2d innerFrontierPoint = innerFrontier.get(innerFrontierPointIndex);
                Point2d innerFrontierTextureCoordinates = polygon.getInnerFrontierTextureCoordinates(innerFrontierIndex, innerFrontierPointIndex);

                double[] coords = new double[6];
                // point coordinates
                coords[0] = innerFrontierPoint.x;
                coords[1] = innerFrontierPoint.y;
                coords[2] = 0;
                // texture coordinates
                coords[3] = texCoord ? innerFrontierTextureCoordinates.x / 100. : innerFrontierPoint.x;
                coords[4] = texCoord ? innerFrontierTextureCoordinates.y / 100. : innerFrontierPoint.y;
                //        coords[3] = texCoord ? innerFrontierTextureCoordinates.x / 1000. : innerFrontierPoint.x;
                //        coords[4] = texCoord ? innerFrontierTextureCoordinates.y / 1000. : innerFrontierPoint.y;
                coords[5] = 0;
                tesselator.gluTessVertex(coords, 0, coords);
            }

            tesselator.gluTessEndContour();
        }

        tesselator.gluTessEndPolygon();

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
