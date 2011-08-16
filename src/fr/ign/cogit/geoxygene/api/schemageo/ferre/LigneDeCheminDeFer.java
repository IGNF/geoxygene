/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.ferre;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.AgregatReseau;

/**
 * ligne de chemin de fer (exemple: le train des pigne, la petite ceinture,
 * ligne Marseille - Vintimille, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface LigneDeCheminDeFer extends AgregatReseau {

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

  /**
   * @return le caractere touristique de l'objet
   */
  public boolean isTouristique();

  public void setTouristique(boolean touristique);

}
