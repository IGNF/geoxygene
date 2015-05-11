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

import fr.ign.cogit.cartagen.util.multicriteriadecision.Criterion;

/**
 * A Promethee criterion with a name and a preference function.
 * @author GTouya
 * 
 */
public abstract class PrometheeCriterion extends Criterion {

  private PreferenceFunction preferenceFunction;

  public PrometheeCriterion(String name, PreferenceFunction preferenceFunction) {
    super(name);
    this.preferenceFunction = preferenceFunction;
  }

  public PreferenceFunction getPreferenceFunction() {
    return preferenceFunction;
  }

  public void setPreferenceFunction(PreferenceFunction preferenceFunction) {
    this.preferenceFunction = preferenceFunction;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
    result = prime * result
        + ((preferenceFunction == null) ? 0 : preferenceFunction.hashCode());
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
    PrometheeCriterion other = (PrometheeCriterion) obj;
    if (getName() == null) {
      if (other.getName() != null)
        return false;
    } else if (!getName().equals(other.getName()))
      return false;
    if (preferenceFunction == null) {
      if (other.preferenceFunction != null)
        return false;
    } else if (!preferenceFunction.equals(other.preferenceFunction))
      return false;
    return true;
  }

}
