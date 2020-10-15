package fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

public class BufferComputing {

  private static Logger logger = LogManager.getLogger(BufferComputing.class
      .getName());

  /**
   * Builds the half-buffer of a line, i.e. cuts in two the buffer in the middle
   * of the rounded part of the buffer. Be careful, the twisted cases of the
   * corresponding Lull function are not taken into account !
   * 
   * @param line
   * @param distance
   * @param vectorLength
   * @param side
   * @return
   * @author GTouya
   */
  public static IPolygon buildLineHalfBuffer(ILineString line, double distance,
      Side side) {

    if (line.coord().size() < 3) {
      // the line doesn't have enough points to compute a half buffer
      ILineSegment segment = new GM_LineSegment(line.coord());
      return buildSegmentHalfBuffer(segment, distance, side);
    }

    // initialise the twisted cases markers
    boolean leftTwisted = false;
    boolean rightTwisted = false;

    // first build the buffer of the line and get its outline
    IPolygon buffer = (IPolygon) line.buffer(distance);
    IRing outerRing = buffer.getExterior();

    // 1. Lengthen the line onto the buffer at the start of the line
    // first get the vector composed by the first two points of the line
    Vector2D startVector = new Vector2D(line.coord().get(1), line.coord()
        .get(0));
    // then project the starting point on the buffer according to this vector
    IDirectPosition buffPtStart = CommonAlgorithmsFromCartAGen.projection(line
        .coord().get(0), new GM_LineString(outerRing.getPositive().coord()),
        startVector);
    // test if twisted (if distance is much bigger than buffer distance)
    if (line.coord().get(0).distance(buffPtStart) > distance * 1.2) {
      leftTwisted = true;
      // buffPtStart has to be changed
      HashMap<IDirectPosition, Integer> endingPts = getEndingPtsOfRoundPart(
          line.coord().get(0), outerRing, distance);
      if (endingPts.size() < 2) {
        logger
            .info("problem during buffer computation, null geometry returned!!");
        return null;
      }
      // between the two points of the round part, select the one with the
      // smallest angle
      Iterator<IDirectPosition> iter = endingPts.keySet().iterator();
      IDirectPosition firstPt = iter.next();
      IDirectPosition secondPt = iter.next();
      double angle1 = Angle.angleTroisPoints(buffPtStart, line.coord().get(0),
          firstPt).getValeur();
      double angle2 = Angle.angleTroisPoints(buffPtStart, line.coord().get(0),
          secondPt).getValeur();
      if (Math.abs(angle1) < Math.abs(angle2))
        buffPtStart = firstPt;
      else
        buffPtStart = secondPt;
    }

    // 2. Lengthen the line onto the buffer at the end of the line
    // first get the vector composed by the last two points of the line
    int nbPts = line.coord().size();
    Vector2D endVector = new Vector2D(line.coord().get(nbPts - 2), line.coord()
        .get(nbPts - 1));
    // then project the starting point on the buffer according to this vector
    IDirectPosition buffPtEnd = CommonAlgorithmsFromCartAGen.projection(line
        .coord().get(nbPts - 1), new GM_LineString(outerRing.getPositive()
        .coord()), endVector);
    if (line.coord().get(nbPts - 1).distance(buffPtEnd) > distance * 1.2) {
      rightTwisted = true;
      // buffPtStart has to be changed
      HashMap<IDirectPosition, Integer> endingPts = getEndingPtsOfRoundPart(
          line.coord().get(nbPts - 1), outerRing, distance);
      if (endingPts.size() < 2) {
        logger
            .info("problem during buffer computation, null geometry returned!!");
        return null;
      }
      // between the two points of the round part, select the one with the
      // smallest angle
      Iterator<IDirectPosition> iter = endingPts.keySet().iterator();
      IDirectPosition firstPt = iter.next();
      IDirectPosition secondPt = iter.next();
      double angle1 = Angle.angleTroisPoints(buffPtEnd,
          line.coord().get(nbPts - 1), firstPt).getValeur();
      double angle2 = Angle.angleTroisPoints(buffPtEnd,
          line.coord().get(nbPts - 1), secondPt).getValeur();
      if (Math.abs(angle1) < Math.abs(angle2))
        buffPtEnd = firstPt;
      else
        buffPtEnd = secondPt;
    }

    // 3. Build the half-buffer according to the side
    IDirectPositionList halfBuffer = new DirectPositionList();
    if (side.equals(Side.LEFT)) {
      if (leftTwisted) {
        // TODO
      }
      // look for the indexes of the two buffer points
      // first inserts the two points in the line
      IDirectPositionList leftOutline = outerRing.getPositive().coord();
      int idStart = CommonAlgorithmsFromCartAGen
          .getNearestVertexPositionBeforePoint(outerRing.getPositive(),
              buffPtStart);
      leftOutline.add(idStart + 1, buffPtStart);
      int idEnd = CommonAlgorithmsFromCartAGen
          .getNearestVertexPositionBeforePoint(outerRing.getPositive(),
              buffPtEnd);
      leftOutline.add(idEnd + 1, buffPtEnd);
      for (int i = 0; i < leftOutline.size(); i++) {
        if (leftOutline.get(i).equals2D(buffPtStart))
          idStart = i;
        if (leftOutline.get(i).equals2D(buffPtEnd))
          idEnd = i;
      }
      // add the points of leftOutline between idStart and idEnd to halfBuffer
      if (idStart < idEnd)
        for (int i = idStart; i <= idEnd; i++)
          halfBuffer.add(leftOutline.get(i));
      else {
        for (int i = idStart; i < leftOutline.size() - 1; i++)
          halfBuffer.add(leftOutline.get(i));
        for (int i = 1; i <= idEnd; i++)
          halfBuffer.add(leftOutline.get(i));
      }
      // then add the points of line in reverse
      for (int i = 0; i < line.coord().size() - 1; i++)
        halfBuffer.add(line.coord().get(line.coord().size() - 1 - i));

      // then finally close the half buffer
      halfBuffer.add(buffPtStart);
    } else {
      if (rightTwisted) {
        // TODO
      }
      // look for the indexes of the two buffer points
      // first inserts the two points in the line
      IDirectPositionList rightOutline = outerRing.getPositive().coord();
      int idStart = CommonAlgorithmsFromCartAGen
          .getNearestVertexPositionBeforePoint(outerRing.getPositive(),
              buffPtStart);
      rightOutline.add(idStart + 1, buffPtStart);
      int idEnd = CommonAlgorithmsFromCartAGen
          .getNearestVertexPositionBeforePoint(outerRing.getPositive(),
              buffPtEnd);
      rightOutline.add(idEnd + 1, buffPtEnd);
      for (int i = 0; i < rightOutline.size(); i++) {
        if (rightOutline.get(i).equals2D(buffPtStart))
          idStart = i;
        if (rightOutline.get(i).equals2D(buffPtEnd))
          idEnd = i;
      }
      // add the points of rightOutline between idEnd and idStart to halfBuffer
      if (idEnd < idStart)
        for (int i = idEnd; i <= idStart; i++)
          halfBuffer.add(rightOutline.get(i));
      else {
        for (int i = idEnd; i < rightOutline.size() - 1; i++)
          halfBuffer.add(rightOutline.get(i));
        for (int i = 1; i <= idStart; i++)
          halfBuffer.add(rightOutline.get(i));
      }

      // then add the points of line
      for (int i = 0; i < line.coord().size() - 1; i++)
        halfBuffer.add(line.coord().get(i));
      // then finally close the half buffer
      halfBuffer.add(buffPtEnd);
    }

    // 4. Build the buffer from the point list
    return new GM_Polygon(new GM_LineString(halfBuffer));
  }

  public static IPolygon buildSegmentHalfBuffer(ILineSegment line,
      double distance, Side side) {

    // first build the buffer of the line and get its outline
    IPolygon buffer = (IPolygon) line.buffer(distance);
    IRing outerRing = buffer.getExterior();

    // 1. Lengthen the line onto the buffer at the start of the line
    // first get the vector composed by the first two points of the line
    Vector2D startVector = new Vector2D(line.getEndPoint(),
        line.getStartPoint());
    // then project the starting point on the buffer according to this vector
    IDirectPosition buffPtStart = CommonAlgorithmsFromCartAGen.projection(
        line.getStartPoint(),
        new GM_LineString(outerRing.getPositive().coord()), startVector);

    // 2. Lengthen the line onto the buffer at the end of the line
    // first get the vector composed by the last two points of the line
    Vector2D endVector = new Vector2D(line.getStartPoint(), line.getEndPoint());
    // then project the starting point on the buffer according to this vector
    IDirectPosition buffPtEnd = CommonAlgorithmsFromCartAGen.projection(
        line.getEndPoint(), new GM_LineString(outerRing.getPositive().coord()),
        endVector);

    // 3. Build the half-buffer according to the side
    IDirectPositionList halfBuffer = new DirectPositionList();
    if (side.equals(Side.LEFT)) {
      // look for the indexes of the two buffer points
      // first inserts the two points in the line
      IDirectPositionList leftOutline = outerRing.getPositive().coord();
      int idStart = CommonAlgorithmsFromCartAGen
          .getNearestVertexPositionBeforePoint(outerRing.getPositive(),
              buffPtStart);
      leftOutline.add(idStart + 1, buffPtStart);
      int idEnd = CommonAlgorithmsFromCartAGen
          .getNearestVertexPositionBeforePoint(outerRing.getPositive(),
              buffPtEnd);
      leftOutline.add(idEnd + 1, buffPtEnd);
      for (int i = 0; i < leftOutline.size(); i++) {
        if (leftOutline.get(i).equals2D(buffPtStart))
          idStart = i;
        if (leftOutline.get(i).equals2D(buffPtEnd))
          idEnd = i;
      }
      // add the points of leftOutline between idStart and idEnd to halfBuffer
      if (idStart < idEnd)
        for (int i = idStart; i <= idEnd; i++)
          halfBuffer.add(leftOutline.get(i));
      else {
        for (int i = idStart; i < leftOutline.size() - 1; i++)
          halfBuffer.add(leftOutline.get(i));
        for (int i = 1; i <= idEnd; i++)
          halfBuffer.add(leftOutline.get(i));
      }
      // then add the end point of the segment
      halfBuffer.add(line.getEndPoint());

      // then finally close the half buffer
      halfBuffer.add(buffPtStart);
    } else {
      // look for the indexes of the two buffer points
      // first inserts the two points in the line
      IDirectPositionList rightOutline = outerRing.getPositive().coord();
      int idStart = CommonAlgorithmsFromCartAGen
          .getNearestVertexPositionBeforePoint(outerRing.getPositive(),
              buffPtStart);
      rightOutline.add(idStart + 1, buffPtStart);
      int idEnd = CommonAlgorithmsFromCartAGen
          .getNearestVertexPositionBeforePoint(outerRing.getPositive(),
              buffPtEnd);
      rightOutline.add(idEnd + 1, buffPtEnd);
      for (int i = 0; i < rightOutline.size(); i++) {
        if (rightOutline.get(i).equals2D(buffPtStart))
          idStart = i;
        if (rightOutline.get(i).equals2D(buffPtEnd))
          idEnd = i;
      }
      // add the points of rightOutline between idEnd and idStart to halfBuffer
      if (idEnd < idStart)
        for (int i = idEnd; i <= idStart; i++)
          halfBuffer.add(rightOutline.get(i));
      else {
        for (int i = idEnd; i < rightOutline.size() - 1; i++)
          halfBuffer.add(rightOutline.get(i));
        for (int i = 1; i <= idStart; i++)
          halfBuffer.add(rightOutline.get(i));
      }

      // then add the start point of the segment
      halfBuffer.add(line.getStartPoint());
      // then finally close the half buffer
      halfBuffer.add(buffPtEnd);
    }

    // 4. Build the buffer from the point list
    return new GM_Polygon(new GM_LineString(halfBuffer));
  }

  /**
   * This method allows to get the starting and ending points of the round part
   * of a buffer around a line, on one side of the line (represented by the
   * ending point of the line). This method is useful to build a real offset
   * buffer of a line that works with twisted lines.
   * 
   * @param point
   * @param buffer
   * @param distance
   * @return
   * @author GTouya
   */
  public static HashMap<IDirectPosition, Integer> getEndingPtsOfRoundPart(
      IDirectPosition point, IRing buffer, double distance) {
    HashMap<IDirectPosition, Integer> map = new HashMap<IDirectPosition, Integer>();
    // first get a random point on the buffer
    int randomId = CommonAlgorithmsFromCartAGen
        .getNearestVertexPositionFromPoint(buffer, point);

    // first go forward
    IDirectPositionList forwardList = new DirectPositionList();
    forwardList.addAll(buffer.coord());
    forwardList.addAll(buffer.coord());
    int forwardId = 0;
    for (int i = randomId; i < buffer.coord().size() + randomId; i++) {
      IDirectPosition pt = forwardList.get(i);
      double dist = pt.distance(point);
      if (dist > 1.1 * distance) {
        forwardId = i - 1;
        break;
      }
    }
    if (forwardId >= buffer.coord().size())
      forwardId = forwardId - buffer.coord().size();
    if (forwardId < 0)
      forwardId = buffer.coord().size() - 1;
    map.put(buffer.coord().get(forwardId), forwardId);

    // then go backward
    IDirectPositionList backwardList = new DirectPositionList();
    backwardList.addAll(buffer.coord());
    backwardList.addAll(buffer.coord());
    int backwardId = 0;
    for (int i = buffer.coord().size() + randomId; i > randomId; i--) {
      IDirectPosition pt = backwardList.get(i);
      double dist = pt.distance(point);
      if (dist > 1.1 * distance) {
        backwardId = i + 1;
        break;
      }
    }
    if (backwardId >= buffer.coord().size())
      backwardId = backwardId - buffer.coord().size();
    map.put(buffer.coord().get(backwardId), backwardId);

    return map;
  }

  /**
   * Get the first point of the rounded part of a buffer on one side and for one
   * of the endings of the line buffered. The method is useful to build a half
   * offset buffer of a line.
   * 
   * @param side LEFT or RIGHT
   * @param start true if the search is around initial point of the line
   * @param point the initial or final point of the line
   * @param buffer the buffer of the line
   * @param distance the distance of the buffer
   * @return
   * @author GTouya
   */
  public static int getSideEndingPtsOfRoundPart(Side side, boolean start,
      IDirectPosition point, IRing buffer, double distance) {
    HashMap<IDirectPosition, Integer> endingPts = getEndingPtsOfRoundPart(
        point, buffer, distance);
    // between the two points of the round part, select the one with the
    // smallest angle
    Iterator<IDirectPosition> iter = endingPts.keySet().iterator();
    if (endingPts.size() < 2)
      return endingPts.get(iter.next());
    IDirectPosition firstPt = iter.next();
    IDirectPosition secondPt = iter.next();
    IDirectPositionList ptsList = buffer.getPositive().coord();
    if ((side.equals(Side.LEFT) && !start)
        || (side.equals(Side.RIGHT) && start))
      ptsList = ptsList.reverse();
    for (int i = 0; i < ptsList.size(); i++) {
      if (ptsList.get(i).equals2D(firstPt)) {
        IDirectPosition next = null;
        if (i < ptsList.size() - 1)
          next = ptsList.get(i + 1);
        else
          next = ptsList.get(1);
        if (next.distance(point) > 1.1 * distance)
          return endingPts.get(firstPt);
        return endingPts.get(secondPt);
      }
    }
    return 0;
  }

  /**
   * Builds a half offset buffer of a linestring. An offset buffer is like a
   * buffer but without the cap (the round part at the end of the line).
   * 
   * @param side the side (related to line's initial point) on which the half
   *          buffer is built
   * @param line the line to be buffered
   * @param distance the buffer distance
   * @return
   * @author GTouya
   */
  public static IPolygon buildHalfOffsetBuffer(Side side, ILineString line,
      double distance) {
    IDirectPosition startPt = line.coord().get(0);
    IDirectPosition endPt = line.coord().get(line.coord().size() - 1);
    IPolygon buffer = (IPolygon) line.buffer(distance);
    if (buffer == null)
      return null;
    IDirectPositionList coordList = new DirectPositionList();
    // get the start vertex index
    int startId = getSideEndingPtsOfRoundPart(side, true, startPt,
        buffer.getExterior(), distance);
    // get the end vertex index
    int endId = getSideEndingPtsOfRoundPart(side, false, endPt,
        buffer.getExterior(), distance);
    // now build the half offset buffer
    // begin with the buffer part
    IDirectPositionList outline = buffer.getExterior().coord();
    if (side.equals(Side.LEFT)) {
      if (startId < endId)
        for (int i = startId; i <= endId; i++)
          coordList.add(outline.get(i));
      else {
        for (int i = startId; i < outline.size(); i++)
          coordList.add(outline.get(i));
        for (int i = 1; i <= endId; i++)
          coordList.add(outline.get(i));
      }
    } else {
      if (endId < startId)
        for (int i = startId; i >= endId; i--)
          coordList.add(outline.get(i));
      else {
        for (int i = startId; i >= 0; i--)
          coordList.add(outline.get(i));
        for (int i = outline.size() - 1; i >= endId; i--)
          coordList.add(outline.get(i));
      }
    }
    // then add the line's points in reverse
    for (int i = 0; i < line.coord().size(); i++)
      coordList.add(line.coord().get(line.coord().size() - 1 - i));

    // then finally close the half buffer
    coordList.add(outline.get(startId));

    return new GM_Polygon(new GM_LineString(coordList));
  }

  /**
   * Builds a half offset line of a linestring. Be careful, this method return
   * an empty linestring for the interior side of closed line.
   * 
   * @param side the side (related to line's initial point) on which the half
   *          buffer is built
   * @param line the line to be buffered
   * @param distance the buffer distance
   * @return
   * @author GTouya
   */
  public static ILineString buildHalfOffsetLine(Side side, ILineString line,
      double distance) {
    IPolygon offsetBuffer = buildHalfOffsetBuffer(side, line, distance);
    IDirectPositionList pts = offsetBuffer.coord();

    for (IDirectPosition pt : line.coord())
      pts.remove(pt);
    // then remove the last point of the polygon
    pts.remove(pts.size() - 1);

    return new GM_LineString(pts);

  }
}
