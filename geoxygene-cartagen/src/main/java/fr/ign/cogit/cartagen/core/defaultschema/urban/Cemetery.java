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

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.genericschema.urban.ICemetery;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class Cemetery extends GeneObjSurfDefault implements ICemetery {

  /**
   * The Block the urban element is part of.
   */
  private IUrbanBlock block;
  private CemeteryType type = CemeteryType.UNKNOWN;

  public Cemetery(IPolygon geom, CemeteryType type) {
    super();
    this.setGeom(geom);
    this.setInitialGeom(geom);
    this.setType(type);
  }

  @Override
  public IUrbanBlock getBlock() {
    return block;
  }

  @Override
  public void setBlock(IUrbanBlock block) {
    this.block = block;
  }

  @Override
  public CemeteryType getType() {
    return type;
  }

  public void setType(CemeteryType type) {
    this.type = type;
  }

  @Override
  public String getTypeSymbol() {
    return type.name();
  }

  @Override
  public IPolygon getSymbolGeom() {
    return getGeom();
  }

}
