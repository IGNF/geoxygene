/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.graphe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

public class GraphPath {

  private List<IEdge> arcs;
  private List<INode> nodes;
  private double length;
  private int id;
  private IGraph graph;

  private static AtomicInteger counter = new AtomicInteger();

  public List<IEdge> getArcs() {
    return this.arcs;
  }

  public void setArcs(List<IEdge> arcs) {
    this.arcs = arcs;
  }

  public List<INode> getNodes() {
    return this.nodes;
  }

  public void setNodes(List<INode> nodes) {
    this.nodes = nodes;
  }

  public double getLength() {
    return this.length;
  }

  public void setLength(double length) {
    this.length = length;
  }

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public GraphPath(double length, List<IEdge> arcList, List<INode> nodeList,
      IGraph graph) {
    this.arcs = arcList;
    this.nodes = nodeList;
    this.length = length;
    this.graph = graph;
    this.id = graph.getId() * 1000 + GraphPath.counter.incrementAndGet();
  }

  /**
   * Get the initial node of the path.
   * @return
   */
  public INode getInitialNode() {
    return nodes.get(0);
  }

  /**
   * Get the final node of the path.
   * @return
   */
  public INode getFinalNode() {
    return nodes.get(nodes.size() - 1);
  }

  public ILineString getPathGeometry() {
    IDirectPositionList coord = new DirectPositionList();
    for (INode node : nodes)
      coord.add(node.getPosition());
    return new GM_LineString(coord);
  }

  public GraphPath reverse() {
    List<IEdge> edges = new ArrayList<IEdge>(getArcs());
    Collections.reverse(edges);
    List<INode> nodes = new ArrayList<INode>(getNodes());
    Collections.reverse(nodes);
    return new GraphPath(getLength(), edges, nodes, graph);
  }
}
