package fr.ign.cogit.geoxygene.matching.hmmm;

import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Chargeur;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

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
 * Specialisation of the minimalistic implementation of "Hidden Markov Map
 * Matching Through Noise and Sparseness".
 * <p>
 * This class contains the {@link #importNetwork(FT_FeatureCollection)} method
 * using the BDTOPO2.0 conceptual schema. More precisely, it allows to load and
 * build a proper navigable network from a BDTOPO2.0 dataset.
 
 * @see HMMMapMatcher
 * 
 * @author Julien Perret
 */
public class BDTopoMapMatcher extends HMMMapMatcher {
  /**
   * @param gpsPop
   * @param networkPop
   * @param sigmaZ
   * @param selection
   * @param beta
   * @param distanceLimit
   */
  public BDTopoMapMatcher(IFeatureCollection<? extends IFeature> gpsPop,
      IFeatureCollection<? extends IFeature> networkPop, double sigmaZ,
      double selection, double beta, double distanceLimit) {
    super(gpsPop, networkPop, sigmaZ, selection, beta, distanceLimit);
  }

  @Override
  protected void importNetwork(
      IFeatureCollection<? extends IFeature> network) {
    String attribute = "SENS";
    Map<Object, Integer> orientationMap = new HashMap<Object, Integer>(2);
    orientationMap.put("Direct", new Integer(1));
    orientationMap.put("Inverse", new Integer(-1));
    orientationMap.put("Double", new Integer(2));
    orientationMap.put("NC", new Integer(2));
    String groundAttribute = "POS_SOL";
    double tolerance = 0.1;
    Chargeur.importAsEdges(network, this.getNetworkMap(), attribute,
        orientationMap, "", null, groundAttribute, tolerance);
  }
}
