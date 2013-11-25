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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.vecmath.Point2d;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.gl.TextureImage.TexturePixel;

/**
 * @author JeT
 *         Tool collection for texture images
 */
public class TextureImageUtil {

    private static final double PI2 = Math.PI * 2;
    private static final Logger logger = Logger.getLogger(TextureImageUtil.class.getName()); // logger

    public static void blurDistance(TextureImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                pixel.distance = blurDistanceNeighborhood(image, x, y);
            }
        }

    }

    public static double blurDistanceNeighborhood(TextureImage image, int x, int y) {
        double neighborsWeightSum = 0.;
        double blurredDistance = 0;
        final int blurWindowHalfSize = 10;
        for (int dy = -blurWindowHalfSize; dy <= blurWindowHalfSize; dy++) {

            for (int dx = -blurWindowHalfSize; dx <= blurWindowHalfSize; dx++) {
                double neighborWeight = 1.;
                TexturePixel neighbor = image.getPixel(x + dx, y + dy);
                if (neighbor != null && neighbor.in) {
                    blurredDistance += neighbor.distance;
                    neighborsWeightSum += neighborWeight;
                }
            }
        }
        if (neighborsWeightSum > 1E-6) {
            return blurredDistance / neighborsWeightSum;
        }
        return blurredDistance;
    }

    public static void blurTextureCoordinates(TextureImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                Point2d textureCoordinates = blurTextureCoordinatesNeighborhood(image, x, y);
                pixel.uTexture = textureCoordinates.x;
                pixel.vTexture = textureCoordinates.y;
            }
        }

    }

    public static Point2d blurTextureCoordinatesNeighborhood(TextureImage image, int x, int y) {
        double blurredUcos = 0.;
        double blurredUsin = 0.;
        double neighborsWeightSum = 0.;
        double blurredV = 0;
        final int blurWindowHalfSize = 5;
        for (int dy = -blurWindowHalfSize; dy <= blurWindowHalfSize; dy++) {

            for (int dx = -blurWindowHalfSize; dx <= blurWindowHalfSize; dx++) {
                double neighborWeight = 1.;
                TexturePixel neighbor = image.getPixel(x + dx, y + dy);
                if (neighbor != null && (neighbor.in || neighbor.frontier != 0)) {
                    blurredV += neighbor.vTexture * neighborWeight;
                    blurredUcos += neighborWeight * Math.cos(PI2 * neighbor.uTexture);
                    blurredUsin += neighborWeight * Math.sin(PI2 * neighbor.uTexture);
                    neighborsWeightSum += neighborWeight;
                }
            }
        }
        double blurredU = 0;
        if (Math.abs(blurredUcos) > 1E-6) {
            blurredU = Math.atan2(blurredUsin, blurredUcos) / PI2;

        }
        if (neighborsWeightSum > 1E-6) {
            return new Point2d(blurredU, blurredV / neighborsWeightSum);
        }
        return new Point2d(blurredU, blurredV / neighborsWeightSum);
    }

    public static double circularWeightedAverage(double v1, double v2, double w1, double w2) {
        if (Math.abs(v2 - v1) > 0.5) {
            double average = (w1 * v1 + w2 * v2) / (w1 + w2);
            if (average < 0.5) {
                return average + 0.5;
            }
            return average - 0.5;
        } else {
            return (w1 * v1 + w2 * v2) / (w1 + w2);
        }
    }

    public static void save(TextureImage image, String filename) throws IOException {
        saveTextureCoordinates(image, filename + "-t");
        saveHeight(image, filename + "-h");
    }

    /**
     * Save this image height field to a file using grey gradient and a yellow
     * border for outer frontier, red border for inner frontiers
     * 
     * @param image
     * @param filename
     * @throws IOException
     */
    public static void saveHeight(TextureImage image, String filename) throws IOException {
        File f = new File(filename + ".png");
        BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        double minD = Double.MAX_VALUE;
        double maxD = -Double.MAX_VALUE;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                double d = pixel.distance;
                if (d > maxD) {
                    maxD = d;
                }
                if (d < minD) {
                    minD = d;
                }
            }

        }
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                int rValue = (int) (255. * (pixel.distance - minD) / (maxD - minD));
                rValue = Math.max(0, Math.min(255, rValue));

                Color rgb = new Color(30, 30, 60);
                if (pixel.in) {
                    rgb = new Color(rValue, rValue, rValue);
                }
                if (pixel.frontier > 0) {
                    rgb = Color.yellow;
                }
                if (pixel.frontier < 0) {
                    rgb = Color.red;
                }
                //                int rgb = pixel.frontier ? Color.black.getRGB() : Color.white.getRGB();
                rgbImage.setRGB(x, y, rgb.getRGB());
            }
        }
        logger.debug("Save image '" + f.getName() + "'");
        ImageIO.write(rgbImage, "png", f);
    }

    /**
     * Save this image texture coordinates to a file using special colors
     * 
     * @param image
     * @param filename
     * @throws IOException
     */
    public static void saveTextureCoordinates(TextureImage image, String filename) throws IOException {
        File f = new File(filename + ".png");
        BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                int rgb = 0;
                if (pixel.in || pixel.frontier != 0) {
                    double u = pixel.uTexture;
                    double v = pixel.vTexture;
                    rgb = Color.HSBtoRGB((float) u, (float) (1. - v), 1.f);
                }

                rgbImage.setRGB(x, y, rgb);
            }

        }

        logger.debug("Save image '" + f.getName() + "'");
        ImageIO.write(rgbImage, "png", f);
    }

    /**
     * Apply a texture using (u,v) coordinates contained in the TextureImage.
     * An RGB image with transparent background is generated and returned
     */
    public static BufferedImage applyTexture(TextureImage image, BufferedImage texture) {
        // create an image with transparent background
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        // fill every image pixel
        g2d.setComposite(AlphaComposite.Src);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                int rgb = 0;
                if (pixel.in || pixel.frontier != 0) {
                    int xTexture = (int) Math.abs(pixel.uTexture * texture.getWidth()) % texture.getWidth();
                    int yTexture = (int) Math.abs(pixel.vTexture * texture.getHeight()) % texture.getHeight();
                    //                    System.err.println("pixel = " + pixel + " => " + xTexture + "x" + yTexture + "");
                    rgb = texture.getRGB(xTexture, yTexture);
                    bufferedImage.setRGB(x, y, rgb);
                }
            }
        }
        return bufferedImage;
    }

    /**
     * Check if all texture coordinates values are in [0..1] for in and frontier
     * points
     * 
     * @param textureImage
     */
    public static void checkTextureCoordinates(TextureImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                if (pixel.in || pixel.frontier != 0) {
                    if (pixel.uTexture > 1. && pixel.uTexture < 0. || pixel.vTexture > 1. && pixel.vTexture < 0.) {
                        logger.warn("invalid texture coordinate for pixel " + x + "x" + y + " " + (pixel.in ? "in" : "frontier") + " => u=" + pixel.uTexture
                                + " v=" + pixel.vTexture);
                    }
                }
            }
        }

    }
}
