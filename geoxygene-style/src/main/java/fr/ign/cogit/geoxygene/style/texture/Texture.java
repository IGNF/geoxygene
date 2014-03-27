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
package fr.ign.cogit.geoxygene.style.texture;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class Texture {

    private int width, height; // image texture dimension
    private TextureDrawingMode textureDrawingMode = TextureDrawingMode.VIEWPORTSPACE;
    private BufferedImage textureImage = null;

    public Texture(){
      //Do nothing
    }
    
    /**
     * default constructor for
     */
    public Texture(TextureDrawingMode textureDrawingMode) {
        this.textureDrawingMode = textureDrawingMode;
    }

    /**
     * invalidate the precomputed image texture and force recomputation.
     * For async generation this method should be overridden to stop
     * potential in-progress generation
     */
    public void invalidateTexture() {
        this.setTextureImage(null);
    }

    /**
     * This set method breaks the object encapsulation paradigm. It is
     * due to geoxygene Style module independence. Textures have to be declared
     * into "Style" module but the implementation of how texture are generated
     * and
     * visualized must not be in style (they can have dependencies to appli or
     * other
     * modules). The generation has to be external and use this set method
     * (carefully)
     * 
     * @param textureImage
     *            the Texture Image to set
     */
    synchronized public void setTextureImage(BufferedImage textureImage) {
        this.textureImage = textureImage;
        if (this.textureImage != null) {
            this.setTextureDimension(this.textureImage.getWidth(), this.textureImage.getHeight());
        }
    }

    /**
     * get the image texture
     * 
     * @return the image or null
     */
    public BufferedImage getTextureImage() {
        return this.textureImage;
    }

    /**
     * Set the texture image size
     */
    public void setTextureDimension(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Set the texture image size
     */
    public void setTextureDimension(Dimension textureDimension) {
        this.width = textureDimension.width;
        this.height = textureDimension.height;
    }

    /**
     * @return the width
     */
    public int getTextureWidth() {
        return this.width;
    }

    /**
     * @param width
     *            the width to set
     */
    public void setTextureWidth(int width) {
        this.width = width;
    }

    /**
     * @return the height
     */
    public int getTextureHeight() {
        return this.height;
    }

    /**
     * @param height
     *            the height to set
     */
    public void setTextureHeight(int height) {
        this.height = height;
    }

    /**
     * @return the textureDrawingMode
     */
    public TextureDrawingMode getTextureDrawingMode() {
        return this.textureDrawingMode;
    }

    /**
     * @param textureDrawingMode
     *            the textureDrawingMode to set
     */
    public void setTextureDrawingMode(TextureDrawingMode textureDrawingMode) {
        this.textureDrawingMode = textureDrawingMode;
    }

    public static enum TextureDrawingMode {
        SCREENSPACE, VIEWPORTSPACE,
    }
}
