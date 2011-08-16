/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.ferre;

import fr.ign.cogit.geoxygene.api.schemageo.ferre.LigneDeCheminDeFer;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.AgregatReseauImpl;

/**
 * ligne de chemin de fer (exemple: le train des pigne, la petite ceinture,
 * ligne Marseille - Vintimille, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class LigneDeCheminDeFerImpl extends AgregatReseauImpl implements
    LigneDeCheminDeFer {

  /**
   * le nom
   */
  private String nom = "";

  public String getNom() {
    return this.nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  /**
   * le caractere touristique de l'objet
   */
  private boolean touristique = false;

  public boolean isTouristique() {
    return this.touristique;
  }

  public void setTouristique(boolean touristique) {
    this.touristique = touristique;
  }

}
