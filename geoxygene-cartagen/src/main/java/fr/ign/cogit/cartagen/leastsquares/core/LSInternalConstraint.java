/*
 * Cr�� le 26 mars 2008
 * 
 * Pour changer le mod�le de ce fichier g�n�r�, allez � :
 * Fen�tre&gt;Pr�f�rences&gt;Java&gt;G�n�ration de code&gt;Code et commentaires
 */
package fr.ign.cogit.cartagen.leastsquares.core;

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
