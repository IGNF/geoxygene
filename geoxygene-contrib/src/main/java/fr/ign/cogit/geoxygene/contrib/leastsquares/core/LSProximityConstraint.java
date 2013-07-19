/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.leastsquares.core;

import java.util.Vector;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author G. Touya
 * 
 */
public class LSProximityConstraint extends LSExternalConstraint {

  private LSSpatialConflict conflict;

  /**
   * @param pt
   * @param obj1
   * @param obj2
   * @param scheduler
   */
  public LSProximityConstraint(LSPoint pt, IFeature obj1, IFeature obj2,
      LSScheduler scheduler) {
    super(pt, obj1, obj2, scheduler);
  }

  /**
   * @param pt
   * @param obj1
   * @param obj2
   * @param scheduler
   * @param conflit : le conflit spatial qui a engendr� cette contrainte
   */
  public LSProximityConstraint(LSPoint pt, IFeature obj1, IFeature obj2,
      LSScheduler scheduler, LSSpatialConflict conflit) {
    super(pt, obj1, obj2, scheduler);
    this.conflict = conflit;
  }

  public String getNom() {
    return "CEMC_Proximite_TIN";
  }

  double tolerance = 0.2;

  /*
   * (non-Javadoc)
   */
  @Override
  public EquationsSystem calculeSystemeEquations() {

    // on v�rifie s'il y a overlap
    boolean overlap = false;
    IGeometry geom = this.getObj().getGeom();
    IGeometry geomVoisin = this.getNeighbour().getGeom();
    if (!geom.disjoint(geomVoisin)) {
      overlap = true;
    }
    // on calcule le nouveau syst�me d'�quation en fonction du type de conflit
    EquationsSystem nouveau = null;
    if (this.conflict.isPToP()) {
      nouveau = this.calculePointToPoint(this.conflict.getPoint2(), overlap);
    } else {
      LSPoint point2 = this.conflict.getSegment()[0];
      LSPoint point3 = this.conflict.getSegment()[1];
      nouveau = this.calculePointToSegment(point2, point3, overlap);
    }

    return nouveau;
  }

  private EquationsSystem calculePointToPoint(LSPoint point2, boolean overlap) {
    EquationsSystem systeme = this.sched.initSystemeLocal();

    // construction du vecteur des inconnues
    systeme.setUnknowns(new Vector<LSPoint>());
    systeme.getUnknowns().addElement(this.getPoint());
    systeme.getUnknowns().addElement(this.getPoint());
    systeme.getUnknowns().addElement(point2);
    systeme.getUnknowns().addElement(point2);

    // construction du vecteur des contraintes
    systeme.setConstraints(new Vector<LSConstraint>());
    systeme.getConstraints().add(this);

    // construction de la matrice des observations
    // c'est une matrice (1,1) contenant un 0
    systeme.initObservations(1);

    // on calcule la norme du vecteur w
    double normeW = Math.sqrt((this.getPoint().getIniPt().getX() - point2
        .getIniPt().getX())
        * (this.getPoint().getIniPt().getX() - point2.getIniPt().getX())
        + (this.getPoint().getIniPt().getY() - point2.getIniPt().getY())
        * (this.getPoint().getIniPt().getY() - point2.getIniPt().getY()));
    // on calcule dist_min, la constante de l'équation
    double dist_min = 0.0;
    if (normeW < (this.seuilSep * this.sched.getMapspec().getEchelle() / 1000.0
        + this.getPoint().getSymbolWidth() + point2.getSymbolWidth())) {
      if (overlap) {
        dist_min = this.seuilSep * this.sched.getMapspec().getEchelle()
            / 1000.0 + this.getPoint().getSymbolWidth()
            + point2.getSymbolWidth() + normeW;
      } else {
        dist_min = this.seuilSep * this.sched.getMapspec().getEchelle()
            / 1000.0 + this.getPoint().getSymbolWidth()
            + point2.getSymbolWidth() - normeW;
      }
    }
    systeme.setObs(0, dist_min);

    // calcul des facteurs de l'équation sur les angles
    double a = 0.0, b = 0.0, c = 0.0, d = 0.0;
    a = (this.getPoint().getIniPt().getX() - point2.getIniPt().getX()) / normeW;
    b = (this.getPoint().getIniPt().getY() - point2.getIniPt().getY()) / normeW;
    c = (point2.getIniPt().getX() - this.getPoint().getIniPt().getX()) / normeW;
    d = (point2.getIniPt().getY() - this.getPoint().getIniPt().getY()) / normeW;

    // construction de la matrice A
    systeme.initMatriceA(1, 4);
    systeme.setA(0, 0, a);
    systeme.setA(0, 1, b);
    systeme.setA(0, 2, c);
    systeme.setA(0, 3, d);
    systeme.setNonNullValues(4);

    return systeme;
  }

  private EquationsSystem calculePointToSegment(LSPoint point2, LSPoint point3,
      boolean overlap) {
    EquationsSystem systeme = this.sched.initSystemeLocal();

    // construction du vecteur des inconnues
    systeme.setUnknowns(new Vector<LSPoint>());
    systeme.getUnknowns().addElement(this.getPoint());
    systeme.getUnknowns().addElement(this.getPoint());
    systeme.getUnknowns().addElement(point2);
    systeme.getUnknowns().addElement(point2);
    systeme.getUnknowns().addElement(point3);
    systeme.getUnknowns().addElement(point3);

    // construction du vecteur des contraintes
    systeme.setConstraints(new Vector<LSConstraint>());
    systeme.getConstraints().add(this);

    // construction de la matrice des observations
    // c'est une matrice (1,1) contenant un 0
    systeme.initObservations(1);

    // on calcule l'équation de la droite passant par point2 et point 3
    double a = 0.0, b = 1.0, c = 0.0;
    a = (point3.getIniPt().getY() - point2.getIniPt().getY())
        / (point2.getIniPt().getX() - point3.getIniPt().getX());
    c = point2.getIniPt().getX()
        * (point2.getIniPt().getY() - point3.getIniPt().getY())
        / (point2.getIniPt().getX() - point3.getIniPt().getX())
        - point2.getIniPt().getY();

    // calcul des facteurs de l'équation par approximation des dérivées
    // on utilise la méthode quotients différentiels centrés pour approximer
    // les dérivées partielles trop compliquées à calculer sinon
    double u = 0.0, v = 0.0, w = 0.0, d = 0.0, e = 0.0, f = 0.0;
    double h = 0.001;
    u = (Math.abs(a * (this.getPoint().getIniPt().getX() + h) + b
        * this.getPoint().getIniPt().getY() + c) - Math.abs(a
        * (this.getPoint().getIniPt().getX() - h) + b
        * this.getPoint().getIniPt().getY() + c))
        / Math.sqrt(a * a + b * b) * 2 * h;

    v = (Math.abs(a * this.getPoint().getIniPt().getX() + b
        * (this.getPoint().getIniPt().getY() + h) + c) - Math.abs(a
        * this.getPoint().getIniPt().getX() + b
        * (this.getPoint().getIniPt().getY() - h) + c))
        / Math.sqrt(a * a + b * b) * 2 * h;

    w = 1
        / (2 * h)
        * (Math.abs(this.getPoint().getIniPt().getY()
            + this.getPoint().getIniPt().getX()
            * (point3.getIniPt().getY() - point2.getIniPt().getY())
            / (point2.getIniPt().getX() + h - point3.getIniPt().getX())
            - point2.getIniPt().getY() + (point2.getIniPt().getX() + h)
            * (point2.getIniPt().getY() - point3.getIniPt().getY())
            / (point2.getIniPt().getX() + h - point3.getIniPt().getX()))
            / Math
                .sqrt((point3.getIniPt().getY() - point2.getIniPt().getY())
                    * (point3.getIniPt().getY() - point2.getIniPt().getY())
                    / ((point2.getIniPt().getX() + h - point3.getIniPt().getX()) * (point2
                        .getIniPt().getX() + h - point3.getIniPt().getX())) + 1) - Math
            .abs(this.getPoint().getIniPt().getY()
                + this.getPoint().getIniPt().getX()
                * (point3.getIniPt().getY() - point2.getIniPt().getY())
                / (point2.getIniPt().getX() - h - point3.getIniPt().getX())
                - point2.getIniPt().getY() + (point2.getIniPt().getX() - h)
                * (point2.getIniPt().getY() - point3.getIniPt().getY())
                / (point2.getIniPt().getX() - h - point3.getIniPt().getX()))
            / Math
                .sqrt((point3.getIniPt().getY() - point2.getIniPt().getY())
                    * (point3.getIniPt().getY() - point2.getIniPt().getY())
                    / ((point2.getIniPt().getX() - h - point3.getIniPt().getX()) * (point2
                        .getIniPt().getX() - h - point3.getIniPt().getX())) + 1));

    d = 1
        / (2 * h)
        * (Math.abs(this.getPoint().getIniPt().getY()
            + this.getPoint().getIniPt().getX()
            * (point3.getIniPt().getY() - (point2.getIniPt().getY() + h))
            / (point2.getIniPt().getX() - point3.getIniPt().getX())
            - point2.getIniPt().getY() - h + point2.getIniPt().getX()
            * (point2.getIniPt().getY() + h - point3.getIniPt().getY())
            / (point2.getIniPt().getX() - point3.getIniPt().getX()))
            / Math
                .sqrt((point3.getIniPt().getY() - (point2.getIniPt().getY() + h))
                    * (point3.getIniPt().getY() - (point2.getIniPt().getY() + h))
                    / ((point2.getIniPt().getX() - point3.getIniPt().getX()) * (point2
                        .getIniPt().getX() - point3.getIniPt().getX())) + 1) - Math
            .abs(this.getPoint().getIniPt().getY()
                + this.getPoint().getIniPt().getX()
                * (point3.getIniPt().getY() - (point2.getIniPt().getY() - h))
                / (point2.getIniPt().getX() - point3.getIniPt().getX())
                - point2.getIniPt().getY() + h + point2.getIniPt().getX()
                * (point2.getIniPt().getY() - h - point3.getIniPt().getY())
                / (point2.getIniPt().getX() - point3.getIniPt().getX()))
            / Math
                .sqrt((point3.getIniPt().getY() - (point2.getIniPt().getY() - h))
                    * (point3.getIniPt().getY() - (point2.getIniPt().getY() - h))
                    / ((point2.getIniPt().getX() - point3.getIniPt().getX()) * (point2
                        .getIniPt().getX() - point3.getIniPt().getX())) + 1));

    e = 1
        / (2 * h)
        * (Math.abs(this.getPoint().getIniPt().getY()
            + this.getPoint().getIniPt().getX()
            * (point3.getIniPt().getY() - point2.getIniPt().getY())
            / (point2.getIniPt().getX() - (point3.getIniPt().getX() + h))
            - point2.getIniPt().getY() + point2.getIniPt().getX()
            * (point2.getIniPt().getY() - point3.getIniPt().getY())
            / (point2.getIniPt().getX() - (point3.getIniPt().getX() + h)))
            / Math
                .sqrt((point3.getIniPt().getY() - point2.getIniPt().getY())
                    * (point3.getIniPt().getY() - point2.getIniPt().getY())
                    / ((point2.getIniPt().getX() - (point3.getIniPt().getX() + h)) * (point2
                        .getIniPt().getX() - (point3.getIniPt().getX() + h)))
                    + 1) - Math.abs(this.getPoint().getIniPt().getY()
            + this.getPoint().getIniPt().getX()
            * (point3.getIniPt().getY() - point2.getIniPt().getY())
            / (point2.getIniPt().getX() - (point3.getIniPt().getX() - h))
            - point2.getIniPt().getY() + point2.getIniPt().getX()
            * (point2.getIniPt().getY() - point3.getIniPt().getY())
            / (point2.getIniPt().getX() - (point3.getIniPt().getX() - h)))
            / Math
                .sqrt((point3.getIniPt().getY() - point2.getIniPt().getY())
                    * (point3.getIniPt().getY() - point2.getIniPt().getY())
                    / ((point2.getIniPt().getX() - (point3.getIniPt().getX() - h)) * (point2
                        .getIniPt().getX() - (point3.getIniPt().getX() - h)))
                    + 1));

    f = 1
        / (2 * h)
        * (Math.abs(this.getPoint().getIniPt().getY()
            + this.getPoint().getIniPt().getX()
            * (point3.getIniPt().getY() + h - point2.getIniPt().getY())
            / (point2.getIniPt().getX() - point3.getIniPt().getX())
            - point2.getIniPt().getY() + point2.getIniPt().getX()
            * (point2.getIniPt().getY() - (point3.getIniPt().getY() + h))
            / (point2.getIniPt().getX() - point3.getIniPt().getX()))
            / Math
                .sqrt((point3.getIniPt().getY() + h - point2.getIniPt().getY())
                    * (point3.getIniPt().getY() + h - point2.getIniPt().getY())
                    / ((point2.getIniPt().getX() - point3.getIniPt().getX()) * (point2
                        .getIniPt().getX() - point3.getIniPt().getX())) + 1) - Math
            .abs(this.getPoint().getIniPt().getY()
                + this.getPoint().getIniPt().getX()
                * (point3.getIniPt().getY() - h - point2.getIniPt().getY())
                / (point2.getIniPt().getX() - point3.getIniPt().getX())
                - point2.getIniPt().getY() + point2.getIniPt().getX()
                * (point2.getIniPt().getY() - (point3.getIniPt().getY() - h))
                / (point2.getIniPt().getX() - point3.getIniPt().getX()))
            / Math
                .sqrt((point3.getIniPt().getY() - h - point2.getIniPt().getY())
                    * (point3.getIniPt().getY() - point2.getIniPt().getY() - h)
                    / ((point2.getIniPt().getX() - point3.getIniPt().getX()) * (point2
                        .getIniPt().getX() - point3.getIniPt().getX())) + 1));

    double dist = Math.abs(a * this.getPoint().getIniPt().getX() + b
        * this.getPoint().getIniPt().getY() + c)
        / Math.sqrt(a * a + b * b);
    double dist_min = 0.0;
    if (overlap) {
      dist_min = this.seuilSep * this.sched.getMapspec().getEchelle() / 1000.0
          + this.getPoint().getSymbolWidth() + point2.getSymbolWidth() + dist;
    } else {
      dist_min = this.seuilSep * this.sched.getMapspec().getEchelle() / 1000.0
          + this.getPoint().getSymbolWidth() + point2.getSymbolWidth() - dist;
    }
    if (dist_min < 0.0) {
      dist_min = 0.0;
    }

    systeme.setObs(0, dist_min);

    // construction de la matrice A
    systeme.initMatriceA(1, 6);
    systeme.setA(0, 0, u);
    systeme.setA(0, 1, v);
    systeme.setA(0, 2, w);
    systeme.setA(0, 3, d);
    systeme.setA(0, 4, e);
    systeme.setA(0, 5, f);
    systeme.setNonNullValues(6);

    return systeme;
  }

}
