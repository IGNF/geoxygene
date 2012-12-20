/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.misc;

import java.awt.Image;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjPoint;

public interface IPointOfInterest extends IGeneObjPoint {
  public static final String FEAT_TYPE_NAME = "PointOfInterest"; //$NON-NLS-1$

  public String getName();

  public String getNature();

  public Image getSymbol();
}
