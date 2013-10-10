package fr.ign.cogit.evidence.delayedmassvaluation;

public class MassComplement<E> implements MassFunctor<E> {
  MassFunctor<E> m;
  public MassComplement(MassFunctor<E> m) {
    this.m = m;
  }
  @Override
  public double evaluate(E event) {
    return 1d - this.m.evaluate(event);
  }
}
