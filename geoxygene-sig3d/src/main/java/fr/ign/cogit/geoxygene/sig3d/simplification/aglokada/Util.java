package fr.ign.cogit.geoxygene.sig3d.simplification.aglokada;

import java.util.ArrayList;

import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

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
 * @author Aurélien Velten
 *  
 * @version 0.1
 * 
 * Classes utilitaires pour l'exécution de l'algorithme
 * 
 */
public class Util {

  /**
   * Elimine les segments d'une liste dont la taille est inférieure à un seuil
   * 
   * @param lLS
   * @param threshold
   * @return renvoie une liste nettoyé des segments dont la longueur est
   *         inférieur à threshold
   */
  public static ArrayList<GM_LineString> elimination(
      ArrayList<GM_LineString> lLS, double threshold) {
    int nb = lLS.size();

    ArrayList<GM_LineString> lResultat = new ArrayList<GM_LineString>(nb);

    for (int i = 0; i < nb; i++) {
      GM_LineString ls = lLS.get(i);

      if (ls.length() > threshold) {

        lResultat.add(ls);

      }

    }

    return lResultat;

  }

  /**
   * Projete le point M sur la droite (AB)
   * 
   * @param M
   * @param A
   * @param B
   * @return la projection de M sur (AB)
   */
  public static DirectPosition casting(DirectPosition M, DirectPosition A,
      DirectPosition B) {
    double lambda;
    Vecteur uAB, AM;

    if (A.distance(B) == 0) {
      return A; // cas ou A et B sont confondus
    }

    uAB = new Vecteur(A, B).vectNorme();
    AM = new Vecteur(A, M);
    lambda = AM.prodScalaire(uAB);

    DirectPosition dpFinal = new DirectPosition(A.getCoordinate());

    dpFinal.move(uAB.getX() * lambda, uAB.getY() * lambda, uAB.getZ() * lambda);

    return dpFinal;

  }

  /**
   * Indique si 2 lineString(composées d'un seul élément) sont quasiement
   * colinéaires
   * 
   * @param gls1
   * @param gls2
   * @return boolean indiquant si les premiers segments des 2 linestring sont
   *         colinéairs
   */
  public static boolean isColinear(GM_LineString gls1, GM_LineString gls2) {
    Vecteur v1 = new Vecteur(gls1.coord().get(1).getX()
        - gls1.coord().get(0).getX(), gls1.coord().get(1).getY()
        - gls1.coord().get(0).getY(), 0);

    Vecteur v2 = new Vecteur(gls2.coord().get(1).getX()
        - gls2.coord().get(0).getX(), gls2.coord().get(1).getY()
        - gls2.coord().get(0).getY(), 0);

    v1.normalise();
    v2.normalise();

    double scal = Math.abs(v1.prodScalaire(v2));

    if (scal > 0.85) {

      return true;
    }
    return false;

  }

}
