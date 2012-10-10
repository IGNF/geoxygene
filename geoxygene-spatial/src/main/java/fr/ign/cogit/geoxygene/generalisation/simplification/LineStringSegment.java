/**
 * @author julien Gaffuri 30 sept. 2008
 */
package fr.ign.cogit.geoxygene.generalisation.simplification;

import java.util.ArrayList;

import com.vividsolutions.jts.geom.LinearRing;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * classe servant a stocker un cote d'une ligne
 * @author julien Gaffuri 30 sept. 2008
 * 
 */
public class LineStringSegment {

  /**
   * l'index du cote
   */
  public int index = -1;

  /**
   * la longueur du cote
   */
  public double length = -1.0;

  public LineStringSegment(double longueur, int index) {
    this.length = longueur;
    this.index = index;
  }

  /**
   * Recupere le plus petit cote d'une ligne
   * @param ls
   * @return
   */
  public static LineStringSegment getSmallest(ILineString ls) {
    LineStringSegment ppcls = new LineStringSegment(Double.MAX_VALUE, -1);

    IDirectPositionList coord = ls.coord();
    for (int i = 0; i < coord.size() - 1; i++) {
      double lg = coord.get(i).distance(coord.get(i + 1));
      if (lg < ppcls.length) {
        ppcls.length = lg;
        ppcls.index = i;
      }
    }
    return ppcls;
  }

  public static LineStringSegment getSmallest(IRing ring) {
    return LineStringSegment.getSmallest(new GM_LineString(ring.coord()));
  }

  /**
   * Recupere les cotes plus petits qu'un certain seuil
   * 
   * @param ls
   * @param seuil
   * @return
   */
  public static ArrayList<LineStringSegment> getSmallest(ILineString ls,
      double seuil) {
    ArrayList<LineStringSegment> al = new ArrayList<LineStringSegment>();
    IDirectPositionList coord = ls.coord();
    for (int i = 0; i < coord.size() - 1; i++) {
      double lg = coord.get(i).distance(coord.get(i + 1));
      if (lg < seuil) {
        al.add(new LineStringSegment(lg, i));
      }
    }
    return al;
  }

  public static ArrayList<LineStringSegment> getSmallest(IRing ring,
      double seuil) {
    return LineStringSegment
        .getSmallest(new GM_LineString(ring.coord()), seuil);
  }

  public static ArrayList<LineStringSegment> getSmallest(LinearRing lr_,
      double seuil) {
    try {
      return LineStringSegment.getSmallest(new GM_LineString(AdapterFactory
          .toGM_Object(lr_).coord()), seuil);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}
