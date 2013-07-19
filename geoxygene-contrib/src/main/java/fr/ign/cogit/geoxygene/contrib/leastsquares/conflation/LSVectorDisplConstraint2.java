package fr.ign.cogit.geoxygene.contrib.leastsquares.conflation;

import java.util.Vector;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.EquationsSystem;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSConstraint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSPoint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSScheduler;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

/**
 * This constraint is useful in conflation processes as it conflates a given
 * {@link LSPoint} according to one of the displacement vectors created through
 * data matching. The displacement it constrains decreases proportionally with
 * the inverse of the squared norm of the displacement vector.
 * @author GTouya
 * 
 */
public class LSVectorDisplConstraint2 extends LSVectorDisplConstraint {

  public LSVectorDisplConstraint2(LSPoint pt, IFeature obj1, IFeature obj2,
      LSScheduler scheduler) {
    super(pt, obj1, obj2, scheduler);
  }

  public LSVectorDisplConstraint2(LSPoint pt, IFeature obj1, IFeature obj2,
      LSScheduler scheduler, DisplacementVector vector) {
    super(pt, obj1, obj2, scheduler, vector);
  }

  @Override
  public EquationsSystem calculeSystemeEquations() {
    EquationsSystem systeme = this.sched.initSystemeLocal();
    // construction du vecteur des inconnues
    systeme.setUnknowns(new Vector<LSPoint>());
    systeme.getUnknowns().addElement(getVector().getConflatedPoint());
    systeme.getUnknowns().addElement(getVector().getConflatedPoint());

    // construction du vecteur des contraintes
    systeme.setConstraints(new Vector<LSConstraint>());
    for (int i = 0; i < 2; i++) {
      systeme.getConstraints().add(this);
    }

    // construction de la matrice des observations
    // c'est une matrice (2,1) contenant deux 0
    Vector2D vect = this.getVector().inverseSquareProjection();
    systeme.initObservations(2);
    systeme.setObs(0, vect.getX());
    systeme.setObs(1, vect.getY());

    // construction de la matrice A
    // ici, les équations sont simples : Delta(x) = vect.getX() et Delta(y) =
    // vect.getY()
    systeme.initMatriceA(2, 2);
    systeme.setA(0, 0, 1.0);
    systeme.setA(1, 1, 1.0);
    systeme.setNonNullValues(2);

    return systeme;
  }

}
