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
public class LinePlacement implements Placement {

    @XmlElement(name = "PerpendicularOffset")
    private float perpendicularOffset = 0f;

    /**
     * Renvoie la valeur de l'attribut perpendicularOffset.
     * 
     * @return la valeur de l'attribut perpendicularOffset
     */
    public float getPerpendicularOffset() {
        return this.perpendicularOffset;
    }

    /**
     * Affecte la valeur de l'attribut perpendicularOffset.
     * 
     * @param perpendicularOffset
     *            l'attribut perpendicularOffset à affecter
     */
    public void setPerpendicularOffset(float perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
    }

    @XmlElement(name = "IsAligned")
    private boolean aligned = true;

    public boolean isAligned() {
        return this.aligned;
    }

    public void setAligned(boolean aligned) {
        this.aligned = aligned;
    }

    @XmlElement(name = "IsRepeated")
    private boolean repeated = false;

    public boolean isRepeated() {
        return this.repeated;
    }

    public void setRepeated(boolean repeated) {
        this.repeated = repeated;
    }

    @XmlElement(name = "InitialGap")
    private float initialGap = 0f;

    public float getInitialGap() {
        return this.initialGap;
    }

    public void setInitialGap(float initialGap) {
        this.initialGap = initialGap;
    }

    @XmlElement(name = "Gap")
    private float gap = 0f;

    public float getGap() {
        return this.gap;
    }

    public void setGap(float gap) {
        this.gap = gap;
    }

    @XmlElement(name = "GeneralizeLine")
    private boolean generalizeLine = false;

    public boolean isGeneralizeLine() {
        return this.generalizeLine;
    }

    public void setGeneralizeLine(boolean generalizeLine) {
        this.generalizeLine = generalizeLine;
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
        result = prime * result + (this.aligned ? 1231 : 1237);
        result = prime * result + Float.floatToIntBits(this.gap);
        result = prime * result + (this.generalizeLine ? 1231 : 1237);
        result = prime * result + Float.floatToIntBits(this.initialGap);
        result = prime * result
                + Float.floatToIntBits(this.perpendicularOffset);
        result = prime * result + (this.repeated ? 1231 : 1237);
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
        LinePlacement other = (LinePlacement) obj;
        if (this.aligned != other.aligned) {
            return false;
        }
        if (Float.floatToIntBits(this.gap) != Float.floatToIntBits(other.gap)) {
            return false;
        }
        if (this.generalizeLine != other.generalizeLine) {
            return false;
        }
        if (Float.floatToIntBits(this.initialGap) != Float
                .floatToIntBits(other.initialGap)) {
            return false;
        }
        if (Float.floatToIntBits(this.perpendicularOffset) != Float
                .floatToIntBits(other.perpendicularOffset)) {
            return false;
        }
        if (this.repeated != other.repeated) {
            return false;
        }
        return true;
    }

}
