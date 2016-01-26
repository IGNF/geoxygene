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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fr.ign.cogit.geoxygene.style.colorimetry.ColorJaxbAdaptor;

/**
 * 
 * 
 * @author JeT
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="GradientTexture")
public class BinaryGradientImageDescriptor extends Texture {

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

    // border color
    @XmlJavaTypeAdapter(ColorJaxbAdaptor.class)
    @XmlElement(name = "BorderColor")
    private Color borderColor = Color.green;

    // color 1
    @XmlJavaTypeAdapter(ColorJaxbAdaptor.class)
    @XmlElement(name = "Color1")
    private Color color1 = Color.white;

    // color 2
    @XmlJavaTypeAdapter(ColorJaxbAdaptor.class)
    @XmlElement(name = "Color2")
    private Color color2 = Color.black;

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
     * 
     * @return state telling if maxCoastLineIsEnabled, and so if the orientation field 
     * should be taken into account to generate the gradient, or not.
     */
    public boolean isMaxCoastLineEnabled() {
      return this.maxCoastlineLength != Double.POSITIVE_INFINITY;
    }

    /**
     * @param maxCoastlineLength
     *            the maxCoastlineLength to set
     */
    public void setMaxCoastlineLength(double maxCoastlineLength) {
        this.maxCoastlineLength = maxCoastlineLength;
    }

    public Color getBorderColor() {
        return this.borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getColor1() {
        return this.color1;
    }

    public void setColor1(Color color1) {
        this.color1 = color1;
    }

    public Color getColor2() {
        return this.color2;
    }

    public void setColor2(Color color2) {
        this.color2 = color2;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.blurSize;
        result = prime
                * result
                + ((this.borderColor == null) ? 0 : this.borderColor.hashCode());
        result = prime * result
                + ((this.color1 == null) ? 0 : this.color1.hashCode());
        result = prime * result
                + ((this.color2 == null) ? 0 : this.color2.hashCode());
        long temp;
        temp = Double.doubleToLongBits(this.mapScale);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.maxCoastlineLength);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.textureResolution);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        BinaryGradientImageDescriptor other = (BinaryGradientImageDescriptor) obj;
        if (this.blurSize != other.blurSize)
            return false;
        if (this.borderColor == null) {
            if (other.borderColor != null)
                return false;
        } else if (!this.borderColor.equals(other.borderColor))
            return false;
        if (this.color1 == null) {
            if (other.color1 != null)
                return false;
        } else if (!this.color1.equals(other.color1))
            return false;
        if (this.color2 == null) {
            if (other.color2 != null)
                return false;
        } else if (!this.color2.equals(other.color2))
            return false;
        if (Double.doubleToLongBits(this.mapScale) != Double
                .doubleToLongBits(other.mapScale))
            return false;
        if (Double.doubleToLongBits(this.maxCoastlineLength) != Double
                .doubleToLongBits(other.maxCoastlineLength))
            return false;
        if (Double.doubleToLongBits(this.textureResolution) != Double
                .doubleToLongBits(other.textureResolution))
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BinaryGradientImageDescriptor [textureResolution="
                + this.textureResolution + ", mapScale=" + this.mapScale
                + ", maxCoastlineLength=" + this.maxCoastlineLength
                + ", blurSize=" + this.blurSize + "]";
    }

}
