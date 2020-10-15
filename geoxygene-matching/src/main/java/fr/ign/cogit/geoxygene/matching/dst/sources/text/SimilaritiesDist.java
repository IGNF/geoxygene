package fr.ign.cogit.geoxygene.matching.dst.sources.text;

import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.sources.GeoSource;

/**
 * Critère basé sur une distance de similarité.
 * 
 * @author Bertrand Dumenieu
 */
@XmlRootElement(name = "SimilaritiesDist")
public class SimilaritiesDist extends GeoSource {
  
private static Logger DST_LOGGER = LogManager.getLogger("DSTLogger");
  
  private String attributeNameRef = "";
  private String attributeNameCandidat = "";
  
  
  public SimilaritiesDist(String att1, String att2) {
    this.attributeNameRef = att1;
    this.attributeNameCandidat = att2;
  }
  
  @Override
  public String getName() {
      return "Distance de similarité";
  }
  
  @Override
  public double[] evaluate(IFeature ref, GeomHypothesis candidate) {
    
    Object valueRef = ref.getAttribute(attributeNameRef).toString();
    Object valueCandidat = candidate.getAttribute(attributeNameCandidat);
    
    if (valueRef != null && valueCandidat != null) {
      
      double distance = compute(valueRef.toString(), valueCandidat.toString());
      DST_LOGGER.info("        distance = " + distance + " (ref = " + valueRef.toString() + ", candidat = " + valueCandidat.toString() + ")");
      
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
  
  /**
   * Mesure de similarité.
   * 
   * @param s
   * @param t
   * @return int
   *    as coefficient de similarité
   */
  public double compute(String s1, String s2) {
    if (s1 == null || s2 == null) {
      throw new IllegalArgumentException("Strings must not be null");
    }

    double l = 0.0;
    s1 = s1.toLowerCase();
    s2 = s2.toLowerCase();

    s1 = s1.replaceAll("[\\W]|_\\-", " "); //$NON-NLS-1$ //$NON-NLS-2$
    s2 = s2.replaceAll("[\\W]|_\\-", " "); //$NON-NLS-1$ //$NON-NLS-2$

    StringTokenizer tokenizer = new StringTokenizer(s1);
    StringTokenizer tokenizer2 = new StringTokenizer(s2);
    String s1dm = ""; //$NON-NLS-1$
    DoubleMetaphone m = new DoubleMetaphone();
    while (tokenizer.hasMoreTokens()) {
      s1dm += m.doubleMetaphone(tokenizer.nextToken()) + " "; //$NON-NLS-1$
    }
    s1dm = s1dm.trim();
    String s2dm = ""; //$NON-NLS-1$
    while (tokenizer2.hasMoreTokens()) {
      s2dm += m.doubleMetaphone(tokenizer2.nextToken()) + " "; //$NON-NLS-1$
    }
    s2dm = s2dm.trim();
    
    l = longestSubstrSim(s1dm, s2dm);
    DST_LOGGER.info("        Distance de similarité : " + l);
    
    return 1 - l;

  }

  public static float longestSubstrSim(String s_, String t_) {
    int max_size = Math.max(s_.replaceAll(" ", "").length(), //$NON-NLS-1$ //$NON-NLS-2$
        t_.replaceAll(" ", "").length()); //$NON-NLS-1$ //$NON-NLS-2$
    return (float) longestSubstr(s_, t_) / (float) max_size;
  }

  public static int longestSubstr(String s_, String t_) {
    String s = s_.toLowerCase();
    s = s.replaceAll("[\\W]|_\\- ", ""); //$NON-NLS-1$ //$NON-NLS-2$

    String t = t_.toLowerCase();
    t = t.replaceAll("[\\W]|_\\- ", ""); //$NON-NLS-1$ //$NON-NLS-2$
    s = s.trim();
    t = t.trim();
    if (s.isEmpty() || t.isEmpty()) {
      return 0;
    }

    int m = s.length();
    int n = t.length();
    int cost = 0;
    int maxLen = 0;
    int[] p = new int[n];
    int[] d = new int[n];

    for (int i = 0; i < m; ++i) {
      for (int j = 0; j < n; ++j) {
        if (s.charAt(i) != t.charAt(j)) {
          cost = 0;
        } else {
          if ((i == 0) || (j == 0)) {
            cost = 1;
          } else {
            cost = p[j - 1] + 1;
          }
        }
        d[j] = cost;

        if (cost > maxLen) {
          maxLen = cost;
        }
      }
      int[] swap = p;
      p = d;
      d = swap;
    }

    return maxLen;
  }
}
