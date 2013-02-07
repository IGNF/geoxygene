/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 * @copyright twak
 ******************************************************************************/
package fr.ign.cogit.cartagen.genealgorithms.polygon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import straightskeleton.Corner;
import straightskeleton.Edge;
import straightskeleton.Machine;
import straightskeleton.Output.SharedEdge;
import straightskeleton.Skeleton;
import utils.Loop;
import utils.LoopL;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

/**
 * Extract the skeleton of polygons by different methods
 * @author Guillaume
 * 
 */
public class Skeletonize {

  class MultiSkeleton {
    private Set<ILineSegment> segments = new HashSet<ILineSegment>();
    private List<ILineSegment> segFace = new ArrayList<ILineSegment>();
    private Set<IDirectPosition> isolatedPts = new HashSet<IDirectPosition>();

    public Set<ILineSegment> getSegments() {
      return segments;
    }

    public void setSegments(Set<ILineSegment> segments) {
      this.segments = segments;
    }

    public List<ILineSegment> getSegFace() {
      return segFace;
    }

    public void setSegFace(List<ILineSegment> segFace) {
      this.segFace = segFace;
    }

    public Set<IDirectPosition> getIsolatedPts() {
      return isolatedPts;
    }

    public void setIsolatedPts(Set<IDirectPosition> isolatedPts) {
      this.isolatedPts = isolatedPts;
    }

  }

  /**
   * Raw Straight Skeleton algorithm using the campskeleton project
   * implementation. Returns all the skeleton segments that do not touch the
   * polygon outline. Be careful, the computation time may be extremely long
   * with very large polygons.
   * @param polygon
   * @return
   */
  public static Set<ILineSegment> skeletonizeStraightSkeleton(IPolygon polygon) {
    Set<ILineSegment> skeletonSegments = new HashSet<ILineSegment>();
    Machine directionMachine = new Machine();

    // when the geometry is too big, it needs to be simplified first
    IPolygon geom = polygon;
    if (polygon.numPoints() > 500) {
      geom = (IPolygon) Filtering.DouglasPeucker(polygon, 10.0);
    }

    LoopL<Edge> loopL = new LoopL<Edge>();
    Loop<Edge> loop = new Loop<Edge>();
    Corner cFirst = null, c1, c2 = null;
    boolean first = true;
    int i = 0;
    List<Segment> segmentList = Segment.getReverseSegmentList(geom, geom
        .coord().get(0));
    for (ILineSegment seg : segmentList) {
      if (first) {
        c1 = new Corner(seg.getStartPoint().getX(), seg.getStartPoint().getY());
        cFirst = c1;
        first = false;
      } else
        c1 = c2;
      if (i < segmentList.size() - 1)
        c2 = new Corner(seg.getEndPoint().getX(), seg.getEndPoint().getY());
      else
        c2 = cFirst;
      Edge edge = new Edge(c1, c2);
      edge.machine = directionMachine;
      loop.append(edge);
      i++;
    }
    loopL.add(loop);
    Skeleton ske = new Skeleton(loopL, true);
    ske.skeleton();

    for (SharedEdge edge : ske.output.edges.map.keySet()) {
      ILineSegment segment = new GM_LineSegment(
          new DirectPosition(edge.getStart(edge.left).x,
              edge.getStart(edge.left).y),
          new DirectPosition(edge.getEnd(edge.left).x, edge.getEnd(edge.left).y));
      if (segment.intersects(polygon.getExterior().getPrimitive()))
        continue;
      if (polygon.disjoint(segment))
        continue;
      skeletonSegments.add(segment);
    }

    return skeletonSegments;
  }

  /**
   * Connect a skeleton computed by any method to the nearest edges of the
   * skeletonized polygon.
   * @param skeleton
   * @param polygon
   */
  public static Set<ILineString> connectSkeletonToPolygon(
      Set<ILineSegment> skeleton, IPolygon polygon) {
    Set<ILineString> extendedSkeleton = new HashSet<ILineString>();
    for (ILineSegment skeSeg : skeleton) {
      ILineString line = new GM_LineString(skeSeg.coord());
      // first check if the start node has to be extended
      boolean extend = true;
      for (ILineSegment other : skeleton) {
        if (other.equals(skeSeg))
          continue;
        if (other.coord().contains(skeSeg.coord().get(0))) {
          extend = false;
          break;
        }
      }
      // extend it if necessary
      if (extend) {
        Vector2D vect = new Vector2D(skeSeg.coord().get(1), skeSeg.coord().get(
            0));
        IDirectPosition firstPt = CommonAlgorithmsFromCartAGen.projection(
            skeSeg.coord().get(0), polygon.exteriorLineString(), vect);
        line.addControlPoint(0, firstPt);
      }

      extend = true;
      for (ILineSegment other : skeleton) {
        if (other.equals(skeSeg))
          continue;
        if (other.coord().contains(skeSeg.coord().get(1))) {
          extend = false;
          break;
        }
      }
      // then, extend the last node
      if (extend) {
        Vector2D vect = new Vector2D(skeSeg.coord().get(0), skeSeg.coord().get(
            1));
        IDirectPosition lastPt = CommonAlgorithmsFromCartAGen.projection(skeSeg
            .coord().get(1), polygon.exteriorLineString(), vect);
        line.addControlPoint(lastPt);
      }
      extendedSkeleton.add(line);
    }
    return extendedSkeleton;
  }
}
