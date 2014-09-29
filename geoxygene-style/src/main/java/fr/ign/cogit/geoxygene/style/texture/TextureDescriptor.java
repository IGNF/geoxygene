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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import fr.ign.cogit.geoxygene.style.Fill2DDescriptor;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class TextureDescriptor extends Fill2DDescriptor {

    @XmlAttribute(name = "xRepeat")
    private boolean xRepeat = false;
    @XmlAttribute(name = "yRepeat")
    private boolean yRepeat = false;

    // private int width, height; // image texture dimension (in pixels)
    private TextureDrawingMode textureDrawingMode = TextureDrawingMode.VIEWPORTSPACE;
    // private BufferedImage textureImage = null;
    //
    // // image dimension (in world coordinates)
    // // @XmlElement(name = "Dimension")
    // private DimensionDescriptor dimension = new DimensionDescriptor();

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
    public TextureDescriptor() {
        this.scaleFactor.setX(1.);
        this.scaleFactor.setY(1.);
    }

    /**
     * default constructor for
     */
    public TextureDescriptor(TextureDrawingMode textureDrawingMode) {
        this();
        this.textureDrawingMode = textureDrawingMode;
    }

    /**
     * @return the xRepeat
     */
    public boolean isRepeatedX() {
        return this.xRepeat;
    }

    /**
     * @param xRepeat
     *            the xRepeat to set
     */
    public void setxRepeat(boolean xRepeat) {
        this.xRepeat = xRepeat;
    }

    /**
     * @return the yRepeat
     */
    public boolean isRepeatedY() {
        return this.yRepeat;
    }

    /**
     * @param yRepeat
     *            the yRepeat to set
     */
    public void setyRepeat(boolean yRepeat) {
        this.yRepeat = yRepeat;
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
    //
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

    // /**
    // * Set the texture image size in pixels. Shortcut to
    // setWidth()/setHeight()
    // */
    // public void setTextureDimension(int width, int height) {
    // this.setTextureWidth(width);
    // this.setTextureHeight(height);
    // }
    //
    // /**
    // * Set the texture image size in pixels. Shortcut to
    // setWidth()/setHeight()
    // */
    // public void setTextureDimension(Dimension textureDimension) {
    // this.setTextureWidth(textureDimension.width);
    // this.setTextureHeight(textureDimension.height);
    // }
    //
    // /**
    // * @return image width in pixels
    // */
    // public int getTextureWidth() {
    // return this.width;
    // }
    //
    // /**
    // * @param width
    // * image width in pixels
    // */
    // public void setTextureWidth(int width) {
    // this.width = width;
    // }
    //
    // /**
    // * @return the image height in pixels
    // */
    // public int getTextureHeight() {
    // return this.height;
    // }
    //
    // /**
    // * @param height
    // * image height in pixels
    // */
    // public void setTextureHeight(int height) {
    // this.height = height;
    // }

    // /**
    // * @return the texture dimension in world coordinates
    // */
    // public DimensionDescriptor getDimension() {
    // return this.dimension;
    // }
    //
    // /**
    // * @param dimension
    // * texture dimension in world coordinates (0 if internally
    // * computed or screen space)
    // */
    // public void setDimension(DimensionDescriptor dimension) {
    // this.dimension = dimension;
    // }

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
                + ((this.displacement == null) ? 0 : this.displacement
                        .hashCode());
        result = prime * result
                + ((this.rotation == null) ? 0 : this.rotation.hashCode());
        result = prime
                * result
                + ((this.scaleFactor == null) ? 0 : this.scaleFactor.hashCode());
        result = prime
                * result
                + ((this.textureDrawingMode == null) ? 0
                        : this.textureDrawingMode.ordinal());
        result = prime * result + (this.xRepeat ? 1231 : 1237);
        result = prime * result + (this.yRepeat ? 1231 : 1237);
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
        TextureDescriptor other = (TextureDescriptor) obj;
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
        if (this.textureDrawingMode != other.textureDrawingMode) {
            return false;
        }
        if (this.xRepeat != other.xRepeat) {
            return false;
        }
        if (this.yRepeat != other.yRepeat) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TextureDescriptor [xRepeat=" + this.xRepeat + ", yRepeat="
                + this.yRepeat + ", textureDrawingMode="
                + this.textureDrawingMode + ", displacement="
                + this.displacement + ", scaleFactor=" + this.scaleFactor
                + ", rotation=" + this.rotation + "]";
    }

}
