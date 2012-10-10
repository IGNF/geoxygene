/**
 * @author julien Gaffuri 29 juin 2009
 */
package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.lineoffset;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IArc2;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.Side;

/**
 * @author julien Gaffuri 29 juin 2009
 * 
 */
public class OffsetSegment {
  private static Logger logger = Logger
      .getLogger(OffsetSegment.class.getName());

  /**
   * the id of the segment of the line
   */
  public int idSegment;

  /**
   * the side of the offset segment
   */
  public Side side;

  /**
   * the beginning arc (can be null)
   */
  public IArc2 arc0;

  /**
   * the central segment (can be null)
   */
  public ILineSegment segment;

  /**
   * the ending arc (can be null, too)
   */
  public IArc2 arc1;

  public OffsetSegment(int idSegment, Side side, IArc2 arc0,
      ILineSegment segment, IArc2 arc1) {
    this.idSegment = idSegment;
    this.side = side;
    this.arc0 = arc0;
    this.segment = segment;
    this.arc1 = arc1;
  }

  /**
   * compute the intersection with another offset segment
   * @param sd
   * @return
   */
  public IGeometry intersection(OffsetSegment sd) {
    IGeometry intersection = new GM_Aggregate<IGeometry>();
    IGeometry intersec;

    // segment
    if (this.segment != null) {
      if (sd.segment != null) {
        intersec = this.segment.intersection(sd.segment);
        if (intersec != null) {
          intersection = intersection.union(intersec);
        }
      }
      if (sd.arc0 != null) {
        intersec = this.segment.intersection(sd.arc0);
        if (intersec != null) {
          intersection = intersection.union(intersec);
        }
      }
      if (sd.arc1 != null) {
        intersec = this.segment.intersection(sd.arc1);
        if (intersec != null) {
          intersection = intersection.union(intersec);
        }
      }
    }

    // arc0
    if (this.arc0 != null) {
      if (sd.segment != null) {
        intersec = this.arc0.intersection(sd.segment);
        if (intersec != null) {
          intersection = intersection.union(intersec);
        }
      }
      if (sd.arc0 != null) {
        intersec = this.arc0.intersection(sd.arc0);
        if (intersec != null) {
          intersection = intersection.union(intersec);
        }
      }
      if (sd.arc1 != null) {
        intersec = this.arc0.intersection(sd.arc1);
        if (intersec != null) {
          intersection = intersection.union(intersec);
        }
      }
    }

    // arc1
    if (this.arc1 != null) {
      if (sd.segment != null) {
        intersec = this.arc1.intersection(sd.segment);
        if (intersec != null) {
          intersection = intersection.union(intersec);
        }
      }
      if (sd.arc0 != null) {
        intersec = this.arc1.intersection(sd.arc0);
        if (intersec != null) {
          intersection = intersection.union(intersec);
        }
      }
      if (sd.arc1 != null) {
        intersec = this.arc1.intersection(sd.arc1);
        if (intersec != null) {
          intersection = intersection.union(intersec);
        }
      }
    }
    return intersection;
  }

  /**
   * compute the distance between an offset segment and a linestring
   * @param line
   * @return
   */
  public double distance(ILineString line) {

    // check if at least one of the components of the object is not null
    if (this.arc0 == null && this.segment == null && this.arc1 == null) {
      OffsetSegment.logger
          .error("Impossible to compute distance between linestring and offset segment: all components of offset segment are null");
      return Double.MAX_VALUE;
    }

    double dist = Double.MAX_VALUE;
    if (this.arc0 != null) {
      dist = Math.min(dist, this.arc0.distance(line));
    }
    if (this.segment != null) {
      dist = Math.min(dist, this.segment.distance(line));
    }
    if (this.arc1 != null) {
      dist = Math.min(dist, this.arc1.distance(line));
    }
    return dist;
  }

  /**
   * check if a point is at the tip of a segment offset the point is supposed to
   * belong to the segment offset
   * @param dp
   * @return
   */
  public boolean isExtremPoint(IDirectPosition dp, double angleTolerance,
      double distanceTolerance) {

    // empty offset segment
    if (this.arc0 == null && this.segment == null && this.arc1 == null) {
      return false;
    }

    // point at the beginning of arc0
    if (this.arc0 != null
        && Math.abs(this.arc0.getStartOfArc()
            - Math.atan2(dp.getY() - this.arc0.getCenter().getY(), dp.getX()
                - this.arc0.getCenter().getX())) <= angleTolerance) {
      return true;
    }

    // point at the end of arc1
    if (this.arc1 != null
        && Math.abs(this.arc1.getEndOfArc()
            - Math.atan2(dp.getY() - this.arc1.getCenter().getY(), dp.getX()
                - this.arc1.getCenter().getX())) <= angleTolerance) {
      return true;
    }

    // point at the beginning of the segment
    if (this.arc0 == null && this.segment != null
        && this.segment.getStartPoint().distance(dp) <= distanceTolerance) {
      return true;
    }

    // point at the end of the segment
    if (this.arc1 == null && this.segment != null
        && this.segment.getEndPoint().distance(dp) <= distanceTolerance) {
      return true;
    }

    // point at the end of arc0
    if (this.arc0 != null
        && this.segment == null
        && this.arc1 == null
        && Math.abs(this.arc0.getEndOfArc()
            - Math.atan2(dp.getY() - this.arc0.getCenter().getY(), dp.getX()
                - this.arc0.getCenter().getX())) <= angleTolerance) {
      return true;
    }

    // point at the beginning of arc1
    if (this.arc0 == null
        && this.segment == null
        && this.arc1 != null
        && Math.abs(this.arc1.getStartOfArc()
            - Math.atan2(dp.getY() - this.arc1.getCenter().getY(), dp.getX()
                - this.arc1.getCenter().getX())) <= angleTolerance) {
      return true;
    }

    return false;
  }
}
