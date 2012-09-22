package fr.ign.cogit.geoxygene.contrib.clustering.minimalspanningtree.triangulationmodel;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Cluster (used e.g. in the minimal spanning tree clustering algorithm based on
 * Delaunay triangulation).
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 * 
 */
public class Cluster {

  private static int nbClusters = 0;

  /**
   * Id of the cluster.
   */
  private int clusterId;

  /**
   * Returns the id of the cluster.
   * 
   * @return the id of the cluster
   */
  public int getClusterId() {
    return this.clusterId;
  }

  /**
   * Sets the id of the cluster.
   * 
   * @param clusterId
   */
  public void setClusterId(int clusterId) {
    this.clusterId = clusterId;
  }

  /**
   * Default constructor.
   */
  public Cluster() {
    Cluster.nbClusters++;
    this.clusterId = new Integer(Cluster.nbClusters);
  }

  /**
   * Returns a Polygon of the cluster. This polygon is created from a buffer
   * (size 4 by default) if the cluster contains only a node (i.e. a Point
   * geometry type), or two nodes (i.e. a Segment geometry type). If the cluster
   * contains strictly more than two nodes, the polygon is created from the
   * convex hull of the cluster, except the case where all nodes in the cluster
   * are in the same straight line. In this last problematic case, the geometry
   * is computed with a buffer (size 4 by default).
   * 
   * 
   * @return a polygon which represents graphically the cluster in order to
   *         correctly display it.
   */
  public GM_Polygon getClusterGeom() {

    List<NodeSpecific> nodes = this.getNodes();

    int clusterSize = nodes.size();

    if (clusterSize == 1) {
      return (GM_Polygon) new GM_Point(nodes.get(0).getCoord()).buffer(4);
    } else {
      List<IDirectPosition> points = new ArrayList<IDirectPosition>();

      for (NodeSpecific node : nodes) {
        points.add(node.getCoord());
      }
      GM_LineString line = new GM_LineString(points);
      if (clusterSize > 2) {
        // case where the cluster geometry is reduced to a point
        // or is a straight line
        if (this.isStraightLine(line) || this.isReducedToAPoint(line)) {
          return (GM_Polygon) line.buffer(4);
        }
        return (GM_Polygon) line.convexHull();
      } else { // clusterSize = 2: the cluster geometry is a segment or a point
        return (GM_Polygon) line.buffer(4);
      }
    }
  }

  /**
   * Returns the {@link Population} of the nodes of the cluster.
   * 
   * @return the population of the nodes of the cluster.
   */
  public Population<DefaultFeature> getPopulationNoeuds() {

    Population<DefaultFeature> population = new Population<DefaultFeature>(
        false, "Nodes of the cluster " + this.getClusterId(), //$NON-NLS-1$
        DefaultFeature.class, true);

    IFeature feature;
    for (NodeSpecific node : this.getNodes()) {
      feature = population.nouvelElement();
      feature.setGeom(node.getGeom());
    }

    return population;
  }

  /**
   * General orientation of the cluster (computed using a PCA method).
   */
  private Angle orientation = null;

  /**
   * (This method is not implemented) Returns the general orientation of the
   * cluster computed using a PCA method (Principal Component Analysis).
   * 
   * @return the general orientation of the cluster computed using a PCA method
   */
  public Angle getOrientation() {
    if (this.orientation == null) {
      // TODO: compute orientation using principal component analysis
      this.setOrientation(new Angle());
    }

    return this.orientation;
  }

  /**
   * Defines the general orientation of the cluster (computed using a PCA method
   * -- Principal Component Analysis --).
   * 
   * @param orientation the general orientation of the cluster computed using a
   *          PCA method
   */
  public void setOrientation(Angle orientation) {
    this.orientation = orientation;
  }

  /**
   * Returns true if the LineString is straight, false otherwise.
   * 
   * @param line LineString to process
   * @return true if the line is straight, false otherwise
   */
  private boolean isStraightLine(GM_LineString line) {

    IDirectPositionList coords = line.coord();
    for (int i = 1; i < coords.size() - 1; i++) {
      if (Angle.angleTroisPoints(coords.get(i - 1), coords.get(i),
          coords.get(i + 1)) != Angle.anglePlat) {

        return false;
      }
    }
    return true;
  }

  /**
   * Returns true if the cluster has all its points at the same position, false
   * otherwise.
   * 
   * @param line LineString to process
   * @return true if the cluster has all its points at the same position, false
   *         otherwise
   */
  private boolean isReducedToAPoint(GM_LineString line) {

    IDirectPositionList coords = line.coord();
    for (int i = 0; i < coords.size() - 1; i++) {
      if (coords.get(i).getX() != coords.get(i + 1).getX()) {
        return false;
      }

      if (coords.get(i).getY() != coords.get(i + 1).getY()) {
        return false;
      }
    }
    return true;
  }

  // /////////////////////////////////////////////////////////
  // /////// Bidirectional link 1-n to NodeSpecific /////////
  // /////////////////////////////////////////////////////////

  /**
   * List of the NodeSpecific in relation.
   */
  private List<NodeSpecific> nodes = new ArrayList<NodeSpecific>();

  /**
   * Returns the list of the NodeSpecific in relation.
   * 
   * @return the list of the NodeSpecific in relation.
   */
  public List<NodeSpecific> getNodes() {
    return this.nodes;
  }

  /**
   * Define the list of the NodeSpecific in relation, and update the inverse
   * relation.
   * 
   * @param nodes list of the nodes which are in relation with the cluster
   */
  public void setNodes(List<NodeSpecific> nodes) {
    for (NodeSpecific node : new ArrayList<NodeSpecific>(this.nodes)) {
      node.setCluster(null);
    }

    for (NodeSpecific node : nodes) {
      node.setCluster(this);
    }
  }

  /**
   * Returns the node at the specified position in the list of nodes in relation
   * with the cluster.
   * 
   * @param i index of the node to return
   * @return the node at the specified position in the list of nodes in relation
   *         with the cluster
   */
  public NodeSpecific getNode(int i) {
    return this.nodes.get(i);
  }

  /**
   * Appends the specified node to the end of the list of nodes in relation with
   * the cluster and update the inverse relation.
   * 
   * @param node node to append
   */
  public void addNode(NodeSpecific node) {
    if (node == null) {
      return;
    }

    this.nodes.add(node);
    node.setCluster(this);
  }

  /**
   * Appends the specified list of nodes to the end of the list of nodes in
   * relation with the cluster and update the inverse relation.
   * 
   * @param nodes nodes to append
   */
  public void addNodes(List<NodeSpecific> nodes) {
    if (nodes == null) {
      return;
    }

    for (NodeSpecific node : nodes) {
      this.nodes.add(node);
      node.setCluster(this);
    }
  }

  /**
   * Removes the specified node in the list of nodes in relation with the
   * cluster and update the inverse relation.
   * 
   * @param node node to remove
   */
  public void removeNode(NodeSpecific node) {
    if (node == null) {
      return;
    }

    this.nodes.remove(node);
    node.setCluster(null);
  }

  /**
   * Removes the specified list of nodes in the list of nodes in relation with
   * the cluster and update the inverse relation.
   * 
   * @param nodes list of nodes to remove
   */
  public void removeNodes(List<NodeSpecific> nodes) {
    if (nodes == null) {
      return;
    }

    for (NodeSpecific noeud : nodes) {
      this.nodes.remove(noeud);
      noeud.setCluster(null);
    }
  }

  /**
   * Empty the list of nodes in relation with the cluster and update the inverse
   * relation.
   */
  public void emptyNodes() {
    for (NodeSpecific node : new ArrayList<NodeSpecific>(this.nodes)) {
      node.setCluster(null);
    }
  }

}
