/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.graph;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GraphPath {

  private List<IEdge> arcs;
  private List<INode> nodes;
  private double length;
  private int id;

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
    this.id = graph.getId() * 1000 + GraphPath.counter.incrementAndGet();
  }
}
