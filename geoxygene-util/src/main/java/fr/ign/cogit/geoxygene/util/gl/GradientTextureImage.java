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

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

/**
 * @author JeT a texture image is used to project a polygon onto with
 *         distance and linear parameterization informations.
 *         each pixel contains texture coordinates and some
 *         temporary information
 */
public class GradientTextureImage {

    public class TexturePixel {
        public double uTexture = 0;
        public double vTexture = 0;
        public double uTextureWeightSum = 0;
        public double vTextureWeightSum = 0;
        public double distance = -1.;
        public double linearParameter = 0;
        public int closestFrontier = 0; // 0: not defined -1: outer frontier -n: nth inner frontier
        public Point2d closestPoint = null;
        public boolean in = false; // pixel inside the polygon (frontier excluded)
        public int frontier = 0; // pixel on the polygon boundary (border count)
        public double weightSum = 0; // some elements are computed as a weighted average
        public Point2d vGradient = null; // gradient component of v Value
        public double mainDirection; // 
        public Sample sample = null; // associated sample (or null)

        public TexturePixel() {

        }

        public TexturePixel(final TexturePixel src) {
            this.uTexture = src.uTexture;
            this.vTexture = src.vTexture;
            this.uTextureWeightSum = src.uTextureWeightSum;
            this.vTextureWeightSum = src.vTextureWeightSum;
            this.distance = src.distance;
            this.linearParameter = src.linearParameter;
            this.closestFrontier = src.closestFrontier;
            this.closestPoint = src.closestPoint == null ? null : new Point2d(src.closestPoint);
            this.in = src.in;
            this.frontier = src.frontier;
            this.weightSum = src.weightSum;
            this.vGradient = src.vGradient == null ? null : new Point2d(src.vGradient);
            this.mainDirection = src.mainDirection;
            this.sample = src.sample;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "TexturePixel [uTexture=" + this.uTexture + ", vTexture=" + this.vTexture + ", distance=" + this.distance + ", linearParameter="
                    + this.linearParameter + ", closestFrontier=" + this.closestFrontier + ", in=" + this.in + ", frontier=" + this.frontier + ", v-gradient="
                    + this.vGradient + ", sample=" + this.sample + "]";
        }
    }

    private int width = 0;
    private int height = 0;
    private int size = 0; // width * height
    private TexturePixel[] pixels = null;
    private static final Logger logger = Logger.getLogger(GradientTextureImage.class.getName()); // logger
    public Double uMin = null;
    public Double uMax = null;
    public Double vMin = null;
    public Double vMax = null;
    public Double dMin = null;
    public Double dMax = null;

    /**
     * constructor
     * 
     */
    public GradientTextureImage() {
    }

    /**
     * Copy constructor
     */
    public GradientTextureImage(final GradientTextureImage src) {
        this.setDimension(src.width, src.height);
        for (int l = 0; l < src.size; l++) {
            this.pixels[l] = new TexturePixel(src.getPixels()[l]);
        }

    }

    /**
     * constructor
     * 
     * @param width
     *            projected image width
     * @param height
     *            projected image height
     */
    public GradientTextureImage(final int width, final int height) {
        this.setDimension(width, height);
    }

    /**
     * get the transformation applied to a rectangle at a given point
     * 
     * @param x
     *            rectangle center X coordinate
     * @param y
     *            rectangle center Y coordinate
     * @param width
     *            rectangle width
     * @param height
     *            rectangle height
     * @return
     */
    public AffineTransform tileTransform(int x, int y, int width, int height) {
        TexturePixel pixel = this.getPixel(x, y);
        if (pixel == null) {
            return null;
        }
        // that's not really a good idea to set the gradient here. But sometimes
        // (one pixel width lines) gradient cannot be computed...
        if (pixel.vGradient == null) {
            pixel.vGradient = new Point2d(0, 0);
        }
        Point2D rotation = new Point2D.Double(pixel.vGradient.x, pixel.vGradient.y);
        AffineTransform transform = new AffineTransform(); // from tile to image pixel coordinates
        transform.translate(x - width / 2., y - height / 2.);
        transform.rotate(rotation.getX(), rotation.getY(), width / 2., height / 2.);
        return transform;
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
     * @return the uMin
     */
    public final Double getuMin() {
        if (this.uMin == null) {
            this.computeUVBounds();
        }
        return this.uMin;
    }

    /**
     * @return the uMax
     */
    public final Double getuMax() {
        if (this.uMax == null) {
            this.computeUVBounds();
        }
        return this.uMax;
    }

    /**
     * @return the vMin
     */
    public final Double getvMin() {
        if (this.vMin == null) {
            this.computeUVBounds();
        }
        return this.vMin;
    }

    /**
     * @return the vMax
     */
    public final Double getvMax() {
        if (this.vMax == null) {
            this.computeUVBounds();
        }
        return this.vMax;
    }

    /**
     * @return the dMin
     */
    public final Double getdMin() {
        if (this.dMax == null) {
            this.computeUVBounds();
        }
        return this.dMin;
    }

    /**
     * @return the dMax
     */
    public final Double getdMax() {
        if (this.dMax == null) {
            this.computeUVBounds();
        }
        return this.dMax;
    }

    /**
     * Compute min & max values of the image uTexture/vTexture pixels
     */
    private void computeUVBounds() {
        double uMin = Double.MAX_VALUE;
        double uMax = -Double.MAX_VALUE;
        double vMin = Double.MAX_VALUE;
        double vMax = -Double.MAX_VALUE;
        double dMin = Double.MAX_VALUE;
        double dMax = -Double.MAX_VALUE;
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                TexturePixel pixel = this.getPixel(x, y);
                //                    System.err.println("calcul distance d = " + pixel.distance + " min = " + dMin + " max = " + dMax);
                if (pixel.uTexture < uMin) {
                    uMin = pixel.uTexture;
                }
                if (pixel.uTexture > uMax && pixel.uTexture != Double.POSITIVE_INFINITY && pixel.uTexture != Double.MAX_VALUE) {
                    uMax = pixel.uTexture;
                }
                if (pixel.vTexture < vMin) {
                    vMin = pixel.vTexture;
                }
                if (pixel.vTexture > vMax && pixel.vTexture != Double.POSITIVE_INFINITY && pixel.vTexture != Double.MAX_VALUE) {
                    vMax = pixel.vTexture;
                }
                if (pixel.distance < dMin) {
                    dMin = pixel.distance;
                }
                if (pixel.distance > dMax && pixel.distance != Double.POSITIVE_INFINITY && pixel.distance != Double.MAX_VALUE) {
                    dMax = pixel.distance;
                }
            }
        }
        this.uMin = new Double(uMin);
        this.uMax = new Double(uMax);
        this.vMin = new Double(vMin);
        this.vMax = new Double(vMax);
        this.dMin = new Double(dMin);
        this.dMax = new Double(dMax);
    }

    /**
     * invlaidate precomputations
     */
    public void invalidateUVBounds() {
        this.uMin = this.uMax = this.vMin = this.vMax = this.dMin = this.dMax = null;
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
