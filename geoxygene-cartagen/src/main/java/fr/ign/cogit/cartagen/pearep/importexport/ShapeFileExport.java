/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.pearep.importexport;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMaster;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.util.CRSConversion;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.I18N;

public class ShapeFileExport {

  private static Logger logger = Logger.getLogger(ShapeFileExport.class
      .getName());

  private File exportDir;
  private CartAGenDataSet dataset;
  private ScaleMaster scaleMaster;
  private int finalScale;

  public ShapeFileExport(File exportDir, CartAGenDataSet dataset,
      ScaleMaster scaleMaster, int finalScale) {
    super();
    this.exportDir = exportDir;
    this.dataset = dataset;
    this.setScaleMaster(scaleMaster);
    this.setFinalScale(finalScale);
  }

  public void setDataset(CartAGenDataSet dataset) {
    this.dataset = dataset;
  }

  public CartAGenDataSet getDataset() {
    return this.dataset;
  }

  public File getExportDir() {
    return this.exportDir;
  }

  public void setExportDir(File exportDir) {
    this.exportDir = exportDir;
  }

  public ScaleMaster getScaleMaster() {
    return this.scaleMaster;
  }

  public void setScaleMaster(ScaleMaster scaleMaster) {
    this.scaleMaster = scaleMaster;
  }

  public int getFinalScale() {
    return this.finalScale;
  }

  public void setFinalScale(int finalScale) {
    this.finalScale = finalScale;
  }

  /**
   * Export the generalised data of the dataset as shapefiles (one shapefile per
   * scale master line).
   */
  public void exportToShapefiles() {
    // loop on the lines of the ScaleMaster
    for (ScaleLine line : this.scaleMaster.getScaleLines()) {
      // one shapefile is exported per line
      // first get the name of the theme to create the name of the shapefile
      String shapeFileName = this.scaleMaster.getPointOfView().toString() + "_"
          + this.finalScale + "_" + line.getTheme().getName();
      if (ShapeFileExport.logger.isLoggable(Level.FINE)) {
        ShapeFileExport.logger.fine(shapeFileName);
        ShapeFileExport.logger.fine(this.exportDir.getPath());
      }

      // get the element corresponding to final scale
      ScaleMasterElement elem = line.getElementFromScale(this.finalScale);
      if (elem == null)
        continue;
      Class<?> classObj = elem.getClasses().iterator().next();
      // get the features to export
      IPopulation<IGeneObj> features = new Population<IGeneObj>();
      IPopulation<IGeneObj> pop = dataset.getCartagenPop(dataset
          .getPopNameFromClass(classObj));
      if (pop == null) {
        // these features have not been imported
        continue;
      }
      for (IGeneObj obj : pop) {
        if (classObj.isInstance(obj))
          features.add(obj);
      }

      // write the shapefile
      // FIXME coder la gestion des projections de manière plus propre!
      String projEpsg = "32631";
      if (this.dataset.getCartAGenDB().getSourceDLM().equals(
          SourceDLM.MGCPPlusPlus))
        projEpsg = "32629";
      ShapeFileExport.write(features, line.getTheme().getGeometryType()
          .toGeomClass(), this.exportDir.getPath() + "\\" + shapeFileName,
          projEpsg, "4326");
    }
  }

  @SuppressWarnings("unchecked")
  public static <Feature extends IFeature> void write(
      IPopulation<Feature> featurePop, Class<? extends IGeometry> geomType,
      String shapefileName, String epsgIni, String epsgFin) {
    if (featurePop == null)
      return;
    if (featurePop.isEmpty()) {
      return;
    }
    try {
      if (!shapefileName.contains(".shp")) { //$NON-NLS-1$
        shapefileName = shapefileName + ".shp"; //$NON-NLS-1$
      }
      ShapefileDataStore store = new ShapefileDataStore(new File(shapefileName)
          .toURI().toURL());

      // specify the geometry type
      String specs = "geom:"; //$NON-NLS-1$
      specs += AdapterFactory.toJTSGeometryType(geomType).getSimpleName();

      // specify the attributes: there is only one the MRDB link
      specs += "," + "a_pour_antecedant" + ":" + Integer.class.getName();
      List<String> getters = new ArrayList<String>();
      if (featurePop.size() != 0) {
        Class<?> classObj = featurePop.get(0).getClass();
        Vector<Object> result = addAttributesToHeader(classObj);
        getters = (List<String>) result.get(0);
        specs += result.get(1);
      }

      String featureTypeName = shapefileName.substring(shapefileName
          .lastIndexOf("/") + 1, //$NON-NLS-1$
          shapefileName.lastIndexOf(".")); //$NON-NLS-1$
      featureTypeName = featureTypeName.replace('.', '_');
      SimpleFeatureType type = DataUtilities.createType(featureTypeName, specs);
      store.createSchema(type);
      FeatureStore featureStore = (FeatureStore) store
          .getFeatureSource(featureTypeName);
      Transaction t = new DefaultTransaction();
      FeatureCollection collection = FeatureCollections.newCollection();
      int i = 1;
      for (Feature feature : featurePop) {
        if (feature.isDeleted()) {
          continue;
        }
        List<Object> liste = new ArrayList<Object>(0);
        // change the CRS if needed
        IGeometry geom = feature.getGeom();
        if (!epsgIni.equals(epsgFin)) {
          geom = CRSConversion.changeCRS(geom, epsgIni, epsgFin, true, false);
        }
        liste.add(AdapterFactory.toGeometry(new GeometryFactory(), geom));
        liste.add(feature.getId());
        // put the attributes in the list, after the geometry
        for (String getter : getters) {
          Method m = feature.getClass().getDeclaredMethod(getter);
          liste.add(m.invoke(feature));
        }
        SimpleFeature simpleFeature = SimpleFeatureBuilder.build(type, liste
            .toArray(), String.valueOf(i++));
        collection.add(simpleFeature);
      }
      featureStore.addFeatures(collection);
      CoordinateReferenceSystem crs = CRS.decode("EPSG:" + epsgFin);
      if (crs != null) {
        store.forceSchemaCRS(crs);
      }
      t.commit();
      t.close();
      store.dispose();
    } catch (MalformedURLException e) {
      ShapeFileExport.logger.log(Level.SEVERE, I18N
          .getString("ShapefileWriter.FileName") //$NON-NLS-1$
          + shapefileName + I18N.getString("ShapefileWriter.Malformed")); //$NON-NLS-1$
      e.printStackTrace();
    } catch (IOException e) {
      ShapeFileExport.logger.log(Level.SEVERE, I18N
          .getString("ShapefileWriter.ErrorWritingFile") //$NON-NLS-1$
          + shapefileName);
      e.printStackTrace();
    } catch (SchemaException e) {
      ShapeFileExport.logger.log(Level.SEVERE, I18N
          .getString("ShapefileWriter.SchemeUsedForWritingFile") //$NON-NLS-1$
          + shapefileName + I18N.getString("ShapefileWriter.Incorrect")); //$NON-NLS-1$
      e.printStackTrace();
    } catch (Exception e) {
      ShapeFileExport.logger.log(Level.SEVERE, I18N
          .getString("ShapefileWriter.ErrorWritingFile") //$NON-NLS-1$
          + shapefileName);
      e.printStackTrace();
    }
  }

  private static Vector<Object> addAttributesToHeader(Class<?> featureClass) {
    List<String> attrNames = new ArrayList<String>();
    Set<String> acceptedTypes = new HashSet<String>();
    acceptedTypes.add("java.lang.Integer");
    acceptedTypes.add("java.lang.String");
    acceptedTypes.add("java.lang.Double");
    acceptedTypes.add("java.lang.Boolean");
    acceptedTypes.add("int");
    acceptedTypes.add("double");
    acceptedTypes.add("long");
    acceptedTypes.add("boolean");
    String specs = "";
    for (Method m : featureClass.getDeclaredMethods()) {
      if (!m.getName().startsWith("get"))
        continue;
      if (m.getName().equals("getGeom"))
        continue;
      if (m.getName().equals("getGeoxObj"))
        continue;
      if (m.getName().equals("getSymbolId"))
        continue;
      if (m.getName().equals("getId"))
        continue;
      if (m.getName().equals("getAttributeMap"))
        continue;
      if (m.getName().equals("getSymbolExtent"))
        continue;
      String returnType = m.getReturnType().getName();
      if (!acceptedTypes.contains(returnType))
        continue;
      attrNames.add(m.getName());
      String attributeName = m.getName().substring(3, 4).toLowerCase()
          + m.getName().substring(4);
      if (returnType.equals(long.class.getName()))
        returnType = Integer.class.getName();
      specs += "," + attributeName + ":" + returnType;
    }
    Vector<Object> vect = new Vector<Object>(2);
    vect.add(attrNames);
    vect.add(specs);
    return vect;
  }

}
