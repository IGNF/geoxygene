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

package fr.ign.cogit.geoxygene.style.texture;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author JeT
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public class RotationDescriptor {
    @XmlAttribute(name = "angle")
    private double angle = 0.; // angle in degrees

    /**
     * Default constructor
     */
    public RotationDescriptor() {
        super();
    }

    /**
     * @param angle
     *            in degrees
     */
    public RotationDescriptor(double angle) {
        super();
        this.setAngleInDegrees(angle);
    }

    /**
     * @return the angle
     */
    public double getAngle() {
        return this.angle;
    }

    /**
     * @param angle
     *            the angle to set
     */
    public void setAngleInDegrees(double angle) {
        this.angle = angle;
    }

    /**
     * @param angle
     *            the angle to set
     */
    public void setAngleInRadians(double angle) {
        this.angle = angle / Math.PI * 180.;
    }

    public double getAngleInRadians() {
        return this.angle * Math.PI / 180.;
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
        temp = Double.doubleToLongBits(this.angle);
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
        RotationDescriptor other = (RotationDescriptor) obj;
        if (Double.doubleToLongBits(this.angle) != Double.doubleToLongBits(other.angle)) {
            return false;
        }
        return true;
    }

}
