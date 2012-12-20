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

import java.awt.Color;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;

/*
 * ###### IGN / CartAGen ###### Title: RoadLine Description: Tronçons de route
 * Author: J. Renard Date: 16/09/2009
 */

public interface IRoadLine extends INetworkSection {

  @Override
  public int getImportance();

  @Override
  public void setImportance(int importance);

  public static final String FEAT_TYPE_NAME = "RoadLine"; //$NON-NLS-1$

  public Color getSeparatorColor();

  public Color getFrontColor();
}
