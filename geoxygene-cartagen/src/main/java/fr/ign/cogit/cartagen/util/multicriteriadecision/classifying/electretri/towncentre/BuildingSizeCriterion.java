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

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.ELECTRECriterion;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.schemageo.api.support.elementsIndependants.ElementIndependant;

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
    this.setPreference(0.2);
    this.setVeto(0.1);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IUrbanBlock block = (IUrbanBlock) param.get("block");
    BuildingSizeCriterion.logger.finest("block: " + block.getId());
    double meanArea = ((Double) param.get("meanBuildingArea")).doubleValue();
    double maxArea = 3.0 * meanArea;
    Ilot ilot = (Ilot) block.getGeoxObj();
    double totalArea = 0.0;
    for (ElementIndependant e : ilot.getComposants()) {
      totalArea += e.getGeom().area();
    }
    double area = totalArea / ilot.getComposants().size();
    if (area < meanArea) {
      BuildingSizeCriterion.logger.finest(this.getName() + " : " + area
          / meanArea);
      return area / meanArea;
    }
    // progression linéaire entre 0.5 et 1 pour les ilots dont l'aire des
    // bâtiments varie entre la moyenne et le maximum
    double value = Math.min(1.0, block.getGeom().area()
        / (2.0 * (maxArea - meanArea)) + 1 - 1 / (2.0 * (maxArea - meanArea))
        + 0.1);
    BuildingSizeCriterion.logger.finest(this.getName() + " : " + value);
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
