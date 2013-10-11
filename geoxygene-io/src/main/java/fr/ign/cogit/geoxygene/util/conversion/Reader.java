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

package fr.ign.cogit.geoxygene.util.conversion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.prj.PrjFileReader;
import org.geotools.data.shapefile.shp.ShapeType;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * @author Julien Perret
 * 
 */
public class Reader {

    /** LOGGER. */
    private final static Logger LOGGER = Logger.getLogger(Reader.class.getName());
    
    String shapefileName;
    double minX;
    double maxX;
    double minY;
    double maxY;
    int nbFields;
    int nbFeatures;
    Object[][] fieldValues;
    String[] fieldNames;
    Class<?>[] fieldClasses;
    Geometry[] geometries;
    Class<? extends GM_Object> shapeType;
    CoordinateReferenceSystem localCRS;

    public Reader(String shapefileName) throws MalformedURLException {
        
        this.shapefileName = shapefileName;
        
        ShapefileReader shapefileReader = null;
        DbaseFileReader dbaseFileReader = null;
        PrjFileReader prjFileReader = null;
        ShpFiles shpf;
        shpf = new ShpFiles(shapefileName);
        try {
            shapefileReader = new ShapefileReader(shpf, true, false, new GeometryFactory());
            dbaseFileReader = new DbaseFileReader(shpf, true, Charset.forName("ISO-8859-1")); //$NON-NLS-1$
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.FINE, I18N.getString("ShapefileReader.File") + shapefileName //$NON-NLS-1$
                    + I18N.getString("ShapefileReader.NotFound")); //$NON-NLS-1$
            return;
        } catch (ShapefileException e) {
            LOGGER.log(Level.SEVERE, I18N.getString("ShapefileReader.ErrorReadingShapefile") //$NON-NLS-1$
                    + shapefileName);
            return;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, I18N.getString("ShapefileReader.ErrorReadingFile") //$NON-NLS-1$
                    + shapefileName);
            e.printStackTrace();
            return;
        }
        
        try {
            prjFileReader = new PrjFileReader(shpf);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.FINE, I18N.getString("ShapefileReader.PrjFile") + shapefileName //$NON-NLS-1$
                    + I18N.getString("ShapefileReader.NotFound")); //$NON-NLS-1$
        } catch (ShapefileException e) {
            LOGGER.log(Level.FINE, I18N.getString("ShapefileReader.ErrorReadingPrjFile") //$NON-NLS-1$
                    + shapefileName);
        } catch (IOException e) {
            LOGGER.log(Level.FINE, I18N.getString("ShapefileReader.ErrorReadingPrjFile") //$NON-NLS-1$
                    + shapefileName);
        }

        this.minX = shapefileReader.getHeader().minX();
        this.maxX = shapefileReader.getHeader().maxX();
        this.minY = shapefileReader.getHeader().minY();
        this.maxY = shapefileReader.getHeader().maxY();
        this.shapeType = Reader.geometryType(shapefileReader.getHeader().getShapeType());
        LOGGER.log(Level.INFO, "ShapeType = " + shapefileReader.getHeader().getShapeType());
        this.nbFields = dbaseFileReader.getHeader().getNumFields();
        this.nbFeatures = dbaseFileReader.getHeader().getNumRecords();
        this.fieldValues = new Object[this.nbFeatures][this.nbFields];
        this.fieldNames = new String[this.nbFields];
        this.fieldClasses = new Class<?>[this.nbFields];
        for (int i = 0; i < this.nbFields; i++) {
            this.fieldNames[i] = dbaseFileReader.getHeader().getFieldName(i);
            this.fieldClasses[i] = dbaseFileReader.getHeader().getFieldClass(i);
            LOGGER.log(Level.INFO, "field " + i + " = " + this.fieldNames[i] + " (" + this.fieldClasses[i] + ")");
        }
        /*
         * // FIXME gère le SRID String wkt =
         * "PROJCS[\"unnamed\",GEOGCS[\"DHDN\",DATUM[\"Deutsches_Hauptdreiecksnetz\",SPHEROID[\"Bessel 1841\",6377397.155,299.1528128,AUTHORITY[\"EPSG\",\"7004\"]],TOWGS84[606,23,413,0,0,0,0],AUTHORITY[\"EPSG\",\"6314\"]],PRIMEM[\"Greenwich\",0,AUTHORITY[\"EPSG\",\"8901\"]],UNIT[\"degree\",0.01745329251994328,AUTHORITY[\"EPSG\",\"9122\"]],AUTHORITY[\"EPSG\",\"4314\"]],PROJECTION[\"Cassini_Soldner\"],PARAMETER[\"latitude_of_origin\",52.41864827777778],PARAMETER[\"central_meridian\",13.62720366666667],PARAMETER[\"false_easting\",40000],PARAMETER[\"false_northing\",10000],UNIT[\"Meter\",1]]"
         * ; // String wkt =
         * "PROJCS[\"NAD_1983_StatePlane_Massachusetts_Mainland_FIPS_2001\",GEOGCS[\"GCS_North_American_1983\",DATUM[\"D_North_American_1983\",SPHEROID[\"GRS_1980\", 6378137.0, 298.257222101]],PRIMEM[\"Greenwich\", 0.0], UNIT[\"degree\", 0.017453292519943295],AXIS[\"Longitude\", EAST],AXIS[\"Latitude\", NORTH]],PROJECTION[\"Lambert_Conformal_Conic\"], PARAMETER[\"central_meridian\", -71.5],PARAMETER[\"latitude_of_origin\", 41.0],PARAMETER[\"standard_parallel_1\", 41.71666666666667],PARAMETER[\"scale_factor\", 1.0],PARAMETER[\"false_easting\", 200000.0],PARAMETER[\"false_northing\", 750000.0],PARAMETER[\"standard_parallel_2\", 42.68333333333334],UNIT[\"m\", 1.0],AXIS[\"x\", EAST],AXIS[\"y\", NORTH]] "
         * ; CoordinateReferenceSystem example; try {
         * System.out.println(GeoTools.getVersion()); example =
         * CRS.parseWKT(wkt); } catch (FactoryException e3) { // TODO
         * Auto-generated catch block e3.printStackTrace(); }
         * System.out.println(prjFileReader.getCoodinateSystem());
         * System.out.println("code = " +
         * prjFileReader.getCoodinateSystem().getName().getCode());
         * System.out.println("SRS=" +
         * CRS.toSRS(prjFileReader.getCoodinateSystem()));
         * 
         * try { System.out.println("SRS="+CRS.lookupIdentifier(prjFileReader
         * .getCoodinateSystem(),true));
         * System.out.println("SRS="+CRS.lookupEpsgCode(prjFileReader
         * .getCoodinateSystem(),true)); } catch (FactoryException e1) {
         * e1.printStackTrace(); }
         */
        if (prjFileReader != null) {
            this.localCRS = prjFileReader.getCoodinateSystem();
        }
        this.geometries = new Geometry[this.nbFeatures];
        int indexFeatures = 0;
        try {
            while (shapefileReader.hasNext() && dbaseFileReader.hasNext()) {
                Object[] entry = dbaseFileReader.readEntry();
                Record record = shapefileReader.nextRecord();
                try {
                    this.geometries[indexFeatures] = (Geometry) record.shape();
                } catch (Exception e) {
                    // logger.error("Error for geometry of object " + entry[2]);
                    this.geometries[indexFeatures] = null;
                }
                for (int index = 0; index < this.nbFields; index++) {
                    this.fieldValues[indexFeatures][index] = entry[index];
                }
                indexFeatures++;
            }
            shapefileReader.close();
            dbaseFileReader.close();
            if (prjFileReader != null) {
                prjFileReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Renvoie la valeur de l'attribut minX.
     * @return la valeur de l'attribut minX
     */
    public double getMinX() {
        return this.minX;
    }

    /**
     * Renvoie la valeur de l'attribut maxX.
     * @return la valeur de l'attribut maxX
     */
    public double getMaxX() {
        return this.maxX;
    }

    /**
     * Renvoie la valeur de l'attribut minY.
     * @return la valeur de l'attribut minY
     */
    public double getMinY() {
        return this.minY;
    }

    /**
     * Renvoie la valeur de l'attribut maxY.
     * @return la valeur de l'attribut maxY
     */
    public double getMaxY() {
        return this.maxY;
    }

    /**
     * Renvoie la valeur de l'attribut nbFields.
     * @return la valeur de l'attribut nbFields
     */
    public int getNbFields() {
        return this.nbFields;
    }

    /**
     * Renvoie la valeur de l'attribut nbFields.
     * @return la valeur de l'attribut nbFields
     */
    public int getNbFeatures() {
        return this.nbFeatures;
    }

    /**
     * @param i
     * @return the name of the given field
     */
    public String getFieldName(int i) {
        return this.fieldNames[i];
    }

    /**
     * @param i
     * @return the class of the given field
     */
    public Class<?> getFieldClass(int i) {
        return this.fieldClasses[i];
    }

    public Class<? extends GM_Object> getShapeType() {
        return this.shapeType;
    }

    /**
     * @param type
     * @return the class of the given geometry type
     */
    private static Class<? extends GM_Object> geometryType(ShapeType type) {
        LOGGER.log(Level.FINE, "shapeType = " + type); //$NON-NLS-1$
        if (type.isPointType()) {
            return GM_Point.class;
        }
        if (type.isMultiPointType()) {
            return GM_MultiPoint.class;
        }
        if (type.isLineType()) {
            return GM_MultiCurve.class;
        }
        if (type.isPolygonType()) {
            return GM_MultiSurface.class;
        }
        return GM_MultiSurface.class;
    }

    public CoordinateReferenceSystem getCRS() {
        return this.localCRS;
    }

}
