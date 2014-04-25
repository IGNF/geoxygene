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

package test.App;

import java.awt.geom.Point2D;

import fr.ign.cogit.geoxygene.util.gl.GLVertex;

/**
 * @author JeT
 * 
 */
public class GLPaintingVertex implements GLVertex {

    public float[] xy = { 0f, 0f };
    public float[] uv = { 0f, 0f };
    public float[] normal = { 0f, 0f };
    private float curvature = 0;
    private float thickness = 0;

    public static final int vertexPositionLocation = 0;
    public static final int vertexUVLocation = 1;
    public static final int vertexNormalLocation = 2;
    public static final int vertexCurvatureLocation = 3;
    public static final int vertexThicknessLocation = 4;
    public static final String vertexPositionVariableName = "vertexPosition";
    public static final String vertexUVVariableName = "vertexUV";
    public static final String vertexNormalVariableName = "vertexNormal";
    public static final String vertexCurvatureVariableName = "vertexCurvature";
    public static final String vertexThicknessVariableName = "vertexThickness";

    @Override
    public GLPaintingVertex clone() {
        GLPaintingVertex vertex = new GLPaintingVertex();
        vertex.setXY(this.getXY());
        vertex.setUV(this.getUV());
        vertex.setNormal(this.getNormal());
        vertex.setCurvature(this.getCurvature());
        vertex.setThickness(this.getThickness());
        return vertex;
    }

    /**
     * 
     */
    public GLPaintingVertex() {
    }

    /**
     * 
     */
    public GLPaintingVertex(float x, float y) {
        this();
        this.setXY(x, y);
    }

    public GLPaintingVertex(Point2D p) {
        this((float) p.getX(), (float) p.getY());
    }

    /**
     * @return the xy
     */
    public float[] getXY() {
        return this.xy;
    }

    /**
     * @param xy
     *            the xy to set
     */
    public void setXY(float x, float y) {
        this.xy[0] = x;
        this.xy[1] = y;
    }

    /**
     * @param xy
     *            the xy to set
     */
    public void setXY(float[] xy) {
        this.xy[0] = xy[0];
        this.xy[1] = xy[1];
    }

    /**
     * @return the uv
     */
    public float[] getUV() {
        return this.uv;
    }

    /**
     * @param uv
     *            the uv to set
     */
    public void setUV(float u, float v) {
        this.uv[0] = u;
        this.uv[1] = v;
    }

    /**
     * @param uv
     *            the uv to set
     */
    public void setUV(float[] uv) {
        this.uv[0] = uv[0];
        this.uv[1] = uv[1];
    }

    /**
     * @return the normal
     */
    public float[] getNormal() {
        return this.normal;
    }

    /**
     * @param normal
     *            the normal to set
     */
    public void setNormal(float x, float y) {
        this.normal[0] = x;
        this.normal[1] = y;
    }

    /**
     * @param normal
     *            the normal to set
     */
    public void setNormal(float[] xy) {
        this.normal[0] = xy[0];
        this.normal[1] = xy[1];
    }

    /**
     * @return the curvature
     */
    public float getCurvature() {
        return this.curvature;
    }

    /**
     * @param curvature
     *            the curvature to set
     */
    public void setCurvature(float curvature) {
        this.curvature = curvature;
    }

    /**
     * @return the thickness
     */
    public float getThickness() {
        return this.thickness;
    }

    /**
     * @param thickness
     *            the thickness to set
     */
    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    /**
     * @return the vertexpositionlocation
     */
    public static int getVertexpositionlocation() {
        return vertexPositionLocation;
    }

    /**
     * @return the vertexuvlocation
     */
    public static int getVertexuvlocation() {
        return vertexUVLocation;
    }

    /**
     * @return the vertexnormallocation
     */
    public static int getVertexnormallocation() {
        return vertexNormalLocation;
    }

    /**
     * @return the vertexcurvaturelocation
     */
    public static int getVertexcurvaturelocation() {
        return vertexCurvatureLocation;
    }

    /**
     * @return the vertexthicknesslocation
     */
    public static int getVertexthicknesslocation() {
        return vertexThicknessLocation;
    }

    /**
     * @return the vertexpositionvariablename
     */
    public static String getVertexpositionvariablename() {
        return vertexPositionVariableName;
    }

    /**
     * @return the vertexuvvariablename
     */
    public static String getVertexuvvariablename() {
        return vertexUVVariableName;
    }

    /**
     * @return the vertexnormalvariablename
     */
    public static String getVertexnormalvariablename() {
        return vertexNormalVariableName;
    }

    /**
     * @return the vertexcurvaturevariablename
     */
    public static String getVertexcurvaturevariablename() {
        return vertexCurvatureVariableName;
    }

    /**
     * @return the vertexthicknessvariablename
     */
    public static String getVertexthicknessvariablename() {
        return vertexThicknessVariableName;
    }

}
