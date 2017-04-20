/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.genealgorithms.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.spatialanalysis.measures.congestion.CongestionComputation;
import fr.ign.cogit.cartagen.spatialanalysis.urban.CornerBuildings;
import fr.ign.cogit.cartagen.util.multicriteriadecision.promethee.PreferenceFunction;
import fr.ign.cogit.cartagen.util.multicriteriadecision.promethee.PrometheeCandidate;
import fr.ign.cogit.cartagen.util.multicriteriadecision.promethee.PrometheeCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.promethee.PrometheeDecision;
import fr.ign.cogit.cartagen.util.multicriteriadecision.promethee.Type2PreferenceFunction;
import fr.ign.cogit.cartagen.util.multicriteriadecision.promethee.Type4PreferenceFunction;
import fr.ign.cogit.cartagen.util.multicriteriadecision.promethee.Type5PreferenceFunction;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Batiment;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;

/**
 * Algorithm to sort the buildings of a block with the first one being the first
 * to be deleted, and the last one should be the last to be deleted. Uses the
 * PROMETHEE multiple criteria decision technique, and the following criteria:
 * congestion, congestion direction, corner buildings, size and the types of the
 * neighbours.
 * @author GTouya
 *
 */
public class BuildingDeletionPromethee {

  private static Logger LOGGER = Logger
      .getLogger(BuildingDeletionPromethee.class);

  private Collection<PrometheeCriterion> criteria;
  private Map<PrometheeCriterion, Double> weights;
  private static String PARAM_SIM_AREA = "simulated area";
  private static String PARAM_GEOM = "geometry";
  private static String PARAM_CORNER_BUILDINGS = "corner buildings";
  private static String PARAM_DIST_MAX = "maximum distance";
  private static String PARAM_BUILDING = "building";
  private static String PARAM_AREA_THRESH = "size threshold";

  public BuildingDeletionPromethee() {
    getDefaultCriteria();
  }

  public Collection<PrometheeCriterion> getCriteria() {
    return criteria;
  }

  public void setCriteria(Collection<PrometheeCriterion> criteria) {
    this.criteria = criteria;
  }

  public List<IUrbanElement> compute(IUrbanBlock ai) {

    List<IUrbanElement> removedBuildings = new ArrayList<IUrbanElement>();

    CornerBuildings cornerBuilding = new CornerBuildings(
        (Ilot) ai.getGeoxObj());
    cornerBuilding.compute();
    double minArea = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
        * Legend.getSYMBOLISATI0N_SCALE() * Legend.getSYMBOLISATI0N_SCALE()
        / 1000000.0;

    Collection<PrometheeCandidate> candidates = new HashSet<PrometheeCandidate>();

    for (IUrbanElement urbanElement : ai.getUrbanElements()) {
      if (urbanElement.isDeleted()) {
        continue;
      }
      // compute the parameters
      Map<String, Object> parameters = new HashMap<>();
      // Geometry
      parameters.put(PARAM_GEOM, urbanElement.getGeom());
      // corner
      parameters.put(PARAM_CORNER_BUILDINGS,
          cornerBuilding.getCornerBuildings());
      if (LOGGER.isTraceEnabled())
        LOGGER.trace(cornerBuilding.getCornerBuildings().size()
            + " corner buildings in the block");
      // simulated area

      double area = urbanElement.getGeom().area();
      parameters.put(PARAM_SIM_AREA, Math.max(area, minArea));
      // distanceMax
      parameters.put(PARAM_DIST_MAX,
          GeneralisationSpecifications.DISTANCE_MAX_PROXIMITE);
      // building
      parameters.put(PARAM_BUILDING, urbanElement);
      // sizeThreshold
      parameters.put(PARAM_AREA_THRESH,
          GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT);

      Map<PrometheeCriterion, Double> criteriaValues = new HashMap<PrometheeCriterion, Double>();
      for (PrometheeCriterion crit : this.criteria) {
        criteriaValues.put(crit, crit.value(parameters));
      }

      // create the action object
      PrometheeCandidate candidate = new PrometheeCandidate(urbanElement,
          criteriaValues);
      candidates.add(candidate);
    }

    if (LOGGER.isTraceEnabled())
      LOGGER.trace(candidates.size() + " candidates for " + ai);
    PrometheeDecision method = new PrometheeDecision(weights);

    List<PrometheeCandidate> list = method.makeRankingDecision(candidates);
    if (LOGGER.isTraceEnabled()) {
      LOGGER.trace("list of ranked candidates:");
      for (PrometheeCandidate cand : list)
        LOGGER.trace(cand);
    }
    for (int i = 0; i < list.size(); i++) {
      removedBuildings.add((IUrbanElement) list.get(i).getCandidateObject());
    }
    return removedBuildings;
  }

  public Map<PrometheeCriterion, Double> getWeights() {
    return weights;
  }

  public void setWeights(Map<PrometheeCriterion, Double> weights) {
    this.weights = weights;
  }

  private void getDefaultCriteria() {
    this.criteria = new HashSet<>();
    this.weights = new HashMap<>();
    // size criterion
    PrometheeCriterion size = new BuildElimSizeCriterion("size",
        new Type5PreferenceFunction(0.1, 0.7));
    this.criteria.add(size);
    this.weights.put(size, 0.2);

    // congestion criterion
    PrometheeCriterion congestion = new BuildElimCongestionCriterion(
        "congestion", new Type4PreferenceFunction(0.2, 0.4));
    this.criteria.add(congestion);
    this.weights.put(congestion, 0.2);

    // congestion direction criterion
    PrometheeCriterion congDir = new BuildElimCongDirCriterion(
        "congestion direction", new Type4PreferenceFunction(0.2, 0.4));
    this.criteria.add(congDir);
    this.weights.put(congDir, 0.2);

    // corner buildings criterion
    PrometheeCriterion corner = new BuildElimCornerCriterion("corner buildings",
        new Type2PreferenceFunction(0.5));
    this.criteria.add(corner);
    this.weights.put(corner, 0.2);

    // types of neighbours criterion
    PrometheeCriterion neighbours = new BuildElimNeighTypesCriterion(
        "types of neighbours", new Type5PreferenceFunction(0.15, 0.6));
    this.criteria.add(neighbours);
    this.weights.put(neighbours, 0.2);
  }

  private class BuildElimSizeCriterion extends PrometheeCriterion {

    public BuildElimSizeCriterion(String name,
        PreferenceFunction preferenceFunction) {
      super(name, preferenceFunction);
    }

    @Override
    public double value(Map<String, Object> param) {
      IPolygon geom = (IPolygon) param.get(PARAM_GEOM);
      double simArea = (Double) param.get(PARAM_SIM_AREA);
      double sizeThreshold = (Double) param.get(PARAM_AREA_THRESH);
      double area = geom.area();
      if (Math.abs(simArea - area) > 15.0)
        area = simArea;
      if (area <= sizeThreshold)
        return 1.0;
      if (area <= 2 * sizeThreshold) {
        double a = -0.5 * 1.0 / sizeThreshold;
        double b = (2.0 * sizeThreshold - 1.0) / (2.0 * sizeThreshold);
        return a * area + b;
      }
      // a segment with a softer slope
      double a = -1.0 / (4.0 * sizeThreshold);
      double b = 1.0;
      return a * area + b;
    }

  }

  private class BuildElimNeighTypesCriterion extends PrometheeCriterion {

    // Public constructors //
    public BuildElimNeighTypesCriterion(String name,
        PreferenceFunction preferenceFunction) {
      super(name, preferenceFunction);
    }

    // Other public methods //
    @Override
    public double value(Map<String, Object> param) {
      IBuilding building = (IBuilding) param.get(PARAM_BUILDING);
      double distMax = (Double) param.get(PARAM_DIST_MAX);
      CongestionComputation congestion = new CongestionComputation();
      congestion.calculEncombrement(building, distMax);

      double neighTypes;
      if (building.getProximitySegments().size() == 0) {
        neighTypes = 0.0;
      } else {
        neighTypes = congestion.getNbSegmentsBatiBati()
            / building.getProximitySegments().size();
      }
      return neighTypes;
    }
  }

  class BuildElimCornerCriterion extends PrometheeCriterion {

    // Public constructors //
    public BuildElimCornerCriterion(String name,
        PreferenceFunction preferenceFunction) {
      super(name, preferenceFunction);
    }

    // Other public methods //
    @Override
    @SuppressWarnings("unchecked")
    public double value(Map<String, Object> param) {
      IBuilding building = (IBuilding) param.get(PARAM_BUILDING);
      HashSet<Batiment> cornerBuilds = (HashSet<Batiment>) param
          .get(PARAM_CORNER_BUILDINGS);
      if (cornerBuilds.contains(building.getGeoxObj()))
        return 1.0;
      return 0.0;
    }
  }

  public class BuildElimCongestionCriterion extends PrometheeCriterion {

    public BuildElimCongestionCriterion(String name,
        PreferenceFunction preferenceFunction) {
      super(name, preferenceFunction);
    }

    // Other public methods //
    @Override
    public double value(Map<String, Object> param) {
      IBuilding building = (IBuilding) param.get(PARAM_BUILDING);
      double distMax = (Double) param.get(PARAM_DIST_MAX);
      CongestionComputation congestion = new CongestionComputation();
      congestion.calculEncombrement(building, distMax);
      int nb = congestion.getEncombrements().length;
      double cong = 0.0;
      for (int i = 0; i < nb; i++) {
        cong += congestion.getEncombrements()[i];
      }
      cong /= nb;

      return cong;
    }
  }

  class BuildElimCongDirCriterion extends PrometheeCriterion {

    public BuildElimCongDirCriterion(String name,
        PreferenceFunction preferenceFunction) {
      super(name, preferenceFunction);
    }

    @Override
    public double value(Map<String, Object> param) {
      IBuilding building = (IBuilding) param.get(PARAM_BUILDING);
      double distMax = (Double) param.get(PARAM_DIST_MAX);
      CongestionComputation congestion = new CongestionComputation();
      congestion.calculEncombrement(building, distMax);
      int nb = congestion.getEncombrements().length;
      double tDirEnc = 0.0;
      for (int i = 0; i < nb; i++) {
        if (congestion.getEncombrements()[i] > 0.05) {
          tDirEnc++;
        }
      }
      tDirEnc /= nb;

      return tDirEnc;
    }
  }

}
