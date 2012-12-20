/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.road;

import java.util.Set;

/*
 * ###### IGN / CartAGen ###### Title: RoundAbout Description: Ronds-points
 * Author: J. Renard Date: 16/09/2009
 */

public interface IRoundAbout extends IComplexCrossRoad {

  public Set<IBranchingCrossroad> getBranchings();

  public double getDiameter();

  public static final String FEAT_TYPE_NAME = "RoundAbout"; //$NON-NLS-1$
}
