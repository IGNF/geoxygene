/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.multicriteriadecision;

import java.util.Map;

/**
 * @author PTaillandier
 * @author GTouya Criterion used for multiple criteria decision making
 */
public abstract class Criterion {

  // criterion name
  /**
   * @uml.property name="name"
   */
  private String name;

  /**
   * Criterion value for a given state
   * @param param : parameters map (the state) : Key : String : parameter name
   *          -> Value : Object : the value
   * @return the criterion value for this state
   */
  public abstract double value(Map<String, Object> param);

  /**
   * @return
   * @uml.property name="name"
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name
   * @uml.property name="name"
   */
  public void setName(String name) {
    this.name = name;
  }

  public Criterion(String name) {
    super();
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }

  @Override
  public int hashCode() {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + (this.name == null ? 0 : this.name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final Criterion other = (Criterion) obj;
    if (this.name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!this.name.equals(other.name)) {
      return false;
    }
    return true;
  }

}
