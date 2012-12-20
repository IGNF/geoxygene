/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.relief;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjPoint;

/*
 * ###### IGN / CartAGen ###### Title: reliefElementPoint Description: Points
 * cotés Author: J. Renard Date: 30/06/2010
 */

public interface ISpotHeight extends IGeneObjPoint {

  /**
   * gets the altitude of the spot height
   * @return
   */
  public double getZ();

  /**
   * sets an altitude to the spot height
   * @return
   */
  public void setZ(double z);

  public static final String FEAT_TYPE_NAME = "SpotHeight"; //$NON-NLS-1$
}
