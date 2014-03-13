/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.spatialrelation.api;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * A spatial relation between two objects.
 * 
 * @author GTouya
 * 
 */
public interface BinarySpatialRelation extends SpatialRelation {

  /**
   * Get the first member of the {@link BinarySpatialRelation}.
   * 
   * @return
   */
  public IFeature getMember1();

  /**
   * Get the second member of the {@link BinarySpatialRelation}.
   * 
   * @return
   */
  public IFeature getMember2();
}
