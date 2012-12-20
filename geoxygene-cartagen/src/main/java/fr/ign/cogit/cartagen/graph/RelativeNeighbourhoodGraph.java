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
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.GeometryProximity;
import fr.ign.cogit.geoxygene.util.index.Tiling;

public class RelativeNeighbourhoodGraph extends Graph {

  public RelativeNeighbourhoodGraph(String name, boolean oriented) {
    super(name, oriented);
  }

  public RelativeNeighbourhoodGraph(String name, boolean oriented,
      Set<IFeature> features) {
    super(name, oriented);
    buildRNGraph(features);
  }

  /**
   * Literal implementation of the relative neighbourhood graph computation, it
   * is not efficient at all. Should only be used for small sets like the
   * buildings in a block.
   * @param features
   */
  public void buildRNGraph(Set<IFeature> features) {

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
      // remover node from the neighbours set
      neighbours.remove(node);
      // get the node position
      IDirectPosition pt1 = node.getGeom().getPosition();
      // loop on the neighbours
      for (Node neigh : neighbours) {
        // get the neighbour position
        IDirectPosition pt2 = neigh.getGeom().getPosition();

        // test if a RNG edge has to be built between these two nodes
        if (!rngBuildEdge1(pt1, pt2, nodeFeats))
          continue;

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
   * Same as buildRNGraph but the graph is built according to the real geometry
   * of the features and not just the distances between nodes.
   * @param features
   */
  public void buildRNGraph2(Set<IFeature> features) {

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
      nodeFeats.add(feat);
    }

    // build a spatial index on the feature collection
    nodeFeats.initSpatialIndex(Tiling.class, true);
    // loop on the nodes set
    for (Node node : nodes) {
      // remover node from the neighbours set
      neighbours.remove(node);
      // get the node position
      IDirectPosition pt1 = node.getGeom().getPosition();
      // loop on the neighbours
      for (Node neigh : neighbours) {
        // get the neighbour position
        IDirectPosition pt2 = neigh.getGeom().getPosition();

        // test if a RNG edge has to be built between these two nodes
        if (!rngBuildEdge2(node, neigh, nodeFeats))
          continue;

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
   * return true if there is no other node inside the circles centred on the two
   * nodes and with a radius equal to the distance between the two nodes
   * @param coord1
   * @param coord2
   * @return
   */
  public boolean rngBuildEdge1(IDirectPosition node1, IDirectPosition node2,
      IFeatureCollection<IFeature> nodeFeats) {
    // initialisation
    double epsilon = 0.1;
    // get the geo object from the node
    // compute the distance between the two features related to the nodes
    double dist = node1.distance2D(node2);
    IPolygon geom1 = GeometryFactory.buildCircle(node1, dist + epsilon, 40);
    IPolygon geom2 = GeometryFactory.buildCircle(node2, dist + epsilon, 40);
    Collection<IFeature> querySet1 = nodeFeats.select(geom1);
    Collection<IFeature> querySet2 = nodeFeats.select(geom2);
    querySet1.retainAll(querySet2);
    // count the nodes in the collection
    if (querySet1.size() > 2)
      return false;

    return true;
  }

  /**
   * return true if there is no other node inside the circles centred on the two
   * nodes and with a radius equal to the distance between the two nodes
   * @param coord1
   * @param coord2
   * @return
   */
  public boolean rngBuildEdge2(Node node1, Node node2,
      IFeatureCollection<IFeature> nodeFeats) {
    // initialisation
    double epsilon = 0.1;
    // get the geo object from the node
    IFeature geo1 = node1.getGeoObjects().iterator().next();
    IFeature geo2 = node2.getGeoObjects().iterator().next();
    // compute the distance between the two features related to the nodes
    double dist = new GeometryProximity(geo1.getGeom(), geo2.getGeom())
        .getDistance();
    IPolygon geom1 = GeometryFactory.buildCircle(node1.getGeom().getPosition(),
        dist + epsilon, 40);
    IPolygon geom2 = GeometryFactory.buildCircle(node2.getGeom().getPosition(),
        dist + epsilon, 40);
    Collection<IFeature> querySet1 = nodeFeats.select(geom1);
    Collection<IFeature> querySet2 = nodeFeats.select(geom2);
    querySet1.retainAll(querySet2);
    // count the nodes in the collection
    if (querySet1.size() > 2)
      return false;

    return true;
  }

  /**
   * Builds a RN graph from a previously computed Delaunay Triangulation. This
   * method is much faster than the standard method, particularly when the
   * Delaunay triangulation computation is fast.
   * @param triGraph
   */
  public void buildRNGraphFromTriangulation(ITriangulation triGraph) {

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
      IPolygon[] circles = buildRNCircles(triEdge.getInitialNode()
          .getPosition(), triEdge.getFinalNode().getPosition());
      // check that no neighbour belong to the circle
      boolean isRN = true;
      for (INode neighbour : neighbours) {
        if (circles[0].contains(neighbour.getGeom())
            & circles[1].contains(neighbour.getGeom())) {
          isRN = false;
          break;
        }
      }

      if (!isRN)
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

  private IPolygon[] buildRNCircles(IDirectPosition position1,
      IDirectPosition position2) {
    // initialisation
    double epsilon = 0.1;
    // get the geo object from the node
    // compute the distance between the two features related to the nodes
    double dist = position1.distance2D(position2);
    IPolygon geom1 = GeometryFactory.buildCircle(position1, dist + epsilon, 40);
    IPolygon geom2 = GeometryFactory.buildCircle(position2, dist + epsilon, 40);
    return new IPolygon[] { geom1, geom2 };
  }

}
