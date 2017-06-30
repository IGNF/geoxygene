package fr.ign.cogit.geoxygene.osm.lodanalysis.relations;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.osm.lodanalysis.LoDCategory;

public class LoDSpatialRelation {

  private IFeature feature1, feature2;
  private LoDCategory category1, category2;
  private String name;

  public LoDSpatialRelation(IFeature feature1, IFeature feature2,
      LoDCategory category1, LoDCategory category2, String name) {
    super();
    this.feature1 = feature1;
    this.feature2 = feature2;
    this.category1 = category1;
    this.category2 = category2;
    this.setName(name);
  }

  public IFeature getFeature1() {
    return feature1;
  }

  public void setFeature1(IFeature feature1) {
    this.feature1 = feature1;
  }

  public IFeature getFeature2() {
    return feature2;
  }

  public void setFeature2(IFeature feature2) {
    this.feature2 = feature2;
  }

  public LoDCategory getCategory1() {
    return category1;
  }

  public void setCategory1(LoDCategory category1) {
    this.category1 = category1;
  }

  public LoDCategory getCategory2() {
    return category2;
  }

  public void setCategory2(LoDCategory category2) {
    this.category2 = category2;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name + " [" + feature1.toString() + "," + feature2.toString() + "]";
  }

}
