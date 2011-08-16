/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.administratif;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * 
 * chef lieu d'unite administrative
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface ChefLieu extends IFeature {

  /**
   * @return le nom de l'objet
   */
  public String getNom();

  public void setNom(String nom);

  /**
   * @return l'unite administrative dont l'objet est chef-lieu
   */
  public UniteAdministrative getUniteAdministrative();

  public void setUniteAdministrative(UniteAdministrative uniteAdministrative);

}
