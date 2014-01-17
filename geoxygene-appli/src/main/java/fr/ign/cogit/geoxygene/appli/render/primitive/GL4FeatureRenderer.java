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

import static org.lwjgl.opengl.GL11.GL_FALSE;
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

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
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

    /**
     * Constructor
     * 
     * @param lwjglLayerRenderer
     */
    public GL4FeatureRenderer(LwjglLayerRenderer lwjglLayerRenderer) {
        this.lwjglLayerRenderer = lwjglLayerRenderer;

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
    public void render(final IFeature feature, final Symbolizer symbolizer, final Viewport viewport) throws RenderingException {
        if (this.needInitialization()) {
            this.initializeRendering();
        }

        synchronized (this.displayables) {
            // try to retrieve previously generated geometry matching the given view environment
            GLDisplayable displayable = this.getDisplayable(feature);
            if (displayable != null) {
                this.renderDisplayable(displayable, viewport);
                return;
            }
            IGeometry geometry = feature.getGeom();
            if (geometry == null) {
                logger.warn("null geometry for feature " + feature.getId());
                return;
            } else if (geometry.isPolygon()) {
                logger.warn("polygon geometry for feature " + feature.getId());
                DisplayablePolygon displayablePolygon = new DisplayablePolygon("polygon #" + feature.getId(), viewport, (IPolygon) geometry, symbolizer);
                displayable = displayablePolygon;
            } else if (geometry.isMultiSurface()) {
                DisplayablePolygon displayablePolygon = new DisplayablePolygon("polygon #" + feature.getId(), viewport, (IMultiSurface<?>) geometry, symbolizer);
                displayable = displayablePolygon;
            } else {
                logger.warn("GL4FeatureRenderer cannot handle geometry type " + geometry.getClass().getSimpleName());
            }
            if (displayable != null) {
                this.addDisplayable(feature, displayable); // stores generated geometry
                this.renderDisplayable(displayable, viewport);
            } else {
                logger.warn(this.getClass().getSimpleName() + " do not know how to render feature " + feature.getGeom().getClass().getSimpleName());
            }
        }
    }

    private void addDisplayable(IFeature feature, GLDisplayable displayable) {
        this.displayables.put(feature, displayable);
        displayable.addTaskListener(this);
        if (!displayable.start()) {
            logger.error("Cannot start displayable generation task " + displayable.getName());
        }
        GeOxygeneEventManager.getInstance().getApplication().getTaskManager().addTask(displayable);
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
    private void renderGLPrimitive(Collection<GLComplex> complexes, Viewport viewport) {
        for (GLComplex complex : complexes) {
            this.renderGLPrimitive(complex, viewport);
        }

    }

    private GLDisplayable getDisplayable(IFeature feature) {
        return this.displayables.get(feature);
    }

    /**
     * Draw a filled shape with open GL
     */
    private void renderGLPrimitive(GLComplex primitive, final Viewport viewport) {
        //        glEnableVertexAttribArray(COLOR_ATTRIBUTE_ID);

        switch (primitive.getRenderingCapability()) {
        case TEXTURE:
            this.currentProgramId = (primitive.getTexture() instanceof DistanceFieldTexture) ? this.glDMapTextureProgramId : this.glTextureProgramId;
            GL20.glUseProgram(this.currentProgramId);
            break;
        default:
            logger.warn("Rendering capability " + primitive.getRenderingCapability() + " is not handled by " + this.getClass().getSimpleName());
        case POSITION:
        case COLOR:
            this.currentProgramId = this.glColorProgramId;
            GL20.glUseProgram(this.currentProgramId);
        }

        Texture texture = primitive.getTexture();
        if (texture != null) {
            texture.initializeRendering();
            GL20.glUniform1i(glGetUniformLocation(this.currentProgramId, "colorTexture1"), 0);
            GL20.glUniform1i(glGetUniformLocation(this.currentProgramId, "dMapTexture"), 4);

        }

        this.setGLViewMatrix(viewport, primitive.getMinX(), primitive.getMinY());
        GL30.glBindVertexArray(primitive.getVaoId());

        if (((LayerViewGLPanel) this.getLayerViewPanel()).isWireframe()) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        } else {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }
        for (GLMesh mesh : primitive.getMeshes()) {
            //            float[] color = mesh.getColor();
            //            GL20.glVertexAttrib4f(COLOR_ATTRIBUTE_ID, color[0], color[1], color[2], color[3]);
            // Draw the vertices
            //            System.err.println("draw type = " + mesh.getGlType() + " from " + mesh.getFirstIndex() + " to " + mesh.getLastIndex() + " (included)");
            GL11.glDrawArrays(mesh.getGlType(), mesh.getFirstIndex(), mesh.getLastIndex() - mesh.getFirstIndex() + 1);
            //            System.err.println("Drawing mode = " + mesh.getGlType() + " first = " + mesh.getFirstIndex() + " count = "
            //                    + (mesh.getLastIndex() - mesh.getFirstIndex() + 1));
        }
        if (texture != null) {
            texture.finalizeRendering();
        }

        GL30.glBindVertexArray(0);

    }

    /**
     * Draw a displayable with full or partial representation depending on
     * the displayable termination
     */
    private void renderDisplayable(GLDisplayable displayable, final Viewport viewport) {
        if (displayable == null) {
            return;
        }
        Collection<GLComplex> fullRepresentation = displayable.getFullRepresentation();
        if (fullRepresentation == null) {
            this.renderGLPrimitive(displayable.getPartialRepresentation(), viewport);
        } else {
            this.renderGLPrimitive(fullRepresentation, viewport);
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
        int fragColorShader = 0;
        int fragTextureShader = 0;
        int fragDMapTextureShader = 0;
        try {
            vertShader = GLTools.createShader("./src/main/resources/shaders/screen.vert.glsl", GL_VERTEX_SHADER);
            fragColorShader = GLTools.createShader("./src/main/resources/shaders/polygon.color.frag.glsl", GL_FRAGMENT_SHADER);
            fragTextureShader = GLTools.createShader("./src/main/resources/shaders/polygon.texture.frag.glsl", GL_FRAGMENT_SHADER);
            fragDMapTextureShader = GLTools.createShader("./src/main/resources/shaders/polygon.dmap.frag.glsl", GL_FRAGMENT_SHADER);
        } catch (Exception exc) {
            exc.printStackTrace();
            return;
        } finally {
            if (vertShader <= 0) {
                logger.error("Unable to create vertex shader");
                return;
            }
            if (fragColorShader <= 0) {
                logger.error("Unable to create color fragment shader");
                return;
            }
            if (fragTextureShader <= 0) {
                logger.error("Unable to create texture fragment shader");
                return;
            }
            if (fragDMapTextureShader <= 0) {
                logger.error("Unable to create Distance Map fragment shader");
                return;
            }
        }
        this.initColorProgram(vertShader, fragColorShader);
        this.initTextureProgram(vertShader, fragTextureShader);
        this.initDMapTextureProgram(vertShader, fragDMapTextureShader);

    }

    /**
     * @param vertShader
     * @param fragColorShader
     */
    private void initColorProgram(int vertShader, int fragColorShader) {
        this.glColorProgramId = glCreateProgram();
        if (this.glColorProgramId <= 0) {
            logger.error("Unable to create GL program");
            return;
        }

        // if the vertex and fragment shaders setup successfully,
        // attach them to the shader program, link the shader program
        // into the GL context, and validate
        glAttachShader(this.glColorProgramId, vertShader);
        glAttachShader(this.glColorProgramId, fragColorShader);
        //        glAttachShader(this.programId, fragLineShader);
        for (int nAttrib = 0; nAttrib < GLVertex.ATTRIBUTES_COUNT; nAttrib++) {
            //            GL20.glEnableVertexAttribArray(GLVertex.ATTRIBUTES_ID[nAttrib]);
            GL20.glBindAttribLocation(this.glColorProgramId, GLVertex.ATTRIBUTES_ID[nAttrib], GLVertex.ELEMENTS_NAME[nAttrib]);
            //            System.err.println("Bind attribute " + GLVertex.ELEMENTS_NAME[nAttrib] + " location to " + GLVertex.ATTRIBUTES_ID[nAttrib]);
        }
        //        GL20.glBindAttribLocation(this.programId, COLOR_ATTRIBUTE_ID, COLOR_ATTRIBUTE_NAME);

        glLinkProgram(this.glColorProgramId);
        if (glGetProgrami(this.glColorProgramId, GL_LINK_STATUS) == GL_FALSE) {
            logger.error(GLTools.getProgramLogInfo(this.glColorProgramId));
            return;
        }
        glValidateProgram(this.glColorProgramId);
        if (glGetProgrami(this.glColorProgramId, GL_VALIDATE_STATUS) == GL_FALSE) {
            logger.error(GLTools.getProgramLogInfo(this.glColorProgramId));
            return;
        }

        //        this.m00Location = glGetUniformLocation(this.glColorProgramId, GL4FeatureRenderer.m00ModelToViewMatrixUniformVarName);
        //        this.m02Location = glGetUniformLocation(this.glColorProgramId, GL4FeatureRenderer.m02ModelToViewMatrixUniformVarName);
        //        this.m11Location = glGetUniformLocation(this.glColorProgramId, GL4FeatureRenderer.m11ModelToViewMatrixUniformVarName);
        //        this.m12Location = glGetUniformLocation(this.glColorProgramId, GL4FeatureRenderer.m12ModelToViewMatrixUniformVarName);
        //        this.screenWidthLocation = glGetUniformLocation(this.glColorProgramId, GL4FeatureRenderer.screenWidthUniformVarName);
        //        this.screenHeightLocation = glGetUniformLocation(this.glColorProgramId, GL4FeatureRenderer.screenHeightUniformVarName);
    }

    /**
     * @param vertShader
     * @param fragTextureShader
     */
    private void initTextureProgram(int vertShader, int fragTextureShader) {
        this.glTextureProgramId = glCreateProgram();
        if (this.glTextureProgramId <= 0) {
            logger.error("Unable to create GL program");
            return;
        }

        // if the vertex and fragment shaders setup successfully,
        // attach them to the shader program, link the shader program
        // into the GL context, and validate
        glAttachShader(this.glTextureProgramId, vertShader);
        glAttachShader(this.glTextureProgramId, fragTextureShader);
        //        glAttachShader(this.programId, fragLineShader);
        for (int nAttrib = 0; nAttrib < GLVertex.ATTRIBUTES_COUNT; nAttrib++) {
            GL20.glBindAttribLocation(this.glTextureProgramId, GLVertex.ATTRIBUTES_ID[nAttrib], GLVertex.ELEMENTS_NAME[nAttrib]);
            //            System.err.println("Bind attribute " + GLVertex.ELEMENTS_NAME[nAttrib] + " location to " + GLVertex.ATTRIBUTES_ID[nAttrib]);
        }
        //        GL20.glBindAttribLocation(this.programId, COLOR_ATTRIBUTE_ID, COLOR_ATTRIBUTE_NAME);

        glLinkProgram(this.glTextureProgramId);
        if (glGetProgrami(this.glTextureProgramId, GL_LINK_STATUS) == GL_FALSE) {
            logger.error(GLTools.getProgramLogInfo(this.glTextureProgramId));
            return;
        }
        glValidateProgram(this.glTextureProgramId);
        if (glGetProgrami(this.glTextureProgramId, GL_VALIDATE_STATUS) == GL_FALSE) {
            logger.error(GLTools.getProgramLogInfo(this.glTextureProgramId));
            return;
        }

        //        this.m00Location = glGetUniformLocation(this.glTextureProgramId, GL4FeatureRenderer.m00ModelToViewMatrixUniformVarName);
        //        this.m02Location = glGetUniformLocation(this.glTextureProgramId, GL4FeatureRenderer.m02ModelToViewMatrixUniformVarName);
        //        this.m11Location = glGetUniformLocation(this.glTextureProgramId, GL4FeatureRenderer.m11ModelToViewMatrixUniformVarName);
        //        this.m12Location = glGetUniformLocation(this.glTextureProgramId, GL4FeatureRenderer.m12ModelToViewMatrixUniformVarName);
        //        this.screenWidthLocation = glGetUniformLocation(this.glTextureProgramId, GL4FeatureRenderer.screenWidthUniformVarName);
        //        this.screenHeightLocation = glGetUniformLocation(this.glTextureProgramId, GL4FeatureRenderer.screenHeightUniformVarName);
        //        System.err.println("m00location = " + this.m00Location);
        //        System.err.println("m02location = " + this.m02Location);
    }

    /**
     * @param vertShader
     * @param fragDMapTextureShader
     */
    private void initDMapTextureProgram(int vertShader, int fragDMapTextureShader) {
        this.glDMapTextureProgramId = glCreateProgram();
        if (this.glDMapTextureProgramId <= 0) {
            logger.error("Unable to create GL program");
            return;
        }

        // if the vertex and fragment shaders setup successfully,
        // attach them to the shader program, link the shader program
        // into the GL context, and validate
        glAttachShader(this.glDMapTextureProgramId, vertShader);
        glAttachShader(this.glDMapTextureProgramId, fragDMapTextureShader);
        //        glAttachShader(this.programId, fragLineShader);
        for (int nAttrib = 0; nAttrib < GLVertex.ATTRIBUTES_COUNT; nAttrib++) {
            GL20.glBindAttribLocation(this.glDMapTextureProgramId, GLVertex.ATTRIBUTES_ID[nAttrib], GLVertex.ELEMENTS_NAME[nAttrib]);
            //            System.err.println("Bind attribute " + GLVertex.ELEMENTS_NAME[nAttrib] + " location to " + GLVertex.ATTRIBUTES_ID[nAttrib]);
        }
        //        GL20.glBindAttribLocation(this.programId, COLOR_ATTRIBUTE_ID, COLOR_ATTRIBUTE_NAME);

        glLinkProgram(this.glDMapTextureProgramId);
        if (glGetProgrami(this.glDMapTextureProgramId, GL_LINK_STATUS) == GL_FALSE) {
            logger.error(GLTools.getProgramLogInfo(this.glDMapTextureProgramId));
            return;
        }
        glValidateProgram(this.glDMapTextureProgramId);
        if (glGetProgrami(this.glDMapTextureProgramId, GL_VALIDATE_STATUS) == GL_FALSE) {
            logger.error(GLTools.getProgramLogInfo(this.glDMapTextureProgramId));
            return;
        }

        //        this.m00Location = glGetUniformLocation(this.glDMapTextureProgramId, GL4FeatureRenderer.m00ModelToViewMatrixUniformVarName);
        //        this.m02Location = glGetUniformLocation(this.glDMapTextureProgramId, GL4FeatureRenderer.m02ModelToViewMatrixUniformVarName);
        //        this.m11Location = glGetUniformLocation(this.glDMapTextureProgramId, GL4FeatureRenderer.m11ModelToViewMatrixUniformVarName);
        //        this.m12Location = glGetUniformLocation(this.glDMapTextureProgramId, GL4FeatureRenderer.m12ModelToViewMatrixUniformVarName);
        //        this.screenWidthLocation = glGetUniformLocation(this.glDMapTextureProgramId, GL4FeatureRenderer.screenWidthUniformVarName);
        //        this.screenHeightLocation = glGetUniformLocation(this.glDMapTextureProgramId, GL4FeatureRenderer.screenHeightUniformVarName);
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

        glUniform1f(glGetUniformLocation(this.currentProgramId, m00ModelToViewMatrixUniformVarName), (float) modelToViewTransform.getScaleX());
        glUniform1f(glGetUniformLocation(this.currentProgramId, m02ModelToViewMatrixUniformVarName), (float) (modelToViewTransform.getTranslateX() + minX
                * modelToViewTransform.getScaleX()));
        glUniform1f(glGetUniformLocation(this.currentProgramId, m11ModelToViewMatrixUniformVarName), (float) modelToViewTransform.getScaleY());
        glUniform1f(glGetUniformLocation(this.currentProgramId, m12ModelToViewMatrixUniformVarName), (float) (modelToViewTransform.getTranslateY() + minY
                * modelToViewTransform.getScaleY()));
        glUniform1f(glGetUniformLocation(this.currentProgramId, screenWidthUniformVarName), windowWidth);
        glUniform1f(glGetUniformLocation(this.currentProgramId, screenHeightUniformVarName), windowHeight);

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
            logger.warn("need redraw. task " + task.getName() + " finished with state " + task.getState());
            GeOxygeneEventManager.getInstance().getApplication().getMainFrame().getCurrentDesktop().repaint();
        }
    }

}
