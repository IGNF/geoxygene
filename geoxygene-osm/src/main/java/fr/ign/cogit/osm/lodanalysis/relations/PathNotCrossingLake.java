package fr.ign.cogit.osm.lodanalysis.relations;

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
import fr.ign.cogit.osm.lodanalysis.LoDCategory;
import fr.ign.cogit.osm.lodanalysis.individual.LoDMultiCriteria;
import fr.ign.cogit.osm.schema.OsmGeneObj;

public class PathNotCrossingLake extends LoDSpatialRelationDetection {

  public PathNotCrossingLake(IFeatureCollection<IGeneObj> features1,
      IFeatureCollection<IGeneObj> features2, int lodDiffThreshold) {
    super(features1, features2, lodDiffThreshold);
  }

  @Override
  public Set<LoDSpatialRelation> findInstances() {
    // build the decision method and its criteria
    RobustELECTRETRIMethod electre = LoDMultiCriteria.buildELECTRETRIMethod();
    ConclusionIntervals conclusion = LoDMultiCriteria.initConclusion(electre
        .getCriteria());
    Set<LoDSpatialRelation> instances = new HashSet<LoDSpatialRelation>();
    // features1 is the set of buildings
    for (IGeneObj lake : getFeatures1()) {
      Collection<IGeneObj> pathes = getFeatures2().select(lake.getGeom());

      if (pathes.size() == 0)
        continue;

      // get the LoD category for the lake
      Map<String, Double> valeursCourantes = new HashMap<String, Double>();
      for (Criterion crit : electre.getCriteria()) {
        Map<String, Object> param = LoDMultiCriteria.initParameters(
            (OsmGeneObj) lake, crit);
        valeursCourantes.put(crit.getName(), new Double(crit.value(param)));
      }
      LoDCategory category1 = LoDCategory.valueOf(electre.decision(
          electre.getCriteria(), valeursCourantes, conclusion).getCategory());

      for (IGeneObj path : pathes) {
        // check the LoDs
        Map<String, Double> valeursCourantes2 = new HashMap<String, Double>();
        for (Criterion crit : electre.getCriteria()) {
          Map<String, Object> param = LoDMultiCriteria.initParameters(
              (OsmGeneObj) path, crit);
          valeursCourantes2.put(crit.getName(), new Double(crit.value(param)));
        }
        LoDCategory category2 = LoDCategory
            .valueOf(electre.decision(electre.getCriteria(), valeursCourantes2,
                conclusion).getCategory());
        if (Math.abs(category1.ordinal() - category2.ordinal()) < this
            .getLodDiffThreshold())
          continue;
        // build a new instance
        instances.add(new LoDSpatialRelation(lake, path, category1, category2,
            getName()));
      }
    }
    return instances;
  }

  @Override
  public String getName() {
    return PathNotCrossingLake.class.getSimpleName();
  }

}
