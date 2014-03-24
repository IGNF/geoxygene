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
package fr.ign.cogit.geoxygene.style.texture;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Simple texture containing a single image
 * 
 * @author JeT
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TileDistributionTexture extends Texture {

    // coast geometry segments greater than this value won't be considered as coast lines
    @XmlElement(name = "maxCoastlineLength")
    private double maxCoastlineLength = Double.POSITIVE_INFINITY;

    /**
     * default constructor
     */
    public TileDistributionTexture() {
        super(TextureDrawingMode.VIEWPORTSPACE);
    }

    /**
     * @return the maxCoastlineLength
     */
    public double getMaxCoastlineLength() {
        return this.maxCoastlineLength;
    }

    /**
     * @param maxCoastlineLength
     *            the maxCoastlineLength to set
     */
    public void setMaxCoastlineLength(double maxCoastlineLength) {
        this.maxCoastlineLength = maxCoastlineLength;
    }

}
