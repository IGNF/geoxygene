/*
 * Cr�� le 26 mars 2008
 * 
 * Pour changer le mod�le de ce fichier g�n�r�, allez � :
 * Fen�tre&gt;Pr�f�rences&gt;Java&gt;G�n�ration de code&gt;Code et commentaires
 */
package fr.ign.cogit.cartagen.leastsquares.core;

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
