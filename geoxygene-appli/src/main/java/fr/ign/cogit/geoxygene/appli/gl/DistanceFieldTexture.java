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

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL30.GL_RG;
import static org.lwjgl.opengl.GL30.GL_RG32F;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL13;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;
import fr.ign.cogit.geoxygene.util.gl.GradientTextureImage;
import fr.ign.cogit.geoxygene.util.gl.GradientTextureImage.TexturePixel;
import fr.ign.cogit.geoxygene.util.gl.Texture;
import fr.ign.cogit.geoxygene.util.gl.TextureImageUtil;

/**
 * @author JeT
 * 
 */
public class DistanceFieldTexture implements Parameterizer, Texture {

    private static final Logger logger = Logger
            .getLogger(DistanceFieldTexture.class.getName()); // logger

    private final boolean firstCall = true;
    private int distanceFieldTextureId = -1;
    private Viewport viewport = null;
    private IFeature feature = null;
    private GradientTextureImage texImage = null;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private double imageToPolygonFactorX;
    private double imageToPolygonFactorY;
    private BasicTexture textureToApply = null;

    private double uScale = 1.;
    private double vScale = 1.;

    /**
     * Constructor
     */
    public DistanceFieldTexture() {
    }

    /**
     * Constructor
     * 
     * @param shape
     *            shape representing a line
     * @param viewport
     *            viewport in which the shape has been generated
     */
    public DistanceFieldTexture(final Viewport viewport, final IFeature feature) {
        this.setViewport(viewport);
        this.setFeature(feature);
        this.computeBoundaries(feature.getGeom());
    }

    /**
     * @return the textureToApply
     */
    public final BasicTexture getTextureToApply() {
        return this.textureToApply;
    }

    /**
     * @param textureToApply
     *            the textureToApply to set
     */
    public final void setTextureToApply(BasicTexture textureToApply) {
        this.textureToApply = textureToApply;
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
        // // inflate 1% min & max
        // this.minX -= (this.maxX - this.minX) / 100.;
        // this.maxX += (this.maxX - this.minX) / 100.;
        // this.minY -= (this.maxY - this.minY) / 100.;
        // this.maxY += (this.maxY - this.minY) / 100.;
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

    }

    /**
     * @return the uScale
     */
    @Override
    public double getScaleX() {
        return this.uScale;
    }

    /**
     * @param uScale
     *            the uScale to set
     */
    public void setuScale(double uScale) {
        this.uScale = uScale;
    }

    /**
     * @return the vScale
     */
    @Override
    public double getScaleY() {
        return this.vScale;
    }

    /**
     * @param vScale
     *            the vScale to set
     */
    public void setvScale(double vScale) {
        this.vScale = vScale;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    @Override
    public void finalizeParameterization() {

        // DecimalFormat df = new DecimalFormat("#.0000");
        // try {
        // TextureImageUtil.save(this.getTextureImage(), "./z-" +
        // df.format(this.polygon.getGM_Polygon().area()) + "-polygon");
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.render.primitive.Parameterizer#
     * getTextureCoordinates(double, double)
     */
    @Override
    public Point2d getTextureCoordinates(final double... vertex) {
        // do not remove MinX & minY. incoming vertex is in object coordinates
        // (is already translated to minX/minY)
        double xTexture = (vertex[0]) / (this.maxX - this.minX);
        double yTexture = (vertex[1]) / (this.maxY - this.minY);
        // System.err.println("DistanceFieldParameterizer.getTextureCoordinates("
        // + vertex[0] + ", " + vertex[1] + ") = " + xTexture + " x " +
        // yTexture);
        // System.err.println("\t = " + vertex[0] + "/" + (this.maxX -
        // this.minX) + " , " + vertex[1] + " / " + (this.maxY - this.minY));
        return new Point2d(xTexture, yTexture);
    }

    /**
     * Lazy getter of the texture image. It creates the texture if not already
     * generated. This process can be time consuming. Use
     * DensityFieldGenerationTask for async generation
     * 
     */
    public GradientTextureImage getUVTextureImage() {
        if (this.texImage == null) {
            if (this.getFeature().getGeom().isPolygon()) {
                // generate the field image
                this.generateDistanceField((IPolygon) this.getFeature()
                        .getGeom(), this.viewport);
            } else if (this.getFeature().getGeom().isMultiSurface()) {
                // generate the field image
                this.generateTextureImage((IMultiSurface<?>) this.getFeature()
                        .getGeom(), this.viewport);
            } else {
                logger.warn("Distance Field Parameterizer does not handle geometry type "
                        + this.getFeature().getGeom().getClass()
                                .getSimpleName());
            }
        }
        return this.texImage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.util.gl.BasicTexture#initializeRendering()
     */
    @Override
    public boolean initializeRendering() {
        if (this.firstCall) {
            GradientTextureImage uvMap = this.getUVTextureImage();
            // save image for debug purpose only
            // String generatedTextureFilename = this.generateTextureFilename();
            // TextureImageUtil.saveHeight(uvMap, generatedTextureFilename);

            glEnable(GL_TEXTURE_2D);
            GL13.glActiveTexture(GL13.GL_TEXTURE4);
            this.distanceFieldTextureId = glGenTextures(); // Generate texture
                                                           // ID
            glBindTexture(GL_TEXTURE_2D, this.distanceFieldTextureId); // Bind
                                                                       // texture
                                                                       // ID

            // Setup wrap mode
            // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
            // GL_NEAREST);
            // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,
            // GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            // Setup texture scaling filtering
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            // Send texel data to OpenGL
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RG32F, uvMap.getWidth(),
                    uvMap.getHeight(), 0, GL_RG, GL_FLOAT,
                    TextureImageUtil.toFloatBuffer(uvMap));

        } else {
            GL13.glActiveTexture(GL13.GL_TEXTURE4);
            glBindTexture(GL_TEXTURE_2D, this.distanceFieldTextureId); // Bind
                                                                       // texture
                                                                       // ID
        }
        return this.textureToApply.initializeRendering();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.util.gl.BasicTexture#finalizeRendering()
     */
    @Override
    public void finalizeRendering() {
        this.textureToApply.finalizeRendering();
    }

    /**
     * generate the texture image for a geometry of type multisurface
     * 
     * @param multiSurface
     * @param viewport
     */
    private void generateTextureImage(IMultiSurface<?> multiSurface,
            Viewport viewport) {

        if (multiSurface == null) {
            logger.error("Cannot compute boundaries of a null polygon");
            this.texImage = null;
            return;
        }

        final double imagesize = 1E6; // 1000*1000 (if ratio aspect = 1)
        double ratio = (this.maxY - this.minY) / (this.maxX - this.minX);
        int imageWidth = (int) (Math.sqrt(imagesize / ratio));
        int imageHeight = (int) (Math.sqrt(imagesize * ratio));
        this.texImage = new GradientTextureImage(imageWidth, imageHeight);
        this.imageToPolygonFactorX = (this.maxX - this.minX) / (imageWidth - 1);
        this.imageToPolygonFactorY = (this.maxY - this.minY)
                / (imageHeight - 1);

        this.projectMultiSurface(multiSurface, viewport);
    }

    /**
     * generate the texture image for a geometry of type polygon
     * 
     * @param polygon
     * @param viewport
     */
    private void generateDistanceField(IPolygon polygon, Viewport viewport) {

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
        this.imageToPolygonFactorY = (this.maxY - this.minY)
                / (imageHeight - 1);

        this.projectPolygon(polygon, viewport);
    }

    /**
     * Fills image pixels draw all boundaries and stores all points into a table
     * ordered by Y values then fill the polygon content using Y values then
     * fill recursively distance pixels for inner pixels
     */
    private void projectPolygon(IPolygon polygon, final Viewport viewport) {
        List<IPolygon> polygons = new ArrayList<IPolygon>();
        polygons.add(polygon);
        this.projectPolygons(polygons, viewport);
    }

    /**
     * Fills image pixels draw all boundaries and stores all points into a table
     * ordered by Y values then fill the polygon content using Y values then
     * fill recursively distance pixels for inner pixels
     */
    private void projectMultiSurface(IMultiSurface<?> multiSurface,
            final Viewport viewport) {
        List<IPolygon> polygons = new ArrayList<IPolygon>();
        // convert the multisurface as a collection of polygons
        for (IOrientableSurface surface : multiSurface.getList()) {
            if (surface instanceof IPolygon) {
                IPolygon polygon = (IPolygon) surface;
                polygons.add(polygon);
            } else {
                logger.error("Distance Field Parameterizer does handle multi surfaces containing only polygons, not "
                        + surface.getClass().getSimpleName());
            }
        }
        this.projectPolygons(polygons, viewport);
    }

    /**
     * Fills image pixels draw all boundaries and stores all points into a table
     * ordered by Y values then fill the polygon content using Y values then
     * fill recursively distance pixels for inner pixels
     */
    private void projectPolygons(Collection<IPolygon> polygons,
            final Viewport viewport) {
        DistanceFieldFrontierPixelRenderer pixelRenderer = new DistanceFieldFrontierPixelRenderer();

        for (IPolygon polygon : polygons) {
            // draw the outer frontier
            this.drawFrontier(polygon.getExterior(), 1, pixelRenderer);

            // draw all inner frontiers
            for (int innerFrontierIndex = 0; innerFrontierIndex < polygon
                    .getInterior().size(); innerFrontierIndex++) {
                IRing innerFrontier = polygon.getInterior().get(
                        innerFrontierIndex);
                this.drawFrontier(innerFrontier, -innerFrontierIndex - 1,
                        pixelRenderer);
            }
        }

        // fills the inner pixels
        this.fillHorizontally(pixelRenderer.getYs());

        // fill the pixels distance recursively
        // fillFrontierDistance(this, pixelRenderer.getModifiedPixels());
        // fillOuterFrontierDistance(this, pixelRenderer.getModifiedPixels());
        // blurDistance(this);
        // fillUVTextureFromOuterFrontier( this,
        // pixelRenderer.getModifiedPixels());
        Set<Point> nonInfiniteModifiedPixels = this
                .getModifiedPixelsButInfiniteDistancePixels(pixelRenderer
                        .getModifiedPixels());
        // Set<Point> nonInfiniteModifiedPixels =
        // pixelRenderer.getModifiedPixels();
        fillTextureCoordinates(this.texImage, nonInfiniteModifiedPixels,
                this.imageToPolygonFactorX, this.imageToPolygonFactorY);
        TextureImageUtil.rescaleTextureCoordinates(this.texImage, this.uScale,
                this.vScale);

        // TextureImageUtil.checkTextureCoordinates(this);

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
    private Set<Point> getModifiedPixelsButInfiniteDistancePixels(
            Set<Point> modifiedPixels) {
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
                logger.warn("x values count cannot be even ! y = " + y + " : "
                        + xs.size() + " : " + xs);
            }
            // draw horizontal lines between xs pixel pairs/couples
            for (int n = 0; n < xs.size() / 2; n++) {
                int x1 = xs.get(2 * n);
                int x2 = xs.get(2 * n + 1);
                for (int x = x1; x <= x2; x++) {
                    TexturePixel pixel = this.texImage.getPixel(x, y);
                    if (pixel != null) {
                        pixel.in = true;
                        if (pixel.frontier == 0) {
                            pixel.distance = Double.MAX_VALUE;
                        }
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
    private void drawFrontier(IRing frontier, int frontierId,
            DistanceFieldFrontierPixelRenderer pixelRenderer) {
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
        Point2D proj0 = this.worldToProj(p0);
        Point2D proj1 = this.worldToProj(p1);
        Point2D proj2 = this.worldToProj(p2);
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
            pixelRenderer.setLinearParameterization(linearDistance,
                    linearDistance + segmentLength);
            // FIXME: very special case for 'mer JDD plancoet'. Long outer
            // frontier
            // don't have to be of distance 0
            pixelRenderer.setDistanceToZero(segmentLength < 1000);
            if (!(x1 == x2 && y1 == y2)) {
                this.texImage.drawLine(x1, y1, x2, y2, pixelRenderer);
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
    private static void fillFrontierDistance(GradientTextureImage image,
            Set<Point> set, final double pixelWidth, final double pixelHeight) {
        double maxDistance = 0;
        while (set.size() > 0) {
            // System.err.println(modifiedPixels.size() + " modified pixels");
            HashSet<Point> newlyModifiedPixels = new HashSet<Point>();
            for (Point p : set) {
                TexturePixel pixel = image.getPixel(p.x, p.y);
                if (pixel == null) {
                    throw new IllegalStateException(
                            "modified pixels cannot be outside image ... "
                                    + p.x + "x" + p.y);
                }
                double distance = pixel.distance;
                boolean w = fillFrontierDistance(distance + pixelWidth,
                        new Point(p.x - 1, p.y), image, newlyModifiedPixels);
                boolean e = fillFrontierDistance(distance + pixelWidth,
                        new Point(p.x + 1, p.y), image, newlyModifiedPixels);
                boolean n = fillFrontierDistance(distance + pixelHeight,
                        new Point(p.x, p.y - 1), image, newlyModifiedPixels);
                boolean s = fillFrontierDistance(distance + pixelHeight,
                        new Point(p.x, p.y + 1), image, newlyModifiedPixels);
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
    private static void fillOuterFrontierDistance(GradientTextureImage image,
            Set<Point> set, final double pixelWidth, final double pixelHeight) {
        double maxDistance = 0;
        while (set.size() > 0) {
            // System.err.println(modifiedPixels.size() + " modified pixels");
            HashSet<Point> newlyModifiedPixels = new HashSet<Point>();
            for (Point p : set) {
                TexturePixel pixel = image.getPixel(p.x, p.y);
                if (pixel == null) {
                    throw new IllegalStateException(
                            "modified pixels cannot be outside image ... "
                                    + p.x + "x" + p.y);
                }
                if (pixel.frontier < 0) {
                    continue; // skip inner frontier ( pixel.frontier < 0 )
                }
                double distance = pixel.distance;
                boolean w = fillFrontierDistance(distance + pixelWidth,
                        new Point(p.x - 1, p.y), image, newlyModifiedPixels);
                boolean e = fillFrontierDistance(distance + pixelWidth,
                        new Point(p.x + 1, p.y), image, newlyModifiedPixels);
                boolean n = fillFrontierDistance(distance + pixelHeight,
                        new Point(p.x, p.y - 1), image, newlyModifiedPixels);
                boolean s = fillFrontierDistance(distance + pixelHeight,
                        new Point(p.x, p.y + 1), image, newlyModifiedPixels);
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
    private static boolean fillFrontierDistance(double d, Point p,
            GradientTextureImage image, Set<Point> newlyModifiedPixels) {
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
    private static void fillTextureCoordinates(GradientTextureImage image,
            Set<Point> set, final double pixelWidth, final double pixelHeight) {
        double maxDistance = 0;
        while (set.size() > 0) {
            // System.err.println(modifiedPixels.size() + " modified pixels");
            HashSet<Point> newlyModifiedPixels = new HashSet<Point>();
            for (Point p : set) {
                TexturePixel pixel = image.getPixel(p.x, p.y);
                if (pixel == null) {
                    throw new IllegalStateException(
                            "modified pixels cannot be outside image ... "
                                    + p.x + "x" + p.y);
                }
                double distance = pixel.distance;
                boolean w = fillTextureCoordinates(image,
                        distance + pixelWidth, pixel.uTexture, new Point(
                                p.x - 1, p.y), newlyModifiedPixels);
                boolean e = fillTextureCoordinates(image,
                        distance + pixelWidth, pixel.uTexture, new Point(
                                p.x + 1, p.y), newlyModifiedPixels);
                boolean n = fillTextureCoordinates(image, distance
                        + pixelHeight, pixel.uTexture, new Point(p.x, p.y - 1),
                        newlyModifiedPixels);
                boolean s = fillTextureCoordinates(image, distance
                        + pixelHeight, pixel.uTexture, new Point(p.x, p.y + 1),
                        newlyModifiedPixels);
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
    private static boolean fillTextureCoordinates(
            GradientTextureImage texImage, double d, double uTexture, Point p,
            Set<Point> newlyModifiedPixels) {
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
        return new Point2D.Double((polygonCoordinates.getX() - this.minX)
                / this.imageToPolygonFactorX,
                (polygonCoordinates.getY() - this.minY)
                        / this.imageToPolygonFactorY);
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
                (polygonCoordinates.y - this.minY) / this.imageToPolygonFactorY);
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

    public void setUScale(double d) {
        this.uScale = d;

    }

    public void setVScale(double d) {
        this.vScale = d;

    }

    @Override
    public int getTextureWidth() {
        return this.texImage.getWidth();
    }

    @Override
    public int getTextureHeight() {
        return this.texImage.getHeight();
    }

}
