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

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import fr.ign.cogit.geoxygene.style.expressive.ExpressiveParameterValue;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class Texture implements ExpressiveParameterValue {

    /**
     * The absolute texture location.
     */
    @XmlTransient
    private URI absolute_uri = null;

    @XmlAttribute(name = "XRepeat")
    private boolean xRepeat = false;
    @XmlAttribute(name = "YRepeat")
    private boolean yRepeat = false;

    @XmlTransient
    private TextureDrawingMode textureDrawingMode = TextureDrawingMode.VIEWPORTSPACE;

    // image displacement (in world coordinates)
    @XmlElement(name = "Displacement")
    private Point displacement = new Point();

    // image scale factor
    @XmlElement(name = "ScaleFactor")
    private Point scaleFactor = new Point();

    // rotation angle
    @XmlElement(name = "Rotation")
    private final Rotation rotation = new Rotation();

    /**
     * default constructor
     */
    public Texture() {
        this.scaleFactor.setX(1.);
        this.scaleFactor.setY(1.);
    }

    /**
     * default constructor for
     */
    public Texture(TextureDrawingMode textureDrawingMode) {
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

    /**
     * @return the rotation
     */
    public Rotation getRotation() {
        return this.rotation;
    }

    /**
     * @return the scale
     */
    public Point getScaleFactor() {
        return this.scaleFactor;
    }

    /**
     * @param scale
     *            the scale to set
     */
    public void setScaleFactor(Point scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    /**
     * @return the displacement
     */
    public Point getDisplacement() {
        return this.displacement;
    }

    /**
     * @param displacement
     *            the displacement to set
     */
    public void setDisplacement(Point displacement) {
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
        result = prime * result + ((this.displacement == null) ? 0 : this.displacement.hashCode());
        result = prime * result + ((this.rotation == null) ? 0 : this.rotation.hashCode());
        result = prime * result + ((this.scaleFactor == null) ? 0 : this.scaleFactor.hashCode());
        result = prime * result + ((this.textureDrawingMode == null) ? 0 : this.textureDrawingMode.ordinal());
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
        Texture other = (Texture) obj;
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
        return "TextureDescriptor [xRepeat=" + this.xRepeat + ", yRepeat=" + this.yRepeat + ", textureDrawingMode=" + this.textureDrawingMode + ", displacement=" + this.displacement
                + ", scaleFactor=" + this.scaleFactor + ", rotation=" + this.rotation + "]";
    }

    public void setAbsoluteURI(URI uri) {
        this.absolute_uri = uri;
    }

    public URI getAbsoluteURI() {
        return this.absolute_uri;
    }

}
