/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.spatialanalysis.urban;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

import fr.ign.cogit.cartagen.util.SpatialQuery;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * @author jGaffuri
 * 
 */
public class UrbanAreaComputationJTS {
  private static Logger logger = Logger.getLogger(UrbanAreaComputationJTS.class
      .getName());

  public static IGeometry calculTacheUrbaine(ArrayList<IGeometry> geoms,
      double distanceBuffer, double distanceErosion, int quadrantSegments,
      double seuilDP) {

    // cree la collection des batiments bufferises
    Geometry[] bufferGeoms = new Geometry[geoms.size()];
    GeometryFactory gf = new GeometryFactory();
    int i = 0;
    if (UrbanAreaComputationJTS.logger.isDebugEnabled()) {
      UrbanAreaComputationJTS.logger.debug("construction des " + geoms.size()
          + " buffers");
    }
    for (IGeometry geom : geoms) {
      if (UrbanAreaComputationJTS.logger.isInfoEnabled()) {
        UrbanAreaComputationJTS.logger.info("   buffers des objets: " + i + "/"
            + geoms.size());
      }
      try {
        bufferGeoms[i++] = AdapterFactory.toGeometry(gf, geom).buffer(
            distanceBuffer, quadrantSegments, BufferParameters.CAP_ROUND);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    System.gc();

    UrbanAreaComputationJTS.logger.debug("fusion des buffers");
    Geometry union = JtsAlgorithms.union(bufferGeoms);
    bufferGeoms = null;

    UrbanAreaComputationJTS.logger.debug("filtre dp");
    union = JtsAlgorithms.filtreDouglasPeucker(union, seuilDP);

    UrbanAreaComputationJTS.logger.debug("fermeture");
    union = JtsAlgorithms.fermeture(union, distanceErosion, quadrantSegments,
        BufferParameters.CAP_ROUND);

    UrbanAreaComputationJTS.logger.debug("filtre dp");
    union = JtsAlgorithms.filtreDouglasPeucker(union, seuilDP);

    UrbanAreaComputationJTS.logger.debug("fusion");
    union = union.buffer(0);

    UrbanAreaComputationJTS.logger.debug("suppression des trous");
    if (union instanceof Polygon) {
      union = JtsAlgorithms.supprimeTrous((Polygon) union);
    } else if (union instanceof MultiPolygon) {
      union = JtsAlgorithms.supprimeTrous((MultiPolygon) union);
    } else {
      UrbanAreaComputationJTS.logger
          .error("Impossible de creer tache urbaine. Type de geometrie non traite: "
              + union);
      return null;
    }

    // pour supprimer les polygones qui sont dans des trous d'autres polygones
    union = union.buffer(0);

    try {
      IGeometry union_ = AdapterFactory.toGM_Object(union);
      return union_;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public static IGeometry calculTacheUrbaine(ArrayList<IGeometry> geoms,
      double distanceBuffer, double distanceErosion, int quadrantSegments,
      double seuilDP, double holeMinArea) {

    // cree la collection des batiments bufferises
    Geometry[] bufferGeoms = new Geometry[geoms.size()];
    GeometryFactory gf = new GeometryFactory();
    int i = 0;
    if (UrbanAreaComputationJTS.logger.isDebugEnabled()) {
      UrbanAreaComputationJTS.logger.debug("construction des " + geoms.size()
          + " buffers");
    }
    for (IGeometry geom : geoms) {
      if (UrbanAreaComputationJTS.logger.isInfoEnabled()) {
        UrbanAreaComputationJTS.logger.info("   buffers des objets: " + i + "/"
            + geoms.size());
      }
      try {
        bufferGeoms[i++] = AdapterFactory.toGeometry(gf, geom).buffer(
            distanceBuffer, quadrantSegments, BufferParameters.CAP_ROUND);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    System.gc();

    UrbanAreaComputationJTS.logger.debug("fusion des buffers");
    Geometry union = JtsAlgorithms.union(bufferGeoms);
    bufferGeoms = null;

    UrbanAreaComputationJTS.logger.debug("filtre dp");
    union = JtsAlgorithms.filtreDouglasPeucker(union, seuilDP);

    UrbanAreaComputationJTS.logger.debug("fermeture");
    union = JtsAlgorithms.fermeture(union, distanceErosion, quadrantSegments,
        BufferParameters.CAP_ROUND);

    UrbanAreaComputationJTS.logger.debug("filtre dp");
    union = JtsAlgorithms.filtreDouglasPeucker(union, seuilDP);

    UrbanAreaComputationJTS.logger.debug("fusion");
    union = union.buffer(0);

    UrbanAreaComputationJTS.logger.debug("suppression des trous");
    if (union instanceof Polygon) {
      union = JtsAlgorithms.supprimeTrous((Polygon) union, holeMinArea);
    } else if (union instanceof MultiPolygon) {
      union = JtsAlgorithms.supprimeTrous((MultiPolygon) union, holeMinArea);
    } else {
      UrbanAreaComputationJTS.logger
          .error("Impossible de creer tache urbaine. Type de geometrie non traite: "
              + union);
      return null;
    }

    // pour supprimer les polygones qui sont dans des trous d'autres polygones
    union = union.buffer(0);

    try {
      IGeometry union_ = AdapterFactory.toGM_Object(union);
      return union_;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Compute a built-up area from a collection of building features, using the
   * method from Chaudhry and Mackaness 2008. The method is similar to Boffet
   * (2000) but the buffer size is different for each building, related to the
   * citiness index of the building.
   * @param features the feature collection of buildings
   * @param nbNeighbours the nb of neighbours to consider around buildings (30
   *          is a good default value)
   * @param k the constant used to compute buildings buffer from citiness (90.0
   *          is a good default value).
   * @param minArea the minimum area to be kept as a built-up area
   * @param douglasThreshold the Douglas & Peucker filter threshold applied to
   *          the built-up outlines
   * @param holeMinArea the minimum area for a hole to be kept
   * @return
   */
  @SuppressWarnings("unchecked")
  public static IGeometry computeCitinessBuiltUpArea(
      IFeatureCollection<? extends IFeature> features, int nbNeighbours,
      double k, double minArea, int quadrantSegments, double douglasThreshold,
      double holeMinArea) {

    Map<IFeature, Double> citinessIndices = new HashMap<IFeature, Double>();
    double maxCitiness = 0.0;
    // compute citiness indices for the given features
    for (IFeature feat : features) {
      IPolygon geom = null;
      if (feat.getGeom() instanceof IPolygon)
        geom = (IPolygon) feat.getGeom();
      else if (feat.getGeom() instanceof IMultiSurface<?>)
        geom = CommonAlgorithmsFromCartAGen
            .getBiggerFromAggregate((IAggregate<IGeometry>) feat.getGeom());
      double citiness = computeCitiness(geom, nbNeighbours, features);
      if (citiness > maxCitiness)
        maxCitiness = citiness;
      citinessIndices.put(feat, citiness);
    }

    // cree la collection des batiments bufferises
    Geometry[] bufferGeoms = new Geometry[citinessIndices.size()];
    GeometryFactory gf = new GeometryFactory();
    int i = 0;
    if (UrbanAreaComputationJTS.logger.isDebugEnabled()) {
      UrbanAreaComputationJTS.logger.debug("construction des "
          + citinessIndices.size() + " buffers");
    }
    for (IFeature feat : citinessIndices.keySet()) {
      if (UrbanAreaComputationJTS.logger.isInfoEnabled()) {
        UrbanAreaComputationJTS.logger.info("   buffers des objets: " + i + "/"
            + citinessIndices.size());
      }
      try {
        double citiness = citinessIndices.get(feat);
        // normalise citiness
        citiness = citiness / maxCitiness;
        double distanceBuffer = k * citiness;
        if (distanceBuffer < 2.0)
          distanceBuffer = 2.0;
        IGeometry geom = feat.getGeom();
        bufferGeoms[i++] = AdapterFactory.toGeometry(gf, geom).buffer(
            distanceBuffer, quadrantSegments, BufferParameters.CAP_ROUND);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    System.gc();

    UrbanAreaComputationJTS.logger.debug("fusion des buffers");
    Geometry union = JtsAlgorithms.union(bufferGeoms);
    bufferGeoms = null;

    UrbanAreaComputationJTS.logger.debug("filtre dp");
    union = JtsAlgorithms.filtreDouglasPeucker(union, douglasThreshold);

    UrbanAreaComputationJTS.logger.debug("fusion");
    union = union.buffer(0);

    UrbanAreaComputationJTS.logger.debug("suppression des trous");
    if (union instanceof Polygon) {
      union = JtsAlgorithms.supprimeTrous((Polygon) union, holeMinArea);
    } else if (union instanceof MultiPolygon) {
      union = JtsAlgorithms.supprimeTrous((MultiPolygon) union, holeMinArea);
    } else {
      UrbanAreaComputationJTS.logger
          .error("Impossible de creer tache urbaine. Type de geometrie non traite: "
              + union);
      return null;
    }

    // pour supprimer les polygones qui sont dans des trous d'autres polygones
    union = union.buffer(0);

    try {
      IGeometry union_ = AdapterFactory.toGM_Object(union);
      return union_;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static double computeCitiness(IPolygon building, int nbNeighbours,
      IFeatureCollection<? extends IFeature> features) {
    double area = building.area();
    double areaSum = 0.0, distanceSum = 0.0;

    // get the nearest neighbours
    Set<IFeature> neighbours = SpatialQuery.selectNearestN(building, features,
        nbNeighbours, 100.0);
    for (IFeature neighbour : neighbours) {
      double distance = building.distance(neighbour.getGeom());
      distanceSum += distance * distance;
      areaSum += neighbour.getGeom().area();
    }
    double citiness = Math.sqrt(areaSum) * Math.sqrt(area) / distanceSum;
    return citiness;
  }

}
