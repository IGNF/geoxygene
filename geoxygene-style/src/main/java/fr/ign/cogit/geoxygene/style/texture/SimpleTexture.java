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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Simple texture containing a single image
 * 
 * @author JeT, Bertrand Duménieu
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SimpleTexture")
public class SimpleTexture extends Texture {

    /**
     * The Texture location. THis uri may be relative.
     */
    @XmlElement(name = "URI")
    private URI tex_location = null;

    /**
     * default constructor
     */
    public SimpleTexture() {
        super(TextureDrawingMode.VIEWPORTSPACE);
    }

    public SimpleTexture(TextureDrawingMode drawingMode) {
        super(drawingMode);
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
        result = prime * result + ((this.tex_location == null) ? 0 : this.tex_location.hashCode());
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
        SimpleTexture tex = (SimpleTexture) obj;
        if (this.tex_location != tex.tex_location) {
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
        return "BasicTextureDescriptor [uri=" + this.tex_location + ", toString()=" + super.toString() + "]";
    }

    public URI getURI() {
        return tex_location;
    }

    public void setURI(URI location) {
        this.tex_location = location;
    }

    public URI resolveAbsoluteURI(URI root_uri) throws URISyntaxException {
        File f = new File(this.tex_location.toString());
        if (!this.tex_location.isAbsolute()) {
            if (!f.exists()) {
                URI resolved = root_uri.resolve(this.tex_location);
                if (resolved.isAbsolute())
                    this.setAbsoluteURI(resolved);
                else
                    this.setAbsoluteURI(null);
            } else {
                URI abs_uri;
                abs_uri = f.toURI();
                this.setAbsoluteURI(abs_uri);
            }
        } else {
            this.setAbsoluteURI(this.tex_location);
        }
        return this.getAbsoluteURI();
    }

}
