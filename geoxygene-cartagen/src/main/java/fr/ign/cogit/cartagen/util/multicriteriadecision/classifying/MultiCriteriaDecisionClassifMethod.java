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

import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.util.multicriteriadecision.Criterion;

/**
 * @author PTaillandier Interface for multiple criteria decision methods that
 *         give classification conclusions
 */
public interface MultiCriteriaDecisionClassifMethod {

  /**
   * The aim of the method is to define the current values vector
   * (currentValues) inside the interval set (ConclusionIntervals).
   * @param criteria : Criteria set : criteria used for multiple criteria
   *          decision
   * @param currentValues : current values vector : Key : String : criterion
   *          name -> Value : Double : criterion value
   * @param conclusion : the intervals the current values vector has to be
   *          attached to.
   * @return the conclusion associated to the interval the current values vector
   *         is attached to
   */
  public ClassificationResult decision(Set<Criterion> criteria,
      Map<String, Double> currentValues, ConclusionIntervals conclusion);

}
