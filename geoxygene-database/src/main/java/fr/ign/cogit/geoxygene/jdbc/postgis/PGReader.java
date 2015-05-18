package fr.ign.cogit.geoxygene.jdbc.postgis;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryType;
import org.opengis.filter.Filter;
import org.geotools.filter.text.cql2.CQL;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 *
 * @author Marie-Dominique Van Damme
 */

public class PGReader {

  /** LOGGER. */
  private final static Logger LOGGER = Logger.getLogger(PGReader.class
      .getName());

  /** Params to connect. */
  private Map<String, String> params;
  private String tablename;

  /** Coordinate of bounds. */
  private double minX = 0;
  private double maxX = 0;
  private double minY = 0;
  private double maxY = 0;

  private int nbFields;
  private int nbFeatures;

  Object[][] fieldValues;
  String[] fieldNames;
  Class<?>[] fieldClasses;
  Geometry[][] geometries;
  String[] geomColumnNames;
  Class<? extends GM_Object>[] shapesType;

  CoordinateReferenceSystem localCRS;

  /**
   * Constructor.
   * @param params
   */
  @SuppressWarnings("unchecked")
  public PGReader(Map<String, String> connectionParameters, String name, String filterString)
      throws Exception {

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
      SimpleFeatureSource featureSource = pgDatastore
          .getFeatureSource(tablename);
      
      SimpleFeatureType featureType = featureSource.getSchema();

      // CRS
      localCRS = featureType.getCoordinateReferenceSystem();

      // Attributes
      int nbGeomFields = 0;
      for (int i = 0; i < featureType.getTypes().size(); i++) {
        if (featureType.getTypes().get(i) instanceof GeometryType) {
          //  AttributeType attr = featureType.getTypes().get(i);
          // System.out.println("-- " + featureType.getTypes().get(i).getName());
          nbGeomFields++;
        }
      }
      
      nbFields = featureType.getAttributeCount() - nbGeomFields;
      LOGGER.info("Nb fields = " + nbFields);

      // Fields
      // Préparation de la requête
      Query query = new Query(tablename);
      if (filterString != null && !filterString.trim().equals("")) {
        Filter filter = CQL.toFilter(filterString);
        query.setFilter(filter);
        System.out.println(query.toString());
      }
      
      // LOGGER.debug("Query : " + query.toString());
      SimpleFeatureCollection resFeatureCollection = featureSource
          .getFeatures(query);

      // if (resFeatureCollection.getBounds() != null) {
      /*
       * minX = resFeatureCollection.getBounds().getMinX(); maxX =
       * resFeatureCollection.getBounds().getMaxX(); minY =
       * resFeatureCollection.getBounds().getMinY(); maxY =
       * resFeatureCollection.getBounds().getMaxY();
       */
      // }

      nbFeatures = resFeatureCollection.size();
      LOGGER.debug("Nb features = " + nbFeatures);

      fieldValues = new Object[this.nbFeatures][this.nbFields];
      geometries = new Geometry[nbFeatures][nbGeomFields];
      
      fieldNames = new String[this.nbFields];
      fieldClasses = new Class<?>[this.nbFields];
      
      geomColumnNames = new String[nbGeomFields];
      shapesType = new Class[nbGeomFields];

      // Set AttributeType
      int cptField = 0;
      int cptGeomField = 0;
      for (int i = 0; i < featureType.getTypes().size(); i++) {
        AttributeType type = featureType.getTypes().get(i);
        if (type.getClass().getName().endsWith("GeometryTypeImpl")) {
          geomColumnNames[cptGeomField] = type.getName().toString();
          LOGGER.log(Level.TRACE, "Nom de la colonne géométrique : "
              + geomColumnNames[cptGeomField]);
          shapesType[cptGeomField] = PGReader.geometryType(type);
          cptGeomField++;
        } else {
          // Field Name
          String attributeTypeName = type.getName().toString();
          this.fieldNames[cptField] = attributeTypeName;
          LOGGER.log(Level.TRACE, "Field " + cptField + " = "
              + this.fieldNames[cptField]);
          // Field class
          fieldClasses[cptField] = type.getBinding();
          cptField++;
        }
      }

      // Data
      SimpleFeature feature = null;
      int indexFeatures = 0;
      FeatureIterator<SimpleFeature> reader = resFeatureCollection.features();
      while (reader.hasNext()) {
        try {
          feature = reader.next();
        } catch(Exception e) {
        }
        cptField = 0;
        Iterator<Property> iterator = feature.getProperties().iterator();
        while (iterator.hasNext()) {
          Property property = iterator.next();
          boolean isGeom = false;
          for (int j = 0; j < this.geomColumnNames.length; j++) {
            String geomColumnName = this.geomColumnNames[j];
            if (property.getName().toString().trim().equals(geomColumnName.trim())) {
              isGeom = true;
            } 
          }
          if (!isGeom) {
            fieldValues[indexFeatures][cptField] = property.getValue();
            cptField++;
          } else {
            cptGeomField = getPositionGeomColumn(property.getName().toString());
            geometries[indexFeatures][cptGeomField] = (Geometry) property.getValue();
          }
        }
        indexFeatures++;
      }

      // shapefileReader.close();
      // dbaseFileReader.close();

    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

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
   * Return the name of the given field.
   * @param indice i
   * @return String.
   */
  public String getFieldName(int i) {
    return this.fieldNames[i];
  }

  /**
   * Return the class of the given field.
   * @param indice i
   * @return Class
   */
  public Class<?> getFieldClass(int i) {
    return this.fieldClasses[i];
  }

  /**
   * 
   * @return
   */
  public Class<? extends GM_Object>[] getShapesType() {
    return this.shapesType;
  }

  /**
   * @param type
   * @return the class of the given geometry type
   */
  private static Class<? extends GM_Object> geometryType(AttributeType type) {

    String typeGeometry = type.getBinding().getSimpleName();
    LOGGER.log(Level.INFO, "shapeType = " + typeGeometry);

    if (typeGeometry.equals("LineString")) {
      return GM_MultiCurve.class;
    }
    if (typeGeometry.equals("MultiLineString")) {
      return GM_MultiCurve.class;
    }
    if (typeGeometry.equals("Point")) {
      return GM_Point.class;
    }
    if (typeGeometry.equals("MultiPoint")) {
      return GM_MultiPoint.class;
    }
    return GM_MultiSurface.class;
  }

  /**
   * 
   * @return
   */
  public CoordinateReferenceSystem getCRS() {
    return this.localCRS;
  }
  
  
  public int getPositionGeomColumn(String geomColumnName) {
    for (int i = 0; i < geomColumnNames.length; i++) {
      if (geomColumnName.toUpperCase().trim().equals(geomColumnNames[i].toUpperCase().trim())) {
        return i;
      }
    }
    return -1;
  }

}
