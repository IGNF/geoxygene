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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

/*
 * ###### IGN / CartAGen ###### Title: Node Description: A node of a Graph
 * object. Author: G. Touya Version: 1.0 Changes: 1.0 (27/02/10) : creation
 */

public class Node implements INode {

  private int id;
  private Set<IFeature> geoObjects;
  private IGraphLinkableFeature graphLinkableFeature;
  private Set<IEdge> edgesIn, edgesOut;
  private IPoint geom;
  private IGraph graph;
  private double proxiCentrality, betweenCentrality;
  private IDirectPosition positionIni;

  private static AtomicInteger counter = new AtomicInteger();

  @Override
  public Set<IFeature> getGeoObjects() {
    return this.geoObjects;
  }

  @Override
  public void setGeoObjects(Set<IFeature> geoObjects) {
    this.geoObjects = geoObjects;
  }

  @Override
  public IGraphLinkableFeature getGraphLinkableFeature() {
    return this.graphLinkableFeature;
  }

  @Override
  public void setGraphLinkableFeature(IGraphLinkableFeature feature) {
    this.graphLinkableFeature = feature;
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
  public IPoint getGeom() {
    return this.geom;
  }

  @Override
  public void setGeom(IPoint geom) {
    this.geom = geom;
  }

  @Override
  public IDirectPosition getPosition() {
    return this.geom.getPosition();
  }

  @Override
  public IDirectPosition getPositionIni() {
    return this.positionIni;
  }

  @Override
  public Set<IEdge> getEdgesIn() {
    return this.edgesIn;
  }

  @Override
  public void setEdgesIn(Set<IEdge> edgesIn) {
    this.edgesIn = edgesIn;
  }

  @Override
  public Set<IEdge> getEdgesOut() {
    return this.edgesOut;
  }

  @Override
  public void setEdgesOut(Set<IEdge> edgesOut) {
    this.edgesOut = edgesOut;
  }

  @Override
  public IGraph getGraph() {
    return this.graph;
  }

  @Override
  public void setGraph(IGraph graph) {
    this.graph = graph;
  }

  public Node(Set<IFeature> geoObjects, IPoint geom, IGraph graph) {
    super();
    this.geoObjects = geoObjects;
    this.geom = geom;
    this.positionIni = new DirectPosition(geom.getPosition().getX(), geom
        .getPosition().getY(), geom.getPosition().getZ());
    this.graph = graph;
    this.edgesIn = new HashSet<IEdge>();
    this.edgesOut = new HashSet<IEdge>();
    this.id = graph.getId() * 100000 + Node.counter.incrementAndGet();
  }

  public Node(IPoint geom) {
    super();
    this.geoObjects = new HashSet<IFeature>();
    this.geom = geom;
    this.positionIni = new DirectPosition(geom.getPosition().getX(), geom
        .getPosition().getY(), geom.getPosition().getZ());
    this.edgesIn = new HashSet<IEdge>();
    this.edgesOut = new HashSet<IEdge>();
    this.id = 100000 + Node.counter.incrementAndGet();
  }

  @Override
  public boolean equals(Object obj) {
    Node autre = (Node) obj;
    if (!autre.geom.equals(this.geom)) {
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
    return "node " + this.id;
  }

  @Override
  public Set<IEdge> getEdges() {
    Set<IEdge> edges = new HashSet<IEdge>();
    edges.addAll(this.edgesIn);
    edges.addAll(this.edgesOut);
    return edges;
  }

  public List<IEdge> getEdgesList() {
    List<IEdge> edges = new ArrayList<IEdge>();
    edges.addAll(this.edgesIn);
    edges.addAll(this.edgesOut);
    return edges;
  }

  @Override
  public Set<INode> getNextNodes() {
    Set<INode> nodesNext = new HashSet<INode>();

    // find if the graph is oriented
    boolean orientation = this.getGraph().isOriented();

    // if the graph is oriented, search only through outing arcs
    if (orientation) {
      for (IEdge a : this.getEdgesOut()) {
        nodesNext.add(a.getFinalNode());
      }
    } else {
      // search through all the arcs connected to this
      for (IEdge a : this.getEdges()) {
        Set<INode> nodes = a.getNodes();
        nodes.remove(this);
        nodesNext.addAll(nodes);
      }
    }
    return nodesNext;
  }

  @Override
  public int getDegree() {
    int nbNodes = 0;
    nbNodes += this.edgesIn.size();
    nbNodes += this.edgesOut.size();
    return nbNodes;
  }

  /**
   * <p>
   * Get this' neigbouring nodes along with the shortest arc to connect this to
   * the node.<br>
   * 
   * @return HashMap<Node, Arc> : the next nodes as keys and the shortest arcs
   *         as values.
   */
  @Override
  public Map<INode, IEdge> getNeighbourEdgeNode() {

    // initialisation
    Map<INode, IEdge> mapVoisins = new HashMap<INode, IEdge>();

    // get all the next nodes
    Set<INode> nextNodes = this.getNextNodes();
    // iterate through the next nodes
    for (INode node : nextNodes) {
      // get the arcs between the two nodes
      Set<IEdge> edges = this.getEdgesDirectlyBetween(node);
      // iterate through this set
      double minLong = Double.MAX_VALUE;
      for (IEdge edge : edges) {
        if (edge.getWeight() < minLong) {
          mapVoisins.put(node, edge);
          minLong = edge.getWeight();
        }
      }
    }
    return mapVoisins;
  }

  /**
   * Get all the direct arcs between this node and another node of the graph.
   * @param node : the other node of the graph
   * @return HashSet<Arc> : the set of the arcs directly between the nodes
   */
  public Set<IEdge> getEdgesDirectlyBetween(INode node) {
    Set<IEdge> edges = new HashSet<IEdge>();
    edges.addAll(this.getEdges());
    edges.retainAll(node.getEdges());
    return edges;
  }

  @Override
  public void addEdgeIn(IEdge e) {
    this.edgesIn.add(e);
  }

  @Override
  public void addEdgeOut(IEdge e) {
    this.edgesOut.add(e);
  }

  @Override
  public double getProximityCentrality() {
    return this.proxiCentrality;
  }

  @Override
  public double getBetweenCentrality() {
    return this.betweenCentrality;
  }
}
