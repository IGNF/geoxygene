/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.railway;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;

/*
 * ###### IGN / CartAGen ###### Title: RailwayLine Description: Tronçons ferrés
 * Author: J. Renard Date: 16/09/2009
 */

public interface IRailwayLine extends INetworkSection {
  public static final String FEAT_TYPE_NAME = "RailwayLine"; //$NON-NLS-1$

  public void setSidetrack(boolean sidetrack);

  public boolean isSideTrack();

}
