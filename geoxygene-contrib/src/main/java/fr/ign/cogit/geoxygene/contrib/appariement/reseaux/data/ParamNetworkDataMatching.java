package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data;

import java.util.Map;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;

/**
 * 
 * 
 *
 */
public class ParamNetworkDataMatching {
  
  private ParamDatasetNetworkDataMatching paramDataset = null;
  // private ParamTopoTreatmentNetworkDataMatching paramTopoTreatment = null;
  private ParamDirectionNetworkDataMatching paramDirection = null;

  /** A classic logger. */
  static Logger logger = Logger.getLogger(ParamNetworkDataMatching.class.getName());
  
  /**
   * Constructor.
   */
  public ParamNetworkDataMatching() {
    paramDataset = new ParamDatasetNetworkDataMatching();
    paramDirection = new ParamDirectionNetworkDataMatching();
  }
  
  public void setParamDirection(ParamDirectionNetworkDataMatching pdnm) {
    paramDirection = pdnm;
  }
  
  /**
   * Transform new structure to old structure.
   * @return ParametresApp
   */
  public ParametresApp ParamNDMToParamApp() {
    // Create old object paramApp
    ParametresApp param = new ParametresApp();
    
    // Set parameters
    
    // Set dataset
    param.populationsArcs1 = paramDataset.getPopulationsArcs1();
    param.populationsArcs2 = paramDataset.getPopulationsArcs1();
    param.populationsNoeuds1 = paramDataset.getPopulationsNoeuds1();
    param.populationsNoeuds2 = paramDataset.getPopulationsNoeuds2();
    
    // Set direction param
    param.populationsArcsAvecOrientationDouble = paramDirection.getPopulationsArcsAvecOrientationDouble();
    param.attributOrientation1 = paramDirection.getAttributOrientation1();
    param.attributOrientation2 = paramDirection.getAttributOrientation2();
    param.orientationMap1 = paramDirection.getOrientationMap1();
    param.orientationMap2 = paramDirection.getOrientationMap2();
    
    // Set topo treatment param
    
    // param.distanceArcsMax = paramAppData.getNoeudsMax();
    // param.distanceArcsMin = paramAppData.getArcsMin();
    // param.distanceNoeudsMax = paramAppData.getArcsMax();
    // param.distanceNoeudsImpassesMax = paramAppData.getNoeudsImpassesMax();
    // param.topologieFusionArcsDoubles1 = true;
    // param.topologieFusionArcsDoubles2 = true;
    // param.topologieGraphePlanaire1 = true;
    // param.topologieGraphePlanaire2 = true;
    // param.topologieSeuilFusionNoeuds2 = 0.1;
    // param.varianteFiltrageImpassesParasites = false;
    // param.projeteNoeuds1SurReseau2 = false;
    // param.projeteNoeuds1SurReseau2DistanceNoeudArc = 10; // 25
    // param.projeteNoeuds1SurReseau2DistanceProjectionNoeud = 25; // 50
    // param.projeteNoeuds2SurReseau1 = false;
    // param.projeteNoeuds2SurReseau1DistanceNoeudArc = 10; // 25
    // param.projeteNoeuds2SurReseau1DistanceProjectionNoeud = 25; // 50
    // param.projeteNoeuds2SurReseau1ImpassesSeulement = false;
    // param.varianteForceAppariementSimple = true;
    // param.varianteRedecoupageArcsNonApparies = true;
    // param.debugTirets = false;
    // param.debugBilanSurObjetsGeo = false;
    // param.varianteRedecoupageArcsNonApparies = true;
    // param.debugAffichageCommentaires = 2;
    
    return param;
  }
}
