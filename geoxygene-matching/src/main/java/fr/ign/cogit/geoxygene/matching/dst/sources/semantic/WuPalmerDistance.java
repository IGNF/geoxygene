package fr.ign.cogit.geoxygene.matching.dst.sources.semantic;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.sources.GeoSource;

public class WuPalmerDistance extends GeoSource {
  
  private static Logger DST_LOGGER = Logger.getLogger("DSTLogger");
  
  @Override
  public String getName() {
      return "Distance sémantique de Wu and Palmer";
  }

  @Override
  public double[] evaluate(IFeature reference, GeomHypothesis candidate) {
    
    float distance = 0.5f;
    DST_LOGGER.debug("        distance = " + distance);
    
    return getMasses(distance);
  }

}
