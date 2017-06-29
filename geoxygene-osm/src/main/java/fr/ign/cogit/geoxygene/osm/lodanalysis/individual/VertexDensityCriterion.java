package fr.ign.cogit.geoxygene.osm.lodanalysis.individual;

import java.util.Map;
import java.util.logging.Logger;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.electretri.ELECTRECriterion;

/**
 * ELECTRE TRI criterion used to assess the Level of Detail of a feature. This
 * criterion computes the vertex density, i.e. the number of vertices divided by
 * the length of the feature. The criterion does not apply to point features.
 * The bigger the density is, the higher resolution is.
 * @author GTouya
 * 
 */
public class VertexDensityCriterion extends ELECTRECriterion {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static Logger logger = Logger
      .getLogger(ELECTRECriterion.class.getName());

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
  public VertexDensityCriterion(String nom) {
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
    double power = (Double) param.get("power");
    double density = geom.numPoints() / geom.length();
    double value = Math.pow(1 - density, power);
    if (value > 1)
      value = 1;
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
