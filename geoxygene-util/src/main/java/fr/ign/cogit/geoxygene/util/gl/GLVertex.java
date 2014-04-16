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

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * @author JeT a GLVertex is a point with 3 coordinates (x,y,z), 2 texture
 *         coordinates (u,v) and an associated color (RGBA)
 */
public final class GLVertex {

    private final float[] xyz = new float[3];
    private final float[] uv = new float[2];
    private final float[] rgba = new float[4];
    public static final int NumberOfFloatValues = 9;

    public static final int vertexPostionLocation = 0;
    public static final int vertexUVLocation = 1;
    public static final int vertexColorLocation = 2;
    public static final String vertexPositionVariableName = "vertexPosition";
    public static final String vertexUVVariableName = "vertexUV";
    public static final String vertexColorVariableName = "vertexColor";

    // public static final int ATTRIBUTES_COUNT = 3;
    // private static final int POSITION_ATTRIBUTE_LOCATION = 0;
    // private static final int UV_ATTRIBUTE_LOCATION = 1;
    // private static final int COLOR_ATTRIBUTE_LOCATION = 2;
    // public static final int VERTEX_BYTESIZE = Float.SIZE / 8
    // * NumberOfFloatValues;
    // public static final int[] ATTRIBUTES_ID = { POSITION_ATTRIBUTE_LOCATION,
    // UV_ATTRIBUTE_LOCATION, COLOR_ATTRIBUTE_LOCATION };
    // public static final int[] ATTRIBUTES_COMPONENT_NUMBER = { 3, 2, 4 };
    // public static final int[] ATTRIBUTES_TYPE = { GL11.GL_FLOAT,
    // GL11.GL_FLOAT,
    // GL11.GL_FLOAT };
    // public static final int[] ATTRIBUTES_BYTEOFFSET = { 0, Float.SIZE / 8 *
    // 3,
    // Float.SIZE / 8 * 5 };

    // public static final String[] ELEMENTS_NAME = { "inPosition",
    // "inTextureCoord", "inColor" };

    /**
     * Default constructor
     */
    public GLVertex() {
        this.rgba[1] = 0f;
        this.rgba[0] = 1f;
        this.rgba[2] = 0f;
        this.rgba[3] = 1f;
    }

    public GLVertex(GLVertex vertex) {
        this();
        this.setXYZ(vertex.getXYZ());
        this.setUV(vertex.getUV());
        this.setRGBA(vertex.getRGBA());
    }

    /**
     * Vertex constructor
     */
    public GLVertex(final float[] xyz) {
        this();
        this.setXYZ(xyz);
    }

    /**
     * Vertex constructor
     */
    public GLVertex(final float[] xyz, final float[] uv) {
        this();
        this.setXYZ(xyz);
        this.setUV(uv);
    }

    /**
     * Vertex constructor
     */
    public GLVertex(final float x, final float y, final float z) {
        this();
        this.setXYZ(x, y, z);
    }

    /**
     * Vertex constructor
     */
    public GLVertex(final Point2D xy, final Point2D uv) {
        this();
        this.setXYZ(xy);
        this.setUV(uv);
    }

    /**
     * Vertex constructor
     */
    public GLVertex(final Point2D xy, final Point2D uv, final Color c) {
        this();
        this.setXYZ(xy);
        this.setUV(uv);
        this.setRGBA(c);
    }

    /**
     * Vertex constructor
     */
    public GLVertex(final Point2D xy, final Color c) {
        this();
        this.setXYZ(xy);
        this.setRGBA(c);
    }

    /**
     * Vertex constructor
     */
    public GLVertex(final Point2D xy) {
        this();
        this.setXYZ(xy);
    }

    public final void setXYZ(final float x, final float y, final float z) {
        this.xyz[0] = x;
        this.xyz[1] = y;
        this.xyz[2] = z;
    }

    public final void setXYZ(final float x, final float y) {
        this.xyz[0] = x;
        this.xyz[1] = y;
        this.xyz[2] = 0;
    }

    public final void setXYZ(final Point2D p) {
        this.xyz[0] = (float) p.getX();
        this.xyz[1] = (float) p.getY();
        this.xyz[2] = 0;
    }

    public final void setXYZ(final float[] xyz) {
        this.xyz[0] = xyz[0];
        this.xyz[1] = xyz[1];
        this.xyz[2] = xyz[2];
    }

    public final void setUV(final float u, final float v) {
        this.uv[0] = u;
        this.uv[1] = v;
    }

    public final void setUV(final float[] uv) {
        this.uv[0] = uv[0];
        this.uv[1] = uv[1];
    }

    public final void setUV(final Point2D p) {
        this.uv[0] = (float) p.getX();
        this.uv[1] = (float) p.getY();
    }

    public final void setRGBA(final Color c) {
        this.rgba[0] = c.getRed() / 255.f;
        this.rgba[1] = c.getGreen() / 255.f;
        this.rgba[2] = c.getBlue() / 255.f;
        this.rgba[3] = c.getAlpha() / 255.f;
    }

    public final void setRGBA(final float[] c) {
        this.rgba[0] = c[0];
        this.rgba[1] = c[1];
        this.rgba[2] = c[2];
        this.rgba[3] = c[3];
    }

    public final void setRGBA(final float r, final float g, final float b,
            final float a) {
        this.rgba[0] = r;
        this.rgba[1] = g;
        this.rgba[2] = b;
        this.rgba[3] = a;
    }

    public final void setAlpha(final float a) {
        this.rgba[3] = a;
    }

    /**
     * @return the xyz
     */
    public final float[] getXYZ() {
        return this.xyz;
    }

    /**
     * @return the uv
     */
    public final float[] getUV() {
        return this.uv;
    }

    /**
     * @return the rgba
     */
    public final float[] getRGBA() {
        return this.rgba;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GLVertex [xyz=" + Arrays.toString(this.xyz) + ", uv="
                + Arrays.toString(this.uv) + ", rgba="
                + Arrays.toString(this.rgba) + "]";
    }

}
