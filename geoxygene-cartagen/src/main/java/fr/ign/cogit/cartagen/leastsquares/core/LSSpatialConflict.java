package fr.ign.cogit.cartagen.leastsquares.core;

import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

public class LSSpatialConflict {
  private LSScheduler sched;
  private boolean pToP;
  private LSPoint point1, point2;
  private LSPoint[] segment;

  public LSScheduler getSched() {
    return this.sched;
  }

  public void setSched(LSScheduler sched) {
    this.sched = sched;
  }

  public boolean isPToP() {
    return this.pToP;
  }

  public void setPToP(boolean toP) {
    this.pToP = toP;
  }

  public LSPoint getPoint1() {
    return this.point1;
  }

  public void setPoint1(LSPoint point1) {
    this.point1 = point1;
  }

  public LSPoint getPoint2() {
    return this.point2;
  }

  public void setPoint2(LSPoint point2) {
    this.point2 = point2;
  }

  public LSPoint[] getSegment() {
    return this.segment;
  }

  public void setSegment(LSPoint[] segment) {
    this.segment = segment;
  }

  @Override
  public boolean equals(Object obj) {
    LSSpatialConflict autre = (LSSpatialConflict) obj;
    if (!this.sched.equals(autre.sched)) {
      return false;
    }
    if (this.pToP != autre.pToP) {
      return false;
    }
    if (!this.point1.equals(autre.point1)) {
      return false;
    }
    if (this.pToP) {
      if (!this.point2.equals(autre.point2)) {
        return false;
      }
    } else if (!this.segment.equals(autre.segment)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return this.point1.hashCode();
  }

  @Override
  public String toString() {
    if (this.pToP) {
      return "conflit entre " + this.point1.toString() + " et "
          + this.point2.toString();
    }
    return "conflit entre " + this.point1.toString() + " et le segment "
        + this.segment.toString();
  }

  public LSSpatialConflict(LSScheduler sched, boolean toP, LSPoint point1,
      LSPoint point2, LSPoint[] segment) {
    this.sched = sched;
    this.pToP = toP;
    this.point1 = point1;
    this.point2 = point2;
    this.segment = segment;
  }

  public Set<IFeature> getObjsVoisins() {
    if (this.pToP) {
      return this.point2.getObjs();
    }
    return this.segment[0].getObjs();
  }

  /**
   * Calcule la distance du conflit.
   * 
   * @return
   */
  public double distance() {
    if (this.pToP) {
      return this.point1.getIniPt().distance2D(this.point2.getIniPt());
    }
    // on calcule la distance de point1 au segment
    // on calcule l'équation de la droite passant par point2 et point 3
    double a = 0.0, b = 1.0, c = 0.0;
    a = (this.segment[1].getIniPt().getY() - this.segment[0].getIniPt().getY())
        / (this.segment[0].getIniPt().getX() - this.segment[1].getIniPt()
            .getX());
    c = this.segment[0].getIniPt().getX()
        * (this.segment[0].getIniPt().getY() - this.segment[1].getIniPt()
            .getY())
        / (this.segment[0].getIniPt().getX() - this.segment[1].getIniPt()
            .getX()) - this.segment[0].getIniPt().getY();
    double dist = Math.abs(a * this.point1.getIniPt().getX() + b
        * this.point1.getIniPt().getY() + c)
        / Math.sqrt(a * a + b * b);
    return dist;
  }
}
