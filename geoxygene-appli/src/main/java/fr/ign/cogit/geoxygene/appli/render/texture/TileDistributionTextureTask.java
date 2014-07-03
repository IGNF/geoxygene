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
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import fr.ign.cogit.geoxygene.appli.gl.GradientTextureImage;
import fr.ign.cogit.geoxygene.appli.gl.GradientTextureImage.GradientTextureImageParameters;
import fr.ign.cogit.geoxygene.appli.gl.GradientTextureImage.TexturePixel;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.texture.ProbabilistTileDescriptor;
import fr.ign.cogit.geoxygene.style.texture.TileDistributionTextureDescriptor;
import fr.ign.cogit.geoxygene.style.texture.TileDistributionTextureDescriptor.TileBlendingType;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;
import fr.ign.cogit.geoxygene.util.gl.Sample;
import fr.ign.cogit.geoxygene.util.gl.Tile;
import fr.ign.cogit.geoxygene.util.graphcut.DefaultTile;
import fr.ign.cogit.geoxygene.util.graphcut.GraphCut;

/**
 * @author JeT
 * 
 */
public class TileDistributionTextureTask extends
        AbstractTextureTask<BasicTexture> {

    private static final Logger logger = Logger
            .getLogger(TileDistributionTextureTask.class.getName()); // logger

    private final List<Pair<TileProbability, Tile>> tilesToBeApplied = new ArrayList<Pair<TileProbability, Tile>>();
    private GradientTextureImage texImage; //
    private IEnvelope envelope = null;
    private Shape featureShape = null; // shape corresponding to the given
                                       // feature in the image texture space
    // private final DistanceFieldTexture texture = null;
    // private double minX;
    // private double minY;
    // private double maxX;
    // private double maxY;
    private int textureWidth = -1; // final textured image width & height
    private int textureHeight = -1; // dimension are computed using resolution
                                    // and map scale
    private double printResolution = 72; // expressed in DPI
    private double mapScale = 1 / 100000.; // map scale
    private final List<IPolygon> polygons = new ArrayList<IPolygon>();
    private final List<IRing> rings = new ArrayList<IRing>();
    private final List<ParameterizedSegment> segments = new ArrayList<ParameterizedSegment>();
    // private DistanceFieldFrontierPixelRenderer pixelRenderer = null;
    // private final Set<Point> modifiedPixels = new HashSet<Point>();
    // private final AffineTransform transform = new AffineTransform();
    private IFeatureCollection<IFeature> featureCollection = null;
    private Viewport viewport = null;

    private final boolean memoryMonitoring = false;
    private long previousUsedMemory = 0;

    private BasicTexture basicTexture = null;

    private TileDistributionTextureDescriptor textureDescriptor = null;

    /**
     * @param textureDescriptor
     * @param featureCollection
     * @param viewport
     */
    public TileDistributionTextureTask(String name,
            TileDistributionTextureDescriptor textureDescriptor,
            IFeatureCollection<IFeature> featureCollection, Viewport viewport) {
        super("TileDistribution" + name);
        this.textureDescriptor = textureDescriptor;
        this.basicTexture = new BasicTexture();
        this.setFeatureCollection(featureCollection);
        this.setViewport(viewport);
    }

    /**
     * @return the textureDescriptor
     */
    public TileDistributionTextureDescriptor getTextureDescriptor() {
        return this.textureDescriptor;
    }

    /**
     * Initialize tiles (the texture image must be previously generated)
     * 
     * @throws IOException
     */
    private final void initTiles() throws IOException {
        for (ProbabilistTileDescriptor tileDesc : this.getTextureDescriptor()
                .getTiles()) {
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
        this.setPrintResolution(this.getTextureDescriptor()
                .getTextureResolution());

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
            // logger.debug(this.getName() + " texture width: envelope width = "
            // + this.getEnvelope().width() + " * scale = "
            // + this.getMapScale() + " resolution = "
            // + this.getPrintResolution() + " /  MperINCH  = "
            // + M_PER_INCH);
            this.textureWidth = (int) (this.getEnvelope().width()
                    * this.getMapScale() * this.getPrintResolution() / GradientTextureTask.M_PER_INCH);
            if (this.textureWidth <= 0) {
                logger.debug("texture width is invalid: envelope width = "
                        + this.getEnvelope().width() + " * scale = "
                        + this.getMapScale() + " resolution = "
                        + this.getPrintResolution() + " /  MperINCH  = "
                        + GradientTextureTask.M_PER_INCH);
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
                    * this.getMapScale() * this.getPrintResolution() / GradientTextureTask.M_PER_INCH);
            if (this.textureHeight <= 0) {
                logger.error("texture height is invalid: envelope height = "
                        + this.getEnvelope().height() + " * scale = "
                        + this.getMapScale() + " resolution = "
                        + this.getPrintResolution() + " /  MperINCH  = "
                        + GradientTextureTask.M_PER_INCH);
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
            this.envelope = this.featureCollection.getEnvelope();
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
            this.getTextureDescriptor().setxRepeat(false);
            this.getTextureDescriptor().setyRepeat(false);
            this.basicTexture.createTextureImage(this.getTextureWidth(),
                    this.getTextureHeight());

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
        double maxCoastLine = this.getTextureDescriptor()
                .getMaxCoastlineLength();
        GradientTextureImageParameters params = new GradientTextureImageParameters(
                this.getTextureWidth(), this.getTextureHeight(), this.polygons,
                this.getEnvelope(), maxCoastLine);
        this.texImage = GradientTextureImage
                .generateGradientTextureImage(params);
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
        if (this.getTextureDescriptor().getBlending() == TileBlendingType.GRAPHCUT) {
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
                if (this.getTextureDescriptor().getBlending() == TileBlendingType.GRAPHCUT) {
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

    @Override
    public BasicTexture getTexture() {
        return this.basicTexture;
    }

}
