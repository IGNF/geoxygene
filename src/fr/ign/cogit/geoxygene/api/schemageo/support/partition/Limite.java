/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.partition;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface Limite extends IFeature {

  /**
   * @return les zones dont l'objet est limite
   */
  public Collection<Zone> getZones();

  @Override
  public ILineString getGeom();

}
