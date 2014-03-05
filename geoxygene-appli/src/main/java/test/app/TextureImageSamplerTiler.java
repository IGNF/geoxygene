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

package test.app;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point2d;

import utils.Pair;
import fr.ign.cogit.geoxygene.util.gl.TextureImage;
import fr.ign.cogit.geoxygene.util.gl.TextureImage.TexturePixel;

/**
 * @author JeT
 * 
 */
public class TextureImageSamplerTiler implements SamplingAlgorithm {

    private TextureImage image = null;
    private double overlapRatio = 0.6; // max overlap between tile and image during initial sampling
    private List<Sample> samples = null;
    private double jitteringFactor = 0.5;
    private double scale = 1.;
    private TileChooser tileChooser = null;
    private BufferedImage imageMask = null;

    //    private double jitteringFactor = 0.;
    /**
     * Default constructor
     */
    public TextureImageSamplerTiler(TextureImage image, TileChooser tileChooser, double overlapRatio, double scale) {
        this.image = image;
        this.overlapRatio = overlapRatio;
        this.tileChooser = tileChooser;
        this.scale = scale;
    }

    /**
     * @return the jitteringFactor
     */
    public double getJitteringFactor() {
        return this.jitteringFactor;
    }

    /**
     * @return the tileChooser
     */
    public TileChooser getTileChooser() {
        return this.tileChooser;
    }

    /**
     * @param jitteringFactor
     *            the jitteringFactor to set
     */
    public void setJitteringFactor(double jitteringFactor) {
        this.jitteringFactor = jitteringFactor;
        this.invalidateSamples();
    }

    private void invalidateSamples() {
        this.samples = null;
    }

    private void computeSamples() {
        this.samples = new ArrayList<Sample>();
        List<Point> frontier = new ArrayList<Point>();

        int w = this.image.getWidth();
        int h = this.image.getHeight();
        this.imageMask = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        byte[] imageMaskPixels = ((DataBufferByte) this.imageMask.getRaster().getDataBuffer()).getData();

        int nbPixelsToBeMerged = 0;
        // set the image mask and compute frontier
        for (int lImageMask = 0, y = 0; y < h; y++) {
            for (int x = 0; x < w; x++, lImageMask++) {
                TexturePixel pixel = this.image.getPixel(x, y);
                if (pixel.in) {
                    imageMaskPixels[lImageMask] = Tile.MASK_OUT;
                    nbPixelsToBeMerged++;
                } else {
                    imageMaskPixels[lImageMask] = Tile.MASK_IN;
                }
                if (pixel.frontier != 0) {
                    frontier.add(new Point(x, y));
                    System.err.println("frontier pixel : " + x + "x" + y + " gradient = " + pixel.vGradient);
                }
            }
        }

        System.err.println("initial frontier size = " + frontier.size());
        while (frontier.size() != 0) {

            Point p = frontier.get(0);
            Tile tile = this.tileChooser.getTile(new Sample(p.x, p.y, null));
            if (tile == null) {

                frontier.remove(0);
                continue;
            }
            this.pastePatch(frontier, p, tile, imageMaskPixels);

        }
    }

    private void pastePatch(List<Point> frontier, Point p, Tile tile, byte[] imageMaskPixels) {
        Pair<Double, BufferedImage> tileMatching = evaluateTileMatching(tile, p, this.image, this.imageMask);
        if (tileMatching == null) {
            return;
        }
        System.err.println("evaluate patch " + p + " = " + tileMatching.first());
        this.pastePatchRec(tileMatching, frontier, p, tile, imageMaskPixels);
    }

    /**
     * @param frontier
     * @param p
     * @param tile
     * @param imageMaskPixels
     */
    private void pastePatchRec(Pair<Double, BufferedImage> tileMatching, List<Point> frontier, Point p, Tile tile, byte[] imageMaskPixels) {
        int w = this.image.getWidth();
        int h = this.image.getHeight();

        System.err.println("recursively evaluate patch " + p + " = " + tileMatching.first());

        int lImageMask = p.x + p.y * w;
        Pair<Double, BufferedImage> north = null, east = null, south = null, west = null, best = null;
        best = tileMatching;
        if (imageMaskPixels[lImageMask] == Tile.MASK_OUT) {
            return;
        }
        if (p.x > 0 && imageMaskPixels[lImageMask - 1] == Tile.MASK_OUT) {
            west = evaluateTileMatching(tile, new Point(p.x - 1, p.y), this.image, this.imageMask);
            if (west.first() > best.first()) {
                best = west;
            }
        }
        if (p.x < w - 1 && imageMaskPixels[lImageMask + 1] == Tile.MASK_OUT) {
            east = evaluateTileMatching(tile, new Point(p.x + 1, p.y), this.image, this.imageMask);
            if (east.first() > best.first()) {
                best = east;
            }
        }
        if (p.y > 0 && imageMaskPixels[lImageMask - w] == Tile.MASK_OUT) {
            north = evaluateTileMatching(tile, new Point(p.x, p.y - 1), this.image, this.imageMask);
            if (north.first() > best.first()) {
                best = north;
            }
        }
        if (p.y < h - 1 && imageMaskPixels[lImageMask + w] == Tile.MASK_OUT) {
            south = evaluateTileMatching(tile, new Point(p.x, p.y - 1), this.image, this.imageMask);
            if (south.first() > best.first()) {
                best = south;
            }
        }

        if (tileMatching == best) {
            System.err.println("found a new match at " + p);
            TexturePixel pixel = this.image.getPixel(p.x, p.y);
            if (pixel == null || pixel.vGradient == null) {
                return;
            }
            Point2d location = new Point2d(p.x, p.y);
            Point2d rotation = new Point2d(pixel.vGradient.x, pixel.vGradient.y);
            Sample sample = new Sample(location, rotation, null);
            AffineTransform transform = new AffineTransform();
            int tileWidth = tile.getImage().getWidth();
            int tileHeight = tile.getImage().getHeight();
            transform.translate(sample.getLocation().x - tileWidth / 2, sample.getLocation().y - tileHeight / 2);
            transform.rotate(sample.getRotation().x, sample.getRotation().y, tileWidth / 2, tileHeight / 2);

            BufferedImage mergedTile = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_BYTE_GRAY);
            byte[] tileMaskPixels = ((DataBufferByte) tile.getMask().getRaster().getDataBuffer()).getData();
            byte[] tileBorderPixels = ((DataBufferByte) tile.getBorder().getRaster().getDataBuffer()).getData();
            byte[] mergedTilePixels = ((DataBufferByte) mergedTile.getRaster().getDataBuffer()).getData();

            // in mask : 255 = inside, 0 = outside
            for (int lTileMask = 0, y = 0; y < tileHeight; y++) {
                for (int x = 0; x < tileWidth; x++, lTileMask++) {
                    byte tilePixel = tileMaskPixels[lTileMask];

                    Point2D imageMaskPixelLocation = new Point2D.Double();
                    transform.transform(new Point2D.Double(x, y), imageMaskPixelLocation);
                    int imageWidth = this.imageMask.getWidth();
                    int imageHeight = this.imageMask.getHeight();
                    int xImage = (int) imageMaskPixelLocation.getX(); // pixel coordinates in image mask
                    int yImage = (int) imageMaskPixelLocation.getY();
                    // check if pasted pixel is in the mask image (borders management)
                    if (xImage < 0 || xImage >= imageWidth || yImage < 0 || yImage >= imageHeight) {
                        continue;
                    }
                    byte tileBorderPixel = tileBorderPixels[lTileMask];
                    lImageMask = (xImage + yImage * imageWidth); // offset in imageMask
                    byte imageMaskPixel = imageMaskPixels[lImageMask];
                    if (imageMaskPixel == Tile.MASK_IN && tileBorderPixel == Tile.MASK_IN) {
                        frontier.remove(new Point(xImage, yImage));
                    }
                    if (imageMaskPixel == Tile.MASK_OUT && tilePixel == Tile.MASK_IN) {
                        mergedTilePixels[lTileMask] = Tile.MASK_IN;
                    } else if (imageMaskPixel == Tile.MASK_IN && tilePixel == Tile.MASK_IN) {
                        mergedTilePixels[lTileMask] = Tile.MASK_IN;
                    } else {
                        // imagePixel == IN => just copy image mask value 
                        mergedTilePixels[lTileMask] = imageMaskPixel;
                    }
                }
            }
            return;
        } else if (best == east) {
            this.pastePatchRec(east, frontier, new Point(p.x + 1, p.y), tile, imageMaskPixels);
        } else if (best == west) {
            this.pastePatchRec(west, frontier, new Point(p.x - 1, p.y), tile, imageMaskPixels);
        } else if (best == north) {
            this.pastePatchRec(north, frontier, new Point(p.x, p.y - 1), tile, imageMaskPixels);
        } else if (best == south) {
            this.pastePatchRec(south, frontier, new Point(p.x, p.y + 1), tile, imageMaskPixels);
        }
    }

    private static Pair<Double, BufferedImage> evaluateTileMatching(Tile tile, Point p, TextureImage image, BufferedImage imageMask) {
        //        int w = image.getWidth();
        //        int h = image.getHeight();
        TexturePixel pixel = image.getPixel(p.x, p.y);
        if (pixel == null || pixel.vGradient == null) {
            return null;
        }
        Point2d location = new Point2d(p.x, p.y);
        System.err.println("evaluate Tile matching at pixel " + pixel);
        System.err.println("evaluate Tile matching vGradient = " + pixel.vGradient);
        Point2d rotation = new Point2d(pixel.vGradient.x, pixel.vGradient.y);
        Sample potentialSample = new Sample(location, rotation, null);

        //        int nbNewPixels = 0;
        int nbOverlapPixels = 0;
        int coveredTileBorder = 0;
        AffineTransform transform = new AffineTransform();
        int tileWidth = tile.getImage().getWidth();
        int tileHeight = tile.getImage().getHeight();
        transform.translate(potentialSample.getLocation().x - tileWidth / 2, potentialSample.getLocation().y - tileHeight / 2);
        transform.rotate(potentialSample.getRotation().x, potentialSample.getRotation().y, tileWidth / 2, tileHeight / 2);

        BufferedImage mergedTile = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_BYTE_GRAY);
        byte[] tileMaskPixels = ((DataBufferByte) tile.getMask().getRaster().getDataBuffer()).getData();
        byte[] tileBorderPixels = ((DataBufferByte) tile.getBorder().getRaster().getDataBuffer()).getData();
        byte[] mergedTilePixels = ((DataBufferByte) mergedTile.getRaster().getDataBuffer()).getData();
        byte[] imageMaskPixels = ((DataBufferByte) imageMask.getRaster().getDataBuffer()).getData();

        // in mask : 255 = inside, 0 = outside
        for (int lTileMask = 0, y = 0; y < tileHeight; y++) {
            for (int x = 0; x < tileWidth; x++, lTileMask++) {
                byte tilePixel = tileMaskPixels[lTileMask];

                Point2D imageMaskPixelLocation = new Point2D.Double();
                transform.transform(new Point2D.Double(x, y), imageMaskPixelLocation);
                int imageWidth = imageMask.getWidth();
                int imageHeight = imageMask.getHeight();
                int xImage = (int) imageMaskPixelLocation.getX(); // pixel coordinates in image mask
                int yImage = (int) imageMaskPixelLocation.getY();
                // check if pasted pixel is in the mask image (borders management)
                if (xImage < 0 || xImage >= imageWidth || yImage < 0 || yImage >= imageHeight) {
                    continue;
                }
                byte tileBorderPixel = tileBorderPixels[lTileMask];
                int lImageMask = (xImage + yImage * imageWidth); // offset in imageMask
                byte imageMaskPixel = imageMaskPixels[lImageMask];
                if (imageMaskPixel == Tile.MASK_IN && tileBorderPixel == Tile.MASK_IN) {
                    coveredTileBorder++;
                }
                if (imageMaskPixel == Tile.MASK_OUT && tilePixel == Tile.MASK_IN) {
                    mergedTilePixels[lTileMask] = Tile.MASK_IN;
                } else if (imageMaskPixel == Tile.MASK_IN && tilePixel == Tile.MASK_IN) {
                    mergedTilePixels[lTileMask] = Tile.MASK_IN;
                    nbOverlapPixels++;
                } else {
                    // imagePixel == IN => just copy image mask value 
                    mergedTilePixels[lTileMask] = imageMaskPixel;
                }
            }
        }

        return new Pair<Double, BufferedImage>(Math.log(nbOverlapPixels * (1 - 0.3)) / Math.log(coveredTileBorder), mergedTile);
    }

    private static List<Point> extractFrontier(TextureImage image) {
        List<Point> frontier = new ArrayList<Point>();
        int w = image.getWidth();
        int h = image.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                TexturePixel pixel = image.getPixel(x, y);
                if (pixel.frontier != 0) {
                    frontier.add(new Point(x, y));
                }
            }
        }

        return frontier;
    }

    //    private void computeSamples() {
    //        this.samples = new ArrayList<Sample>();
    //
    //        // generate regular/jitterized grid sample
    //        TextureImageSamplerRegularGrid regularSampler = new TextureImageSamplerRegularGrid(this.image, 20, 20, this.scale, this.tileChooser);
    //        regularSampler.setJitteringFactor(0.2);
    //        List<Sample> regularSamples = regularSampler.getSamples();
    //        if (regularSamples.size() == 0) {
    //            return;
    //        }
    //        Random random = new Random(0);
    //        // first shuffle then sort on tile size for shuffling tiles of the same size together 
    //        Collections.shuffle(regularSamples, random);
    //        Collections.sort(regularSamples, Collections.reverseOrder(new Sample.TileSizeComparator()));
    //
    //        // generate the image mask
    //        int w = this.image.getWidth();
    //        int h = this.image.getHeight();
    //        this.imageMask = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
    //        byte[] imageMaskPixels = ((DataBufferByte) this.imageMask.getRaster().getDataBuffer()).getData();
    //
    //        int nbPixelsToBeMerged = 0;
    //        // set the image mask
    //        for (int lImageMask = 0, y = 0; y < h; y++) {
    //            for (int x = 0; x < w; x++, lImageMask++) {
    //                if (this.image.getPixel(x, y).in) {
    //                    imageMaskPixels[lImageMask] = Tile.MASK_OUT;
    //                    nbPixelsToBeMerged++;
    //                } else {
    //                    imageMaskPixels[lImageMask] = Tile.MASK_IN;
    //                }
    //            }
    //        }
    //
    //        List<Sample> rejectedSamples = new ArrayList<Sample>();
    //
    //        // try to paste patches in a random order
    //        int nbSkipped = 0;
    //        for (Sample sample : regularSamples) {
    //            Tile tile = sample.getTile();
    //            if (tile != null) {
    //                int tileMergedPixels = this.tryToPasteTile(sample, this.imageMask, tile);
    //                if (tileMergedPixels > 0) {
    //                    this.samples.add(sample);
    //                } else {
    //                    rejectedSamples.add(sample);
    //                    nbSkipped++;
    //                    if (nbSkipped >= 1) {
    //                        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
    //                        Graphics2D gg = img.createGraphics();
    //                        //                        gg.setColor(Color.black);
    //                        //                        gg.drawRect(0, 0, w, h);
    //                        gg.setComposite(AlphaComposite.SrcOver.derive(0.5f));
    //                        gg.drawImage(this.imageMask, 0, 0, null);
    //                        AffineTransform transform = new AffineTransform();
    //                        int tileWidth = tile.getImage().getWidth();
    //                        int tileHeight = tile.getImage().getHeight();
    //                        transform.translate(sample.getLocation().x - tileWidth / 2, sample.getLocation().y - tileHeight / 2);
    //                        transform.rotate(sample.getRotation().x, sample.getRotation().y, tileWidth / 2, tileHeight / 2);
    //
    //                        gg.setTransform(transform);
    //                        gg.setComposite(AlphaComposite.SrcOver);
    //                        gg.drawImage(tile.getImage(), null, 0, 0);
    //                        gg.setColor(Color.gray);
    //                        gg.drawRect(0, 0, tile.getImage().getWidth(), tile.getImage().getHeight());
    //
    //                        gg.setColor(Color.white);
    //                        gg.setTransform(new AffineTransform());
    //                        for (Sample s : regularSamples) {
    //                            gg.drawRect((int) s.getLocation().x - 1, (int) s.getLocation().y - 1, 3, 3);
    //
    //                        }
    //                        gg.setColor(Color.white);
    //                        gg.setTransform(new AffineTransform());
    //                        for (Sample s : rejectedSamples) {
    //                            gg.fillRect((int) s.getLocation().x - 2, (int) s.getLocation().y - 2, 4, 4);
    //
    //                        }
    //                        ImageUtil.displayImageInWindow(img);
    //                    }
    //
    //                }
    //                nbPixelsToBeMerged -= tileMergedPixels;
    //            }
    //        }
    //
    //        // find remaining holes and fill them
    //
    //        //        while (nbPixelsToBeMerged > 0) {
    //        //            floodHoles();
    //        //        }
    //        System.err.println(this.samples.size() + " samples created");
    //        //        ImageUtil.displayImageInWindow(this.imageMask);
    //    }

    private int tryToPasteTile(Sample sample, BufferedImage imageMask, Tile tile) {
        int nbNewPixels = 0;
        int nbOverlapPixels = 0;
        AffineTransform transform = new AffineTransform();
        int tileWidth = tile.getImage().getWidth();
        int tileHeight = tile.getImage().getHeight();
        transform.translate(sample.getLocation().x - tileWidth / 2, sample.getLocation().y - tileHeight / 2);
        transform.rotate(sample.getRotation().x, sample.getRotation().y, tileWidth / 2, tileHeight / 2);

        BufferedImage mergedTile = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_BYTE_GRAY);
        byte[] tileMaskPixels = ((DataBufferByte) tile.getMask().getRaster().getDataBuffer()).getData();
        byte[] mergedTilePixels = ((DataBufferByte) mergedTile.getRaster().getDataBuffer()).getData();
        byte[] imageMaskPixels = ((DataBufferByte) imageMask.getRaster().getDataBuffer()).getData();

        // in mask : 255 = inside, 0 = outside
        for (int lTileMask = 0, y = 0; y < tileHeight; y++) {
            for (int x = 0; x < tileWidth; x++, lTileMask++) {
                byte tilePixel = tileMaskPixels[lTileMask];

                Point2D imageMaskPixelLocation = new Point2D.Double();
                transform.transform(new Point2D.Double(x, y), imageMaskPixelLocation);
                int imageWidth = imageMask.getWidth();
                int imageHeight = imageMask.getHeight();
                int xImage = (int) imageMaskPixelLocation.getX(); // pixel coordinates in image mask
                int yImage = (int) imageMaskPixelLocation.getY();
                // check if pasted pixel is in the mask image (borders management)
                if (xImage < 0 || xImage >= imageWidth || yImage < 0 || yImage >= imageHeight) {
                    continue;
                }
                int lImageMask = (xImage + yImage * imageWidth); // offset in imageMask
                byte imageMaskPixel = imageMaskPixels[lImageMask];
                if (imageMaskPixel == Tile.MASK_OUT && tilePixel == Tile.MASK_IN) {
                    mergedTilePixels[lTileMask] = Tile.MASK_IN;
                    nbNewPixels++;

                } else if (imageMaskPixel == Tile.MASK_IN && tilePixel == Tile.MASK_IN) {
                    mergedTilePixels[lTileMask] = Tile.MASK_IN;
                    nbOverlapPixels++;
                    if ((double) nbOverlapPixels / (double) tile.getSize() > this.overlapRatio) {

                        return 0;
                    }
                } else {
                    // imagePixel == IN => just copy image mask value 
                    mergedTilePixels[lTileMask] = imageMaskPixel;
                }
            }
        }
        Graphics2D g2 = imageMask.createGraphics();
        g2.setTransform(transform);
        g2.drawImage(mergedTile, 0, 0, null);
        return nbNewPixels;
    }

    /**
     * @return the imageMask
     */
    public BufferedImage getImageMask() {
        return this.imageMask;
    }

    /**
     * @return the samples
     */
    public List<Sample> getSamples() {
        if (this.samples == null) {
            this.computeSamples();
        }
        return this.samples;
    }

    /*
     * (non-Javadoc)
     * 
     * @see test.app.SamplingAlgorithm#getSample()
     */
    @Override
    public Iterator<Sample> getSampleIterator() {
        return this.getSamples().iterator();
    }

}
