package fr.ign.cogit.geoxygene.osm.quality.spatialrelations;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.osm.util.OSMVersionComparator;

/**
 * A class to compute the quality assessment of OSM station point features,
 * based on spatial relations. In this case, the spatial relation is the
 * intersection with at least one subway line.
 * @author GTouya
 *
 */
public class StationQualityAssessment {

  private IFeatureCollection<IFeature> stations, subwayLines;
  private Map<IFeature, Integer> nbLines;
  private Map<String, Map<Long, Integer>> nbLinesHist;

  public StationQualityAssessment(IFeatureCollection<IFeature> junctions,
      IFeatureCollection<IFeature> roads) {
    super();
    this.subwayLines = roads;
    this.stations = junctions;
    this.nbLines = new HashMap<>();
    this.nbLinesHist = new HashMap<>();
  }

  public IFeatureCollection<IFeature> getStations() {
    return stations;
  }

  public void setStations(IFeatureCollection<IFeature> junctions) {
    this.stations = junctions;
  }

  public IFeatureCollection<IFeature> getSubwayLines() {
    return subwayLines;
  }

  public void setSubwayLines(IFeatureCollection<IFeature> subwayLines) {
    this.subwayLines = subwayLines;
  }

  public Map<IFeature, Integer> getNbLines() {
    return nbLines;
  }

  public void setNbLines(Map<IFeature, Integer> nbLines) {
    this.nbLines = nbLines;
  }

  public Map<String, Map<Long, Integer>> getNbLinesHist() {
    return nbLinesHist;
  }

  public void setNbLinesHist(Map<String, Map<Long, Integer>> nbLinesHist) {
    this.nbLinesHist = nbLinesHist;
  }

  /**
   * Computes the nb of intersecting roads and the number of intersecting
   * motorways among intersecting roads.
   */
  public void assessIndividualPositions() {
    // loop on all the ATM features
    for (IFeature feat : this.getStations()) {
      Collection<IFeature> intersecting = this.getSubwayLines()
          .select(feat.getGeom());
      this.getNbLines().put(feat, intersecting.size());

    }
  }

  /**
   * Computes the nb of intersecting roads and the number of intersecting
   * motorways among intersecting roads.
   */
  public void assessIndividualPositionsWithHistory(
      Map<String, List<IFeature>> stations) {
    for (String id : stations.keySet()) {
      List<IFeature> junctionsList = stations.get(id);
      Collections.sort(junctionsList, new OSMVersionComparator());
      Map<Long, Integer> mapSub = new HashMap<>();
      for (IFeature feat : junctionsList) {
        Collection<IFeature> intersecting = this.getSubwayLines()
            .select(feat.getGeom());
        mapSub.put((Long) feat.getAttribute("version"), intersecting.size());
      }
      this.getNbLinesHist().put(id, mapSub);
    }
  }
}
