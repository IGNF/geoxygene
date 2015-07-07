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

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjPoint;

/*
 * ###### IGN / CartAGen ###### Title: LabelPoint Description: Points
 * représentatifs d'activité et d'intérêt Author: J. Renard Date: 16/09/2009
 */

public interface ILabelPoint extends IGeneObjPoint {
  public static final String FEAT_TYPE_NAME = "LabelPoint"; //$NON-NLS-1$

  /**
   * The name of the label
   * @return
   */
  public String getName();

  /**
   * The importance of the label from 1 (maximum importance) to 8 (minimum
   * importance)
   * @return
   */
  public int getImportance();

  /**
   * The nature of the label, e.g. "city"
   * @return
   */
  public String getNature();

  /**
   * The category of the label.
   * @return
   */
  public LabelCategory getLabelCategory();

  public enum LabelCategory {
    LIVING_PLACE, PLACE, COMMUNICATION, WATER, RELIEF, OTHER
  }
}
