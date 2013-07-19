package fr.ign.cogit.geoxygene.contrib.leastsquares.conflation;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSPoint;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

/**
 * A displacement vector instance manages the impact of a displacement vector
 * created by matching, on the conflation of {@link LSPoint}
 * @author GTouya
 * 
 */
public class DisplacementVector {

  private LSPoint pointIni, conflatedPoint;
  private IDirectPosition iniPos, conflPos;
  private Vector2D vector;

  public DisplacementVector(LSPoint pointIni, LSPoint conflatedPoint,
      Vector2D vector) {
    super();
    this.pointIni = pointIni;
    this.setConflatedPoint(conflatedPoint);
    this.vector = vector;
    this.conflPos = conflatedPoint.getIniPt();
    if (pointIni == null)
      this.iniPos = vector.opposite().translate(conflPos);
    else
      this.iniPos = pointIni.getIniPt();
  }

  public LSPoint getPointIni() {
    return pointIni;
  }

  public void setPointIni(LSPoint point) {
    this.pointIni = point;
  }

  public Vector2D getVector() {
    return vector;
  }

  public void setVector(Vector2D vector) {
    this.vector = vector;
  }

  public LSPoint getConflatedPoint() {
    return conflatedPoint;
  }

  public void setConflatedPoint(LSPoint conflatedPoint) {
    this.conflatedPoint = conflatedPoint;
  }

  /**
   * Compute the value of the vector field created by {@code this} at a given LS
   * point, with a linear projection method.
   * @return
   */
  public Vector2D linearProjection() {
    double dist = iniPos.distance2D(conflPos);
    double newNorm = vector.norme() - dist / (4 * vector.norme());
    Vector2D vect = vector.changeNorm(newNorm);
    return vect;
  }

  /**
   * Compute the value of the vector field created by {@code this} at a given LS
   * point, with an inverse square projection method.
   * @return
   */
  public Vector2D inverseSquareProjection() {
    double dist = iniPos.distance2D(conflPos);
    double newNorm = vector.norme() / (dist * dist);
    Vector2D vect = vector.changeNorm(newNorm);
    return vect;
  }

}
