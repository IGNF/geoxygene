/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.ferre;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.NoeudReseau;

/**
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface NoeudFerre extends NoeudReseau {

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

}
