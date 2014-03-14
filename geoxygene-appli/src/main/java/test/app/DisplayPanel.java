package test.app;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import utils.Pair;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.gl.DistanceFieldFrontierPixelRenderer;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.gl.TextureImage;
import fr.ign.cogit.geoxygene.util.gl.TextureImage.TexturePixel;
import fr.ign.cogit.geoxygene.util.gl.TextureImageUtil;
import fr.ign.util.graphcut.GraphCut;
import fr.ign.util.graphcut.MinSourceSinkCut;
import fr.ign.util.graphcut.PixelEdge;
import fr.ign.util.graphcut.PixelVertex;
import fr.ign.util.graphcut.Tile;

public class DisplayPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    public enum RepeatType {
        Repeat, None, Clamp, Mirror;

        public static Double repeat(double x, RepeatType repeat) {
            switch (repeat) {
            case None:
                return (x <= 0) ? 0 : (x >= 1) ? 1 : x;
            case Clamp:
                return (x < 0 || x > 1) ? null : x;
            case Repeat:
                return x % 1;
            case Mirror:
                return x < 0 ? -x % 1 : x % 1;
            }
            return null;
        }
    }

    public static class ParameterizedPoint {
        public double x, y; // point coordinates in world frame
        public double u, v; // texture coordinates

        public ParameterizedPoint(double x, double y, double u, double v) {
            super();
            this.x = x;
            this.y = y;
            this.u = u;
            this.v = v;
        }

    }

    public static class ParameterizedSegment {
        public ParameterizedPoint p1, p2;

        public ParameterizedSegment(ParameterizedPoint p1, ParameterizedPoint p2) {
            super();
            this.p1 = p1;
            this.p2 = p2;
        }

        public double getU(double t) {
            return this.p1.u * (1 - t) + this.p2.u * t;
        }

        public double getV(double t) {
            return this.p1.v * (1 - t) + this.p2.v * t;
        }

    }

    private static final Logger logger = Logger.getLogger(DisplayPanel.class.getName()); // logger

    private static final double PI2 = Math.PI * 2;
    private static Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
            "null");
    private IFeature feature = null;
    private IEnvelope envelope = null;
    private Shape featureShape = null; // shape corresponding to the given feature in the image texture space
    //    private final DistanceFieldTexture texture = null;
    private DistanceFieldApplication app = null;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;
    private double imageToPolygonFactorX;
    private double imageToPolygonFactorY;
    public TextureImage texImage; // the one that is visualized (with filters)
    public TextureImage initialTexImage; // texture before filter application 
    private final List<IPolygon> polygons = new ArrayList<IPolygon>();
    private final List<IRing> rings = new ArrayList<IRing>();
    private final List<ParameterizedSegment> segments = new ArrayList<ParameterizedSegment>();
    private DistanceFieldFrontierPixelRenderer pixelRenderer = null;
    private boolean frontierDrawn = false;
    private boolean insideDrawn = false;
    private boolean infinitePixelRemoved = false;
    private boolean texCoordFilled = false;
    private boolean vScaled = false;
    private boolean gradientComputed = false;
    private double gScale = 1.;
    private int stepCount = 0;
    private Set<Point> modifiedPixels = new HashSet<Point>();
    private AffineTransform pressedTransform = null;
    private AffineTransform transform = new AffineTransform();
    private int clickX = -1, clickY = -1;
    private String viz = "U";
    private String gradientViz = "None";
    private final BufferedImage textureToBeApplied = null;
    private final List<Pair<TileProbability, Tile>> tilesToBeApplied = new ArrayList<Pair<TileProbability, Tile>>();
    private RepeatType uRepeat = RepeatType.Repeat;
    private RepeatType vRepeat = RepeatType.Repeat;
    private BufferedImage bi = null;
    private boolean screenSpace = false;
    private boolean recomputeImage = true;
    private boolean drag = true;

    public boolean hasNoStepLeft = false;

    private Point2D currentSelectedPixel = null;
    private Point2D currentClosestPixel = null;

    public DisplayPanel(DistanceFieldApplication app) {
        this.app = app;
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addMouseListener(this);
        this.updateContent();
    }

    public void reset() {
        this.updateContent();
    }

    public void updateContent() {
        this.frontierDrawn = false;
        this.insideDrawn = false;
        this.infinitePixelRemoved = false;
        this.texCoordFilled = false;
        this.vScaled = false;
        this.stepCount = 0;
        this.gradientComputed = false;
        this.hasNoStepLeft = false;
        for (Layer layer : this.app.sld.getLayers()) {
            for (IFeature f : layer.getFeatureCollection()) {
                this.feature = f;
            }
        }
        if (this.feature == null) {
            return;
        }
        this.envelope = this.feature.getGeom().getEnvelope();
        this.minX = this.envelope.getLowerCorner().getX();
        this.minY = this.envelope.getLowerCorner().getY();
        this.maxX = this.envelope.getUpperCorner().getX();
        this.maxY = this.envelope.getUpperCorner().getY();
        final double imagesize = 1E6;
        double ratio = (this.maxY - this.minY) / (this.maxX - this.minX);
        int imageWidth = (int) (Math.sqrt(imagesize / ratio));
        int imageHeight = (int) (Math.sqrt(imagesize * ratio));
        this.texImage = new TextureImage(imageWidth, imageHeight);
        this.imageToPolygonFactorX = (this.maxX - this.minX) / (imageWidth - 1);
        this.imageToPolygonFactorY = (this.maxY - this.minY) / (imageHeight - 1);

        this.polygons.clear();
        // convert the multisurface as a collection of polygons
        for (IOrientableSurface surface : ((IMultiSurface<?>) this.feature.getGeom()).getList()) {
            if (surface instanceof IPolygon) {
                IPolygon polygon = (IPolygon) surface;
                this.polygons.add(polygon);
            } else {
                logger.error("Distance Field Parameterizer does handle multi surfaces containing only polygons, not " + surface.getClass().getSimpleName());
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
        this.pixelRenderer = null;
        this.modifiedPixels.clear();

        DistanceTileProbability closeProbability = new DistanceTileProbability(this.texImage, Double.NEGATIVE_INFINITY, 100, 1, 0);
        DistanceTileProbability mediumProbability = new DistanceTileProbability(this.texImage, 100, 200, 1, 0);
        DistanceTileProbability farmediumProbability = new DistanceTileProbability(this.texImage, 200, 250, 1, 0);
        DistanceTileProbability farProbability = new DistanceTileProbability(this.texImage, 200, 300, 0.7, 0);
        DistanceTileProbability allProbability = new DistanceTileProbability(this.texImage, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 1, 0);
        try {

            Tile tileTexture = Tile.read("/export/home/kandinsky/turbet/cassini samples/waves small.png");
            this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(closeProbability, tileTexture));
            //            tileTexture = Tile.read("/home/turbet/Documents/s2.png");
            //            this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(closeProbability, tileTexture));
            //            tileTexture = Tile.read("/home/turbet/Documents/s3.png");
            //            this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(closeProbability, tileTexture));

            tileTexture = Tile.read("/export/home/kandinsky/turbet/cassini samples/waves small.png");
            this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(mediumProbability, tileTexture));
            tileTexture = Tile.read("/export/home/kandinsky/turbet/cassini samples/waves big.png");
            this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(mediumProbability, tileTexture));
            //            tileTexture = Tile.read("/home/turbet/Documents/t3.png");
            //            this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(mediumProbability, tileTexture));

            tileTexture = Tile.read("/export/home/kandinsky/turbet/cassini samples/crest small.png");
            this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(farProbability, tileTexture));
            tileTexture = Tile.read("/export/home/kandinsky/turbet/cassini samples/crest big.png");
            this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(farProbability, tileTexture));
            //            tileTexture = Tile.read("/home/turbet/Documents/u3.png");
            //            this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(farProbability, tileTexture));

        } catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
        }

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
     * @return the texImage
     */
    public final TextureImage getTexImage() {
        return this.texImage;
    }

    public void invalidateImage() {
        this.recomputeImage = true;
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.white);
        GradientPaint redtowhite = new GradientPaint(0, 0, new Color(230, 230, 250), 0, this.getHeight(), Color.white);
        g2.setPaint(redtowhite);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        if (this.feature == null) {
            return;
        }
        if (this.recomputeImage) {
            this.screenSpace = false;
            this.bi = null;
            if (this.viz.equals("U HSV")) {
                this.bi = toBufferedImageU(this.texImage);
            } else if (this.viz.equals("UV HSV + light")) {
                this.bi = toBufferedImageUV(this.texImage);
            } else if (this.viz.equals("UV textured")) {
                this.bi = this.toBufferedImageTexturedUV(this.texImage, this.textureToBeApplied);
            } else if (this.viz.equals("UV pixel textured")) {
                this.bi = this.toBufferedImagePixelTexturedUV(this.texImage, this.textureToBeApplied);
                this.screenSpace = true;
            } else if (this.viz.equals("UV pixel tile")) {
                if (this.bi == null) {
                    int y = 0;
                    double min = Double.POSITIVE_INFINITY;
                    for (Pair<TileProbability, Tile> pairTileTexture : this.tilesToBeApplied) {
                        BufferedImage tileTexture = pairTileTexture.second().getImage();
                        if (tileTexture.getWidth() < min) {
                            min = tileTexture.getWidth();
                        }
                        if (tileTexture.getHeight() < min) {
                            min = tileTexture.getHeight();
                        }
                        g2.drawImage(tileTexture, null, 1, y + 1);
                        g2.setColor(Color.black);
                        g2.drawRect(0, y, tileTexture.getWidth() + 2, tileTexture.getHeight() + 2);
                        y += tileTexture.getHeight() + 2;
                    }
                    double minDistance = min / 1.5 / this.transform.getScaleX();
                    double minVDistance = min / 1.5 / this.transform.getScaleX();
                    //                System.err.println("minDistance = " + minDistance + " min V Distance = " + minVDistance);
                    TextureImageSamplerUVSampler sampler = new TextureImageSamplerUVSampler(this.texImage, minDistance, minVDistance);
                    //                System.err.println(sampler.getSamples().size() + " samples generated");
                    //                for (Sample sample : sampler.getSamples()) {
                    //                    System.err.println("sample " + sample);
                    //                }
                    //                double scale = this.transform.getScaleX();
                    //                double sampleX = 5;
                    //                double sampleY = 5;
                    //                TextureImageSamplerRegularGrid sampler = new TextureImageSamplerRegularGrid(this.texImage, sampleX, sampleY, scale);
                    //                sampler.setJitteringFactor(0.3);
                    this.bi = this.toBufferedImagePixelUVTile(this.texImage, this.tilesToBeApplied, sampler, this.featureShape);
                }
                this.screenSpace = false;

            } else if (this.viz.equals("MipMap pixel tile")) {
                int y = 0;
                double min = Double.POSITIVE_INFINITY;
                for (Pair<TileProbability, Tile> pairTileTexture : this.tilesToBeApplied) {
                    BufferedImage tileTexture = pairTileTexture.second().getImage();
                    if (tileTexture.getWidth() < min) {
                        min = tileTexture.getWidth();
                    }
                    if (tileTexture.getHeight() < min) {
                        min = tileTexture.getHeight();
                    }
                    g2.drawImage(tileTexture, null, 1, y + 1);
                    g2.setColor(Color.black);
                    g2.drawRect(0, y, tileTexture.getWidth() + 2, tileTexture.getHeight() + 2);
                    y += tileTexture.getHeight() + 2;
                }
                TextureImageTileChooser tileChooser = new TextureImageTileChooser();
                for (Pair<TileProbability, Tile> pair : this.tilesToBeApplied) {
                    tileChooser.addTile(pair.first(), pair.second());
                }
                System.err.println("compute sampler");
                TextureImageSamplerMipMap sampler = new TextureImageSamplerMipMap(this.texImage, tileChooser);
                System.err.println("display tiles");
                this.bi = this.toBufferedImagePixelUVTile(this.texImage, this.tilesToBeApplied, sampler, this.featureShape);
                //                this.bi = this.toBufferedImagePixelUVTileGraphCut(this.texImage, this.tilesToBeApplied, sampler, this.featureShape);

                this.screenSpace = true;

            } else if (this.viz.equals("GraphCut")) {
                int y = 0;
                double min = Double.POSITIVE_INFINITY;
                for (Pair<TileProbability, Tile> pairTileTexture : this.tilesToBeApplied) {
                    Tile tile = pairTileTexture.second();
                    BufferedImage tileTexture = tile.getImage();
                    if (tileTexture.getWidth() < min) {
                        min = tileTexture.getWidth();
                    }
                    if (tileTexture.getHeight() < min) {
                        min = tileTexture.getHeight();
                    }
                    g2.drawImage(tileTexture, null, 1, y + 1);
                    g2.setColor(Color.black);
                    g2.drawRect(0, y, tileTexture.getWidth() + 2, tileTexture.getHeight() + 2);
                    g2.drawImage(tile.getMask(), null, 3 + tileTexture.getWidth(), y + 1);
                    g2.setColor(Color.black);
                    g2.drawRect(2 + tileTexture.getWidth(), y, tile.getMask().getWidth() + 2, tile.getMask().getHeight() + 2);
                    y += tileTexture.getHeight() + 2;
                }
                //                System.err.println("minDistance = " + minDistance + " min V Distance = " + minVDistance);

                TextureImageTileChooser tileChooser = new TextureImageTileChooser();
                for (Pair<TileProbability, Tile> pair : this.tilesToBeApplied) {
                    tileChooser.addTile(pair.first(), pair.second());
                }

                TextureImageSamplerTiler sampler = new TextureImageSamplerTiler(this.texImage, tileChooser, 0.3, this.transform.getScaleX());
                this.bi = this.toBufferedImagePixelUVTileGraphCut(this.texImage, this.tilesToBeApplied, sampler, this.featureShape);

                this.screenSpace = true;

            } else if (this.viz.equals("Distance HSV")) {
                this.bi = toBufferedImageDistanceHSV(this.texImage);
            } else if (this.viz.equals("Distance Strip 10")) {
                this.bi = toBufferedImageDistanceStrip(this.texImage, 10);
            } else if (this.viz.equals("Distance Strip 50")) {
                this.bi = toBufferedImageDistanceStrip(this.texImage, 50);
            } else if (this.viz.equals("Distance Strip 200")) {
                this.bi = toBufferedImageDistanceStrip(this.texImage, 200);
            } else if (this.viz.equals("Distance Strip 500")) {
                this.bi = toBufferedImageDistanceStrip(this.texImage, 500);
            } else if (this.viz.equals("Distance WB")) {
                this.bi = toBufferedImageDistance(this.texImage, Color.white, Color.black);
            } else {
                this.bi = toBufferedImageDistance(this.texImage, Color.black, Color.white);

            }

            addModifiedPointsOnBufferedImage(this.bi, this.modifiedPixels);
            this.recomputeImage = false;
        }
        if (!this.screenSpace) {
            g2.setComposite(AlphaComposite.SrcOver);
            g2.drawImage(this.bi, this.transform, null);
        } else {
            g2.setComposite(AlphaComposite.SrcOver);
            g2.drawImage(this.bi, null, 0, 0);
        }

        if (this.gradientViz.equals("3x3")) {
            this.drawGradients(g2, 3);
        } else if (this.gradientViz.equals("5x5")) {
            this.drawGradients(g2, 5);
        } else if (this.gradientViz.equals("7x7")) {
            this.drawGradients(g2, 7);
        } else if (this.gradientViz.equals("10x10")) {
            this.drawGradients(g2, 10);
        } else if (this.gradientViz.equals("20x20")) {
            this.drawGradients(g2, 20);
        } else if (this.gradientViz.equals("30x30")) {
            this.drawGradients(g2, 30);
        } else if (this.gradientViz.equals("40x40")) {
            this.drawGradients(g2, 40);
        } else if (this.gradientViz.equals("50x50")) {
            this.drawGradients(g2, 50);
        } else if (this.gradientViz.equals("100x100")) {
            this.drawGradients(g2, 100);
        } else if (this.gradientViz.equals("200x200")) {
            this.drawGradients(g2, 200);
        }

        if (this.featureShape != null) {
            Shape screenSpaceShape = this.transform.createTransformedShape(this.featureShape);
            g2.setColor(Color.blue);
            g2.draw(screenSpaceShape);
        }

        if (this.currentSelectedPixel != null) {
            if (this.drag) {
                this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else {
                this.setCursor(blankCursor);
            }

            Point p1 = this.drawTexturePixel(g2, this.currentSelectedPixel);
            if (this.currentClosestPixel != null) {
                Point p2 = this.drawTexturePixel(g2, this.currentClosestPixel);
                g2.setColor(Color.yellow);
                g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        } else {
            this.setCursor(Cursor.getDefaultCursor());
        }

    }

    private void drawGradients(Graphics2D g2, int i) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC));
        g2.setColor(new Color(20, 200, 20, 100));
        for (int y = i / 2; y < this.getHeight(); y += i) {
            for (int x = i / 2; x < this.getWidth(); x += i) {
                Point2D pixelLocation = new Point2D.Double();
                try {
                    this.transform.inverseTransform(new Point2D.Double(x, y), pixelLocation);
                } catch (NoninvertibleTransformException e) {
                    e.printStackTrace();
                }
                int xTexture = (int) pixelLocation.getX();
                int yTexture = (int) pixelLocation.getY();
                TexturePixel pixel = this.texImage.getPixel(xTexture, yTexture);
                if (pixel == null || pixel.vGradient == null) {
                    continue;
                } else {
                    int x2 = (int) (x + pixel.vGradient.x * i * this.gScale);
                    int y2 = (int) (y + pixel.vGradient.y * i * this.gScale);
                    g2.drawLine(x, y, x2, y2);
                    if (i > 10) {
                        g2.drawOval(x2 - 1, y2 - 1, 3, 3);
                    }
                }
            }
        }

    }

    /**
     * @param g2
     */
    private Point drawTexturePixel(Graphics2D g2, Point2D texturePixel) {
        Point2D imageSelectedPixel = new Point2D.Double();
        Point2D imageSelectedPixel1 = new Point2D.Double();
        Point2D textureSelectedPixel = new Point2D.Double((int) texturePixel.getX(), (int) texturePixel.getY());
        Point2D textureSelectedPixel1 = new Point2D.Double((int) texturePixel.getX() + 1, (int) texturePixel.getY() + 1);
        this.transform.transform(textureSelectedPixel, imageSelectedPixel);
        this.transform.transform(textureSelectedPixel1, imageSelectedPixel1);
        this.setCursor(blankCursor);
        int x1 = (int) (imageSelectedPixel.getX() - 1);
        int y1 = (int) (imageSelectedPixel.getY() - 1);
        int x2 = (int) (imageSelectedPixel1.getX());
        int y2 = (int) (imageSelectedPixel1.getY());
        g2.setColor(Color.black);
        g2.drawRect(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
        g2.setColor(Color.white);
        g2.drawRect(x1 - 1, y1 - 1, x2 - x1 + 3, y2 - y1 + 3);
        return new Point((x1 + x2) / 2, (y1 + y2) / 2);
    }

    private static void addModifiedPointsOnBufferedImage(BufferedImage bi, Set<Point> modifiedPixels) {
        for (Point p : modifiedPixels) {
            //            int color = bi.getRGB(p.x, p.y);
            bi.setRGB(p.x, p.y, Color.blue.getRGB());
        }

    }

    //    private static BufferedImage toBufferedImageUVSum(TextureImage image) {
    //        image.invalidateUVBounds();
    //        double uMin = Double.MAX_VALUE;
    //        double uMax = -Double.MAX_VALUE;
    //        double vMin = Double.MAX_VALUE;
    //        double vMax = -Double.MAX_VALUE;
    //
    //        for (int y = 0; y < image.getHeight(); y++) {
    //            for (int x = 0; x < image.getWidth(); x++) {
    //                TexturePixel pixel = image.getPixel(x, y);
    //                if (pixel.in) {
    //                    double u = pixel.uTextureWeightSum < 1E-6 ? 0. : pixel.uTexture / pixel.uTextureWeightSum;
    //                    double v = pixel.vTextureWeightSum < 1E-6 ? 0. : pixel.vTexture / pixel.vTextureWeightSum;
    //                    if (u < uMin) {
    //                        uMin = u;
    //                    }
    //                    if (u > uMax) {
    //                        uMax = u;
    //                    }
    //                    if (v < vMin) {
    //                        vMin = v;
    //                    }
    //                    if (v > vMax) {
    //                        vMax = v;
    //                    }
    //                }
    //            }
    //        }
    //        BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
    //        for (int y = 0; y < image.getHeight(); y++) {
    //            for (int x = 0; x < image.getWidth(); x++) {
    //                TexturePixel pixel = image.getPixel(x, y);
    //                double u = pixel.uTextureWeightSum < 1E-6 ? 0. : pixel.uTexture / pixel.uTextureWeightSum;
    //                double v = pixel.vTextureWeightSum < 1E-6 ? 0. : pixel.vTexture / pixel.vTextureWeightSum;
    //                double u1 = (u - uMin) / (uMax - uMin);
    //                double v1 = (v - vMin) / (vMax - vMin);
    //                Color c = Color.getHSBColor((float) u1, 1f, 1f);
    //                if (!pixel.in) {
    //                    c = new Color(c.getRed() / 3, c.getGreen() / 3, c.getBlue() / 3);
    //                } else if (pixel.distance == 0) {
    //                    c = new Color((int) Math.min(c.getRed() * 1.2, 255), (int) Math.min(c.getGreen() * 1.2, 255), (int) Math.min(c.getBlue() * 1.2, 255));
    //                }
    //                bi.setRGB(x, y, c.getRGB());
    //            }
    //        }
    //        return bi;
    //    }

    private static BufferedImage toBufferedImageDistance(TextureImage image, Color c1, Color c2) {
        image.invalidateUVBounds();
        BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                if (pixel.closestFrontier != 0) {
                    bi.setRGB(x, y, Color.yellow.getRGB());
                } else if (pixel.distance == Double.POSITIVE_INFINITY || pixel.distance == Double.MAX_VALUE) {
                    bi.setRGB(x, y, Color.red.getRGB());
                } else if (!pixel.in) {
                    bi.setRGB(x, y, Color.black.getRGB());
                } else {
                    float v = (float) Math.max(0, Math.min(1, pixel.distance / image.getdMax()));
                    //                    System.err.println("v = " + v + " d = " + pixel.distance + " dMax = " + image.getdMax());
                    Color c = new Color(c1.getRed() / 255f * (1 - v) + v * c2.getRed() / 255f, c1.getGreen() / 255f * (1 - v) + v * c2.getGreen() / 255f,
                            c1.getBlue() / 255f * (1 - v) + v * c2.getBlue() / 255f);
                    bi.setRGB(x, y, c.getRGB());
                }
            }
        }
        return bi;
    }

    private static BufferedImage toBufferedImageDistanceHSV(TextureImage image) {
        image.invalidateUVBounds();
        BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                if (pixel.closestFrontier != 0) {
                    bi.setRGB(x, y, Color.yellow.getRGB());
                } else if (pixel.distance == Double.POSITIVE_INFINITY || pixel.distance == Double.MAX_VALUE) {
                    bi.setRGB(x, y, Color.red.getRGB());
                } else if (!pixel.in) {
                    bi.setRGB(x, y, Color.black.getRGB());
                } else {
                    float v = (float) Math.max(0, Math.min(1, pixel.distance / image.getdMax()));
                    Color c = Color.getHSBColor(v, 1f, 1f);
                    bi.setRGB(x, y, c.getRGB());
                }
            }
        }
        return bi;
    }

    private static BufferedImage toBufferedImageDistanceStrip(TextureImage image, int nbStrips) {
        image.invalidateUVBounds();
        BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                if (pixel.closestFrontier != 0) {
                    bi.setRGB(x, y, Color.yellow.getRGB());
                } else if (pixel.distance == Double.POSITIVE_INFINITY || pixel.distance == Double.MAX_VALUE) {
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

    private static BufferedImage toBufferedImageU(TextureImage image) {
        image.invalidateUVBounds();
        BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                if (!pixel.in) {
                    bi.setRGB(x, y, Color.black.getRGB());
                } else {
                    double u = (pixel.uTexture - image.getuMin()) / (image.getuMax() - image.getuMin());
                    Color c = Color.getHSBColor((float) u, 1f, 1f);
                    bi.setRGB(x, y, c.getRGB());
                }
            }
        }
        return bi;
    }

    private static BufferedImage toBufferedImageUV(TextureImage image) {
        image.invalidateUVBounds();
        BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                if (!pixel.in) {
                    bi.setRGB(x, y, Color.black.getRGB());
                } else {
                    double u = (pixel.uTexture - image.getuMin()) / (image.getuMax() - image.getuMin());
                    double v = (pixel.vTexture - image.getvMin()) / (image.getvMax() - image.getvMin());
                    Color c = Color.getHSBColor((float) u, 1f, (float) v);
                    bi.setRGB(x, y, c.getRGB());
                }
            }
        }
        return bi;
    }

    private BufferedImage toBufferedImageTexturedUV(TextureImage image, BufferedImage texture) {
        image.invalidateUVBounds();
        BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                if (!pixel.in) {
                    bi.setRGB(x, y, Color.black.getRGB());
                } else {
                    Double u = (pixel.uTexture - image.getuMin()) / (image.getuMax() - image.getuMin()) * this.app.getScaleU();
                    Double v = (pixel.vTexture - image.getvMin()) / (image.getvMax() - image.getvMin()) * this.app.getScaleV();
                    u = RepeatType.repeat(u, this.uRepeat);
                    v = RepeatType.repeat(v, this.vRepeat);
                    if (u == null || v == null) {
                        bi.setRGB(x, y, Color.black.getRGB());
                    } else {
                        int xTexture = Math.abs((int) (u * texture.getWidth()) % texture.getWidth());
                        int yTexture = Math.abs((int) ((1 - v) * texture.getHeight()) % texture.getHeight());
                        bi.setRGB(x, y, texture.getRGB(xTexture, yTexture));
                    }
                }
            }
        }
        return bi;
    }

    private BufferedImage toBufferedImagePixelUVTile(TextureImage image, List<Pair<TileProbability, Tile>> tilesToBeApplied, SamplingAlgorithm sampler,
            Shape clippingShape) {

        image.invalidateUVBounds();
        TextureImageTileChooser tileChooser = new TextureImageTileChooser();
        for (Pair<TileProbability, Tile> pair : tilesToBeApplied) {
            tileChooser.addTile(pair.first(), pair.second());
        }
        BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (clippingShape != null) {
            Shape screenSpaceShape = this.transform.createTransformedShape(clippingShape);
            g2.setClip(screenSpaceShape);
        }
        Iterator<Sample> sampleIterator = sampler.getSampleIterator();
        while (sampleIterator.hasNext()) {
            Sample sample = sampleIterator.next();
            double xTexture = sample.getLocation().x;
            double yTexture = sample.getLocation().y;
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
                continue;
            } else {
                //                        g2.setColor(Color.yellow);
                //                        g2.drawOval(xScreen - 2, yScreen - 2, 4, 4);
                //                        System.err.println("draw tile at " + (xScreen - texture.getWidth() / 2) + "x" + (yScreen - texture.getHeight() / 2));
                AffineTransform transform = new AffineTransform();
                transform.translate(screenPixelLocation.getX() - texture.getWidth() / 2, screenPixelLocation.getY() - texture.getHeight() / 2);
                transform.rotate(pixel.vGradient.x, pixel.vGradient.y, texture.getWidth() / 2, texture.getHeight() / 2);

                // FIXME: v Texture attenuation is computed with a *3 factor just for fun 
                //                float opacity = (float) Math.max(0, 1 - (3 * pixel.vTexture / this.texImage.getvMax()));
                float opacity = 1f;
                if (opacity > 0.1) {
                    float[] scales = { 1f, 1f, 1f, opacity };
                    float[] offsets = new float[4];
                    RescaleOp rop = new RescaleOp(scales, offsets, null);
                    BufferedImage textureWithAlpha = rop.filter(texture, null);
                    g2.drawImage(textureWithAlpha, transform, null);
                    //                        g2.fillRect(xScreen - texture.getWidth() / 2, yScreen - texture.getHeight() / 2, texture.getWidth(), texture.getHeight());
                }
            }

        }
        this.displaySamples(sampler, g2);
        return bi;
    }

    private BufferedImage toBufferedImagePixelUVTileGraphCut(TextureImage image, List<Pair<TileProbability, Tile>> tilesToBeApplied, SamplingAlgorithm sampler,
            Shape clippingShape) {
        image.invalidateUVBounds();
        BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //        g2.drawImage(((TextureImageSamplerTiler) sampler).getImageMask(), 0, 0, null);
        if (clippingShape != null) {
            Shape screenSpaceShape = this.transform.createTransformedShape(clippingShape);
            g2.setClip(screenSpaceShape);
        }

        List<Pair<MinSourceSinkCut<PixelVertex, PixelEdge>, AffineTransform>> algos = new ArrayList<Pair<MinSourceSinkCut<PixelVertex, PixelEdge>, AffineTransform>>();
        GraphCut graphCut = new GraphCut(bi);
        graphCut.setClippingShape(this.transform.createTransformedShape(clippingShape));
        Iterator<Sample> sampleIterator = sampler.getSampleIterator();
        int count = 0;
        while (sampleIterator.hasNext()) {
            Sample sample = sampleIterator.next();
            double xTexture = sample.getLocation().x;
            double yTexture = sample.getLocation().y;
            Tile tile = sample.getTile();
            if (tile == null) {
                System.err.println("tiles must be precomputed by samplers for the graphcut algorithm");
                continue;
            }
            BufferedImage texture = tile.getImage();
            Point2D screenPixelLocation = new Point2D.Double();
            this.transform.transform(new Point2D.Double(xTexture, yTexture), screenPixelLocation);

            TexturePixel pixel = image.getPixel((int) xTexture, (int) yTexture);
            if (pixel == null || !pixel.in || pixel.vGradient == null) {
                continue;
            } else {
                AffineTransform transform = new AffineTransform();
                transform.translate(screenPixelLocation.getX() - texture.getWidth() / 2, screenPixelLocation.getY() - texture.getHeight() / 2);
                transform.rotate(pixel.vGradient.x, pixel.vGradient.y, texture.getWidth() / 2, texture.getHeight() / 2);
                MinSourceSinkCut<PixelVertex, PixelEdge> algo = graphCut.pasteTile(tile, transform);
                algos.add(new Pair<MinSourceSinkCut<PixelVertex, PixelEdge>, AffineTransform>(algo, transform));

                //                g2.setTransform(transform);
                //                g2.setColor(Color.red);
                //
                //                // draw the the graph cut edges (Debug)
                //                g2.setClip(null);
                //                g2.setStroke(new BasicStroke(1.f));
                //                for (PixelEdge e : algo.getCutEdges()) {
                //                    g2.drawLine(e.getSource().getX(), e.getSource().getY(), e.getTarget().getX(), e.getTarget().getY());
                //
                //                }
                //                try {
                //                    ImageIO.write(bi, "PNG", new File("graphcut" + count + ".png"));
                //                } catch (IOException e1) {
                //                    // TODO Auto-generated catch block
                //                    e1.printStackTrace();
                //                }
                count++;
            }

        }
        //        this.displaySamples(sampler, (Graphics2D) bi.getGraphics());
        //        this.displayEdges(algos, (Graphics2D) bi.getGraphics());
        return bi;
    }

    private void displayEdges(List<Pair<MinSourceSinkCut<PixelVertex, PixelEdge>, AffineTransform>> algos, Graphics2D g2) {
        Random rand = new Random(0);
        for (Pair<MinSourceSinkCut<PixelVertex, PixelEdge>, AffineTransform> pair : algos) {
            AffineTransform transform = pair.second();
            MinSourceSinkCut<PixelVertex, PixelEdge> algo = pair.first();
            g2.setTransform(transform);
            g2.setColor(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));

            // draw the the graph cut edges (Debug)
            g2.setClip(null);
            g2.setStroke(new BasicStroke(1.f));
            for (PixelEdge e : algo.getCutEdges()) {
                g2.drawLine(e.getSource().getX(), e.getSource().getY(), e.getTarget().getX(), e.getTarget().getY());

            }

        }
    }

    /**
     * @param sampler
     * @param g2
     */
    private void displaySamples(SamplingAlgorithm sampler, Graphics2D g2) {
        Iterator<Sample> sampleIterator;
        // show Samples
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(Color.red);
        g2.setTransform(new AffineTransform());
        sampleIterator = sampler.getSampleIterator();
        while (sampleIterator.hasNext()) {
            Sample sample = sampleIterator.next();
            double xTexture = sample.getLocation().x;
            double yTexture = sample.getLocation().y;
            Point2D screenPixelLocation = new Point2D.Double();
            this.transform.transform(new Point2D.Double(xTexture, yTexture), screenPixelLocation);
            //                        Point2D texturePixelLocation = new Point2D.Double();
            //                        this.transform.inverseTransform(new Point2D.Double(xScreen, yScreen), texturePixelLocation);
            //                        double xTexture = texturePixelLocation.getX();
            //                        double yTexture = texturePixelLocation.getY();
            g2.drawRect((int) screenPixelLocation.getX() - 1, (int) screenPixelLocation.getY() - 1, 3, 3);
        }
    }

    //    /**
    //     * Choose the right texture depending on proba functions
    //     * 
    //     * @param xTexture
    //     * @param yTexture
    //     * @param rand
    //     * @param tilesToBeApplied
    //     * @return
    //     */
    //    private BufferedImage chooseTextureToApply(double xTexture, double yTexture, Random rand, List<Pair<TileProbability, BufferedImage>> tilesToBeApplied) {
    //        double sumProbability = 0;
    //        double[] sumProbabilities = new double[tilesToBeApplied.size()];
    //        for (int n = 0; n < tilesToBeApplied.size(); n++) {
    //            Pair<TileProbability, BufferedImage> pair = tilesToBeApplied.get(n);
    //            sumProbability += pair.first().getProbability(xTexture, yTexture);
    //            sumProbabilities[n] = sumProbability;
    //        }
    //        if (sumProbability < 1E-6) {
    //            return null;
    //        }
    //        double randomValue = rand.nextDouble() * sumProbability;
    //        int n = 0;
    //        while (n < tilesToBeApplied.size()) {
    //            if (randomValue < sumProbabilities[n]) {
    //                //                System.err.println("probabilities: " + Arrays.toString(sumProbabilities) + " random value = " + randomValue + " => index = " + n + "["
    //                //                        + sumProbabilities[n] + "]");
    //                return tilesToBeApplied.get(n).second();
    //            }
    //            n++;
    //        }
    //        throw new IllegalStateException("impossible case random value = " + randomValue + " max Value = " + sumProbabilities[sumProbabilities.length - 1]);
    //    }

    private BufferedImage toBufferedImagePixelUVTileScreenSpace(TextureImage image, BufferedImage texture, SamplingAlgorithm sampling, Shape clippingShape) {

        image.invalidateUVBounds();
        int xSampling = texture.getWidth() / 2;
        int ySampling = texture.getHeight() / 2;
        BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
        g2.setComposite(AlphaComposite.SrcOver);
        if (clippingShape != null) {
            Shape screenSpaceShape = this.transform.createTransformedShape(clippingShape);
            g2.setClip(screenSpaceShape);
        }
        try {
            for (int yScreen = 0; yScreen < this.getHeight(); yScreen += ySampling) {
                for (int xScreen = 0; xScreen < this.getWidth(); xScreen += xSampling) {
                    Point2D pixelLocation = new Point2D.Double();
                    this.transform.inverseTransform(new Point2D.Double(xScreen, yScreen), pixelLocation);
                    double x = pixelLocation.getX();
                    double y = pixelLocation.getY();
                    TexturePixel pixel = image.getPixel((int) x, (int) y);
                    if (pixel == null || !pixel.in) {
                        continue;
                    } else {
                        //                        g2.setColor(Color.yellow);
                        //                        g2.drawOval(xScreen - 2, yScreen - 2, 4, 4);
                        //                        System.err.println("draw tile at " + (xScreen - texture.getWidth() / 2) + "x" + (yScreen - texture.getHeight() / 2));
                        AffineTransform transform = new AffineTransform();
                        transform.translate(xScreen - texture.getWidth() / 2, yScreen - texture.getHeight() / 2);
                        transform.rotate(pixel.vGradient.x, pixel.vGradient.y, texture.getWidth() / 2, texture.getHeight() / 2);
                        g2.drawImage(texture, transform, null);
                        //                        g2.fillRect(xScreen - texture.getWidth() / 2, yScreen - texture.getHeight() / 2, texture.getWidth(), texture.getHeight());
                    }

                }
            }
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        return bi;
    }

    private BufferedImage softenEdgeTexture(BufferedImage sharpEdgeTexture) {
        double pow = 8;
        BufferedImage softEdgeTexture = new BufferedImage(sharpEdgeTexture.getWidth(), sharpEdgeTexture.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) softEdgeTexture.getGraphics();
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, softEdgeTexture.getWidth(), softEdgeTexture.getHeight());
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int y = 0; y < sharpEdgeTexture.getHeight(); y++) {
            for (int x = 0; x < sharpEdgeTexture.getWidth(); x++) {
                float[] rgba = getRGBA(sharpEdgeTexture.getRGB(x, y));
                double ax = (2 * x / (double) sharpEdgeTexture.getWidth() - 1);
                double ay = (2 * y / (double) sharpEdgeTexture.getHeight() - 1);
                double a = 1 - Math.pow(Math.pow(ax, pow) + Math.pow(ay, pow), 1 / pow);
                a = (a < 0) ? 0 : (a > 1) ? 1 : a;
                rgba[3] *= (float) a;
                softEdgeTexture.setRGB(x, y, getInt(rgba));
            }
        }
        return softEdgeTexture;
    }

    private BufferedImage softenLREdgeTexture(BufferedImage sharpEdgeTexture) {
        double pow = 8;
        BufferedImage softEdgeTexture = new BufferedImage(sharpEdgeTexture.getWidth(), sharpEdgeTexture.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) softEdgeTexture.getGraphics();
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, softEdgeTexture.getWidth(), softEdgeTexture.getHeight());
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int y = 0; y < sharpEdgeTexture.getHeight(); y++) {
            for (int x = 0; x < sharpEdgeTexture.getWidth(); x++) {
                float[] rgba = getRGBA(sharpEdgeTexture.getRGB(x, y));
                double ax = (2 * x / (double) sharpEdgeTexture.getWidth() - 1);
                double ay = (2 * y / (double) sharpEdgeTexture.getHeight() - 1);
                double a = 1 - Math.pow(ax, pow);
                a = (a < 0) ? 0 : (a > 1) ? 1 : a;
                rgba[3] *= (float) a;
                softEdgeTexture.setRGB(x, y, getInt(rgba));
            }
        }
        return softEdgeTexture;
    }

    private static float[] getRGBA(int elem) {
        float[] f = new float[4];
        f[0] = (elem & 0xFF) / 255.f;
        f[1] = ((elem >> 8) & 0xFF) / 255.f;
        f[2] = ((elem >> 16) & 0xFF) / 255.f;
        f[3] = ((elem >> 24) & 0xFF) / 255.f;
        return f;
    }

    private static int getInt(float[] f) {
        int r = (int) Math.min(255, Math.max(0, f[0] * 255));
        int g = (int) Math.min(255, Math.max(0, f[1] * 255));
        int b = (int) Math.min(255, Math.max(0, f[2] * 255));
        int a = (int) Math.min(255, Math.max(0, f[3] * 255));
        return r + (g << 8) + (b << 16) + (a << 24);
    }

    /**
     * @param srcDataBuffer
     */
    private String getDataType(DataBuffer srcDataBuffer) {
        switch (srcDataBuffer.getDataType()) {
        case DataBuffer.TYPE_BYTE:
            return "BYTE";
        case DataBuffer.TYPE_DOUBLE:
            return "DOUBLE";
        case DataBuffer.TYPE_FLOAT:
            return "FLOAT";
        case DataBuffer.TYPE_INT:
            return "INT";
        case DataBuffer.TYPE_SHORT:
            return "SHORT";
        case DataBuffer.TYPE_UNDEFINED:
            return "UNDEFINED";
        case DataBuffer.TYPE_USHORT:
            return "UNSIGNED SHORT";
        }
        return "unknown type";
    }

    private BufferedImage toBufferedImagePixelTexturedUV(TextureImage image, BufferedImage texture) {
        image.invalidateUVBounds();
        BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);

        try {
            for (int yScreen = 0; yScreen < this.getHeight(); yScreen++) {
                for (int xScreen = 0; xScreen < this.getWidth(); xScreen++) {
                    Point2D pixelLocation = new Point2D.Double();
                    this.transform.inverseTransform(new Point2D.Double(xScreen, yScreen), pixelLocation);
                    double x = pixelLocation.getX();
                    double y = pixelLocation.getY();
                    double xFrac = x - (int) x;
                    double yFrac = y - (int) y;
                    int dx = 0;
                    int dy = 0;
                    if (xFrac > 0.5 + 1E-6) {
                        dx = +1;
                    } else {
                        dx = -1;
                    }
                    if (yFrac > 0.5 + 1E-6) {
                        dy = +1;
                    } else {
                        dy = -1;
                    }

                    Point2d uv = this.getTextureCoordinates(x, y, image);
                    Point2d uvx = this.getTextureCoordinates(x + dx, y, image);
                    Point2d uvy = this.getTextureCoordinates(x, y + dy, image);
                    Point2d uvxy = this.getTextureCoordinates(x + dx, y + dy, image);
                    if (uv == null) {
                        continue;
                    }
                    Double u = uv.x;
                    Double v = uv.y;
                    if (uvx != null && uvy != null && uvxy != null) {
                        double lx = dx * (xFrac - 0.5);
                        double ly = dy * (yFrac - 0.5);
                        double a = (1 - lx) * (1 - ly);
                        double ax = (1 - ly) * lx;
                        double ay = (1 - lx) * ly;
                        double axy = lx * ly;

                        if (lx < 0 || ly < 0 || lx > 0.5 || ly > 0.5) {
                            System.err.println("lx, ly out of range");
                        }
                        u = (uv.x * a + uvx.x * ax + uvy.x * ay + uvxy.x * axy);
                        v = (uv.y * a + uvx.y * ax + uvy.y * ay + uvxy.y * axy);
                    }
                    u = RepeatType.repeat(u, this.uRepeat);
                    v = RepeatType.repeat(v, this.vRepeat);
                    if (u == null || v == null) {
                        bi.setRGB(xScreen, yScreen, Color.black.getRGB());
                    } else {
                        int xTexture = Math.abs((int) (u * texture.getWidth()) % texture.getWidth());
                        int yTexture = Math.abs((int) ((1 - v) * texture.getHeight()) % texture.getHeight());

                        bi.setRGB(xScreen, yScreen, texture.getRGB(xTexture, yTexture));
                    }
                }
            }
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        return bi;
    }

    private Point2d getTextureCoordinates(double x, double y, TextureImage image) {
        TexturePixel pixel = image.getPixel((int) x, (int) y);
        if (pixel == null || !pixel.in) {
            return null;
        }
        double u = (pixel.uTexture - image.getuMin()) / (image.getuMax() - image.getuMin()) * this.app.getScaleU();
        double v = (pixel.vTexture - image.getvMin()) / (image.getvMax() - image.getvMin()) * this.app.getScaleV();
        return new Point2d(u, v);

    }

    /**
     * @param image
     * @param texture
     * @param bi
     * @param x
     * @param y
     * @return
     */
    private Color getTextureColor(TextureImage image, BufferedImage texture, double x, double y) {
        TexturePixel pixel = image.getPixel((int) x, (int) y);
        if (pixel == null || !pixel.in) {
            return Color.black;
        } else {
            Double u = (pixel.uTexture - image.getuMin()) / (image.getuMax() - image.getuMin()) * this.app.getScaleU();
            Double v = (pixel.vTexture - image.getvMin()) / (image.getvMax() - image.getvMin()) * this.app.getScaleV();
            u = RepeatType.repeat(u, this.uRepeat);
            v = RepeatType.repeat(v, this.vRepeat);
            if (u == null || v == null) {
                return Color.black;
            } else {
                int xTexture = Math.abs((int) (u * texture.getWidth()) % texture.getWidth());
                int yTexture = Math.abs((int) ((1 - v) * texture.getHeight()) % texture.getHeight());
                return new Color(texture.getRGB(xTexture, yTexture));
            }
        }
    }

    private static BufferedImage toBufferedImageUDistance(TextureImage image) {
        image.invalidateUVBounds();
        BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                if (pixel.closestFrontier != 0) {
                    bi.setRGB(x, y, Color.yellow.getRGB());
                } else if (pixel.distance == Double.POSITIVE_INFINITY || pixel.distance == Double.MAX_VALUE) {
                    bi.setRGB(x, y, Color.red.getRGB());
                } else if (!pixel.in) {
                    bi.setRGB(x, y, Color.black.getRGB());
                } else {
                    double u = (pixel.uTexture - image.getuMin()) / (image.getuMax() - image.getuMin());
                    Color c = Color.getHSBColor((float) u, 1f, 1f);
                    bi.setRGB(x, y, c.getRGB());
                }
            }
        }
        return bi;
    }

    private void drawGeometry(Graphics2D g2, IGeometry geom) {
        g2.setColor(Color.black);
        IDirectPosition previous = null;
        for (IDirectPosition p : geom.coord()) {
            if (previous != null) {
                Point2d p1 = this.toScreen(previous);
                Point2d p2 = this.toScreen(p);
                g2.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
            }
            previous = p;
        }
    }

    private Point2d toScreen(IDirectPosition p) {
        return new Point2d(this.getWidth() * (p.getX() - this.envelope.minX()) / (this.envelope.maxX() - this.envelope.minX()), this.getHeight()
                * (p.getY() - this.envelope.minY()) / (this.envelope.maxY() - this.envelope.minY()));
    }

    public String doStepShrink4() {
        this.invalidateImage();
        if (!this.frontierDrawn) {
            this.pixelRenderer = new DistanceFieldFrontierPixelRenderer();

            for (IPolygon polygon : this.polygons) {
                // draw the outer frontier
                this.drawFrontier(polygon.getExterior(), 1, this.pixelRenderer);

                // draw all inner frontiers
                for (int innerFrontierIndex = 0; innerFrontierIndex < polygon.getInterior().size(); innerFrontierIndex++) {
                    IRing innerFrontier = polygon.getInterior().get(innerFrontierIndex);
                    this.drawFrontier(innerFrontier, -innerFrontierIndex - 1, this.pixelRenderer);
                }
            }
            this.frontierDrawn = true;
            return "exterior and interior frontiers drawn";
        }
        // fills the inner pixels
        if (this.insideDrawn == false) {
            this.fillHorizontally(this.pixelRenderer.getYs());
            this.insideDrawn = true;
            this.modifiedPixels.clear();
            return "horizontally filled";
        }

        if (this.infinitePixelRemoved == false) {
            this.modifiedPixels = this.getModifiedPixelsButInfiniteDistancePixels(this.pixelRenderer.getModifiedPixels());
            this.infinitePixelRemoved = true;
            return "inifinite pixels removed";
        }
        //        Set<Point> nonInfiniteModifiedPixels = pixelRenderer.getModifiedPixels();
        if (this.texCoordFilled == false) {
            if (!this.modifiedPixels.isEmpty()) {
                this.modifiedPixels = fillTextureCoordinates4(this.texImage, this.modifiedPixels, this.imageToPolygonFactorX, this.imageToPolygonFactorY);
                this.stepCount++;
                return "texture coordinates step " + this.stepCount + " next step contains " + this.modifiedPixels.size() + " pixels to treat";
            } else {
                this.texCoordFilled = true;
                return "texture coordinates done";
            }
        }
        if (this.vScaled == false) {
            scaleV(this.texImage, this.texImage.getdMax());
            this.vScaled = true;
            return "v scaling done";
        }
        if (this.gradientComputed == false) {
            this.computeGradient();
            this.gradientComputed = true;
            return "v coordinates gradient computed";
        }

        this.hasNoStepLeft = true;
        return "process finished";
    }

    /**
     * Shrink4 exact distance
     */
    public String doStepShrink4ExactDistance() {
        this.invalidateImage();
        if (!this.frontierDrawn) {
            this.pixelRenderer = new DistanceFieldFrontierPixelRenderer();

            //            stocker la frontiere la plus proche (segment ??)

            for (IPolygon polygon : this.polygons) {
                // draw the outer frontier
                this.drawFrontier(polygon.getExterior(), 1, this.pixelRenderer);

                // draw all inner frontiers
                for (int innerFrontierIndex = 0; innerFrontierIndex < polygon.getInterior().size(); innerFrontierIndex++) {
                    IRing innerFrontier = polygon.getInterior().get(innerFrontierIndex);
                    this.drawFrontier(innerFrontier, -innerFrontierIndex - 1, this.pixelRenderer);
                }
            }
            this.frontierDrawn = true;
            return "exterior and interior frontiers drawn";
        }
        // fills the inner pixels
        if (this.insideDrawn == false) {
            this.fillHorizontally(this.pixelRenderer.getYs());
            this.insideDrawn = true;
            this.modifiedPixels.clear();
            return "horizontally filled";
        }

        if (this.infinitePixelRemoved == false) {
            this.modifiedPixels = this.getModifiedPixelsButInfiniteDistancePixels(this.pixelRenderer.getModifiedPixels());
            this.infinitePixelRemoved = true;
            return "inifinite pixels removed";
        }
        //        Set<Point> nonInfiniteModifiedPixels = pixelRenderer.getModifiedPixels();
        if (this.texCoordFilled == false) {
            if (!this.modifiedPixels.isEmpty()) {
                this.modifiedPixels = this.fillTextureCoordinates4ExactDistance(this.texImage, this.modifiedPixels);
                this.stepCount++;
                return "texture coordinates step " + this.stepCount + " next step contains " + this.modifiedPixels.size() + " pixels to treat";
            } else {
                this.texCoordFilled = true;
                return "texture coordinates done";
            }
        }
        if (this.vScaled == false) {
            scaleV(this.texImage, this.texImage.getdMax());
            this.vScaled = true;
            return "v scaling done";
        }
        if (this.gradientComputed == false) {
            this.computeGradient();
            this.gradientComputed = true;
            return "v coordinates gradient computed";
        }
        this.hasNoStepLeft = true;
        return "process finished";
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

    private static Point2d compute5VGradient(TextureImage image, int x, int y) {
        final int windowDimension = 5;
        double[][] xSobelWeight = { { 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 1 }, { 0, 0, 0, 0, 0 }, { -1, -1, -1, -1, -1 }, { -1, -1, -1, -1, -1 } };
        double[][] ySobelWeight = { { 1, 1, 0, -1, -1 }, { 1, 1, 0, -1, -1 }, { 1, 1, 0, -1, -1 }, { 1, 1, 0, -1, -1 }, { 1, 1, 0, -1, -1 } };
        double xSumWeight = 0;
        double ySumWeight = 0;
        double xGradient = 0;
        double yGradient = 0;
        boolean edge = false;
        for (int wy = 0; wy < windowDimension; wy++) {
            for (int wx = 0; wx < windowDimension; wx++) {
                TexturePixel wPixel = image.getPixel(x + wx - windowDimension, y + wy - windowDimension);
                if (wPixel == null || wPixel.in == false) {
                    edge = true;
                    continue;
                }
                xSumWeight += xSobelWeight[wx][wy];
                ySumWeight += ySobelWeight[wx][wy];
                xGradient += xSobelWeight[wx][wy] * wPixel.vTexture;
                yGradient += ySobelWeight[wx][wy] * wPixel.vTexture;

            }

        }
        //        if (edge) {
        //            TexturePixel pixel = image.getPixel(x, y);
        //            return new Point2d(x - pixel.closestPoint.x, y - pixel.closestPoint.y);
        //
        //        }
        if (xSumWeight != 0) {
            xGradient /= xSumWeight;
        }
        if (ySumWeight != 0) {
            yGradient /= ySumWeight;
        }
        return new Point2d(-yGradient, xGradient);
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

    public String doStepShrink8() {
        this.invalidateImage();
        if (!this.frontierDrawn) {
            this.pixelRenderer = new DistanceFieldFrontierPixelRenderer();

            for (IPolygon polygon : this.polygons) {
                // draw the outer frontier
                this.drawFrontier(polygon.getExterior(), 1, this.pixelRenderer);

                // draw all inner frontiers
                for (int innerFrontierIndex = 0; innerFrontierIndex < polygon.getInterior().size(); innerFrontierIndex++) {
                    IRing innerFrontier = polygon.getInterior().get(innerFrontierIndex);
                    this.drawFrontier(innerFrontier, -innerFrontierIndex - 1, this.pixelRenderer);
                }
            }
            this.frontierDrawn = true;
            return "exterior and interior frontiers drawn";
        }
        // fills the inner pixels
        if (this.insideDrawn == false) {
            this.fillHorizontally(this.pixelRenderer.getYs());
            this.insideDrawn = true;
            this.modifiedPixels.clear();
            return "horizontally filled";
        }

        if (this.infinitePixelRemoved == false) {
            this.modifiedPixels = this.getModifiedPixelsButInfiniteDistancePixels(this.pixelRenderer.getModifiedPixels());
            this.infinitePixelRemoved = true;
            return "inifinite pixels removed";
        }
        //        Set<Point> nonInfiniteModifiedPixels = pixelRenderer.getModifiedPixels();
        if (this.texCoordFilled == false) {
            if (!this.modifiedPixels.isEmpty()) {
                this.modifiedPixels = fillTextureCoordinates8(this.texImage, this.modifiedPixels, this.imageToPolygonFactorX, this.imageToPolygonFactorY);
                this.stepCount++;
                return "texture coordinates step " + this.stepCount + " next step contains " + this.modifiedPixels.size() + " pixels to treat";
            } else {
                this.texCoordFilled = true;
                return "texture coordinates done";
            }
        }
        if (this.vScaled == false) {
            scaleV(this.texImage, this.texImage.getdMax());
            this.vScaled = true;
            return "v scaling done";
        }
        if (this.gradientComputed == false) {
            this.computeGradient();
            this.gradientComputed = true;
            return "v coordinates gradient computed";
        }
        this.hasNoStepLeft = true;
        return "process finished";
    }

    /**
     * Modify the neighbors pixel distance according to the current pixel
     * distance
     * 
     * @param set
     */
    private Set<Point> fillTextureCoordinates4ExactDistance(TextureImage image, Set<Point> set) {
        //            System.err.println(modifiedPixels.size() + " modified pixels");
        HashSet<Point> newlyModifiedPixels = new HashSet<Point>();
        for (Point p : set) {
            TexturePixel pixel = image.getPixel(p.x, p.y);
            if (pixel == null) {
                throw new IllegalStateException("modified pixels cannot be outside image ... " + p.x + "x" + p.y);
            }
            double currentDistance = pixel.distance;
            if (currentDistance < 1E-6) {
                pixel.closestPoint = new Point2d(p.x, p.y);
            }
            boolean w = this.fillTextureCoordinatesExactDistance(image, new Point(p.x - 1, p.y), pixel.closestPoint, newlyModifiedPixels);
            boolean e = this.fillTextureCoordinatesExactDistance(image, new Point(p.x + 1, p.y), pixel.closestPoint, newlyModifiedPixels);
            boolean n = this.fillTextureCoordinatesExactDistance(image, new Point(p.x, p.y - 1), pixel.closestPoint, newlyModifiedPixels);
            boolean s = this.fillTextureCoordinatesExactDistance(image, new Point(p.x, p.y + 1), pixel.closestPoint, newlyModifiedPixels);
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
    private boolean fillTextureCoordinatesExactDistance(TextureImage image, Point p, Point2d closestPoint, HashSet<Point> newlyModifiedPixels) {
        TexturePixel pixel = this.texImage.getPixel(p.x, p.y);
        if (pixel == null) {
            return false;
        }
        double d = distance(closestPoint, new Point2d(p.x, p.y));
        TexturePixel closestPixel = this.texImage.getPixel((int) closestPoint.x, (int) closestPoint.y);
        if (pixel.in && pixel.distance > d) {
            pixel.distance = d;
            pixel.uTexture = closestPixel.uTexture;
            pixel.closestPoint = closestPoint;
            pixel.mainDirection = closestPixel.mainDirection;
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
    private static Set<Point> fillTextureCoordinates8(TextureImage image, Set<Point> set, final double pixelWidth, final double pixelHeight) {
        double maxDistance = 0;
        //            System.err.println(modifiedPixels.size() + " modified pixels");
        double pixelDiag = Math.sqrt(pixelWidth * pixelWidth + pixelHeight * pixelHeight);
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
            boolean nw = fillTextureCoordinates(image, distance + pixelDiag, pixel.uTexture, new Point(p.x - 1, p.y - 1), newlyModifiedPixels);
            boolean ne = fillTextureCoordinates(image, distance + pixelDiag, pixel.uTexture, new Point(p.x + 1, p.y - 1), newlyModifiedPixels);
            boolean se = fillTextureCoordinates(image, distance + pixelDiag, pixel.uTexture, new Point(p.x + 1, p.y + 1), newlyModifiedPixels);
            boolean sw = fillTextureCoordinates(image, distance + pixelDiag, pixel.uTexture, new Point(p.x - 1, p.y + 1), newlyModifiedPixels);
            if ((n || s) && (distance + pixelHeight > maxDistance)) {
                maxDistance = distance + pixelHeight;
            }
            if ((e || w) && (distance + pixelWidth > maxDistance)) {
                maxDistance = distance + pixelWidth;
            }
            if ((ne || nw || se || sw) && (distance + pixelDiag > maxDistance)) {
                maxDistance = distance + pixelDiag;
            }

        }
        return newlyModifiedPixels;
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
            // FIXME: very special case for 'mer_decoupee2 (cassini)'. Long outer frontier
            // don't have to be of distance 0
            pixelRenderer.setDistanceToZero(segmentLength < 5000);
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

    /**
     * convert point coordinates from image space to polygon space
     * 
     * @param pixelCoordinates
     * @return
     */
    public Point2d projToWorld(double x, double y) {
        return new Point2d(x * this.imageToPolygonFactorX + this.minX, y * this.imageToPolygonFactorY + this.minY);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        final double precision = 0.1;
        double scale = e.getWheelRotation();
        if (scale < 0) {
            scale = 1 - scale * precision;
        } else {
            scale = 1. / (1 + scale * precision);
        }
        double scaleX = this.transform.getScaleX();
        double scaleY = this.transform.getScaleY();
        this.transform.translate(+e.getX() / scaleX, +e.getY() / scaleY);
        this.transform.scale(scale, scale);
        this.transform.translate(-e.getX() / scaleX, -e.getY() / scaleY);
        if (this.screenSpace) {
            this.invalidateImage();
        }
        this.repaint();

    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.drag = true;
        if (e.getButton() == MouseEvent.BUTTON2) {
            this.transform = new AffineTransform();
            if (this.viz.equals("UV pixel textured")) {
                this.invalidateImage();
            }
            this.repaint();
        } else {
            this.clickX = e.getX();
            this.clickY = e.getY();
            this.pressedTransform = new AffineTransform(this.transform);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.clickX = -1;
        this.clickY = -1;
        this.drag = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.clickX != -1 && this.clickY != -1) {
            this.drag = true;
            this.transform = new AffineTransform(this.pressedTransform);
            this.transform.translate((e.getX() - this.clickX) / this.transform.getScaleX(), (e.getY() - this.clickY) / this.transform.getScaleY());
            if (this.screenSpace) {
                this.invalidateImage();
            }

            this.repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (this.texImage == null) {
            return;
        }
        try {
            Point2D pixelLocation = new Point2D.Double();
            this.transform.inverseTransform(new Point2D.Double(e.getX(), e.getY()), pixelLocation);
            int x = (int) pixelLocation.getX();
            int y = (int) pixelLocation.getY();
            this.currentSelectedPixel = null;
            this.currentClosestPixel = null;
            if (!(x < 0 || x >= this.texImage.getWidth() || y < 0 || y >= this.texImage.getHeight())) {
                this.currentSelectedPixel = pixelLocation;
                TexturePixel pixel = this.texImage.getPixel(x, y);
                if (pixel.closestPoint != null) {
                    this.currentClosestPixel = new Point2D.Double(pixel.closestPoint.x, pixel.closestPoint.y);
                }
            }
            this.app.updatePixelContent(pixelLocation);
            this.repaint();
        } catch (NoninvertibleTransformException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    public void setViz(String item) {
        this.viz = item;

    }

    public String doStepIDW() {
        this.invalidateImage();
        if (!this.frontierDrawn) {
            this.pixelRenderer = new DistanceFieldFrontierPixelRenderer();

            for (IPolygon polygon : this.polygons) {
                // draw the outer frontier
                this.drawFrontier(polygon.getExterior(), 1, this.pixelRenderer);

                // draw all inner frontiers
                for (int innerFrontierIndex = 0; innerFrontierIndex < polygon.getInterior().size(); innerFrontierIndex++) {
                    IRing innerFrontier = polygon.getInterior().get(innerFrontierIndex);
                    this.drawFrontier(innerFrontier, -innerFrontierIndex - 1, this.pixelRenderer);
                }
            }
            this.frontierDrawn = true;
            return "exterior and interior frontiers drawn";
        }
        // fills the inner pixels
        if (this.insideDrawn == false) {
            this.fillHorizontally(this.pixelRenderer.getYs());
            this.insideDrawn = true;
            this.modifiedPixels.clear();
            return "horizontally filled";
        }

        if (this.texCoordFilled == false) {
            this.texCoordFilled = true;
            this.fillTextureCoordinatesIDW(this.texImage, this.segments, 4);

            //            for (int y = 0; y < this.texImage.getHeight(); y++) {
            //                for (int x = 0; x < this.texImage.getWidth(); x++) {
            //                    TexturePixel pixel = this.texImage.getPixel(x, y);
            //                    if (pixel.in) {
            //                        if (pixel.uTextureWeightSum > 1E-6) {
            //                            pixel.uTexture /= pixel.uTextureWeightSum;
            //                        }
            //                        if (pixel.vTextureWeightSum > 1E-6) {
            //                            pixel.vTexture /= pixel.vTextureWeightSum;
            //                        }
            //                    }
            //                }
            //            }

            this.stepCount++;
            return "texture coordinates done (" + this.segments.size() + " segments treated)";

        }
        if (this.gradientComputed == false) {
            this.computeGradient();
            this.gradientComputed = true;
            return "v coordinates gradient computed";
        }

        this.hasNoStepLeft = true;
        return "process finished";
    }

    private void fillTextureCoordinatesIDW(TextureImage image, List<ParameterizedSegment> segments, double powerParameter) {
        int prevDone = 0;
        double[] distances = new double[segments.size()];
        double imageMaxDistance = -Double.MAX_VALUE;
        for (int y = 0; y < image.getHeight(); y++) {
            int percentDone = (100 * y) / image.getHeight();
            if (prevDone != percentDone) {
                System.err.println(percentDone + "% done ");
                prevDone = percentDone;
            }
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                if (pixel.in || pixel.closestFrontier != 0) {
                    Point2d p = this.projToWorld(x, y);

                    // compute all distances and the sum of all distances to the power of -p
                    double distanceSum = 0;
                    double minDistance = Double.MAX_VALUE;
                    double maxDistance = -Double.MAX_VALUE;
                    for (int segmentIndex = 0; segmentIndex < segments.size(); segmentIndex++) {
                        ParameterizedSegment segment = segments.get(segmentIndex);
                        distances[segmentIndex] = distance(segment, p);
                        minDistance = Math.min(minDistance, distances[segmentIndex]);
                        maxDistance = Math.max(maxDistance, distances[segmentIndex]);
                        distanceSum += Math.pow(distances[segmentIndex], -powerParameter);
                    }
                    imageMaxDistance = Math.max(imageMaxDistance, maxDistance);
                    // sum all terms
                    double weightedUCos = 0;
                    double weightedUSin = 0;
                    for (int segmentIndex = 0; segmentIndex < segments.size(); segmentIndex++) {
                        ParameterizedSegment segment = segments.get(segmentIndex);
                        double t = getT(segment, p);
                        double u = (segment.getU(t) - image.getuMin()) / (image.getuMax() - image.getuMin());
                        double weight = Math.pow(distances[segmentIndex], -powerParameter) / distanceSum;
                        weightedUCos += weight * Math.cos(PI2 * u);
                        weightedUSin += weight * Math.sin(PI2 * u);
                    }
                    if (Math.abs(weightedUCos) > 1E-6) {
                        double weightedU = Math.atan2(weightedUSin, weightedUCos) / PI2;
                        pixel.uTexture = weightedU * (image.getuMin() - image.getuMax()) + image.getuMin();

                    }
                    pixel.distance = minDistance;
                }
            }
        }
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                if (pixel.in) {
                    pixel.vTexture = pixel.distance / imageMaxDistance;
                }
            }
        }
    }

    public static double distance(Point2d p1, Point2d p2) {
        return Math.sqrt(distance2(p1, p2));
    }

    public static double distance2(Point2d p1, Point2d p2) {
        return (p2.x - p1.x) * (p2.x - p1.x) + (p2.y - p1.y) * (p2.y - p1.y);
    }

    public static double norm(Point2d v) {
        return Math.sqrt(norm2(v));
    }

    public static double norm2(Point2d v) {
        return v.x * v.x + v.y * v.y;
    }

    private void fillTextureCoordinatesHalfSpace(TextureImage image, int x, int y, ParameterizedSegment segment) {
        TexturePixel pixel = image.getPixel(x, y);

        Point2d p1 = new Point2d(segment.p1.x, segment.p1.y);
        Point2d p2 = new Point2d(segment.p2.x, segment.p2.y);
        Point2d p1p2 = new Point2d(p2.x - p1.x, p2.y - p1.y);
        double p1p2l = norm(p1p2);
        System.err.println("Treat Segment length = " + p1p2l);
        if (p1p2l < 1E-6) {
            return;
        }
        Point2d p = this.projToWorld(x, y);
        Point2d p1p = new Point2d(p.x - p1.x, p.y - p1.y);
        double p1pl = norm(p1p);
        if (p1pl < 1E-6) {
            return;
        }
        Point2d p2p = new Point2d(p.x - p2.x, p.y - p2.y);
        double p2pl = norm(p2p);
        if (p2pl < 1E-6) {
            return;
            // theta1 is the angle between p1p2 and p1P
        }

        double p1p2DOTp1p = p1p2.x * p1p.x + p1p2.y * p1p.y;

        // theta1 is the angle between p2p1 and p2P
        double p2p1DOTp2p = -p1p2.x * p2p.x - p1p2.y * p2p.y;

        if (p1p2DOTp1p < 0) {
            double theta1 = Math.acos(p1p2DOTp1p / (p1p2l * p1pl));
            // outside segment (p1 side)
            //            double distance = norm(p1p);
            //            double uWeight = 2 * (Math.PI - Math.abs(theta1)) / Math.PI;
            //            double vWeight = 2 * (Math.PI - Math.abs(theta1)) / Math.PI;
            //            double u = segment.p1.u;
            //            double v = distance;
            double uWeight = 1;
            double vWeight = 0;
            double u = 1;
            double v = 0;
            pixel.uTexture += u * uWeight;
            pixel.vTexture += v * vWeight;
            pixel.uTextureWeightSum += uWeight;
            pixel.vTextureWeightSum += vWeight;
        } else if (p2p1DOTp2p < 0) {
            double theta2 = Math.acos(p2p1DOTp2p / (p1p2l * p2pl));

            // outside segment (p2 side) 
            //                        double distance = norm(p2p);
            //                        double uWeight = 2 * (Math.PI - Math.abs(theta1)) / Math.PI;
            //                        double vWeight = 2 * (Math.PI - Math.abs(theta1)) / Math.PI;
            //                        double u = segment.p2.u;
            //                        double v = distance;
            double uWeight = 1;
            double vWeight = 1;
            double u = 0.5;
            double v = 0;
            pixel.uTexture += u * uWeight;
            pixel.vTexture += v * vWeight;
            pixel.uTextureWeightSum += uWeight;
            pixel.vTextureWeightSum += vWeight;
        } else {
            // inside segment
            //            double t = ((p1p.x) * (p1p2.x) + (p1p.y) * (p1p2.y)) / norm2(p1p2);
            //            double distance = Math.abs(p1pl * Math.sin(theta1));
            //            double uWeight = 1 / distance;
            //            double vWeight = 1 / distance;
            //            double u = segment.p1.u * (1 - t) + segment.p2.u * t;
            //            double v = distance;
            //            pixel.uTexture += u * uWeight;
            //            pixel.vTexture += v * vWeight;
            //            pixel.uTextureWeightSum += uWeight;
            //            pixel.vTextureWeightSum += vWeight;
        }

    }

    /**
     * Distance entre un point et un segment
     * 
     * @param segment
     *            segment to compute the distance with
     * @param p
     *            point to compute distance with the segment
     * @return
     */
    public static double distance(ParameterizedSegment segment, Point2d p) {
        return distance(new Point2d(segment.p1.x, segment.p1.y), new Point2d(segment.p2.x, segment.p2.y), p);
    }

    /**
     * Distance entre un point et un segment
     * 
     * @param p1
     *            first segment point
     * @param p2
     *            second segment point
     * @param p
     *            point to compute distance with the segment
     * @return
     */
    private static double distance(Point2d p1, Point2d p2, Point2d p) {
        Point2d p1p2 = new Point2d(p2.x - p1.x, p2.y - p1.y);
        double p1p2l = norm(p1p2);
        Point2d p1p = new Point2d(p.x - p1.x, p.y - p1.y);
        double p1pl = norm(p1p);
        if (p1pl < 1E-6 || p1p2l < 1E-6) {
            return p1pl;
        }
        Point2d p2p = new Point2d(p.x - p2.x, p.y - p2.y);
        double p2pl = norm(p2p);
        if (p2pl < 1E-6) {
            return p2pl;
        }
        double p1p2DOTp1p = p1p2.x * p1p.x + p1p2.y * p1p.y;

        // theta1 is the angle between p2p1 and p2P
        double p2p1DOTp2p = -p1p2.x * p2p.x - p1p2.y * p2p.y;

        if (p1p2DOTp1p < 0) {
            return p1pl;
        } else if (p2p1DOTp2p < 0) {
            return p2pl;
        } else {
            // inside segment
            double theta1 = Math.acos(p1p2DOTp1p / (p1p2l * p1pl));
            double distance = Math.abs(p1pl * Math.sin(theta1));
            return distance;
        }
    }

    /**
     * return the parametric value of the closest point
     * t = 0 => p1 t = 1 => p2
     * 
     * @param segment
     * @param p
     * @return
     */
    public static double getT(ParameterizedSegment segment, Point2d p) {
        Point2d p1 = new Point2d(segment.p1.x, segment.p1.y);
        Point2d p2 = new Point2d(segment.p2.x, segment.p2.y);
        Point2d p1p2 = new Point2d(p2.x - p1.x, p2.y - p1.y);
        double p1p2l = norm(p1p2);
        if (p1p2l < 1E-6) {
            return 0.5; // arbitrary value
        }
        Point2d p1p = new Point2d(p.x - p1.x, p.y - p1.y);
        double p1pl = norm(p1p);
        if (p1pl < 1E-6) {
            return 0.;
        }
        Point2d p2p = new Point2d(p.x - p2.x, p.y - p2.y);
        double p2pl = norm(p2p);
        if (p2pl < 1E-6) {
            return 1.;
        }
        double p1p2DOTp1p = p1p2.x * p1p.x + p1p2.y * p1p.y;

        // theta1 is the angle between p2p1 and p2P
        double p2p1DOTp2p = -p1p2.x * p2p.x - p1p2.y * p2p.y;

        if (p1p2DOTp1p < 0) {
            return 0.;
        } else if (p2p1DOTp2p < 0) {
            return 1.;
        } else {
            // inside segment
            return ((p1p.x) * (p1p2.x) + (p1p.y) * (p1p2.y)) / norm2(p1p2);

        }
    }

    public void setGradientViz(String item) {
        this.gradientViz = item;

    }

    public String applyFilter(String filterName) {
        if (this.initialTexImage == null) {
            this.initialTexImage = new TextureImage(this.texImage);
        }
        this.initialTexImage.invalidateUVBounds();
        this.texImage.invalidateUVBounds();
        if (filterName.equals("None")) {
            this.texImage = new TextureImage(this.initialTexImage);
            return "Back to unfiltered distance field texture";
        } else if (filterName.equals("Blur distance 1px")) {
            return this.applyBlurDistance(this.initialTexImage, 1);
        } else if (filterName.equals("Blur distance 2px")) {
            return this.applyBlurDistance(this.initialTexImage, 2);
        } else if (filterName.equals("Blur distance 3px")) {
            return this.applyBlurDistance(this.initialTexImage, 3);
        } else if (filterName.equals("Blur distance 10px")) {
            return this.applyBlurDistance(this.initialTexImage, 10);
        } else if (filterName.equals("Blur distance 30px")) {
            return this.applyBlurDistance(this.initialTexImage, 30);
        } else if (filterName.equals("Blur UV 1px")) {
            return this.applyBlurUV(this.initialTexImage, 1);
        } else if (filterName.equals("Blur UV 2px")) {
            return this.applyBlurUV(this.initialTexImage, 2);
        } else if (filterName.equals("Blur UV 3px")) {
            return this.applyBlurUV(this.initialTexImage, 3);
        } else if (filterName.equals("Blur UV 10px")) {
            return this.applyBlurUV(this.initialTexImage, 10);
        } else if (filterName.equals("Blur UV 30px")) {
            return this.applyBlurUV(this.initialTexImage, 30);
        }
        return "Unknown filter '" + filterName + "'";
    }

    private String applyBlurDistance(TextureImage sourceTexImage, int i) {
        this.texImage = new TextureImage(sourceTexImage);
        TextureImageUtil.blurDistance(this.texImage, i);
        this.computeGradient();
        return i + " pixels distance-blur applied to initial image. Gradient recomputed.";
    }

    private String applyBlurUV(TextureImage sourceTexImage, int i) {
        this.texImage = new TextureImage(sourceTexImage);
        TextureImageUtil.blurTextureCoordinates(this.texImage, i);
        this.computeGradient();
        return i + " pixels UV-blur applied to initial image. Gradient recomputed.";
    }

    public void setGradientScale(double gScale) {
        this.gScale = gScale;

    }

    public void setURepeat(String repeat) {
        this.uRepeat = RepeatType.valueOf(repeat);
    }

    public void setVRepeat(String repeat) {
        this.vRepeat = RepeatType.valueOf(repeat);
    }

}
