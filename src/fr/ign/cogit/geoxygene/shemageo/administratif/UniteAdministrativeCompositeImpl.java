/**
 * 
 */
package fr.ign.cogit.geoxygene.shemageo.administratif;

import fr.ign.cogit.geoxygene.api.schemageo.administratif.ChefLieu;
import fr.ign.cogit.geoxygene.api.schemageo.administratif.UniteAdministrativeComposite;
import fr.ign.cogit.geoxygene.shemageo.support.partition.ZoneCompositeImpl;

/**
 * @author JGaffuri 21 octobre 2009
 * 
 */
public class UniteAdministrativeCompositeImpl extends ZoneCompositeImpl
    implements UniteAdministrativeComposite {

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
