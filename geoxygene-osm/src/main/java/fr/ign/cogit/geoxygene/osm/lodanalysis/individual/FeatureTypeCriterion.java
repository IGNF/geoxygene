package fr.ign.cogit.geoxygene.osm.lodanalysis.individual;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.land.ITreePoint;
import fr.ign.cogit.cartagen.core.genericschema.misc.IPointOfInterest;
import fr.ign.cogit.cartagen.core.genericschema.road.IPathLine;
import fr.ign.cogit.cartagen.core.genericschema.road.IRoadLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.ELECTRECriterion;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObj;

/**
 * An ELECTRE TRI criterion that analyses the feature type to assess the
 * possible level of detail: some feature types correspond to high resolution
 * features, some to low resolution features, but most are indifferent (roads
 * and rivers for instance).
 * @author GTouya
 * 
 */
public class FeatureTypeCriterion extends ELECTRECriterion {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static Logger logger = Logger.getLogger(FeatureTypeCriterion.class
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
  public FeatureTypeCriterion(String nom) {
    super(nom);
    this.setWeight(0.8);
    this.setIndifference(0.2);// e.g. buildings and building points are
    // indifferent
    this.setPreference(0.4);
    this.setVeto(1.0);// no veto for this criterion
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    // the parameter is the feature itself
    OsmGeneObj obj = (OsmGeneObj) param.get("feature");
    double value = 0.5;
    if (obj instanceof IBuilding) {
      value = 0.2;
    }
    if (obj instanceof IPointOfInterest) {
      value = 0.1;
    }
    if (obj instanceof IPathLine) {
      value = 0.3;
    }
    if (obj instanceof IRoadLine) {
      if (((IRoadLine) obj).getImportance() == 0) {
        value = 0.3;
      }
    }
    if (obj instanceof IBuildPoint) {
      value = 0.4;
    }
    if (obj instanceof ISimpleLandUseArea) {
      value = 0.7;
    }
    if (obj instanceof ITreePoint) {
      value = 0.3;
    }
    if (FeatureTypeCriterion.logger.isLoggable(Level.FINER)) {
      FeatureTypeCriterion.logger.finer("criterion value: " + value);
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
