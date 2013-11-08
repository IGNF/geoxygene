package fr.ign.cogit.process.geoxygene.netmatching;

import org.apache.log4j.Logger;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

import fr.ign.cogit.process.geoxygene.GeoxygeneProcess;
import fr.ign.parameters.Parameters;

/**
 * 
 * @author MDVan-Damme
 */
@DescribeProcess(title = "AppariementRéseaux", description = "Appariement")
public class NetworkDataMatchingWithParamProcess implements GeoxygeneProcess {
    
    /** LOGGER. */
    private final static Logger LOGGER = Logger.getLogger(NetworkDataMatchingWithParamProcess.class.getName());
    
    @DescribeResult(name = "OK", description = "Ok !!")
    public String execute(
        @DescribeParameter(name = "param",  description = "Paramètres") Parameters param,
        @DescribeParameter(name = "reseau1", description = "Réseau 1 (moins détaillé)") SimpleFeatureCollection reseau1,
        @DescribeParameter(name = "reseau2", description = "Réseau 2 (plus détaillé)") SimpleFeatureCollection reseau2) {
        
        LOGGER.debug("====================================================================");
        LOGGER.debug("NetworkDataMatchingWithParamProcess");
        LOGGER.debug(param);
        LOGGER.debug("====================================================================");
        
        System.out.println("DistanceNoeudsMax = " + param.get("DistanceNoeudsMax"));
        System.out.println("DistanceArcsMax = " + param.get("DistanceArcsMax"));
        
        // Set parameters
        /*ParamNetworkDataMatching param = new ParamNetworkDataMatching();
        param.setParamDistance(param1);
        
        // Converting networks
        LOGGER.debug("Start Converting networks : reference and comparative");
        IFeatureCollection<?> gReseau1 = GeOxygeneGeoToolsTypes.convert2IFeatureCollection(reseau1);
        IFeatureCollection<?> gReseau2 = GeOxygeneGeoToolsTypes.convert2IFeatureCollection(reseau2);
        LOGGER.debug("End Converting networks");
        
        
        LOGGER.debug("Start setting parameters");
        
        DatasetNetworkDataMatching datasetNetwork1 = new DatasetNetworkDataMatching();
        datasetNetwork1.addPopulationsArcs((IPopulation<IFeature>)gReseau1);
        DatasetNetworkDataMatching datasetNetwork2 = new DatasetNetworkDataMatching();
        datasetNetwork2.addPopulationsArcs((IPopulation<IFeature>)gReseau2);
        
        NetworkDataMatching networkDataMatchingProcess = new NetworkDataMatching(param, datasetNetwork1, datasetNetwork2);
        networkDataMatchingProcess.setActions(true, false);
        // ResultNetworkDataMatching resultatAppariement = networkDataMatchingProcess.networkDataMatching();*/
        
        return "OK";
    }

}
