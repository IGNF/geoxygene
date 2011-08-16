/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.reseau;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface NoeudReseau extends ElementDuReseau {

  @Override
  public IPoint getGeom();

  /**
   * @return les arcs entrant sur le noeud
   */
  public Collection<ArcReseau> getArcsEntrants();

  /**
   * @return les arcs sortant du noeud
   */
  public Collection<ArcReseau> getArcsSortants();
}
