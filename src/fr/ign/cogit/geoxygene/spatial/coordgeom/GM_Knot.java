package fr.ign.cogit.geoxygene.spatial.coordgeom;

public class GM_Knot {
  double value;
  double weight;
  int multiplicity;

  public double getValue() {
    return this.value;
  }

  public double getWeight() {
    return this.weight;
  }

  public int getMultiplicity() {
    return this.multiplicity;
  }

  public GM_Knot(double value, double weight, int multiplicity) {
    super();
    this.value = value;
    this.weight = weight;
    this.multiplicity = multiplicity;
  }
}
