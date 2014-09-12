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

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.Util;

import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLContext;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.RenderingException;
import fr.ign.cogit.geoxygene.util.gl.RenderingStatistics;

/**
 * @author JeT Viewport is contained into the LwjglLayerRenderer
 */
public abstract class AbstractGeoxComplexRenderer implements
        GeoxComplexRenderer {

    private final static Logger logger = Logger
            .getLogger(GeoxComplexRenderer.class.getName());

    private Color backgroundColor = Color.white;
    private Color foregroundColor = Color.black;
    private float lineWidth = 2.f;
    private float pointWidth = 2.f;

    private boolean needInitialization = true;

    // Uniform Variables

    // private final int screenHeightLocation = -1; // GLSL

    // REF1
    // private final Map<IFeature, GLDisplayable> displayables = new
    // HashMap<IFeature, GLDisplayable>();
    private LwjglLayerRenderer lwjglLayerRenderer = null;
    private boolean fboRendering; // true if fbo rendering is in progress

    // private Viewport viewport = null;

    /**
     * Constructor
     * 
     * @param lwjglLayerRenderer
     */
    public AbstractGeoxComplexRenderer(LwjglLayerRenderer lwjglLayerRenderer) {
        if (lwjglLayerRenderer == null) {
            throw new IllegalArgumentException("layer renderer cannot be null");
        }
        this.lwjglLayerRenderer = lwjglLayerRenderer;
    }

    /**
     * @return the glContext
     * @throws GLException
     */
    public final GLContext getGlContext() throws GLException {
        return this.lwjglLayerRenderer.getGLContext();
    }

    // /**
    // * Constructor
    // *
    // * @param backgroundColor
    // * @param foregroundColor
    // */
    // public final GL4FeatureRenderer(LwjglLayerRenderer lwjglLayerRenderer,
    // final
    // Color backgroundColor, final Color foregroundColor) {
    // this(lwjglLayerRenderer);
    // this.backgroundColor = backgroundColor;
    // this.foregroundColor = foregroundColor;
    // }
    //
    /**
     * @return the lwjglLayerRenderer
     */
    public final LwjglLayerRenderer getLayerRenderer() {
        return this.lwjglLayerRenderer;
    }

    /**
     * @return the lwjglLayerRenderer
     */
    public final LayerViewGLPanel getLayerViewPanel() {
        return this.lwjglLayerRenderer.getLayerViewPanel();
    }

    /**
     * @return the needInitialization
     */
    public final boolean needInitialization() {
        return this.needInitialization;
    }

    /**
     * @return the lineWidth
     */
    public final float getLineWidth() {
        return this.lineWidth;
    }

    /**
     * @param lineWidth
     *            the lineWidth to set
     */
    public final void setLineWidth(final float lineWidth) {
        this.lineWidth = lineWidth;
    }

    /**
     * @return the pointWidth
     */
    public final float getPointWidth() {
        return this.pointWidth;
    }

    /**
     * @param pointWidth
     *            the pointWidth to set
     */
    public final void setPointWidth(final float pointWidth) {
        this.pointWidth = pointWidth;
    }

    /**
     * @return the backgroundColor
     */
    public final Color getBackgroundColor() {
        return this.backgroundColor;
    }

    /**
     * @param backgroundColor
     *            the backgroundColor to set
     */
    public final void setBackgroundColor(final Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * @return the foregroundColor
     */
    public final Color getForegroundColor() {
        return this.foregroundColor;
    }

    /**
     * @param foregroundColor
     *            the foregroundColor to set
     */
    public final void setForegroundColor(final Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    /**
     * Render one Feature
     */
    @Override
    public final void render(GLComplex complex, double opacity)
            throws RenderingException {
        // System.err.println("rendering feature " + feature.getId() +
        // " part of "
        // + feature.getFeatureCollections().size()
        // + " feature collections");
        GLTools.glCheckError("gl error ocurred before main render method");
        try {
            if (this.getGlContext() == null) {
                logger.error("no GL Context defined");
                return;
            }
        } catch (GLException e1) {
            logger.error("GL Context exception thrown: " + e1.getMessage());
            e1.printStackTrace();
        }
        // this.getGlContext().checkContext();
        if (this.needInitialization()) {
            this.initializeRendering();
            GLTools.glCheckError("gl error ocurred after rendering initialization");
        }

        double layerOpacity = this.getLayerRenderer().getLayer().getOpacity();
        try {
            GLTools.glCheckError("gl error before rendering");
            this.localRendering(complex, complex.getOverallOpacity()
                    * layerOpacity);
            GLTools.glCheckError("gl error after rendering");
        } catch (GLException e) {
            throw new RenderingException(e);
        }

        this.finalizeRendering();
        if (!GLTools.glCheckError("gl error ocurred during rendering")) {
            throw new RenderingException(Util.translateGLErrorString(GL11
                    .glGetError()));
        }
        // this.checkCurrentProgram("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ render()");
    }

    protected abstract void localRendering(GLComplex primitive, double opacity)
            throws RenderingException, GLException;

    public final int getCanvasWidth() {
        if (this.lwjglLayerRenderer != null
                && this.lwjglLayerRenderer.getLayerViewPanel() != null) {
            return this.lwjglLayerRenderer.getLayerViewPanel().getWidth();
        }
        return 0;
    }

    public final int getCanvasHeight() {
        if (this.lwjglLayerRenderer != null
                && this.lwjglLayerRenderer.getLayerViewPanel() != null) {
            return this.lwjglLayerRenderer.getLayerViewPanel().getHeight();
        }
        return 0;
    }

    @Override
    public boolean getFBORendering() {
        return this.fboRendering;
    }

    /**
     * @param fboRendering
     *            the fboRendering to set
     */
    @Override
    public final void setFBORendering(boolean fboRendering) {
        this.fboRendering = fboRendering;
    }

    // /**
    // * @param primitive
    // * @throws GLException
    // */
    // private final void wireframeRendering(GLComplex primitive, float
    // lineWidth,
    // float pointSize) throws GLException {
    // this.setGLViewMatrix(this.getViewport(), primitive.getMinX(),
    // primitive.getMinY());
    // GL30.glBindVertexArray(primitive.getVaoId());
    // glDisable(GL_TEXTURE_2D); // if not set to disable, line smoothing won't
    // // work
    // GL11.glLineWidth(lineWidth);
    // GL11.glPointSize(pointSize);
    // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
    // this.drawComplex(primitive);
    // GL30.glBindVertexArray(0);
    // }
    //

    /**
     * do a GL draw call for all complex meshes
     * 
     * @param primitive
     *            primitive to render
     */
    protected final void drawComplex(GLComplex primitive) {
        RenderingStatistics.drawGLComplex(primitive);
        for (GLMesh mesh : primitive.getMeshes()) {
            RenderingStatistics.doDrawCall();
            // System.err.println("draw call for mesh " + mesh +
            // " indices from "
            // + mesh.getFirstIndex() + " to " + mesh.getLastIndex());
            GL11.glDrawElements(mesh.getGlType(),
                    mesh.getLastIndex() - mesh.getFirstIndex() + 1,
                    GL11.GL_UNSIGNED_INT, mesh.getFirstIndex()
                            * (Integer.SIZE / 8));
        }
    }

    //
    @Override
    public void initializeRendering() throws RenderingException {
        this.initShader();
    }

    @Override
    public void finalizeRendering() throws RenderingException {
        // nothing to finalize
    }

    public final void initShader() {
    }

    @Override
    public final void reset() {
        this.needInitialization = true;
    }

}
