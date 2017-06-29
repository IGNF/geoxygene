/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.multicriteriadecision.ranking;

import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.Criterion;

public abstract class ELECTREIIICriterion extends Criterion {

  /**
   * veto threshold
   * @uml.property name="veto"
   */
  private double veto;

  /**
   * preference value
   * @uml.property name="preference"
   */
  private double preference;

  /**
   * weight of the criterion
   * @uml.property name="weight"
   */
  private double weight;

  /**
   * indifference threshold
   * @uml.property name="indifference"
   */
  private double indifference;

  public ELECTREIIICriterion(String name) {
    super(name);
  }

  /**
   * @return
   * @uml.property name="poids"
   */
  public double getWeight() {
    return this.weight;
  }

  /**
   * @param weight
   * @uml.property name="poids"
   */
  public void setWeight(double weight) {
    this.weight = weight;
  }

  /**
   * @return
   * @uml.property name="preference"
   */
  public double getPreference() {
    return this.preference;
  }

  /**
   * @param preference
   * @uml.property name="preference"
   */
  public void setPreference(double preference) {
    this.preference = preference;
  }

  /**
   * @return
   * @uml.property name="veto"
   */
  public double getVeto() {
    return this.veto;
  }

  /**
   * @param veto
   * @uml.property name="veto"
   */
  public void setVeto(double veto) {
    this.veto = veto;
  }

  /**
   * @return
   * @uml.property name="indifference"
   */
  public double getIndifference() {
    return this.indifference;
  }

  /**
   * @param indifference
   * @uml.property name="indifference"
   */
  public void setIndifference(double indifference) {
    this.indifference = indifference;
  }

  public double getDiscordanceIndex(double a, double b) {
    double diff = a - b;
    if (diff <= -this.veto) {
      return 1;
    }
    if (diff >= -this.preference) {
      return 0;
    }

    return (diff + this.preference) / (this.preference - this.veto);
  }

  /**
   * Determines if the criterion is in the J(S) coalition concerning the
   * "a outranks b" relation. It means that this criterion is in favor of the
   * assertion that a outranks b.
   * 
   * @param a
   * @param b
   * @return
   * @author GTouya
   */
  public boolean isInScoalition(double a, double b) {
    if (a + this.indifference >= b)
      return true;
    return false;
  }

  /**
   * Determines if the criterion is in the J(Q) coalition concerning the
   * "a outranks b" relation. It means that this criterion is in favor of the
   * assertion that a outranks b, but with a weak preference.
   * 
   * @param a
   * @param b
   * @return
   * @author GTouya
   */
  public boolean isInQcoalition(double a, double b) {
    if ((a + this.indifference < b) && (b <= a + this.preference))
      return true;
    return false;
  }
}
