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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.graph.jgrapht.GeographicNetworkGraph;
import fr.ign.cogit.cartagen.graph.jgrapht.GraphFactory;
import fr.ign.cogit.cartagen.graph.jgrapht.MetricalGraphWeighter;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.AttractionPoint;
import fr.ign.cogit.cartagen.spatialanalysis.network.roads.AttractionPointDetection;

/**
 * Road network selection algorithms based on a traffic assessment, using
 * shortest paths (Richardson & Thomson 96, Touya 2010).
 * @author GTouya
 * 
 */
public class RoadNetworkTrafficBasedSelection {

  private INetwork network;
  private CartAGenDataSet dataset;

  public RoadNetworkTrafficBasedSelection(CartAGenDataSet dataset,
      INetwork network) {
    super();
    this.network = network;
  }

  /**
   * Select the roads in a network that have a weight bigger than the given
   * minimum threshold. The weight corresponds the number of shortest paths
   * between network attraction points that use the road.
   * @param minWeight
   * @return the roads to eliminate
   */
  public Set<INetworkSection> trafficBasedSelection(int minWeight) {
    Set<INetworkSection> toEliminate = new HashSet<>();
    // find attraction points
    AttractionPointDetection detection = new AttractionPointDetection(dataset,
        network);
    detection.findAttractionPoints();

    // build the graph
    GeographicNetworkGraph<INetworkNode, DefaultWeightedEdge, INetworkSection> graph = GraphFactory
        .buildGeoGraphFromNetwork(network, new MetricalGraphWeighter());
    Stack<AttractionPoint> ptStack = new Stack<>();
    ptStack.addAll(detection.getAttractionPoints());
    Map<IRoadLine, Integer> weightMap = new HashMap<>();
    while (!ptStack.isEmpty()) {
      AttractionPoint initialPt = ptStack.pop();
      Set<AttractionPoint> others = new HashSet<>();
      others.addAll(ptStack);
      for (AttractionPoint finalPt : others) {
        // compute the node weight
        int nodeWeight = initialPt.getWeight() + finalPt.getWeight();

        DijkstraShortestPath<INetworkNode, DefaultWeightedEdge> shortestPathFinder = new DijkstraShortestPath<INetworkNode, DefaultWeightedEdge>(
            graph.getGraph(), initialPt.getRoadNode(), finalPt.getRoadNode());
        for (DefaultWeightedEdge edge : shortestPathFinder.getPathEdgeList()) {
          INetworkSection road = graph.getFeatureFromEdge(edge);
          if (weightMap.containsKey(road)) {
            int weight = weightMap.get(road);
            weightMap.put((IRoadLine) road, weight + nodeWeight);
          } else
            weightMap.put((IRoadLine) road, nodeWeight);
        }
      }
    }

    // selection phase
    for (INetworkSection road : network.getSections()) {
      // if the road is not in weighted map, no shortest path used it so
      // eliminate it
      if (weightMap.containsKey(road) == false) {
        toEliminate.add(road);
        continue;
      }

      int weight = weightMap.get(road);
      if (weight < minWeight)
        toEliminate.add(road);
    }

    return toEliminate;
  }

  /**
   * Select the roads in a network that have a weight bigger than the given
   * minimum threshold. The weight corresponds the number of shortest paths
   * between network attraction points that use the road. Here, attraction
   * points are created randomly in the network, e.g. 10% of the nodes are
   * attraction points. Be careful, the number of attraction points should be
   * quite small to have significant results.
   * @param minWeight
   * @param ratio the number of attraction points to create related to the
   *          number of nodes in the network.
   * @return the roads to eliminate
   */
  public Set<INetworkSection> randomTrafficBasedSelection(int minWeight,
      double ratio) {
    Set<INetworkSection> toEliminate = new HashSet<>();
    // find attraction points
    AttractionPointDetection detection = new AttractionPointDetection(dataset,
        network);
    detection.createRandomAttractionPoints(ratio);

    // build the graph
    GeographicNetworkGraph<INetworkNode, DefaultWeightedEdge, INetworkSection> graph = GraphFactory
        .buildGeoGraphFromNetwork(network, new MetricalGraphWeighter());
    Stack<AttractionPoint> ptStack = new Stack<>();
    ptStack.addAll(detection.getAttractionPoints());
    Map<IRoadLine, Integer> weightMap = new HashMap<>();
    while (!ptStack.isEmpty()) {
      AttractionPoint initialPt = ptStack.pop();
      Set<AttractionPoint> others = new HashSet<>();
      others.addAll(ptStack);
      for (AttractionPoint finalPt : others) {
        // compute the node weight
        int nodeWeight = initialPt.getWeight() + finalPt.getWeight();

        DijkstraShortestPath<INetworkNode, DefaultWeightedEdge> shortestPathFinder = new DijkstraShortestPath<INetworkNode, DefaultWeightedEdge>(
            graph.getGraph(), initialPt.getRoadNode(), finalPt.getRoadNode());

        // case where attraction points are not connected in the network
        if (shortestPathFinder.getPathEdgeList() == null)
          continue;

        for (DefaultWeightedEdge edge : shortestPathFinder.getPathEdgeList()) {
          INetworkSection road = graph.getFeatureFromEdge(edge);
          if (weightMap.containsKey(road)) {
            int weight = weightMap.get(road);
            weightMap.put((IRoadLine) road, weight + nodeWeight);
          } else
            weightMap.put((IRoadLine) road, nodeWeight);
        }
      }
    }

    // selection phase
    for (INetworkSection road : network.getSections()) {
      // if the road is not in weighted map, no shortest path used it so
      // eliminate it
      if (weightMap.containsKey(road) == false) {
        toEliminate.add(road);
        continue;
      }

      int weight = weightMap.get(road);
      if (weight < minWeight)
        toEliminate.add(road);
    }

    return toEliminate;
  }
}
