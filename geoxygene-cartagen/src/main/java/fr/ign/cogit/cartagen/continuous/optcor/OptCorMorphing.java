/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.continuous.optcor;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.cartagen.continuous.ContinuousGeneralisationMethod;
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

    private ILineString geomIni, geomFinal;
    /**
     * The threshold distance between a Bezier curve and a line that find
     * characteristic points
     */
    private double epsilon = 25.0;
    private double sigma = 5.0;
    private double step = 10.0;
    private int lookBackK = 4;

    private enum LineDistance {
        WIDTH, FRECHET, INTEGRAL, HAUSDORFF
    };

    private LineDistance usedDistance = LineDistance.WIDTH;

    public OptCorMorphing(ILineString geomIni, ILineString geomFinal) {
        super();
        this.geomIni = geomIni;
        this.geomFinal = geomFinal;
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
        List<ILineString> subLinesIni = characteristicPoints(lineIni);
        List<ILineString> subLinesFin = characteristicPoints(lineFin);

        // then, map the points of each line
        List<SubLineCorrespondance> mapping = matchLinePoints(subLinesIni,
                subLinesFin);

        // then, compute the intermediate position between each correspondant
        IDirectPositionList coord = new DirectPositionList();
        for (SubLineCorrespondance correspondance : mapping) {
            coord.addAll(correspondance.morphCorrespondance(t));
        }
        ILineString morphedLine = GeometryEngine.getFactory()
                .createILineString(coord);
        return morphedLine;
    }

    private List<ILineString> characteristicPoints(ILineString line) {
        List<ILineString> subLines = new ArrayList<>();

        // first, densify and smooth the line
        ILineString smoothLine = GaussianFilter.gaussianFilter(line, sigma,
                step);

        // then, loop on the line vertices from find points from start to end
        IDirectPositionList currentSubLine = new DirectPositionList();
        for (int i = 0; i < smoothLine.coord().size(); i++) {
            if (currentSubLine.size() < 3) {
                currentSubLine.add(smoothLine.coord().get(i));
                continue;
            }
            // first, if it's te last point of the line, just end the current
            // subLine
            if (i == smoothLine.coord().size() - 1) {
                currentSubLine.add(smoothLine.coord().get(i));
                ILineString subLine = GeometryEngine.getFactory()
                        .createILineString(currentSubLine);
                subLines.add(subLine);
                break;
            }
            // creates the Bezier curve and compute the distance
            ILineString subLine = GeometryEngine.getFactory()
                    .createILineString(currentSubLine);
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
                currentSubLine.add(smoothLine.coord().get(i));
                currentSubLine.add(smoothLine.coord().get(i - 1));
            }
        }
        return subLines;
    }

    private List<SubLineCorrespondance> matchLinePoints(
            List<ILineString> subLinesIni, List<ILineString> subLinesFin) {
        List<SubLineCorrespondance> mapping = new ArrayList<>();
        double[][] distanceTable = new double[subLinesIni.size() + 1][subLinesFin
                .size() + 1];
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
        for (int i = 1; i <= subLinesIni.size(); i++) {
            for (int j = 1; j <= subLinesFin.size(); j++) {
                double min = Double.MAX_VALUE;
                CorrespondanceType typeMin = null;
                int bestK = 0;
                // compute C1 distance
                double dist = Distances.distance(subLinesFin.get(j - 1)
                        .endPoint(), subLinesIni.get(i - 1));
                double distC1 = distanceTable[i - 1][j] + dist;
                if (distC1 < min) {
                    min = distC1;
                    typeMin = CorrespondanceType.C1;
                }
                // compute C1' distance
                dist = Distances.distance(subLinesIni.get(i - 1).endPoint(),
                        subLinesFin.get(j - 1));
                double distC1_ = distanceTable[i][j - 1] + dist;
                if (distC1_ < min) {
                    min = distC1_;
                    typeMin = CorrespondanceType.C1_;
                }
                // compute C2 distance
                dist = this.distance(subLinesIni.get(i - 1),
                        subLinesFin.get(j - 1));
                double distC2 = distanceTable[i - 1][j - 1] + dist;
                if (distC1_ < min) {
                    min = distC2;
                    typeMin = CorrespondanceType.C2;
                }
                // compute C3 distance
                for (int k = 2; k <= lookBackK; k++) {
                    List<ILineString> toMerge = new ArrayList<>();
                    for (int l = j - k; l < j; l++)
                        toMerge.add(subLinesFin.get(l));
                    ILineString merged = Operateurs.compileArcs(toMerge);
                    dist = this.distance(subLinesIni.get(i - 1), merged);
                    double distC3 = distanceTable[i - 1][j - k] + dist;
                    if (distC3 < min) {
                        min = distC3;
                        typeMin = CorrespondanceType.C3;
                        bestK = k;
                    }
                }

                // compute C3' distance
                for (int k = 2; k <= lookBackK; k++) {
                    List<ILineString> toMerge = new ArrayList<>();
                    for (int l = i - k; l < i; l++)
                        toMerge.add(subLinesIni.get(l));
                    ILineString merged = Operateurs.compileArcs(toMerge);
                    dist = this.distance(merged, subLinesFin.get(j - 1));
                    double distC3_ = distanceTable[i - k][j - 1] + dist;
                    if (distC3_ < min) {
                        min = distC3_;
                        typeMin = CorrespondanceType.C3_;
                        bestK = k;
                    }
                }

                distanceTable[i][j] = min;
                // store the minimum correspondance
                if (typeMin.equals(CorrespondanceType.C1)) {
                    mapping.add(new C1SubLineCorrespondance(subLinesIni
                            .get(i - 1), subLinesFin.get(j - 1).endPoint()));
                    continue;
                }
                if (typeMin.equals(CorrespondanceType.C1_)) {
                    mapping.add(new C1InvSubLineCorrespondance(subLinesIni.get(
                            i - 1).endPoint(), subLinesFin.get(j - 1)));
                    continue;
                }
                if (typeMin.equals(CorrespondanceType.C2)) {
                    mapping.add(new C2SubLineCorrespondance(subLinesIni
                            .get(i - 1), subLinesFin.get(j - 1)));
                    continue;
                }
                if (typeMin.equals(CorrespondanceType.C3)) {
                    List<ILineString> toMerge = new ArrayList<>();
                    for (int l = j - bestK; l < j; l++)
                        toMerge.add(subLinesFin.get(l));
                    mapping.add(new C3SubLineCorrespondance(subLinesIni
                            .get(i - 1), toMerge));
                    continue;
                }
                if (typeMin.equals(CorrespondanceType.C3_)) {
                    List<ILineString> toMerge = new ArrayList<>();
                    for (int l = i - bestK; l < i; l++)
                        toMerge.add(subLinesIni.get(l));
                    mapping.add(new C3SubLineCorrespondance(subLinesFin
                            .get(j - 1), toMerge));
                    continue;
                }
            }
        }

        return mapping;
    }

    private double distanceToBezier(IBezier curve, ILineString line) {
        // first, compute the resampling step, i.e.
        // length/10*nb_of_vertices(line)
        double step = line.length() / (10 * line.coord().size());

        // resample the curve
        ILineString densifiedCurve = curve.asLineString(step, 0.0);
        ILineString densifiedLine = LineDensification
                .densification2(line, step);

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
            IDirectPosition finalPt = Operateurs.pointEnAbscisseCurviligne(
                    line2, curvi);
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
}
