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

import java.awt.image.BufferedImage;
import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class TileDescriptor {

    @XmlElement(name = "URI")
    private URI uri = null;

    @XmlTransient
    private URI absolute_uri = null;
    
    @XmlElement(name = "ScaleFactor")
    private double scaleFactor = 1.;

    private BufferedImage textureImage = null;

    /**
     * default constructor
     */
    public TileDescriptor() {
    }

    public URI getURI() {
        return this.uri;
    }

    
    public void setURI(URI uri) {
        this.uri = uri;
    }

    /**
     * @return the textureImage
     */
    public BufferedImage getTextureImage() {
        return this.textureImage;
    }

    /**
     * @param textureImage
     *            the textureImage to set
     */
    public void setTextureImage(BufferedImage textureImage) {
        this.textureImage = textureImage;
    }

    /**
     * @return the scaleFactor
     */
    public double getScaleFactor() {
        return this.scaleFactor;
    }

    /**
     * @param scaleFactor
     *            the scaleFactor to set
     */
    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
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
        long temp;
        temp = Double.doubleToLongBits(this.scaleFactor);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result
                + ((this.uri == null) ? 0 : this.uri.hashCode());
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
        TileDescriptor other = (TileDescriptor) obj;
        if (Double.doubleToLongBits(this.scaleFactor) != Double
                .doubleToLongBits(other.scaleFactor)) {
            return false;
        }
        if (this.uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!this.uri.equals(other.uri)) {
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
        return "TileDescriptor [url=" + this.uri + ", scaleFactor="
                + this.scaleFactor + "]";
    }
    
    public URI resolveAbsoluteURI(URI root_uri){
        if(!this.uri.isAbsolute()) {
            URI resolved = root_uri.resolve(this.uri);
            if(resolved.isAbsolute())
                 this.setAbsoluteURI(resolved);
            else
                this.setAbsoluteURI(null);
        }else{
            this.setAbsoluteURI(null);
        }
        return this.getAbsoluteURI();
    }

    void setAbsoluteURI(URI _absolute_uri) {
        this.absolute_uri = _absolute_uri;
    }
    
    public URI getAbsoluteURI(){
        return this.absolute_uri;
    }

}
