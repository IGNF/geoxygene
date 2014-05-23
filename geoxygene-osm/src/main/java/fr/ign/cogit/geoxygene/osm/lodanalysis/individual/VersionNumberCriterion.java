package fr.ign.cogit.geoxygene.osm.lodanalysis.individual;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.util.multicriteriadecision.classifying.electretri.ELECTRECriterion;

public class VersionNumberCriterion extends ELECTRECriterion {

  private static Logger logger = Logger.getLogger(VersionNumberCriterion.class
      .getName());

  public VersionNumberCriterion(String nom) {
    super(nom);
    this.setWeight(0.8);
    this.setIndifference(0.1);
    this.setPreference(0.2);
    this.setVeto(0.7);
  }

  // Getters and setters //

  // Other public methods //
  @Override
  public double value(Map<String, Object> param) {
    // get the source as parameter
    double value = 0.5;
    int number = (Integer) param.get("version");
    if (number == 1)
      value = 0.6;
    else if (number == 2)
      value = 0.5;
    else if (number == 3)
      value = 0.3;
    else if (number <= 10)
      value = 0.2;
    else if (number > 10)
      value = 0.1;
    if (logger.isLoggable(Level.FINER))
      logger.finer("criterion value: " + value);
    return value;
  }

}
