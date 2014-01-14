package fr.ign.cogit.process.geoxygene.cartetopo;

import java.util.logging.Logger;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;
import fr.ign.cogit.geoxygene.util.conversion.GeOxygeneGeoToolsTypes;
import fr.ign.cogit.process.geoxygene.GeoxygeneProcess;

/**
 *
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL_V2-fr.txt
 *        see Licence_CeCILL_V2-en.txt
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info</a>
 * 
 * 
 * @copyright IGN
 * 
 * @author Julien Perret
 * 
 * 
 */
@DescribeProcess(title = "BuildingBlocksCreation", description = "Create Faces from a Feature Collection representing a Network")
public class BuildingBlocksCreationProcess implements GeoxygeneProcess {
  
  private final static Logger logger = Logger.getLogger(BuildingBlocksCreationProcess.class.getName());

  @DescribeResult(name = "result", description = "output result")
  public SimpleFeatureCollection execute(
      @DescribeParameter(name = "features", description = "The feature collection whose faces will be computed") SimpleFeatureCollection features,
      @DescribeParameter(name = "tolerance", description = "The tolerance used in order to make the graph planar") double tolerance) {
    
    logger.info("Start Converting");
    IFeatureCollection<?> collection = GeOxygeneGeoToolsTypes.convert2IFeatureCollection(features);
    logger.info("End Converting");
    
    CarteTopo map = new CarteTopo("MAP");
    Chargeur.importAsEdges(collection, map, null, null, null, null, null, tolerance);
    
    logger.info("Making planar");
    map.rendPlanaire(tolerance);
    
    logger.info("Creating faces");
    map.creeTopologieFaces();
    
    logger.info("Start Converting");
    try {
      SimpleFeatureCollection faces = GeOxygeneGeoToolsTypes
          .convert2FeatureCollection(map.getPopFaces(), features.getSchema()
              .getCoordinateReferenceSystem());
      logger.info("End Converting");
      return faces;
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    logger.info("Failed Converting");
    return null;
  }

}
