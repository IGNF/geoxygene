package fr.ign.cogit.geoxygene.contrib.clustering.minimalspanningtree.triangulationmodel;

import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.delaunay.TriangleDelaunay;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * A specific triangle is the triangle type of the triangulation model used in
 * the minimal spanning tree clustering algorithm based on Delaunay
 * triangulation.
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 */
public class TriangleSpecific extends TriangleDelaunay {

  /**
   * Constructor of a TriangleSpecific from three NodeSpecific.
   * 
   * @param n1 fist SpecificNode of the triangle to build
   * @param n2 second SpecificNode of the triangle to build
   * @param n3 third SpecificNode of the triangle to build
   */
  public TriangleSpecific(NodeSpecific n1, NodeSpecific n2, NodeSpecific n3) {
    DirectPositionList dpl = new DirectPositionList();
    dpl.add(n1.getCoord());
    dpl.add(n2.getCoord());
    dpl.add(n3.getCoord());
    dpl.add(n1.getCoord());
    this.setCoord(dpl);

    Arc edge;
    List<Arc> edges = n1.arcs();
    Iterator<Arc> itEdges = edges.iterator();
    while (itEdges.hasNext()) {
      edge = itEdges.next();
      if ((edge.getNoeudIni() == n1) && (edge.getNoeudFin() == n2)) {
        edge.setFaceGauche(this);
        break;
      } else if ((edge.getNoeudIni() == n2) && (edge.getNoeudFin() == n1)) {
        edge.setFaceDroite(this);
        break;
      }
    }

    edges = n2.arcs();
    itEdges = edges.iterator();
    while (itEdges.hasNext()) {
      edge = itEdges.next();
      if ((edge.getNoeudIni() == n2) && (edge.getNoeudFin() == n3)) {
        edge.setFaceGauche(this);
        break;
      } else if ((edge.getNoeudIni() == n3) && (edge.getNoeudFin() == n2)) {
        edge.setFaceDroite(this);
        break;
      }
    }

    edges = n3.arcs();
    itEdges = edges.iterator();
    while (itEdges.hasNext()) {
      edge = itEdges.next();
      if ((edge.getNoeudIni() == n3) && (edge.getNoeudFin() == n1)) {
        edge.setFaceGauche(this);
        break;
      } else if ((edge.getNoeudIni() == n1) && (edge.getNoeudFin() == n3)) {
        edge.setFaceDroite(this);
        break;
      }
    }
  }

}
