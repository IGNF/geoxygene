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
public class GLInput {
    private int location = 0;
    private String name = "unnamed";
    private int componentCount = 1;
    private int glType = GL11.GL_FLOAT;
    private boolean normalized = false;

    /**
     * @param location
     * @param name
     * @param componentCount
     * @param glType
     * @param normalized
     * @param stride
     */
    public GLInput(int location, String name, int componentCount, int glType, boolean normalized) {
        super();
        this.location = location;
        this.name = name;
        this.componentCount = componentCount;
        this.glType = glType;
        this.normalized = normalized;
    }

    /**
     * @return the location
     */
    public int getLocation() {
        return this.location;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the componentCount
     */
    public int getComponentCount() {
        return this.componentCount;
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

}
