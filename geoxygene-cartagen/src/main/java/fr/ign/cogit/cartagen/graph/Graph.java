/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.cartagen.core.genericschema.network.INetworkNode;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Direction;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;

/*
 * ###### IGN / CartAGen ###### Title: Graph Description: A graph with its arcs
 * and nodes and methods to build from Gothic and manipulate Author: G. Touya
 * Version: 1.0 Changes: 1.0 (27/02/10) : creation
 */
public class Graph implements IGraph {

  private Set<IEdge> edges;
  private Set<INode> nodes;
  private Set<GraphPath> paths;
  private String name;
  private boolean oriented;
  private int id;

  private static AtomicInteger counter = new AtomicInteger();

  @Override
  public Set<IEdge> getEdges() {
    return this.edges;
  }

  @Override
  public void setEdges(Set<IEdge> arcs) {
    this.edges = arcs;
  }

  @Override
  public Set<INode> getNodes() {
    return this.nodes;
  }

  @Override
  public void setNodes(Set<INode> nodes) {
    this.nodes = nodes;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean isOriented() {
    return this.oriented;
  }

  @Override
  public void setOriented(boolean oriented) {
    this.oriented = oriented;
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
  public Set<GraphPath> getPaths() {
    return this.paths;
  }

  @Override
  public void setPaths(Set<GraphPath> paths) {
    this.paths = paths;
  }

  @Override
  public boolean equals(Object arg0) {
    Graph autre = (Graph) arg0;
    if (!autre.name.equals(this.name)) {
      return false;
    }
    if (!autre.edges.equals(this.edges)) {
      return false;
    }
    if (!autre.nodes.equals(this.nodes)) {
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
    // TODO Auto-generated method stub
    return super.toString();
  }

  public Graph(String name, boolean oriented) {
    this.name = name;
    this.id = Graph.counter.incrementAndGet();
    this.oriented = oriented;
    this.edges = new HashSet<IEdge>();
    this.nodes = new HashSet<INode>();
    this.paths = new HashSet<GraphPath>();
  }

  /**
   * A constructor based on linear features that implements the standardised
   * COGIT network element interface.
   * @param name
   * @param oriented
   * @param linearFeatures
   */
  public Graph(String name, boolean oriented, Set<ArcReseau> linearFeatures) {
    this.name = name;
    this.id = Graph.counter.incrementAndGet();
    this.oriented = oriented;
    this.edges = new HashSet<IEdge>();
    this.nodes = new HashSet<INode>();
    this.paths = new HashSet<GraphPath>();

    // build a map of nodes with the geoObject node as key and the graph node as
    // value
    Map<NoeudReseau, INode> mapNodes = new HashMap<NoeudReseau, INode>();
    // loop on the linear features used to build the graph
    for (ArcReseau arc : linearFeatures) {

      // get the geometry of the linear feature
      ICurve geom = arc.getGeom();
      // build a set to store the geo objects of the future edge
      Set<IFeature> setRwos = new HashSet<IFeature>();
      setRwos.add(arc);

      INode initialNode, finalNode;
      // get the nodes of the geo objects
      NoeudReseau ini = arc.getNoeudInitial();
      NoeudReseau fin = arc.getNoeudFinal();

      // test if the initial node has already been treated
      if (mapNodes.containsKey(ini)) {
        // get the node from the map
        initialNode = mapNodes.get(ini);
      } else {
        // build a new node object
        IPoint geomNode = ini.getGeom();
        HashSet<IFeature> geoObjs = new HashSet<IFeature>();
        geoObjs.add(ini);
        initialNode = new Node(geoObjs, geomNode, this);
        this.nodes.add(initialNode);
        // add the initial node to the map of nodes
        mapNodes.put(ini, initialNode);
      }

      // test if the final node has already been treated
      if (mapNodes.containsKey(fin)) {
        // get the node from the map
        finalNode = mapNodes.get(fin);
      } else {
        // build a new node object
        IPoint geomNode = fin.getGeom();
        HashSet<IFeature> geoObjs = new HashSet<IFeature>();
        geoObjs.add(fin);
        finalNode = new Node(geoObjs, geomNode, this);
        this.nodes.add(finalNode);
        // add the final node to the map of nodes
        mapNodes.put(fin, finalNode);
      }

      // build the edge object and update the references between nodes and edges
      if (oriented && (arc.getDirection().equals(Direction.INDIRECT))) {
        Edge e = new Edge(this, finalNode, initialNode, setRwos, geom);
        finalNode.addEdgeOut(e);
        initialNode.addEdgeIn(e);
        this.edges.add(e);
      } else if (oriented
          && (arc.getDirection().equals(Direction.BIDIRECTIONNEL))) {
        Edge e1 = new Edge(this, finalNode, initialNode, setRwos, geom);
        finalNode.addEdgeOut(e1);
        initialNode.addEdgeIn(e1);
        Edge e2 = new Edge(this, initialNode, finalNode, setRwos, geom);
        initialNode.addEdgeOut(e2);
        finalNode.addEdgeIn(e2);
        this.edges.add(e1);
        this.edges.add(e2);
      } else {
        Edge e = new Edge(this, initialNode, finalNode, setRwos, geom);
        initialNode.addEdgeOut(e);
        finalNode.addEdgeIn(e);
        this.edges.add(e);
      }
    }
  }

  /**
   * A constructor based on linear features that implements the standardised
   * COGIT network element interface.
   * @param name
   * @param oriented
   * @param linearFeatures
   */
  public Graph(String name, boolean oriented,
      Collection<INetworkSection> linearFeatures) {
    this.name = name;
    this.id = Graph.counter.incrementAndGet();
    this.oriented = oriented;
    this.edges = new HashSet<IEdge>();
    this.nodes = new HashSet<INode>();
    this.paths = new HashSet<GraphPath>();

    // build a map of nodes with the geoObject node as key and the graph node as
    // value
    HashMap<INetworkNode, Node> mapNodes = new HashMap<INetworkNode, Node>();
    // loop on the linear features used to build the graph
    for (INetworkSection section : linearFeatures) {

      // get the geometry of the linear feature
      ICurve geom = section.getGeom();
      // build a set to store the geo objects of the future edge
      HashSet<IFeature> setRwos = new HashSet<IFeature>();
      setRwos.add(section);

      Node initialNode, finalNode;
      // get the nodes of the geo objects
      INetworkNode ini = section.getInitialNode();
      INetworkNode fin = section.getFinalNode();

      // test if the initial node has already been treated
      if (mapNodes.containsKey(ini)) {
        // get the node from the map
        initialNode = mapNodes.get(ini);
      } else {
        // build a new node object
        IPoint geomNode = ini.getGeom();
        HashSet<IFeature> geoObjs = new HashSet<IFeature>();
        geoObjs.add(ini);
        initialNode = new Node(geoObjs, geomNode, this);
        this.nodes.add(initialNode);
        // add the initial node to the map of nodes
        mapNodes.put(ini, initialNode);
      }

      // test if the final node has already been treated
      if (mapNodes.containsKey(fin)) {
        // get the node from the map
        finalNode = mapNodes.get(fin);
      } else {
        // build a new node object
        IPoint geomNode = fin.getGeom();
        HashSet<IFeature> geoObjs = new HashSet<IFeature>();
        geoObjs.add(fin);
        finalNode = new Node(geoObjs, geomNode, this);
        this.nodes.add(finalNode);
        // add the final node to the map of nodes
        mapNodes.put(fin, finalNode);
      }

      // build the edge object and update the references between nodes and edges
      if (oriented && (section.getDirection().equals(Direction.INDIRECT))) {
        Edge e = new Edge(this, finalNode, initialNode, setRwos, geom);
        finalNode.addEdgeOut(e);
        initialNode.addEdgeIn(e);
        this.edges.add(e);
      } else if (oriented
          && (section.getDirection().equals(Direction.BIDIRECTIONNEL))) {
        Edge e1 = new Edge(this, finalNode, initialNode, setRwos, geom);
        finalNode.addEdgeOut(e1);
        initialNode.addEdgeIn(e1);
        Edge e2 = new Edge(this, initialNode, finalNode, setRwos, geom);
        initialNode.addEdgeOut(e2);
        finalNode.addEdgeIn(e2);
        this.edges.add(e1);
        this.edges.add(e2);
      } else {
        Edge e = new Edge(this, initialNode, finalNode, setRwos, geom);
        initialNode.addEdgeOut(e);
        finalNode.addEdgeIn(e);
        this.edges.add(e);
      }
    }
  }

  /**
   * <p>
   * Compute the shortest path between two nodes of the graph using Dijkstra
   * algorithm. Usable for small graphs as all is computed in memory.<br>
   * @param initialNode : the initial node
   * @param finalNode : the final node.
   * 
   * @return GraphPath : returns a GraphPath object describing the shortest
   *         path.
   * 
   */
  public GraphPath computeDijkstraShortestPath(INode initialNode,
      INode finalNode) {

    // ***************************************
    // ALGORITHM INITIALISATION
    Map<INode, Double> mapNodeWghts = new HashMap<INode, Double>();
    Map<INode, INode> mapNodePrev = new HashMap<INode, INode>();
    Map<INode, IEdge> mapNodePrevArc = new HashMap<INode, IEdge>();
    List<INode> nodeList = new ArrayList<INode>();
    List<IEdge> arcList = new ArrayList<IEdge>();
    Set<INode> treatedNodes = new HashSet<INode>();

    // create a stack for nodes
    Stack<INode> stack = new Stack<INode>();
    nodeList.add(initialNode);
    treatedNodes.add(initialNode);
    mapNodeWghts.put(initialNode, new Double(0.0));
    // get the nodes and arcs neighbouring initialNode
    Map<INode, IEdge> neighbours = initialNode.getNeighbourEdgeNode();
    // iterate through the neighbouring nodes
    for (INode node : neighbours.keySet()) {
      IEdge arc = neighbours.get(node);
      mapNodeWghts.put(node, new Double(arc.getWeight()));
      mapNodePrev.put(node, initialNode);
      mapNodePrevArc.put(node, arc);
      stack.add(node);
    }

    // ***************************************
    // FORWARD STEP OF THE ALGORITHM
    // look for neighbours while the stack is not empty
    while (!stack.empty()) {
      // look for the node with the lowest weight in the map to put it on top of
      // the stack
      Set<INode> copy = new HashSet<INode>();
      copy.addAll(stack);
      double poidsMin = Double.MAX_VALUE;
      for (INode candidat : copy) {
        double poids = mapNodeWghts.get(candidat).doubleValue();
        if (poids < poidsMin) {
          stack.remove(candidat);
          stack.push(candidat);
        }
      }
      // get the top of the stack
      INode nearest = stack.pop();
      // count it as a treated node
      treatedNodes.add(nearest);
      // if it is the final node, break the loop : indeed, if it's the nearest
      // node
      // we won't be able to find a shorter path than this one.
      if (nearest.equals(finalNode)) {
        break;
      }

      // otherwise, look for its neighbours
      neighbours = nearest.getNeighbourEdgeNode();
      // iterate through the neighbouring nodes
      for (INode neighbour : neighbours.keySet()) {
        IEdge arc = neighbours.get(neighbour);
        double neighWght = arc.getWeight();

        // if the node has already been treated, continue
        if (treatedNodes.contains(neighbour)) {
          continue;
        }
        // if the node has already been reached, check if this path is shorter
        if (stack.contains(neighbour)) {
          if (mapNodeWghts.get(neighbour).doubleValue() > (neighWght + mapNodeWghts
              .get(nearest).doubleValue())) {
            // modify the attributes of the shortest path to 'neighbour' node
            mapNodePrev.put(neighbour, nearest);
            mapNodePrevArc.put(neighbour, arc);
            mapNodeWghts.put(neighbour, new Double(neighWght
                + mapNodeWghts.get(nearest).doubleValue()));
          }
          continue;
        }

        // a new node is reached, it has to be initialised
        mapNodeWghts.put(neighbour,
            new Double(neighWght + mapNodeWghts.get(nearest).doubleValue()));
        mapNodePrev.put(neighbour, nearest);
        mapNodePrevArc.put(neighbour, arc);
        // put this node in the stack
        stack.add(neighbour);
      }// for, loop on the neighbours of 'nearest'
    }

    // ***************************************
    // BACKWARD STEP OF THE ALGORITHM
    // test if the finalNode has really been reached (if the initial and final
    // nodes are connex)
    if (!treatedNodes.contains(finalNode)) {
      return null;
    }
    INode next = finalNode;
    nodeList.add(1, finalNode);
    double spWght = 0.0;
    while (true) {
      // get the node before 'next'
      INode previousNode = mapNodePrev.get(next);
      // add the previous arc at the beginning of the list of arcs
      IEdge previousArc = mapNodePrevArc.get(next);
      spWght += previousArc.getWeight();
      arcList.add(0, previousArc);

      // if it is the initial node, break
      if (initialNode.equals(previousNode)) {
        break;
      }
      // otherwise, insert it at the dbeginning of the nodes list
      nodeList.add(1, previousNode);
      next = previousNode;

    }// while (true) : phase arrière

    GraphPath sp = new GraphPath(spWght, arcList, nodeList, this);
    return sp;
  }

  public INode getNodeFromGeoObj(IFeature feature) {
    for (INode n : this.nodes) {
      if (n.getGeoObjects().contains(feature)) {
        return n;
      }
    }
    return null;
  }

  public IEdge getEdgeFromGeoObj(IFeature feature) {
    for (IEdge a : this.edges) {
      if (a.getGeoObjects().contains(feature)) {
        return a;
      }
    }
    return null;
  }

  /**
   * <p>
   * Cut the graph into connex parts. The nodes of each connex part are
   * clustered in a set.<br>
   * 
   * @return HashSet : a set of sets of nodes.
   * 
   */
  public Set<HashSet<INode>> cutInConnexParts() {
    // get all the graph nodes in a stack.
    Stack<INode> pile = new Stack<INode>();
    pile.addAll(this.getNodes());
    // initialise the final set
    Set<HashSet<INode>> connexes = new HashSet<HashSet<INode>>();
    // find connex parts until the stack is empty
    while (!pile.empty()) {
      // build a new connex part
      HashSet<INode> partieConn = new HashSet<INode>();
      // pop the stack with a random node of the graph
      INode node = pile.pop();
      partieConn.add(node);
      // get the nodes connected in the graph to node
      Set<INode> noeudsSuiv = node.getNextNodes();
      // while there are next nodes, they are added to the connex part
      while (noeudsSuiv.size() != 0) {
        // on initialise les noeuds suivants pour la prochaine passe
        Set<INode> copie = new HashSet<INode>();
        copie.addAll(noeudsSuiv);
        noeudsSuiv.clear();
        // Iterate through the copy set
        for (INode n : copie) {
          // remove n from the stack
          pile.remove(n);
          // put n into the current connex part
          partieConn.add(n);
          // get the next nodes of n
          Set<INode> suivants = n.getNextNodes();
          // remove partieConn from suivants
          suivants.removeAll(partieConn);
          // add all that remains into noeudsSuiv
          noeudsSuiv.addAll(suivants);
        }// iteration on noeudsSuivants
      }// while noeudsSuiv is not empty

      // add the connex part to the final set
      connexes.add(partieConn);
    }// tant que la pile n'est pas vide

    return connexes;
  }

  public void computeCentralities() {
    this.computeProxiCentrality();
    this.computeBetweenCentrality();
  }

  private void computeBetweenCentrality() {
    // TODO Auto-generated method stub

  }

  private void computeProxiCentrality() {
    // TODO Auto-generated method stub

  }

  /**
   * Get the ratio of nodes in relation to edges
   * @return
   */
  public double nodeEdgeRatio() {
    if (this.edges.size() == 0) {
      return 0.0;
    }
    return this.nodes.size() / this.edges.size();
  }

  @Override
  public Set<IEdge> getEdgesConnectingNodes(INode node1, INode node2) {
    if (node1 == null)
      return new HashSet<IEdge>();
    if (node2 == null)
      return new HashSet<IEdge>();
    Set<IEdge> edges = node1.getEdges();
    edges.retainAll(node2.getEdges());
    return edges;
  }

  @Override
  /**
   * Be careful this implementation is not efficient!
   */
  public INode getNodeAt(IDirectPosition pt) {
    for (INode node : getNodes()) {
      if (node.getPosition().equals(pt, 0.1))
        return node;
    }
    return null;
  }
}
