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
    private ByteBuffer buffer = null;
    private int textTextureId = -1;
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
            this.drawFBO();
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
        if (this.textImage == null
                || this.textImage.getWidth() != this.getLayerViewPanel()
                        .getWidth()
                || this.textImage.getHeight() != this.getLayerViewPanel()
                        .getHeight()) {
            this.textImage = new BufferedImage(this.getLayerViewPanel()
                    .getWidth(), this.getLayerViewPanel().getHeight(),
                    BufferedImage.TYPE_4BYTE_ABGR);
            this.buffer = BufferUtils.createByteBuffer(this.textImage
                    .getWidth() * this.textImage.getHeight() * 4);
            this.pixels = new int[this.textImage.getWidth()
                    * this.textImage.getHeight()];

        }
        Graphics2D g = this.textImage.createGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.setColor(Color.red);
        g.fillRect(0, 0, this.textImage.getWidth(), this.textImage.getHeight());

        Composite fade = AlphaComposite
                .getInstance(AlphaComposite.SRC_OVER, 1f);
        g.setComposite(fade);
    }

    private void drawFBO() throws GLException {
        int height = this.textImage.getHeight();
        int width = this.textImage.getWidth();
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
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.getTextTextureId());

        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
                GL11.GL_REPEAT);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
                GL11.GL_REPEAT);

        // Setup texture scaling filtering
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                GL11.GL_LINEAR);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR);
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.buffer);

        GLProgram program = this.getGlContext().setCurrentProgram(
                LayerViewGLPanel.screenspaceAntialiasedTextureProgramName);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        GL11.glViewport(0, 0, width, height);
        GL11.glDrawBuffer(GL11.GL_BACK);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL11.GL_POLYGON_SMOOTH);

        program.setUniform1i(LayerViewGLPanel.colorTexture1UniformVarName,
                COLORTEXTURE2_SLOT);
        GLTools.glCheckError("FBO bind antialiasing");
        program.setUniform1i(LayerViewGLPanel.antialiasingSizeUniformVarName, 1);
        GLTools.glCheckError("FBO activate texture");
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + COLORTEXTURE2_SLOT);
        GLTools.glCheckError("FBO bound texture");
        glBindTexture(GL_TEXTURE_2D, this.getTextTextureId());
        GLTools.glCheckError("FBO bound texture");

        GL11.glDepthMask(false);
        glDisable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(LayerViewGLPanel.getScreenQuad().getVaoId());

        program.setUniform1f(LayerViewGLPanel.objectOpacityUniformVarName, 1f);
        program.setUniform1f(LayerViewGLPanel.globalOpacityUniformVarName, 1f);
        glEnable(GL_BLEND);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR);
        //
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

        GLTools.glCheckError("before FBO drawing textured quad");
        LwjglLayerRenderer.drawComplex(LayerViewGLPanel.getScreenQuad());
        // LayerViewGLPanel.getScreenQuad().setColor(new Color(1f, 1f, 1f,
        // .5f));
        GLTools.glCheckError("FBO drawing textured quad");

        GL30.glBindVertexArray(0); // unbind VAO
        GLTools.glCheckError("exiting FBO rendering");
        glBindTexture(GL_TEXTURE_2D, 0); // unbind texture
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

    }

    /**
     * Render toponyms into an AWT graphics
     */
    private void awtRendering() {
        TextSymbolizer symbolizer = this.getSymbolizer();
        Viewport viewport = this.getLayerViewPanel().getViewport();
        if (symbolizer.getLabel() == null) {
            return;
        }
        this.textImage.getRGB(0, 0, this.textImage.getWidth(),
                this.textImage.getHeight(), this.pixels, 0,
                this.textImage.getWidth());
        Graphics2D g = this.textImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        for (IFeature feature : this.getLayerRenderer().getLayer()
                .getFeatureCollection()) {

            Object value = feature.getAttribute(symbolizer.getLabel());
            String text = (value == null) ? null : value.toString();
            if (text != null) {
                RenderUtil.paint(symbolizer, text, feature.getGeom(), viewport,
                        g, 1.);
            }
        }
        g.dispose();

        // try {
        // ImageIO.write(this.textImage, "PNG", new File("toponyms.png"));
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        //
    }

    private int getTextTextureId() {
        // System.err.println("get FBO texture ID : " + this.fboTextureId);
        if (this.textTextureId == -1) {
            this.textTextureId = GL11.glGenTextures();
            // System.err.println("generated FBO texture ID : "
            // + this.fboTextureId);
            if (this.textTextureId < 0) {
                logger.error("Unable to use Overlay texture");
            }
        }
        return this.textTextureId;
    }

    /**
     * Render a polygon using color, texture or wireframe GL_BLEND has to be set
     * before this rendering method. It uses FBOs. FBO initialization is donne
     * in activateRenderer() and drawing FBO is done in switchRenderer() methods
     * 
     * @param primitive
     * @throws GLException
     */
    private void normalSimpleRendering(GLTextComplex primitive, double opacity)
            throws RenderingException, GLException {
        GLTools.glCheckError("gl error before text normal rendering");

        // AWT toponyms rendering in the this.textImage
        this.awtRendering();

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
