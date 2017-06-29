package fr.ign.cogit.geoxygene.osm.quality.spatialrelations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class StoreQualityAssessment {

  private IFeatureCollection<IFeature> cinemas, cafes, gifts, hairdresser;
  private IFeatureCollection<IFeature> buildings;
  private Map<IFeature, Double> distToCentroid, distToEdge;

  public StoreQualityAssessment(IFeatureCollection<IFeature> cinemas,
      IFeatureCollection<IFeature> cafes, IFeatureCollection<IFeature> gifts,
      IFeatureCollection<IFeature> hairdressers,
      IFeatureCollection<IFeature> buildings) {
    super();
    this.cinemas = cinemas;
    this.cafes = cafes;
    this.gifts = gifts;
    this.hairdresser = hairdressers;
    this.setBuildings(buildings);
    this.distToCentroid = new HashMap<>();
    this.distToEdge = new HashMap<>();
  }

  public IFeatureCollection<IFeature> getCinemas() {
    return cinemas;
  }

  public void setCinemas(IFeatureCollection<IFeature> cinemas) {
    this.cinemas = cinemas;
  }

  public IFeatureCollection<IFeature> getCafes() {
    return cafes;
  }

  public void setCafes(IFeatureCollection<IFeature> cafes) {
    this.cafes = cafes;
  }

  public IFeatureCollection<IFeature> getGifts() {
    return gifts;
  }

  public void setGifts(IFeatureCollection<IFeature> gifts) {
    this.gifts = gifts;
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
   * Computes the distance between the store point feature to the centroid of
   * the containing building and to the nearest edge of the containing building,
   * as a proxy for the "enter" of the store.
   */
  @SuppressWarnings("unchecked")
  public void assessIndividualPositions() {
    // loop on all the ATM features
    for (IFeature feat : this.getAllFeatures()) {
      // get the building containing this atm
      IFeature building = this.getContainingBuilding(feat);
      if (building == null) {
        this.getDistToCentroid().put(feat, null);
        this.getDistToEdge().put(feat, null);
        continue;
      }

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

  private Collection<IFeature> getAllFeatures() {
    Collection<IFeature> coln = new HashSet<>();
    coln.addAll(cafes);
    coln.addAll(cinemas);
    coln.addAll(gifts);
    coln.addAll(hairdresser);

    return coln;
  }

  public IFeatureCollection<IFeature> getHairdresser() {
    return hairdresser;
  }

  public void setHairdresser(IFeatureCollection<IFeature> hairdresser) {
    this.hairdresser = hairdresser;
  }
}
