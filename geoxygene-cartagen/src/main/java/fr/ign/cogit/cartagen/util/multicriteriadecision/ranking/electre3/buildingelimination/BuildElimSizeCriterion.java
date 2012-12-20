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

import fr.ign.cogit.cartagen.util.multicriteriadecision.ranking.electre3.ELECTREIIICriterion;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public class BuildElimSizeCriterion extends ELECTREIIICriterion {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  public static final String PARAM_GEOM = "geometry";
  public static final String PARAM_SIM_AREA = "simulatedArea";
  public static final String PARAM_AREA_THRESH = "sizeThreshold";

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
  public BuildElimSizeCriterion(String name) {
    super(name);
    this.setWeight(1.5);
    this.setIndifference(0.1);
    this.setPreference(0.2);
    this.setVeto(0.3);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IPolygon geom = (IPolygon) param.get(PARAM_GEOM);
    double simArea = (Double) param.get(PARAM_SIM_AREA);
    double sizeThreshold = (Double) param.get(PARAM_AREA_THRESH);
    double area = geom.area();
    if (Math.abs(simArea - area) > 15.0)
      area = simArea;
    if (area <= sizeThreshold)
      return 1.0;
    if (area <= 2 * sizeThreshold) {
      double a = -0.5 * 1.0 / sizeThreshold;
      double b = (2.0 * sizeThreshold - 1.0) / (2.0 * sizeThreshold);
      return a * area + b;
    }
    // a segment with a softer slope
    double a = -1.0 / (4.0 * sizeThreshold);
    double b = 1.0;
    return a * area + b;
  }

}
