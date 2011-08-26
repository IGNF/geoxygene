package fr.ign.cogit.geoxygene.contrib.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Computation of a non convex hull using swinging arm method.
 * <p>
 * Galton, A., Duckham, M. (2006) What is the Region Occupied by a Set of
 * Points? In Raubal, M., Miller, H., Frank, A., and Goodchild, M. (eds) Lecture
 * Notes in Computer Science 4197. Springer, Berlin.
 * @author J. Renard (d'après Galton & Duckham) 11/08/2009
 */
public class SwingingArmNonConvexHull {
  public final static Logger logger = Logger
      .getLogger(SwingingArmNonConvexHull.class.getName());
  /**
   * Points to construct the hull
   */
  private List<IDirectPosition> points;
  /**
   * Swinging arm radius
   */
  private double radius = 0.0;
  /**
   * Constructor
   */
  public SwingingArmNonConvexHull(List<IDirectPosition> list, double radius) {
    this.points = list;
    this.radius = radius;
  }
  /**
   * Method launch The idea is to compute two non-convex hulls using swinging
   * arm algorithm, one clockwise and one anticlockwise, and then to return the
   * union of these two non-convex hulls
   */
  public IGeometry compute() {
    if (this.points.isEmpty()) {
      if (SwingingArmNonConvexHull.logger.isTraceEnabled()) {
        SwingingArmNonConvexHull.logger
            .trace("No points to construct the hull");
      }
      return null;
    }
    if (SwingingArmNonConvexHull.logger.isTraceEnabled()) {
      SwingingArmNonConvexHull.logger
          .trace("Computation of the non-convex hull using Swinging Arm method with radius="
              + this.radius);
    }
    // Swinging arm clockwise
    if (SwingingArmNonConvexHull.logger.isTraceEnabled()) {
      SwingingArmNonConvexHull.logger.trace("clockwise");
    }
    IGeometry clockWise = this.swingingArmCompute(-1.0);
    // Swinging arm anticlockwise
    if (SwingingArmNonConvexHull.logger.isTraceEnabled()) {
      SwingingArmNonConvexHull.logger.trace("anticlockwise");
    }
    IGeometry antiClockWise = this.swingingArmCompute(1.0);
    // Union of the two geometries
    if (SwingingArmNonConvexHull.logger.isTraceEnabled()) {
      SwingingArmNonConvexHull.logger.trace("union of the two hulls");
    }
    if (!clockWise.isValid() || !antiClockWise.isValid()) {
      return null;
    } else if (!clockWise.isValid()) {
      return antiClockWise;
    } else if (!antiClockWise.isValid()) {
      return clockWise;
    }
    return clockWise.union(antiClockWise);
  }

  /**
   * Swinging arm algorithm The idea is to construct the hull step by step,
   * point after point. For each point, the following one is searched in a
   * certain radius, as the point in this radius whose direction is closest to
   * the direction of the last two points of the geometry. When the initial
   * point is recovered, it forms a connex component, and the union of all
   * connex components is the non-convex hull
   */
  private IGeometry swingingArmCompute(double direction) {
    // if there is no point, return a null hull
    if (this.points.isEmpty()) {
      if (SwingingArmNonConvexHull.logger.isTraceEnabled()) {
        SwingingArmNonConvexHull.logger
            .trace("No points to construct the hull");
      }
      return null;
    }
    IGeometry hull = new GM_Polygon(new GM_LineString(new DirectPositionList()));
    int nbConnexComponents = 0;
    /*
     * Mark all points of S as ‘available’ and ‘unvisited’.
     */
    // Boolean list for available points
    List<Boolean> isAvailable = new ArrayList<Boolean>(this.points.size());
    for (int i = 0; i < this.points.size(); i++) {
      isAvailable.add(Boolean.TRUE);
    }
    // Search for points to construct connex components util they are all
    // unavailable
    while (isAvailable.contains(Boolean.TRUE)) {
      DirectPositionList hullPoints = new DirectPositionList();
      // List of absolute positions of points of the current connex component
      ArrayList<Integer> absPos = new ArrayList<Integer>();
      // Line being constructed (to verify non self intersection)
      GM_LineString provisoryLine = new GM_LineString();
      // Boolean list for visited points
      List<Boolean> isVisited = new ArrayList<Boolean>(this.points.size());
      for (int i = 0; i < this.points.size(); i++) {
        isVisited.add(Boolean.FALSE);
      }
      // Initial point of the current connex component
      IDirectPosition initialPoint = new DirectPosition();
      double Xmax = Double.NEGATIVE_INFINITY;
      double Ymax = Double.NEGATIVE_INFINITY;
      int ptPos = -1;
      /*
       * Let Hi,0 be the available point in S with maximal y-coordinate (if
       * there is more than one such, choose the one with minimal x-coordinate).
       */
      for (int pos = 0; pos < this.points.size(); pos++) {
        if (isAvailable.get(pos).equals(Boolean.FALSE)) {
          continue;
        }
        if ((this.points.get(pos).getY() > Ymax)) {
          initialPoint = this.points.get(pos);
          Ymax = initialPoint.getY();
          Xmax = initialPoint.getX();
          ptPos = pos;
        } else if ((this.points.get(pos).getY() == Ymax)
            && (this.points.get(pos).getX() > Xmax)) {
          initialPoint = this.points.get(pos);
          Xmax = initialPoint.getX();
          ptPos = pos;
        }
      }
      // Addition of the initial point to the hull
      hullPoints.add(initialPoint);
      /*
       * Let L be a line-segment of length r anchored at Hi,0 parallel to the
       * positive y-axis.
       */
      provisoryLine.addControlPoint(initialPoint);
      absPos.add(new Integer(ptPos));
      // Following points of the current connex component
      while (true) {
        // Initialisation of parameters
        IDirectPosition nextPoint = new DirectPosition();
        IDirectPosition lastPoint = this.points.get(absPos
            .get(absPos.size() - 1).intValue());
        double alphaMin = Double.POSITIVE_INFINITY;
        double ptRadius = this.radius;
        ptPos = 0;
        // Angle of the last segment
        double alphaDiff = Math.PI / 2.0;
        if (absPos.size() > 1) {
          IDirectPosition lastLastPoint = this.points.get(absPos.get(
              absPos.size() - 2).intValue());
          alphaDiff = Math.atan((lastLastPoint.getY() - lastPoint.getY())
              / (lastLastPoint.getX() - lastPoint.getX()));
          if (lastLastPoint.getX() < lastPoint.getX()) {
            alphaDiff += Math.PI;
          }
          if (alphaDiff < 0) {
            alphaDiff += 2 * Math.PI;
          }
        }
        // Searching for the next point
        for (int pos = 0; pos < this.points.size(); pos++) {
          IDirectPosition pt = this.points.get(pos);
          // Point not considered if: not available, already visited, too far
          // from last point, or same as last two points
          if (isAvailable.get(pos).equals(Boolean.FALSE)) {
            continue;
          }
          if (isVisited.get(pos).equals(Boolean.TRUE)) {
            continue;
          }
          if (pt.distance(lastPoint) > this.radius) {
            continue;
          }
          if (pt.equals(lastPoint)) {
            continue;
          }
          if (absPos.size() > 1) {
            IDirectPosition lastLastPoint = this.points.get(absPos.get(
                absPos.size() - 2).intValue());
            if (pt.equals(lastLastPoint)) {
              continue;
            }
          }
          // Particular case: the potential next segment intersects the line
          // being constructed
          if (absPos.size() > 2 && !absPos.contains(new Integer(pos))) {
            GM_LineString potentialSegment = new GM_LineString();
            potentialSegment.addControlPoint(lastPoint);
            potentialSegment.addControlPoint(pt);
            provisoryLine.removeControlPoint(absPos.size() - 1);
            if (potentialSegment.intersects(provisoryLine)) {
              provisoryLine.addControlPoint(lastPoint);
              continue;
            }
            provisoryLine.addControlPoint(lastPoint);
          }
          // Computation of the angle
          double alpha = Math.atan((pt.getY() - lastPoint.getY())
              / (pt.getX() - lastPoint.getX()));
          if (pt.getX() < lastPoint.getX()) {
            alpha += Math.PI;
          }
          if (alpha < 0) {
            alpha += 2 * Math.PI;
          }
          alpha -= alphaDiff;
          alpha *= direction;
          if (alpha < 0) {
            alpha += 2 * Math.PI;
          }
          // Point kept as next one if the angle to searching direction is
          // minimum
          if (alpha < alphaMin
              || (alpha == alphaMin && pt.distance(lastPoint) < ptRadius)) {
            alphaMin = alpha;
            ptRadius = pt.distance(lastPoint);
            nextPoint = pt;
            ptPos = pos;
          }
        }
        // If no following point is found, the current connex component is
        // unvalid
        if (Double.isInfinite(alphaMin)) {
          isAvailable.set(absPos.get(absPos.size() - 1).intValue(),
              Boolean.FALSE);
          // If the connex component is reduces to one point, it is unvalid
          if (absPos.size() == 1) {
            break;
          }
          // else the last point is removed, and the algorithm keeps on
          // completing the connex component
          hullPoints.remove(hullPoints.size() - 1);
          provisoryLine
              .removeControlPoint(provisoryLine.sizeControlPoint() - 1);
          absPos.remove(absPos.size() - 1);
          continue;
        }
        // Addition of the next point to the current connex component
        hullPoints.add(nextPoint);
        provisoryLine.addControlPoint(nextPoint);
        absPos.add(new Integer(ptPos));
        isVisited.set(ptPos, Boolean.TRUE);
        // If the next point the initial point of the current connex component,
        // it is closed
        // the connex component has to be added to the hull and all interior
        // points are marked unavailable
        if (nextPoint.equals(initialPoint)) {
          // Addition of all points of the connex component to unavailable
          // points
          for (Integer k : absPos) {
            if (isAvailable.get(k.intValue()).equals(Boolean.TRUE)) {
              isAvailable.set(k.intValue(), Boolean.FALSE);
            }
          }
          // Addition of the component connex
          GM_Object connexComponent = new GM_Polygon(new GM_LineString(
              hullPoints));
          if (hull == null || hull.isEmpty() || !hull.isValid()) {
            hull = connexComponent;
          } else {
            IGeometry union = hull.union(connexComponent);
            if (union != null) {
              hull = union;
            }
          }
          if (!connexComponent.isValid()) {
            logger.error("non valid connex component: " + connexComponent);
          }
          nbConnexComponents++;
          // Addition of all interior points to unavailable points
          for (int k = 0; k < this.points.size(); k++) {
            if (isAvailable.get(k).equals(Boolean.FALSE)) {
              continue;
            }
            if (connexComponent.contains(new GM_Point(this.points.get(k)))) {
              isAvailable.set(k, Boolean.FALSE);
            }
          }
          break;
        }
      }
    }
    if (SwingingArmNonConvexHull.logger.isTraceEnabled()) {
      SwingingArmNonConvexHull.logger.trace("non-convex-hull containing "
          + nbConnexComponents + " connex components");
    }
    return hull;
  }
}
