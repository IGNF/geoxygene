/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util.comparators;

import java.util.Comparator;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
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
  private DistanceType type;

  public enum DistanceType {
    MIN_DIST, HAUSDORFF, MEDIAN, MEDIAN2
  }

  public DistanceFeatureComparator(IGeometry geomToCompare, DistanceType type) {
    super();
    this.geomToCompare = geomToCompare;
    this.type = type;
  }

  @Override
  public int compare(T o1, T o2) {
    // get the geometries
    IGeometry geom1 = o1.getGeom();
    IGeometry geom2 = o2.getGeom();
    // compute the distances to geomToCompare
    double dist1 = 0, dist2 = 0;
    if (type.equals(DistanceType.MIN_DIST)) {
      dist1 = new GeometryProximity(geomToCompare, geom1).getDistance();
      dist2 = new GeometryProximity(geomToCompare, geom2).getDistance();
    } else if (type.equals(DistanceType.HAUSDORFF)) {
      if (geom1 instanceof ILineString && geomToCompare instanceof ILineString)
        dist1 = Distances.hausdorff((ILineString) geomToCompare,
            (ILineString) geom1);
      else
        dist1 = 0;
      if (geom2 instanceof ILineString && geomToCompare instanceof ILineString)
        dist2 = Distances.hausdorff((ILineString) geomToCompare,
            (ILineString) geom2);
      else
        dist2 = 0;
    } else if (type.equals(DistanceType.MEDIAN)) {
      if (geom1 instanceof ILineString && geomToCompare instanceof ILineString)
        dist1 = Distances.lineMedianDistance((ILineString) geomToCompare,
            (ILineString) geom1);
      else
        dist1 = 0;
      if (geom2 instanceof ILineString && geomToCompare instanceof ILineString)
        dist2 = Distances.lineMedianDistance((ILineString) geomToCompare,
            (ILineString) geom2);
      else
        dist2 = 0;
    } else if (type.equals(DistanceType.MEDIAN2)) {
      if (geom1 instanceof ILineString && geomToCompare instanceof ILineString)
        dist1 = Distances.lineMedianDistance2((ILineString) geomToCompare,
            (ILineString) geom1);
      else
        dist1 = 0;
      if (geom2 instanceof ILineString && geomToCompare instanceof ILineString)
        dist2 = Distances.lineMedianDistance2((ILineString) geomToCompare,
            (ILineString) geom2);
      else
        dist2 = 0;
    }
    return new Long(Math.round(dist1 - dist2)).intValue();
  }

  public void setGeomToCompare(IGeometry geomToCompare) {
    this.geomToCompare = geomToCompare;
  }

  public IGeometry getGeomToCompare() {
    return geomToCompare;
  }

}
