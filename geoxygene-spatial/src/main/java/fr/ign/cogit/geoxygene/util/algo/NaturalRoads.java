package fr.ign.cogit.geoxygene.util.algo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

/**
 * Implementation of Bin Jiang's natural roads in
 * "Self-organized Natural Roads for Predicting Traffic Flow: A Sensitivity Study"
 * @see <a href="https://arxiv.org/pdf/0804.1630.pdf" >the paper (accessed
 *      212/06/2016) : https://arxiv.org/pdf/0804.1630.pdf</a>
 * @author Bertrand Duménieu
 */

public class NaturalRoads {

  /**
   * Create the natural roads on a given input road network
   * @param lines : the input roads network as a set of linestrings
   * @param strategy : 0 = Self Best Fit, 1 = Every Best Fit. Self-fit is not
   *          yet implemented
   * @param pos_threshold : 2 nodes are consiredered to be at the same position
   *          if the euclidean distance between them is leq than pos_threshold
   * @param max_angle_rad : maximum angle between two linestrings in the same
   *          natural road
   * @return the natural roads built over the input network lines TODO Implement
   *         the self fit strategy
   */
  public static Collection<ILineString> MakeNaturalRoads(
      Collection<ILineString> lines, int strategy, double pos_threshold,
      double max_angle_rad) {
    if (lines.isEmpty()) {
      return null;
    }
    Collection<ILineString> result = new ArrayList<>();
    if (lines.size() == 1) {
      result.add(lines.iterator().next());
      return result;
    }
    Collection<ILineString> untagged = new ArrayList<>(lines);
    while (!untagged.isEmpty()) {
      List<IDirectPosition> merged_pts = new ArrayList<>();
      ILineString start_line = untagged.iterator().next();
      merged_pts.addAll(start_line.getControlPoint());
      untagged.remove(start_line);
      // Build the natural road to the right (following the starting line
      // direction)
      if (strategy == 0) {
        exploreSBF(merged_pts, start_line, start_line.endPoint(), untagged,
            pos_threshold, max_angle_rad, 1);
      } else if (strategy == 1) {
        exploreEBF(merged_pts, start_line, start_line.endPoint(), untagged,
            pos_threshold, max_angle_rad, 1);
      }
      // Build the natural road to the left (following the starting line
      // direction)
      Collections.reverse(merged_pts);
      if (strategy == 0) {
        exploreSBF(merged_pts, start_line, start_line.startPoint(), untagged,
            pos_threshold, max_angle_rad, -1);
      } else if (strategy == 1) {
        exploreEBF(merged_pts, start_line, start_line.startPoint(), untagged,
            pos_threshold, max_angle_rad, -1);
      }
      result.add(new GM_LineString(merged_pts));
    }
    return result;

  }

  private static void exploreSBF(List<IDirectPosition> already_merged_pts,
      ILineString last_line, IDirectPosition current_point,
      Collection<ILineString> untagged, double pos_threshold,
      double max_angle_rad, int direction) {
    // TODO Use a spatial index
    ILineString candidate = null;
    boolean reverse = false;
    double min_angle = Double.MAX_VALUE;

    for (ILineString c : untagged) {
      if (c.startPoint().distance(current_point) < pos_threshold) {
        double angle = defangle(last_line, c, pos_threshold);
        if (angle < max_angle_rad && angle < min_angle) {
          min_angle = angle;
          candidate = c;
          reverse = false;
        }
      } else if (c.endPoint().distance(current_point) < pos_threshold) {
        double angle = defangle(last_line, c, pos_threshold);
        if (angle < max_angle_rad && angle < min_angle) {
          min_angle = angle;
          candidate = c;
          reverse = true;
        }
      }
    }
    if (candidate == null) {
      return;
    }
    untagged.remove(candidate);
    if (reverse) {
      for (int i = candidate.getControlPoint().size() - 2; i >= 0; i--) {
        already_merged_pts.add(candidate.getControlPoint().get(i));
      }
      exploreSBF(already_merged_pts, candidate, candidate.startPoint(),
          untagged, pos_threshold, max_angle_rad, direction);
    } else {
      for (int i = 1; i < candidate.getControlPoint().size(); i++) {
        already_merged_pts.add(candidate.getControlPoint().get(i));
      }
      exploreSBF(already_merged_pts, candidate, candidate.endPoint(), untagged,
          pos_threshold, max_angle_rad, direction);
    }
  }

  private static void exploreEBF(List<IDirectPosition> already_merged_pts,
      ILineString last_line, IDirectPosition current_point,
      Collection<ILineString> untagged, double pos_threshold,
      double max_angle_rad, int direction) {
    // TODO Use a spatial index
    ILineString candidate = null;
    boolean reverse = false;
    double min_angle = Double.MAX_VALUE;

    Map<ILineString, Double> ebf_mem = new HashMap<ILineString, Double>();
    for (ILineString c1 : untagged) {
      if (c1.startPoint().distance(current_point) < pos_threshold
          || c1.endPoint().distance(current_point) < pos_threshold
          && c1 != last_line) {
        if (!ebf_mem.containsKey(c1)) {
          ebf_mem.put(c1, Double.MAX_VALUE);
        }
        for (ILineString c2 : untagged) {
          if (c1 != c2 && c2 != last_line) {
            if (c2.startPoint().distance(current_point) < pos_threshold
                || c2.endPoint().distance(current_point) < pos_threshold) {
              double angle = defangle(c1, c2, pos_threshold);
              if (angle < ebf_mem.get(c1)) {
                ebf_mem.put(c1, angle);
              }
            }
          }
        }
      }
    }
    for (ILineString c : untagged) {
      if (c.startPoint().distance(current_point) < pos_threshold) {
        double angle = defangle(last_line, c, pos_threshold);
        if (angle < max_angle_rad && angle < min_angle
            && angle < ebf_mem.get(c)) {
          min_angle = angle;
          candidate = c;
          reverse = false;
        }
      } else if (c.endPoint().distance(current_point) < pos_threshold) {
        double angle = defangle(last_line, c, pos_threshold);
        if (angle < max_angle_rad && angle < min_angle
            && angle < ebf_mem.get(c)) {
          min_angle = angle;
          candidate = c;
          reverse = true;
        }
      }
    }

    if (candidate == null) {
      return;
    }
    untagged.remove(candidate);
    if (reverse) {
      for (int i = candidate.getControlPoint().size() - 2; i >= 0; i--) {
        already_merged_pts.add(candidate.getControlPoint().get(i));
      }
      exploreEBF(already_merged_pts, candidate, candidate.startPoint(),
          untagged, pos_threshold, max_angle_rad, direction);
    } else {
      for (int i = 1; i < candidate.getControlPoint().size(); i++) {
        already_merged_pts.add(candidate.getControlPoint().get(i));
      }
      exploreEBF(already_merged_pts, candidate, candidate.endPoint(), untagged,
          pos_threshold, max_angle_rad, direction);
    }
  }

  private static double defangle(ILineString p, ILineString q, double epsilon) {

    IDirectPosition v1_a = null;
    IDirectPosition v1_b = null;
    IDirectPosition v2_a = null;
    IDirectPosition v2_b = null;
    if (p.startPoint().distance(q.startPoint()) < epsilon) {
      /* <-------------------.----------------------> */
      v1_a = p.coord().get(1);
      v1_b = p.startPoint();
      v2_a = q.startPoint();
      v2_b = q.coord().get(1);
    } else if (p.endPoint().distance(q.startPoint()) < epsilon) {
      /* .-------------------->.--------------------> */
      v1_a = p.coord().get(p.coord().size() - 2);
      v1_b = p.endPoint();
      v2_a = q.startPoint();
      v2_b = q.coord().get(1);
    } else if (p.startPoint().distance(q.endPoint()) < epsilon) {
      /* <--------------------.<--------------------. */
      v1_a = p.coord().get(1);
      v1_b = p.startPoint();
      v2_a = q.endPoint();
      v2_b = q.coord().get(q.coord().size() - 2);
    } else if (p.endPoint().distance(q.endPoint()) < epsilon) {
      /* .--------------------><--------------------. */
      v1_a = p.coord().get(p.coord().size() - 2);
      v1_b = p.endPoint();
      v2_a = q.endPoint();
      v2_b = q.coord().get(q.coord().size() - 2);
    }
    Vector2D v1 = new Vector2D(v1_a, v1_b);
    Vector2D v2 = new Vector2D(v2_a, v2_b);
    return v1.angleVecteur(v2).getValeur();
  }
}
