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

import test.app.GLBezierShadingComplex;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;
import fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLProgramAccessor;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/**
 * @author JeT This renderer writes GL Code to perform GL rendering
 */
public class GL4FeatureRendererBezier extends GL4FeatureRenderer {

    private static Logger logger = Logger
            .getLogger(GL4FeatureRendererBezier.class.getName());

    // Uniform Variables
    public static final int COLORTEXTURE1_SLOT = 0;

    private BasicTextureExpressiveRenderingDescriptor shaderDescriptor = null;

    /**
     * Constructor
     * 
     * @param lwjglLayerRenderer
     * @param strtex
     */
    public GL4FeatureRendererBezier(
            final LwjglLayerRenderer lwjglLayerRenderer,
            final BasicTextureExpressiveRenderingDescriptor strtex) {
        super(lwjglLayerRenderer);
        this.shaderDescriptor = strtex;
    }

    @Override
    public void normalRendering(GLComplex primitive, double opacity)
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
        // BasicTextureExpressiveRendering strtex = primitive
        // .getExpressiveRendering();
        program.setUniform1f(LayerViewGLPanel.objectOpacityUniformVarName,
                (float) primitive.getOverallOpacity());
        program.setUniform1f(LayerViewGLPanel.globalOpacityUniformVarName,
                (float) opacity);
        program.setUniform1f(LayerViewGLPanel.fboWidthUniformVarName,
                this.getFBOImageWidth());
        program.setUniform1f(LayerViewGLPanel.fboHeightUniformVarName,
                this.getFBOImageHeight());
        GLTools.glCheckError("program set to " + program.getName()
                + " in normal painting rendering");
        //

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GLTools.glCheckError("active brushTexture");
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, primitive.getBrushTexture()
                .getTextureId());
        // GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
        // GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        // GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
        // GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        program.setUniform1i(LayerViewGLPanel.colorTexture1UniformVarName,
                COLORTEXTURE1_SLOT);

        this.setGLViewMatrix(this.getViewport(), primitive.getMinX(),
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
        String shaderId = this.shaderDescriptor.getShaderDescriptor().getId();
        GLProgram program = this.getGlContext().setCurrentProgram(shaderId);
        if (program == null) {
            // set the accessor
            GLProgramAccessor bezierAccessor = this
                    .getLayerRenderer()
                    .getLayerViewPanel()
                    .createBezierAccessor(
                            this.shaderDescriptor.getShaderDescriptor());
            this.getGlContext().addProgram(shaderId, bezierAccessor);
            // generate the program by calling the accessor
            program = this.getGlContext().setCurrentProgram(shaderId);
        }
        return program;
    }

    // /**
    // * Render representing a line. It does not use FBOs
    // *
    // * @param primitive
    // * @throws GLException
    // */
    // private void normalPaintingRendering(GLPaintingComplex primitive,
    // double opacity, boolean inFBO) throws GLException {
    // GLTools.glCheckError("gl error before normal painting rendering");
    //
    // GLProgram program = this.getGlContext().setCurrentProgram(
    // LayerViewGLPanel.linePaintingProgramName);
    // StrokeTextureExpressiveRendering strtex = primitive
    // .getExpressiveRendering();
    // program.setUniform1f(LayerViewGLPanel.objectOpacityUniformVarName,
    // (float) primitive.getOverallOpacity());
    // program.setUniform1f(LayerViewGLPanel.globalOpacityUniformVarName,
    // (float) opacity);
    // if (inFBO) {
    // program.setUniform1f(LayerViewGLPanel.fboWidthUniformVarName,
    // this.fboImageWidth);
    // program.setUniform1f(LayerViewGLPanel.fboHeightUniformVarName,
    // this.fboImageHeight);
    // } else {
    // program.setUniform1f(LayerViewGLPanel.fboWidthUniformVarName,
    // this.getCanvasWidth());
    // program.setUniform1f(LayerViewGLPanel.fboHeightUniformVarName,
    // this.getCanvasHeight());
    //
    // }
    //
    // GLTools.glCheckError("program set to " + program.getName()
    // + " in normal painting rendering");
    // //
    // this.checkCurrentProgram("normalRendering(): after setCurrentProgram");
    //
    // GL11.glEnable(GL11.GL_TEXTURE_2D);
    // GLTools.glCheckError("enable paperTexture");
    // GL13.glActiveTexture(GL13.GL_TEXTURE0);
    // GLTools.glCheckError("active paperTexture");
    // GL11.glBindTexture(GL11.GL_TEXTURE_2D, primitive.getPaperTexture()
    // .getTextureId());
    // GLTools.glCheckError("bind paperTexture");
    // program.setUniform1i(LayerViewGLPanel.paperTextureUniformVarName, 0);
    //
    // GL13.glActiveTexture(GL13.GL_TEXTURE1);
    // GLTools.glCheckError("active brushTexture");
    // GL11.glBindTexture(GL11.GL_TEXTURE_2D, primitive.getBrushTexture()
    // .getTextureId());
    // // GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
    // // GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
    // // GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
    // // GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    // GLTools.glCheckError("bind brushTexture");
    // program.setUniform1i(LayerViewGLPanel.brushTextureUniformVarName, 1);
    //
    // GLTools.glCheckError("setUniform paperTexture");
    //
    // program.setUniform1i(LayerViewGLPanel.brushWidthUniformVarName,
    // primitive.getBrushTexture().getTextureWidth());
    // program.setUniform1i(LayerViewGLPanel.brushHeightUniformVarName,
    // primitive.getBrushTexture().getTextureHeight());
    // program.setUniform1i(LayerViewGLPanel.brushStartWidthUniformVarName,
    // strtex.getBrushStartLength());
    // program.setUniform1i(LayerViewGLPanel.brushEndWidthUniformVarName,
    // strtex.getBrushEndLength());
    // program.setUniform1f(LayerViewGLPanel.brushScaleUniformVarName,
    // (float) (strtex.getBrushSize() / primitive.getBrushTexture()
    // .getTextureHeight()));
    // program.setUniform1f(LayerViewGLPanel.paperScaleUniformVarName,
    // (float) (strtex.getPaperScaleFactor()));
    // program.setUniform1f(LayerViewGLPanel.paperDensityUniformVarName,
    // (float) (strtex.getPaperDensity()));
    // program.setUniform1f(LayerViewGLPanel.brushDensityUniformVarName,
    // (float) (strtex.getBrushDensity()));
    // program.setUniform1f(LayerViewGLPanel.strokePressureUniformVarName,
    // (float) (strtex.getStrokePressure()));
    // program.setUniform1f(LayerViewGLPanel.sharpnessUniformVarName,
    // (float) (strtex.getSharpness()));
    // strtex.getShader().setUniforms(program);
    // this.setGLViewMatrix(this.getViewport(), primitive.getMinX(),
    // primitive.getMinY());
    // //
    // this.checkCurrentProgram("normalRendering(): after setGLViewMatrix()");
    //
    // GL30.glBindVertexArray(primitive.getVaoId());
    // GLTools.glCheckError("direct rendering binding vaoId = "
    // + primitive.getVaoId());
    // GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
    // // this.checkCurrentProgram("normalRendering(): before drawComplex()");
    //
    // // GLTools.displayBuffer(primitive.getFlippedVerticesBuffer());
    // this.drawComplex(primitive);
    // // this.checkCurrentProgram("normalRendering(): after drawComplex()");
    // GLTools.glCheckError("direct rendering drawing GLSimpleComplex class = "
    // + primitive.getClass().getSimpleName());
    //
    // GL30.glBindVertexArray(0);
    // GLTools.glCheckError("exiting direct rendering");
    // //
    // this.checkCurrentProgram("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ exiting direct rendering");
    // }
    //
    // /**
    // * @param primitive
    // * @throws GLException
    // */
    // private void wireframeRendering(GLComplex primitive, float lineWidth,
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
    // /**
    // * do a GL draw call for all complex meshes
    // *
    // * @param primitive
    // * primitive to render
    // */
    // private void drawComplex(GLComplex primitive) {
    // RenderingStatistics.drawGLComplex(primitive);
    // for (GLMesh mesh : primitive.getMeshes()) {
    // RenderingStatistics.doDrawCall();
    // GL11.glDrawElements(mesh.getGlType(),
    // mesh.getLastIndex() - mesh.getFirstIndex() + 1,
    // GL11.GL_UNSIGNED_INT, mesh.getFirstIndex()
    // * (Integer.SIZE / 8));
    // }
    // }
    //
    // /**
    // * Draw a displayable with full or partial representation depending on the
    // * displayable termination
    // *
    // * @throws GLException
    // */
    // private void renderDisplayable(GLDisplayable displayable, double opacity)
    // throws GLException {
    // if (displayable == null) {
    // return;
    // }
    // // boolean quickRendering = this.getLayerViewPanel().getProjectFrame()
    // // .getMainFrame().getMode().getCurrentMode().getRenderingType() !=
    // // RenderingTypeMode.FINAL;
    //
    // Collection<GLComplex> fullRepresentation = displayable
    // .getFullRepresentation();
    // // if (quickRendering || fullRepresentation == null) {
    // if (fullRepresentation == null) {
    // this.renderGLPrimitive(displayable.getPartialRepresentation(),
    // opacity);
    // } else {
    // this.renderGLPrimitive(fullRepresentation, opacity);
    // }
    // }
    //
    @Override
    public void initializeRendering() throws RenderingException {
        super.initializeRendering();
    }

    @Override
    public void finalizeRendering() throws RenderingException {
        super.finalizeRendering();
    }

    // /**
    // * bezier painting program
    // */
    // private static GLProgram createBezierProgram(int basicVertexShader,
    // int basicFragmentShader, Shader shader) throws GLException {
    // // basic program
    // GLProgram program = new GLProgram(
    // LayerViewGLPanel.bezierLineProgramName);
    // program.setVertexShader(basicVertexShader);
    // program.setFragmentShader(basicFragmentShader);
    // program.addInputLocation(
    // GLBezierShadingVertex.vertexPositionVariableName,
    // GLBezierShadingVertex.vertexPositionLocation);
    // program.addInputLocation(GLBezierShadingVertex.vertexUsVariableName,
    // GLBezierShadingVertex.vertexUsLocation);
    // program.addInputLocation(GLBezierShadingVertex.vertexColorVariableName,
    // GLBezierShadingVertex.vertexColorLocation);
    // program.addInputLocation(
    // GLBezierShadingVertex.vertexLineWidthVariableName,
    // GLBezierShadingVertex.vertexLineWidthLocation);
    // program.addInputLocation(GLBezierShadingVertex.vertexMaxUVariableName,
    // GLBezierShadingVertex.vertexMaxULocation);
    // program.addInputLocation(GLBezierShadingVertex.vertexP0VariableName,
    // GLBezierShadingVertex.vertexP0Location);
    // program.addInputLocation(GLBezierShadingVertex.vertexP1VariableName,
    // GLBezierShadingVertex.vertexP1Location);
    // program.addInputLocation(GLBezierShadingVertex.vertexP2VariableName,
    // GLBezierShadingVertex.vertexP2Location);
    // program.addInputLocation(GLBezierShadingVertex.vertexN0VariableName,
    // GLBezierShadingVertex.vertexN0Location);
    // program.addInputLocation(GLBezierShadingVertex.vertexN2VariableName,
    // GLBezierShadingVertex.vertexN2Location);
    // shader.declareUniforms(program);
    // program.addUniform(LayerViewGLPanel.m00ModelToViewMatrixUniformVarName);
    // program.addUniform(LayerViewGLPanel.m02ModelToViewMatrixUniformVarName);
    // program.addUniform(LayerViewGLPanel.m00ModelToViewMatrixUniformVarName);
    // program.addUniform(LayerViewGLPanel.m11ModelToViewMatrixUniformVarName);
    // program.addUniform(LayerViewGLPanel.m12ModelToViewMatrixUniformVarName);
    // program.addUniform(LayerViewGLPanel.screenWidthUniformVarName);
    // program.addUniform(LayerViewGLPanel.screenHeightUniformVarName);
    // program.addUniform(LayerViewGLPanel.fboWidthUniformVarName);
    // program.addUniform(LayerViewGLPanel.fboHeightUniformVarName);
    // program.addUniform(LayerViewGLPanel.brushTextureUniformVarName);
    // program.addUniform(LayerViewGLPanel.brushWidthUniformVarName);
    // program.addUniform(LayerViewGLPanel.brushHeightUniformVarName);
    // program.addUniform(LayerViewGLPanel.brushScaleUniformVarName);
    // program.addUniform(LayerViewGLPanel.globalOpacityUniformVarName);
    // program.addUniform(LayerViewGLPanel.objectOpacityUniformVarName);
    // program.addUniform(LayerViewGLPanel.colorTexture1UniformVarName);
    //
    // return program;
    // }

}
