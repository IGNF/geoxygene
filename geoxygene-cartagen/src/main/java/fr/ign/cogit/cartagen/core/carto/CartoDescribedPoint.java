/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.carto;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * This class describes at the methods that are part of the description of the
 * symbol of a ponctual object necessary for it generalisation. The objects
 * handled within CartAGen for cartographic generalisation purposes, that are
 * represented with a linear symbol, should belong to a class that implements
 * this interface.
 * 
 * @author Cecile Duchene, IGN-F, COGIT Lab.
 */

public interface CartoDescribedPoint extends CartoDescribed {

  /**
   * @return the precise point of the symbol
   */
  public IGeometry getPreciseSymbol();

  /**
   * @return the global covering of the symbol (different from the bulk)
   */
  public IGeometry getSymbolCovering();

}
