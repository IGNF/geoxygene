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

import java.awt.Color;
import java.util.Arrays;

import javax.vecmath.Point2d;

import fr.ign.cogit.geoxygene.util.gl.GLVertex;

/**
 * @author JeT
 * 
 */
public class GLBezierShadingVertex implements GLVertex {

    public float[] xy = { 0f, 0f };
    public float[] us = { 0f, 0f };
    public float[] color = { 0f, 0f, 0f, 0f };
    private float lineWidth = 0;
    public float maxU = 0f;
    public float[] p0 = { 0f, 0f };
    public float[] p1 = { 0f, 0f };
    public float[] p2 = { 0f, 0f };
    public float[] n0 = { 0f, 0f };
    public float[] n2 = { 0f, 0f };
    public float[] paperUV = { 0f, 0f };

    public static final int vertexPositionLocation = 0;
    public static final int vertexUsLocation = 1;
    public static final int vertexColorLocation = 2;
    public static final int vertexLineWidthLocation = 3;
    public static final int vertexMaxULocation = 4;
    public static final int vertexP0Location = 5;
    public static final int vertexP1Location = 6;
    public static final int vertexP2Location = 7;
    public static final int vertexN0Location = 8;
    public static final int vertexN2Location = 9;
    public static final int vertexPaperUVLocation = 10;
    public static final String vertexPositionVariableName = "vertexPosition";
    public static final String vertexUsVariableName = "vertexUs";
    public static final String vertexColorVariableName = "vertexColor";
    public static final String vertexLineWidthVariableName = "lineWidth";
    public static final String vertexMaxUVariableName = "uMax";
    public static final String vertexP0VariableName = "p0";
    public static final String vertexP1VariableName = "p1";
    public static final String vertexP2VariableName = "p2";
    public static final String vertexN0VariableName = "n0";
    public static final String vertexN2VariableName = "n2";
    public static final String vertexPaperUVVariableName = "vertexPaperUV";

    @Override
    public GLBezierShadingVertex clone() {
        GLBezierShadingVertex vertex = new GLBezierShadingVertex();
        vertex.setXY(this.getXY());
        vertex.setUs(this.getUs());
        vertex.setLineWidth(this.getLineWidth());
        vertex.setColor(this.getColor());
        vertex.setMaxU(this.getMaxU());
        vertex.setP0(this.getP0());
        vertex.setP1(this.getP1());
        vertex.setP2(this.getP2());
        vertex.setN0(this.getN0());
        vertex.setN2(this.getN2());
        vertex.setPaperUV(this.getPaperUV());
        return vertex;
    }

    /**
     * 
     */
    public GLBezierShadingVertex() {
    }

    public GLBezierShadingVertex(float x, float y, float u0, float u2, float r,
            float g, float b, float a, float curvature, float maxU, float p0x,
            float p0y, float p1x, float p1y, float p2x, float p2y, float n0x,
            float n0y, float n2x, float n2y, float paperU, float paperV) {
        this();
        this.setXY(x, y);
        this.setUs(u0, u2);
        this.setColor(r, g, b, a);
        this.setLineWidth(curvature);
        this.setMaxU(maxU);
        this.setP0(p0x, p0y);
        this.setP1(p1x, p1y);
        this.setP2(p2x, p2y);
        this.setN0(n0x, n0y);
        this.setN2(n2x, n2y);
        this.setPaperUV(paperU, paperV);
        // System.err.println("create bezier point :" + this.toString());
    }

    public GLBezierShadingVertex(Point2d p, Point2d us, Color c,
            float curvature, float maxU, Point2d p0, Point2d p1, Point2d p2,
            Point2d n0, Point2d n2, Point2d paperUV) {
        this((float) p.x, (float) p.y, (float) us.x, (float) us.y,
                c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c
                        .getAlpha() / 255f, curvature, maxU, (float) p0.x,
                (float) p0.y, (float) p1.x, (float) p1.y, (float) p2.x,
                (float) p2.y, (float) n0.x, (float) n0.y, (float) n2.x,
                (float) n2.y, (float) paperUV.x, (float) paperUV.y);
    }

    /**
     * @return the xy
     */
    public float[] getXY() {
        return this.xy;
    }

    /**
     * @return the maxU
     */
    public float getMaxU() {
        return this.maxU;
    }

    /**
     * @param maxU
     *            the maxU to set
     */
    public void setMaxU(float maxU) {
        this.maxU = maxU;
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
     * @param f
     *            the xy to set
     */
    public void setXY(float[] f) {
        this.xy[0] = f[0];
        this.xy[1] = f[1];
    }

    /**
     * @return the uv
     */
    public float[] getPaperUV() {
        return this.paperUV;
    }

    /**
     * @param us
     *            the uv to set
     */
    public void setPaperUV(float u, float v) {
        this.paperUV[0] = u;
        this.paperUV[1] = v;
    }

    /**
     * @param uv
     *            the uv to set
     */
    public void setPaperUV(float[] uv) {
        this.paperUV[0] = uv[0];
        this.paperUV[1] = uv[1];
    }

    /**
     * @return the p0
     */
    public float[] getP0() {
        return this.p0;
    }

    /**
     * @param f
     *            the p0 to set
     */
    public void setP0(float[] f) {
        this.p0[0] = f[0];
        this.p0[1] = f[1];
    }

    /**
     * @param p0
     *            the p0 to set
     */
    public void setP0(Point2d p) {
        this.p0[0] = (float) p.x;
        this.p0[1] = (float) p.y;
    }

    /**
     * @param p0
     *            the p0 to set
     */
    public void setP0(float p00, float p01) {
        this.p0[0] = p00;
        this.p0[1] = p01;
    }

    /**
     * @return the p1
     */
    public float[] getP1() {
        return this.p1;
    }

    /**
     * @param p1
     *            the p1 to set
     */
    public void setP1(float[] p1) {
        this.p1[0] = p1[0];
        this.p1[1] = p1[1];
    }

    /**
     * @param p0
     *            the p0 to set
     */
    public void setP1(Point2d p) {
        this.p1[0] = (float) p.x;
        this.p1[1] = (float) p.y;
    }

    /**
     * @param p1
     *            the p1 to set
     */
    public void setP1(float p10, float p11) {
        this.p1[0] = p10;
        this.p1[1] = p11;
    }

    /**
     * @return the p2
     */
    public float[] getP2() {
        return this.p2;
    }

    /**
     * @param p2
     *            the p2 to set
     */
    public void setP2(float[] p2) {
        this.p2[0] = p2[0];
        this.p2[1] = p2[1];
    }

    /**
     * @param p0
     *            the p0 to set
     */
    public void setP2(Point2d p) {
        this.p2[0] = (float) p.x;
        this.p2[1] = (float) p.y;
    }

    /**
     * @param p2
     *            the p2 to set
     */
    public void setP2(float p20, float p21) {
        this.p2[0] = p20;
        this.p2[1] = p21;
    }

    /**
     * @return the p0
     */
    public float[] getN0() {
        return this.n0;
    }

    /**
     * @param p0
     *            the p0 to set
     */
    public void setN0(float[] p) {
        this.n0[0] = p[0];
        this.n0[1] = p[1];
    }

    /**
     * @param p0
     *            the p0 to set
     */
    public void setN0(Point2d p) {
        this.n0[0] = (float) p.x;
        this.n0[1] = (float) p.y;
    }

    /**
     * @param n2
     *            the n2 to set
     */
    public void setN0(float n20, float n21) {
        this.n0[0] = n20;
        this.n0[1] = n21;
    }

    /**
     * @return the p0
     */
    public float[] getN2() {
        return this.n2;
    }

    /**
     * @param p0
     *            the p0 to set
     */
    public void setN2(float[] p) {
        this.n2[0] = p[0];
        this.n2[1] = p[1];
    }

    /**
     * @param p0
     *            the p0 to set
     */
    public void setN2(Point2d p) {
        this.n2[0] = (float) p.x;
        this.n2[1] = (float) p.y;
    }

    /**
     * @param n2
     *            the n2 to set
     */
    public void setN2(float n20, float n21) {
        this.n2[0] = n20;
        this.n2[1] = n21;
    }

    /**
     * @return the uv
     */
    public float[] getUs() {
        return this.us;
    }

    /**
     * @param us
     *            the uv to set
     */
    public void setUs(float u, float v) {
        this.us[0] = u;
        this.us[1] = v;
    }

    /**
     * @param us
     *            the uv to set
     */
    public void setUs(float[] f) {
        this.us[0] = f[0];
        this.us[1] = f[1];
    }

    /**
     * @param color
     *            the color to set
     */
    public void setColor(float[] color) {
        this.color[0] = color[0];
        this.color[1] = color[1];
        this.color[2] = color[2];
        this.color[3] = color[3];
    }

    /**
     * @return the uv
     */
    public float[] getColor() {
        return this.color;
    }

    /**
     * @param color
     *            the color to set
     */
    public void setColor(float r, float g, float b, float a) {
        this.color[0] = r;
        this.color[1] = g;
        this.color[2] = b;
        this.color[3] = a;
    }

    /**
     * @return the curvature
     */
    public float getLineWidth() {
        return this.lineWidth;
    }

    /**
     * @param lineWidth
     *            the curvature to set
     */
    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GLBezierShadingVertex [xy=" + Arrays.toString(this.xy)
                + ", us=" + Arrays.toString(this.us) + ", color="
                + Arrays.toString(this.color) + ", lineWidth=" + this.lineWidth
                + ", maxU=" + this.maxU + ", p0=" + Arrays.toString(this.p0)
                + ", p1=" + Arrays.toString(this.p1) + ", p2="
                + Arrays.toString(this.p2) + ", n0=" + Arrays.toString(this.n0)
                + ", n2=" + Arrays.toString(this.n2) + ", paperUV="
                + Arrays.toString(this.paperUV) + "]";
    }

}
