package fr.ign.cogit.geoxygene.osm.lodanalysis.relations;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.FunctionalSite;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.util.multicriteriadecision.Criterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.ConclusionIntervals;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.RobustELECTRETRIMethod;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.osm.lodanalysis.LoDCategory;
import fr.ign.cogit.geoxygene.osm.lodanalysis.individual.LoDMultiCriteria;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObj;

public class FunctionalSiteComponents extends LoDSpatialRelationDetection {

  private int belongThreshold, excludeThreshold;
  private Set<LoDSpatialRelation> includeInstances, excludeInstances;

  public FunctionalSiteComponents(IFeatureCollection<IGeneObj> features1,
      IFeatureCollection<IGeneObj> features2, int lodDiffThreshold,
      int belongThreshold, int excludeThreshold) {
    super(features1, features2, lodDiffThreshold);
    this.belongThreshold = belongThreshold;
    this.excludeThreshold = excludeThreshold;
    includeInstances = new HashSet<LoDSpatialRelation>();
    excludeInstances = new HashSet<LoDSpatialRelation>();
  }

  @Override
  public Set<LoDSpatialRelation> findInstances() {
    // build the decision method and its criteria
    RobustELECTRETRIMethod electre = LoDMultiCriteria.buildELECTRETRIMethod();
    ConclusionIntervals conclusion = LoDMultiCriteria.initConclusion(electre
        .getCriteria());
    Set<LoDSpatialRelation> instances = new HashSet<LoDSpatialRelation>();
    // features1 is the set of functional sites
    for (IGeneObj site : getFeatures1()) {
      // get the components that intersect the site
      Collection<IGeneObj> components = getFeatures2().select(site.getGeom());
      Map<String, Double> valeursCourantes = new HashMap<String, Double>();
      for (Criterion crit : electre.getCriteria()) {
        Map<String, Object> param = LoDMultiCriteria.initParameters(
            (OsmGeneObj) site, crit);
        valeursCourantes.put(crit.getName(), new Double(crit.value(param)));
      }
      LoDCategory category1 = LoDCategory.valueOf(electre.decision(
          electre.getCriteria(), valeursCourantes, conclusion).getCategory());
      // qualify the intersection with each component
      for (IGeneObj component : components) {
        if (site.getGeom().contains(component.getGeom()))
          continue;
        Map<String, Double> valeursCourantes2 = new HashMap<String, Double>();
        for (Criterion crit : electre.getCriteria()) {
          Map<String, Object> param = LoDMultiCriteria.initParameters(
              (OsmGeneObj) component, crit);
          valeursCourantes2.put(crit.getName(), new Double(crit.value(param)));
        }
        LoDCategory category2 = LoDCategory
            .valueOf(electre.decision(electre.getCriteria(), valeursCourantes2,
                conclusion).getCategory());
        // check LoD difference
        if (Math.abs(category1.ordinal() - category2.ordinal()) < this
            .getLodDiffThreshold())
          continue;
        LoDSpatialRelation relation = new LoDSpatialRelation(site, component,
            category1, category2, getName());
        instances.add(relation);
        int funcBelong = ((FunctionalSite) site)
            .getFunctionalBelonging(component);
        int spatialBelong = ((FunctionalSite) site)
            .getSpatialBelonging(component);
        if (funcBelong + spatialBelong > belongThreshold)
          includeInstances.add(relation);
        else if (funcBelong + spatialBelong < excludeThreshold)
          excludeInstances.add(relation);
      }
    }

    return instances;
  }

  @Override
  public String getName() {
    return FunctionalSiteComponents.class.getSimpleName();
  }

  public Set<LoDSpatialRelation> getIncludeInstances() {
    return includeInstances;
  }

  public void setIncludeInstances(Set<LoDSpatialRelation> includeInstances) {
    this.includeInstances = includeInstances;
  }

  public Set<LoDSpatialRelation> getExcludeInstances() {
    return excludeInstances;
  }

  public void setExcludeInstances(Set<LoDSpatialRelation> excludeInstances) {
    this.excludeInstances = excludeInstances;
  }

}
