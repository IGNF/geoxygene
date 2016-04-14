/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.towncentre;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.ELECTRECriterion;

public class BuildingSizeCriterion extends ELECTRECriterion {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static Logger logger = Logger.getLogger(ELECTRECriterion.class
      .getName());

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
  public BuildingSizeCriterion(String nom) {
    super(nom);
    this.setWeight(1.5);
    this.setIndifference(0.05);
    this.setPreference(0.15);
    this.setVeto(0.5);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IUrbanBlock block = (IUrbanBlock) param.get("block");
    BuildingSizeCriterion.logger.finest("block: " + block.getId());
    double meanArea = ((Double) param.get("meanBuildingArea")).doubleValue();
    DescriptiveStatistics buildingAreas = new DescriptiveStatistics();
    if (meanArea == 0.0 || meanArea == Double.NaN)
      return 0.0;
    double maxArea = 3.0 * meanArea;
    for (IUrbanElement e : block.getUrbanElements()) {
      buildingAreas.addValue(e.getGeom().area());
    }

    double area = 0.0;
    if (block.getUrbanElements().size() != 0)
      area = buildingAreas.getPercentile(50);
    if (area < meanArea) {
      BuildingSizeCriterion.logger.finest(this.getName() + " : " + area
          / (2 * meanArea) + " - small area");
      return area / (2 * meanArea);
    }
    // progression linéaire entre 0.5 et 1 pour les ilots dont l'aire des
    // bâtiments varie entre la moyenne et le maximum
    double areaValue = -area / (2.0 * (meanArea - maxArea)) + 1 + maxArea
        / (2.0 * (meanArea - maxArea));
    double value = Math.min(1.0, areaValue);
    BuildingSizeCriterion.logger.finest("maxArea: " + maxArea);
    BuildingSizeCriterion.logger.finest("meanArea: " + meanArea);
    BuildingSizeCriterion.logger.finest("current area: " + area);
    BuildingSizeCriterion.logger.finest("computed value: " + areaValue);
    BuildingSizeCriterion.logger.finest(this.getName() + " : " + value
        + " - large area");
    return value;
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
