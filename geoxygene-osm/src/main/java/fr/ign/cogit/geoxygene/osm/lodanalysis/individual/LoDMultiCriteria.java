package fr.ign.cogit.geoxygene.osm.lodanalysis.individual;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.util.multicriteriadecision.Criterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.ConclusionIntervals;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.RobustELECTRETRIMethod;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.osm.lodanalysis.LoDCategory;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObj;

/**
 * This class provides static methods to build multiple criteria analysis of
 * features LoD, using the ELECTRE TRI implementation of CartAGen.
 * @author GTouya
 * 
 */
public class LoDMultiCriteria {

  /**
   * 
   * @param criteria
   * @return
   */
  public static ConclusionIntervals initConclusion(Set<Criterion> criteria) {
    ConclusionIntervals conclusion = new ConclusionIntervals(criteria);
    Map<String, Double> borneSupTB = new Hashtable<String, Double>();
    Map<String, Double> borneInfTB = new Hashtable<String, Double>();
    Map<String, Double> borneInfB = new Hashtable<String, Double>();
    Map<String, Double> borneInfMy = new Hashtable<String, Double>();
    Map<String, Double> borneInfMv = new Hashtable<String, Double>();
    Map<String, Double> borneInfTMv = new Hashtable<String, Double>();

    Iterator<Criterion> itc = criteria.iterator();
    while (itc.hasNext()) {
      Criterion ct = itc.next();
      borneSupTB.put(ct.getName(), new Double(1));
      borneInfTB.put(ct.getName(), new Double(0.8));
      borneInfB.put(ct.getName(), new Double(0.6));
      borneInfMy.put(ct.getName(), new Double(0.4));
      borneInfMv.put(ct.getName(), new Double(0.2));
      borneInfTMv.put(ct.getName(), new Double(0));
    }
    conclusion.addInterval(borneInfTMv, borneInfMv, LoDCategory.STREET.name());
    conclusion.addInterval(borneInfMv, borneInfMy, LoDCategory.CITY.name());
    conclusion.addInterval(borneInfMy, borneInfB, LoDCategory.COUNTY.name());
    conclusion.addInterval(borneInfB, borneInfTB, LoDCategory.REGION.name());
    conclusion.addInterval(borneInfTB, borneSupTB, LoDCategory.COUNTRY.name());
    return conclusion;
  }

  /**
   * Initialise the parameters of the used criteria from a {@link OsmGeneObj}
   * feature that are the geometry for several criteria, the source and the
   * feature itself (when the feature type has to be analysed).
   * @param obj
   * @param crit
   * @return
   */
  public static Map<String, Object> initParameters(OsmGeneObj obj,
      Criterion crit) {
    Map<String, Object> param = new HashMap<String, Object>();
    if ((crit instanceof EdgeLengthMedianCriterion)
        || (crit instanceof GranularityCriterion)
        || (crit instanceof VertexDensityCriterion)
        || (crit instanceof SizeCriterion)
        || (crit instanceof CoalescenceCriterion))
      param.put("geometry", obj.getGeom());
    if (crit instanceof SourceCriterion)
      param.put("source", obj.getSource());
    if ((crit instanceof FeatureTypeCriterion)
        || (crit instanceof CoalescenceCriterion))
      param.put("feature", obj);
    if (crit instanceof VertexDensityCriterion)
      param.put("power", 8.0);
    if (crit instanceof TagNumberCriterion)
      param.put("number", obj.getTags().size());
    if (crit instanceof VersionNumberCriterion)
      param.put("version", obj.getVersion());
    return param;
  }

  /**
   * Compute the global density of a population. If it is a point population,
   * the number of features is used, if it is linear, the total length is used
   * and if it is polygonal, the total area is used.
   * @param popName
   * @return
   */
  public static double computeGlobalDensity(String popName) {
    IPopulation<IGeneObj> pop = CartAGenDocOld.getInstance()
        .getCurrentDataset().getCartagenPop(popName);
    IEnvelope env = pop.getEnvelope();
    double numerator = pop.size();
    IGeometry geom = pop.get(0).getGeom();
    if (geom instanceof ILineString) {
      numerator = 0.0;
      for (IGeneObj obj : pop)
        numerator += obj.getGeom().length();
    } else if (geom instanceof IPolygon) {
      numerator = 0.0;
      for (IGeneObj obj : pop)
        numerator += obj.getGeom().area();
    }
    return numerator / (env.height() * env.width());
  }

  public static RobustELECTRETRIMethod buildELECTRETRIMethod() {
    RobustELECTRETRIMethod electre = new RobustELECTRETRIMethod();
    Set<Criterion> criteria = new HashSet<Criterion>();
    criteria.add(new CoalescenceCriterion("Coalescence"));
    criteria.add(new EdgeLengthMedianCriterion("EdgeLengthMedian"));
    criteria.add(new FeatureTypeCriterion("FeatureType"));
    criteria.add(new GranularityCriterion("Granularity"));
    criteria.add(new SizeCriterion("Size"));
    criteria.add(new SourceCriterion("Source"));
    criteria.add(new VertexDensityCriterion("VertexDensity"));
    electre.setCriteriaParamsFromCriteria(criteria);

    return electre;
  }

  public static RobustELECTRETRIMethod buildELECTRETRIMethodForPts() {
    RobustELECTRETRIMethod electre = new RobustELECTRETRIMethod();
    Set<Criterion> criteria = new HashSet<Criterion>();
    criteria.add(new FeatureTypeCriterion("FeatureType"));
    criteria.add(new SourceCriterion("Source"));
    criteria.add(new TagNumberCriterion("TagNumber"));
    criteria.add(new VersionNumberCriterion("VersionNumber"));
    electre.setCriteriaParamsFromCriteria(criteria);

    return electre;
  }

}
