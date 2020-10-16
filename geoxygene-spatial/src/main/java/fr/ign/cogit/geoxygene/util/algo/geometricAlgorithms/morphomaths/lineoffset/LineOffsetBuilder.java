/**
 * @author julien Gaffuri 29 juin 2009
 */
package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.lineoffset;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IArc2.ArcDirection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Arc2;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.Side;

/**
 * Message de Guillaume : Attention, ce code ne fonctionne pas car non terminé
 * par Julien!!!!! A voir ce qu'on en fait.
 * @author julien Gaffuri 12 juillet 2009
 */
public class LineOffsetBuilder {
  private static Logger logger = LogManager.getLogger(LineOffsetBuilder.class
      .getName());

  /**
   * the line
   */
  private ILineString line = null;

  /**
   * the offset distance
   */
  private double offsetDistance = 0.0;

  /**
   * the left offset segments
   */
  private ArrayList<OffsetSegment> leftOffsetSegments = null;

  /**
   * @return
   */
  public ArrayList<OffsetSegment> getLeftOffsetSegments() {
    if (this.leftOffsetSegments == null) {
      this.compute();
    }
    return this.leftOffsetSegments;
  }

  /**
   * the right offset segments
   */
  private ArrayList<OffsetSegment> rightOffsetSegments = null;

  /**
   * @return
   */
  public ArrayList<OffsetSegment> getRightOffsetSegments() {
    if (this.rightOffsetSegments == null) {
      this.compute();
    }
    return this.rightOffsetSegments;
  }

  /**
   * intersections of offset segments
   */
  private ArrayList<OffsetSegmentIntersection> intersections = null;

  /**
   * @return
   */
  public ArrayList<OffsetSegmentIntersection> getIntersections() {
    if (this.intersections == null) {
      this.computeIntersections();
    }
    return this.intersections;
  }

  /**
     */
  private GM_Curve leftOffsetLine = null;

  /**
   * @return
   */
  public ICurve getLeftOffsetLine() {
    if (this.line == null) {
      return null;
    } else if (this.line.isEmpty()) {
      return new GM_LineString();
    }

    if (this.leftOffsetLine == null) {
      this.compute();
    }
    return this.leftOffsetLine;
  }

  /**
     */
  private GM_Curve rightOffsetLine = null;

  /**
   * @return
   */
  public ICurve getRightOffsetLine() {
    if (this.line == null) {
      return null;
    } else if (this.line.isEmpty()) {
      return new GM_LineString();
    }

    if (this.rightOffsetLine == null) {
      this.compute();
    }
    return this.rightOffsetLine;
  }

  /**
   * the side of the offset under computation
   */
  private Side side;

  /**
   * the offset list under computation
   */
  private ArrayList<OffsetSegment> getOffsetSegments() {
    if (this.side == Side.LEFT) {
      return this.leftOffsetSegments;
    }
    return this.rightOffsetSegments;
  }

  private void setOffsetSegments(ArrayList<OffsetSegment> list) {
    if (this.side == Side.LEFT) {
      this.leftOffsetSegments = list;
    } else {
      this.rightOffsetSegments = list;
    }
  }

  /**
   * default constructor: a line offset builder of a line, with a given distance
   * @param line
   * @param offsetDistance
   */
  public LineOffsetBuilder(ILineString line, double offsetDistance) {
    this.line = line;
    this.offsetDistance = offsetDistance;
  }

  private static double DISTANCE_TOL = 0.00001;
  private static double ANGLE_TOL = 0.00001;

  /**
   * compute the offset line
   */
  public void compute() {

    // trivial case: if the offset distance is zero
    if (this.offsetDistance == 0.0) {
      this.leftOffsetLine = (GM_Curve) this.line.clone();
      this.rightOffsetLine = (GM_Curve) this.line.clone();
      return;
    }

    // compute the offset segments of both sides
    this.computeOffsetSegments(Side.LEFT);
    this.computeOffsetSegments(Side.RIGHT);

    // cut the intersecting offset segments
    this.cut();

    // delete segment offset too far from the line
    this.deleteTooCloseOffsetSegments(Side.LEFT);
    this.deleteTooCloseOffsetSegments(Side.RIGHT);

    // compute the intersections
    this.computeIntersections();
  }

  /**
   * cut the offset segments according to their intersections
   * @param angleTolerance
   * @param distanceTolerance
   */
  private void cut() {

    // retrieve the intersections between the offset segments that need to be
    // cut, and cut them, while some can be found
    OffsetSegmentIntersection inter = this.getSegmentOffsetIntersectionToCut();
    while (inter != null) {
      this.cut(inter);
      inter = this.getSegmentOffsetIntersectionToCut();
    }
  }

  /**
   * delete the segments from one side that are too closed to the line (has to
   * be computed after the cut method, of course)
   * @param side_
   * @param distanceTolerance
   */
  private void deleteTooCloseOffsetSegments(Side side_) {
    this.side = side_;

    // the list of too closed offset segments to delete
    ArrayList<OffsetSegment> offsetSegmentsToDelete = new ArrayList<OffsetSegment>();

    // retrieve the segments to delete
    for (OffsetSegment offseg : this.getOffsetSegments()) {
      if (offseg.distance(this.line) < this.offsetDistance
          - LineOffsetBuilder.DISTANCE_TOL) {
        offsetSegmentsToDelete.add(offseg);
      }
    }

    // delete the segments
    this.getOffsetSegments().removeAll(offsetSegmentsToDelete);
  }

  /**
   * compute offset segments
   */
  private void computeOffsetSegments(Side side_) {
    this.side = side_;

    // retrieve the line points
    IDirectPositionList dpl = this.line.coord();

    // if there is no segment, out
    int segmentsNb = dpl.size() - 1;
    if (segmentsNb <= 0) {
      return;
    }

    // build the offset segments table
    this.setOffsetSegments(new ArrayList<OffsetSegment>());

    // build the offset segments

    // specific case: only one segment
    if (segmentsNb == 1) {
      this.getOffsetSegments().add(
          this.computeOffsetSegment(0, null, dpl.get(0), dpl.get(1), null));
      return;
    }

    // the first segment
    if (this.line.isClosed()) {
      this.getOffsetSegments().add(
          this.computeOffsetSegment(0, dpl.get(segmentsNb - 1), dpl.get(0),
              dpl.get(1), dpl.get(2)));
    } else {
      this.getOffsetSegments()
          .add(
              this.computeOffsetSegment(0, null, dpl.get(0), dpl.get(1),
                  dpl.get(2)));
    }

    // the following segments
    for (int i = 1; i < segmentsNb - 1; i++) {
      // construit le segment decale de [dp(i), dp(i+1)]
      this.getOffsetSegments().add(
          this.computeOffsetSegment(1, dpl.get(i - 1), dpl.get(i),
              dpl.get(i + 1), dpl.get(i + 2)));
    }

    // the last segment
    if (this.line.isClosed()) {
      this.getOffsetSegments().add(
          this.computeOffsetSegment(segmentsNb - 1, dpl.get(segmentsNb - 2),
              dpl.get(segmentsNb - 1), dpl.get(segmentsNb), dpl.get(1)));
    } else {
      this.getOffsetSegments().add(
          this.computeOffsetSegment(segmentsNb - 1, dpl.get(segmentsNb - 2),
              dpl.get(segmentsNb - 1), dpl.get(segmentsNb), null));
    }
  }

  /**
   * build the offset segment between two direct position dp0 and dp1 (the
   * previous and following direct position dp0_ et dp1_ are used to build the
   * arcs)
   * 
   * @param idSegment
   * @param dp0_
   * @param dp0
   * @param dp1
   * @param dp1_
   */
  private OffsetSegment computeOffsetSegment(int idSegment,
      IDirectPosition dp0_, IDirectPosition dp0, IDirectPosition dp1,
      IDirectPosition dp1_) {
    // the offset vector of the segment
    IDirectPosition offset = this.offsetVector(dp0, dp1);

    // two offset points of the segment
    IDirectPosition dp0off = new DirectPosition(dp0.getX() + offset.getX(),
        dp0.getY() + offset.getY());
    IDirectPosition dp1off = new DirectPosition(dp1.getX() + offset.getX(),
        dp1.getY() + offset.getY());

    // take into account the side
    double s = 0;
    if (this.side == Side.RIGHT) {
      s = -1;
    } else {
      s = 1;
    }

    // compute the direction of the arc
    ArcDirection direction = (this.side == Side.RIGHT) ? ArcDirection.DIRECT
        : ArcDirection.INDIRECT;

    // compute the circle arcs

    // starting arc
    GM_Arc2 arc0 = null;
    if (dp0_ == null) {
      // a quarter circle

      // compute the orientation of the start and end of the arc
      double or = dp0.orientation(dp1);
      double start = or - Math.PI;
      if (start > Math.PI) {
        start -= 2 * Math.PI;
      }
      if (start <= -Math.PI) {
        start += 2 * Math.PI;
      }
      double end = or + s * Math.PI * 0.5;
      if (end > Math.PI) {
        end -= 2 * Math.PI;
      }
      if (end <= -Math.PI) {
        end += 2 * Math.PI;
      }

      arc0 = new GM_Arc2(dp0, this.offsetDistance, start, end, direction);
    } else {
      // compute the starting deviation, within ]0, 2Pi[
      double dev = Math.atan2(
          (dp0_.getX() - dp0.getX()) * (dp1.getY() - dp0.getY())
              - (dp0_.getY() - dp0.getY()) * (dp1.getX() - dp0.getX()),
          (dp0_.getX() - dp0.getX()) * (dp1.getX() - dp0.getX())
              + (dp0_.getY() - dp0.getY()) * (dp1.getY() - dp0.getY()));
      if (dev < 0.0) {
        dev += 2 * Math.PI;
      }

      // build arc only if it turns toward the good direction
      if ((this.side == Side.RIGHT && dev > Math.PI)
          || (this.side == Side.LEFT && dev < Math.PI)) {
        // an arc has to be built

        // offset of the first point according to the previous segment
        IDirectPosition offset_ = this.offsetVector(dp0_, dp0);

        // compute the orientation of the start and end of the arc
        double start = Math.atan2(offset.getY() + offset_.getY(), offset.getX()
            + offset_.getX());
        double end = Math.atan2(offset.getY(), offset.getX());

        arc0 = new GM_Arc2(dp0, this.offsetDistance, start, end, direction);
      }
    }

    // ending arc
    GM_Arc2 arc1 = null;
    if (dp1_ == null) {
      // a quarter circle

      // compute the orientation of the start and end of the arc
      double or = dp0.orientation(dp1);
      double start = or + s * Math.PI * 0.5;
      if (start > Math.PI) {
        start -= 2 * Math.PI;
      }
      if (start <= -Math.PI) {
        start += 2 * Math.PI;
      }
      double end = or;
      if (end > Math.PI) {
        end -= 2 * Math.PI;
      }
      if (end <= -Math.PI) {
        end += 2 * Math.PI;
      }

      arc1 = new GM_Arc2(dp1, this.offsetDistance, start, end, direction);
    } else {
      // compute the ending deviation, within ]0, 2Pi[
      double dev = Math.atan2(
          (dp0.getX() - dp1.getX()) * (dp1_.getY() - dp1.getY())
              - (dp0.getY() - dp1.getY()) * (dp1_.getX() - dp1.getX()),
          (dp0.getX() - dp1.getX()) * (dp1_.getX() - dp1.getX())
              + (dp0.getY() - dp1.getY()) * (dp1_.getY() - dp1.getY()));
      if (dev < 0.0) {
        dev += 2 * Math.PI;
      }

      // build arc only if it turns toward the good direction
      if ((this.side == Side.RIGHT && dev > Math.PI)
          || (this.side == Side.LEFT && dev < Math.PI)) {
        // an arc has to be built

        // offset of the first point according to the previous segment
        IDirectPosition offset_ = this.offsetVector(dp1, dp1_);

        // compute the orientation of the start and end of the arc
        double start = Math.atan2(offset.getY(), offset.getX());
        double end = Math.atan2(offset.getY() + offset_.getY(), offset.getX()
            + offset_.getX());

        arc1 = new GM_Arc2(dp1, this.offsetDistance, start, end, direction);
      }
    }
    return new OffsetSegment(idSegment, this.side, arc0, new GM_LineSegment(
        dp0off, dp1off), arc1);
  }

  /**
   * compute the offset vector from a segment defined by two points
   * 
   * @param dp0
   * @param dp1
   * @return
   */
  private IDirectPosition offsetVector(IDirectPosition dp0, IDirectPosition dp1) {
    double dx = dp1.getX() - dp0.getX();
    double dy = dp1.getY() - dp0.getY();
    double n = Math.sqrt(dx * dx + dy * dy);

    // take into account the side
    double s = 0;
    if (this.side == Side.RIGHT) {
      s = -1;
    } else {
      s = 1;
    }

    // the offset vector
    double decx = s * this.offsetDistance * -dy / n;
    double decy = s * this.offsetDistance * dx / n;

    // return the offset point
    return new DirectPosition(decx, decy);
  }

  /**
   * construit les intersections des segments decales
   */
  private void computeIntersections() {

    // build the intersections list
    this.intersections = new ArrayList<OffsetSegmentIntersection>();

    // retrieve a list of all offset segments
    ArrayList<OffsetSegment> all = new ArrayList<OffsetSegment>();
    all.addAll(this.getLeftOffsetSegments());
    all.addAll(this.getRightOffsetSegments());
    int nb = all.size();

    OffsetSegment si, sj;
    for (int i = 0; i < nb; i++) {
      si = all.get(i);
      for (int j = i + 1; j < nb; j++) {
        sj = all.get(j);

        // consider intersection between si and sj
        IGeometry inter = si.intersection(sj);

        if (inter == null || inter.isEmpty()) {
          continue;
        } else if (inter instanceof IPoint) {
          this.intersections.add(new OffsetSegmentIntersection(si, sj,
              ((IPoint) inter).getPosition()));
        } else if (inter instanceof IMultiPoint) {
          IMultiPoint mpt = (IMultiPoint) inter;
          for (IPoint pt : mpt.getList()) {
            this.intersections.add(new OffsetSegmentIntersection(si, sj, pt
                .getPosition()));
          }
        } else {
          LineOffsetBuilder.logger
              .warn("attention: intersection de segments decales n'est pas un point: "
                  + inter);
        }
      }
    }
  }

  /**
   * get an intersection composed of segment offsets to cut this is the case
   * when the intersection point between both is
   * @return
   */
  private OffsetSegmentIntersection getSegmentOffsetIntersectionToCut() {

    // retrieve a list of all offset segments
    ArrayList<OffsetSegment> all = new ArrayList<OffsetSegment>();
    all.addAll(this.getLeftOffsetSegments());
    all.addAll(this.getRightOffsetSegments());
    int nb = all.size();

    // go throught the pairs of segments
    OffsetSegment si, sj;
    for (int i = 0; i < nb; i++) {
      si = all.get(i);
      for (int j = i + 1; j < nb; j++) {
        sj = all.get(j);

        // consider intersection between si and sj
        IGeometry inter = si.intersection(sj);

        // no intersection
        if (inter == null || inter.isEmpty()) {
          continue;
        } else if (inter instanceof IPoint) {
          // intersection is only one point: return the intersection only if the
          // point is not an extrem point of one of the segments offset
          // otherwise, no cut is needed
          IDirectPosition dp = ((IPoint) inter).getPosition();
          if (!si.isExtremPoint(dp, LineOffsetBuilder.ANGLE_TOL,
              LineOffsetBuilder.DISTANCE_TOL)
              || !sj.isExtremPoint(dp, LineOffsetBuilder.ANGLE_TOL,
                  LineOffsetBuilder.DISTANCE_TOL)) {
            return new OffsetSegmentIntersection(si, sj, dp);
          }
        } else if (inter instanceof IMultiPoint) {
          IMultiPoint mpt = (IMultiPoint) inter;
          for (IPoint pt : mpt.getList()) {
            if (!si.isExtremPoint(pt.getPosition(),
                LineOffsetBuilder.ANGLE_TOL, LineOffsetBuilder.DISTANCE_TOL)
                || !sj
                    .isExtremPoint(pt.getPosition(),
                        LineOffsetBuilder.ANGLE_TOL,
                        LineOffsetBuilder.DISTANCE_TOL)) {
              return new OffsetSegmentIntersection(si, sj, pt.getPosition());
            }
          }
        } else {
          LineOffsetBuilder.logger
              .warn("attention: intersection de segments decales n'est pas un point: "
                  + inter);
        }
      }
    }
    // no intersection has been found: return null
    return null;
  }

  /**
   * cut the offset segments linked to an intersection
   */
  public void cut(OffsetSegmentIntersection inter) {
    IDirectPosition pos = inter.pos;

    // cut all offset segments linked to the intersection
    for (OffsetSegment offseg : inter.offsetSegments) {
      // the considered side is the one of the current offset segment
      this.side = offseg.side;

      // check at which part of the offset segment the intersection belongs

      if (offseg.arc0 != null && offseg.arc0.contains2(pos)) {
        // the point belongs to the first arc

        // retrieve the arc characterisitcs
        IDirectPosition c = offseg.arc0.getCenter();
        double r = offseg.arc0.getRadius();
        ArcDirection direction = offseg.arc0.getDirection();

        // compute the new angle value
        double angle = Math.atan2(pos.getY() - c.getY(), pos.getX() - c.getX());

        // possible case: pt is at the begining of arc0
        if (Math.abs(angle - offseg.arc0.getStartOfArc()) <= LineOffsetBuilder.ANGLE_TOL) {
          // nothing to cut
          continue;
        }
        // other possible case: pt is at the end of arc0
        else if (Math.abs(angle - offseg.arc0.getEndOfArc()) <= LineOffsetBuilder.ANGLE_TOL) {
          // cut the offset segment between the first arc and the segment
          this.getOffsetSegments().remove(offseg);
          this.getOffsetSegments().add(
              new OffsetSegment(offseg.idSegment, this.side, offseg.arc0, null,
                  null));
          if (offseg.segment != null || offseg.arc1 != null) {
            this.getOffsetSegments().add(
                new OffsetSegment(offseg.idSegment, this.side, null,
                    offseg.segment, offseg.arc1));
          }
        } else {
          // build new arcs
          GM_Arc2 arc_ = new GM_Arc2(c, r, offseg.arc0.getStartOfArc(), angle,
              direction);
          GM_Arc2 arc__ = new GM_Arc2(c, r, angle, offseg.arc0.getEndOfArc(),
              direction);

          // build the new offset segments
          this.getOffsetSegments().remove(offseg);
          this.getOffsetSegments().add(
              new OffsetSegment(offseg.idSegment, this.side, arc_, null, null));
          this.getOffsetSegments().add(
              new OffsetSegment(offseg.idSegment, this.side, arc__,
                  offseg.segment, offseg.arc1));
        }
      } else if (offseg.segment != null
          && offseg.segment.getStartPoint().distance(pos) <= offseg.segment
              .length()
          && offseg.segment.getEndPoint().distance(pos) <= offseg.segment
              .length()) {
        // the intersection point belongs to the segment

        // possible case: pt is at the begining of the segment
        if (pos.distance(offseg.segment.getStartPoint()) <= LineOffsetBuilder.DISTANCE_TOL) {
          // cut the offset segment between the first arc and the segment
          this.getOffsetSegments().remove(offseg);
          if (offseg.arc0 != null) {
            this.getOffsetSegments().add(
                new OffsetSegment(offseg.idSegment, this.side, offseg.arc0,
                    null, null));
          }
          this.getOffsetSegments().add(
              new OffsetSegment(offseg.idSegment, this.side, null,
                  offseg.segment, offseg.arc1));
        }
        // other possible case: pt is at the end of the segment
        else if (pos.distance(offseg.segment.getEndPoint()) <= LineOffsetBuilder.DISTANCE_TOL) {
          // cut the offset segment between the segment and the last arc
          this.getOffsetSegments().remove(offseg);
          this.getOffsetSegments().add(
              new OffsetSegment(offseg.idSegment, this.side, offseg.arc0,
                  offseg.segment, null));
          if (offseg.arc1 != null) {
            this.getOffsetSegments().add(
                new OffsetSegment(offseg.idSegment, this.side, null, null,
                    offseg.arc1));
          }
        } else {
          // the segment has to be cut
          // build the new segments
          GM_LineSegment ls0 = new GM_LineSegment(
              offseg.segment.getStartPoint(), pos);
          GM_LineSegment ls1 = new GM_LineSegment(pos,
              offseg.segment.getEndPoint());

          // build the new offset segments
          this.getOffsetSegments().remove(offseg);
          this.getOffsetSegments().add(
              new OffsetSegment(offseg.idSegment, this.side, offseg.arc0, ls0,
                  null));
          this.getOffsetSegments().add(
              new OffsetSegment(offseg.idSegment, this.side, null, ls1,
                  offseg.arc1));
        }

      } else if (offseg.arc1 != null && offseg.arc1.contains2(pos)) {
        // the point belongs to the second arc

        // retrieve the arc characterisitcs
        IDirectPosition c = offseg.arc1.getCenter();
        double r = offseg.arc1.getRadius();
        ArcDirection direction = offseg.arc1.getDirection();

        // compute the new angle value
        double angle = Math.atan2(pos.getY() - c.getY(), pos.getX() - c.getX());

        // possible case: pt is at the end of arc1
        if (Math.abs(angle - offseg.arc1.getEndOfArc()) <= LineOffsetBuilder.ANGLE_TOL) {
          // nothing to cut
          continue;
        }
        // other possible case: pt is at the start of arc1
        else if (Math.abs(angle - offseg.arc1.getStartOfArc()) <= LineOffsetBuilder.ANGLE_TOL) {
          // cut the offset segment between the segment and the last arc
          this.getOffsetSegments().remove(offseg);
          if (offseg.arc0 != null || offseg.segment != null) {
            this.getOffsetSegments().add(
                new OffsetSegment(offseg.idSegment, this.side, offseg.arc0,
                    offseg.segment, null));
          }
          this.getOffsetSegments().add(
              new OffsetSegment(offseg.idSegment, this.side, null, null,
                  offseg.arc1));
        } else {
          // build new arcs
          GM_Arc2 arc_ = new GM_Arc2(c, r, offseg.arc1.getStartOfArc(), angle,
              direction);
          GM_Arc2 arc__ = new GM_Arc2(c, r, angle, offseg.arc1.getEndOfArc(),
              direction);

          // build the new offset segments
          this.getOffsetSegments().remove(offseg);
          this.getOffsetSegments().add(
              new OffsetSegment(offseg.idSegment, this.side, offseg.arc0,
                  offseg.segment, arc_));
          this.getOffsetSegments()
              .add(
                  new OffsetSegment(offseg.idSegment, this.side, null, null,
                      arc__));
        }
      }
    }
  }
}
