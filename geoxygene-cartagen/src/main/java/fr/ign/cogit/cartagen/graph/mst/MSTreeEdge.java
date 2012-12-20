/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.graph.mst;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.cartagen.graph.Edge;
import fr.ign.cogit.cartagen.graph.IEdge;
import fr.ign.cogit.cartagen.graph.IGraph;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;

/*
 * ###### IGN / CartAGen ###### Title: MSTreeEdge Description: An edge of a
 * minimum spanning tree Author: J. Renard Version: 1.0 Changes: 1.0 (08/11/10)
 * : creation
 */

public class MSTreeEdge extends Edge {

  // The ID of the edge
  private int id;

  public int getId() {
    return this.id;
  }

  public void setId(int id) {
    this.id = id;
  }

  private double weight;

  private static AtomicInteger counter = new AtomicInteger();

  // The spanning tree the edge belongs to
  private MinSpanningTree spanningTree;

  public MinSpanningTree getSpanningTree() {
    return this.spanningTree;
  }

  public void setSpanningTree(MinSpanningTree spanningTree) {
    this.spanningTree = spanningTree;
  }

  // The nodes at the edge extremities
  private MSTreeNode firstNode, secondNode;

  public Set<INode> getNodes() {
    Set<INode> nodes = new HashSet<INode>();
    nodes.add(this.firstNode);
    nodes.add(this.secondNode);
    return nodes;
  }

  // The length value according to the distance between the connected GeneObj
  // (different from real length of the edge segment)
  private double lengthValue;

  public double getLengthValue() {
    return this.lengthValue;
  }

  public void setLengthValue(double lengthValue) {
    this.lengthValue = lengthValue;
  }

  // The geometry of the edge
  private ILineSegment geom;

  public ILineSegment getGeom() {
    return this.geom;
  }

  @Override
  public boolean equals(Object obj) {
    MSTreeEdge autre = (MSTreeEdge) obj;
    if (!autre.firstNode.equals(this.firstNode)) {
      return false;
    }
    if (!autre.secondNode.equals(this.secondNode)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return this.id;
  }

  @Override
  public String toString() {
    return "edge " + this.id;
  }

  /**
   * Default constructor
   */

  public MSTreeEdge() {
    super(null, null);
    this.id = MSTreeEdge.counter.incrementAndGet();
    this.spanningTree = null;
    this.firstNode = null;
    this.secondNode = null;
    this.lengthValue = 0.0;
    this.geom = null;
  }

  /**
   * Constructor
   * @param spanningTree : the spanning tree the edge belongs to
   * @param firstNode : extremity node
   * @param secondNode : extremity node
   * @param geom : LineSegment geometry
   */

  public MSTreeEdge(MinSpanningTree spanningTree, MSTreeNode firstNode,
      MSTreeNode secondNode, ILineSegment geom, double lengthValue) {
    super(spanningTree, firstNode, secondNode, new HashSet<IFeature>(), geom);
    this.spanningTree = spanningTree;
    this.firstNode = firstNode;
    this.firstNode.addEdgeOut(this);
    this.secondNode = secondNode;
    this.secondNode.addEdgeIn(this);
    this.geom = geom;
    this.lengthValue = lengthValue;
    this.id = spanningTree.getId() * 100000
        + MSTreeEdge.counter.incrementAndGet();
  }

  /**
   * Get the arcs following this after one of this' node passed as input of the
   * method.
   * @param n : the node of the arc that is followed
   * @return
   */

  public Set<IEdge> getNextArcs(MSTreeNode n) {
    Set<IEdge> edges = new HashSet<IEdge>();
    edges.addAll(n.getEdges());
    edges.remove(this);
    return edges;
  }

  /**
   * Get the arcs following this after both of this' nodes. Takes the
   * orientation into account.
   * @return
   */

  public Set<IEdge> getNextArcs() {
    Set<IEdge> edges = new HashSet<IEdge>();
    edges.addAll(this.getNextArcs(this.firstNode));
    edges.addAll(this.getNextArcs(this.secondNode));
    return edges;
  }

  @Override
  public INode getFinalNode() {
    return this.secondNode;
  }

  @Override
  public IGraph getGraph() {
    return spanningTree;
  }

  @Override
  public INode getInitialNode() {
    return this.firstNode;
  }

  @Override
  public double getWeight() {
    return weight;
  }

  @Override
  public void setFinalNode(INode node) {
    this.secondNode = (MSTreeNode) node;
  }

  @Override
  public void setGeoObjects(Set<IFeature> geoObjects) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setGeom(ICurve geom) {
    this.geom = (ILineSegment) geom;
  }

  @Override
  public void setGraph(IGraph graph) {
    this.spanningTree = (MinSpanningTree) graph;
  }

  @Override
  public void setInitialNode(INode node) {
    this.firstNode = (MSTreeNode) node;
  }

  @Override
  public void setWeight(double weight) {
    this.weight = weight;
  }

}
