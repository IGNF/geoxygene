/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.graph.jgrapht;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedPseudograph;

import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * A factory to build JGraphT graphs out of GeOxygene/CartAGen features.
 * @author GTouya
 * 
 */
public class GraphFactory {

  /**
   * Build a pseudo gaph, i.e. undirected weighted graph, with possible loops
   * and multiple edges, from a CartAGen {@link INetwork} instance, and a
   * {@link GraphWeighter} instance.
   * @param network
   * @param graphWeighter
   * @return
   */
  public static WeightedPseudograph<INetworkNode, DefaultWeightedEdge> buildGraphFromNetwork(
      INetwork network, GraphWeighter graphWeighter) {
    WeightedPseudograph<INetworkNode, DefaultWeightedEdge> graph = new WeightedPseudograph<INetworkNode, DefaultWeightedEdge>(
        DefaultWeightedEdge.class);

    // first create nodes
    for (INetworkNode node : network.getNodes()) {
      graph.addVertex(node);
    }

    // then, create edges
    for (INetworkSection section : network.getSections()) {
      double weight = graphWeighter.getEdgeWeight(section);
      DefaultWeightedEdge edge = graph.addEdge(section.getInitialNode(),
          section.getFinalNode());
      graph.setEdgeWeight(edge, weight);
    }

    return graph;
  }

  /**
   * Same as buildGraphFromNetwork but returns a {@link GeographicNetworkGraph}
   * instead of just the graph, which can be helpful to get the {@link IFeature}
   * instances related to a graph edge.
   * @param network
   * @param graphWeighter
   * @return
   */
  public static GeographicNetworkGraph<INetworkNode, DefaultWeightedEdge, INetworkSection> buildGeoGraphFromNetwork(
      INetwork network, GraphWeighter graphWeighter) {
    WeightedPseudograph<INetworkNode, DefaultWeightedEdge> graph = new WeightedPseudograph<INetworkNode, DefaultWeightedEdge>(
        DefaultWeightedEdge.class);
    Map<DefaultWeightedEdge, INetworkSection> map = new HashMap<DefaultWeightedEdge, INetworkSection>();
    // first create nodes
    for (INetworkNode node : network.getNodes()) {
      graph.addVertex(node);
    }

    // then, create edges
    for (INetworkSection section : network.getSections()) {
      if (section.isEliminated())
        continue;
      double weight = graphWeighter.getEdgeWeight(section);
      DefaultWeightedEdge edge = graph.addEdge(section.getInitialNode(),
          section.getFinalNode());
      graph.setEdgeWeight(edge, weight);
      map.put(edge, section);
    }

    return new GeographicNetworkGraph<INetworkNode, DefaultWeightedEdge, INetworkSection>(
        graph, map);
  }
}
