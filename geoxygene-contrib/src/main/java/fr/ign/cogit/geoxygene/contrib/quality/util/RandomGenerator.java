package fr.ign.cogit.geoxygene.contrib.quality.util;

import java.util.Random;

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
 * A class to generate random numbers.
 * @author JFGirres
 * 
 */
public class RandomGenerator {

  /**
   * retourne un nombre aléatoire selon une loi gaussienne
   * @return
   */
  public static double genereNumLoiNormale() {
    Random generator;
    generator = new Random();
    double num = generator.nextGaussian();
    return num;
  }

  /** retourne aléatoirement un numéro */
  public static double genereNum() {
    double num = Math.random();
    return num;
  }

  /** retourne aléatoirement un angle de 0 à 90° */
  public static double genereAngle() {
    double angle = Math.random() * 90;
    return angle;
  }

  /** retourne aléatoirement +1 ou -1 */
  public static double genereSigne() {
    if (Math.random() <= 0.5)
      return -1;
    return 1;
  }

}
