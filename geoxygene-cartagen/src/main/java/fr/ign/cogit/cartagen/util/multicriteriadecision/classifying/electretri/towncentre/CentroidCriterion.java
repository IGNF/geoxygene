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
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

public class CentroidCriterion extends ELECTRECriterion {

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
  public CentroidCriterion(String nom) {
    super(nom);
    this.setWeight(1.5);
    this.setIndifference(0.1);
    this.setPreference(0.2);
    this.setVeto(0.4);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IUrbanBlock block = (IUrbanBlock) param.get("block");
    CentroidCriterion.logger.finest("block: " + block.getId());
    IDirectPosition centroid = (IDirectPosition) param.get("centroid");
    double maxDist = (Double) param.get("max_dist");
    maxDist = maxDist * 2 / 3;
    if (block.getGeom().contains(centroid.toGM_Point())) {
      CentroidCriterion.logger.finest(this.getName() + " : " + 0.95);
      return 0.95;
    }
    double distance = centroid.distance(block.getGeom().centroid());
    double value = Math.max(0.0, Math.pow(1.0 - distance / maxDist, 2));
    CentroidCriterion.logger.finest(this.getName() + " : " + value);
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
