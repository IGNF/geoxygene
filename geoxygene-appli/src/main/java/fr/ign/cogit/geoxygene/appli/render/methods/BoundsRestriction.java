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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import fr.ign.cogit.geoxygene.style.StylingParameter;
import fr.ign.cogit.geoxygene.style.SvgParameter;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveParameter;

@XmlAccessorType(XmlAccessType.FIELD)
public class BoundsRestriction implements RenderingMethodParameterRestriction {

    @XmlAttribute
    double min;
    @XmlAttribute
    double max;
    @XmlAttribute
    boolean min_strict = false;
    @XmlAttribute
    boolean max_strict = false;

    @Override
    public boolean validate(StylingParameter p) {
        double d = Double.NEGATIVE_INFINITY;
        if (p instanceof SvgParameter) {
            try {
                d = Double.parseDouble(((SvgParameter) p).getValue());
            } catch (NumberFormatException nfe) {
                return false;
            }
        } else if (p instanceof ExpressiveParameter) {
            try {
                d = Double.parseDouble((String) ((ExpressiveParameter) p).getValue());
            } catch (NumberFormatException nfe) {
                return false;
            }
        }
        if (min_strict ? d > min : d >= min) {
            return max_strict ? d < max : d <= max;
        }
        return false;
    }
}
