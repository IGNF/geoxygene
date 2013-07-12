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

import java.util.HashMap;
import java.util.Map;

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

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.Recalage;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.DatasetNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDirectionNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDistanceNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamProjectionNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamTopologyTreatmentNetwork;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkDataMatching;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.process.NetworkDataMatching;
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
  private final static Logger LOGGER = Logger.getLogger(NetworkDataMatchingProcess.class.getName());

  /**
   * 
   * 
   * 
   * @param popRef
   * @param popComp
   * @return Results ResultatAppariement
   */
  @DescribeResult(name = "popApp", description = "Network Matched")
  public ResultNetworkDataMatching execute(
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
    LOGGER.debug("Start Converting networks : reference and comparative");
    IFeatureCollection<?> gPopRef = GeOxygeneGeoToolsTypes.convert2IFeatureCollection(popRef);
    IFeatureCollection<?> gPopComp = GeOxygeneGeoToolsTypes.convert2IFeatureCollection(popComp);
    LOGGER.debug("End Converting networks");
    
    // Set parameters
    LOGGER.debug("Start setting parameters");
    
    DatasetNetworkDataMatching datasetNetwork1 = new DatasetNetworkDataMatching();
    datasetNetwork1.addPopulationsArcs((IPopulation<IFeature>)gPopRef);
    DatasetNetworkDataMatching datasetNetwork2 = new DatasetNetworkDataMatching();
    datasetNetwork2.addPopulationsArcs((IPopulation<IFeature>)gPopComp);
    
    ParamNetworkDataMatching param = new ParamNetworkDataMatching();
    float distanceNoeudsMax = 20;
    
    // Direction
    // Direction réseau 1
    ParamDirectionNetworkDataMatching paramDirectionNetwork1 = new ParamDirectionNetworkDataMatching();
    param.setParamDirectionNetwork1(paramDirectionNetwork1);
    // Direction réseau 2
    ParamDirectionNetworkDataMatching paramDirectionNetwork2 = new ParamDirectionNetworkDataMatching();
    paramDirectionNetwork2.setAttributOrientation("sens_de_circulation");
    Map<Integer, String> orientationMap2 = new HashMap<Integer, String>();
    orientationMap2.put(1, "Sens direct");
    orientationMap2.put(-1, "Sens inverse");
    orientationMap2.put(2, "Double sens");
    paramDirectionNetwork2.setOrientationMap(orientationMap2);
    param.setParamDirectionNetwork2(paramDirectionNetwork2);
    
    // Distance
    ParamDistanceNetworkDataMatching paramDistance = new ParamDistanceNetworkDataMatching();
    paramDistance.setDistanceNoeudsMax(distanceNoeudsMax);
    paramDistance.setDistanceArcsMax(2 * distanceNoeudsMax);
    paramDistance.setDistanceArcsMin(distanceNoeudsMax);
    param.setParamDistance(paramDistance);
    
    // Topologie
    // Topologie reseau 1
    ParamTopologyTreatmentNetwork paramTopo1 = new ParamTopologyTreatmentNetwork();
    paramTopo1.setGraphePlanaire(true);
    paramTopo1.setFusionArcsDoubles(true);
    paramTopo1.setSeuilFusionNoeuds(0.1);
    param.setParamTopoNetwork1(paramTopo1);
    // Topologie reseau 2
    ParamTopologyTreatmentNetwork paramTopo2 = new ParamTopologyTreatmentNetwork();
    paramTopo2.setGraphePlanaire(false);
    paramTopo2.setFusionArcsDoubles(false);
    // paramTopo2.setSeuilFusionNoeuds(0.1);
    param.setParamTopoNetwork1(paramTopo2);
    
    // Projection
    // Projection reseau 1
    ParamProjectionNetworkDataMatching paramProj1 = new ParamProjectionNetworkDataMatching();
    paramProj1.setProjeteNoeuds1SurReseau2(true);
    paramProj1.setProjeteNoeuds1SurReseau2DistanceNoeudArc(distanceNoeudsMax);
    paramProj1.setProjeteNoeuds1SurReseau2DistanceProjectionNoeud(2 * distanceNoeudsMax);
    param.setParamProjNetwork1(paramProj1);
    // Projection reseau 2
    ParamProjectionNetworkDataMatching paramProj2 = new ParamProjectionNetworkDataMatching();
    paramProj2.setProjeteNoeuds1SurReseau2(true);
    paramProj2.setProjeteNoeuds1SurReseau2DistanceNoeudArc(distanceNoeudsMax);
    paramProj2.setProjeteNoeuds1SurReseau2DistanceProjectionNoeud(2 * distanceNoeudsMax);
    paramProj2.setProjeteNoeuds1SurReseau2ImpassesSeulement(false);
    param.setParamProjNetwork2(paramProj2);
    
    /*
    // Ces paramètres ne sont plus pris en compte je crois, à vérifier que ce sont ceux par défaut
    param.varianteForceAppariementSimple = false;
    param.varianteRedecoupageArcsNonApparies = true;
    param.debugBilanSurObjetsGeo = false;
    param.varianteFiltrageImpassesParasites = false;
    */
    
    LOGGER.debug("End setting parameters");
    

    try {

      LOGGER.debug("Start network data matching");
      
      NetworkDataMatching networkDataMatchingProcess = new NetworkDataMatching(param, datasetNetwork1, datasetNetwork2);
      networkDataMatchingProcess.setActions(true, false);
      
      // On lance l'appariement
      ResultNetworkDataMatching resultatAppariement = networkDataMatchingProcess.networkDataMatching();
      // ResultNetworkDataMatching resultatAppariement = NetworkDataMatching.networkDataMatching(param);
      LOGGER.debug("End network data matching");
      
      System.out.println(resultatAppariement.getResultStat().getStatsEdgesOfNetwork1().toString());
      System.out.println(resultatAppariement.getResultStat().getStatsNodesOfNetwork1().toString());
      System.out.println(resultatAppariement.getResultStat().getStatsEdgesOfNetwork2().toString());
      System.out.println(resultatAppariement.getResultStat().getStatsNodesOfNetwork2().toString());
      
      System.out.println("Nombre arcs reseau 1 = " + resultatAppariement.getReseauStat1().getReseauApp().getPopArcs().size());
      System.out.println("Nombre noeuds reseau 1 = " + resultatAppariement.getReseauStat1().getReseauApp().getPopNoeuds().size());
      System.out.println("Nombre arcs reseau 2 = " + resultatAppariement.getReseauStat2().getReseauApp().getPopArcs().size());
      System.out.println("Nombre noeuds reseau 2 = " + resultatAppariement.getReseauStat2().getReseauApp().getPopNoeuds().size());

      LOGGER.debug("Start recalage");
      CarteTopo reseauRecale = Recalage.recalage(resultatAppariement.getReseauStat1().getReseauApp(), 
          resultatAppariement.getReseauStat2().getReseauApp(), 
          resultatAppariement.getLiens());
      LOGGER.debug("End recalage");

      // Get arcs 
      IPopulation<Arc> arcs = reseauRecale.getPopArcs();

      // Convert to geoserver object
      LOGGER.debug("Start Converting");
      SimpleFeatureCollection correctedNetwork = GeOxygeneGeoToolsTypes.convert2FeatureCollection(arcs, popRef.getSchema()
        .getCoordinateReferenceSystem());
      resultatAppariement.setNetworkMatched(correctedNetwork);
      LOGGER.debug("End Converting");
      
      return resultatAppariement;

    } catch (Exception e) {
      e.printStackTrace();
      throw new ProcessException("Error during network data matching process");
    }
    
  }
  
}
