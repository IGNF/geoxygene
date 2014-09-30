package fr.ign.cogit.geoxygene.matching.hmmm;

import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;

/**
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 * 
 * 
 * Minimalistic implementation of "Hidden Markov Map Matching Through Noise and
 * Sparseness", Paul Newson and John Krumm, 17th ACM SIGSPATIAL International
 * Conference on Advances in Geographic Information Systems (ACM SIGSPATIAL GIS
 * 2009).
 * @see {@link http
 *      ://research.microsoft.com/en-us/um/people/jckrumm/MapMatchingData
 *      /data.htm}
 *      <p>
 *      In this implementation, we only use the proposed emission and transition
 *      probabilities but most of the breaks were ignored.
 */
public class SeattleMapMatcher extends HMMMapMatcher {
  
  /**
   * @param gpsPop
   * @param networkPop
   * @param sigmaZ
   * @param selection
   * @param beta
   * @param distanceLimit
   */
  public SeattleMapMatcher(IFeatureCollection<? extends IFeature> gpsPop,
      IFeatureCollection<? extends IFeature> networkPop, double sigmaZ,
      double selection, double beta, double distanceLimit) {
    
    super(gpsPop, networkPop, sigmaZ, selection, beta, distanceLimit);
  }

  @Override
  protected void importNetwork(
      IFeatureCollection<? extends IFeature> network) {
    
    String attribute = "TWO_WAY";
    Map<Object, Integer> orientationMap = new HashMap<Object, Integer>(2);
    long direct = 0;
    long doubleSens = 1;
    orientationMap.put(direct, new Integer(1));
    orientationMap.put(doubleSens, new Integer(2));

    String groundAttribute = null;
    double tolerance = 0.1;
    
    Chargeur.importAsEdges(network, this.getNetworkMap(), attribute,
        orientationMap, "", null, groundAttribute, tolerance);
  }

}
