package fr.ign.cogit.geoxygene.sig3d.calculation;

import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

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
 *
 * Permet de calculer certains indicateur
 * 
 * Class wich calculte some indicators
 */
public class Indicator {

  /**
   * Indicateur convexité : Rapport du volume du corps sur le volume de
   * l'enveloppe convexe
   * 
   * @param solid l'objet dont on calcul l'indicateur
   * @return la valeur du rapport du volume du corps sur le volume de
   *         l'enveloppe convexe
   */
  public static double convexityIndicator(GM_Solid solid) {

    GM_Solid s = Calculation3D.convexHull(solid);

    double v1 = Calculation3D.volume(s);
    double v2 = Calculation3D.volume(solid);

    return v2 / v1;
  }

  /**
   * Rapport de la surface du corps sur la surface du corps convexe
   * 
   * @param solid l'objet dont on calcul l'indicateur
   * @return Rapport de la surface du corps sur la surface du corps convexe
   */
  public static double surfacicConvexIndicator(GM_Solid solid) {

    GM_Solid s = Calculation3D.convexHull(solid);

    double a1 = Calculation3D.area(s);
    double a2 = Calculation3D.area(solid);

    return a2 / a1;
  }

}
