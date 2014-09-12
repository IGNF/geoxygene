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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AnchorPoint {
    @XmlElement(name = "AnchorPointX")
    private float x = 0.5f;

    /**
     * Renvoie la valeur de l'attribut x.
     * 
     * @return la valeur de l'attribut x
     */
    public float getAnchorPointX() {
        return this.x;
    }

    /**
     * Affecte la valeur de l'attribut x.
     * 
     * @param x
     *            l'attribut x à affecter
     */
    public void setAnchorPointX(float x) {
        this.x = x;
    }

    @XmlElement(name = "AnchorPointY")
    private float y = 0.5f;

    /**
     * Renvoie la valeur de l'attribut y.
     * 
     * @return la valeur de l'attribut y
     */
    public float getAnchorPointY() {
        return this.y;
    }

    /**
     * Affecte la valeur de l'attribut y.
     * 
     * @param y
     *            l'attribut y à affecter
     */
    public void setAnchorPointY(float y) {
        this.y = y;
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
        result = prime * result + Float.floatToIntBits(this.x);
        result = prime * result + Float.floatToIntBits(this.y);
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
        AnchorPoint other = (AnchorPoint) obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        return true;
    }

}
