/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;

import fr.ign.cogit.geoxygene.api.spatial.AbstractGeomFactory;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomengine.AbstractGeometryEngine;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Rectangle;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

public class CommonAlgorithmsFromCartAGen {

  /**
   * Gets the nearest vertex of the geometry to a point. The point does not need
   * to be in the geometry.
   * 
   * @param geom
   * @param point the point the vertex has to be near to
   * @return the nearest IDirectPosition point in the coord() DirectPositionList
   * @author GTouya
   */
  public static IDirectPosition getNearestVertexFromPoint(IGeometry geom,
      IDirectPosition point) {
    double distMin = Double.MAX_VALUE;
    IDirectPosition nearest = null;
    for (IDirectPosition pt : geom.coord()) {
      if (pt.distance(point) < distMin) {
        distMin = pt.distance(point);
        nearest = pt;
      }
    }
    return nearest;
  }

  /**
   * Gets the position in the coordList of the nearest vertex of the geometry to
   * a point. The point does not need to be in the geometry.
   * 
   * @param geom
   * @param point the point the vertex has to be near to
   * @return the nearest IDirectPosition point in the coord() DirectPositionList
   * @author GTouya
   */
  public static int getNearestVertexPositionFromPoint(IGeometry geom,
      IDirectPosition point) {
    double distMin = Double.MAX_VALUE;
    int position = 0;
    for (int i = 0; i < geom.coord().size(); i++) {
      IDirectPosition pt = geom.coord().get(i);
      if (pt.distance(point) < distMin) {
        distMin = pt.distance(point);
        position = i;
      }
    }
    return position;
  }

  /**
   * Gets the position in the coordList of the nearest vertex of the geometry to
   * a point. The point does not need to be in the geometry.
   * 
   * @param geom
   * @param point the point the vertex has to be near to
   * @return the nearest IDirectPosition point in the coord() DirectPositionList
   * @author GTouya
   */
  public static int getNearestVertexPositionBeforePoint(IGeometry geom,
      IDirectPosition point) {
    double distMin = Double.MAX_VALUE;
    int position = 0;
    IDirectPosition nearest = null;
    for (int i = 0; i < geom.coord().size(); i++) {
      IDirectPosition pt = geom.coord().get(i);
      if (pt.distance(point) < distMin) {
        distMin = pt.distance(point);
        position = i;
        nearest = pt;
      }
    }
    if (position == geom.coord().size() - 1)
      return position - 1;

    Vector2D vect1 = new Vector2D(nearest, point);
    Vector2D vect2 = new Vector2D(point, geom.coord().get(position + 1));
    if (vect2.getX() * vect1.getY() - vect2.getY() * vect1.getX() == 0.0)
      return position;
    return position - 1;
  }

  /**
   * Inserts as a new vertex a point of the given line that is not already a
   * vertex. The point has to be on the line.
   * @param line
   * @param newVertex
   * @return
   */
  public static ILineString insertVertex(ILineString line,
      IDirectPosition newVertex) {
    if (line.coord().contains(newVertex))
      return line;
    IDirectPositionList pts = new DirectPositionList();
    pts.add(line.startPoint());
    List<Segment> segments = Segment.getSegmentList(line);
    boolean added = false;
    for (int i = 0; i < segments.size(); i++) {
      Segment seg = segments.get(i);
      if (!seg.containsPoint(newVertex) || added) {
        pts.add(seg.endPoint());
        continue;
      }
      pts.add(newVertex);
      pts.add(seg.endPoint());
      added = true;
    }
    return new GM_LineString(pts);
  }

  /**
   * Inserts as a new vertex a point of the given line that is not already a
   * vertex. The point has to be on the line.
   * @param line
   * @param newVertex
   * @param tolerance a tolerance to deal with rounding approximations.
   * @return
   */
  public static ILineString insertVertex(ILineString line,
      IDirectPosition newVertex, double tolerance) {
    if (line.coord().contains(newVertex))
      return line;
    IDirectPositionList pts = new DirectPositionList();
    pts.add(line.startPoint());
    List<Segment> segments = Segment.getSegmentList(line);
    boolean added = false;
    for (int i = 0; i < segments.size(); i++) {
      Segment seg = segments.get(i);
      if (!seg.containsPoint(newVertex, tolerance) || added) {
        pts.add(seg.endPoint());
        continue;
      }
      pts.add(newVertex);
      pts.add(seg.endPoint());
      added = true;
    }
    return new GM_LineString(pts);
  }

  /**
   * Inserts as a new vertex a point of the given line that is not already a
   * vertex. The point has to be on one of the rings of the polygon.
   * @param polygon
   * @param newVertex
   * @return
   */
  public static IPolygon insertVertex(IPolygon polygon,
      IDirectPosition newVertex) {
    AbstractGeomFactory factory = AbstractGeometryEngine.getFactory();

    if (polygon.coord().contains(newVertex))
      return polygon;
    IDirectPositionList pts = new DirectPositionList();
    pts.add(polygon.coord().get(0));
    List<Segment> segments = Segment
        .getSegmentList(polygon.exteriorLineString());
    boolean added = false;
    for (int i = 0; i < segments.size(); i++) {
      Segment seg = segments.get(i);
      if (!seg.containsPoint(newVertex) || added) {
        pts.add(seg.endPoint());
        continue;
      }
      pts.add(newVertex);
      pts.add(seg.endPoint());
      added = true;
    }
    return factory.createIPolygon(factory.createILineString(pts));
  }

  /**
   * Inserts as a new vertex a point of the given polygon that is not already a
   * vertex. The point has to be on one of the rings of the polygon.
   * @param polygon
   * @param newVertex
   * @param tolerance a tolerance to deal with rounding approximations.
   * @return
   */
  public static IPolygon insertVertex(IPolygon polygon,
      IDirectPosition newVertex, double tolerance) {
    if (polygon.coord().contains(newVertex))
      return polygon;
    IDirectPositionList pts = new DirectPositionList();
    pts.add(polygon.coord().get(0));
    List<Segment> segments = Segment
        .getSegmentList(polygon.exteriorLineString());
    boolean added = false;
    for (int i = 0; i < segments.size(); i++) {
      Segment seg = segments.get(i);
      if (!seg.containsPoint(newVertex, tolerance) || added) {
        pts.add(seg.endPoint());
        continue;
      }
      pts.add(newVertex);
      pts.add(seg.endPoint());
      added = true;
    }
    return AbstractGeometryEngine.getFactory().createIPolygon(
        AbstractGeometryEngine.getFactory().createILineString(pts));
  }

  /**
   * Split the given line in two new polylines at a given point that is not
   * necessarily a vertex. The point has to be on the line given a tolerance.
   * @param line
   * @param splitVertex
   * @param tolerance a tolerance to deal with rounding approximations.
   * @return a two-dimension array with the splitted lines.
   */
  public static ILineString[] splitLine(ILineString line,
      IDirectPosition splitVertex, double tolerance) {

    IDirectPositionList pts1 = new DirectPositionList();
    IDirectPositionList pts2 = new DirectPositionList();
    pts1.add(line.startPoint());
    List<Segment> segments = Segment.getSegmentList(line);
    boolean line1 = true;
    for (int i = 0; i < segments.size(); i++) {
      Segment seg = segments.get(i);
      if (!seg.containsPoint(splitVertex, tolerance) && line1) {
        pts1.add(seg.endPoint());
        continue;
      } else if (seg.containsPoint(splitVertex, tolerance) && line1) {
        pts1.add(splitVertex);
        line1 = false;
        if (!splitVertex.equals(seg.getEndPoint())) {
          pts2.add(splitVertex);
          pts2.add(seg.endPoint());
        }
        continue;
      } else if (seg.containsPoint(splitVertex, tolerance) && !line1) {
        pts2.add(splitVertex);
        pts2.add(seg.endPoint());
        continue;
      }
      pts2.add(seg.endPoint());
    }

    // build the lines and the array
    ILineString[] lines = new ILineString[2];
    lines[0] = GeometryEngine.getFactory().createILineString(pts1);
    lines[1] = GeometryEngine.getFactory().createILineString(pts2);
    return lines;
  }

  /**
   * Gets the nearest point of the geometry to a point. If the point is a
   * vertex, it is not chosen.
   * 
   * @param geom
   * @param point the point the vertex has to be near to
   * @return the nearest IDirectPosition point in the coord() DirectPositionList
   * @author GTouya
   */
  public static IDirectPosition getNearestOtherVertexFromPoint(IGeometry geom,
      IDirectPosition point) {
    double distMin = Double.MAX_VALUE;
    IDirectPosition nearest = null;
    for (IDirectPosition pt : geom.coord()) {
      if (pt.distance(point) < distMin && pt.distance(point) != 0.0) {
        distMin = pt.distance(point);
        nearest = pt;
      }
    }
    return nearest;
  }

  /**
   * Gets the nearest segment of the geometry to a point. If the point is a
   * vertex, it is not chosen.
   * 
   * @param geom
   * @param point the point the vertex has to be near to
   * @return the nearest ILineSegment coord() DirectPositionList
   * @author GTouya
   */
  public static ILineSegment getNearestSegmentFromPoint(IGeometry geom,
      IDirectPosition point) {
    double distMin = Double.MAX_VALUE;
    IDirectPosition nearest = null;
    int i = 0;
    int rangNearest = 0;
    for (IDirectPosition pt : geom.coord()) {
      if (pt.distance(point) < distMin && pt.distance(point) != 0.0) {
        distMin = pt.distance(point);
        nearest = pt;
        rangNearest = i;
      }
      i++;
    }
    if (nearest == null) {
      return null;
    }
    ILineSegment seg1 = new GM_LineSegment(geom.coord().get(rangNearest - 1),
        geom.coord().get(rangNearest));
    ILineSegment seg2 = new GM_LineSegment(geom.coord().get(rangNearest),
        geom.coord().get(rangNearest + 1));
    if (seg1.distance(new GM_Point(point)) > seg2
        .distance(new GM_Point(point))) {
      return seg2;
    }
    return seg1;

  }

  /**
   * Rotates a vector object.
   * 
   * @param v the vector to be rotated
   * @param angle the angle of rotation is radians
   * @return the rotated vector
   * @author GTouya
   */
  public static Vector2D rotateVector(Vector2D v, double angle) {
    DirectPositionList points = new DirectPositionList();
    points.add(new DirectPosition(0.0, 0.0));
    points.add(new DirectPosition(v.getX(), v.getY()));
    ILineString lsG = new GM_LineString(points);
    LineString ls = null;
    try {
      ls = (LineString) JtsGeOxygene.makeJtsGeom(lsG, true);
    } catch (Exception e) {
      e.printStackTrace();
    }
    ls = CommonAlgorithms.rotation(ls, new Coordinate(0.0, 0.0), angle);
    DirectPosition endPoint = new DirectPosition(ls.getEndPoint().getX(),
        ls.getEndPoint().getY());
    return new Vector2D(new DirectPosition(0.0, 0.0), endPoint);
  }

  /**
   * Computes the distance of overlaping between buildings, along the axis
   * joining the two centers of gravity.
   * 
   * @param poly1 First overlapping geometry
   * @param poly2 Second overlapping geometry
   * @return Length of overlap between the two geometries in the direction
   *         defined by the two centres of gravity. Returns 0 if the two
   *         polygons are disjoint.
   * @author GTouya
   */
  public static double getOverlappingLength(IPolygon poly1, IPolygon poly2) {
    IDirectPosition centre1 = poly1.centroid();
    IDirectPosition centre2 = poly2.centroid();
    // test intersection
    if (!poly1.intersects(poly2)) {
      return 0.0;
    }

    // builds the intersection
    IGeometry inter = poly1.intersection(poly2);
    if (inter == null)
      return 0.0;

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

    if (inter2.isEmpty()) {
      return 0.0;
    }

    // the length of the intersection represents the length of overlap
    return inter2.length();
  }

  /**
   * Compute the ratio of length of a line that is inside a polygon (0 if
   * disjoint, 1 if contained).
   * @param line
   * @param poly
   * @return
   */
  public static double getOverlappingLength(ILineString line, IPolygon poly) {
    if (line.disjoint(poly))
      return 0.0;
    if (poly.contains(line))
      return 1.0;
    IGeometry intersection = line.intersection(poly);
    return intersection.length() / line.length();
  }

  /**
   * Computes the angle between two crossing lines. The angle is not directly
   * computed between the first vertices after the crossing to avoid capture
   * artifacts.
   * 
   * @param line1 the first line of the angle
   * @param line2 the second line of the angle
   * @return an Angle object between [0, Pi]
   * @author GTouya
   */
  public static double angleBetween2Lines(ILineString line1,
      ILineString line2) {
    // search for the intersection point between the two geometries
    DirectPosition coordIni1 = (DirectPosition) line1.startPoint();
    DirectPosition coordFin1 = (DirectPosition) line1.endPoint();
    DirectPosition coordIni2 = (DirectPosition) line2.startPoint();
    DirectPosition coordFin2 = (DirectPosition) line2.endPoint();
    boolean interGeom1 = true, interGeom2 = true;

    DirectPosition coordInter = null;
    if (coordIni2.equals(coordIni1)) {
      coordInter = coordIni1;
      interGeom1 = true;
      interGeom2 = true;
    }
    if (coordFin2.equals(coordIni1)) {
      coordInter = coordIni1;
      interGeom1 = true;
      interGeom2 = false;
    }
    if (coordFin2.equals(coordFin1)) {
      coordInter = coordFin1;
      interGeom1 = false;
      interGeom2 = false;
    }
    if (coordIni2.equals(coordFin1)) {
      coordInter = coordIni2;
      interGeom1 = false;
      interGeom2 = true;
    }

    // if there is a topological problem, return false
    if (coordInter == null) {
      return 0.0;
    }

    // count vertices in each geometry : indeed, if one or the other has only 2
    // vertices, the angle continuity cannot be tested.
    int nbVert1 = line1.numPoints();
    int nbVert2 = line2.numPoints();

    // define the nodes to compute the angle
    DirectPosition v1 = null, v2 = null;

    // if nbVert1 > 2, get the second vertex in geometry 1
    if (nbVert1 > 2) {
      if (interGeom1) {
        v1 = (DirectPosition) line1.coord().get(2);
      } else {
        v1 = (DirectPosition) line1.coord().get(nbVert1 - 3);
      }
    } else {

      // get the first vertex on geometry 1
      if (interGeom1) {
        v1 = (DirectPosition) line1.coord().get(1);
      } else {
        v1 = (DirectPosition) line1.coord().get(nbVert1 - 2);
      }
    }

    // si nbVert2 > 2, get the second vertex in geometry 2
    if (nbVert2 > 2) {
      if (interGeom2) {
        v2 = (DirectPosition) line2.coord().get(2);
      } else {
        v2 = (DirectPosition) line2.coord().get(nbVert2 - 3);
      }
    } else {
      // get the first vertex on geometry 2
      if (interGeom2) {
        v2 = (DirectPosition) line2.coord().get(1);
      } else {
        v2 = (DirectPosition) line2.coord().get(nbVert2 - 2);
      }
    }

    // now, compute interAngle between geom and geomFoll
    double angle = Angle.angleTroisPoints(v1, coordInter, v2).getValeur();
    if (angle > Math.PI) {
      angle = 2 * Math.PI - angle;
    }
    return angle;
  }

  /**
   * Computes the orientation of line geometry at a vertex of the line.
   * 
   * @param line the line on which orientation is computed
   * @param point thr point where orientation is computed
   * @return an Angle object corresponding to the absolute orientation [0,2Pi]
   * @author GTouya
   */
  public static Angle lineAbsoluteOrientation(ILineString line,
      IDirectPosition point) {

    // the first point of the angle is the central point staggered in the
    // abscise axis
    DirectPosition v1 = new DirectPosition(point.getX() + 20.0, point.getY());

    // then get the third point in the line geometry
    IDirectPosition v2 = getNearestOtherVertexFromPoint(line, point);

    // now, compute interAngle between geom and geomFoll
    Angle interAngle = Angle.angleTroisPoints(v1, point, v2);
    if (interAngle.getValeur() < 0)
      interAngle = new Angle(interAngle.getValeur() + Math.PI);
    return interAngle;
  }

  /**
   * Compute the number of common vertices between two geometries.
   * 
   * @param geom1
   * @param geom2
   * @return
   * @author GTouya
   */
  public static int getNbCommonVertices(IGeometry geom1, IGeometry geom2) {
    int nb = 0;
    HashSet<IDirectPosition> treated = new HashSet<IDirectPosition>();
    for (IDirectPosition v1 : geom1.coord()) {
      if (treated.contains(v1)) {
        continue;
      }
      for (IDirectPosition v2 : geom2.coord()) {
        if (v1.equals(v2)) {
          nb++;
          break;
        }
      }
      treated.add(v1);
    }
    return nb;
  }

  public static LineConnectionInfo getLineConnectionInfo(ILineString line1,
      ILineString line2) {
    // search connection at the start point of line1
    double angleS = -10.0;
    int nbCommonNodes = 0;
    if (line1.startPoint().equals(line2.startPoint())) {
      nbCommonNodes++;
      angleS = Angle.angleTroisPoints(line1.coord().get(1), line1.startPoint(),
          line2.coord().get(1)).getValeur();
    }
    if (line1.startPoint().equals(line2.endPoint())) {
      nbCommonNodes++;
      angleS = Angle.angleTroisPoints(line1.coord().get(1), line1.startPoint(),
          line2.coord().get(line2.numPoints() - 2)).getValeur();
    }

    // search connection at the end point of line1
    double angleE = -10.0;
    if (line1.endPoint().equals(line2.startPoint())) {
      nbCommonNodes++;
      angleE = Angle.angleTroisPoints(line1.coord().get(line1.numPoints() - 2),
          line1.endPoint(), line2.coord().get(1)).getValeur();
    }
    if (line1.endPoint().equals(line2.endPoint())) {
      nbCommonNodes++;
      angleE = Angle
          .angleTroisPoints(line1.coord().get(line1.numPoints() - 2),
              line1.endPoint(), line2.coord().get(line2.numPoints() - 2))
          .getValeur();
    }

    // get the angles between -Pi & Pi
    if (angleS > Math.PI)
      angleS = angleS - 2 * Math.PI;
    if (angleE > Math.PI)
      angleE = angleE - 2 * Math.PI;

    return new CommonAlgorithmsFromCartAGen().new LineConnectionInfo(
        nbCommonNodes, angleS, angleE);
  }

  /**
   * Class to group the information resulting from the function
   * getLineConnectionInfo(ILineString line1, ILineString line2)
   * @author GTouya
   * 
   */
  public class LineConnectionInfo {
    private int nbCommonNodes;
    /**
     * Angle between two edges connected to the starting node of geom1 . -10 if
     * not connected.
     */
    private double angleS = -10.0;
    /**
     * Angle between two edges connected to the ending node of geom2. -10 if not
     * connected
     */
    private double angleE = -10.0;

    public LineConnectionInfo(int nbCommonNodes, double angleS, double angleE) {
      super();
      this.nbCommonNodes = nbCommonNodes;
      this.angleS = angleS;
      this.angleE = angleE;
    }

    public LineConnectionInfo(int nbCommonNodes) {
      super();
      this.nbCommonNodes = nbCommonNodes;
    }

    public int getNbCommonNodes() {
      return nbCommonNodes;
    }

    public void setNbCommonNodes(int nbCommonNodes) {
      this.nbCommonNodes = nbCommonNodes;
    }

    public double getAngleS() {
      return angleS;
    }

    public void setAngleS(double angleS) {
      this.angleS = angleS;
    }

    public double getAngleE() {
      return angleE;
    }

    public void setAngleE(double angleE) {
      this.angleE = angleE;
    }

  }

  /**
   * Check if a line geometry is crossing entirely a polygon (with 2
   * intersection points and passing through the polygon.
   * 
   * @param line
   * @param poly
   * @return true if the line is crossing the polygon
   * @author GTouya
   */
  public static boolean isLineCrossingPolygon(ILineString line, IPolygon poly) {
    // test if the geometries intersect
    if (!line.intersects(poly)) {
      return false;
    }
    // if the line is closed, it cannot cross the polygon
    if (line.isClosed()) {
      return false;
    }

    // compute the intersection between line and poly's ring
    IGeometry inter = line.intersection(poly.exteriorLineString());

    // if the intersection is a simple point, return false
    if (!(inter instanceof IMultiPoint)) {
      return false;
    }
    IMultiPoint multi = (IMultiPoint) inter;

    // now check that line really crosses the polygon.
    // thus search if a random line point between the 2 intersection points
    // is inside the polygon
    IDirectPosition pt1 = multi.coord().get(0);
    IDirectPosition pt2 = multi.coord().get(1);
    IDirectPosition middle = null;
    for (int i = 0; i < line.coord().size() - 1; i++) {
      IDirectPosition pt = line.coord().get(i);
      if (pt.equals(pt1) || pt.equals(pt2)) {
        // get the following point
        middle = line.coord().get(i + 1);
        if (middle.equals(pt1) || middle.equals(pt2)) {
          middle = new DirectPosition((pt1.getX() + pt2.getY()) / 2.0,
              (pt1.getY() + pt2.getY()) / 2.0);
        }
        break;
      }
    }

    if (middle == null) {
      return false;
    }

    // now check if middle is inside poly
    if (poly.contains(middle.toGM_Point())) {
      return true;
    }

    return false;
  }

  /**
   * Computes the union of all geometries contained in a collection.
   * @return the geometry resulting from the union
   */
  public static IGeometry geomColnUnion(Collection<IGeometry> geomColn) {
    IGeometry unionGeom = null;
    int i = 0;
    for (IGeometry geom : geomColn) {
      if (geom == null)
        continue;
      i = i + 1;
      // Initialisation
      if (i == 1) {
        unionGeom = geom;
        // Union with result of last iteration
      } else if (!(unionGeom == null)) {
        unionGeom = unionGeom.union(geom);
      } else {
        unionGeom = geom;
      }
    }
    return unionGeom;
  }

  /**
   * Computes the intersection of all geometries contained in a collection.
   * @return the geometry resulting from the intersection. Returns an empty
   *         {@code GM_Aggregate} if the intersection is empty.
   */
  public static IGeometry geomColnIntersection(Collection<IGeometry> geomColn) {
    IGeometry intersectionGeom = null;
    int i = 0;
    for (IGeometry geom : geomColn) {
      i = i + 1;
      // Initialisation
      if (i == 1) {
        intersectionGeom = geom;
      } else {
        // Check if the result of the last iteration is empty - if yes returns
        // it
        if (intersectionGeom == null) {
          return new GM_Aggregate<IGeometry>();
        }
        if (intersectionGeom.isEmpty()) {
          return intersectionGeom;
        }
        // Otherwise computes the intersection with result of previous iteration
        intersectionGeom = intersectionGeom.intersection(geom);
      }
    }
    return intersectionGeom;
  }

  public static double getSidelongMaxDist(IPolygon poly, double orientation) {
    IPolygon mbr = SmallestSurroundingRectangleComputation.getSSR(poly);

    // Shift the segment to make sure it passes through the intersection
    IDirectPosition centreHull = mbr.centroid();
    double norm = poly.perimeter();
    Vector2D vectHoriz = new Vector2D(norm, 0);
    Vector2D vect = CommonAlgorithmsFromCartAGen.rotateVector(vectHoriz,
        orientation);
    IDirectPosition mid = Operateurs.milieu(poly.centroid(),
        vect.translate(poly.centroid()));

    // build a small segment between the two centres of gravity
    ILineSegment segment = new GM_LineSegment(poly.centroid(),
        vect.translate(poly.centroid()));
    segment = (ILineSegment) new Vector2D(centreHull.getX() - mid.getX(),
        centreHull.getY() - mid.getY()).translate(segment);

    // combine the segment with the intersection of the two geometries
    IGeometry inter2 = segment.intersection(mbr);

    if (inter2.isEmpty()) {
      return 0.0;
    }

    // the length of the intersection represents the length of overlap
    return inter2.length();
  }

  /**
   * Projection of a point on a line according to a direction given by a vector.
   * 
   * @param point
   * @param line
   * @param direction
   * @return
   * @author GTouya
   */
  public static IDirectPosition projection(IDirectPosition point,
      ILineString line, Vector2D direction) {
    // checks that the direction is not the null vector
    if (direction.isNull())
      return null;

    // get the line MBR
    Rectangle mbr = Rectangle.boundingRectangle(line);
    // Build an auxiliary point outside the mbr:
    // transle point according to direction, using the MBR diameter
    Vector2D transVect = direction.changeNorm(mbr.getDiameter() + 1);
    IDirectPosition auxPt = transVect.translate(point);

    // now it is sure that (point,aux) intersects line and we just have to find
    // the intersection nearest to point.

    // get the intersection between line and (point,aux)
    ILineString segment = new GM_LineSegment(point, auxPt);
    IGeometry intersection = line.intersection(segment);
    if (intersection.isEmpty()) {
      // extend segment
      transVect.scalarMultiplication(10.0);
      auxPt = transVect.translate(point);
      segment = new GM_LineSegment(point, auxPt);
      intersection = line.intersection(segment);
    }
    if (intersection instanceof IPoint)
      return ((IPoint) intersection).getPosition();
    else if (intersection instanceof IMultiPoint) {
      IDirectPosition nearest = null;
      double distMin = mbr.getDiameter();
      for (int i = 0; i < ((IMultiPoint) intersection).size(); i++) {
        IPoint pt = ((IMultiPoint) intersection).get(i);
        if (point.distance(pt.getPosition()) < distMin) {
          distMin = point.distance(pt.getPosition());
          nearest = pt.getPosition();
        }
      }
      return nearest;
    } else {
      return null;
    }
  }

  /**
   * Projection of a point on a line according to the orthogonal of a direction
   * given by a vector. The closest projected point is kept.
   * 
   * @param point
   * @param line
   * @param direction
   * @return
   * @author GTouya
   */
  public static IDirectPosition projectionOrtho(IDirectPosition point,
      ILineString line, Vector2D direction) {
    // checks that the direction is not the null vector
    if (direction.isNull())
      return null;

    // compute both orthogonal directions
    Vector2D ortho1 = direction.rotate(-Math.PI / 2);
    Vector2D ortho2 = direction.rotate(Math.PI / 2);

    // get the projected points
    IDirectPosition proj1 = projection(point, line, ortho1);
    IDirectPosition proj2 = projection(point, line, ortho2);

    // if one is null, return the other
    if (proj1 == null)
      return proj2;
    if (proj2 == null)
      return proj1;

    // now, return the closest one
    if (proj1.distance2D(point) < proj2.distance2D(point))
      return proj1;
    else
      return proj2;
  }

  /**
   * Return the simple surface with the biggest area in a multi surface
   * geometry.
   * @param multi
   * @return
   */
  public static IPolygon getBiggerFromMultiSurface(
      IMultiSurface<IOrientableSurface> multi) {
    double max = 0.0;
    IPolygon bigger = null;
    for (IOrientableSurface surf : multi.getList()) {
      if (surf.area() > max) {
        max = surf.area();
        bigger = (IPolygon) surf;
      }
    }
    return bigger;
  }

  /**
   * Return the simple line with the biggest length in a multi curve geometry.
   * @param multi
   * @return
   */
  public static ILineString getLongestFromMultiCurve(
      IMultiCurve<IOrientableCurve> multi) {
    double max = 0.0;
    ILineString longest = null;
    for (IOrientableCurve curve : multi.getList()) {
      if (curve.length() > max) {
        max = curve.length();
        longest = (ILineString) curve;
      }
    }
    return longest;
  }

  /**
   * For any aggregate geometry, get the biggest simple polygon of the
   * aggregate.
   * @param aggr
   * @return
   */
  public static IPolygon getBiggerFromAggregate(IAggregate<IGeometry> aggr) {
    double max = 0.0;
    IPolygon bigger = null;
    for (IGeometry surf : aggr.getList()) {
      if (!(surf instanceof IPolygon))
        continue;
      if (surf.area() > max) {
        max = surf.area();
        bigger = (IPolygon) surf;
      }
    }
    return bigger;
  }

  /**
   * Get the (first) intersection point between two lines.
   * @param line1
   * @param line2
   * @return
   */
  public static IDirectPosition getCommonVertexBetween2Lines(ILineString line1,
      ILineString line2) {
    if (!line1.intersects(line2))
      return null;
    return line1.intersection(line2).coord().get(0);
  }

  /**
   * Get among the inner rings of a polygon, the ones that intersect a geometry.
   * @param multiRing
   * @param geom
   * @return
   */
  public static Set<IRing> getIntersectingInnerRings(IPolygon multiRing,
      IGeometry geom) {
    Set<IRing> intersecting = new HashSet<IRing>();
    for (IRing ring : multiRing.getInterior())
      if (ring.intersects(geom))
        intersecting.add(ring);

    return intersecting;
  }

  /**
   * Get among the inner rings of a polygon, the one that contains a geometry.
   * If none contains it, returns null;
   * @param multiRing
   * @param geom
   * @return
   */
  public static IRing getContainingInnerRing(IPolygon multiRing,
      IGeometry geom) {
    for (IRing ring : multiRing.getInterior())
      if (new GM_Polygon(ring).contains(geom))
        return ring;

    return null;
  }

  /**
   * Translates only one side of a Line String, the displacement vector being
   * absorbed when vertices move away from the displaced extremity.
   * @param line
   * @param start
   * @param displacementVector
   * @return
   */
  public static ILineString displaceOneSide(ILineString line, boolean start,
      Vector2D displacementVector) {
    IDirectPositionList displacedLine = new DirectPositionList();
    IDirectPositionList ptList = line.coord();
    if (!start)
      ptList.inverseOrdre();
    // each vertex of the line is displaced according to the vector absorbed in
    // relation to the distance to the displaced side.
    double cumulatedDistance = 0.0;
    IDirectPosition previousPt = null;
    for (IDirectPosition vertex : ptList) {
      // first vertex special case: no absorbtion
      if (previousPt == null) {
        displacedLine.add(displacementVector.translate(vertex));
        previousPt = vertex;
        continue;
      }
      // last vertex special case, no displacement
      if (vertex.equals(ptList.get(ptList.size() - 1))) {
        displacedLine.add(vertex);
        continue;
      }
      // increment the cumulated distance
      cumulatedDistance += vertex.distance2D(previousPt);
      // compute the absorbed vector
      double newNorm = displacementVector.norme()
          / (0.5 * Math.log(cumulatedDistance));
      if (newNorm > displacementVector.norme())
        newNorm = displacementVector.norme();
      Vector2D vect = displacementVector.changeNorm(newNorm);
      // translate the vertex
      displacedLine.add(vect.translate(vertex));
      previousPt = vertex;
    }
    // reverse the directposition list to preserve the initial order of the line
    if (!start)
      displacedLine.inverseOrdre();
    return new GM_LineString(displacedLine);
  }

  /**
   * Get the distance along the line between the first vertex and the vertex at
   * the given index.
   * @param line
   * @param index
   * @return
   */
  public static double getLineDistanceToIndex(ILineString line, int index) {
    double distance = 0.0;
    int i = 0;
    IDirectPosition previous = null;
    for (IDirectPosition vertex : line.coord()) {
      i++;
      if (previous == null)
        continue;
      distance += previous.distance2D(vertex);
      if (i == index)
        return distance;
    }

    return line.length();
  }

  /**
   * Get the distance along the line between the first vertex and the vertex at
   * the given index.
   * @param line
   * @param index1
   * @param index2
   * @return
   */
  public static double getLineDistanceBetweenIndexes(ILineString line,
      int index1, int index2) {
    double distance = 0.0;
    int i = 0;
    IDirectPosition previous = null;
    for (IDirectPosition vertex : line.coord()) {
      i++;
      if (i <= index1)
        continue;
      if (previous == null)
        continue;
      distance += previous.distance2D(vertex);
      if (i == index2)
        return distance;
    }

    return line.length();
  }

  /**
   * Get the sub line between two points on a given line (not necessarily
   * vertices). The first point has to be before the second point in the line
   * order.
   * @param line
   * @param pt1
   * @param pt2
   * @return
   */
  public static ILineString getSubLine(ILineString line, IDirectPosition pt1,
      IDirectPosition pt2) {
    // first, insert both pts as vertices of the line
    ILineString updatedLine = insertVertex(insertVertex(line, pt1, 0.01), pt2,
        0.01);
    IDirectPositionList subLine = new DirectPositionList();
    boolean start = false;
    boolean end = false;
    for (IDirectPosition vertex : updatedLine.coord()) {
      if (vertex.equals(pt1, 0.01))
        start = true;
      if (end)
        break;
      if (vertex.equals(pt2, 0.01))
        end = true;
      if (!start)
        continue;
      subLine.add(vertex);
    }

    // case where the line is not in the same direction as the given points
    if (subLine.size() == 0) {
      start = false;
      end = false;
      subLine.clear();
      for (IDirectPosition vertex : updatedLine.coord().reverse()) {
        if (vertex.equals(pt1, 0.01))
          start = true;
        if (!start)
          continue;
        if (end)
          break;
        if (vertex.equals(pt2, 0.01))
          end = true;
        subLine.add(vertex);
      }
    }

    return new GM_LineString(subLine);
  }

  /**
   * Retrieves the length of the shortest edge of the given geometry.
   * @param geom
   * @return
   */
  public static double getShortestEdgeLength(IGeometry geom) {
    if (geom instanceof IPoint)
      return 0.0;

    double minLength = Double.MAX_VALUE;
    if (geom instanceof ILineString) {
      for (Segment seg : Segment.getSegmentList((ILineString) geom)) {
        if (seg.length() < minLength)
          minLength = seg.length();
      }
    } else if (geom instanceof IPolygon) {
      for (Segment seg : Segment.getSegmentList((IPolygon) geom,
          geom.coord().get(0))) {
        if (seg.length() < minLength)
          minLength = seg.length();
      }
    }
    if (minLength == Double.MAX_VALUE)
      return 0.0;
    return minLength;
  }

  /**
   * Retrieves the median of edges' length of the given geometry.
   * @param geom
   * @return
   */
  public static double getEdgeLengthMedian(IGeometry geom) {
    if (geom instanceof IPoint)
      return 0.0;
    List<Double> lengths = new ArrayList<Double>();
    if (geom instanceof ILineString) {
      for (Segment seg : Segment.getSegmentList((ILineString) geom))
        lengths.add(seg.length());
    } else if (geom instanceof IPolygon) {
      for (Segment seg : Segment.getSegmentList((IPolygon) geom,
          geom.coord().get(0)))
        lengths.add(seg.length());
    }
    // sort the list of lengths
    Collections.sort(lengths);
    // then, compute the median from the sorted list
    if (lengths.size() == 0)
      return 0.0;
    if (lengths.size() == 1)
      return lengths.get(0);
    if (lengths.size() % 2 == 0)
      return lengths.get(lengths.size() / 2);
    else {
      double before = lengths
          .get(Double.valueOf(Math.floor(lengths.size() / 2)).intValue() - 1);
      double after = lengths
          .get(Double.valueOf(Math.floor(lengths.size() / 2)).intValue());
      return (after + before) / 2.0;
    }
  }

  /**
   * Cette fonction renvoie le point de la ligne qui a le x le plus grand. S'il
   * y en a plusieurs, le point choisi est celui qui a le plus petit y.
   * @param line
   * @return
   */
  public static Vector<Object> getPtMaxXFromLine(ILineString line) {
    double xMax = -Double.MAX_VALUE;
    double yMin = Double.MAX_VALUE;
    IDirectPosition ptMaxX = null;
    int index = 0;
    int i = 0;
    for (IDirectPosition pt : line.coord()) {
      if (pt.getX() > xMax) {
        xMax = pt.getX();
        yMin = pt.getY();
        ptMaxX = pt;
        index = i;
      } else if (pt.getX() == xMax) {
        if (pt.getY() < yMin) {
          xMax = pt.getX();
          yMin = pt.getY();
          ptMaxX = pt;
          index = i;
        }
      }
      i++;
    }
    Vector<Object> vect = new Vector<Object>(2);
    vect.add(ptMaxX);
    vect.add(index);
    return vect;
  }

  /**
   * Cette fonction renvoie le point du polygone qui a le x le plus grand. S'il
   * y en a plusieurs, le point choisi est celui qui a le plus petit y.
   * @param line
   * @return un vecteur avec le point (IDirectPosition) et son indice.
   */
  public static Vector<Object> getPtMaxXFromPolygon(IPolygon line) {
    double xMax = -Double.MAX_VALUE;
    double yMin = Double.MAX_VALUE;
    IDirectPosition ptMaxX = null;
    int index = 0;
    int i = 0;
    for (IDirectPosition pt : line.coord()) {
      if (pt.getX() > xMax) {
        xMax = pt.getX();
        yMin = pt.getY();
        ptMaxX = pt;
        index = i;
      } else if (pt.getX() == xMax) {
        if (pt.getY() < yMin) {
          xMax = pt.getX();
          yMin = pt.getY();
          ptMaxX = pt;
          index = i;
        }
      }
      i++;
    }
    Vector<Object> vect = new Vector<Object>(2);
    vect.add(ptMaxX);
    vect.add(index);
    return vect;
  }

  /**
   * Cette fonction renvoie le point de la ligne qui est le plus eloigne dans la
   * direction donnee par l'angle theta. S'il y en a plusieurs, le point choisi
   * est le premier dans le sens direct.
   * @param line
   * @return
   * @throws Exception
   */
  public static IDirectPosition getPtMaxDirFromLine(ILineString line,
      Angle theta) throws Exception {
    // rotate the line of -theta to make it a getPtMaxXFromLine problem
    ILineString rotatedLine = CommonAlgorithms.rotation(line, line.centroid(),
        -theta.getValeur());

    // then, get the point with maximum x on the rotated Line
    Vector<Object> result = getPtMaxXFromLine(rotatedLine);
    // get the vertex with the same index in the initial line
    return line.coord().get((Integer) result.get(1));
  }

  /**
   * Returns a copy of the parameter polygon with its holes (i.e. inner rings)
   * removed.
   * @param polygon
   * @return
   */
  public static IPolygon removeHoles(IPolygon polygon) {
    return new GM_Polygon(polygon.getExterior());
  }

  /**
   * Returns a copy of the parameter polygon with its small holes (i.e. inner
   * rings with area < given threshold) removed.
   * @param polygon
   * @return
   */
  public static IPolygon removeSmallHoles(IPolygon polygon, double area) {
    IPolygon noHolePol = new GM_Polygon(polygon.getExterior());
    for (IRing hole : polygon.getInterior()) {
      if (hole.area() < area)
        continue;
      noHolePol.addInterior(hole);
    }
    return noHolePol;
  }

  /**
   * Get the longest segment that can be traced inside a polygon in a given
   * orientation.
   * @param polygon
   * @param orientation
   * @return
   * @throws Exception
   */
  public static ILineString getLongestInsideSegment(IPolygon polygon,
      double orientation) throws Exception {
    double maxDist = 0.0;
    ILineSegment longest = null;
    // first rotate the polygon
    IPolygon horiz = CommonAlgorithms.rotation(polygon, -orientation);
    double maxX = ((IDirectPosition) getPtMaxXFromPolygon(horiz).get(0)).getX();
    // then, find the longest segment by looping on the vertices
    for (IDirectPosition vertex : horiz.coord()) {
      ILineSegment seg = new GM_LineSegment(vertex,
          new DirectPosition(maxX, vertex.getY()));
      // the segment is intersected with the rotated polygon
      IGeometry inter = horiz.intersection(seg);
      if (inter instanceof ILineString) {
        if (seg.length() < maxDist)
          continue;
        maxDist = seg.length();
        longest = seg;
      } else if (inter instanceof IMultiCurve) {
        @SuppressWarnings("unchecked")
        ILineString longestCurve = getLongestFromMultiCurve(
            (IMultiCurve<IOrientableCurve>) inter);
        if (longestCurve.length() < maxDist)
          continue;
        maxDist = seg.length();
        longest = seg;
      }
    }

    return CommonAlgorithms.rotation(longest, polygon.centroid(), orientation);
  }

  /**
   * Get the longest segment that can be traced inside a polygon in a given
   * orientation.
   * @param polygon
   * @return
   * @throws Exception
   */
  public static ILineString getPolygonDiameter(IPolygon polygon) {
    double maxDist = 0.0;
    ILineSegment longest = null;
    // then, find the longest segment by looping on the vertices
    for (IDirectPosition vertex : polygon.coord()) {
      for (IDirectPosition other : polygon.coord()) {
        if (vertex.equals(other))
          continue;
        ILineSegment seg = new GM_LineSegment(vertex, other);
        // the segment is intersected with the rotated polygon
        IGeometry inter = polygon.intersection(seg);
        if (inter instanceof ILineString) {
          if (seg.length() < maxDist)
            continue;
          maxDist = seg.length();
          longest = seg;
        }
      }
    }

    return longest;
  }

  /**
   * For a line and a subline of this line, returns true if vertices of both
   * lines are in the same direction.
   * @param line
   * @param subLine
   * @return
   */
  public static boolean areLinesSameDirection(ILineString line,
      ILineString subLine) {
    if (!line.contains(subLine))
      return false;
    IDirectPosition first = subLine.coord().get(0);
    IDirectPosition second = subLine.coord().get(1);
    int index1 = line.coord().getList().indexOf(first);
    int index2 = line.coord().getList().indexOf(second);
    if (index1 < index2)
      return true;
    return false;
  }

  /**
   * Open a ring at the vertex corresponding to the given index.
   * @param ring
   * @param index
   * @return
   */
  public static ILineString openRingAtIndex(IRing ring, int index) {
    IDirectPositionList list = new DirectPositionList();
    for (int i = index; i < ring.coord().size() - 1; i++)
      list.add(ring.coord().get(i));
    for (int i = 0; i < index; i++)
      list.add(ring.coord().get(i));
    return new GM_LineString(list);
  }

  /**
   * Get the vertices where the angles are sharp. They may correspond to
   * triangle corners for triangular shapes for instance.
   * @param polygon
   * @return
   */
  public static List<IDirectPosition> getSharpAngleVertices(IPolygon polygon,
      double maxAngle) {
    ArrayList<IDirectPosition> points = new ArrayList<IDirectPosition>();
    points.addAll(polygon.coord().getList());
    points.remove(points.size() - 1);
    points.addAll(polygon.coord().getList());
    points.remove(points.size() - 1);
    points.addAll(polygon.coord().getList());
    List<IDirectPosition> sharpVertices = new ArrayList<IDirectPosition>();
    for (int i = polygon.coord().size(); i < 2 * polygon.coord().size()
        - 1; i++) {
      double angle = Angle
          .angleTroisPoints(points.get(i - 1), points.get(i), points.get(i + 1))
          .getValeur();
      if (angle > Math.PI) {
        angle = 2 * Math.PI - angle;
      }

      if (angle < maxAngle) {
        sharpVertices.add(points.get(i));
      }
    }

    return sharpVertices;
  }

  /**
   * Compute the mean line between two lines. It's a very simple algorithm that
   * may not work in complex cases like for instance lines that intersect. It
   * loops on the points of the first line and searches the closest point on
   * line 2, and takes the middle of both points.
   * @param line1
   * @param line2
   * @return
   */
  public static ILineString getMeanLine(ILineString line1, ILineString line2) {
    IDirectPositionList coordList = new DirectPositionList();
    for (IDirectPosition pt : line1.coord()) {
      // get the closest point on line2
      IDirectPosition closest = JtsAlgorithms.getClosestPoint(pt, line2);
      if (closest.equals(pt)) {
        coordList.add(pt);
        continue;
      }
      // add the middle point
      IDirectPosition middle = new Segment(pt, closest).getMiddlePoint();
      coordList.add(middle);
    }
    return new GM_LineString(coordList);
  }

  /**
   * Compute the self intersections of a line string ordered according the line
   * vertices order. Intersections can lie on vertices or between two vertices.
   * Uses a non-optimal method.
   * 
   * @param line
   * @return
   */
  public static List<IDirectPosition> getSelfIntersections(ILineString line) {
    List<IDirectPosition> selfIntersections = new ArrayList<>();
    List<Segment> segments = Segment.getSegmentList(line);
    Set<Segment> remainingSegments = new HashSet<>(segments);

    for (int i = 0; i < segments.size(); i++) {
      Segment segment = segments.get(i);
      remainingSegments.remove(segment);
      Set<Segment> toTest = new HashSet<>(remainingSegments);
      // remove the next segment
      if (i < segments.size() - 1)
        toTest.remove(segments.get(i + 1));
      for (Segment other : toTest) {
        if (segment.intersects(other)) {
          if (!selfIntersections
              .contains(segment.intersection(other).centroid()))
            selfIntersections.add(segment.intersection(other).centroid());
        }
      }
    }
    return selfIntersections;
  }

  /**
   * Finds the duplicate points of a line and returns a new line without
   * duplicates.
   * @param line
   * @return
   * @throws Exception
   */
  public static ILineString removeDuplicates(ILineString line)
      throws Exception {
    Coordinate[] coordinateArray = JtsGeOxygene.makeJtsGeom(line)
        .getCoordinates();
    IDirectPositionList coords = new DirectPositionList();
    // List<Coordinate> coordinates = new ArrayList<Coordinate>();
    // build a list of linestring coordinates and remove duplicates
    Coordinate previous = coordinateArray[0];
    coords.add(new DirectPosition(previous.x, previous.y));
    for (int i = 1; i < coordinateArray.length; i++) {
      if (!coordinateArray[i].equals2D(previous)) {
        coords.add(
            new DirectPosition(coordinateArray[i].x, coordinateArray[i].y));
        previous = coordinateArray[i];
      }
    }
    if (coords.size() >= 2) {
      GeometryEngine.init();
      return GeometryEngine.getFactory().createILineString(coords);
    } else {
      return null;
    }
  }

}
