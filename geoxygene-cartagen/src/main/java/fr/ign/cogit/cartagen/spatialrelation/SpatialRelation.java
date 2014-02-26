package fr.ign.cogit.cartagen.spatialrelation;

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * Interface to implement with Java objects the spatial relation ontological
 * model from Touya et al. (2012).
 * @author GTouya
 * 
 */
public interface SpatialRelation {

  /**
   * The members of the spatial relation. There are two members for common
   * binary relations.
   * @return
   */
  public List<IFeature> getMembers();
}
