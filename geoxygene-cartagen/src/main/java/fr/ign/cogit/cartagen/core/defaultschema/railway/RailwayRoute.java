/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.railway;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjLinDefault;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayRoute;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.ferre.LigneDeCheminDeFer;
import fr.ign.cogit.geoxygene.schemageo.impl.ferre.LigneDeCheminDeFerImpl;

/*
 * ###### IGN / CartAGen ###### Title: RailwayRoute Description: Lignes de
 * chemin de fer Author: J. Renard Date: 18/09/2009
 */

public class RailwayRoute extends GeneObjLinDefault implements IRailwayRoute {

  /**
   * Associated Geoxygene schema object
   */
  private LigneDeCheminDeFer geoxObj;

  /**
   * Constructor
   */
  public RailwayRoute(LigneDeCheminDeFer geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public RailwayRoute(ILineString line) {
    super();
    this.geoxObj = new LigneDeCheminDeFerImpl();
    this.geoxObj.setGeom(line);
    this.setInitialGeom(line);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
