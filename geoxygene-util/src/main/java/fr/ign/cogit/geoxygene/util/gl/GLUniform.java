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

import org.lwjgl.opengl.GL11;

/**
 * @author JeT
 * 
 */
public class GLUniform {
    private String name = "unnamed";
    private int glType = GL11.GL_FLOAT;
    private boolean normalized = false;
    private int stride = 0; // distance between 2 same elements in bytes

    /**
     * @param name
     * @param glType
     * @param normalized
     * @param stride
     */
    public GLUniform(String name, int glType, boolean normalized, int stride) {
        super();
        this.name = name;
        this.glType = glType;
        this.normalized = normalized;
        this.stride = stride;
    }



    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }


    /**
     * @return the glType
     */
    public int getGlType() {
        return this.glType;
    }

    /**
     * @return the normalized
     */
    public boolean isNormalized() {
        return this.normalized;
    }

    /**
     * @return the stride
     */
    public int getStride() {
        return this.stride;
    }
    
    public String toString(){
        return this.getName()+":"+this.getGlType();
    }
    

}
