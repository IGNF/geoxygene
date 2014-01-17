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

package fr.ign.cogit.geoxygene.style.gl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.primitive.BasicParameterizer;
import fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer;
import fr.ign.cogit.geoxygene.style.AbstractSymbolizer;
import fr.ign.cogit.geoxygene.util.gl.Texture;

/**
 * 
 * @author JeT
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class TexturedPolygonSymbolizer extends AbstractSymbolizer {

    private Parameterizer parameterizer = null;
    private Texture texture = null;

    public TexturedPolygonSymbolizer(IFeature feature, Viewport viewport) {
        super();
        this.parameterizer = new BasicParameterizer(feature);
    }

    @Override
    public boolean isPolygonSymbolizer() {
        return true;
    }

    /**
     * @return the parameterizer
     */
    public Parameterizer getParameterizer() {
        return this.parameterizer;
    }

    /**
     * @param parameterizer
     *            the parameterizer to set
     */
    public void setParameterizer(Parameterizer parameterizer) {
        this.parameterizer = parameterizer;
    }

    /**
     * @return the texture
     */
    public Texture getTexture() {
        return this.texture;
    }

    /**
     * @param texture
     *            the texture to set
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    // TODO : generate hashCode & equals methods
}
