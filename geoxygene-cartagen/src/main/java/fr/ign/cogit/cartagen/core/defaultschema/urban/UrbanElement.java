/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.urban;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public abstract class UrbanElement extends GeneObjDefault implements
    IUrbanElement {

  /**
   * The Block the urban element is part of.
   */
  private IUrbanBlock block;

  @Override
  public IUrbanBlock getBlock() {
    return this.block;
  }

  @Override
  public void setBlock(IUrbanBlock block) {
    this.block = block;
  }

  @Override
  public IPolygon getSymbolGeom() {
    return (IPolygon) super.getSymbolGeom();
  }

}
