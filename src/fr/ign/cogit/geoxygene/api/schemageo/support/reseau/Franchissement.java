/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.reseau;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

/**
 * permet de modeliser les franchissements sans noeud (comme dans la BD carto)
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface Franchissement extends ElementDuReseau {

  @Override
  public IPoint getGeom();

  /**
   * @return
   */
  public Collection<PassePar> getPassePar();
}
