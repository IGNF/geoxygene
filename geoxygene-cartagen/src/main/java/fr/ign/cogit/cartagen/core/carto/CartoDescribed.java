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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * This abtract class describes at the most generic level the methods that are
 * part of the description of the symbol of an object necessary for it
 * generalisation. This class extended by more complete classes depending on the
 * symbol geometric types: CartAGenCartoDescribedLin (linear symbol),
 * CartAGenCartoDescribedSurf (surfacic symbol), CartAGenCartoDescribedPct
 * (punctual symbol). The geographic objects handled within CartAGen for
 * cartographic generalisation purposes should belong to classes that implement
 * this interface or one of the more specific ones.
 * 
 * @author Cecile Duchene, IGN-F, COGIT Lab.
 */

public interface CartoDescribed {

  /**
   * @return the name of the symbol
   */
  public String getSymbolName();

  /**
   * @return the bulk of the symbol (surfacic geometry)
   */
  public IPolygon getSymbolBulk();

}
