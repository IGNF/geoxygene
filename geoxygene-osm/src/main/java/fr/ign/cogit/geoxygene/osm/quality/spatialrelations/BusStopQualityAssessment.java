package fr.ign.cogit.geoxygene.osm.quality.spatialrelations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.contrib.algorithms.SpatialQuery;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public class BusStopQualityAssessment {

  private IFeatureCollection<IFeature> busStops, roads;
  private IFeatureCollection<IFeature> buildings;
  private Map<IFeature, Double> distToRoads, distToBuildings;

  public BusStopQualityAssessment(IFeatureCollection<IFeature> busStops,
      IFeatureCollection<IFeature> roads,
      IFeatureCollection<IFeature> buildings) {
    super();
    this.busStops = busStops;
    this.roads = roads;
    this.setBuildings(buildings);
    this.distToRoads = new HashMap<>();
    this.distToBuildings = new HashMap<>();
  }

  public IFeatureCollection<IFeature> getBusStops() {
    return busStops;
  }

  public void setBusStops(IFeatureCollection<IFeature> busStops) {
    this.busStops = busStops;
  }

  public IFeatureCollection<IFeature> getRoads() {
    return roads;
  }

  public void setRoads(IFeatureCollection<IFeature> roads) {
    this.roads = roads;
  }

  public IFeatureCollection<IFeature> getBuildings() {
    return buildings;
  }

  public void setBuildings(IFeatureCollection<IFeature> buildings) {
    this.buildings = buildings;
  }

  public Map<IFeature, Double> getDistToRoads() {
    return distToRoads;
  }

  public void setDistToRoads(Map<IFeature, Double> distToRoads) {
    this.distToRoads = distToRoads;
  }

  public Map<IFeature, Double> getDistToBuildings() {
    return distToBuildings;
  }

  public void setDistToBuildings(Map<IFeature, Double> distToBuildings) {
    this.distToBuildings = distToBuildings;
  }

  private IFeature getContainingBuilding(IFeature atmFeature) {
    Collection<IFeature> selection = this.getBuildings()
        .select(atmFeature.getGeom());
    if (selection != null && selection.size() != 0)
      return selection.iterator().next();
    return null;
  }

  /**
   * Computes the distance between the store point feature to the centroid of
   * the containing building and to the nearest edge of the containing building,
   * as a proxy for the "enter" of the store.
   */
  public void assessIndividualPositions() {
    // loop on all the ATM features
    for (IFeature feat : this.getBusStops()) {
      // first compute the distance to nearest road
      IFeature nearestRoad = SpatialQuery.selectNearestFeature(feat.getGeom(),
          roads, 20.0);
      if (nearestRoad == null) {
        this.getDistToRoads().put(feat, -1.0);
      } else {
        IDirectPosition nearestPt = CommonAlgorithms
            .getNearestPoint(nearestRoad.getGeom(), feat.getGeom());
        this.getDistToRoads().put(feat,
            nearestPt.distance2D(feat.getGeom().centroid()));
      }

      // get the building containing this atm
      IFeature building = this.getContainingBuilding(feat);
      if (building != null) {
        this.getDistToBuildings().put(feat, 0.0);
        continue;
      }

      // get the nearest building
      IFeature nearestBuilding = SpatialQuery
          .selectNearestFeature(feat.getGeom(), buildings, 25.0);
      if (nearestBuilding == null) {
        this.getDistToBuildings().put(feat, -1.0);
      } else {
        IDirectPosition nearestPt = CommonAlgorithms
            .getNearestPoint(nearestBuilding.getGeom(), feat.getGeom());
        this.getDistToBuildings().put(feat,
            nearestPt.distance2D(feat.getGeom().centroid()));
      }
    }
  }

}
