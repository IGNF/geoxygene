package fr.ign.cogit.geoxygene.util.algo;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * @author jGaffuri
 * 
 */
public class SmallestSurroundingRectangleComputation {
  private static Logger logger = Logger
      .getLogger(SmallestSurroundingRectangleComputation.class.getName());

  /**
   * @param geom
   * @return The smallest surrounding rectangle of a geometry
   */
  public static IPolygon getSSR(IGeometry geom) {
    // conversion JTS/geoxygene
    IPolygon poly = null;
    try {
      poly = (IPolygon) AdapterFactory
          .toGM_Object(SmallestSurroundingRectangleComputation
              .getSSR(AdapterFactory.toGeometry(new GeometryFactory(), geom)));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return poly;
  }

  /**
   * @param geom
   * @return The smallest surrounding rectangle of a geometry
   */
  public static Polygon getSSR(Geometry geom) {

    if (SmallestSurroundingRectangleComputation.logger.isTraceEnabled()) {
      SmallestSurroundingRectangleComputation.logger
          .trace("SSR computation of " + geom);
    }
    if (SmallestSurroundingRectangleComputation.logger.isTraceEnabled()) {
      SmallestSurroundingRectangleComputation.logger
          .trace("Convex hull computation");
    }
    Geometry hull_ = geom.convexHull();

    // Convex hull is not a polygon; the SSR is not defined: return null
    if (!(hull_ instanceof Polygon)) {
      SmallestSurroundingRectangleComputation.logger
          .warn("WARN in SSR computation of "
              + geom
              + ": convex hull computation returned something else as a polygon: "
              + hull_);
      return null;
    }
    Polygon convHull = (Polygon) hull_;

    // center coordinates (for rotation)
    Coordinate rotationCenter = geom.getCentroid().getCoordinate();

    // get convex hull coordinates
    Coordinate[] coord = convHull.getExteriorRing().getCoordinates();

    // go through the segments
    double minArea = Double.MAX_VALUE, minAngle = 0.0;
    Polygon ssr = null;
    for (int i = 0; i < coord.length - 1; i++) {
      // compute the rectangular hull of the rotated convew hull

      // compute the angle value
      double angle = Math.atan2(coord[i + 1].y - coord[i].y, coord[i + 1].x
          - coord[i].x);

      Polygon rect = (Polygon) CommonAlgorithms.rotation(convHull,
          rotationCenter, -1.0 * angle).getEnvelope();
      if (SmallestSurroundingRectangleComputation.logger.isTraceEnabled()) {
        SmallestSurroundingRectangleComputation.logger
            .trace("   rectangle hull try: " + rect);
      }

      // compute the rectangle area
      double area = rect.getArea();

      // check if it is minimum
      if (area < minArea) {
        minArea = area;
        ssr = rect;
        minAngle = angle;
      }
    }
    return CommonAlgorithms.rotation(ssr, rotationCenter, minAngle);
  }

  /**
   * @param geom
   * @return The smallest surrounding rectangle scaled to preserve its area
   */
  public static Polygon getSSRPreservedArea(Geometry geom) {
    return SmallestSurroundingRectangleComputation.getSSRGoalArea(geom,
        geom.getArea());
  }

  /**
   * @param geom
   * @return The smallest surrounding rectangle scaled to preserve its area
   */
  public static IPolygon getSSRPreservedArea(IGeometry geom) {
    return SmallestSurroundingRectangleComputation.getSSRGoalArea(geom,
        geom.area());
  }

  /**
   * @param geom
   * @param goalArea
   * @return The smallest surrounding rectangle of a geometry scaled to a given
   *         goal area
   */
  public static Polygon getSSRGoalArea(Geometry geom, double goalArea) {

    if (SmallestSurroundingRectangleComputation.logger.isDebugEnabled()) {
      SmallestSurroundingRectangleComputation.logger.debug("SSR computation");
    }
    Polygon ssr = SmallestSurroundingRectangleComputation.getSSR(geom);

    if (SmallestSurroundingRectangleComputation.logger.isDebugEnabled()) {
      SmallestSurroundingRectangleComputation.logger.debug("SSR homothetie");
    }
    return CommonAlgorithms.homothetie(ssr,
        (float) Math.sqrt(goalArea / ssr.getArea()));
  }

  /**
   * @param geom
   * @param goalArea
   * @return The smallest surrounding rectangle of a geometry scaled to a given
   *         goal area
   */
  public static IPolygon getSSRGoalArea(IGeometry geom, double goalArea) {
    IPolygon ppre = SmallestSurroundingRectangleComputation.getSSR(geom);
    if (ppre == null) {
      return null;
    }

    return CommonAlgorithms.homothetie(ppre,
        (float) Math.sqrt(goalArea / ppre.area()));
  }

}
