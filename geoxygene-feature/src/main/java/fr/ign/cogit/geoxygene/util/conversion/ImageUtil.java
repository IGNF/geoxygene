package fr.ign.cogit.geoxygene.util.conversion;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.opengis.geometry.Envelope;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_Coverage;

public class ImageUtil {

  public static GridCoverage2D bufferredImageToGridCovergade2D(
      BufferedImage image,
      IFeatureCollection<? extends IFeature> templateFeatColl) {

    Envelope env = ((FT_Coverage) templateFeatColl.get(0)).coverage()
        .getEnvelope();
    return bufferedImageToGridCoverage2D(image, env);
  }

  public static GridCoverage2D bufferredImageToGridCovergade2D(
      BufferedImage image, GridCoverage2D sourceCoverage) {
    Envelope env = sourceCoverage.getEnvelope();
    return bufferedImageToGridCoverage2D(image, env);
  }

  public static GridCoverage2D bufferedImageToGridCoverage2D(
      BufferedImage image, Envelope env) {
    GridCoverageFactory coverageFactory = new GridCoverageFactory();
    GridCoverage2D newCoverage = coverageFactory.create("coverage", image, env);
    return newCoverage;
  }

  public static BufferedImage toBufferedImage(RenderedImage img) {
    if (img instanceof BufferedImage) {
      return (BufferedImage) img;
    }

    ColorModel cm = img.getColorModel();

    int width = img.getWidth();
    int height = img.getHeight();
    WritableRaster raster = cm.createCompatibleWritableRaster(width, height);
    boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
    Hashtable<String, Object> properties = new Hashtable<String, Object>();
    String[] keys = img.getPropertyNames();
    if (keys != null) {
      for (int i = 0; i < keys.length; i++) {
        properties.put(keys[i], img.getProperty(keys[i]));
      }
    }

    BufferedImage tempImg = new BufferedImage(cm, raster, isAlphaPremultiplied,
        properties);
    img.copyData(raster);

    BufferedImage resultImg = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_ARGB);
    Graphics g = resultImg.createGraphics();
    g.drawImage(tempImg, 0, 0, null);

    return resultImg;
  }
}
