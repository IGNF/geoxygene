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
 * ###### IGN / CartAGen ###### Title: BranchingCrossroad Description:
 * Carrefours complexes Author: J. Renard Date: 16/09/2009
 */

public interface IBranchingCrossroad extends IComplexCrossRoad {

  public Set<IRoadLine> getMainRoadIntern();

  public void setMainRoadIntern(Set<IRoadLine> mainRoadIntern);

  public IRoadLine getMinorRoadExtern();

  public void setMinorRoadExtern(IRoadLine minorRoadExtern);

  public IRoundAbout getRoundAbout();

  public void setRoundAbout(IRoundAbout roundAbout);

  public static final String FEAT_TYPE_NAME = "BranchingCrossroad"; //$NON-NLS-1$
}
