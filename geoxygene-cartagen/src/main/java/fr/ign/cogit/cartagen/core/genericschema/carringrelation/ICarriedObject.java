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

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public interface ICarriedObject extends IGeneObj {

  public int getRoadRelativePosition();

  public void setRoadRelativePosition(int k);

  public ICarrierObject getCarrierObject();

  public void setCarrierObject(ICarrierObject carrierObject);

  public boolean isCarried();

  public IGeometry getCarrierGeom();

}
