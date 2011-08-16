/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.reseau;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ElementPonctuelReseau;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * exemple: equipement le long d'une route, point de captage hydro, etc.
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class ElementPonctuelReseauImpl extends ElementLieAuReseauImpl implements
    ElementPonctuelReseau {

  @Override
  public IPoint getGeom() {
    return (IPoint) super.getGeom();
  }

}
