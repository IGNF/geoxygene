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
 * Gives the weight of geographic features when they are used to build a graph.
 * @author GTouya
 * 
 */
public interface GraphWeighter {

  /**
   * Get the weight of a feature as an edge in a graph.
   * @param feature
   * @return
   */
  public double getEdgeWeight(IFeature feature);
}
