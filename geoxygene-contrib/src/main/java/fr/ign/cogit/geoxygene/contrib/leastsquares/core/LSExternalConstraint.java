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

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * @author moi
 * 
 * 
 */
public abstract class LSExternalConstraint extends LSConstraint {

  private IFeature obj;
  private IFeature neighbour;
  double seuilSep;

  public LSExternalConstraint(LSPoint pt, IFeature obj1, IFeature obj2,
      LSScheduler scheduler) {
    super();
    setPoint(pt);
    setObj(obj1);
    setNeighbour(obj2);
    sched = scheduler;
  }

  /**
   * Utile pour l'interface de param�trage
   */
  public LSExternalConstraint() {
    super();
  }

  public EquationsSystem calculeSystemeEquations() {
    return null;
  }

  public void setSeuilSep(double seuil) {
    seuilSep = seuil;
  }

  public IFeature getObj() {
    return obj;
  }

  public void setObj(IFeature obj) {
    this.obj = obj;
  }

  public IFeature getNeighbour() {
    return neighbour;
  }

  public void setNeighbour(IFeature neighbour) {
    this.neighbour = neighbour;
  }

}// interface ContrainteExterneMC
