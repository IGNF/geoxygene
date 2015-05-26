package fr.ign.cogit.geoxygene.spatialrelation.properties;

import fr.ign.cogit.geoxygene.spatialrelation.api.RelationOperation;
import fr.ign.cogit.geoxygene.spatialrelation.api.RelationProperty;
import fr.ign.cogit.geoxygene.spatialrelation.relation.BuildingAlongARoad;

public class DistanceBuildingRoad implements RelationProperty {

  // ***************
  // PROPERTIES
  // ***************
  private double distance;
  private BuildingAlongARoad relation;

  // ***************
  // CONSTRUCTOR
  // ***************
  public DistanceBuildingRoad() {
    super();
  }

  // ***************
  // GETTERS
  // ***************
  @Override
  public Object getValue() {
    return distance;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  public BuildingAlongARoad getRelation() {
    return relation;
  }

  @Override
  public RelationOperation getOperation() {
    // TODO Auto-generated method stub
    return null;
  }

  // ***************
  // SETTERS
  // ***************

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public void setRelation(BuildingAlongARoad relation) {
    this.relation = relation;
  }

}
