package fr.ign.cogit.osm.lodanalysis.individual;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.ELECTRECriterion;
import fr.ign.cogit.osm.schema.OsmSource;

/**
 * ELECTRE TRI criterion used to assess the Level of Detail of a feature. This
 * criterion uses the "source" OpenStreetMap tag to assess resolution.
 * @author GTouya
 * 
 */
public class SourceCriterion extends ELECTRECriterion {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static Logger logger = Logger.getLogger(SourceCriterion.class
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
  public SourceCriterion(String nom) {
    super(nom);
    this.setWeight(0.9);
    this.setIndifference(0.2);
    this.setPreference(0.3);
    this.setVeto(0.6);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    // get the source as parameter
    double value = 0.5;
    OsmSource source = (OsmSource) param.get("source");
    if (source.equals(OsmSource.UNKNOWN))
      value = 0.5;
    if (source.equals(OsmSource.DGI))
      value = 0.1;
    if (source.equals(OsmSource.CORINE_LANDCOVER))
      value = 0.7;
    if (source.equals(OsmSource.BING))
      value = 0.3;
    if (source.equals(OsmSource.PGS))
      value = 0.9;
    if (source.equals(OsmSource.STATIONS_GPL))
      value = 0.25;
    if (source.equals(OsmSource.SURVEY))
      value = 0.25;
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
