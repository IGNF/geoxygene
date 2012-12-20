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

import java.util.Map;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.spatialanalysis.measures.congestion.CongestionComputation;
import fr.ign.cogit.cartagen.util.multicriteriadecision.ranking.electre3.ELECTREIIICriterion;

public class BuildElimCongestionCriterion extends ELECTREIIICriterion {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  public static final String PARAM_BUILDING = "building";
  public static final String PARAM_DIST_MAX = "distanceMax";

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
  public BuildElimCongestionCriterion(String name) {
    super(name);
    this.setWeight(1.5);
    this.setIndifference(0.2);
    this.setPreference(0.3);
    this.setVeto(0.1);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IBuilding building = (IBuilding) param
        .get(BuildElimCongestionCriterion.PARAM_BUILDING);
    double distMax = (Double) param
        .get(BuildElimCongestionCriterion.PARAM_DIST_MAX);
    CongestionComputation congestion = new CongestionComputation();
    congestion.calculEncombrement(building, distMax);
    int nb = congestion.getEncombrements().length;
    double cong = 0.0;
    for (int i = 0; i < nb; i++) {
      cong += congestion.getEncombrements()[i];
    }
    cong /= nb;

    return cong;
  }

}
