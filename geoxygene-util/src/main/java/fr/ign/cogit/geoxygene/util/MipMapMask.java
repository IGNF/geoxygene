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

package fr.ign.cogit.geoxygene.util;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author JeT Image pyramids containing only black and white pixel counts.
 *         level 0 is the full image scaled to the closest greater power of two.
 *         level one is half size, etc. each cell contains a number of black &
 *         white points level getMipmapNbLevels()-1 has only one pixel image
 *         image at level 0 has (2^(nbLevels-1))² pixels
 */
public class MipMapMask {

    private int fullMipmapImageWidth = 0; // number of pixels of the level 0
                                          // image
    private int imageWidth = 0; // source image width
    private int imageHeight = 0; // source image Height
    private int mipmapNbLevels = 0; // image pyramid number of levels

    /**
     * @return the mipmapSize
     */
    public int getMipmapSize() {
        return this.fullMipmapImageWidth;
    }

    /**
     * @return the imageWidth
     */
    public int getImageWidth() {
        return this.imageWidth;
    }

    /**
     * @return the imageHeight
     */
    public int getImageHeight() {
        return this.imageHeight;
    }

    /**
     * @return the mipmapNbLevels
     */
    public int getMipmapNbLevels() {
        return this.mipmapNbLevels;
    }

    /**
     * @return the mipmapSizePerLevel
     */
    public int getMipmapImageWidthPerLevel(int level) {
        return this.mipmapImageWidthPerLevel[level];
    }

    private MipMapImage[] mipmapImages = null;
    private int mipmapImageWidthPerLevel[] = null;
    private static final Point zeroPoint = new Point(0, 0);

    // /**
    // * @param source
    // */
    // public MipMapMask(BufferedImage source) {
    // super();
    // this.setSource(source);
    // }
    //
    /**
     * @param source
     */
    public MipMapMask() {
        super();
    }

    public void setSize(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
        // just count the number of zero in binary representation to get the
        // closest power of two (+1 for the ceil value)
        this.mipmapNbLevels = 32 - Integer.numberOfLeadingZeros((int) Math
                .ceil((Math.max(this.imageWidth, this.imageHeight)))) + 1;
        this.fullMipmapImageWidth = 1 << (this.mipmapNbLevels - 1);
        this.mipmapImages = new MipMapImage[this.mipmapNbLevels];

        this.mipmapImageWidthPerLevel = new int[this.mipmapNbLevels];

        for (int mipmapImageWidth = this.fullMipmapImageWidth, level = 0; level < this.mipmapNbLevels; level++, mipmapImageWidth /= 2) {
            this.mipmapImageWidthPerLevel[level] = mipmapImageWidth;
            this.mipmapImages[level] = new MipMapImage(this, mipmapImageWidth,
                    level);
        }

    }

    // public void setSource(BufferedImage source) {
    // if (source.getType() != BufferedImage.TYPE_BYTE_GRAY) {
    // throw new
    // IllegalArgumentException("mipmap Image type must be grayscale");
    // }
    // this.setSize(source.getWidth(), source.getHeight());
    // byte[] sourcePixels = ((DataBufferByte)
    // source.getRaster().getDataBuffer()).getData();
    //
    // for (int y = 0, l = 0; y < this.imageHeight; y++) {
    // for (int x = 0; x < this.imageWidth; x++, l++) {
    // System.err.println("image source " + l + " = " + sourcePixels[l]);
    // int pixel = sourcePixels[l] & 0xFF;
    // if (pixel != 0) {
    // System.err.println("image source is not null at " + x + "x" + y);
    // this.addWhitePixel(x, y);
    // for (int level = 0; level < this.getMipmapNbLevels(); level++) {
    // System.err.println("pixel " + x + "x" + y + " level " + level +
    // " nbWhite = "
    // + this.getCell(this.source2MipMapCoordinate(x, y, level),
    // level).nbWhite);
    // }
    // }
    // }
    // }
    //
    // }

    /**
     * X & Y coordinates are expressed as pixel coordinates in source image
     * 
     * @param x
     *            pixel coordinates in source image
     * @param y
     *            pixel coordinates in source image
     * @param level
     *            mipmap level
     * @return X & Y coordinates are expressed as pixel coordinates in source
     *         image
     */
    public Point source2MipMapCoordinate(double x, double y, int level) {
        return new Point(
                (int) Math.floor (((double)x) * ((double)this.mipmapImageWidthPerLevel[level]) / ((double)this.imageWidth)),
                (int) Math.floor (((double)y) * ((double)this.mipmapImageWidthPerLevel[level]) / ((double)this.imageHeight)));
    }

    /**
     * X & Y coordinates are expressed as pixel coordinates in mipmap
     * coordinates at a given level
     * 
     * @param x
     *            pixel coordinates in mipmap images at the given level
     * @param y
     *            pixel coordinates in mipmap images at the given level
     * @param level
     *            mipmap level
     * @return X & Y coordinates are expressed as mipmap coordinates at the
     *         given level
     */
    public Point mipmap2SourceCoordinates(Point p) {
        // System.err.println(" convert from mipmap = " + p.x + "x" + p.y +
        // " to " + (((double) p.x / this.getMipmapSize() *
        // this.getImageWidth())) + "x"
        // + (((double) p.y / this.getMipmapSize() * this.getImageHeight())));
        return new Point(
            (int) Math.floor(((double) p.x+1) / ((double)this.getMipmapSize()) * ((double)this.getImageWidth())), 
            (int) Math.floor(((double) p.y+1) / ((double)this.getMipmapSize()) * ((double)this.getImageHeight())));
    }

    /**
     * get a cell from a given level (in mipmap pixel coordinates, not image)
     * use source2MipMapCoordinate if you have image source pixel coordinates
     */
    public MipMapCell getCell(int x, int y, int level) {
        return this.getCell(new Point(x, y), level);
    }

    /**
     * get a cell from a given level
     */
    public MipMapCell getCell(Point p, int level) {
        return this.mipmapImages[level].getCell(p);
    }

    /**
     * get the 4 point coordinates from level - 1 matching the pixel at given
     * level
     * 
     * @param x
     *            pixel coordinate at given level
     * @param y
     *            pixel coordinate at given level
     * @param currentLevel
     *            level of current pixel
     * @return 4 point coordinates at level - 1 or null if level == 0
     */
    public Point[] getWiderLevelCellsCoordinates(int x, int y, int currentLevel) {
        if (currentLevel <= 0) {
            return null;
        }
        
        Point[] cellsCoordinates = new Point[4];
        cellsCoordinates[0] = new Point(x * 2, y * 2);
        cellsCoordinates[1] = new Point(x * 2 + 1, y * 2);
        cellsCoordinates[2] = new Point(x * 2, y * 2 + 1);
        cellsCoordinates[3] = new Point(x * 2 + 1, y * 2 + 1);
        return cellsCoordinates;
    }

    /**
     * get the point coordinates from level + 1 matching the pixel at given
     * level
     * 
     * @param x
     *            pixel coordinate at given level
     * @param y
     *            pixel coordinate at given level
     * @param currentLevel
     *            level of current pixel
     * @return point coordinates at level + 1 or null if level == nbLevels - 1
     */
    public Point getSmallerLevelCellsCoordinates(int x, int y, int currentLevel) {
        if (currentLevel >= this.mipmapNbLevels - 1) {
            return null;
        }
        return new Point(x / 2, y / 2);
    }

    /**
     * Add a white pixel at given pixel (image source coordinate system)
     * 
     * @param x
     *            x coordinate expressed in image source coordinate system
     * @param y
     *            y coordinate expressed in image source coordinate system
     */
    public void addWhitePixel(int x, int y) {
        for (int level = 0; level < this.mipmapNbLevels; level++) {
            this.mipmapImages[level].getCell(this.source2MipMapCoordinate(x, y,
                    level)).nbWhite++;
        }
    }

    /**
     * Remove a white pixel at given pixel (image source coordinate system)
     * 
     * @param x
     *            x coordinate expressed in image source coordinate system
     * @param y
     *            y coordinate expressed in image source coordinate system
     *            
     *            
     * 
     */
    public void removeWhitePixel(int x, int y) {
      /// check that the pixel is actually white
      int nbwhite = this.mipmapImages[0].getCell(this.source2MipMapCoordinate(x, y, 0)).nbWhite;
      if ( nbwhite == 1){
        for (int level = 0; level < this.mipmapNbLevels; level++) {
          this.mipmapImages[level].getCell(this.source2MipMapCoordinate(x, y, 
              level)).nbWhite--;
        }
      } else if (nbwhite != 0)
        throw new RuntimeException("Wrong number of white pixels!");
    }

    /**
     * Get the white pixel count at given pixel (image source coordinate system)
     * 
     * @param x
     *            x coordinate expressed in image source coordinate system
     * @param y
     *            y coordinate expressed in image source coordinate system
     */
    public int getWhitePixelCount(int x, int y) {
        return this.mipmapImages[0].getCell(this.source2MipMapCoordinate(x, y,
                0)).nbWhite;
    }

    public int getNbWhite() {
        if (this.mipmapImages == null
                || this.mipmapImages[this.mipmapNbLevels - 1] == null
                || this.mipmapImages[this.mipmapNbLevels - 1]
                        .getCell(zeroPoint) == null) {
            return 0;
        }
        return this.mipmapImages[this.mipmapNbLevels - 1].getCell(zeroPoint).nbWhite;
    }

    public class MipMapImage {

        private int imageWidth = 0;
        private int level = 0;
        private MipMapMask mipmapParent = null;
        private MipMapCell cells[] = null;

        public MipMapImage(MipMapMask mipMap, int size, int level) {
            this.mipmapParent = mipMap;
            this.imageWidth = size;
            this.level = level;
            this.cells = new MipMapCell[size * size];
            for (int n = 0; n < size * size; n++) {
                this.cells[n] = new MipMapCell();
            }
        }

        public MipMapCell getCell(Point p) {
            return this.cells[p.x + p.y * this.imageWidth];
        }

        /**
         * @return the imageWidth
         */
        public int getImageWidth() {
            return this.imageWidth;
        }

        /**
         * @return the level
         */
        public int getLevel() {
            return this.level;
        }

        /**
         * @return the mipmapParent
         */
        public MipMapMask getMipmapParent() {
            return this.mipmapParent;
        }

    }

    public class MipMapCell {
        public int nbWhite = 0;

    }

    public static void save(MipMapMask mask, String filename) {
        BufferedImage bi = new BufferedImage(3 * mask.getMipmapSize() / 2,
                mask.getMipmapSize(), BufferedImage.TYPE_4BYTE_ABGR);
        byte[] pixels = ((DataBufferByte) bi.getRaster().getDataBuffer())
                .getData();

        saveMipMapLevel(pixels, 0, 0, mask, 0);
        int y = 0;
        for (int level = 1; level < mask.getMipmapNbLevels(); level++) {
            saveMipMapLevel(pixels, mask.getMipmapSize(), y, mask, level);
            y += mask.getMipmapImageWidthPerLevel(level);
        }
        try {
            ImageIO.write(bi, "png", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveMipMapLevel(byte[] pixels, int x0, int y0,
            MipMapMask mipmap, int level) {
        int ly = (x0 + y0 * 3 * mipmap.getMipmapSize() / 2) * 4;
        for (int y = 0; y < mipmap.getMipmapImageWidthPerLevel(level); y++) {
            int l = ly + (y * 3 * mipmap.getMipmapSize() / 2) * 4;
            for (int x = 0; x < mipmap.getMipmapImageWidthPerLevel(level); x++, l += 4) {
                double pixelDensity = mipmap.getImageWidth()
                        * mipmap.getImageHeight()
                        / (mipmap.getMipmapImageWidthPerLevel(level) * mipmap
                                .getMipmapImageWidthPerLevel(level));
                int v = Math
                        .max(0,
                                Math.min(255, (int) (255 * mipmap.getCell(x, y,
                                        level).nbWhite / pixelDensity)));
                pixels[l + 0] = (byte) 255;
                if (mipmap.getCell(x, y, level).nbWhite == 0) {
                    pixels[l + 1] = (byte) 0;
                    pixels[l + 2] = (byte) 0;
                    pixels[l + 3] = (byte) 255;
                } else if (mipmap.getCell(x, y, level).nbWhite < 0) {
                    pixels[l + 1] = (byte) 0;
                    pixels[l + 2] = (byte) 255;
                    pixels[l + 3] = (byte) 0;
                } else if (mipmap.getCell(x, y, level).nbWhite == 1) {
                    pixels[l + 1] = (byte) 255;
                    pixels[l + 2] = (byte) 0;
                    pixels[l + 3] = (byte) 0;
                } else {
                    pixels[l + 1] = (byte) v;
                    pixels[l + 2] = (byte) v;
                    pixels[l + 3] = (byte) v;
                }
            }
        }
    }

}
