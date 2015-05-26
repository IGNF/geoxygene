package fr.ign.cogit.geoxygene.spatialrelation.operations;

import java.util.List;

import org.apache.log4j.LogManager;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.spatialrelation.api.AchievementMeasureOperation;
import fr.ign.cogit.geoxygene.spatialrelation.relation.BuildingAlongARoad;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.OrientationMeasure;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;
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
    this.compute();
  }

  // ***************
  // METHODS
  // ***************
  @Override
  public void compute() {
    // Find the closest road for this building
    Integer i = 0;
    Integer j = 0;
    IFeature closestRoad = new DefaultFeature(roads.get(0).getGeom());
    // calculate the minimum distance between building and road
    GeometryProximity distanceMin = new GeometryProximity(building.getGeom(),
        closestRoad.getGeom());
    // Iterate to find the closest road
    for (IFeature road : roads) {
      GeometryProximity distanceEval = new GeometryProximity(
          building.getGeom(), road.getGeom());
      if (distanceEval.getDistance() < distanceMin.getDistance()) {
        // check if the distance validate the relation expression
        if (relation.getConditionOfAchievement().validate(
            distanceEval.getDistance())) {
          // check if there is no obstacle between the building and this road
          if (!findObstacles(building, road, distanceEval.getDistance())) {
            // if (checkOrientation(building, road)) {
            // LogManager.resetConfiguration();
            distanceMin = distanceEval;
            closestRoad.setGeom(road.getGeom());
            j = i;
            // }
          }
        }
      }
      i++;
    }
    if (relation.getConditionOfAchievement()
        .validate(distanceMin.getDistance())) {
      relation.setMember2(roads.get(j));
      relation.getDistance().setDistance(distanceMin.getDistance());
    } else {
      relation.setMember2(building);
      relation.getDistance().setDistance(distanceMin.getDistance());
    }

  }

  @Override
  public boolean measureAchievement() {
    // TODO Auto-generated method stub
    // if blablabla return true;
    return false;
  }

  public boolean findObstacles(IFeature building, IFeature road,
      Double distanceBR) {

    JtsAlgorithms algo = new JtsAlgorithms();
    // Find the objects intersected by the spatial relation
    // create the relation link (geometry)
    IGeometry geomSR = relation.createGeom(building, road);
    // create the buffer
    IGeometry buffer = algo.buffer(geomSR, 5);
    // Find the buildings intersected
    List<IFeature> buildings = building.getFeatureCollection(0).getElements();

    for (IFeature buildingTest : buildings) {
      // avoid the building himself
      if (buildingTest.getId() != building.getId()) {
        IGeometry intersection = algo.intersection(buildingTest.getGeom(),
            buffer);
        if (intersection != null && intersection.area() != 0.0) {

          // avoid the case : corner intersection
          Double buildingTestArea = buildingTest.getGeom().area();
          if (intersection.area() > (buildingTestArea * 0.2)) {

            // avoid the case : building on the other side of the road
            GeometryProximity d1 = new GeometryProximity(
                buildingTest.getGeom(), building.getGeom());
            if (d1.getDistance() < distanceBR) {

              // avoid the case : building on the other side of the building
              GeometryProximity d2 = new GeometryProximity(
                  buildingTest.getGeom(), road.getGeom());
              if (d2.getDistance() < distanceBR) {

                // System.out.println("intersection entre: "
                // + building.getAttribute("refCleBDUni") + "  "
                // + road.getAttribute("refCleBDUni") + " = "
                // + buildingTest.getAttribute("refCleBDUni"));
                return true;
              }
            }
          }
        }
      }
    }
    return false;
  }

  public double checkOrientation(IFeature building, IFeature road) {
    // TODO isolate the closest segment of the road

    // disable the SSR debug logger
    LogManager.shutdown();
    // calculate the building and road orientation
    try {
      IPolygon buildingSSR = SmallestSurroundingRectangleComputation
          .getSSR(building.getGeom());
      OrientationMeasure buildingOrientation = new OrientationMeasure(
          buildingSSR);
      Angle angleBuilding = new Angle(
          buildingOrientation.getGeneralOrientation());
      IPolygon roadSSR = SmallestSurroundingRectangleComputation.getSSR(road
          .getGeom());
      OrientationMeasure roadOrientation = new OrientationMeasure(roadSSR);
      Angle angleRoad = new Angle(roadOrientation.getGeneralOrientation());
      // calculate the difference between the two angles
      Angle angleBR = Angle.ecart(angleBuilding, angleRoad);
      // Double pi = Math.PI;
      // System.out.println(angleBR.getValeur());
      return angleBR.getValeur();
    } catch (ClassCastException e) {
      return 0.0;
    }
    // if (angleBR.getValeur() <= (pi / 4) && angleBR.getValeur() >= (3 * pi /
    // 4)) {
    // return true;
    // }
    // return false;
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
