/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.leastsquares.core;

import java.util.Vector;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;

/**
 * @author G. Touya
 * 
 *         A constraint specific to network intersections as such points are not
 *         constrained by curvature and movement direction constraints.
 * 
 */
public class LSCrossingConstraint extends LSInternalConstraint {

  private double weightFactor = 1.0;

  /**
   * True if the constraint is applicable on point.
   * @param point
   * @return
   */
  public static boolean appliesTo(LSPoint point) {
    if (point.isCrossing()) {
      return true;
    }
    return false;
  }

  public LSCrossingConstraint(LSPoint pt, LSScheduler scheduler) {
    super(pt, scheduler);
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.gothic.cogit.guillaume.moindresCarres.ContrainteInterneMC#
   * calculeSystemeEquations(gothic.main.GothicObject,
   * fr.ign.gothic.cogit.guillaume.moindresCarres.MCPoint)
   */
  @Override
  public EquationsSystem calculeSystemeEquations(IFeature obj, LSPoint point) {

    // ******************************************************************
    // initialisation du système
    EquationsSystem system = null;

    // ******************************************************************
    // on commence par faire une boucle sur tous les objets qui contiennent
    // point
    IFeature previous = null;
    LSPoint previousPt = null;
    double totalWeightFactor = 0.0;
    for (IFeature feat : point.getObjs()) {
      ILineString geom = (ILineString) LineDensification.densification2(
          feat.getGeom(), this.sched.getMapspec().getDensStep());
      if (previous == null) {
        previous = feat;
        if (point.getIniPt().equals(geom.coord().get(0)))
          previousPt = sched.getPointFromCoord(geom.coord().get(1), feat);
        else
          previousPt = sched.getPointFromCoord(
              geom.coord().get(geom.coord().size() - 2), feat);
        continue;
      }
      // build a new system for this pair of features
      EquationsSystem localSystem = initialiseSystem(point);

      LSPoint currentPt = sched.getPointFromCoord(
          geom.coord().get(geom.coord().size() - 2), feat);
      if (point.getIniPt().equals(geom.coord().get(0)))
        currentPt = sched.getPointFromCoord(geom.coord().get(1), feat);
      // System.out.println("point courant: " + point);
      // System.out.println("point prec: " + previousPt);
      // System.out.println("point suiv: " + currentPt);
      // now compute angle preservation constraint between previousPt, point and
      // currentPt
      // calcul des facteurs de l'équation sur les angles
      double a = 0.0, b = 0.0, c = 0.0, d = 0.0, e = 0.0, f = 0.0;
      double normeU = Math.sqrt((point.getIniPt().getX() - previousPt
          .getIniPt().getX())
          * (point.getIniPt().getX() - previousPt.getIniPt().getX())
          + (point.getIniPt().getY() - previousPt.getIniPt().getY())
          * (point.getIniPt().getY() - previousPt.getIniPt().getY()));
      double normeW = Math.sqrt((currentPt.getIniPt().getX() - point.getIniPt()
          .getX())
          * (currentPt.getIniPt().getX() - point.getIniPt().getX())
          + (currentPt.getIniPt().getY() - point.getIniPt().getY())
          * (currentPt.getIniPt().getY() - point.getIniPt().getY()));
      a = (point.getIniPt().getY() - currentPt.getIniPt().getY())
          / (normeU * normeW);
      b = (-point.getIniPt().getX() + currentPt.getIniPt().getX())
          / (normeU * normeW);
      c = (-previousPt.getIniPt().getY() + currentPt.getIniPt().getY())
          / (normeU * normeW);
      d = (previousPt.getIniPt().getX() - currentPt.getIniPt().getX())
          / (normeU * normeW);
      e = (previousPt.getIniPt().getY() - point.getIniPt().getY())
          / (normeU * normeW);
      f = (-previousPt.getIniPt().getX() + point.getIniPt().getX())
          / (normeU * normeW);

      if (!point.isFixed()) {
        if (previousPt.isFixed() && currentPt.isFixed()) {
          // update the unknowns vector: no additional unknown in this case.
          // fill the matrix A
          localSystem.initMatriceA(1, 2);
          localSystem.setA(0, 0, c);
          localSystem.setA(0, 1, d);
          localSystem.setObs(0,
              -a * previousPt.getDeltaX() - b * previousPt.getDeltaY() - e
                  * currentPt.getDeltaX() - f * currentPt.getDeltaY());
        } else if (previousPt.isFixed()) {
          // update the unknowns vector
          if (!localSystem.getUnknowns().contains(currentPt)) {
            localSystem.getUnknowns().addElement(currentPt);
            localSystem.getUnknowns().addElement(currentPt);
          }
          // fill the matrix A
          localSystem.initMatriceA(1, 4);
          localSystem.setA(0, 0, c);
          localSystem.setA(0, 1, d);
          localSystem.setA(0, 2, e);
          localSystem.setA(0, 3, f);
          localSystem.setObs(0,
              -a * previousPt.getDeltaX() - b * previousPt.getDeltaY());
        } else if (currentPt.isFixed()) {
          // update the unknowns vector
          if (!localSystem.getUnknowns().contains(previousPt)) {
            localSystem.getUnknowns().addElement(previousPt);
            localSystem.getUnknowns().addElement(previousPt);
          }
          // fill the matrix A
          localSystem.initMatriceA(1, 4);
          localSystem.setA(0, 0, c);
          localSystem.setA(0, 1, d);
          localSystem.setA(0, 2, a);
          localSystem.setA(0, 3, b);
          double obs = -e * currentPt.getDeltaX() - f * currentPt.getDeltaY();
          localSystem.setObs(0, obs);
        } else {
          // update the unknowns vector
          if (!localSystem.getUnknowns().contains(previousPt)) {
            localSystem.getUnknowns().addElement(previousPt);
            localSystem.getUnknowns().addElement(previousPt);
          }
          if (!localSystem.getUnknowns().contains(currentPt)) {
            localSystem.getUnknowns().addElement(currentPt);
            localSystem.getUnknowns().addElement(currentPt);
          }
          // fill the matrix A
          localSystem.initMatriceA(1, 6);
          localSystem.setA(0, 2, a);
          localSystem.setA(0, 3, b);
          localSystem.setA(0, 0, c);
          localSystem.setA(0, 1, d);
          localSystem.setA(0, 4, e);
          localSystem.setA(0, 5, f);
        }
      } else {
        if (previousPt.isFixed() && currentPt.isFixed()) {
          // all points are fixed there, so no equation added
          localSystem = null;
        } else if (previousPt.isFixed()) {
          // update the unknowns vector
          if (!localSystem.getUnknowns().contains(currentPt)) {
            localSystem.getUnknowns().addElement(currentPt);
            localSystem.getUnknowns().addElement(currentPt);
          }
          // fill the matrix A
          localSystem.initMatriceA(1, 2);
          localSystem.setA(0, 0, e);
          localSystem.setA(0, 1, f);
          localSystem.setObs(0,
              -a * previousPt.getDeltaX() - b * previousPt.getDeltaY() - c
                  * point.getDeltaX() - d * point.getDeltaY());
        } else if (currentPt.isFixed()) {
          // update the unknowns vector
          if (!localSystem.getUnknowns().contains(previousPt)) {
            localSystem.getUnknowns().addElement(previousPt);
            localSystem.getUnknowns().addElement(previousPt);
          }
          // fill the matrix A
          localSystem.initMatriceA(1, 2);
          localSystem.setA(0, 0, a);
          localSystem.setA(0, 1, b);
          double obs = -e * currentPt.getDeltaX() - f * currentPt.getDeltaY()
              - c * point.getDeltaX() - d * point.getDeltaY();
          localSystem.setObs(0, obs);
        } else {
          // update the unknowns vector
          if (!localSystem.getUnknowns().contains(previousPt)) {
            localSystem.getUnknowns().addElement(previousPt);
            localSystem.getUnknowns().addElement(previousPt);
          }
          if (!localSystem.getUnknowns().contains(currentPt)) {
            localSystem.getUnknowns().addElement(currentPt);
            localSystem.getUnknowns().addElement(currentPt);
          }
          // fill the matrix A
          localSystem.initMatriceA(1, 4);
          localSystem.setA(0, 0, a);
          localSystem.setA(0, 1, b);
          localSystem.setA(0, 2, e);
          localSystem.setA(0, 3, f);
          double obs = -c * point.getDeltaX() - d * point.getDeltaY();
          localSystem.setObs(0, obs);
        }
      }
      if (system == null)
        system = localSystem;
      else
        system = system.assemble(localSystem);
      previousPt = currentPt;
      previous = feat;
      totalWeightFactor += previousPt.getIniPt().distance2D(point.getIniPt())
          * currentPt.getIniPt().distance2D(point.getIniPt());
    }
    this.weightFactor = totalWeightFactor / (point.getObjs().size() - 1);
    // System.out.println("observations du système: " + system.getObsVector());
    // System.out.println(system);
    return system;
  }

  private EquationsSystem initialiseSystem(LSPoint point) {
    EquationsSystem systeme = this.sched.initSystemeLocal();
    // construction du vecteur des inconnues
    systeme.setUnknowns(new Vector<LSPoint>());
    if (!point.isFixed()) {
      systeme.getUnknowns().addElement(point);
      systeme.getUnknowns().addElement(point);
    }
    // construction du vecteur des contraintes
    systeme.setConstraints(new Vector<LSConstraint>());
    systeme.getConstraints().add(this);
    systeme.initObservations(1);
    return systeme;
  }

  @Override
  public double getWeightFactor() {
    return weightFactor;
  }

}
