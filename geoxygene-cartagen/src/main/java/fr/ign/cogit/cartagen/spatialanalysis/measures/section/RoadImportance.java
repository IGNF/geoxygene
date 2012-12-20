/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.measures.section;

import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadClassBdTopo;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadNatureBdTopo;

public class RoadImportance {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private RoadClassBdTopo bdTopoClass;
  private RoadNatureBdTopo bdTopoNature;
  private int laneNumber;
  private int importance;// 0 is the less important

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public RoadImportance(RoadClassBdTopo bdTopoClass,
      RoadNatureBdTopo bdTopoNature, int laneNumber) {
    super();
    this.bdTopoClass = bdTopoClass;
    this.bdTopoNature = bdTopoNature;
    this.laneNumber = laneNumber;
    this.computeImportanceZeroToEight();
  }

  // Getters and setters //
  public void setImportance(int importance) {
    this.importance = importance;
  }

  public int getImportance() {
    return this.importance;
  }

  // Other public methods //

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////
  private void computeImportanceZeroToEight() {
    if (this.bdTopoNature.equals(RoadNatureBdTopo.HIGHWAY)
        && this.bdTopoClass.equals(RoadClassBdTopo.HIGHWAY)) {
      this.setImportance(8);
      return;
    }
    if (this.bdTopoNature.equals(RoadNatureBdTopo.HIGHWAY)
        || this.bdTopoClass.equals(RoadClassBdTopo.HIGHWAY)) {
      this.setImportance(7);
      return;
    }
    if (this.bdTopoNature.equals(RoadNatureBdTopo.PRINCIPAL)
        && this.laneNumber >= 4) {
      this.setImportance(6);
      return;
    }
    if (this.bdTopoNature.equals(RoadNatureBdTopo.PRINCIPAL)) {
      this.setImportance(5);
      return;
    }
    if (this.bdTopoNature.equals(RoadNatureBdTopo.REGIONAL)) {
      this.setImportance(4);
      return;
    }
    if (this.bdTopoNature.equals(RoadNatureBdTopo.LOCAL)
        && (this.bdTopoClass.equals(RoadClassBdTopo.SECONDARY) || this.bdTopoClass
            .equals(RoadClassBdTopo.NATIONAL))) {
      this.setImportance(3);
      return;
    }
    if (this.bdTopoNature.equals(RoadNatureBdTopo.LOCAL)
        && this.laneNumber >= 2) {
      this.setImportance(2);
      return;
    }
    if (this.bdTopoNature.equals(RoadNatureBdTopo.LOCAL)
        && this.laneNumber == 1) {
      this.setImportance(1);
      return;
    }
    this.setImportance(0);
  }

}
