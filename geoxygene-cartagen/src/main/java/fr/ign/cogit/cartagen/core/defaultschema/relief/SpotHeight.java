/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.relief;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjPointDefault;
import fr.ign.cogit.cartagen.core.genericschema.relief.ISpotHeight;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.support.champContinu.PointCote;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.ChampContinuImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.PointCoteImpl;

/*
 * ###### IGN / CartAGen ###### Title: ReliefElementPoint Description: Points
 * cotés Author: J. Renard Date: 30/06/2010
 */

public class SpotHeight extends GeneObjPointDefault implements ISpotHeight {

  /**
   * Associated Geoxygene schema object
   */
  private PointCote geoxObj;

  /**
   * Constructor
   */
  public SpotHeight(PointCote geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public SpotHeight(IPoint point, double value) {
    super();
    this.geoxObj = new PointCoteImpl(new ChampContinuImpl(), value, point);
    this.setInitialGeom(point);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

  @Override
  public double getZ() {
    return this.geoxObj.getValeur();
  }

  @Override
  public void setZ(double z) {
    this.geoxObj.setValeur(z);
  }

}
