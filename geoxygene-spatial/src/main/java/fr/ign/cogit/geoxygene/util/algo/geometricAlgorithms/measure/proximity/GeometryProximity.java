package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

public class GeometryProximity {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private IGeometry geom1, geom2;
  private double distance;
  /**
   * the point of geom1 (resp. geom2) the closest to geom2 (resp. geom1)
   */
  private IDirectPosition point1, point2;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public GeometryProximity(IGeometry geom1, IGeometry geom2) {
    this.geom1 = geom1;
    this.geom2 = geom2;
    this.point1 = CommonAlgorithms.getNearestPoint(geom1, geom2);
    this.point2 = CommonAlgorithms.getNearestPoint(geom2, geom1);
    this.distance = this.point1.distance(this.point2);
  }

  // Getters and setters //
  public IGeometry getGeom1() {
    return this.geom1;
  }

  public void setGeom1(IGeometry geom1) {
    this.geom1 = geom1;
  }

  public IGeometry getGeom2() {
    return this.geom2;
  }

  public void setGeom2(IGeometry geom2) {
    this.geom2 = geom2;
  }

  public double getDistance() {
    return this.distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public IDirectPosition getPoint1() {
    return this.point1;
  }

  public void setPoint1(IDirectPosition point1) {
    this.point1 = point1;
  }

  public IDirectPosition getPoint2() {
    return this.point2;
  }

  public void setPoint2(IDirectPosition point2) {
    this.point2 = point2;
  }

  // Other public methods //
  public ILineSegment toSegment() {
    return new GM_LineSegment(this.point1, this.point2);
  }

  public ILineSegment toOverlapSegment() {
    if (!(geom1 instanceof IPolygon))
      return null;
    if (!(geom2 instanceof IPolygon))
      return null;

    IDirectPosition centre1 = geom1.centroid();
    IDirectPosition centre2 = geom2.centroid();
    // test intersection
    if (!geom1.intersects(geom2)) {
      return null;
    }

    // builds the intersection
    IGeometry inter = geom1.intersection(geom2);

    // Work with the convex hull of the intersection to make sure it is
    // a simple area.
    IPolygon interHull = (IPolygon) inter.convexHull();

    // Shift the segment to make sure it passes through the intersection
    IDirectPosition centreHull = interHull.centroid();
    IDirectPosition mid = Operateurs.milieu(centre1, centre2);

    // build a small segment between the two centres of gravity
    ILineSegment segment = new GM_LineSegment(centre1, centre2);
    segment = (ILineSegment) new Vector2D(centreHull.getX() - mid.getX(),
        centreHull.getY() - mid.getY()).translate(segment);

    // combine the segment with the intersection of the two geometries
    IGeometry inter2 = segment.intersection(interHull);
    return (ILineSegment) inter2;
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
