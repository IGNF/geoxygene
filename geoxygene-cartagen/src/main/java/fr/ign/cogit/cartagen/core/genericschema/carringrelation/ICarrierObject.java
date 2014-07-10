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

  void addCarriedObject(ICarriedObject route);

  Collection<ICarriedObject> getCarriedObjects();

}
