/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/

package fr.ign.cogit.cartagen.core.genericschema.carringrelation;

import java.util.Collection;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;

public interface ICarrierObject extends IGeneObj {

  /**
   * Add a new object to be carried on an arbitrary free position.
   * @param route
   */
  void addCarriedObject(ICarriedObject route);

  /**
   * Get all currently carried object
   * @return
   */
  Collection<ICarriedObject> getCarriedObjects();

  /**
   * Get the current width from left (if left == true) or right (if left ==
   * false).
   * @param left
   * @return
   */
  double distance(boolean left);

  /**
   * Get the maximum width the object can take if all carried objects are on the
   * same side
   * @return
   */
  double maxWidth();

}
