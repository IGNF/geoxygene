package fr.ign.cogit.cartagen.genealgorithms.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.cartagen.spatialanalysis.urban.CornerBuildings;
import fr.ign.cogit.cartagen.util.multicriteriadecision.ranking.electre3.ELECTREIIIAction;
import fr.ign.cogit.cartagen.util.multicriteriadecision.ranking.electre3.ELECTREIIICriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.ranking.electre3.ELECTREIIIMethod;
import fr.ign.cogit.cartagen.util.multicriteriadecision.ranking.electre3.buildingelimination.BuildElimCongDirCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.ranking.electre3.buildingelimination.BuildElimCongestionCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.ranking.electre3.buildingelimination.BuildElimCornerCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.ranking.electre3.buildingelimination.BuildElimNeighTypesCriterion;
import fr.ign.cogit.cartagen.util.multicriteriadecision.ranking.electre3.buildingelimination.BuildElimSizeCriterion;
import fr.ign.cogit.geoxygene.schemageo.api.bati.Ilot;

/**
 * Algorithm to sort the buildings of a block with the first one being the first
 * to be deleted, and the last one should be the last to be deleted. Uses the
 * ELECTRE III multiple criteria decision technique, and the following criteria:
 * congestion, congestion direction, corner buildings, size and the types of the
 * neighbours.
 * @author GTouya
 *
 */
public class BuildingsDeletionProximityMultiCriterion {

  private static Collection<ELECTREIIICriterion> criteria;

  private static Collection<ELECTREIIICriterion> getCriteria() {
    if (criteria != null)
      return criteria;
    Collection<ELECTREIIICriterion> criteria = new HashSet<ELECTREIIICriterion>();
    criteria.add(new BuildElimCongDirCriterion("CongDir"));
    criteria.add(new BuildElimCongestionCriterion("Congestion"));
    criteria.add(new BuildElimCornerCriterion("Corner"));
    criteria.add(new BuildElimNeighTypesCriterion("NeighTypes"));
    criteria.add(new BuildElimSizeCriterion("Size"));
    return criteria;
  }

  public static List<IUrbanElement> compute(IUrbanBlock ai) {

    List<IUrbanElement> removedBuildings = new ArrayList<IUrbanElement>();

    // le nombre de batiments non supprimes

    List<ELECTREIIIAction> actions = new ArrayList<ELECTREIIIAction>();

    for (IUrbanElement urbanElement : ai.getUrbanElements()) {
      if (urbanElement.isDeleted()) {
        continue;
      }
      HashMap<String, Object> parameters = new HashMap<String, Object>();
      // Geometry
      parameters.put(BuildElimSizeCriterion.PARAM_GEOM, urbanElement.getGeom());
      // corner
      CornerBuildings cornerBuilding = new CornerBuildings(
          (Ilot) ai.getGeoxObj());
      cornerBuilding.compute();
      parameters.put(BuildElimCornerCriterion.PARAM_CORNER_BUILDINGS,
          cornerBuilding.getCornerBuildings());
      // simulated area
      double minArea = GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT
          * Legend.getSYMBOLISATI0N_SCALE() * Legend.getSYMBOLISATI0N_SCALE()
          / 1000000.0;
      double area = urbanElement.getGeom().area();
      parameters.put(BuildElimSizeCriterion.PARAM_SIM_AREA,
          Math.max(area, minArea));
      // distanceMax
      parameters.put(BuildElimCongestionCriterion.PARAM_DIST_MAX,
          GeneralisationSpecifications.DISTANCE_MAX_PROXIMITE);
      // building
      parameters.put(BuildElimCornerCriterion.PARAM_BUILDING, urbanElement);
      // sizeThreshold
      parameters.put(BuildElimSizeCriterion.PARAM_AREA_THRESH,
          GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT);
      // create the action object
      ELECTREIIIAction action = new ELECTREIIIAction(urbanElement, parameters);
      actions.add(action);
    }

    ELECTREIIIMethod method = new ELECTREIIIMethod(getCriteria(), actions, 0.5);

    List<ELECTREIIIAction> decision = method.decision();
    for (int i = 0; i < decision.size(); i++) {
      removedBuildings.add((IBuilding) decision.get(i).getObj());
    }
    return removedBuildings;
  }

}
