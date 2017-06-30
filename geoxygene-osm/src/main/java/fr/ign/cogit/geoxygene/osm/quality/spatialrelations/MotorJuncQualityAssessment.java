package fr.ign.cogit.geoxygene.osm.quality.spatialrelations;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

/**
 * A class to compute the quality assessment of OSM motorway junction point
 * features, based on spatial relations. In this case, the spatial relation is
 * the intersection with at least two roads, one being a motorway.
 * @author GTouya
 *
 */
public class MotorJuncQualityAssessment {

  private IFeatureCollection<IFeature> junctions, roads;
  private Map<IFeature, Integer> nbRoads, nbMotorways;
  private Map<String, Map<Long, Integer>> nbRoadsHist, nbMotorwaysHist;

  public MotorJuncQualityAssessment(IFeatureCollection<IFeature> junctions,
      IFeatureCollection<IFeature> roads) {
    super();
    this.roads = roads;
    this.junctions = junctions;
    this.nbRoads = new HashMap<>();
    this.nbMotorways = new HashMap<>();
    this.nbRoadsHist = new HashMap<>();
    this.nbMotorwaysHist = new HashMap<>();
  }

  public IFeatureCollection<IFeature> getJunctions() {
    return junctions;
  }

  public void setJunctions(IFeatureCollection<IFeature> junctions) {
    this.junctions = junctions;
  }

  public IFeatureCollection<IFeature> getRoads() {
    return roads;
  }

  public void setRoads(IFeatureCollection<IFeature> roads) {
    this.roads = roads;
  }

  public Map<IFeature, Integer> getNbRoads() {
    return nbRoads;
  }

  public void setNbRoads(Map<IFeature, Integer> nbRoads) {
    this.nbRoads = nbRoads;
  }

  public Map<IFeature, Integer> getNbMotorways() {
    return nbMotorways;
  }

  public void setNbMotorways(Map<IFeature, Integer> nbMotorways) {
    this.nbMotorways = nbMotorways;
  }

  public Map<String, Map<Long, Integer>> getNbRoadsHist() {
    return nbRoadsHist;
  }

  public void setNbRoadsHist(Map<String, Map<Long, Integer>> nbRoadsHist) {
    this.nbRoadsHist = nbRoadsHist;
  }

  public Map<String, Map<Long, Integer>> getNbMotorwaysHist() {
    return nbMotorwaysHist;
  }

  public void setNbMotorwaysHist(
      Map<String, Map<Long, Integer>> nbMotorwaysHist) {
    this.nbMotorwaysHist = nbMotorwaysHist;
  }

  /**
   * Computes the nb of intersecting roads and the number of intersecting
   * motorways among intersecting roads.
   */
  public void assessIndividualPositions() {
    // loop on all the ATM features
    for (IFeature feat : this.getJunctions()) {
      Collection<IFeature> intersecting = this.getRoads()
          .select(feat.getGeom());
      this.getNbRoads().put(feat, intersecting.size());
      int nbMotorway = 0;
      for (IFeature road : intersecting) {
        String roadType = (String) road.getAttribute("type");
        if (roadType.equals("trunk"))
          nbMotorway++;
        else if (roadType.equals("motorway"))
          nbMotorway++;
      }
      this.getNbMotorways().put(feat, nbMotorway);
    }
  }

  /**
   * Computes the nb of intersecting roads and the number of intersecting
   * motorways among intersecting roads, for each version of the feature.
   * @param junctions
   */
  public void assessIndividualPositionsWithHistory(
      Map<String, List<IFeature>> junctions) {
    for (String id : junctions.keySet()) {
      List<IFeature> junctionsList = junctions.get(id);
      Collections.sort(junctionsList, new OSMVersionComparator());
      Map<Long, Integer> mapRoads = new HashMap<>();
      Map<Long, Integer> mapMotors = new HashMap<>();
      for (IFeature feat : junctionsList) {
        Collection<IFeature> intersecting = this.getRoads()
            .select(feat.getGeom());
        mapRoads.put((Long) feat.getAttribute("version"), intersecting.size());
        int nbMotorway = 0;
        for (IFeature road : intersecting) {
          String roadType = (String) road.getAttribute("type");
          if (roadType.equals("trunk"))
            nbMotorway++;
          else if (roadType.equals("motorway"))
            nbMotorway++;
        }
        mapMotors.put((Long) feat.getAttribute("version"), nbMotorway);
      }
      this.getNbRoadsHist().put(id, mapRoads);
      this.getNbMotorwaysHist().put(id, mapMotors);
    }
  }

}
