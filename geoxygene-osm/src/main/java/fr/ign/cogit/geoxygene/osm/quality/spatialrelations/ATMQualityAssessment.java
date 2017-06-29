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
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class ATMQualityAssessment {

  private IFeatureCollection<IFeature> features;
  private IFeatureCollection<IFeature> buildings;
  private Map<IFeature, Double> distToCentroid, distToEdge;
  private IFeatureCollection<IFeature> bankBuildings;
  private Set<BankBuilding> banks = new HashSet<>();

  public Set<BankBuilding> getBanks() {
    return banks;
  }

  public void setBanks(Set<BankBuilding> banks) {
    this.banks = banks;
  }

  public ATMQualityAssessment(IFeatureCollection<IFeature> features,
      IFeatureCollection<IFeature> buildings) {
    super();
    this.features = features;
    this.setBuildings(buildings);
    this.distToCentroid = new HashMap<>();
    this.distToEdge = new HashMap<>();
    this.bankBuildings = new FT_FeatureCollection<>();
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

  private IFeature getContainingBuilding(IFeature atmFeature) {
    Collection<IFeature> selection = this.getBuildings()
        .select(atmFeature.getGeom());
    if (selection != null && selection.size() != 0)
      return selection.iterator().next();
    return null;
  }

  /**
   * Computes the distance between the ATM point feature to the centroid of the
   * containing building and to the nearest edge of the containing building, as
   * a proxy for the "walls" of the building where the actual ATM is located.
   */
  @SuppressWarnings("unchecked")
  public void assessIndividualPositions() {
    // loop on all the ATM features
    for (IFeature feat : this.getFeatures()) {
      // get the building containing this atm
      IFeature building = this.getContainingBuilding(feat);
      if (building == null) {
        this.getDistToCentroid().put(feat, null);
        this.getDistToEdge().put(feat, null);
        continue;
      }

      this.bankBuildings.add(building);
      // compute distance to centroid
      IDirectPosition centroid = building.getGeom().centroid();
      this.getDistToCentroid().put(feat,
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
        this.getDistToEdge().put(feat, null);
        continue;
      }

      // compute distance to edge
      IDirectPosition nearest = CommonAlgorithms
          .getNearestPoint(buildingGeom.exteriorLineString(), feat.getGeom());
      this.getDistToEdge().put(feat,
          nearest.distance2D(feat.getGeom().centroid()));
    }
  }

  public void assessBuildingConsistency() {
    // check that the basic positions have been computed
    if (this.getDistToCentroid().size() == 0)
      this.assessIndividualPositions();

    // group atms as banks
    for (IFeature bankBuilding : this.bankBuildings) {
      Collection<IFeature> containedAtms = this.features
          .select(bankBuilding.getGeom());
      if (containedAtms.size() != 0) {
        banks.add(new BankBuilding(bankBuilding, containedAtms));
      }
    }
  }

  public class BankBuilding {

    private IFeature building;
    private Collection<IFeature> atms;
    private double meanDistanceCentroid, stdDistanceCentroid, meanDistanceEdge,
        stdDistanceEdge;

    public BankBuilding(IFeature building, Collection<IFeature> atms) {
      super();
      this.setBuilding(building);
      this.setAtms(atms);
      this.computeStats();
    }

    private void computeStats() {
      SummaryStatistics centroidStats = new SummaryStatistics();
      SummaryStatistics edgeStats = new SummaryStatistics();
      for (IFeature atm : atms) {
        centroidStats.addValue(getDistToCentroid().get(atm));
        edgeStats.addValue(getDistToEdge().get(atm));
      }
      meanDistanceCentroid = centroidStats.getMean();
      stdDistanceCentroid = centroidStats.getStandardDeviation();
      meanDistanceEdge = edgeStats.getMean();
      stdDistanceEdge = edgeStats.getStandardDeviation();
    }

    public IFeature getBuilding() {
      return building;
    }

    public void setBuilding(IFeature building) {
      this.building = building;
    }

    public Collection<IFeature> getAtms() {
      return atms;
    }

    public void setAtms(Collection<IFeature> atms) {
      this.atms = atms;
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

  }
}
