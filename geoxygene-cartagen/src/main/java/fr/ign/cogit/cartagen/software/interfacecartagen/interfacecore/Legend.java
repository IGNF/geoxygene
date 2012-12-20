/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
/**
 * @author julien Gaffuri 29 janv. 2009
 */
package fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore;

/**
 * la legende: defini la symbolisation des objets
 * @author julien Gaffuri 29 janv. 2009
 */
public class Legend {

  // echelle de symbolisation
  private static double SYMBOLISATI0N_SCALE = 15000.0;

  /**
   * @return
   */
  public static double getSYMBOLISATI0N_SCALE() {
    return Legend.SYMBOLISATI0N_SCALE;
  }

  /**
   * @param echelle_cible
   */
  public static void setSYMBOLISATI0N_SCALE(double echelle_cible) {
    Legend.SYMBOLISATI0N_SCALE = echelle_cible;
  }

}
