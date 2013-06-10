package fr.ign.cogit.cartagen.genealgorithms.points;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.common.triangulation.Triangulation;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationPoint;
import fr.ign.cogit.cartagen.graph.triangulation.TriangulationTriangle;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationPointImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationSegmentFactoryImpl;
import fr.ign.cogit.cartagen.graph.triangulation.impl.TriangulationTriangleFactoryImpl;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * Computation of a non convex hull using Delaunay triangulation and edge
 * removal
 * @author J. Renard (d'après M. Duckham) 11/08/2009
 * 
 */
public class DelaunayNonConvexHull {
  private final static Logger logger = Logger
      .getLogger(DelaunayNonConvexHull.class.getName());

  /**
   * Points to construct the hull
   */
  private IDirectPositionList points;

  /**
   * Minimal edge length to stop the algorithm
   */
  private double minLength = 0.0;

  /**
   * Constructor
   */
  public DelaunayNonConvexHull(IDirectPositionList points, double minLength) {
    this.points = points;
    this.minLength = minLength;
  }

  /**
   * Method launch The idea is to create a Delaunay Triangulation of the
   * geographic object, and to eliminate one by one the longest segments (and
   * their associated triangles) on the boundary of the hull, in order to reduce
   * its spatial shape to the very structure of the points of the object The
   * algorithm stops in 4 cases: - a point is put outside of the hull - the hull
   * is not regular anymore (ie. contains bridging edges) - the hull doesn't
   * satisfy Jordan criteria anymore (ie. contains bridging points) - minimal
   * edge length removal is reached
   */

  public IGeometry compute() {

    List<TriangulationPoint> triPoints = new ArrayList<TriangulationPoint>();
    for (IDirectPosition dp : this.points) {
      if (this.contains(triPoints, dp)) {
        continue;
      }
      triPoints.add(new TriangulationPointImpl(dp));
    }
    if (triPoints.isEmpty()) {
      if (DelaunayNonConvexHull.logger.isTraceEnabled()) {
        DelaunayNonConvexHull.logger.trace("No point to construct the hull");
      }
      return null;
    }

    if (DelaunayNonConvexHull.logger.isTraceEnabled()) {
      DelaunayNonConvexHull.logger
          .trace("Computation of the non-convex hull using Delaunay Triangulation and edge removal");
    }
    int nbTrianglesSuppressed = 0;

    // Triangulation
    Triangulation tri = new Triangulation(triPoints,
        new TriangulationSegmentFactoryImpl(),
        new TriangulationTriangleFactoryImpl());
    tri.compute(true);
    Collection<TriangulationTriangle> triangles = tri.getTriangles();

    // Construction of the convex hull
    IGeometry hull = new GM_Polygon(new GM_LineString(new DirectPositionList()));
    if (triangles.isEmpty()) {
      return hull;
    }
    Iterator<TriangulationTriangle> markTri = triangles.iterator();
    while (markTri.hasNext()) {
      TriangulationTriangle tritri = markTri.next();
      if (hull.isEmpty()) {
        hull = tritri.getGeom();
      } else {
        hull = hull.union(tritri.getGeom());
      }
    }

    // Boolean list for triangles available for suppression
    ArrayList<String> isAvailable = new ArrayList<String>(triangles.size());
    for (int i = 0; i < triangles.size(); i++) {
      isAvailable.add("true");
    }
    ArrayList<TriangulationTriangle> arrayTri = new ArrayList<TriangulationTriangle>(
        triangles.size());

    // Loop until no more triangles are available to be suppressed
    while (isAvailable.contains("true")) {

      double maxLength = this.minLength;
      TriangulationTriangle triangleToSuppress = null;

      // Search for the triangle to suppress, those who contains the longest
      // segment on the boundary of the non-convex hull
      Iterator<TriangulationTriangle> markTriangles = triangles.iterator();
      while (markTriangles.hasNext()) {
        TriangulationTriangle triangle = markTriangles.next();
        arrayTri.add(triangle);

        // triangle unavailable for suppression
        if (isAvailable.get(arrayTri.indexOf(triangle)) == "false") {
          continue;
        }

        // Regularity criteria of the hull
        // If 3 points of a triangle are on the boundary of the hull (eg.
        // intersects it)
        // The triangle removal would create a non-regular hull (eg. with
        // disjoint parts or with a point outside the hull)
        if (((IPolygon) hull).getExterior().intersects(
            triangle.getPoint1().getGeom())
            && ((IPolygon) hull).getExterior().intersects(
                triangle.getPoint2().getGeom())
            && ((IPolygon) hull).getExterior().intersects(
                triangle.getPoint3().getGeom())) {
          continue;
        }

        // If the triangle is totally on the boundary of the hull, two of its
        // points intersects the hull
        // 1st case
        if (((IPolygon) hull).getExterior().intersects(
            triangle.getPoint1().getGeom())
            && ((IPolygon) hull).getExterior().intersects(
                triangle.getPoint2().getGeom())) {
          double length = triangle.getPoint1().getGeom().distance(
              triangle.getPoint3().getGeom());
          if (length > maxLength) {
            maxLength = length;
            triangleToSuppress = triangle;
          }
        }
        // 2nd case
        else if (((IPolygon) hull).getExterior().intersects(
            triangle.getPoint1().getGeom())
            && ((IPolygon) hull).getExterior().intersects(
                triangle.getPoint3().getGeom())) {
          double length = triangle.getPoint1().getGeom().distance(
              triangle.getPoint3().getGeom());
          if (length > maxLength) {
            maxLength = length;
            triangleToSuppress = triangle;
          }
        }
        // 3rd case
        else if (((IPolygon) hull).getExterior().intersects(
            triangle.getPoint3().getGeom())
            && ((IPolygon) hull).getExterior().intersects(
                triangle.getPoint2().getGeom())) {
          double length = triangle.getPoint3().getGeom().distance(
              triangle.getPoint2().getGeom());
          if (length > maxLength) {
            maxLength = length;
            triangleToSuppress = triangle;
          }
        } else {
          continue;
        }

      }

      // No triangle to suppress anymore -> end of the algorithm
      if (triangleToSuppress == null || maxLength == this.minLength) {
        break;
      }

      // Suppression of the selected triangle and update of the non-convex hull
      hull = hull.difference(triangleToSuppress.getGeom());
      if (!hull.isPolygon()) {
        hull = hull.union(triangleToSuppress.getGeom());
        break;
      }
      isAvailable.set(arrayTri.indexOf(triangleToSuppress), "false");
      nbTrianglesSuppressed++;

    }
    if (DelaunayNonConvexHull.logger.isTraceEnabled()) {
      DelaunayNonConvexHull.logger.trace(nbTrianglesSuppressed
          + " triangles suppressed from the Delaunay Triangulation");
    }
    return hull;
  }

  /**
   * checks if a points list contains a point located at the same position as
   * another one
   * 
   * @param points
   * @param point
   * @return
   */
  private boolean contains(List<TriangulationPoint> triPoints,
      IDirectPosition dp) {
    for (TriangulationPoint p : triPoints) {
      if (p.getPosition().getX() == dp.getX()
          && p.getPosition().getY() == dp.getY()) {
        return true;
      }
    }
    return false;
  }

}
