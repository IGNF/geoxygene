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

package fr.ign.cogit.geoxygene.util.gl;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glEnable;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 * @author JeT Basic texture returns the coordinates equal to the provided
 *         point. It manages a texture by its filename and is a good base class
 *         for inheritance
 */
public class BasicTexture implements GLTexture {

    private static final Logger logger = LogManager.getLogger(BasicTexture.class.getName()); // logger

    private static final int CHECKERBOARDSIZE = 20;

    private static final Color[] CHECKERBOARDCOLOR = { new Color(0.f, 0.f, 0.f, 0.25f), new Color(1.f, 1.f, 1.f, 0.25f) };

    protected int textureId = -1;
    protected int textureSlot = GL13.GL_TEXTURE0;
    private URL tex_location = null;
    private BufferedImage textureImage = null;
    private double scaleX, scaleY;

    private String uniformVarName;

    
    public URL getTextureURL(){
        return this.tex_location;
    }
    /**
     * Constructor
     */
    public BasicTexture() {
        this.scaleX = 1.;
        this.scaleY = 1.;
    }

    /**
     * Constructor with an image to read
     * 
     * @param tex_location : the real location of the texture.
     */
    public BasicTexture(final URL tex_location) {
        this();
        this.setTextureURL(tex_location);
    }

    /**
     * @param width
     * @param height
     * @return
     */
    public final void createTextureImage(final int width, final int height) {
        if (width * height == 0) {
            throw new IllegalArgumentException("Basic texture request with null size");
        }
        BufferedImage img = null;
        try {
            if (width > 3000) {
                System.err.println();
            }
            // logger.debug("********************************************************************** Create a Buffered Texture Image "
            // + width + "x" + height);
            img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        } catch (Throwable e) {
            logger.error("An error ocurred creating RGBA image size " + width + "x" + height);
            e.printStackTrace();
            return;
        }
        Graphics2D g2 = img.createGraphics();
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, width, height);
        g2.setComposite(AlphaComposite.Src);
        for (int y = 0; y < height; y += CHECKERBOARDSIZE) {
            for (int x = 0; x < width; x += CHECKERBOARDSIZE) {
                g2.setPaint(CHECKERBOARDCOLOR[(x / CHECKERBOARDSIZE + y / CHECKERBOARDSIZE) % 2]);
                g2.fillRect(x, y, CHECKERBOARDSIZE, CHECKERBOARDSIZE);
            }
        }
        this.setTextureImage(img);
    }

    /**
     * Constructor with an image in memory
     * 
     * @param textureImage
     */
    public BasicTexture(BufferedImage textureImage) {
        this();
        this.textureImage = textureImage;
        this.textureId = -1;
    }

    /**
     * @return the textureSlot
     */
    public int getTextureSlot() {
        return this.textureSlot;
    }

    /**
     * @return the uniformVarName
     */
    public String getUniformVarName() {
        return this.uniformVarName;
    }

    /**
     * @return the scaleX
     */
    @Override
    public double getScaleX() {
        return this.scaleX;
    }

    /**
     * @param scaleX
     *            the scaleX to set
     */
    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
    }

    /**
     * @return the scaleY
     */
    @Override
    public double getScaleY() {
        return this.scaleY;
    }

    /**
     * @param scaleY
     *            the scaleY to set
     */
    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
    }

    /**
     * @param textureSlot
     *            the textureSlot to set
     */
    public void setTextureSlot(String uniformVarName, int textureSlot) {
        this.uniformVarName = uniformVarName;
        this.textureSlot = textureSlot;
    }

    /**
     * @return the generated texture id
     */
    public Integer getTextureId() {
        if (this.textureId < 0) {
            GL13.glActiveTexture(this.textureSlot);
            BufferedImage textureImage = this.getTextureImage();
            if (textureImage != null) {
                this.textureId = GLTools.loadOrRetrieveTexture(textureImage, false);
            }
        }
        return this.textureId;
    }

    /**
     * @return the textureImage
     */
    public final BufferedImage getTextureImage() {
        if (this.textureImage == null && this.tex_location != null) {
            // if the texture image is not set, try to read it from a file
            try {
                this.textureImage = GLTools.loadImage(tex_location);
            } catch (IOException e) {
                logger.error("Cannot read file '" + tex_location + "'");
                e.printStackTrace();
            }
        }
        return this.textureImage;
    }

    /**
     * @return the texture image width (in pixels)
     */
    @Override
    public final int getTextureWidth() {
        if (this.textureImage == null) {
            return 0;
        }
        return this.textureImage.getWidth();
    }

    /**
     * @return the texture image height (in pixels)
     */
    @Override
    public final int getTextureHeight() {
        if (this.textureImage == null) {
            return 0;
        }
        return this.textureImage.getHeight();
    }

    /**
     * @param _tex_url
     *            the texture url
     */
    public final void setTextureURL(final URL _tex_url) {
        this.tex_location = _tex_url;
        this.textureId = -1;
        this.textureImage = null;
    }

    /**
     * initialize the texture rendering
     */
    @Override
    public boolean initializeRendering(int programId) {
        Integer texIndex = this.getTextureId();
        if (texIndex == null || texIndex ==-1) {
            GL11.glDisable(GL_TEXTURE_2D);
            return false;
        }
        glEnable(GL_TEXTURE_2D);
        GL13.glActiveTexture(this.textureSlot);
        glBindTexture(GL_TEXTURE_2D, texIndex);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.gl.Texture#finalizeRendering()
     */
    @Override
    public void finalizeRendering() {

    }

    public void setTextureImage(BufferedImage textureImage) {
        this.textureImage = textureImage;
        this.textureId = -1;
        // this.textureId = GLTools.loadTexture(this.textureImage);

    }

}
