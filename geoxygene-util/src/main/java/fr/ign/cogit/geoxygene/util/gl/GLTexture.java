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
package fr.ign.cogit.geoxygene.util.gl;

/**
 * Texture management. This class returns texture coordinates depending on the
 * kind of texture type It maps 2D points to 2D texture coordinates
 * 
 * paint method: texture.initializeRendering(), texture.finalizeRendering()
 * 
 * shader usage: uniform sampler2D colorTexture1; in vec2 fragmentTextureCoord;
 * vec4 tcolor = texture(colorTexture1, fragmentTextureCoord);
 * 
 * @author JeT
 * 
 */
public interface GLTexture {

    /**
     * Texture initialization. This method must be called before
     * vertexCoordinates() method calls It can use the current GLSL program Id
     * (to set uniforms)
     * 
     * @return true if texture is valid
     */
    boolean initializeRendering(int programId);

    /**
     * Finalize rendering. After this call, vertexCoordinates() method calls
     * returns unpredictive results
     */
    void finalizeRendering();

    /**
     * @return the texture image width (in pixels)
     */
    int getTextureWidth();

    /**
     * @return the texture image height (in pixels)
     */
    int getTextureHeight();

    /**
     * @return the texture image width (in pixels)
     */
    double getScaleX();

    /**
     * @return the texture image height (in pixels)
     */
    double getScaleY();

    /**
     * Assign a GL texture slot to this texture.
     * @param name
     * @param i
     */
    void setTextureSlot(String name, int i);

}
