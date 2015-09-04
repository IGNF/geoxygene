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

package fr.ign.cogit.geoxygene.appli.render;

import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.DataSourceException;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.joda.time.LocalTime;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniform1i;
import fr.ign.cogit.geoxygene.appli.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.appli.gl.RasterImage;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.style.Fill2DDescriptor;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.style.expressive.GradientSubshaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.RasterSubshaderDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLProgramAccessor;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.RenderingException;
import fr.ign.cogit.geoxygene.util.gl.Texture;

import javax.imageio.ImageIO;

/**
 * @author amasse This renderer writes GL Code to perform GL rendering using a
 *         raster map
 */
public class GeoxComplexRendererRaster extends AbstractGeoxComplexRenderer {

    private static Logger logger = Logger
            .getLogger(GeoxComplexRendererRaster.class.getName());

    // Uniform Variables

    public static final int COLORTEXTURE1_SLOT = 0;
   
    private RasterSubshaderDescriptor rasterSubshaderDescriptor = null;

    /**
     * Constructor
     * 
     * @param lwjglLayerRenderer
     */
    public GeoxComplexRendererRaster(LwjglLayerRenderer lwjglLayerRenderer,
            RasterSymbolizer symbolizer) {
        super(lwjglLayerRenderer, symbolizer);
           
        this.rasterSubshaderDescriptor = new RasterSubshaderDescriptor();

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
     * Render a raster using color?, texture? or wireframe? GL_BLEND has to be set
     * before this rendering method. It does not use FBOs
     * 
     * @param primitive
     * @throws GLException
     */
    private void normalSimpleRendering(GLSimpleComplex primitive, double opacity)
            throws RenderingException, GLException {
        GLTools.glCheckError("gl error before normal rendering");

        GLProgram program = this.setOrCreateRasterSubshaderProgram();

        if (program == null) {
            logger.error("GL program cannot be set");
            return;
        }

        // Opacity
        program.setUniform1f(LayerViewGLPanel.objectOpacityUniformVarName, (float) opacity);
        program.setUniform1f(LayerViewGLPanel.globalOpacityUniformVarName, (float) primitive.getOverallOpacity());
        
        // TODO do that in a much gracious way
        // TEMP time is temp, in a future, not that far, it will be possible to take a Delorean and travel in time
        LocalTime thisSec = LocalTime.now();
        program.setUniform1i(LayerViewGLPanel.timeUniformVarName,(int) thisSec.getMillisOfSecond()+1000*thisSec.getSecondOfMinute() );
        
        // Animation activation
        program.setUniform1i(LayerViewGLPanel.animateUniformVarName,primitive.getRasterImage().getAnimate());
        
        // Colormap  
        GLTools.glCheckError("initializing colormap");
        if ((primitive.getRasterImage().getDefColormap()==true)) {
            //
            program.setUniform1i("typeColormap",primitive.getRasterImage().getImageColorMap().getTypeColormap());
            program.setUniform1i("nbPointsColormap",primitive.getRasterImage().getImageColorMap().getNbPoints());
            primitive.getRasterImage().getImageColorMap().initializeRendering(program.getProgramId());
        }
        GLTools.glCheckError("colormap initialized");

        // Expressive rendering
        if (primitive.getExpressiveRendering() != null) {
            primitive.getExpressiveRendering().initializeRendering(program);
        }
        GLTools.glCheckError("program set to " + program.getName()
                + " in normal rendering");
        // this.checkCurrentProgram("normalRendering(): after setCurrentProgram");

        // TODO : better comments than "Raster stuff"
        GLTools.glCheckError("initializing image");
        primitive.getRasterImage().initializeRendering(program.getProgramId());
        GLTools.glCheckError("image initialized");
      

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
      
        
        if (primitive.getExpressiveRendering() != null) {
            primitive.getExpressiveRendering().finalizeRendering(program);
        }
        GL30.glBindVertexArray(0);
        GLTools.glCheckError("exiting direct rendering");
        // this.checkCurrentProgram("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ exiting direct rendering");
    }

    /**
     * @return
     * @throws GLException
     */
    private GLProgram setOrCreateRasterSubshaderProgram() throws GLException {
        String shaderId = "raster-subshader-"
                + this.rasterSubshaderDescriptor.getShaderDescriptor()
                        .getId();
        GLProgramAccessor accessor = this.getGlContext().getProgramAccessor(
                shaderId);
        GLProgram program = null;      
        if (accessor == null) {
            // set the accessor
            GLProgramAccessor rasterAccessor = this
                    .getLayerRenderer()
                    .getLayerViewPanel()
                    .createRasterSubshaderAccessor(
                            this.rasterSubshaderDescriptor
                                    .getShaderDescriptor());
            this.getGlContext().addProgram(shaderId, rasterAccessor);
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
