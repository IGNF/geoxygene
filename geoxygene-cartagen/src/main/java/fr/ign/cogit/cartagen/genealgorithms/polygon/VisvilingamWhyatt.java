/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.genealgorithms.polygon;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;

/**
 * Implementation of the Visvilingam-Whyatt (1993) algorithm for line
 * simplification by vertex filtering. It is more adapted to natural line than
 * the Douglas&Peucker algorithm.
 * @author GTouya
 * 
 */
public class VisvilingamWhyatt {

  private double areaTolerance;

  public VisvilingamWhyatt(double areaTolerance) {
    this.areaTolerance = areaTolerance;
  }

  /**
   * Visvalingam-Whyatt simplification of a line string.
   * @param line
   * @return
   */
  public ILineString simplify(ILineString line) {
    ILineString copy = new GM_LineString(line.coord());
    boolean loop = true;
    while (loop) {
      IDirectPosition ptAreaMin = null;
      double areaMin = Double.MAX_VALUE;
      for (IDirectPosition pt : copy.coord()) {
        if (pt.equals(line.startPoint()))
          continue;
        if (pt.equals(line.endPoint()))
          continue;
        double area = computeEffectiveArea(copy, pt);
        if (area < areaMin) {
          areaMin = area;
          if (area < areaTolerance)
            ptAreaMin = pt;
        }
      }
      if (ptAreaMin == null)
        break;
      // remove the point
      IDirectPositionList newCoord = copy.coord();
      newCoord.remove(ptAreaMin);
      copy = new GM_LineString(newCoord);
    }
    return copy;
  }

  /**
   * Visvalingam-Whyatt simplification of a polygon.
   * @param line
   * @return
   */
  public IPolygon simplify(IPolygon polygon) {
    IPolygon newPol = new GM_Polygon(simplify(polygon.exteriorLineString()));
    for (int i = 0; i < polygon.getInterior().size(); i++) {
      ILineString hole = simplify(polygon.interiorLineString(i));
      newPol.addInterior(new GM_Ring(hole));
    }

    return newPol;
  }

  private double computeEffectiveArea(ILineString copy, IDirectPosition pt) {
    IDirectPosition first = null, last = null;
    for (int i = 1; i < copy.coord().size() - 1; i++) {
      if (copy.coord().get(i).equals(pt)) {
        first = copy.coord().get(i - 1);
        last = copy.coord().get(i + 1);
      }
    }
    IPolygon triangle = GeometryFactory.buildTriangle(first, pt, last);
    return triangle.area();
  }
}
