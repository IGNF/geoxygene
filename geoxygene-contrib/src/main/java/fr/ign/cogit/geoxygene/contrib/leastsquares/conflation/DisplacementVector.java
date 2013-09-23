package fr.ign.cogit.geoxygene.contrib.leastsquares.conflation;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.contrib.conflation.ConflationVector;
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
  private ConflationVector conflationVector;

  public ConflationVector getConflationVector() {
    return conflationVector;
  }

  public void setConflationVector(ConflationVector conflationVector) {
    this.conflationVector = conflationVector;
  }

  public DisplacementVector(LSPoint pointIni, LSPoint conflatedPoint,
      Vector2D vector) {
    super();
    this.pointIni = pointIni;
    this.setConflatedPoint(conflatedPoint);
    IDirectPosition conflPos = conflatedPoint.getIniPt();
    IDirectPosition iniPos = null;
    if (pointIni == null)
      iniPos = vector.opposite().translate(conflPos);
    else
      iniPos = pointIni.getIniPt();
    this.conflationVector = new ConflationVector(iniPos, vector);

  }

  public LSPoint getPointIni() {
    return pointIni;
  }

  public void setPointIni(LSPoint point) {
    this.pointIni = point;
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
    return this.conflationVector.linearProjection();
  }

  /**
   * Compute the value of the vector field created by {@code this} at a given LS
   * point, with an inverse square projection method.
   * @return
   */
  public Vector2D inverseSquareProjection() {
    return this.conflationVector.inverseSquareProjection();
  }

}
