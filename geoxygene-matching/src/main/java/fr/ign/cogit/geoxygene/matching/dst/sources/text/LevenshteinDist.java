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

import org.apache.commons.lang.StringUtils;
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
  
  private String attributeName1 = "nature";  // toponyme
  private String attributeName2 = "NATURE";  // NOM
  
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
  
  
  /**
   * Mesure de similarité définie par Samal, A., Seth, S. et Cueto, K. A feature-based approach to conflation
   *     of geospatial sources. IJGIS, juillet-août 2004, vol. 18, n°5, p. 459-489
   * 
   * @param s
   * @param t
   * @return int
   *    as coefficient de similarité
   */
  private int compute(String s, String t) {
    if (s == null || t == null) {
      throw new IllegalArgumentException("Strings must not be null");
    }

    return 1 - StringUtils.getLevenshteinDistance(s, t) / Math.max (s.length(), t.length());
  }    
    

}
