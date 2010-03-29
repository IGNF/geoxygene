package fr.ign.cogit.geoxygene.util.conversion;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.PlanarImage;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.DataSourceException;
import org.opengis.geometry.Envelope;

/**
 * Arc/Info ASCII Grid file reader.
 * @author Julien Perret
 *
 */
public class ArcGridReader {
    public static BufferedImage loadAsc(String fileName,
            double[][] range) {

        // Get the file
        File file = null;
        file = new File(fileName);
        
        // Get the channel
        //FileChannel fc = file.getChannel();

        // Open the GeoTiff reader
        org.geotools.gce.arcgrid.ArcGridReader reader;
        try {
            // This function always allocates about 23Mb, both for 2Mb and 225Mb
            System.out.println("Start reading " + fileName); //$NON-NLS-1$
            ImageIO.setUseCache(false);
            reader = new org.geotools.gce.arcgrid.ArcGridReader(file);
            System.out.println("Done reading"); //$NON-NLS-1$
        } catch (DataSourceException ex) {
            ex.printStackTrace();
            return null;
        }

        // Get the image properties
        GridCoverage2D coverage;
        try {
            coverage = (GridCoverage2D) reader.read(null);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        RenderedImage renderedImage = coverage.getRenderedImage();
        Envelope env = coverage.getEnvelope();
        /*
        int dimensionX = renderedImage.getWidth();
        int dimensionY = renderedImage.getHeight();
        System.out.println(dimensionX+ " X "+dimensionY);
        
        MathTransform transform = coverage.getGridGeometry().getGridToCRS();
        GM_PointGrid pointGrid = new GM_PointGrid();
        for (int x = 0; x < dimensionX; x++) {
            DirectPositionList row = new DirectPositionList();
            for (int y = 0; y < dimensionY; y++) {
                System.out.println(x + " " + y);
                GeneralDirectPosition dest;
                try {
                    dest = (GeneralDirectPosition) transform.transform(
                            new DirectPosition2D(x,y), null);
                    double[] value = renderedImage.getData().getPixel(x, y, new double[1]);
                    System.out.println("-> " + dest.getOrdinate(0) + " " + dest.getOrdinate(1)+ " " + value[0]);
                    row.add(new DirectPosition(dest.getOrdinate(0), dest.getOrdinate(1), value[0]));
                } catch (MismatchedDimensionException e) {
                    e.printStackTrace();
                } catch (TransformException e) {
                    e.printStackTrace();
                }
            }
            pointGrid.addRow(row);
        }
        GM_GriddedSurface grid = new GM_GriddedSurface(pointGrid);
        */
        // Range
        range[0][0] = env.getMinimum(0);
        range[0][1] = env.getMaximum(0);
        range[1][0] = env.getMinimum(1);
        range[1][1] = env.getMaximum(1);
        
        // Get the coordinate system information
        // parseCoordinateSystem(crs.toWKT(), coordSys);

        // Return image
        return PlanarImage.wrapRenderedImage(renderedImage).getAsBufferedImage();
    }
}
