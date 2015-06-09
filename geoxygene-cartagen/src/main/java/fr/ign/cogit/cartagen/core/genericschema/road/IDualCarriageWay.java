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

import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;

/*
 * ###### IGN / CartAGen ###### Title: DualCarriageWay Description: Voie à
 * chaussée séparée Author: J. Renard Date: 16/09/2009
 */

public interface IDualCarriageWay extends IGeneObjSurf {
  public static final String FEAT_TYPE_NAME = "DualCarriageWay"; //$NON-NLS-1$

  public int getImportance();

  public void setImportance(int importance);

  /**
   * The roads that form the dual carriageways, that surround the separator.
   * @return
   */
  public Set<IRoadLine> getInnerRoads();

  /**
   * Get the roads that are connected to the inner roads but that are not part
   * of the dual carriageways.
   * @return
   */
  public Set<IRoadLine> getOuterRoads();
}
