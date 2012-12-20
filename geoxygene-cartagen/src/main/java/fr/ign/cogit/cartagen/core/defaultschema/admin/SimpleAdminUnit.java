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

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjSurfDefault;
import fr.ign.cogit.cartagen.core.genericschema.admin.ISimpleAdminUnit;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.administratif.UniteAdministrativeElementaire;
import fr.ign.cogit.geoxygene.schemageo.impl.administratif.UniteAdministrativeElementaireImpl;

/*
 * ###### IGN / CartAGen ###### Title: SimpleAdminUnit Description: Unités
 * administratives élémentaires Author: J. Renard Date: 18/09/2009
 */

public class SimpleAdminUnit extends GeneObjSurfDefault implements
    ISimpleAdminUnit {

  /**
   * Associated Geoxygene schema object
   */
  private UniteAdministrativeElementaire geoxObj;

  /**
   * Constructor
   */
  public SimpleAdminUnit(UniteAdministrativeElementaire geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public SimpleAdminUnit(IPolygon poly) {
    super();
    this.geoxObj = new UniteAdministrativeElementaireImpl();
    this.geoxObj.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
