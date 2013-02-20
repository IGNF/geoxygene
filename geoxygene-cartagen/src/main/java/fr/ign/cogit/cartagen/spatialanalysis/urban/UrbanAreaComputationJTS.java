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

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
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

}
