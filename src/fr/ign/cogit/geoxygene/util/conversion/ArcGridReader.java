/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
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

        BufferedImage bufferedImage = PlanarImage.wrapRenderedImage(renderedImage).getAsBufferedImage();
        /*System.out.println("subsampling");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName+"subsampled.asc"));
            int numColumns = renderedImage.getWidth() * 2 - 1;
            int numRows = renderedImage.getHeight() * 2 - 1;
            writer.write("ncols " + numColumns);
            writer.newLine();
            writer.write("nrows " + numRows);
            writer.newLine();
            writer.write("xllcorner " + range[0][0]);
            writer.newLine();
            writer.write("yllcorner " + range[1][0]);
            writer.newLine();
            writer.write("cellsize " + ((range[0][1] - range[0][0]) / numColumns));
            writer.newLine();
            writer.write("NODATA_value " + -9999);
            writer.newLine();
            for (int j = 0; j < renderedImage.getHeight(); j++) {
                double pijy = toY(j, renderedImage, range);
                for (int i = 0; i < renderedImage.getWidth(); i++) {
                    double pijx = toX(i, renderedImage, range);
                    System.out.println("Pij = " + pijx+ " " + pijy);
                    writer.write(""+altitude(bufferedImage, range, pijx, pijy) + " ");
                    double pijx2 = toX(i + 1, renderedImage, range);
                    if (i < renderedImage.getWidth() - 1) {
                        writer.write(""+altitude(bufferedImage, range, (pijx + pijx2) / 2, pijy) + " ");
                    }
                }
                writer.newLine();
                if (j < renderedImage.getHeight() - 1) {
                    double pijy2 = toY(j+1, renderedImage, range);
                    for (int i = 0; i < renderedImage.getWidth(); i++) {
                        double pijx = toX(i, renderedImage, range);
                        writer.write(""+altitude(bufferedImage, range, pijx, (pijy + pijy2) / 2) + " ");
                        double pijx2 = toX(i + 1, renderedImage, range);
                        if (i < renderedImage.getWidth() - 1) {
                            writer.write(""+altitude(bufferedImage, range, (pijx + pijx2) / 2, (pijy + pijy2) / 2) + " ");
                        }
                    }
                    writer.newLine();
                }
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("subsampling finished");*/
        // Return image
        return bufferedImage;
    }

    private static double toY(int j, RenderedImage renderedImage,
                double[][] range) {
        return ((double) j * (range[1][1] - range[1][0]) / ((double)renderedImage.getHeight()) + range[1][0]);
    }

    private static double toX(int i, RenderedImage renderedImage,
                double[][] range) {
        return ((double) i * (range[0][1] - range[0][0]) / ((double)renderedImage.getWidth()) + range[0][0]);
    }

    public static double altitude(BufferedImage image, double[][] range, double x, double y) {
        double minx = range[0][0];
        double maxx = range[0][1];
        double miny = range[1][0];
        double maxy = range[1][1];
        if (x < minx || x > maxx || y < miny || y > maxy) { return -999.0; }
        double w = (x - minx) / (maxx - minx);
        double h = (y - miny) / (maxy - miny);
        double dw = w * image.getRaster().getWidth();
        double dh = h * image.getRaster().getHeight();
        int i = (int) dw;
        int j = (int) dh;
        double zA=image.getRaster().getPixel(i, j, new double[1])[0];
        if (i == image.getRaster().getWidth() - 1 || j == image.getRaster().getHeight() -1 || ((dw == i && dh == j))) {
            return zA;
        }
        double zB=image.getRaster().getPixel(i+1, j, new double[1])[0];
        double zC=image.getRaster().getPixel(i, j+1, new double[1])[0];
        double zD=image.getRaster().getPixel(i+1, j+1, new double[1])[0];
        double weight = 1;
        double wA = Math.pow(distance(dw, dh, i, j), -weight);
        double wB = Math.pow(distance(dw, dh, i+1, j), -weight);
        double wC = Math.pow(distance(dw, dh, i, j+1), -weight);
        double wD = Math.pow(distance(dw, dh, i+1, j+1), -weight);
        double altitude =  (wA * zA + wB * zB + wC * zC + wD * zD) / (wA + wB + wC + wD);
        return altitude;
    }
    private static double distance (double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}
