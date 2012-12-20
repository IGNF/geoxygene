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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;

/*
 * ###### IGN / CartAGen ###### Title: Arc Description: An arc of a Graph
 * object. Author: G. Touya Version: 1.0 Changes: 1.0 (27/02/10) : creation
 */

public class Edge implements IEdge {
  private IGraph graph;
  private double weight;
  private INode initialNode, finalNode;
  private Set<IFeature> geoObjects;
  private int id;
  private ICurve geom;

  private static AtomicInteger counter = new AtomicInteger();

  @Override
  public double getWeight() {
    return this.weight;
  }

  @Override
  public void setWeight(double weight) {
    this.weight = weight;
  }

  @Override
  public boolean isOriented() {
    return this.graph.isOriented();
  }

  @Override
  public INode getInitialNode() {
    return this.initialNode;
  }

  @Override
  public void setInitialNode(INode initialNode) {
    this.initialNode = initialNode;
  }

  @Override
  public INode getFinalNode() {
    return this.finalNode;
  }

  @Override
  public void setFinalNode(INode finalNode) {
    this.finalNode = finalNode;
  }

  @Override
  public Set<IFeature> getGeoObjects() {
    return this.geoObjects;
  }

  @Override
  public void setGeoObjects(Set<IFeature> geoObjects) {
    this.geoObjects = geoObjects;
  }

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public ICurve getGeom() {
    return this.geom;
  }

  @Override
  public void setGeom(ICurve geom) {
    this.geom = geom;
  }

  @Override
  public IGraph getGraph() {
    return this.graph;
  }

  @Override
  public void setGraph(IGraph graph) {
    this.graph = graph;
  }

  public Edge(IGraph graph, INode initialNode, INode finalNode,
      Set<IFeature> geoObjects, ICurve geom) {
    super();
    this.graph = graph;
    this.initialNode = initialNode;
    this.finalNode = finalNode;
    this.geoObjects = geoObjects;
    this.geom = geom;
    this.weight = geom.length();
    this.id = graph.getId() * 100000 + Edge.counter.incrementAndGet();
  }

  public Edge(INode initialNode, INode finalNode) {
    super();
    this.initialNode = initialNode;
    this.finalNode = finalNode;
    this.geoObjects = new HashSet<IFeature>();
    this.id = 100000 + Edge.counter.incrementAndGet();
  }

  @Override
  public Set<INode> getNodes() {
    Set<INode> nodes = new HashSet<INode>();
    nodes.add(this.initialNode);
    nodes.add(this.finalNode);
    return nodes;
  }

  @Override
  public boolean equals(Object obj) {
    Edge autre = (Edge) obj;
    if (!autre.geom.equals(this.geom)) {
      return false;
    }
    if (!autre.initialNode.equals(this.initialNode)) {
      return false;
    }
    if (!autre.finalNode.equals(this.finalNode)) {
      return false;
    }
    if (!autre.geoObjects.equals(this.geoObjects)) {
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
   * Get the arcs following this after one of this' node passed as input of the
   * method. Takes the orientation into account.
   * @param n : the node of the arc that is followed
   * @return
   */
  public Set<IEdge> getNextArcs(INode n) {
    Set<IEdge> edges = new HashSet<IEdge>();
    edges.addAll(n.getEdgesOut());
    if (!this.isOriented()) {
      edges.addAll(n.getEdgesIn());
    }
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
    edges.addAll(this.getNextArcs(this.initialNode));
    edges.addAll(this.getNextArcs(this.finalNode));
    return edges;
  }
}
