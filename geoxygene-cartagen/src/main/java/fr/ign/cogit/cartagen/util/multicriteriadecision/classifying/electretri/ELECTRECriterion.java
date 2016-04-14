/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri;

import fr.ign.cogit.cartagen.util.multicriteriadecision.Criterion;

/**
 * @author PTaillandier
 * @author GTouya
 * 
 *         Critère destiné à la méthode de décision multicritère ELECTRE TRI
 *         (Figueira et al 2005)
 */
public abstract class ELECTRECriterion extends Criterion {

  public ELECTRECriterion(String nom) {
    super(nom);

  }

  // valeur du seuil de véto
  /**
   * The veto threshold value. If two compared vectors have a difference in the
   * value for {@code this} criterion that is over the veto threshold, the
   * vector with the highest value will be preferred whatever the other criteria
   * values are.
   * @uml.property name="veto"
   */
  private double veto;

  // valeur du seuil de préférence
  /**
   * The preference threshold value. If one of the compared values is bigger
   * than the other + the preference threshold, it is considered as preferred
   * for this criterion.
   * @uml.property name="preference"
   */
  private double preference;

  // valeur du seuil d'indifference
  /**
   * The indifference threshold value. If the difference between the compared
   * values for this criterion is below the indifference threshold, both values
   * are considered equal for this criterion.
   * @uml.property name="indifference"
   */
  private double indifference;

  // poids du crit�re
  /**
   * The weight of {@code this} criterion compared to the others.
   * @uml.property name="weight"
   */
  private double weight;

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
   * Calcul de la concordance entre a et b (degré de certitude que le vecteur de
   * valeurs qui pour valeur pour ce critère a est mieux que le vecteur qui a
   * pour valeur pour ce critère b)
   * @param a : valeur du premier vecteur de valeurs pour ce critère
   * @param b : valeur du second vecteur de valeurs pour ce critère
   * @return la valeur de concordance entre a et b
   */
  public double computeConcordance(double a, double b) {
    double diff = a - b;
    if (diff <= -this.preference) {
      return 0;
    }
    if (diff >= -this.indifference) {
      return 1;
    }

    return (diff + this.preference) / (this.preference - this.indifference);
  }

  /**
   * Calcul de la discrodance entre a et b (degré de certitude que le vecteur de
   * valeurs qui pour valeur pour ce critère a n'est pas mieux que le vecteur
   * qui a pour valeur pour ce critère b)
   * @param a : valeur du premier vecteur de valeurs pour ce critère
   * @param b : valeur du second vecteur de valeurs pour ce critère
   * @return la valeur de discordance entre a et b
   */
  public double computeDiscordance(double a, double b) {
    double diff = a - b;
    if (diff <= -this.veto) {
      return 1;
    }
    if (diff >= -this.preference) {
      return 0;
    }

    return (diff + this.preference) / (this.preference - this.veto);
  }

}
