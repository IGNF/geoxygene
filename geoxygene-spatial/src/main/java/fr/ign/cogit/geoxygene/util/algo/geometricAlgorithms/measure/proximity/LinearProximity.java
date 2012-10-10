package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class LinearProximity extends GeometryProximity {

  public LinearProximity(ILineString geom1, ILineString geom2) {
    super(geom1, geom2);
  }

  /**
   * Get the minimum distance segment between the two lines but away from the
   * intersection between the lines. Useful to detect real overlapping problems
   * between network elements.
   * @return
   */
  public ILineSegment getMinDistAwayIntersection() {
    if (!getGeom1().intersects(getGeom2()))
      return toSegment();
    IDirectPosition interPt = getGeom1().intersection(getGeom2()).coord()
        .get(0);
    // get the position list of geom1
    IDirectPositionList pointList1 = getGeom1().coord();
    int index1 = -1;
    if (pointList1.contains(interPt)) {
      // get the index of the intersection point
      index1 = pointList1.getList().indexOf(interPt);
    } else {
      // the intersection is not a vertex, find the nearest one
      IDirectPosition nearest = CommonAlgorithmsFromCartAGen
          .getNearestVertexFromPoint(getGeom1(), interPt);
      // get the index of the nearest point
      index1 = pointList1.getList().indexOf(nearest);
    }
    // remove the intersection point and the two vertices before and after
    List<ILineString> listGeom1 = new ArrayList<ILineString>();
    if (index1 > 3) {
      IDirectPositionList pts = new DirectPositionList();
      for (int i = 0; i < index1 - 3; i++)
        pts.add(pointList1.get(i));
      if (pts.size() > 1)
        listGeom1.add(new GM_LineString(pts));
    }
    if (index1 < pointList1.size() - 4) {
      IDirectPositionList pts = new DirectPositionList();
      for (int i = index1 + 4; i < pointList1.size(); i++)
        pts.add(pointList1.get(i));
      if (pts.size() > 1)
        listGeom1.add(new GM_LineString(pts));
    }

    // get the position of the intersection on geom2
    IDirectPositionList pointList2 = getGeom1().coord();
    int index2 = -1;
    if (pointList2.contains(interPt)) {
      // get the index of the intersection point
      index2 = pointList2.getList().indexOf(interPt);
    } else {
      // the intersection is not a vertex, find the nearest one
      IDirectPosition nearest = CommonAlgorithmsFromCartAGen
          .getNearestVertexFromPoint(getGeom1(), interPt);
      // get the index of the nearest point
      index2 = pointList2.getList().indexOf(nearest);
    }
    // remove the intersection point and the two vertices before and after
    List<ILineString> listGeom2 = new ArrayList<ILineString>();
    if (index2 > 3) {
      IDirectPositionList pts = new DirectPositionList();
      for (int i = 0; i < index2 - 3; i++)
        pts.add(pointList2.get(i));
      if (pts.size() > 1)
        listGeom2.add(new GM_LineString(pts));
    }
    if (index2 < pointList2.size() - 4) {
      IDirectPositionList pts = new DirectPositionList();
      for (int i = index2 + 4; i < pointList2.size(); i++)
        pts.add(pointList2.get(i));
      if (pts.size() > 1)
        listGeom2.add(new GM_LineString(pts));
    }

    // now compute the classical proximity between the multiple geometries
    ILineSegment minSeg = null;
    double minDist = Double.MAX_VALUE;
    for (ILineString line1 : listGeom1) {
      for (ILineString line2 : listGeom2) {
        GeometryProximity proxi = new GeometryProximity(line1, line2);
        if (proxi.getDistance() < minDist) {
          minDist = proxi.getDistance();
          minSeg = proxi.toSegment();
        }
      }
    }
    return minSeg;
  }
}
