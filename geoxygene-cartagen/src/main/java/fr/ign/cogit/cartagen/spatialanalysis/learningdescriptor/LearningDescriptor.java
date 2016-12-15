/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.learningdescriptor;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * A geographic descriptor that can be used in a learning process for spatial
 * analysis, e.g. the identification of landmarks among buildings or the
 * classification of buildings.
 * @author GTouya
 *
 */
public interface LearningDescriptor {

  /**
   * The value of the descriptor for a given feature.
   * @param feature
   * @return
   */
  public double getValue(IFeature feature);

  /**
   * The name of the descriptor.
   * @return
   */
  public String getName();
}
