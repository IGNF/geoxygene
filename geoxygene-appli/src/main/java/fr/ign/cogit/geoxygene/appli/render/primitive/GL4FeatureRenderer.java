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
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;
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
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLComplexFactory;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.GLVertex;

/**
 * @author JeT
 *         This renderer writes GL Code to perform GL rendering
 */
public class GL4FeatureRenderer extends AbstractFeatureRenderer {

    private static Logger logger = Logger.getLogger(GL4FeatureRenderer.class.getName());

    //    private static final int GL_inPosition_INDEX = 0;
    //    private static final String GL_inPosition_VARNAME = "inPosition";
    //    private static final int GL_inColor_INDEX = 1;
    //    private static final String GL_inTextureCoord_VARNAME = "inTextureCoord";
    //    private static final int GL_inTextureCoord_INDEX = 2;
    //    private static final String GL_inColor_VARNAME = "inColor";

    private int textureId = -1;
    private String textureFilename = null;
    private Color backgroundColor = Color.white;
    private Color foregroundColor = Color.black;
    private float lineWidth = 2.f;
    private float pointWidth = 2.f;

    private boolean needInitialization = true;

    // shaders
    private int programId = 0; // shader program id
    private static final String m00ModelToViewMatrixUniformVarName = "m00"; // view matrix GLSL variable name
    private int m00Location = -1; // GLSL id
    private static final String m02ModelToViewMatrixUniformVarName = "m02"; // view matrix GLSL variable name
    private int m02Location = -1; // GLSL
    private static final String m11ModelToViewMatrixUniformVarName = "m11"; // view matrix GLSL variable name
    private int m11Location = -1; // GLSL
    private static final String m12ModelToViewMatrixUniformVarName = "m12"; // view matrix GLSL variable name
    private int m12Location = -1; // GLSL
    private static final String screenWidthUniformVarName = "screenWidth"; // screen width GLSL variable name
    private int screenWidthLocation = -1; // GLSL
    private static final String screenHeightUniformVarName = "screenHeight"; // screen height GLSL variable name

    private static final int COLOR_ATTRIBUTE_ID = 10;
    private static final String COLOR_ATTRIBUTE_NAME = "inColor";

    private int screenHeightLocation = -1; // GLSL

    private final Map<ViewEnvironment, GLComplex> cachedComplex = new HashMap<ViewEnvironment, GLComplex>();
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

    /**
     * Render one Feature
     */
    @Override
    public void render(final IFeature feature, final Symbolizer symbolizer, final Viewport viewport) throws RenderingException {
        if (this.needInitialization()) {
            this.initializeRendering();
        }

        ViewEnvironment venv = new ViewEnvironment(feature, symbolizer, viewport);

        synchronized (this.cachedComplex) {
            // try to retrieve previously generated geometry matching the given view environment
            GLComplex complex = this.getComplex(venv);
            if (complex != null) {
                this.renderGLPrimitive(complex, viewport);
                return;
            }
            IGeometry geometry = feature.getGeom();
            if (geometry == null) {
                logger.warn("null geometry for feature " + feature.getId());
                return;
            }
            if (geometry.isPolygon()) {
                complex = this.generateGLPrimitiveFromPolygon(viewport, (GM_Polygon) geometry, symbolizer);
            }
            if (geometry.isMultiSurface()) {
                complex = this.generateGLPrimitiveFromMultiSurface(viewport, (IMultiSurface<?>) geometry, symbolizer);
            }
            if (complex != null) {
                this.cachedComplex.put(venv, complex); // stores generated geometry
                this.renderGLPrimitive(complex, viewport);
                System.err.println("Generate a new GL primitive with " + complex.getVertices().size() + " vertices for feature " + feature.getId());
            } else {
                logger.warn(this.getClass().getSimpleName() + " do not know how to render feature " + feature.getGeom().getClass().getSimpleName());
            }
        }
    }

    /**
     */
    private GLComplex generateGLPrimitiveFromMultiSurface(Viewport viewport, IMultiSurface<?> multiSurface, Symbolizer symbolizer) {
        if (symbolizer.isPolygonSymbolizer()) {
            return this.generateGLPrimitiveFromMultiSurface(viewport, multiSurface, (PolygonSymbolizer) symbolizer);
        }
        logger.warn("Do not know how to generate primitive from a " + multiSurface.getClass().getSimpleName() + " and a "
                + symbolizer.getClass().getSimpleName());
        return null;
    }

    /**
         */
    private GLComplex generateGLPrimitiveFromPolygon(Viewport viewport, IPolygon polygon, Symbolizer symbolizer) {
        if (symbolizer.isPolygonSymbolizer()) {
            return this.generateGLPrimitiveFromPolygon(viewport, polygon, (PolygonSymbolizer) symbolizer);
        }
        logger.warn("Do not know how to generate primitive from a " + polygon.getClass().getSimpleName() + " and a " + symbolizer.getClass().getSimpleName());
        return null;
    }

    /**
         */
    private GLComplex generateGLPrimitiveFromMultiSurface(Viewport viewport, IMultiSurface<?> multiSurface, PolygonSymbolizer symbolizer) {
        //        return GLComplexFactory.createFilledPolygon(multiSurface, symbolizer.getStroke().getColor());
        GLComplex content = GLComplexFactory.createFilledPolygon(multiSurface, symbolizer.getStroke().getColor());
        content.setColor(symbolizer.getFill().getColor());
        BasicStroke awtStroke = GLComplexFactory.geoxygeneStrokeToAWTStroke(viewport, symbolizer);

        GLComplex outline = GLComplexFactory.createMultiSurfaceOutline(multiSurface, awtStroke);
        outline.setColor(symbolizer.getStroke().getColor());
        content.addGLComplex(outline);
        return content;
    }

    /**
         */
    private GLComplex generateGLPrimitiveFromPolygon(Viewport viewport, IPolygon polygon, PolygonSymbolizer symbolizer) {
        GLComplex content = GLComplexFactory.createFilledPolygon(polygon, symbolizer.getStroke().getColor());

        BasicStroke awtStroke = GLComplexFactory.geoxygeneStrokeToAWTStroke(viewport, symbolizer);

        GLComplex outline = GLComplexFactory.createPolygonOutline(polygon, awtStroke);
        content.addGLComplex(outline);
        return content;
    }

    /**
     * try to retrieve a GLComplex associated with the given view environment
     * TODO: this method is O(n) but I don't know how to retrieve key AND value
     * at the same time. We can do something much more intelligent
     * 
     * @param venv
     * @return
     */
    private GLComplex getComplex(ViewEnvironment currentVenv) {
        //        if (this.cachedComplex.size() == 1) {
        //            ViewEnvironment storedVenv = this.cachedComplex.keySet().iterator().next();
        //            System.err.println("venv equality ? => " + currentVenv.equals(storedVenv));
        //        }
        for (Map.Entry<ViewEnvironment, GLComplex> entry : this.cachedComplex.entrySet()) {
            ViewEnvironment storedVenv = entry.getKey();
            if (storedVenv.equals(currentVenv)) {
                if (!storedVenv.needRegenerateGeometry(currentVenv)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Draw a filled shape with open GL
     */
    private void renderGLPrimitive(GLComplex primitive, final Viewport viewport) {
        glEnableVertexAttribArray(COLOR_ATTRIBUTE_ID);
        GL20.glUseProgram(this.programId);
        this.setGLViewMatrix(viewport);
        GL30.glBindVertexArray(primitive.getVaoId());

        if (((LayerViewGLPanel) this.getLayerViewPanel()).isWireframe()) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        } else {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }

        for (GLMesh mesh : primitive.getMeshes()) {
            float[] color = mesh.getColor();
            GL20.glVertexAttrib4f(COLOR_ATTRIBUTE_ID, color[0], color[1], color[2], color[3]);
            // Draw the vertices
            //            System.err.println("draw type = " + mesh.getGlType() + " from " + mesh.getFirstIndex() + " to " + mesh.getLastIndex() + " (included)");
            GL11.glDrawArrays(mesh.getGlType(), mesh.getFirstIndex(), mesh.getLastIndex() - mesh.getFirstIndex() + 1);
            //            System.err.println("Drawing mode = " + mesh.getGlType() + " first = " + mesh.getFirstIndex() + " count = "
            //                    + (mesh.getLastIndex() - mesh.getFirstIndex() + 1));
        }
        GL30.glBindVertexArray(0);

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
        int vertShader = 0, fragShader = 0;
        try {
            vertShader = GLTools.createShader("./src/main/resources/shaders/screen.vert", GL_VERTEX_SHADER);
            fragShader = GLTools.createShader("./src/main/resources/shaders/screen.frag", GL_FRAGMENT_SHADER);
        } catch (Exception exc) {
            exc.printStackTrace();
            return;
        } finally {
            if (vertShader <= 0) {
                logger.error("Unable to create vertex shader");
                return;
            }
            if (fragShader <= 0) {
                logger.error("Unable to create fragment shader");
                return;
            }
        }
        this.programId = glCreateProgram();
        if (this.programId <= 0) {
            logger.error("Unable to create GL program");
            return;
        }

        // if the vertex and fragment shaders setup successfully,
        // attach them to the shader program, link the shader program
        // into the GL context, and validate
        glAttachShader(this.programId, vertShader);
        glAttachShader(this.programId, fragShader);
        for (int nAttrib = 0; nAttrib < GLVertex.ATTRIBUTES_COUNT; nAttrib++) {
            GL20.glBindAttribLocation(this.programId, GLVertex.ATTRIBUTES_ID[nAttrib], GLVertex.ELEMENTS_NAME[nAttrib]);
            //            System.err.println("Bind attribute " + GLVertex.ELEMENTS_NAME[nAttrib] + " location to " + GLVertex.ATTRIBUTES_ID[nAttrib]);
        }
        GL20.glBindAttribLocation(this.programId, COLOR_ATTRIBUTE_ID, COLOR_ATTRIBUTE_NAME);

        glLinkProgram(this.programId);
        if (glGetProgrami(this.programId, GL_LINK_STATUS) == GL_FALSE) {
            logger.error(GLTools.getProgramLogInfo(this.programId));
            return;
        }
        glValidateProgram(this.programId);
        if (glGetProgrami(this.programId, GL_VALIDATE_STATUS) == GL_FALSE) {
            logger.error(GLTools.getProgramLogInfo(this.programId));
            return;
        }

        this.m00Location = glGetUniformLocation(this.programId, GL4FeatureRenderer.m00ModelToViewMatrixUniformVarName);
        this.m02Location = glGetUniformLocation(this.programId, GL4FeatureRenderer.m02ModelToViewMatrixUniformVarName);
        this.m11Location = glGetUniformLocation(this.programId, GL4FeatureRenderer.m11ModelToViewMatrixUniformVarName);
        this.m12Location = glGetUniformLocation(this.programId, GL4FeatureRenderer.m12ModelToViewMatrixUniformVarName);
        this.screenWidthLocation = glGetUniformLocation(this.programId, GL4FeatureRenderer.screenWidthUniformVarName);
        this.screenHeightLocation = glGetUniformLocation(this.programId, GL4FeatureRenderer.screenHeightUniformVarName);

    }

    /**
     * Set the GL uniform view matrix (stored in viewMatrixLocation)
     * using a viewport
     */
    private boolean setGLViewMatrix(final Viewport viewport) {
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
        glUniform1f(this.m00Location, (float) modelToViewTransform.getScaleX());
        glUniform1f(this.m02Location, (float) modelToViewTransform.getTranslateX());
        glUniform1f(this.m11Location, (float) modelToViewTransform.getScaleY());
        glUniform1f(this.m12Location, (float) modelToViewTransform.getTranslateY());
        glUniform1f(this.screenWidthLocation, windowWidth);
        glUniform1f(this.screenHeightLocation, windowHeight);

        //        System.err.println("push view matrix to GPU in var " + GL4PrimitiveRenderer.viewMatrixUniformVarName + " location = " + this.viewMatrixLocation);
        //        System.err.println(this.matrix4x4Buffer.get(0) + " " + this.matrix4x4Buffer.get(1) + " " + this.matrix4x4Buffer.get(2) + " "
        //                + this.matrix4x4Buffer.get(3) + "\n" + this.matrix4x4Buffer.get(4) + " " + this.matrix4x4Buffer.get(5) + " " + this.matrix4x4Buffer.get(6)
        //                + " " + this.matrix4x4Buffer.get(7) + "\n" + this.matrix4x4Buffer.get(8) + " " + this.matrix4x4Buffer.get(9) + " "
        //                + this.matrix4x4Buffer.get(10) + " " + this.matrix4x4Buffer.get(11) + "\n" + this.matrix4x4Buffer.get(12) + " " + this.matrix4x4Buffer.get(13)
        //                + " " + this.matrix4x4Buffer.get(14) + " " + this.matrix4x4Buffer.get(15));
        return true;
    }

    @Override
    public void reset() {
        synchronized (this.cachedComplex) {
            this.cachedComplex.clear();
        }

    }

}
