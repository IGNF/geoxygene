/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.defaultschema.hydro;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjLinDefault;
import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterCourse;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.CoursDEau;
import fr.ign.cogit.geoxygene.schemageo.impl.hydro.CoursDEauImpl;

/*
 * ###### IGN / CartAGen ###### Title: WaterCourse Description: Cours d'eau
 * Author: J. Renard Date: 18/09/2009
 */

public class WaterCourse extends GeneObjLinDefault implements IWaterCourse {

  /**
   * Associated Geoxygene schema object
   */
  private CoursDEau geoxObj;

  /**
   * Constructor
   */
  public WaterCourse(CoursDEau geoxObj) {
    super();
    this.geoxObj = geoxObj;
    this.setInitialGeom(geoxObj.getGeom());
    this.setEliminated(false);
  }

  public WaterCourse(ILineString line) {
    super();
    this.geoxObj = new CoursDEauImpl();
    this.geoxObj.setGeom(line);
    this.setInitialGeom(line);
    this.setEliminated(false);
  }

  @Override
  public IFeature getGeoxObj() {
    return this.geoxObj;
  }

}
