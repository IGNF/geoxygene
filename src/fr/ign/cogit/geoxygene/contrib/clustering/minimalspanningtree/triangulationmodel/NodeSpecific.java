package fr.ign.cogit.geoxygene.contrib.clustering.minimalspanningtree.triangulationmodel;

import fr.ign.cogit.geoxygene.contrib.delaunay.NoeudDelaunay;

/**
 * A specific node is the node type of the triangulation model used in the
 * minimal spanning tree clustering algorithm based on Delaunay triangulation.
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 */
public class NodeSpecific extends NoeudDelaunay {

  /**
   * True if the node is linked to a cluster, otherwise false.
   */
  private boolean clusterLinked;

  /**
   * Returns true if the node is linked to a cluster, otherwise false.
   * 
   * @return true if the node is linked to a cluster, otherwise false
   */
  public boolean isClusterLinked() {
    return this.clusterLinked;
  }

  /**
   * Sets the status of this node: true if the node is linked to a cluster,
   * otherwise false.
   * 
   * @param clusterLinked true if the node is linked to a cluster, otherwise
   *          false.
   */
  public void setClusterLinked(boolean clusterLinked) {
    this.clusterLinked = clusterLinked;
  }

  /**
   * True if all the clustering tests have been computed for this node,
   * otherwise false.
   */
  private boolean fullyTested;

  /**
   * Returns true if all the clustering tests have been computed for this node,
   * otherwise false.
   * 
   * @return true if all the clustering tests have been computed for this node,
   *         otherwise false
   */
  public boolean isFullyTested() {
    return this.fullyTested;
  }

  /**
   * Sets the status of this node: true if all the clustering tests have been
   * computed for this node, otherwise false.
   * 
   * @param fullyTested true if all the clustering tests have been computed for
   *          this node, otherwise false.
   */
  public void setFullyTested(boolean fullyTested) {
    this.fullyTested = fullyTested;
  }

  /**
   * Default constructor.
   */
  public NodeSpecific() {
    this.clusterLinked = false;
    this.fullyTested = false;
  }

  // ///////////////////////////////////////////////////
  // ///// Bidirectional link 1-n to the cluster ///////
  // ///////////////////////////////////////////////////
  /**
   * Cluster in relation with this node
   */
  private Cluster cluster;

  /**
   * Returns the cluster in relation with this node.
   * 
   * @return the cluster in relation with this node
   */
  public Cluster getCluster() {
    return this.cluster;
  }

  /**
   * Define the cluster in relation with this node, and update the inverse
   * relation.
   * 
   * @param cluster cluster which is in relation with this node
   */
  public void setCluster(Cluster cluster) {
    Cluster oldCluster = this.cluster;
    this.cluster = cluster;

    if (oldCluster != null) {
      oldCluster.getNodes().remove(this);
    }

    if (cluster != null) {
      if (!cluster.getNodes().contains(this)) {
        cluster.getNodes().add(this);
      }
    }
  }

}
