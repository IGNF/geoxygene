package fr.ign.cogit.geoxygene.sig3d.util;

import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;

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
 * Contient des constante vectorielle pour éviter d'avoir à les redéfinir
 * 
 * Useful constant to avoid to redefine them
 */
public class MathConstant {
  /**
   * Vector 1,0,0
   */
  public static final Vecteur vectX = new Vecteur(1, 0, 0);

  /**
   * Vector 0,1,0
   */
  public static final Vecteur vectY = new Vecteur(0, 1, 0);

  /**
   * Vector 0,0,1
   */
  public static final Vecteur vectZ = new Vecteur(0, 0, 1);

}
