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

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fr.ign.cogit.geoxygene.style.colorimetry.ColorJaxbAdaptor;

@XmlAccessorType(XmlAccessType.NONE)
public class PerlinNoiseTextureDescriptor extends BasicTextureDescriptor {

    @XmlElement(name = "Resolution")
    private double textureResolution = 600; // texture resolution in DPI

    public PerlinNoiseTextureDescriptor() {
        super();
    }

    public PerlinNoiseTextureDescriptor(float scale, float amount, float angle,
            float stretch, Color color1, Color color2) {
        this();
        this.scale = scale;
        this.amount = amount;
        this.angle = angle;
        this.stretch = stretch;
        this.color1 = color1;
        this.color2 = color2;
    }

    @XmlElement(name = "Scale")
    private float scale = 10f;

    public float getScale() {
        return this.scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @XmlElement(name = "Amount")
    private float amount = 0.5f;

    public float getAmount() {
        return this.amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    @XmlElement(name = "Angle")
    private float angle = (float) Math.PI;

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @XmlElement(name = "Stretch")
    private float stretch = 1f;

    public float getStretch() {
        return this.stretch;
    }

    public void setStretch(float stretch) {
        this.stretch = stretch;
    }

    @XmlJavaTypeAdapter(ColorJaxbAdaptor.class)
    @XmlElement(name = "Color1")
    private Color color1 = Color.YELLOW;

    public Color getColor1() {
        return this.color1;
    }

    public void setColor1(Color color1) {
        this.color1 = color1;
    }

    @XmlJavaTypeAdapter(ColorJaxbAdaptor.class)
    @XmlElement(name = "Color2")
    private Color color2 = Color.RED;

    public Color getColor2() {
        return this.color2;
    }

    public void setColor2(Color color2) {
        this.color2 = color2;
    }

    /**
     * @return the textureResolution
     */
    public double getTextureResolution() {
        return this.textureResolution;
    }

    /**
     * @param textureResolution
     *            the textureResolution to set
     */
    public void setTextureResolution(double textureResolution) {
        this.textureResolution = textureResolution;
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
        result = prime * result + Float.floatToIntBits(this.amount);
        result = prime * result + Float.floatToIntBits(this.angle);
        result = prime * result
                + ((this.color1 == null) ? 0 : this.color1.hashCode());
        result = prime * result
                + ((this.color2 == null) ? 0 : this.color2.hashCode());
        result = prime * result + Float.floatToIntBits(this.scale);
        result = prime * result + Float.floatToIntBits(this.stretch);
        long temp;
        temp = Double.doubleToLongBits(this.textureResolution);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        PerlinNoiseTextureDescriptor other = (PerlinNoiseTextureDescriptor) obj;
        if (Float.floatToIntBits(this.amount) != Float
                .floatToIntBits(other.amount)) {
            return false;
        }
        if (Float.floatToIntBits(this.angle) != Float
                .floatToIntBits(other.angle)) {
            return false;
        }
        if (this.color1 == null) {
            if (other.color1 != null) {
                return false;
            }
        } else if (!this.color1.equals(other.color1)) {
            return false;
        }
        if (this.color2 == null) {
            if (other.color2 != null) {
                return false;
            }
        } else if (!this.color2.equals(other.color2)) {
            return false;
        }
        if (Float.floatToIntBits(this.scale) != Float
                .floatToIntBits(other.scale)) {
            return false;
        }
        if (Float.floatToIntBits(this.stretch) != Float
                .floatToIntBits(other.stretch)) {
            return false;
        }
        if (Double.doubleToLongBits(this.textureResolution) != Double
                .doubleToLongBits(other.textureResolution)) {
            return false;
        }
        return true;
    }

}
