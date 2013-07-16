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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract class for Least Squares constraints.
 * @author GTouya
 * 
 */
public abstract class LSConstraint {
  // le point sur lequel porte cet objet contrainte externe
  private LSPoint point;
  protected LSScheduler sched;
  private int id;
  private static AtomicInteger counter = new AtomicInteger();

  public LSConstraint() {
    id = counter.getAndIncrement();
  }

  public void setPoint(LSPoint point) {
    this.point = point;
  }

  public LSPoint getPoint() {
    return point;
  }

  /**
   * Cette méthode renvoie le poids relatif de cette contrainte par rapport aux
   * autres contraintes de cette classe. Renvoie 1 par défaut ce qui signifie
   * que toutes les contraintes ont le même poids.
   * @return
   */
  public double getWeightFactor() {
    return 1.0;
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
    LSConstraint other = (LSConstraint) obj;
    if (point == null) {
      if (other.point != null)
        return false;
    } else if (!point.equals(other.point))
      return false;
    return true;
  }

}
