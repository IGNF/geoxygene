package fr.ign.cogit.geoxygene.util.algo.geomstructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;

/**
 * Class to store and handle mathematical triangles
 * @author GTouya
 * 
 */
public class Triangle {

  private IDirectPosition pt1, pt2, pt3;

  public Triangle(IDirectPosition pt1, IDirectPosition pt2, IDirectPosition pt3) {
    super();
    this.pt1 = pt1;
    this.pt2 = pt2;
    this.pt3 = pt3;
  }

  public IDirectPosition getPt1() {
    return pt1;
  }

  public void setPt1(IDirectPosition pt1) {
    this.pt1 = pt1;
  }

  public IDirectPosition getPt2() {
    return pt2;
  }

  public void setPt2(IDirectPosition pt2) {
    this.pt2 = pt2;
  }

  public IDirectPosition getPt3() {
    return pt3;
  }

  public void setPt3(IDirectPosition pt3) {
    this.pt3 = pt3;
  }

  public Collection<IDirectPosition> getPoints() {
    Collection<IDirectPosition> points = new HashSet<IDirectPosition>();
    points.add(pt1);
    points.add(pt2);
    points.add(pt3);
    return points;
  }

  /**
   * Compute the area of the triangle using the vector product method.
   * @return
   */
  public double area() {
    Vector2D vect1 = new Vector2D(pt1, pt2);
    Vector2D vect2 = new Vector2D(pt1, pt3);
    return 0.5 * vect1.prodVectoriel(vect2).norme();
  }

  /**
   * Compute the circumscribed circle of {@code this} triangle.
   * @return
   */
  public Circle getCircumscribedCircle() {
    // first compute the radius of the circumscribed circle
    double dist = pt2.distance2D(pt3);
    double angle = Angle.angleTroisPoints(pt3, pt1, pt2).getValeur();
    double radius = Math.abs(dist / (2 * Math.sin(angle)));
    // then, compute the center of the circle
    List<Segment> segments = getSegments();
    Segment bisection1 = segments.get(0).getBisectionSegment();
    Segment bisection2 = segments.get(1).getBisectionSegment();
    IDirectPosition center = bisection1.straightLineIntersection(bisection2);
    return new Circle(radius, center);
  }

  /**
   * Get the segments of the triangle.
   * @return
   */
  public List<Segment> getSegments() {
    List<Segment> segments = new ArrayList<Segment>();
    segments.add(new Segment(pt1, pt2));
    segments.add(new Segment(pt1, pt3));
    segments.add(new Segment(pt3, pt2));

    return segments;
  }
}
