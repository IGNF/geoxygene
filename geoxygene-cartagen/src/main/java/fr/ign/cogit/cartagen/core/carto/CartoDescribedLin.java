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
 * This class describes at the methods that are part of the description of the
 * symbol of a linear object necessary for it generalisation. The objects
 * handled within CartAGen for cartographic generalisation purposes, that are
 * represented with a linear symbol, should belong to a class that implements
 * this interface.
 * 
 * @author Cecile Duchene, IGN-F, COGIT Lab.
 */
public interface CartoDescribedLin extends CartoDescribed {

  /**
   * @return the external width of the symbol, i.e. the total width of the
   *         symbol including its casing (black border) if it has one
   */
  public double getSymbolExtWidth();

  /**
   * @return the internal width of the symbol, i.e. the width of the symbol
   *         without its casing (black border) if it has one
   */
  public double getSymbolIntWidth();

  // TODO prevoir une autre interface? pour gérer les symboles dissymétriques
  // type talus
  // => getSymbolLeftWidth(), getSymbolRightWidth()

}
