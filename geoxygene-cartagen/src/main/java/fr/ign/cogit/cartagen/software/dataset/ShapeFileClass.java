/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.dataset;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.shp.ShapeType;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.util.StringUtil;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

public class ShapeFileClass implements GeographicClass {

  private String path;
  private String fileName;
  /**
   * The IGeneObj subclass in CartAGen.
   */
  private String featureTypeName;
  /**
   * The CartAGen dataset related to this shapefile class
   */
  private CartAGenDB dataSet;
  private Class<? extends IGeometry> geometryType;

  /**
   * Default Constructor using the three fields of the class.
   * @param dataSet
   * @param path
   * @param popName
   */
  public ShapeFileClass(CartAGenDB dataSet, String path,
      String featureTypeName, Class<? extends IGeometry> geometryType) {
    super();
    this.dataSet = dataSet;
    this.path = path;
    this.fileName = path.substring(path.lastIndexOf("/") + 1);
    this.featureTypeName = featureTypeName;
    this.geometryType = geometryType;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void addCartAGenId() {
    try {
      ShpFiles shpf = new ShpFiles(this.path + ".shp");
      DbaseFileReader dbr = new DbaseFileReader(shpf, true,
          Charset.defaultCharset());
      if (this.isIdFieldPresent(dbr.getHeader())) {
        dbr.close();
        return;
      }
      // get the shapefile header
      DbaseFileHeader headerIni = dbr.getHeader();
      DbaseFileHeader header = new DbaseFileHeader();
      for (int i = 0; i < headerIni.getNumFields(); i++) {
        header.addColumn(headerIni.getFieldName(i), headerIni.getFieldType(i),
            headerIni.getFieldLength(i) + 6, headerIni.getFieldDecimalCount(i));
      }
      // get the population of Cartagen objects
      IPopulation<? extends IFeature> pop = this.getDataSet().getDataSet()
          .getPopulationByFeatureTypeName(this.featureTypeName);
      // get the records of the shapefile
      HashMap<Integer, Object[]> fields = new HashMap<Integer, Object[]>();
      int i = 0;
      while (dbr.hasNext()) {
        Object[] champs = dbr.readEntry();
        if (header.getNumFields() != 0) {
          Object[] newFields = new Object[champs.length + 2];
          for (int j = 0; j < champs.length; j++) {
            newFields[j + 1] = champs[j];
            // remove special characters
            if (newFields[j + 1] instanceof String) {
              newFields[j + 1] = StringUtil
                  .removeSpecialCharacters((String) newFields[j + 1]);
            }
          }
          fields.put(i, newFields);
        } else {
          fields.put(i, new Object[2]);
        }
        i++;
      }
      // add the new column to the dbf file
      dbr.close();
      // get the geometry type
      ShapefileReader shpReader = new ShapefileReader(shpf, false, true,
          new GeometryFactory());
      ShapeType shapeType = shpReader.getHeader().getShapeType();
      String geomType = "Geometry";
      if (shapeType.isLineType())
        geomType = "LineString";
      if (shapeType.isPolygonType())
        geomType = "Polygon";
      if (shapeType.isPointType())
        geomType = "Point";
      if (shapeType.isMultiPointType())
        geomType = "MultiPoint";
      shpReader.close();
      // delete the shapefiles to write new ones
      shpf.delete();

      // build the shapefile datastore
      ShapefileDataStore store = new ShapefileDataStore(new File(this.path
          + ".shp").toURI().toURL());
      // build the specification String of the shapefile
      // specify the geometry type
      String specs = "geom:" + geomType; //$NON-NLS-1$
      // now add the attributes to the specs
      for (int j = 0; j < header.getNumFields(); j++) {
        specs += "," + header.getFieldName(j) + ":"
            + getFieldTypeFromChar(header.getFieldType(j));
      }
      specs += "," + GeographicClass.ID_NAME + ":" + Integer.class.getName();

      // build the shapefile datastore schema
      String featureTypeName = this.path
          .substring(this.path.lastIndexOf("/") + 1); //$NON-NLS-1$
      featureTypeName = featureTypeName.replace('.', '_');
      SimpleFeatureType type = DataUtilities.createType(featureTypeName, specs);
      store.createSchema(type);
      FeatureStore featureStore = (FeatureStore) store
          .getFeatureSource(featureTypeName);

      // write features in the datastore
      Transaction t = new DefaultTransaction();
      FeatureCollection collection = FeatureCollections.newCollection();
      // loop on the records
      for (Integer j = 0; j < fields.size(); j++) {
        // get the object related to record using the shape id
        IFeature feat = null;
        for (IFeature f : pop) {
          IGeneObj gene = (IGeneObj) f;
          if (gene.getShapeId() == j) {
            feat = gene;
            break;
          }
        }
        // update the cartagen id field (the last as it was just added)
        Object[] objectFields = fields.get(j);
        if (feat != null) {
          objectFields[0] = AdapterFactory.toGeometry(new GeometryFactory(),
              feat.getGeom());
          objectFields[objectFields.length - 1] = feat.getId();
        }

        SimpleFeature simpleFeature = SimpleFeatureBuilder.build(type,
            objectFields, String.valueOf(j));
        collection.add(simpleFeature);
      }
      featureStore.addFeatures(collection);
      t.commit();
      t.close();
      store.dispose();

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SchemaException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getName() {
    return this.fileName;
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getFileName() {
    return this.fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setFeatureTypeName(String featureTypeName) {
    this.featureTypeName = featureTypeName;
  }

  @Override
  public String getFeatureTypeName() {
    return this.featureTypeName;
  }

  public void setDataSet(CartAGenDB dataSet) {
    this.dataSet = dataSet;
  }

  public CartAGenDB getDataSet() {
    return this.dataSet;
  }

  /**
   * Checks if the Cartagen Id field is present in the existing shapefile.
   * 
   * @param header
   * @return
   */
  private boolean isIdFieldPresent(DbaseFileHeader header) {
    for (int i = 0; i < header.getNumFields(); i++) {
      if (header.getFieldName(i).equals(GeographicClass.ID_NAME)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return getName() + " [" + getPath() + "]";
  }

  private String getFieldTypeFromChar(char type) {
    if (type == 'N')
      return Integer.class.getName();
    if (type == 'F')
      return Double.class.getName();
    if (type == 'D')
      return Date.class.getName();
    if (type == 'L')
      return Boolean.class.getName();
    return String.class.getName();
  }

  @Override
  public Class<? extends IGeometry> getGeometryType() {
    return geometryType;
  }
}
