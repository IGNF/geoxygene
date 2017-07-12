package fr.ign.cogit.geoxygene.osm.lodanalysis.individual;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.electretri.ELECTRECriterion;

/**
 * ELECTRE TRI criterion used to assess the Level of Detail of a feature. This
 * criterion uses the size of the feature to assess the level of detail. When
 * the size is small, eye perception limits allows to relate it to scale or
 * resolution. When size is big, it is indifferent as it could still be a high
 * resolution feature.
 * @author GTouya
 * 
 */
public class SizeCriterion extends ELECTRECriterion {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static double TOO_SMALL_THRESHOLD = 750.0;
  /**
   * Threshold expressed in map mm extracted from (Ruas, 1999).
   */
  private static double MIN_LENGTH_THRESHOLD = 0.001;
  private static Logger logger = Logger
      .getLogger(SizeCriterion.class.getName());

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
  public SizeCriterion(String nom) {
    super(nom);
    this.setWeight(0.8);
    this.setIndifference(0.01);
    this.setPreference(0.5);
    this.setVeto(0.8);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IGeometry geom = (IGeometry) param.get("geometry");
    // default value
    double value = 0.5;
    if ((geom instanceof IPolygon)) {
      if (geom.area() < TOO_SMALL_THRESHOLD)
        value = 0.1;
      if (geom instanceof ILineString) {
        if (geom.length() < MIN_LENGTH_THRESHOLD * 50000)
          value = 0.2;
        if (geom.length() < MIN_LENGTH_THRESHOLD * 100000)
          value = 0.3;
        if (geom.length() < MIN_LENGTH_THRESHOLD * 250000)
          value = 0.4;
      }
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
