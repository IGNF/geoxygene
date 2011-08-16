/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.routier;

import fr.ign.cogit.geoxygene.api.schemageo.routier.EquipementRoutier;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.ElementPonctuelReseauImpl;

/**
 *element ponctuel de reseau routier
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class EquipementRoutierImpl extends ElementPonctuelReseauImpl implements
    EquipementRoutier {

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
