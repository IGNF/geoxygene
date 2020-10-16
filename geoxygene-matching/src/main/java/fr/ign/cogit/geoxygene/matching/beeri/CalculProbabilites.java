/**
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 * 
 */
package fr.ign.cogit.geoxygene.matching.beeri;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * 
 *
 */
public class CalculProbabilites {
  
  private static final Logger LOGGER = LogManager.getLogger(CalculProbabilites.class);

  /**
   * 
   * @param popRef
   * @param popComp
   * @param alpha
   * @return
   */
  public static List<Double> calculeProba(IPopulation<IFeature> popRef, IPopulation<IFeature> popComp, double alpha) {
    
    List<Double> listeProba = new ArrayList<Double>();
    
    // pour chaque objet A ( dans Ref ) on calcule la probabilité qu'il chosie B (dans Comp)
    
    // Indexation de la population de comparaison et de référence
    if (!popComp.hasSpatialIndex()) {
      LOGGER.info("Indexation de popComp (oronymes)");
      popComp.initSpatialIndex(Tiling.class, true, 10);
    }
    if (!popRef.hasSpatialIndex()) {
      LOGGER.info("Indexation de popRef (points remarquable de relief).");
      popRef.initSpatialIndex(Tiling.class, true,10);
    }
    
    // On parcourt les objets ref un par un
    for (IFeature objetRef : popRef) {
      double distanceTotaleRefComp = CalculProbabilites.calculDistanceTotale(objetRef, popComp, alpha);
      for (IFeature objetComp : popComp) {    
          double distanceTotaleCompRef = CalculProbabilites.calculDistanceTotale(objetComp, popRef, alpha);
          double distance = ((GM_Point) objetRef.getGeom()).getPosition().distance(((GM_Point) objetComp.getGeom()).getPosition());
          double probabObjRefChoisieObjComp = Math.pow(distance, alpha)/distanceTotaleRefComp;
          double probabObjCompChoisieObjetRef = Math.pow(distance, alpha)/distanceTotaleCompRef;
          double produitProba = probabObjRefChoisieObjComp * probabObjCompChoisieObjetRef ;
          listeProba.add(produitProba);
          // System.out.println("proba :"+probabObjRefChoisieObjComp);
      }
    }
      
    return listeProba;
      
  } 
  
  /**
   * 
   * @param popRef
   * @param popComp
   * @param alpha
   * @return
   */
  public static List<Double> calculeProbaRefChoisiePasComp(IPopulation<IFeature> popRef, IPopulation<IFeature> popComp, double alpha) {
    
    List<Double> listeProduitProba = new ArrayList<Double>();
    
    // pour chaque objet A ( dans Ref ) on calcule la probabilité qu'il chosie B (dans Comp)

    // Indexation de la population de comparaison et de référence
    if (!popComp.hasSpatialIndex()) {
      LOGGER.info("Indexation de popComp (oronymes)");
      popComp.initSpatialIndex(Tiling.class, true, 10);
    }
    if (!popRef.hasSpatialIndex()) {
      LOGGER.info("Indexation de popRef (points remarquable de relief).");
      popRef.initSpatialIndex(Tiling.class, true,10);
    }
    
    // On parcourt les objets ref un par un
    double produitChoisiePas = 1;
    for (IFeature objetRef : popRef) {
      // double distanceTotaleRefComp = CalculProbabilites.calculDistanceTotale(objetRef, popComp,alpha);
      for (IFeature objetComp : popComp) {    
          double distanceTotaleCompRef = CalculProbabilites.calculDistanceTotale(objetComp, popRef,alpha );
          double distance = ((GM_Point)objetRef.getGeom()).getPosition().distance(((GM_Point)objetComp.getGeom()).getPosition());
          // probabObjRefChoisieObjComp = Math.pow(distance,alpha )/distanceTotaleRefComp ;
          double probabObjCompChoisieObjetRef = Math.pow(distance,alpha )/distanceTotaleCompRef;
          // System.out.println("proba :"+probabObjCompChoisieObjetRef);
          produitChoisiePas = produitChoisiePas*(1-probabObjCompChoisieObjetRef);
      }
      listeProduitProba.add(produitChoisiePas);
          
      // System.out.println("produit proba :"+produitChoisiePas);
      // System.out.println("-------------------------");
    }
      
    return listeProduitProba;
      
  }
  
  /** 
   * Methode qui renvoie la distance au 2eme plus proche voisin d'un point P1 
   * dans une population pop, le plus proche voisin etant P2.
   *
   * S'il n'y a pas de plus proche voisin, on renvoie -1;
   * S'il n'y a pas de deuxieme plus proche voisin, on renvoie double.MaxValue;
   * 
   * @author amolteanu
   * @return distance; 
   * 
   */
  public static double deuxiemePlusProcheVoisin(IFeature P1, IFeature P2, IPopulation<IFeature> pop, double seuilDistanceMax) {
      
    // On recherche tous les objets assez proches de P1 dans pop (à moins du seuil)
    
    IPopulation<IFeature> pop1Ref = new Population<IFeature>("Ref");
    pop1Ref.setFeatureType(P1.getFeatureType());
    pop1Ref.add(P1);
    IPopulation<IFeature> candidatsPopPPV = pop.selectionElementsProchesGenerale(pop1Ref, 2 * seuilDistanceMax);
      
    // On enleve P2 de ces candidats
    candidatsPopPPV.remove(P2);
    if (candidatsPopPPV.size() == 0) {
      return Double.MAX_VALUE;
    }

    // On parcourt ces candidats 
    // On retrouve le plus proche voisin, P3 et sa distance à P1
      
    IFeature candidatRetenu =null;
    double distanceMin = 2 * seuilDistanceMax;
    for (IFeature objetPop : candidatsPopPPV) {
      double distance = P1.getGeom().distance(objetPop.getGeom());
      if (distance <= distanceMin) {
        distanceMin = distance;
        candidatRetenu = objetPop;
      }
    }
  
    double dP1P3  = P1.getGeom().distance(candidatRetenu.getGeom());
    return dP1P3;
  }
  
  /**
   * 
   * @param ref
   * @param popComp
   * @param alphfa
   * @return
   */
  public static double calculDistanceTotale(IFeature ref, IPopulation<IFeature> popComp, double alphfa) {
      double distanceTotale = 0;
      for (IFeature objetComp : popComp) {
        double distance = ((GM_Point)objetComp.getGeom()).getPosition().distance(((GM_Point)ref.getGeom()).getPosition());
        distance = Math.pow(distance, alphfa);
        distanceTotale = distance +distanceTotale;
      }
      if(distanceTotale == 0) {
          LOGGER.warn("une distanceTotale nulle trouvée");
          return Double.MAX_VALUE;
      } else { 
        return distanceTotale;
      }
  }

}
