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

/**
 * @author JeT
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public class BasicTextureExpressiveRenderingDescriptor extends
        ExpressiveRenderingDescriptor {

    @XmlElement(name = "TransitionSize")
    private double transitionSize = 10;

    @XmlElement(name = "PaperTexture")
    private String paperTextureFilename = "./src/main/resources/textures/papers/black-normalized.png";
    @XmlElement(name = "BrushTexture")
    private String brushTextureFilename = "./src/main/resources/textures/brushes/chalk2-100-200.png";
    @XmlElement(name = "BrushStartLength")
    private int brushStartLength = 100;
    @XmlElement(name = "BrushEndLength")
    private int brushEndLength = 200;
    @XmlElement(name = "BrushAspectRatio")
    private double brushAspectRatio = 8;
    @XmlElement(name = "PaperSizeInCm")
    private double paperSizeInCm = 4;
    @XmlElement(name = "PaperReferenceMapScale")
    private double paperReferenceMapScale = 100000;
    @XmlElement(name = "PaperRoughness")
    private double paperDensity = 0.7;
    @XmlElement(name = "BrushRoughness")
    private double brushDensity = 1.9;
    @XmlElement(name = "StrokePressure")
    private double strokePressure = 2.64;
    @XmlElement(name = "StrokeSoftness")
    private double sharpness = 0.1;

    @XmlElements({
            @XmlElement(name = "DefaultShader", type = DefaultLineShaderDescriptor.class),
            @XmlElement(name = "RandomShader", type = RandomVariationShaderDescriptor.class),
            @XmlElement(name = "UserShader", type = UserLineShaderDescriptor.class) })
    private ShaderDescriptor shader = new DefaultLineShaderDescriptor();

    /**
     * @return the transitionSize
     */
    public double getTransitionSize() {
        return this.transitionSize;
    }

    /**
     * @param transitionSize
     *            the transitionSize to set
     */
    public void setTransitionSize(double transitionSize) {
        this.transitionSize = transitionSize;
    }

    /**
     * @return the shader
     */
    public ShaderDescriptor getShaderDescriptor() {
        return this.shader;
    }

    /**
     * @param shader
     *            the shader to set
     */
    public void setShader(RandomVariationShaderDescriptor shader) {
        this.shader = shader;
    }

    /**
     * @return the paperTextureFilename
     */
    public String getPaperTextureFilename() {
        return this.paperTextureFilename;
    }

    /**
     * @param paperTextureFilename
     *            the paperTextureFilename to set
     */
    public void setPaperTextureFilename(String paperTextureFilename) {
        this.paperTextureFilename = paperTextureFilename;
    }

    /**
     * @return the brushTextureFilename
     */
    public String getBrushTextureFilename() {
        return this.brushTextureFilename;
    }

    /**
     * @param brushTextureFilename
     *            the brushTextureFilename to set
     */
    public void setBrushTextureFilename(String brushTextureFilename) {
        this.brushTextureFilename = brushTextureFilename;
    }

    /**
     * @return the brushStartLength
     */
    public int getBrushStartLength() {
        return this.brushStartLength;
    }

    /**
     * @param brushStartLength
     *            the brushStartLength to set
     */
    public void setBrushStartLength(int brushStartLength) {
        this.brushStartLength = brushStartLength;
    }

    /**
     * @return the brushEndLength
     */
    public int getBrushEndLength() {
        return this.brushEndLength;
    }

    /**
     * @param brushEndLength
     *            the brushEndLength to set
     */
    public void setBrushEndLength(int brushEndLength) {
        this.brushEndLength = brushEndLength;
    }

    /**
     * @return the brushSize
     */
    public double getBrushAspectRatio() {
        return this.brushAspectRatio;
    }

    /**
     * @param brushSize
     *            the brushSize to set
     */
    public void setBrushAspectRatio(double brushSize) {
        this.brushAspectRatio = brushSize;
    }

    /**
     * @return the paperScaleFactor
     */
    public double getPaperSizeInCm() {
        return this.paperSizeInCm;
    }

    /**
     * @param paperSizeInCm
     *            the paperScaleFactor to set
     */
    public void setPaperSizeInCm(double paperSizeInCm) {
        this.paperSizeInCm = paperSizeInCm;
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

    /**
     * @return the paperDensity
     */
    public double getPaperDensity() {
        return this.paperDensity;
    }

    /**
     * @param paperDensity
     *            the paperDensity to set
     */
    public void setPaperDensity(double paperDensity) {
        this.paperDensity = paperDensity;
    }

    /**
     * @return the brushDensity
     */
    public double getBrushDensity() {
        return this.brushDensity;
    }

    /**
     * @param brushDensity
     *            the brushDensity to set
     */
    public void setBrushDensity(double brushDensity) {
        this.brushDensity = brushDensity;
    }

    /**
     * @return the strokePressure
     */
    public double getStrokePressure() {
        return this.strokePressure;
    }

    /**
     * @param strokePressure
     *            the strokePressure to set
     */
    public void setStrokePressure(double strokePressure) {
        this.strokePressure = strokePressure;
    }

    /**
     * @return the sharpness
     */
    public double getSharpness() {
        return this.sharpness;
    }

    /**
     * @param sharpness
     *            the sharpness to set
     */
    public void setSharpness(double sharpness) {
        this.sharpness = sharpness;
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
                + ((this.brushTextureFilename == null) ? 0
                        : this.brushTextureFilename.hashCode());
        result = prime
                * result
                + ((this.paperTextureFilename == null) ? 0
                        : this.paperTextureFilename.hashCode());
        result = prime * result
                + ((this.shader == null) ? 0 : this.shader.hashCode());
        long temp;
        temp = Double.doubleToLongBits(this.transitionSize);
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
        BasicTextureExpressiveRenderingDescriptor other = (BasicTextureExpressiveRenderingDescriptor) obj;
        if (this.brushTextureFilename == null) {
            if (other.brushTextureFilename != null) {
                return false;
            }
        } else if (!this.brushTextureFilename
                .equals(other.brushTextureFilename)) {
            return false;
        }
        if (this.paperTextureFilename == null) {
            if (other.paperTextureFilename != null) {
                return false;
            }
        } else if (!this.paperTextureFilename
                .equals(other.paperTextureFilename)) {
            return false;
        }
        if (this.shader == null) {
            if (other.shader != null) {
                return false;
            }
        } else if (!this.shader.equals(other.shader)) {
            return false;
        }
        if (Double.doubleToLongBits(this.transitionSize) != Double
                .doubleToLongBits(other.transitionSize)) {
            return false;
        }
        return true;
    }

}
