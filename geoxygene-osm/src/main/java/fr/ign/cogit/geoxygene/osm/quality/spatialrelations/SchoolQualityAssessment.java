package fr.ign.cogit.geoxygene.osm.quality.spatialrelations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class SchoolQualityAssessment {

  private IFeatureCollection<IFeature> schools, schoolAreas;
  private IFeatureCollection<IFeature> buildings;
  private Map<IFeature, Double> distToCentroid, distToEdge;
  private Map<IFeature, Double> distToSchoolCentroid, distToSchoolEdge;
  private Map<IFeature, Double> buildingAreaMap, schoolAreaMap;

  public SchoolQualityAssessment(IFeatureCollection<IFeature> schools,
      IFeatureCollection<IFeature> schoolAreas,
      IFeatureCollection<IFeature> buildings) {
    super();
    this.schools = schools;
    this.setSchoolAreas(schoolAreas);
    this.setBuildings(buildings);
    this.distToCentroid = new HashMap<>();
    this.distToEdge = new HashMap<>();
    this.distToSchoolCentroid = new HashMap<>();
    this.distToSchoolEdge = new HashMap<>();
    this.schoolAreaMap = new HashMap<>();
    this.buildingAreaMap = new HashMap<>();
  }

  public IFeatureCollection<IFeature> getSchools() {
    return schools;
  }

  public void setSchools(IFeatureCollection<IFeature> schools) {
    this.schools = schools;
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

  public IFeatureCollection<IFeature> getSchoolAreas() {
    return schoolAreas;
  }

  public void setSchoolAreas(IFeatureCollection<IFeature> schoolAreas) {
    this.schoolAreas = schoolAreas;
  }

  public Map<IFeature, Double> getDistToSchoolCentroid() {
    return distToSchoolCentroid;
  }

  public void setDistToSchoolCentroid(Map<IFeature, Double> distToSchoolCentroid) {
    this.distToSchoolCentroid = distToSchoolCentroid;
  }

  public Map<IFeature, Double> getDistToSchoolEdge() {
    return distToSchoolEdge;
  }

  public void setDistToSchoolEdge(Map<IFeature, Double> distToSchoolEdge) {
    this.distToSchoolEdge = distToSchoolEdge;
  }

  public Map<IFeature, Double> getBuildingAreaMap() {
    return buildingAreaMap;
  }

  public void setBuildingAreaMap(Map<IFeature, Double> buildingAreaMap) {
    this.buildingAreaMap = buildingAreaMap;
  }

  public Map<IFeature, Double> getSchoolAreaMap() {
    return schoolAreaMap;
  }

  public void setSchoolAreaMap(Map<IFeature, Double> schoolAreaMap) {
    this.schoolAreaMap = schoolAreaMap;
  }

  private IFeature getContainingBuilding(IFeature feature) {
    Collection<IFeature> selection = this.getBuildings().select(
        feature.getGeom());
    if (selection != null && selection.size() != 0)
      return selection.iterator().next();
    return null;
  }

  private IFeature getContainingSchoolArea(IFeature feature) {
    Collection<IFeature> selection = this.getSchoolAreas().select(
        feature.getGeom());
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
    // loop on all the school features
    for (IFeature feat : this.getSchools()) {
      // first, check if it belongs to a school area
      IFeature schoolArea = this.getContainingSchoolArea(feat);
      if (schoolArea != null) {
        // compute distance to centroid
        IDirectPosition centroid = schoolArea.getGeom().centroid();
        this.getDistToSchoolCentroid().put(feat,
            centroid.distance2D(feat.getGeom().centroid()));

        // get school geometry as a Polygon (it is sometimes encoded as a
        // multi-polygon
        IPolygon schoolGeom = null;
        if (schoolArea.getGeom() instanceof IPolygon)
          schoolGeom = (IPolygon) schoolArea.getGeom();
        else if (schoolArea.getGeom() instanceof IMultiSurface<?>) {
          schoolGeom = CommonAlgorithmsFromCartAGen
              .getBiggerFromMultiSurface((IMultiSurface<IOrientableSurface>) schoolArea
                  .getGeom());
        }
        // compute distance to edge
        IDirectPosition nearest = CommonAlgorithms.getNearestPoint(
            schoolGeom.exteriorLineString(), feat.getGeom());
        this.getDistToSchoolEdge().put(feat,
            nearest.distance2D(feat.getGeom().centroid()));

        this.getSchoolAreaMap().put(feat, schoolArea.getGeom().area());
      }

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
        buildingGeom = CommonAlgorithmsFromCartAGen
            .getBiggerFromMultiSurface((IMultiSurface<IOrientableSurface>) building
                .getGeom());
      } else {
        this.getDistToEdge().put(feat, null);
        continue;
      }

      // compute distance to edge
      IDirectPosition nearest = CommonAlgorithms.getNearestPoint(
          buildingGeom.exteriorLineString(), feat.getGeom());
      this.getDistToEdge().put(feat,
          nearest.distance2D(feat.getGeom().centroid()));

      this.buildingAreaMap.put(feat, building.getGeom().area());
    }
  }

}
