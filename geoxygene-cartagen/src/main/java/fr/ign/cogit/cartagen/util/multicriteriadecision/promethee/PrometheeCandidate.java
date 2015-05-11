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

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A candidate for the PROMETHEE multicriteria decision technique. A candidate
 * has values for a given list of criteria.
 * @author GTouya
 * 
 */
public class PrometheeCandidate {

  private int id;
  private Map<PrometheeCriterion, Double> criteriaValues;
  private static AtomicInteger COUNTER = new AtomicInteger();
  private Object candidateObject;

  public PrometheeCandidate(Object candidateObject,
      Map<PrometheeCriterion, Double> criteriaValues) {
    this.id = COUNTER.getAndIncrement();
    this.criteriaValues = criteriaValues;
    this.candidateObject = candidateObject;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public Map<PrometheeCriterion, Double> getCriteriaValues() {
    return criteriaValues;
  }

  public void setCriteriaValues(Map<PrometheeCriterion, Double> criteriaValues) {
    this.criteriaValues = criteriaValues;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((candidateObject == null) ? 0 : candidateObject.hashCode());
    result = prime * result
        + ((criteriaValues == null) ? 0 : criteriaValues.hashCode());
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
    PrometheeCandidate other = (PrometheeCandidate) obj;
    if (candidateObject == null) {
      if (other.candidateObject != null)
        return false;
    } else if (!candidateObject.equals(other.candidateObject))
      return false;
    if (criteriaValues == null) {
      if (other.criteriaValues != null)
        return false;
    } else if (!criteriaValues.equals(other.criteriaValues))
      return false;
    if (id != other.id)
      return false;
    return true;
  }

  public Object getCandidateObject() {
    return candidateObject;
  }

  public void setCandidateObject(Object candidateObject) {
    this.candidateObject = candidateObject;
  }

  @Override
  public String toString() {
    return candidateObject.toString() + "(" + id + "): "
        + criteriaValues.toString();
  }

}
