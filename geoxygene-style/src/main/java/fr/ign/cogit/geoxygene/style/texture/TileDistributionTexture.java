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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Simple texture containing a single image
 * 
 * @author JeT
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="TileDistributionTexture")
public class TileDistributionTexture extends Texture {

    // coast geometry segments greater than this value won't be considered as
    // coast lines
    @XmlElement(name = "MaxCoastlineLength")
    private double maxCoastlineLength = Double.POSITIVE_INFINITY;
    
    @XmlElement(name = "Tile")
    private final List<ProbabilistTileDescriptor> tiles = new ArrayList<ProbabilistTileDescriptor>();

    @XmlElement(name = "Resolution")
    private double textureResolution = 600; // resolution in DPI

    @XmlElement(name = "Blending")
    private final TileBlendingType blending = TileBlendingType.NONE;

    @XmlElement(name = "DistributionManagement")
    private DistributionManagementType distributionManagement = DistributionManagementType.EXACT;

    @XmlElement(name = "BlurSize")
    private int blurSize = 2;

    /**
     * default constructor
     */
    public TileDistributionTexture() {
        super(TextureDrawingMode.VIEWPORTSPACE);
    }
    
    /**
     * 
     * @return state telling if maxCoastLineIsEnabled, and so if the orientation field 
     * should be taken into account to generate the gradient, or not.
     */
    public boolean isMaxCoastLineEnabled() {
      return this.maxCoastlineLength != Double.POSITIVE_INFINITY
          && this.maxCoastlineLength >= 0;
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

    /**
     * @return the tiles
     */
    public List<ProbabilistTileDescriptor> getTiles() {
        return this.tiles;
    }

    /**
     * @return the textureResolution
     */
    public double getTextureResolution() {
        return this.textureResolution;
    }

    /**
     * @param textureResolution
     *            the textureResolution to set
     */
    public void setTextureResolution(double textureResolution) {
        this.textureResolution = textureResolution;
    }

    /**
     * @return the blurSize
     */
    public int getBlurSize() {
        return this.blurSize;
    }

    /**
     * @param blurSize
     *            the blurSize to set
     */
    public void setBlurSize(int blurSize) {
        this.blurSize = blurSize;
    }

    /**
     * @return the blending
     */
    public TileBlendingType getBlending() {
        return this.blending;
    }

    /**
     * @return the distributionManagement
     */
    public DistributionManagementType getDistributionManagement() {
        return this.distributionManagement;
    }

    /**
     * @param distributionManagement
     *            the distributionManagement to set
     */
    public void setDistributionManagement(
            DistributionManagementType distributionManagement) {
        this.distributionManagement = distributionManagement;
    }

    public enum TileBlendingType {
        NONE, ALPHA, GRAPHCUT
    }

    public enum DistributionManagementType {
        EXACT, KEEP_OUTSIDE, CUT_OUTSIDE
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TileDistributionTextureDescriptor [maxCoastlineLength="
                + this.maxCoastlineLength + ", textureResolution="
                + this.textureResolution + ", blending=" + this.blending
                + ", distributionManagement=" + this.distributionManagement
                + ", toString()=" + super.toString() + "]";
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
                + ((this.blending == null) ? 0 : this.blending.ordinal());
        result = prime * result + this.blurSize;
        result = prime
                * result
                + ((this.distributionManagement == null) ? 0
                        : this.distributionManagement.ordinal());
        long temp;
        temp = Double.doubleToLongBits(this.maxCoastlineLength);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.textureResolution);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result
                + ((this.tiles == null) ? 0 : this.tiles.hashCode());
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
        TileDistributionTexture other = (TileDistributionTexture) obj;
        if (this.blending.ordinal() != other.blending.ordinal()) {
            return false;
        }
        if (this.blurSize != other.blurSize) {
            return false;
        }
        if (this.distributionManagement.ordinal() != other.distributionManagement
                .ordinal()) {
            return false;
        }
        if (Double.doubleToLongBits(this.maxCoastlineLength) != Double
                .doubleToLongBits(other.maxCoastlineLength)) {
            return false;
        }
        if (Double.doubleToLongBits(this.textureResolution) != Double
                .doubleToLongBits(other.textureResolution)) {
            return false;
        }
        if (this.tiles == null) {
            if (other.tiles != null) {
                return false;
            }
        } else if (!this.tiles.equals(other.tiles)) {
            return false;
        }
        return true;
    }
    


    
}
