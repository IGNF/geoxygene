/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.shemageo.support.partition;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.schemageo.support.partition.Limite;
import fr.ign.cogit.geoxygene.api.schemageo.support.partition.Partition;
import fr.ign.cogit.geoxygene.api.schemageo.support.partition.Zone;
import fr.ign.cogit.geoxygene.api.schemageo.support.partition.ZoneComposite;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public abstract class ZoneImpl extends DefaultFeature implements Zone {

  /**
   * la partition a laquelle l'objet appartient
   */
  private Partition partition = null;

  public Partition getPartition() {
    return this.partition;
  }

  public void setPartition(Partition partition) {
    this.partition = partition;
  }

  /**
   * les limites de la zone
   */
  Collection<Limite> limites = new FT_FeatureCollection<Limite>();

  public Collection<Limite> getLimites() {
    return this.limites;
  }

  public void setLimites(Collection<Limite> limites) {
    this.limites = limites;
  }

  /**
   * les zones composees auxquelles la zone appartient eventuellement
   */
  private Collection<ZoneComposite> zonesComposees = new FT_FeatureCollection<ZoneComposite>();

  public Collection<ZoneComposite> getZonesComposees() {
    return this.zonesComposees;
  }

  public void setZonesComposees(Collection<ZoneComposite> zonesComposees) {
    this.zonesComposees = zonesComposees;
  }

  @Override
  public IMultiSurface<?> getGeom() {
    return (IMultiSurface<?>) super.getGeom();
  };

}
