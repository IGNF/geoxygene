/**
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
 * 
 */

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * NON IMPLEMENTE. Un segment d'arc, défini par 3 points.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

class GM_Arc extends GM_ArcString {
  private static Logger logger = Logger.getLogger(GM_Arc.class.getName());
  private GM_Point startPoint;

  public GM_Point getStartPoint() {
    return startPoint;
  }

  public void setStartPoint(GM_Point startPoint) {
    this.startPoint = startPoint;
  }

  private GM_Point midPoint;

  public GM_Point getMidPoint() {
    return midPoint;
  }

  public void setMidPoint(GM_Point midPoint) {
    this.midPoint = midPoint;
  }

  private GM_Point endPoint;

  public GM_Point getEndPoint() {
    return endPoint;
  }

  public void setEndPoint(GM_Point endPoint) {
    this.endPoint = endPoint;
  }

  /**
   * cree un arc de cercle a partir de trois points
   * 
   * @param startPoint
   * @param midPoint
   * @param endPoint
   */
  public GM_Arc(GM_Point startPoint, GM_Point midPoint, GM_Point endPoint) {
    setStartPoint(startPoint);
    setMidPoint(midPoint);
    setEndPoint(endPoint);
  }

  /**
   * cree un arc de cercle a partir de deux point et la distance du cercle au
   * milieu du segment quand la distance est positive, l'arc de cercle est a
   * gauche, et � droite sinon (on ne suit pas la norme, qui traite le cas 3D)
   * 
   * @param startPoint
   * @param endPoint
   * @param offset
   */
  public GM_Arc(GM_Point startPoint, GM_Point endPoint, double offset) {
    setStartPoint(startPoint);
    setEndPoint(endPoint);

    double dx = endPoint.getPosition().getX() - startPoint.getPosition().getX();
    double dy = endPoint.getPosition().getY() - startPoint.getPosition().getY();
    double n = Math.sqrt(dx * dx + dy * dy);

    double midx = (startPoint.getPosition().getX() + endPoint.getPosition()
        .getX())
        * 0.5 - offset * dy / n;
    double midy = (startPoint.getPosition().getY() + endPoint.getPosition()
        .getY())
        * 0.5 + offset * dx / n;

    setMidPoint(new GM_Point(new DirectPosition(midx, midy)));
  }

  public DirectPosition getCenter() {

    // retrieve the points coodinates
    double xa = getStartPoint().getPosition().getX();
    double ya = getStartPoint().getPosition().getY();
    double xb = getEndPoint().getPosition().getX();
    double yb = getEndPoint().getPosition().getY();
    double xc = getMidPoint().getPosition().getX();
    double yc = getMidPoint().getPosition().getY();

    double q = ya + yb - 2 * yc;

    // no circle center can be computed; 3 points are aligned
    if (q == 0)
      return null;

    double t = (xb - xa) / (2 * q);
    return new DirectPosition((xa + xc) * 0.5 + t * (ya - yc), (ya + yc) * 0.5
        + t * (xc - xa));
  }

  public double getRadius() {

    // retrieve the points coodinates
    double xa = getStartPoint().getPosition().getX();
    double ya = getStartPoint().getPosition().getY();
    double xb = getMidPoint().getPosition().getX();
    double yb = getMidPoint().getPosition().getY();
    double xc = getEndPoint().getPosition().getX();
    double yc = getEndPoint().getPosition().getY();

    double ab2 = (xa - xb) * (xa - xb) + (ya - yb) * (ya - yb);
    double ac2 = (xa - xc) * (xa - xc) + (ya - yc) * (ya - yc);

    return 0.5 * ab2 / Math.sqrt(ab2 - ac2 * 0.25);
  }

  /**
   * @return the angle, in radian, within [-Pi, Pi], between the [O, x] axis and
   *         the line [O, startpoint]
   * 
   */
  public double startOfArc() {
    DirectPosition center = getCenter();
    return Math.atan2(getStartPoint().getPosition().getY() - center.getY(),
        getStartPoint().getPosition().getX() - center.getX());
  }

  /**
   * @return the angle, in radian, within [-Pi, Pi], between the [O, x] axis and
   *         the line [O, endpoint]
   * 
   */
  public double endOfArc() {
    DirectPosition center = getCenter();
    return Math.atan2(getEndPoint().getPosition().getY() - center.getY(),
        getEndPoint().getPosition().getX() - center.getX());
  }

  /**
   * @return the angle, in radian, within [-Pi, Pi], between the [O, x] axis and
   *         the line [O, midpoint]
   * 
   */
  public double midOfArc() {
    DirectPosition center = getCenter();
    return Math.atan2(getMidPoint().getPosition().getY() - center.getY(),
        getMidPoint().getPosition().getX() - center.getX());
  }

  /**
   * @return the angle value of the arc, within [-2Pi, 2Pi]
   */
  public double delta() {
    // half delta within [-Pi, Pi]
    double demidelta = midOfArc() - startOfArc();
    if (demidelta > Math.PI)
      demidelta -= 2 * Math.PI;
    else if (demidelta < -Math.PI)
      demidelta += 2 * Math.PI;

    return 2 * demidelta;
  }

  @Override
  public GM_Object intersection(GM_Object geom) {

    // compute intersection between two arcs
    if (geom instanceof GM_Arc) {
      return new GM_Point();
    }

    // compute intersection between an arc and a segment
    if (geom instanceof GM_LineSegment) {

      // retrieve segment's coordinates
      GM_LineSegment ls = (GM_LineSegment) geom;
      DirectPosition dp0 = ls.getControlPoint(0);
      DirectPosition dp1 = ls.getControlPoint(1);

      // center of the arc
      DirectPosition c = getCenter();

      // projection of the center on the line
      double dx = dp1.getX() - dp0.getX();
      double dy = dp1.getY() - dp0.getY();
      double n = Math.sqrt(dx * dx + dy * dy);
      dx = dx / n;
      dy = dy / n;
      double ps = dx * (c.getX() - dp0.getX()) + dy * (c.getY() - dp0.getY());
      DirectPosition dpp = new DirectPosition(dp0.getX() + dx * ps, dp0.getY()
          + dy * ps);

      // compute the distance between the center and the projection of the
      // center on the line
      double dist = dpp.distance(c);

      // compute the radius
      double r = getRadius();

      // if that distance is greater than the radius, no intersection possible,
      // return an empty geometry
      if (dist > r)
        return new GM_Point();

      // if that distance is equal to the radius, a candidate to be the
      // intersection is the projection
      // (the circle is tangent to the line)
      if (dist == r) {
        GM_Point pt = new GM_Point(dpp);
        if (contains(pt) && ls.contains(pt))
          return pt;
        else
          return new GM_Point();
      }

      // two points could be at the intersection
      double d = dpp.distance(c);
      double cos = d / r;
      double sin = Math.sqrt(r * r - d * d) / r;
      DirectPosition u = new DirectPosition((dpp.getX() - c.getX()) / d, (dpp
          .getY() - c.getY())
          / d);

      // the two candidate points
      GM_Point pt1 = new GM_Point(new DirectPosition(c.getX() + r
          * (cos * u.getX() + sin * u.getY()), c.getY() + r
          * (-sin * u.getX() + cos * u.getY())));
      GM_Point pt2 = new GM_Point(new DirectPosition(c.getX() + r
          * (cos * u.getX() - sin * u.getY()), c.getY() + r
          * (+sin * u.getX() + cos * u.getY())));

      // check if these points are contained in the arc and the line segment
      boolean cont1 = contains(pt1) && ls.contains(pt1);
      boolean cont2 = contains(pt2) && ls.contains(pt2);

      // return one of the point, both or none
      if (cont1 && cont2) {
        GM_MultiPoint mp = new GM_MultiPoint();
        mp.add(pt1);
        mp.add(pt2);
        return mp;
      } else if (cont1 && !cont2)
        return pt1;
      else if (!cont1 && cont2)
        return pt2;
      else
        return new GM_Point();
    }
    logger.error("Error in intersection computation of " + this + " and "
        + geom);
    return null;
  }

  @Override
  public boolean contains(GM_Object geom) {
    if (geom instanceof GM_Point) {
      DirectPosition pt = ((GM_Point) geom).getPosition();

      // retrieve the center and the radius of the arc
      DirectPosition c = getCenter();
      double r = getRadius();

      // if the point is not on the circle, return false
      if (pt.distance(c) != r)
        return false;

      // the angle, in radian, within [-Pi, Pi], between the [O, x] axis and the
      // line [O, pt]
      double angle = Math.atan2(pt.getY() - c.getY(), pt.getX() - c.getX());

      double delta = delta();
      double startAngle = startOfArc();
      double endAngle = endOfArc();
      if (delta > 0) {
        if (endAngle > startAngle)
          return (angle >= startAngle) && (angle <= endAngle);
        if (endAngle < startAngle)
          return (angle >= startAngle) || (angle <= endAngle);
      } else if (delta < 0) {
        if (endAngle > startAngle)
          return (angle <= startAngle) || (angle >= endAngle);
        if (endAngle < startAngle)
          return (angle <= startAngle) && (angle >= endAngle);
      } else {
        return (pt.getX() == getStartPoint().getPosition().getX() && pt.getY() == getStartPoint()
            .getPosition().getY());
      }
    }
    logger.error("Error in contains computation of " + this + " and " + geom);
    return true;
  }
}
