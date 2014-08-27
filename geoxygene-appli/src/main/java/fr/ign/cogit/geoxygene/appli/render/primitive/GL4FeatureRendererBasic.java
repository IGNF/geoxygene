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

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.RenderingException;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleComplex.GLSimpleRenderingCapability;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.Texture;

/**
 * @author JeT This renderer writes GL Code to perform GL rendering
 */
public class GL4FeatureRendererBasic extends GL4FeatureRenderer {

    private static Logger logger = Logger
            .getLogger(GL4FeatureRendererBasic.class.getName());

    // Uniform Variables

    public static final int COLORTEXTURE1_SLOT = 0;

    /**
     * Constructor
     * 
     * @param lwjglLayerRenderer
     */
    public GL4FeatureRendererBasic(LwjglLayerRenderer lwjglLayerRenderer) {
        super(lwjglLayerRenderer);
    }

    @Override
    public void normalRendering(GLComplex primitive, double opacity)
            throws GLException {
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
            throws GLException {
        GLTools.glCheckError("gl error before normal rendering");
        GLProgram program = null;
        if (Arrays.binarySearch(primitive.getRenderingCapabilities(),
                GLSimpleRenderingCapability.TEXTURE) >= 0) {
            // System.err.println("use texture");
            program = this.getGlContext().setCurrentProgram(
                    LayerViewGLPanel.worldspaceTextureProgramName);
        } else if (Arrays.binarySearch(primitive.getRenderingCapabilities(),
                GLSimpleRenderingCapability.COLOR) >= 0
                || Arrays.binarySearch(primitive.getRenderingCapabilities(),
                        GLSimpleRenderingCapability.POSITION) >= 0) {
            // System.err.println("use color");
            program = this.getGlContext().setCurrentProgram(
                    LayerViewGLPanel.worldspaceColorProgramName);
        } else {
            logger.warn("Rendering capability "
                    + Arrays.toString(primitive.getRenderingCapabilities())
                    + " is not handled by " + this.getClass().getSimpleName());
        }
        if (program == null) {
            logger.error("GL program cannot be set");
            return;
        }

        program.setUniform1f(LayerViewGLPanel.objectOpacityUniformVarName, 1f);
        program.setUniform1f(LayerViewGLPanel.globalOpacityUniformVarName,
                (float) opacity);

        // program.setUniform1f(LayerViewGLPanel.fboWidthUniformVarName,
        // this.getFBOImageWidth());
        // program.setUniform1f(LayerViewGLPanel.fboHeightUniformVarName,
        // this.getFBOImageHeight());
        GLTools.glCheckError("program set to " + program.getName()
                + " in normal rendering");
        // this.checkCurrentProgram("normalRendering(): after setCurrentProgram");
        Texture texture = primitive.getTexture();
        if (texture != null) {
            GLTools.glCheckError("initializing texture");
            texture.initializeRendering();
            GLTools.glCheckError("texture initialized");
            program.setUniform1i(LayerViewGLPanel.colorTexture1UniformVarName,
                    COLORTEXTURE1_SLOT);
            program.setUniform2f(
                    LayerViewGLPanel.textureScaleFactorUniformVarName,
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

    @Override
    public void initializeRendering() throws RenderingException {
        super.initializeRendering();
    }

    @Override
    public void finalizeRendering() throws RenderingException {
        super.finalizeRendering();
    }

}
