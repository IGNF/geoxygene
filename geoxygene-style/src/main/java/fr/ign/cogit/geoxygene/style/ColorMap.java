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

@XmlAccessorType(XmlAccessType.FIELD)
public class ColorMap {

    @XmlElement(name = "PropertyName")
    String propertyName = null;
    
    // ColorMap version Categorize
    @XmlElement(name = "Categorize")
    Categorize categorize = null;
    
    public Categorize getCategorize() {
        return categorize;
    }
    
    public void setCategorize(Categorize categorize) {
        this.categorize = categorize;
    }

    // ColorMap version Interpolate
    @XmlElement(name = "Interpolate")
    Interpolate interpolate = null;
    
    public Interpolate getInterpolate() {
        return this.interpolate;
    }

    public void setInterpolate(Interpolate interpolate) {
        this.interpolate = interpolate;
    }

    // TODO ColorMap version Intervals
    @XmlElement(name = "Intervals")
    Intervals intervals = null;
    
    public Intervals getIntervals() {
        return this.intervals;
    }

    public void setIntervals(Intervals intervals) {
        this.intervals = intervals;
    }
    
    @XmlTransient
    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

   

    public int getColor(double value) {
        if (this.interpolate != null) {
            InterpolationPoint previous = null;
            for (InterpolationPoint point : this.interpolate
                    .getInterpolationPoint()) {
                if (value <= point.getData()) {
                    if (previous == null) {
                        return point.getColor().getRGB();
                    }
                    return this.interpolateColor(value, previous.getData(),
                            previous.getColor(), point.getData(),
                            point.getColor()).getRGB();
                }
                previous = point;
            }
            return previous.getColor().getRGB();
        }
        return 0;
    }
       

    private Color interpolateColor(double value, double data1, Color color1,
            double data2, Color color2) {
        double r1 = color1.getRed();
        double g1 = color1.getGreen();
        double b1 = color1.getBlue();
        double r2 = color2.getRed();
        double g2 = color2.getGreen();
        double b2 = color2.getBlue();
        return new Color(
                (float) this.interpolate(value, data1, r1, data2, r2) / 255f,
                (float) this.interpolate(value, data1, g1, data2, g2) / 255f,
                (float) this.interpolate(value, data1, b1, data2, b2) / 255f);
    }

    private double interpolate(double value, double data1, double value1,
            double data2, double value2) {
        return value1 + (value - data1) * (value2 - value1) / (data2 - data1);
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
                + ((this.interpolate == null) ? 0 : this.interpolate.hashCode());
        result = prime
                * result
                + ((this.propertyName == null) ? 0 : this.propertyName
                        .hashCode());
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
        ColorMap other = (ColorMap) obj;
        if (this.interpolate == null) {
            if (other.interpolate != null) {
                return false;
            }
        } else if (!this.interpolate.equals(other.interpolate)) {
            return false;
        }
        if (this.propertyName == null) {
            if (other.propertyName != null) {
                return false;
            }
        } else if (!this.propertyName.equals(other.propertyName)) {
            return false;
        }
        return true;
    }
    
    public boolean isCategorize() {
        if (categorize!=null) {
            return true;
        } else {
            return false;
        }      
    }
    
    public boolean isInterpolate() {
        if (interpolate!=null) {
            return true;
        } else {
            return false;
        }
    }

}
