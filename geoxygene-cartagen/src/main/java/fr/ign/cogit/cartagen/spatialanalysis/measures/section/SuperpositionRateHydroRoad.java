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

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public class SuperpositionRateHydroRoad {

  /**
   * the river on which the superposition will be calculated
   */

  private IWaterLine river;

  public void setRiver(IWaterLine river) {
    this.river = river;
  }

  public IWaterLine getRiver() {
    return this.river;
  }

  /**
   * 
   */
  private INetwork net;

  public INetwork getRoadNetwork() {
    return this.net;
  }

  public void setRoadNetwork(INetwork net) {
    this.net = net;
  }

  /**
   * Coçnstructor
   * @param river
   */
  public SuperpositionRateHydroRoad(IWaterLine river, INetwork net) {
    this.river = river;
    this.net = net;
  }

  public double compute() {

    if (this.river.isDeleted()) {
      return 0.0;
    }

    IGeometry union = null;
    double distSep = GeneralisationSpecifications.DISTANCE_SEPARATION_HYDRO_ROUTIER
        * Legend.getSYMBOLISATI0N_SCALE() / 1000.0;
    IGeometry emprise = SectionSymbol.getSymbolExtent(this.river);

    for (INetworkSection at : this.net.getSections()) {
      if (at.isDeleted()) {
        continue;
      }
      IGeometry empriseTR = SectionSymbol.getSymbolExtent(at);
      if (emprise.distance(empriseTR) > distSep) {
        continue;
      }
      IGeometry intersection = CommonAlgorithms.buffer(empriseTR, distSep)
          .intersection(emprise);
      if (union == null) {
        union = intersection;
      } else {
        union = intersection.union(union);
      }
    }

    if (union == null) {
      return 0.0;
    }
    return union.area() / emprise.area();

  }

}
