package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.hulls;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.algorithm.ConvexHull;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

/**
 * Computation of a convex hull on a list of points
 * @author J. Renard 11/08/2009
 * 
 */
public class PointsConvexHull {
  private final static Logger logger = LogManager.getLogger(PointsConvexHull.class
      .getName());

  /**
   * Method launch: The idea is to create a Delaunay Triangulation of the
   * points, and to unify all triangles to create the convex hull
   */

  public static IPolygon compute(IDirectPositionList points) {

    Coordinate[] coords = new Coordinate[points.size()];
    int i = 0;
    for (IDirectPosition point : points) {
      coords[i] = new Coordinate(point.getX(), point.getY());
      i++;
    }

    ConvexHull convexHull = new ConvexHull(coords, new GeometryFactory());
    IGeometry hull = null;
    try {
      hull = JtsGeOxygene.makeGeOxygeneGeom(convexHull.getConvexHull());
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (hull == null || !(hull instanceof IPolygon)) {
      PointsConvexHull.logger.debug("Non valid convex hull");
      return new GM_Polygon(new GM_LineString(new DirectPositionList()));
    }

    return (IPolygon) hull;

  }

}
