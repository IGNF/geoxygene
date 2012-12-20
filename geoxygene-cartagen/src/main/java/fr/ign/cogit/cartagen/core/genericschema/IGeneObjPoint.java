/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * Interface describing the requirements for punctual geographic objects handled
 * by the different generalisation modules of CartAGen. Other interfaces extend
 * this one, based on the geographical thematic of the objects. The objects
 * handled within CartAGen belong to classes that implement these more specific
 * interfaces.
 * @author Cecile Duchene, IGN-F, COGIT Lab.
 */
public interface IGeneObjPoint extends IGeneObj {

  @Override
  public IPoint getGeom();

  /**
   * @return the node supporting the CartAGen object, provided it has been
   *         structured. TODO Finaliser quand le schéma topologique aura été
   *         défini.
   */
  // public Node getNode() ;

}
