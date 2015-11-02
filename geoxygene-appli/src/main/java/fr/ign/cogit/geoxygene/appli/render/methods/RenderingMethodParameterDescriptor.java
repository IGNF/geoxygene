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

package fr.ign.cogit.geoxygene.appli.render.methods;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

import fr.ign.cogit.geoxygene.style.SvgParameter;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveParameter;

@XmlAccessorType(XmlAccessType.FIELD)
/**
 * RenderingMethodParameterDescriptor are identified by their name.
 * 
 * @author bdumenieu
 *
 */
public class RenderingMethodParameterDescriptor {

    @XmlElement(name = "Description")
    private String description = "";

    @XmlElement(name = "UniformRef")
    private String uniform_name = null;

    @XmlElement(name = "Name")
    private String name;

    @XmlElements({ @XmlElement(name = "Default", type = SvgParameter.class), @XmlElement(name = "Default", type = ExpressiveParameter.class) })
    private Object pdefault;

    @XmlAttribute(name = "required")
    private boolean required = false;

    @XmlElementWrapper(name = "Restrictions")
    @XmlElements({ @XmlElement(name = "Restriction", type = BoundsRestriction.class), @XmlElement(name = "Restriction", type = ChoiceRestriction.class) })
    private List<RenderingMethodParameterRestriction> restrictions = new ArrayList<RenderingMethodParameterRestriction>();

    @XmlElement(name = "Type")
    private String type;

    public RenderingMethodParameterDescriptor() {
    }

    public RenderingMethodParameterDescriptor(String _name) {
        this.name = _name;
    }

    public boolean isRequired() {
        return this.required;
    }

    public Object getDefaultValue() {
        return this.pdefault;
    }

    public String getName() {
        return this.name;
    }

    public String getParameterUniformName() {
        if (this.uniform_name == null)
            return this.name;
        return this.uniform_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUniform_name() {
        return uniform_name;
    }

    public void setUniform_name(String uniform_name) {
        this.uniform_name = uniform_name;
    }

    public Object getPdefault() {
        return pdefault;
    }

    public void setPdefault(Object pdefault) {
        this.pdefault = pdefault;
    }

    public List<RenderingMethodParameterRestriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<RenderingMethodParameterRestriction> restrictions) {
        this.restrictions = restrictions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isTextureParameter() {
        return this.type.equals("Texture");
    }

    public String toString() {
        return this.getClass().getSimpleName() + " " + this.name + " : [" + this.description + "," + this.type + "," + this.uniform_name + "]";
    }
}
