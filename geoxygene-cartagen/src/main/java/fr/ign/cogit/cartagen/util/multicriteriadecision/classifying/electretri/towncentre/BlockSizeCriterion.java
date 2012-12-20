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

public class BlockSizeCriterion extends ELECTRECriterion {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static double TOO_SMALL_THRESHOLD = 750.0;
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
  public BlockSizeCriterion(String nom) {
    super(nom);
    this.setWeight(1.0);
    this.setIndifference(0.05);
    this.setPreference(0.2);
    this.setVeto(0.1);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IUrbanBlock block = (IUrbanBlock) param.get("block");
    BlockSizeCriterion.logger.finer("block: " + block.getId());
    double maxArea = 2.0 * ((Double) param.get("meanBlockArea")).doubleValue();
    if (block.getGeom().area() < BlockSizeCriterion.TOO_SMALL_THRESHOLD) {
      BlockSizeCriterion.logger.finest(this.getName() + " : " + 0.0);
      return 0.0;
    }
    double value = Math.max(0.0, 1.0 - block.getGeom().area() / maxArea);
    BlockSizeCriterion.logger.finest(this.getName() + " maxArea : " + maxArea);
    BlockSizeCriterion.logger.finest(this.getName() + " area : "
        + block.getGeom().area());
    BlockSizeCriterion.logger.finer(this.getName() + " : " + value);
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
