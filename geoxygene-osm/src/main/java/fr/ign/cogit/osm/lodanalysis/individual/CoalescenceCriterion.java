package fr.ign.cogit.osm.lodanalysis.individual;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.ELECTRECriterion;
import fr.ign.cogit.osm.schema.OsmGeneObj;
import fr.ign.cogit.osm.schema.OsmGeneObjLin;

/**
 * ELECTRE TRI criterion used to assess the Level of Detail of a feature. This
 * criterion is only dedicated to linear features. It adapts the methods to
 * assess the generalisation level from Girres (2011, 2012). Iterative sizes of
 * buffers are applied on the feature and the first buffer size where
 * coalescence happens correspond to the generalised symbol size (directly
 * related to scale). If features aren't sinuous, the criterion is indifferent.
 * @author GTouya
 * 
 */
public class CoalescenceCriterion extends ELECTRECriterion {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static Logger logger = Logger.getLogger(CoalescenceCriterion.class
      .getName());

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
  public CoalescenceCriterion(String nom) {
    super(nom);
    this.setWeight(0.4);
    this.setIndifference(0.2);
    this.setPreference(0.4);
    this.setVeto(0.6);
    // TODO
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    OsmGeneObj obj = (OsmGeneObj) param.get("feature");
    // IGeometry geom = (IGeometry) param.get("geometry");
    double value = 0.5;
    if (obj instanceof OsmGeneObjLin) {
      // compute sinuosity
      // TODO à coder avec une énumération pour le résultat
      // if it is not sinuous, default value
      // TODO
      // else, try to find the buffer size that causes coalescence
      // TODO
    }
    if (CoalescenceCriterion.logger.isLoggable(Level.FINER)) {
      CoalescenceCriterion.logger.finer("criterion value: " + value);
    }
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
