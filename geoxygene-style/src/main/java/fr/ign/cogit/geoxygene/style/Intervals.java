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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author AMasse
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class Intervals {
    @XmlElement(name = "LookupValue")
    String lookupvalue;

    public String getLookupvalue() {
        return this.lookupvalue;
    }

    public void setLookupvalue(String lookupvalue) {
        this.lookupvalue = lookupvalue;
    }

    @XmlElement(name = "IntervalsPoint")
    private List<IntervalsPoint> intervalsPoint = new ArrayList<IntervalsPoint>(
            0);

    public List<IntervalsPoint> getIntervalsPoint() {
        return this.intervalsPoint;
    }
    
    public IntervalsPoint getIntervalsPoint(int index) {
        return this.intervalsPoint.get(index);
    }

    public void setIntervalsPoint(List<IntervalsPoint> intervalsPoint) {
        this.intervalsPoint = intervalsPoint;
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
                + ((this.intervalsPoint == null) ? 0
                        : this.intervalsPoint.hashCode());
        result = prime
                * result
                + ((this.lookupvalue == null) ? 0 : this.lookupvalue.hashCode());
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
        Intervals other = (Intervals) obj;
        if (this.intervalsPoint == null) {
            if (other.intervalsPoint != null) {
                return false;
            }
        } else if (!this.intervalsPoint.equals(other.intervalsPoint)) {
            return false;
        }
        if (this.lookupvalue == null) {
            if (other.lookupvalue != null) {
                return false;
            }
        } else if (!this.lookupvalue.equals(other.lookupvalue)) {
            return false;
        }
        return true;
    }
    
    public int getNbIntervalsPoint() {
        return intervalsPoint.size();
    }
}
