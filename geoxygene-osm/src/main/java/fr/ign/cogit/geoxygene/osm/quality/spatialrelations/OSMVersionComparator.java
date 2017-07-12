package fr.ign.cogit.geoxygene.osm.quality.spatialrelations;

import java.util.Comparator;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * Compares OpenStreetMap features according to their version (the feature need
 * to have a "version" attribute).
 * @author GTouya
 *
 */
public class OSMVersionComparator implements Comparator<IFeature> {

  @Override
  public int compare(IFeature o1, IFeature o2) {
    Object v1 = o1.getAttribute("version");
    Object v2 = o2.getAttribute("version");
    if (v1 == null)
      return 0;
    if (v2 == null)
      return 0;
    return ((Long) v1).compareTo((Long) v2);
  }

}
