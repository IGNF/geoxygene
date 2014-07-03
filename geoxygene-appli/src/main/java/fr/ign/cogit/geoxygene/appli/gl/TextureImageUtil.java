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
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.vecmath.Point2d;

import org.apache.log4j.Logger;
import org.lwjgl.BufferUtils;

import fr.ign.cogit.geoxygene.appli.gl.GradientTextureImage.TexturePixel;

/**
 * @author JeT Tool collection for texture images
 */
public class TextureImageUtil {

    private static final double PI2 = Math.PI * 2;
    private static final Logger logger = Logger
            .getLogger(TextureImageUtil.class.getName()); // logger

    /**
     * Blur the 'distance' pixel member with a square window
     * 
     * @param image
     *            image to blur
     * @param blurWindowHalfSize
     *            half size of the blur square
     */
    public static void blurDistance(GradientTextureImage image,
            int blurWindowHalfSize) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                pixel.distance = blurDistanceNeighborhood(image, x, y,
                        blurWindowHalfSize);
            }
        }
    }

    /**
     * Compute the blurred value of a given window centered at (x,y)
     * 
     * @param image
     *            pixel content
     * @param x
     *            blur window center
     * @param y
     *            blur window center
     * @param blurWindowHalfSize
     *            half size of the blur window
     * @return
     */
    private static double blurDistanceNeighborhood(GradientTextureImage image,
            int x, int y, int blurWindowHalfSize) {
        double neighborsWeightSum = 0.;
        double blurredDistance = 0;
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

    /**
     * Scan all pixels and multiply uv texture coordinates by the given factor
     * 
     * @param image
     * @param scaleFactor
     */
    public static void rescaleTextureCoordinates(GradientTextureImage image,
            double uScaleFactor, double vScaleFactor) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                if (pixel.in) {
                    pixel.uTexture *= uScaleFactor;
                    pixel.vTexture *= vScaleFactor;
                }
            }
        }

    }

    public static void blurTextureCoordinates(GradientTextureImage image,
            int blurWindowHalfSize) {
        image.invalidateUVBounds();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                Point2d textureCoordinates = blurTextureCoordinatesNeighborhood(
                        image, x, y, blurWindowHalfSize);
                pixel.uTexture = textureCoordinates.x;
                pixel.vTexture = textureCoordinates.y;
            }
        }

    }

    public static Point2d blurTextureCoordinatesNeighborhood(
            GradientTextureImage image, int x, int y, int blurWindowHalfSize) {
        double blurredUcos = 0.;
        double blurredUsin = 0.;
        double neighborsWeightSum = 0.;
        double blurredV = 0;
        for (int dy = -blurWindowHalfSize; dy <= blurWindowHalfSize; dy++) {

            for (int dx = -blurWindowHalfSize; dx <= blurWindowHalfSize; dx++) {
                double neighborWeight = 1.;
                TexturePixel neighbor = image.getPixel(x + dx, y + dy);
                if (neighbor != null && (neighbor.in || neighbor.frontier != 0)
                        && neighbor.vTexture != Double.POSITIVE_INFINITY) {
                    blurredV += neighbor.vTexture * neighborWeight;
                    double uNormalized = (neighbor.uTexture - image.getuMin())
                            / (image.getuMax() - image.getuMin());
                    blurredUcos += Math.cos(PI2 * uNormalized);
                    blurredUsin += Math.sin(PI2 * uNormalized);
                    neighborsWeightSum += neighborWeight;
                }
            }
        }
        double blurredU = 0;
        if (Math.abs(blurredUcos) > 1E-6) {
            blurredU = Math.atan2(blurredUsin, blurredUcos) / PI2;

        }
        blurredU = blurredU * (image.getuMax() - image.getuMin())
                + image.getuMax();
        if (neighborsWeightSum > 1E-6) {
            return new Point2d(blurredU, blurredV / neighborsWeightSum);
        }
        return new Point2d(blurredU, blurredV);
    }

    public static double circularWeightedAverage(double v1, double v2,
            double w1, double w2) {
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

    public static void save(GradientTextureImage image, String filename)
            throws IOException {
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
    public static void saveHeight(GradientTextureImage image, String filename)
            throws IOException {
        File f = new File(filename + ".png");
        BufferedImage rgbImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_RGB);
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
                // int rgb = pixel.frontier ? Color.black.getRGB() :
                // Color.white.getRGB();
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
    public static void saveTextureCoordinates(GradientTextureImage image,
            String filename) throws IOException {
        File f = new File(filename + ".png");
        BufferedImage rgbImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_RGB);
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
     * Apply a texture using (u,v) coordinates contained in the TextureImage. An
     * RGB image with transparent background is generated and returned
     */
    public static BufferedImage applyTexture(GradientTextureImage image,
            BufferedImage texture) {
        // create an image with transparent background
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_ARGB);
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
                    int xTexture = (int) Math.abs(pixel.uTexture
                            * texture.getWidth())
                            % texture.getWidth();
                    int yTexture = (int) Math.abs((1 - pixel.vTexture)
                            * texture.getHeight())
                            % texture.getHeight();
                    // System.err.println("pixel = " + pixel + " => " + xTexture
                    // + "x" + yTexture + "");
                    rgb = texture.getRGB(xTexture, yTexture);
                    bufferedImage.setRGB(x, y, rgb);
                }
            }
        }
        return bufferedImage;
    }

    /**
     * code texture coordinates (u,v) with saturation and hue. u & v coordinates
     * must be between 0 & 1
     */
    public static BufferedImage toHSB(GradientTextureImage image) {
        // create an image with transparent background
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_ARGB);
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
                    double u = pixel.uTexture;
                    double v = pixel.vTexture;
                    rgb = Color.HSBtoRGB((float) u, (float) (1. - v), 1.f);
                    bufferedImage.setRGB(x, y, rgb);
                }
            }
        }
        return bufferedImage;
    }

    /**
     * code texture coordinates (u,v) with saturation and hue. u & v coordinates
     * must be between 0 & 1
     */
    public static BufferedImage toColors(GradientTextureImage image,
            Color uMinColor, Color uMaxColor, Color vMaxColor) {
        double uMin = Double.MAX_VALUE, uMax = -Double.MAX_VALUE;
        double vMin = Double.MAX_VALUE, vMax = -Double.MAX_VALUE;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                int rgb = 0;
                if (pixel.in || pixel.frontier != 0) {
                    double u = pixel.uTexture;
                    double v = pixel.vTexture;
                    uMin = Math.min(uMin, u);
                    uMax = Math.max(uMax, u);
                    vMin = Math.min(vMin, v);
                    vMax = Math.max(vMax, v);
                }
            }
        }
        // create an image with transparent background
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_ARGB);
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
                    double u = pixel.uTexture;
                    double v = pixel.vTexture;
                    float uf = (float) ((u - uMin) / (uMax - uMin));
                    Color uColor = new Color(
                            (int) (uMaxColor.getRed() * uf + uMinColor.getRed()
                                    * (1 - uf)), (int) (uMaxColor.getGreen()
                                    * uf + uMinColor.getGreen() * (1 - uf)),
                            (int) (uMaxColor.getBlue() * uf + uMinColor
                                    .getBlue() * (1 - uf)));
                    float vf = (float) ((v - vMin) / (vMax - vMin));
                    rgb = new Color(
                            (int) (vMaxColor.getRed() * vf + uColor.getRed()
                                    * (1 - vf)), (int) (vMaxColor.getGreen()
                                    * vf + uColor.getGreen() * (1 - vf)),
                            (int) (vMaxColor.getBlue() * vf + uColor.getBlue()
                                    * (1 - vf))).getRGB();
                    bufferedImage.setRGB(x, y, rgb);
                }
            }
        }
        return bufferedImage;
    }

    /**
     * code v texture coordinates as level of colors
     */
    public static BufferedImage toHeight(GradientTextureImage image,
            Color vMinColor, Color vMaxColor) {
        double vMin = Double.MAX_VALUE, vMax = -Double.MAX_VALUE;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                if (pixel.in || pixel.frontier != 0) {
                    double v = pixel.vTexture;
                    vMin = Math.min(vMin, v);
                    vMax = Math.max(vMax, v);
                }
            }
        }
        // create an image with transparent background
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_INT_ARGB);
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
                    double v = pixel.vTexture;
                    float vf = (float) ((v - vMin) / (vMax - vMin));
                    rgb = new Color(
                            (int) (vMaxColor.getRed() * vf + vMinColor.getRed()
                                    * (1 - vf)), (int) (vMaxColor.getGreen()
                                    * vf + vMinColor.getGreen() * (1 - vf)),
                            (int) (vMaxColor.getBlue() * vf + vMinColor
                                    .getBlue() * (1 - vf))).getRGB();
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
    public static void checkTextureCoordinates(GradientTextureImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                TexturePixel pixel = image.getPixel(x, y);
                if (pixel.in || pixel.frontier != 0) {
                    if (pixel.uTexture > 1. && pixel.uTexture < 0.
                            || pixel.vTexture > 1. && pixel.vTexture < 0.) {
                        logger.warn("invalid texture coordinate for pixel " + x
                                + "x" + y + " "
                                + (pixel.in ? "in" : "frontier") + " => u="
                                + pixel.uTexture + " v=" + pixel.vTexture);
                    }
                }
            }
        }

    }

    /**
     * Convert a texture image into a byte buffer containing (u,v) couple of
     * float values for each pixel. non defined coordinates are set to (0,0)
     * 
     * @param image
     *            image to be converted
     * @return
     */
    public static ByteBuffer toFloatBuffer(GradientTextureImage image) {
        if (image == null) {
            return null;
        }

        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(image.getWidth()
                * image.getHeight() * 2 * Float.SIZE / 8);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                // byteBuffer.putFloat((float) x / (float) image.getWidth());
                // byteBuffer.putFloat((float) y / (float) image.getHeight());
                TexturePixel pixel = image.getPixel(x, y);
                if (pixel.in) {
                    // System.err.println("uv texture (" + x + "," + y + ") = "
                    // + pixel.uTexture + "x" + pixel.vTexture);
                    byteBuffer.putFloat((float) Math.abs(pixel.uTexture)); // U
                                                                           // component
                    byteBuffer.putFloat((float) Math.abs(pixel.vTexture)); // V
                                                                           // component
                } else {
                    byteBuffer.putFloat(-1f); // U component
                    byteBuffer.putFloat(-1f); // V component
                }
            }
        }
        byteBuffer.rewind();
        // for (int y = 0; y < image.getHeight(); y++) {
        // for (int x = 0; x < image.getWidth(); x++) {
        // float u = byteBuffer.getFloat();
        // float v = byteBuffer.getFloat();
        // if (u != -1 && v != -1) {
        // System.err.println("pixel(" + x + "," + y + ") = " + u + "x" + v);
        // }
        // }
        // }
        return byteBuffer;
    }

    public static void displayPixel(GradientTextureImage texImage, int i, int j) {
        TexturePixel pixel = texImage.getPixel(i, j);
        System.err.println("pixel(" + i + "," + j + ") = " + pixel.uTexture
                + "x" + pixel.vTexture + " "
                + (pixel.in ? "inside" : "outside") + " frontier = "
                + (pixel.frontier));
    }
}
