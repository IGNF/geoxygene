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

package fr.ign.cogit.geoxygene.appli.gl;

/**
 * @author JeT
 * 
 */
public class TexturedVertex {
    // Vertex data
    private float[] xyzw = new float[] { 0f, 0f, 0f, 1f };
    private float[] rgba = new float[] { 1f, 1f, 1f, 1f };
    private float[] st = new float[] { 0f, 0f };

    // The amount of bytes an element has
    public static final int elementBytes = 4;

    // Elements per parameter
    public static final int positionElementCount = 4;
    public static final int colorElementCount = 4;
    public static final int textureElementCount = 2;

    // Bytes per parameter
    public static final int positionBytesCount = positionElementCount * elementBytes;
    public static final int colorByteCount = colorElementCount * elementBytes;
    public static final int textureByteCount = textureElementCount * elementBytes;

    // Byte offsets per parameter
    public static final int positionByteOffset = 0;
    public static final int colorByteOffset = positionByteOffset + positionBytesCount;
    public static final int textureByteOffset = colorByteOffset + colorByteCount;

    // The amount of elements that a vertex has
    public static final int elementCount = positionElementCount + colorElementCount + textureElementCount;
    // The size of a vertex in bytes, like in C/C++: sizeof(Vertex)
    public static final int stride = positionBytesCount + colorByteCount + textureByteCount;

    // Setters
    public void setXYZ(float x, float y, float z) {
        this.setXYZW(x, y, z, 1f);
    }

    public void setRGB(float r, float g, float b) {
        this.setRGBA(r, g, b, 1f);
    }

    public void setST(float s, float t) {
        this.st = new float[] { s, t };
    }

    public void setXYZW(float x, float y, float z, float w) {
        this.xyzw = new float[] { x, y, z, w };
    }

    public void setRGBA(float r, float g, float b, float a) {
        this.rgba = new float[] { r, g, b, 1f };
    }

    // Getters      
    public float[] getElements() {
        float[] out = new float[TexturedVertex.elementCount];
        int i = 0;

        // Insert XYZW elements
        out[i++] = this.xyzw[0];
        out[i++] = this.xyzw[1];
        out[i++] = this.xyzw[2];
        out[i++] = this.xyzw[3];
        // Insert RGBA elements
        out[i++] = this.rgba[0];
        out[i++] = this.rgba[1];
        out[i++] = this.rgba[2];
        out[i++] = this.rgba[3];
        // Insert ST elements
        out[i++] = this.st[0];
        out[i++] = this.st[1];

        return out;
    }

    public float[] getXYZW() {
        return new float[] { this.xyzw[0], this.xyzw[1], this.xyzw[2], this.xyzw[3] };
    }

    public float[] getRGBA() {
        return new float[] { this.rgba[0], this.rgba[1], this.rgba[2], this.rgba[3] };
    }

    public float[] getST() {
        return new float[] { this.st[0], this.st[1] };
    }
}