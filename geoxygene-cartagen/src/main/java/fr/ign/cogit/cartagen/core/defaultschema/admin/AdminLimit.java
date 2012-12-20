/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.admin;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjLinDefault;
import fr.ign.cogit.cartagen.core.genericschema.admin.IAdminLimit;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.administratif.LimiteAdministrative;
import fr.ign.cogit.geoxygene.schemageo.impl.administratif.LimiteAdministrativeImpl;

/*
 * ###### IGN / CartAGen ###### Title: AdminLimit Description: Limites
 * administratives Author: J. Renard Date: 18/09/2009
 */

public class AdminLimit extends GeneObjLinDefault implements IAdminLimit {

  /**
   * Associated Geoxygene schema object
   */
  private LimiteAdministrative geoxObj;

  /**
   * Constructor
   */
  public AdminLimit(LimiteAdministrative geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public AdminLimit(ILineString line) {
    super();
    this.geoxObj = new LimiteAdministrativeImpl();
    this.geoxObj.setGeom(line);
    this.setInitialGeom(line);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
