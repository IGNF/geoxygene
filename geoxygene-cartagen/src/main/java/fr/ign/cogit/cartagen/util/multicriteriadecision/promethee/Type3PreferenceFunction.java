/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util.multicriteriadecision.promethee;

/**
 * Type 3 preference function from Brans & Mareschal 2005, it's a function
 * giving 0 for negative (or zero) deviations, linear from 0 to 1 for positive
 * but below parameter p deviations, and giving 1 for deviations over parameter
 * p.
 * @author GTouya
 * 
 */
public class Type3PreferenceFunction implements PreferenceFunction {

  /**
   * the function is linear between 0 and p, and constant after p.
   * 
   */
  private double p;

  @Override
  public double value(double deviation) {
    if (deviation <= 0)
      return 0;
    else if (deviation <= p)
      return deviation / p;
    else
      return 1;
  }

  public Type3PreferenceFunction(double p) {
    super();
    this.p = p;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(p);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Type3PreferenceFunction other = (Type3PreferenceFunction) obj;
    if (Double.doubleToLongBits(p) != Double.doubleToLongBits(other.p))
      return false;
    return true;
  }

}
