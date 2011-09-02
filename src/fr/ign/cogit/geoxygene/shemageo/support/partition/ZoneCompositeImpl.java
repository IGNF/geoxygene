/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.partition;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.schemageo.support.partition.Zone;
import fr.ign.cogit.geoxygene.api.schemageo.support.partition.ZoneComposite;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public class ZoneCompositeImpl extends ZoneImpl implements ZoneComposite {

  /**
   * les zones composant l'objet
   */
  private Collection<Zone> zones = new FT_FeatureCollection<Zone>();

  @Override
  public Collection<Zone> getZones() {
    return this.zones;
  }

  @Override
  public void setZones(Collection<Zone> zones) {
    this.zones = zones;
  }
}
