package fr.ign.cogit.evidence.delayedmassvaluation;

public class MassValue<E> implements MassFunctor<E> {
  double value;
  public MassValue(double value) {
    this.value = value;
  }
  @Override
  public double evaluate(E event) {
    return this.value;
  }
}
