/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.graph.jgrapht;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * A graph weighter that uses the features geometry to compute the weight.
 * @author GTouya
 * 
 */
public class MetricalGraphWeighter implements GraphWeighter {

  /**
   * The weight of the edge is the length of the feature geometry.
   */
  @Override
  public double getEdgeWeight(IFeature feature) {
    return feature.getGeom().length();
  }

}
