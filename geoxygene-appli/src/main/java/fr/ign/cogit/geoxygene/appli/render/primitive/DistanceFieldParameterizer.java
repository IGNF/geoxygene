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

package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.awt.Point;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.DistanceFieldFrontierPixelRenderer;
import fr.ign.cogit.geoxygene.util.gl.TextureImage;
import fr.ign.cogit.geoxygene.util.gl.TextureImageUtil;
import fr.ign.cogit.geoxygene.util.gl.TextureImage.TexturePixel;

/**
 * @author JeT
 *         Compute texture coordinates using the distance to the primitive edge
 */
public class DistanceFieldParameterizer implements Parameterizer {

    //    private static final int TEXTURE_WIDTH_HEIGHT = 1024;
    private static Logger logger = Logger.getLogger(DistanceFieldParameterizer.class.getName());
    private Viewport viewport = null;
    private IFeature feature = null;
    private TextureImage texImage = null;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private double imageToPolygonFactorX;
    private double imageToPolygonFactorY;

    /**
     * Constructor
     * 
     * @param shape
     *            shape representing a line
     * @param viewport
     *            viewport in which the shape has been generated
     */
    public DistanceFieldParameterizer(final Viewport viewport, final IFeature feature) {
        this.setViewport(viewport);
        this.setFeature(feature);
        this.computeBoundaries(feature.getGeom());
    }

    /**
     * Lazy getter of the texture image. It creates the texture if not already
     * generated. This process can be time consuming. Use
     * DensityFieldGenerationTask for async generation
     * 
     */
    public TextureImage getTextureImage() {
        if (this.texImage == null) {
            if (this.getFeature().getGeom().isPolygon()) {
                // generate the field image
                this.generateTextureImage((IPolygon) this.getFeature().getGeom(), this.viewport);
            } else if (this.getFeature().getGeom().isMultiSurface()) {
                // generate the field image
                this.generateTextureImage((IMultiSurface<?>) this.getFeature().getGeom(), this.viewport);
            } else {
                logger.warn("Distance Field Parameterizer does not handle geometry type " + this.getFeature().getGeom().getClass().getSimpleName());
            }
        }
        return this.texImage;
    }

    /**
     * @return the viewport
     */
    public final Viewport getViewport() {
        return this.viewport;
    }

    /**
     * @param viewport
     *            the viewport to set
     */
    public final void setViewport(final Viewport viewport) {
        this.viewport = viewport;
    }

    /**
     * @return the polygon
     */
    public final IFeature getFeature() {
        return this.feature;
    }

    /**
     * @param feature
     *            the feature to set
     */
    public final void setFeature(IFeature feature) {
        this.feature = feature;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer#initialize()
     */
    @Override
    public void initializeParameterization() {
        TextureImage texImage = this.getTextureImage();
        try {
            TextureImageUtil.save(texImage, "test");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    @Override
    public void finalizeParameterization() {

        //        DecimalFormat df = new DecimalFormat("#.0000");
        //        try {
        //            TextureImageUtil.save(this.getTextureImage(), "./z-" + df.format(this.polygon.getGM_Polygon().area()) + "-polygon");
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer#
     * getTextureCoordinates(double, double)
     */
    /**
     * convert screen coordinates to model (world) coordinates then to texture
     * image coordinates. Image coordinates are directly the texture coordinates
     * (normalized by image size) because source texture has already been
     * applies using applyTexture() method
     */
    @Override
    public Point2d getTextureCoordinates(final double[] vertex) {

        double xTexture = vertex[0] / (this.maxX - this.minX);
        double yTexture = vertex[1] / (this.maxY - this.minY);
        //        System.err.println("DistanceFieldParameterizer.getTextureCoordinates(" + vertex[0] + ", " + vertex[1] + ") = " + xTexture + " x " + yTexture);
        //        System.err.println("\t = " + vertex[0] + "/" + (this.maxX - this.minX) + " , " + vertex[1] + " / " + (this.maxY - this.minY));
        return new Point2d(xTexture, yTexture);
    }

    //    /*
    //     * (non-Javadoc)
    //     * 
    //     * @see fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer#
    //     * getLinearParameter(float, float)
    //     */
    //    @Override
    //    public double getLinearParameter(final double[] vertex) {
    //        Point2D modelPoint;
    //        try {
    //            modelPoint = this.viewport.toModelPoint(new Point2D.Double(vertex[0], vertex[1]));
    //            Point2D imageCoordinates = this.worldToProj(modelPoint);
    //            TexturePixel pixel = this.getTextureImage().getPixel((int) imageCoordinates.getX(), (int) imageCoordinates.getY());
    //            return pixel.uTexture;
    //        } catch (NoninvertibleTransformException e) {
    //            e.printStackTrace();
    //            return 0.;
    //        }
    //    }

    /**
     * generate the texture image for a geometry of type multisurface
     * 
     * @param multiSurface
     * @param viewport
     */
    private void generateTextureImage(IMultiSurface<?> multiSurface, Viewport viewport) {

        if (multiSurface == null) {
            logger.error("Cannot compute boundaries of a null polygon");
            this.texImage = null;
            return;
        }

        final double imagesize = 1E6; // 1000*1000 (if ratio aspect = 1) 
        double ratio = (this.maxY - this.minY) / (this.maxX - this.minX);
        int imageWidth = (int) (Math.sqrt(imagesize) / ratio);
        int imageHeight = (int) (Math.sqrt(imagesize) * ratio);
        this.texImage = new TextureImage(imageWidth, imageHeight);
        this.imageToPolygonFactorX = (this.maxX - this.minX) / (imageWidth - 1);
        this.imageToPolygonFactorY = (this.maxY - this.minY) / (imageHeight - 1);

        this.projectMultiSurface(multiSurface, viewport);
    }

    /**
     * generate the texture image for a geometry of type polygon
     * 
     * @param polygon
     * @param viewport
     */
    private void generateTextureImage(IPolygon polygon, Viewport viewport) {

        if (polygon == null) {
            logger.error("Cannot compute boundaries of a null polygon");
            this.texImage = null;
            return;
        }

        final double imagesize = 1E6; // 1000*1000 (if ratio aspect = 1) 
        double ratio = (this.maxY - this.minY) / (this.maxX - this.minX);
        int imageWidth = (int) (Math.sqrt(imagesize) / ratio);
        int imageHeight = (int) (Math.sqrt(imagesize) * ratio);
        this.texImage.setDimension(imageWidth, imageHeight);
        this.imageToPolygonFactorX = (this.maxX - this.minX) / (imageWidth - 1);
        this.imageToPolygonFactorY = (this.maxY - this.minY) / (imageHeight - 1);

        this.projectPolygon(polygon, viewport);
    }

    /**
     * @param polygon
     */
    private void computeBoundaries(IGeometry geometry) {
        this.minX = geometry.getEnvelope().getLowerCorner().getX();
        this.minY = geometry.getEnvelope().getLowerCorner().getY();
        this.maxX = geometry.getEnvelope().getUpperCorner().getX();
        this.maxY = geometry.getEnvelope().getUpperCorner().getY();
        if (this.minX > this.maxX) {
            double tmp = this.minX;
            this.minX = this.maxX;
            this.maxX = tmp;
        }
        if (this.minY > this.maxY) {
            double tmp = this.minY;
            this.minY = this.maxY;
            this.maxY = tmp;
        }
        //        // inflate 1% min & max
        //        this.minX -= (this.maxX - this.minX) / 100.;
        //        this.maxX += (this.maxX - this.minX) / 100.;
        //        this.minY -= (this.maxY - this.minY) / 100.;
        //        this.maxY += (this.maxY - this.minY) / 100.;
    }

    /**
     * Fills image pixels
     * draw all boundaries and stores all points into a table ordered by Y
     * values
     * then fill the polygon content using Y values
     * then fill recursively distance pixels for inner pixels
     */
    private void projectPolygon(IPolygon polygon, final Viewport viewport) {
        List<IPolygon> polygons = new ArrayList<IPolygon>();
        polygons.add(polygon);
        this.projectPolygons(polygons, viewport);
    }

    /**
     * Fills image pixels
     * draw all boundaries and stores all points into a table ordered by Y
     * values
     * then fill the polygon content using Y values
     * then fill recursively distance pixels for inner pixels
     */
    private void projectMultiSurface(IMultiSurface<?> multiSurface, final Viewport viewport) {
        List<IPolygon> polygons = new ArrayList<IPolygon>();
        // convert the multisurface as a collection of polygons
        for (IOrientableSurface surface : multiSurface.getList()) {
            if (surface instanceof IPolygon) {
                IPolygon polygon = (IPolygon) surface;
                polygons.add(polygon);
            } else {
                logger.error("Distance Field Parameterizer does handle multi surfaces containing only polygons, not " + surface.getClass().getSimpleName());
            }
        }
        this.projectPolygons(polygons, viewport);
    }

    /**
     * Fills image pixels
     * draw all boundaries and stores all points into a table ordered by Y
     * values
     * then fill the polygon content using Y values
     * then fill recursively distance pixels for inner pixels
     */
    private void projectPolygons(Collection<IPolygon> polygons, final Viewport viewport) {
        DistanceFieldFrontierPixelRenderer pixelRenderer = new DistanceFieldFrontierPixelRenderer();

        for (IPolygon polygon : polygons) {
            // draw the outer frontier
            this.drawFrontier(polygon.getExterior(), 1, pixelRenderer, viewport);

            // draw all inner frontiers
            for (int innerFrontierIndex = 0; innerFrontierIndex < polygon.getInterior().size(); innerFrontierIndex++) {
                IRing innerFrontier = polygon.getInterior().get(innerFrontierIndex);
                this.drawFrontier(innerFrontier, -innerFrontierIndex - 1, pixelRenderer, viewport);
            }
        }

        // fills the inner pixels
        this.fillHorizontally(pixelRenderer.getYs());

        // fill the pixels distance recursively
        //        fillFrontierDistance(this, pixelRenderer.getModifiedPixels());
        //fillOuterFrontierDistance(this, pixelRenderer.getModifiedPixels());
        //blurDistance(this);
        //fillUVTextureFromOuterFrontier( this, pixelRenderer.getModifiedPixels());

        System.err.println("u texture coordinate range = " + pixelRenderer.getuMin() + " - " + pixelRenderer.getuMax());
        System.err.println("image to polygon factor = " + this.imageToPolygonFactorX + " - " + this.imageToPolygonFactorY);
        System.err.println("viewport scale = " + viewport.getScale());
        TextureImageUtil.rescaleTextureCoordinates(this.texImage, viewport.getScale() * 0.001);
        Set<Point> nonInfiniteModifiedPixels = this.getModifiedPixelsButInfiniteDistancePixels(pixelRenderer.getModifiedPixels());
        //        Set<Point> nonInfiniteModifiedPixels = pixelRenderer.getModifiedPixels();
        fillTextureCoordinates(this.texImage, nonInfiniteModifiedPixels, this.imageToPolygonFactorX, this.imageToPolygonFactorY);

        //        TextureImageUtil.checkTextureCoordinates(this);

        // FIXME: it seems that there is a bug in the blur algo
        TextureImageUtil.blurTextureCoordinates(this.texImage, 10);
    }

    /**
     * Remove all pixels which have an infinite distance from the modified
     * pixels
     * 
     * @param modifiedPixels
     * @return
     */
    private Set<Point> getModifiedPixelsButInfiniteDistancePixels(Set<Point> modifiedPixels) {
        Set<Point> nonInfiniteModifiedPixels = new HashSet<Point>();
        for (Point p : modifiedPixels) {
            TexturePixel pixel = this.texImage.getPixel(p.x, p.y);
            if (pixel.distance != Double.POSITIVE_INFINITY) {
                nonInfiniteModifiedPixels.add(p);
            }
        }
        return nonInfiniteModifiedPixels;
    }

    /**
     * @param ys
     *            list of y values containing a list of x-values
     * @param image
     */
    private void fillHorizontally(Map<Integer, List<Integer>> ys) {
        for (int y = 0; y < this.texImage.getHeight(); y++) {
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
                    TexturePixel pixel = this.texImage.getPixel(x, y);
                    if (pixel != null && pixel.frontier == 0) {
                        pixel.in = true;
                        pixel.distance = Double.MAX_VALUE;
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
    private void drawFrontier(IRing frontier, int frontierId, DistanceFieldFrontierPixelRenderer pixelRenderer, Viewport viewport) {
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
        double segmentLength = Math.sqrt((p2.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY()) * (p2.getY() - p1.getY()));
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
            // check if previous and next points are on the same Y side (cusp)
            // if the line is horizontal, keep previous cusp
            if (y1 != y2) {
                pixelRenderer.setCusp(lastDirection * (y2 - y1) < 0);
                lastDirection = y2 - y1;
            }

            // here we can choose the parameterization along frontiers
            pixelRenderer.setLinearParameterization(linearDistance, linearDistance + segmentLength);
            // FIXME: very special case for 'mer JDD plancoet'. Long outer frontier
            // don't have to be of distance 0
            pixelRenderer.setDistanceToZero(segmentLength < 1000);
            if (!(x1 == x2 && y1 == y2)) {
                this.texImage.drawLine(x1, y1, x2, y2, pixelRenderer);
            }

            linearDistance += segmentLength;
            p0 = p1;
            p1 = p2;
            p2 = frontier.coord().get((nPoint + 1) % frontierSize);
            segmentLength = Math.sqrt((p2.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY()) * (p2.getY() - p1.getY()));

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
     * Modify the neighbors pixel distance according to the current pixel
     * distance
     * 
     * @param set
     */
    private static void fillFrontierDistance(TextureImage image, Set<Point> set, final double pixelWidth, final double pixelHeight) {
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
                boolean w = fillFrontierDistance(distance + pixelWidth, new Point(p.x - 1, p.y), image, newlyModifiedPixels);
                boolean e = fillFrontierDistance(distance + pixelWidth, new Point(p.x + 1, p.y), image, newlyModifiedPixels);
                boolean n = fillFrontierDistance(distance + pixelHeight, new Point(p.x, p.y - 1), image, newlyModifiedPixels);
                boolean s = fillFrontierDistance(distance + pixelHeight, new Point(p.x, p.y + 1), image, newlyModifiedPixels);
                if ((n || s) && (distance + pixelHeight > maxDistance)) {
                    maxDistance = distance + pixelHeight;
                }
                if ((e || w) && (distance + pixelWidth > maxDistance)) {
                    maxDistance = distance + pixelWidth;
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
    private static void fillOuterFrontierDistance(TextureImage image, Set<Point> set, final double pixelWidth, final double pixelHeight) {
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
                boolean w = fillFrontierDistance(distance + pixelWidth, new Point(p.x - 1, p.y), image, newlyModifiedPixels);
                boolean e = fillFrontierDistance(distance + pixelWidth, new Point(p.x + 1, p.y), image, newlyModifiedPixels);
                boolean n = fillFrontierDistance(distance + pixelHeight, new Point(p.x, p.y - 1), image, newlyModifiedPixels);
                boolean s = fillFrontierDistance(distance + pixelHeight, new Point(p.x, p.y + 1), image, newlyModifiedPixels);
                if ((n || s) && (distance + pixelHeight > maxDistance)) {
                    maxDistance = distance + pixelHeight;
                }
                if ((e || w) && (distance + pixelWidth > maxDistance)) {
                    maxDistance = distance + pixelWidth;
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
    private static void fillTextureCoordinates(TextureImage image, Set<Point> set, final double pixelWidth, final double pixelHeight) {
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
                boolean w = fillTextureCoordinates(image, distance + pixelWidth, pixel.uTexture, new Point(p.x - 1, p.y), newlyModifiedPixels);
                boolean e = fillTextureCoordinates(image, distance + pixelWidth, pixel.uTexture, new Point(p.x + 1, p.y), newlyModifiedPixels);
                boolean n = fillTextureCoordinates(image, distance + pixelHeight, pixel.uTexture, new Point(p.x, p.y - 1), newlyModifiedPixels);
                boolean s = fillTextureCoordinates(image, distance + pixelHeight, pixel.uTexture, new Point(p.x, p.y + 1), newlyModifiedPixels);
                if ((n || s) && (distance + pixelHeight > maxDistance)) {
                    maxDistance = distance + pixelHeight;
                }
                if ((e || w) && (distance + pixelWidth > maxDistance)) {
                    maxDistance = distance + pixelWidth;
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
    private static boolean fillTextureCoordinates(TextureImage texImage, double d, double uTexture, Point p, Set<Point> newlyModifiedPixels) {
        TexturePixel pixel = texImage.getPixel(p.x, p.y);
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
     * convert point coordinates from polygon space to image space
     * 
     * @param polygonCoordinates
     * @return
     */
    public Point2D worldToProj(Point2D polygonCoordinates) {
        return new Point2D.Double((polygonCoordinates.getX() - this.minX) / this.imageToPolygonFactorX, (polygonCoordinates.getY() - this.minY)
                / this.imageToPolygonFactorY);
    }

    /**
     * convert point coordinates from polygon space to image space
     * 
     * @param polygonCoordinates
     * @return
     */
    public Point2D worldToProj(Point2d polygonCoordinates) {
        return new Point2D.Double((polygonCoordinates.x - this.minX) / this.imageToPolygonFactorX, (polygonCoordinates.y - this.minY)
                / this.imageToPolygonFactorY);
    }

    /**
     * convert point coordinates from polygon space to image space
     * 
     * @param polygonCoordinates
     * @return
     */
    public Point2D worldToProj(IDirectPosition polygonCoordinates) {
        return new Point2D.Double((polygonCoordinates.getX() - this.minX) / this.imageToPolygonFactorX, (polygonCoordinates.getY() - this.minY)
                / this.imageToPolygonFactorY);
    }

}
