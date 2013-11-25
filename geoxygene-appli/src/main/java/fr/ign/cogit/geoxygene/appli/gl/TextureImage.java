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

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.primitive.ParameterizedPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * @author JeT a texture image is used to project a polygon onto with
 *         distance and linear parameterization informations.
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
    private ParameterizedPolygon polygon = null;
    private Viewport viewport = null;
    private Double minx = null, maxx = null, miny = null, maxy = null; // projection boundaries
    private Double imageToPolygonFactorX = null;
    private Double imageToPolygonFactorY = null;
    private int imagesize = 1000000; // 1000*1000 (if ratio aspect = 1) 
    private static final Logger logger = Logger.getLogger(TextureImage.class.getName()); // logger
    private static final double SQRT2 = Math.sqrt(2.);

    private static final double LINEAR_PARAMETER_SCALE = 50.; // 1: world coordinates N: texture coordinates divided by N 

    /**
     * constructor
     * 
     * @param width
     *            projected image width
     * @param height
     *            projected image height
     * @param polygon
     *            polygon to project
     * @param viewport
     *            pixel-world coordinates converter
     */
    public TextureImage(final ParameterizedPolygon polygon, final Viewport viewport) {
        this.viewport = viewport;
        this.setPolygon(polygon);
    }

    /**
     * @return the minx
     */
    public double getMinX() {
        if (this.minx == null) {
            this.computeBoundaries();
        }
        return this.minx;
    }

    /**
     * @return the maxx
     */
    public double getMaxX() {
        if (this.maxx == null) {
            this.computeBoundaries();
        }
        return this.maxx;
    }

    /**
     * @return the miny
     */
    public double getMinY() {
        if (this.miny == null) {
            this.computeBoundaries();
        }
        return this.miny;
    }

    /**
     * @return the maxy
     */
    public double getMaxY() {
        if (this.maxy == null) {
            this.computeBoundaries();
        }
        return this.maxy;
    }

    /**
     * @return the imageToPolygonFactorX
     */
    public Double getImageToPolygonFactorX() {
        if (this.imageToPolygonFactorX == null) {
            this.imageToPolygonFactorX = (this.getMaxX() - this.getMinX()) / (this.getWidth() - 1);
        }
        return this.imageToPolygonFactorX;
    }

    /**
     * @return the imageToPolygonFactorY
     */
    public Double getImageToPolygonFactorY() {
        if (this.imageToPolygonFactorY == null) {
            this.imageToPolygonFactorY = (this.getMaxY() - this.getMinY()) / (this.getHeight() - 1);
        }
        return this.imageToPolygonFactorY;
    }

    /**
     * @param imagesize
     *            approximative number of image pixels.
     *            Width & Height are computed to fit at best the requested
     *            number of pixels
     */
    public final void setNbPixels(int imagesize) {
        this.imagesize = imagesize;
        if ((this.getMaxY() - this.getMinY()) < 1E-3 || (this.getMaxX() - this.getMinX()) < 1E-3) {
            return;
        }
        double ratio = (this.getMaxY() - this.getMinY()) / (this.getMaxX() - this.getMinX());
        this.setDimension((int) (Math.sqrt(this.imagesize) / ratio), (int) (Math.sqrt(this.imagesize) * ratio));
    }

    /**
     * @return the width
     */
    public final int getWidth() {
        return this.width;
    }

    /**
     * @return the height
     */
    public final int getHeight() {
        return this.height;
    }

    /**
     * Set image dimension and initialize pixels
     * 
     * @param width
     *            image width
     * @param height
     *            image height
     */
    public void setDimension(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.size = width * height;
        if (this.size <= 1) {
            throw new IllegalStateException("image too small = " + width + "x" + height + " = " + this.size);
        }
    }

    /**
     * get the specified pixel
     */
    public TexturePixel getPixel(int x, int y) {
        if (x < 0 || x >= this.getWidth() || y < 0 || y >= this.getHeight()) {
            return null;
        }
        if (this.pixels == null) {
            this.projectPolygon();
        }
        try {
            return this.pixels[x + y * this.getWidth()];
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("try to get pixel " + x + "x" + y + " = " + (x + y * this.getHeight()) + " <? " + this.pixels.length + " / " + this.pixels.length
                    + " = " + ((x + y * this.getHeight()) < this.size) + " " + this.getWidth() + "x" + this.getHeight());
            return null;
        }
    }

    /**
     * @param polygon
     *            the polygon to set
     */
    public final void setPolygon(ParameterizedPolygon polygon) {
        this.polygon = polygon;
        this.invalidatePolygon();
        //this.projectPolygon();
    }

    /**
     * @return the polygon
     */
    public ParameterizedPolygon getPolygon() {
        return this.polygon;
    }

    /**
     * initialize lazy data
     */
    private void invalidatePolygon() {
        this.minx = null;
        this.maxx = null;
        this.miny = null;
        this.maxy = null;
        this.imageToPolygonFactorX = null;
        this.imageToPolygonFactorY = null;
        this.pixels = null;
    }

    /**
     * get all pixels
     */
    public TexturePixel[] getPixels() {
        if (this.pixels == null) {
            this.projectPolygon();
        }
        return this.pixels;
    }

    /**
     * Do the polygon projection on the image and compute data
     */
    private void projectPolygon() {
        if (this.getPolygon() == null) {
            logger.warn("Parameterizer called on a null polygon");
            return;
        }
        if (this.pixels == null) {
            int size = this.getWidth() * this.getHeight();
            this.pixels = new TexturePixel[size];
            for (int l = 0; l < size; l++) {
                this.pixels[l] = new TexturePixel();
            }
        }
        this.rasterize();

        //        // TODO: TBD Debug purpose
        //        if (this.getPolygon().getGM_Polygon().area() > 4645 && this.getPolygon().getGM_Polygon().area() < 4646) {
        //            logger.debug("Information on polygon " + this.getPolygon().getGM_Polygon().area());
        //            IDirectPositionList coord = this.getPolygon().getGM_Polygon().getExterior().coord();
        //            logger.debug("\tnumber of points: " + coord.size());
        //            for (IDirectPosition pos : coord) {
        //                DensityFieldPixel pixel = this.getPixel((int) this.worldToProj(pos).getX(), (int) this.worldToProj(pos).getY());
        //                logger.debug("\t\t" + pos.getX() + "x" + pos.getY() + " => " + this.worldToProj(pos).getX() + "x" + this.worldToProj(pos).getY()
        //                        + "      value = " + pixel);
        //            }
        //            logger.debug("\tboundaries: " + this.getMinX() + "x" + this.getMinY() + " - " + this.getMaxX() + "x" + this.getMaxY());
        //            logger.debug("\timage size: " + this.getWidth() + "x" + this.getHeight());
        //        }
    }

    /**
     * Fills image pixels
     * draw all boundaries and stores all points into a table ordered by Y
     * values
     * then fill the polygon content using Y values
     * then fill recursively distance pixels for inner pixels
     */
    void rasterize() {

        DensityFieldFrontierPixelRenderer pixelRenderer = new DensityFieldFrontierPixelRenderer();

        // draw the outer frontier
        this.drawFrontier(this.getPolygon().getGM_Polygon().getExterior(), 1, pixelRenderer, this.viewport);

        // draw all inner frontiers
        for (int innerFrontierIndex = 0; innerFrontierIndex < this.polygon.getInnerFrontierCount(); innerFrontierIndex++) {
            IRing innerFrontier = this.getPolygon().getGM_Polygon().getInterior().get(innerFrontierIndex);
            this.drawFrontier(innerFrontier, -innerFrontierIndex - 1, pixelRenderer, this.viewport);
        }

        // fills the inner pixels
        fillHorizontally(this, pixelRenderer.getYs());

        // fill the pixels distance recursively
        //        fillFrontierDistance(this, pixelRenderer.getModifiedPixels());
        //fillOuterFrontierDistance(this, pixelRenderer.getModifiedPixels());
        //blurDistance(this);
        //fillUVTextureFromOuterFrontier( this, pixelRenderer.getModifiedPixels());

        fillTextureCoordinates(this, pixelRenderer.getModifiedPixels());

        //        TextureImageUtil.checkTextureCoordinates(this);

        //        TextureImageUtil.blurTextureCoordinates(this);
    }

    /**
     * @param ys
     *            list of y values containing a list of x-values
     * @param image
     */
    private static void fillHorizontally(TextureImage image, Map<Integer, List<Integer>> ys) {
        for (int y = 0; y < image.height; y++) {
            List<Integer> xs = ys.get(y);
            if (xs == null || xs.size() == 0) {
                continue;
            }
            Collections.sort(xs); // order by x values
            if (xs.size() % 2 != 0) {
                logger.warn("x values count cannot be even ! y = " + y + " : " + xs.size() + " : " + xs);
            }
            // draw horizontal lines between xs pixel pairs/couples
            for (int n = 0; n < xs.size() / 2; n++) {
                int x1 = xs.get(2 * n);
                int x2 = xs.get(2 * n + 1);
                for (int x = x1; x <= x2; x++) {
                    TexturePixel pixel = image.getPixel(x, y);
                    if (pixel != null && pixel.frontier == 0) {
                        pixel.in = true;
                        pixel.distance = Double.MAX_VALUE;
                    }

                }
            }

        }
    }

    /**
     * Modify the neighbors pixel distance according to the current pixel
     * distance
     * 
     * @param set
     */
    private static void fillFrontierDistance(TextureImage image, Set<Point> set) {
        double maxDistance = 0;
        while (set.size() > 0) {
            //            System.err.println(modifiedPixels.size() + " modified pixels");
            HashSet<Point> newlyModifiedPixels = new HashSet<Point>();
            for (Point p : set) {
                TexturePixel pixel = image.getPixel(p.x, p.y);
                if (pixel == null) {
                    throw new IllegalStateException("modified pixels cannot be outside image ... " + p.x + "x" + p.y);
                }
                double distance = pixel.distance;
                boolean w = fillFrontierDistance(distance + 1, new Point(p.x - 1, p.y), image, newlyModifiedPixels);
                boolean e = fillFrontierDistance(distance + 1, new Point(p.x + 1, p.y), image, newlyModifiedPixels);
                boolean n = fillFrontierDistance(distance + 1, new Point(p.x, p.y - 1), image, newlyModifiedPixels);
                boolean s = fillFrontierDistance(distance + 1, new Point(p.x, p.y + 1), image, newlyModifiedPixels);
                boolean nw = fillFrontierDistance(distance + SQRT2, new Point(p.x - 1, p.y - 1), image, newlyModifiedPixels);
                boolean ne = fillFrontierDistance(distance + SQRT2, new Point(p.x + 1, p.y - 1), image, newlyModifiedPixels);
                boolean sw = fillFrontierDistance(distance + SQRT2, new Point(p.x - 1, p.y + 1), image, newlyModifiedPixels);
                boolean se = fillFrontierDistance(distance + SQRT2, new Point(p.x + 1, p.y + 1), image, newlyModifiedPixels);
                if ((n || e || s || w) && (distance + 1 > maxDistance)) {
                    maxDistance = distance + 1;
                } else if ((nw || ne || sw || se) && (distance + SQRT2 > maxDistance)) {
                    maxDistance = distance + SQRT2;
                }
            }
            set = newlyModifiedPixels;
        }
        // fill yTexture coordinates as distance / maxDistance for any pixel
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                pixel.vTexture = pixel.distance / maxDistance;
            }
        }
    }

    /**
     * Compute the distance from any point to the outer frontier (skipping inner
     * frontiers)
     */
    private static void fillOuterFrontierDistance(TextureImage image, Set<Point> set) {
        double maxDistance = 0;
        while (set.size() > 0) {
            //            System.err.println(modifiedPixels.size() + " modified pixels");
            HashSet<Point> newlyModifiedPixels = new HashSet<Point>();
            for (Point p : set) {
                TexturePixel pixel = image.getPixel(p.x, p.y);
                if (pixel == null) {
                    throw new IllegalStateException("modified pixels cannot be outside image ... " + p.x + "x" + p.y);
                }
                if (pixel.frontier < 0) {
                    continue; // skip inner frontier ( pixel.frontier < 0 )
                }
                double distance = pixel.distance;
                boolean w = fillFrontierDistance(distance + 1, new Point(p.x - 1, p.y), image, newlyModifiedPixels);
                boolean e = fillFrontierDistance(distance + 1, new Point(p.x + 1, p.y), image, newlyModifiedPixels);
                boolean n = fillFrontierDistance(distance + 1, new Point(p.x, p.y - 1), image, newlyModifiedPixels);
                boolean s = fillFrontierDistance(distance + 1, new Point(p.x, p.y + 1), image, newlyModifiedPixels);
                boolean nw = fillFrontierDistance(distance + SQRT2, new Point(p.x - 1, p.y - 1), image, newlyModifiedPixels);
                boolean ne = fillFrontierDistance(distance + SQRT2, new Point(p.x + 1, p.y - 1), image, newlyModifiedPixels);
                boolean sw = fillFrontierDistance(distance + SQRT2, new Point(p.x - 1, p.y + 1), image, newlyModifiedPixels);
                boolean se = fillFrontierDistance(distance + SQRT2, new Point(p.x + 1, p.y + 1), image, newlyModifiedPixels);
                if ((n || e || s || w) && (distance + 1 > maxDistance)) {
                    maxDistance = distance + 1;
                } else if ((nw || ne || sw || se) && (distance + SQRT2 > maxDistance)) {
                    maxDistance = distance + SQRT2;
                }
            }
            set = newlyModifiedPixels;
        }
        // fill yTexture coordinates as distance / maxDistance for any pixel
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                pixel.vTexture = pixel.distance / maxDistance;
            }
        }
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
    private static boolean fillFrontierDistance(double d, Point p, TextureImage image, Set<Point> newlyModifiedPixels) {
        TexturePixel pixel = image.getPixel(p.x, p.y);
        if (pixel == null) {
            return false;
        }
        if (pixel.in && pixel.distance > d) {
            pixel.distance = d;
            newlyModifiedPixels.add(p);
            return true;
        }
        return false;
    }

    /**
     * Modify the neighbors pixel distance according to the current pixel
     * distance
     * 
     * @param set
     */
    private static void fillTextureCoordinates(TextureImage image, Set<Point> set) {
        double maxDistance = 0;
        while (set.size() > 0) {
            //            System.err.println(modifiedPixels.size() + " modified pixels");
            HashSet<Point> newlyModifiedPixels = new HashSet<Point>();
            for (Point p : set) {
                TexturePixel pixel = image.getPixel(p.x, p.y);
                if (pixel == null) {
                    throw new IllegalStateException("modified pixels cannot be outside image ... " + p.x + "x" + p.y);
                }
                double distance = pixel.distance;
                boolean w = fillTextureCoordinates(distance + 1, pixel.uTexture, new Point(p.x - 1, p.y), image, newlyModifiedPixels);
                boolean e = fillTextureCoordinates(distance + 1, pixel.uTexture, new Point(p.x + 1, p.y), image, newlyModifiedPixels);
                boolean n = fillTextureCoordinates(distance + 1, pixel.uTexture, new Point(p.x, p.y - 1), image, newlyModifiedPixels);
                boolean s = fillTextureCoordinates(distance + 1, pixel.uTexture, new Point(p.x, p.y + 1), image, newlyModifiedPixels);
                boolean nw = fillTextureCoordinates(distance + SQRT2, pixel.uTexture, new Point(p.x - 1, p.y - 1), image, newlyModifiedPixels);
                boolean ne = fillTextureCoordinates(distance + SQRT2, pixel.uTexture, new Point(p.x + 1, p.y - 1), image, newlyModifiedPixels);
                boolean sw = fillTextureCoordinates(distance + SQRT2, pixel.uTexture, new Point(p.x - 1, p.y + 1), image, newlyModifiedPixels);
                boolean se = fillTextureCoordinates(distance + SQRT2, pixel.uTexture, new Point(p.x + 1, p.y + 1), image, newlyModifiedPixels);
                if ((n || e || s || w) && (distance + 1 > maxDistance)) {
                    maxDistance = distance + 1;
                } else if ((nw || ne || sw || se) && (distance + SQRT2 > maxDistance)) {
                    maxDistance = distance + SQRT2;
                }

            }
            set = newlyModifiedPixels;
        }
        // fill yTexture coordinates as distance / maxDistance for any pixel
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                pixel.vTexture = pixel.distance / maxDistance;
            }
        }
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
    private static boolean fillTextureCoordinates(double d, double uTexture, Point p, TextureImage image, Set<Point> newlyModifiedPixels) {
        TexturePixel pixel = image.getPixel(p.x, p.y);
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
     * draw a polygon's frontier in the image using the selected renderer
     * 
     * @param frontier
     * @param pixelRenderer
     */
    private void drawFrontier(IRing frontier, int frontierId, DensityFieldFrontierPixelRenderer pixelRenderer, Viewport viewport) {
        pixelRenderer.setCurrentFrontier(frontierId);
        int frontierSize = frontier.coord().size();
        if (frontierSize < 3) {
            logger.error("Cannot fill a polygon with less than 3 points");
            return;
        }
        IDirectPosition p0 = frontier.coord().get(frontierSize - 1);// previous point
        IDirectPosition p1 = frontier.coord().get(0); // start point line to draw
        IDirectPosition p2 = frontier.coord().get(1); // end point line to draw
        //        double frontierLength = frontier.length();
        double segmentLength = Math.sqrt((p2.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY()) * (p2.getY() - p1.getY()))
                / LINEAR_PARAMETER_SCALE;
        // convert world-based coordinates to projection-space coordinates
        Point2D proj0 = this.worldToProj(p0);
        Point2D proj1 = this.worldToProj(p1);
        Point2D proj2 = this.worldToProj(p2);
        //                int x0 = (int) proj0.getX();
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
            y0 = (int) this.worldToProj(frontier.coord().get(index)).getY();
            lastDirection = y1 - y0;
            index--;
        }
        y0 = (int) proj0.getY();
        y1 = (int) proj1.getY();

        double linearDistance = 0; // linear parameterization along the frontier
        for (int nPoint = 0; nPoint < frontierSize; nPoint++) {
            //pixelRenderer.setHorizontalLine((int) proj1.getY() == (int) proj2.getY());
            // check if previous and next points are on the same Y side (cusp)
            // if the line is horizontal, keep previous cusp
            if (y1 != y2) {
                pixelRenderer.setCusp(lastDirection * (y2 - y1) < 0);
                lastDirection = y2 - y1;
            }

            pixelRenderer.setLinearParameterization(linearDistance, linearDistance + segmentLength);

            if (!(x1 == x2 && y1 == y2)) {
                drawLine(x1, y1, x2, y2, this, pixelRenderer);
            }
            linearDistance += segmentLength;
            p0 = p1;
            p1 = p2;
            p2 = frontier.coord().get((nPoint + 1) % frontierSize);
            segmentLength = Math.sqrt((p2.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY()) * (p2.getY() - p1.getY()))
                    / LINEAR_PARAMETER_SCALE;

            proj0 = proj1;
            proj1 = proj2;
            proj2 = this.worldToProj(p2);
            y0 = y1;
            x1 = x2;
            y1 = y2;
            x2 = (int) proj2.getX();
            y2 = (int) proj2.getY();

        }
    }

    /**
     * convert point coordinates from polygon space to image space
     * 
     * @param polygonCoordinates
     * @return
     */
    public Point2D worldToProj(Point2D polygonCoordinates) {
        return new Point2D.Double((polygonCoordinates.getX() - this.getMinX()) / this.getImageToPolygonFactorX(), (polygonCoordinates.getY() - this.getMinY())
                / this.getImageToPolygonFactorY());
    }

    /**
     * convert point coordinates from polygon space to image space
     * 
     * @param polygonCoordinates
     * @return
     */
    public Point2D worldToProj(Point2d polygonCoordinates) {
        return new Point2D.Double((polygonCoordinates.x - this.getMinX()) / this.getImageToPolygonFactorX(), (polygonCoordinates.y - this.getMinY())
                / this.getImageToPolygonFactorY());
    }

    /**
     * convert point coordinates from polygon space to image space
     * 
     * @param polygonCoordinates
     * @return
     */
    public Point2D worldToProj(IDirectPosition polygonCoordinates) {
        return new Point2D.Double((polygonCoordinates.getX() - this.getMinX()) / this.getImageToPolygonFactorX(), (polygonCoordinates.getY() - this.getMinY())
                / this.getImageToPolygonFactorY());
    }

    /**
     * compute minx, maxx, miny & maxy values
     */
    private void computeBoundaries() {
        this.minx = Double.MAX_VALUE;
        this.maxx = -Double.MAX_VALUE;
        this.miny = Double.MAX_VALUE;
        this.maxy = -Double.MAX_VALUE;
        if (this.getPolygon() == null) {
            logger.error("Cannot compute boundaries of a null polygon");
            Thread.dumpStack();
            return;
        }
        GM_Polygon gmPolygon = this.getPolygon().getGM_Polygon();

        for (IDirectPosition pos : gmPolygon.getExterior().coord()) {
            Point2D p = new Point2D.Double(pos.getX(), pos.getY());
            if (p.getX() < this.minx) {
                this.minx = p.getX();
            }
            if (p.getX() > this.maxx) {
                this.maxx = p.getX();
            }
            if (p.getY() > this.maxy) {
                this.maxy = p.getY();
            }
            if (p.getY() < this.miny) {
                this.miny = p.getY();
            }
        }
        // inflate 1% min & max
        this.minx -= (this.maxx - this.minx) / 100.;
        this.maxx += (this.maxx - this.minx) / 100.;
        this.miny -= (this.maxy - this.miny) / 100.;
        this.maxy += (this.maxy - this.miny) / 100.;
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
    private static void drawLine(final int xi, final int yi, final int xf, final int yf, TextureImage image, TexturePixelRenderer renderer) {
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
        renderer.renderFirstPixel(x, y, image);
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
                    renderer.renderPixel(x, y, image);
                } else {
                    renderer.renderLastPixel(x, y, image);
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
                    renderer.renderPixel(x, y, image);
                } else {
                    renderer.renderLastPixel(x, y, image);
                }
            }
        }
    }

}
