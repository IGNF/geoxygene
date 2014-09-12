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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;

/**
 * @author Julien Perret
 * 
 */
public class RasterSymbolizer extends AbstractSymbolizer {
    @XmlElement(name = "ShadedRelief")
    ShadedRelief shadedRelief = null;
    private double opacity = 1.0d;

    public ShadedRelief getShadedRelief() {
        return this.shadedRelief;
    }

    public void setShadedRelief(ShadedRelief shadedRelief) {
        this.shadedRelief = shadedRelief;
    }

    @XmlElement(name = "ColorMap")
    ColorMap colorMap = null;

    public ColorMap getColorMap() {
        return this.colorMap;
    }

    public void setColorMap(ColorMap colorMap) {
        this.colorMap = colorMap;
    }

    @Override
    public boolean isRasterSymbolizer() {
        return true;
    }

    @XmlTransient
    Map<IFeature, GM_MultiSurface<GM_Triangle>> map = new HashMap<IFeature, GM_MultiSurface<GM_Triangle>>();
    @XmlTransient
    Map<GM_Triangle, Vecteur> normalMap = new HashMap<GM_Triangle, Vecteur>();
    @XmlTransient
    Map<IDirectPosition, List<GM_Triangle>> triangleMap = new HashMap<IDirectPosition, List<GM_Triangle>>();
    @XmlTransient
    Map<IDirectPosition, Vecteur> positionMap = new HashMap<IDirectPosition, Vecteur>();

    public double getOpacity() {
        return this.opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
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
        result = prime * result
                + ((this.colorMap == null) ? 0 : this.colorMap.hashCode());
        result = prime * result
                + ((this.map == null) ? 0 : this.map.hashCode());
        result = prime * result
                + ((this.normalMap == null) ? 0 : this.normalMap.hashCode());
        long temp;
        temp = Double.doubleToLongBits(this.opacity);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime
                * result
                + ((this.positionMap == null) ? 0 : this.positionMap.hashCode());
        result = prime
                * result
                + ((this.shadedRelief == null) ? 0 : this.shadedRelief
                        .hashCode());
        result = prime
                * result
                + ((this.triangleMap == null) ? 0 : this.triangleMap.hashCode());
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
        RasterSymbolizer other = (RasterSymbolizer) obj;
        if (this.colorMap == null) {
            if (other.colorMap != null) {
                return false;
            }
        } else if (!this.colorMap.equals(other.colorMap)) {
            return false;
        }
        if (this.map == null) {
            if (other.map != null) {
                return false;
            }
        } else if (!this.map.equals(other.map)) {
            return false;
        }
        if (this.normalMap == null) {
            if (other.normalMap != null) {
                return false;
            }
        } else if (!this.normalMap.equals(other.normalMap)) {
            return false;
        }
        if (Double.doubleToLongBits(this.opacity) != Double
                .doubleToLongBits(other.opacity)) {
            return false;
        }
        if (this.positionMap == null) {
            if (other.positionMap != null) {
                return false;
            }
        } else if (!this.positionMap.equals(other.positionMap)) {
            return false;
        }
        if (this.shadedRelief == null) {
            if (other.shadedRelief != null) {
                return false;
            }
        } else if (!this.shadedRelief.equals(other.shadedRelief)) {
            return false;
        }
        if (this.triangleMap == null) {
            if (other.triangleMap != null) {
                return false;
            }
        } else if (!this.triangleMap.equals(other.triangleMap)) {
            return false;
        }
        return true;
    }

}
