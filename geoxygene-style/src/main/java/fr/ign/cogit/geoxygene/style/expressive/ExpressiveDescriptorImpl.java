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

import java.util.ArrayList;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
@XmlAccessorType(XmlAccessType.FIELD)
public class ExpressiveDescriptorImpl implements ExpressiveDescriptor {

    @XmlElement(name = "ExpressiveMethod")
    private String renderingmethod;

    @XmlElement(name = "ExpressiveParameter")
    private List<ExpressiveParameter> expressiveparams = new ArrayList<ExpressiveParameter>(0);

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        for (ExpressiveParameter ep : this.expressiveparams) {
            result = prime * result + ((ep == null) ? 0 : ep.hashCode());
        }
        result = prime * result + ((this.renderingmethod == null) ? 0 : this.renderingmethod.hashCode());
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
        ExpressiveDescriptorImpl other = (ExpressiveDescriptorImpl) obj;
        if (this.expressiveparams == null) {
            if (other.expressiveparams != null) {
                return false;
            }
        } else if (!this.expressiveparams.equals(other.expressiveparams)) {
            return false;
        }

        if (this.renderingmethod == null) {
            if (other.renderingmethod != null) {
                return false;
            }
        } else if (!this.renderingmethod.equals(other.renderingmethod)) {
            return false;
        }
        return true;
    }

    @Override
    public String getRenderingMethod() {
        return this.renderingmethod;
    }

    public List<ExpressiveParameter> getExpressiveParameters() {
        return this.expressiveparams;
    }

    public ExpressiveParameter getExpressiveParameter(String paramName) {
        for (ExpressiveParameter param : this.expressiveparams) {
            if (param.name.equalsIgnoreCase(paramName)) {
                return param;
            }
        }
        return null;
    }

    public void setRenderingMethod(String method_name) {
        this.renderingmethod = method_name;
    }

    public void addExpressiveParameter(ExpressiveParameter texparam) {
        if (this.expressiveparams == null)
            this.expressiveparams = new ArrayList<ExpressiveParameter>();
        this.expressiveparams.add(texparam);
    }

}
