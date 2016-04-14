package fr.ign.cogit.cartagen.spatialanalysis.measures.section;

import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.cartagen.spatialanalysis.measures.section.LineCurvature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.morphomaths.Side;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;

/**
 * A bend in a line (e.g. in a mountain road). The extent of the bend has to be
 * computed in some way.
 * @author GTouya
 * 
 */
public class Bend {

  private static AtomicInteger counter = new AtomicInteger();

  private ILineString geom;
  /**
   * sigma value for smoothing the line when the summit is computed.
   */
  private double summitSigma = 15.0;
  private IDirectPosition summit = null;
  private int summitIndex = -1;
  private int id;

  public Bend(ILineString geom) {
    super();
    this.id = counter.getAndIncrement();
    this.geom = geom;
  }

  public ILineString getGeom() {
    return geom;
  }

  public void setGeom(ILineString geom) {
    this.geom = geom;
  }

  /**
   * Get the side of the interior of the bend regarding the line direction. The
   * algorithm comes from the AGENT project.
   * @return
   */
  public Side getBendSide() {
    Side side = Side.LEFT;
    IDirectPosition first = getGeom().startPoint();
    double angleSum = 0.0;
    for (int i = 1; i < getGeom().numPoints() - 1; i++) {
      IDirectPosition point1 = getGeom().coord().get(i);
      IDirectPosition point2 = getGeom().coord().get(i + 1);
      Angle angle = Angle.angleTroisPoints(first, point2, point1);
      double angleValue = angle.getValeur();
      if (angleValue > Math.PI)
        angleValue = angleValue - 2 * Math.PI;
      angleSum += angleValue;
    }

    if (angleSum < 0.0)
      side = Side.RIGHT;

    return side;
  }

  /**
   * Close the bend by joining both extremities.
   * @return
   */
  public IPolygon closeBend() {
    IDirectPositionList points = new DirectPositionList();
    points.addAll(getGeom().coord());
    points.add(points.get(0));
    return new GM_Polygon(new GM_LineString(points));
  }

  /**
   * Measure bend length, i.e. the length of the line.
   * @return
   */
  public double getLength() {
    return getGeom().length();
  }

  /**
   * Measure the width of the bend, i.e. the length of the base segment drawn
   * between the inflexion points that create the bend.
   * @return
   */
  public double getWidth() {
    return getGeom().startPoint().distance2D(getGeom().endPoint());
  }

  /**
   * Measure bend height, i.e. the length of the segment between the summit of
   * the bend and the middle of the base segment.
   * @return
   */
  public double getHeight() {
    // first, get the base middle point.
    IDirectPosition baseMiddle = new Segment(getGeom().startPoint(), getGeom()
        .endPoint()).getMiddlePoint();

    // then, get the bend summit
    IDirectPosition summit = this.getBendSummit();

    return summit.distance2D(baseMiddle);
  }

  /**
   * Get the summit point of the bend.
   * @return
   */
  public IDirectPosition getBendSummit() {
    if (summit != null)
      return summit;
    // first, densify the line
    ILineString densLine = LineDensification.densification(getGeom(), 1.0);
    // then, smooth the line
    ILineString smoothLine = GaussianFilter.gaussianFilter(densLine,
        this.summitSigma, 1);
    IDirectPosition smoothSummit = null;
    double maxCurvature = 0.0;
    for (int i = 1; i < smoothLine.numPoints() - 1; i++) {
      double curvature = LineCurvature.getCircumscribedCircleCurvature(
          smoothLine, smoothLine.coord().get(i), smoothLine.coord().get(i - 1),
          smoothLine.coord().get(i + 1));
      if (curvature >= maxCurvature) {
        maxCurvature = curvature;
        smoothSummit = smoothLine.coord().get(i);
      }
    }
    summit = CommonAlgorithmsFromCartAGen.getNearestVertexFromPoint(getGeom(),
        smoothSummit);
    summitIndex = CommonAlgorithmsFromCartAGen
        .getNearestVertexPositionFromPoint(getGeom(), smoothSummit);

    return summit;
  }

  /**
   * Compute a measure of bend symmetry. If D1 is the curvilinear length from
   * bend start and bend summit and D2 the curvilinear length between bend end
   * and bend summit, symmetry is Min(D1,D2)/Max(D1,D2).
   * @return a value between 0 and 1.
   */
  public double getSymmetry() {
    if (summit == null) {
      getBendSummit();
    }
    double d1 = CommonAlgorithmsFromCartAGen.getLineDistanceBetweenIndexes(
        getGeom(), 0, summitIndex);
    double d2 = CommonAlgorithmsFromCartAGen.getLineDistanceBetweenIndexes(
        getGeom(), summitIndex, getGeom().numPoints() - 1);

    return Math.min(d1, d2) / Math.max(d1, d2);
  }

  /**
   * Compute a shape measure of the bend. Ported from PlaGe, and F. Lecordix
   * schematisation algorithm.
   * @return
   */
  public double getShapeMeasure() {
    return (4.0 * getHeight() + getWidth()) / 5.0;
  }

  /**
   * Compute a size measure of the bend. Ported from PlaGe, and F. Lecordix
   * schematisation algorithm.
   * @return
   */
  public double getSizeMeasure() {
    return (4.0 * getHeight() * getWidth() + getLength() * getWidth()) / 5.0;
  }

  /**
   * Compute the orientation of the bend, i.e. the orientation of the segment
   * between the middle of the base segment and the summit.
   * @return the orientation between 0 and 2.Pi
   */
  public double getOrientation() {
    // first, get the base middle point.
    IDirectPosition baseMiddle = new Segment(getGeom().startPoint(), getGeom()
        .endPoint()).getMiddlePoint();

    // then, get the bend summit
    IDirectPosition summit = this.getBendSummit();

    // get the orientation of the segment [baseMiddle, summit]
    Segment segment = new Segment(baseMiddle, summit);

    return segment.orientation();
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Bend other = (Bend) obj;
    if (this.id != other.id)
      return false;
    return true;
  }

}
