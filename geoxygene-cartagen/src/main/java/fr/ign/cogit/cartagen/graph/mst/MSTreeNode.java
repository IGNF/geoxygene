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

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.graph.IEdge;
import fr.ign.cogit.cartagen.graph.IGraph;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.cartagen.graph.Node;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/*
 * ###### IGN / CartAGen ###### Title: MSTreeNode Description: A node of a
 * minimum spanning tree Author: J. Renard Version: 1.0 Changes: 1.0 (08/11/10)
 * : creation
 */

public class MSTreeNode extends Node {

  // The ID of the node
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

  // The spanning tree the node belongs to
  private MinSpanningTree spanningTree;

  public MinSpanningTree getSpanningTree() {
    return this.spanningTree;
  }

  public void setSpanningTree(MinSpanningTree spanningTree) {
    this.spanningTree = spanningTree;
  }

  // The edges connecting the node
  private HashSet<MSTreeEdge> edges;

  @Override
  public Set<IEdge> getEdges() {
    Set<IEdge> set = new HashSet<IEdge>();
    set.addAll(this.edgesIn);
    set.addAll(this.edgesOut);
    return set;
  }

  private Set<IEdge> edgesIn;
  private Set<IEdge> edgesOut;

  @Override
  public boolean equals(Object obj) {
    MSTreeNode autre = (MSTreeNode) obj;
    try {
      if (!autre.getGeoObjects().equals(this.getGeoObjects())) {
        return false;
      }
    } catch (Exception e) {
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

  /**
   * Constructor
   * @param geoObj
   * @param spanningTree
   */
  public MSTreeNode(IGeneObj geoObj, MinSpanningTree spanningTree) {
    super(new GM_Point(geoObj.getGeom().centroid()));
    this.setGraph(spanningTree);
    Set<IFeature> geoObjs = new HashSet<IFeature>();
    geoObjs.add(geoObj);
    this.setGeoObjects(geoObjs);
    this.spanningTree = spanningTree;
    this.edges = new HashSet<MSTreeEdge>();
    this.id = spanningTree.getId() * 100000
        + MSTreeNode.counter.incrementAndGet();
  }

  /**
   * Constructor
   * @param geoObj
   * @param spanningTree
   */
  public MSTreeNode(IFeature geoObj, MinSpanningTree spanningTree) {
    super(new GM_Point(geoObj.getGeom().centroid()));
    this.setGraph(spanningTree);
    Set<IFeature> geoObjs = new HashSet<IFeature>();
    geoObjs.add(geoObj);
    this.setGeoObjects(geoObjs);
    this.spanningTree = spanningTree;
    this.edges = new HashSet<MSTreeEdge>();
    this.id = spanningTree.getId() * 100000
        + MSTreeNode.counter.incrementAndGet();
  }

  /**
   * Returns the next nodes of the tree according to connected edges
   * @return
   */

  @Override
  public Set<INode> getNextNodes() {
    Set<INode> nodesNext = new HashSet<INode>();

    // search through all the arcs connected to this
    for (IEdge a : this.getEdges()) {
      Set<INode> nodes = a.getNodes();
      nodes.remove(this);
      nodesNext.addAll(nodes);
    }
    return nodesNext;
  }

  /**
   * Returns the degree of the node
   * @return
   */

  @Override
  public int getDegree() {
    return this.edges.size();
  }

  /**
   * Returns the distance between two tree nodes according to the geometry of
   * their attached GeneObj
   * @param node
   * @return
   */

  public double getDistance(MSTreeNode node) {
    return this.getGeoObjects().iterator().next().getGeom()
        .distance(node.getGeoObjects().iterator().next().getGeom());
  }

  @Override
  public Set<IEdge> getEdgesIn() {
    return this.edgesIn;
  }

  @Override
  public Set<IEdge> getEdgesOut() {
    return this.edgesOut;
  }

  @Override
  public IGraph getGraph() {
    return this.spanningTree;
  }

}
