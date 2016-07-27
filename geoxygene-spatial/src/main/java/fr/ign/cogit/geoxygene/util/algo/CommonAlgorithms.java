package fr.ign.cogit.geoxygene.util.algo;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.operation.buffer.BufferBuilder;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

/**
 * @author julien Gaffuri 2 févr. 07
 * 
 */
public class CommonAlgorithms {
  private static Logger logger = Logger
      .getLogger(CommonAlgorithms.class.getName());

  /**
   * determine the farest point of a polygon to another given point the polygon
   * must be convex and without hole
   * 
   * @param pt a point
   * @param poly a convex polygon without hole
   */
  public static Point getPointLePlusLoin(Point pt, Polygon poly) {
    Point pt_max = poly.getExteriorRing().getPointN(0);
    double dist_max = pt.distance(pt_max);
    for (int i = 1; i < poly.getExteriorRing().getNumPoints(); i++) {
      double dist = pt.distance(poly.getExteriorRing().getPointN(i));
      if (dist > dist_max) {
        pt_max = poly.getExteriorRing().getPointN(i);
        dist_max = dist;
      }
    }
    return pt_max;
  }

  /**
   * le point de geom1 le plus proche de geom2
   * 
   * @param geom1
   * @param geom2
   * @return
   */
  public static IDirectPosition getNearestPoint(IGeometry geom1,
      IGeometry geom2) {

    // conversion en geometrie JTS
    Geometry geom1_ = null;
    Geometry geom2_ = null;
    try {
      geom1_ = AdapterFactory.toGeometry(new GeometryFactory(), geom1);
      geom2_ = AdapterFactory.toGeometry(new GeometryFactory(), geom2);
    } catch (Exception e) {
      return null;
    }

    // recupere points les plus proches
    Coordinate[] cp = new DistanceOp(geom1_, geom2_).nearestPoints();

    return new DirectPosition(cp[0].x, cp[0].y, cp[0].z);
  }

  /**
   * Gets the nearest point on an envelope from a direct position. Partition
   * space around the envelope into regions. Returns the initial point if it's
   * inside the envelope.
   * 
   * @param point
   * @param env
   * @return
   */
  public static IDirectPosition getNearestPoint(IDirectPosition point,
      IEnvelope env) {
    double xMin = env.getLowerCorner().getX();
    double xMax = env.getUpperCorner().getX();
    double yMin = env.getLowerCorner().getY();
    double yMax = env.getUpperCorner().getY();
    if (point.getX() < xMin) { // Region I, VIII, or VII
      if (point.getY() < yMin) { // I
        return env.getLowerCorner();
      } else if (point.getY() > yMax) { // VII
        return new DirectPosition(xMin, yMax);
      } else { // VIII
        return new DirectPosition(xMin, point.getY());
      }
    } else if (point.getX() > xMax) { // Region III, IV, or V
      if (point.getY() < yMin) { // III
        return new DirectPosition(xMax, yMin);
      } else if (point.getY() > yMax) { // V
        return env.getUpperCorner();
      } else { // IV
        return new DirectPosition(xMax, point.getY());
      }
    } else { // Region II, IX, or VI
      if (point.getY() < yMin) { // II
        return new DirectPosition(point.getX(), yMin);
      } else if (point.getY() > yMax) { // VI
        return new DirectPosition(point.getX(), yMax);
      } else { // IX
        return point;
      }
    }
  }

  /**
   * Get the nearest points between two envelopes, the first one being on env1
   * and the second one being on env2. Returns an empty list if envelopes
   * intersect.
   * @param env1
   * @param env2
   * @return
   */
  public static IDirectPositionList getNearestPoints(IEnvelope env1,
      IEnvelope env2) {
    IDirectPositionList list = new DirectPositionList();
    if (!env1.intersects(env2)) {
      if (env1.maxY() < env2.minY()) {
        if (env1.maxX() < env2.minX()) {
          list.add(env1.getUpperCorner());
          list.add(env2.getLowerCorner());
        } else if (env1.maxX() < env2.maxX()) {
          list.add(env1.getUpperCorner());
          list.add(new DirectPosition(env1.maxX(), env2.minY()));
        } else if (env1.minX() < env2.maxX()) {
          list.add(new DirectPosition(env1.minX(), env1.maxY()));
          list.add(new DirectPosition(env1.minX(), env2.minY()));
        } else {
          list.add(new DirectPosition(env1.minX(), env1.maxY()));
          list.add(new DirectPosition(env2.maxX(), env2.minY()));
        }
      } else if (env1.maxY() < env2.maxY()) {
        if (env1.maxX() < env2.minX()) {
          list.add(new DirectPosition(env1.maxX(), env1.maxY()));
          list.add(new DirectPosition(env2.minX(), env1.maxY()));
        } else {
          list.add(new DirectPosition(env1.minX(), env1.maxY()));
          list.add(new DirectPosition(env2.maxX(), env1.maxY()));
        }
      } else {
        if (env1.maxX() < env2.minX()) {
          list.add(new DirectPosition(env1.maxX(), env1.minY()));
          list.add(new DirectPosition(env2.minX(), env2.maxY()));
        } else if (env1.maxX() < env2.maxX()) {
          list.add(new DirectPosition(env1.maxX(), env1.minY()));
          list.add(new DirectPosition(env1.maxX(), env2.maxY()));
        } else if (env1.minX() < env2.maxX()) {
          list.add(new DirectPosition(env1.minX(), env1.minY()));
          list.add(new DirectPosition(env1.minX(), env2.maxY()));
        } else {
          list.add(env1.getLowerCorner());
          list.add(env2.getUpperCorner());
        }
      }
    }
    return list;
  }

  // angle: angle de la direction de l'affinite, a partir de l'axe des x
  public static LineString affinite(LineString ls, Coordinate c, double angle,
      double coef) {
    // rotation
    LineString rot = CommonAlgorithms.rotation(ls, c, -1.0 * angle);

    Coordinate[] coord = rot.getCoordinates();
    Coordinate[] coord_ = new Coordinate[coord.length];
    for (int i = 0; i < coord.length; i++) {
      coord_[i] = new Coordinate(c.x + coef * (coord[i].x - c.x), coord[i].y);
    }

    return CommonAlgorithms
        .rotation(new GeometryFactory().createLineString(coord_), c, angle);
  }

  // angle: angle de la direction de l'affinite, a partir de l'axe des x
  public static Polygon affinite(Polygon geom, Coordinate c, double angle,
      double coef) {
    // pivote le polygone
    Polygon rot = CommonAlgorithms.rotation(geom, c, -1.0 * angle);

    GeometryFactory gf = new GeometryFactory();

    // le contour externe
    Coordinate[] coord = rot.getExteriorRing().getCoordinates();
    Coordinate[] coord_ = new Coordinate[coord.length];
    for (int i = 0; i < coord.length; i++) {
      coord_[i] = new Coordinate(c.x + coef * (coord[i].x - c.x), coord[i].y);
    }
    LinearRing lr = new LinearRing(new CoordinateArraySequence(coord_), gf);

    // les trous
    LinearRing[] trous = new LinearRing[rot.getNumInteriorRing()];
    for (int j = 0; j < rot.getNumInteriorRing(); j++) {
      Coordinate[] hole_coord = rot.getInteriorRingN(j).getCoordinates();
      Coordinate[] hole_coord_ = new Coordinate[hole_coord.length];
      for (int i = 0; i < hole_coord.length; i++) {
        hole_coord_[i] = new Coordinate(c.x + coef * (hole_coord[i].x - c.x),
            coord[i].y);
      }
      trous[j] = new LinearRing(new CoordinateArraySequence(hole_coord_), gf);
    }
    return CommonAlgorithms.rotation(new Polygon(lr, trous, gf), c, angle);
  }

  public static Polygon affinite(Polygon geom, double angle, double scale) {
    return CommonAlgorithms.affinite(geom, geom.getCentroid().getCoordinate(),
        angle, scale);
  }

  public static IPolygon affinite(IPolygon geom, double angle, double scale) {
    IPolygon poly = null;
    try {
      poly = (IPolygon) AdapterFactory.toGM_Object(CommonAlgorithms.affinite(
          (Polygon) AdapterFactory.toGeometry(new GeometryFactory(), geom),
          angle, scale));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return poly;
  }

  public static Polygon homothetie(Polygon geom, double x0, double y0,
      double scale) {
    GeometryFactory gf = new GeometryFactory();

    // le contour externe
    Coordinate[] coord = geom.getExteriorRing().getCoordinates();
    Coordinate[] coord_ = new Coordinate[coord.length];
    for (int i = 0; i < coord.length; i++) {
      coord_[i] = new Coordinate(x0 + scale * (coord[i].x - x0),
          y0 + scale * (coord[i].y - y0));
    }
    LinearRing lr = gf.createLinearRing(new CoordinateArraySequence(coord_));

    // les trous
    LinearRing[] trous = new LinearRing[geom.getNumInteriorRing()];
    for (int j = 0; j < geom.getNumInteriorRing(); j++) {
      Coordinate[] hole_coord = geom.getInteriorRingN(j).getCoordinates();
      Coordinate[] hole_coord_ = new Coordinate[hole_coord.length];
      for (int i = 0; i < hole_coord.length; i++) {
        hole_coord_[i] = new Coordinate(x0 + scale * (hole_coord[i].x - x0),
            y0 + scale * (hole_coord[i].y - y0));
      }
      trous[j] = gf.createLinearRing(new CoordinateArraySequence(hole_coord_));
    }
    return gf.createPolygon(lr, trous);
  }

  public static Polygon homothetie(Polygon geom, double scale) {
    return CommonAlgorithms.homothetie(geom, geom.getCentroid().getX(),
        geom.getCentroid().getY(), scale);
  }

  public static IPolygon homothetie(IPolygon geom, double scale) {
    IPolygon poly = null;
    try {
      poly = (IPolygon) AdapterFactory.toGM_Object(CommonAlgorithms.homothetie(
          (Polygon) AdapterFactory.toGeometry(new GeometryFactory(), geom),
          scale));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return poly;
  }

  public static IPolygon homothetie(IPolygon geom, double scale,
      IDirectPosition center) {
    IPolygon poly = null;
    try {
      poly = (IPolygon) AdapterFactory.toGM_Object(CommonAlgorithms.homothetie(
          (Polygon) AdapterFactory.toGeometry(new GeometryFactory(), geom),
          center.getX(), center.getY(), scale));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return poly;
  }

  public static Geometry translation(Geometry geom, double dx, double dy) {
    if (geom instanceof Polygon) {
      return CommonAlgorithms.translation(geom, dx, dy);
    } else if (geom instanceof LineString) {
      return CommonAlgorithms.translation(geom, dx, dy);
    } else if (geom instanceof LinearRing) {
      return CommonAlgorithms.translation(geom, dx, dy);
    }

    CommonAlgorithms.logger.warn("translation de geometrie  " + geom
        + " impossible: type non pris en compte");
    return null;
  }

  public static Polygon translation(Polygon geom, double dx, double dy) {

    // le contour externe
    LinearRing lr = CommonAlgorithms
        .translation((LinearRing) geom.getExteriorRing(), dx, dy);

    // les trous
    LinearRing[] trous = new LinearRing[geom.getNumInteriorRing()];
    for (int j = 0; j < geom.getNumInteriorRing(); j++) {
      trous[j] = CommonAlgorithms
          .translation((LinearRing) geom.getInteriorRingN(j), dx, dy);
    }

    return new GeometryFactory().createPolygon(lr, trous);
  }

  public static LinearRing translation(LinearRing lr, double dx, double dy) {
    return new GeometryFactory().createLinearRing(
        CommonAlgorithms.translation(lr.getCoordinates(), dx, dy));
  }

  public static LineString translation(LineString ls, double dx, double dy) {
    return new GeometryFactory().createLineString(
        CommonAlgorithms.translation(ls.getCoordinates(), dx, dy));
  }

  public static Coordinate[] translation(Coordinate[] coord, double dx,
      double dy) {
    Coordinate[] coord_ = new Coordinate[coord.length];
    for (int i = 0; i < coord.length; i++) {
      coord_[i] = new Coordinate(coord[i].x + dx, coord[i].y + dy);
    }
    return coord_;
  }

  public static IGeometry translation(IGeometry geom, double dx, double dy) {
    if (geom instanceof IPolygon) {
      return CommonAlgorithms.translation((IPolygon) geom, dx, dy);
    } else if (geom instanceof ILineString) {
      return CommonAlgorithms.translation((ILineString) geom, dx, dy);
    } else if (geom instanceof IRing) {
      return CommonAlgorithms.translation((IRing) geom, dx, dy);
    }

    CommonAlgorithms.logger.warn("translation de geometrie  " + geom
        + " impossible: type non pris en compte");
    return null;
  }

  public static IPolygon translation(IPolygon geom, double dx, double dy) {

    // le contour externe
    GM_Polygon poly = new GM_Polygon(
        CommonAlgorithms.translation(geom.getExterior(), dx, dy));

    // les trous
    for (int j = 0; j < geom.getInterior().size(); j++) {
      poly.addInterior(
          CommonAlgorithms.translation(geom.getInterior(j), dx, dy));
    }

    return poly;
  }

  public static IRing translation(IRing ring, double dx, double dy) {
    return new GM_Ring(
        new GM_LineString(CommonAlgorithms.translation(ring.coord(), dx, dy)));
  }

  public static ILineString translation(ILineString ls, double dx, double dy) {
    return new GM_LineString(CommonAlgorithms.translation(ls.coord(), dx, dy));
  }

  public static IDirectPositionList translation(IDirectPositionList coords,
      double dx, double dy) {
    IDirectPositionList coords_ = new DirectPositionList();
    for (int i = 0; i < coords.size(); i++) {
      coords_.add(new DirectPosition(coords.get(i).getX() + dx,
          coords.get(i).getY() + dy));
    }
    return coords_;
  }

  public static LineString rotation(LineString ls, Coordinate c, double angle) {
    double cos = Math.cos(angle), sin = Math.sin(angle);

    Coordinate[] coord = ls.getCoordinates();
    Coordinate[] coord_ = new Coordinate[coord.length];
    for (int i = 0; i < coord.length; i++) {
      double x = coord[i].x, y = coord[i].y;
      coord_[i] = new Coordinate(c.x + cos * (x - c.x) - sin * (y - c.y),
          c.y + sin * (x - c.x) + cos * (y - c.y));
    }
    return new GeometryFactory().createLineString(coord_);
  }

  /**
   * Même chose que rotation(LineString ls, Coordinate c, double angle) mais
   * encapsulé avec des objets GeOx.
   * @param ls
   * @param c
   * @param angle
   * @return
   * @throws Exception
   */
  public static ILineString rotation(ILineString ls, IDirectPosition c,
      double angle) throws Exception {
    LineString jtsLine = AdapterFactory.toLineString(new GeometryFactory(), ls);
    LineString rotatedLine = rotation(jtsLine,
        new Coordinate(c.getX(), c.getY()), angle);
    return (ILineString) AdapterFactory.toGM_Object(rotatedLine);
  }

  // angle en radian, rotation dans le sens direct de centre c
  public static Polygon rotation(Polygon geom, Coordinate c, double angle) {
    double cos = Math.cos(angle), sin = Math.sin(angle);
    GeometryFactory gf = new GeometryFactory();

    // rotation de l'enveloppe
    Coordinate[] coord = geom.getExteriorRing().getCoordinates();
    Coordinate[] coord_ = new Coordinate[coord.length];
    for (int i = 0; i < coord.length; i++) {
      double x = coord[i].x, y = coord[i].y;
      coord_[i] = new Coordinate(c.x + cos * (x - c.x) - sin * (y - c.y),
          c.y + sin * (x - c.x) + cos * (y - c.y));
    }
    LinearRing lr = gf.createLinearRing(coord_);

    // rotation des trous
    LinearRing[] trous = new LinearRing[geom.getNumInteriorRing()];
    for (int j = 0; j < geom.getNumInteriorRing(); j++) {
      Coordinate[] coord2 = geom.getInteriorRingN(j).getCoordinates();
      Coordinate[] coord2_ = new Coordinate[coord2.length];
      for (int i = 0; i < coord2.length; i++) {
        double x = coord2[i].x, y = coord2[i].y;
        coord2_[i] = new Coordinate(c.x + cos * (x - c.x) - sin * (y - c.y),
            c.y + sin * (x - c.x) + cos * (y - c.y));
      }
      trous[j] = gf.createLinearRing(coord2_);
    }
    return gf.createPolygon(lr, trous);
  }

  public static IPolygon rotation(IPolygon geom, IDirectPosition c,
      double angle) {
    IPolygon poly = null;
    try {
      poly = (IPolygon) AdapterFactory.toGM_Object(CommonAlgorithms.rotation(
          (Polygon) AdapterFactory.toGeometry(new GeometryFactory(), geom),
          new Coordinate(c.getX(), c.getY()), angle));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return poly;
  }

  // angle en radian, rotation dans le sens direct
  public static Polygon rotation(Polygon geom, double angle) {
    return CommonAlgorithms.rotation(geom, geom.getCentroid().getCoordinate(),
        angle);
  }

  public static IPolygon rotation(IPolygon geom, double angle) {
    return CommonAlgorithms.rotation(geom, geom.centroid(), angle);
  }

  /**
   * l'elongation. c'est un reel entre 0 et 1. 1: carré plus proche de 0: tend
   * vers un segment c'est le quotient de la largeur et de la longueur du PPRE.
   * 
   * @param geom
   * @return
   */
  public static double elongation(Geometry geom) {
    Polygon ppre = SmallestSurroundingRectangleComputation.getSSR(geom);

    // FIXME des fois ppre == null... j'ai mis ça en attendant qu'on corrige
    // proprement ce bug
    if (ppre == null) {
      return 0;
    }

    Coordinate[] coords = ppre.getCoordinates();
    double lg1 = coords[0].distance(coords[1]);
    double lg2 = coords[1].distance(coords[2]);
    if (lg1 > lg2) {
      return lg2 / lg1;
    }
    return lg1 / lg2;
  }

  public static double elongation(IGeometry geom) {
    Geometry geom_ = null;
    try {
      geom_ = AdapterFactory.toGeometry(new GeometryFactory(), geom);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return CommonAlgorithms.elongation(geom_);
  }

  // c'est le quotient de la surface par celle de l'enveloppe convexe.
  // valeur entre 0 (peu convexe) et 1 (parfaitement convexe)
  // renvoit -999.9 pas definie (quand l'enveloppe convexe est d'aire nulle ou
  // la geometrie nulle)
  public static double convexite(Geometry geom) {
    if (geom == null) {
      return -999.9;
    }
    double aireC = geom.convexHull().getArea();
    if (aireC == 0.0) {
      return -999.9;
    }
    return geom.getArea() / aireC;
  }

  public static double convexity(IGeometry geom) {
    Geometry geom_ = null;
    try {
      geom_ = AdapterFactory.toGeometry(new GeometryFactory(), geom);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return CommonAlgorithms.convexite(geom_);
  }

  public static IGeometry filtreDouglasPeucker(IGeometry geom, double seuil) {
    IGeometry geom_ = null;
    try {
      geom_ = AdapterFactory.toGM_Object(JtsAlgorithms.filtreDouglasPeucker(
          AdapterFactory.toGeometry(new GeometryFactory(), geom), seuil));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return geom_;
  }

  public static IGeometry buffer(IGeometry geom, int quad, int cap, int join,
      double mitre, double distance) {
    BufferBuilder bb = new BufferBuilder(
        new BufferParameters(quad, cap, join, mitre));
    IGeometry geom_ = null;
    try {
      geom_ = AdapterFactory.toGM_Object(bb.buffer(
          AdapterFactory.toGeometry(new GeometryFactory(), geom), distance));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return geom_;
  }

  public static IGeometry buffer(IGeometry geom, double distance) {
    return CommonAlgorithms.buffer(geom, 10, BufferParameters.CAP_FLAT,
        BufferParameters.JOIN_MITRE, 100.0, distance);
  }

  // renvoie les deux points les plus proches de deux geometries
  public static IDirectPositionList getPointsLesPlusProches(IGeometry geom1,
      IGeometry geom2) {
    DirectPositionList dl = new DirectPositionList();

    Coordinate[] coords = null;
    try {
      coords = new DistanceOp(
          AdapterFactory.toGeometry(new GeometryFactory(), geom1),
          AdapterFactory.toGeometry(new GeometryFactory(), geom2))
              .nearestPoints();
      dl.add(new DirectPosition(coords[0].x, coords[0].y));
      dl.add(new DirectPosition(coords[1].x, coords[1].y));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return dl;
  }

  /**
   * Rotates a vector object.
   * 
   * @param v the vector to be rotated
   * @param angle the angle of rotation is radians
   * @return the rotated vector
   * @author GTouya
   */
  public static Vecteur rotateVector(Vecteur v, double angle) {
    DirectPositionList points = new DirectPositionList();
    points.add(new DirectPosition(0.0, 0.0));
    points.add(new DirectPosition(v.getX(), v.getY()));
    GM_LineString lsG = new GM_LineString(points);
    LineString ls = null;
    try {
      ls = (LineString) JtsGeOxygene.makeJtsGeom(lsG, true);
    } catch (Exception e) {
      e.printStackTrace();
    }
    ls = CommonAlgorithms.rotation(ls, new Coordinate(0.0, 0.0), angle);
    DirectPosition endPoint = new DirectPosition(ls.getEndPoint().getX(),
        ls.getEndPoint().getY());
    return new Vecteur(new DirectPosition(0.0, 0.0), endPoint);
  }

  public static double getSidelongMaxDist(IPolygon poly, double orientation) {
    IPolygon mbr = SmallestSurroundingRectangleComputation.getSSR(poly);

    // Shift the segment to make sure it passes through the intersection
    IDirectPosition centreHull = mbr.centroid();
    double norm = poly.perimeter();
    Vecteur vectHoriz = new Vecteur(norm, 0.0, 0.0);
    Vecteur vect = CommonAlgorithms.rotateVector(vectHoriz, orientation);
    IDirectPosition mid = Operateurs.milieu(poly.centroid(),
        vect.translate(poly.centroid()));

    // build a small segment between the two centres of gravity
    DirectPositionList list = new DirectPositionList();
    list.add(poly.centroid());
    list.add(vect.translate(poly.centroid()));
    GM_LineString segment = new GM_LineString(list);
    segment = (GM_LineString) new Vecteur(centreHull.getX() - mid.getX(),
        centreHull.getY() - mid.getY(), 0.0).translate(segment);

    // combine the segment with the intersection of the two geometries
    IGeometry inter2 = segment.intersection(mbr);

    if (inter2.isEmpty()) {
      return 0.0;
    }

    // the length of the intersection represents the length of overlap
    return inter2.length();
  }
}
