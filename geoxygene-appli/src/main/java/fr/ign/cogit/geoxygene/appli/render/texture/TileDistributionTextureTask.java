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

package fr.ign.cogit.geoxygene.appli.render.texture;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import utils.Pair;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.DistanceFieldFrontierPixelRenderer;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.texture.DimensionDescriptor;
import fr.ign.cogit.geoxygene.style.texture.ProbabilistTileDescriptor;
import fr.ign.cogit.geoxygene.style.texture.TileDistributionTexture;
import fr.ign.cogit.geoxygene.style.texture.TileDistributionTexture.TileBlendingType;
import fr.ign.cogit.geoxygene.util.gl.GradientTextureImage;
import fr.ign.cogit.geoxygene.util.gl.GradientTextureImage.TexturePixel;
import fr.ign.cogit.geoxygene.util.gl.Sample;
import fr.ign.cogit.geoxygene.util.gl.Tile;
import fr.ign.cogit.geoxygene.util.graphcut.DefaultTile;
import fr.ign.cogit.geoxygene.util.graphcut.GraphCut;

/**
 * @author JeT
 * 
 */
public class TileDistributionTextureTask extends
        AbstractTextureTask<TileDistributionTexture> {

    private static final Logger logger = Logger
            .getLogger(TileDistributionTextureTask.class.getName()); // logger

    private final List<Pair<TileProbability, Tile>> tilesToBeApplied = new ArrayList<Pair<TileProbability, Tile>>();
    private GradientTextureImage texImage; //
    private IEnvelope envelope = null;
    private Shape featureShape = null; // shape corresponding to the given
                                       // feature in the image texture space
    // private final DistanceFieldTexture texture = null;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private int textureWidth = -1; // final textured image width & height
    private int textureHeight = -1; // dimension are computed using resolution
                                    // and map scale
    private double printResolution = 600; // expressed in DPI
    private double mapScale = 1 / 100000.; // map scale
    private double imageToPolygonFactorX;
    private double imageToPolygonFactorY;
    private final List<IPolygon> polygons = new ArrayList<IPolygon>();
    private final List<IRing> rings = new ArrayList<IRing>();
    private final List<ParameterizedSegment> segments = new ArrayList<ParameterizedSegment>();
    // private DistanceFieldFrontierPixelRenderer pixelRenderer = null;
    // private final Set<Point> modifiedPixels = new HashSet<Point>();
    // private final AffineTransform transform = new AffineTransform();
    private IFeatureCollection<IFeature> featureCollection = null;
    private Viewport viewport = null;

    private final boolean memoryMonitoring = true;
    private long previousUsedMemory = 0;

    private static final double CM_PER_INCH = 2.540005;
    private static final double M_PER_INCH = CM_PER_INCH / 100.;

    /**
     * @param texture
     * @param featureCollection2
     * @param viewport2
     */
    public TileDistributionTextureTask(TileDistributionTexture texture,
            IFeatureCollection<IFeature> featureCollection2, Viewport viewport2) {
        super(texture);
        this.setFeatureCollection(featureCollection2);
        this.setViewport(viewport2);
        this.setPrintResolution(texture.getTextureResolution());
        this.computeEnvelope();

    }

    /**
     * Initialize tiles (the texture image must be previously generated)
     * 
     * @throws IOException
     */
    private final void initTiles() throws IOException {
        for (ProbabilistTileDescriptor tileDesc : this.getTexture().getTiles()) {
            Tile tile = DefaultTile.read(new URL(tileDesc.getUrl()));
            DistanceTileProbability p = new DistanceTileProbability(
                    this.texImage, tileDesc.getMinDistance(),
                    tileDesc.getMaxDistance(),
                    tileDesc.getInRangeProbability(),
                    tileDesc.getOutOfRangeProbability());
            this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(p, tile));
        }
    }

    synchronized public void updateContent()
            throws NoninvertibleTransformException {
        this.setPrintResolution(this.getTexture().getTextureResolution());
        this.computeEnvelope();

        this.texImage = new GradientTextureImage(this.getTextureWidth(),
                this.getTextureHeight());
        this.imageToPolygonFactorX = (this.maxX - this.minX)
                / (this.getTextureWidth() - 1);
        this.imageToPolygonFactorY = (this.maxY - this.minY)
                / (this.getTextureHeight() - 1);

        this.polygons.clear();
        // convert the multisurface as a collection of polygons
        for (IFeature feature : this.featureCollection) {
            if (feature.getGeom() instanceof IMultiSurface<?>) {
                IMultiSurface<?> multiSurface = (IMultiSurface<?>) feature
                        .getGeom();
                for (IOrientableSurface surface : multiSurface.getList()) {
                    if (surface instanceof IPolygon) {
                        IPolygon polygon = (IPolygon) surface;
                        this.polygons.add(polygon);
                    } else {
                        logger.error("Distance Field Parameterizer does handle multi surfaces containing only polygons, not "
                                + surface.getClass().getSimpleName());
                    }
                }

            } else {
                System.err.println("geometry type not handled : "
                        + feature.getGeom().getClass().getSimpleName());
            }
        }
        // collect all rings in one list
        this.rings.clear();
        for (IPolygon polygon : this.polygons) {
            this.rings.add(polygon.getExterior());
            for (IRing ring : polygon.getInterior()) {
                this.rings.add(ring);
            }
        }
        // generate all segments
        this.segments.clear();
        for (IRing ring : this.rings) {
            double u = 0;
            for (int i = 0; i < ring.coord().size(); i++) {
                int j = (i + 1) % ring.coord().size();
                IDirectPosition pd1 = ring.coord().get(i);
                IDirectPosition pd2 = ring.coord().get(j);
                double segmentLength = pd2.distance(pd1);

                ParameterizedPoint p1 = new ParameterizedPoint(pd1.getX(),
                        pd1.getY(), u, 0);
                u += segmentLength;
                ParameterizedPoint p2 = new ParameterizedPoint(pd2.getX(),
                        pd2.getY(), u, 0);

                this.segments.add(new ParameterizedSegment(p1, p2));
            }
        }

        // create Shape
        IDirectPositionList viewDirectPositionList = null;
        IDirectPosition lastPosition = null;
        for (IPolygon polygon : this.polygons) {
            IDirectPositionList list = this.viewport
                    .toViewDirectPositionList(polygon);
            if (viewDirectPositionList == null) {
                viewDirectPositionList = list;
                lastPosition = list.get(list.size() - 1);
            } else {
                viewDirectPositionList.addAll(list);
                viewDirectPositionList.add(lastPosition);
            }
        }
        this.featureShape = this.toPolygonShape(viewDirectPositionList);

        // sort segments to begin with smallest ones
        // Collections.sort(this.segments, new SegmentComparator());

    }

    /**
     * @return the printResolution
     */
    public double getPrintResolution() {
        return this.printResolution;
    }

    /**
     * @param printResolution
     *            the printResolution to set
     */
    public void setPrintResolution(double printResolution) {
        this.printResolution = printResolution;
        this.textureWidth = -1;
        this.textureHeight = -1;
    }

    /**
     * @return the mapScale
     */
    public double getMapScale() {
        return this.mapScale;
    }

    /**
     * @param mapScale
     *            the mapScale to set
     */
    public void setMapScale(double mapScale) {
        this.mapScale = mapScale;
    }

    /**
     * 
     */
    private void computeEnvelope() {
        if (this.featureCollection == null) {
            throw new IllegalStateException("Feature is not set");
        }
        this.envelope = this.featureCollection.getEnvelope();
        this.minX = this.envelope.getLowerCorner().getX();
        this.minY = this.envelope.getLowerCorner().getY();
        this.maxX = this.envelope.getUpperCorner().getX();
        this.maxY = this.envelope.getUpperCorner().getY();
    }

    private void computeGradient() {
        for (int y = 0; y < this.texImage.getHeight(); y++) {
            for (int x = 0; x < this.texImage.getWidth(); x++) {
                TexturePixel pixel = this.texImage.getPixel(x, y);
                if (pixel.in) {
                    // pixel.vGradient = new
                    // Point2d(Math.cos(pixel.mainDirection),
                    // Math.sin(pixel.mainDirection));
                    pixel.vGradient = computeGradient(this.texImage, x, y);
                } else {
                    pixel.vGradient = null;
                }
            }
        }
    }

    private static Point2d computeGradient(GradientTextureImage image, int x,
            int y) {
        TexturePixel p = image.getPixel(x, y);
        TexturePixel pxp1 = image.getPixel(x + 1, y);
        TexturePixel pxm1 = image.getPixel(x - 1, y);
        TexturePixel pyp1 = image.getPixel(x, y + 1);
        TexturePixel pym1 = image.getPixel(x, y - 1);
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
     * Transform a direct position list in view coordinates to an awt shape.
     * 
     * @param viewDirectPositionList
     *            a direct position list in view coordinates
     * @return A shape representing the polygon in view coordinates
     */
    private Shape toPolygonShape(
            final IDirectPositionList viewDirectPositionList) {
        int numPoints = viewDirectPositionList.size();
        int[] xpoints = new int[numPoints];
        int[] ypoints = new int[numPoints];
        for (int i = 0; i < viewDirectPositionList.size(); i++) {
            IDirectPosition p = viewDirectPositionList.get(i);
            xpoints[i] = (int) p.getX();
            ypoints[i] = (int) p.getY();
        }
        return new Polygon(xpoints, ypoints, numPoints);
    }

    /**
     * get or compute the image width using screen resolution, map scale and
     * feature real world size
     * 
     * @return the final image texture width
     */
    @Override
    public int getTextureWidth() {
        if (this.textureWidth <= 0) {
            this.textureWidth = (int) (this.getEnvelope().width()
                    * this.getMapScale() * this.getPrintResolution() / M_PER_INCH);
            if (this.textureWidth <= 0) {
                logger.error("texture width is invalid: envelope width = "
                        + this.getEnvelope().width() + " * scale = "
                        + this.getMapScale() + " resolution = "
                        + this.getPrintResolution() + " /  MperINCH  = "
                        + M_PER_INCH);
            }
        }
        return this.textureWidth;
    }

    /**
     * get or compute the image height using screen resolution, map scale and
     * feature real world size
     * 
     * @return the final image texture height
     */
    @Override
    public int getTextureHeight() {
        if (this.textureHeight <= 0) {
            this.textureHeight = (int) (this.getEnvelope().length()
                    * this.getMapScale() * this.getPrintResolution() / M_PER_INCH);
            if (this.textureHeight <= 0) {
                logger.error("texture height is invalid: envelope height = "
                        + this.getEnvelope().height() + " * scale = "
                        + this.getMapScale() + " resolution = "
                        + this.getPrintResolution() + " /  MperINCH  = "
                        + M_PER_INCH);
            }
        }
        return this.textureHeight;
    }

    /**
     * get the envelope or compute it if needed
     * 
     * @return the feature collection envelope
     */
    private IEnvelope getEnvelope() {
        if (this.envelope == null) {
            this.computeEnvelope();
        }
        return this.envelope;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#isProgressable()
     */
    @Override
    public boolean isProgressable() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#isPausable()
     */
    @Override
    public boolean isPausable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#isStoppable()
     */
    @Override
    public boolean isStoppable() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        this.setState(TaskState.WAITING);
        this.setState(TaskState.INITIALIZING);
        this.setState(TaskState.RUNNING);
        this.setProgress(0);
        try {
            this.monitorMemory("Start");
            if (!this.generateGradientTexture()) {
                this.setState(TaskState.STOPPED);
                return;
            }
            this.monitorMemory("after generate gradient texture");
            this.getTexture().setxRepeat(false);
            this.getTexture().setyRepeat(false);
            this.getTexture().setDimension(
                    new DimensionDescriptor(this.envelope.width(),
                            this.envelope.length()));
            this.getTexture().setTextureWidth(this.getTextureWidth());
            this.getTexture().setTextureWidth(this.getTextureHeight());

            this.monitorMemory("after setting dimension");
            TextureImageTileChooser tileChooser = new TextureImageTileChooser();
            for (Pair<TileProbability, Tile> pair : this.tilesToBeApplied) {
                tileChooser.addTile(pair.first(), pair.second());
            }
            this.monitorMemory("after tile chooser creation");
            if (this.isStopRequested()) {
                this.setState(TaskState.STOPPED);
                return;
            }
            TextureImageSamplerMipMap sampler = new TextureImageSamplerMipMap(
                    this.texImage, tileChooser);

            if (this.isStopRequested()) {
                this.setState(TaskState.STOPPED);
                return;
            }

            this.monitorMemory("after sampler creation");
            BufferedImage bi = null;
            bi = this.pasteTiles(this.texImage, this.tilesToBeApplied, sampler,
                    this.featureShape);
            this.monitorMemory("tiles pasted");
            // TextureImageUtil.save(this.texImage, "texturedImage");
            // ImageIO.write(bi, "PNG", new File("texturedPolygon.png"));
            // Flip the image vertically
            AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
            tx.translate(0, -bi.getHeight(null));
            AffineTransformOp op = new AffineTransformOp(tx,
                    AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            bi = op.filter(bi, null);

            this.monitorMemory("texture image transformed");
            this.getTexture().setTextureImage(bi);
            System.err.println("final texture image is set : "
                    + this.getTexture().getTextureImage());
            this.setProgress(1);
            this.setState(TaskState.FINISHED);
            this.monitorMemory("termination");
        } catch (Exception e) {
            e.printStackTrace();
            this.setError(e);
            this.setState(TaskState.ERROR);
        }
    }

    /**
     * @throws NoninvertibleTransformException
     * @throws IOException
     */
    private boolean generateGradientTexture()
            throws NoninvertibleTransformException, IOException {
        this.updateContent();
        if (this.isStopRequested()) {
            this.setState(TaskState.STOPPED);
            return false;
        }
        this.generateGradientTextureImage();
        if (this.isStopRequested()) {
            this.setState(TaskState.STOPPED);
            return false;
        }
        this.initTiles();
        if (this.isStopRequested()) {
            this.setState(TaskState.STOPPED);
            return false;
        }
        return true;
    }

    synchronized private void generateGradientTextureImage() {
        // draw Frontiers into texture image
        DistanceFieldFrontierPixelRenderer pixelRenderer = new DistanceFieldFrontierPixelRenderer();
        for (IPolygon polygon : this.polygons) {
            pixelRenderer.getYs().clear(); // #note1
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

            this.fillHorizontally(pixelRenderer.getYs()); // #note1
        }
        // #note1
        // fillHorizontally was previously outside the polygon loop. It is valid
        // if polygons
        // do not self intersect. It has been modified due to a shape file that
        // has a full duplicated geometry
        // may be we can assert that polygons may not self intersect and get
        // back to previous version...

        // try {
        // TextureImageUtil.save(this.texImage, "1-frontiers");
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
        // TextureImageUtil.save(this.texImage, "2-filled");
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        Set<Point> modifiedPixels = new HashSet<Point>();

        // FIXME: HACK: remove long edges (for the sea outside borders not to be
        // considered as sea edges)
        modifiedPixels = this
                .getModifiedPixelsButInfiniteDistancePixels(pixelRenderer
                        .getModifiedPixels());

        // Set<Point> nonInfiniteModifiedPixels =
        // pixelRenderer.getModifiedPixels();
        while (!modifiedPixels.isEmpty()) {
            modifiedPixels = fillTextureCoordinates4(this.texImage,
                    modifiedPixels, this.imageToPolygonFactorX,
                    this.imageToPolygonFactorY);
        }
        // try {
        // TextureImageUtil.save(this.texImage, "3-texcoord");
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

        scaleV(this.texImage, this.texImage.getdMax());
        this.computeGradient();
        // try {
        // TextureImageUtil.save(this.texImage, "4-gradient");
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }

    }

    /**
     * @param image
     * @param maxDistance
     */
    private static void scaleV(GradientTextureImage image, double maxDistance) {
        // fill yTexture coordinates as distance / maxDistance for any pixel
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                pixel.vTexture = pixel.distance / maxDistance;
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
            GradientTextureImage image, Set<Point> set,
            final double pixelWidth, final double pixelHeight) {
        // System.err.println(modifiedPixels.size() + " modified pixels");
        HashSet<Point> newlyModifiedPixels = new HashSet<Point>();
        for (Point p : set) {
            TexturePixel pixel = image.getPixel(p.x, p.y);
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
     * Remove all pixels which have an infinite distance from the modified
     * pixels.
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
            } else {
                pixel.uTexture = 0;
                pixel.vTexture = 0;
            }
        }
        this.texImage.invalidateUVBounds();
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
                int x1 = Math.min(xs.get(2 * n), xs.get(2 * n + 1));
                int x2 = Math.max(xs.get(2 * n), xs.get(2 * n + 1));
                for (int x = x1; x <= x2; x++) {
                    TexturePixel pixel = this.texImage.getPixel(x, y);
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
            // special case not to set distance to zero in some cases (for outer
            // sea limits which are not real coast lines
            if (segmentLength > this.getTexture().getMaxCoastlineLength()) {
                logger.debug("segment " + segmentLength + " long removed (>"
                        + this.getTexture().getMaxCoastlineLength() + ")");
            }
            pixelRenderer.setDistanceToZero(segmentLength < this.getTexture()
                    .getMaxCoastlineLength());
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

    /**
     * Paste all computed tiles using blending modes
     * 
     * @param image
     *            image to fill
     * @param tilesToBeApplied
     *            list of tiles to apply with their probabilities
     * @param sampler
     *            object computing samples
     * @param clippingShape
     * @return
     */
    private BufferedImage pasteTiles(GradientTextureImage image,
            List<Pair<TileProbability, Tile>> tilesToBeApplied,
            SamplingAlgorithm sampler, Shape clippingShape) {
        image.invalidateUVBounds();
        TextureImageTileChooser tileChooser = new TextureImageTileChooser();
        for (Pair<TileProbability, Tile> pair : tilesToBeApplied) {
            tileChooser.addTile(pair.first(), pair.second());
        }
        BufferedImage bi = new BufferedImage(this.getTextureWidth(),
                this.getTextureHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        GraphCut graphCut = null;
        if (this.getTexture().getBlending() == TileBlendingType.GRAPHCUT) {
            graphCut = new GraphCut(bi);
        }

        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, this.getTextureWidth(), this.getTextureHeight());
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        // if (clippingShape != null) {
        // Shape screenSpaceShape =
        // this.transform.createTransformedShape(clippingShape);
        // g2.setClip(screenSpaceShape);
        // }
        int nbSamples = sampler.getSampleCount();
        int nSample = 0;
        Iterator<Sample> sampleIterator = sampler.getSampleIterator();
        while (sampleIterator.hasNext()) {
            if (this.isStopRequested()) {
                this.setState(TaskState.STOPPED);
                return null;
            }

            Sample sample = sampleIterator.next();
            double xTexture = sample.getLocation().getX();
            double yTexture = sample.getLocation().getY();
            Tile tile = sample.getTile() != null ? sample.getTile()
                    : tileChooser.getTile(sample);
            if (tile == null) {
                continue;
            }
            BufferedImage tileImage = tile.getImage();

            TexturePixel pixel = image.getPixel((int) xTexture, (int) yTexture);
            if (pixel == null || !(pixel.in || pixel.frontier != 0)
                    || pixel.vGradient == null) {
                logger.warn("invalid pixel = " + pixel);
                continue;
            } else {
                AffineTransform transform = image.tileTransform((int) xTexture,
                        (int) yTexture, tileImage.getWidth(),
                        tileImage.getHeight());
                if (this.getTexture().getBlending() == TileBlendingType.GRAPHCUT) {
                    graphCut.pasteTile(tile, transform);
                } else {
                    g2.drawImage(tileImage, transform, null);
                    g2.fillRect(tileImage.getWidth() / 2,
                            tileImage.getHeight() / 2, tileImage.getWidth(),
                            tileImage.getHeight());
                }
                // try {
                // ImageIO.write(bi, "PNG", new File("tile-" + nSample +
                // ".png"));
                // } catch (IOException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }
            }
            this.setProgress((double) nSample / nbSamples);
            nSample++;
        }
        return bi;
    }

    public void setFeatureCollection(
            IFeatureCollection<IFeature> iFeatureCollection) {
        this.featureCollection = iFeatureCollection;

    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    private final void monitorMemory(String message) {
        if (this.memoryMonitoring) {
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            System.err.println(message + " : "
                    + (usedMemory - this.previousUsedMemory));
            this.previousUsedMemory = usedMemory;
        }
    }
}
