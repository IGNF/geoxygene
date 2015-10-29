/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.continuous.leastsquares;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import Jama.Matrix;
import fr.ign.cogit.cartagen.continuous.ContinuousGeneralisationMethod;
import fr.ign.cogit.cartagen.continuous.MorphingVertexMapping;
import fr.ign.cogit.cartagen.continuous.optcor.OptCorMorphing;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.leastsquares.NonLinearLeastSquares;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomengine.AbstractGeometryEngine;

/**
 * Implementation of the continuous generalisation method from Peng et al.
 * (2013, ICA Generalisation Workshop). It is a line morphing optimised by a
 * Least Squares adjustment. The equations are not linear, so an iterative least
 * squares adjustment is used. The initial step to match line vertices uses the
 * optcor matching process.
 * @author GTouya
 * 
 */
public class LeastSquaresMorphing extends NonLinearLeastSquares implements
    ContinuousGeneralisationMethod {

  private static final Logger logger = Logger
      .getLogger(LeastSquaresMorphing.class);

  private ILineString geomIni, geomFinal;
  /**
   * The mapping between the vertices of the initial lines.
   */
  private MorphingVertexMapping mapping;

  /**
   * The set of anchor points, i.e. the points that move linearly regarding the
   * matching, and that are not unknowns of the least squares adjustment (e.g.
   * the first and last points of the line).
   */
  private Set<IDirectPosition> anchorPts = new HashSet<>();

  /**
   * The time value for the current iteration
   */
  private double time;
  /**
   * The time value at the current iteration of the process
   */
  private double currentTime;
  /**
   * The number of iterations to reach the morphed line.
   */
  private int k = 20;
  private IDirectPositionList currentPoints = new DirectPositionList();
  private List<ILineString> intermediateLines = new ArrayList<>();
  private boolean fillInterLines = true;
  private Matrix a, b, p, currentObs;
  private List<IDirectPosition> initialAnchors, finalAnchors;

  public LeastSquaresMorphing(ILineString geomIni, ILineString geomFinal) {
    super();
    this.geomIni = geomIni;
    this.geomFinal = geomFinal;
    // check to line are in the same direction
    double dist1 = geomIni.startPoint().distance2D(geomFinal.startPoint());
    double dist2 = geomIni.startPoint().distance2D(geomFinal.endPoint());
    if (dist2 < dist1) {
      this.geomFinal = geomFinal.reverse();
    }

    // add first and last points as anchors
    IDirectPosition anchor1 = new DirectPosition(this.geomIni.startPoint()
        .getX(), this.geomIni.startPoint().getY());
    IDirectPosition anchor2 = new DirectPosition(
        this.geomIni.endPoint().getX(), this.geomIni.endPoint().getY());
    this.anchorPts.add(anchor1);
    this.anchorPts.add(anchor2);
    this.initialAnchors = new ArrayList<>();
    this.initialAnchors.add(this.geomIni.startPoint());
    this.initialAnchors.add(this.geomIni.endPoint());
    this.finalAnchors = new ArrayList<>();
    this.finalAnchors.add(this.geomFinal.startPoint());
    this.finalAnchors.add(this.geomFinal.endPoint());
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
    this.setTime(t);
    // first, compute the vertex mapping between both polylines
    OptCorMorphing optcor = new OptCorMorphing(geomIni, geomFinal);
    mapping = optcor.matchLinesVertices();

    // set the initial points as current points
    for (IDirectPosition pt : mapping.getInitialCoords()) {
      currentPoints.add(pt);
    }
    if (logger.isTraceEnabled()) {
      logger.trace("current points list initially: " + currentPoints);
    }

    // k iterations to time t are used to compute the final morphing
    for (int i = 0; i < k; i++) {
      this.a = null;
      this.b = null;
      this.p = null;
      // compute the time value at iteration i
      currentTime = t * (i + 1) / k;
      Matrix dx = computeDx();
      updatePointList(dx);
      if (logger.isTraceEnabled()) {
        logger.trace("dx matrix at " + i + "th iteration: ");
        for (int j = 0; j < dx.getRowDimension(); j++)
          logger.trace(dx.get(j, 0));
        for (int j = 0; j < getA().getRowDimension(); j++) {
          for (int l = 0; l < getA().getColumnDimension(); l++) {
            double value = getA().get(j, l);
            if (value != 0.0)
              logger.trace("Matrix A at (" + j + ", " + l + "): " + value);
          }
        }
        Matrix b = getB();
        logger.trace("B matrix at " + i + "th iteration: ");
        for (int j = 0; j < b.getRowDimension(); j++)
          logger.trace(b.get(j, 0));
        logger.trace("current points list at " + i + "th iteration: "
            + currentPoints);
      }

      // add a new intermediate line if required
      if (fillInterLines) {
        this.intermediateLines.add(AbstractGeometryEngine.getFactory()
            .createILineString(currentPoints));
      }
    }

    return AbstractGeometryEngine.getFactory().createILineString(currentPoints);
  }

  /**
   * Update currentPoints with a dx matrix computed at an iteration of the
   * process. Also updates the anchor points
   * @param dx
   */
  private void updatePointList(Matrix dx) {
    int n = currentPoints.size();
    int nbAnchor = 0;
    Set<IDirectPosition> newAnchors = new HashSet<>();
    for (int i = 0; i < n; i++) {
      IDirectPosition oldPt = currentPoints.get(i);
      if (isAnchorPt(oldPt)) {
        double xNew = (1 - currentTime) * initialAnchors.get(nbAnchor).getX()
            + currentTime * finalAnchors.get(nbAnchor).getX();
        double yNew = (1 - currentTime) * initialAnchors.get(nbAnchor).getY()
            + currentTime * finalAnchors.get(nbAnchor).getY();
        IDirectPosition newPt = new DirectPosition(xNew, yNew);
        newAnchors.add(newPt);
        currentPoints.set(i, newPt);
        nbAnchor++;
        continue;
      }

      double devX = dx.get(2 * (i - nbAnchor), 0);
      double devY = dx.get(2 * (i - nbAnchor) + 1, 0);
      IDirectPosition newPt = new DirectPosition(oldPt.getX() + devX,
          oldPt.getY() + devY);
      currentPoints.set(i, newPt);

    }
    // update anchorPts
    anchorPts.clear();
    anchorPts.addAll(newAnchors);
  }

  @Override
  public Matrix getA() {
    if (a == null) {
      int n = (currentPoints.size() - anchorPts.size()) * 2;
      // a = new Matrix(2 * n + 1, n);
      a = new Matrix(2 * n, n);
      int j = 0;
      int anchorNb = 1;
      IDirectPosition prevPt = currentPoints.get(0);
      for (int i = 1; i < currentPoints.size() - 1; i++) {
        IDirectPosition pt = currentPoints.get(i);
        if (isAnchorPt(pt)) {
          anchorNb++;
          continue;
        }
        IDirectPosition nextPt = currentPoints.get(i + 1);

        // add the angle constraint for point pt
        if (i == 1)
          this.addFirstAngleConstraint(a, pt, nextPt);
        else if (i == currentPoints.size() - 2)
          this.addLastAngleConstraint(a, prevPt, pt, j);
        else
          this.addAngleConstraint(a, prevPt, pt, nextPt, i - anchorNb, j);
        j++;

        // add the edge constraint
        if (i == 1)
          this.addFirstEdgeConstraint(a, pt);
        else
          this.addEdgeConstraint(a, prevPt, pt, i - anchorNb, j);
        j++;

        // add the point constraint
        this.addPointConstraint(a, pt, i - anchorNb, j);
        j += 2;

        // update the previous point
        prevPt = pt;
      }
      anchorNb++;
      // add the last edge constraint
      /*
       * if (!isAnchorPt(currentPoints.get(currentPoints.size() - 2)))
       * this.addLastEdgeConstraint(a, currentPoints.get(currentPoints.size() -
       * 1), j);
       */
    }
    return a;
  }

  private void addEdgeConstraint(Matrix a, IDirectPosition prevPt,
      IDirectPosition pt, int ptIndex, int lineIndex) {
    // once linearised, the equation is a1.xi-1 + a2.yi-1 + b1.xi + b2.yi where
    // a1, b1, ... are partial derivates of the non linear equation on segment
    // length preservation.
    double denominator = Math.pow(pt.getX() - prevPt.getX(), 2.0)
        + Math.pow(pt.getY() - prevPt.getY(), 2.0);
    double a1 = (prevPt.getX() - pt.getX()) / Math.sqrt(denominator);
    double a2 = (prevPt.getY() - pt.getY()) / Math.sqrt(denominator);
    double b1 = (pt.getX() - prevPt.getX()) / Math.sqrt(denominator);
    double b2 = (pt.getY() - prevPt.getY()) / Math.sqrt(denominator);

    // now fill Matrix a
    if (!isAnchorPt(prevPt)) {
      a.set(lineIndex, Math.max(0, 2 * (ptIndex - 1)), a1);
      a.set(lineIndex, Math.max(1, 2 * (ptIndex - 1) + 1), a2);
    }

    a.set(lineIndex, 2 * ptIndex, b1);
    a.set(lineIndex, 2 * ptIndex + 1, b2);

  }

  private void addFirstEdgeConstraint(Matrix a, IDirectPosition pt) {
    double xIni = this.initialAnchors.get(0).getX() * (1 - currentTime)
        + currentTime * this.finalAnchors.get(0).getX();
    double yIni = this.initialAnchors.get(0).getY() * (1 - currentTime)
        + currentTime * this.finalAnchors.get(0).getY();
    IDirectPosition prevPt = new DirectPosition(xIni, yIni);

    double denominator = Math.pow(pt.getX() - prevPt.getX(), 2.0)
        + Math.pow(pt.getY() - prevPt.getY(), 2.0);
    double b1 = (pt.getX() - prevPt.getX()) / Math.sqrt(denominator);
    double b2 = (pt.getY() - prevPt.getY()) / Math.sqrt(denominator);

    // now fill Matrix a
    a.set(1, 0, b1);
    a.set(1, 1, b2);
  }

  private void addLastEdgeConstraint(Matrix a, IDirectPosition pt, int lineIndex) {
    double xIni = this.initialAnchors.get(1).getX() * (1 - currentTime)
        + currentTime * this.finalAnchors.get(1).getX();
    double yIni = this.initialAnchors.get(1).getY() * (1 - currentTime)
        + currentTime * this.finalAnchors.get(1).getY();
    IDirectPosition prevPt = new DirectPosition(xIni, yIni);

    double denominator = Math.pow(pt.getX() - prevPt.getX(), 2.0)
        + Math.pow(pt.getY() - prevPt.getY(), 2.0);
    double a1 = (prevPt.getX() - pt.getX()) / Math.sqrt(denominator);
    double a2 = (prevPt.getY() - pt.getY()) / Math.sqrt(denominator);

    // now fill Matrix a
    a.set(lineIndex, a.getColumnDimension() - 2, a1);
    a.set(lineIndex, a.getColumnDimension() - 1, a2);
  }

  private void addPointConstraint(Matrix a, IDirectPosition pt, int ptIndex,
      int lineIndex) {
    a.set(lineIndex, 2 * ptIndex, 1);
    a.set(lineIndex + 1, 2 * ptIndex + 1, 1);
  }

  private void addAngleConstraint(Matrix a, IDirectPosition prevPt,
      IDirectPosition pt, IDirectPosition nextPt, int ptIndex, int lineIndex) {
    // once linearised, the equation is a1.xi-1 + a2.yi-1 + b1.xi + b2.yi
    // +c1.xi+1 + c2.yi+1 where
    // a1, b1, ... are partial derivates of the non linear equation on angle
    // preservation.

    double a1 = (prevPt.getY() - pt.getY())
        / (Math.pow(pt.getX() - prevPt.getX(), 2.0) + Math.pow(pt.getY()
            - prevPt.getY(), 2.0));
    double a2 = (pt.getX() - prevPt.getX())
        / (Math.pow(pt.getX() - prevPt.getX(), 2.0) + Math.pow(pt.getY()
            - prevPt.getY(), 2.0));
    double b1 = (nextPt.getY() - pt.getY())
        / (Math.pow(nextPt.getX() - pt.getX(), 2.0) + Math.pow(nextPt.getY()
            - pt.getY(), 2.0))
        + (pt.getY() - prevPt.getY())
        / (Math.pow(prevPt.getX() - pt.getX(), 2.0) + Math.pow(pt.getY()
            - prevPt.getY(), 2.0));
    double b2 = (pt.getX() - nextPt.getX())
        / (Math.pow(nextPt.getX() - pt.getX(), 2.0) + Math.pow(nextPt.getY()
            - pt.getY(), 2.0))
        - (pt.getX() - prevPt.getX())
        / (Math.pow(pt.getX() - prevPt.getX(), 2.0) + Math.pow(prevPt.getY()
            - pt.getY(), 2.0));
    double c1 = (pt.getY() - nextPt.getY())
        / ((Math.pow(pt.getX() - nextPt.getX(), 2.0) + Math.pow(nextPt.getY()
            - pt.getY(), 2.0)));
    double c2 = (nextPt.getX() - pt.getX())
        / (Math.pow(nextPt.getX() - pt.getX(), 2.0) + Math.pow(pt.getY()
            - nextPt.getY(), 2.0));

    // now fill Matrix a
    if (!isAnchorPt(prevPt)) {
      a.set(lineIndex, Math.max(0, 2 * (ptIndex - 1)), a1);
      a.set(lineIndex, Math.max(1, 2 * (ptIndex - 1) + 1), a2);
    }

    a.set(lineIndex, 2 * ptIndex, b1);
    a.set(lineIndex, 2 * ptIndex + 1, b2);

    if (!isAnchorPt(nextPt)) {
      a.set(lineIndex, 2 * (ptIndex + 1), c1);
      a.set(lineIndex, 2 * (ptIndex + 1) + 1, c2);
    }

  }

  private void addFirstAngleConstraint(Matrix a, IDirectPosition pt,
      IDirectPosition nextPt) {
    double xIni = this.initialAnchors.get(0).getX() * (1 - currentTime)
        + currentTime * this.finalAnchors.get(0).getX();
    double yIni = this.initialAnchors.get(0).getY() * (1 - currentTime)
        + currentTime * this.finalAnchors.get(0).getY();
    IDirectPosition prevPt = new DirectPosition(xIni, yIni);

    double b1 = (nextPt.getY() - pt.getY())
        / (Math.pow(nextPt.getX() - pt.getX(), 2.0) + Math.pow(nextPt.getY()
            - pt.getY(), 2.0))
        + (pt.getY() - prevPt.getY())
        / (Math.pow(prevPt.getX() - pt.getX(), 2.0) + Math.pow(pt.getY()
            - prevPt.getY(), 2.0));
    double b2 = (pt.getX() - nextPt.getX())
        / (Math.pow(nextPt.getX() - pt.getX(), 2.0) + Math.pow(nextPt.getY()
            - pt.getY(), 2.0))
        - (pt.getX() - prevPt.getX())
        / (Math.pow(pt.getX() - prevPt.getX(), 2.0) + Math.pow(prevPt.getY()
            - pt.getY(), 2.0));
    double c1 = (pt.getY() - nextPt.getY())
        / ((Math.pow(pt.getX() - nextPt.getX(), 2.0) + Math.pow(nextPt.getY()
            - pt.getY(), 2.0)));
    double c2 = (nextPt.getX() - pt.getX())
        / (Math.pow(nextPt.getX() - pt.getX(), 2.0) + Math.pow(pt.getY()
            - nextPt.getY(), 2.0));

    // now fill Matrix a
    a.set(0, 0, b1);
    a.set(0, 1, b2);
    a.set(0, 2, c1);
    a.set(0, 3, c2);
  }

  private void addLastAngleConstraint(Matrix a, IDirectPosition prevPt,
      IDirectPosition pt, int lineIndex) {
    double xIni = this.initialAnchors.get(1).getX() * (1 - currentTime)
        + currentTime * this.finalAnchors.get(1).getX();
    double yIni = this.initialAnchors.get(1).getY() * (1 - currentTime)
        + currentTime * this.finalAnchors.get(1).getY();
    IDirectPosition nextPt = new DirectPosition(xIni, yIni);

    double a1 = (prevPt.getY() - pt.getY())
        / (Math.pow(pt.getX() - prevPt.getX(), 2.0) + Math.pow(pt.getY()
            - prevPt.getY(), 2.0));
    double a2 = (pt.getX() - prevPt.getX())
        / (Math.pow(pt.getX() - prevPt.getX(), 2.0) + Math.pow(pt.getY()
            - prevPt.getY(), 2.0));
    double b1 = (nextPt.getY() - pt.getY())
        / (Math.pow(nextPt.getX() - pt.getX(), 2.0) + Math.pow(nextPt.getY()
            - pt.getY(), 2.0))
        + (pt.getY() - prevPt.getY())
        / (Math.pow(prevPt.getX() - pt.getX(), 2.0) + Math.pow(pt.getY()
            - prevPt.getY(), 2.0));
    double b2 = (pt.getX() - nextPt.getX())
        / (Math.pow(nextPt.getX() - pt.getX(), 2.0) + Math.pow(nextPt.getY()
            - pt.getY(), 2.0))
        - (pt.getX() - prevPt.getX())
        / (Math.pow(pt.getX() - prevPt.getX(), 2.0) + Math.pow(prevPt.getY()
            - pt.getY(), 2.0));

    // now fill Matrix a
    a.set(lineIndex, a.getColumnDimension() - 4, a1);
    a.set(lineIndex, a.getColumnDimension() - 3, a2);
    a.set(lineIndex, a.getColumnDimension() - 2, b1);
    a.set(lineIndex, a.getColumnDimension() - 1, b2);

  }

  @Override
  public Matrix getY() {
    int n = (currentPoints.size() - anchorPts.size()) * 2;
    /*
     * Matrix y = new Matrix(2 * n + 1, 1); this.currentObs = new Matrix(2 * n +
     * 1, 1);
     */
    Matrix y = new Matrix(2 * n, 1);
    this.currentObs = new Matrix(2 * n, 1);
    int matrixRow = 0;
    for (int i = 1; i < currentPoints.size() - 1; i++) {
      IDirectPosition pt = currentPoints.get(i);
      if (isAnchorPt(pt))
        continue;
      // get the counterparts in initial geom and final geom
      IDirectPosition iniPt = mapping.getInitialCoords().get(i);
      IDirectPosition iniPrevPt = mapping.getInitialCoords().get(i - 1);
      IDirectPosition iniNextPt = mapping.getInitialCoords().get(i + 1);
      IDirectPosition finalPt = mapping.getFinalCoords().get(i);
      IDirectPosition finalPrevPt = mapping.getFinalCoords().get(i - 1);
      IDirectPosition finalNextPt = mapping.getFinalCoords().get(i + 1);
      // compute the observation for the angle preservation
      double angleIni = Math.atan((iniNextPt.getY() - iniPt.getY())
          / (iniNextPt.getX() - iniPt.getX()))
          - Math.atan((iniPt.getY() - iniPrevPt.getY())
              / (iniPt.getX() - iniPrevPt.getX()));

      double angleFin = Math.atan((finalNextPt.getY() - finalPt.getY())
          / (finalNextPt.getX() - finalPt.getX()))
          - Math.atan((finalPt.getY() - finalPrevPt.getY())
              / (finalPt.getX() - finalPrevPt.getX()));

      double obsAngle = angleIni * (1 - currentTime) + currentTime * angleFin;
      // set the observation in the matrix
      y.set(matrixRow, 0, obsAngle);

      // now do the same thing for the current observation
      // get the counterparts in initial geom and final geom
      IDirectPosition prevPt = currentPoints.get(i - 1);
      IDirectPosition nextPt = currentPoints.get(i + 1);
      // compute the observation for the angle preservation
      double currObsAngle = Math.atan((nextPt.getY() - pt.getY())
          / (nextPt.getX() - pt.getX()))
          - Math
              .atan((pt.getY() - prevPt.getY()) / (pt.getX() - prevPt.getX()));
      // set the observation in the matrix and change row
      currentObs.set(matrixRow, 0, currObsAngle);
      matrixRow++;

      // compute the observation for the length preservation
      double lengthIni = Math.sqrt(Math.pow(iniPt.getX() - iniPrevPt.getX(),
          2.0) + Math.pow(iniPt.getY() - iniPrevPt.getY(), 2.0));
      double lengthFin = Math.sqrt(Math.pow(
          finalPt.getX() - finalPrevPt.getX(), 2.0)
          + Math.pow(finalPt.getY() - finalPrevPt.getY(), 2.0));
      double obsLength = lengthIni * (1 - currentTime) + currentTime
          * lengthFin;
      // set the observation in the matrix
      y.set(matrixRow, 0, obsLength);
      // compute the observation for the length preservation for current line
      double currObsLength = Math.sqrt(Math.pow(pt.getX() - prevPt.getX(), 2.0)
          + Math.pow(pt.getY() - prevPt.getY(), 2.0));
      // set the observation in the matrix and change row
      currentObs.set(matrixRow, 0, currObsLength);
      matrixRow++;

      // now add the point constraint observation
      double stepDistX = (finalPt.getX() - iniPt.getX()) * time / k;
      y.set(matrixRow, 0, stepDistX);
      currentObs.set(matrixRow, 0, 0.0);
      matrixRow++;
      double stepDistY = (finalPt.getY() - iniPt.getY()) * time / k;
      y.set(matrixRow, 0, stepDistY);
      currentObs.set(matrixRow, 0, 0.0);
      matrixRow++;
    }/*
      * // add the last observation for the last segment length constraint //
      * get the counterparts in initial geom and final geom IDirectPosition
      * iniPt = mapping.getInitialCoords().get(
      * mapping.getInitialCoords().size() - 1); IDirectPosition iniPrevPt =
      * mapping.getInitialCoords().get( mapping.getInitialCoords().size() - 2);
      * IDirectPosition finalPt = mapping.getFinalCoords().get(
      * mapping.getInitialCoords().size() - 1); IDirectPosition finalPrevPt =
      * mapping.getFinalCoords().get( mapping.getInitialCoords().size() - 2); //
      * compute the observation for the length preservation double lengthIni =
      * Math.sqrt(Math.pow(iniPt.getX() - iniPrevPt.getX(), 2.0) +
      * Math.pow(iniPt.getY() - iniPrevPt.getY(), 2.0)); double lengthFin =
      * Math.sqrt(Math.pow(finalPt.getX() - finalPrevPt.getX(), 2.0) +
      * Math.pow(finalPt.getY() - finalPrevPt.getY(), 2.0)); double obsLength =
      * lengthIni * (1 - currentTime) + currentTime * lengthFin; // set the
      * observation in the matrix y.set(matrixRow, 0, obsLength);
      * 
      * // finally, the last observation for the current line IDirectPosition pt
      * = currentPoints.get(currentPoints.size() - 1); IDirectPosition prevPt =
      * currentPoints.get(currentPoints.size() - 2);
      * 
      * // compute the observation for the length preservation double
      * currObsLength = Math.sqrt(Math.pow(pt.getX() - prevPt.getX(), 2.0) +
      * Math.pow(pt.getY() - prevPt.getY(), 2.0));
      * 
      * // set the observation in the matrix currentObs.set(matrixRow, 0,
      * currObsLength);
      */

    return y;
  }

  @Override
  public Matrix getP() {
    // simple weight matrix, all constraints have the same weight
    if (p == null) {
      p = new Matrix(getA().getRowDimension(), getA().getRowDimension());
      int i = 0;
      while (i < p.getRowDimension() - 2) {
        p.set(i, i, 20);
        i++;
        p.set(i, i, 20);
        i++;
        p.set(i, i, 1);
        i++;
        p.set(i, i, 1);
        i++;
      }
      // p.set(i, i, 20);
    }
    return p;
  }

  @Override
  public Matrix getB() {
    if (b == null) {
      Matrix y = getY();
      b = y.minus(currentObs);
    }
    return b;
  }

  private boolean isAnchorPt(IDirectPosition pt) {
    for (IDirectPosition anchor : anchorPts) {
      if (anchor.equals2D(pt, 0.0001))
        return true;
    }
    return false;
  }

  public boolean isFillInterLines() {
    return fillInterLines;
  }

  public void setFillInterLines(boolean fillInterLines) {
    this.fillInterLines = fillInterLines;
  }

  public Set<IDirectPosition> getAnchorPts() {
    return anchorPts;
  }

  public void setAnchorPts(Set<IDirectPosition> anchorPts) {
    this.anchorPts = anchorPts;
  }

  public List<ILineString> getIntermediateLines() {
    return intermediateLines;
  }

  public void setIntermediateLines(List<ILineString> intermediateLines) {
    this.intermediateLines = intermediateLines;
  }

  public int getK() {
    return k;
  }

  public void setK(int k) {
    this.k = k;
  }

  public MorphingVertexMapping getMapping() {
    return mapping;
  }

  public void setMapping(MorphingVertexMapping mapping) {
    this.mapping = mapping;
  }

  public double getTime() {
    return time;
  }

  public void setTime(double time) {
    this.time = time;
  }

}
