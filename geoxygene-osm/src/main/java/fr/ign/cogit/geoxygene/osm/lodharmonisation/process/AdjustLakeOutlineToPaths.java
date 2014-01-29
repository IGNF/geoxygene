package fr.ign.cogit.geoxygene.osm.lodharmonisation.process;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.osm.lodanalysis.relations.LoDSpatialRelation;
import fr.ign.cogit.geoxygene.osm.schema.hydro.OsmWaterArea;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.JTSAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.BufferComputing;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.Side;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

public class AdjustLakeOutlineToPaths {

  private static Logger logger = Logger
      .getLogger(AdjustLakeOutlineToPaths.class.getName());

  private Set<LoDSpatialRelation> inconsistencies;

  private boolean searchBridges = true;
  private double lakeBuffer = 4.0, shoreBuffer = 20.0, cushionRadius = 5.0,
      bridgeTol = 0.9, bridgeAngle = Math.PI / 3;

  public AdjustLakeOutlineToPaths() {
    super();
  }

  public AdjustLakeOutlineToPaths(Set<LoDSpatialRelation> inconsistencies,
      double lakeBuffer, double shoreBuffer, double cushionRadius,
      double bridgeTol, double bridgeAngle) {
    super();
    this.inconsistencies = inconsistencies;
    this.lakeBuffer = lakeBuffer;
    this.shoreBuffer = shoreBuffer;
    this.cushionRadius = cushionRadius;
    this.bridgeTol = bridgeTol;
    this.bridgeAngle = bridgeAngle;
  }

  /**
   * 
   * @return a set of the modified features.
   */
  public Set<IGeneObj> harmonise() {
    Set<IGeneObj> modifiedFeats = new HashSet<IGeneObj>();
    for (LoDSpatialRelation instance : inconsistencies) {
      instance.getFeature2();
      ILineString path = (ILineString) instance.getFeature2().getGeom();
      OsmWaterArea lake = (OsmWaterArea) instance.getFeature1();
      List<Segment> path_segments = Segment.getSegmentList(path);

      Side lakeSide = null;
      Set<Segment> bridges = new HashSet<Segment>();
      if (searchBridges)
        bridges.addAll(findBridges(path_segments, lake.getGeom()));
      IGeometry diffGeom = lake.getGeom();
      for (int i = 0; i < path_segments.size(); i++) {
        if (bridges.contains(path_segments.get(i)))
          continue;
        if (path_segments.get(i).intersects(lake.getGeom())) {
          if (lakeSide == null)
            lakeSide = computeLakeSide(path_segments.get(i), lake.getGeom());
          diffGeom = controlledDifference(diffGeom,
              geomToSubstract(path_segments.get(i), lake.getGeom(), lakeSide));
          if (i == 0) {
            while (i + 1 < path_segments.size()
                && GeometryFactory
                    .buildCircle(path_segments.get(i + 1).getStartPoint(),
                        cushionRadius, 12).intersects(lake.getGeom())) {
              diffGeom = controlledDifference(
                  diffGeom,
                  geomToSubstract(path_segments.get(i + 1), lake.getGeom(),
                      lakeSide));
              i++;
            }
          } else {
            int j = i;
            while (j > 0
                && GeometryFactory.buildCircle(
                    path_segments.get(j - 1).getEndPoint(), cushionRadius, 12)
                    .intersects(lake.getGeom())) {
              if (bridges.contains(path_segments.get(j - 1)))
                break;
              diffGeom = controlledDifference(
                  diffGeom,
                  geomToSubstract(path_segments.get(j - 1), lake.getGeom(),
                      lakeSide));
              j--;
              logger.fine("on passe dans la boucle arrière " + j);
            }
            diffGeom = controlledDifference(
                diffGeom,
                geomToSubstract(path_segments.get(j - 1), lake.getGeom(),
                    lakeSide));
            while (i + 1 < path_segments.size()
                && GeometryFactory
                    .buildCircle(path_segments.get(i + 1).getStartPoint(),
                        cushionRadius, 12).intersects(lake.getGeom())) {
              if (bridges.contains(path_segments.get(i + 1)))
                break;
              diffGeom = controlledDifference(
                  diffGeom,
                  geomToSubstract(path_segments.get(i + 1), lake.getGeom(),
                      lakeSide));
              i++;
              logger.fine("on passe dans la boucle avant " + i);
            }
            if (i + 1 < path_segments.size())
              diffGeom = controlledDifference(
                  diffGeom,
                  geomToSubstract(path_segments.get(i + 1), lake.getGeom(),
                      lakeSide));
          }
        }
      }

      if (diffGeom instanceof IPolygon) {
        lake.setGeom(diffGeom);
        continue;
      } else {
        @SuppressWarnings("unchecked")
        IPolygon simplePol = CommonAlgorithmsFromCartAGen
            .getBiggerFromMultiSurface((IMultiSurface<IOrientableSurface>) diffGeom);
        lake.setGeom(simplePol);
      }
    }
    return modifiedFeats;
  }

  public Set<Segment> findBridges(List<Segment> segList, IPolygon lake) {
    Set<Segment> bridges = new HashSet<Segment>();
    Set<Segment> directBridge = new HashSet<Segment>();
    int i = 0;
    for (Segment segment : segList) {
      // only the contained segment can be bridges
      if (lake.contains(segment)) {
        logger.fine("tested segment: " + segment.toString());
        // first, the nearest shore point to the middle point should not be
        // closer than the nearest shore point to the extremities of the
        // segment. i.e. we measure that the bridge does not follow the lake
        // shore but goes inside the lake.
        IDirectPosition middlePt = segment.getMiddlePoint();
        IDirectPosition nearest = CommonAlgorithms.getNearestPoint(lake
            .getExterior().getPrimitive(), middlePt.toGM_Point());
        double distance = middlePt.distance2D(nearest);
        IDirectPosition nearestStart = CommonAlgorithms.getNearestPoint(lake
            .getExterior().getPrimitive(), segment.startPoint().toGM_Point());
        double distStart = middlePt.distance2D(nearestStart);
        IDirectPosition nearestEnd = CommonAlgorithms.getNearestPoint(lake
            .getExterior().getPrimitive(), segment.endPoint().toGM_Point());
        double distEnd = middlePt.distance2D(nearestEnd);
        if (distance < bridgeTol * Math.min(distStart, distEnd)) {
          i++;
          logger.fine("the distance test is not passed: " + distance + " > "
              + Math.min(distStart, distEnd));
          continue;
        }
        logger.fine("pass the distance test: " + distance + " < "
            + Math.min(distStart, distEnd));

        // arrived here, the first test has been succesfully passed
        // the second test is about the orientation of the segment that should
        // be orthogonal to at least one extremity lake shore
        double orientation = convertAngle(segment.orientation());
        ILineSegment nearestSegStart = CommonAlgorithmsFromCartAGen
            .getNearestSegmentFromPoint(lake, segment.startPoint());
        double orientStart = convertAngle(new Segment(
            nearestSegStart.startPoint(), nearestSegStart.endPoint())
            .orientation());
        ILineSegment nearestSegEnd = CommonAlgorithmsFromCartAGen
            .getNearestSegmentFromPoint(lake, segment.endPoint());
        double orientEnd = convertAngle(new Segment(nearestSegEnd.startPoint(),
            nearestSegEnd.endPoint()).orientation());
        if ((Math.abs(orientation - orientStart) < bridgeAngle)
            && (Math.abs(orientation - orientEnd) < bridgeAngle)) {
          i++;
          logger.fine("the orientation test is not passed: " + orientation
              + " / " + orientStart + " & " + orientEnd);
          continue;
        }
        logger.fine("pass the orientation test: " + orientation + " / "
            + orientStart + " & " + orientEnd);

        // arrived here, the segment is a bridge. Add i-1 and i+1 if they are
        // mainly inside the lake.
        bridges.add(segment);
        directBridge.add(segment);
        if (i != 0)
          bridges.add(segList.get(i - 1));
        if (i < segList.size() - 1)
          bridges.add(segList.get(i + 1));
      }
      i++;
    }

    // make a second loop to identify segments connected to bridges that failed
    // the angle test but that are not parallel to the shore.

    for (int j = 0; j < segList.size(); j++) {
      Segment segment = segList.get(j);
      if (directBridge.contains(segment))
        continue;
      if (!bridges.contains(segment))
        continue;
      // make the segment pass the angle test again with a less strict threshold
      // arrived here, the first test has been succesfully passed
      // the second test is about the orientation of the segment that should
      // be orthogonal to at least one extremity lake shore
      double orientation = convertAngle(segment.orientation());
      ILineSegment nearestSegStart = CommonAlgorithmsFromCartAGen
          .getNearestSegmentFromPoint(lake, segment.startPoint());
      double orientStart = convertAngle(new Segment(
          nearestSegStart.startPoint(), nearestSegStart.endPoint())
          .orientation());
      ILineSegment nearestSegEnd = CommonAlgorithmsFromCartAGen
          .getNearestSegmentFromPoint(lake, segment.endPoint());
      double orientEnd = convertAngle(new Segment(nearestSegEnd.startPoint(),
          nearestSegEnd.endPoint()).orientation());
      if ((Math.abs(orientation - orientStart) < bridgeAngle / 2)
          && (Math.abs(orientation - orientEnd) < bridgeAngle / 2)) {
        logger.fine("the orientation test is not passed in the second loop: "
            + orientation + " / " + orientStart + " & " + orientEnd);
        continue;
      }
      logger.fine("pass the orientation test in the second loop: "
          + orientation + " / " + orientStart + " & " + orientEnd);
      if (j != 0)
        bridges.add(segList.get(j - 1));
      if (j < segList.size() - 1)
        bridges.add(segList.get(j + 1));
    }
    return bridges;
  }

  // put the orientation between 0 & Pi
  private double convertAngle(double angle) {
    if (angle > Math.PI)
      angle = angle - Math.PI;
    return angle;
  }

  private IPolygon geomToSubstract(Segment segment, IPolygon lake, Side lakeSide) {
    if (lake.intersects(segment)) {
      // First, compute the buffer on the lake side
      IPolygon lakePolygon = BufferComputing.buildLineHalfBuffer(segment,
          lakeBuffer, lakeSide);
      // then, compute the buffer on the shore side
      IPolygon shorePolygon = BufferComputing.buildHalfOffsetBuffer(
          lakeSide.inverse(), segment, shoreBuffer);
      // union in two steps using a temporary geometry because the union of
      // adjacent polygons sometimes return a multipolygon.
      IPolygon temp = (IPolygon) segment.buffer(1.0);
      IPolygon half = safeUnion(temp, lakePolygon);
      IPolygon geomToSubstract = safeUnion(half, shorePolygon);
      return geomToSubstract;
    } else {
      return (IPolygon) segment.buffer(lakeBuffer);
    }
  }

  private Side computeLakeSide(Segment segment, IPolygon lake) {
    ILineString extLine = lake.exteriorLineString();
    if (JTSAlgorithms.isClockwise(extLine))
      extLine = extLine.reverse();
    // first extend the segment to touch the polygon
    // extend startPt first
    IDirectPosition startPt = CommonAlgorithmsFromCartAGen.projection(
        segment.startPoint(), extLine,
        new Vector2D(segment.endPoint(), segment.startPoint()));
    boolean startOutside = false;
    if (startPt == null) {
      // it means that segment.startPt() is outside the lake
      // then, the intersection is taken instead
      startOutside = true;
      startPt = CommonAlgorithmsFromCartAGen.getCommonVertexBetween2Lines(
          segment, extLine);
    }
    // extend endPt first
    IDirectPosition endPt = CommonAlgorithmsFromCartAGen.projection(
        segment.endPoint(), extLine,
        new Vector2D(segment.startPoint(), segment.endPoint()));
    boolean endOutside = false;
    if (endPt == null) {
      // it means that segment.startPt() is outside the lake
      // then, the intersection is taken instead
      endOutside = true;
      endPt = CommonAlgorithmsFromCartAGen.getCommonVertexBetween2Lines(
          segment, extLine);
    }

    // then, compute two polygons, one on the right, one on the left
    ILineString subLineStart = CommonAlgorithmsFromCartAGen.getSubLine(extLine,
        startPt, endPt);
    IDirectPositionList rightPolList = new DirectPositionList();
    rightPolList.add(startPt);
    rightPolList.addAll(subLineStart.coord());
    rightPolList.add(endPt);
    if (!endOutside)
      rightPolList.add(segment.endPoint());
    if (!startOutside)
      rightPolList.add(segment.startPoint());
    rightPolList.add(startPt);
    IPolygon rightPol = new GM_Polygon(new GM_LineString(rightPolList));
    ILineString subLineEnd = CommonAlgorithmsFromCartAGen.getSubLine(extLine,
        endPt, startPt);
    IDirectPositionList leftPolList = new DirectPositionList();
    leftPolList.add(endPt);
    leftPolList.addAll(subLineEnd.coord());
    leftPolList.add(startPt);
    if (!startOutside)
      leftPolList.add(segment.startPoint());
    if (!endOutside)
      leftPolList.add(segment.endPoint());
    leftPolList.add(endPt);
    IPolygon leftPol = new GM_Polygon(new GM_LineString(leftPolList));

    // then, select the bigger polygon
    if (rightPol.area() > leftPol.area())
      return Side.RIGHT;
    return Side.LEFT;
  }

  private IGeometry controlledDifference(IGeometry geom1, IGeometry geom2) {
    IGeometry diff = null;
    try {
      diff = geom1.difference(geom2);
    } catch (Exception e) {
      diff = geom1.difference(geom2.buffer(0.1));
    }
    if (diff == null) {
      diff = geom1.difference(geom2.buffer(0.1));
      if (diff == null) {
        diff = geom1.buffer(0.1).difference(geom2.buffer(0.1));
      }
    }
    return diff;
  }

  @SuppressWarnings("unchecked")
  private IPolygon safeUnion(IPolygon pol1, IPolygon pol2) {
    IGeometry union = pol1.union(pol2);
    if (union == null) {
      union = pol1.buffer(0.1).union(pol2.buffer(0.1));
    }
    if (union instanceof IMultiSurface) {
      union = CommonAlgorithmsFromCartAGen
          .getBiggerFromMultiSurface((IMultiSurface<IOrientableSurface>) union);
    }
    return (IPolygon) union;
  }

  public boolean isSearchBridges() {
    return searchBridges;
  }

  public void setSearchBridges(boolean searchBridges) {
    this.searchBridges = searchBridges;
  }
}
