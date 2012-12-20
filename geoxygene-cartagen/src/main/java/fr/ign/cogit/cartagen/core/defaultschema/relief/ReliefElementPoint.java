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
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementPoint;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.relief.ElementCaracteristiqueDuRelief;
import fr.ign.cogit.geoxygene.schemageo.impl.relief.ElementCaracteristiqueDuReliefImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.ChampContinuImpl;

/*
 * ###### IGN / CartAGen ###### Title: ReliefElementPoint Description: Eléments
 * ponctuels caractéristiques du relief Author: J. Renard Date: 18/09/2009
 */

public class ReliefElementPoint extends GeneObjPointDefault implements
    IReliefElementPoint {

  /**
   * Associated Geoxygene schema object
   */
  private ElementCaracteristiqueDuRelief geoxObj;

  /**
   * Constructor
   */
  public ReliefElementPoint(ElementCaracteristiqueDuRelief geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public ReliefElementPoint(IPoint point) {
    super();
    this.geoxObj = new ElementCaracteristiqueDuReliefImpl(
        new ChampContinuImpl(), point);
    this.setInitialGeom(point);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
