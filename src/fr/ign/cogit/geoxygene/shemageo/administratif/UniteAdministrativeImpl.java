/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.administratif;

import fr.ign.cogit.geoxygene.api.schemageo.administratif.ChefLieu;
import fr.ign.cogit.geoxygene.api.schemageo.administratif.UniteAdministrative;
import fr.ign.cogit.geoxygene.shemageo.support.partition.ZoneImpl;

/**
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class UniteAdministrativeImpl extends ZoneImpl implements
    UniteAdministrative {

  /**
   * le chef lieu eventuel de l'objet
   */
  private ChefLieu chefLieu = null;

  public ChefLieu getChefLieu() {
    return this.chefLieu;
  }

  public void setChefLieu(ChefLieu chefLieu) {
    this.chefLieu = chefLieu;
  }

  /**
   * le code de l'objet
   */
  private String code = null;

  public String getCode() {
    return this.code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  /**
   * le nom de l'objet
   */
  private String nom = null;

  public String getNom() {
    return this.nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

}
