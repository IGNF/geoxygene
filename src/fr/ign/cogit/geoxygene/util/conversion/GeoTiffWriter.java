package fr.ign.cogit.geoxygene.util.conversion;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.geotools.coverage.CoverageFactoryFinder;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.geometry.Envelope2D;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.ign.cogit.geoxygene.appli.ProjectFrame;

/**
 * GeoTiff writer.
 * 
 * @author Elodie Buard
 */
public class GeoTiffWriter {
	/**
	 * Logger.
	 */
	private static Logger logger = Logger.getLogger(GeoTiffWriter.class
			.getName());

	/**
	 * Write a GeoTiff image.
	 * 
	 * @param image
	 *            the input image
	 * @param range
	 *            the envelope coordinates
	 * @param crs
	 *            the coordinate reference system
	 * @param file
	 *            the file in which to save the geotiff
	 */
	public static void writeGeoTiffImage(BufferedImage image, double[][] range,
			CoordinateReferenceSystem crs, String file) {
		// create a coverage
		GridCoverageFactory factory = CoverageFactoryFinder
				.getGridCoverageFactory(null);
		Envelope2D envelope = new Envelope2D(crs, range[0][0], range[0][1],
				range[1][0], range[1][1]);
		GridCoverage2D coverage = factory.create(null, image, envelope);

		// Get the file
		FileOutputStream fileName = null;
		try {
			fileName = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (file == null) {
			return;
		}
		// Open the GeoTiff writer
		try {
			// This function always allocates about 23Mb, both for 2Mb and 225Mb
			logger.info("Start writing " + fileName); //$NON-NLS-1$
			ImageIO.setUseCache(false);
			org.geotools.gce.geotiff.GeoTiffWriter writer = new org.geotools.gce.geotiff.GeoTiffWriter(
					fileName);
			writer.write(coverage, null);
			logger.info("Done writing"); //$NON-NLS-1$
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
