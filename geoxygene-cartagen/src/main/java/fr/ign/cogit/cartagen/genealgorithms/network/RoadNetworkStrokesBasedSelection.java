/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.genealgorithms.network;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.CrossRoadDetection;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.RoadStrokesNetwork;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.TCrossRoad;
import fr.ign.cogit.geoxygene.schemageo.api.routier.NoeudRoutier;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

/**
 * Road network selection algorithms based on strokes.
 * @author GTouya
 * 
 */
public class RoadNetworkStrokesBasedSelection {

  private INetwork network;
  private RoadStrokesNetwork strokes;
  private Map<ArcReseau, IRoadLine> map = new HashMap<ArcReseau, IRoadLine>();
  private String attributeName;

  public RoadNetworkStrokesBasedSelection(CartAGenDataSet dataset,
      INetwork network) {
    super();
    this.network = network;
    for (INetworkSection obj : network.getSections()) {
      map.put((ArcReseau) obj.getGeoxObj(), (IRoadLine) obj);
    }
    strokes = new RoadStrokesNetwork(map.keySet());
    HashSet<String> attributeNames = new HashSet<String>();
    if (this.getAttributeName() != null) {
      if (this.getAttributeName() != "")
        attributeNames.add(this.getAttributeName());
    }
    strokes.buildStrokes(attributeNames, 112.5, 45.0, true);
  }

  public RoadNetworkStrokesBasedSelection(CartAGenDataSet dataset,
      INetwork network, RoadStrokesNetwork strokes,
      Map<ArcReseau, IRoadLine> map) {
    super();
    this.network = network;
    this.strokes = strokes;
    this.map = map;
  }

  /**
   * Select the roads in a network that belong to a stroke longer than the given
   * minimum threshold. Strokes that are part of enough T-shaped crossroads can
   * also be added to selection even if length selection fails.
   * @param minLength
   * @param minTs the minimum number of T crossroads for a stroke to be selected
   *          (-1 means don't use Ts)
   * @return the roads to eliminate
   */
  public Set<INetworkSection> strokesBasedSelection(double minLength, int minTs) {
    Set<INetworkSection> toEliminate = new HashSet<>();

    for (Stroke stroke : strokes.getStrokes()) {
      if (stroke.getLength() > minLength) {
        continue;
      }

      if (minTs > -1) {
        // now checks the number of Ts on the stroke
        Collection<INetworkNode> strokeNodes = network.getNodes().select(
            stroke.getGeomStroke());
        int nbTs = 0;
        CrossRoadDetection detection = new CrossRoadDetection();
        for (INetworkNode node : strokeNodes) {
          if (TCrossRoad.isTNode((NoeudRoutier) node.getGeoxObj(),
              detection.getFlatAngle(), detection.getBisAngle()))
            nbTs++;
        }

        if (nbTs > minTs)
          continue;
      }

      // arrived here, eliminate all road sections of the stroke
      for (ArcReseau feat : stroke.getFeatures()) {
        IRoadLine road = map.get(feat);
        if (road != null)
          toEliminate.add(road);
      }
    }

    return toEliminate;
  }

  public String getAttributeName() {
    return attributeName;
  }

  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }

}
