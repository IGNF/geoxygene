package fr.ign.cogit.geoxygene.osm.lodanalysis.relations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.Criterion;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.ConclusionIntervals;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.electretri.RobustELECTRETRIMethod;
import fr.ign.cogit.geoxygene.osm.lodanalysis.LoDCategory;
import fr.ign.cogit.geoxygene.osm.lodanalysis.individual.LoDMultiCriteria;
import fr.ign.cogit.geoxygene.osm.schema.OSMFeature;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;
import fr.ign.cogit.geoxygene.schemageo.api.support.elementsIndependants.ElementIndependant;

public class HouseGroupInForest extends LoDSpatialRelationDetection {

  private double overlapThreshold;

  public HouseGroupInForest(IFeatureCollection<IFeature> features1,
      IFeatureCollection<IFeature> features2, int lodDiffThreshold,
      double overlapThreshold) {
    super(features1, features2, lodDiffThreshold);
    this.overlapThreshold = overlapThreshold;
  }

  @Override
  public Set<LoDSpatialRelation> findInstances() {
    // build the decision method and its criteria
    RobustELECTRETRIMethod electre = LoDMultiCriteria.buildELECTRETRIMethod();
    ConclusionIntervals conclusion = LoDMultiCriteria
        .initConclusion(electre.getCriteria());
    Set<LoDSpatialRelation> instances = new HashSet<LoDSpatialRelation>();
    // features1 is the set of urban blocks
    for (IFeature block : getFeatures1()) {
      // get the intersecting forests
      Collection<IFeature> forests = getFeatures2().select(block.getGeom());
      if (forests.size() == 0)
        continue;
      List<LoDCategory> categories = new ArrayList<LoDCategory>();
      for (ElementIndependant element : ((Ilot) block).getComposants()) {
        Map<String, Double> valeursCourantes = new HashMap<String, Double>();
        for (Criterion crit : electre.getCriteria()) {
          Map<String, Object> param = LoDMultiCriteria
              .initParameters((OSMFeature) element, crit);
          valeursCourantes.put(crit.getName(), new Double(crit.value(param)));
        }
        categories.add(LoDCategory.valueOf(electre
            .decision(electre.getCriteria(), valeursCourantes, conclusion)
            .getCategory()));
      }
      LoDCategory category1 = LoDCategory.mean(categories);
      for (IFeature forest : forests) {
        IGeometry inter = forest.getGeom().intersection(block.getGeom());
        if (inter == null)
          continue;
        double overlapRatio = inter.area() / block.getGeom().area();
        if (overlapRatio > overlapThreshold) {
          Map<String, Double> valeursCourantes = new HashMap<String, Double>();
          for (Criterion crit : electre.getCriteria()) {
            Map<String, Object> param = LoDMultiCriteria
                .initParameters((OSMFeature) forest, crit);
            valeursCourantes.put(crit.getName(), new Double(crit.value(param)));
          }
          LoDCategory category2 = LoDCategory.valueOf(electre
              .decision(electre.getCriteria(), valeursCourantes, conclusion)
              .getCategory());
          System.out.println(category1);
          System.out.println(category2);
          if (Math.abs(category1.ordinal() - category2.ordinal()) < this
              .getLodDiffThreshold())
            continue;
          instances.add(new LoDSpatialRelation(block, forest, category1,
              category2, getName()));
        }
      }

    }
    return instances;
  }

  @Override
  public String getName() {
    return HouseGroupInForest.class.getSimpleName();
  }

}
