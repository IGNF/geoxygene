/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.energy;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;

/*
 * ###### IGN / CartAGen ###### Title: ElectricityLine Description: Lignes
 * électriques Author: J. Renard Date: 30/09/2010
 */

public interface IElectricityLine extends INetworkSection {
  public static final String FEAT_TYPE_NAME = "ElectricityLine"; //$NON-NLS-1$
}
