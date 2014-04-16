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

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.util.MipMapMask;
import fr.ign.cogit.geoxygene.util.gl.GradientTextureImage;
import fr.ign.cogit.geoxygene.util.gl.Sample;
import fr.ign.cogit.geoxygene.util.gl.TextureImageUtil;
import fr.ign.cogit.geoxygene.util.gl.Tile;
import fr.ign.cogit.geoxygene.util.gl.GradientTextureImage.TexturePixel;

/**
 * @author JeT
 * 
 */
public class TextureImageSamplerMipMap implements SamplingAlgorithm {

    private static final Logger logger = Logger.getLogger(TextureImageSamplerMipMap.class.getName()); // logger

    private GradientTextureImage image = null;
    private List<Sample> samples = null;
    private TileChooser tileChooser = null;
    private MipMapMask imageMask = null;

    public TextureImageSamplerMipMap(GradientTextureImage image, TileChooser tileChooser) {
        super();
        this.tileChooser = tileChooser;
        this.setImage(image);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.texture.SamplingAlgorithm#getSampleCount
     * ()
     */
    @Override
    public int getSampleCount() {
        return this.getSamples() == null ? 0 : this.getSamples().size();
    }

    private void setImage(GradientTextureImage image) {
        this.image = image;
        this.imageMask = new MipMapMask();
        logger.debug("Generate MipMap from image size " + this.image.getWidth() + "x" + image.getHeight() + " from gradient image " + this.image.hashCode());
        this.imageMask.setSize(image.getWidth(), image.getHeight());
        if (this.tileChooser == null) {
            throw new IllegalStateException("Tile Chooser must be set in MipMapSampler");
        }
        // set all pixels where tiles can appear (in polygon & tile is not null)
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                TexturePixel pixel = image.getPixel(x, y);
                if (pixel == null) {
                    throw new IllegalStateException("Impossible case. pixel " + x + "x" + y + " must be valid in texture image");
                }
                Sample sample = new Sample(x, y, null);
                Tile tile = this.tileChooser.getTile(sample);
                if ((pixel.in || pixel.frontier != 0) && tile != null) {
                    this.imageMask.addWhitePixel(x, y);
                    pixel.sample = sample;
                    sample.setTile(tile);
                }
            }

        }
        //        MipMapMask.save(this.imageMask, "initialMipMap.png");
    }

    /**
     * @return the tileChooser
     */
    public TileChooser getTileChooser() {
        return this.tileChooser;
    }

    private void invalidateSamples() {
        this.samples = null;
    }

    private void computeSamples() {
        this.samples = new ArrayList<Sample>();
        Point p;
        Random rand = new Random(0);
        //        try {
        //            TextureImageUtil.save(this.image, "gradientImage");
        //        } catch (IOException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        //        int count = 0;
        //        System.err.println("compute samples using mipmap");
        //        MipMapMask.save(this.imageMask, "./initial mipmap.png");
        //        System.err.println("save file initial mipmap.png");
        while ((p = this.nextWhitePixel(rand)) != null) {
            //            System.err.println("Chosen sample = " + p + " between " + this.imageMask.getNbWhite() + " white pixels");
            TexturePixel pixel = this.image.getPixel(p.x, p.y);
            //            System.err.println("pixel = " + pixel);
            Sample sample = pixel.sample;
            if (sample == null) {
                this.imageMask.removeWhitePixel(p.x, p.y);
                continue;
                //                throw new IllegalStateException("point " + p + " is not set with a sample");
            }
            Tile tile = sample.getTile();
            if (tile == null) {
                throw new IllegalStateException("point " + p + " cannot have an empty tile");
            }
            if (this.pastePatch(sample)) {
                this.samples.add(sample);
            } else {
                logger.error("An error ocured pasting patch at " + sample);
                //                System.err.println("pixel = " + pixel);
                // remove the invalid pixel...
                this.imageMask.removeWhitePixel((int) sample.getLocation().getX(), (int) sample.getLocation().getY());
            }

            //            MipMapMask.save(this.imageMask, "./mipmap" + count + "-" + this.imageMask.getNbWhite() + ".png");
            //            System.err.println("save file mipmap.png");
            //            System.err.println("count = " + count);
            //            count++;
        }
        //        MipMapMask.save(this.imageMask, "./mipmap.png");
        //        System.err.println("save file mipmap.png");
    }

    private boolean pastePatch(Sample sample) {
        Tile tile = sample.getTile();
        if (tile == null) {
            throw new IllegalStateException("Tile should be set before pasting it");
        }
        int tileWidth = tile.getMask().getWidth();
        int tileHeight = tile.getMask().getHeight();
        AffineTransform transform = this.image.tileTransform((int) sample.getLocation().getX(), (int) sample.getLocation().getY(), tileWidth, tileHeight);

        byte[] tileMaskPixels = ((DataBufferByte) tile.getMask().getRaster().getDataBuffer()).getData();

        // in mask : 255 = inside, 0 = outside
        int lTileMask = 0;
        for (double y = 0; y < tileHeight; y += 0.5) {
            for (double x = 0; x < tileWidth; x += 0.5) {
                lTileMask = (int) x + ((int) y) * tileWidth;
                byte tilePixel = tileMaskPixels[lTileMask];
                if (tilePixel == Tile.MASK_OUT) {
                    continue;
                }
                Point2D imageSourcePixelLocation = new Point2D.Double();
                transform.transform(new Point2D.Double(x, y), imageSourcePixelLocation);
                int imageWidth = this.image.getWidth();
                int imageHeight = this.image.getHeight();
                int xImage = (int) imageSourcePixelLocation.getX(); // pixel coordinates in image mask
                int yImage = (int) imageSourcePixelLocation.getY();
                // check if pasted pixel is in the mask image (borders management)
                if (xImage < 0 || xImage >= imageWidth || yImage < 0 || yImage >= imageHeight) {
                    continue;
                }
                if (this.imageMask.getWhitePixelCount(xImage, yImage) != 0) {
                    this.imageMask.removeWhitePixel(xImage, yImage);
                }
            }
        }
        return true;
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

    //    private int tryToPasteTile(Sample sample, BufferedImage imageMask, Tile tile) {
    //        int nbNewPixels = 0;
    //        int nbOverlapPixels = 0;
    //        AffineTransform transform = new AffineTransform();
    //        int tileWidth = tile.getImage().getWidth();
    //        int tileHeight = tile.getImage().getHeight();
    //        transform.translate(sample.getLocation().x - tileWidth / 2, sample.getLocation().y - tileHeight / 2);
    //        transform.rotate(sample.getRotation().x, sample.getRotation().y, tileWidth / 2, tileHeight / 2);
    //
    //        BufferedImage mergedTile = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_BYTE_GRAY);
    //        byte[] tileMaskPixels = ((DataBufferByte) tile.getMask().getRaster().getDataBuffer()).getData();
    //        byte[] mergedTilePixels = ((DataBufferByte) mergedTile.getRaster().getDataBuffer()).getData();
    //        byte[] imageMaskPixels = ((DataBufferByte) imageMask.getRaster().getDataBuffer()).getData();
    //
    //        // in mask : 255 = inside, 0 = outside
    //        for (int lTileMask = 0, y = 0; y < tileHeight; y++) {
    //            for (int x = 0; x < tileWidth; x++, lTileMask++) {
    //                byte tilePixel = tileMaskPixels[lTileMask];
    //
    //                Point2D imageMaskPixelLocation = new Point2D.Double();
    //                transform.transform(new Point2D.Double(x, y), imageMaskPixelLocation);
    //                int imageWidth = imageMask.getWidth();
    //                int imageHeight = imageMask.getHeight();
    //                int xImage = (int) imageMaskPixelLocation.getX(); // pixel coordinates in image mask
    //                int yImage = (int) imageMaskPixelLocation.getY();
    //                // check if pasted pixel is in the mask image (borders management)
    //                if (xImage < 0 || xImage >= imageWidth || yImage < 0 || yImage >= imageHeight) {
    //                    continue;
    //                }
    //                int lImageMask = (xImage + yImage * imageWidth); // offset in imageMask
    //                byte imageMaskPixel = imageMaskPixels[lImageMask];
    //                if (imageMaskPixel == Tile.MASK_OUT && tilePixel == Tile.MASK_IN) {
    //                    mergedTilePixels[lTileMask] = Tile.MASK_IN;
    //                    nbNewPixels++;
    //
    //                } else if (imageMaskPixel == Tile.MASK_IN && tilePixel == Tile.MASK_IN) {
    //                    mergedTilePixels[lTileMask] = Tile.MASK_IN;
    //                    nbOverlapPixels++;
    //                    if ((double) nbOverlapPixels / (double) tile.getSize() > this.overlapRatio) {
    //
    //                        return 0;
    //                    }
    //                } else {
    //                    // imagePixel == IN => just copy image mask value 
    //                    mergedTilePixels[lTileMask] = imageMaskPixel;
    //                }
    //            }
    //        }
    //        Graphics2D g2 = imageMask.createGraphics();
    //        g2.setTransform(transform);
    //        g2.drawImage(mergedTile, 0, 0, null);
    //        return nbNewPixels;
    //    }

    /**
     * Recursively traverse the pyramid levels to find the cell with the maximum
     * white count. if cells have the same white count, one is randomly picked.
     * 
     * @param rand
     *            random seed used when multiple cells have the same maximum
     *            white pixels
     * @return one of the max cell or null if none
     */
    private Point nextWhitePixel(Random rand) {
        if (this.imageMask == null || this.imageMask.getNbWhite() == 0) {
            return null;
        }
        Point pixelInMipmapCoordinates = this.nextWhitePixelRec(0, 0, this.imageMask.getMipmapNbLevels() - 1, rand);
        if (pixelInMipmapCoordinates == null) {
            return null;
        }
        return this.imageMask.mipmap2SourceCoordinates(pixelInMipmapCoordinates);
    }

    /**
     * Recursively traverse the pyramid levels to find the cell with the maximum
     * white count.
     * if cells have the same white count, one is randomly picked.
     * 
     * @param x
     * @param y
     * @param level
     * @param rand
     * @return
     */
    private Point nextWhitePixelRec(int x, int y, int level, Random rand) {
        List<Point> points = new ArrayList<Point>();
        int nbWhite = this.imageMask.getCell(x, y, level).nbWhite;
        if (nbWhite == 0) {
            return null;
        }
        int nbLevels = this.imageMask.getMipmapNbLevels();
        if (level == 0) {
            return (nbWhite != 0) ? new Point(x, y) : null;
        }

        int currentLevelWidth = this.imageMask.getMipmapImageWidthPerLevel(nbLevels - level - 1);
        //        System.err.println("pixel " + x + "x" + y + " #white: " + nbWhite + " >=? " + currentLevelWidth + "² at level " + level);
        //        System.err.println("choose among " + (currentLevelWidth / 2) + " pixels");
        //        System.err.println("x shifting: x = " + x + " multiplied by " + (1 << (nbLevels - level - 1)));
        // TODO: we should integrate the image pixel surface ratio because the following test
        // can be true only when the initial image has the same size as the mipmap (so: never) 
        //        //  to reduce pyramid traversal, we can choose a random point as soon as cell is full of white pixels 
        //        if (nbWhite >= currentLevelWidth * currentLevelWidth) {
        //            if (currentLevelWidth / 2 == 0) {
        //                return new Point(x * (1 << (nbLevels - level - 1)), y * (1 << (nbLevels - level - 1)));
        //            }
        //            return new Point(x * (1 << (nbLevels - level - 1)) + rand.nextInt(currentLevelWidth / 2), y * (1 << (nbLevels - level - 1))
        //                    + rand.nextInt(currentLevelWidth / 2));
        //        }
        Point[] ps = this.imageMask.getWiderLevelCellsCoordinates(x, y, level);
        //        System.err.println("level " + level + " cell " + x + "x" + y + " up cells = " + Arrays.toString(ps));
        if (ps == null) {
            System.err.println("no up level for " + x + "x" + y + " level " + level);
            return null;
        }
        // random pick between all cells with the same maximum white count
        int nbMax = 0; // number of point with their white count equal to max number
        int maxNbWhite = 0; // max value of white pixel count
        for (int i = 0; i < 4; i++) {
            nbWhite = this.imageMask.getCell(ps[i], level - 1).nbWhite;
            //            System.err.println("pixel " + ps[i] + " # white = " + nbWhite + " (max = " + maxNbWhite + ")");
            if (nbWhite > maxNbWhite) {
                maxNbWhite = nbWhite;
                points.clear();
                points.add(ps[i]);
                nbMax = 1;
            } else if (maxNbWhite == nbWhite) {
                points.add(ps[i]);
                nbMax++;
            }
        }
        if (nbMax == 0) {
            return null;
        }
        int randIndex = rand.nextInt(nbMax);
        Point chosenPoint = points.get(randIndex);
        //        System.err.println("chosen point " + chosenPoint + " index = " + randIndex + " from " + points);
        return this.nextWhitePixelRec(chosenPoint.x, chosenPoint.y, level - 1, rand);
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
