package fr.ign.cogit.geoxygene.contrib.conflation;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

public class ConflationVector {

  private IDirectPosition iniPos, conflPos;
  private Vector2D vector;

  public ConflationVector(IDirectPosition iniPos, Vector2D vector) {
    super();
    this.iniPos = iniPos;
    this.vector = vector;
    this.conflPos = vector.translate(iniPos);
  }

  public IDirectPosition getIniPos() {
    return iniPos;
  }

  public void setIniPos(IDirectPosition iniPos) {
    this.iniPos = iniPos;
  }

  public IDirectPosition getConflPos() {
    return conflPos;
  }

  public void setConflPos(IDirectPosition conflPos) {
    this.conflPos = conflPos;
  }

  public Vector2D getVector() {
    return vector;
  }

  public void setVector(Vector2D vector) {
    this.vector = vector;
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

  @Override
  public String toString() {
    return this.iniPos.toString() + " (" + vector.getX() + ", " + vector.getY()
        + ")";
  }

}
