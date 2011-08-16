/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.elementsIndependants;

import java.util.Collection;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface Meso extends ElementIndependant {

  /**
   * @return les composants
   */
  public Collection<ElementIndependant> getComposants();

  /**
   * @param composants les composants a affecter a l'objet
   */
  public void setComposants(Collection<ElementIndependant> composants);

}
