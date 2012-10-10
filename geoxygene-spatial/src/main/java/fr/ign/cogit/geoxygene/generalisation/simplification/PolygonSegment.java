/**
 * @author julien Gaffuri 30 sept. 2008
 */
package fr.ign.cogit.geoxygene.generalisation.simplification;

import java.util.ArrayList;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * @author julien Gaffuri 30 sept. 2008
 * 
 */
public class PolygonSegment {

  /**
   * l'index de l'anneau du polygone qui porte le cote. c'est celui donné dans
   * poly.getInterior. Vaut -1 si l'anneau est le ring externe
   */
  public int ringIndex = -999;

  /**
   * le coté de l'anneau concerné
   */
  public LineStringSegment segment = null;

  public PolygonSegment(int ringIndex, LineStringSegment segment) {
    this.ringIndex = ringIndex;
    this.segment = segment;
  }

  /**
   * Recupere le plus petit cote d'un polygone
   * @param poly
   * @return
   */
  public static PolygonSegment getSmallest(IPolygon poly) {
    PolygonSegment ppcp = new PolygonSegment(-1, LineStringSegment
        .getSmallest(poly.getExterior()));

    // the holes
    for (int i = 0; i < poly.getInterior().size(); i++) {
      LineStringSegment ppcls = LineStringSegment.getSmallest(poly
          .getInterior(i));
      if (ppcls.length < ppcp.segment.length) {
        ppcp.ringIndex = i;
        ppcp.segment = ppcls;
      }
    }
    return ppcp;
  }

  /**
   * Recupere les cotes plus petits qu'un certain seuil d'un polygone
   * 
   * @param poly
   * @param seuil
   * @return
   */
  public static ArrayList<PolygonSegment> getSmallest(IPolygon poly,
      double seuil) {
    // la liste des cotes de polygone en retour
    ArrayList<PolygonSegment> cps = new ArrayList<PolygonSegment>();

    // liste des cotes trop courts de l'anneau externe
    ArrayList<LineStringSegment> clsl = LineStringSegment.getSmallest(poly
        .getExterior(), seuil);
    // ajout de ces cotes a la liste en retour
    for (LineStringSegment cls : clsl) {
      cps.add(new PolygonSegment(-1, cls));
    }

    // pareil pour les trous
    for (int i = 0; i < poly.getInterior().size(); i++) {
      // liste des cotes trop courts du trou
      clsl = LineStringSegment.getSmallest(poly.getInterior(i), seuil);
      // ajout de ces cotes a la liste en retour
      for (LineStringSegment cls : clsl) {
        cps.add(new PolygonSegment(i, cls));
      }
    }

    return cps;
  }

}
