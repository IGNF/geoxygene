/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.land;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;

/*
 * ###### IGN / CartAGen ###### Title: SimpleLandUseArea Description: Zones
 * d'occupation du sol simples Author: J. Renard Date: 16/09/2009
 */

public interface ISimpleLandUseArea extends IGeneObjSurf {

  /**
   * the type of land use
   */
  public int getType();

  public void setType(int type);

  public static final String FEAT_TYPE_NAME = "SimpleLandUseArea"; //$NON-NLS-1$
}
