/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.urban;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;
import fr.ign.cogit.cartagen.spatialanalysis.network.DeadEndGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.streets.StreetNetwork;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/*
 * ###### IGN / CartAGen ###### Title: Town Description: Villes Author: J.
 * Renard Date: 04/02/2010
 */

public interface ITown extends IGeneObjSurf {

  /**
   * The internal blocks composing the town
   * @return
   */
  public IFeatureCollection<IUrbanBlock> getTownBlocks();

  public void setTownBlocks(IFeatureCollection<IUrbanBlock> townBlocks);

  /**
   * The street network the town
   * @return
   */
  public StreetNetwork getStreetNetwork();

  public void setStreetNetwork(StreetNetwork net);

  /**
   * The dead ends of the town
   * @return
   */
  public IFeatureCollection<DeadEndGroup> getDeadEnds();

  public void setDeadEnds(IFeatureCollection<DeadEndGroup> deadEnds);

  /**
   * Get the block that can represent the town historical centre, not the
   * centroid of the town outline.
   * 
   * @return
   * @author GTouya
   */
  public boolean isTownCentre(IUrbanBlock block);

  public static final String FEAT_TYPE_NAME = "Town"; //$NON-NLS-1$
}
