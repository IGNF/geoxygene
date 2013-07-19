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

import java.util.Vector;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * @author G. Touya
 * 
 */
public class LSMovementConstraint extends LSInternalConstraint {

  /**
   * @param point
   */
  public static Boolean appliesTo(LSPoint point) {
    if (point.isFixed())
      return false;
    return true;
  }

  public LSMovementConstraint(LSPoint pt, LSScheduler scheduler) {
    super(pt, scheduler);
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.gothic.cogit.guillaume.moindresCarres.ContrainteInterneMC#
   * calculeSystemeEquations(gothic.main.GothicObject,
   * fr.ign.gothic.cogit.guillaume.moindresCarres.MCPoint)
   */
  @Override
  public EquationsSystem calculeSystemeEquations(IFeature obj, LSPoint point) {

    EquationsSystem systeme = this.sched.initSystemeLocal();
    // construction du vecteur des inconnues
    systeme.setUnknowns(new Vector<LSPoint>());
    systeme.getUnknowns().addElement(point);
    systeme.getUnknowns().addElement(point);

    // construction du vecteur des contraintes
    systeme.setConstraints(new Vector<LSConstraint>());
    for (int i = 0; i < 2; i++) {
      systeme.getConstraints().add(this);
    }

    // construction de la matrice des observations
    // c'est une matrice (2,1) contenant deux 0
    systeme.initObservations(2);

    // construction de la matrice A
    // ici, les équations sont simples : Delta(x) = 0 et Delta(y) = 0
    systeme.initMatriceA(2, 2);
    systeme.setA(0, 0, 1.0);
    systeme.setA(1, 1, 1.0);
    systeme.setNonNullValues(2);

    return systeme;
  }

}
