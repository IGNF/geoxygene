package fr.ign.cogit.geoxygene.osm.lodanalysis.individual;

import java.util.Map;
import java.util.logging.Logger;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.electretri.ELECTRECriterion;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

/**
 * ELECTRE TRI criterion used to assess the Level of Detail of a feature. The
 * criterion computes the median of the edge lengths of the geometry, which is
 * directly related to scale (Girres 2012). This criterion is not dedicated to
 * point features.
 * @author GTouya
 * 
 */
public class EdgeLengthMedianCriterion extends ELECTRECriterion {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static Logger logger = Logger
      .getLogger(EdgeLengthMedianCriterion.class.getName());

  // Public constructors //
  public EdgeLengthMedianCriterion(String nom) {
    super(nom);
    this.setWeight(1.0);
    this.setIndifference(0.1);
    this.setPreference(0.2);
    this.setVeto(0.5);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IGeometry geom = (IGeometry) param.get("geometry");
    double value = 0;
    if (!(geom instanceof IPoint)) {
      // the edge length median is not between 0 & 1, so a transformation of the
      // measurement has to be made to get a criterion value between 0 et 1. The
      // thresholds derive from studies in the PhD of JF Girres and empirical
      // measurements on OSM and BDTOPO data.
      double median = CommonAlgorithmsFromCartAGen.getEdgeLengthMedian(geom);
      if (median <= 10.0)
        value = 0.03 * median;
      else if (median <= 25.0)
        value = 0.013 * median + 0.27;
      else if (median <= 50.0)
        value = 0.008 * median + 0.3;
      else
        value = 0.007 * median + 0.37;
      if (value > 1)
        value = 1;
    }
    logger.finer(EdgeLengthMedianCriterion.class.getName() + ": " + value);
    return value;
  }

}
