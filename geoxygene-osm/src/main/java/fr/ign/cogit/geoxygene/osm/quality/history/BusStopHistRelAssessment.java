package fr.ign.cogit.geoxygene.osm.quality.history;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.contrib.algorithms.SpatialQuery;
import fr.ign.cogit.geoxygene.osm.quality.spatialrelations.OSMVersionComparator;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;

public class BusStopHistRelAssessment {

  private Map<String, List<IFeature>> busStops;
  private IFeatureCollection<IFeature> roads;
  private IFeatureCollection<IFeature> buildings;
  private Map<String, Map<Long, Double>> distToRoads, distToBuildings;

  public BusStopHistRelAssessment(Map<String, List<IFeature>> busStops,
      IFeatureCollection<IFeature> roads,
      IFeatureCollection<IFeature> buildings) {
    super();
    this.busStops = busStops;
    this.roads = roads;
    this.setBuildings(buildings);
    this.distToRoads = new HashMap<>();
    this.distToBuildings = new HashMap<>();
  }

  public Map<String, List<IFeature>> getBusStops() {
    return busStops;
  }

  public void setBusStops(Map<String, List<IFeature>> busStops) {
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

  public Map<String, Map<Long, Double>> getDistToRoads() {
    return distToRoads;
  }

  public void setDistToRoads(Map<String, Map<Long, Double>> distToRoads) {
    this.distToRoads = distToRoads;
  }

  public Map<String, Map<Long, Double>> getDistToBuildings() {
    return distToBuildings;
  }

  public void setDistToBuildings(
      Map<String, Map<Long, Double>> distToBuildings) {
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
    for (String id : this.getBusStops().keySet()) {
      List<IFeature> busStops = this.getBusStops().get(id);
      Collections.sort(busStops, new OSMVersionComparator());
      Map<Long, Double> mapRoads = new HashMap<>();
      Map<Long, Double> mapBuildings = new HashMap<>();
      System.out.println(busStops.size() + " versions for feature " + id);
      for (IFeature feat : busStops) {
        // first compute the distance to nearest road
        IFeature nearestRoad = SpatialQuery.selectNearestFeature(feat.getGeom(),
            roads, 20.0);
        if (nearestRoad == null) {
          mapRoads.put((Long) feat.getAttribute("version"), -1.0);
        } else {
          IDirectPosition nearestPt = CommonAlgorithms
              .getNearestPoint(nearestRoad.getGeom(), feat.getGeom());
          mapRoads.put((Long) feat.getAttribute("version"),
              nearestPt.distance2D(feat.getGeom().centroid()));
        }

        // get the building containing this atm
        IFeature building = this.getContainingBuilding(feat);
        if (building != null) {
          mapBuildings.put((Long) feat.getAttribute("version"), 0.0);
          continue;
        }

        // get the nearest building
        IFeature nearestBuilding = SpatialQuery
            .selectNearestFeature(feat.getGeom(), buildings, 25.0);
        if (nearestBuilding == null) {
          mapBuildings.put((Long) feat.getAttribute("version"), -1.0);
        } else {
          IDirectPosition nearestPt = CommonAlgorithms
              .getNearestPoint(nearestBuilding.getGeom(), feat.getGeom());
          mapBuildings.put((Long) feat.getAttribute("version"),
              nearestPt.distance2D(feat.getGeom().centroid()));
        }
      }
      this.getDistToRoads().put(id, mapRoads);
      this.getDistToBuildings().put(id, mapBuildings);
    }
  }

}
