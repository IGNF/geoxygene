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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanBlock;
import fr.ign.cogit.cartagen.core.genericschema.urban.IUrbanElement;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * Algorithm to delete the buildings of a block using the overlapping rate of
 * the buildings: at each iteration, the building with the highest overlapping
 * rate is deleted if this rate is higher than a threshold. The overlapping rate
 * is the area of the building divided by the sum of the areas of intersections
 * with the overlapping buildings. If two buildings have the same overlapping
 * rate, the smallest one is ranked first.
 * @author GTouya
 *
 */
public class BuildingDeletionOverlap {

  private static Logger LOGGER = Logger
      .getLogger(BuildingDeletionOverlap.class);

  private double minimumRate = 0.4;

  public BuildingDeletionOverlap() {
    super();
  }

  public BuildingDeletionOverlap(double minimumRate) {
    super();
    this.setMinimumRate(minimumRate);
  }

  /**
   * For a given block, returns the urban element (i.e. buildings) to eliminate.
   * @param ai
   * @return
   */
  public List<IUrbanElement> compute(IUrbanBlock ai) {

    List<IUrbanElement> removedBuildings = new ArrayList<IUrbanElement>();
    boolean continueDeletion = true;
    Set<IFeature> remainingBuildings = new HashSet<>();
    remainingBuildings.addAll(ai.getUrbanElements());
    while (continueDeletion) {
      Map<IFeature, Double> overlapRatios = updateOverlapRatios(
          remainingBuildings);
      RatioComparator comparator = new RatioComparator(overlapRatios);
      List<IFeature> buildings = new ArrayList<>();
      buildings.addAll(remainingBuildings);
      Collections.sort(buildings, comparator);
      Collections.reverse(buildings);
      IFeature first = buildings.get(0);
      double ratio = overlapRatios.get(first);
      if (ratio > minimumRate) {
        removedBuildings.add((IUrbanElement) first);
        remainingBuildings.remove(first);
      } else
        continueDeletion = false;
    }

    return removedBuildings;
  }

  /**
   * For a given block, returns the urban element (i.e. buildings) to eliminate.
   * @param ai
   * @return
   */
  public List<IFeature> compute(Collection<IFeature> features) {

    List<IFeature> removedBuildings = new ArrayList<IFeature>();
    boolean continueDeletion = true;
    Set<IFeature> remainingBuildings = new HashSet<>();
    remainingBuildings.addAll(features);
    while (continueDeletion) {
      Map<IFeature, Double> overlapRatios = updateOverlapRatios(
          remainingBuildings);
      RatioComparator comparator = new RatioComparator(overlapRatios);
      List<IFeature> buildings = new ArrayList<>();
      buildings.addAll(remainingBuildings);
      Collections.sort(buildings, comparator);
      Collections.reverse(buildings);
      IFeature first = buildings.get(0);
      double ratio = overlapRatios.get(first);
      if (ratio > minimumRate) {
        removedBuildings.add(first);
        remainingBuildings.remove(first);
      } else
        continueDeletion = false;
    }

    return removedBuildings;
  }

  private Map<IFeature, Double> updateOverlapRatios(Set<IFeature> buildings) {
    Map<IFeature, Double> overlapRatios = new HashMap<>();
    for (IFeature building : buildings) {
      IFeatureCollection<IFeature> fc = new FT_FeatureCollection<>();
      fc.addAll(buildings);
      fc.remove(building);
      Collection<IFeature> overlapping = fc.select(building.getGeom());
      double overlappingTotal = 0.0;
      for (IFeature other : overlapping) {
        IGeometry intersection = other.getGeom()
            .intersection(building.getGeom());
        if (intersection != null)
          overlappingTotal += intersection.area();
      }
      double ratio = overlappingTotal / building.getGeom().area();
      overlapRatios.put(building, ratio);
    }
    return overlapRatios;
  }

  public double getMinimumRate() {
    return minimumRate;
  }

  public void setMinimumRate(double minimumRate) {
    this.minimumRate = minimumRate;
  }

  private class RatioComparator implements Comparator<IFeature> {

    private Map<IFeature, Double> overlapRatios;

    public RatioComparator(Map<IFeature, Double> overlapRatios) {
      super();
      this.overlapRatios = overlapRatios;
    }

    @Override
    public int compare(IFeature o1, IFeature o2) {
      int comp = overlapRatios.get(o1).compareTo(overlapRatios.get(o2));
      if (comp == 0)
        comp = -(Double.valueOf(o1.getGeom().area())
            .compareTo(Double.valueOf(o2.getGeom().area())));
      else
        comp = comp * 1000000;
      return comp;
    }

  }
}
