package fr.ign.cogit.geoxygene.style.interpolation;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.style.AbstractSymbolizer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

public class SLDMixer {
  protected static Logger logger = Logger.getLogger(AbstractSymbolizer.class
    .getName());
  public static StyledLayerDescriptor mix(StyledLayerDescriptor sld1, StyledLayerDescriptor sld2){
    logger.warn("Un-implemented method. Return second SLD");
    return sld2;    
  }
}
