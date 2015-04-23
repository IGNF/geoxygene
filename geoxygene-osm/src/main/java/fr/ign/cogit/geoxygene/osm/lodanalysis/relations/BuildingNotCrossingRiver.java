package fr.ign.cogit.geoxygene.osm.lodanalysis.relations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.util.multicriteriadecision.Criterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.ConclusionIntervals;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.RobustELECTRETRIMethod;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.osm.lodanalysis.LoDCategory;
import fr.ign.cogit.geoxygene.osm.lodanalysis.individual.LoDMultiCriteria;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObj;

public class BuildingNotCrossingRiver extends LoDSpatialRelationDetection {

  private double distanceThreshold;

  public BuildingNotCrossingRiver(IFeatureCollection<IGeneObj> features1,
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
    // features1 is the set of buildings
    for (IGeneObj building : getFeatures1()) {
      // get the LoD category for the building
      Map<String, Double> valeursCourantes = new HashMap<String, Double>();
      for (Criterion crit : electre.getCriteria()) {
        Map<String, Object> param = LoDMultiCriteria.initParameters(
            (OsmGeneObj) building, crit);
        valeursCourantes.put(crit.getName(), new Double(crit.value(param)));
      }
      LoDCategory category1 = LoDCategory.valueOf(electre.decision(
          electre.getCriteria(), valeursCourantes, conclusion).getCategory());

      // searches for a water areas that cross the building
      Collection<IGeneObj> rivers = getFeatures2().select(
          building.getGeom().buffer(distanceThreshold));

      if (rivers.size() == 0) {
        continue;
      } else {
        // there should be only one built-up area
        OsmGeneObj river = (OsmGeneObj) rivers.iterator().next();

        // now check the LoDs
        Map<String, Double> valeursCourantes2 = new HashMap<String, Double>();
        for (Criterion crit : electre.getCriteria()) {
          Map<String, Object> param = LoDMultiCriteria.initParameters(river,
              crit);
          valeursCourantes2.put(crit.getName(), new Double(crit.value(param)));
        }
        LoDCategory category2 = LoDCategory
            .valueOf(electre.decision(electre.getCriteria(), valeursCourantes2,
                conclusion).getCategory());
        if (Math.abs(category1.ordinal() - category2.ordinal()) < this
            .getLodDiffThreshold())
          continue;
        // build a new instance
        instances.add(new LoDSpatialRelation(building, river, category1,
            category2, getName()));
      }
    }
    return instances;
  }

  @Override
  public String getName() {
    return BuildingNotCrossingRiver.class.getSimpleName();
  }

  public void setDistanceThreshold(double distanceThreshold) {
    this.distanceThreshold = distanceThreshold;
  }

  public double getDistanceThreshold() {
    return distanceThreshold;
  }

}
