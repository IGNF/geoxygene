/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.util.color;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * @author Julien Perret
 * 
 */
public class ColorUtil {
  static Logger logger = Logger.getLogger(ColorUtil.class.getName());

  public static Map<Color, float[]> labMap = new HashMap<Color, float[]>();
  public static final int RGB = 0;
  public static final int LAB = 1;
  public static final int XYZ = 2;
  private static int colorSpace = ColorUtil.LAB;

  public static int getColorSpace() {
    return ColorUtil.colorSpace;
  }

  public static void setColorSpace(int colorSpace) {
    ColorUtil.colorSpace = colorSpace;
  }

  public static final int UNIFORM = 0;
  public static final int AB = 1;
  public static final int LAABB = 2;
  public static final int RGGB = 3;
  private static float[] weights = { 1f, 1f, 1f };

  public static void setWeights(int type) {
    if (type == ColorUtil.AB) {
      ColorUtil.weights = new float[] { 0f, 1f, 1f };
    } else if (type == ColorUtil.UNIFORM) {
      ColorUtil.weights = new float[] { 1f, 1f, 1f };
    } else if (type == ColorUtil.LAABB) {
      ColorUtil.weights = new float[] { 1f, 2f, 2f };
    } else if (type == ColorUtil.RGGB) {
      ColorUtil.weights = new float[] { 1f, 2f, 1f };
    }
  }

  /**
   * @param color a color
   * @return the color in LAB reference system
   */
  public static float[] toLab(Color color) {
    float[] lab = ColorUtil.labMap.get(color);
    if (lab == null) {
      lab = ColorUtil.toLab(ColorUtil.toXyz(color));
      ColorUtil.labMap.put(color, lab);
    }
    return lab;
  }

  /**
   * @param color a color
   * @return the color in XYZ reference system
   */
  public static float[] toXyz(Color color) {
    return color.getColorComponents(ColorSpace
        .getInstance(ColorSpace.CS_CIEXYZ), null);
  }

  /**
   * @param xyz a color in XYZ reference system
   * @return the color in LAB reference system
   */
  public static float[] toLab(float[] xyz) {
    float x = xyz[0];
    float y = xyz[1];
    float z = xyz[2];
    float l = 116f * ColorUtil.f(y) - 16f;
    float a = 500f * (ColorUtil.f(x) - ColorUtil.f(y));
    float b = 200f * (ColorUtil.f(y) - ColorUtil.f(z));
    return new float[] { l, a, b };
  }

  /**
   * Helper function for the translation of XYZ to LAB
   * @param t parameter
   * @return result of the helper function
   */
  private static float f(float t) {
    return (t > ColorUtil.delta * ColorUtil.delta * ColorUtil.delta) ? (float) Math
        .pow(t, 1f / 3f)
        : t / (3f * ColorUtil.delta * ColorUtil.delta) + 4f / 29f;
  }

  private static float delta = 6f / 29f;

  public static Color toColor(float[] lab) {
    float fy = (lab[0] + 16f) / 116f;
    float[] f = new float[] { fy + lab[1] / 500f, fy, fy - lab[2] / 200f };
    float[] xyz = new float[3];
    for (int i = 0; i < 3; i++) {
      xyz[i] = (f[i] > ColorUtil.delta) ? f[i] * f[i] * f[i]
          : (f[i] - 16f / 116f) * ColorUtil.delta * ColorUtil.delta * 3f;
      xyz[i] = Math.max(0.0f, Math.min(1.0f, xyz[i]));
    }
    return new Color(ColorSpace.getInstance(ColorSpace.CS_CIEXYZ), xyz, 1f);
  }

  public static float[] toXyz(float[] lab) {
    float fy = (lab[0] + 16f) / 116f;
    float[] f = new float[] { fy + lab[1] / 500f, fy, fy - lab[2] / 200f };
    float[] xyz = new float[3];
    for (int i = 0; i < 3; i++) {
      xyz[i] = (f[i] > ColorUtil.delta) ? f[i] * f[i] * f[i]
          : (f[i] - 16f / 116f) * ColorUtil.delta * ColorUtil.delta * 3f;
    }
    return xyz;
  }

  /**
   * Square Euclidean distance between 2 colors in LAB
   * @param lab1 first color
   * @param lab2 second color
   * @return the square Euclidean distance between the 2 colors in parameter
   * @see #sqDistanceLab(Color, Color)
   */
  public static float sqDistance(float[] lab1, float[] lab2) {
    float result = 0;
    for (int i = 0; i < 3; i++) {
      float x = lab1[i] - lab2[i];
      result += ColorUtil.weights[i] * x * x;
    }
    return result;
  }

  /**
   * Square Euclidean distance between 2 colors
   * @param color1 first color
   * @param color2 second color
   * @return the square Euclidean distance between the 2 colors in parameter
   */
  public static float sqDistance(Color color1, Color color2) {
    if (ColorUtil.colorSpace == ColorUtil.RGB) {
      return ColorUtil.sqDistanceRgb(color1, color2);
    } else if (ColorUtil.colorSpace == ColorUtil.LAB) {
      return ColorUtil.sqDistanceLab(color1, color2);
    } else {
      return ColorUtil.sqDistanceXyz(color1, color2);
    }
  }

  /**
   * Square Euclidean distance between 2 colors in LAB
   * @param color1 first color
   * @param color2 second color
   * @return the square Euclidean distance between the 2 colors in parameter
   */
  public static float sqDistanceLab(Color color1, Color color2) {
    return ColorUtil.sqDistance(ColorUtil.toLab(color1), ColorUtil
        .toLab(color2));
  }

  /**
   * Square Euclidean distance between 2 colors in RGB
   * @param color1 first color
   * @param color2 second color
   * @return the square Euclidean distance between the 2 colors in parameter
   */
  public static float sqDistanceRgb(Color color1, Color color2) {
    int r = color1.getRed() - color2.getRed();
    int g = color1.getGreen() - color2.getGreen();
    int b = color1.getBlue() - color2.getBlue();
    return ColorUtil.weights[0] * r * r + ColorUtil.weights[1] * g * g
        + ColorUtil.weights[2] * b * b;
  }

  /**
   * Square Euclidean distance between 2 colors in XYZ
   * @param color1 first color
   * @param color2 second color
   * @return the square Euclidean distance between the 2 colors in parameter
   */
  public static float sqDistanceXyz(Color color1, Color color2) {
    return ColorUtil.sqDistance(ColorUtil.toXyz(color1), ColorUtil
        .toXyz(color2));
  }

  public static void writeChromaticityImage(Collection<Color> colors,
      int imageSize, int clusterSize, String chromaticityImageName) {
    BufferedImage chromaticityImage = ColorUtil.buildChromaticityImage(colors,
        imageSize, clusterSize);
    ColorUtil.writeImage(chromaticityImage, chromaticityImageName);
  }

  public static BufferedImage buildChromaticityImage(Collection<Color> colors,
      int imageSize, int clusterSize) {
    BufferedImage clusterImage = new BufferedImage(imageSize, imageSize,
        BufferedImage.TYPE_INT_RGB);
    Graphics2D graphics = clusterImage.createGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
        RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
    graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
        RenderingHints.VALUE_COLOR_RENDER_SPEED);
    graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_SPEED);
    graphics.setStroke(new BasicStroke(1.0f));
    int halfImageSize = imageSize / 2;
    for (Color color : colors) {
      if (color == null) {
        continue;
      }
      float[] lab = ColorUtil.toLab(color);
      int a = new Float(halfImageSize * lab[1] / 100f).intValue()
          + halfImageSize;
      int b = new Float(halfImageSize * lab[2] / 100f).intValue()
          + halfImageSize;
      graphics.setColor(color);
      graphics.fillOval(a - clusterSize / 2, b - clusterSize / 2, clusterSize,
          clusterSize);
    }
    return clusterImage;
  }

  public static void writePaletteImage(Color[] colors, int sizeElement,
      String imageName) {
    BufferedImage image = ColorUtil.buildPaletteImage(colors, sizeElement);
    ColorUtil.writeImage(image, imageName);
  }

  public static void writePaletteImage(Color[] colors, int sizeElement,
      String imageName, int numberOfLines, int numberOfColumns) {
    BufferedImage image = ColorUtil.buildPaletteImage(colors, sizeElement,
        numberOfLines, numberOfColumns);
    ColorUtil.writeImage(image, imageName);
  }

  /**
   * Build an image representing the palette.
   * @param colors the palette's list of colors
   * @param sizeElement size of the palette elements
   * @return an image representing the palette
   */
  public static BufferedImage buildPaletteImage(Color[] colors, int sizeElement) {
    int numberOfColors = colors.length;
    // compute the number of columns and lines to get as close to a square as
    // possible
    int numberOfColumns = (int) Math.sqrt(numberOfColors);
    int numberOfLines = numberOfColors / numberOfColumns;
    if (numberOfColors > numberOfColumns * numberOfLines) {
      numberOfLines++;
    }
    return buildPaletteImage(colors, sizeElement, numberOfLines, numberOfColumns);
  }

  /**
   * Build an image representing the palette.
   * @param colors the palette's list of colors
   * @param sizeElement size of the palette elements
   * @return an image representing the palette
   */
  public static BufferedImage buildPaletteImage(Color[] colors,
      int sizeElement, int numberOfLines, int numberOfColumns) {
    BufferedImage image = new BufferedImage(numberOfColumns * sizeElement,
        numberOfLines * sizeElement, BufferedImage.TYPE_INT_ARGB);
    for (int index = 0; index < colors.length; index++) {
      int w = (index % numberOfColumns) * sizeElement;
      int h = (index / numberOfColumns) * sizeElement;
      for (int i = 0; i < sizeElement; i++) {
        for (int j = 0; j < sizeElement; j++) {
          if (colors[index] != null) {
            image.setRGB(w + i, h + j, colors[index].getRGB());
          }
        }
      }
    }
    return image;
  }

  public static void writeProportionalPaletteImage(BufferedImage image,
      String imageName) {
    BufferedImage paletteImage = ColorUtil.buildProportionalPaletteImage(image);
    ColorUtil.writeImage(paletteImage, imageName);
  }

  public static BufferedImage buildProportionalPaletteImage(BufferedImage image) {
    long t = System.currentTimeMillis();
    BufferedImage paletteImage = new BufferedImage(image.getWidth(), image
        .getHeight(), BufferedImage.TYPE_INT_RGB);
    Set<Color> colors = ColorUtil.getColors(image);
    int x = 0;
    int y = 0;
    Map<Color, Integer> occurrenceMap = ColorUtil.occurrenceMap(image);
    for (Color color : colors) {
      // count the occurence of this color in the image
      int occurrence = occurrenceMap.get(color).intValue();
      for (int count = 0; count < occurrence; count++) {
        paletteImage.setRGB(x, y, color.getRGB());
        if (x < image.getWidth() - 1) {
          x++;
        } else {
          x = 0;
          y++;
        }
      }
    }
    t = System.currentTimeMillis() - t;
    if (ColorUtil.logger.isDebugEnabled()) {
      ColorUtil.logger
          .debug("The construction of the Proportional Palette Image took " + t
              + " ms");
    }
    return paletteImage;
  }

  /**
   * @param image
   * @return a map containing the number of occurences for all colors found
   */
  public static Map<Color, Integer> occurrenceMap(BufferedImage image) {
    Map<Color, Integer> map = new HashMap<Color, Integer>();
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        Color color = new Color(image.getRGB(x, y));
        if (map.get(color) == null) {
          map.put(color, new Integer(1));
        } else {
          map.put(color, new Integer(map.get(color).intValue() + 1));
        }
      }
    }
    return map;
  }

  /**
   * Remaps the given image using the given colors.
   * @param imageToReMap image to remap
   */
  public static BufferedImage reMap(BufferedImage imageToReMap,
      Collection<Color> colors) {
    BufferedImage remappedImage = new BufferedImage(imageToReMap.getWidth(),
        imageToReMap.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < imageToReMap.getHeight(); y++) {
      for (int x = 0; x < imageToReMap.getWidth(); x++) {
        Color color = new Color(imageToReMap.getRGB(x, y));
        color = ColorUtil.findClosestColor(colors, color);
        remappedImage.setRGB(x, y, color.getRGB());
      }
    }
    return remappedImage;
  }

  /**
   * @param colors
   * @param color
   * @return
   */
  private static Color findClosestColor(Collection<Color> colors, Color color) {
    float minDistance = Float.MAX_VALUE;
    Color bestColor = null;
    for (Color comparedColor : colors) {
      float distance = ColorUtil.sqDistance(color, comparedColor);
      if (distance < minDistance) {
        minDistance = distance;
        bestColor = comparedColor;
      }
    }
    return bestColor;
  }

  /**
   * @param image
   * @param imageName
   */
  public static void writeImage(BufferedImage image, String imageName) {
    if (ColorUtil.logger.isDebugEnabled()) {
      ColorUtil.logger.debug("Writing image " + imageName);
    }
    try {
      ImageIO.write(image, "PNG", new File(imageName));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Set<Color> getColors(BufferedImage image) {
    Set<Color> allColors = new HashSet<Color>();
    // For each pixel of the input image, insert the color into the octree
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        Color color = new Color(image.getRGB(x, y));
        allColors.add(color);
      }
    }
    return allColors;
  }

  /**
   * Convert a color to grayscale.
   * @param color a rgb color
   * @return a grayscale color
   */
  public static int toGray(Color color) {
    return (int) (0.299d * color.getRed() + 0.587d * color.getGreen() + 0.114d * color
        .getBlue());
  }
}
