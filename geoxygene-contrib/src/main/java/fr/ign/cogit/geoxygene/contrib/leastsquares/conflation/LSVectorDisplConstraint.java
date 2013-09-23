package fr.ign.cogit.geoxygene.contrib.leastsquares.conflation;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSExternalConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSPoint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSScheduler;

/**
 * Abstract class for conflation constraints based on a matching displacement
 * vector.
 * @author GTouya
 * 
 */
public abstract class LSVectorDisplConstraint extends LSExternalConstraint {

  private DisplacementVector vector;

  /**
   * @param pt
   * @param obj1
   * @param obj2
   * @param scheduler
   */
  public LSVectorDisplConstraint(LSPoint pt, IFeature obj1, IFeature obj2,
      LSScheduler scheduler) {
    super(pt, obj1, obj2, scheduler);
  }

  /**
   * @param pt
   * @param obj1
   * @param obj2
   * @param scheduler
   * @param conflit : the displacement vector that created {@code this}
   *          constraint.
   */
  public LSVectorDisplConstraint(LSPoint pt, IFeature obj1, IFeature obj2,
      LSScheduler scheduler, DisplacementVector vector) {
    super(pt, obj1, obj2, scheduler);
    this.setVector(vector);
  }

  public String getNom() {
    return "LSVectorDisplConstraint";
  }

  public DisplacementVector getVector() {
    return vector;
  }

  public void setVector(DisplacementVector vector) {
    this.vector = vector;
  }

  @Override
  public double getWeightFactor() {
    return vector.getConflationVector().getVector().norme();
  }

}
