/**
 * @author julien Gaffuri 25 juin 2009
 */
package fr.ign.cogit.geoxygene.api.schemageo.support.partition;

import java.util.Collection;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;

/**
 * @author julien Gaffuri 25 juin 2009
 * 
 */
public interface Zone extends IFeature {

  /**
   * @return la partition a laquelle la zone appartient
   */
  public Partition getPartition();

  /**
   * @param partition la partition a laquelle la zone appartient
   */
  public void setPartition(Partition partition);

  /**
   * @return les limites sur lesquelles s'appuie la zone
   */
  public Collection<Limite> getLimites();

  /**
   * @param limites les limites sur lesquelles s'appuie la zone
   */
  public void setLimites(Collection<Limite> limites);

  /**
   * @return les zones composees auxquelles la zone appartient eventuellement
   */
  public Collection<ZoneComposite> getZonesComposees();

  /**
   * @param zonesComposees les zones composees aquelles la zone appartient
   *          eventuellement
   */
  public void setZonesComposees(Collection<ZoneComposite> zonesComposees);

  @Override
  public IMultiSurface<?> getGeom();

}
