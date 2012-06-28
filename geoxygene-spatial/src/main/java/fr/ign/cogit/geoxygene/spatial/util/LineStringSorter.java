package fr.ign.cogit.geoxygene.spatial.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class LineStringSorter {
  /**
   * Sort the collection according to the minimum arc length of their
   * intersection with the line.
   * <p>
   * The lines which do not intersect the reference line are ignored.
   * @param line
   *        the reference line
   * @param collection
   *        the collection to sort
   * @return a sorted collection of linestrings.
   */
  public static List<ILineString> sort(ILineString line, Collection<ILineString> collection) {
    final Map<ILineString, Double> paramForLineString = new HashMap<ILineString, Double>();
    for (ILineString l : collection) {
      if (line.intersects(l)) {
        IGeometry intersection = line.intersection(l);
        double minParam = Double.POSITIVE_INFINITY;
        for (IDirectPosition p : intersection.coord()) {
          double param = line.paramForPoint(p)[0];
          minParam = Math.min(minParam, param);
        }
        paramForLineString.put(l, new Double(minParam));
      }
    }
    Comparator<ILineString> comparator = new Comparator<ILineString>() {
      @Override
      public int compare(ILineString o1, ILineString o2) {
        double p1 = paramForLineString.get(o1).doubleValue();
        double p2 = paramForLineString.get(o2).doubleValue();
        return Double.compare(p1, p2);
      }
    };
    List<ILineString> result = new ArrayList<ILineString>(paramForLineString.keySet());
    Collections.sort(result, comparator);
    return result;
  }

  /**
   * Sort the collection according to the distance to the point.
   * @param point
   *        the reference point
   * @param collection
   *        the collection to sort
   * @return a sorted collection of linestrings.
   */
  public static List<ILineString> sort(IDirectPosition point, Collection<ILineString> collection) {
    final Map<ILineString, Double> distances = new HashMap<ILineString, Double>();
    IPoint p = point.toGM_Point();
    for (ILineString l : collection) {
      double distance = l.distance(p);
      distances.put(l, new Double(distance));
    }
    Comparator<ILineString> comparator = new Comparator<ILineString>() {
      @Override
      public int compare(ILineString o1, ILineString o2) {
        double p1 = distances.get(o1).doubleValue();
        double p2 = distances.get(o2).doubleValue();
        return Double.compare(p1, p2);
      }
    };
    List<ILineString> result = new ArrayList<ILineString>(distances.keySet());
    Collections.sort(result, comparator);
    return result;
  }
}
