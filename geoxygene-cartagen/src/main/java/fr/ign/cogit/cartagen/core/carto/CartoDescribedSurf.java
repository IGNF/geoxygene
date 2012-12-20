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

/**
 * This interface describes at the methods that are part of the description of
 * the symbol of a surfacic object necessary for it generalisation. The objects
 * handled within CartAGen for cartographic generalisation purposes, that are
 * represented with a linear symbol, should belong to a class that implements
 * this interface.
 * 
 * @author Cecile Duchene, IGN-F, COGIT Lab.
 */
public interface CartoDescribedSurf extends CartoDescribed {

  /**
   * @return the width of the symbol border
   */
  public double getSymbolBorderWidth();

}
