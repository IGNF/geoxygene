package fr.ign.cogit.geoxygene.osm.lodanalysis.individual;

import java.util.Map;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.ELECTRECriterion;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * ELECTRE TRI criterion used to assess the Level of Detail of a feature. This
 * criterion computes the vertex density, i.e. the number of vertices divided by
 * the length of the feature. The criterion is normalised according to the
 * formula from (Reimer et al 14).
 * @author GTouya
 * 
 */
public class ReimerScaleCriterion extends ELECTRECriterion {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static Logger logger = Logger.getLogger(ELECTRECriterion.class
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
  public ReimerScaleCriterion(String nom) {
    super(nom);
    this.setWeight(1.2);
    this.setIndifference(0.1);
    this.setPreference(0.2);
    this.setVeto(0.5);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    IGeometry geom = (IGeometry) param.get("geometry");
    if (geom == null)
      return 0;
    // length is in map millimeters at the 1:250000 scale
    double density = geom.numPoints() / (geom.length() / 250.0);
    double scale = 250000.0 * (1 / density) * (1 / density);
    double value = 1.0;
    if (scale < 15000.0)
      value = scale / 15000.0 * 0.2;
    else if (scale < 50000.0)
      value = (scale - 15000.0) / 35000.0 * 0.2 + 0.2;
    else if (scale < 150000.0)
      value = (scale - 50000.0) / 100000.0 * 0.2 + 0.4;
    else if (scale < 750000.0)
      value = (scale - 150000.0) / 600000.0 * 0.2 + 0.6;
    else
      value = Math.min((scale - 750000.0) / 600000.0 * 0.2 + 0.8, 1.0);
    logger.finer("criterion value: " + value + " with scale value of " + scale);
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
