/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.routier;

import fr.ign.cogit.geoxygene.api.schemageo.routier.RouteItineraire;
import fr.ign.cogit.geoxygene.shemageo.support.reseau.AgregatReseauImpl;

/**
 * 
 * Itineraire routier (exemple: paris - marseille) ou route nommee (exemple: la
 * route des vins d'aix, la route Napoleon, la route des cretes, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class RouteItineraireImpl extends AgregatReseauImpl implements
    RouteItineraire {

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
