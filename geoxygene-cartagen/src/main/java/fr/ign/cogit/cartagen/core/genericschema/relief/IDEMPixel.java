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
 * ###### IGN / CartAGen ###### Title: DEMPIxel Description: Pixels MNT cotés
 * Author: J. Renard Date: 30/06/2010
 */

public interface IDEMPixel extends IGeneObjPoint {

  /**
   * gets the coordinates of the pixel
   * @return
   */
  public double getX();

  public double getY();

  /**
   * sets coordinates to the pixel
   * @return
   */
  public void setX(double x);

  public void setY(double y);

  public void setCoordinates(double x, double y);

  /**
   * gets the altitude of the pixel
   * @return
   */
  public double getZ();

  /**
   * sets an altitude to the pixel
   * @return
   */
  public void setZ(double z);

  public static final String FEAT_TYPE_NAME = "DEMPixel"; //$NON-NLS-1$
}
