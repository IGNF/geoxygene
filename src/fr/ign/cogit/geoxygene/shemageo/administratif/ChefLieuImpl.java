/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.administratif;

import fr.ign.cogit.geoxygene.api.schemageo.administratif.ChefLieu;
import fr.ign.cogit.geoxygene.api.schemageo.administratif.UniteAdministrative;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;

/**
 * 
 * chef lieu d'unite administrative
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class ChefLieuImpl extends DefaultFeature implements ChefLieu {

  /**
   * le nom de l'objet
   */
  private String nom = null;

  @Override
  public String getNom() {
    return this.nom;
  }

  @Override
  public void setNom(String nom) {
    this.nom = nom;
  }

  /**
   * l'uniteAdministrative de l'objet
   */
  private UniteAdministrative uniteAdministrative = null;

  @Override
  public UniteAdministrative getUniteAdministrative() {
    return this.uniteAdministrative;
  }

  @Override
  public void setUniteAdministrative(UniteAdministrative uniteAdministrative) {
    this.uniteAdministrative = uniteAdministrative;
  }

}
