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

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.GeometryProximity;
import fr.ign.cogit.geoxygene.util.index.Tiling;

public class NearestNeighbourGraph extends Graph {

  public NearestNeighbourGraph(String name, boolean oriented) {
    super(name, oriented);
  }

  public void buildNNGraphCentroid(Set<IFeature> features) {

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
    // loop on the neighbours to find the nearest neighbour
    Node nearest = null;
    double minDist = Double.MAX_VALUE;
    for (Node node : nodes) {
      // get the node position
      IDirectPosition pt1 = node.getGeom().getPosition();
      // loop on the neighbours
      for (Node neigh : neighbours) {
        if (neigh.equals(node))
          continue;
        // get the nearest neighbour position
        IDirectPosition pt2 = neigh.getGeom().getPosition();
        // test if a RNG edge has to be built between these two nodes
        double dist = pt1.distance2D(pt2);
        if (dist >= minDist)
          continue;
        // update the information on the nearest neighbour
        nearest = neigh;
        minDist = dist;
      }
      // get the nearest neighbour position
      IDirectPosition pt2 = nearest.getGeom().getPosition();
      // test if the edge already exists
      if (this.getEdgesConnectingNodes(node, nearest).size() > 0)
        continue;
      // create the new edge
      IEdge edge = new Edge(this, node, nearest, null, new GM_LineString(pt1,
          pt2));
      this.getEdges().add(edge);
      node.addEdgeOut(edge);
      nearest.addEdgeIn(edge);

    }
  }

  /**
   * Same as buildNNGraphCentroid but the graph is built according to the real
   * geometry of the features and not just the distances between nodes.
   * @param features
   */
  public void buildNNGraph(Set<IFeature> features) {

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
      // get the node position
      IDirectPosition pt1 = node.getGeom().getPosition();
      // loop on the neighbours to find the nearest neighbour
      Node nearest = null;
      double minDist = Double.MAX_VALUE;
      for (Node neigh : neighbours) {
        if (neigh.equals(node))
          continue;
        // computes the smallest distance between both features
        IGeometry geom1 = node.getGeoObjects().iterator().next().getGeom();
        IGeometry geom2 = neigh.getGeoObjects().iterator().next().getGeom();
        GeometryProximity proxi = new GeometryProximity(geom1, geom2);
        // test if a RNG edge has to be built between these two nodes
        if (proxi.getDistance() >= minDist)
          continue;
        // update the information on the nearest neighbour
        nearest = neigh;
        minDist = proxi.getDistance();
      }
      // test if the edge already exists
      if (this.getEdgesConnectingNodes(node, nearest).size() > 0)
        continue;
      // create the new edge
      // get the neighbour position
      IDirectPosition pt2 = nearest.getGeom().getPosition();
      IEdge edge = new Edge(this, node, nearest, null, new GM_LineString(pt1,
          pt2));
      this.getEdges().add(edge);
      node.addEdgeOut(edge);
      nearest.addEdgeIn(edge);

    }
  }

}
