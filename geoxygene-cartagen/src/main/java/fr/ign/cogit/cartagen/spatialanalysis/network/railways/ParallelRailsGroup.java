package fr.ign.cogit.cartagen.spatialanalysis.network.railways;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokesNetwork;
import fr.ign.cogit.cartagen.util.comparators.DistanceFeatureComparator;
import fr.ign.cogit.cartagen.util.comparators.StrokeLengthComparator;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.BufferComputing;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.Side;

/**
 * A model to group parallel tracks of railways, in order to collapse them
 * properly. The model is described in (Savino & Touya 2015).
 * @author GTouya
 * 
 */
public class ParallelRailsGroup {

  private Stroke centreStroke;
  private Set<ParallelStroke> parallelStrokes;

  public Stroke getCentreStroke() {
    return centreStroke;
  }

  public void setCentreStroke(Stroke centreStroke) {
    this.centreStroke = centreStroke;
  }

  public Set<ParallelStroke> getParallelStrokes() {
    return parallelStrokes;
  }

  public void setParallelStrokes(Set<ParallelStroke> parallelStrokes) {
    this.parallelStrokes = parallelStrokes;
  }

  /**
   * A constructor from the group components.
   * @param centreStroke
   * @param parallelStrokes
   */
  public ParallelRailsGroup(Stroke centreStroke,
      Set<ParallelStroke> parallelStrokes) {
    super();
    this.centreStroke = centreStroke;
    this.parallelStrokes = parallelStrokes;
  }

  /**
   * Algorithm to find {@link ParallelRailsGroup} instances in a strokes network
   * of railway tracks.
   * @param network
   * @param minLength the minimum length of a stroke to build a group around
   * @param maxDist the maximum distance allowed between two parallel tracks
   * @return
   */
  public static Set<ParallelRailsGroup> findParallelRailsGroup(
      StrokesNetwork network, double minLength, double maxDist) {
    Set<ParallelRailsGroup> groups = new HashSet<ParallelRailsGroup>();
    List<Stroke> strokeList = new ArrayList<Stroke>();
    for (Stroke stroke : network.getStrokes()) {
      if (stroke.getLength() > minLength)
        strokeList.add(stroke);
    }
    Collections.sort(strokeList, new StrokeLengthComparator<Stroke>());

    // reverse the sort order to get the longest strokes at the beginning of the
    // list
    Collections.reverse(strokeList);

    while (!strokeList.isEmpty()) {
      // get the longest stroke
      Stroke longest = strokeList.get(0);

      // arrived here, a ParallelRailsGroup is created around longest
      // we have to find the parallel strokes
      Set<ParallelStroke> parallelStrokes = new HashSet<ParallelStroke>();
      // first search parallel tracks on the left
      IFeatureCollection<Stroke> strokeFc = new FT_FeatureCollection<Stroke>();
      strokeFc.addAll(strokeList);
      parallelStrokes.addAll(recursiveSearchForParallelStrokes(longest,
          Side.LEFT, 0, parallelStrokes, strokeFc, maxDist, longest));
      // then search parallel tracks on the right
      parallelStrokes.addAll(recursiveSearchForParallelStrokes(longest,
          Side.RIGHT, 0, parallelStrokes, strokeFc, maxDist, longest));

      ParallelRailsGroup group = new ParallelRailsGroup(longest,
          parallelStrokes);
      groups.add(group);

      // remove the strokes grouped from the list
      strokeList.remove(0);
      for (ParallelStroke pStroke : parallelStrokes)
        strokeList.remove(pStroke.getStroke());
    }
    return groups;
  }

  /**
   * Recursive method to get all parallel strokes, on one side, of a given
   * stroke.
   * @param stroke
   * @param side
   * @param position
   * @param strokes
   * @param maxDist
   * @return
   */
  private static Set<ParallelStroke> recursiveSearchForParallelStrokes(
      Stroke stroke, Side side, int position, Set<ParallelStroke> pStrokes,
      IFeatureCollection<Stroke> strokes, double maxDist, Stroke centralStroke) {
    Set<ParallelStroke> parallelStrokes = new HashSet<ParallelStroke>();
    IPolygon buffer = BufferComputing.buildLineHalfBuffer(
        stroke.getGeomStroke(), maxDist, side);
    Collection<Stroke> neighbours = strokes.select(buffer);
    neighbours.remove(stroke);
    neighbours.remove(centralStroke);
    for (ParallelStroke pStroke : pStrokes)
      neighbours.remove(pStroke.getStroke());
    if (neighbours.size() == 0)
      return parallelStrokes;

    // order neighbours by proximity to the central stroke
    List<Stroke> orderedNeighbours = new ArrayList<Stroke>(neighbours);
    Collections.sort(orderedNeighbours, new DistanceFeatureComparator<Stroke>(
        centralStroke.getGeomStroke()));

    for (Stroke neighbour : orderedNeighbours) {
      // check it's a parallel neighbour or just a punctual neighbourhood
      IGeometry intersection = buffer.intersection(neighbour.getGeom());
      if (intersection == null) {
        // there is a JTS intersection problem. Try with a slightly different
        // buffer
        IGeometry differentBuffer = buffer.buffer(0.2);
        intersection = differentBuffer.intersection(neighbour.getGeom());
        if (intersection == null)
          continue;
      }
      if (intersection.length() < 100.0)
        continue;

      // increment the position
      if (position == 0) {
        if (side.equals(Side.LEFT))
          position = -1;
        else
          position = 1;
      } else if (position < 0)
        position -= 1;
      else
        position += 1;

      // create the parallelStroke object
      boolean parallel = false;
      ParallelismEnding start = null, end = null;
      IDirectPositionList parallelGeomCoord = new DirectPositionList();
      ILineString line2 = stroke.getGeomStroke();
      for (IDirectPosition pt : neighbour.getGeomStroke().coord()) {
        IDirectPosition closest = JtsAlgorithms.getClosestPoint(pt, line2);
        double dist = closest.distance2D(pt);
        // if it is a parallel section, check if it's a diverging point or a
        // converging point
        if (parallel) {
          if (dist == 0.0) {
            // it's a converging point that finishes parallelism
            Map<ILineString, IDirectPosition> positionOnLines = new HashMap<ILineString, IDirectPosition>();
            positionOnLines.put(line2, pt);
            positionOnLines.put(neighbour.getGeomStroke(), pt);
            end = new ParallelismEnding(ParallelismEndingType.CONVERGING,
                positionOnLines);
            parallelGeomCoord.add(pt);
            parallel = false;
            continue;
          }
          if (dist > maxDist) {
            // it's a diverging point
            Map<ILineString, IDirectPosition> positionOnLines = new HashMap<ILineString, IDirectPosition>();
            positionOnLines.put(line2, closest);
            positionOnLines.put(neighbour.getGeomStroke(), pt);
            end = new ParallelismEnding(ParallelismEndingType.DIVERGING,
                positionOnLines);
            parallelGeomCoord.add(pt);
            parallel = false;
            continue;
          }
        }
        if (dist == 0.0) {
          // the parallelism starts with a converging point
          Map<ILineString, IDirectPosition> positionOnLines = new HashMap<ILineString, IDirectPosition>();
          positionOnLines.put(line2, pt);
          positionOnLines.put(neighbour.getGeomStroke(), pt);
          start = new ParallelismEnding(ParallelismEndingType.CONVERGING,
              positionOnLines);
          parallelGeomCoord.add(pt);
          parallel = true;
          continue;
        }
        if (dist < maxDist) {
          // it's a diverging/dangling point that starts parallelism
          Map<ILineString, IDirectPosition> positionOnLines = new HashMap<ILineString, IDirectPosition>();
          positionOnLines.put(line2, closest);
          positionOnLines.put(neighbour.getGeomStroke(), pt);
          if (pt.equals(neighbour.getGeomStroke().startPoint())) {
            start = new ParallelismEnding(ParallelismEndingType.DANGLING,
                positionOnLines);
          } else {
            start = new ParallelismEnding(ParallelismEndingType.DIVERGING,
                positionOnLines);
          }
          parallelGeomCoord.add(pt);
          parallel = true;
          continue;
        }
      }
      // if end is still null, it means that end is a dangling ending
      if (end == null) {
        Map<ILineString, IDirectPosition> positionOnLines = new HashMap<ILineString, IDirectPosition>();
        IDirectPosition pt = neighbour.getGeomStroke().endPoint();
        IDirectPosition closest = JtsAlgorithms.getClosestPoint(pt, line2);
        positionOnLines.put(line2, closest);
        positionOnLines.put(neighbour.getGeomStroke(), pt);
        end = new ParallelismEnding(ParallelismEndingType.DANGLING,
            positionOnLines);
      }
      ILineString parallelGeom = new GM_LineString(parallelGeomCoord);
      ParallelStroke pStroke = new ParallelStroke(neighbour, position,
          parallelGeom, start, end);
      parallelStrokes.add(pStroke);
      // recursively search for parallel neighbours for neighbour
      IFeatureCollection<Stroke> remainingStrokes = new FT_FeatureCollection<Stroke>();
      remainingStrokes.addAll(strokes);
      remainingStrokes.remove(neighbour);
      // compute the new side (i.e. the neighbour is not necessarily oriented
      // the same way as stroke)
      Side newSide = side;
      IPolygon newBuffer = BufferComputing.buildLineHalfBuffer(
          neighbour.getGeomStroke(), maxDist, side);
      IGeometry interSide = newBuffer.intersection(buffer);
      if (interSide != null) {
        if (interSide.area() > newBuffer.area() / 3)
          newSide = side.inverse();
      }
      parallelStrokes.addAll(recursiveSearchForParallelStrokes(neighbour,
          newSide, position, parallelStrokes, remainingStrokes, maxDist,
          centralStroke));
    }
    return parallelStrokes;
  }
}
