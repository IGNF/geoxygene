package fr.ign.cogit.geoxygene.spatial.coordgeom;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IKnot;

public class GM_Knot implements IKnot {
  double value;
  double weight;
  int multiplicity;
  @Override
  public double getValue() {
    return this.value;
  }
  public double getWeight() {
    return this.weight;
  }
  @Override
  public int getMultiplicity() {
    return this.multiplicity;
  }
  /**
   * Constructs a new knot.
   * @param value
   * @param weight
   * @param multiplicity
   */
  public GM_Knot(double value, double weight, int multiplicity) {
    super();
    this.value = value;
    this.weight = weight;
    this.multiplicity = multiplicity;
  }
}
