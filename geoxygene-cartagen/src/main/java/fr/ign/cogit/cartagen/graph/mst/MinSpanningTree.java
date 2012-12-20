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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.graph.Graph;
import fr.ign.cogit.cartagen.graph.IEdge;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;

/*
 * ###### IGN / CartAGen ###### Title: MinSpanningTree Description: A minimum
 * spanning tree with its arcs and nodes and methods Author: J. Renard Version:
 * 1.0 Changes: 1.0 (08/11/10) : creation
 */

public class MinSpanningTree extends Graph {

  // The ID of the tree
  private int id;

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  private static AtomicInteger counter = new AtomicInteger();

  // The edges of the tree
  private Set<IEdge> edges;

  @Override
  public Set<IEdge> getEdges() {
    return this.edges;
  }

  public void addEdge(MSTreeEdge e) {
    this.edges.add(e);
  }

  // The nodes of the tree
  private Set<INode> nodes;

  @Override
  public Set<INode> getNodes() {
    return this.nodes;
  }

  @Override
  public boolean equals(Object arg0) {
    MinSpanningTree autre = (MinSpanningTree) arg0;
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

  /**
   * Default constructor
   */

  public MinSpanningTree() {
    super("MST", false); //$NON-NLS-1$
    this.id = MinSpanningTree.counter.incrementAndGet();
    this.edges = new HashSet<IEdge>();
    this.nodes = new HashSet<INode>();
  }

  /**
   * Constructor of a spanning tree with one single node
   * @param geoObj
   */

  public MinSpanningTree(IGeneObj geoObj) {
    super("MST", false); //$NON-NLS-1$
    this.id = MinSpanningTree.counter.incrementAndGet();
    this.edges = new HashSet<IEdge>();
    this.nodes = new HashSet<INode>();
    this.nodes.add(new MSTreeNode(geoObj, this));
  }

  /**
   * Constructor of a spanning tree with one single node
   * @param geoObj
   */

  public MinSpanningTree(IFeature geoObj) {
    super("MST", false); //$NON-NLS-1$
    this.id = MinSpanningTree.counter.incrementAndGet();
    this.edges = new HashSet<IEdge>();
    this.nodes = new HashSet<INode>();
    this.nodes.add(new MSTreeNode(geoObj, this));
  }

  /**
   * Builds the minimum spanning trees conecting a collection of GeneObjs
   * @param geoObjs : the GeneObjs to build the trees on
   * @param distanceMax : the maximum distance for edges of trees
   */

  public static ArrayList<MinSpanningTree> buildMinSpanningTree(
      Collection<IFeature> geoObjs, double distanceMax) {

    ArrayList<MinSpanningTree> recursiveTrees = new ArrayList<MinSpanningTree>();

    // Particular cases
    if (geoObjs.size() == 0) {
      return null;
    }
    if (geoObjs.size() == 1) {
      recursiveTrees.add(new MinSpanningTree(geoObjs.iterator().next()));
      return recursiveTrees;
    }

    // Construction of single-node-spanning trees
    for (IFeature geoObj : geoObjs) {
      recursiveTrees.add(new MinSpanningTree(geoObj));
    }

    // Recursive connection of spanning trees to have one single tree at the end
    while (recursiveTrees.size() > 1) {
      // computation of the next edge to be built
      MSTreeEdge newEdge = MinSpanningTree.getNextEdgeToBuild(recursiveTrees);
      if (newEdge == null || newEdge.getSpanningTree() == null) {
        break;
      }
      if (newEdge.getLengthValue() > distanceMax) {
        break;
      }
      newEdge.getSpanningTree().addEdge(newEdge);
      // merging of the two trees connected by this edge
      MinSpanningTree treeToBeMerged;
      if (newEdge.getInitialNode().getGraph().equals(newEdge.getSpanningTree())) {
        treeToBeMerged = (MinSpanningTree) newEdge.getFinalNode().getGraph();
      } else if (newEdge.getFinalNode().getGraph().equals(
          newEdge.getSpanningTree())) {
        treeToBeMerged = (MinSpanningTree) newEdge.getInitialNode().getGraph();
      } else {
        treeToBeMerged = (MinSpanningTree) newEdge.getFinalNode().getGraph();
        System.out.println("chaud!!"); //$NON-NLS-1$
      }
      for (IEdge edge : treeToBeMerged.getEdges()) {
        edge.setGraph(newEdge.getSpanningTree());
      }
      for (INode node : treeToBeMerged.getNodes()) {
        node.setGraph(newEdge.getSpanningTree());
      }
      newEdge.getSpanningTree().getNodes().addAll(treeToBeMerged.getNodes());
      newEdge.getSpanningTree().getEdges().addAll(treeToBeMerged.getEdges());
      recursiveTrees.remove(treeToBeMerged);

    }

    return recursiveTrees;

  }

  /**
   * Returns the next edge to build to join the best two spanning trees in a
   * whole collection
   * @param recursiveTrees : list of initial spanning trees
   * @return
   */

  private static MSTreeEdge getNextEdgeToBuild(
      ArrayList<MinSpanningTree> recursiveTrees) {
    // Initialisation
    MSTreeEdge newEdge = new MSTreeEdge();
    newEdge.setLengthValue(Double.MAX_VALUE);
    // Loop on all spanning trees
    for (int i = 0; i < recursiveTrees.size(); i++) {
      for (int j = i + 1; j < recursiveTrees.size(); j++) {
        MinSpanningTree tree1 = recursiveTrees.get(i);
        MinSpanningTree tree2 = recursiveTrees.get(j);
        MSTreeEdge edge = tree1.getConnectingEdge(tree2);
        if (edge.getLengthValue() < newEdge.getLengthValue()) {
          if (edge.getInitialNode().getGeoObjects().iterator().next() instanceof IBuilding) {
            newEdge = edge;
          }
        }
      }
    }
    return newEdge;
  }

  /**
   * Returns the best edge to connect this with another minimal spanning tree
   * @param tree
   * @return
   */

  public MSTreeEdge getConnectingEdge(MinSpanningTree tree) {
    // Initialisation
    MSTreeEdge connectingEdge = new MSTreeEdge();
    connectingEdge.setLengthValue(Double.MAX_VALUE);
    // Loop on all nodes of each tree
    for (INode node1 : this.getNodes()) {
      for (INode node2 : tree.getNodes()) {
        double lengthValue = ((MSTreeNode) node1)
            .getDistance((MSTreeNode) node2);
        if (lengthValue >= connectingEdge.getLengthValue()) {
          continue;
        }
        ILineSegment geom = new GM_LineSegment(node1.getGeoObjects().iterator()
            .next().getGeom().centroid(), node2.getGeoObjects().iterator()
            .next().getGeom().centroid());
        connectingEdge = new MSTreeEdge(this, (MSTreeNode) node1,
            (MSTreeNode) node2, geom, lengthValue);
      }
    }
    return connectingEdge;
  }

  /**
   * Returns the tree node possibly attached to a GeneObj
   * @param geoObj : the GeneObj tested
   * @return
   */

  public MSTreeNode getNodeFromGeoObj(IGeneObj geoObj) {
    for (INode n : this.nodes) {
      if (n.getGeoObjects().equals(geoObj)) {
        return (MSTreeNode) n;
      }
    }
    return null;
  }

  @Override
  public void setEdges(Set<IEdge> arcs) {
    this.edges = arcs;
  }

  @Override
  public void setNodes(Set<INode> nodes) {
    this.nodes = nodes;
  }

}
