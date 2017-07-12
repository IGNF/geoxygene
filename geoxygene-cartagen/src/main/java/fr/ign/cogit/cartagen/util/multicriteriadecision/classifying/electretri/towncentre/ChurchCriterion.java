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

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.ELECTRECriterion;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;

public class ChurchCriterion extends ELECTRECriterion {

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
  private String attributeValue = "Eglise";

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public ChurchCriterion(String nom) {
    super(nom);
    this.setWeight(0.7);
    this.setIndifference(0.05);
    this.setPreference(0.2);
    this.setVeto(0.1);
  }

  public ChurchCriterion(String nom, String attributeValue) {
    super(nom);
    this.setWeight(0.7);
    this.setIndifference(0.05);
    this.setPreference(0.2);
    this.setVeto(0.1);
    this.attributeValue = attributeValue;
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IUrbanBlock block = (IUrbanBlock) param.get("block");
    ChurchCriterion.logger.finest("block: " + block.getId());
    boolean church = false;
    Ilot ilot = (Ilot) block.getGeoxObj();

    for (IUrbanElement e : block.getUrbanElements()) {
      if (!(e instanceof IBuilding))
        continue;
      IBuilding b = (IBuilding) e;
      if (b.getNature().equals(attributeValue)) {
        church = true;
        break;
      }
    }

    if (!church) {
      ChurchCriterion.logger.finest(this.getName() + " : " + 0.5);
      return 0.5;
    }
    double value = Math.max(1.0 - 0.2 * (ilot.getComposants().size() - 1.0),
        0.5);
    ChurchCriterion.logger.finest(this.getName() + " : " + value);
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
