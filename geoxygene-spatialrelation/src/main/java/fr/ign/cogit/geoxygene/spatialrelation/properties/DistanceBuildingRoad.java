package fr.ign.cogit.geoxygene.spatialrelation.properties;

import fr.ign.cogit.geoxygene.spatialrelation.api.RelationOperation;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationProperty;
import fr.ign.cogit.geoxygene.spatialrelation.relation.BuildingAlongARoad;

public class DistanceBuildingRoad implements RelationProperty {

  private double distance;
  private BuildingAlongARoad relation;

  @Override
  public Object getValue() {
    return distance;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public RelationOperation getOperation() {
    // TODO Auto-generated method stub
    return null;
  }

  public BuildingAlongARoad getRelation() {
    return relation;
  }

  public void setRelation(BuildingAlongARoad relation) {
    this.relation = relation;
  }

}
