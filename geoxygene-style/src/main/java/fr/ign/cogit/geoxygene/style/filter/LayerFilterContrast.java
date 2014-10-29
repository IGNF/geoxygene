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

package fr.ign.cogit.geoxygene.style.filter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
/*
 * @XmlType(name = "", propOrder = { "name", //"description",
 * "layerFeatureConstraints", "styles" })
 */
@XmlRootElement(name = "ContrastFilter")
public class LayerFilterContrast extends LayerFilter {

    @XmlElement(name = "Contrast", required = true)
    private double contrast = 1.;
    @XmlElement(name = "Brightness", required = true)
    private double luminosity = 0;
    @XmlElement(name = "Gamma", required = true)
    private double gamma = 2.2;

    /**
	 */
    public LayerFilterContrast() {
        super();
    }

    /**
     * @return the contrast
     */
    public double getContrast() {
        return this.contrast;
    }

    /**
     * @param contrast
     *            the contrast to set
     */
    public void setContrast(double contrast) {
        this.contrast = contrast;
    }

    /**
     * @return the luminosity
     */
    public double getLuminosity() {
        return this.luminosity;
    }

    /**
     * @param luminosity
     *            the luminosity to set
     */
    public void setLuminosity(double luminosity) {
        this.luminosity = luminosity;
    }

    /**
     * @return the gamma
     */
    public double getGamma() {
        return this.gamma;
    }

    /**
     * @param gamma
     *            the gamma to set
     */
    public void setGamma(double gamma) {
        this.gamma = gamma;
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
        temp = Double.doubleToLongBits(this.contrast);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.gamma);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.luminosity);
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
        LayerFilterContrast other = (LayerFilterContrast) obj;
        if (Double.doubleToLongBits(this.contrast) != Double
                .doubleToLongBits(other.contrast)) {
            return false;
        }
        if (Double.doubleToLongBits(this.gamma) != Double
                .doubleToLongBits(other.gamma)) {
            return false;
        }
        if (Double.doubleToLongBits(this.luminosity) != Double
                .doubleToLongBits(other.luminosity)) {
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
        return "LayerFilterContrast [contrast=" + this.contrast
                + ", luminosity=" + this.luminosity + ", gamma=" + this.gamma
                + "]";
    }

}
