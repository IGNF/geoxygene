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
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import fr.ign.cogit.geoxygene.filter.expression.Expression;
import fr.ign.cogit.geoxygene.filter.expression.Literal;

/**
 * @author Julien Perret
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Graphic", propOrder = { "marks", "opacity", "size",
        "rotation", "externalGraphics" })
public class Graphic {

    @XmlElement(name = "ExternalGraphic")
    private List<ExternalGraphic> externalGraphics = new ArrayList<ExternalGraphic>(
            0);

    /**
     * Renvoie la valeur de l'attribut externalGraphics.
     * 
     * @return la valeur de l'attribut externalGraphics
     */
    public List<ExternalGraphic> getExternalGraphics() {
        return this.externalGraphics;
    }

    /**
     * Affecte la valeur de l'attribut externalGraphics.
     * 
     * @param externalGraphics
     *            l'attribut externalGraphics à affecter
     */
    public void setExternalGraphics(List<ExternalGraphic> externalGraphics) {
        this.externalGraphics = externalGraphics;
    }

    @XmlElement(name = "Mark")
    private List<Mark> marks = new ArrayList<Mark>(0);

    /**
     * Renvoie la valeur de l'attribut marks.
     * 
     * @return la valeur de l'attribut marks
     */
    public List<Mark> getMarks() {
        return this.marks;
    }

    /**
     * Affecte la valeur de l'attribut marks.
     * 
     * @param marks
     *            l'attribut marks à affecter
     */
    public void setMarks(List<Mark> marks) {
        this.marks = marks;
    }

    private float opacity = 1.0f;

    /**
     * Renvoie la valeur de l'attribut opacity.
     * 
     * @return la valeur de l'attribut opacity
     */
    public float getOpacity() {
        return this.opacity;
    }

    /**
     * Affecte la valeur de l'attribut opacity.
     * 
     * @param opacity
     *            l'attribut opacity à affecter
     */
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    @XmlElement(name = "Size")
    private float size = 6.0f;

    /**
     * Renvoie la valeur de l'attribut size.
     * 
     * @return la valeur de l'attribut size
     */
    public float getSize() {
        return this.size;
    }

    /**
     * Affecte la valeur de l'attribut size.
     * 
     * @param size
     *            l'attribut size à affecter
     */
    public void setSize(float size) {
        this.size = size;
    }

    @XmlElementRefs({ @XmlElementRef })
    @XmlElementWrapper(name = "Rotation")
    private final Expression[] rotation = new Expression[] { new Literal("0") };

    /**
     * Renvoie la valeur de l'attribut rotation.
     * 
     * @return la valeur de l'attribut rotation
     */
    public Expression getRotation() {
        return this.rotation[0];
    }

    /**
     * Affecte la valeur de l'attribut rotation.
     * 
     * @param rotation
     *            l'attribut rotation à affecter
     */
    public void setRotation(Expression rotation) {
        this.rotation[0] = rotation;
    }

    public float getWidth() {
        // TODO handle external graphics width according to aspect ratio
        return this.size;
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
                + ((this.externalGraphics == null) ? 0 : this.externalGraphics
                        .hashCode());
        result = prime * result
                + ((this.marks == null) ? 0 : this.marks.hashCode());
        result = prime * result + Float.floatToIntBits(this.opacity);
        result = prime * result + Arrays.hashCode(this.rotation);
        result = prime * result + Float.floatToIntBits(this.size);
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
        Graphic other = (Graphic) obj;
        if (this.externalGraphics == null) {
            if (other.externalGraphics != null) {
                return false;
            }
        } else if (!this.externalGraphics.equals(other.externalGraphics)) {
            return false;
        }
        if (this.marks == null) {
            if (other.marks != null) {
                return false;
            }
        } else if (!this.marks.equals(other.marks)) {
            return false;
        }
        if (Float.floatToIntBits(this.opacity) != Float
                .floatToIntBits(other.opacity)) {
            return false;
        }
        if (!Arrays.equals(this.rotation, other.rotation)) {
            return false;
        }
        if (Float.floatToIntBits(this.size) != Float.floatToIntBits(other.size)) {
            return false;
        }
        return true;
    }

}
