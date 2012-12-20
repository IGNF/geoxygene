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

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjLinDefault;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadRoute;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.routier.RouteItineraire;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.RouteItineraireImpl;

/*
 * ###### IGN / CartAGen ###### Title: RoadRoute Description: Itinéraires
 * routiers Author: J. Renard Date: 18/09/2009
 */

public class RoadRoute extends GeneObjLinDefault implements IRoadRoute {

  /**
   * Associated Geoxygene schema object
   */
  private RouteItineraire geoxObj;

  /**
   * Constructor
   */
  public RoadRoute(RouteItineraire geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public RoadRoute(ILineString line) {
    super();
    this.geoxObj = new RouteItineraireImpl();
    this.geoxObj.setGeom(line);
    this.setInitialGeom(line);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
