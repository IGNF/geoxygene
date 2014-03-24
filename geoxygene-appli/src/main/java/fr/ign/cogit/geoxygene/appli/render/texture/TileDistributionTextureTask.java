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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import test.app.DisplayPanel.ParameterizedPoint;
import test.app.DisplayPanel.ParameterizedSegment;
import test.app.DistanceFieldApplication;
import test.app.DistanceTileProbability;
import test.app.TextureImageSamplerMipMap;
import test.app.TextureImageTileChooser;
import test.app.TileProbability;
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
import fr.ign.cogit.geoxygene.api.texture.Sample;
import fr.ign.cogit.geoxygene.api.texture.Tile;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.DistanceFieldFrontierPixelRenderer;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.style.texture.TileDistributionTexture;
import fr.ign.cogit.geoxygene.util.gl.TextureImage;
import fr.ign.cogit.geoxygene.util.gl.TextureImage.TexturePixel;
import fr.ign.cogit.geoxygene.util.graphcut.DefaultTile;

/**
 * @author JeT
 * 
 */
public class TileDistributionTextureTask extends AbstractTextureTask<TileDistributionTexture> {

    private static final Logger logger = Logger.getLogger(TileDistributionTextureTask.class.getName()); // logger

    private final List<Pair<TileProbability, Tile>> tilesToBeApplied = new ArrayList<Pair<TileProbability, Tile>>();
    private TextureImage texImage; // the one that is visualized (with filters)
    private IEnvelope envelope = null;
    private Shape featureShape = null; // shape corresponding to the given feature in the image texture space
    //    private final DistanceFieldTexture texture = null;
    private final DistanceFieldApplication app = null;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private int textureWidth = -1; // final textured image width & height
    private int textureHeight = -1; // dimension are computed using resolution and map scale
    private double printResolution = 600; // expressed in DPI
    private double mapScale = 1 / 100000.; // map scale
    private double imageToPolygonFactorX;
    private double imageToPolygonFactorY;
    private final List<IPolygon> polygons = new ArrayList<IPolygon>();
    private final List<IRing> rings = new ArrayList<IRing>();
    private final List<ParameterizedSegment> segments = new ArrayList<ParameterizedSegment>();
    //    private DistanceFieldFrontierPixelRenderer pixelRenderer = null;
    //    private final Set<Point> modifiedPixels = new HashSet<Point>();
    private final AffineTransform transform = new AffineTransform();
    private IFeatureCollection<IFeature> featureCollection = null;
    private Viewport viewport = null;

    private static final double CM_PER_INCH = 2.540005;
    private static final double M_PER_INCH = CM_PER_INCH / 100.;

    /**
     * Initialize tiles (the texture image must be previously generated)
     * 
     * @throws IOException
     */
    private final void initTiles() throws IOException {
        //        DistanceTileProbability closeProbability = new DistanceTileProbability(this.texImage, Double.NEGATIVE_INFINITY, 100, 1, 0);
        //        DistanceTileProbability mediumProbability = new DistanceTileProbability(this.texImage, 100, 200, 1, 0);
        //        DistanceTileProbability farmediumProbability = new DistanceTileProbability(this.texImage, 200, 250, 1, 0);
        //        DistanceTileProbability farProbability = new DistanceTileProbability(this.texImage, 200, 300, 0.7, 0);
        DistanceTileProbability allProbability = new DistanceTileProbability(this.texImage, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1, 0);

        Tile tileTexture = DefaultTile.read("/export/home/kandinsky/turbet/cassini samples/waves small.png");
        this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(allProbability, tileTexture));
        //            tileTexture = DefaultTile.read("/home/turbet/Documents/s2.png");
        //            this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(closeProbability, tileTexture));
        //            tileTexture = DefaultTile.read("/home/turbet/Documents/s3.png");
        //            this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(closeProbability, tileTexture));

        //        tileTexture = DefaultTile.read("/export/home/kandinsky/turbet/cassini samples/waves small.png");
        //        this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(mediumProbability, tileTexture));
        //        tileTexture = DefaultTile.read("/export/home/kandinsky/turbet/cassini samples/waves big.png");
        //        this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(mediumProbability, tileTexture));
        //        //            tileTexture = DefaultTile.read("/home/turbet/Documents/t3.png");
        //        //            this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(mediumProbability, tileTexture));
        //
        //        tileTexture = DefaultTile.read("/export/home/kandinsky/turbet/cassini samples/crest small.png");
        //        this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(farProbability, tileTexture));
        //        tileTexture = DefaultTile.read("/export/home/kandinsky/turbet/cassini samples/crest big.png");
        //        this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(farProbability, tileTexture));
        //        //            tileTexture = DefaultTile.read("/home/turbet/Documents/u3.png");
        //        //            this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(farProbability, tileTexture));

    }

    public void updateContent() {
        this.computeEnvelope();

        this.texImage = new TextureImage(this.getTextureWidth(), this.getTextureHeight());
        this.imageToPolygonFactorX = (this.maxX - this.minX) / (this.getTextureWidth() - 1);
        this.imageToPolygonFactorY = (this.maxY - this.minY) / (this.getTextureHeight() - 1);

        this.polygons.clear();
        // convert the multisurface as a collection of polygons
        for (IFeature feature : this.featureCollection) {
            if (feature.getGeom() instanceof IMultiSurface<?>) {
                IMultiSurface<?> multiSurface = (IMultiSurface<?>) feature.getGeom();
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
                System.err.println("geometry type not handled : " + feature.getGeom().getClass().getSimpleName());
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

                ParameterizedPoint p1 = new ParameterizedPoint(pd1.getX(), pd1.getY(), u, 0);
                u += segmentLength;
                ParameterizedPoint p2 = new ParameterizedPoint(pd2.getX(), pd2.getY(), u, 0);

                this.segments.add(new ParameterizedSegment(p1, p2));
            }
        }

        // create Shape
        IDirectPositionList viewDirectPositionList = null;
        IDirectPosition lastPosition = null;
        for (IPolygon polygon : this.polygons) {
            IDirectPositionList list = this.toViewDirectPositionList(polygon);
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
        Collections.sort(this.segments, new SegmentComparator());

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
                    //                    pixel.vGradient = new Point2d(Math.cos(pixel.mainDirection), Math.sin(pixel.mainDirection));
                    pixel.vGradient = computeGradient(this.texImage, x, y);
                } else {
                    pixel.vGradient = null;
                }
            }
        }
    }

    private static Point2d computeSobel3VGradient(TextureImage image, int x, int y) {
        final int windowDimension = 3;
        double[][] xSobelWeight = { { +1, +2, +1 }, { 0, 0, 0 }, { -1, -2, -1 } };
        double[][] ySobelWeight = { { +1, 0, -1 }, { +2, 0, -2 }, { +1, 0, -1 } };
        double xSumWeight = 0;
        double ySumWeight = 0;
        double xGradient = 0;
        double yGradient = 0;
        for (int wy = 0; wy < windowDimension; wy++) {
            for (int wx = 0; wx < windowDimension; wx++) {
                TexturePixel wPixel = image.getPixel(x + wx - windowDimension, y + wy - windowDimension);
                if (wPixel == null || wPixel.in == false) {
                    continue;
                }
                xSumWeight += xSobelWeight[wx][wy];
                ySumWeight += ySobelWeight[wx][wy];
                xGradient += xSobelWeight[wx][wy] * wPixel.vTexture;
                yGradient += ySobelWeight[wx][wy] * wPixel.vTexture;

            }

        }
        if (xSumWeight != 0) {
            xGradient /= xSumWeight;
        }
        if (ySumWeight != 0) {
            yGradient /= ySumWeight;
        }
        return new Point2d(xGradient, yGradient);
    }

    private static Point2d computeGradient(TextureImage image, int x, int y) {
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
    private Shape toPolygonShape(final IDirectPositionList viewDirectPositionList) {
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

    public final IDirectPositionList toViewDirectPositionList(final IPolygon p) {
        IDirectPositionList viewDirectPositionList = this.toViewDirectPositionList(p.getExterior().coord());
        if (viewDirectPositionList.isEmpty()) {
            return null;
        }
        IDirectPosition lastExteriorRingDirectPosition = viewDirectPositionList.get(viewDirectPositionList.size() - 1);
        for (int i = 0; i < p.sizeInterior(); i++) {
            viewDirectPositionList.addAll(this.toViewDirectPositionList(p.getInterior(i).coord()));
            viewDirectPositionList.add(lastExteriorRingDirectPosition);
        }
        return viewDirectPositionList;
    }

    public final IDirectPositionList toViewDirectPositionList(final IDirectPositionList modelDirectPositionList) {
        IDirectPositionList viewDirectPositionList = new DirectPositionList();
        if (modelDirectPositionList.isEmpty()) {
            return viewDirectPositionList;
        }
        int numberOfModelPoints = modelDirectPositionList.size();
        for (int i = 0; i < numberOfModelPoints; i++) {
            IDirectPosition pi = modelDirectPositionList.get(i);
            Point2D point2D = this.toImageCoordinates(pi);
            viewDirectPositionList.add(new DirectPosition(point2D.getX(), point2D.getY()));
        }
        return viewDirectPositionList;
    }

    private Point2D toImageCoordinates(IDirectPosition pi) {
        double x = (pi.getX() - this.minX) / this.imageToPolygonFactorX;
        double y = (pi.getY() - this.minY) / this.imageToPolygonFactorY;
        return new Point2D.Double(x, y);
    }

    public static class SegmentComparator implements Comparator<ParameterizedSegment> {

        @Override
        public int compare(ParameterizedSegment o1, ParameterizedSegment o2) {
            double l1 = Math.sqrt((o1.p2.x - o1.p1.x) * (o1.p2.x - o1.p1.x) + (o1.p2.y - o1.p1.y) * (o1.p2.y - o1.p1.y));
            double l2 = Math.sqrt((o2.p2.x - o2.p1.x) * (o2.p2.x - o2.p1.x) + (o2.p2.y - o2.p1.y) * (o2.p2.y - o2.p1.y));
            return l1 < l2 ? -1 : l2 > l1 ? +1 : 0;
        }

    }

    /**
     * get or compute the image width using screen resolution, map scale and
     * feature real world size
     * 
     * @return the final image texture width
     */
    public int getTextureWidth() {
        if (this.textureWidth < 0) {
            this.textureWidth = (int) (this.getEnvelope().width() * this.getMapScale() * this.getPrintResolution() / M_PER_INCH);
            if (this.textureWidth <= 0) {
                logger.error("texture width is invalid: envelope height = " + this.getEnvelope().width() + " * scale = " + this.getMapScale()
                        + " resolution = " + this.getPrintResolution() + " /  MperINCH  = " + M_PER_INCH);
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
    public int getTextureHeight() {
        if (this.textureHeight < 0) {
            this.textureHeight = (int) (this.getEnvelope().length() * this.getMapScale() * this.getPrintResolution() / M_PER_INCH);
            if (this.textureHeight <= 0) {
                logger.error("texture height is invalid: envelope height = " + this.getEnvelope().height() + " * scale = " + this.getMapScale()
                        + " resolution = " + this.getPrintResolution() + " /  MperINCH  = " + M_PER_INCH);
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

    /**
     * @param texture
     */
    public TileDistributionTextureTask(TileDistributionTexture texture) {
        super(texture);
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
        this.setState(TaskState.INITIALIZING);
        this.setState(TaskState.RUNNING);
        this.setProgress(0);
        try {
            this.updateContent();

            System.err.println("generate gradient map");
            this.generateGradientTextureImage();
            this.initTiles();

            System.err.println("create tile chooser");
            TextureImageTileChooser tileChooser = new TextureImageTileChooser();
            for (Pair<TileProbability, Tile> pair : this.tilesToBeApplied) {
                tileChooser.addTile(pair.first(), pair.second());
            }
            System.err.println("compute sampler");
            TextureImageSamplerMipMap sampler = new TextureImageSamplerMipMap(this.texImage, tileChooser);
            System.err.println("display tiles");
            BufferedImage bi = this.toBufferedImagePixelUVTile(this.texImage, this.tilesToBeApplied, sampler, this.featureShape);
            //                this.bi = this.toBufferedImagePixelUVTileGraphCut(this.texImage, this.tilesToBeApplied, sampler, this.featureShape);
            System.err.println("finish");
            //            TextureImageUtil.save(this.texImage, "texturedImage");
            //            ImageIO.write(bi, "PNG", new File("texturedPolygon.png"));
            this.getTexture().setTextureImage(bi);
            this.setProgress(1);
            this.setState(TaskState.FINISHED);
        } catch (Exception e) {
            e.printStackTrace();
            this.setState(TaskState.ERROR);
        }
    }

    private void generateGradientTextureImage() {
        // draw Frontiers into texture image
        DistanceFieldFrontierPixelRenderer pixelRenderer = new DistanceFieldFrontierPixelRenderer();

        for (IPolygon polygon : this.polygons) {
            pixelRenderer.getYs().clear(); // #note1
            // draw the outer frontier
            this.drawFrontier(polygon.getExterior(), 1, pixelRenderer);

            // draw all inner frontiers
            for (int innerFrontierIndex = 0; innerFrontierIndex < polygon.getInterior().size(); innerFrontierIndex++) {
                IRing innerFrontier = polygon.getInterior().get(innerFrontierIndex);
                this.drawFrontier(innerFrontier, -innerFrontierIndex - 1, pixelRenderer);
            }

            this.fillHorizontally(pixelRenderer.getYs()); // #note1
        }
        // #note1
        // fillHorizontally was previously outside the polygon loop. It is valid if polygons
        // do not self intersect. It has been modified due to a shape file that has a full duplicated geometry
        // may be we can assert that polygons may not self intersect and get back to previous version...

        //        try {
        //            TextureImageUtil.save(this.texImage, "1-frontiers");
        //        } catch (IOException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        //        for (Map.Entry<Integer, List<Integer>> entry : pixelRenderer.getYs().entrySet()) {
        //            System.err.println("Y(" + entry.getKey() + ") = " + Arrays.toString(entry.getValue().toArray(new Integer[0])));
        //        }
        // fills the inner pixels
        //        try {
        //            TextureImageUtil.save(this.texImage, "2-filled");
        //        } catch (IOException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        Set<Point> modifiedPixels = new HashSet<Point>();

        // FIXME: HACK: remove long edges (for the sea outside borders not to be considered as sea edges)
        modifiedPixels = this.getModifiedPixelsButInfiniteDistancePixels(pixelRenderer.getModifiedPixels());

        //        Set<Point> nonInfiniteModifiedPixels = pixelRenderer.getModifiedPixels();
        while (!modifiedPixels.isEmpty()) {
            modifiedPixels = fillTextureCoordinates4(this.texImage, modifiedPixels, this.imageToPolygonFactorX, this.imageToPolygonFactorY);
        }
        //        try {
        //            TextureImageUtil.save(this.texImage, "3-texcoord");
        //        } catch (IOException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }

        scaleV(this.texImage, this.texImage.getdMax());
        this.computeGradient();
        //        try {
        //            TextureImageUtil.save(this.texImage, "4-gradient");
        //        } catch (IOException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }

    }

    /**
     * @param image
     * @param maxDistance
     */
    private static void scaleV(TextureImage image, double maxDistance) {
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
    private static Set<Point> fillTextureCoordinates4(TextureImage image, Set<Point> set, final double pixelWidth, final double pixelHeight) {
        //            System.err.println(modifiedPixels.size() + " modified pixels");
        HashSet<Point> newlyModifiedPixels = new HashSet<Point>();
        for (Point p : set) {
            TexturePixel pixel = image.getPixel(p.x, p.y);
            if (pixel == null) {
                throw new IllegalStateException("modified pixels cannot be outside image ... " + p.x + "x" + p.y);
            }
            double distance = pixel.distance;
            fillTextureCoordinates(image, distance + pixelWidth, pixel.uTexture, new Point(p.x - 1, p.y), newlyModifiedPixels);
            fillTextureCoordinates(image, distance + pixelWidth, pixel.uTexture, new Point(p.x + 1, p.y), newlyModifiedPixels);
            fillTextureCoordinates(image, distance + pixelHeight, pixel.uTexture, new Point(p.x, p.y - 1), newlyModifiedPixels);
            fillTextureCoordinates(image, distance + pixelHeight, pixel.uTexture, new Point(p.x, p.y + 1), newlyModifiedPixels);
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
     * Remove all pixels which have an infinite distance from the modified
     * pixels.
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
                logger.warn("x values count cannot be even ! y = " + y + " : " + xs.size() + " : " + xs);
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
                        logger.warn("forget unknown pixel " + x + "x" + y + " = " + pixel);
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
    private void drawFrontier(IRing frontier, int frontierId, DistanceFieldFrontierPixelRenderer pixelRenderer) {
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
            // special case not to set distance to zero in some cases (for outer sea limits which are not real coast lines
            if (segmentLength > this.getTexture().getMaxCoastlineLength()) {
                System.err.println(segmentLength + "segment removed");
            }
            pixelRenderer.setDistanceToZero(segmentLength < this.getTexture().getMaxCoastlineLength());
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

    private BufferedImage toBufferedImagePixelUVTile(TextureImage image, List<Pair<TileProbability, Tile>> tilesToBeApplied, SamplingAlgorithm sampler,
            Shape clippingShape) {

        image.invalidateUVBounds();
        TextureImageTileChooser tileChooser = new TextureImageTileChooser();
        for (Pair<TileProbability, Tile> pair : tilesToBeApplied) {
            tileChooser.addTile(pair.first(), pair.second());
        }
        BufferedImage bi = new BufferedImage(this.getTextureWidth(), this.getTextureHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, this.getTextureWidth(), this.getTextureHeight());
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //        if (clippingShape != null) {
        //            Shape screenSpaceShape = this.transform.createTransformedShape(clippingShape);
        //            g2.setClip(screenSpaceShape);
        //        }
        Iterator<Sample> sampleIterator = sampler.getSampleIterator();
        while (sampleIterator.hasNext()) {
            Sample sample = sampleIterator.next();
            double xTexture = sample.getLocation().getX();
            double yTexture = sample.getLocation().getY();
            Tile tile = sample.getTile() != null ? sample.getTile() : tileChooser.getTile(sample);
            if (tile == null) {
                continue;
            }
            BufferedImage texture = tile.getImage();
            Point2D screenPixelLocation = new Point2D.Double();
            this.transform.transform(new Point2D.Double(xTexture, yTexture), screenPixelLocation);

            //                        Point2D texturePixelLocation = new Point2D.Double();
            //                        this.transform.inverseTransform(new Point2D.Double(xScreen, yScreen), texturePixelLocation);
            //                        double xTexture = texturePixelLocation.getX();
            //                        double yTexture = texturePixelLocation.getY();
            TexturePixel pixel = image.getPixel((int) xTexture, (int) yTexture);
            if (pixel == null || !pixel.in || pixel.vGradient == null) {
                logger.warn("invalid pixel = " + pixel);
                continue;
            } else {
                //                        g2.setColor(Color.yellow);
                //                        g2.drawOval(xScreen - 2, yScreen - 2, 4, 4);
                //                        System.err.println("draw tile at " + (xScreen - texture.getWidth() / 2) + "x" + (yScreen - texture.getHeight() / 2));
                AffineTransform transform = new AffineTransform();
                transform.translate(screenPixelLocation.getX() - texture.getWidth() / 2, screenPixelLocation.getY() - texture.getHeight() / 2);
                transform.rotate(pixel.vGradient.x, pixel.vGradient.y, texture.getWidth() / 2, texture.getHeight() / 2);

                g2.drawImage(texture, transform, null);
                g2.fillRect(texture.getWidth() / 2, texture.getHeight() / 2, texture.getWidth(), texture.getHeight());

                // FIXME: v Texture attenuation is computed with a *3 factor just for fun 
                //                float opacity = (float) Math.max(0, 1 - (3 * pixel.vTexture / this.texImage.getvMax()));
                //                float opacity = 1f;
                //                if (opacity > 0.1) {
                //                    float[] scales = { 1f, 1f, 1f, opacity };
                //                    float[] offsets = new float[4];
                //                    RescaleOp rop = new RescaleOp(scales, offsets, null);
                //                    BufferedImage textureWithAlpha = rop.filter(texture, null);
                //                    g2.drawImage(textureWithAlpha, transform, null);
                //                    //                        g2.fillRect(xScreen - texture.getWidth() / 2, yScreen - texture.getHeight() / 2, texture.getWidth(), texture.getHeight());
                //                }
            }

        }
        return bi;
    }

    public void setFeatureCollection(IFeatureCollection<IFeature> iFeatureCollection) {
        this.featureCollection = iFeatureCollection;

    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    //    private BufferedImage toBufferedImagePixelUVTileGraphCut(TextureImage image, List<Pair<TileProbability, Tile>> tilesToBeApplied, SamplingAlgorithm sampler,
    //            Shape clippingShape) {
    //        image.invalidateUVBounds();
    //        BufferedImage bi = new BufferedImage(this.getTextureWidth(), this.getTextureHeight(), BufferedImage.TYPE_4BYTE_ABGR);
    //        Graphics2D g2 = (Graphics2D) bi.getGraphics();
    //        g2.setComposite(AlphaComposite.Clear);
    //        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
    //        g2.setComposite(AlphaComposite.SrcOver);
    //        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    //        //        g2.drawImage(((TextureImageSamplerTiler) sampler).getImageMask(), 0, 0, null);
    //        if (clippingShape != null) {
    //            Shape screenSpaceShape = this.transform.createTransformedShape(clippingShape);
    //            g2.setClip(screenSpaceShape);
    //        }
    //
    //        List<Pair<MinSourceSinkCut<PixelVertex, PixelEdge>, AffineTransform>> algos = new ArrayList<Pair<MinSourceSinkCut<PixelVertex, PixelEdge>, AffineTransform>>();
    //        GraphCut graphCut = new GraphCut(bi);
    //        graphCut.setClippingShape(this.transform.createTransformedShape(clippingShape));
    //        Iterator<Sample> sampleIterator = sampler.getSampleIterator();
    //        int count = 0;
    //        while (sampleIterator.hasNext()) {
    //            Sample sample = sampleIterator.next();
    //            double xTexture = sample.getLocation().getX();
    //            double yTexture = sample.getLocation().getY();
    //            Tile tile = sample.getTile();
    //            if (tile == null) {
    //                System.err.println("tiles must be precomputed by samplers for the graphcut algorithm");
    //                continue;
    //            }
    //            BufferedImage texture = tile.getImage();
    //            Point2D screenPixelLocation = new Point2D.Double();
    //            this.transform.transform(new Point2D.Double(xTexture, yTexture), screenPixelLocation);
    //
    //            TexturePixel pixel = image.getPixel((int) xTexture, (int) yTexture);
    //            if (pixel == null || !pixel.in || pixel.vGradient == null) {
    //                continue;
    //            } else {
    //                AffineTransform transform = new AffineTransform();
    //                transform.translate(screenPixelLocation.getX() - texture.getWidth() / 2, screenPixelLocation.getY() - texture.getHeight() / 2);
    //                transform.rotate(pixel.vGradient.x, pixel.vGradient.y, texture.getWidth() / 2, texture.getHeight() / 2);
    //                MinSourceSinkCut<PixelVertex, PixelEdge> algo = graphCut.pasteTile(tile, transform);
    //                algos.add(new Pair<MinSourceSinkCut<PixelVertex, PixelEdge>, AffineTransform>(algo, transform));
    //
    //                //                g2.setTransform(transform);
    //                //                g2.setColor(Color.red);
    //                //
    //                //                // draw the the graph cut edges (Debug)
    //                //                g2.setClip(null);
    //                //                g2.setStroke(new BasicStroke(1.f));
    //                //                for (PixelEdge e : algo.getCutEdges()) {
    //                //                    g2.drawLine(e.getSource().getX(), e.getSource().getY(), e.getTarget().getX(), e.getTarget().getY());
    //                //
    //                //                }
    //                //                try {
    //                //                    ImageIO.write(bi, "PNG", new File("graphcut" + count + ".png"));
    //                //                } catch (IOException e1) {
    //                //                    // TODO Auto-generated catch block
    //                //                    e1.printStackTrace();
    //                //                }
    //                count++;
    //            }
    //
    //        }
    //        //        this.displaySamples(sampler, (Graphics2D) bi.getGraphics());
    //        //        this.displayEdges(algos, (Graphics2D) bi.getGraphics());
    //        return bi;
    //    }

}
