package fr.ign.cogit.geoxygene.osm.lodanalysis.relations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.Criterion;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.ConclusionIntervals;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.electretri.RobustELECTRETRIMethod;
import fr.ign.cogit.geoxygene.osm.lodanalysis.LoDCategory;
import fr.ign.cogit.geoxygene.osm.lodanalysis.individual.LoDMultiCriteria;
import fr.ign.cogit.geoxygene.osm.schema.OSMFeature;

public class CoastlineCrossingLand extends LoDSpatialRelationDetection {

  public CoastlineCrossingLand(IFeatureCollection<IFeature> features1,
      IFeatureCollection<IFeature> features2, int lodDiffThreshold) {
    super(features1, features2, lodDiffThreshold);
  }

  @Override
  public Set<LoDSpatialRelation> findInstances() {
    // build the decision method and its criteria
    RobustELECTRETRIMethod electre = LoDMultiCriteria.buildELECTRETRIMethod();
    ConclusionIntervals conclusion = LoDMultiCriteria
        .initConclusion(electre.getCriteria());
    Set<LoDSpatialRelation> instances = new HashSet<LoDSpatialRelation>();
    // features1 is the set of urban blocks
    for (IFeature coastline : getFeatures1()) {
      // get the land use parcels that intersect the coastline
      Collection<IFeature> parcels = getFeatures2().select(coastline.getGeom());
      Map<String, Double> valeursCourantes = new HashMap<String, Double>();
      for (Criterion crit : electre.getCriteria()) {
        Map<String, Object> param = LoDMultiCriteria
            .initParameters((OSMFeature) coastline, crit);
        valeursCourantes.put(crit.getName(), new Double(crit.value(param)));
      }
      LoDCategory category1 = LoDCategory.valueOf(
          electre.decision(electre.getCriteria(), valeursCourantes, conclusion)
              .getCategory());
      // qualify the intersection with each parcel
      for (IFeature parcel : parcels) {
        if (!coastline.getGeom().crosses(parcel.getGeom()))
          continue;
        Map<String, Double> valeursCourantes2 = new HashMap<String, Double>();
        for (Criterion crit : electre.getCriteria()) {
          Map<String, Object> param = LoDMultiCriteria
              .initParameters((OSMFeature) parcel, crit);
          valeursCourantes2.put(crit.getName(), new Double(crit.value(param)));
        }
        LoDCategory category2 = LoDCategory.valueOf(electre
            .decision(electre.getCriteria(), valeursCourantes2, conclusion)
            .getCategory());
        // check LoD difference
        if (Math.abs(category1.ordinal() - category2.ordinal()) < this
            .getLodDiffThreshold())
          continue;
        instances.add(new LoDSpatialRelation(coastline, parcel, category1,
            category2, getName()));
      }
    }

    return instances;
  }

  @Override
  public String getName() {
    return CoastlineCrossingLand.class.getSimpleName();
  }

}
