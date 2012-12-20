/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network.roads;

public enum RoadNatureBdTopo {
  BEING_BUILT("En construction"), LOCAL("Locale"), REGIONAL("Régionale"), PRINCIPAL(
      "Principale"), HIGHWAY("Autoroutière");

  private String valueBdTopo;

  private RoadNatureBdTopo(String value) {
    this.valueBdTopo = value;
  }

  public String getValueBdTopo() {
    return this.valueBdTopo;
  }

  public static RoadNatureBdTopo getFromValueBdTopo(String value) {
    for (RoadNatureBdTopo nature : RoadNatureBdTopo.values()) {
      if (nature.getValueBdTopo().equals(value)) {
        return nature;
      }
    }
    return null;
  }
}
