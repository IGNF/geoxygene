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

package fr.ign.cogit.geoxygene.style.expressive;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import fr.ign.cogit.geoxygene.style.Fill2DDescriptor;

/**
 * @author JeT
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public class GradientSubshaderDescriptor extends Fill2DDescriptor {

    @XmlElements({ @XmlElement(name = "UserShader", type = UserFill2DShaderDescriptor.class) })
    private Fill2DShaderDescriptor shader = new DefaultFill2DShaderDescriptor();

    @XmlElement(name = "Resolution")
    private double textureResolution = 72; // texture resolution in DPI

    @XmlElement(name = "MapScale")
    private double mapScale = 100000; // map scale value 1:MapScale

    // coast geometry segments greater than this value won't be considered as
    // coast lines
    @XmlElement(name = "MaxCoastlineLength")
    private double maxCoastlineLength = Double.POSITIVE_INFINITY;

    // size of the bluring filter
    @XmlElement(name = "BlurSize")
    private int blurSize = 2;


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

    /**
     * @return the maxCoastlineLength
     */
    public double getMaxCoastlineLength() {
        return this.maxCoastlineLength;
    }

    /**
     * @return the mapScale
     */
    public double getMapScale() {
        return this.mapScale;
    }

    /**
     * @param mapScale
     *            the mapScale to set
     */
    public void setMapScale(double mapScale) {
        this.mapScale = mapScale;
    }

    /**
     * @param maxCoastlineLength
     *            the maxCoastlineLength to set
     */
    public void setMaxCoastlineLength(double maxCoastlineLength) {
        this.maxCoastlineLength = maxCoastlineLength;
    }

    /**
     * @return the shader
     */
    public Fill2DShaderDescriptor getShaderDescriptor() {
        return this.shader;
    }

    /**
     * @param shader
     *            the shader to set
     */
    public void setShaderDescriptor(Fill2DShaderDescriptor shader) {
        this.shader = shader;
    }

    /**
     * @return the blurSize
     */
    public int getBlurSize() {
        return this.blurSize;
    }

    /**
     * @param blurSize
     *            the blurSize to set
     */
    public void setBlurSize(int blurSize) {
        this.blurSize = blurSize;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + blurSize;
        long temp;
        temp = Double.doubleToLongBits(mapScale);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(maxCoastlineLength);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((shader == null) ? 0 : shader.hashCode());
        temp = Double.doubleToLongBits(textureResolution);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GradientSubshaderDescriptor other = (GradientSubshaderDescriptor) obj;
        if (blurSize != other.blurSize)
            return false;
        if (Double.doubleToLongBits(mapScale) != Double
                .doubleToLongBits(other.mapScale))
            return false;
        if (Double.doubleToLongBits(maxCoastlineLength) != Double
                .doubleToLongBits(other.maxCoastlineLength))
            return false;
        if (shader == null) {
            if (other.shader != null)
                return false;
        } else if (!shader.equals(other.shader))
            return false;
        if (Double.doubleToLongBits(textureResolution) != Double
                .doubleToLongBits(other.textureResolution))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GradientSubshaderDescriptor [shader=" + shader
                + ", textureResolution=" + textureResolution + ", mapScale="
                + mapScale + ", maxCoastlineLength=" + maxCoastlineLength
                + ", blurSize=" + blurSize + "]";
    }


}
