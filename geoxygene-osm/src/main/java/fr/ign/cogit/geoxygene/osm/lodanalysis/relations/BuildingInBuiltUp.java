package fr.ign.cogit.geoxygene.osm.lodanalysis.relations;

import java.util.Collection;
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
import fr.ign.cogit.geoxygene.osm.lodanalysis.LoDCategory;
import fr.ign.cogit.geoxygene.osm.lodanalysis.individual.LoDMultiCriteria;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObj;

public class BuildingInBuiltUp extends LoDSpatialRelationDetection {

  private double distanceThreshold;

  public BuildingInBuiltUp(IFeatureCollection<IGeneObj> features1,
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

      // searches for a built-up area that contains building
      Collection<IGeneObj> builtUps = getFeatures2().select(building.getGeom());

      if (builtUps.size() == 0) {
        // case without builtUp area: is there one close?
        // select the nearest built-up area
        if (building.getGeom() == null)
          continue;
        Vector<Object> nearest = SpatialQuery.selectNearestWithDistance(
            building.getGeom(), getFeatures2(), distanceThreshold + 1.0);
        if (nearest.get(0) == null)
          continue;
        if ((Double) nearest.get(1) > distanceThreshold)
          continue;

        // now check the LoDs
        OsmGeneObj builtUp = (OsmGeneObj) nearest.get(0);
        Map<String, Double> valeursCourantes2 = new HashMap<String, Double>();
        for (Criterion crit : electre.getCriteria()) {
          Map<String, Object> param = LoDMultiCriteria.initParameters(builtUp,
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
        instances.add(new LoDSpatialRelation(building, builtUp, category1,
            category2, getName()));
      } else {
        // there should be only one built-up area
        OsmGeneObj builtUp = (OsmGeneObj) builtUps.iterator().next();
        // test if it contains the building (no need to create a relation)
        if (builtUp.getGeom().contains(building.getGeom()))
          continue;
        // now check the LoDs
        Map<String, Double> valeursCourantes2 = new HashMap<String, Double>();
        for (Criterion crit : electre.getCriteria()) {
          Map<String, Object> param = LoDMultiCriteria.initParameters(builtUp,
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
        instances.add(new LoDSpatialRelation(building, builtUp, category1,
            category2, getName()));
      }
    }
    return instances;
  }

  @Override
  public String getName() {
    return BuildingInBuiltUp.class.getSimpleName();
  }

  public void setDistanceThreshold(double distanceThreshold) {
    this.distanceThreshold = distanceThreshold;
  }

  public double getDistanceThreshold() {
    return distanceThreshold;
  }

}
