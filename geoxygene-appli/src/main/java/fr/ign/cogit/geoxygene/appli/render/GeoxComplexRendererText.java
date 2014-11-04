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

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.apache.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLTextComplex;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.RenderingException;

/**
 * @author JeT This renderer writes GL Code to perform GL rendering for toponyms
 */
public class GeoxComplexRendererText extends AbstractGeoxComplexRenderer {

    private static Logger logger = Logger
            .getLogger(GeoxComplexRendererText.class.getName());

    // Uniform Variables

    public static final int COLORTEXTURE1_SLOT = 1;
    private static final int COLORTEXTURE2_SLOT = 2;
    private BufferedImage textImage = null;
    private Graphics2D textImageGraphics = null;
    private ByteBuffer buffer = null;
    private int textTextureId = -1;
    private int previousWidth = -1;
    private int previousHeight = -1;
    private int[] pixels = null;

    /**
     * Constructor
     * 
     * @param lwjglLayerRenderer
     */
    public GeoxComplexRendererText(LwjglLayerRenderer lwjglLayerRenderer,
            TextSymbolizer symbolizer) {
        super(lwjglLayerRenderer, symbolizer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.AbstractGeoxComplexRenderer#
     * activateRenderer()
     */
    @Override
    public void activateRenderer() throws RenderingException {
        super.activateRenderer();
        this.initTextImage();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.AbstractGeoxComplexRenderer#
     * switchRenderer()
     */
    @Override
    public void switchRenderer() throws RenderingException {
        super.switchRenderer();
        try {
            this.drawText();
        } catch (GLException e) {
            throw new RenderingException(e);
        }
    }

    @Override
    public void localRendering(GLComplex primitive, double opacity)
            throws RenderingException, GLException {
        if (primitive instanceof GLTextComplex) {
            this.normalSimpleRendering((GLTextComplex) primitive, opacity);
            return;
        }
        throw new UnsupportedOperationException(
                "GLComplex basic Rendering is not supported for Complex type "
                        + primitive.getClass().getSimpleName());

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.AbstractGeoxComplexRenderer#getSymbolizer
     * ()
     */
    @Override
    public TextSymbolizer getSymbolizer() {
        return (TextSymbolizer) super.getSymbolizer();
    }

    private void initTextImage() {
        int width = this.getLayerRenderer().getCanvasWidth();
        int height = this.getLayerRenderer().getCanvasHeight();
        this.textImage = new BufferedImage(width, height,
                BufferedImage.TYPE_4BYTE_ABGR);
        this.textImageGraphics = this.textImage.createGraphics();
        // clear all image with a transparent bg
        this.textImageGraphics.setComposite(AlphaComposite.Clear);
        this.textImageGraphics.setColor(Color.black);
        this.textImageGraphics.fillRect(0, 0, width, height);
        // reinit compositing to default behaviour (transparency fading enabled)
        Composite fade = AlphaComposite
                .getInstance(AlphaComposite.SRC_OVER, 1f);
        this.textImageGraphics.setComposite(fade);
    }

    /**
     * Draw the complete text image into the current image
     * 
     * @throws GLException
     */
    private void drawText() throws GLException {
        int width = this.getLayerRenderer().getCanvasWidth();
        int height = this.getLayerRenderer().getCanvasHeight();
        if (width != this.previousWidth || height != this.previousHeight
                || this.buffer == null || this.pixels == null) {
            this.buffer = BufferUtils.createByteBuffer(width * height * 4);
            this.pixels = new int[width * height];
            this.previousWidth = width;
            this.previousHeight = height;
        }
        // try {
        // ImageIO.write(this.textImage, "PNG", new File("toponyms-drawn.png"));
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        this.textImage.getRGB(0, 0, width, height, this.pixels, 0, width);
        this.buffer.rewind();
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                int pixel = this.pixels[y * width + x];
                this.buffer.put((byte) (pixel >> 16 & 0xFF)); // Red component
                this.buffer.put((byte) (pixel >> 8 & 0xFF)); // Green component
                this.buffer.put((byte) (pixel >> 0 & 0xFF)); // Blue component
                this.buffer.put((byte) (pixel >> 24 & 0xFF)); // Alpha
                // component.
                // Only for RGBA
                // System.err.println("transparency = " + (pixel >> 24 & 0xFF));
            }
        }
        this.buffer.rewind();
        this.textImageGraphics.dispose();
        glEnable(GL_TEXTURE_2D);
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + COLORTEXTURE2_SLOT);
        glBindTexture(GL_TEXTURE_2D, this.getTextTextureId());

        // Setup texture scaling filtering
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                GL11.GL_LINEAR);
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.buffer);

        GLProgram program = this.getGlContext().setCurrentProgram(
                LayerViewGLPanel.textLayerProgramName);
        GL11.glViewport(0, 0, this.getLayerRenderer().getFBOImageWidth(), this
                .getLayerRenderer().getFBOImageHeight());
        glDisable(GL11.GL_POLYGON_SMOOTH);

        program.setUniform1i(LayerViewGLPanel.colorTexture2UniformVarName,
                COLORTEXTURE2_SLOT);
        GLTools.glCheckError("texture binding");

        GL11.glDepthMask(false);
        glDisable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(LayerViewGLPanel.getScreenQuad().getVaoId());
        GLTools.glCheckError("before drawing textured quad VAO binding");

        program.setUniform1f(LayerViewGLPanel.objectOpacityUniformVarName, 1f);
        program.setUniform1f(LayerViewGLPanel.globalOpacityUniformVarName, 1f);
        glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GLTools.glCheckError("blending set for textured quad");

        LwjglLayerRenderer.drawComplex(LayerViewGLPanel.getScreenQuad());
        GLTools.glCheckError("Drawing textured quad");

        GL30.glBindVertexArray(0); // unbind VAO
        GLTools.glCheckError("exiting Text rendering");
        glBindTexture(GL_TEXTURE_2D, 0); // unbind texture

    }

    /**
     * Render toponyms into an AWT graphics
     * 
     * @param primitive
     */
    private void awtRendering(GLTextComplex primitive) {
        TextSymbolizer symbolizer = this.getSymbolizer();
        Viewport viewport = this.getLayerViewPanel().getViewport();

        if (symbolizer.getLabel() == null || viewport == null) {
            return;
        }
        this.textImageGraphics = this.textImage.createGraphics();
        this.textImageGraphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        IFeature feature = primitive.getFeature();

        Object value = feature.getAttribute(symbolizer.getLabel());
        String text = (value == null) ? null : value.toString();

        if (text != null) {
            RenderUtil.paint(symbolizer, text, feature.getGeom(), viewport,
                    this.textImageGraphics, 1.);
        }

        // try {
        // ImageIO.write(this.textImage, "PNG", new File("toponyms-render"
        // + (new Date().getTime()) + ".png"));
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
    }

    private int getTextTextureId() {
        // System.err.println("get Text texture ID : " + this.fboTextureId);
        if (this.textTextureId == -1) {
            this.textTextureId = GL11.glGenTextures();
            // System.err.println("generated Text texture ID : "
            // + this.fboTextureId);
            if (this.textTextureId < 0) {
                logger.error("Unable to use Text texture");
            }
        }
        return this.textTextureId;
    }

    /**
     * Render a polygon using color, texture or wireframe GL_BLEND has to be set
     * before this rendering method. It uses FBOs. FBO initialization is dnne in
     * activateRenderer() and drawing FBO is done in switchRenderer() methods
     * 
     * @param primitive
     * @throws GLException
     */
    private void normalSimpleRendering(GLTextComplex primitive, double opacity)
            throws RenderingException, GLException {
        GLTools.glCheckError("gl error before text normal rendering");

        // AWT toponyms rendering in the this.textImage
        this.awtRendering(primitive);

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
