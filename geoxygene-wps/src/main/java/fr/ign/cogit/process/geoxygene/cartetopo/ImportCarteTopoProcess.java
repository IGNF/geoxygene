package fr.ign.cogit.process.geoxygene.cartetopo;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.data.Transaction;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.process.ProcessException;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CatalogBuilder;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.ProjectionPolicy;
import org.geoserver.catalog.StoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.wps.WPSException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopoData;
import fr.ign.cogit.geoxygene.util.conversion.GeOxygeneGeoToolsTypes;
import fr.ign.cogit.process.geoxygene.GeoxygeneProcess;

/**
 * Imports a cartetopo into the GeoServer catalog.
 * 
 */
@DescribeProcess(title = "ImportCarteTopo", description = "Imports a cartetopo into the GeoServer catalog")
public class ImportCarteTopoProcess implements GeoxygeneProcess {

  private Catalog catalog;

  public ImportCarteTopoProcess(Catalog catalog) {
    this.catalog = catalog;
    System.out.println("????????????????????????????????????????????????????????");
  }

  @DescribeResult(name = "layerName", description = "Name of the new featuretype, with workspace")
  public String execute() throws Exception {
    System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    /*ProjectionPolicy srsHandling = ProjectionPolicy.FORCE_DECLARED;

    // Target store
    WorkspaceInfo ws = catalog.getWorkspaceByName("Appariement");

    // Create a builder to help build catalog objects
    CatalogBuilder cb = new CatalogBuilder(catalog);
    cb.setWorkspace(ws);

    // Target store
    StoreInfo storeInfo = catalog.getDataStoreByName(ws.getName(), "T3");

    // check if the target layer and the target feature type are not
    // already there (this is a half-assed attempt as we don't have
    // an API telling us how the feature type name will be changed
    // by DataStore.createSchema(...), but better than fully importing
    // the data into the target store to find out we cannot create the layer...)
    String tentativeTargetName = ws.getName() + ":" + "cartetopo-edges";
    if (catalog.getLayer(tentativeTargetName) != null) {
      throw new ProcessException("Target layer " + tentativeTargetName + " already exists");
    }

    // Target CRS
    String targetSRSCode = "EPSG:2154";

    // import the data into the target store
    SimpleFeatureType targetType;
    SimpleFeatureCollection features = GeOxygeneGeoToolsTypes.convert2FeatureCollection(carteTopo.getPopEdge());
    
    try {
      targetType = importDataIntoStore(features, "cartetopo-edges", (DataStoreInfo) storeInfo);
    } catch (IOException e) {
      throw new ProcessException("Failed to import data into the target store", e);
    }

    // now import the newly created layer into GeoServer
    try { 
      
      cb.setStore(storeInfo);
      
      // build the typeInfo and set CRS if necessary 
      FeatureTypeInfo typeInfo = cb.buildFeatureType(targetType.getName()); 
      if (targetSRSCode != null) {
        typeInfo.setSRS(targetSRSCode); 
      } 
      if (srsHandling != null) {
        typeInfo.setProjectionPolicy(srsHandling); 
      } 
      // compute the bounds
      cb.setupBounds(typeInfo);
      
      // build the layer and set a style 
      LayerInfo layerInfo = cb.buildLayer(typeInfo); 
      
      catalog.add(typeInfo); catalog.add(layerInfo);
      
      return layerInfo.prefixedName(); 
      
    } catch (Exception e) { 
      throw new ProcessException("Failed to complete the import inside the GeoServer catalog", e); 
    }*/
    
    return "OK";
    
//  workspace = "Appariement";
//  store = "T3";
//  ProjectionPolicy srsHandling = ProjectionPolicy.FORCE_DECLARED;
//
//  // Target store
//  WorkspaceInfo ws = catalog.getWorkspaceByName(workspace);
//
//  // Target store
//  StoreInfo storeInfo = catalog.getDataStoreByName(ws.getName(), store);
//
//  // Check if the target layer and the target feature type are not already there 
//  String tentativeTargetEdgesName = ws.getName() + ":" + EDGES_SHAPEFILE_NAME;
//  if (catalog.getLayer(tentativeTargetEdgesName) != null) {
//    throw new ProcessException("Target layer " + tentativeTargetEdgesName + " already exists");
//  }
//  /*String tentativeTargetNodesName = ws.getName() + ":" + "cartetopo-nodes";
//  if (catalog.getLayer(tentativeTargetNodesName) != null) {
//    throw new ProcessException("Target layer " + tentativeTargetNodesName + " already exists");
//  }
//  String tentativeTargetFacesName = ws.getName() + ":" + "cartetopo-faces";
//  if (catalog.getLayer(tentativeTargetFacesName) != null) {
//    throw new ProcessException("Target layer " + tentativeTargetFacesName + " already exists");
//  }*/
//
//  // Target CRS
//  String targetSRSCode = null;
//  if (srs != null) {
//      try {
//          Integer code = CRS.lookupEpsgCode(srs, true);
//          if (code == null) {
//              throw new WPSException("Could not find a EPSG code for " + srs);
//          }
//          targetSRSCode = "EPSG:" + code;
//      } catch (Exception e) {
//          throw new ProcessException("Could not lookup the EPSG code for the provided srs", e);
//      }
//  } else {
//    targetSRSCode = "EPSG:2154";
//  }
//  targetSRSCode = "EPSG:2154";
//
//  // import the data into the target store
//  SimpleFeatureType targetEdgesType;
//  //SimpleFeatureType targetNodesType;
//  //SimpleFeatureType targetFacesType;
//  SimpleFeatureCollection edgesFeatures = GeOxygeneGeoToolsTypes.convert2FeatureCollection(carteTopo.getPopEdge());
//  //SimpleFeatureCollection nodesFeatures = GeOxygeneGeoToolsTypes.convert2FeatureCollection(carteTopo.getPopNode());
//  //SimpleFeatureCollection facesFeatures = GeOxygeneGeoToolsTypes.convert2FeatureCollection(carteTopo.getPopFace());
//  
//  try {
//    targetEdgesType = importDataIntoStore(edgesFeatures, EDGES_SHAPEFILE_NAME, (DataStoreInfo) storeInfo);
//    //targetNodesType = importDataIntoStore(nodesFeatures, "cartetopo-nodes", (DataStoreInfo) storeInfo);
//    //targetFacesType = importDataIntoStore(facesFeatures, "cartetopo-faces", (DataStoreInfo) storeInfo);
//  } catch (IOException e) {
//    throw new ProcessException("Failed to import data into the target store", e);
//  }
//
//  
//  // Create a builder to help build catalog objects
//  CatalogBuilder edgesCB = new CatalogBuilder(catalog);
//  //CatalogBuilder nodesCB = new CatalogBuilder(catalog);
//  //CatalogBuilder facesCB = new CatalogBuilder(catalog);
//  edgesCB.setWorkspace(ws);
//  //nodesCB.setWorkspace(ws);
// // facesCB.setWorkspace(ws);
//  
//  // now import the newly created layer into GeoServer
//  try { 
//    
//    edgesCB.setStore(storeInfo);
//    //nodesCB.setStore(storeInfo);
//    //facesCB.setStore(storeInfo);
//    
//    // build the typeInfo and set CRS if necessary 
//    FeatureTypeInfo edgesTypeInfo = edgesCB.buildFeatureType(targetEdgesType.getName());
//    //FeatureTypeInfo nodesTypeInfo = nodesCB.buildFeatureType(targetNodesType.getName());
//    //FeatureTypeInfo facesTypeInfo = facesCB.buildFeatureType(targetFacesType.getName());
//    
//    if (targetSRSCode != null) {
//      edgesTypeInfo.setSRS(targetSRSCode);
//      //nodesTypeInfo.setSRS(targetSRSCode);
//      //facesTypeInfo.setSRS(targetSRSCode);
//    } 
//    if (srsHandling != null) {
//      edgesTypeInfo.setProjectionPolicy(srsHandling);
//      //nodesTypeInfo.setProjectionPolicy(srsHandling);
//      //facesTypeInfo.setProjectionPolicy(srsHandling);
//    } 
//    // compute the bounds
//    edgesCB.setupBounds(edgesTypeInfo);
//    //nodesCB.setupBounds(nodesTypeInfo);
//    //facesCB.setupBounds(facesTypeInfo);
//    
//    // build the layer and set a style 
//    LayerInfo layerEdgesInfo = edgesCB.buildLayer(edgesTypeInfo); 
//    //LayerInfo layerNodesInfo = nodesCB.buildLayer(nodesTypeInfo); 
//    //LayerInfo layerFacesInfo = facesCB.buildLayer(facesTypeInfo); 
//    
//    catalog.add(edgesTypeInfo); 
//    //catalog.add(nodesTypeInfo); 
//    //catalog.add(facesTypeInfo); 
//    
//    catalog.add(layerEdgesInfo);
//    //catalog.add(layerNodesInfo);
//    //catalog.add(layerFacesInfo);
//    
//    //return "[" + layerEdgesInfo.prefixedName() + ", " + layerNodesInfo.prefixedName() + ", " + layerFacesInfo.prefixedName() + "]";
//    return "[" + layerEdgesInfo.prefixedName() + "]";
//    
//  } catch (Exception e) { 
//    throw new ProcessException("Failed to complete the import inside the GeoServer catalog", e); 
//  }

    
  }

  /**
   * 
   * @param features
   * @param name
   * @param storeInfo
   * @return
   */
  /*private SimpleFeatureType importDataIntoStore(SimpleFeatureCollection features, String name, DataStoreInfo storeInfo) throws IOException,
      ProcessException {

    SimpleFeatureType targetType;
    // grab the data store
    DataStore ds = (DataStore) storeInfo.getDataStore(null);

    // decide on the target ft name
    SimpleFeatureType sourceType = features.getSchema();
    if (name != null) {
      SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
      tb.init(sourceType);
      tb.setName(name);
      sourceType = tb.buildFeatureType();
    }

    // create the schema
    ds.createSchema(sourceType);

    // try to get the target feature type (might have slightly different
    // name and structure)
    targetType = ds.getSchema(sourceType.getTypeName());
    if (targetType == null) {
      // ouch, the name was changed... we can only guess now...
      // try with the typical Oracle mangling
      targetType = ds.getSchema(sourceType.getTypeName().toUpperCase());
    }

    if (targetType == null) {
      throw new WPSException("The target schema was created, but with a name "
          + "that we cannot relate to the one we provided the data store. Cannot proceeed further");
    } else {
      // check the layer is not already there
      String newLayerName = storeInfo.getWorkspace().getName() + ":" + targetType.getTypeName();
      LayerInfo layer = catalog.getLayerByName(newLayerName);
      // todo: we should not really reach here and know beforehand what the
      // targetType
      // name is, but if we do we should at least get a way to drop it
      if (layer != null) {
        throw new ProcessException("Target layer " + newLayerName + " already exists in the catalog");
      }
    }

    // try to establish a mapping with old and new attributes. This is again
    // just guesswork until we have a geotools api that will give us the
    // exact mapping to be performed
    Map<String, String> mapping = buildAttributeMapping(sourceType, targetType);

    // start a transaction and fill the target with the input features
    Transaction t = new DefaultTransaction();
    SimpleFeatureStore fstore = (SimpleFeatureStore) ds.getFeatureSource(targetType.getTypeName());
    fstore.setTransaction(t);
    SimpleFeatureIterator fi = features.features();
    SimpleFeatureBuilder fb = new SimpleFeatureBuilder(targetType);
    while (fi.hasNext()) {
      SimpleFeature source = fi.next();
      fb.reset();
      for (String sname : mapping.keySet()) {
        fb.set(mapping.get(sname), source.getAttribute(sname));
      }
      SimpleFeature target = fb.buildFeature(null);
      fstore.addFeatures(DataUtilities.collection(target));
    }
    t.commit();
    t.close();

    return targetType;
  }*/
  
  /**
   * Applies a set of heuristics to find which target attribute corresponds to a certain input
   * attribute
   * 
   * @param sourceType
   * @param targetType
   * @return
   */
  /*Map<String, String> buildAttributeMapping(SimpleFeatureType sourceType,
          SimpleFeatureType targetType) {
      // look for the typical manglings. For example, if the target is a
      // shapefile store it will move the geometry and name it the_geom

      // collect the source names
      Set<String> sourceNames = new HashSet<String>();
      for (AttributeDescriptor sd : sourceType.getAttributeDescriptors()) {
          sourceNames.add(sd.getLocalName());
      }

      // first check if we have been kissed by sheer luck and the names are
      // the same
      Map<String, String> result = new HashMap<String, String>();
      for (String name : sourceNames) {
          if (targetType.getDescriptor(name) != null) {
              result.put(name, name);
          }
      }
      sourceNames.removeAll(result.keySet());

      // then check for simple case difference (Oracle case)
      for (String name : sourceNames) {
          for (AttributeDescriptor td : targetType.getAttributeDescriptors()) {
              if (td.getLocalName().equalsIgnoreCase(name)) {
                  result.put(name, td.getLocalName());
                  break;
              }
          }
      }
      sourceNames.removeAll(result.keySet());

      // then check attribute names being cut (another Oracle case)
      for (String name : sourceNames) {
          String loName = name.toLowerCase();
          for (AttributeDescriptor td : targetType.getAttributeDescriptors()) {
              String tdName = td.getLocalName().toLowerCase();
              if (loName.startsWith(tdName)) {
                  result.put(name, td.getLocalName());
                  break;
              }
          }
      }
      sourceNames.removeAll(result.keySet());

      // consider the shapefile geometry descriptor mangling
      if (targetType.getGeometryDescriptor() != null
              && "the_geom".equals(targetType.getGeometryDescriptor().getLocalName())
              && !"the_geom".equalsIgnoreCase(sourceType.getGeometryDescriptor().getLocalName())) {
          result.put(sourceType.getGeometryDescriptor().getLocalName(), "the_geom");
      }

      // and finally we return with as much as we can match
      if (!sourceNames.isEmpty()) {
          System.out.println("Could not match the following attributes " + sourceNames
                  + " to the target feature type ones: " + targetType);
      }
      return result;
  }*/
}
