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

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjLinDefault;
import fr.ign.cogit.cartagen.core.genericschema.relief.IReliefElementLine;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.relief.ElementCaracteristiqueDuRelief;
import fr.ign.cogit.geoxygene.schemageo.impl.relief.ElementCaracteristiqueDuReliefImpl;
import fr.ign.cogit.geoxygene.schemageo.impl.support.champContinu.ChampContinuImpl;

/*
 * ###### IGN / CartAGen ###### Title: ReliefElementLine Description: Eléments
 * linéaires caractéristiques du relief Author: J. Renard Date: 18/09/2009
 */

public class ReliefElementLine extends GeneObjLinDefault implements
    IReliefElementLine {

  /**
   * Associated Geoxygene schema object
   */
  private ElementCaracteristiqueDuRelief geoxObj;

  /**
   * Constructor
   */
  public ReliefElementLine(ElementCaracteristiqueDuRelief geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public ReliefElementLine(ILineString line) {
    super();
    this.geoxObj = new ElementCaracteristiqueDuReliefImpl(
        new ChampContinuImpl(), line);
    this.setInitialGeom(line);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
