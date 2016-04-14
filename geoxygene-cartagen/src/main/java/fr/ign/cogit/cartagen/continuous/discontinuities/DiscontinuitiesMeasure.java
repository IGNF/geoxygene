/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.continuous.discontinuities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * For a given list of continuous representations of a feature between scales,
 * triggers the legibility functions for each representation and the distance
 * function for each transition.
 * @author GTouya
 * 
 */
public class DiscontinuitiesMeasure {

  private static final Logger logger = Logger
      .getLogger(DiscontinuitiesMeasure.class);
  private SortedMap<Double, IGeometry> continuousGeoms;
  private Set<LegibilityFunction> legibilityFunctions = new HashSet<>();
  private Set<DistanceFunction> distanceFunctions = new HashSet<>();
  private Map<Double, Map<String, Double>> legibilityResults = new HashMap<>();
  private Map<ContinuousTransition, Map<String, Double>> distanceResults = new HashMap<>();

  public DiscontinuitiesMeasure(SortedMap<Double, IGeometry> continuousGeoms) {
    super();
    this.continuousGeoms = continuousGeoms;
  }

  public void addLegibilityFunction(LegibilityFunction function) {
    this.legibilityFunctions.add(function);
  }

  public void removeLegibilityFunction(LegibilityFunction function) {
    this.legibilityFunctions.remove(function);
  }

  public void addDistanceFunction(DistanceFunction function) {
    this.distanceFunctions.add(function);
  }

  public void removeDistanceFunction(DistanceFunction function) {
    this.distanceFunctions.remove(function);
  }

  public SortedMap<Double, IGeometry> getContinuousGeoms() {
    return continuousGeoms;
  }

  public void setContinuousGeoms(SortedMap<Double, IGeometry> continuousGeoms) {
    this.continuousGeoms = continuousGeoms;
  }

  public Map<ContinuousTransition, Map<String, Double>> getDistanceResults() {
    return distanceResults;
  }

  public void setDistanceResults(
      Map<ContinuousTransition, Map<String, Double>> distanceResults) {
    this.distanceResults = distanceResults;
  }

  public Map<Double, Map<String, Double>> getLegibilityResults() {
    return legibilityResults;
  }

  public void setLegibilityResults(
      Map<Double, Map<String, Double>> legibilityResults) {
    this.legibilityResults = legibilityResults;
  }

  /**
   * Compute the discontinuity measures with the added functions on the given
   * continuous geometries.
   */
  public void computeDiscontinuities() {
    IGeometry previous = null;
    Double previousScale = null;
    for (Double scale : continuousGeoms.keySet()) {
      IGeometry current = continuousGeoms.get(scale);
      // first, deal with legibility functions
      Map<String, Double> scaleLegMap = new HashMap<>();
      for (LegibilityFunction lFunc : this.legibilityFunctions) {
        System.out.println(lFunc.getName());
        Double value = lFunc.getValue(current, scale);
        scaleLegMap.put(lFunc.getName(), value);
      }
      this.legibilityResults.put(scale, scaleLegMap);

      // now, deal with the distance functions
      if (previous != null) {
        ContinuousTransition transition = new ContinuousTransition(
            previousScale, scale, previous, current);
        Map<String, Double> scaleDistMap = new HashMap<>();
        for (DistanceFunction dFunc : this.distanceFunctions) {
          System.out.println(dFunc.getName());
          Double value = dFunc.getDistance(previous, current);
          scaleDistMap.put(dFunc.getName(), value);
        }
        this.distanceResults.put(transition, scaleDistMap);
      }

      // update the previous geometry/scale
      previous = current;
      previousScale = scale;
      if (logger.isTraceEnabled())
        logger.trace("scale " + scale + " measured");
    }
  }

  public void writeToCsv(File file) throws IOException {
    // Create a CSV writer
    CSVWriter writer = new CSVWriter(new FileWriter(file), ';');

    // write header
    int i = 1;
    String[] line = new String[continuousGeoms.size() + 1];
    line[0] = "Measures";
    for (Double scale : continuousGeoms.keySet()) {
      line[i] = scale.toString();
      i++;
    }
    writer.writeNext(line);
    for (LegibilityFunction lFunc : this.legibilityFunctions) {
      line = new String[continuousGeoms.size() + 1];
      i = 1;
      line[0] = lFunc.getName();
      for (Double scale : continuousGeoms.keySet()) {
        line[i] = this.legibilityResults.get(scale).get(lFunc.getName())
            .toString();
        i++;
      }
      writer.writeNext(line);
    }

    for (DistanceFunction dFunc : this.distanceFunctions) {
      line = new String[continuousGeoms.size() + 1];
      Double previousScale = null;
      IGeometry previous = null;
      line[0] = dFunc.getName();
      i = 1;
      for (Double scale : continuousGeoms.keySet()) {
        IGeometry current = continuousGeoms.get(scale);
        ContinuousTransition transition = null;
        if (previousScale != null)
          transition = new ContinuousTransition(previousScale, scale, previous,
              current);
        if (i == 1)
          line[i] = "";
        else
          line[i] = this.distanceResults.get(transition).get(dFunc.getName())
              .toString();
        i++;
        previousScale = scale;
        previous = current;
      }
      writer.writeNext(line);
    }

    writer.close();
  }
}
