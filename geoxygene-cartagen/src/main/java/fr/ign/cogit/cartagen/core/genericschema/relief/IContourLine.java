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

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjLin;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/*
 * ###### IGN / CartAGen ###### Title: ContourLine Description: Courbes de
 * niveau Author: J. Renard Date: 16/09/2009
 */

public interface IContourLine extends IGeneObjLin {

  /**
   * returns the altitude value of the contour line
   * @return
   */
  public double getAltitude();

  /**
   * sets an altitude value to the contour line
   * @param z
   */
  public void setAltitude(double z);

  /**
   * Master contour line or not
   * @return
   */
  public boolean isMaster();

  /**
   * Width of the line on the map, in mm
   * @return
   */
  public double getWidth();

  /**
   * Symbol extent on the field dependind on the map scale, in meters
   * @return
   */
  public IPolygon getSymbolExtent();

  public static final String FEAT_TYPE_NAME = "ContourLine"; //$NON-NLS-1$
}
