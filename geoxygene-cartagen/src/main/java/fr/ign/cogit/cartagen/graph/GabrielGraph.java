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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.graph.triangulation.ITriangulation;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;
import fr.ign.cogit.geoxygene.util.index.Tiling;

public class GabrielGraph extends Graph {

  public GabrielGraph(String name, boolean oriented) {
    super(name, oriented);
  }

  public GabrielGraph(String name, boolean oriented, Set<IFeature> features) {
    super(name, oriented);
    buildGabrielGraph(features);
  }

  public GabrielGraph(String name, boolean oriented, ITriangulation triGraph) {
    super(name, oriented);
    buildGabrielGraphFromTriangulation(triGraph);
  }

  /**
   * Builds a gabriel graph from a previously computed Delaunay Triangulation.
   * This method is much faster than the standard method, particularly when the
   * Delaunay triangulation computation is fast.
   * @param triGraph
   */
  public void buildGabrielGraphFromTriangulation(ITriangulation triGraph) {

    // first create the nodes of the graph from the triangulation nodes
    Map<INode, INode> nodesMap = new HashMap<INode, INode>();
    for (INode triNode : triGraph.getNodes()) {

      // build the node in 'this' graph
      INode node = new Node(triNode.getGeoObjects(), triNode.getGeom(), this);
      this.getNodes().add(node);
      nodesMap.put(triNode, node);
    }

    // now loop on triangulation edges to see which are kept and which are
    // dropped
    for (IEdge triEdge : triGraph.getEdges()) {
      // get the neighbour nodes of this edge
      HashSet<INode> neighbours = new HashSet<INode>();
      neighbours.addAll(triEdge.getInitialNode().getNextNodes());
      neighbours.addAll(triEdge.getFinalNode().getNextNodes());
      neighbours.remove(triEdge.getInitialNode());
      neighbours.remove(triEdge.getFinalNode());
      // build the circle for the Gabriel Graph rule
      IPolygon circle = buildGabrielCircle(triEdge.getInitialNode()
          .getPosition(), triEdge.getFinalNode().getPosition());
      // check that no neighbour belong to the circle
      boolean isGabriel = true;
      for (INode neighbour : neighbours) {
        if (circle.contains(neighbour.getGeom())) {
          isGabriel = false;
          break;
        }
      }

      if (!isGabriel)
        continue;

      // now copy the triangulation edge into the Gabriel Graph
      INode node = nodesMap.get(triEdge.getInitialNode());
      INode neigh = nodesMap.get(triEdge.getFinalNode());
      IDirectPosition pt1 = triEdge.getInitialNode().getPosition();
      IDirectPosition pt2 = triEdge.getFinalNode().getPosition();
      IEdge edge = new Edge(this, node, neigh, null,
          new GM_LineString(pt1, pt2));
      this.getEdges().add(edge);
      node.addEdgeOut(edge);
      neigh.addEdgeIn(edge);
    }
  }

  public void buildGabrielGraph(Set<IFeature> features) {

    Set<Node> nodes = new HashSet<Node>();
    HashSet<Node> neighbours = new HashSet<Node>();
    IFeatureCollection<IFeature> nodeFeats = new FT_FeatureCollection<IFeature>();

    // first create the nodes of the graph from the input objects
    for (IFeature feat : features) {
      HashSet<IFeature> geoObjects = new HashSet<IFeature>();
      geoObjects.add(feat);
      // build the node in 'this' graph
      Node node = new Node(geoObjects, feat.getGeom().centroid().toGM_Point(),
          this);
      this.getNodes().add(node);
      nodes.add(node);
      neighbours.add(node);
      nodeFeats.add(new DefaultFeature(node.getGeom()));
    }

    // build a spatial index on the feature collection
    nodeFeats.initSpatialIndex(Tiling.class, true);

    // loop on the nodes set
    for (Node node : nodes) {
      // remove node from the neighbours set
      neighbours.remove(node);
      // get the node position
      IDirectPosition pt1 = node.getGeom().getPosition();
      IPolygon biggest = null;
      // loop on the neighbours
      for (Node neigh : neighbours) {
        // get the neighbour position
        IDirectPosition pt2 = neigh.getGeom().getPosition();

        // test if a RNG edge has to be built between these two nodes
        IPolygon circle = buildGabrielCircle(pt1, pt2);
        // heuristic to avoid to spatial queries
        if (biggest != null)
          if (biggest.contains(circle))
            continue;
        if (!ggBuildEdge(circle, nodeFeats)) {
          if (biggest == null)
            biggest = circle;
          else if (circle.contains(biggest))
            biggest = circle;
          continue;
        }

        // create the new edge
        IEdge edge = new Edge(this, node, neigh, null, new GM_LineString(pt1,
            pt2));
        this.getEdges().add(edge);
        node.addEdgeOut(edge);
        neigh.addEdgeIn(edge);
      }
    }
  }

  /**
   * return true if there is no other node inside the circle centred in the
   * middle of the two nodes and with a radius equal to half the distance
   * between the two nodes.
   * @param coord1
   * @param coord2
   * @return
   */
  private boolean ggBuildEdge(IPolygon circle,
      IFeatureCollection<IFeature> nodeFeats) {
    Collection<IFeature> querySet = nodeFeats.select(circle);
    // count the nodes in the collection
    if (querySet.size() > 2)
      return false;

    return true;
  }

  private IPolygon buildGabrielCircle(IDirectPosition pt1, IDirectPosition pt2) {
    // initialisation
    double epsilon = 0.1;
    // compute the distance between the two points
    double dist = pt1.distance2D(pt2);
    // get the middle point
    IDirectPosition middle = new DirectPosition((pt1.getX() + pt2.getX()) / 2,
        (pt1.getY() + pt2.getY()) / 2);
    // build a circle around the middle point
    IPolygon circle = GeometryFactory.buildCircle(middle, dist / 2 + epsilon,
        40);
    return circle;
  }
}
