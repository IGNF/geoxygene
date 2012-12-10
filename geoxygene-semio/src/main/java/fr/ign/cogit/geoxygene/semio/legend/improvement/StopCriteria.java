package fr.ign.cogit.geoxygene.semio.legend.improvement;

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
 * @author Charlotte Hoarau - IGN / Laboratoire COGIT
 * 
 */
interface StopCriteria {

  /**
   * Returns <tt>true</tt> if the Stop Criteria had been reached.
   * @return <tt>true</tt> if the Stop Criteria had been reached.
   */
  boolean isChecked();

  /**
   * Initialize the Stop Criteria
   */
  void initialize();
}
