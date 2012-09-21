package fr.ign.cogit.geoxygene.sig3d.simplification.aglokada;

import java.util.ArrayList;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.sig3d.calculation.Proximity;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 * Permet de plaquer un toit sur un bâtiment (pour la simplification)
 * 
 * Map a roof on a building (during simplification process)
 * 
 */
@Deprecated
public class RoofCreation {

  /**
   * 
   * @param lBuildingsSummits sommets du batiment
   * @param lFacesRoofs faces du toit initial
   * @param threshold seuil de déplacement des sommets du toit
   * @return une liste de faces correspondant au nouveau toit
   */
  public static ArrayList<IOrientableSurface> roof(
      IDirectPositionList lBuildingsSummits,
      ArrayList<IOrientableSurface> lFacesRoofs, double threshold) {

    ArrayList<IOrientableSurface> toit = new ArrayList<IOrientableSurface>();

    int nbpointsatraiter = lBuildingsSummits.size();

    // Faire une correspondance entre les points du toits et du sommet du
    // batiment simplifié
    // Il s'agit d'une relation par numéro de lignes
    IDirectPositionList lPointsCorresToits = new DirectPositionList();
    double[] distance = new double[nbpointsatraiter];

    // On effectue le traitement pour chaquue sommet de l'ancien bâtiment
    for (int i = 0; i < nbpointsatraiter; i++) {

      // On lance l'Opérateur de calcul de proximité
      // on calcule le point du batiment simplifié le plus proche du toit
      // initial
      Proximity cp = new Proximity();
      IDirectPosition tempP = lBuildingsSummits.get(i);
      cp.nearest(tempP, lFacesRoofs);

      // C'est la facette de toit la plus proche du point du batiment
      // simplifié
      IOrientableSurface faceTemp = cp.containingFace;

      // C'est le point le plus proche dans cette facette d'un point du
      // batiment simplifié
      IDirectPosition pointCorres = cp.nearest2;

      // On complète le tableau de distances
      distance[i] = cp.distance;

      // on fait correspondre le sommet avec l'arrète du toit reliant le
      // bas du toit et le sommet du toit
      // de la face la plus proche, puisque que les points sont ordonnés
      // L'arrète est formé par ce point et le point suivant ou précédent
      // On prendra le point ayant le plus grand z

      // On récupère les points de la facette la plus proche du point )
      // traiter
      IDirectPositionList lPointTemp = faceTemp.coord();

      // On retrouve la place occupé par ce point dans le tableau de
      // points
      int pos = lPointTemp.getList().indexOf(pointCorres);

      // On regaarde quel point entre le suivant ou le précédent a le Z le
      // plus élevé
      int size = lPointTemp.size();
      int ind1, ind2;

      if (pos + 1 == size) {
        // Il faut changer a cause de la fermeture
        ind1 = pos - 1;
        ind2 = 1;

      } else if (pos == 0) {
        // Il faut mettre -2 et non -1 a cause de la fermeture
        ind1 = size - 2;
        ind2 = 1;

      } else {
        ind1 = pos + 1;
        ind2 = pos - 1;

      }

      IDirectPosition pInd1 = lPointTemp.get(ind1);
      IDirectPosition pInd2 = lPointTemp.get(ind2);

      // On créer un tableau de correspondance entre 1 sommet du batiment
      // simplifié
      // et le sommet de hauteur maximal sur le toit
      if (pInd1.getZ() > pInd2.getZ()) {
        lPointsCorresToits.add(pInd1);

      } else {

        lPointsCorresToits.add(pInd2);
      }

    }

    // Nous allons construire les toits à l'aide de 4 points :
    // Les points du sommet du batimente et les points correspondant

    IDirectPositionList lPointManque = new DirectPositionList();

    // On parcourt les points du cycle supérieur du batiment
    for (int i = 0; i < nbpointsatraiter; i = i + 2) {

      // On récupère les 2 points a traiter
      IDirectPosition pATraiter1 = lBuildingsSummits.get(i);
      IDirectPosition pATraiter2 = lBuildingsSummits.get(i + 1);

      // Ajout de la notion de seuil
      // Si ils sont vraiment trop éloigné du toit, on ne fabrique pas
      // cette portion de toit
      if (distance[i] > threshold) {

        continue;
      }

      if (distance[i + 1] > threshold) {

        continue;

      }

      // On créer alors le toit avec les 4 points
      DirectPositionList lPF = new DirectPositionList();

      lPF.add(pATraiter1);
      IDirectPosition pAjout1 = lPointsCorresToits.get(i);
      IDirectPosition pAjout2 = lPointsCorresToits.get(i + 1);

      lPF.add(pAjout1);

      lPF.add(pAjout2);
      lPF.add(pATraiter2);

      lPointManque.add(pAjout1);

      GM_LineString ls = new GM_LineString(lPF);

      GM_OrientableSurface f = new GM_Polygon(ls);

      toit.add(f);

    }

    // Résolution des problèmes

    // Problème 1 : un triangle avec 1 point sur le batiment
    // et 2 points dans le toit

    GM_LineString ls = new GM_LineString(lPointManque);

    GM_OrientableSurface f = new GM_Polygon(ls);

    toit.add(f);
    return toit;

  }

}
