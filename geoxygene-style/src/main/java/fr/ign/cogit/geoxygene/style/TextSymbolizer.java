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

import javax.xml.bind.annotation.XmlElement;

import fr.ign.cogit.geoxygene.filter.expression.PropertyName;

/**
 * @author Julien Perret
 */
public class TextSymbolizer extends AbstractSymbolizer {

    @XmlElement(name = "Label")
    private Label label;

    @XmlElement(name = "Font")
    private Font font;

    @XmlElement(name = "LabelPlacement")
    private LabelPlacement labelPlacement;

    @XmlElement(name = "Halo")
    private Halo halo;

    @XmlElement(name = "Fill")
    private Fill fill;

    @Override
    public boolean isTextSymbolizer() {
        return true;
    }

    /**
     * Renvoie la valeur de l'attribut label.
     * 
     * @return la valeur de l'attribut label
     */
    public String getLabel() {
        return this.label == null ? null : this.label.getPropertyName()
                .toString();
    }

    /**
     * Affecte la valeur de l'attribut label.
     * 
     * @param label
     *            l'attribut label à affecter
     */
    public void setLabel(String label) {
        if (this.label == null) {
            this.label = new Label();
            this.label.setPropertyName(new PropertyName());
        }
        this.label.getPropertyName().setPropertyName(label);
    }

    /**
     * Renvoie la valeur de l'attribut font.
     * 
     * @return la valeur de l'attribut font
     */
    public Font getFont() {
        return this.font;
    }

    /**
     * Affecte la valeur de l'attribut font.
     * 
     * @param font
     *            l'attribut font à affecter
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Renvoie la valeur de l'attribut labelPlacement.
     * 
     * @return la valeur de l'attribut labelPlacement
     */
    public LabelPlacement getLabelPlacement() {
        return this.labelPlacement;
    }

    /**
     * Affecte la valeur de l'attribut labelPlacement.
     * 
     * @param labelPlacement
     *            l'attribut labelPlacement à affecter
     */
    public void setLabelPlacement(LabelPlacement labelPlacement) {
        this.labelPlacement = labelPlacement;
    }

    /**
     * Renvoie la valeur de l'attribut halo.
     * 
     * @return la valeur de l'attribut halo
     */
    public Halo getHalo() {
        return this.halo;
    }

    /**
     * Affecte la valeur de l'attribut halo.
     * 
     * @param halo
     *            l'attribut halo à affecter
     */
    public void setHalo(Halo halo) {
        this.halo = halo;
    }

    /**
     * Renvoie la valeur de l'attribut fill.
     * 
     * @return la valeur de l'attribut fill
     */
    public Fill getFill() {
        return this.fill;
    }

    /**
     * Affecte la valeur de l'attribut fill.
     * 
     * @param fill
     *            l'attribut fill à affecter
     */
    public void setFill(Fill fill) {
        this.fill = fill;
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
                + ((this.fill == null) ? 0 : this.fill.hashCode());
        result = prime * result
                + ((this.font == null) ? 0 : this.font.hashCode());
        result = prime * result
                + ((this.halo == null) ? 0 : this.halo.hashCode());
        result = prime * result
                + ((this.label == null) ? 0 : this.label.hashCode());
        result = prime
                * result
                + ((this.labelPlacement == null) ? 0 : this.labelPlacement
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
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        TextSymbolizer other = (TextSymbolizer) obj;
        if (this.fill == null) {
            if (other.fill != null) {
                return false;
            }
        } else if (!this.fill.equals(other.fill)) {
            return false;
        }
        if (this.font == null) {
            if (other.font != null) {
                return false;
            }
        } else if (!this.font.equals(other.font)) {
            return false;
        }
        if (this.halo == null) {
            if (other.halo != null) {
                return false;
            }
        } else if (!this.halo.equals(other.halo)) {
            return false;
        }
        if (this.label == null) {
            if (other.label != null) {
                return false;
            }
        } else if (!this.label.equals(other.label)) {
            return false;
        }
        if (this.labelPlacement == null) {
            if (other.labelPlacement != null) {
                return false;
            }
        } else if (!this.labelPlacement.equals(other.labelPlacement)) {
            return false;
        }
        return true;
    }

}
