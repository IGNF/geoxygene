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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * The class that makes PROMETHEE decisions among several candidates, according
 * to some given criteria.
 * @author GTouya
 * 
 */
public class PrometheeDecision {

  private static Logger logger = Logger.getLogger(PrometheeDecision.class
      .getName());

  /**
   * The weights giving the relative importance of each criterion. The sum of
   * the weights is equal to 1 (all weights are between 0 and 1).
   */
  private Map<PrometheeCriterion, Double> weights;
  private Set<PrometheeCriterion> criteria = new HashSet<>();

  public PrometheeDecision(Map<PrometheeCriterion, Double> criteriaWeights) {
    super();
    this.criteria.addAll(criteriaWeights.keySet());
    this.weights = criteriaWeights;
  }

  /**
   * Picks the best candidate considering the criteria, using the Promethée II
   * complete ranking.
   * @param candidates
   * @return
   */
  public PrometheeCandidate makeDecision(
      Collection<PrometheeCandidate> candidates) {
    PrometheeCandidate best = null;
    double maxNetOutranking = Double.NEGATIVE_INFINITY;
    // loop on each candidate to compute its positive and negative outranking
    // flow
    for (PrometheeCandidate a : candidates) {
      double positiveOutrankFlow = 0.0, negativeOutrankFlow = 0.0;
      for (PrometheeCandidate b : candidates) {
        if (b.equals(a))
          continue;
        // compute the aggregated preference indices of a and b
        double aggrPrefIndexAB = 0.0;
        double aggrPrefIndexBA = 0.0;
        for (PrometheeCriterion criterion : criteria) {
          double weight = weights.get(criterion);
          // first compute deviation between a and b
          double devAtoB = a.getCriteriaValues().get(criterion)
              - b.getCriteriaValues().get(criterion);
          double prefAtoB = criterion.getPreferenceFunction().value(devAtoB);
          aggrPrefIndexAB += prefAtoB * weight;
          double prefBtoA = criterion.getPreferenceFunction().value(-devAtoB);
          aggrPrefIndexBA += prefBtoA * weight;
        }

        positiveOutrankFlow += aggrPrefIndexAB;
        negativeOutrankFlow += aggrPrefIndexBA;
      }
      positiveOutrankFlow = positiveOutrankFlow / (candidates.size() - 1);
      negativeOutrankFlow = negativeOutrankFlow / (candidates.size() - 1);
      double netOutrankingFlow = positiveOutrankFlow - negativeOutrankFlow;
      if (netOutrankingFlow > maxNetOutranking) {
        best = a;
        maxNetOutranking = netOutrankingFlow;
      }
    }

    logger.info(best.toString() + " is the best candidate (" + maxNetOutranking
        + ")");
    return best;
  }
}
