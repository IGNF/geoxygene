/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util;

import java.util.Comparator;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.GeometryProximity;

/**
 * A comparator to sort features lists using the distance to a given geometry:
 * the nearest to the geometry is the smallest for the comparator.
 * @author GTouya
 * 
 * @param <T>
 */
public class DistanceFeatureComparator<T extends IFeature> implements
    Comparator<T> {

  private IGeometry geomToCompare;

  public DistanceFeatureComparator(IGeometry geomToCompare) {
    super();
    this.geomToCompare = geomToCompare;
  }

  @Override
  public int compare(T o1, T o2) {
    // get the geometries
    IGeometry geom1 = o1.getGeom();
    IGeometry geom2 = o2.getGeom();
    // compute the distances to geomToCompare
    double dist1 = new GeometryProximity(geomToCompare, geom1).getDistance();
    double dist2 = new GeometryProximity(geomToCompare, geom2).getDistance();
    return new Long(Math.round(dist1 - dist2)).intValue();
  }

  public void setGeomToCompare(IGeometry geomToCompare) {
    this.geomToCompare = geomToCompare;
  }

  public IGeometry getGeomToCompare() {
    return geomToCompare;
  }

}
