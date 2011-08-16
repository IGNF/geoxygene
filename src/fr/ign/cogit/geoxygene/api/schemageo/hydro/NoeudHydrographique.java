/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.hydro;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.NoeudReseau;

/**
 * Noeud du reseau routier + certains points d'eau (sources, pertes, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface NoeudHydrographique extends NoeudReseau {

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

  /**
   * @return la cote
   */
  public double getCote();

  public void setCote(double cote);

}
