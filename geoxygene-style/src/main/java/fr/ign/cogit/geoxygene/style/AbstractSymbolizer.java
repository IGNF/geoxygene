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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.style.filter.LayerFilter;
import fr.ign.cogit.geoxygene.style.filter.LayerFilterContrast;
import fr.ign.cogit.geoxygene.style.filter.LayerFilterIdentity;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractSymbolizer implements Symbolizer {
    protected static Logger logger = Logger.getLogger(AbstractSymbolizer.class
            .getName());

    @XmlElement(name = "Stroke")
    private Stroke stroke = null;

    @Override
    public Stroke getStroke() {
        return this.stroke;
    }

    @Override
    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    @Override
    public boolean isLineSymbolizer() {
        return false;
    }

    @Override
    public boolean isPointSymbolizer() {
        return false;
    }

    @Override
    public boolean isPolygonSymbolizer() {
        return false;
    }

    @Override
    public boolean isRasterSymbolizer() {
        return false;
    }

    @Override
    public boolean isTextSymbolizer() {
        return false;
    }

    @XmlElement(name = "GeometryPropertyName")
    private String geometryPropertyName = "geom"; //$NON-NLS-1$

    @Override
    public String getGeometryPropertyName() {
        return this.geometryPropertyName;
    }

    @Override
    public void setGeometryPropertyName(String geometryPropertyName) {
        this.geometryPropertyName = geometryPropertyName;
    }

    @XmlAttribute(name = "uom")
    private String uom = Symbolizer.METRE;

    @Override
    public String getUnitOfMeasure() {
        return this.uom;
    }

    @Override
    public void setUnitOfMeasure(String uom) {
        this.uom = uom;
    }

    @Override
    public void setUnitOfMeasureMetre() {
        this.setUnitOfMeasure(Symbolizer.METRE);
    }

    @Override
    public void setUnitOfMeasureFoot() {
        this.setUnitOfMeasure(Symbolizer.FOOT);
    }

    @Override
    public void setUnitOfMeasurePixel() {
        this.setUnitOfMeasure(Symbolizer.PIXEL);
    }

    @XmlElement(name = "Shadow")
    private Shadow shadow = null;

    @Override
    public Shadow getShadow() {
        return this.shadow;
    }

    @Override
    public void setShadow(Shadow shadow) {
        this.shadow = shadow;
    }

    @XmlElements({
            @XmlElement(name = "NormalBlending", type = BlendingModeNormal.class),
            @XmlElement(name = "OverlayBlending", type = BlendingModeOverlay.class),
            @XmlElement(name = "MultiplyBlending", type = BlendingModeMultiply.class) })
    private BlendingMode blendingMode = null;

    @Override
    public BlendingMode getBlendingMode() {
        return this.blendingMode;
    }

    @Override
    public void setBlendingMode(BlendingMode blendingMode) {
        this.blendingMode = blendingMode;
    }

    @XmlElements({
            @XmlElement(name = "ContrastFilter", type = LayerFilterContrast.class),
            @XmlElement(name = "NoFilter", type = LayerFilterIdentity.class) })
    LayerFilter filter = null;

    /**
     * @return the filters
     */
    @Override
    public LayerFilter getFilter() {
        return this.filter;
    }

    /**
     * @param filters
     *            the filters to set
     */
    @Override
    public void setFilter(LayerFilter filter) {
        this.filter = filter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.style.Symbolizer#reset()
     */
    @Override
    public void reset() {
        // default behavior is to do nothing

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
                + ((this.blendingMode == null) ? 0 : this.blendingMode
                        .hashCode());
        result = prime
                * result
                + ((this.geometryPropertyName == null) ? 0
                        : this.geometryPropertyName.hashCode());
        result = prime * result
                + ((this.shadow == null) ? 0 : this.shadow.hashCode());
        result = prime * result
                + ((this.stroke == null) ? 0 : this.stroke.hashCode());
        result = prime * result
                + ((this.uom == null) ? 0 : this.uom.hashCode());
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
        AbstractSymbolizer other = (AbstractSymbolizer) obj;
        if (this.blendingMode == null) {
            if (other.blendingMode != null) {
                return false;
            }
        } else if (!this.blendingMode.equals(other.blendingMode)) {
            return false;
        }
        if (this.geometryPropertyName == null) {
            if (other.geometryPropertyName != null) {
                return false;
            }
        } else if (!this.geometryPropertyName
                .equals(other.geometryPropertyName)) {
            return false;
        }
        if (this.shadow == null) {
            if (other.shadow != null) {
                return false;
            }
        } else if (!this.shadow.equals(other.shadow)) {
            return false;
        }
        if (this.stroke == null) {
            if (other.stroke != null) {
                return false;
            }
        } else if (!this.stroke.equals(other.stroke)) {
            return false;
        }
        if (this.uom == null) {
            if (other.uom != null) {
                return false;
            }
        } else if (!this.uom.equals(other.uom)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AbstractSymbolizer [stroke=" + this.stroke
                + ", geometryPropertyName=" + this.geometryPropertyName
                + ", uom=" + this.uom + ", shadow=" + this.shadow
                + ", blendingMode=" + this.blendingMode + "]";
    }

}
