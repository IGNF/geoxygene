package fr.ign.cogit.process.geoxygene.cartetopo;

import java.util.logging.Logger;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.data.CarteTopoData;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;
import fr.ign.cogit.geoxygene.util.conversion.GeOxygeneGeoToolsTypes;
import fr.ign.cogit.process.geoxygene.GeoxygeneProcess;

/**
 * 
 * 
 * @author MDVan-Damme
 */
@DescribeProcess(title = "CreateCarteTopo", description = "Create topological map from a Feature Collection representing a Network")
public class PlanarGraphCreationProcess implements GeoxygeneProcess {
  
  private final static Logger LOGGER = Logger.getLogger(BuildingBlocksCreationProcess.class.getName());
  
  
  @DescribeResult(name = "CarteTopo", description = "Topological map")
  public CarteTopoData execute(
      @DescribeParameter(name = "features", description = "The feature collection whose faces will be computed") SimpleFeatureCollection features,
      // EPSG
      @DescribeParameter(name = "doPlanarGraph", description = "Make the graph planar") boolean doPlanarGraph,
      @DescribeParameter(name = "mergedThreshold", description = "All nodes within tolerance are merged") double mergedThreshold,
      @DescribeParameter(name = "tolerance", description = "The tolerance used in order to make the graph planar") double tolerance) {
    
    LOGGER.info("Start Converting");
    IFeatureCollection<?> collection = GeOxygeneGeoToolsTypes.convert2IFeatureCollection(features);
    LOGGER.info("End Converting");
    
    CarteTopo map = new CarteTopo("MAP");
    Chargeur.importAsEdges(collection, map, null, null, null, null, null, tolerance);
    
    LOGGER.info("Creation des noeuds manquants");
    map.creeNoeudsManquants(tolerance);
    
    LOGGER.info("Filtrage des noeuds doublons (plusieurs noeuds localisés au même endroit).");
    map.filtreDoublons(tolerance);
    
    LOGGER.info("Creation les relation 'noeud initial' et 'noeud final' d'un arc");
    map.creeTopologieArcsNoeuds(tolerance);
    
    LOGGER.info("Filtre les arcs en double (en double = même géométrie et même orientation).");
    map.filtreArcsDoublons();
    
    if (doPlanarGraph) {
      LOGGER.info("Making planar");
      map.rendPlanaire(tolerance);
    }
    
    LOGGER.info("Filtrage des noeuds doublons (plusieurs noeuds localisés au même endroit).");
    map.filtreDoublons(tolerance);
    
    LOGGER.info("Fusionne en un seul noeud, tous les noeuds proches de moins de 'tolerance'.");
    map.fusionNoeuds(mergedThreshold);
    
    LOGGER.info("Filtrage des noeuds isolés (c'est-à-dire connectés à aucun arc). Ceux-ci sont enlevés de la Carte Topo.");
    map.filtreNoeudsIsoles();
    
    LOGGER.info("Filtrage des noeuds 'simples', c'est-à-dire avec seulement deux arcs incidents, si ils ont des orientations compatibles.");
    map.filtreNoeudsSimples();
    
    LOGGER.info("Filtre les arcs en double (en double = même géométrie et même orientation).");
    map.filtreArcsDoublons();
    
    // LOGGER.info("Creating faces");
    map.creeTopologieFaces();
    
    // Prepare the result format
    CarteTopoData cartetopo = new CarteTopoData();
    cartetopo.setPopEdge(map.getPopArcs());
    cartetopo.setPopNode(map.getPopNoeuds());
    cartetopo.setPopFace(map.getPopFaces());
    
    // Return carte topo
    return cartetopo;
    
  }

}
