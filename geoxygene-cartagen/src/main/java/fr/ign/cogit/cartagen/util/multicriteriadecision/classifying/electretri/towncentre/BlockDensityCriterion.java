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

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.ELECTRECriterion;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class BlockDensityCriterion extends ELECTRECriterion {

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
    IGeometry geom = block.getGeom();
    double buildArea = 0.0;
    for (IUrbanElement e : block.getUrbanElements()) {
      if (e.isDeleted()) {
        continue;
      }
      if (!geom.contains(e.getGeom())) {
        continue;
      }
      buildArea += e.getGeom().area();
    }

    for (INetworkSection route : block.getSurroundingNetwork()) {
      if (route.isDeleted()) {
        continue;
      }
      if (!geom.intersects(route.getGeom())) {
        continue;
      }
      double symbolWidth = route.getWidth() / 2;
      geom = geom.difference(route.getGeom().buffer(symbolWidth));
    }
    double density = 1.0;
    if (geom.area() != 0.0) {
      density = Math.min(1.0, buildArea / geom.area());
    }
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
