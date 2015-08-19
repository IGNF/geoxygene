package fr.ign.cogit.geoxygene.util.algo.geomstructure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Jama.Matrix;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;

public class Segment extends GM_LineSegment {

  /**
     */
  private double coefA;// straight line containing the segment equation
  // coefficents from ax + by + c = 0
  /**
     */
  private double coefB;
  /**
     */
  private double coefC;

  public Segment(IDirectPosition point1, IDirectPosition point2) {
    super(point1, point2);
    this.coefA = point2.getY() - point1.getY();
    this.coefB = point1.getX() - point2.getX();
    this.coefC = point1.getY() * (point2.getX() - point1.getX())
        + point1.getX() * (point1.getY() - point2.getY());
  }

  /**
   * @return
   */
  public double getCoefA() {
    return this.coefA;
  }

  /**
   * @return
   */
  public double getCoefB() {
    return this.coefB;
  }

  /**
   * @return
   */
  public double getCoefC() {
    return this.coefC;
  }

  public Segment getPerpendicularSegment(boolean start) {
    // computes the equation of the perpendicular line y = ax+b
    double aPrime = (getEndPoint().getY() - getStartPoint().getY())
        / (getEndPoint().getX() - getStartPoint().getX());
    double a = -1.0 / aPrime;
    if (start) {
      double b = getStartPoint().getY() - getStartPoint().getX() * a;
      double perpX = getStartPoint().getX() + 1.0;
      double perpY = a * perpX + b;
      IDirectPosition perp = new DirectPosition(perpX, perpY);
      return new Segment(getStartPoint(), perp);
    } else {
      double b = getEndPoint().getY() - getEndPoint().getX() * a;
      double perpX = getStartPoint().getX() + 1.0;
      double perpY = a * perpX + b;
      IDirectPosition perp = new DirectPosition(perpX, perpY);
      return new Segment(perp, getEndPoint());
    }
  }

  /**
   * Get the bisection line of the segment, as another segment.
   * @return
   */
  public Segment getBisectionSegment() {
    // computes the equation of the perpendicular line y = ax+b
    IDirectPosition middle = getMiddlePoint();
    double aPrime = (getEndPoint().getY() - middle.getY())
        / (getEndPoint().getX() - middle.getX());
    double a = -1.0 / aPrime;
    double b = middle.getY() - middle.getX() * a;
    double perpX = middle.getX() + 1.0;
    double perpY = a * perpX + b;
    IDirectPosition perp = new DirectPosition(perpX, perpY);
    return new Segment(middle, perp);
  }

  /**
   * Computes the intersection between two segments using the straight line
   * equations.
   * 
   * @param other the other segment eventually intersecting this
   * @return the intersection point or null if there is no intersection
   * @author GTouya
   */
  public IDirectPosition straightLineIntersection(Segment other) {
    // build a matrix to solve the equation system
    Matrix matrice = new Matrix(2, 2);
    matrice.set(0, 0, this.coefA);
    matrice.set(1, 0, other.coefA);
    matrice.set(0, 1, this.coefB);
    matrice.set(1, 1, other.coefB);
    // test if the straight lines are parallel
    if (matrice.det() == 0.0) {
      return null;
    }
    Matrix inverse = matrice.inverse();
    double xInter = -inverse.get(0, 0) * this.coefC - inverse.get(0, 1)
        * other.coefC;
    double yInter = -inverse.get(1, 0) * this.coefC - inverse.get(1, 1)
        * other.coefC;
    return new DirectPosition(xInter, yInter);
  }

  /**
   * Computes the intersection between the line prolonging the segment and a
   * given circle.
   * @param centre
   * @param radius
   * @return
   */
  public Set<IDirectPosition> intersectionWithCircle(IDirectPosition centre,
      double radius) {
    Set<IDirectPosition> intersections = new HashSet<IDirectPosition>();
    double xc = centre.getX();
    double yc = centre.getY();
    // cela revient à résoudre Ax²+Bx+C=0.0 avec
    double a = -coefA / coefB;
    double b = -coefC / coefB;
    double A = 1 + a * a;
    double B = 2.0 * (a * (b - yc) - xc);
    double C = (xc * xc + (b - yc) * (b - yc) - radius * radius);
    // on calcule le déterminant
    double delta = B * B - 4.0 * A * C;
    if (delta > 0.0) {
      double x1 = -(B + Math.sqrt(delta)) / (2.0 * A);
      double x2 = -(B - Math.sqrt(delta)) / (2.0 * A);
      // on calcule les coordonnées en y à partir de l'équation de droite
      double y1 = a * x1 + b;
      double y2 = a * x2 + b;
      intersections.add(new DirectPosition(x1, y1));
      intersections.add(new DirectPosition(x2, y2));
    } else if (delta == 0.0) {
      double x = -B / (2.0 * A);
      double y = a * x + b;
      intersections.add(new DirectPosition(x, y));
    }

    return intersections;

  }

  public double distanceToPoint(IDirectPosition point) {
    return Math.abs(this.coefA * point.getX() + this.coefB * point.getY()
        + this.coefC)
        / Math.sqrt(this.coefA * this.coefA + this.coefB * this.coefB);
  }

  /**
   * Compute the absolute orientation of the segment between [0,2Pi]. It's the
   * angle between the X axis and the segment.
   * @return
   */
  public double orientation() {
    IDirectPosition xAxisPt = new DirectPosition(
        this.getStartPoint().getX() + 10.0, this.getStartPoint().getY());
    return Angle.angleTroisPoints(xAxisPt, this.getStartPoint(),
        this.getEndPoint()).getValeur();
  }

  public void print() {
    System.out.println("start point of segment :");
    System.out.println(this.getStartPoint().toString());
    System.out.println("end point of segment :");
    System.out.println(this.getEndPoint().toString());
    System.out.println("coefficient a : " + this.coefA);
    System.out.println("coefficient b : " + this.coefB);
    System.out.println("coefficient c : " + this.coefC);

  }

  /**
   * Gets the list of Segments composing a LineString geometry.
   * 
   * @param line the line to decompose into segments
   * @return the list of segments objects composing the line
   * @author GTouya
   */
  public static List<Segment> getSegmentList(ILineString line) {
    List<Segment> segments = new ArrayList<Segment>();
    IDirectPositionList points = line.coord();
    for (int i = 1; i < points.size(); i++) {
      segments.add(new Segment(points.get(i - 1), points.get(i)));
    }
    return segments;
  }

  /**
   * Gets the list of Segments composing a IPolygon geometry starting from an
   * origin vertex. If origin is null, the first vertex is used.
   * 
   * @param line the line to decompose into segments
   * @return the list of segments objects composing the line
   * @author GTouya
   */
  public static List<Segment> getSegmentList(IPolygon polygon,
      IDirectPosition origin) {
    List<Segment> segments = new ArrayList<Segment>();
    IDirectPositionList points = polygon.coord();
    int index = 0;
    for (int i = 1; i < points.size(); i++) {
      segments.add(new Segment(points.get(i - 1), points.get(i)));
      if (points.get(i - 1).equals(origin))
        index = i - 1;
    }
    if (origin == null)
      return segments;
    if (origin.equals(points.get(0)))
      return segments;

    // swap the segments to fit with correct order
    ArrayList<Segment> swappedSegments = new ArrayList<Segment>();
    for (int i = index; i < segments.size(); i++)
      swappedSegments.add(segments.get(i));
    for (int i = 0; i < index; i++)
      swappedSegments.add(segments.get(i));
    return swappedSegments;
  }

  /**
   * Gets the list of Segments composing a IRing geometry starting from an
   * origin vertex. If origin is null, the first vertex is used.
   * 
   * @param line the line to decompose into segments
   * @return the list of segments objects composing the line
   * @author GTouya
   */
  public static List<Segment> getSegmentList(IRing ring, IDirectPosition origin) {
    List<Segment> segments = new ArrayList<Segment>();
    IDirectPositionList points = ring.coord();
    int index = 0;
    for (int i = 1; i < points.size(); i++) {
      segments.add(new Segment(points.get(i - 1), points.get(i)));
      if (points.get(i - 1).equals(origin))
        index = i - 1;
    }
    if (origin == null)
      return segments;
    if (origin.equals(points.get(0)))
      return segments;

    // swap the segments to fit with correct order
    ArrayList<Segment> swappedSegments = new ArrayList<Segment>();
    for (int i = index; i < segments.size(); i++)
      swappedSegments.add(segments.get(i));
    for (int i = 0; i < index; i++)
      swappedSegments.add(segments.get(i));
    return swappedSegments;
  }

  /**
   * Like getSegmentList but in the reverse order.
   * 
   * @param line the line to decompose into segments
   * @return the list of segments objects composing the line
   * @author GTouya
   */
  public static List<Segment> getReverseSegmentList(IPolygon polygon,
      IDirectPosition origin) {
    List<Segment> segments = new ArrayList<Segment>();
    IDirectPositionList points = polygon.coord();
    int index = 0;
    for (int i = points.size() - 2; i > 0; i--) {
      segments.add(new Segment(points.get(i), points.get(i - 1)));
      if (points.get(i).equals(origin))
        index = i;
    }
    if (origin == null)
      return segments;
    if (origin.equals(points.get(0)))
      return segments;

    // swap the segments to fit with correct order
    ArrayList<Segment> swappedSegments = new ArrayList<Segment>();
    for (int i = index; i > 0; i--)
      swappedSegments.add(segments.get(i));
    for (int i = 0; i < index; i++)
      swappedSegments.add(segments.get(i));
    return swappedSegments;
  }

  /**
   * Like getSegmentList but in the reverse order.
   * 
   * @param line the line to decompose into segments
   * @return the list of segments objects composing the line
   * @author GTouya
   */
  public static List<Segment> getReverseSegmentList(IRing ring,
      IDirectPosition origin) {
    List<Segment> segments = new ArrayList<Segment>();
    IDirectPositionList points = ring.coord();
    int index = 0;
    for (int i = points.size() - 2; i > 0; i--) {
      segments.add(new Segment(points.get(i), points.get(i - 1)));
      if (points.get(i).equals(origin))
        index = i;
    }
    if (origin == null)
      return segments;
    if (origin.equals(points.get(0)))
      return segments;

    // swap the segments to fit with correct order
    ArrayList<Segment> swappedSegments = new ArrayList<Segment>();
    for (int i = index; i > 0; i--)
      swappedSegments.add(segments.get(i));
    for (int i = 0; i < index; i++)
      swappedSegments.add(segments.get(i));
    return swappedSegments;
  }

  /**
   * Checks if a point is contained by the segment using vector calculus. Point
   * C is on segment [AB] if and only if AC and CB are colinear, and if C is
   * between A and B (verified by scalar products).
   * @param point
   * @return
   */
  public boolean containsPoint(IDirectPosition point, double tolerance) {
    Vector2D vect1 = new Vector2D(startPoint(), point);
    Vector2D vect2 = new Vector2D(point, endPoint());
    Vector2D vect3 = new Vector2D(startPoint(), endPoint());
    if (!vect1.isColinear(vect2, tolerance))
      return false;

    double kAC = vect3.prodScalaire(vect1);
    double kAB = vect3.prodScalaire(vect3);
    if (kAC < 0)
      return false;
    if (kAC > kAB)
      return false;

    return true;
  }

  /**
   * Checks if a point is contained by the segment using vector calculus. Point
   * C is on segment [AB] if and only if AC and CB are colinear, with the same
   * way and that ||AC|| < ||AB||.
   * @param point
   * @return
   */
  public boolean containsPoint(IDirectPosition point) {
    Vector2D vect1 = new Vector2D(startPoint(), point);
    Vector2D vect2 = new Vector2D(point, endPoint());
    Vector2D vect3 = new Vector2D(startPoint(), endPoint());
    if (!vect1.isColinear(vect2))
      return false;

    double kAC = vect3.prodScalaire(vect1);
    double kAB = vect3.prodScalaire(vect3);
    if (kAC < 0)
      return false;
    if (kAC > kAB)
      return false;

    return true;
  }

  public boolean lineContainsPoint(IDirectPosition point) {
    if (coefA * point.getX() + coefB * point.getY() + coefC == 0.0)
      return true;
    return false;
  }

  public boolean lineContainsPoint(IDirectPosition point, double tolerance) {
    if (Math.abs(coefA * point.getX() + coefB * point.getY() + coefC) <= tolerance)
      return true;
    return false;
  }

  /**
   * Get the point that is the middle of the segment.
   * @return
   */
  public IDirectPosition getMiddlePoint() {
    return new DirectPosition((startPoint().getX() + endPoint().getX()) / 2,
        (startPoint().getY() + endPoint().getY()) / 2);
  }

  /**
   * Get the point that is the middle of the segment, weighted by a ratio: when
   * the ratio tends to 0, the resulting point tends to the segment start point;
   * when the ratio tends to infinity, the resulting point tends to segment end
   * point; when the ratio tends to 1, the resulting point is the segment
   * middle.
   * @return
   */
  public IDirectPosition getWeightedMiddlePoint(double ratio) {
    Vector2D vect = new Vector2D(getStartPoint(), getEndPoint());
    if (ratio == 1.0)
      return this.getMiddlePoint();
    else if (ratio < 1) {
      vect.scalarMultiplication(ratio / 2.0);
      return vect.translate(getStartPoint());
    } else {
      vect.scalarMultiplication(3.0 / (2.0 * ratio));
      return vect.translate(getStartPoint());
    }
  }

  /**
   * Extend a segment by a given length at both extremities.
   * @param length
   * @return
   */
  public Segment extendAtExtremities(double length) {
    // first, extend start point
    Vector2D vect = new Vector2D(this.getEndPoint(), this.getStartPoint());
    Vector2D normalisedVect = vect.changeNorm(length);
    IDirectPosition startPt = normalisedVect.translate(getStartPoint());
    // then, extend end point
    IDirectPosition endPt = normalisedVect.opposite().translate(getEndPoint());
    return new Segment(startPt, endPt);
  }
}
