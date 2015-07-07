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

import java.util.Map;

import org.jgrapht.Graph;

/**
 * A utility class that combines a jgrapht graph with a map that relates the
 * graph edges to geographic instances that implement IFeature.
 * @author GTouya
 * 
 */
public class GeographicNetworkGraph<V, E, F> {

  private Graph<V, E> graph;
  private Map<E, F> maptoFeatures;

  public GeographicNetworkGraph(Graph<V, E> graph, Map<E, F> maptoFeatures) {
    super();
    this.graph = graph;
    this.maptoFeatures = maptoFeatures;
  }

  /**
   * Get the geographic feature from the graph edge.
   * @param edge
   * @return
   */
  public F getFeatureFromEdge(E edge) {
    return maptoFeatures.get(edge);
  }

  /**
   * Get the geographic feature from a graph initial and final nodes.
   * @param initialNode
   * @param finalNode
   * @return
   */
  public F getFeatureBetweenNodes(V initialNode, V finalNode) {
    E edge = graph.getEdge(initialNode, finalNode);
    return maptoFeatures.get(edge);
  }

  public Graph<V, E> getGraph() {
    return graph;
  }

  public void setGraph(Graph<V, E> graph) {
    this.graph = graph;
  }

}
