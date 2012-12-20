/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.urban;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;

/*
 * ###### IGN / CartAGen ###### Title: Building Description: Batiments Author:
 * J. Renard Date: 16/09/2009
 */

public interface IBuilding extends IUrbanElement, IGeneObjSurf {
  public static final String FEAT_TYPE_NAME = "Building"; //$NON-NLS-1$

  /**
   * The nature of a building (e.g. housing, industrial...)
   * @return
   */
  public String getNature();

  public void setNature(String nature);
}
