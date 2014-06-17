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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.Util;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventManager;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLPaintingComplex;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;
import fr.ign.cogit.geoxygene.appli.task.Task;
import fr.ign.cogit.geoxygene.appli.task.TaskListener;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRendering;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLContext;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleComplex.GLSimpleRenderingCapability;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleVertex;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.RenderingStatistics;
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
    private GLSimpleComplex screenQuad = null;
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
        this.screenQuad = new GLSimpleComplex("scrren", 0f, 0f);
        GLMesh mesh = this.screenQuad.addGLMesh(GL11.GL_QUADS);
        mesh.addIndex(this.screenQuad.addVertex(new GLSimpleVertex(
                new Point2D.Double(-1, -1), new Point2D.Double(0, 0))));
        mesh.addIndex(this.screenQuad.addVertex(new GLSimpleVertex(
                new Point2D.Double(-1, 1), new Point2D.Double(0, 1))));
        mesh.addIndex(this.screenQuad.addVertex(new GLSimpleVertex(
                new Point2D.Double(1, 1), new Point2D.Double(1, 1))));
        mesh.addIndex(this.screenQuad.addVertex(new GLSimpleVertex(
                new Point2D.Double(1, -1), new Point2D.Double(1, 0))));
        this.screenQuad.setColor(Color.blue);
        this.screenQuad.setOverallOpacity(0.5);
    }

    /**
     * @return the screenQuad
     */
    public GLSimpleComplex getScreenQuad() {
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
        System.err.println("rendering feature " + feature.getId() + " part of "
                + feature.getFeatureCollections().size()
                + " feature collections");
        GLTools.glCheckError("gl error ocurred before main render method");
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
            } else if (geometry.isLineString()) {
                DisplayableCurve displayableLine = new DisplayableCurve(
                        layer.getName() + "-linestring #" + feature.getId(),
                        viewport, (ILineString) geometry, symbolizer);
                displayable = displayableLine;
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

        if (!GLTools.glCheckError("gl error ocurred during rendering")) {
            throw new RenderingException(Util.translateGLErrorString(GL11
                    .glGetError()));
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
            this.renderGLPrimitiveWireframe(primitive);
        } else {
            this.renderGLPrimitivePlain(primitive, opacity);
        }

    }

    /**
     * @param primitive
     * @param opacity
     * @throws GLException
     */
    private void renderGLPrimitivePlain(GLComplex primitive, double opacity)
            throws GLException {
        // System.err.println("rendering primitive "
        // + primitive.getMeshes().size() + " meshes");
        if (this.getLayerViewPanel().useFBO() && primitive.mayOverlap()) {
            // System.err.println("draw primitive with "
            // + primitive.getMeshes().size()
            // + " meshes using FBO rendering");
            this.fboRendering(primitive, opacity);
        } else {
            // System.err.println("draw primitive with "
            // + primitive.getMeshes().size()
            // + " meshes using normal rendering");
            this.normalRendering(primitive, opacity);
        }
    }

    private void normalRendering(GLComplex primitive, double opacity)
            throws GLException {
        if (primitive instanceof GLSimpleComplex) {
            this.normalSimpleRendering((GLSimpleComplex) primitive, opacity);
            return;
        } else if (primitive instanceof GLPaintingComplex) {
            this.normalPaintingRendering((GLPaintingComplex) primitive, opacity);
            return;
        }
        throw new UnsupportedOperationException(
                "GLComplex normal Rendering is not supported for Complex type "
                        + primitive.getClass().getSimpleName());

    }

    private void fboRendering(GLComplex primitive, double opacity)
            throws GLException {
        if (primitive instanceof GLSimpleComplex) {
            this.fboSimpleRendering((GLSimpleComplex) primitive, opacity);
            return;
        } else if (primitive instanceof GLPaintingComplex) {
            this.fboPaintingRendering((GLPaintingComplex) primitive, opacity);
            return;
        }
        throw new UnsupportedOperationException(
                "GLComplex FBO Rendering is not supported for Complex type "
                        + primitive.getClass().getSimpleName());

    }

    /**
     * @param primitive
     * @throws GLException
     */
    private void renderGLPrimitiveWireframe(GLComplex primitive)
            throws GLException {
        GLProgram program = this.glContext
                .setCurrentProgram(LwjglLayerRenderer.worldspaceColorProgramName);

        glEnable(GL_BLEND);
        if (this.getLayerViewPanel().getAntialiasingSize() > 0) {
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

    // /**
    // * @return the currentProgramId
    // */
    // public GLProgram getCurrentProgram() {
    // return program;
    // }
    //
    // /**
    // * @return the currentProgramId
    // * @throws GLException
    // */
    // public int getCurrentProgramId() throws GLException {
    // return program == null ? -1 : this.glContext.getCurrentProgram()
    // .getProgramId();
    // }

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
    private void fboSimpleRendering(GLSimpleComplex primitive, double opacity)
            throws GLException {
        // render primitive in a FBO (offscreen rendering)
        GLTools.glCheckError("entering FBO rendering");
        // bind a read-only framebuffer
        glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.fboId);

        // check if the screen size has change since previous rendering
        int antialisingSize = this.getLayerViewPanel().getAntialiasingSize() + 1;
        if (this.fboImageWidth != antialisingSize * this.getCanvasWidth()
                || this.fboImageHeight != antialisingSize
                        * this.getCanvasHeight()) {
            this.fboImageWidth = antialisingSize * this.getCanvasWidth();
            this.fboImageHeight = antialisingSize * this.getCanvasHeight();

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
        GLProgram program = this.glContext
                .setCurrentProgram(LwjglLayerRenderer.worldspaceColorProgramName);
        // no transparency at all
        program.setUniform1f(LwjglLayerRenderer.objectOpacityUniformVarName, 1f);
        program.setUniform1f(LwjglLayerRenderer.globalOpacityUniformVarName, 1f);
        program.setUniform1i(LwjglLayerRenderer.colorTexture1UniformVarName,
                COLORTEXTURE1_SLOT);

        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        glEnable(GL_TEXTURE_2D);
        GL11.glDrawBuffer(GL_COLOR_ATTACHMENT0);

        GL11.glViewport(0, 0, this.fboImageWidth, this.fboImageHeight);

        GL11.glClearColor(0.5f, 0.5f, 0.5f, 0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        glDisable(GL11.GL_BLEND);
        this.normalRendering(primitive, opacity);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        GLTools.glCheckError("FBO plain rendering");

        // display the computed texture on the screen
        // using object opacity * overall opacity
        program = this.glContext
                .setCurrentProgram(LwjglLayerRenderer.screenspaceAntialiasedTextureProgramName);
        GL11.glViewport(0, 0, this.getCanvasWidth(), this.getCanvasHeight());
        GL11.glDrawBuffer(GL11.GL_BACK);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL11.GL_POLYGON_SMOOTH);

        program.setUniform1i(LwjglLayerRenderer.colorTexture1UniformVarName,
                COLORTEXTURE1_SLOT);
        program.setUniform1i(
                LwjglLayerRenderer.textureScaleFactorUniformVarName,
                COLORTEXTURE1_SLOT);
        program.setUniform1i(LwjglLayerRenderer.antialiasingSizeUniformVarName,
                antialisingSize);
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + COLORTEXTURE1_SLOT);
        glBindTexture(GL_TEXTURE_2D, this.fboTextureId);

        GL11.glDepthMask(false);
        glDisable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(this.getScreenQuad().getVaoId());
        // program.setUniform1f(
        // LwjglLayerRenderer.objectOpacityUniformVarName,
        // (float) primitive.getOverallOpacity());
        // program
        // .setUniform1f(LwjglLayerRenderer.globalOpacityUniformVarName,
        // (float) opacity);
        program.setUniform1f(LwjglLayerRenderer.objectOpacityUniformVarName,
                (float) primitive.getOverallOpacity());
        program.setUniform1f(LwjglLayerRenderer.globalOpacityUniformVarName,
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
    private void normalSimpleRendering(GLSimpleComplex primitive, double opacity)
            throws GLException {
        GLTools.glCheckError("gl error before normal rendering");
        GLProgram program = null;
        if (Arrays.binarySearch(primitive.getRenderingCapabilities(),
                GLSimpleRenderingCapability.TEXTURE) >= 0) {
            // System.err.println("use texture");
            program = this.glContext
                    .setCurrentProgram(LwjglLayerRenderer.worldspaceTextureProgramName);
        } else if (Arrays.binarySearch(primitive.getRenderingCapabilities(),
                GLSimpleRenderingCapability.COLOR) >= 0
                || Arrays.binarySearch(primitive.getRenderingCapabilities(),
                        GLSimpleRenderingCapability.POSITION) >= 0) {
            // System.err.println("use color");
            program = this.glContext
                    .setCurrentProgram(LwjglLayerRenderer.worldspaceColorProgramName);
        } else {
            logger.warn("Rendering capability "
                    + Arrays.toString(primitive.getRenderingCapabilities())
                    + " is not handled by " + this.getClass().getSimpleName());
        }
        if (program == null) {
            logger.error("GL program cannot be set");
            return;
        }
        program.setUniform1f(LwjglLayerRenderer.objectOpacityUniformVarName, 1f);
        program.setUniform1f(LwjglLayerRenderer.globalOpacityUniformVarName,
                (float) opacity);
        GLTools.glCheckError("program set to " + program.getName()
                + " in normal rendering");
        // this.checkCurrentProgram("normalRendering(): after setCurrentProgram");
        Texture texture = primitive.getTexture();
        if (texture != null) {
            GLTools.glCheckError("initializing texture");
            texture.initializeRendering();
            GLTools.glCheckError("texture initialized");
            program.setUniform1i(
                    LwjglLayerRenderer.colorTexture1UniformVarName,
                    COLORTEXTURE1_SLOT);
            program.setUniform2f(
                    LwjglLayerRenderer.textureScaleFactorUniformVarName,
                    (float) texture.getScaleX(), (float) texture.getScaleY());
            GLTools.glCheckError("initialize texture rendering vao = "
                    + primitive.getVaoId() + " current program = "
                    + program.getName());
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

        // GLTools.displayBuffer(primitive.getFlippedVerticesBuffer());
        this.drawComplex(primitive);
        // this.checkCurrentProgram("normalRendering(): after drawComplex()");
        GLTools.glCheckError("direct rendering drawing GLSimpleComplex class = "
                + primitive.getClass().getSimpleName());
        if (texture != null) {
            texture.finalizeRendering();
            GLTools.glCheckError("direct rendering finalizing texture rendering GLSimpleComplex class = "
                    + primitive.getClass().getSimpleName());
        }

        GL30.glBindVertexArray(0);
        GLTools.glCheckError("exiting direct rendering");
        // this.checkCurrentProgram("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ exiting direct rendering");
    }

    /**
     * Draw a Primitive using FBOs
     * 
     * @param primitive
     * @throws GLException
     */
    private void fboPaintingRendering(GLPaintingComplex primitive,
            double opacity) throws GLException {
        // render primitive in a FBO (offscreen rendering)
        GLTools.glCheckError("entering FBO rendering");
        // bind a read-only framebuffer
        glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.fboId);

        // check if the screen size has change since previous rendering
        int antialisingSize = this.getLayerViewPanel().getAntialiasingSize() + 1;
        if (this.fboImageWidth != antialisingSize * this.getCanvasWidth()
                || this.fboImageHeight != antialisingSize
                        * this.getCanvasHeight()) {
            this.fboImageWidth = antialisingSize * this.getCanvasWidth();
            this.fboImageHeight = antialisingSize * this.getCanvasHeight();

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
        GLProgram program = this.glContext
                .setCurrentProgram(LwjglLayerRenderer.worldspaceColorProgramName);
        // no transparency at all
        program.setUniform1f(LwjglLayerRenderer.objectOpacityUniformVarName, 1f);
        program.setUniform1f(LwjglLayerRenderer.globalOpacityUniformVarName, 1f);
        program.setUniform1i(LwjglLayerRenderer.colorTexture1UniformVarName,
                COLORTEXTURE1_SLOT);

        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

        glEnable(GL_TEXTURE_2D);
        GL11.glDrawBuffer(GL_COLOR_ATTACHMENT0);

        GL11.glViewport(0, 0, this.fboImageWidth, this.fboImageHeight);

        GL11.glClearColor(0.5f, 0.5f, 0.5f, 0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        glDisable(GL11.GL_BLEND);
        this.normalRendering(primitive, opacity);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        GLTools.glCheckError("FBO plain rendering");

        // display the computed texture on the screen
        // using object opacity * overall opacity
        program = this.glContext
                .setCurrentProgram(LwjglLayerRenderer.screenspaceAntialiasedTextureProgramName);
        GL11.glViewport(0, 0, this.getCanvasWidth(), this.getCanvasHeight());
        GL11.glDrawBuffer(GL11.GL_BACK);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL11.GL_POLYGON_SMOOTH);

        program.setUniform1i(LwjglLayerRenderer.colorTexture1UniformVarName,
                COLORTEXTURE1_SLOT);
        program.setUniform1i(LwjglLayerRenderer.antialiasingSizeUniformVarName,
                antialisingSize);
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + COLORTEXTURE1_SLOT);
        glBindTexture(GL_TEXTURE_2D, this.fboTextureId);

        GL11.glDepthMask(false);
        glDisable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(this.getScreenQuad().getVaoId());
        // program.setUniform1f(
        // LwjglLayerRenderer.objectOpacityUniformVarName,
        // (float) primitive.getOverallOpacity());
        // program
        // .setUniform1f(LwjglLayerRenderer.globalOpacityUniformVarName,
        // (float) opacity);
        program.setUniform1f(LwjglLayerRenderer.objectOpacityUniformVarName,
                (float) primitive.getOverallOpacity());
        program.setUniform1f(LwjglLayerRenderer.globalOpacityUniformVarName,
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
     * Render representing a line. It does not use FBOs
     * 
     * @param primitive
     * @throws GLException
     */
    private void normalPaintingRendering(GLPaintingComplex primitive,
            double opacity) throws GLException {
        GLTools.glCheckError("gl error before normal painting rendering");

        GLProgram program = this.glContext
                .setCurrentProgram(LwjglLayerRenderer.linePaintingProgramName);
        StrokeTextureExpressiveRendering strtex = primitive
                .getExpressiveRendering();
        program.setUniform1f(LwjglLayerRenderer.objectOpacityUniformVarName, 1f);
        program.setUniform1f(LwjglLayerRenderer.globalOpacityUniformVarName,
                (float) opacity);
        GLTools.glCheckError("program set to " + program.getName()
                + " in normal painting rendering");
        // this.checkCurrentProgram("normalRendering(): after setCurrentProgram");

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GLTools.glCheckError("enable paperTexture");
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GLTools.glCheckError("active paperTexture");
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, primitive.getPaperTexture()
                .getTextureId());
        GLTools.glCheckError("bind paperTexture");
        program.setUniform1i(LwjglLayerRenderer.paperTextureUniformVarName, 0);

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GLTools.glCheckError("active brushTexture");
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, primitive.getBrushTexture()
                .getTextureId());
        // GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
        // GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        // GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
        // GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GLTools.glCheckError("bind brushTexture");
        program.setUniform1i(LwjglLayerRenderer.brushTextureUniformVarName, 1);

        GLTools.glCheckError("setUniform paperTexture");

        program.setUniform1i(LwjglLayerRenderer.brushWidthUniformVarName,
                primitive.getBrushTexture().getTextureWidth());
        program.setUniform1i(LwjglLayerRenderer.brushHeightUniformVarName,
                primitive.getBrushTexture().getTextureHeight());
        program.setUniform1i(LwjglLayerRenderer.brushStartWidthUniformVarName,
                strtex.getBrushStartLength());
        program.setUniform1i(LwjglLayerRenderer.brushEndWidthUniformVarName,
                strtex.getBrushEndLength());
        program.setUniform1f(LwjglLayerRenderer.brushScaleUniformVarName,
                (float) (strtex.getBrushSize() / primitive.getBrushTexture()
                        .getTextureHeight()));
        program.setUniform1f(LwjglLayerRenderer.paperScaleUniformVarName,
                (float) (strtex.getPaperScaleFactor()));
        program.setUniform1f(LwjglLayerRenderer.paperDensityUniformVarName,
                (float) (strtex.getPaperDensity()));
        program.setUniform1f(LwjglLayerRenderer.brushDensityUniformVarName,
                (float) (strtex.getBrushDensity()));
        program.setUniform1f(LwjglLayerRenderer.strokePressureUniformVarName,
                (float) (strtex.getStrokePressure()));
        program.setUniform1f(LwjglLayerRenderer.sharpnessUniformVarName,
                (float) (strtex.getSharpness()));
        program.setUniform1f(
                LwjglLayerRenderer.strokePressureVariationAmplitudeUniformVarName,
                (float) (strtex.getStrokePressureVariationAmplitude()));
        program.setUniform1f(
                LwjglLayerRenderer.strokePressureVariationWavelengthUniformVarName,
                (float) (strtex.getStrokePressureVariationWavelength()));
        program.setUniform1f(
                LwjglLayerRenderer.strokeShiftVariationAmplitudeUniformVarName,
                (float) (strtex.getStrokeShiftVariationAmplitude()));
        program.setUniform1f(
                LwjglLayerRenderer.strokeShiftVariationWavelengthUniformVarName,
                (float) (strtex.getStrokeShiftVariationWavelength()));
        program.setUniform1f(
                LwjglLayerRenderer.strokeThicknessVariationAmplitudeUniformVarName,
                (float) (strtex.getStrokeThicknessVariationAmplitude()));
        program.setUniform1f(
                LwjglLayerRenderer.strokeThicknessVariationWavelengthUniformVarName,
                (float) (strtex.getStrokeThicknessVariationWavelength()));

        this.setGLViewMatrix(this.getViewport(), primitive.getMinX(),
                primitive.getMinY());
        // this.checkCurrentProgram("normalRendering(): after setGLViewMatrix()");

        GL30.glBindVertexArray(primitive.getVaoId());
        GLTools.glCheckError("direct rendering binding vaoId = "
                + primitive.getVaoId());
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        // this.checkCurrentProgram("normalRendering(): before drawComplex()");

        // GLTools.displayBuffer(primitive.getFlippedVerticesBuffer());
        this.drawComplex(primitive);
        // this.checkCurrentProgram("normalRendering(): after drawComplex()");
        GLTools.glCheckError("direct rendering drawing GLSimpleComplex class = "
                + primitive.getClass().getSimpleName());

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
        RenderingStatistics.drawGLComplex(primitive);
        for (GLMesh mesh : primitive.getMeshes()) {
            RenderingStatistics.doDrawCall();
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

        GLProgram program = this.glContext.getCurrentProgram();
        if (program == null) {
            logger.error("setting GL view matrix with no current program. Exiting.");
            return false;
        }
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                LwjglLayerRenderer.m00ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getScaleX()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                LwjglLayerRenderer.m02ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getTranslateX() + minX
                        * modelToViewTransform.getScaleX()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                LwjglLayerRenderer.m11ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getScaleY()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(
                LwjglLayerRenderer.m12ModelToViewMatrixUniformVarName,
                (float) (modelToViewTransform.getTranslateY() + minY
                        * modelToViewTransform.getScaleY()));
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        float width = this.getCanvasWidth();
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        float height = this.getCanvasHeight();
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(LwjglLayerRenderer.screenWidthUniformVarName,
                width);
        GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform1f(LwjglLayerRenderer.screenHeightUniformVarName,
                height);

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
        if (!task.getState().isRunning()) {
            GeOxygeneEventManager.getInstance().getApplication().getMainFrame()
                    .getCurrentDesktop().repaint();
        }
    }

}
