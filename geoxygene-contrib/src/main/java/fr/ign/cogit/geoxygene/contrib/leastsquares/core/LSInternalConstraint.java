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
 * @author G. Touya
 * 
 */
public abstract class LSInternalConstraint extends LSConstraint {

  protected LSInternalConstraint(LSPoint pt, LSScheduler scheduler) {
    super();
    this.setPoint(pt);
    this.sched = scheduler;
  }

  public abstract EquationsSystem calculeSystemeEquations(IFeature obj,
      LSPoint point);

  /**
   * @param contrainte
   */
  public boolean equals(LSInternalConstraint contrainte) {
    return false;
  }

}// interface ContrainteInterneMC
