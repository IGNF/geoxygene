/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 */
package fr.ign.cogit.geoxygene.contrib.algorithms;

import java.util.Collection;
import java.util.Vector;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.GeometryProximity;

/**
 * Spatial queries that a more complex than the standard select methods.
 * @author GTouya
 *
 */
public class SpatialQuery {

  /**
   * Get the nearest features of the geometry from the parameter collection. If
   * no feature is close enough (distance under distanceMax), null is returned.
   * @param geom
   * @param features
   * @param distanceMax
   * @return a 2-sized vector containing the nearest road and the distance.
   */
  public static Vector<Object> selectNearestWithDistance(IGeometry geom,
      IFeatureCollection<IFeature> features, double distanceMax) {
    Collection<IFeature> closeObjs = features.select(geom, distanceMax);
    if (closeObjs.size() == 0) {
      Vector<Object> vect = new Vector<Object>(2);
      vect.add(null);
      return vect;
    }
    IFeature nearest = null;
    double minDist = distanceMax;
    for (IFeature obj : closeObjs) {
      GeometryProximity proxi = new GeometryProximity(geom, obj.getGeom());
      if (proxi.getDistance() <= minDist) {
        nearest = obj;
        minDist = proxi.getDistance();
      }
    }

    Vector<Object> vect = new Vector<Object>(2);
    vect.add(nearest);
    vect.add(minDist);
    return vect;
  }

  /**
   * Select the features in a FeatureCollection that contain a given geometry.
   * @param geom
   * @param features
   * @return
   */
  public static IFeature selectContains(IGeometry geom,
      IFeatureCollection<IFeature> features) {
    Collection<IFeature> closeObjs = features.select(geom);
    if (closeObjs.size() == 0) {
      return null;
    }

    for (IFeature obj : closeObjs) {
      if (obj.getGeom().contains(geom))
        return obj;
    }

    return null;
  }

  /**
   * Get the nearest features of the geometry from the parameter collection. If
   * no feature is close enough (distance under distanceMax), null is returned.
   * @param geom
   * @param features
   * @param distanceMax
   * @return
   */
  public static IFeature selectNearestFeature(IGeometry geom,
      IFeatureCollection<IFeature> features, double distanceMax) {
    Collection<IFeature> closeObjs = features.select(geom, distanceMax);
    if (closeObjs.size() == 0) {
      return null;
    }
    IFeature nearest = null;
    double minDist = distanceMax;
    for (IFeature obj : closeObjs) {
      GeometryProximity proxi = new GeometryProximity(geom, obj.getGeom());
      if (proxi.getDistance() <= minDist) {
        nearest = obj;
        minDist = proxi.getDistance();
      }
    }

    return nearest;
  }

}
