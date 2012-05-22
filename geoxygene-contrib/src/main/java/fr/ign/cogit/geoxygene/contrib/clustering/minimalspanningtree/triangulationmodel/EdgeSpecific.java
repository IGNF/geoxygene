package fr.ign.cogit.geoxygene.contrib.clustering.minimalspanningtree.triangulationmodel;

import fr.ign.cogit.geoxygene.contrib.delaunay.ArcDelaunay;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * A specific edge is the edge type of the triangulation model used in the
 * minimal spanning tree clustering algorithm based on Delaunay triangulation.
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 */
public class EdgeSpecific extends ArcDelaunay {

  /**
   * Constructor of a specific edge from two specific nodes.
   * 
   * @param node first node of the edge
   * @param node2 second node of the edge
   */
  public EdgeSpecific(NodeSpecific node, NodeSpecific node2) {
    DirectPositionList dpl = new DirectPositionList();
    dpl.add(node.getCoord());
    dpl.add(node2.getCoord());
    this.setCoord(dpl);
    this.setNoeudIni(node);
    this.setNoeudFin(node2);
  }

}
