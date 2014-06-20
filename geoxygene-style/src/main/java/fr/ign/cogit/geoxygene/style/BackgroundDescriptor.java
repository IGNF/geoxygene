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
import java.awt.image.BufferedImage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import fr.ign.cogit.geoxygene.style.texture.DimensionDescriptor;
import fr.ign.cogit.geoxygene.style.texture.PointDescriptor;
import fr.ign.cogit.geoxygene.style.texture.RotationDescriptor;

/**
 * @author JeT
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BackgroundDescriptor {
    @XmlElement(name = "url")
    private String url = null;

    @XmlTransient
    private int width, height; // image texture dimension (in pixels)
    @XmlTransient
    private BufferedImage textureImage = null;

    /**
     * The raw color of the stroke, without opacity information.
     */
    @XmlTransient
    private Color color = null;

    @XmlElement(name = "Color")
    private String colorDescriptor = "#FFFFFF";

    // image dimension (in world coordinates)
    private DimensionDescriptor dimension = new DimensionDescriptor();

    // image displacement (in world coordinates)
    @XmlElement(name = "Displacement")
    private PointDescriptor displacement = new PointDescriptor();

    // image scale factor
    @XmlElement(name = "ScaleFactor")
    private PointDescriptor scaleFactor = new PointDescriptor();

    // rotation angle
    @XmlElement(name = "Rotation")
    private final RotationDescriptor rotation = new RotationDescriptor();

    /**
     * default constructor
     */
    public BackgroundDescriptor() {
        super();
    }

    /**
     * constructor
     * 
     * @param url
     *            url to the texture image location
     */
    public BackgroundDescriptor(String url) {
        this();
        this.url = url;
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

    /**
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * invalidate the precomputed image texture and force recomputation. For
     * async generation this method should be overridden to stop potential
     * in-progress generation
     */
    public void invalidateTexture() {
        this.setTextureImage(null);
    }

    /**
     * This set method breaks the object encapsulation paradigm. It is due to
     * geoxygene Style module independence. Textures have to be declared into
     * "Style" module but the implementation of how texture are generated and
     * visualized must not be in style (they can have dependencies to appli or
     * other modules). The generation has to be external and use this set method
     * (carefully)
     * 
     * @param textureImage
     *            the Texture Image to set
     */
    synchronized public void setTextureImage(BufferedImage textureImage) {
        this.textureImage = textureImage;
        if (this.textureImage != null) {
            this.setTextureDimension(this.textureImage.getWidth(),
                    this.textureImage.getHeight());
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
     * @return the rotation
     */
    public RotationDescriptor getRotation() {
        return this.rotation;
    }

    /**
     * @return the scale
     */
    public PointDescriptor getScaleFactor() {
        return this.scaleFactor;
    }

    /**
     * @param scale
     *            the scale to set
     */
    public void setScaleFactor(PointDescriptor scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

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

    /**
     * @return the texture dimension in world coordinates
     */
    public DimensionDescriptor getDimension() {
        return this.dimension;
    }

    /**
     * @param dimension
     *            texture dimension in world coordinates (0 if internally
     *            computed or screen space)
     */
    public void setDimension(DimensionDescriptor dimension) {
        this.dimension = dimension;
    }

    /**
     * @return the displacement
     */
    public PointDescriptor getDisplacement() {
        return this.displacement;
    }

    /**
     * @param displacement
     *            the displacement to set
     */
    public void setDisplacement(PointDescriptor displacement) {
        this.displacement = displacement;
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
        result = prime
                * result
                + ((this.colorDescriptor == null) ? 0 : this.colorDescriptor
                        .hashCode());
        result = prime * result
                + ((this.dimension == null) ? 0 : this.dimension.hashCode());
        result = prime
                * result
                + ((this.displacement == null) ? 0 : this.displacement
                        .hashCode());
        result = prime * result
                + ((this.rotation == null) ? 0 : this.rotation.hashCode());
        result = prime
                * result
                + ((this.scaleFactor == null) ? 0 : this.scaleFactor.hashCode());
        result = prime * result
                + ((this.url == null) ? 0 : this.url.hashCode());
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
        if (this.dimension == null) {
            if (other.dimension != null) {
                return false;
            }
        } else if (!this.dimension.equals(other.dimension)) {
            return false;
        }
        if (this.displacement == null) {
            if (other.displacement != null) {
                return false;
            }
        } else if (!this.displacement.equals(other.displacement)) {
            return false;
        }
        if (this.rotation == null) {
            if (other.rotation != null) {
                return false;
            }
        } else if (!this.rotation.equals(other.rotation)) {
            return false;
        }
        if (this.scaleFactor == null) {
            if (other.scaleFactor != null) {
                return false;
            }
        } else if (!this.scaleFactor.equals(other.scaleFactor)) {
            return false;
        }
        if (this.url == null) {
            if (other.url != null) {
                return false;
            }
        } else if (!this.url.equals(other.url)) {
            return false;
        }
        return true;
    }

}
