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

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * 
 * @author AMasse
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class IntervalsPoint {
    public IntervalsPoint() {
    }

    public IntervalsPoint(double data, String value) {
        this.data = data;
        this.value = value;
    }

    public IntervalsPoint(double data, Color color) {
        this.data = data;
        String rgb = Integer.toHexString(color.getRGB());
        rgb = "#" + rgb.substring(2, rgb.length()); //$NON-NLS-1$
        this.value = rgb;
        this.color = color;
    }

    @XmlElement(name = "Data")
    private double data;

    public double getData() {
        return this.data;
    }

    public void setData(double data) {
        this.data = data;
    }

    @XmlElement(name = "Value")
    private String value;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @XmlTransient
    private Color color = null;

    public Color getColor() {
        if (this.color == null) {
            this.color = Color.decode(this.value);
        }
        return this.color;
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
                + ((this.color == null) ? 0 : this.color.hashCode());
        long temp;
        temp = Double.doubleToLongBits(this.data);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result
                + ((this.value == null) ? 0 : this.value.hashCode());
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
        IntervalsPoint other = (IntervalsPoint) obj;
        if (this.color == null) {
            if (other.color != null) {
                return false;
            }
        } else if (!this.color.equals(other.color)) {
            return false;
        }
        if (Double.doubleToLongBits(this.data) != Double
                .doubleToLongBits(other.data)) {
            return false;
        }
        if (this.value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!this.value.equals(other.value)) {
            return false;
        }
        return true;
    }
}
