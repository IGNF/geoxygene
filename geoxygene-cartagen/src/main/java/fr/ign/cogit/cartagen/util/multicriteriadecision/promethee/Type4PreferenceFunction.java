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
 * Type 4 preference function from Brans & Mareschal 2005, it's a stair-shaped
 * function giving 0 for deviations below a parameter q, 0.5 for deviations
 * between parameter q and parameter p (p>q) and 1 for deviations over p.
 * @author GTouya
 * 
 */
public class Type4PreferenceFunction implements PreferenceFunction {

  private double p, q;

  @Override
  public double value(double deviation) {
    if (deviation <= q)
      return 0;
    else if (deviation <= p)
      return 0.5;
    else
      return 1;
  }

  /**
   * Constructor from both function parameters (p > q).
   * @param q
   * @param p
   */
  public Type4PreferenceFunction(double q, double p) {
    super();
    this.p = p;
    this.q = q;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(p);
    result = prime * result + (int) (temp ^ (temp >>> 32));
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
    Type4PreferenceFunction other = (Type4PreferenceFunction) obj;
    if (Double.doubleToLongBits(p) != Double.doubleToLongBits(other.p))
      return false;
    if (Double.doubleToLongBits(q) != Double.doubleToLongBits(other.q))
      return false;
    return true;
  }

}
