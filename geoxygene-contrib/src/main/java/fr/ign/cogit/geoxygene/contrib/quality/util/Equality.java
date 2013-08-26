package fr.ign.cogit.geoxygene.contrib.quality.util;

/**
 *
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL-C_fr.html
 *        see Licence_CeCILL-C_en.html
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * A class to compute equality with decimal tolerance
 * @author JFGirres
 * 
 */
public class Equality {
  /**
   * Equality computation with decimal tolerance
   * @param d1
   * @param d2
   * @return
   */
  public static boolean equals(double d1, double d2) {
    double diff = d1 - d2;
    return Math.abs(diff) <= 0.001;
  }
}
