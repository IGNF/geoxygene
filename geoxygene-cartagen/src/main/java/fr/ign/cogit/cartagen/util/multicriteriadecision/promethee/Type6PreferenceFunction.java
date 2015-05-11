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
 * Type 6 preference function from Brans & Mareschal 2005, it's a gaussian
 * criterion, with a function giving 0 for negative (or zero) deviations and
 * tends to 1 when deviation tends to infinity, a parameter s giving the
 * inflexion point of the curve.
 * @author GTouya
 * 
 */
public class Type6PreferenceFunction implements PreferenceFunction {

  private double s;

  @Override
  public double value(double deviation) {
    if (deviation <= 0)
      return 0;
    else {
      double expo = Math.exp(-deviation * deviation / (2 * s * s));
      return 1 - expo;
    }
  }

  public Type6PreferenceFunction(double s) {
    super();
    this.s = s;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(s);
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
    Type6PreferenceFunction other = (Type6PreferenceFunction) obj;
    if (Double.doubleToLongBits(s) != Double.doubleToLongBits(other.s))
      return false;
    return true;
  }

}
