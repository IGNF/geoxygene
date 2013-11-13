package fr.ign.cogit.process.geoxygene.cartetopo;

import java.util.logging.Logger;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopoData;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;
import fr.ign.cogit.geoxygene.util.conversion.GeOxygeneGeoToolsTypes;
import fr.ign.cogit.process.geoxygene.GeoxygeneProcess;

/**
 * 
 * 
 * @author MDVan-Damme
 */
@DescribeProcess(title = "CreateCarteTopo", description = "Create topological map from a Feature Collection representing a Network")
public class CreateCarteTopoProcess implements GeoxygeneProcess {
  
  private final static Logger LOGGER = Logger.getLogger(CreateFacesProcess.class.getName());
  
  
  @DescribeResult(name = "CarteTopo", description = "topological map")
  public CarteTopoData execute(
      @DescribeParameter(name = "features", description = "The feature collection whose faces will be computed") SimpleFeatureCollection features,
      @DescribeParameter(name = "tolerance", description = "The tolerance used in order to make the graph planar") double tolerance) {
    
    LOGGER.info("Start Converting");
    IFeatureCollection<?> collection = GeOxygeneGeoToolsTypes.convert2IFeatureCollection(features);
    LOGGER.info("End Converting");
    
    CarteTopo map = new CarteTopo("MAP");
    Chargeur.importAsEdges(collection, map, null, null, null, null, null, tolerance);
    
    LOGGER.info("Making planar");
    map.rendPlanaire(tolerance);
    
    LOGGER.info("Creating faces");
    map.creeTopologieFaces();
    
    // Prepare the result format
    CarteTopoData cartetopo = new CarteTopoData();
    cartetopo.setPopEdge(map.getPopArcs());
    
    //
    return cartetopo;
    
  }

}
