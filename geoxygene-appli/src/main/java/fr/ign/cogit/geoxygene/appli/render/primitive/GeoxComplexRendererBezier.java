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
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.appli.gl.GLBezierShadingComplex;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.texture.BasicTextureExpressiveRendering;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLProgramAccessor;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.RenderingException;

/**
 * @author JeT This renderer writes GL Code to perform GL rendering
 */
public class GeoxComplexRendererBezier extends AbstractGeoxComplexRenderer {

    private static Logger logger = Logger
            .getLogger(GeoxComplexRendererBezier.class.getName());

    // Uniform Variables
    public static final int COLORTEXTURE1_SLOT = 0;

    private BasicTextureExpressiveRenderingDescriptor shaderDescriptor = null;

    /**
     * Constructor
     * 
     * @param lwjglLayerRenderer
     * @param strtex
     */
    public GeoxComplexRendererBezier(
            final LwjglLayerRenderer lwjglLayerRenderer, Symbolizer symbolizer,
            final BasicTextureExpressiveRenderingDescriptor strtex) {
        super(lwjglLayerRenderer, symbolizer);
        this.shaderDescriptor = strtex;
    }

    @Override
    public void localRendering(GLComplex primitive, double opacity)
            throws GLException {
        if (primitive instanceof GLBezierShadingComplex) {
            this.bezierRendering((GLBezierShadingComplex) primitive, opacity);
            return;
        }
        throw new UnsupportedOperationException(
                "GLComplex bezier Rendering is not supported for Complex type "
                        + primitive.getClass().getSimpleName());

    }

    /**
     * @param primitive
     * @throws GLException
     */
    private void bezierRendering(GLBezierShadingComplex primitive,
            double opacity) throws GLException {
        GLTools.glCheckError("gl error before normal painting rendering");

        GLProgram program = this.setOrCreateBezierProgram();
        BasicTextureExpressiveRendering strtex = primitive
                .getExpressiveRendering();
        program.setUniform1f(LayerViewGLPanel.objectOpacityUniformVarName, 1f);
        program.setUniform1f(LayerViewGLPanel.globalOpacityUniformVarName,
                (float) opacity);
        program.setUniform1f(LayerViewGLPanel.fboWidthUniformVarName, this
                .getLayerRenderer().getFBOImageWidth());
        program.setUniform1f(LayerViewGLPanel.fboHeightUniformVarName, this
                .getLayerRenderer().getFBOImageHeight());
        GLTools.glCheckError("program set to " + program.getName()
                + " in normal painting rendering");
        //

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GLTools.glCheckError("enable paperTexture");
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GLTools.glCheckError("active paperTexture");
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, primitive.getPaperTexture()
                .getTextureId());
        GLTools.glCheckError("bind paperTexture");
        program.setUniform1i(LayerViewGLPanel.paperTextureUniformVarName, 0);

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GLTools.glCheckError("active brushTexture");
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, primitive.getBrushTexture()
                .getTextureId());
        // GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
        // GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        // GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
        // GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GLTools.glCheckError("bind brushTexture");
        program.setUniform1i(LayerViewGLPanel.brushTextureUniformVarName, 1);

        GLTools.glCheckError("setUniform paperTexture");

        program.setUniform1i(LayerViewGLPanel.brushWidthUniformVarName,
                primitive.getBrushTexture().getTextureWidth());
        program.setUniform1i(LayerViewGLPanel.brushHeightUniformVarName,
                primitive.getBrushTexture().getTextureHeight());
        program.setUniform1i(LayerViewGLPanel.brushStartWidthUniformVarName,
                strtex.getBrushStartLength());
        program.setUniform1i(LayerViewGLPanel.brushEndWidthUniformVarName,
                strtex.getBrushEndLength());
        program.setUniform1f(LayerViewGLPanel.brushScaleUniformVarName,
                (float) (strtex.getBrushAspectRatio() / primitive
                        .getBrushTexture().getTextureHeight()));
        program.setUniform1f(LayerViewGLPanel.paperScaleUniformVarName,
                (float) (strtex.getPaperSizeInCm()));
        program.setUniform1f(LayerViewGLPanel.paperDensityUniformVarName,
                (float) (strtex.getPaperDensity()));
        program.setUniform1f(LayerViewGLPanel.brushDensityUniformVarName,
                (float) (strtex.getBrushDensity()));
        program.setUniform1f(LayerViewGLPanel.strokePressureUniformVarName,
                (float) (strtex.getStrokePressure()));
        program.setUniform1f(LayerViewGLPanel.sharpnessUniformVarName,
                (float) (strtex.getSharpness()));

        strtex.getShader().setUniforms(program);
        this.getLayerRenderer().setGLViewMatrix(primitive.getMinX(),
                primitive.getMinY());
        //

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
        //
    }

    /**
     * @return
     * @throws GLException
     */
    private GLProgram setOrCreateBezierProgram() throws GLException {
        String shaderId = "bezier-subshader-"
                + this.shaderDescriptor.getShaderDescriptor().getId();
        GLProgramAccessor accessor = this.getGlContext().getProgramAccessor(
                shaderId);
        GLProgram program = null;
        if (accessor == null) {
            // set the accessor
            GLProgramAccessor bezierAccessor = this
                    .getLayerRenderer()
                    .getLayerViewPanel()
                    .createBezierAccessor(
                            this.shaderDescriptor.getShaderDescriptor());
            this.getGlContext().addProgram(shaderId, bezierAccessor);
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