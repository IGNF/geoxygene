/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.routier;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.AgregatReseau;

/**
 * carrefour complexe du reseau routier (rond-point, echangeur, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface CarrefourComplexe extends AgregatReseau {

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

}
