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
public class Halo {
    @XmlElement(name = "Radius")
    private float radius = 1.0f;

    /**
     * Renvoie la valeur de l'attribut radius.
     * 
     * @return la valeur de l'attribut radius
     */
    public float getRadius() {
        return this.radius;
    }

    /**
     * Affecte la valeur de l'attribut radius.
     * 
     * @param radius
     *            l'attribut radius à affecter
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    @XmlElement(name = "Fill")
    private Fill fill = null;

    /**
     * @return the Fill properties to be used for drawing this Halo
     */
    public Fill getFill() {
        return this.fill;
    }

    /**
     * @param fill
     */
    public void setFill(Fill fill) {
        this.fill = fill;
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
        result = prime * result
                + ((this.fill == null) ? 0 : this.fill.hashCode());
        result = prime * result + Float.floatToIntBits(this.radius);
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
        Halo other = (Halo) obj;
        if (this.fill == null) {
            if (other.fill != null) {
                return false;
            }
        } else if (!this.fill.equals(other.fill)) {
            return false;
        }
        if (Float.floatToIntBits(this.radius) != Float
                .floatToIntBits(other.radius)) {
            return false;
        }
        return true;
    }

}
