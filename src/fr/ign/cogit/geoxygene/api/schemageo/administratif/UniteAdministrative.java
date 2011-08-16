/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.administratif;

import fr.ign.cogit.geoxygene.api.schemageo.support.partition.Zone;

/**
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface UniteAdministrative extends Zone {

  /**
   * @return le nom de l'objet
   */
  public String getNom();

  public void setNom(String nom);

  /**
   * @return le code de l'objet
   */
  public String getCode();

  public void setCode(String Code);

  /**
   * @return le chef-lieu de l'unite administrative
   */
  public ChefLieu getChefLieu();

  public void setChefLieu(ChefLieu chefLieu);

}
