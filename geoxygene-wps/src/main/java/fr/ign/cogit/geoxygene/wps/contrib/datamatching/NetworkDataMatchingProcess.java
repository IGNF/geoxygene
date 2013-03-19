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
*/
package fr.ign.cogit.geoxygene.wps.contrib.datamatching;

// import java.util.ArrayList;
// import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.geoserver.wps.gs.GeoServerProcess;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.ProcessException;
// import org.geotools.data.
// import org.geoserver.wps.jts.DescribeParameter;
// import org.geoserver.wps.jts.DescribeProcess;
// import org.geoserver.wps.jts.DescribeResult;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.NetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Recalage;
// import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetwork;
// import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkElement;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultatAppariement;

// import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
// import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresAppData;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.util.conversion.GeOxygeneGeoToolsTypes;

/**
 * Network data matching process.
* 
* @version 1.6
*/
@DescribeProcess(title = "NetworkDataMatching", description = "Do network data matching")
public class NetworkDataMatchingProcess implements GeoServerProcess {

  /** LOGGER. */
  private final static Logger LOGGER = Logger
      .getLogger(NetworkDataMatchingProcess.class.getName());

  /**
   * 
   * 
   * 
   * @param popRef
   * @param popComp
   * @return Results ResultatAppariement
   */
  @DescribeResult(name = "popApp", description = "network Matched")
  public ResultatAppariement execute(
      @DescribeParameter(name = "popRef", description = "Less detailed network") SimpleFeatureCollection popRef,
      @DescribeParameter(name = "popComp", description = "Comparison network") SimpleFeatureCollection popComp) {
      
      // defaultValue = 50    
      // @DescribeParameter(name = "paramFilename", description = "XML Parameters") String paramFilename)
    
    System.out.println("----------------------------------------------------------------------------------------");
    System.out.println("Start Network Data Matching Process");
    
    /** Log memory status. */
    if (LOGGER.isEnabledFor(Level.DEBUG)) {
      Runtime runtime = Runtime.getRuntime();
      long maxMemory = runtime.maxMemory();
      long allocatedMemory = runtime.totalMemory();
      long freeMemory = runtime.freeMemory();
      LOGGER.debug("free memory: " + freeMemory / 1024);
      LOGGER.debug("allocated memory: " + allocatedMemory / 1024);
      LOGGER.debug("max memory: " + maxMemory / 1024);
      LOGGER.debug("total free memory: " + (freeMemory + (maxMemory - allocatedMemory)) / 1024);
    }
    
    // Converting networks
    LOGGER.info("Start Converting networks : reference and comparative");
    IFeatureCollection<?> gPopRef = GeOxygeneGeoToolsTypes.convert2IFeatureCollection(popRef);
    IFeatureCollection<?> gPopComp = GeOxygeneGeoToolsTypes.convert2IFeatureCollection(popComp);
    LOGGER.info("End Converting networks");
    
    // Set parameters
    LOGGER.info("Start setting parameters");
    // ParametresAppData paramAppData = null;
    // paramAppData = ParametresAppData.unmarshall(paramFilename);

    ParametresApp param = new ParametresApp();

    param.populationsArcs1.add(gPopRef);
    param.populationsArcs2.add(gPopComp);

    param.topologieFusionArcsDoubles1 = true;
    param.topologieFusionArcsDoubles2 = true;
    param.topologieGraphePlanaire1 = true;
    param.topologieGraphePlanaire2 = true;
    param.topologieSeuilFusionNoeuds2 = 0.1;
    param.varianteFiltrageImpassesParasites = false;
    param.projeteNoeuds1SurReseau2 = false;
    param.projeteNoeuds1SurReseau2DistanceNoeudArc = 10; // 25
    param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 25; // 50
    param.projeteNoeuds2SurReseau1 = false;
    param.projeteNoeuds2SurReseau1DistanceNoeudArc = 10; // 25
    param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 25; // 50
    param.projeteNoeuds2SurReseau1ImpassesSeulement = false;
    param.varianteForceAppariementSimple = true;
    param.distanceArcsMax = 50;
    param.distanceArcsMin = 30;
    param.distanceNoeudsMax = 10;
    param.varianteRedecoupageArcsNonApparies = true;
    param.debugTirets = true;
    param.debugBilanSurObjetsGeo = false;
    param.varianteRedecoupageArcsNonApparies = true;
    param.debugAffichageCommentaires = 2;
    LOGGER.info("End setting parameters");

    try {

      LOGGER.info("Start network data matching");
      ResultatAppariement resultatAppariement = NetworkDataMatching.networkDataMatching(param);
      LOGGER.info("End network data matching");

      LOGGER.info("Start recalage");
      CarteTopo reseauRecale = Recalage.recalage(resultatAppariement.getReseauRef(), 
          resultatAppariement.getReseauComp(), resultatAppariement.getLinkDataSet());
      LOGGER.info("End recalage");

      // Get links
      IPopulation<Arc> arcs = reseauRecale.getPopArcs();

      // Convert to geoserver object
      LOGGER.info("Start Converting");
      SimpleFeatureCollection correctedNetwork = GeOxygeneGeoToolsTypes.convert2FeatureCollection(arcs, popRef.getSchema()
        .getCoordinateReferenceSystem());
      resultatAppariement.setNetworkMatched(correctedNetwork);
      LOGGER.info("End Converting");
      
      if (LOGGER.isEnabledFor(Level.DEBUG)) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        LOGGER.debug("free memory: " + freeMemory / 1024);
        LOGGER.debug("allocated memory: " + allocatedMemory / 1024);
        LOGGER.debug("max memory: " + maxMemory / 1024);
        LOGGER.debug("total free memory: " + (freeMemory + (maxMemory - allocatedMemory)) / 1024);
      }
      
      // Create result
      // ResultatAppariement result = new ResultatAppariement(resultatAppariement, correctedNetwork);
      // Return result
      // return result;
      // return correctedNetwork;
      
      return resultatAppariement;

    } catch (Exception e) {
      e.printStackTrace();
      throw new ProcessException("Error during network data matching process");
    }
    
  }
  
}
