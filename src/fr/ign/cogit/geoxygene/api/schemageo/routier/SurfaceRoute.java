/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.routier;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ElementZonalReseau;

/**
 * surface de route, peage, etc.
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface SurfaceRoute extends ElementZonalReseau {

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

}
