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

/**
 * @author JeT
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public class BasicTextureExpressiveRendering extends ExpressiveRendering {

    @XmlElement(name = "BrushTexture")
    private String brushTextureFilename = "./src/main/resources/textures/brushes/chalk2-100-200.png";
    @XmlElement(name = "AspectRatio")
    private double aspectRation = 8;
    @XmlElement(name = "TransitionSize")
    private double transitionSize = 10;

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
     * @return the aspectRation
     */
    public double getAspectRation() {
        return this.aspectRation;
    }

    /**
     * @param aspectRation
     *            the aspectRation to set
     */
    public void setAspectRation(double aspectRation) {
        this.aspectRation = aspectRation;
    }

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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(this.aspectRation);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime
                * result
                + ((this.brushTextureFilename == null) ? 0
                        : this.brushTextureFilename.hashCode());
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
        BasicTextureExpressiveRendering other = (BasicTextureExpressiveRendering) obj;
        if (Double.doubleToLongBits(this.aspectRation) != Double
                .doubleToLongBits(other.aspectRation)) {
            return false;
        }
        if (this.brushTextureFilename == null) {
            if (other.brushTextureFilename != null) {
                return false;
            }
        } else if (!this.brushTextureFilename
                .equals(other.brushTextureFilename)) {
            return false;
        }
        if (Double.doubleToLongBits(this.transitionSize) != Double
                .doubleToLongBits(other.transitionSize)) {
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
        return "BasicTextureExpressiveRendering [brushTextureFilename="
                + this.brushTextureFilename + ", aspectRation="
                + this.aspectRation + ", transitionSize=" + this.transitionSize
                + "]";
    }

}
