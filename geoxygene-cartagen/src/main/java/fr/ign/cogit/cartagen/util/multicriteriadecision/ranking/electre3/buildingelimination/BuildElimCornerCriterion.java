/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util.multicriteriadecision.ranking.electre3.buildingelimination;

import java.util.HashSet;
import java.util.Map;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.util.multicriteriadecision.ranking.electre3.ELECTREIIICriterion;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Batiment;

public class BuildElimCornerCriterion extends ELECTREIIICriterion {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  public static final String PARAM_BUILDING = "building";
  public static final String PARAM_CORNER_BUILDINGS = "cornerBuildings";

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public BuildElimCornerCriterion(String name) {
    super(name);
    this.setWeight(0.8);
    this.setIndifference(0.2);
    this.setPreference(0.4);
    this.setVeto(0.5);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  @SuppressWarnings("unchecked")
  public double value(Map<String, Object> param) {
    IBuilding building = (IBuilding) param.get(PARAM_BUILDING);
    HashSet<Batiment> cornerBuilds = (HashSet<Batiment>) param
        .get(PARAM_CORNER_BUILDINGS);
    if (cornerBuilds.contains(building.getGeoxObj()))
      return 1.0;
    return 0.0;
  }

}
