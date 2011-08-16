/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.reseau;

import fr.ign.cogit.geoxygene.api.schemageo.support.reseau.ElementZonalReseau;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

/**
 * exemple: aire de peage, riviere large, etc.
 * 
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class ElementZonalReseauImpl extends ElementLieAuReseauImpl implements
    ElementZonalReseau {

  @Override
  public IPolygon getGeom() {
    return (IPolygon) super.getGeom();
  }

}
