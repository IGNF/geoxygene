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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import fr.ign.cogit.geoxygene.style.texture.BinaryGradientImageDescriptor;
import fr.ign.cogit.geoxygene.style.texture.PerlinNoiseTexture;
import fr.ign.cogit.geoxygene.style.texture.SimpleTexture;
import fr.ign.cogit.geoxygene.style.texture.TileDistributionTexture;

@XmlType(name = "AdaptedExpressiveParameter")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class AdaptedExpressiveParameter {

    @XmlAttribute(name = "name")
    String name;

    @XmlElementRefs({ @XmlElementRef(name = "SimpleTexture", type = SimpleTexture.class), @XmlElementRef(name = "PerlinNoiseTexture", type = PerlinNoiseTexture.class),
            @XmlElementRef(name = "TileDistributionTexture", type = TileDistributionTexture.class),
            @XmlElementRef(name = "GradientTexture", type = BinaryGradientImageDescriptor.class)})
    @XmlMixed
    List<Object> content;

    public AdaptedExpressiveParameter(ExpressiveParameter v) {
        this.name = v.name;
        this.content = new ArrayList<Object>();
        this.content.add(v.isSimpleParameter() ? v.value.toString() : v.value);
    }

    /**
     * The void constructor must be defined for JAXB unmarshalling.
     */
    private AdaptedExpressiveParameter() {
        // Nothing to do?
    }


}
