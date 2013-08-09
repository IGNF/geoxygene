package fr.ign.cogit.geoxygene.jdbc.postgis;


import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


/**
 * 
 * @author Marie-Dominique Van Damme
 */

public class PGReader {
    
    private final static Logger LOGGER = Logger.getLogger(PGReader.class.getName());
    
    /** Params to connect. */
    private Map<String,String> params;
    private String tablename;
    
    /** Coordinate of bounds. */ 
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    
    private int nbFields;
    private int nbFeatures;
    
    Object[][] fieldValues;
    String[] fieldNames;
    Class<?>[] fieldClasses;
    /*Geometry[] geometries;
    Class<? extends GM_Object> shapeType;*/
    
    CoordinateReferenceSystem localCRS;

    /**
     * Constructor.
     * @param params
     */
    public PGReader(Map<String,String> connectionParameters, String name) throws Exception {
      
        params = connectionParameters;
        LOGGER.debug("params = " + params);
        
        tablename = name;
        LOGGER.debug("Table name = " + tablename);
        
        try {
            
            DataStore pgDatastore = DataStoreFinder.getDataStore(params);
            if (pgDatastore == null) {
                LOGGER.log(Level.ERROR, "Could not connect - check parameters");
                return;
            } 
            
            // Exécute la requete + resultat
            SimpleFeatureSource featureSource = pgDatastore.getFeatureSource(tablename);
            // System.out.println("*** = " + featureSource.getDataStore().getNames().get(0));
            
            // Préparation de la requête
            Query query = new Query(tablename);
            LOGGER.debug("Query : " + query.toString());
            
            SimpleFeatureCollection resFeatureCollection = featureSource.getFeatures(query);
       
            minX = resFeatureCollection.getBounds().getMinX();
            maxX = resFeatureCollection.getBounds().getMaxX();
            minY = resFeatureCollection.getBounds().getMinY();
            maxY = resFeatureCollection.getBounds().getMaxY();
            
            nbFeatures = resFeatureCollection.size();
            LOGGER.debug("Nb features = " + nbFeatures);
            
            SimpleFeatureType featureType = featureSource.getSchema();
            nbFields = featureType.getAttributeCount();
            LOGGER.info("Nb fields = " + nbFields);
            
            this.fieldValues = new Object[this.nbFeatures][this.nbFields];
            this.fieldNames = new String[this.nbFields];
            this.fieldClasses = new Class<?>[this.nbFields];

            // Set AttributeType
            for (int i = 0; i < this.nbFields; i++) {
                
                AttributeType type = featureType.getTypes().get(i);
                
                String attributeTypeName = type.getName().toString();
                this.fieldNames[i] = attributeTypeName;
                LOGGER.log(Level.DEBUG, "Field " + i + " = " + this.fieldNames[i]);
                
                System.out.println(type.getClass().getName());
                // this.fieldClasses[i] = dbaseFileReader.getHeader().getFieldClass(i);
                
            }
            
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, e.toString());
            return;
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, e.toString());
            return;
        }
        
      /*
      
      
      
      
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
      }*/
    }

    /**
     * Return minX.
     * @return double
     */
    public double getMinX() {
        return this.minX;
    }

    /**
     * Return maxX.
     * @return double
     */
    public double getMaxX() {
        return this.maxX;
    }

    /**
     * Return minY.
     * @return double
     */
    public double getMinY() {
        return this.minY;
    }

    /**
     * Return maxY.
     * @return double
     */
    public double getMaxY() {
        return this.maxY;
    }

    /**
     * Return nbFields.
     * @return int
     */
    public int getNbFields() {
        return this.nbFields;
    }

    /**
     * Return nbFeatures.
     * @return int
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
    /*public Class<?> getFieldClass(int i) {
      return this.fieldClasses[i];
    }*/

    /*public Class<? extends GM_Object> getShapeType() {
      return this.shapeType;
    }*/

    /**
     * @param type
     * @return the class of the given geometry type
     */
    /*private static Class<? extends GM_Object> geometryType(ShapeType type) {
      Reader.logger.log(Level.FINE, "shapeType = " + type); //$NON-NLS-1$
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
    }*/

    public CoordinateReferenceSystem getCRS() {
      return this.localCRS;
    }

}
