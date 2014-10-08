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

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.style.Fill2DDescriptor;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.expressive.GradientSubshaderDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLProgramAccessor;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.RenderingException;
import fr.ign.cogit.geoxygene.util.gl.Texture;

/**
 * @author JeT This renderer writes GL Code to perform GL rendering using a
 *         gradient map
 */
public class GeoxComplexRendererGradient extends AbstractGeoxComplexRenderer {

    private static Logger logger = Logger
            .getLogger(GeoxComplexRendererGradient.class.getName());

    // Uniform Variables

    public static final int COLORTEXTURE1_SLOT = 0;
    private GradientSubshaderDescriptor gradientSubshaderDescriptor = null;

    /**
     * Constructor
     * 
     * @param lwjglLayerRenderer
     */
    public GeoxComplexRendererGradient(LwjglLayerRenderer lwjglLayerRenderer,
            PolygonSymbolizer symbolizer) {
        super(lwjglLayerRenderer, symbolizer);
        Fill2DDescriptor fill2dDescriptor = symbolizer.getFill()
                .getFill2DDescriptor();
        if (!(fill2dDescriptor instanceof GradientSubshaderDescriptor)) {
            throw new UnsupportedOperationException(this.getClass()
                    .getSimpleName()
                    + " cannot handle fill descriptor type "
                    + fill2dDescriptor.getClass().getSimpleName());
        }
        this.gradientSubshaderDescriptor = (GradientSubshaderDescriptor) fill2dDescriptor;
    }

    @Override
    public void localRendering(GLComplex primitive, double opacity)
            throws RenderingException, GLException {
        if (primitive instanceof GLSimpleComplex) {
            this.normalSimpleRendering((GLSimpleComplex) primitive, opacity);
            return;
        }
        throw new UnsupportedOperationException(
                "GLComplex basic Rendering is not supported for Complex type "
                        + primitive.getClass().getSimpleName());

    }

    /**
     * Render a polygon using color, texture or wireframe GL_BLEND has to be set
     * before this rendering method. It does not use FBOs
     * 
     * @param primitive
     * @throws GLException
     */
    private void normalSimpleRendering(GLSimpleComplex primitive, double opacity)
            throws RenderingException, GLException {
        GLTools.glCheckError("gl error before normal rendering");

        GLProgram program = this.setOrCreateGradientSubshaderProgram();

        if (program == null) {
            logger.error("GL program cannot be set");
            return;
        }

        program.setUniform1f(LayerViewGLPanel.objectOpacityUniformVarName, 1f);
        program.setUniform1f(LayerViewGLPanel.globalOpacityUniformVarName,
                (float) opacity);

        GLTools.glCheckError("program set to " + program.getName()
                + " in normal rendering");
        // this.checkCurrentProgram("normalRendering(): after setCurrentProgram");
        // Texture texture = primitive.getTexture();
        Texture texture = primitive.getTexture();
        if (texture != null) {
            GLTools.glCheckError("initializing texture");
            texture.initializeRendering();
            GLTools.glCheckError("texture initialized");
            program.setUniform1i(
                    LayerViewGLPanel.gradientTextureUniformVarName,
                    COLORTEXTURE1_SLOT);
            GLTools.glCheckError("initialize texture rendering vao = "
                    + primitive.getVaoId() + " current program = "
                    + program.getName());
            // this.checkCurrentProgram("normalRendering(): after texture::initializeRendering()");
        }

        // this.checkCurrentProgram("normalRendering(): before setGLViewMatrix()");
        this.getLayerRenderer().setGLViewMatrix(primitive.getMinX(),
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
        GLTools.glCheckError("direct rendering drawing class = "
                + primitive.getClass().getSimpleName());
        // if (texture != null) {
        // texture.finalizeRendering();
        // GLTools.glCheckError("direct rendering finalizing texture rendering GLSimpleComplex class = "
        // + primitive.getClass().getSimpleName());
        // }

        GL30.glBindVertexArray(0);
        GLTools.glCheckError("exiting direct rendering");
        // this.checkCurrentProgram("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ exiting direct rendering");
    }

    /**
     * @return
     * @throws GLException
     */
    private GLProgram setOrCreateGradientSubshaderProgram() throws GLException {
        String shaderId = "gradient-subshader-"
                + this.gradientSubshaderDescriptor.getShaderDescriptor()
                        .getId();
        GLProgramAccessor accessor = this.getGlContext().getProgramAccessor(
                shaderId);
        GLProgram program = null;
        if (accessor == null) {
            // set the accessor
            GLProgramAccessor gradientAccessor = this
                    .getLayerRenderer()
                    .getLayerViewPanel()
                    .createGradientSubshaderAccessor(
                            this.gradientSubshaderDescriptor
                                    .getShaderDescriptor());
            this.getGlContext().addProgram(shaderId, gradientAccessor);
        }
        program = this.getGlContext().setCurrentProgram(shaderId);
        return program;
    }

    @Override
    public void initializeRendering() throws RenderingException {
        super.initializeRendering();
    }

    @Override
    public void finalizeRendering() throws RenderingException {
        super.finalizeRendering();
    }

}
