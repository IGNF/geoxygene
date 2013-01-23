package fr.ign.cogit.geoxygene.util.algo.geomstructure;

import java.util.ArrayList;
import java.util.List;

import Jama.Matrix;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
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
}
