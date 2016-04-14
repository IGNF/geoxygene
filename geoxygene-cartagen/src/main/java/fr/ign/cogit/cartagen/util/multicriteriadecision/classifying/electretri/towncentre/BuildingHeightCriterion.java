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
import fr.ign.cogit.geoxygene.schemageo.api.bati.Batiment;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.schemageo.api.support.elementsIndependants.ElementIndependant;

public class BuildingHeightCriterion extends ELECTRECriterion {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static double MIN_HEIGHT = 6.0;
  private static double MAX_HEIGHT = 40.0;
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
  public BuildingHeightCriterion(String nom) {
    super(nom);
    this.setWeight(1.2);
    this.setIndifference(0.05);
    this.setPreference(0.1);
    this.setVeto(0.4);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IUrbanBlock block = (IUrbanBlock) param.get("block");
    BuildingHeightCriterion.logger.finest("block: " + block.getId());
    Ilot ilot = (Ilot) block.getGeoxObj();
    double totalHeight = 0.0;
    for (ElementIndependant e : ilot.getComposants()) {
      Batiment b = (Batiment) e;
      totalHeight += b.getHauteur();
    }
    double height = totalHeight / ilot.getComposants().size();
    if (height < BuildingHeightCriterion.MIN_HEIGHT) {
      BuildingHeightCriterion.logger.finest(this.getName() + " : " + 0.2
          * (height / BuildingHeightCriterion.MIN_HEIGHT));
      return 0.2 * (height / BuildingHeightCriterion.MIN_HEIGHT);
    }
    if (height > BuildingHeightCriterion.MAX_HEIGHT) {
      BuildingHeightCriterion.logger.finest(this.getName() + 0.2);
      return 0.2;
    }
    double best = (2.0 * BuildingHeightCriterion.MIN_HEIGHT + BuildingHeightCriterion.MAX_HEIGHT) / 3.0;
    double value = Math.max(0.2, 1.0 - Math.abs(height - best) / best);
    BuildingHeightCriterion.logger.finest(this.getName() + " : " + value);
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
