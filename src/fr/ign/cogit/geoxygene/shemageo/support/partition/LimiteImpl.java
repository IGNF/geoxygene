/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.partition;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.schemageo.support.partition.Limite;
import fr.ign.cogit.geoxygene.api.schemageo.support.partition.Zone;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class LimiteImpl extends DefaultFeature implements Limite {

  /**
   * les zones dont l'objet est limite
   */
  private Collection<Zone> zones = new FT_FeatureCollection<Zone>();

  @Override
  public Collection<Zone> getZones() {
    return this.zones;
  }

  @Override
  public ILineString getGeom() {
    return (ILineString) super.getGeom();
  };

}
