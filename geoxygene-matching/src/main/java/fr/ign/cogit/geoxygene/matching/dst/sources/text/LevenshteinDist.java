/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.sources.text;

import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.function.FunctionEvaluationException;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.EvidenceCodec;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeoSource;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

/**
 * Critère basé sur la distance de Levenshtein.
 * 
 * @author Marie-Dominique Van Damme
 */
public class LevenshteinDist extends GeoSource {
  
  private static Logger DST_LOGGER = Logger.getLogger("DSTLogger");
  
  private String attributeName1 = "nature";
  private String attributeName2 = "NATURE";
  
  @Override
  public String getName() {
      return "Distance de Levenshtein";
      // sur les attributs " + attributeName1 + " et " + attributeName2
  }

  @Override
  public double[] evaluate(IFeature ref, GeomHypothesis candidate) {
    double[] masses = new double[3];
    
    Object bdcNature = ref.getAttribute(attributeName1).toString();
    Object bdnNature = candidate.getAttribute("NATURE");
    // System.out.print(ref.getFeatureType().getFeatureAttributes().size() + ", " + candidate.getFeatureType().getFeatureAttributes().size() + " -- ");
    
    if (bdcNature != null && bdnNature != null) {
      
      float distance = compute(bdcNature.toString(), bdnNature.toString());
      DST_LOGGER.info("        distance = " + distance + "(bdcNature = " + bdcNature.toString() + ", bdnNature = " + bdnNature.toString() + ")");
      // System.out.println(" bdcNature = " + bdcNature.toString() + ", bdnNature = " + bdnNature.toString() + " => " + distance);
      
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
      
    } else {
      masses[0] = 0;
      masses[1] = 0;
      masses[2] = 0;
      
      System.out.println(ref.getFeatureType().getFeatureAttributes().size() + ", " + candidate.getFeatureType().getFeatureAttributes().size() + " -- ");
      /*for (int i = 0; i < ref.getFeatureType().getFeatureAttributes().size(); i++) {
        System.out.print(ref.getFeatureType().getFeatureAttributes().get(i).getMemberName()+", ");
      }
      System.out.print(" -- ");
      for (int i = 0; i < candidate.getFeatureType().getFeatureAttributes().size(); i++) {
        System.out.print(candidate.getFeatureType().getFeatureAttributes().get(i).getMemberName()+", ");
      }
      System.out.println("");*/
      
    }
     
    return masses;
  }
  
  @Override
  public List<Pair<byte[], Float>> evaluate(IFeature reference,
        final List<GeomHypothesis> candidates, EvidenceCodec<GeomHypothesis> codec) {
  
      /*List<Pair<byte[], Float>> weightedfocalset = new ArrayList<Pair<byte[], Float>>();
      float sum = 0;
      for (GeomHypothesis h : candidates) {
          
          System.out.print(h.getClass() + " - ");
          System.out.print(h.getFeatureType().getFeatureAttributes().size() + " - ");
          System.out.print(h.getFeatureType().getFeatureAttributeByName("nature") + " - ");
          System.out.println("");
          
          float distance = (float) 0.8;   // StringUtils.getLevenshteinDistance(reference.getGeom(), h.getGeom());
          if (distance < this.threshold) {
              distance = (this.threshold - distance) / this.threshold;
              byte[] encoded = codec.encode(new GeomHypothesis[] { h });
              weightedfocalset.add(new Pair<byte[], Float>(encoded, distance));
              sum += distance;
          }
      }
      for (Pair<byte[], Float> st : weightedfocalset) {
          st.setSecond(st.getSecond() / sum);
      }
      CombinationAlgos.sortKernel(weightedfocalset);
      return weightedfocalset;*/
    return null;
  }
  
  private int compute(String s, String t) {
    if (s == null || t == null) {
      throw new IllegalArgumentException("Strings must not be null");
  }

  /*
     The difference between this impl. and the previous is that, rather 
     than creating and retaining a matrix of size s.length()+1 by t.length()+1, 
     we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
     is the 'current working' distance array that maintains the newest distance cost
     counts as we iterate through the characters of String s.  Each time we increment
     the index of String t we are comparing, d is copied to p, the second int[].  Doing so
     allows us to retain the previous cost counts as required by the algorithm (taking 
     the minimum of the cost count to the left, up one, and diagonally up and to the left
     of the current cost count being calculated).  (Note that the arrays aren't really 
     copied anymore, just switched...this is clearly much better than cloning an array 
     or doing a System.arraycopy() each time  through the outer loop.)

     Effectively, the difference between the two implementations is this one does not 
     cause an out of memory condition when calculating the LD over two very large strings.
   */

  int n = s.length(); // length of s
  int m = t.length(); // length of t

  if (n == 0) {
      return m;
  } else if (m == 0) {
      return n;
  }

  if (n > m) {
      // swap the input strings to consume less memory
      String tmp = s;
      s = t;
      t = tmp;
      n = m;
      m = t.length();
  }

  int p[] = new int[n+1]; //'previous' cost array, horizontally
  int d[] = new int[n+1]; // cost array, horizontally
  int _d[]; //placeholder to assist in swapping p and d

  // indexes into strings s and t
  int i; // iterates through s
  int j; // iterates through t

  char t_j; // jth character of t

  int cost; // cost

  for (i = 0; i<=n; i++) {
      p[i] = i;
  }

  for (j = 1; j<=m; j++) {
      t_j = t.charAt(j-1);
      d[0] = j;

      for (i=1; i<=n; i++) {
          cost = s.charAt(i-1)==t_j ? 0 : 1;
          // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
          d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
      }

      // copy current distance counts to 'previous row' distance counts
      _d = p;
      p = d;
      d = _d;
  }

  // our last action in the above loop was to switch d and p, so p now 
  // actually has the most recent cost counts
  return p[n];
}


}
