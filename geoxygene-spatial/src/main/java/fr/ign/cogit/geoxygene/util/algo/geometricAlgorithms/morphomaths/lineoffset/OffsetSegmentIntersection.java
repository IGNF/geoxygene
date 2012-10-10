/**
 * 21 juil. 2009
 */
package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.lineoffset;

import java.util.ArrayList;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;

/**
 * @author jgaffuri 21 juil. 2009
 * 
 */
public class OffsetSegmentIntersection {

  /**
	 */
  public ArrayList<OffsetSegment> offsetSegments;
  /**
	 */
  public IDirectPosition pos;

  public OffsetSegmentIntersection(OffsetSegment offSeg1,
      OffsetSegment offSeg2, IDirectPosition pos) {
    this.offsetSegments = new ArrayList<OffsetSegment>();
    this.offsetSegments.add(offSeg1);
    this.offsetSegments.add(offSeg2);
    this.pos = pos;
  }

}
