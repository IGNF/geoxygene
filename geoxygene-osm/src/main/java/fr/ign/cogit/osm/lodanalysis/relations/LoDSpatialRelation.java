package fr.ign.cogit.osm.lodanalysis.relations;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.osm.lodanalysis.LoDCategory;

public class LoDSpatialRelation {

  private IGeneObj feature1, feature2;
  private LoDCategory category1, category2;
  private String name;

  public LoDSpatialRelation(IGeneObj feature1, IGeneObj feature2,
      LoDCategory category1, LoDCategory category2, String name) {
    super();
    this.feature1 = feature1;
    this.feature2 = feature2;
    this.category1 = category1;
    this.category2 = category2;
    this.setName(name);
  }

  public IGeneObj getFeature1() {
    return feature1;
  }

  public void setFeature1(IGeneObj feature1) {
    this.feature1 = feature1;
  }

  public IGeneObj getFeature2() {
    return feature2;
  }

  public void setFeature2(IGeneObj feature2) {
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

}
