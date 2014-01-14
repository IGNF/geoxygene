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

import org.apache.log4j.Logger;

/**
 * @author JeT a texture image is used to project a polygon onto with
 *         distance and linear parameterization informations.
 *         each pixel contains texture coordinates and some
 *         temporary information
 */
public class TextureImage {

    public class TexturePixel {
        public double uTexture = 0;
        public double vTexture = 0;
        public double distance = -1.;
        public double linearParameter = 0;
        public int closestFrontier = 0; // 0: not defined -1: outer frontier -n: nth inner frontier
        public boolean in = false; // pixel inside the polygon (frontier excluded)
        public int frontier = 0; // pixel on the polygon boundary (border count)
        public int weightSum = 0; // some elements are computed as a weighted average

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "TexturePixel [uTexture=" + this.uTexture + ", vTexture=" + this.vTexture + ", distance=" + this.distance + ", linearParameter="
                    + this.linearParameter + ", closestFrontier=" + this.closestFrontier + ", in=" + this.in + ", frontier=" + this.frontier + "]";
        }

    }

    private int width = 0;
    private int height = 0;
    private int size = 0; // width * height
    private TexturePixel[] pixels = null;
    private static final Logger logger = Logger.getLogger(TextureImage.class.getName()); // logger
    private static final double SQRT2 = Math.sqrt(2.);

    private static final double LINEAR_PARAMETER_SCALE = 50.; // 1: world coordinates N: texture coordinates divided by N 

    /**
     * constructor
     * 
     */
    public TextureImage() {
    }

    /**
     * constructor
     * 
     * @param width
     *            projected image width
     * @param height
     *            projected image height
     */
    public TextureImage(final int width, final int height) {
        this.setDimension(width, height);
    }

    /**
     * Set image dimension and initialize pixels
     * 
     * @param width
     *            image width
     * @param height
     *            image height
     */
    public final void setDimension(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.size = width * height;
        if (this.size <= 1) {
            throw new IllegalStateException("image too small = " + width + "x" + height + " = " + this.size);
        }
        this.pixels = new TexturePixel[this.size];
        for (int l = 0; l < this.size; l++) {
            this.pixels[l] = new TexturePixel();
        }

    }

    /**
     * @return the width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * get the specified pixel
     */
    public TexturePixel getPixel(int x, int y) {
        if (x < 0 || x >= this.getWidth() || y < 0 || y >= this.getHeight()) {
            return null;
        }
        if (this.pixels == null) {
            return null;
        }
        try {
            return this.pixels[x + y * this.getWidth()];
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("try to get pixel " + x + "x" + y + " = " + (x + y * this.getHeight()) + " <? " + this.pixels.length + " / " + this.pixels.length
                    + " = " + ((x + y * this.getHeight()) < this.size) + " " + this.getWidth() + "x" + this.getHeight());
            return null;
        }
    }

    //    /**
    //     * @param polygon
    //     *            the polygon to set
    //     */
    //    public final void setPolygon(ParameterizedPolygon polygon) {
    //        this.polygon = polygon;
    //        this.invalidatePolygon();
    //        //this.projectPolygon();
    //    }
    //
    //    /**
    //     * @return the polygon
    //     */
    //    public ParameterizedPolygon getPolygon() {
    //        return this.polygon;
    //    }

    /**
     * get all pixels
     */
    public TexturePixel[] getPixels() {
        return this.pixels;
    }

    /**
     * Draw a line using bresenham algorithm in image
     * 
     * @param xi
     *            x origin
     * @param yi
     *            y origin
     * @param xf
     *            x end
     * @param yf
     *            y end
     * @param image
     *            image in which the line is drawn
     * @param renderer
     *            how to write pixels
     */
    public void drawLine(final int xi, final int yi, final int xf, final int yf, TexturePixelRenderer renderer) {
        int dx, dy, i, xinc, yinc, cumul, x, y;
        renderer.setCurrentLine(xi, yi, xf, yf);
        x = xi;
        y = yi;
        dx = xf - xi;
        dy = yf - yi;
        xinc = (dx > 0) ? 1 : -1;
        yinc = (dy > 0) ? 1 : -1;
        dx = Math.abs(dx);
        dy = Math.abs(dy);
        renderer.renderFirstPixel(x, y, this);
        if (dx > dy) {
            cumul = dx / 2;
            for (i = 1; i <= dx; i++) {
                x += xinc;
                cumul += dy;
                if (cumul >= dx) {
                    cumul -= dx;
                    y += yinc;
                }
                if (i < dx) {
                    renderer.renderPixel(x, y, this);
                } else {
                    renderer.renderLastPixel(x, y, this);
                }
            }
        } else {
            cumul = dy / 2;
            for (i = 1; i <= dy; i++) {
                y += yinc;
                cumul += dx;
                if (cumul >= dy) {
                    cumul -= dy;
                    x += xinc;
                }
                if (i < dy) {
                    renderer.renderPixel(x, y, this);
                } else {
                    renderer.renderLastPixel(x, y, this);
                }
            }
        }
    }

}
