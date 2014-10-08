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
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * 
 * @author JeT
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public class BinaryGradientImageDescriptor extends TextureDescriptor {

    @XmlElement(name = "Resolution")
    private double textureResolution = 72; // texture resolution in DPI

    @XmlElement(name = "MapScale")
    private double mapScale = 100000; // map scale value 1:MapScale

    // coast geometry segments greater than this value won't be considered as
    // coast lines
    @XmlElement(name = "MaxCoastlineLength")
    private double maxCoastlineLength = Double.POSITIVE_INFINITY;

    /**
     * default constructor
     */
    public BinaryGradientImageDescriptor() {
        super(TextureDrawingMode.VIEWPORTSPACE);
    }

    /**
     * constructor
     * 
     * @param url
     *            url to the texture image location
     */
    public BinaryGradientImageDescriptor(String url) {
        this();
    }

    public BinaryGradientImageDescriptor(TextureDrawingMode drawingMode) {
        super(drawingMode);
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(this.mapScale);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.maxCoastlineLength);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        BinaryGradientImageDescriptor other = (BinaryGradientImageDescriptor) obj;
        if (Double.doubleToLongBits(this.mapScale) != Double
                .doubleToLongBits(other.mapScale)) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxCoastlineLength) != Double
                .doubleToLongBits(other.maxCoastlineLength)) {
            return false;
        }
        if (Double.doubleToLongBits(this.textureResolution) != Double
                .doubleToLongBits(other.textureResolution)) {
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
        return "GradientTextureDescriptor [textureResolution="
                + this.textureResolution + ", mapScale=" + this.mapScale
                + ", maxCoastlineLength=" + this.maxCoastlineLength + "]";
    }

}
