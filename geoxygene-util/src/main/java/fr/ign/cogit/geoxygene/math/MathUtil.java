package fr.ign.cogit.geoxygene.math;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector2d;

/**
 * tool static class for math/point/vector computation 
 * @author JeT
 *
 */
public final class MathUtil {

  private static final double epsilon = 1.E-6; // default epsilon value used when none are given

  /**
   * Private constructor
   */
  private MathUtil() {
    // utility class
  }

  /**
   * Compute the vector which is perpendicular to the p1p2 vector
   * @param p1 first point
   * @param p2 second point
   * @return n unit vector where n scalar p1p2 = 0 
   */
  public static Vector2d computeNormal(final Point2d p1, final Point2d p2) {

    Vector2d normal = new Vector2d(p1.y - p2.y, p2.x - p1.x);
    // normalization if not null (Vector2d.normalize() method returns NaN if norm is 0!
    double norm = norm(normal);
    if (norm > 1.E-6) {
      normal.x /= norm;
      normal.y /= norm;
    }
    return normal;

  }

  /**
   * Compute the vector which is perpendicular to the v vector
   * @param v vector to find normal
   * @return n unit vector where n scalar v = 0 
   */
  public static Vector2d computeNormal(final Vector2d v) {

    Vector2d normal = new Vector2d(-v.y, v.x);
    double norm = norm(normal);
    if (norm > 1.E-6) {
      normal.x /= norm;
      normal.y /= norm;
    }
    return normal;

  }

  /**
   * Compute the norm of a vector
   * @param v vector to compute norm
   * @return the vector norm
   */
  public static double norm(final Vector2d v) {
    return Math.sqrt(v.x * v.x + v.y * v.y);
  }

  /**
   * Compute the squared norm of a vector
   * @param v vector to compute squared norm
   * @return the vector squared norm
   */
  public static double norm2(final Vector2d v) {
    return v.x * v.x + v.y * v.y;
  }

  /**
   * Compute the intersection of 2 lines described by a point and a vector.
   * If there is no or infinite number of intersection, returns null
   * @param p1 first line point
   * @param v1 first line vector
   * @param p2 second line point
   * @param v2 second line vector
   * @return the intersection point or null if lines are quite parallel
   */
  public static Point2d intersectionPoint(final Point2d p1, final Vector2d v1, final Point2d p2, final Vector2d v2) {
    return intersectionPoint(p1, v1, p2, v2, MathUtil.epsilon);
  }

  /**
   * Compute the intersection of 2 lines described by a point and a vector.
   * If there is no or infinite number of intersection, returns null
   * @param p1 first line point
   * @param v1 first line vector
   * @param p2 second line point
   * @param v2 second line vector
   * @param epsilon min value of the dot product v1.v2 which tells if lines are intersecting or not
   * @return the intersection point or null if lines are quite parallel
   */
  public static Point2d intersectionPoint(final Point2d p1, final Vector2d v1, final Point2d p2, final Vector2d v2, final double epsilon) {
    double denom = cross(v1, v2);
    if (isZero(denom, epsilon)) {
      return null;
    }
    double t2 = -((p2.y - p1.y) * v1.x - v1.y * (p2.x - p1.x)) / denom;
    return new Point2d(p2.x + t2 * v2.x, p2.y + t2 * v2.y);
  }

  /**
   * @return true if x is quite equal to zero ( -eps < x < eps)
   */
  public static boolean isZero(final double x, final double epsilon) {
    return Math.abs(x) < epsilon;
  }

  /**
   * Compute the dot product between two vectors
   * @param v1 first vector
   * @param v2 second vector
   * @return v1x * v2x + v1y * v2y 
   */
  public static double dot(final Vector2d v1, final Vector2d v2) {
    return v1.x * v2.x + v1.y * v2.y;
  }

  /**
   * Compute the cross product between two vectors
   * @param v1 first vector
   * @param v2 second vector
   * @return v1x * v2y - v1y * v2x 
   */
  public static double cross(final Vector2d v1, final Vector2d v2) {
    return v1.x * v2.y - v1.y * v2.x;
  }

  /**
   * Compute a point on a parametric line
   * @param p start point of the parametric line
   * @param v direction vector of the parametric line
   * @param t parameter value on the line
   * @return a newly created point on the line ( = p + v * t)
   */
  public static Point2d pointOfLine(final Point2d p, final Vector2d v, final double t) {
    Point2d pt = new Point2d();
    return pointOfLine(pt, p, v, t);
  }

  /**
   * Fill the given point with computed coordinates of a point on a parametric line
   * @param p start point of the parametric line
   * @param v direction vector of the parametric line
   * @param t parameter value on the line
   * @return the given point
   */
  public static Point2d pointOfLine(final Point2d p2Fill, final Point2d p, final Vector2d v, final double t) {
    p2Fill.x = p.x + t * v.x;
    p2Fill.y = p.y + t * v.y;
    return p2Fill;
  }

  /**
   * compute a vector coordinates between two points
   * @param v vector to fill
   * @param pA origin vector point
   * @param pB end vector point
   */
  public static Vector2d vector(final Vector2d v, final Point2d pA, final Point2d pB) {
    v.x = pB.x - pA.x;
    v.y = pB.y - pA.y;
    return v;
  }

  /**
   * compute a vector coordinates between two points
   * @param pA origin vector point
   * @param pB end vector point
   */
  public static Vector2d vector(final Point2d pA, final Point2d pB) {
    return new Vector2d(pB.x - pA.x, pB.y - pA.y);
  }

  /**
   * add two points
   * @param p point to fill
   * @param pA first point to add
   * @param pB second point to add
   */
  public static Point2d add(final Point2d p, final Point2d pA, final Tuple2d pB) {
    p.x = pB.x + pA.x;
    p.y = pB.y + pA.y;
    return p;
  }

  /**
   * add two points
   * @param p point to fill
   * @param x1Pixel first point to add
   * @param nPixel second point to add
   */
  public static Point2d add(final Point2d pA, final Tuple2d v) {
    return add(new Point2d(), pA, v);
  }

  /**
   * sub two points
   * @param p point to fill
   * @param pA first point to sub
   * @param pB second point to sub
   */
  public static Point2d sub(final Point2d p, final Point2d pA, final Point2d pB) {
    p.x = pA.x - pB.x;
    p.y = pA.y - pB.y;
    return p;
  }

  /**
   * sub two points
   * @param p point to fill
   * @param pA first point to sub
   * @param pB second point to sub
   */
  public static Point2d sub(final Point2d pA, final Point2d pB) {
    return sub(new Point2d(), pA, pB);
  }

  /**
   * div two points
   * @param p point to fill
   * @param pA first point to div
   * @param x divider
   */
  public static Point2d div(final Point2d p, final Point2d pA, final double x) {
    p.x = pA.x / x;
    p.y = pA.y / x;
    return p;
  }

  /**
   * div two points
   * @param p point to fill
   * @param pA first point to div
   * @param x divider
   */
  public static Point2d div(final Point2d pA, final double x) {
    return div(new Point2d(), pA, x);
  }

  /**
   * compute the mean (average) of a point array
   * @param points points
   * @return a newly created point = sum (points) / point count
   */
  public static Point2d mean(final Point2d... points) {
    Point2d sum = new Point2d();
    for (Point2d p : points) {
      add(sum, sum, p);
    }
    return div(sum, sum, points.length);
  }
}