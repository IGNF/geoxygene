/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.routier;

import fr.ign.cogit.geoxygene.api.schemageo.routier.SurfaceRoute;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.ElementZonalReseauImpl;

/**
 * surface de route, peage, etc.
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class SurfaceRouteImpl extends ElementZonalReseauImpl implements
    SurfaceRoute {

  /**
   * le nom
   */
  private String nom = "";

  @Override
  public String getNom() {
    return this.nom;
  }

  @Override
  public void setNom(String nom) {
    this.nom = nom;
  }

}
