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
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLContext;
import fr.ign.cogit.geoxygene.appli.gl.ResourcesManager;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewGLPanel;
import fr.ign.cogit.geoxygene.appli.render.groups.RenderingGroup;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.appli.render.primitive.AbstractDisplayable;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.RenderingException;

public class DisplayableTextRenderer extends DisplayableRenderer<AbstractDisplayable> {

    private BufferedImage textImage = null;
    private Graphics2D textImageGraphics = null;
    private ByteBuffer buffer = null;
    private int textTextureId = -1;
    private int previousWidth = -1;
    private int previousHeight = -1;
    private int width = 0;
    private int height = 0;
    private int[] pixels = null;
    private GLProgram program;

    public DisplayableTextRenderer(Viewport _viewport) {
        super(_viewport);
        RenderingMethodDescriptor method = (RenderingMethodDescriptor) ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_RenderingMethodsManagerName).getResourceByName("Text");
        program = method.getGLProgram();
    }

    @Override
    public boolean render(AbstractDisplayable displayable_to_draw, double global_opacity) {
        if (displayable_to_draw.getSymbolizer() instanceof TextSymbolizer) {
            GLTools.glCheckError("gl error before text normal rendering");
            Integer width = (Integer) GLContext.getActiveGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_ScreenWidth);
            Integer height = (Integer) GLContext.getActiveGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_ScreenHeight);
            if (this.textImage == null || width != previousWidth || height != previousHeight) {
                System.out.println("Create a new image " + this);
                this.createTextImage(width, height);
            }
            // AWT toponyms rendering in the this.textImage
            this.awtRendering(displayable_to_draw.getFeature(), (TextSymbolizer) displayable_to_draw.getSymbolizer(), global_opacity);
            return true;
        }
        return false;
    }

    @Override
    protected Collection<GLComplex> getComplexesForGroup(RenderingGroup g, AbstractDisplayable displayable_to_draw) {
        // TODO Auto-generated method stub
        return null;
    }

    private void createTextImage(Integer width, Integer height) {
        System.out.println("Create a new image of size " + width + "x" + height);
        if (this.textImageGraphics != null) {
            // this.textImageGraphics.dispose();
        }
        this.textImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        this.textImageGraphics = this.textImage.createGraphics();
        this.textImageGraphics.setBackground(new Color(0, 0, 0, 1));
        this.buffer = BufferUtils.createByteBuffer(width * height * 4);
        this.pixels = new int[width * height];
        this.previousHeight = this.height;
        this.previousWidth = this.width;
        this.width = width;
        this.height = height;
    }

    private void clearTextImage() {
        this.textImageGraphics.clearRect(0, 0, width, height);
    }

    /**
     * Render toponyms into an AWT graphics
     */
    private void awtRendering(IFeature f, TextSymbolizer textSym, double opacity) {
        this.textImageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Object value = f.getAttribute(textSym.getLabel());
        String text = (value == null) ? null : value.toString();
        if (text != null) {
            RenderUtil.paint(textSym, text, f.getGeom(), viewport, this.textImageGraphics, opacity);
        }
    }

    @Override
    public void switchRenderer() throws RenderingException {
        try {
            if (this.textImageGraphics != null) {
                this.drawText();
                this.clearTextImage(); // clear the image for the next
                                       // rendering.
            }
        } catch (GLException e) {
            throw new RenderingException(e);
        }
    }

    private void drawText() throws GLException {
        if (this.program == null || this.textImage == null) {
            Logger.getRootLogger().debug("The GeoxGLTextRenderer " + this.hashCode() + "is not ready yet");
            return;
        }
        this.textImage.getRGB(0, 0, width, height, this.pixels, 0, width);
        this.buffer.rewind();
        for (int y = height - 1; y >= 0; y--) {
            for (int x = 0; x < width; x++) {
                int pixel = this.pixels[y * width + x];
                this.buffer.put((byte) (pixel >> 16 & 0xFF)); // Red component
                this.buffer.put((byte) (pixel >> 8 & 0xFF)); // Green component
                this.buffer.put((byte) (pixel >> 0 & 0xFF)); // Blue component
                this.buffer.put((byte) (pixel >> 24 & 0xFF)); // Alpha component
            }
        }
        this.buffer.rewind();
        glEnable(GL_TEXTURE_2D);
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + 2);
        glBindTexture(GL_TEXTURE_2D, this.getTextTextureId());

        // Setup texture scaling filtering
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.buffer);

        Integer fbow = (Integer) GLContext.getActiveGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_FboWidth);
        Integer fboh = (Integer) GLContext.getActiveGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_FboHeight);
        GL11.glViewport(0, 0, fbow, fboh);
        glDisable(GL11.GL_POLYGON_SMOOTH);
        GLContext.getActiveGlContext().setCurrentProgram(program);
        program.setUniform1i("colorTexture2", 2);
        GLTools.glCheckError("texture binding");

        GL11.glDepthMask(false);
        glDisable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(LayerViewGLPanel.getScreenQuad().getVaoId());
        GLTools.glCheckError("before drawing textured quad VAO binding");

        program.setUniform(GeoxygeneConstants.GL_VarName_ObjectOpacityVarName, 1f);
        program.setUniform(GeoxygeneConstants.GL_VarName_GlobalOpacityVarName, 1f);
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

    private int getTextTextureId() {
        if (this.textTextureId == -1) {
            this.textTextureId = GL11.glGenTextures();
            if (this.textTextureId < 0) {
                Logger.getRootLogger().error("Unable to use Text texture");
            }
        }
        return this.textTextureId;
    }

}
