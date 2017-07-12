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

public class BlockDensityCriterion extends ELECTRECriterion {

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
  public BlockDensityCriterion(String nom) {
    super(nom);
    this.setWeight(1.5);
    this.setIndifference(0.05);
    this.setPreference(0.2);
    this.setVeto(0.4);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IUrbanBlock block = (IUrbanBlock) param.get("block");
    BlockDensityCriterion.logger.finer("block: " + block.getId());
    // 1.3 factor to spread the density distribution for 0 to 1.
    double density = Math.min(1.0, block.getDensity() * 1.3);
    BlockDensityCriterion.logger.finer(this.getName() + " : " + density);
    return density;
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
