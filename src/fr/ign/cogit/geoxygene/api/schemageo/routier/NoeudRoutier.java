/**
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.routier;

import java.util.Set;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.NoeudReseau;

/**
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public interface NoeudRoutier extends NoeudReseau {

  public int getDegre();
  
  public Set<TronconDeRoute> getRoutes();
}
