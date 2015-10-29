/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.continuous.optcor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.continuous.ContinuousGeneralisationMethod;
import fr.ign.cogit.cartagen.continuous.MorphingVertexMapping;
import fr.ign.cogit.cartagen.continuous.leastsquares.LeastSquaresMorphing;
import fr.ign.cogit.cartagen.continuous.optcor.SubLineCorrespondance.CorrespondanceType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IBezier;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.distance.Frechet;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Bezier;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.util.Pair;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

/**
 * This implementation of continuous generalisation is derived from the paper
 * from Nöllenburg et al 2008 (CEUS). In this method, the morphing is guided by
 * a matching between characeristic points of the line.
 * 
 * @author Guillaume Touya
 * 
 */
public class OptCorMorphing implements ContinuousGeneralisationMethod {

  private static final Logger logger = Logger.getLogger(OptCorMorphing.class);

  private ILineString geomIni, geomFinal;
  /**
   * The threshold distance between a Bezier curve and a line that find
   * characteristic points
   */
  private double epsilon = 25.0;
  private double sigma = 5.0;
  private double step = 10.0;
  private int lookBackK = 4;

  private double[][] distanceTable;
  private List<ILineString> subLinesIni, subLinesFin;

  private enum LineDistance {
    WIDTH, FRECHET, INTEGRAL, HAUSDORFF
  };

  private LineDistance usedDistance = LineDistance.WIDTH;

  public OptCorMorphing(ILineString geomIni, ILineString geomFinal) {
    super();
    this.geomIni = geomIni;
    this.geomFinal = geomFinal;
    // check to line are in the same direction
    double dist1 = geomIni.startPoint().distance2D(geomFinal.startPoint());
    double dist2 = geomIni.startPoint().distance2D(geomFinal.endPoint());
    if (dist2 < dist1) {
      this.geomFinal = geomFinal.reverse();
    }

  }

  @Override
  public IGeometry getGeomIni() {
    return geomIni;
  }

  @Override
  public IGeometry getGeomFinal() {
    return geomFinal;
  }

  @Override
  public IGeometry continuousGeneralisation(double t) {
    // t must be between 0 and 1
    if (t < 0.0)
      return null;
    if (t > 1.0)
      return null;

    ILineString lineIni = null, lineFin = null;
    if (geomIni instanceof ILineString) {
      lineIni = geomIni;
      lineFin = geomFinal;
    } else if (geomIni instanceof IPolygon) {
      lineIni = ((IPolygon) geomIni).exteriorLineString();
      lineFin = ((IPolygon) geomFinal).exteriorLineString();
    }

    // search for characteristic points in the lines
    if (logger.isTraceEnabled())
      logger.trace("characteristic points segmentation for initial line");
    subLinesIni = characteristicPoints(lineIni);

    if (logger.isTraceEnabled())
      logger.trace("characteristic points segmentation for final line");
    subLinesFin = characteristicPoints(lineFin);

    if (logger.isTraceEnabled()) {
      logger.trace("initial sublines list: " + subLinesIni);
      logger.trace("final sublines list: " + subLinesFin);
    }

    // then, map the points of each line
    List<SubLineCorrespondance> mapping = matchLinePoints(subLinesIni,
        subLinesFin);

    // then, compute the intermediate position between each correspondant
    IDirectPositionList coord = new DirectPositionList();
    for (SubLineCorrespondance correspondance : mapping) {
      coord.addAll(correspondance.morphCorrespondance(t));
    }
    ILineString morphedLine = GeometryEngine.getFactory().createILineString(
        coord);
    return morphedLine;
  }

  private List<ILineString> characteristicPoints(ILineString line) {
    List<ILineString> subLines = new ArrayList<>();

    // first, densify and smooth the line
    ILineString smoothLine = GaussianFilter.gaussianFilter(line, sigma, step);

    if (logger.isTraceEnabled()) {
      logger.trace("initial line: " + line);
      logger.trace("smoothed line: " + smoothLine);
      logger.trace(smoothLine.coord().size());
    }

    // special case: the smoothed line has only 3 vertices
    if (smoothLine.coord().size() == 3) {
      subLines.add(smoothLine);
      return subLines;
    }

    // then, loop on the line vertices from find points from start to end
    IDirectPositionList currentSubLine = new DirectPositionList();
    for (int i = 0; i < smoothLine.coord().size(); i++) {
      if (currentSubLine.size() < 3) {
        currentSubLine.add(smoothLine.coord().get(i));
        if (i == smoothLine.coord().size() - 1 && currentSubLine.size() > 1) {
          ILineString subLine = GeometryEngine.getFactory().createILineString(
              currentSubLine);
          subLines.add(subLine);
        }
        continue;
      }
      // first, if it's the last point of the line, just end the current
      // subLine
      if (i == smoothLine.coord().size() - 1) {
        currentSubLine.add(smoothLine.coord().get(i));
        ILineString subLine = GeometryEngine.getFactory().createILineString(
            currentSubLine);
        subLines.add(subLine);
        break;
      }
      // creates the Bezier curve and compute the distance
      ILineString subLine = GeometryEngine.getFactory().createILineString(
          currentSubLine);
      IBezier bezier = fitBezierToLine(subLine);
      double dist = distanceToBezier(bezier, subLine);
      if (dist < this.epsilon) {
        // continue the current subLine with another point
        currentSubLine.add(smoothLine.coord().get(i));
        continue;
      } else {
        // this is the end of the subLine
        subLines.add(subLine);
        currentSubLine.clear();
        // start another subLine
        currentSubLine.add(smoothLine.coord().get(i - 1));
        currentSubLine.add(smoothLine.coord().get(i));
      }
    }
    if (logger.isTraceEnabled()) {
      logger.trace(subLines);
      logger.trace("for line: " + line);
    }
    return subLines;
  }

  private List<SubLineCorrespondance> matchLinePoints(
      List<ILineString> subLinesIni, List<ILineString> subLinesFin) {
    List<SubLineCorrespondance> mapping = new ArrayList<>();
    distanceTable = new double[subLinesIni.size() + 1][subLinesFin.size() + 1];
    distanceTable[0][0] = 0.0;
    // fill the table for column 0
    for (int i = 1; i <= subLinesIni.size(); i++) {
      double dist = Distances.distance(subLinesFin.get(0).startPoint(),
          subLinesIni.get(i - 1));
      distanceTable[i][0] = distanceTable[i - 1][0] + dist;
    }
    // fill the table for line 0
    for (int j = 1; j <= subLinesFin.size(); j++) {
      double dist = Distances.distance(subLinesIni.get(0).startPoint(),
          subLinesFin.get(j - 1));
      distanceTable[0][j] = distanceTable[0][j - 1] + dist;
    }
    Map<Pair<Integer, Integer>, Pair<Integer, Integer>> minimumStorage = new HashMap<>();
    Map<Pair<Integer, Integer>, CorrespondanceType> typeStorage = new HashMap<>();
    for (int i = 1; i <= subLinesIni.size(); i++) {
      for (int j = 1; j <= subLinesFin.size(); j++) {
        double min = Double.MAX_VALUE;
        // compute C1 distance
        double dist = Math.max(
            subLinesFin.get(j - 1).endPoint()
                .distance2D(subLinesIni.get(i - 1).startPoint()),
            subLinesFin.get(j - 1).endPoint()
                .distance2D(subLinesIni.get(i - 1).endPoint()));
        double distC1 = distanceTable[i - 1][j] + dist;
        if (distC1 < min) {
          min = distC1;
          minimumStorage.put(new Pair<Integer, Integer>(i, j),
              new Pair<Integer, Integer>(i - 1, j));
          typeStorage.put(new Pair<Integer, Integer>(i, j),
              CorrespondanceType.C1);
        }
        // compute C1' distance
        dist = Math.max(
            subLinesIni.get(i - 1).endPoint()
                .distance2D(subLinesFin.get(j - 1).startPoint()),
            subLinesIni.get(i - 1).endPoint()
                .distance2D(subLinesFin.get(j - 1).endPoint()));
        double distC1_ = distanceTable[i][j - 1] + dist;
        if (distC1_ < min) {
          min = distC1_;
          minimumStorage.put(new Pair<Integer, Integer>(i, j),
              new Pair<Integer, Integer>(i, j - 1));
          typeStorage.put(new Pair<Integer, Integer>(i, j),
              CorrespondanceType.C1_);
        }
        // compute C2 distance
        dist = this.distance(subLinesIni.get(i - 1), subLinesFin.get(j - 1));
        double distC2 = distanceTable[i - 1][j - 1] + dist;
        if (distC1_ < min) {
          min = distC2;
          minimumStorage.put(new Pair<Integer, Integer>(i, j),
              new Pair<Integer, Integer>(i - 1, j - 1));
          typeStorage.put(new Pair<Integer, Integer>(i, j),
              CorrespondanceType.C2);
        }

        // compute C3 distance
        for (int k = 2; k <= lookBackK; k++) {
          List<ILineString> toMerge = new ArrayList<>();
          for (int l = Math.max(0, j - k); l < j; l++)
            toMerge.add(subLinesFin.get(l));
          ILineString merged = Operateurs.compileArcs(toMerge);
          dist = this.distance(subLinesIni.get(i - 1), merged);
          double distC3 = distanceTable[i - 1][Math.max(0, j - k)] + dist;
          if (distC3 < min) {
            min = distC3;
            minimumStorage.put(new Pair<Integer, Integer>(i, j),
                new Pair<Integer, Integer>(i - 1, Math.max(0, j - k)));
            typeStorage.put(new Pair<Integer, Integer>(i, j),
                CorrespondanceType.C3);
          }
        }

        // compute C3' distance
        for (int k = 2; k <= lookBackK; k++) {
          List<ILineString> toMerge = new ArrayList<>();
          for (int l = Math.max(0, i - k); l < i; l++)
            toMerge.add(subLinesIni.get(l));
          ILineString merged = Operateurs.compileArcs(toMerge);
          dist = this.distance(merged, subLinesFin.get(j - 1));
          double distC3_ = distanceTable[Math.max(0, i - k)][j - 1] + dist;
          if (distC3_ < min) {
            min = distC3_;
            minimumStorage.put(new Pair<Integer, Integer>(i, j),
                new Pair<Integer, Integer>(Math.max(0, i - k), j - 1));
            typeStorage.put(new Pair<Integer, Integer>(i, j),
                CorrespondanceType.C3_);
          }
        }

        // update table(i,j) with the minimum value
        distanceTable[i][j] = min;
      }
    }

    // backtracking the table to compute the mapping
    boolean finished = false;
    Pair<Integer, Integer> currentPair = new Pair<>(subLinesIni.size(),
        subLinesFin.size());
    while (!finished) {
      // add the current correspondance at the beginning of mappings list
      CorrespondanceType type = typeStorage.get(currentPair);
      if (type.equals(CorrespondanceType.C1)) {
        IDirectPosition prevPt = subLinesFin.get(currentPair.getV() - 1)
            .coord()
            .get(subLinesFin.get(currentPair.getV() - 1).coord().size() - 2);
        IDirectPosition nextPt = null;
        if (currentPair.getV() < subLinesFin.size())
          nextPt = subLinesFin.get(currentPair.getV()).coord().get(1);
        mapping.add(
            0,
            new C1SubLineCorrespondance(
                subLinesIni.get(currentPair.getU() - 1), subLinesFin.get(
                    currentPair.getV() - 1).endPoint(), prevPt, nextPt));
      } else if (type.equals(CorrespondanceType.C1_)) {
        IDirectPosition prevPt = subLinesIni.get(currentPair.getU() - 1)
            .coord()
            .get(subLinesIni.get(currentPair.getU() - 1).coord().size() - 2);
        IDirectPosition nextPt = null;
        if (currentPair.getU() < subLinesIni.size())
          nextPt = subLinesIni.get(currentPair.getU()).coord().get(1);
        mapping.add(
            0,
            new C1InvSubLineCorrespondance(subLinesIni.get(
                currentPair.getU() - 1).endPoint(), subLinesFin.get(currentPair
                .getV() - 1), prevPt, nextPt));
      } else if (type.equals(CorrespondanceType.C2)) {
        mapping.add(
            0,
            new C2SubLineCorrespondance(
                subLinesIni.get(currentPair.getU() - 1), subLinesFin
                    .get(currentPair.getV() - 1)));
      } else if (type.equals(CorrespondanceType.C3)) {
        List<ILineString> finalLines = new ArrayList<>();
        Pair<Integer, Integer> previousPair = minimumStorage.get(currentPair);
        for (int i = Math.max(0, previousPair.getV()); i < currentPair.getV(); i++)
          finalLines.add(subLinesFin.get(i));
        mapping.add(0,
            new C3SubLineCorrespondance(
                subLinesIni.get(currentPair.getU() - 1), finalLines));
      } else {
        List<ILineString> initialLines = new ArrayList<>();
        Pair<Integer, Integer> previousPair = minimumStorage.get(currentPair);
        for (int i = Math.max(0, previousPair.getU()); i < currentPair.getU(); i++)
          initialLines.add(subLinesIni.get(i));
        mapping.add(
            0,
            new C3InvSubLineCorrespondance(
                subLinesFin.get(currentPair.getV() - 1), initialLines));
      }
      // get the previous pair
      currentPair = minimumStorage.get(currentPair);
      if (currentPair.getU() == 0 || currentPair.getV() == 0)
        finished = true;
    }

    if (!isSublineMapped(subLinesIni.get(0), mapping)) {
      mapping.add(0, new C1SubLineCorrespondance(subLinesIni.get(0),
          subLinesFin.get(0).startPoint(), null, subLinesFin.get(0)
              .getControlPoint(1)));
    }

    return mapping;
  }

  private boolean isSublineMapped(ILineString subline,
      List<SubLineCorrespondance> mapping) {
    for (SubLineCorrespondance correspondance : mapping) {
      if (correspondance.containsSubLine(subline))
        return true;
    }
    return false;
  }

  private double distanceToBezier(IBezier curve, ILineString line) {
    // first, compute the resampling step, i.e.
    // length/10*nb_of_vertices(line)
    double step = line.length() / (10 * line.coord().size());

    // resample the curve
    ILineString densifiedCurve = curve.asLineString(step, 0.0);
    ILineString densifiedLine = LineDensification.densification2(line, step);

    double maxDist = 0.0;

    for (int i = 0; i < densifiedCurve.coord().size(); i++) {
      int index = i;
      if (index >= densifiedLine.coord().size())
        index = densifiedLine.coord().size() - 1;
      double dist = densifiedCurve.coord().get(i)
          .distance2D(densifiedLine.coord().get(index));
      if (dist > maxDist)
        maxDist = dist;
    }

    return maxDist;
  }

  /**
   * Creates a Bezier curve that fits the given LineString.
   * 
   * @param line
   * @return
   */
  private IBezier fitBezierToLine(ILineString line) {
    IDirectPositionList controlPts = new DirectPositionList();
    // add start point as control point
    controlPts.add(line.startPoint());

    // compute factor k, as one third of the line (see Schneider 1988 ou
    // Sezgin 2001)
    double k = line.length() / 3;

    // compute the second control point
    Vector2D vect1 = new Vector2D(line.startPoint(), line.coord().get(1))
        .changeNorm(k);
    controlPts.add(vect1.translate(line.startPoint()));

    // compute the third control point
    Vector2D vect2 = new Vector2D(line.endPoint(), line.coord().get(
        line.coord().size() - 2)).changeNorm(k);
    controlPts.add(vect2.translate(line.endPoint()));

    // add last point of the line as control point
    controlPts.add(line.endPoint());
    return new GM_Bezier(controlPts);
  }

  private double distance(ILineString line1, ILineString line2) {
    if (usedDistance.equals(LineDistance.WIDTH))
      return widthDistance(line1, line2);
    if (usedDistance.equals(LineDistance.FRECHET))
      return frechetDistance(line1, line2);
    if (usedDistance.equals(LineDistance.HAUSDORFF))
      return hausdorffDistance(line1, line2);
    if (usedDistance.equals(LineDistance.INTEGRAL))
      return integralDistance(line1, line2);
    return 0.0;
  }

  private double widthDistance(ILineString line1, ILineString line2) {
    double max = 0.0;
    double dist = 0.0;
    double total = line1.length();
    double totalFinal = line2.length();
    IDirectPosition prevPt = null;
    for (IDirectPosition pt : line1.coord()) {
      if (prevPt == null) {
        prevPt = pt;
        double trajDist = pt.distance2D(line2.startPoint());
        if (trajDist > max)
          max = trajDist;
        continue;
      }
      dist += pt.distance2D(prevPt);
      double ratio = dist / total;

      // get the point at the curvilinear coordinate corresponding to
      // ratio
      double curvi = totalFinal * ratio;
      IDirectPosition finalPt = Operateurs.pointEnAbscisseCurviligne(line2,
          curvi);
      double trajDist = pt.distance2D(finalPt);
      if (trajDist > max)
        max = trajDist;
      prevPt = pt;
    }
    return max;
  }

  private double frechetDistance(ILineString line1, ILineString line2) {
    return Frechet.discreteFrechet(line1, line2);
  }

  private double integralDistance(ILineString line1, ILineString line2) {
    return Distances.ecartSurface(line1, line2);
  }

  private double hausdorffDistance(ILineString line1, ILineString line2) {
    return Distances.hausdorff(line1, line2);
  }

  /**
   * Use the OptCor method to produce a vertex mapping rather than a morphing,
   * in order to be used in another morphing method such as
   * {@link LeastSquaresMorphing}.
   * @return
   */
  public MorphingVertexMapping matchLinesVertices() {
    IDirectPositionList initialCoords = new DirectPositionList();
    IDirectPositionList finalCoords = new DirectPositionList();

    ILineString lineIni = null, lineFin = null;
    if (geomIni instanceof ILineString) {
      lineIni = geomIni;
      lineFin = geomFinal;
    } else if (geomIni instanceof IPolygon) {
      lineIni = ((IPolygon) geomIni).exteriorLineString();
      lineFin = ((IPolygon) geomFinal).exteriorLineString();
    }

    // search for characteristic points in the lines
    subLinesIni = characteristicPoints(lineIni);
    subLinesFin = characteristicPoints(lineFin);

    // then, map the points of each line
    List<SubLineCorrespondance> mapping = matchLinePoints(subLinesIni,
        subLinesFin);
    System.out.println("subLinesIni: " + subLinesIni);
    System.out.println("subLinesFin: " + subLinesFin);
    System.out.println(mapping);

    for (SubLineCorrespondance correspondance : mapping) {
      correspondance.matchVertices(initialCoords, finalCoords);
    }

    return new MorphingVertexMapping(initialCoords, finalCoords);
  }

  public void setUsedDistanceToFrechet() {
    this.usedDistance = LineDistance.FRECHET;
  }

  public void setUsedDistanceToIntegral() {
    this.usedDistance = LineDistance.INTEGRAL;
  }

  public void setUsedDistanceToHausdorff() {
    this.usedDistance = LineDistance.HAUSDORFF;
  }

  public void setUsedDistanceToWidth() {
    this.usedDistance = LineDistance.WIDTH;
  }

  public double getEpsilon() {
    return epsilon;
  }

  public void setEpsilon(double epsilon) {
    this.epsilon = epsilon;
  }

  public int getLookBackK() {
    return lookBackK;
  }

  public void setLookBackK(int lookBackK) {
    this.lookBackK = lookBackK;
  }

  public double[][] getDistanceTable() {
    return distanceTable;
  }

  public List<ILineString> getSubLinesIni() {
    return subLinesIni;
  }

  public List<ILineString> getSubLinesFin() {
    return subLinesFin;
  }

}
