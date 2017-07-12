package fr.ign.cogit.geoxygene.osm.lodanalysis.relations;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.osm.schema.OSMFeature;

public class FunctionalSite {

  public enum FunctionalSiteType {
    SCHOOL, HOSPITAL
  }

  public static int getFunctionalBelonging(OSMFeature feature,
      OSMFeature component) {
    FunctionalSiteType type = getType(feature);
    if (type.equals(FunctionalSiteType.HOSPITAL))
      return getHospitalFuncBelonging(feature, component);
    if (type.equals(FunctionalSiteType.SCHOOL))
      return getSchoolFuncBelonging(feature, component);
    return 0;
  }

  public static int getSpatialBelonging(OSMFeature feature,
      OSMFeature component) {
    FunctionalSiteType type = getType(feature);
    if (type.equals(FunctionalSiteType.HOSPITAL))
      return getHospitalSpatialBelonging(feature, component);
    if (type.equals(FunctionalSiteType.SCHOOL))
      return getSchoolSpatialBelonging(feature, component);
    return 0;
  }

  private static FunctionalSiteType getType(OSMFeature feature) {
    if (feature.getTags().containsKey("amenity")) {
      if ("hospital".equals(feature.getTags().get("amenity"))) {
        if (feature.getGeom() instanceof IPolygon)
          return FunctionalSiteType.HOSPITAL;
      }
      if ("school".equals(feature.getTags().get("amenity"))) {
        if (feature.getGeom() instanceof IPolygon)
          return FunctionalSiteType.SCHOOL;
      }
      if ("university".equals(feature.getTags().get("amenity"))) {
        if (feature.getGeom() instanceof IPolygon)
          return FunctionalSiteType.SCHOOL;
      }
    }
    return null;
  }

  private static int getHospitalFuncBelonging(OSMFeature feature,
      OSMFeature component) {
    int functionalScore = 0;
    // positive tags
    if (component.getTags().containsKey("building"))
      functionalScore++;
    if ("hospital".equals(component.getTags().get("building")))
      functionalScore += 5;
    if ("church".equals(component.getTags().get("building")))
      functionalScore += 3;
    if ("chapel".equals(component.getTags().get("building")))
      functionalScore += 3;
    if ("civic".equals(component.getTags().get("building")))
      functionalScore += 3;
    if ("hospital".equals(component.getTags().get("amenity")))
      functionalScore += 10;
    if ("doctors".equals(component.getTags().get("amenity")))
      functionalScore += 10;
    if ("nursing_home".equals(component.getTags().get("amenity")))
      functionalScore += 10;
    if ("service".equals(component.getTags().get("highway")))
      functionalScore += 3;

    // negative tags
    if (component.getTags().containsKey("sport"))
      functionalScore -= 3;
    if ("pitch".equals(component.getTags().get("leisure")))
      functionalScore -= 5;

    return functionalScore;
  }

  private static int getHospitalSpatialBelonging(OSMFeature feature,
      OSMFeature component) {
    if (!feature.getGeom().intersects(component.getGeom()))
      return -20;
    if (component.getGeom() instanceof IPolygon) {
      IPolygon pol = (IPolygon) component.getGeom();
      double overlap = pol.intersection(feature.getGeom()).area() / pol.area();
      // above 0.6, it's a positive likelihood
      if (overlap > 0.6) {
        return (int) Math.round((overlap - 0.6) * 20);
      }
      // below 0.3, it's a negative likelihood
      if (overlap < 0.3) {
        return (int) -Math.round((0.3 - overlap) * 20);
      }
    }
    // the linear case
    if (component.getGeom() instanceof ILineString) {
      if (feature.getGeom().contains(component.getGeom()))
        return 10;
      IGeometry inter = ((IPolygon) feature.getGeom()).exteriorLineString()
          .intersection(component.getGeom());
      if (inter instanceof IMultiPoint)
        return 5;
    }
    return 0;
  }

  private static int getSchoolFuncBelonging(OSMFeature feature,
      OSMFeature component) {

    int functionalScore = 0;
    // positive tags
    if (component.getTags().containsKey("building"))
      functionalScore++;
    if ("dormitory".equals(component.getTags().get("building")))
      functionalScore += 5;
    if ("church".equals(component.getTags().get("building")))
      functionalScore += 3;
    if ("chapel".equals(component.getTags().get("building")))
      functionalScore += 3;
    if ("civic".equals(component.getTags().get("building")))
      functionalScore += 3;
    if ("school".equals(component.getTags().get("amenity")))
      functionalScore += 10;
    if ("university".equals(component.getTags().get("amenity")))
      functionalScore += 10;
    if ("college".equals(component.getTags().get("amenity")))
      functionalScore += 10;
    if ("library".equals(component.getTags().get("amenity")))
      functionalScore += 10;
    if ("pitch".equals(component.getTags().get("leisure")))
      functionalScore += 5;
    if (component.getTags().containsKey("sport"))
      functionalScore += 3;

    // negative tags
    if ("primary".equals(component.getTags().get("highway")))
      functionalScore -= 15;
    if ("secondary".equals(component.getTags().get("highway")))
      functionalScore -= 15;
    if ("tertiary".equals(component.getTags().get("highway")))
      functionalScore -= 10;
    if ("residential".equals(component.getTags().get("highway")))
      functionalScore -= 10;
    if ("residential".equals(component.getTags().get("building")))
      functionalScore -= 15;
    if ("commercial".equals(component.getTags().get("building")))
      functionalScore -= 10;
    if ("industrial".equals(component.getTags().get("building")))
      functionalScore -= 10;
    return functionalScore;
  }

  private static int getSchoolSpatialBelonging(OSMFeature feature,
      OSMFeature component) {
    if (!feature.getGeom().intersects(component.getGeom()))
      return -20;
    if (component.getGeom() instanceof IPolygon) {
      IPolygon pol = (IPolygon) component.getGeom();
      double overlap = pol.intersection(feature.getGeom()).area() / pol.area();
      // above 0.6, it's a positive likelihood
      if (overlap > 0.6) {
        return (int) Math.round((overlap - 0.6) * 20);
      }
      // below 0.3, it's a negative likelihood
      if (overlap < 0.3) {
        return (int) -Math.round((0.3 - overlap) * 20);
      }
    }
    return 0;
  }
}
