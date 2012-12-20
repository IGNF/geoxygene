/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util.multicriteriadecision.classifying;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.util.multicriteriadecision.Criterion;

/**
 * @author PTaillandier The aim of the decision is to find the category
 *         corresponding to the values for the criteria. For each criterion, the
 *         possible values (between 0 & 1) is divised in intervals. 0 :
 *         "very bad" interval : "bad" interval : "medium" interval :
 *         "good interval" : "very good" interval : 1
 * 
 *         L'enjeu du diagnostic est de définir à quelle catégorie de
 *         qualité appartient le jeu de K courant Nous avons pour cela défini
 *         5 qualités de connaissances (chap. E) Pour chaque critère,
 *         l'ensemble des valeurs pouvant être pris par ce critère (compris
 *         entre 0 et 1) est divisé en intervalle 0 : intervalle
 *         "très mauvais" :intervalle "mauvais" : intervalle "moyen" :
 *         intervalle "bon" : intervalle "très bon" : 1
 */
public class ConclusionIntervals {

  // liste des intervalles (très mauvais, mauvais, ...)
  /**
   * @uml.property name="intervalles"
   * @uml.associationEnd multiplicity="(0 -1)" inverse="this$0:fr.ign.cogit.generalisation.lib.gestionConnaissances.diagnostic.decisionmulticritere.conclusion.ConclusionIntervalles$Intervalle"
   */
  private List<Interval> intervals;

  // Ensemble de Critère : critères utilis�s pour la décision multicritère
  /**
   * @uml.property name="criteria"
   */
  private Set<Criterion> criteria;

  @Override
  public String toString() {
    return this.intervals + "";
  }

  public List<Interval> getIntervals() {
    return this.intervals;
  }

  public void setIntervals(List<Interval> intervals) {
    this.intervals = intervals;
  }

  public ConclusionIntervals(Set<Criterion> criteria) {
    super();
    this.intervals = new ArrayList<Interval>();
    this.criteria = criteria;
  }

  public void addInterval(Map<String, Double> startPoint,
      Map<String, Double> endPoint, String conclusion) {
    Interval itv = new Interval(startPoint, endPoint, conclusion);
    this.intervals.add(itv);
  }

  public ConclusionIntervals(List<Interval> intervals, Set<Criterion> criteria) {
    super();
    this.intervals = intervals;
    this.criteria = criteria;
  }

  public Set<Criterion> getCriteria() {
    return this.criteria;
  }

  public void setCriteria(Set<Criterion> criteria) {
    this.criteria = criteria;
  }

  /**
   * @author PTaillandier Correspond pour l'ensemble des intervalles à un
   *         intervalle de valeur associé à une conclusion
   */
  public class Interval {
    // borne inf des valeurs de l'intervalle
    // Clef : String : nom critère -> Valeur : Double : valeur de cette borne
    // inf
    /**
		 */
    private Map<String, Double> startPoint;

    // borne sup des valeurs de l'intervalle
    // Clef : String : nom critè�re -> Valeur : Double : valeur de cette
    // borne
    // sup
    /**
		 */
    private Map<String, Double> endPoint;

    // représentant de la valeur de l'intervalle (typiquement, milieu de
    // l'intervalle)
    // Clef : String : nom critère -> Valeur : Double : valeur de ce
    // représentant
    /**
		 */
    private Map<String, Double> representative;

    // conlusion associée à ces intervalles
    /**
		 */
    private String conclusion;

    public Interval(Map<String, Double> startPoint,
        Map<String, Double> endPoint, String conclusion) {
      super();
      this.startPoint = startPoint;
      this.endPoint = endPoint;
      this.representative = new Hashtable<String, Double>();
      // pour les valeurs du représentant, on prend le millieu de chaque
      // intervalle
      for (String name : startPoint.keySet()) {
        double val = 0.5 * (endPoint.get(name).doubleValue() + startPoint.get(
            name).doubleValue());
        this.representative.put(name, new Double(val));
      }
      this.conclusion = conclusion;
    }

    /**
     * @return
     * @uml.property name="startPoint"
     */
    public Map<String, Double> getStartPoint() {
      return this.startPoint;
    }

    /**
     * @param startPoint
     * @uml.property name="startPoint"
     */
    public void setStartPoint(Map<String, Double> startPoint) {
      this.startPoint = startPoint;
      // pour les valeurs du représentant, on prend le millieu de chaque
      // intervalle
      for (String name : startPoint.keySet()) {
        double val = 0.5 * (this.endPoint.get(name).doubleValue() + startPoint
            .get(name).doubleValue());
        this.representative.put(name, new Double(val));
      }
    }

    /**
     * @return
     * @uml.property name="representant"
     */
    public Map<String, Double> getRepresentative() {
      return this.representative;
    }

    /**
     * @return
     * @uml.property name="endPoint"
     */
    public Map<String, Double> getEndPoint() {
      return this.endPoint;
    }

    /**
     * @param endPoint
     * @uml.property name="endPoint"
     */
    public void setEndPoint(Map<String, Double> endPoint) {
      this.endPoint = endPoint;
      // pour les valeurs du représentant, on prend le millieu de chaque
      // intervalle
      for (String nom : this.startPoint.keySet()) {
        double val = 0.5 * (endPoint.get(nom).doubleValue() + this.startPoint
            .get(nom).doubleValue());
        this.representative.put(nom, new Double(val));
      }
    }

    /**
     * @return
     * @uml.property name="conclusion"
     */
    public String getConclusion() {
      return this.conclusion;
    }

    /**
     * @param conclusion
     * @uml.property name="conclusion"
     */
    public void setConclusion(String conclusion) {
      this.conclusion = conclusion;
    }

    @Override
    public String toString() {
      return this.conclusion;
    }
  }
}
