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
import fr.ign.cogit.cartagen.core.genericschema.urban.IEmptySpace;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.BatimentImpl;

public class EmptySpace extends GeneObjSurfDefault implements IEmptySpace {

  private DefaultFeature geoxObj;
  private IUrbanBlock block;

  public EmptySpace(IPolygon poly) {
    super();
    this.geoxObj = new BatimentImpl(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  public IUrbanBlock getBlock() {
    return this.block;
  }

  public void setBlock(IUrbanBlock block) {
    this.block = block;
  }

}
