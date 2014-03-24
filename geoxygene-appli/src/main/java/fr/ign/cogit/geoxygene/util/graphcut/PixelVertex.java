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

package fr.ign.cogit.geoxygene.util.graphcut;

/**
 * @author JeT
 *         graph nodes (vertices) used in graph cut algorithm
 */
public class PixelVertex {

    private final int x, y;
    private final float srcR, srcG, srcB, srcA;
    private final float dstR, dstG, dstB, dstA;

    /**
     * @param x
     * @param y
     * @param srcR
     * @param srcG
     * @param srcB
     * @param dstR
     * @param dstG
     * @param dstB
     */
    public PixelVertex(int x, int y, float srcR, float srcG, float srcB, float srcA, float dstR, float dstG, float dstB, float dstA) {
        super();
        this.x = x;
        this.y = y;
        this.srcR = srcR;
        this.srcG = srcG;
        this.srcB = srcB;
        this.srcA = srcA;
        this.dstR = dstR;
        this.dstG = dstG;
        this.dstB = dstB;
        this.dstA = dstA;
    }

    public PixelVertex(int i, int j) {
        this(i, j, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    /**
     * @return the x
     */
    public int getX() {
        return this.x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return this.y;
    }

    /**
     * @return the srcR
     */
    public float getTileR() {
        return this.srcR;
    }

    /**
     * @return the srcG
     */
    public float getTileG() {
        return this.srcG;
    }

    /**
     * @return the srcB
     */
    public float getTileB() {
        return this.srcB;
    }

    /**
     * @return the dstR
     */
    public float getImageR() {
        return this.dstR;
    }

    /**
     * @return the dstG
     */
    public float getImageG() {
        return this.dstG;
    }

    /**
     * @return the dstB
     */
    public float getImageB() {
        return this.dstB;
    }

    /**
     * @return the srcA
     */
    public float getTileA() {
        return this.srcA;
    }

    /**
     * @return the dstA
     */
    public float getImageA() {
        return this.dstA;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PixelVertex [x=" + this.x + ", y=" + this.y + "]";
    }

}
