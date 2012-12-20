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

public enum RoadClassBdTopo {
  OTHER("Autre classement", "Autre classemen"), SECONDARY("Départementale",
      "Départementale"), NATIONAL("Nationale", "Nationale"), HIGHWAY(
      "Autoroute", "Autoroute");

  private String valueBdTopo, valueTestData;

  private RoadClassBdTopo(String value, String valueTestData) {
    this.valueBdTopo = value;
    this.valueTestData = valueTestData;
  }

  public String getValueBdTopo() {
    return this.valueBdTopo;
  }

  public String getValueTestData() {
    return this.valueTestData;
  }

  public static RoadClassBdTopo getFromValueBdTopo(String value) {
    for (RoadClassBdTopo classBd : RoadClassBdTopo.values()) {
      if (classBd.getValueBdTopo().equals(value)) {
        return classBd;
      }
    }
    return null;
  }

  public static RoadClassBdTopo getFromValueTestData(String value) {
    for (RoadClassBdTopo classBd : RoadClassBdTopo.values()) {
      if (classBd.getValueTestData().equals(value)) {
        return classBd;
      }
    }
    return null;
  }
}
