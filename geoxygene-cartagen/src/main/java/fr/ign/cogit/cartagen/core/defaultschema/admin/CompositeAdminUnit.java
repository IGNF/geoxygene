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
import fr.ign.cogit.cartagen.core.genericschema.admin.ICompositeAdminUnit;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.administratif.UniteAdministrativeComposite;
import fr.ign.cogit.geoxygene.schemageo.impl.administratif.UniteAdministrativeCompositeImpl;

/*
 * ###### IGN / CartAGen ###### Title: CompositAdminUnit Description: Unités
 * administratives composites Author: J. Renard Date: 18/09/2009
 */

public class CompositeAdminUnit extends GeneObjSurfDefault implements
    ICompositeAdminUnit {

  /**
   * Associated Geoxygene schema object
   */
  private UniteAdministrativeComposite geoxObj;

  /**
   * Constructor
   */
  public CompositeAdminUnit(UniteAdministrativeComposite geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public CompositeAdminUnit(IPolygon poly) {
    super();
    this.geoxObj = new UniteAdministrativeCompositeImpl();
    this.geoxObj.setGeom(poly);
    this.setInitialGeom(poly);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
