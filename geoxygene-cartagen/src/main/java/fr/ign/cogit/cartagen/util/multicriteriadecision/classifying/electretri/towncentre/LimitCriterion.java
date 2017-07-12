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

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.ELECTRECriterion;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

public class LimitCriterion extends ELECTRECriterion {

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
  private IFeatureCollection<IWaterArea> waterAreas;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public LimitCriterion(String nom) {
    super(nom);
    this.setWeight(0.7);
    this.setIndifference(0.05);
    this.setPreference(0.2);
    this.setVeto(0.9);
    waterAreas = new FT_FeatureCollection<>();
  }

  public LimitCriterion(String nom, IFeatureCollection<IWaterArea> waterAreas) {
    super(nom);
    this.setWeight(0.7);
    this.setIndifference(0.05);
    this.setPreference(0.2);
    this.setVeto(0.9);
    this.waterAreas = waterAreas;
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IUrbanBlock block = (IUrbanBlock) param.get("block");
    LimitCriterion.logger.finest("block: " + block.getId());
    ILineString outline = (ILineString) param.get("outline");
    if (block.getGeom().intersects(outline)) {
      LimitCriterion.logger.finest(this.getName() + " : " + 0.0);
      Collection<IWaterArea> interAreas = waterAreas.select(block.getGeom());
      if (interAreas.size() > 1)
        return 0.25;
      return 0.0;
    }
    LimitCriterion.logger.finest(this.getName() + " : " + 0.5);
    return 0.5;
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
