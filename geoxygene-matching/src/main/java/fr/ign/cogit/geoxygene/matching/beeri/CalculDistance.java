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

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.Population;

public class CalculDistance {
  
  /** 
   * Methode qui renvoie la distance au 2eme plus proche voisin d'un point P1 
   * dans une population pop,
   * Si il n'y a pas de plus proche voisin, on renvoie 1;
   * Si il n'y a pas de deuxieme plus proche voisin, on renvoie 1;
   */
  public static double deuxiemePlusProcheVoisin(IFeature P1,  IPopulation<IFeature> candidatsPopPPV, 
      double seuilDistanceMax) {
      
    // On recherche tous les objets assez proches de P1 dans pop (à moins du seuil)
  
    // On parcourt ces candidats 
    // On retrouve le plus proche voisin, P3 et sa distance à P1
    IFeature candidatRetenu =null;
    double distanceMin = 2 * seuilDistanceMax;
    if (candidatsPopPPV.size() == 0) { 
      return 1;
    }
    
    // Normalement cette condition n'est jamais satisfaite parce que je teste dès le début
    // Si le nombre de candidats = 0; si oui je m'arrête.

    for (IFeature objetPop : candidatsPopPPV) {
      double distance = P1.getGeom().distance(objetPop.getGeom());
      if (distance <= distanceMin) {
        distanceMin = distance;
        candidatRetenu = objetPop;
      }
    }
    // dPPV2 = deuxiemePlusProcheVoisin( P1, candidatRetenu, candidatsPopPPV, seuilDistanceMax);
    candidatsPopPPV.remove(candidatRetenu);
    candidatRetenu =null;
    distanceMin = 2 * seuilDistanceMax;
    if (candidatsPopPPV.size() == 0) { 
      return 1;
    }
    
    for (IFeature objetPop : candidatsPopPPV) {
      double distance = P1.getGeom().distance(objetPop.getGeom());
      if(distance <= distanceMin) {
        distanceMin = distance;
        candidatRetenu = objetPop;
      }
    }
    
    double dPPV2  = P1.getGeom().distance(candidatRetenu.getGeom());
    return dPPV2;
  }
  
  /** 
   * Methode qui renvoie la distance au 2eme plus proche voisin d'un point P1 
   * dans une population pop, le plus proche voisin etant P2.
   * Si il n'y a pas de plus proche voisin, on renvoie MaxValue;
   * Si il n'y a pas de deuxieme plus proche voisin, on renvoie double.MaxValue;
   */
  public static double deuxiemePlusProcheVoisin(IFeature P1, IFeature P2, IPopulation<IFeature> pop, double seuilDistanceMax) {
      
    double MaxValue = Double.MAX_VALUE;

    // On recherche tous les objets assez proches de P1 dans pop (à moins du seuil)
    IPopulation<IFeature> pop1Ref = new Population<IFeature>("Ref");
    pop1Ref.setFeatureType(P1.getFeatureType());
    pop1Ref.add(P1);
    IPopulation<IFeature> candidatsPopPPV = pop.selectionElementsProchesGenerale(pop1Ref, seuilDistanceMax);
    // System.out.println("taille candidat 2 : "+candidatsPopPPV.size());
      
    // On enleve P2 de ces candidats
    candidatsPopPPV.remove(P2);
      
    // On parcourt ces candidats  
    // On retrouve le plus proche voisin, P3 et sa distance à P1
    IFeature candidatRetenu = null;
    double distanceMin = 2 * seuilDistanceMax;
    if (candidatsPopPPV.size() == 0) { 
      return MaxValue;
    }
    
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

}
