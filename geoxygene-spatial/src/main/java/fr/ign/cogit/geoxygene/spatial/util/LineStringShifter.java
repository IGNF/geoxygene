package fr.ign.cogit.geoxygene.spatial.util;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Shift linestrings.
 * @author Julien Perret
 *
 */
public class LineStringShifter {
  /**
   * @param points
   * @param index
   * @param dx
   * @param dy
   * @return
   */
  public static List<IDirectPosition> shift(List<IDirectPosition> points, int index, double dx, double dy) {
    List<IDirectPosition> result = new ArrayList<IDirectPosition>(points.size());
    double length = (index == 0) ? 0 : new GM_LineString(points.subList(0, index + 1)).length();
    double abscissa = 0;
    for (int i = 0; i < index; i++) {
      if (i > 0) {
        abscissa += points.get(i).distance(points.get(i - 1));
        double ratio = abscissa / length;
        result.add(new DirectPosition(points.get(i).getX() + dx * ratio, points.get(i).getY() + dy * ratio));
      } else {
        result.add(new DirectPosition(points.get(i).getX(), points.get(i).getY()));
      }
    }
    result.add(new DirectPosition(points.get(index).getX()+ dx, points.get(index).getY()+ dy));
    // compute the length of the part of the line after the index
    length = (index == points.size() - 1) ? 0 : new GM_LineString(points.subList(index, points.size())).length();
    abscissa = 0;
    for (int i = index + 1; i < points.size(); i++) {
      if (i > 0) {
        abscissa += points.get(i).distance(points.get(i - 1));
        double ratio = 1 - abscissa / length;
        result.add(new DirectPosition(points.get(i).getX() + dx * ratio, points.get(i).getY() + dy * ratio));
      } else {
        result.add(new DirectPosition(points.get(i).getX(), points.get(i).getY()));
      }
    }
    return result;
  }
  /**
   * @param line
   * @param index
   * @param dx
   * @param dy
   * @return
   */
  public static ILineString shift(ILineString line, int index, double dx, double dy) {
    return new GM_LineString(shift(line.getControlPoint().getList(), index, dx, dy));
  }
}
