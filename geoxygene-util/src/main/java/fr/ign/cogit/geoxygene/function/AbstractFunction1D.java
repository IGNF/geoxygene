package fr.ign.cogit.geoxygene.function;

/** 
 *
 */
public abstract class AbstractFunction1D implements Function1D {
  
  /** The lower bound of the domain of the function. */
  private double lowerBoundDF;
  
  /** The upper bound of the domain of the function. */
  private double upperBoundDF;
  
  /** If lower bound is accepted in domain of the function. */
  private boolean withMatchLowerBound;
  
  /** If upper bound is accepted in domain of the function. */
  private boolean withMatchUpperBound;

  @Override
  public abstract String help();

  @Override
  public void setDomainOfFunction(Double binf, double bsup, boolean minf, boolean msup) {
    this.lowerBoundDF = binf;
    this.upperBoundDF = bsup;
    this.withMatchLowerBound = minf;
    this.withMatchUpperBound = msup;
  }
  
  @Override
  public double getLowerBoundDF() {
    return this.lowerBoundDF;
  }

  @Override
  public double getUpperBoundDF() {
    return this.upperBoundDF;
  }

  /**
   * TODO : il y a surement mieux.
   */ 
  @Override
  public boolean isBetween(double d) {
    if (withMatchLowerBound) {
      if (withMatchUpperBound) {
        return (this.lowerBoundDF <= d && d <= this.upperBoundDF);
      } else {
        return (this.lowerBoundDF <= d && d < this.upperBoundDF);
      }
    } else {
      if (withMatchUpperBound) {
        return (this.lowerBoundDF < d && d <= this.upperBoundDF);
      } else {
        return (this.lowerBoundDF < d && d < this.upperBoundDF);
      }
    } 
  }

}
