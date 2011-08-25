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

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IArc2;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * An oriented 2D arc segment, defined by its center, its radius, its start and
 * end angles, and its direction This class is bot part of the OGC standard but
 * is needed for some applications
 * 
 * @author julien Gaffuri
 * @version 1.0
 * 
 */
public class GM_Arc2 extends GM_ArcString implements IArc2 {
  private static Logger logger = Logger.getLogger(GM_Arc2.class.getName());

  /**
   * the center of the arc
   */
  private IDirectPosition center;
  @Override
  public IDirectPosition getCenter() {
    return this.center;
  }
  @Override
  public void setCenter(IDirectPosition center) {
    this.center = center;
  }
  /**
   * the radius of the arc
   */
  private double radius;
  @Override
  public double getRadius() {
    return this.radius;
  }
  @Override
  public void setRadius(double radius) {
    this.radius = radius;
  }
  /**
   * the start angle of the arc, in radian, within ]-Pi, Pi], between the [O, x]
   * axis and the line [O, startpoint]
   * 
   */
  private double startOfArc;
  @Override
  public double getStartOfArc() {
    return this.startOfArc;
  }
  @Override
  public void setStartOfArc(double startOfArc) {
    this.startOfArc = startOfArc;
  }
  /**
   * the end angle of the arc, in radian, within ]-Pi, Pi], between the [O, x]
   * axis and the line [O, endpoint]
   * 
   */
  private double endOfArc;
  @Override
  public double getEndOfArc() {
    return this.endOfArc;
  }
  @Override
  public void setEndOfArc(double endOfArc) {
    this.endOfArc = endOfArc;
  }
  public enum ArcDirection {
    DIRECT, INDIRECT, NONE, UNKNOWN
  }
  /**
   * the direction of the arc
   * 
   */
  private ArcDirection direction;
  @Override
  public ArcDirection getDirection() {
    return this.direction;
  }
  @Override
  public void setEndOfArc(ArcDirection direction) {
    this.direction = direction;
  }

  /**
   * build an arc from its center, radius, start angle, end angle (within
   * ]-Pi,Pi]) and direction
   * 
   * @param center
   * @param radius
   * @param startOfArc
   * @param endOfArc
   * @param direction
   */
  public GM_Arc2(IDirectPosition center, double radius, double startOfArc,
      double endOfArc, ArcDirection direction) {
    if (Math.abs(startOfArc - endOfArc) < 0.0001) {
      GM_Arc2.logger.warn("Very small arc is created in " + center
          + ". angle delta is " + Math.abs(startOfArc - endOfArc));
    }

    this.center = center;
    this.radius = radius;
    this.startOfArc = startOfArc;
    this.endOfArc = endOfArc;
    this.direction = direction;
  }

  @Override
  public double getMidOfArc() {
    if (this.direction == ArcDirection.DIRECT) {
      if (this.startOfArc <= this.endOfArc) {
        return (this.startOfArc + this.endOfArc) * 0.5;
      }
      if (this.startOfArc > this.endOfArc) {
        double angle = (this.startOfArc + this.endOfArc) * 0.5 + Math.PI;
        if (angle > Math.PI) {
          return angle - 2 * Math.PI;
        } else {
          return angle;
        }
      }
    }
    if (this.direction == ArcDirection.INDIRECT) {
      if (this.startOfArc <= this.endOfArc) {
        double angle = (this.startOfArc + this.endOfArc) * 0.5 + Math.PI;
        if (angle > Math.PI) {
          return angle - 2 * Math.PI;
        } else {
          return angle;
        }
      }
      if (this.startOfArc > this.endOfArc) {
        return (this.startOfArc + this.endOfArc) * 0.5;
      }
    }
    GM_Arc2.logger.error("impossible to determine arc mid of arc");
    return 0;
  }

  @Override
  public double getDelta() {
    if (this.direction == ArcDirection.NONE
        || this.direction == ArcDirection.UNKNOWN) {
      GM_Arc2.logger.error("impossible to determine arc delta");
      return 0;
    }
    if (this.direction == ArcDirection.DIRECT) {
      if (this.startOfArc <= this.endOfArc) {
        return this.endOfArc - this.startOfArc;
      }
      if (this.startOfArc > this.endOfArc) {
        return 2 * Math.PI - this.startOfArc + this.endOfArc;
      }
      GM_Arc2.logger.error("impossible to determine arc delta. start="
          + this.startOfArc + ", end=" + this.endOfArc);
      return 0;
    }
    if (this.direction == ArcDirection.INDIRECT) {
      if (this.startOfArc <= this.endOfArc) {
        return -2 * Math.PI - this.startOfArc + this.endOfArc;
      }
      if (this.startOfArc > this.endOfArc) {
        return this.endOfArc - this.startOfArc;
      }
      GM_Arc2.logger.error("impossible to determine arc delta. start="
          + this.getStartOfArc() + ", end=" + this.getEndOfArc());
      return 0;
    }
    GM_Arc2.logger.error("impossible to determine arc delta. direction="
        + this.direction);
    return 0;
  }

  @Override
  public IPosition getStartPoint() {
    return new GM_Position(new DirectPosition(this.getCenter().getX()
        + this.getRadius() * Math.cos(this.getStartOfArc()), this.getCenter()
        .getY()
        + this.getRadius() * Math.sin(this.getStartOfArc())));
  }

  @Override
  public IPosition getMidPoint() {
    double angle = this.getMidOfArc();
    return new GM_Position(new DirectPosition(this.getCenter().getX()
        + this.getRadius() * Math.cos(angle), this.getCenter().getY()
        + this.getRadius() * Math.sin(angle)));
  }

  @Override
  public IPosition getEndPoint() {
    return new GM_Position(new DirectPosition(this.getCenter().getX()
        + this.getRadius() * Math.cos(this.getEndOfArc()), this.getCenter()
        .getY()
        + this.getRadius() * Math.sin(this.getEndOfArc())));
  }

  @Override
  public IGeometry intersection(IGeometry geom) {

    // compute intersection between two arcs
    if (geom instanceof IArc2) {
      IArc2 arc = (IArc2) geom;

      // retrieve centers and radius of both arcs
      IDirectPosition c1 = this.getCenter();
      double r1 = this.getRadius();
      IDirectPosition c2 = arc.getCenter();
      double r2 = arc.getRadius();

      // distance between both centers
      double d = c1.distance(c2);

      // if the two centers are further than the sum of the radius, return empty
      // intersection
      if (d > r1 + r2) {
        return new GM_Aggregate<IGeometry>();
      }

      if (c1.distance(c2) == 0 && r1 == r2) {
        // both arcs have the same center and the same radius
        // the intersection could be an arc, a point, two points, two arcs, or a
        // pair of point and an arc

        if (this.getDirection() == arc.getDirection()) {
          // arcs are touching in one extrem point
          if (Math.abs(this.getStartOfArc() - arc.getEndOfArc()) == 0) {
            return new GM_Point(new DirectPosition(c1.getX() + r1
                * Math.cos(this.getStartOfArc()), c1.getY() + r1
                * Math.sin(this.getStartOfArc())));
          }
          if (Math.abs(this.getEndOfArc() - arc.getStartOfArc()) == 0) {
            return new GM_Point(new DirectPosition(c1.getX() + r1
                * Math.cos(this.getEndOfArc()), c1.getY() + r1
                * Math.sin(this.getEndOfArc())));
          }

          // cases where arcs are disjoints
          if (this.getDirection() == ArcDirection.DIRECT) {
            if (this.getStartOfArc() < this.getEndOfArc()
                && arc.getStartOfArc() < arc.getEndOfArc()
                && (this.getStartOfArc() > arc.getEndOfArc() || arc
                    .getStartOfArc() > this.getEndOfArc())) {
              return new GM_Aggregate<IGeometry>();
            }
            if (this.getStartOfArc() > this.getEndOfArc()
                && arc.getStartOfArc() < arc.getEndOfArc()
                && this.getStartOfArc() > arc.getEndOfArc()
                && this.getEndOfArc() < arc.getStartOfArc()) {
              return new GM_Aggregate<IGeometry>();
            }
            if (arc.getStartOfArc() > arc.getEndOfArc()
                && this.getStartOfArc() < this.getEndOfArc()
                && arc.getStartOfArc() > this.getEndOfArc()
                && arc.getEndOfArc() < this.getStartOfArc()) {
              return new GM_Aggregate<IGeometry>();
            }
          }
          if (this.getDirection() == ArcDirection.INDIRECT) {
            if (this.getStartOfArc() > this.getEndOfArc()
                && arc.getStartOfArc() > arc.getEndOfArc()
                && (this.getStartOfArc() > arc.getEndOfArc() || arc
                    .getStartOfArc() > this.getEndOfArc())) {
              return new GM_Aggregate<IGeometry>();
            }
            if (this.getStartOfArc() < this.getEndOfArc()
                && arc.getStartOfArc() > arc.getEndOfArc()
                && this.getStartOfArc() < arc.getEndOfArc()
                && this.getEndOfArc() > arc.getStartOfArc()) {
              return new GM_Aggregate<IGeometry>();
            }
            if (arc.getStartOfArc() < arc.getEndOfArc()
                && this.getStartOfArc() > this.getEndOfArc()
                && arc.getStartOfArc() < this.getEndOfArc()
                && arc.getEndOfArc() > this.getStartOfArc()) {
              return new GM_Aggregate<IGeometry>();
            }
          }
        } else {
          // both arcs have different directions

          // retrieve the direct and the indirect arc
          IArc2 arcD = (this.getDirection() == ArcDirection.DIRECT) ? this
              : arc;
          IArc2 arcI = (this.getDirection() == ArcDirection.DIRECT) ? arc
              : this;

          // arcs are touching in one extrem point
          if (Math.abs(arcD.getStartOfArc() - arcI.getStartOfArc()) == 0) {
            return new GM_Point(new DirectPosition(c1.getX() + r1
                * Math.cos(this.getStartOfArc()), c1.getY() + r1
                * Math.sin(this.getStartOfArc())));
          }
          if (Math.abs(arcD.getEndOfArc() - arcI.getEndOfArc()) == 0) {
            return new GM_Point(new DirectPosition(c1.getX() + r1
                * Math.cos(this.getEndOfArc()), c1.getY() + r1
                * Math.sin(this.getEndOfArc())));
          }

          // cases where arcs are disjoints
          if (arcD.getStartOfArc() < arcD.getEndOfArc()
              && arcI.getStartOfArc() > arcI.getEndOfArc()
              && (arcD.getStartOfArc() > arcI.getStartOfArc() || arcD
                  .getEndOfArc() < arcI.getEndOfArc())) {
            return new GM_Aggregate<IGeometry>();
          }
          if (arcD.getStartOfArc() > arcD.getEndOfArc()
              && arcI.getStartOfArc() > arcI.getEndOfArc()
              && arcD.getStartOfArc() > arcI.getStartOfArc()
              && arcD.getEndOfArc() < arcI.getEndOfArc()) {
            return new GM_Aggregate<IGeometry>();
          }
          if (arcD.getStartOfArc() < arcD.getEndOfArc()
              && arcI.getStartOfArc() < arcI.getEndOfArc()
              && arcD.getStartOfArc() > arcI.getStartOfArc()
              && arcD.getEndOfArc() < arcI.getEndOfArc()) {
            return new GM_Aggregate<IGeometry>();
          }
        }

        GM_Arc2.logger.error("non implemented case");
        GM_Arc2.logger.error(" * c " + c1);
        GM_Arc2.logger.error("" + this.getDirection());
        GM_Arc2.logger.error("" + arc.getDirection());
        GM_Arc2.logger.error("");
        GM_Arc2.logger.error(" * pt s1 " + this.getStartPoint().getDirect());
        GM_Arc2.logger.error(" * pt e1 " + this.getEndPoint().getDirect());
        GM_Arc2.logger.error(" * pt e1s1 "
            + this.getEndPoint().getDirect().distance(
                this.getStartPoint().getDirect()));
        GM_Arc2.logger.error(" * pt s2 " + arc.getStartPoint().getDirect());
        GM_Arc2.logger.error(" * pt e2 " + arc.getEndPoint().getDirect());
        GM_Arc2.logger.error(" * pt e2s2 "
            + arc.getEndPoint().getDirect().distance(
                arc.getStartPoint().getDirect()));
        GM_Arc2.logger.error("");
        GM_Arc2.logger.error(" * a s1 " + this.getStartOfArc() * 180 / Math.PI);
        GM_Arc2.logger.error(" * a e1 " + this.getEndOfArc() * 180 / Math.PI);
        GM_Arc2.logger.error(" * a delta1 " + this.getDelta() * 180 / Math.PI);
        GM_Arc2.logger.error(" * a s2 " + arc.getStartOfArc() * 180 / Math.PI);
        GM_Arc2.logger.error(" * a e2 " + arc.getEndOfArc() * 180 / Math.PI);
        GM_Arc2.logger.error(" * a delta2 " + arc.getDelta() * 180 / Math.PI);
        return new GM_Aggregate<IGeometry>();
      }

      // the intersection between the arcs is no more than two points
      // compute the two possible points
      double alpha = Math.acos((d * d + r1 * r1 - r2 * r2) / (2 * d * r1));
      double angle = c1.orientation(c2);
      GM_Point pt1 = new GM_Point(new DirectPosition(c1.getX() + r1
          * Math.cos(angle + alpha), c1.getY() + r1 * Math.sin(angle + alpha)));
      GM_Point pt2 = new GM_Point(new DirectPosition(c1.getX() + r1
          * Math.cos(angle - alpha), c1.getY() + r1 * Math.sin(angle - alpha)));

      // check if these points are contained in the arc and the line segment
      boolean cont1 = this.contains2(pt1.getPosition())
          && arc.contains2(pt1.getPosition());
      boolean cont2 = this.contains2(pt2.getPosition())
          && arc.contains2(pt2.getPosition());

      // return one of the point, both or none
      if (cont1 && cont2) {
        GM_MultiPoint mp = new GM_MultiPoint();
        mp.add(pt1);
        mp.add(pt2);
        return mp;
      } else if (cont1 && !cont2) {
        return pt1;
      } else if (!cont1 && cont2) {
        return pt2;
      } else {
        return new GM_Aggregate<IGeometry>();
      }
    }

    // compute intersection between an arc and a segment
    if (geom instanceof ILineSegment) {

      // retrieve segment's coordinates
      ILineSegment ls = (ILineSegment) geom;
      IDirectPosition dp0 = ls.getStartPoint();
      IDirectPosition dp1 = ls.getEndPoint();

      // retrieve the center of the arc
      IDirectPosition c = this.getCenter();

      // projection of the center of the arc on the line
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
      double r = this.getRadius();

      // if that distance is greater than the radius, no intersection possible,
      // return an empty geometry
      if (dist > r) {
        return new GM_Aggregate<IGeometry>();
      }

      // if that distance is equal to the radius, a candidate to be the
      // intersection is the projection
      // (the circle is tangent to the line)
      if (dist == r) {
        GM_Point pt = new GM_Point(dpp);
        if (this.contains2(dpp) && ls.contains(pt)) {
          return pt;
        }
        return new GM_Aggregate<IGeometry>();
      }

      // two points could be at the intersection
      // compute the two candidate points
      double alpha = Math.acos(dist / r);
      double angle = Math.atan2(dpp.getY() - c.getY(), dpp.getX() - c.getX());
      GM_Point pt1 = new GM_Point(new DirectPosition(c.getX() + r
          * Math.cos(angle + alpha), c.getY() + r * Math.sin(angle + alpha)));
      GM_Point pt2 = new GM_Point(new DirectPosition(c.getX() + r
          * Math.cos(angle - alpha), c.getY() + r * Math.sin(angle - alpha)));

      // check if these points are contained in the arc and the line segment
      double lg = dp0.distance(dp1);
      boolean cont1 = this.contains2(pt1.getPosition())
          && (dp0.distance(pt1.getPosition()) <= lg && dp1.distance(pt1
              .getPosition()) <= lg);
      boolean cont2 = this.contains2(pt2.getPosition())
          && (dp0.distance(pt2.getPosition()) <= lg && dp1.distance(pt2
              .getPosition()) <= lg);

      // return one of the points, both or none
      if (cont1 && cont2) {
        GM_MultiPoint mp = new GM_MultiPoint();
        mp.add(pt1);
        mp.add(pt2);
        return mp;
      } else if (cont1 && !cont2) {
        return pt1;
      } else if (!cont1 && cont2) {
        return pt2;
      } else {
        return new GM_Aggregate<IGeometry>();
      }
    }
    GM_Arc2.logger.error("Error in intersection computation of " + this
        + " and " + geom);
    return new GM_Aggregate<IGeometry>();
  }

  /**
   * check if a point belongs to the arc. the point is considered as beeing on
   * the circle (its distance to the center is equal to the radius).
   * @param pt
   * @return
   */
  @Override
  public boolean contains2(IDirectPosition pt) {
    // the angle, in radian, within [-Pi, Pi], between the [O, x] axis and the
    // line [O, pt]
    double angle = Math.atan2(pt.getY() - this.getCenter().getY(), pt.getX()
        - this.getCenter().getX());

    double delta = this.getDelta();
    double startAngle = this.getStartOfArc();
    double endAngle = this.getEndOfArc();
    if (delta > 0) {
      if (endAngle > startAngle) {
        return (angle >= startAngle) && (angle <= endAngle);
      }
      if (endAngle < startAngle) {
        return (angle >= startAngle) || (angle <= endAngle);
      }
    } else if (delta < 0) {
      if (endAngle > startAngle) {
        return (angle <= startAngle) || (angle >= endAngle);
      }
      if (endAngle < startAngle) {
        return (angle <= startAngle) && (angle >= endAngle);
      }
    }
    return false;
  }

  @Override
  public boolean contains(IGeometry geom) {
    if (geom instanceof IPoint) {
      IDirectPosition pt = ((IPoint) geom).getPosition();

      // retrieve the center and the radius of the arc
      IDirectPosition c = this.getCenter();
      double r = this.getRadius();

      // if the point is not on the circle, return false
      if (pt.distance(c) != r) {
        return false;
      }

      return this.contains2(pt);
    }
    GM_Arc2.logger.error("Error in contains computation of " + this + " and "
        + geom);
    return true;
  }

  @Override
  public double distance(IGeometry geom) {

    if (geom instanceof ILineSegment) {

      // if the arc intersects the geom, the distance is zero
      IGeometry inter = this.intersection(geom);
      if (inter != null && !(inter.isEmpty())) {
        return 0.0;
      }

      // retrieve segment's coordinates
      ILineSegment ls = (ILineSegment) geom;
      IDirectPosition dp0 = ls.getStartPoint();
      IDirectPosition dp1 = ls.getEndPoint();

      // retrieve the center and the radius of the arc
      IDirectPosition c = this.getCenter();
      double r = this.getRadius();

      double dist = Double.MAX_VALUE;
      double angle;
      DirectPosition dpi;

      // compute the possible distances:

      // extrem points distances
      dist = Math.min(dist, this.getStartPoint().getDirect().distance(dp0));
      dist = Math.min(dist, this.getStartPoint().getDirect().distance(dp1));
      dist = Math.min(dist, this.getEndPoint().getDirect().distance(dp0));
      dist = Math.min(dist, this.getEndPoint().getDirect().distance(dp1));

      // the distance to the intersection point between (c, dpO) and the arc, if
      // it belong to the arc
      angle = Math.atan2(dp0.getY() - c.getY(), dp0.getX() - c.getX());
      dpi = new DirectPosition(c.getX() + r * Math.cos(angle), c.getY() + r
          * Math.sin(angle));
      if (this.contains2(dpi)) {
        dist = Math.min(dist, dpi.distance(dp0));
      }

      // the distance to the intersection point between (c, dp1) and the arc, if
      // it belong to the arc
      angle = Math.atan2(dp1.getY() - c.getY(), dp1.getX() - c.getX());
      dpi = new DirectPosition(c.getX() + r * Math.cos(angle), c.getY() + r
          * Math.sin(angle));
      if (this.contains2(dpi)) {
        dist = Math.min(dist, dpi.distance(dp1));
      }

      // compute the 1-vector of the segment (used to compute projection points)
      double dx = dp1.getX() - dp0.getX();
      double dy = dp1.getY() - dp0.getY();
      double lg = Math.sqrt(dx * dx + dy * dy);
      dx = dx / lg;
      dy = dy / lg;

      double ps;
      DirectPosition proj;

      // the distance to the projection of the center of the arc on the line, if
      // it belongs to the segment, and the point belong to the arc
      ps = dx * (c.getX() - dp0.getX()) + dy * (c.getY() - dp0.getY());
      proj = new DirectPosition(dp0.getX() + dx * ps, dp0.getY() + dy * ps);

      // check if proj belongs to the segment
      if (dp0.distance(proj) <= lg && dp1.distance(proj) <= lg) {
        // compute the intersection between (c,proj) and the arc
        angle = Math.atan2(proj.getY() - c.getY(), proj.getX() - c.getX());
        dpi = new DirectPosition(c.getX() + r * Math.cos(angle), c.getY() + r
            * Math.sin(angle));
        if (this.contains2(dpi)) {
          dist = Math.min(dist, dpi.distance(proj));
        }
      }

      // the distance between the start point of the arc and its projection on
      // the line, if it belongs to the segment
      ps = dx * (this.getStartPoint().getDirect().getX() - dp0.getX()) + dy
          * (this.getStartPoint().getDirect().getY() - dp0.getY());
      proj = new DirectPosition(dp0.getX() + dx * ps, dp0.getY() + dy * ps);
      if (dp0.distance(proj) <= lg && dp1.distance(proj) <= lg) {
        Math.min(dist, proj.distance(this.getStartPoint().getDirect()));
      }

      // and idem with the end point
      ps = dx * (this.getEndPoint().getDirect().getX() - dp0.getX()) + dy
          * (this.getEndPoint().getDirect().getY() - dp0.getY());
      proj = new DirectPosition(dp0.getX() + dx * ps, dp0.getY() + dy * ps);
      if (dp0.distance(proj) <= lg && dp1.distance(proj) <= lg) {
        Math.min(dist, proj.distance(this.getEndPoint().getDirect()));
      }

      return dist;
    }
    if (geom instanceof ILineString) {
      // return the min of distances between the arc and the segments composing
      // geom
      IDirectPositionList dpl = geom.coord();
      double dist = Double.MAX_VALUE;
      IDirectPosition dp0 = dpl.get(0), dp1;
      for (int i = 1; i < dpl.size(); i++) {
        dp1 = dpl.get(i);
        double dist_ = this.distance(new GM_LineSegment(dp0, dp1));
        dist = Math.min(dist, dist_);
        dp0 = dp1;
      }
      return dist;
    }
    GM_Arc2.logger.error("Error in distance computation of " + this + " and "
        + geom + ". implementation is missing");
    return 0;
  }

}
