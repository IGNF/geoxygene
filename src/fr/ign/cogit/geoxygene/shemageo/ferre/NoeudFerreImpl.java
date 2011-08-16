/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.ferre;

import fr.ign.cogit.geoxygene.api.schemageo.ferre.NoeudFerre;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.NoeudReseauImpl;

/**
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class NoeudFerreImpl extends NoeudReseauImpl implements NoeudFerre {

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

}
