package fr.ign.cogit.geoxygene.matching.dst.sources.semantic;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.function.FunctionEvaluationException;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeoSource;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;

public class WuPalmerDistance extends GeoSource {
  
  private static Logger DST_LOGGER = Logger.getLogger("DSTLogger");
  
  @Override
  public String getName() {
      return "Distance sémantique de Wu and Palmer";
  }

  @Override
  public double[] evaluate(IFeature reference, GeomHypothesis candidate) {
    double[] masses = new double[3];
    
    float distance = 0.5f;
    DST_LOGGER.debug("        distance = " + distance);
    
    // Fonction EstApparie
    float masse1 = 0.0f;
    if (fEA != null) {
      for (Function1D f : fEA) {
        if (f.isBetween(distance)) {
          try {
            masse1 = f.evaluate(distance).floatValue();
          } catch (FunctionEvaluationException e) {
            e.printStackTrace();
          }
        }
      }
    }
    masses[0] = masse1;
    
    // Fonction NonApparie
    float masse2 = 0.0f;
    if (fNA != null) {
      for (Function1D f : fNA) {
        if (f.isBetween(distance)) {
          try {
            masse2 = f.evaluate(distance).floatValue();
          } catch (FunctionEvaluationException e) {
            e.printStackTrace();
          }
        }
      }
    }
    masses[1] = masse2;
    
    // Fonction PrononcePas
    float masse3 = 0.0f;
    if (fPP != null) {
      for (Function1D f : fPP) {
        if (f.isBetween(distance)) {
          try {
            masse3 = f.evaluate(distance).floatValue();
          } catch (FunctionEvaluationException e) {
            e.printStackTrace();
          }
        }
      }
    }
    masses[2] = masse3;
     
    return masses;
  }

}
