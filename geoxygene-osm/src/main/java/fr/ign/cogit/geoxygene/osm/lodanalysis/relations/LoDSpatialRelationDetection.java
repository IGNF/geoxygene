package fr.ign.cogit.geoxygene.osm.lodanalysis.relations;

import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/**
 * the classes that extend LoDSpatialRelationDetection are classes to identify
 * instances of a given spatial relation with inconsistent LoD.
 * @author GTouya
 * 
 */
public abstract class LoDSpatialRelationDetection {

  private IFeatureCollection<IFeature> features1, features2;
  private int lodDiffThreshold;

  public LoDSpatialRelationDetection(IFeatureCollection<IFeature> features1,
      IFeatureCollection<IFeature> features2, int lodDiffThreshold) {
    super();
    this.features1 = features1;
    this.features2 = features2;
    this.lodDiffThreshold = lodDiffThreshold;
  }

  public void setFeatures2(IFeatureCollection<IFeature> features2) {
    this.features2 = features2;
  }

  public IFeatureCollection<IFeature> getFeatures2() {
    return features2;
  }

  public void setFeatures1(IFeatureCollection<IFeature> features1) {
    this.features1 = features1;
  }

  public IFeatureCollection<IFeature> getFeatures1() {
    return features1;
  }

  /**
   * Find the {@link LoDSpatialRelation} instances considering the features of
   * features1 and the features of features2, considering the lodDiffThreshold.
   * @return
   */
  public abstract Set<LoDSpatialRelation> findInstances();

  public abstract String getName();

  public void setLodDiffThreshold(int lodDiffThreshold) {
    this.lodDiffThreshold = lodDiffThreshold;
  }

  public int getLodDiffThreshold() {
    return lodDiffThreshold;
  }
}
