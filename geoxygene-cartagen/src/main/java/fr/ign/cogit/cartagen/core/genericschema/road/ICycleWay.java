/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.road;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;

public interface ICycleWay extends INetworkSection {

  /**
   * The getter for the surface of the cycle way, e.g. "paved" or "asphalt".
   * @return
   */
  public String getSurface();

  public void setSurface(String surface);

  /**
   * The getter for the real or "ground" width of the cycle way, different from
   * the getWidth method inherited from INetworkSection that gives the symbol
   * width.
   * @return
   */
  public double getRealWidth();

  public void setRealWidth(double width);
}
