/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.hydro;

import fr.ign.cogit.geoxygene.api.schemageo.hydro.CoursDEau;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.AgregatReseauImpl;

/**
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class CoursDEauImpl extends AgregatReseauImpl implements CoursDEau {

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
