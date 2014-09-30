package fr.ign.cogit.geoxygene.matching.dst.function;

public class Constant extends Function {
  
  private double constant;
  
  public Constant(double cst) {
    super(new Function[0]);
    this.constant = cst;
  }
  
  public double f(double t) {
    return constant;
  }

}
