/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.reseau;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * exemple: equipement le long d'une route, point de captage hydro, etc.
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface ElementPonctuelReseau extends ElementLieAuReseau {

  @Override
  public IPoint getGeom();

}
