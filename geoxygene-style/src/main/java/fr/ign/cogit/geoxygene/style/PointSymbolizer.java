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
public class PointSymbolizer extends AbstractSymbolizer {

    @XmlElement(name = "Graphic")
    private Graphic graphic = null;

    @XmlElement(name = "ColorMap")
    private ColorMap colorMap = null;

    @XmlElement(name = "CategorizedMap")
    private CategorizedMap categorizedMap = null;

    @Override
    public boolean isPointSymbolizer() {
        return true;
    }

    public Graphic getGraphic() {
        return this.graphic;
    }

    public void setGraphic(Graphic graphic) {
        this.graphic = graphic;
    }

    public ColorMap getColorMap() {
        return this.colorMap;
    }

    public void setColorMap(ColorMap colorMap) {
        this.colorMap = colorMap;
    }

    public CategorizedMap getCategorizedMap() {
        return this.categorizedMap;
    }

    public void setCategorizedMap(CategorizedMap categorizedMap) {
        this.categorizedMap = categorizedMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime
                * result
                + ((this.categorizedMap == null) ? 0 : this.categorizedMap
                        .hashCode());
        result = prime * result
                + ((this.colorMap == null) ? 0 : this.colorMap.hashCode());
        result = prime * result
                + ((this.graphic == null) ? 0 : this.graphic.hashCode());
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
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        PointSymbolizer other = (PointSymbolizer) obj;
        if (this.categorizedMap == null) {
            if (other.categorizedMap != null) {
                return false;
            }
        } else if (!this.categorizedMap.equals(other.categorizedMap)) {
            return false;
        }
        if (this.colorMap == null) {
            if (other.colorMap != null) {
                return false;
            }
        } else if (!this.colorMap.equals(other.colorMap)) {
            return false;
        }
        if (this.graphic == null) {
            if (other.graphic != null) {
                return false;
            }
        } else if (!this.graphic.equals(other.graphic)) {
            return false;
        }
        return true;
    }

}
