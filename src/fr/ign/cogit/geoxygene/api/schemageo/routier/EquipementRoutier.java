/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.routier;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ElementPonctuelReseau;

/**
 *element ponctuel de reseau routier
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface EquipementRoutier extends ElementPonctuelReseau {

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

}
