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
public class PointPlacement implements Placement {

    @XmlElement(name = "AnchorPoint")
    private AnchorPoint anchorPoint = null;

    /**
     * Renvoie la valeur de l'attribut anchorPoint.
     * 
     * @return la valeur de l'attribut anchorPoint
     */
    public AnchorPoint getAnchorPoint() {
        return this.anchorPoint;
    }

    /**
     * Affecte la valeur de l'attribut anchorPoint.
     * 
     * @param anchorPoint
     *            l'attribut anchorPoint à affecter
     */
    public void setAnchorPoint(AnchorPoint anchorPoint) {
        this.anchorPoint = anchorPoint;
    }

    @XmlElement(name = "Displacement")
    private Displacement displacement = null;

    /**
     * Renvoie la valeur de l'attribut displacement.
     * 
     * @return la valeur de l'attribut displacement
     */
    public Displacement getDisplacement() {
        return this.displacement;
    }

    /**
     * Affecte la valeur de l'attribut displacement.
     * 
     * @param displacement
     *            l'attribut displacement à affecter
     */
    public void setDisplacement(Displacement displacement) {
        this.displacement = displacement;
    }

    @XmlElement(name = "Rotation")
    private float rotation = 0.0f;

    /**
     * Renvoie la valeur de l'attribut rotation.
     * 
     * @return la valeur de l'attribut rotation
     */
    public float getRotation() {
        return this.rotation;
    }

    /**
     * Affecte la valeur de l'attribut rotation.
     * 
     * @param rotation
     *            l'attribut rotation à affecter
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
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
                + ((this.anchorPoint == null) ? 0 : this.anchorPoint.hashCode());
        result = prime
                * result
                + ((this.displacement == null) ? 0 : this.displacement
                        .hashCode());
        result = prime * result + Float.floatToIntBits(this.rotation);
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
        PointPlacement other = (PointPlacement) obj;
        if (this.anchorPoint == null) {
            if (other.anchorPoint != null) {
                return false;
            }
        } else if (!this.anchorPoint.equals(other.anchorPoint)) {
            return false;
        }
        if (this.displacement == null) {
            if (other.displacement != null) {
                return false;
            }
        } else if (!this.displacement.equals(other.displacement)) {
            return false;
        }
        if (Float.floatToIntBits(this.rotation) != Float
                .floatToIntBits(other.rotation)) {
            return false;
        }
        return true;
    }
}
