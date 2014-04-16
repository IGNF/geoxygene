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
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventManager;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;
import fr.ign.cogit.geoxygene.appli.task.Task;
import fr.ign.cogit.geoxygene.appli.task.TaskListener;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLContext;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.GLVertex;
import fr.ign.cogit.geoxygene.util.gl.Texture;

/**
 * @author JeT This renderer writes GL Code to perform GL rendering
 */
public class GL4FeatureRenderer extends AbstractFeatureRenderer implements
        TaskListener {

    private static final float DEFAULT_POINT_SIZE = 3f;
    private static final float DEFAULT_LINE_WIDTH = 1f;

    private static Logger logger = Logger.getLogger(GL4FeatureRenderer.class
            .getName());

    private Color backgroundColor = Color.white;
    private Color foregroundColor = Color.black;
    private float lineWidth = 2.f;
    private float pointWidth = 2.f;

    private boolean needInitialization = true;

    private GLContext glContext = null;

    // Uniform Variables

    public static final int COLORTEXTURE1_SLOT = 0;
    public static final int DMAPTEXTURE_SLOT = 4;

    // private int screenHeightLocation = -1; // GLSL

    private final Map<IFeature, GLDisplayable> displayables = new HashMap<IFeature, GLDisplayable>();
    private LwjglLayerRenderer lwjglLayerRenderer = null;
    private GLComplex screenQuad = null;
    private int fboId = -1;
    private int fboTextureId = -1;
    private int fboImageWidth = -1;
    private int fboImageHeight = -1;

    /**
     * Constructor
     * 
     * @param lwjglLayerRenderer
     */
    public GL4FeatureRenderer(LwjglLayerRenderer lwjglLayerRenderer,
            GLContext glContext) {
        if (lwjglLayerRenderer == null) {
            throw new IllegalArgumentException("layer renderer cannot be null");
        }
        if (glContext == null) {
            throw new IllegalArgumentException("gl Context cannot be null");
        }
        this.lwjglLayerRenderer = lwjglLayerRenderer;
        this.glContext = glContext;
    }

    /**
     * 
     */
    private void initializeScreenQuad() {
        this.screenQuad = new GLComplex(0f, 0f);
        GLMesh mesh = this.screenQuad.addGLMesh(GL11.GL_QUADS);
        mesh.addIndex(this.screenQuad.addVertex(new GLVertex(
                new Point2D.Double(-1, -1), new Point2D.Double(0, 0))));
        mesh.addIndex(this.screenQuad.addVertex(new GLVertex(
                new Point2D.Double(-1, 1), new Point2D.Double(0, 1))));
        mesh.addIndex(this.screenQuad.addVertex(new GLVertex(
                new Point2D.Double(1, 1), new Point2D.Double(1, 1))));
        mesh.addIndex(this.screenQuad.addVertex(new GLVertex(
                new Point2D.Double(1, -1), new Point2D.Double(1, 0))));
        this.screenQuad.setColor(Color.blue);
        this.screenQuad.setOverallOpacity(0.5);
    }

    /**
     * @return the screenQuad
     */
    public GLComplex getScreenQuad() {
        if (this.screenQuad == null) {
            this.initializeScreenQuad();
        }
        return this.screenQuad;
    }

    // /**
    // * Constructor
    // *
    // * @param backgroundColor
    // * @param foregroundColor
    // */
    // public GL4FeatureRenderer(LwjglLayerRenderer lwjglLayerRenderer, final
    // Color backgroundColor, final Color foregroundColor) {
    // this(lwjglLayerRenderer);
    // this.backgroundColor = backgroundColor;
    // this.foregroundColor = foregroundColor;
    // }
    //
    /**
     * @return the lwjglLayerRenderer
     */
    public LwjglLayerRenderer getLayerRenderer() {
        return this.lwjglLayerRenderer;
    }

    /**
     * @return the lwjglLayerRenderer
     */
    public LayerViewGLPanel getLayerViewPanel() {
        return this.lwjglLayerRenderer.getLayerViewPanel();
    }

    /**
     * @return the needInitialization
     */
    public boolean needInitialization() {
        return this.needInitialization;
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
     * Render one Feature
     */
    @Override
    public void render(final IFeature feature, final Layer layer,
            final Symbolizer symbolizer, final Viewport viewport)
            throws RenderingException {
        // this.checkCurrentProgram("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv render()");
        if (this.glContext == null) {
            logger.error("no GL Context defined");
            return;
        }
        // this.glContext.checkContext();
        this.setViewport(viewport);
        if (this.needInitialization()) {
            this.initializeRendering();
        }

        double layerOpacity = layer.getOpacity();
        synchronized (this.displayables) {
            // try to retrieve previously generated geometry matching the given
            // view environment
            GLDisplayable displayable = this.getDisplayable(feature);
            if (displayable != null) {
                try {
                    this.renderDisplayable(displayable, layerOpacity);
                } catch (GLException e) {
                    throw new RenderingException(e);
                }
                return;
            } else {

            }

            IGeometry geometry = feature.getGeom();
            if (geometry == null) {
                logger.warn("null geometry for feature " + feature.getId());
                return;
            } else if (geometry.isPolygon()) {
                logger.warn("polygon geometry for feature " + feature.getId());
                DisplayableSurface displayablePolygon = new DisplayableSurface(
                        layer.getName() + "-polygon #" + feature.getId(),
                        viewport, (IPolygon) geometry, feature, symbolizer);
                displayable = displayablePolygon;
            } else if (geometry.isMultiSurface()) {
                DisplayableSurface displayablePolygon = new DisplayableSurface(
                        layer.getName() + "-multisurface #" + feature.getId(),
                        viewport, (IMultiSurface<?>) geometry, feature,
                        symbolizer);
                displayable = displayablePolygon;
            } else if (geometry.isMultiCurve()) {
                DisplayableCurve displayableCurve = new DisplayableCurve(
                        layer.getName() + "-multicurve #" + feature.getId(),
                        viewport, (IMultiCurve<?>) geometry, symbolizer);
                displayable = displayableCurve;
            } else if (geometry.isPoint() || (geometry instanceof IMultiPoint)) {
                DisplayablePoint displayablePoint = new DisplayablePoint(
                        layer.getName() + "-multipoint #" + feature.getId(),
                        viewport, geometry, symbolizer);
                displayable = displayablePoint;
            } else {
                logger.warn("GL4FeatureRenderer cannot handle geometry type "
                        + geometry.getClass().getSimpleName());
            }
            if (displayable != null) {
                this.addDisplayable(feature, displayable); // stores generated
                                                           // geometry
                try {
                    this.renderDisplayable(displayable, layerOpacity);
                } catch (GLException e) {
                    throw new RenderingException(e);
                }
            } else {
                logger.warn(this.getClass().getSimpleName()
                        + " do not know how to render feature "
                        + feature.getGeom().getClass().getSimpleName());
            }
        }

        // this.checkCurrentProgram("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ render()");
    }

    private void addDisplayable(IFeature feature, GLDisplayable displayable) {
        this.displayables.put(feature, displayable);
        // task is automatically started when added to the manager
        GeOxygeneEventManager.getInstance().getApplication().getTaskManager()
                .addTask(displayable);
        displayable.addTaskListener(this);
    }

    private void removeDisplayable(IFeature feature) {
        GLDisplayable displayable = this.displayables.remove(feature);
        if (displayable != null) {
            displayable.removeTaskListener(this);
        }
    }

    /**
     * Render a list of primitives
     * 
     * @param complexes
     * @param viewport
     * @throws GLException
     */
    private void renderGLPrimitive(Collection<GLComplex> complexes,
            double opacity) throws GLException {
        for (GLComplex complex : complexes) {
            this.renderGLPrimitive(complex, opacity);
        }

    }

    private GLDisplayable getDisplayable(IFeature feature) {
        return this.displayables.get(feature);
    }

    /**
     * Draw a filled shape with open GL
     * 
     * @throws GLException
     */
    private void renderGLPrimitive(GLComplex primitive, double opacity)
            throws GLException {
        // glEnableVertexAttribArray(COLOR_ATTRIBUTE_ID);
        if (this.getLayerViewPanel().useWireframe()) {
            this.glContext
                    .setCurrentProgram(LwjglLayerRenderer.worldspaceColorProgramName);

            glEnable(GL_BLEND);
            if (this.getLayerViewPanel().useAntialiasing()) {
                glEnable(GL_LINE_SMOOTH);
                glEnable(GL11.GL_POINT_SMOOTH);
                glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
                glHint(GL11.GL_POINT_SMOOTH_HINT, GL_NICEST);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            } else {
                glDisable(GL_LINE_SMOOTH);
                glDisable(GL11.GL_POINT_SMOOTH);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            }
            this.wireframeRendering(primitive, DEFAULT_LINE_WIDTH,
                    DEFAULT_POINT_SIZE);
        } else {
            this.fboRendering(primitive, opacity);
            // if (this.getLayerViewPanel().useFBO() && primitive.mayOverlap())
            // {
            // System.err.println("draw primitive with "
            // + primitive.getMeshes().size()
            // + " meshes using FBO rendering");
            // this.fboRendering(primitive, opacity);
            // } else {
            // System.err.println("draw primitive with "
            // + primitive.getMeshes().size()
            // + " meshes using normal rendering");
            // this.normalRendering(primitive, opacity);
            // }
        }

    }

    /**
     * 
     */
    // private void checkCurrentProgram(String message) {
    // return;
    // if (this.getCurrentProgramId() == -1) {
    // return;
    // }
    // System.err.println("Check " + message + " app: " +
    // this.getCurrentProgramId() + " gl: " +
    // GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM));
    // if (this.getCurrentProgramId() !=
    // GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM)) {
    // System.err.println("C'est quoi c'te merde !!!! " + message + " app: "
    // + this.getCurrentProgramId() + " gl: "
    // + GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM));
    // }
    // }

    /**
     * @return the currentProgramId
     */
    public GLProgram getCurrentProgram() {
        return this.glContext.getCurrentProgram();
    }

    /**
     * @return the currentProgramId
     * @throws GLException
     */
    public int getCurrentProgramId() throws GLException {
        return this.glContext.getCurrentProgram() == null ? -1 : this.glContext
                .getCurrentProgram().getProgramId();
    }

    private int getCanvasWidth() {
        if (this.lwjglLayerRenderer != null
                && this.lwjglLayerRenderer.getLayerViewPanel() != null) {
            return this.lwjglLayerRenderer.getLayerViewPanel().getWidth();
        }
        return 0;
    }

    private int getCanvasHeight() {
        if (this.lwjglLayerRenderer != null
                && this.lwjglLayerRenderer.getLayerViewPanel() != null) {
            return this.lwjglLayerRenderer.getLayerViewPanel().getHeight();
        }
        return 0;
    }

    /**
     * Draw a Primitive using FBOs
     * 
     * @param primitive
     * @throws GLException
     */
    private void fboRendering(GLComplex primitive, double opacity)
            throws GLException {
        // render primitive in a FBO (offscreen rendering)
        GLTools.glCheckError("entering FBO rendering");
        // bind a read-only framebuffer
        glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.fboId);

        // check if the screen size has change since previous rendering
        if (this.fboImageWidth != this.getCanvasWidth()
                || this.fboImageHeight != this.getCanvasHeight()) {
            this.fboImageWidth = this.getCanvasWidth();
            this.fboImageHeight = this.getCanvasHeight();
            GL11.glViewport(0, 0, this.fboImageWidth, this.fboImageHeight);

            glBindTexture(GL_TEXTURE_2D, this.fboTextureId);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.fboImageWidth,
                    this.fboImageHeight, 0, GL11.GL_RGBA,
                    GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        }

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                GL_TEXTURE_2D, this.fboTextureId, 0);
        // check FBO status

        int status = GL30.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            throw new GLException(
                    "Frame Buffer Object is not correctly initialized");
        }
        GLTools.glCheckError("FBO initialization");

        GL11.glDepthMask(false);
        glDisable(GL11.GL_DEPTH_TEST);

        // }
        //
        // // first draw the outline with smooth blending to get the polygon
        // border
        // // smoothness
        this.glContext
                .setCurrentProgram(LwjglLayerRenderer.worldspaceColorProgramName);
        // no transparency at all
        this.glContext.getCurrentProgram().setUniform1f(
                LwjglLayerRenderer.objectOpacityUniformVarName, 1f);
        this.glContext.getCurrentProgram().setUniform1f(
                LwjglLayerRenderer.globalOpacityUniformVarName, 1f);

        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        glEnable(GL_TEXTURE_2D);
        GL11.glDrawBuffer(GL_COLOR_ATTACHMENT0);

        GL11.glClearColor(0.5f, 0.5f, 0.5f, 0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        if (this.getLayerViewPanel().useAntialiasing()) {
            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            // 1.2 line width is a good value to be close to AWT antialiasing
            this.wireframeRendering(primitive, 1.2f, 1.f);
            GLTools.glCheckError("FBO Antialiasing wireframe rendering");
        }
        // then draw the polygon with no antialiasing and no blending
        glDisable(GL11.GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL11.GL_POLYGON_SMOOTH);
        // glBlendFunc(GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // GL14.glBlendFuncSeparate(GL11.GL_SRC_COLOR, GL11.GL_ONE,
        // GL11.GL_CONSTANT_COLOR, GL11.GL_ONE);
        this.normalRendering(primitive, 1f);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        GLTools.glCheckError("FBO plain rendering");

        // display the computed texture on the screen
        // using object opacity * overall opacity
        this.glContext
                .setCurrentProgram(LwjglLayerRenderer.screenspaceTextureProgramName);
        GL11.glViewport(0, 0, this.fboImageWidth, this.fboImageHeight);
        GL11.glDrawBuffer(GL11.GL_BACK);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL11.GL_POLYGON_SMOOTH);
        this.glContext.getCurrentProgram().setUniform1i(
                LwjglLayerRenderer.colorTexture1UniformVarName,
                COLORTEXTURE1_SLOT);
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + COLORTEXTURE1_SLOT);
        glBindTexture(GL_TEXTURE_2D, this.fboTextureId);

        GL11.glDepthMask(false);
        glDisable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(this.getScreenQuad().getVaoId());
        // this.glContext.getCurrentProgram().setUniform1f(
        // LwjglLayerRenderer.objectOpacityUniformVarName,
        // (float) primitive.getOverallOpacity());
        // this.glContext.getCurrentProgram()
        // .setUniform1f(LwjglLayerRenderer.globalOpacityUniformVarName,
        // (float) opacity);
        this.glContext.getCurrentProgram().setUniform1f(
                LwjglLayerRenderer.objectOpacityUniformVarName,
                (float) primitive.getOverallOpacity());
        this.glContext.getCurrentProgram()
                .setUniform1f(LwjglLayerRenderer.globalOpacityUniformVarName,
                        (float) opacity);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GLTools.glCheckError("before FBO drawing textured quad");
        this.drawComplex(this.getScreenQuad());
        this.getScreenQuad().setColor(new Color(1f, 0f, 1f, .5f));
        GLTools.glCheckError("FBO drawing textured quad");
        glBindTexture(GL_TEXTURE_2D, 0); // unbind texture
        GL30.glBindVertexArray(0); // unbind VAO
        GLTools.glCheckError("exiting FBO rendering");
    }

    /**
     * Render a polygon using color, texture or wireframe GL_BLEND has to be set
     * before this rendering method. It does not use FBOs
     * 
     * @param primitive
     * @throws GLException
     */
    private void normalRendering(GLComplex primitive, double opacity)
            throws GLException {

        switch (primitive.getRenderingCapability()) {
        case TEXTURE:
            this.glContext
                    .setCurrentProgram(LwjglLayerRenderer.worldspaceTextureProgramName);
            break;
        case POSITION:
        case COLOR:
            this.glContext
                    .setCurrentProgram(LwjglLayerRenderer.worldspaceColorProgramName);
            break;
        default:
            logger.warn("Rendering capability "
                    + primitive.getRenderingCapability()
                    + " is not handled by " + this.getClass().getSimpleName());
            break;
        }
        this.glContext.getCurrentProgram().setUniform1f(
                LwjglLayerRenderer.objectOpacityUniformVarName, 1f);
        this.glContext.getCurrentProgram()
                .setUniform1f(LwjglLayerRenderer.globalOpacityUniformVarName,
                        (float) opacity);
        GLTools.glCheckError("program set to "
                + this.glContext.getCurrentProgram().getName()
                + " in normal rendering");
        // this.checkCurrentProgram("normalRendering(): after setCurrentProgram");
        Texture texture = primitive.getTexture();
        if (texture != null) {
            GLTools.glCheckError("initializing texture");
            texture.initializeRendering();
            GLTools.glCheckError("texture initialized");
            this.glContext.getCurrentProgram().setUniform1i(
                    LwjglLayerRenderer.colorTexture1UniformVarName,
                    COLORTEXTURE1_SLOT);
            GLTools.glCheckError("initialize texture rendering vao = "
                    + primitive.getVaoId() + " current program = "
                    + this.glContext.getCurrentProgram().getName());
            // this.checkCurrentProgram("normalRendering(): after texture::initializeRendering()");
        }
        // this.checkCurrentProgram("normalRendering(): before setGLViewMatrix()");
        this.setGLViewMatrix(this.getViewport(), primitive.getMinX(),
                primitive.getMinY());
        // this.checkCurrentProgram("normalRendering(): after setGLViewMatrix()");

        GL30.glBindVertexArray(primitive.getVaoId());
        GLTools.glCheckError("direct rendering binding vaoId = "
                + primitive.getVaoId());
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        // this.checkCurrentProgram("normalRendering(): before drawComplex()");

        this.drawComplex(primitive);
        // this.checkCurrentProgram("normalRendering(): after drawComplex()");
        GLTools.glCheckError("direct rendering drawing glComplex class = "
                + primitive.getClass().getSimpleName());
        if (texture != null) {
            texture.finalizeRendering();
            GLTools.glCheckError("direct rendering finalizing texture rendering glComplex class = "
                    + primitive.getClass().getSimpleName());
        }

        GL30.glBindVertexArray(0);
        GLTools.glCheckError("exiting direct rendering");
        // this.checkCurrentProgram("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ exiting direct rendering");
    }

    /**
     * @param primitive
     * @throws GLException
     */
    private void wireframeRendering(GLComplex primitive, float lineWidth,
            float pointSize) throws GLException {
        this.setGLViewMatrix(this.getViewport(), primitive.getMinX(),
                primitive.getMinY());
        GL30.glBindVertexArray(primitive.getVaoId());
        glDisable(GL_TEXTURE_2D); // if not set to disable, line smoothing won't
                                  // work
        GL11.glLineWidth(lineWidth);
        GL11.glPointSize(pointSize);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        this.drawComplex(primitive);
        GL30.glBindVertexArray(0);
    }

    /**
     * do a GL draw call for all complex meshes
     * 
     * @param primitive
     *            primitive to render
     */
    private void drawComplex(GLComplex primitive) {
        for (GLMesh mesh : primitive.getMeshes()) {
            GL11.glDrawElements(mesh.getGlType(),
                    mesh.getLastIndex() - mesh.getFirstIndex() + 1,
                    GL11.GL_UNSIGNED_INT, mesh.getFirstIndex()
                            * (Integer.SIZE / 8));
        }
    }

    /**
     * Draw a displayable with full or partial representation depending on the
     * displayable termination
     * 
     * @throws GLException
     */
    private void renderDisplayable(GLDisplayable displayable, double opacity)
            throws GLException {
        if (displayable == null) {
            return;
        }
        Collection<GLComplex> fullRepresentation = displayable
                .getFullRepresentation();
        if (fullRepresentation == null) {
            this.renderGLPrimitive(displayable.getPartialRepresentation(),
                    opacity);
        } else {
            this.renderGLPrimitive(fullRepresentation, opacity);
        }
    }

    @Override
    public void initializeRendering() throws RenderingException {
        this.initShader();
        this.needInitialization = false;
    }

    @Override
    public void finalizeRendering() throws RenderingException {
        // nothing to finalize
    }

    public void initShader() {
        this.initializeFBO();
    }

    /**
     * initialize Frame Buffer Object and Unit Quad
     */
    private void initializeFBO() {
        this.fboId = glGenFramebuffers();
        if (this.fboId < 0) {
            logger.error("Unable to create frame buffer");
        }

        this.fboTextureId = glGenTextures();
        if (this.fboTextureId < 0) {
            logger.error("Unable to FBO texture");
        }

        this.fboImageWidth = -1;
        this.fboImageHeight = -1;
        // glBindFramebuffer(GL_FRAMEBUFFER, this.fboId);
        // glBindTexture(GL_TEXTURE_2D, this.fboTextureId);
        // GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
        // GL11.GL_LINEAR);
        // glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.fboImageWidth,
        // this.fboImageHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
        // (ByteBuffer) null);
        // glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
        // GL_TEXTURE_2D, this.fboTextureId, 0);
        // // check FBO status
        // int status = GL30.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        // if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
        // logger.error("Frame Buffer Object is not correctly initialized");
        // }
        // glBindFramebuffer(GL_FRAMEBUFFER, 0);
        this.initializeScreenQuad();
    }

    /**
     * Set the GL uniform view matrix (stored in viewMatrixLocation) using a
     * viewport
     * 
     * @throws GLException
     */
    private boolean setGLViewMatrix(final Viewport viewport, final double minX,
            final double minY) throws GLException {
        AffineTransform modelToViewTransform = null;

        try {
            modelToViewTransform = viewport.getModelToViewTransform();
        } catch (NoninvertibleTransformException e1) {
            logger.error("Non invertible viewport matrix");
            return false;
        }

        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        this.glContext.getCurrentProgram().setUniform1f(
                LwjglLayerRenderer.m00ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getScaleX()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        this.glContext.getCurrentProgram().setUniform1f(
                LwjglLayerRenderer.m02ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getTranslateX() + minX
                        * modelToViewTransform.getScaleX()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        this.glContext.getCurrentProgram().setUniform1f(
                LwjglLayerRenderer.m11ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getScaleY()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        this.glContext.getCurrentProgram().setUniform1f(
                LwjglLayerRenderer.m12ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getTranslateY() + minY
                        * modelToViewTransform.getScaleY()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        float width = this.getCanvasWidth();
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        float height = this.getCanvasHeight();
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        this.glContext.getCurrentProgram().setUniform1f(
                LwjglLayerRenderer.screenWidthUniformVarName, width);
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        this.glContext.getCurrentProgram().setUniform1f(
                LwjglLayerRenderer.screenHeightUniformVarName, height);

        // System.err.println("translation x = " + (float)
        // (modelToViewTransform.getTranslateX()) + " y = " +
        // (modelToViewTransform.getTranslateY()));
        // System.err.println("scaling     x = " + (float)
        // (modelToViewTransform.getScaleX()) + " y = " +
        // (modelToViewTransform.getScaleY()));
        // System.err.println("canvas width = " + this.getCanvasWidth() +
        // " height = " + this.getCanvasHeight());
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        return true;
    }

    @Override
    public void reset() {
        synchronized (this.displayables) {
            this.displayables.clear();
        }

    }

    @Override
    public void onStateChange(Task task, TaskState oldState) {
        if (task.getState().isFinished()) {
            GeOxygeneEventManager.getInstance().getApplication().getMainFrame()
                    .getCurrentDesktop().repaint();
        }
    }

}
