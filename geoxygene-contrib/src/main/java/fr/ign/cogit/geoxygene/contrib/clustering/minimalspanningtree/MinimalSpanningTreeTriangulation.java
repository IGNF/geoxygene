package fr.ign.cogit.geoxygene.contrib.clustering.minimalspanningtree;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.clustering.minimalspanningtree.triangulationmodel.Cluster;
import fr.ign.cogit.geoxygene.contrib.clustering.minimalspanningtree.triangulationmodel.NodeSpecific;
import fr.ign.cogit.geoxygene.contrib.clustering.minimalspanningtree.triangulationmodel.TriangulationModel;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;

/**
 * Class to create a Minimal Spanning Tree based on a Delaunay's triangulation.
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 */
public class MinimalSpanningTreeTriangulation {

  /**
   * Logger
   */
  private static final Logger logger = LogManager
      .getLogger(MinimalSpanningTreeTriangulation.class);

  /**
   * Triangulation model.
   */
  private TriangulationModel triangulationModel;

  /**
   * Returns the triangulation model.
   * 
   * @return the triangulation model
   */
  public TriangulationModel getTriangulationModel() {
    return this.triangulationModel;
  }

  /**
   * Set a new triangulation model.
   * 
   * @param triangulationModel
   */
  public void setTriangulationModel(TriangulationModel triangulationModel) {
    this.triangulationModel = triangulationModel;
  }

  /**
   * Default constructor.
   */
  public MinimalSpanningTreeTriangulation() {
  }

  /**
   * A Delaunay triangulation based method to create the minimal spanning tree
   * from a population of points.
   * 
   * @param populationPoints population of the points to cluster
   * @param threshold the maximum distance to link two nodes in the same cluster
   * @return the list of the computed clusters
   */
  @SuppressWarnings("null")
  public <F extends IFeature> List<Cluster> creationMST(
      Population<F> populationPoints, double threshold) {

    this.triangulationModel = MinimalSpanningTreeTriangulation
        .triangle(populationPoints);

    // computation of the square of the threshold to optimise the
    // implementation the euclidian distance computation
    double thresholdSquare = threshold * threshold;

    List<Cluster> clusters = new ArrayList<Cluster>();
    @SuppressWarnings("unchecked")
    List<NodeSpecific> nodes = ((Population<NodeSpecific>) this.triangulationModel.getPopulation("Node")).getElements();

    Cluster cluster = null;
    List<NodeSpecific> adjacentNodes;
    List<NodeSpecific> clusterNodes;
    List<NodeSpecific> adjacentNodesCluster;
    double distance;
    boolean flag;

    for (NodeSpecific node : nodes) {
      node.setClusterLinked(false);
    }

    for (NodeSpecific node : nodes) {

      // if the node has already be fully tested, then process the next
      // node
      if (!node.isFullyTested()) {
        adjacentNodes = MinimalSpanningTreeTriangulation.getAdjacentNodes(
            this.triangulationModel, node);

        flag = false;
        // test with the closest neighbors
        for (NodeSpecific adjacentNode : adjacentNodes) {
          if (!adjacentNode.isFullyTested()) {
            distance = MinimalSpanningTreeTriangulation.distanceSquared(node,
                adjacentNode);

            if (distance < thresholdSquare) {

              if (!node.isClusterLinked()) { // creation of a new
                // cluster
                cluster = new Cluster();
                clusters.add(cluster);
                cluster.addNode(node);
                cluster.addNode(adjacentNode);
                node.setClusterLinked(true);
                flag = true;
              } else {
                cluster.addNode(adjacentNode);
                adjacentNode.setClusterLinked(true);
              }
            }
          }
        } // end of the clusters creation if the node is not isolated

        if (flag) {
          boolean creation = true;

          while (creation) {
            creation = false;
            clusterNodes = cluster.getNodes();
            List<NodeSpecific> addedNodes = new ArrayList<NodeSpecific>();

            for (NodeSpecific nodeCluster : clusterNodes) {
              if (!nodeCluster.isFullyTested()) {
                adjacentNodesCluster = MinimalSpanningTreeTriangulation
                    .getAdjacentNodes(this.triangulationModel, nodeCluster);

                for (NodeSpecific adjacentNode : adjacentNodesCluster) {
                  if (!adjacentNode.isFullyTested()) {
                    distance = MinimalSpanningTreeTriangulation
                        .distanceSquared(nodeCluster, adjacentNode);

                    if (distance < thresholdSquare) { // creation
                      // of
                      // a
                      // new
                      // cluster
                      addedNodes.add(adjacentNode);
                      adjacentNode.setClusterLinked(true);
                    }
                  }
                }
                nodeCluster.setFullyTested(true);
              }
            }

            if (addedNodes.size() != 0) {
              cluster.addNodes(addedNodes);
              creation = true;
            }
          }
        } else { // isolated node: creation of a new cluster
          cluster = new Cluster();
          clusters.add(cluster);
          cluster.addNode(node);
          node.setClusterLinked(true);
          node.setFullyTested(true);
        }
      }
    }

    int k = 0;
    for (Cluster group : clusters) {
      if (group.getNodes().size() == 1) {
        k++;
      }
    }

    MinimalSpanningTreeTriangulation.logger
        .info("--------------------------------------------------"); //$NON-NLS-1$
    MinimalSpanningTreeTriangulation.logger.info("Summary of the clustering:"); //$NON-NLS-1$
    MinimalSpanningTreeTriangulation.logger
        .info(" - " + clusters.size() + " clusters have been created,"); //$NON-NLS-1$ //$NON-NLS-2$
    MinimalSpanningTreeTriangulation.logger
        .info(" - " + k + " clusters contain a single node."); //$NON-NLS-1$ //$NON-NLS-2$
    MinimalSpanningTreeTriangulation.logger
        .info("--------------------------------------------------"); //$NON-NLS-1$

    return clusters;
  }

  /**
   * Display the triangulation model in a new {@link ObjectViewer}
   * 
   * @param triangulation the triangulation model to display
   */
//  public static void display(TriangulationModel triangulation) {
//    ObjectViewer obj = new ObjectViewer();
//    obj.addFeatureCollection(triangulation.getPopFaces(), "Faces"); //$NON-NLS-1$
//    obj.addFeatureCollection(triangulation.getPopArcs(), "Edges"); //$NON-NLS-1$
//    obj.addFeatureCollection(triangulation.getPopNoeuds(), "Nodes"); //$NON-NLS-1$
//  }

  /**
   * Display the triangulation model in the specified{@link ObjectViewer}.
   * 
   * @param triangulation the triangulation model to display
   * @param viewer
   */
//  public static void display(TriangulationModel triangulation,
//      ObjectViewer viewer) {
//    viewer.addFeatureCollection(triangulation.getPopFaces(), "Faces"); //$NON-NLS-1$
//    viewer.addFeatureCollection(triangulation.getPopArcs(), "Edges"); //$NON-NLS-1$
//    viewer.addFeatureCollection(triangulation.getPopNoeuds(), "Nodes"); //$NON-NLS-1$
//  }

  /**
   * Computes the square of the euclidian distance between two nodes.
   * 
   * @param node first node
   * @param node2 second node
   */
  private static double distanceSquared(Noeud node, Noeud node2) {
    IDirectPosition dp = node.getGeometrie().coord().get(0);
    IDirectPosition dp2 = node2.getGeometrie().coord().get(0);

    return (Math.pow(dp.getX() - dp2.getX(), 2) + Math.pow(
        dp.getY() - dp2.getY(), 2));
  }

  /**
   * Returns all the adjacent nodes in link with the considered node of the
   * topological map.
   * 
   * @param topoMap topological map
   * @param node node to compute
   * @return all the adjacent nodes in link with the considered node of the
   *         topological map
   */
  private static List<NodeSpecific> getAdjacentNodes(CarteTopo topoMap,
      Noeud node) {

    List<NodeSpecific> adjacentNodes = new ArrayList<NodeSpecific>();

    // adjacent nodes based on the inner edges
    for (Arc edge : node.getEntrants()) {
      adjacentNodes.add((NodeSpecific) edge.getNoeudIni());
    }

    // adjacent nodes based on the outer edges
    for (Arc edge : node.getSortants()) {
      adjacentNodes.add((NodeSpecific) edge.getNoeudFin());
    }

    return adjacentNodes;
  }

  /**
   * Computes the triangulation model in link to the input population.
   * 
   * @param population to triangulate
   * @return the triangulation model in link to the input population
   */
  private static <F extends IFeature> TriangulationModel triangle(
      Population<F> population) {
    TriangulationModel triangulation = new TriangulationModel(
        "Triangulation model"); //$NON-NLS-1$
    Chargeur.importClasseGeo(population, triangulation);

    try {
      triangulation.triangule();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return triangulation;
  }

  /**
   * A Delaunay triangulation based method to create the minimal spanning tree
   * from a collection of points.
   * 
   * @param collectionPoints population of the points to cluster
   * @param threshold the maximum distance to link two nodes in the same cluster
   * @return the list of the computed clusters
   */
  public <F extends IFeature> List<Cluster> creationMST(
      FT_FeatureCollection<F> collectionPoints, double threshold) {
    Population<F> populationPoints = new Population<F>();
    populationPoints.addUniqueCollection(collectionPoints);
    List<Cluster> clusters = this.creationMST(populationPoints, threshold);
    return clusters;
  }

}
