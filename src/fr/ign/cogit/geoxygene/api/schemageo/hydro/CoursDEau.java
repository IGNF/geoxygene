/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.hydro;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.AgregatReseau;

/**
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface CoursDEau extends AgregatReseau {

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

}
