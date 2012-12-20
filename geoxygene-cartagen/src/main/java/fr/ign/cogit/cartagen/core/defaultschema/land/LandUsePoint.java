/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.land;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjPointDefault;
import fr.ign.cogit.cartagen.core.genericschema.land.ILandUsePoint;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.occSol.ElementIsole;
import fr.ign.cogit.geoxygene.schemageo.impl.occSol.ElementIsoleImpl;

/*
 * ###### IGN / CartAGen ###### Title: LandUsePoint Description: Zones
 * d'occupation du sol simples Author: J. Renard Date: 18/09/2009
 */

public class LandUsePoint extends GeneObjPointDefault implements ILandUsePoint {

  /**
   * Associated Geoxygene schema object
   */
  private ElementIsole geoxObj;

  /**
   * Constructor
   */
  public LandUsePoint(ElementIsole geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public LandUsePoint(IPoint point) {
    super();
    this.geoxObj = new ElementIsoleImpl();
    this.geoxObj.setGeom(point);
    this.setInitialGeom(point);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
