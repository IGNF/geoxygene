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

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/*
 * ###### IGN / CartAGen ###### Title: UrbanElement Description: elements
 * urbains simples Author: J. Renard Date: 16/09/2009
 */

public interface IUrbanElement extends IGeneObj {

  /**
   * Get the block the urban element is part of.
   * 
   * @return
   * @author GTouya
   */
  public IUrbanBlock getBlock();

  public void setBlock(IUrbanBlock block);

  @Override
  public IPolygon getSymbolGeom();

  public static final String FEAT_TYPE_NAME = "UrbanElement"; //$NON-NLS-1$
}
