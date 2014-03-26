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
 * @author JeT
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ProbabilistTileDescriptor extends TileDescriptor {

    @XmlElement(name = "MinDistance")
    private double minDistance = 0;

    @XmlElement(name = "MaxDistance")
    private double maxDistance = Double.POSITIVE_INFINITY;

    @XmlElement(name = "InRangeProbability")
    private double inRangeProbability = 1;

    @XmlElement(name = "OutOfRangeProbability")
    private double outRangeProbability = 0;

    /**
     * Default constructor
     */
    public ProbabilistTileDescriptor() {
        // nothing to initialize
    }

    /**
     * @return the minDistance
     */
    public double getMinDistance() {
        return this.minDistance;
    }

    /**
     * @param minDistance
     *            the minDistance to set
     */
    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    /**
     * @return the maxDistance
     */
    public double getMaxDistance() {
        return this.maxDistance;
    }

    /**
     * @param maxDistance
     *            the maxDistance to set
     */
    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    /**
     * @return the inRangeProbability
     */
    public double getInRangeProbability() {
        return this.inRangeProbability;
    }

    /**
     * @param inRangeProbability
     *            the inRangeProbability to set
     */
    public void setInRangeProbability(double p) {
        this.inRangeProbability = p;
    }

    /**
     * @return the outRangeProbability
     */
    public double getOutOfRangeProbability() {
        return this.outRangeProbability;
    }

    /**
     * @param outRangeProbability
     *            the outRangeProbability to set
     */
    public void setOutOfRangeProbability(double p) {
        this.outRangeProbability = p;
    }

}
