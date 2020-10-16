package fr.ign.cogit.geoxygene.matching.geopoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;
import fr.ign.cogit.geoxygene.util.string.ApproximateMatcher;

/**
 * Points Data matching using distance and name. 
 * Matching criteria are sequentially applied
 * 
 */


public class SequentiallyPositionName {
  
  /** Logger. */
  private static final Logger LOGGER = LogManager.getLogger(SequentiallyPositionName.class);
  
  /**
   * Appariement par recherche du plus proche voisin 
   *    et qui prend en compte les toponymes. 
   * Chaque élément de popRef est apparié avec son plus proche voisin
   * dans popComp
   * 
   * @param popRef Population d'objets avec une géomtrie ponctuelle 
   *               pour lesquels on cherche un appariement.
   * @param popComp Population d'objets candidats avec une géomtrie ponctuelle.
   * 
   * @param seuilEcart seuil distance textuelle
   * @param seuilDistanceMax Seuil de distance au dessous duquel on n'apparie
   *          pas deux objets.
   * @param attributeRef
   * @param attributeComp
   * 
   * @return Ensemble de liens d'appariement. 
   *         Seulement des liens 1-1 sont créés.
   * 
   */
  public static EnsembleDeLiens appariementPPVEvalTop (IPopulation<IFeature> popRef, IPopulation<IFeature> popComp,
      double seuilEcart, double seuilDistanceMax, String attributeRef, String attributeComp) {
    
    EnsembleDeLiens liens = new EnsembleDeLiens();
    
    ApproximateMatcher AM = new ApproximateMatcher();
    AM.setIgnoreCase(true);
    
    // Indexation de la population de comparaison
    if (!popComp.hasSpatialIndex()) {
      LOGGER.info("Indexation de popComp (oronymes)");
      popComp.initSpatialIndex(Tiling.class, true, 10);
    }
    
    int sizeRef = popRef.getElements().size();
    int sizeComp = popComp.getElements().size();
    LOGGER.info("Size popRef " + sizeRef);
    LOGGER.info("Size popComp = " + sizeComp);
    
    // On parcourt les objets ref un par un
    for (IFeature objetRef : popRef) {
      
      IPopulation<IFeature> pop1Ref = new Population<IFeature>("Ref");
      pop1Ref.setFeatureType(objetRef.getFeatureType());
      pop1Ref.add(objetRef);
      IPopulation<IFeature> candidatsApp = popComp.selectionElementsProchesGenerale(pop1Ref, seuilDistanceMax);
    
      if (candidatsApp.size() == 0) {
        continue;
      }
      
      // Pour chaque objet ref on calcule la distance à tous les objets comp
      // proches
      // pour ne garder que le plus proche
      double distPP = seuilDistanceMax;
      IFeature candidatRetenu = null;
      for (IFeature objetComp : candidatsApp) {
        double distance = ((GM_Point) objetRef.getGeom()).getPosition().distance(((GM_Point) objetComp.getGeom()).getPosition());
        if (distance <= distPP) {
          distPP = distance;
          candidatRetenu = objetComp;
        }
      }
      
      // On crée un nouveau lien avec sa géométrie et son évaluation
      Lien lien = liens.nouvelElement();
      lien.addObjetRef(objetRef);
      lien.addObjetComp(candidatRetenu);

      GM_LineString ligne = new GM_LineString();
      ligne.addControlPoint(((GM_Point) objetRef.getGeom()).getPosition());
      ligne.addControlPoint(((GM_Point) candidatRetenu.getGeom()).getPosition());
      lien.setGeom(ligne);
      
      String featureComp = candidatRetenu.getAttribute(attributeComp).toString();
      featureComp = AM.processAccent(featureComp);
      String featureRef = objetRef.getAttribute(attributeRef).toString();
      featureRef = AM.processAccent(featureRef);
      
      int ecart = AM.match(featureRef, featureComp);
      // System.out.println(ecart + "(" + featureRef + ", " + featureComp + ")");
      int ecartRelatif = 100 * ecart / Math.max(featureRef.length(), featureComp.length());
      if (ecart <= seuilEcart || featureComp.startsWith(featureRef)) {
        if (ecartRelatif > 50) {
          if (featureComp.startsWith(featureRef) || featureComp.endsWith(featureRef)) {
            lien.setEvaluation(0.5);
          } else {
            lien.setEvaluation(0);
          }
        } else if (ecartRelatif >= 10 && ecartRelatif <= 50) {
          lien.setEvaluation(0.5);
        } else {
          lien.setEvaluation(1);
        }
      } else {
        lien.setEvaluation(0);
      }
    
    }
    
    return liens;
  }
 
 

}
