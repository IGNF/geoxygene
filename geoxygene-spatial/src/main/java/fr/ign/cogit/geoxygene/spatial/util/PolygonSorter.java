package fr.ign.cogit.geoxygene.spatial.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class PolygonSorter {
  /**
   * Sort the collection according to the minimum arc length of their
   * intersection with the line.
   * <p>
   * The lines which do not intersect the reference line are ignored.
   * @param line
   *        the reference line
   * @param collection
   *        the collection to sort
   * @return a sorted collection of polygons.
   */
  public static List<IPolygon> sort(ILineString line, Collection<IPolygon> collection) {
    final Map<IPolygon, Double> paramForPolygon = new HashMap<>();
    for (IPolygon l : collection) {
      if (line.intersects(l)) {
        IGeometry intersection = line.intersection(l);
        double minParam = Double.POSITIVE_INFINITY;
        for (IDirectPosition p : intersection.coord()) {
          double param = line.paramForPoint(p)[0];
          minParam = Math.min(minParam, param);
        }
        paramForPolygon.put(l, minParam);
      }
    }
    Comparator<IPolygon> comparator = (o1, o2) -> {
      double p1 = paramForPolygon.get(o1);
      double p2 = paramForPolygon.get(o2);
      return Double.compare(p1, p2);
    };
    List<IPolygon> result = new ArrayList<>(paramForPolygon.keySet());
    result.sort(comparator);
    return result;
  }

  /**
   * Sort the collection according to the distance to the point.
   * @param point
   *        the reference point
   * @param collection
   *        the collection to sort
   * @return a sorted collection of polygons.
   */
  public static List<IPolygon> sort(IDirectPosition point, Collection<IPolygon> collection) {
    final Map<IPolygon, Double> distances = new HashMap<>();
    IPoint p = point.toGM_Point();
    for (IPolygon l : collection) {
      double distance = l.distance(p);
      distances.put(l, distance);
    }
    Comparator<IPolygon> comparator = (o1, o2) -> {
      double p1 = distances.get(o1);
      double p2 = distances.get(o2);
      return Double.compare(p1, p2);
    };
    List<IPolygon> result = new ArrayList<>(distances.keySet());
    result.sort(comparator);
    return result;
  }
}
