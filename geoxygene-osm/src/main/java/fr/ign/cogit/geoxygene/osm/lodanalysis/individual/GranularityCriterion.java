package fr.ign.cogit.geoxygene.osm.lodanalysis.individual;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.electretri.ELECTRECriterion;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

/**
 * ELECTRE TRI criterion used to assess the Level of Detail of a feature. The
 * criterion computes the size of the shortest edge of the geometry, which is
 * directly related to scale thanks to eye perception limits. This criterion is
 * not dedicated to point features.
 * @author GTouya
 * 
 */
public class GranularityCriterion extends ELECTRECriterion {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static Logger logger = Logger
      .getLogger(GranularityCriterion.class.getName());

  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public GranularityCriterion(String nom) {
    super(nom);
    this.setWeight(1.0);
    // the distribution of granularity on BD TOPO datasets show bug scattering
    // so, the veto is rare and the preference threshold is big.
    this.setIndifference(0.25);
    this.setPreference(0.4);
    this.setVeto(0.8);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IGeometry geom = (IGeometry) param.get("geometry");
    double value = 0;
    if (!(geom instanceof IPoint)) {
      double shortEdge = CommonAlgorithmsFromCartAGen
          .getShortestEdgeLength(geom);
      // the shortest edge length of most features is not between 0 & 1, so a
      // transformation of the measurement has to be made to get a criterion
      // value between 0 et 1. The thresholds derive from studies in the PhD of
      // JF Girres and empirical measurements on OSM and BDTOPO data.
      if (shortEdge <= 5.0)
        value = 0.08 * shortEdge;
      else if (shortEdge <= 10.0)
        value = 0.04 * shortEdge;
      else if (shortEdge <= 25.0)
        value = 0.013 * shortEdge + 0.47;// 0.6 for 100k limit & 0.8 for 250k
      else
        value = 0.0027 * shortEdge + 1.333; // 0.8 for 250k limit & 1 for 1M
      // limit
      if (value > 1)
        value = 1;
    }
    if (logger.isLoggable(Level.FINER))
      logger.finer("criterion value: " + value);
    return value;
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
