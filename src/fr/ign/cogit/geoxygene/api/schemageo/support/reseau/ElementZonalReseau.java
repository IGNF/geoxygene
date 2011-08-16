/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.reseau;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * exemple: aire de peage, riviere large, etc.
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface ElementZonalReseau extends ElementLieAuReseau {

  @Override
  public IPolygon getGeom();
}
