/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.hydro;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;

/*
 * ###### IGN / CartAGen ###### Title: WaterLine Description: Tronçons
 * hydrographiques Author: J. Renard Date: 16/09/2009
 */

public interface IWaterLine extends INetworkSection {
  public static final String FEAT_TYPE_NAME = "WaterLine"; //$NON-NLS-1$
}
