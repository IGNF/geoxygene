package fr.ign.cogit.geoxygene.osm.quality.spatialrelations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class BuildingConsistencyQualityAssessment {

  private IFeatureCollection<IFeature> features;
  private IFeatureCollection<IFeature> buildings;
  private Map<IFeature, Double> distToCentroid, distToEdge;
  private Set<POIBuilding> poiBuildings = new HashSet<>();

  public Set<POIBuilding> getPoiBuildings() {
    return poiBuildings;
  }

  public void setPoiBuildings(Set<POIBuilding> poiBuildings) {
    this.poiBuildings = poiBuildings;
  }

  public BuildingConsistencyQualityAssessment(
      IFeatureCollection<IFeature> features,
      IFeatureCollection<IFeature> buildings) {
    super();
    this.features = features;
    this.setBuildings(buildings);
    this.distToCentroid = new HashMap<>();
    this.distToEdge = new HashMap<>();
  }

  public IFeatureCollection<IFeature> getFeatures() {
    return features;
  }

  public void setFeatures(IFeatureCollection<IFeature> features) {
    this.features = features;
  }

  public IFeatureCollection<IFeature> getBuildings() {
    return buildings;
  }

  public void setBuildings(IFeatureCollection<IFeature> buildings) {
    this.buildings = buildings;
  }

  public Map<IFeature, Double> getDistToCentroid() {
    return distToCentroid;
  }

  public void setDistToCentroid(Map<IFeature, Double> distToCentroid) {
    this.distToCentroid = distToCentroid;
  }

  public Map<IFeature, Double> getDistToEdge() {
    return distToEdge;
  }

  public void setDistToEdge(Map<IFeature, Double> distToEdge) {
    this.distToEdge = distToEdge;
  }

  public void assessBuildingConsistency() {

    // group atms as banks
    for (IFeature bankBuilding : this.buildings) {
      Collection<IFeature> containedPois = this.features
          .select(bankBuilding.getGeom());
      if (containedPois.size() > 1) {
        this.getPoiBuildings()
            .add(new POIBuilding(bankBuilding, containedPois));
      }
    }
  }

  public class POIBuilding {

    private IFeature building;
    private Collection<IFeature> pois;
    private double meanDistanceCentroid, stdDistanceCentroid, meanDistanceEdge,
        stdDistanceEdge, maxDistanceEdge, minDistanceEdge, maxDistanceCentroid,
        minDistanceCentroid;

    public POIBuilding(IFeature building, Collection<IFeature> pois) {
      super();
      this.setBuilding(building);
      this.setPois(pois);
      this.computeDistances();
      this.computeStats();
    }

    @SuppressWarnings("unchecked")
    private void computeDistances() {
      for (IFeature feat : this.pois) {
        // compute distance to centroid
        IDirectPosition centroid = building.getGeom().centroid();
        getDistToCentroid().put(feat,
            centroid.distance2D(feat.getGeom().centroid()));

        // get building geometry as a Polygon (it is sometimes encoded as a
        // multi-polygon
        IPolygon buildingGeom = null;
        if (building.getGeom() instanceof IPolygon)
          buildingGeom = (IPolygon) building.getGeom();
        else if (building.getGeom() instanceof IMultiSurface<?>) {
          buildingGeom = CommonAlgorithmsFromCartAGen.getBiggerFromMultiSurface(
              (IMultiSurface<IOrientableSurface>) building.getGeom());
        } else {
          getDistToEdge().put(feat, null);
          continue;
        }

        // compute distance to edge
        IDirectPosition nearest = CommonAlgorithms
            .getNearestPoint(buildingGeom.exteriorLineString(), feat.getGeom());
        getDistToEdge().put(feat,
            nearest.distance2D(feat.getGeom().centroid()));
      }
    }

    private void computeStats() {
      SummaryStatistics centroidStats = new SummaryStatistics();
      SummaryStatistics edgeStats = new SummaryStatistics();
      for (IFeature poi : pois) {
        centroidStats.addValue(getDistToCentroid().get(poi));
        edgeStats.addValue(getDistToEdge().get(poi));
      }
      meanDistanceCentroid = centroidStats.getMean();
      stdDistanceCentroid = centroidStats.getStandardDeviation();
      minDistanceCentroid = centroidStats.getMin();
      maxDistanceCentroid = centroidStats.getMax();
      meanDistanceEdge = edgeStats.getMean();
      stdDistanceEdge = edgeStats.getStandardDeviation();
      minDistanceEdge = edgeStats.getMin();
      maxDistanceEdge = edgeStats.getMax();
    }

    public IFeature getBuilding() {
      return building;
    }

    public void setBuilding(IFeature building) {
      this.building = building;
    }

    public Collection<IFeature> getPois() {
      return pois;
    }

    public void setPois(Collection<IFeature> pois) {
      this.pois = pois;
    }

    public double getMeanDistanceCentroid() {
      return meanDistanceCentroid;
    }

    public void setMeanDistanceCentroid(double meanDistance) {
      this.meanDistanceCentroid = meanDistance;
    }

    public double getStdDistanceCentroid() {
      return stdDistanceCentroid;
    }

    public void setStdDistanceCentroid(double stdDistance) {
      this.stdDistanceCentroid = stdDistance;
    }

    public double getMeanDistanceEdge() {
      return meanDistanceEdge;
    }

    public void setMeanDistanceEdge(double meanDistance) {
      this.meanDistanceEdge = meanDistance;
    }

    public double getStdDistanceEdge() {
      return stdDistanceEdge;
    }

    public void setStdDistanceEdge(double stdDistance) {
      this.stdDistanceEdge = stdDistance;
    }

    public double getMaxDistanceEdge() {
      return maxDistanceEdge;
    }

    public void setMaxDistanceEdge(double maxDistanceEdge) {
      this.maxDistanceEdge = maxDistanceEdge;
    }

    public double getMinDistanceEdge() {
      return minDistanceEdge;
    }

    public void setMinDistanceEdge(double minDistanceEdge) {
      this.minDistanceEdge = minDistanceEdge;
    }

    public double getMaxDistanceCentroid() {
      return maxDistanceCentroid;
    }

    public void setMaxDistanceCentroid(double maxDistanceCentroid) {
      this.maxDistanceCentroid = maxDistanceCentroid;
    }

    public double getMinDistanceCentroid() {
      return minDistanceCentroid;
    }

    public void setMinDistanceCentroid(double minDistanceCentroid) {
      this.minDistanceCentroid = minDistanceCentroid;
    }

  }
}
