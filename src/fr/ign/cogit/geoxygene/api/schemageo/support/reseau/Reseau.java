/**
 * @author julien Gaffuri 20 juil. 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.reseau;

import java.util.Collection;

/**
 * un objet reseau (qui est un agregat particulier, qui contient tous les
 * elements d'un meme reseau)
 * 
 * @author julien Gaffuri 20 juil. 2009
 * 
 */
public interface Reseau extends AgregatReseau {

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

  /**
   * @return elements non topologiques lies au reseau
   */
  public Collection<ElementLieAuReseau> getElementsLiesAuReseau();

}
