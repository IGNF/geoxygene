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

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.genericschema.land.ICompositeLandUseArea;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.occSol.ZoneOccSol;
import fr.ign.cogit.geoxygene.schemageo.impl.occSol.ZoneOccSolImpl;

/*
 * ###### IGN / CartAGen ###### Title: CompositLandUseArea Description: Zones
 * d'occupation du sol composites Author: J. Renard Date: 18/09/2009
 */

public class CompositeLandUseArea extends GeneObjSurfDefault implements
    ICompositeLandUseArea {

  /**
   * Associated Geoxygene schema object
   */
  private ZoneOccSol geoxObj;

  /**
   * Constructor
   */
  public CompositeLandUseArea(ZoneOccSol geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public CompositeLandUseArea(IPolygon poly) {
    super();
    this.geoxObj = new ZoneOccSolImpl();
    this.geoxObj.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
