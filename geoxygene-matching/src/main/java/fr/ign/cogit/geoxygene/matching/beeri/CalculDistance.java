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

import java.util.Iterator;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public class CalculDistance {
  
  /** Methode qui renvoie la distance au 2eme plus proche voisin d'un point P1 
   * dans une population pop,
   * Si il n'y a pas de plus proche voisin, on renvoie 1;
   * Si il n'y a pas de deuxieme plus proche voisin, on renvoie 1;
   */
  @SuppressWarnings({ "unchecked", "null" })
  public static double deuxiemePlusProcheVoisin(IFeature P1,  IFeatureCollection candidatsPopPPV, double seuilDistanceMax) {
      
      // On recherche tous les objets assez proches de P1 dans pop (à moins du seuil)
      IFeature objetPop, candidatRetenu;
      double distance, distanceMin, dPPV2;
  
      //on parcourt ces candidats 
      //on retrouve le plus proche voisin, P3 et sa distance à P1
      candidatRetenu =null;
      distanceMin = 2* seuilDistanceMax;
      if (candidatsPopPPV.size() == 0) return 1;
      //normalement cette condition n'est jamais satisfaite parce que je teste dès le début
      //si le nombre de candidats =0; si oui je m'arrête.

      Iterator itcandidatsPPV= candidatsPopPPV.getElements().iterator();
      while(itcandidatsPPV.hasNext()){
          objetPop = (IFeature)itcandidatsPPV.next();
          distance=P1.getGeom().distance(objetPop.getGeom());
          if(distance<=distanceMin) {
              distanceMin=distance;
              candidatRetenu = objetPop;
          }
      }
      //dPPV2=deuxiemePlusProcheVoisin( P1, candidatRetenu, candidatsPopPPV, seuilDistanceMax);
      candidatsPopPPV.remove(candidatRetenu);
      candidatRetenu =null;
      distanceMin = 2* seuilDistanceMax;
      if (candidatsPopPPV.size() == 0) return 1;
      Iterator itcandidatsPPV2= candidatsPopPPV.getElements().iterator();
      while(itcandidatsPPV2.hasNext()){
          objetPop = (IFeature)itcandidatsPPV2.next();
          distance=P1.getGeom().distance(objetPop.getGeom());
          if(distance<=distanceMin) {
              distanceMin=distance;
              candidatRetenu = objetPop;
          }
      }
      dPPV2  = P1.getGeom().distance(candidatRetenu.getGeom());
      return dPPV2;
  }
  
  /** Methode qui renvoie la distance au 2eme plus proche voisin d'un point P1 
   * dans une population pop, le plus proche voisin etant P2.
   * Si il n'y a pas de plus proche voisin, on renvoie MaxValue;
   * Si il n'y a pas de deuxieme plus proche voisin, on renvoie double.MaxValue;
   */
  @SuppressWarnings({ "unchecked", "null" })
  public static double deuxiemePlusProcheVoisin(IFeature P1, IFeature P2, IFeatureCollection pop, double seuilDistanceMax) {
      
      // On recherche tous les objets assez proches de P1 dans pop (à moins du seuil)
      IFeatureCollection candidatsPopPPV; 
      IFeature objetPop, candidatRetenu;
      double distance, distanceMin, dP1P3, MaxValue;
      MaxValue=Double.MAX_VALUE ;
      candidatsPopPPV = (IFeatureCollection) pop.select(((GM_Point)P1.getGeom()).getPosition(),seuilDistanceMax );
      //System.out.println("taille candidat 2 : "+candidatsPopPPV.size());
      
      //On enleve P2 de ces candidats
      candidatsPopPPV.remove(P2);
      
      //on parcourt ces candidats 
      //on retrouve le plus proche voisin, P3 et sa distance à P1
      candidatRetenu =null;
      distanceMin = 2* seuilDistanceMax;
      if (candidatsPopPPV.size() == 0) return MaxValue;
      Iterator <IFeature>itcandidatsPPV= candidatsPopPPV.getElements().iterator();
      while(itcandidatsPPV.hasNext()){
          objetPop = itcandidatsPPV.next();
          distance=P1.getGeom().distance(objetPop.getGeom());
          if(distance<=distanceMin) {
              distanceMin=distance;
              candidatRetenu = objetPop;
          }
      }
      dP1P3  = P1.getGeom().distance(candidatRetenu.getGeom());
      return dP1P3;
  }

}
