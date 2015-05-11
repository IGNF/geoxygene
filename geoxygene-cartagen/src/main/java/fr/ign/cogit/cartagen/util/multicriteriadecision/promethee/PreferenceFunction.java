/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util.multicriteriadecision.promethee;

/**
 * Interface for Promethee preference functions, functions that give a
 * preference value between 0 and 1 for a given double deviation between two
 * candidates.
 * @author GTouya
 * 
 */
public interface PreferenceFunction {

  /**
   * gives the preference value (between 0 and 1) for a given deviation value.
   * @param deviation
   * @return
   */
  public double value(double deviation);
}
