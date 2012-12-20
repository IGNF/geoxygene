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
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildArea;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.bati.AutreConstruction;
import fr.ign.cogit.geoxygene.schemageo.impl.bati.AutreConstructionImpl;

/*
 * ###### IGN / CartAGen ###### Title: BuildArea Description: Autres
 * constructions surfaciques Author: J. Renard Date: 18/09/2009
 */

public class BuildArea extends GeneObjSurfDefault implements IBuildArea {

  /**
   * Associated Geoxygene schema object
   */
  private AutreConstruction geoxObj;

  /**
   * Constructor
   */
  public BuildArea(AutreConstruction geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public BuildArea(IPolygon poly) {
    super();
    this.geoxObj = new AutreConstructionImpl(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
