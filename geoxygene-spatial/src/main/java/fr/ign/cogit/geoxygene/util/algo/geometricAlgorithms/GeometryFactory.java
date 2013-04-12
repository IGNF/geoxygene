package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * This class is a factory that contains static methods to build specific kind
 * of geometries as IPolygon or ILineString (e.g. a circle, a triangle).
 * @author GTouya
 * 
 */
public class GeometryFactory {

  /**
   * Creates a Circle polygon from its centre and radius. Uses a buffer to
   * create the circle so the number of segments of the polygon is required.
   * @param centre the centre of the polygon circle
   * @param radius the radius of the circle
   * @param nbSegments the number of segments of the polygon circle
   * @return the IPolygon circle geometry
   */
  public static IPolygon buildCircle(IDirectPosition centre, double radius,
      int nbSegments) {
    return (IPolygon) centre.toGM_Point().buffer(radius, nbSegments);
  }

  /**
   * Creates a Parallelogram polygon from a segment translated by a vector.
   * 
   * @param segment the segment base of the parallelogram
   * @param vect the vector to translate the segment
   * @return a parallelogram IPolygon geometry
   * @author GTouya
   */
  public static IPolygon buildParallelogram(ILineSegment segment, Vecteur vect) {
    IDirectPositionList points = new DirectPositionList();
    points.add(segment.startPoint());
    points.add(segment.endPoint());
    points.add(vect.translate(segment.endPoint()));
    points.add(vect.translate(segment.startPoint()));
    points.add(segment.startPoint());
    return new GM_Polygon(new GM_LineString(points));
  }

  /**
   * Creates a triangle polygon from three points.
   * 
   * @param segment the segment base of the parallelogram
   * @param vect the vector to translate the segment
   * @return a parallelogram IPolygon geometry
   * @author GTouya
   */
  public static IPolygon buildTriangle(IDirectPosition pt1,
      IDirectPosition pt2, IDirectPosition pt3) {
    IDirectPositionList points = new DirectPositionList();
    points.add(pt1);
    points.add(pt2);
    points.add(pt3);
    points.add(pt1);
    return new GM_Polygon(new GM_LineString(points));
  }

  /**
   * Builds a sub-line from a line and two of its vertices.
   * @param line
   * @param pt1
   * @param pt2
   * @return
   */
  public static ILineString buildSubLine(ILineString line, IDirectPosition pt1,
      IDirectPosition pt2) {
    IDirectPositionList list = new DirectPositionList();
    boolean add = false;
    boolean start = true;
    for (IDirectPosition pt : line.coord()) {
      // case of a closed line, skip first point
      if (start) {
        start = false;
        if (pt.equals2D(pt2))
          continue;
      }
      // general case
      if (pt.equals2D(pt1))
        add = true;
      if (add)
        list.add(pt);
      if (pt.equals2D(pt2))
        break;
    }
    return new GM_LineString(list);
  }

  /**
   * Creates a rectangle polygon parallel to axes from the upper left corner,
   * the length (x coordinate difference) and the width (y coordinate
   * difference).
   * 
   * @param upperLeftCorner the segment base of the parallelogram
   * @param length the length of the X axis parallel side of the rectangle
   * @param width the length of the Y axis parallel side of the rectangle
   * @return a rectangle IPolygon geometry
   * @author GTouya
   */
  public static IPolygon buildRectangle(IDirectPosition upperLeftCorner,
      double length, double width) {
    IDirectPositionList points = new DirectPositionList();
    points.add(upperLeftCorner);
    points.add(new DirectPosition(upperLeftCorner.getX() + length,
        upperLeftCorner.getY()));
    points.add(new DirectPosition(upperLeftCorner.getX() + length,
        upperLeftCorner.getY() - width));
    points.add(new DirectPosition(upperLeftCorner.getX(), upperLeftCorner
        .getY() - width));
    points.add(upperLeftCorner);
    return new GM_Polygon(new GM_LineString(points));
  }

  /**
   * Creates a regular hexagon parallel to the X axis. Width being the
   * perpendicular distances from one side to the side opposite.
   * @param center
   * @param width
   * @return
   */
  public static IPolygon buildHexagon(IDirectPosition center, double width) {
    if (width == 0.0)
      return null;
    IDirectPositionList points = new DirectPositionList();
    // add first point
    IDirectPosition first = new DirectPosition(center.getX() - (width / 2),
        center.getY());
    points.add(first);
    // add second point
    points.add(new DirectPosition(center.getX() - (width / 4), (Math.sqrt(3)
        * width / 4)
        + center.getY()));
    // add third point
    points.add(new DirectPosition(center.getX() + (width / 4), (Math.sqrt(3)
        * width / 4)
        + center.getY()));
    // add fourth point
    points.add(new DirectPosition(center.getX() + (width / 2), center.getY()));
    // add fifth point
    points.add(new DirectPosition(center.getX() + (width / 4), center.getY()
        - (Math.sqrt(3) * width / 4)));
    // add sixth point
    points.add(new DirectPosition(center.getX() - (width / 4), center.getY()
        - (Math.sqrt(3) * width / 4)));
    // close the polygon
    points.add(first);
    return new GM_Polygon(new GM_LineString(points));
  }
}
