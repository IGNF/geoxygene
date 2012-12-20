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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.measure.proximity.GeometryProximity;

/**
 * 
 * @author gtouya
 * 
 *         The class contains static methods carrying out spatial queries. There
 *         are either standard queries encapsulated or more complex spatial
 *         queries like select nearest feature.
 * 
 */
public class SpatialQuery {

  /**
   * Select the gene objects inheriting from classObj that lie in the given
   * radius.
   * @param center
   * @param radius
   * @param classObj
   * @return
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws IllegalArgumentException
   */
  public static Collection<IGeneObj> selectInRadius(IDirectPosition center,
      double radius, Class<?> classObj) throws IllegalArgumentException,
      SecurityException, IllegalAccessException, NoSuchFieldException {
    String popName = (String) classObj.getDeclaredField("FEAT_TYPE_NAME").get(
        null);
    IPopulation<IGeneObj> pop = CartAGenDoc.getInstance().getCurrentDataset()
        .getCartagenPop(popName);
    return pop.select(center, radius);
  }

  /**
   * Select the gene objects inheriting from classObj that cross the given line.
   * @param center
   * @param radius
   * @param classObj
   * @return
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws IllegalArgumentException
   */
  public static Collection<IGeneObj> selectCrossing(ILineString line,
      Class<?> classObj) throws IllegalArgumentException, SecurityException,
      IllegalAccessException, NoSuchFieldException {
    String popName = (String) classObj.getDeclaredField("FEAT_TYPE_NAME").get(
        null);
    IPopulation<IGeneObj> pop = CartAGenDoc.getInstance().getCurrentDataset()
        .getCartagenPop(popName);
    return pop.select(line);
  }

  /**
   * Select the gene objects instancing one of the classes of classObj, that
   * cross the given line.
   * @param center
   * @param radius
   * @param classObj
   * @return
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws IllegalArgumentException
   */
  public static Collection<IGeneObj> selectCrossing(ILineString line,
      Set<Class<?>> classObjs) throws IllegalArgumentException,
      SecurityException, IllegalAccessException, NoSuchFieldException {
    IPopulation<IGeneObj> pop = new Population<IGeneObj>();
    for (Class<?> classObj : classObjs) {
      String popName = (String) classObj.getDeclaredField("FEAT_TYPE_NAME")
          .get(null);
      pop.addAll(CartAGenDoc.getInstance().getCurrentDataset().getCartagenPop(
          popName));
    }
    return pop.select(line);
  }

  /**
   * Select the gene objects instancing one of the classes of classObj, that
   * cross the given line.
   * @param center
   * @param radius
   * @param classObj
   * @return
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws IllegalArgumentException
   * @throws ClassNotFoundException
   */
  public static Collection<IGeneObj> selectCrossingNames(ILineString line,
      Set<String> classNames) throws IllegalArgumentException,
      SecurityException, IllegalAccessException, NoSuchFieldException,
      ClassNotFoundException {
    IPopulation<IGeneObj> pop = new Population<IGeneObj>();
    for (String className : classNames) {
      Class<?> classObj = Class.forName(className);
      String popName = (String) classObj.getDeclaredField("FEAT_TYPE_NAME")
          .get(null);
      pop.addAll(CartAGenDoc.getInstance().getCurrentDataset().getCartagenPop(
          popName));
    }
    return pop.select(line);
  }

  /**
   * Select the gene objects instancing classObj of the current dataset, that
   * cross the given line at a given point.
   * @param line
   * @param point
   * @param classObj
   * @return
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws NoSuchFieldException
   */
  public static Collection<IGeneObj> selectCrossLineAtPoint(ILineString line,
      IDirectPosition point, Class<?> classObj)
      throws IllegalArgumentException, SecurityException,
      IllegalAccessException, NoSuchFieldException {
    Collection<IGeneObj> crossLine = selectCrossing(line, classObj);

    Set<IGeneObj> set = new HashSet<IGeneObj>();
    for (IGeneObj inter : crossLine) {
      if (inter.getGeom().contains(point.toGM_Point()))
        set.add(inter);
    }

    return set;
  }

  /**
   * Select the gene objects instancing one of the classObjs classes, of the
   * current dataset, that cross the given line at a given point.
   * @param line
   * @param point
   * @param classObj
   * @return
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws NoSuchFieldException
   */
  public static Collection<IGeneObj> selectCrossLineAtPoint(ILineString line,
      IDirectPosition point, Set<Class<?>> classObjs)
      throws IllegalArgumentException, SecurityException,
      IllegalAccessException, NoSuchFieldException {
    Collection<IGeneObj> crossLine = selectCrossing(line, classObjs);

    Set<IGeneObj> set = new HashSet<IGeneObj>();
    for (IGeneObj inter : crossLine) {
      if (inter.getGeom().contains(point.toGM_Point()))
        set.add(inter);
    }

    return set;
  }

  /**
   * Select the gene objects instancing one of the classObjs classes, of the
   * current dataset, that cross the given line at a given point.
   * @param line
   * @param point
   * @param classObj
   * @return
   * @throws IllegalArgumentException
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws NoSuchFieldException
   * @throws ClassNotFoundException
   */
  public static Collection<IGeneObj> selectCrossLineAtPointNames(
      ILineString line, IDirectPosition point, Set<String> classNames)
      throws IllegalArgumentException, SecurityException,
      IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
    Collection<IGeneObj> crossLine = selectCrossingNames(line, classNames);

    Set<IGeneObj> set = new HashSet<IGeneObj>();
    for (IGeneObj inter : crossLine) {
      if (inter.getGeom().contains(point.toGM_Point()))
        set.add(inter);
    }

    return set;
  }

  /**
   * Select the gene objects instancing one of the classes of classObj, that lie
   * inside the given area.
   * @param polygon
   * @param classNames
   * @return
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws IllegalArgumentException
   * @throws ClassNotFoundException
   */
  public static Collection<IGeneObj> selectInAreaNames(IPolygon polygon,
      Set<String> classNames) throws IllegalArgumentException,
      SecurityException, IllegalAccessException, NoSuchFieldException,
      ClassNotFoundException {
    IPopulation<IGeneObj> pop = new Population<IGeneObj>();
    for (String className : classNames) {
      Class<?> classObj = Class.forName(className);
      String popName = (String) classObj.getDeclaredField("FEAT_TYPE_NAME")
          .get(null);
      pop.addAll(CartAGenDoc.getInstance().getCurrentDataset().getCartagenPop(
          popName));
    }
    return pop.select(polygon);
  }

  /**
   * Select the gene objects from all population of the current dataset, that
   * lie inside the given area.
   * @param polygon
   * @return
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws IllegalArgumentException
   * @throws ClassNotFoundException
   */
  @SuppressWarnings("unchecked")
  public static Collection<IGeneObj> selectInAreaAll(IPolygon polygon)
      throws IllegalArgumentException, SecurityException,
      IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
    IPopulation<IGeneObj> globalPop = new Population<IGeneObj>();
    for (IPopulation<? extends IFeature> pop : CartAGenDoc.getInstance()
        .getCurrentDataset().getPopulations()) {
      globalPop.addAll((Collection<? extends IGeneObj>) pop.select(polygon));
    }
    return globalPop;
  }

  /**
   * Get the nearest features of the geometry from the parameter collection. If
   * no feature is close enough (distance under distanceMax), null is returned.
   * @param geom
   * @param features
   * @param distanceMax
   * @return
   */
  public static IGeneObj selectNearest(IGeometry geom,
      IFeatureCollection<IGeneObj> features, double distanceMax) {
    Collection<IGeneObj> closeObjs = features.select(geom, distanceMax);
    if (closeObjs.size() == 0)
      return null;
    IGeneObj nearest = null;
    double minDist = distanceMax;
    for (IGeneObj obj : closeObjs) {
      GeometryProximity proxi = new GeometryProximity(geom, obj.getGeom());
      if (proxi.getDistance() <= minDist) {
        nearest = obj;
        minDist = proxi.getDistance();
      }
    }

    return nearest;
  }

  /**
   * Get the nearest features of the geometry from the parameter collection. If
   * no feature is close enough (distance under distanceMax), null is returned.
   * @param geom
   * @param features
   * @param distanceMax
   * @return a 2-sized vector containing the nearest road and the distance.
   */
  public static Vector<Object> selectNearestWithDistance(IGeometry geom,
      IFeatureCollection<IGeneObj> features, double distanceMax) {
    Collection<IGeneObj> closeObjs = features.select(geom, distanceMax);
    if (closeObjs.size() == 0) {
      Vector<Object> vect = new Vector<Object>(2);
      vect.add(null);
      return vect;
    }
    IGeneObj nearest = null;
    double minDist = distanceMax;
    for (IGeneObj obj : closeObjs) {
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
}
