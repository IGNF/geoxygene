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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Type 1 preference function from Brans & Mareschal 2005, it's a simple
 * function giving 0 for negative (or zero) deviations and 1 for positive
 * deviations.
 * @author GTouya
 * 
 */
public class Type1PreferenceFunction implements PreferenceFunction {

  private int id;
  private static AtomicInteger COUNTER = new AtomicInteger();

  @Override
  public double value(double deviation) {
    if (deviation <= 0)
      return 0;
    else
      return 1;
  }

  public Type1PreferenceFunction() {
    super();
    this.id = COUNTER.getAndIncrement();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
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
    Type1PreferenceFunction other = (Type1PreferenceFunction) obj;
    if (id != other.id)
      return false;
    return true;
  }

}
