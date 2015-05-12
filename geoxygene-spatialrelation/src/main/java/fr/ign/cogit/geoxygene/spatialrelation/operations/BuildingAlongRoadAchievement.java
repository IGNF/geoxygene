package fr.ign.cogit.geoxygene.spatialrelation.operations;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.spatialrelation.api.AchievementMeasureOperation;
import fr.ign.cogit.geoxygene.spatialrelation.relation.BuildingAlongARoad;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.GeometryProximity;

public class BuildingAlongRoadAchievement implements
    AchievementMeasureOperation {

  // ***************
  // PROPERTIES
  // ***************

  private IFeature building;
  private IFeatureCollection<IFeature> roads;
  private BuildingAlongARoad relation;

  // ***************
  // CONSTRUCTOR
  // ***************

  public BuildingAlongRoadAchievement(IFeature building,
      IFeatureCollection<IFeature> roads, BuildingAlongARoad relation) {
    super();
    this.building = building;
    this.roads = roads;
    this.relation = relation;
  }

  // ***************
  // METHODS
  // ***************
  @Override
  public void compute() {
    // TODO
    // Find the closest road for this building
    IFeature closestRoad = roads.get(0);
    GeometryProximity distanceMin = new GeometryProximity(building.getGeom(),
        closestRoad.getGeom());
    for (IFeature road : roads) {
      GeometryProximity distanceEval = new GeometryProximity(
          building.getGeom(), road.getGeom());
      if (distanceEval.getDistance() < distanceMin.getDistance()) {
        distanceMin = distanceEval;
        closestRoad = road;
      }
    }
    relation.setMember2(closestRoad);
  }

  @Override
  public boolean measureAchievement() {
    // TODO Auto-generated method stub
    // if blablabla return true;
    return false;
  }

  // ***************
  // GETTERS
  // ***************
  public IFeature getBuilding() {
    return building;
  }

  public IFeatureCollection<IFeature> getRoads() {
    return roads;
  }

  public BuildingAlongARoad getRelation() {
    return relation;
  }

  // ***************
  // SETTERS
  // ***************

  public void setBuilding(IFeature building) {
    this.building = building;
  }

  public void setRoads(IFeatureCollection<IFeature> roads) {
    this.roads = roads;
  }

  public void setRelation(BuildingAlongARoad relation) {
    this.relation = relation;
  }

}
