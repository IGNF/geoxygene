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

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjLinDefault;
import fr.ign.cogit.cartagen.core.genericschema.land.ILandUseLine;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.occSol.ElementIsole;
import fr.ign.cogit.geoxygene.schemageo.impl.occSol.ElementIsoleImpl;

/*
 * ###### IGN / CartAGen ###### Title: LandUsePoint Description: Zones
 * d'occupation du sol simples Author: J. Renard Date: 18/09/2009
 */

public class LandUseLine extends GeneObjLinDefault implements ILandUseLine {

  /**
   * Associated Geoxygene schema object
   */
  private ElementIsole geoxObj;

  /**
   * Constructor
   */
  public LandUseLine(ElementIsole geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public LandUseLine(ILineString line) {
    super();
    this.geoxObj = new ElementIsoleImpl();
    this.geoxObj.setGeom(line);
    this.setInitialGeom(line);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
