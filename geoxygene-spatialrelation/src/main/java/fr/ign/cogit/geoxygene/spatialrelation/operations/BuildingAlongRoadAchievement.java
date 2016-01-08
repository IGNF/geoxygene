package fr.ign.cogit.geoxygene.spatialrelation.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.LogManager;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.spatialrelation.api.AchievementMeasureOperation;
import fr.ign.cogit.geoxygene.spatialrelation.relation.BuildingAlongARoad;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.OrientationMeasure;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;

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
  public Object compute() {

    // prepare the buildings list for the intersections finding
    List<IGeometry> buildingsGeom = new ArrayList<IGeometry>();
    for (IFeature bdg : building.getFeatureCollection(0).getElements()) {
      buildingsGeom.add(bdg.getGeom());
    }

    // Find the closest road for this building
    Integer i = 0;
    // get the close roads (via index spatial)
    Collection<IFeature> closeRoads = roads.getSpatialIndex().select(
        building.getGeom(), 25);

    if (closeRoads.size() == 0) {
      relation.setMember2(building);
      relation.getDistance().setDistance(999);
    } else {
      // get the default road to launch the search
      IFeature closestRoad = closeRoads.iterator().next();

      // calculate the minimum distance between building and road
      IDirectPositionList dpCloseList = JtsAlgorithms.getClosestPoints(
          building.getGeom(), closestRoad.getGeom());
      Double distanceMin = dpCloseList.get(0).distance(dpCloseList.get(1));
      // check if the closest road is the first road
      boolean first = true;

      // Iterate to find the closest road
      for (IFeature road : closeRoads) {
        IDirectPositionList dpEvalList = JtsAlgorithms.getClosestPoints(
            building.getGeom(), road.getGeom());
        Double distanceEval = dpEvalList.get(0).distance(dpEvalList.get(1));
        if (distanceEval <= distanceMin) {
          // check if the distance validate the relation expression : not
          // necessary because of the spatial selection
          if (relation.getConditionOfAchievement().validate(distanceEval)) {
            // check if there is no obstacle between the building and this road
            try {
              if (!findObstacles(building, road, distanceEval, buildingsGeom)) {
                // if (checkOrientation(building, road)) {
                // LogManager.resetConfiguration();
                distanceMin = distanceEval;
                closestRoad = road;
                first = false;
                // }
              }
            } catch (Exception e) {
              // Auto-generated catch block
              e.printStackTrace();
            }
          } else {
            distanceMin = distanceEval;
          }
        }
        i++;
      }

      if (first == false) {
        relation.setMember2(closestRoad);
        relation.getDistance().setDistance(distanceMin);
      } else {
        relation.setMember2(building);
        relation.getDistance().setDistance(998);
      }
    }
    return relation.getDistance();
  }

  @Override
  public boolean measureAchievement() {
    // TODO Auto-generated method stub
    // if blablabla return true;
    return false;
  }

  public boolean findObstacles(IFeature building, IFeature road,
      Double distanceBR, List<IGeometry> buildingsGeom) throws Exception {

    // Find the objects intersected by the spatial relation
    // create the relation link (geometry)
    IGeometry geomSR = relation.createGeom(building, road).buffer(2);

    // Find the buildings intersected
    List<IGeometry> listIntersect = new ArrayList<IGeometry>();
    for (IGeometry g : buildingsGeom)
      if (g.intersects(geomSR)) {
        IGeometry inter = g.intersection(geomSR);
        if (!(inter.isEmpty()) && inter.area() > 0.15 * g.area())
          listIntersect.add(g);
      }

    if (listIntersect.isEmpty()) {
      return false;
    } else {
      // check the intersection area
      // TODO be carefull to the false intersection (on the other side of the
      // road and the buildings, less than 0.2% of the area...
      return true;
    }
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
