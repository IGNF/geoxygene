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
  private static Logger logger = Logger
      .getLogger(ELECTRECriterion.class.getName());

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
    this.setIndifference(0.1);
    this.setPreference(0.25);
    this.setVeto(0.6);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IUrbanBlock block = (IUrbanBlock) param.get("block");
    BuildingSizeCriterion.logger.finest("block: " + block.getId());
    double meanArea = ((Double) param.get("meanBuildingArea")).doubleValue();
    DescriptiveStatistics townStats = (DescriptiveStatistics) param
        .get("buildingAreaStats");
    DescriptiveStatistics buildingAreas = new DescriptiveStatistics();
    if (meanArea == 0.0 || meanArea == Double.NaN)
      return 0.0;
    double maxArea = Math.min(townStats.getMax(),
        3 * townStats.getPercentile(50));
    for (IUrbanElement e : block.getUrbanElements()) {
      buildingAreas.addValue(e.getGeom().area());
    }

    // simple case with one building
    if (block.getUrbanElements().size() == 1) {
      IUrbanElement e = block.getUrbanElements().get(0);
      if (e.getGeom().area() < townStats.getPercentile(50))
        return e.getGeom().area() / (2 * townStats.getPercentile(50));
      else
        return -e.getGeom().area()
            / (2.0 * (townStats.getPercentile(50) - maxArea)) + 1
            + maxArea / (2.0 * (townStats.getPercentile(50) - maxArea));
    }

    double area = 0.0;
    if (block.getUrbanElements().size() != 0)
      area = buildingAreas.getPercentile(50);
    if (area < townStats.getPercentile(50)) {
      BuildingSizeCriterion.logger.finest(
          this.getName() + " : " + area / (2 * meanArea) + " - small area");
      return area / (2 * meanArea);
    }

    // progression linéaire entre 0.5 et 1 pour les ilots dont l'aire des
    // bâtiments varie entre la moyenne et le maximum
    double areaValue = Math
        .sqrt(-area / (2.0 * (townStats.getPercentile(50) - maxArea)) + 1
            + maxArea / (2.0 * (townStats.getPercentile(50) - maxArea)));
    double value = Math.min(1.0, areaValue);
    double percentileValue = 0.5;
    if (buildingAreas.getPercentile(60) <= townStats.getPercentile(60))
      percentileValue = 0.6;
    if (buildingAreas.getPercentile(60) <= townStats.getPercentile(70))
      percentileValue = 0.7;
    if (buildingAreas.getPercentile(60) <= townStats.getPercentile(80))
      percentileValue = 0.8;
    BuildingSizeCriterion.logger.finest("maxArea: " + maxArea);
    BuildingSizeCriterion.logger
        .finest("meanArea: " + townStats.getPercentile(50));
    BuildingSizeCriterion.logger.finest("current area: " + area);
    BuildingSizeCriterion.logger.finest("computed value: " + areaValue);
    BuildingSizeCriterion.logger.finest("percentile value: " + percentileValue);
    BuildingSizeCriterion.logger
        .finest(this.getName() + " : " + value + " - large area");
    BuildingSizeCriterion.logger
        .finest("returned value: " + Math.max(value, percentileValue));
    return Math.max(value, percentileValue);
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
