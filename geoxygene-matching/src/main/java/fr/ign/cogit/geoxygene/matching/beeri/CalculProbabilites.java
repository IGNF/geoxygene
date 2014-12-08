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
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * 
 *
 */
public class CalculProbabilites {

  @SuppressWarnings("unchecked")
  public static List<Double> calculeProba(IFeatureCollection popRef, IFeatureCollection popComp, double alpha) {
      IFeature objetRef, objetComp;
      List<Double> listeProba = new ArrayList<Double>();
      double distance,distanceTotaleRefComp,distanceTotaleCompRef,
             probabObjCompChoisieObjetRef, probabObjRefChoisieObjComp, produitProba;
             distanceTotaleRefComp =0;distanceTotaleCompRef=0;
             probabObjRefChoisieObjComp=0;probabObjCompChoisieObjetRef=0;produitProba =0;
             
      //pour chaque objet A ( dans Ref ) on calcule la probabilité qu'il chosie B (dans Comp)

      //Indexation de la population de comparaison et de référence
             
      if(!popComp.hasSpatialIndex()){
          System.out.println("Indexation des oronymes");
          popComp.initSpatialIndex(Tiling.class, true,10);
      }
      if(!popRef.hasSpatialIndex()){
          System.out.println("Indexation des points remarquable de relief");
          popRef.initSpatialIndex(Tiling.class, true,10);
      }
      // On parcourt les objets ref un par un
      Iterator itPopRef = popRef.getElements().iterator();
      while (itPopRef.hasNext()){
          objetRef = (IFeature)itPopRef.next();
          distanceTotaleRefComp = CalculProbabilites.calculDistanceTotale(objetRef, popComp,alpha);
          
          Iterator itPopComp =popComp.getElements().iterator();
          while(itPopComp.hasNext()){
              objetComp = (IFeature)itPopComp.next();
              distanceTotaleCompRef = CalculProbabilites.calculDistanceTotale(objetComp, popRef,alpha );
              distance= Distances.distance(((GM_Point)objetRef.getGeom()).getPosition(), ((GM_Point)objetComp.getGeom()).getPosition());
              probabObjRefChoisieObjComp = Math.pow(distance,alpha )/distanceTotaleRefComp ;
              probabObjCompChoisieObjetRef = Math.pow(distance,alpha )/distanceTotaleCompRef;
              produitProba = probabObjRefChoisieObjComp * probabObjCompChoisieObjetRef ;
              listeProba.add(produitProba);
              //System.out.println("proba :"+probabObjRefChoisieObjComp);
          }
      }
      
      return listeProba;
      
  } 
  
      @SuppressWarnings("unchecked")
      public static List<Double> calculeProbaRefChoisiePasComp(IFeatureCollection popRef, IFeatureCollection popComp,double alpha){
          IFeature objetRef, objetComp;
          List<Double> listeProduitProba = new ArrayList<Double>();
          @SuppressWarnings("unused")
          double distance,distanceTotaleCompRef,probabObjCompChoisieObjetRef, 
                 probabObjRefChoisieObjComp, produitProba, produitChoisiePas,distanceTotaleRefComp;
                 distanceTotaleCompRef=0; distanceTotaleRefComp=0;
                 probabObjRefChoisieObjComp=0;probabObjCompChoisieObjetRef=0;produitProba =0;
                 produitChoisiePas =1;
                 
          //pour chaque objet A ( dans Ref ) on calcule la probabilité qu'il chosie B (dans Comp)

          //Indexation de la population de comparaison et de référence
                 
          if(!popComp.hasSpatialIndex()){
              System.out.println("Indexation des oronymes");
              popComp.initSpatialIndex(Tiling.class, true,10);
          }
          if(!popRef.hasSpatialIndex()){
              System.out.println("Indexation des points remarquable de relief");
              popRef.initSpatialIndex(Tiling.class, true,10);
          }
          // On parcourt les objets ref un par un
      Iterator itPopRef = popRef.getElements().iterator();
      while (itPopRef.hasNext()){
          objetRef = (IFeature)itPopRef.next();
          distanceTotaleRefComp = CalculProbabilites.calculDistanceTotale(objetRef, popComp,alpha);
          
          Iterator itPopComp =popComp.getElements().iterator();
          while(itPopComp.hasNext()){
              objetComp = (IFeature)itPopComp.next();
              distanceTotaleCompRef = CalculProbabilites.calculDistanceTotale(objetComp, popRef,alpha );
              distance= Distances.distance(((GM_Point)objetRef.getGeom()).getPosition(), ((GM_Point)objetComp.getGeom()).getPosition());
              //probabObjRefChoisieObjComp = Math.pow(distance,alpha )/distanceTotaleRefComp ;
              probabObjCompChoisieObjetRef = Math.pow(distance,alpha )/distanceTotaleCompRef;
              //System.out.println("proba :"+probabObjCompChoisieObjetRef);
              produitChoisiePas=produitChoisiePas*(1-probabObjCompChoisieObjetRef);
          }
              listeProduitProba.add(produitChoisiePas);
          
              //System.out.println("produit proba :"+produitChoisiePas);
              //System.out.println("-------------------------");
      }
      
      return listeProduitProba;
      
  }
  
  /** Methode qui renvoie la distance au 2eme plus proche voisin d'un point P1 
   * dans une population pop, le plus proche voisin etant P2.
   * @author amolteanu
   * @return distance; 
   * Si il n'y a pas de plus proche voisin, on renvoie -1;
   * Si il n'y a pas de deuxieme plus proche voisin, on renvoie double.MaxValue;
   */
  @SuppressWarnings("null")
  public static double deuxiemePlusProcheVoisin(IFeature P1, IFeature P2, IFeatureCollection<IFeature> pop, double seuilDistanceMax) {
      
      // On recherche tous les objets assez proches de P1 dans pop (à moins du seuil)
      IFeatureCollection<IFeature> candidatsPopPPV; 
      IFeature objetPop, candidatRetenu;
      double distance, distanceMin, dP1P3, MaxValue;
      MaxValue=Double.MAX_VALUE ;
      candidatsPopPPV = (IFeatureCollection<IFeature>) pop.select(((GM_Point)P1.getGeom()).getPosition(),2*seuilDistanceMax );
      
      //On enleve P2 de ces candidats
      candidatsPopPPV.remove(P2);
      if (candidatsPopPPV.size() == 0) return MaxValue;

      //on parcourt ces candidats 
      //on retrouve le plus proche voisin, P3 et sa distance à P1
      Iterator<IFeature> itcandidatsPPV= candidatsPopPPV.getElements().iterator();
      candidatRetenu =null;
      distanceMin = 2*seuilDistanceMax;
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
  
  
  @SuppressWarnings("unchecked")
  public static double calculDistanceTotale(IFeature ref, IFeatureCollection popComp,double alphfa) {
      IFeature objetComp;
      double distance,distanceTotale, MaxValue ;
      distanceTotale =0;
      MaxValue=Double.MAX_VALUE ;
      Iterator itPopComp = popComp.getElements().iterator();
      while(itPopComp.hasNext()){
          objetComp=(IFeature)itPopComp.next();
          distance=Distances.distance( ((GM_Point)objetComp.getGeom()).getPosition(), ((GM_Point)ref.getGeom()).getPosition());
          distance = Math.pow(distance, alphfa);
          distanceTotale = distance +distanceTotale;
      }
      if(distanceTotale == 0) {
          System.out.println("une distanceTotale nulle trouvée");
          return MaxValue ;
      }
      else return distanceTotale;
  }

}
