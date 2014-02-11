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
import static org.lwjgl.opengl.GL11.GL_FALSE;
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
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glValidateProgram;
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
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import com.vividsolutions.jts.geom.MultiPoint;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventManager;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.DistanceFieldTexture;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;
import fr.ign.cogit.geoxygene.appli.task.Task;
import fr.ign.cogit.geoxygene.appli.task.TaskListener;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.GLVertex;
import fr.ign.cogit.geoxygene.util.gl.Texture;

/**
 * @author JeT
 *         This renderer writes GL Code to perform GL rendering
 */
public class GL4FeatureRenderer extends AbstractFeatureRenderer implements TaskListener {

    private static Logger logger = Logger.getLogger(GL4FeatureRenderer.class.getName());

    private Color backgroundColor = Color.white;
    private Color foregroundColor = Color.black;
    private float lineWidth = 2.f;
    private float pointWidth = 2.f;

    private boolean needInitialization = true;

    // shaders
    private int currentProgramId = -1; // current program in use in GL context (-1 if none)
    private int glColorProgramId = 0; // shader program id for rendering colors stored in vertices
    private int glColorNoAlphaProgramId = 0; // shader program id for rendering colors stored in vertices (without using alpha)
    private int glScreenSpaceProgramId = 0; // shader program id for rendering in screen space using texture
    private int glTextureProgramId = 0; // shader program id for rendering textured polygons
    private int glDMapTextureProgramId = 0; // shader program id for distance map texture
    private static final String m00ModelToViewMatrixUniformVarName = "m00"; // view matrix GLSL variable name
    //    private int m00Location = -1; // GLSL id
    private static final String m02ModelToViewMatrixUniformVarName = "m02"; // view matrix GLSL variable name
    //    private int m02Location = -1; // GLSL
    private static final String m11ModelToViewMatrixUniformVarName = "m11"; // view matrix GLSL variable name
    //    private int m11Location = -1; // GLSL
    private static final String m12ModelToViewMatrixUniformVarName = "m12"; // view matrix GLSL variable name
    //    private int m12Location = -1; // GLSL
    private static final String screenWidthUniformVarName = "screenWidth"; // screen width GLSL variable name
    //    private int screenWidthLocation = -1; // GLSL
    private static final String screenHeightUniformVarName = "screenHeight"; // screen height GLSL variable name

    //    private int screenHeightLocation = -1; // GLSL

    private final Map<IFeature, GLDisplayable> displayables = new HashMap<IFeature, GLDisplayable>();
    private LwjglLayerRenderer lwjglLayerRenderer = null;
    private GLComplex screenQuad = null;
    private int fboId = -1;
    private int fboTextureId = -1;

    private boolean antialiasing = true;

    /**
     * Constructor
     * 
     * @param lwjglLayerRenderer
     */
    public GL4FeatureRenderer(LwjglLayerRenderer lwjglLayerRenderer) {
        this.lwjglLayerRenderer = lwjglLayerRenderer;
    }

    /**
     * 
     */
    private void initializeScreenQuad() {
        this.screenQuad = new GLComplex(0f, 0f);
        GLMesh mesh = this.screenQuad.addGLMesh(GL11.GL_QUADS);
        mesh.addIndex(this.screenQuad.addVertex(new GLVertex(new Point2D.Double(-1, -1), new Point2D.Double(0, 0))));
        mesh.addIndex(this.screenQuad.addVertex(new GLVertex(new Point2D.Double(-1, 1), new Point2D.Double(0, 1))));
        mesh.addIndex(this.screenQuad.addVertex(new GLVertex(new Point2D.Double(1, 1), new Point2D.Double(1, 1))));
        mesh.addIndex(this.screenQuad.addVertex(new GLVertex(new Point2D.Double(1, -1), new Point2D.Double(1, 0))));
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

    /**
     * Constructor
     * 
     * @param backgroundColor
     * @param foregroundColor
     */
    public GL4FeatureRenderer(LwjglLayerRenderer lwjglLayerRenderer, final Color backgroundColor, final Color foregroundColor) {
        this(lwjglLayerRenderer);
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
    }

    /**
     * @return the lwjglLayerRenderer
     */
    public LwjglLayerRenderer getLayerRenderer() {
        return this.lwjglLayerRenderer;
    }

    /**
     * @return the lwjglLayerRenderer
     */
    public LayerViewPanel getLayerViewPanel() {
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
    public void render(final IFeature feature, final Layer layer, final Symbolizer symbolizer, final Viewport viewport) throws RenderingException {
        this.setViewport(viewport);
        if (this.needInitialization()) {
            this.initializeRendering();
        }

        synchronized (this.displayables) {
            // try to retrieve previously generated geometry matching the given view environment
            GLDisplayable displayable = this.getDisplayable(feature);
            if (displayable != null) {
                this.renderDisplayable(displayable);
                return;
            }

            IGeometry geometry = feature.getGeom();
            if (geometry == null) {
                logger.warn("null geometry for feature " + feature.getId());
                return;
            } else if (geometry.isPolygon()) {
                logger.warn("polygon geometry for feature " + feature.getId());
                DisplayableSurface displayablePolygon = new DisplayableSurface(layer.getName() + " polygon #" + feature.getId(), viewport, (IPolygon) geometry,
                        symbolizer);
                displayable = displayablePolygon;
            } else if (geometry.isMultiSurface()) {
                DisplayableSurface displayablePolygon = new DisplayableSurface(layer.getName() + "multisurface #" + feature.getId(), viewport,
                        (IMultiSurface<?>) geometry, symbolizer);
                displayable = displayablePolygon;
            } else if (geometry.isMultiCurve()) {
                DisplayableCurve displayableCurve = new DisplayableCurve(layer.getName() + "multicurve #" + feature.getId(), viewport,
                        (IMultiCurve<?>) geometry, symbolizer);
                displayable = displayableCurve;
            } else if (geometry.isPoint() || (geometry instanceof IMultiPoint)) {
                DisplayablePoint displayablePoint = new DisplayablePoint(layer.getName() + "multipoint #" + feature.getId(), viewport, geometry, symbolizer);
                displayable = displayablePoint;
            } else {
                logger.warn("GL4FeatureRenderer cannot handle geometry type " + geometry.getClass().getSimpleName());
            }
            if (displayable != null) {
                this.addDisplayable(feature, displayable); // stores generated geometry
                this.renderDisplayable(displayable);
            } else {
                logger.warn(this.getClass().getSimpleName() + " do not know how to render feature " + feature.getGeom().getClass().getSimpleName());
            }
        }
    }

    private void addDisplayable(IFeature feature, GLDisplayable displayable) {
        this.displayables.put(feature, displayable);
        // task is automatically started when added to the manager
        GeOxygeneEventManager.getInstance().getApplication().getTaskManager().addTask(displayable);
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
     */
    private void renderGLPrimitive(Collection<GLComplex> complexes) {
        for (GLComplex complex : complexes) {
            this.renderGLPrimitive(complex);
        }

    }

    private GLDisplayable getDisplayable(IFeature feature) {
        return this.displayables.get(feature);
    }

    /**
     * Draw a filled shape with open GL
     */
    private void renderGLPrimitive(GLComplex primitive) {
        //        glEnableVertexAttribArray(COLOR_ATTRIBUTE_ID);

        boolean wireframe = ((LayerViewGLPanel) this.getLayerViewPanel()).isWireframe();
        if (wireframe) {
            this.setCurrentProgram(this.glColorProgramId);
            glEnable(GL_BLEND);
            glEnable(GL_LINE_SMOOTH);
            glEnable(GL11.GL_POINT_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glHint(GL11.GL_POINT_SMOOTH_HINT, GL_NICEST);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            this.wireframeRendering(primitive, 1f, 3f);
        }
        if (!wireframe) {
            if (primitive.mayOverlap()) {
                this.fboRendering(primitive);
            } else {
                glEnable(GL_BLEND);
                glDisable(GL11.GL_POLYGON_SMOOTH);
                glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
                this.directRendering(primitive);
            }
        }

    }

    private boolean getAntialiasing() {
        return this.antialiasing;
    }

    public void setAntialiasing(boolean b) {
        this.antialiasing = b;
    }

    /**
     * set the current shader program to use.
     * It checks if the current one is not already the given one.
     * 
     * @param programId
     * @return the previous current program
     */
    private int setCurrentProgram(int programId) {
        int previousProgram = this.currentProgramId;
        if (this.currentProgramId != programId) {
            GL20.glUseProgram(programId);
            this.currentProgramId = programId;
        }
        return previousProgram;
    }

    /**
     * @return the currentProgramId
     */
    public int getCurrentProgramId() {
        return this.currentProgramId;
    }

    private int getScreenWidth() {
        if (this.lwjglLayerRenderer != null && this.lwjglLayerRenderer.getLayerViewPanel() != null) {
            return this.lwjglLayerRenderer.getLayerViewPanel().getWidth();
        }
        return 0;
    }

    private int getScreenHeight() {
        if (this.lwjglLayerRenderer != null && this.lwjglLayerRenderer.getLayerViewPanel() != null) {
            return this.lwjglLayerRenderer.getLayerViewPanel().getHeight();
        }
        return 0;
    }

    /**
     * @param primitive
     */
    private void fboRendering(GLComplex primitive) {
        // render primitive in a FBO (offscreen rendering)
        GLTools.glCheckError("entering FBO rendering");
        glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, this.fboId);
        GLTools.glCheckError("FBO bind Frame Buffer");
        //        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
        //        GL11.glDepthMask(false);
        GL11.glViewport(0, 0, this.getScreenWidth(), this.getScreenHeight()); // set FBO viewport to screen size
        GL11.glClearColor(1f, 1f, 1f, .0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        this.setCurrentProgram(this.glColorProgramId);
        if (this.getAntialiasing()) {
            this.setCurrentProgram(this.glColorProgramId);
            glEnable(GL_BLEND);
            glEnable(GL_LINE_SMOOTH);
            glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            this.wireframeRendering(primitive, 1.f, 3f);
            GLTools.glCheckError("FBO Antialiasing wireframe rendering");
        }
        glDisable(GL11.GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        //GL14.glBlendFuncSeparate(GL11.GL_SRC_COLOR, GL11.GL_ONE, GL11.GL_CONSTANT_COLOR, GL11.GL_ONE);
        glDisable(GL11.GL_POLYGON_SMOOTH);
        this.directRendering(primitive);
        GLTools.glCheckError("FBO plain rendering");

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        // display the computed texture on the screen
        this.setCurrentProgram(this.glScreenSpaceProgramId);
        GL20.glUniform1f(glGetUniformLocation(this.getCurrentProgramId(), "alpha"), (float) primitive.getOverallOpacity());
        //        GL20.glUniform1f(glGetUniformLocation(this.getCurrentProgramId(), "alpha"), (float) 0.1);
        GL11.glDrawBuffer(GL11.GL_BACK);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL11.GL_POLYGON_SMOOTH);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, this.fboTextureId);
        GL30.glBindVertexArray(this.getScreenQuad().getVaoId());
        this.getScreenQuad().setColor(new Color(1f, 1f, 1f, (float) this.getScreenQuad().getOverallOpacity()));
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        this.drawComplex(this.getScreenQuad());
        GLTools.glCheckError("FBO drawing textured quad");
        GL30.glBindVertexArray(0);
        GLTools.glCheckError("exiting FBO rendering");
    }

    /**
     * @param primitive
     */
    private void directRendering(GLComplex primitive) {
        GLTools.glCheckError("entering direct rendering");

        switch (primitive.getRenderingCapability()) {
        case TEXTURE:
            int programId = (primitive.getTexture() instanceof DistanceFieldTexture) ? this.glDMapTextureProgramId : this.glTextureProgramId;
            this.setCurrentProgram(programId);
            GL20.glUseProgram(programId);
            break;
        default:
            logger.warn("Rendering capability " + primitive.getRenderingCapability() + " is not handled by " + this.getClass().getSimpleName());
        case POSITION:
        case COLOR:
            this.setCurrentProgram(this.glColorProgramId);
        }

        Texture texture = primitive.getTexture();
        if (texture != null) {
            texture.initializeRendering();
            GL20.glUniform1i(glGetUniformLocation(this.getCurrentProgramId(), "colorTexture1"), 0);
            GL20.glUniform1i(glGetUniformLocation(this.getCurrentProgramId(), "dMapTexture"), 4);

        }

        this.setGLViewMatrix(this.getViewport(), primitive.getMinX(), primitive.getMinY());

        GL30.glBindVertexArray(primitive.getVaoId());
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        this.drawComplex(primitive);
        if (texture != null) {
            texture.finalizeRendering();
        }

        GL30.glBindVertexArray(0);
        GLTools.glCheckError("exiting direct rendering");

    }

    /**
     * @param primitive
     */
    private void wireframeRendering(GLComplex primitive, float lineWidth, float pointSize) {
        this.setGLViewMatrix(this.getViewport(), primitive.getMinX(), primitive.getMinY());
        GL30.glBindVertexArray(primitive.getVaoId());
        glDisable(GL_TEXTURE_2D);  // if not set to disable, line smoothing won't work
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
            GL11.glDrawElements(mesh.getGlType(), mesh.getLastIndex() - mesh.getFirstIndex() + 1, GL11.GL_UNSIGNED_INT, mesh.getFirstIndex()
                    * (Integer.SIZE / 8));
        }
    }

    /**
     * Draw a displayable with full or partial representation depending on
     * the displayable termination
     */
    private void renderDisplayable(GLDisplayable displayable) {
        if (displayable == null) {
            return;
        }
        Collection<GLComplex> fullRepresentation = displayable.getFullRepresentation();
        if (fullRepresentation == null) {
            this.renderGLPrimitive(displayable.getPartialRepresentation());
        } else {
            this.renderGLPrimitive(fullRepresentation);
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
        int vertShader = 0;
        int screenSpaceVertShader = 0;
        int fragColorNoAlphaShader = 0;
        int fragColorShader = 0;
        int fragTextureShader = 0;
        int fragDMapTextureShader = 0;
        try {
            vertShader = GLTools.createShader("./src/main/resources/shaders/screen.vert.glsl", GL_VERTEX_SHADER);
            screenSpaceVertShader = GLTools.createShader("./src/main/resources/shaders/screenspace.vert.glsl", GL_VERTEX_SHADER);
            fragColorShader = GLTools.createShader("./src/main/resources/shaders/polygon.color.frag.glsl", GL_FRAGMENT_SHADER);
            fragColorNoAlphaShader = GLTools.createShader("./src/main/resources/shaders/polygon.color.noalpha.frag.glsl", GL_FRAGMENT_SHADER);
            fragTextureShader = GLTools.createShader("./src/main/resources/shaders/polygon.texture.frag.glsl", GL_FRAGMENT_SHADER);
            fragDMapTextureShader = GLTools.createShader("./src/main/resources/shaders/polygon.dmap.frag.glsl", GL_FRAGMENT_SHADER);
        } catch (Exception exc) {
            exc.printStackTrace();
            return;
        }
        this.glScreenSpaceProgramId = this.createProgramForGLComplex(screenSpaceVertShader, fragTextureShader);
        this.glColorProgramId = this.createProgramForGLComplex(vertShader, fragColorShader);
        this.glColorNoAlphaProgramId = this.createProgramForGLComplex(vertShader, fragColorNoAlphaShader);
        this.glTextureProgramId = this.createProgramForGLComplex(vertShader, fragTextureShader);
        this.glDMapTextureProgramId = this.createProgramForGLComplex(vertShader, fragDMapTextureShader);
        this.initializeFBO();
    }

    /**
     * @param vertShader
     * @param fragColorShader
     */
    private int createProgramForGLComplex(int vertShader, int fragColorShader) {
        int glProgramId = glCreateProgram();
        if (glProgramId <= 0) {
            logger.error("Unable to create GL program");
            return -1;
        }

        // if the vertex and fragment shaders setup successfully,
        // attach them to the shader program, link the shader program
        // into the GL context, and validate
        glAttachShader(glProgramId, vertShader);
        glAttachShader(glProgramId, fragColorShader);
        //        glAttachShader(this.programId, fragLineShader);
        for (int nAttrib = 0; nAttrib < GLVertex.ATTRIBUTES_COUNT; nAttrib++) {
            //            GL20.glEnableVertexAttribArray(GLVertex.ATTRIBUTES_ID[nAttrib]);
            GL20.glBindAttribLocation(glProgramId, GLVertex.ATTRIBUTES_ID[nAttrib], GLVertex.ELEMENTS_NAME[nAttrib]);
            //            System.err.println("Bind attribute " + GLVertex.ELEMENTS_NAME[nAttrib] + " location to " + GLVertex.ATTRIBUTES_ID[nAttrib]);
        }
        //        GL20.glBindAttribLocation(this.programId, COLOR_ATTRIBUTE_ID, COLOR_ATTRIBUTE_NAME);

        glLinkProgram(glProgramId);
        if (glGetProgrami(glProgramId, GL_LINK_STATUS) == GL_FALSE) {
            logger.error(GLTools.getProgramLogInfo(glProgramId));
            return -1;
        }
        glValidateProgram(glProgramId);
        if (glGetProgrami(glProgramId, GL_VALIDATE_STATUS) == GL_FALSE) {
            logger.error(GLTools.getProgramLogInfo(glProgramId));
            return -1;
        }

        //        this.m00Location = glGetUniformLocation(this.glColorProgramId, GL4FeatureRenderer.m00ModelToViewMatrixUniformVarName);
        //        this.m02Location = glGetUniformLocation(this.glColorProgramId, GL4FeatureRenderer.m02ModelToViewMatrixUniformVarName);
        //        this.m11Location = glGetUniformLocation(this.glColorProgramId, GL4FeatureRenderer.m11ModelToViewMatrixUniformVarName);
        //        this.m12Location = glGetUniformLocation(this.glColorProgramId, GL4FeatureRenderer.m12ModelToViewMatrixUniformVarName);
        //        this.screenWidthLocation = glGetUniformLocation(this.glColorProgramId, GL4FeatureRenderer.screenWidthUniformVarName);
        //        this.screenHeightLocation = glGetUniformLocation(this.glColorProgramId, GL4FeatureRenderer.screenHeightUniformVarName);
        return glProgramId;
    }

    /**
     * initialize Frame Buffer Object and Unit Quad
     */
    private void initializeFBO() {
        this.fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fboId);
        if (this.fboId < 0) {
            logger.error("Unable to create frame buffer");
        }

        this.fboTextureId = glGenTextures();
        if (this.fboTextureId < 0) {
            logger.error("Unable to FBO texture");
        }

        glBindTexture(GL_TEXTURE_2D, this.fboTextureId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.getScreenWidth(), this.getScreenHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.fboTextureId, 0);
        // check FBO status
        int status = GL30.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
            logger.error("Frame Buffer Object is not correctly initialized");
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        this.initializeScreenQuad();
    }

    /**
     * Set the GL uniform view matrix (stored in viewMatrixLocation)
     * using a viewport
     */
    private boolean setGLViewMatrix(final Viewport viewport, final double minX, final double minY) {
        AffineTransform modelToViewTransform = null;
        try {
            modelToViewTransform = viewport.getModelToViewTransform();
        } catch (NoninvertibleTransformException e1) {
            logger.error("Non invertible viewport matrix");
            return false;
        }
        LayerViewPanel lvp = viewport.getLayerViewPanels().iterator().next();

        float windowWidth = lvp.getWidth();
        float windowHeight = lvp.getHeight();

        glUniform1f(glGetUniformLocation(this.getCurrentProgramId(), m00ModelToViewMatrixUniformVarName), (float) modelToViewTransform.getScaleX());
        glUniform1f(glGetUniformLocation(this.getCurrentProgramId(), m02ModelToViewMatrixUniformVarName), (float) (modelToViewTransform.getTranslateX() + minX
                * modelToViewTransform.getScaleX()));
        glUniform1f(glGetUniformLocation(this.getCurrentProgramId(), m11ModelToViewMatrixUniformVarName), (float) modelToViewTransform.getScaleY());
        glUniform1f(glGetUniformLocation(this.getCurrentProgramId(), m12ModelToViewMatrixUniformVarName), (float) (modelToViewTransform.getTranslateY() + minY
                * modelToViewTransform.getScaleY()));
        glUniform1f(glGetUniformLocation(this.getCurrentProgramId(), screenWidthUniformVarName), windowWidth);
        glUniform1f(glGetUniformLocation(this.getCurrentProgramId(), screenHeightUniformVarName), windowHeight);

        //        System.err.println("x = " + (float) (modelToViewTransform.getTranslateX()) + " y = " + (modelToViewTransform.getTranslateY()));
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
        if (task.getState() == TaskState.ERROR || task.getState() == TaskState.FINISHED) {
            GeOxygeneEventManager.getInstance().getApplication().getMainFrame().getCurrentDesktop().repaint();
        }
    }

}
