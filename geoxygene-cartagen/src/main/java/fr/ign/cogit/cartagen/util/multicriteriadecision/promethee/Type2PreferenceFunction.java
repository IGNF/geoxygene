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
 * Type 2 preference function from Brans & Mareschal 2005, it's a simple
 * function giving 0 for deviations below or equal to a parameter q and 1 for
 * deviations over parameter q.
 * @author GTouya
 * 
 */
public class Type2PreferenceFunction implements PreferenceFunction {

  /**
   * the deviation value after which the preference value becomes 1.
   */
  private double q;

  @Override
  public double value(double deviation) {
    if (deviation <= q)
      return 0;
    else
      return 1;
  }

  public Type2PreferenceFunction(double q) {
    super();
    this.q = q;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(q);
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
    Type2PreferenceFunction other = (Type2PreferenceFunction) obj;
    if (Double.doubleToLongBits(q) != Double.doubleToLongBits(other.q))
      return false;
    return true;
  }

}
