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

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementArea;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.relief.ElementCaracteristiqueDuRelief;
import fr.ign.cogit.geoxygene.schemageo.impl.relief.ElementCaracteristiqueDuReliefImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.ChampContinuImpl;

/*
 * ###### IGN / CartAGen ###### Title: ReliefElementArea Description: Eléments
 * surfaciques caractéristiques du relief Author: J. Renard Date: 18/09/2009
 */

public class ReliefElementArea extends GeneObjSurfDefault implements
    IReliefElementArea {

  /**
   * Associated Geoxygene schema object
   */
  private ElementCaracteristiqueDuRelief geoxObj;

  /**
   * Constructor
   */
  public ReliefElementArea(ElementCaracteristiqueDuRelief geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public ReliefElementArea(IPolygon poly) {
    super();
    this.geoxObj = new ElementCaracteristiqueDuReliefImpl(
        new ChampContinuImpl(), poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
