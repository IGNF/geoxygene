/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.style;

import java.awt.Color;
import java.awt.Dimension;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import fr.ign.cogit.geoxygene.style.texture.SimpleTexture;
import fr.ign.cogit.geoxygene.style.texture.Texture;

/**
 * @author JeT
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BackgroundDescriptor {

    
    @XmlElement(name="SimpleTexture")
    SimpleTexture tex = null;

    @XmlTransient
    private int width, height; // image texture dimension (in pixels)

    @XmlElement(name = "PaperHeightInCm")
    private double paperHeightInCm = 4;
    @XmlElement(name = "PaperReferenceMapScale")
    private double paperReferenceMapScale = 100000;

    /**
     * The raw color of the stroke, without opacity information.
     */
    @XmlTransient
    private Color color = null;

    @XmlElement(name = "Color")
    private String colorDescriptor = "#FFFFFF";

    /**
     * default constructor
     */
    public BackgroundDescriptor() {
        super();
    }

    /**
     * constructor
     * 
     * @param _tex
     *            the texture used to render the background
     */
    public BackgroundDescriptor(SimpleTexture _tex) {
        this();
        this.tex = _tex;
    }

    /**
     * @return the colorDescriptor
     */
    public String getColorDescriptor() {
        return this.colorDescriptor;
    }

    /**
     * @param colorDescriptor
     *            the colorDescriptor to set
     */
    public void setColorDescriptor(String colorDescriptor) {
        this.colorDescriptor = colorDescriptor;
        this.color = null;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Returns the color of the stroke, considering the opacity attribute.
     * 
     * @return The color of the stroke, considering the opacity attribute.
     */
    public synchronized Color getColor() {
        if (this.color == null) {
            this.color = Color.decode(this.getColorDescriptor().trim());
        }
        return this.color;
    }

    public Texture getTexture() {
        return this.tex;
    }


    /**
     * @return the paperScaleFactor
     */
    public double getPaperHeightInCm() {
        return this.paperHeightInCm;
    }

    /**
     * @param paperScaleFactor
     *            the paperScaleFactor to set
     */
    public void setPaperHeightInCm(double paperHeightInCm) {
        this.paperHeightInCm = paperHeightInCm;
    }

    /**
     * @return the paperReferenceMapScale
     */
    public double getPaperReferenceMapScale() {
        return this.paperReferenceMapScale;
    }

    /**
     * @param paperReferenceMapScale
     *            the paperReferenceMapScale to set
     */
    public void setPaperReferenceMapScale(double paperReferenceMapScale) {
        this.paperReferenceMapScale = paperReferenceMapScale;
    }

    // /**
    // * invalidate the precomputed image texture and force recomputation. For
    // * async generation this method should be overridden to stop potential
    // * in-progress generation
    // */
    // public void invalidateTexture() {
    // this.setTextureImage(null);
    // }

    // /**
    // * This set method breaks the object encapsulation paradigm. It is due to
    // * geoxygene Style module independence. Textures have to be declared into
    // * "Style" module but the implementation of how texture are generated and
    // * visualized must not be in style (they can have dependencies to appli or
    // * other modules). The generation has to be external and use this set
    // method
    // * (carefully)
    // *
    // * @param textureImage
    // * the Texture Image to set
    // */
    // synchronized public void setTextureImage(BufferedImage textureImage) {
    // this.textureImage = textureImage;
    // if (this.textureImage != null) {
    // this.setTextureDimension(this.textureImage.getWidth(),
    // this.textureImage.getHeight());
    // }
    // }
    //
    // /**
    // * get the image texture
    // *
    // * @return the image or null
    // */
    // public BufferedImage getTextureImage() {
    // return this.textureImage;
    // }

    /**
     * Set the texture image size in pixels. Shortcut to setWidth()/setHeight()
     */
    public void setTextureDimension(int width, int height) {
        this.setTextureWidth(width);
        this.setTextureHeight(height);
    }

    /**
     * Set the texture image size in pixels. Shortcut to setWidth()/setHeight()
     */
    public void setTextureDimension(Dimension textureDimension) {
        this.setTextureWidth(textureDimension.width);
        this.setTextureHeight(textureDimension.height);
    }

    /**
     * @return image width in pixels
     */
    public int getTextureWidth() {
        return this.width;
    }

    /**
     * @param width
     *            image width in pixels
     */
    public void setTextureWidth(int width) {
        this.width = width;
    }

    /**
     * @return the image height in pixels
     */
    public int getTextureHeight() {
        return this.height;
    }

    /**
     * @param height
     *            image height in pixels
     */
    public void setTextureHeight(int height) {
        this.height = height;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.colorDescriptor == null) ? 0 : this.colorDescriptor.hashCode());
        long temp;
        temp = Double.doubleToLongBits(this.paperReferenceMapScale);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.paperHeightInCm);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((this.tex == null) ? 0 : this.tex.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        BackgroundDescriptor other = (BackgroundDescriptor) obj;
        if (this.colorDescriptor == null) {
            if (other.colorDescriptor != null) {
                return false;
            }
        } else if (!this.colorDescriptor.equals(other.colorDescriptor)) {
            return false;
        }
        if (Double.doubleToLongBits(this.paperReferenceMapScale) != Double.doubleToLongBits(other.paperReferenceMapScale)) {
            return false;
        }
        if (Double.doubleToLongBits(this.paperHeightInCm) != Double.doubleToLongBits(other.paperHeightInCm)) {
            return false;
        }
        if (this.tex == null) {
            if (other.tex != null) {
                return false;
            }
        } else if (!this.tex.equals(other.tex)) {
            return false;
        }
        return true;
    }

}
