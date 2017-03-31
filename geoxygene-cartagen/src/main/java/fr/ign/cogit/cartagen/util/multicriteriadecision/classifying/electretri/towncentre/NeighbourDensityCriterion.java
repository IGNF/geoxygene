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

public class NeighbourDensityCriterion extends ELECTRECriterion {

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
  public NeighbourDensityCriterion(String nom) {
    super(nom);
    this.setWeight(1.2);
    this.setIndifference(0.05);
    this.setPreference(0.2);
    this.setVeto(0.4);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IUrbanBlock block = (IUrbanBlock) param.get("block");
    logger.finer("block: " + block.getId());
    if (block.getNeighbours().size() == 0)
      return 0.5;
    double meanDensity = 0.0;
    int crossroadBlock = 0;
    for (IUrbanBlock neighbour : block.getNeighbours()) {
      if (neighbour.getGeom().area() < 1500.0
          && neighbour.getUrbanElements().size() == 0) {
        crossroadBlock++;
        continue;
      }
      // 1.3 factor to spread the density distribution for 0 to 1.
      double density = neighbour.getDensity() * 1.3;
      meanDensity += Math.min(1.0, density);
      logger
          .finer("density of neighbour " + neighbour.getId() + " : " + density);
    }
    meanDensity = meanDensity / (block.getNeighbours().size() - crossroadBlock);
    logger.finer(this.getName() + " : " + meanDensity);
    return meanDensity;
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
