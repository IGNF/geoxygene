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
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;
import fr.ign.cogit.geoxygene.util.gl.Sample;

/**
 * @author JeT a texture image is used to project a polygon onto with distance
 *         and linear parameterization informations. each pixel contains texture
 *         coordinates and some temporary information
 */
public class BinaryGradientImage{

    public static class GradientPixel {
        public double uTexture = 0;
        public double vTexture = 0;
        public double uTextureWeightSum = 0;
        public double vTextureWeightSum = 0;
        public double distance = -1.;
        public double linearParameter = 0;
        public int closestFrontier = 0; // 0: not defined -1: outer frontier -n:
                                        // nth inner frontier
        public Point2d closestPoint = null;
        public boolean in = false; // pixel inside the polygon (frontier
                                   // excluded)
        public int frontier = 0; // pixel on the polygon boundary (border count)
        public double weightSum = 0; // some elements are computed as a weighted
                                     // average
        public Point2d vGradient = null; // gradient component of v Value
        public double mainDirection; //
        public Sample sample = null; // associated sample (or null)

        /**
         * default constructor
         */
        public GradientPixel() {
        }

        public GradientPixel(final GradientPixel src) {
            this.uTexture = src.uTexture;
            this.vTexture = src.vTexture;
            this.uTextureWeightSum = src.uTextureWeightSum;
            this.vTextureWeightSum = src.vTextureWeightSum;
            this.distance = src.distance;
            this.linearParameter = src.linearParameter;
            this.closestFrontier = src.closestFrontier;
            this.closestPoint = src.closestPoint == null ? null : new Point2d(
                    src.closestPoint);
            this.in = src.in;
            this.frontier = src.frontier;
            this.weightSum = src.weightSum;
            this.vGradient = src.vGradient == null ? null : new Point2d(
                    src.vGradient);
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
            return "TexturePixel [uTexture=" + this.uTexture + ", vTexture="
                    + this.vTexture + ", distance=" + this.distance
                    + ", linearParameter=" + this.linearParameter
                    + ", closestFrontier=" + this.closestFrontier + ", in="
                    + this.in + ", frontier=" + this.frontier + ", v-gradient="
                    + this.vGradient + ", sample=" + this.sample + "]";
        }
    }

    private int width = 0;
    private int height = 0;
    private int size = 0; // width * height
    private GradientPixel[] pixels = null;
    private static final Logger logger = Logger
            .getLogger(BinaryGradientImage.class.getName()); // logger
    public Double uMin = null;
    public Double uMax = null;
    public Double vMin = null;
    public Double vMax = null;
    public Double dMin = null;
    public Double dMax = null;


    /**
     * constructor
     * 
     * @param width
     *            projected image width
     * @param height
     *            projected image height
     */
    public BinaryGradientImage(final int width, final int height) {

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
        GradientPixel pixel = this.getPixel(x, y);
        if (pixel == null) {
            return null;
        }
        // that's not really a good idea to set the gradient here. But sometimes
        // (one pixel width lines) gradient cannot be computed...
        if (pixel.vGradient == null) {
            pixel.vGradient = new Point2d(0, 0);
        }
        Point2D rotation = new Point2D.Double(pixel.vGradient.x,
                pixel.vGradient.y);
        AffineTransform transform = new AffineTransform(); // from tile to image
                                                           // pixel coordinates
        transform.translate(x - width / 2., y - height / 2.);
        transform.rotate(rotation.getX(), rotation.getY(), width / 2.,
                height / 2.);
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
            this.pixels = null;
        } else {
            this.pixels = new GradientPixel[this.size];
            for (int l = 0; l < this.size; l++) {
                this.pixels[l] = new GradientPixel();
            }
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
                GradientPixel pixel = this.getPixel(x, y);
                // System.err.println("calcul distance d = " + pixel.distance +
                // " min = " + dMin + " max = " + dMax);
                if (pixel.uTexture < uMin) {
                    uMin = pixel.uTexture;
                }
                if (pixel.uTexture > uMax
                        && pixel.uTexture != Double.POSITIVE_INFINITY
                        && pixel.uTexture != Double.MAX_VALUE) {
                    uMax = pixel.uTexture;
                }
                if (pixel.vTexture < vMin) {
                    vMin = pixel.vTexture;
                }
                if (pixel.vTexture > vMax
                        && pixel.vTexture != Double.POSITIVE_INFINITY
                        && pixel.vTexture != Double.MAX_VALUE) {
                    vMax = pixel.vTexture;
                }
                if (pixel.distance < dMin) {
                    dMin = pixel.distance;
                }
                if (pixel.distance > dMax
                        && pixel.distance != Double.POSITIVE_INFINITY
                        && pixel.distance != Double.MAX_VALUE) {
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
    public GradientPixel getPixel(int x, int y) {
        if (x < 0 || x >= this.getWidth() || y < 0 || y >= this.getHeight()) {
            return null;
        }
        if (this.pixels == null) {
            return null;
        }
        try {
            return this.pixels[x + y * this.getWidth()];
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("try to get pixel " + x + "x" + y + " = "
                    + (x + y * this.getHeight()) + " <? " + this.pixels.length
                    + " / " + this.pixels.length + " = "
                    + ((x + y * this.getHeight()) < this.size) + " "
                    + this.getWidth() + "x" + this.getHeight());
            return null;
        }
    }

    // /**
    // * @param polygon
    // * the polygon to set
    // */
    // public final void setPolygon(ParameterizedPolygon polygon) {
    // this.polygon = polygon;
    // this.invalidatePolygon();
    // //this.projectPolygon();
    // }
    //
    // /**
    // * @return the polygon
    // */
    // public ParameterizedPolygon getPolygon() {
    // return this.polygon;
    // }

    /**
     * get all pixels
     */
    public GradientPixel[] getPixels() {
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
    public void drawLine(final int xi, final int yi, final int xf,
            final int yf, TexturePixelRenderer renderer) {
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

    public static BinaryGradientImage generateBinaryGradientImage(
            BinaryGradientImageParameters params) {
        BinaryGradientImage gradientImage = null;
        try {
            gradientImage = new BinaryGradientImage(params.getWidth(),
                    params.getHeight());
        } catch (Throwable e) {
            logger.error("Something wrong occurred generating gradient image size "
                    + params.getWidth()
                    + "x"
                    + params.getHeight()
                    + " : "
                    + e.getMessage());
            return null;
        }
        // draw Frontiers into texture image
        DistanceFieldFrontierPixelRenderer pixelRenderer = new DistanceFieldFrontierPixelRenderer();
        for (IPolygon polygon : params.getPolygons()) {
            pixelRenderer.getYs().clear(); // #note1
            // draw the outer frontier
            drawFrontier(gradientImage, polygon.getExterior(), 1,
                    pixelRenderer, params);
            // draw all inner frontiers
            for (int innerFrontierIndex = 0; innerFrontierIndex < polygon
                    .getInterior().size(); innerFrontierIndex++) {
                IRing innerFrontier = polygon.getInterior().get(
                        innerFrontierIndex);
                drawFrontier(gradientImage, innerFrontier,
                        -innerFrontierIndex - 1, pixelRenderer, params);
            }

            fillHorizontally(gradientImage, pixelRenderer.getYs(), params); // #note1
        }
        // #note1
        // fillHorizontally was previously outside the polygon loop. It is valid
        // if polygons
        // do not self intersect. It has been modified due to a shape file that
        // has a full duplicated geometry
        // may be we can assert that polygons may not self intersect and get
        // back to previous version...

        // try {
        // BinaryGradientImageUtil.save(gradientImage, "1-frontiers");
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // for (Map.Entry<Integer, List<Integer>> entry :
        // pixelRenderer.getYs().entrySet()) {
        // System.err.println("Y(" + entry.getKey() + ") = " +
        // Arrays.toString(entry.getValue().toArray(new Integer[0])));
        // }
        // fills the inner pixels
        // try {
        // TextureImageUtil.save(texImage, "2-filled");
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        Set<Point> modifiedPixels = new HashSet<Point>();

        // FIXME: HACK: remove long edges (for the sea outside borders not to be
        // considered as sea edges)
        modifiedPixels = getModifiedPixelsButInfiniteDistancePixels(
                gradientImage, pixelRenderer.getModifiedPixels());

        // Set<Point> nonInfiniteModifiedPixels =
        // pixelRenderer.getModifiedPixels();
        while (!modifiedPixels.isEmpty()) {
            modifiedPixels = fillTextureCoordinates4(gradientImage,
                    modifiedPixels, params.getImageToPolygonFactorX(),
                    params.getImageToPolygonFactorY());
        }
        // try {
        // TextureImageUtil.save(texImage, "3-texcoord");
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        fillVWithDistance(gradientImage);
        // scaleV(gradientImage, gradientImage.getdMax());
        computeGradient(gradientImage);
        // blurring filter to eliminate some high frequencies

        BinaryGradientImageUtil.blurTextureCoordinates(gradientImage,
                params.blurValue);
        // try {
        // TextureImageUtil.save(texImage, "4-gradient");
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        return gradientImage;
    }

    private static void computeGradient(BinaryGradientImage texImage) {
        for (int y = 0; y < texImage.getHeight(); y++) {
            for (int x = 0; x < texImage.getWidth(); x++) {
                GradientPixel pixel = texImage.getPixel(x, y);
                if (pixel.in) {
                    // pixel.vGradient = new
                    // Point2d(Math.cos(pixel.mainDirection),
                    // Math.sin(pixel.mainDirection));
                    pixel.vGradient = computeGradient(texImage, x, y);
                } else {
                    pixel.vGradient = null;
                }
            }
        }
        
        /// Uncomment these lines to save raw 
//        // -distance field
//        BufferedImage im = toBufferedImageDistanceHSV(texImage);
//        //
//        // -gradient direction 
//        BufferedImage im = toBufferedImageGradientRGB(texImage);
//        File outputfile = new File("/home/nmellado/saved.png");
//        try {
//          ImageIO.write(im, "png", outputfile);
//        } catch (IOException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//        }
    }

    private static Point2d computeGradient(BinaryGradientImage image, int x,
            int y) {
        GradientPixel p = image.getPixel(x, y);
        GradientPixel pxp1 = image.getPixel(x + 1, y);
        GradientPixel pxm1 = image.getPixel(x - 1, y);
        GradientPixel pyp1 = image.getPixel(x, y + 1);
        GradientPixel pym1 = image.getPixel(x, y - 1);
        double dx = 0, dy = 0;
        if (pxp1 != null && pxm1 != null) {
            dx = pxp1.vTexture - pxm1.vTexture;
        } else if (pxp1 == null && pxm1 != null) {
            dx = p.vTexture - pxm1.vTexture;
        } else if (pxm1 == null && pxp1 != null) {
            dx = pxp1.vTexture - p.vTexture;
        }
        if (pyp1 != null && pym1 != null) {
            dy = pyp1.vTexture - pym1.vTexture;
        } else if (pyp1 == null && pym1 != null) {
            dy = p.vTexture - pym1.vTexture;
        } else if (pym1 == null && pyp1 != null) {
            dy = pyp1.vTexture - p.vTexture;
        }
        return new Point2d(-dy, dx);
    }

    /**
     * @param image
     * @param maxDistance
     */
    private static void scaleV(BinaryGradientImage image, double maxDistance) {
        // fill yTexture coordinates as distance / maxDistance for any pixel
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                GradientPixel pixel = image.getPixel(x, y);
                pixel.vTexture = pixel.distance / maxDistance;
            }
        }
    }

    /**
     * @param image
     * @param maxDistance
     */
    private static void fillVWithDistance(BinaryGradientImage image) {
        // fill yTexture coordinates as distance / maxDistance for any pixel
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                GradientPixel pixel = image.getPixel(x, y);
                pixel.vTexture = pixel.distance;
            }
        }
    }

    /**
     * Modify the neighbors pixel distance according to the current pixel
     * distance
     * 
     * @param set
     */
    private static Set<Point> fillTextureCoordinates4(
            BinaryGradientImage image, Set<Point> set, final double pixelWidth,
            final double pixelHeight) {
        // System.err.println(modifiedPixels.size() + " modified pixels");
        HashSet<Point> newlyModifiedPixels = new HashSet<Point>();
        for (Point p : set) {
            GradientPixel pixel = image.getPixel(p.x, p.y);
            if (pixel == null) {
                throw new IllegalStateException(
                        "modified pixels cannot be outside image ... " + p.x
                                + "x" + p.y);
            }
            double distance = pixel.distance;
            fillTextureCoordinates(image, distance + pixelWidth,
                    pixel.uTexture, new Point(p.x - 1, p.y),
                    newlyModifiedPixels);
            fillTextureCoordinates(image, distance + pixelWidth,
                    pixel.uTexture, new Point(p.x + 1, p.y),
                    newlyModifiedPixels);
            fillTextureCoordinates(image, distance + pixelHeight,
                    pixel.uTexture, new Point(p.x, p.y - 1),
                    newlyModifiedPixels);
            fillTextureCoordinates(image, distance + pixelHeight,
                    pixel.uTexture, new Point(p.x, p.y + 1),
                    newlyModifiedPixels);
        }
        return newlyModifiedPixels;
    }

    /**
     * Modify the specified pixel with the given distance if it is smaller than
     * the current stored
     * 
     * @param d
     *            distance to try to set to current pixel
     * @param point
     *            current point to try to set distance
     * @param newlyModifiedPixels
     *            pixel position is added to this list if this pixel distance
     *            value has been modified
     */
    private static boolean fillTextureCoordinates(BinaryGradientImage texImage,
            double d, double uTexture, Point p, Set<Point> newlyModifiedPixels) {
        GradientPixel pixel = texImage.getPixel(p.x, p.y);
        if (pixel == null) {
            return false;
        }
        if (pixel.in && pixel.distance > d) {
            pixel.distance = d;
            pixel.uTexture = uTexture;
            newlyModifiedPixels.add(p);
            return true;
        }
        return false;
    }

    /**
     * Remove all pixels which have an infinite distance from the modified
     * pixels.
     * 
     * @param modifiedPixels
     * @return
     */
    private static Set<Point> getModifiedPixelsButInfiniteDistancePixels(
            BinaryGradientImage texImage, Set<Point> modifiedPixels) {
        Set<Point> nonInfiniteModifiedPixels = new HashSet<Point>();
        for (Point p : modifiedPixels) {
            GradientPixel pixel = texImage.getPixel(p.x, p.y);
            if (pixel.distance != Double.POSITIVE_INFINITY) {
                nonInfiniteModifiedPixels.add(p);
            } else {
                pixel.uTexture = 0;
                pixel.vTexture = 0;
            }
        }
        texImage.invalidateUVBounds();
        return nonInfiniteModifiedPixels;
    }

    /**
     * @param ys
     *            list of y values containing a list of x-values
     * @param image
     */
    private static void fillHorizontally(BinaryGradientImage texImage,
            Map<Integer, List<Integer>> ys, BinaryGradientImageParameters params) {
        for (int y = 0; y < texImage.getHeight(); y++) {
            List<Integer> xs = ys.get(y);
            if (xs == null || xs.size() == 0) {
                continue;
            }
            Collections.sort(xs); // order by x values
            if (xs.size() % 2 != 0) {
                logger.warn("x values count cannot be even ! y = " + y + " : "
                        + xs.size() + " : " + xs);
            }
            // draw horizontal lines between xs pixel pairs/couples
            for (int n = 0; n < xs.size() / 2; n++) {
                int x1 = Math.min(xs.get(2 * n), xs.get(2 * n + 1));
                int x2 = Math.max(xs.get(2 * n), xs.get(2 * n + 1));
                for (int x = x1; x <= x2; x++) {
                    GradientPixel pixel = texImage.getPixel(x, y);
                    if (pixel != null) {
                        pixel.in = true;
                        if (pixel.frontier == 0) {
                            pixel.distance = Double.MAX_VALUE;
                        }
                    } else {
                        logger.warn("forget unknown pixel " + x + "x" + y
                                + " = " + pixel);
                    }

                }
            }

        }
    }

    /**
     * draw a polygon's frontier in the image using the selected renderer
     * 
     * @param frontier
     * @param pixelRenderer
     */
    private static void drawFrontier(BinaryGradientImage texImage,
            IRing frontier, int frontierId,
            DistanceFieldFrontierPixelRenderer pixelRenderer,
            BinaryGradientImageParameters params) {
        pixelRenderer.setCurrentFrontier(frontierId);
        int frontierSize = frontier.coord().size();
        if (frontierSize < 3) {
            logger.error("Cannot fill a polygon with less than 3 points");
            return;
        }
        IDirectPosition p0 = frontier.coord().get(frontierSize - 1);// previous
                                                                    // point
        IDirectPosition p1 = frontier.coord().get(0); // start point line to
                                                      // draw
        IDirectPosition p2 = frontier.coord().get(1); // end point line to draw
        // double frontierLength = frontier.length();
        double segmentLength = Math.sqrt((p2.getX() - p1.getX())
                * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY())
                * (p2.getY() - p1.getY()));
        // convert world-based coordinates to projection-space coordinates
        Point2D proj0 = params.worldToProj(p0);
        Point2D proj1 = params.worldToProj(p1);
        Point2D proj2 = params.worldToProj(p2);
        // int x0 = (int) proj0.getX();
        int y0 = (int) proj0.getY();
        int x1 = (int) proj1.getX();
        int y1 = (int) proj1.getY();
        int x2 = (int) proj2.getX();
        int y2 = (int) proj2.getY();

        // find last non null direction
        int lastDirection = y1 - y0;
        int index = frontierSize - 2;
        while (lastDirection == 0 && index >= 0) {
            y1 = y0;
            y0 = (int) params.worldToProj(frontier.coord().get(index)).getY();
            lastDirection = y1 - y0;
            index--;
        }
        y0 = (int) proj0.getY();
        y1 = (int) proj1.getY();

        double linearDistance = 0; // linear parameterization along the frontier
        for (int nPoint = 0; nPoint < frontierSize; nPoint++) {
            // check if previous and next points are on the same Y side (cusp)
            // if the line is horizontal, keep previous cusp
            if (y1 != y2) {
                pixelRenderer.setCusp(lastDirection * (y2 - y1) < 0);
                lastDirection = y2 - y1;
            }

            // here we can choose the parameterization along frontiers
            pixelRenderer.setLinearParameterization(linearDistance,
                    linearDistance + segmentLength);
            // special case not to set distance to zero in some cases (for outer
            // sea limits which are not real coast lines
            if (segmentLength > params.getMaxCoastlineLength()) {
                logger.debug("segment " + segmentLength + " long removed (>"
                        + params.getMaxCoastlineLength() + ")");
            }
            pixelRenderer.setDistanceToZero(segmentLength < params
                    .getMaxCoastlineLength());
            if (!(x1 == x2 && y1 == y2)) {
                texImage.drawLine(x1, y1, x2, y2, pixelRenderer);
            }

            linearDistance += segmentLength;
            p0 = p1;
            p1 = p2;
            p2 = frontier.coord().get((nPoint + 1) % frontierSize);
            segmentLength = Math.sqrt((p2.getX() - p1.getX())
                    * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY())
                    * (p2.getY() - p1.getY()));

            proj0 = proj1;
            proj1 = proj2;
            proj2 = params.worldToProj(p2);
            y0 = y1;
            x1 = x2;
            y1 = y2;
            x2 = (int) proj2.getX();
            y2 = (int) proj2.getY();

        }
    }

    public static class BinaryGradientImageParameters {

        private final int width;
        private final int height;
        private List<IPolygon> polygons = null;
        private final double minX, maxX;
        private final double minY, maxY;
        private final double imageToPolygonFactorX;
        private final double imageToPolygonFactorY;
        private final double maxCoastlineLength;
        private int blurValue = 0; // half size of a square window around pixel

        public BinaryGradientImageParameters(int width, int height,
                List<IPolygon> polygons, IEnvelope envelope,
                double maxCoastlineLength, int blurValue) {
            super();
            this.width = width;
            this.height = height;
            this.polygons = polygons;
            this.minX = envelope.getLowerCorner().getX();
            this.minY = envelope.getLowerCorner().getY();
            this.maxX = envelope.getUpperCorner().getX();
            this.maxY = envelope.getUpperCorner().getY();
            this.imageToPolygonFactorX = (this.maxX - this.minX)
                    / (this.width - 1);
            this.imageToPolygonFactorY = (this.maxY - this.minY)
                    / (this.height - 1);
            this.maxCoastlineLength = maxCoastlineLength;
            this.blurValue = blurValue;
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
         * @return the polygons
         */
        public List<IPolygon> getPolygons() {
            return this.polygons;
        }

        /**
         * @return the minX
         */
        public double getMinX() {
            return this.minX;
        }

        /**
         * @return the minY
         */
        public double getMinY() {
            return this.minY;
        }

        /**
         * convert point coordinates from polygon space to image space
         * 
         * @param polygonCoordinates
         * @return
         */
        public Point2D worldToProj(Point2D polygonCoordinates) {
            return new Point2D.Double((polygonCoordinates.getX() - this.minX)
                    / this.imageToPolygonFactorX,
                    (polygonCoordinates.getY() - this.minY)
                            / this.imageToPolygonFactorY);
        }

        /**
         * @return the imageToPolygonFactorX
         */
        public double getImageToPolygonFactorX() {
            return this.imageToPolygonFactorX;
        }

        /**
         * @return the imageToPolygonFactorY
         */
        public double getImageToPolygonFactorY() {
            return this.imageToPolygonFactorY;
        }

        public double getMaxCoastlineLength() {
            return this.maxCoastlineLength;
        }

        /**
         * convert point coordinates from polygon space to image space
         * 
         * @param polygonCoordinates
         * @return
         */
        public Point2D worldToProj(Point2d polygonCoordinates) {
            return new Point2D.Double((polygonCoordinates.x - this.minX)
                    / this.imageToPolygonFactorX,
                    (polygonCoordinates.y - this.minY)
                            / this.imageToPolygonFactorY);
        }

        /**
         * convert point coordinates from polygon space to image space
         * 
         * @param polygonCoordinates
         * @return
         */
        public Point2D worldToProj(IDirectPosition polygonCoordinates) {
            return new Point2D.Double((polygonCoordinates.getX() - this.minX)
                    / this.imageToPolygonFactorX,
                    (polygonCoordinates.getY() - this.minY)
                            / this.imageToPolygonFactorY);
        }

    }

    public static BufferedImage toBufferedImageDistance(
            BinaryGradientImage image, Color c1, Color c2, Color cborder) {
        image.invalidateUVBounds();
        BufferedImage bi = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                GradientPixel pixel = image.getPixel(x, y);
                if (pixel.closestFrontier != 0) {
                    bi.setRGB(x, y, cborder.getRGB());
                } else if (pixel.distance == Double.POSITIVE_INFINITY
                        || pixel.distance == Double.MAX_VALUE) {
                    bi.setRGB(x, y, Color.red.getRGB());
                } else if (!pixel.in) {
                    bi.setRGB(x, y, Color.black.getRGB());
                } else {
                    float v = (float) Math.max(0,
                            Math.min(1, pixel.distance / image.getdMax()));
                    // System.err.println("v = " + v + " d = " + pixel.distance
                    // + " dMax = " + image.getdMax());
                    Color c = new Color(c1.getRed() / 255f * (1 - v) + v
                            * c2.getRed() / 255f, c1.getGreen() / 255f
                            * (1 - v) + v * c2.getGreen() / 255f, c1.getBlue()
                            / 255f * (1 - v) + v * c2.getBlue() / 255f);
                    bi.setRGB(x, y, c.getRGB());
                }
            }
        }
        return bi;
    }

    public static BufferedImage toBufferedImageDistanceHSV(
            BinaryGradientImage image) {
        image.invalidateUVBounds();
        BufferedImage bi = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                GradientPixel pixel = image.getPixel(x, y);
                if (pixel.closestFrontier != 0) {
                    bi.setRGB(x, y, Color.yellow.getRGB());
                } else if (pixel.distance == Double.POSITIVE_INFINITY
                        || pixel.distance == Double.MAX_VALUE) {
                    bi.setRGB(x, y, Color.red.getRGB());
                } else if (!pixel.in) {
                    bi.setRGB(x, y, Color.black.getRGB());
                } else {
                    float v = (float) Math.max(0,
                            Math.min(1, pixel.distance / image.getdMax()));
                    Color c = Color.getHSBColor(v, 1f, 1f);
                    bi.setRGB(x, y, c.getRGB());
                }
            }
        }
        return bi;
    }

    public static BufferedImage toBufferedImageDistanceStrip(
            BinaryGradientImage image, int nbStrips) {
        image.invalidateUVBounds();
        BufferedImage bi = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                GradientPixel pixel = image.getPixel(x, y);
                if (pixel.closestFrontier != 0) {
                    bi.setRGB(x, y, Color.yellow.getRGB());
                } else if (pixel.distance == Double.POSITIVE_INFINITY
                        || pixel.distance == Double.MAX_VALUE) {
                    bi.setRGB(x, y, Color.red.getRGB());
                } else if (!pixel.in) {
                    bi.setRGB(x, y, Color.black.getRGB());
                } else {

                    float v = ((int) (pixel.distance / image.getdMax() * nbStrips)) % 2;
                    bi.setRGB(x, y, new Color(v, v, v).getRGB());
                }
            }
        }
        return bi;
    }

    public static BufferedImage toBufferedImageU(BinaryGradientImage image) {
        image.invalidateUVBounds();
        BufferedImage bi = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                GradientPixel pixel = image.getPixel(x, y);
                if (!pixel.in) {
                    bi.setRGB(x, y, Color.black.getRGB());
                } else {
                    double u = (pixel.uTexture - image.getuMin())
                            / (image.getuMax() - image.getuMin());
                    Color c = Color.getHSBColor((float) u, 1f, 1f);
                    bi.setRGB(x, y, c.getRGB());
                }
            }
        }
        return bi;
    }

    
    /**
     * @brief Send normalized UV coordinates to HB components (HSB)
     * @param image
     * @return
     */
    public static BufferedImage toBufferedImageUV(BinaryGradientImage image) {
        if (image == null) {
            return null;
        }
        image.invalidateUVBounds();
        BufferedImage bi = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                GradientPixel pixel = image.getPixel(x, y);
                if (!pixel.in) {
                    bi.setRGB(x, y, Color.black.getRGB());
                } else {
                    double u = (pixel.uTexture - image.getuMin())
                            / (image.getuMax() - image.getuMin());
                    double v = (pixel.vTexture - image.getvMin())
                            / (image.getvMax() - image.getvMin());
                    Color c = Color.getHSBColor((float) u, 1f, (float) v);
                    bi.setRGB(x, y, c.getRGB());
                }
            }
        }
        return bi;
    }
    
    /**
     * @brief Send normalized pixel gradient coordinates to RG component (RGB)
     * @param image
     * @return
     */
    public static BufferedImage toBufferedImageGradientRGB(BinaryGradientImage image) {
      if (image == null) {
          return null;
      }
      image.invalidateUVBounds();
      BufferedImage bi = new BufferedImage(image.getWidth(),
              image.getHeight(), BufferedImage.TYPE_INT_ARGB);
      for (int y = 0; y < image.getHeight(); y++) {
          for (int x = 0; x < image.getWidth(); x++) {
              GradientPixel pixel = image.getPixel(x, y);
              if (!pixel.in) {
                  bi.setRGB(x, y, Color.black.getRGB());
              } else {
                double norm = pixel.vGradient.distance(new Point2d(0,0));
                double u    = (pixel.vGradient.x / norm) * 0.5 + 0.5;
                double v    = (pixel.vGradient.y / norm) * 0.5 + 0.5;
                bi.setRGB(x, y, new Color((float)u,(float)v,0.f).getRGB());
              }
          }
      }
      return bi;
  }

    public static BinaryGradientImage readBinaryGradientImage(File gradientFile)
            throws IOException {
        BinaryGradientImage img = new BinaryGradientImage(0, 0);
        FileInputStream is = null;
        DataInputStream dis = null;
        try {
            is = new FileInputStream(gradientFile);
            dis = new DataInputStream(is);

            int width = dis.readInt();
            int height = dis.readInt();

            try {
                img.setDimension(width, height);
            } catch (Error e) {
                logger.error("An error occurred allocating " + width + "x"
                        + height + " Gradient image from " + gradientFile);
            }
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    GradientPixel pixel = img.getPixel(x, y);
                    readTexturePixel(pixel, dis);
                }
            }

        } finally {

            // releases all system resources from the streams
            if (is != null) {
                is.close();
            }
            if (dis != null) {
                dis.close();
            }
        }
        return img;
    }

    public static void writeBinaryGradientImage(File gradientFile,
            BinaryGradientImage img) throws IOException {
        FileOutputStream fos = null;
        DataOutputStream dos = null;
        try {
            fos = new FileOutputStream(gradientFile);
            dos = new DataOutputStream(fos);

            dos.writeInt(img.getWidth());
            dos.writeInt(img.getHeight());

            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    GradientPixel pixel = img.getPixel(x, y);
                    writeTexturePixel(pixel, dos);

                }
            }

            dos.flush();
        } finally {

            if (dos != null) {
                dos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    private static void writeTexturePixel(GradientPixel pixel,
            DataOutputStream dis) throws IOException {
        dis.writeDouble(pixel.uTexture);
        dis.writeDouble(pixel.vTexture);
        dis.writeDouble(pixel.uTextureWeightSum);
        dis.writeDouble(pixel.vTextureWeightSum);
        dis.writeDouble(pixel.distance);
        dis.writeDouble(pixel.linearParameter);
        dis.writeInt(pixel.closestFrontier);
        if (pixel.closestPoint != null) {
            dis.writeDouble(pixel.closestPoint.x);
            dis.writeDouble(pixel.closestPoint.y);
        } else {
            dis.writeDouble(Double.POSITIVE_INFINITY);
            dis.writeDouble(Double.POSITIVE_INFINITY);
        }
        dis.writeBoolean(pixel.in);
        dis.writeInt(pixel.frontier);
        dis.writeDouble(pixel.weightSum);
        if (pixel.vGradient != null) {
            dis.writeDouble(pixel.vGradient.x);
            dis.writeDouble(pixel.vGradient.y);
        } else {
            dis.writeDouble(Double.POSITIVE_INFINITY);
            dis.writeDouble(Double.POSITIVE_INFINITY);
        }
        dis.writeDouble(pixel.mainDirection);
        pixel.sample = null;
    }

    private static void readTexturePixel(GradientPixel pixel,
            DataInputStream dis) throws IOException {
        pixel.uTexture = dis.readDouble();
        pixel.vTexture = dis.readDouble();
        pixel.uTextureWeightSum = dis.readDouble();
        pixel.vTextureWeightSum = dis.readDouble();
        pixel.distance = dis.readDouble();
        pixel.linearParameter = dis.readDouble();
        pixel.closestFrontier = dis.readInt();
        double x, y;
        x = dis.readDouble();
        y = dis.readDouble();
        pixel.closestPoint = new Point2d(x, y);
        pixel.in = dis.readBoolean();
        pixel.frontier = dis.readInt();
        pixel.weightSum = dis.readDouble();
        x = dis.readDouble();
        y = dis.readDouble();
        pixel.vGradient = new Point2d(x, y);
        pixel.mainDirection = dis.readDouble();
        pixel.sample = null;

    }

}
