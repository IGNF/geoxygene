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

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * @author JeT Basic texture returns the coordinates equal to the provided
 *         point. It manages a texture by its filename and is a good base class
 *         for inheritance
 */
public class GLTexture {

    private static final Logger logger = Logger.getLogger(GLTexture.class
            .getName()); // logger

    private int textureId = -1;
    private String textureFilename = null;
    private BufferedImage textureImage = null;
    private boolean mipmap = false;

    // private double minX = 0; // range of point coordinates in world space
    // private double maxX = 1; // range of point coordinates in world space
    // private double minY = 0; // range of point coordinates in world space
    // private double maxY = 1; // range of point coordinates in world space

    /**
     * Constructor
     */
    public GLTexture() {
    }

    /**
     * @return the mipmap
     */
    public boolean isMipmap() {
        return this.mipmap;
    }

    /**
     * @param mipmap
     *            the mipmap to set
     */
    public void setMipmap(boolean mipmap) {
        this.mipmap = mipmap;
    }

    /**
     * Constructor with an image to read
     * 
     * @param textureFilename
     */
    public GLTexture(final String textureFilename) {
        this();
        this.setTextureFilename(textureFilename);
    }

    /**
     * Constructor with an image in memory
     * 
     * @param textureImage
     */
    public GLTexture(BufferedImage textureImage) {
        this();
        this.textureImage = textureImage;
    }

    /**
     * @return the generated texture id
     */
    public final Integer getTextureId() {
        if (this.textureId < 0) {
            BufferedImage textureImage = this.getTextureImage();
            if (textureImage != null) {
                this.textureId = GLTools.loadOrRetrieveTexture(textureImage,
                        this.mipmap);
            }
        }
        return this.textureId;
    }

    /**
     * @return the textureFilename
     */
    public final String getTextureFilename() {
        return this.textureFilename;
    }

    /**
     * @return the textureImage
     */
    public final BufferedImage getTextureImage() {
        if (this.textureImage == null && this.textureFilename != null) {
            // if the texture image is not set, try to read it from a file
            try {
                this.textureImage = GLTools.loadImage(this.textureFilename);
            } catch (IOException e) {
                logger.error("Cannot read file '" + this.getTextureFilename()
                        + "'");
                e.printStackTrace();
            }
        }
        return this.textureImage;
    }

    /**
     * @return the texture image width (in pixels)
     */
    public final int getTextureWidth() {
        BufferedImage img = this.getTextureImage();
        if (img == null) {
            return 0;
        }
        return img.getWidth();
    }

    /**
     * @return the texture image height (in pixels)
     */
    public final int getTextureHeight() {
        BufferedImage img = this.getTextureImage();
        if (img == null) {
            return 0;
        }
        return img.getHeight();
    }

    /**
     * @param textureFilename
     *            the textureFilename to set
     */
    public final void setTextureFilename(final String textureFilename) {
        this.textureFilename = textureFilename;
        this.textureId = -1;
        this.textureImage = null;
    }

    public void setTextureImage(BufferedImage textureImage) {
        this.textureImage = textureImage;
        this.textureId = -1;
        // this.textureId = GLTools.loadTexture(this.textureImage);

    }

}
