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
import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author JeT
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ParameterDescriptorFloat extends ParameterDescriptor {

    @XmlAttribute
    private String name = "x";

    @XmlAttribute
    private float value = 0f;

    @XmlAttribute
    private String description = "no description";

    @XmlAttribute
    private float min = 0f;

    @XmlAttribute
    private float max = 1f;

    @XmlAttribute
    private float increment = 0.01f;

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the value
     */
    public float getValue() {
        return this.value;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the min
     */
    public float getMin() {
        return this.min;
    }

    /**
     * @param min
     *            the min to set
     */
    public void setMin(float min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public float getMax() {
        return this.max;
    }

    /**
     * @param max
     *            the max to set
     */
    public void setMax(float max) {
        this.max = max;
    }

    /**
     * @return the increment
     */
    public float getIncrement() {
        return this.increment;
    }

    /**
     * @param increment
     *            the increment to set
     */
    public void setIncrement(float increment) {
        this.increment = increment;
    }

    // it is completely normal that hashCode and equals are not overloaded
    // when objects value change, object are the same, and a onParameterChange
    // event is thrown
}
