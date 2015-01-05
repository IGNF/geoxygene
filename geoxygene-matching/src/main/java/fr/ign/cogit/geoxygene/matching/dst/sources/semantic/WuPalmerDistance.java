package fr.ign.cogit.geoxygene.matching.dst.sources.semantic;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.sources.GeoSource;
import fr.ign.cogit.ontology.similarite.WuPalmerSemanticSimilarity;

public class WuPalmerDistance extends GeoSource {
  
  private static Logger DST_LOGGER = Logger.getLogger("DSTLogger");
  
  private String attributeName1 = "";
  private String attributeName2 = "";
  
  private String uriOnto = "";
  
  public WuPalmerDistance(String uriOnto, String att1, String att2) {
    this.uriOnto = uriOnto;
    this.attributeName1 = att1;
    this.attributeName2 = att2;
  }
  
  @Override
  public String getName() {
      return "Distance sémantique de Wu and Palmer";
   // sur les attributs " + attributeName1 + " et " + attributeName2
  }

  @Override
  public double[] evaluate(IFeature ref, GeomHypothesis candidate) {
    
    Object bdcNature = ref.getAttribute(attributeName1).toString();
    Object bdnNature = candidate.getAttribute(attributeName2);
    // System.out.print(ref.getFeatureType().getFeatureAttributes().size() 
    //    + ", " + candidate.getFeatureType().getFeatureAttributes().size() + " -- ");
    
    if (bdcNature != null && bdnNature != null) {
      
      // 
      double distance = WuPalmerSemanticSimilarity.getEvaluation(uriOnto, bdcNature.toString(), bdnNature.toString());
      
      // DST_LOGGER.info("        distance = " + distance + "(bdcNature = " + bdcNature.toString() + ", bdnNature = " + bdnNature.toString() + ")");
      DST_LOGGER.info("        distance = " + distance + " (bdc = " + bdcNature.toString() + ", bdn = " + bdnNature.toString() + ")");
      // System.out.println(" bdcNature = " + bdcNature.toString() + ", bdnNature = " + bdnNature.toString() + " => " + distance);
      
      return getMasses(distance);
      
    } else {
      double[] masses = new double[3];
      masses[0] = 0;
      masses[1] = 0;
      masses[2] = 1;
      
      System.out.println(ref.getFeatureType().getFeatureAttributes().size() + ", " + candidate.getFeatureType().getFeatureAttributes().size() + " -- ");
      return masses;
    }
    
  }
  
}
