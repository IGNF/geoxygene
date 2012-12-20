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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * Interface describing the requirements for surfacic geographic objects handled
 * by the different generalisation modules of CartAGen. Other interfaces extend
 * this one, based on the geographical thematic of the objects. The objects
 * handled within CartAGen belong to classes that implement these more specific
 * interfaces.
 * @author Cecile Duchene, IGN-F, COGIT Lab.
 */
public interface IGeneObjSurf extends IGeneObj {

  @Override
  public IPolygon getGeom();

  /**
   * @return a HashSet of oriented links supporting the CartAGen object,
   *         provided it has been structured. TODO Finaliser quand le schéma
   *         topologique aura été défini. OrientedLink = arc + marker boolean ou
   *         entier donnant son orientation par rapport à l'objet. ~flagged link
   *         dans Gothic.
   */
  // public HashSet<OrientedLink> getLinks() ;
  /**
   * @return a list of oriented links supporting the outer line of the CartAGen
   *         object, provided it has been structured. TODO Finaliser quand le
   *         schéma topologique aura été défini. OrientedLink = arc + marker
   *         boolean ou entier donnant son orientation par rapport à l'objet.
   *         ~flagged link dans Gothic.
   */
  // public ArrayList<OrientedLink> getOuterLineLinks() ;

}
