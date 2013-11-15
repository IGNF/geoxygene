package fr.ign.cogit.osm.lodanalysis.relations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.util.SpatialQuery;
import fr.ign.cogit.cartagen.util.multicriteriadecision.Criterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.ConclusionIntervals;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.RobustELECTRETRIMethod;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.osm.lodanalysis.LoDCategory;
import fr.ign.cogit.osm.lodanalysis.individual.LoDMultiCriteria;
import fr.ign.cogit.osm.schema.OsmGeneObj;

public class BusStopAlongRoad extends LoDSpatialRelationDetection {

  private double distanceThreshold;

  public BusStopAlongRoad(IFeatureCollection<IGeneObj> features1,
      IFeatureCollection<IGeneObj> features2, int lodDiffThreshold,
      double distanceThreshold) {
    super(features1, features2, lodDiffThreshold);
    this.distanceThreshold = distanceThreshold;
  }

  @Override
  public Set<LoDSpatialRelation> findInstances() {
    // build the decision method and its criteria
    RobustELECTRETRIMethod electre = LoDMultiCriteria.buildELECTRETRIMethod();
    ConclusionIntervals conclusion = LoDMultiCriteria.initConclusion(electre
        .getCriteria());
    Set<LoDSpatialRelation> instances = new HashSet<LoDSpatialRelation>();
    // features1 is the set of bus stops
    for (IGeneObj busStop : getFeatures1()) {
      // select the nearest road
      Vector<Object> nearest = SpatialQuery.selectNearestWithDistance(
          busStop.getGeom(), getFeatures2(), distanceThreshold * 10.0);
      if (nearest.get(0) == null)
        continue;
      if ((Double) nearest.get(1) < distanceThreshold)
        continue;
      // get the LoD category for the bus stop
      Map<String, Double> valeursCourantes = new HashMap<String, Double>();
      for (Criterion crit : electre.getCriteria()) {
        Map<String, Object> param = LoDMultiCriteria.initParameters(
            (OsmGeneObj) busStop, crit);
        valeursCourantes.put(crit.getName(), new Double(crit.value(param)));
      }
      LoDCategory category1 = LoDCategory.valueOf(electre.decision(
          electre.getCriteria(), valeursCourantes, conclusion).getCategory());
      // get the road LoD category
      OsmGeneObj road = (OsmGeneObj) nearest.get(0);
      Map<String, Double> valeursCourantes2 = new HashMap<String, Double>();
      for (Criterion crit : electre.getCriteria()) {
        Map<String, Object> param = LoDMultiCriteria.initParameters(road, crit);
        valeursCourantes2.put(crit.getName(), new Double(crit.value(param)));
      }
      LoDCategory category2 = LoDCategory.valueOf(electre.decision(
          electre.getCriteria(), valeursCourantes2, conclusion).getCategory());
      if (Math.abs(category1.ordinal() - category2.ordinal()) < this
          .getLodDiffThreshold())
        continue;
      // add a new instance of the relation
      instances.add(new LoDSpatialRelation(busStop, road, category1, category2,
          getName()));
    }
    return instances;
  }

  @Override
  public String getName() {
    return BusStopAlongRoad.class.getSimpleName();
  }

  public void setDistanceThreshold(double distanceThreshold) {
    this.distanceThreshold = distanceThreshold;
  }

  public double getDistanceThreshold() {
    return distanceThreshold;
  }

}
