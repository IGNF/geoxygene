/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.routier;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.AgregatReseau;

/**
 * 
 * Itineraire routier (exemple: paris - marseille) ou route nommee (exemple: la
 * route des vins d'aix, la route Napoleon, la route des cretes, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface RouteItineraire extends AgregatReseau {

  /**
   * @return le nom
   */
  public String getNom();

  public void setNom(String nom);

}
