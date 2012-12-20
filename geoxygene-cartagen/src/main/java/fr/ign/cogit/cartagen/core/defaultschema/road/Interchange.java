/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.road;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.genericschema.road.IInterchange;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.routier.CarrefourComplexe;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.CarrefourComplexeImpl;

/*
 * ###### IGN / CartAGen ###### Title: Interchange Description: Echangeurs
 * Author: J. Renard Date: 21/10/2009
 */

public class Interchange extends GeneObjSurfDefault implements IInterchange {

  /**
   * Associated Geoxygene schema object
   */
  private CarrefourComplexe geoxObj;

  /**
   * Constructor
   */
  public Interchange(CarrefourComplexe geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public Interchange(IPolygon poly) {
    super();
    this.geoxObj = new CarrefourComplexeImpl();
    this.geoxObj.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
