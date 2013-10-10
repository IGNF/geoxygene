package fr.ign.cogit.evidence.delayedmassvaluation;

public interface MassFunctor<E> {
  double evaluate(E event);
}
