package fr.ign.cogit.geoxygene.util.conversion;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.imageio.ImageIO;
import javax.media.jai.PlanarImage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.DataSourceException;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * GeoTiff reader.
 * @author Julien Perret
 * 
 */
public class GeoTiffReader {
  /**
   * Logger.
   */
  private static Logger logger = LogManager
      .getLogger(GeoTiffReader.class.getName());

  public static BufferedImage loadGeoTiffImage(String fileName,
      double[][] range, CoordinateReferenceSystem[] crs) {

    // Get the file
    FileInputStream file = null;
    try {
      file = new FileInputStream(fileName);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    if (file == null) {
      return null;
    }

    // Get the channel
    FileChannel fc = file.getChannel();

    // Open the GeoTiff reader
    org.geotools.gce.geotiff.GeoTiffReader reader;
    try {
      // This function always allocates about 23Mb, both for 2Mb and 225Mb
      GeoTiffReader.logger.info("Start reading " + fileName); //$NON-NLS-1$
      ImageIO.setUseCache(false);
      reader = new org.geotools.gce.geotiff.GeoTiffReader(fc);
      GeoTiffReader.logger.info("Done reading"); //$NON-NLS-1$
    } catch (DataSourceException ex) {
      ex.printStackTrace();
      return null;
    }

    // Get the image properties
    GridCoverage2D coverage;
    try {
      coverage = reader.read(null);
    } catch (IOException ex) {
      ex.printStackTrace();
      return null;
    }
    RenderedImage renderedImage = coverage.getRenderedImage();
    Envelope env = coverage.getEnvelope();

    // Range
    range[0][0] = env.getMinimum(0);
    range[0][1] = env.getMaximum(0);
    range[1][0] = env.getMinimum(1);
    range[1][1] = env.getMaximum(1);

    crs[0] = coverage.getCoordinateReferenceSystem2D();

    // Return image
    return PlanarImage.wrapRenderedImage(renderedImage).getAsBufferedImage();
  }
}
