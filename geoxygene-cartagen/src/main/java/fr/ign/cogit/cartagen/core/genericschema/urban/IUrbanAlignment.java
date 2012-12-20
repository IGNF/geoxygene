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

import java.util.List;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

/*
 * ###### IGN / CartAGen ###### Title: BuildingAlignment Description:
 * Alignements de batiments Author: J. Renard Date: 16/09/2009
 */

public interface IUrbanAlignment extends IGeneObjSurf {

  public ILineString getShapeLine();

  public void setShapeLine(ILineString shapeLine);

  public ILineString getInitialShapeLine();

  public void setInitialShapeLine(ILineString shapeLine);

  public IUrbanElement getInitialElement();

  public void setInitialElement(IUrbanElement initialElement);

  public IUrbanElement getFinalElement();

  public void setFinalElement(IUrbanElement finalElement);

  /**
   * Gets the urban elements composing the alignment
   * @return
   */
  public List<IUrbanElement> getUrbanElements();

  public void setUrbanElements(List<IUrbanElement> urbanElements);

  /**
   * determines the initial and final urban elements of the alignment
   */
  public void computeInitialAndFinalElements();

  /**
   * computes the shape line of the alignment based on its buildings
   */
  public void computeShapeLine();

  /**
   * completely destroys an alignment, deleting its inner buildings
   */
  public void destroy();

  public static final String FEAT_TYPE_NAME = "UrbanAlignment";
}
