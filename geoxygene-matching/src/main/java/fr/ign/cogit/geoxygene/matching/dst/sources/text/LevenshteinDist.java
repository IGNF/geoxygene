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

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.sources.GeoSource;

/**
 * Critère basé sur la distance de Levenshtein.
 * 
 * @author Marie-Dominique Van Damme
 */
@XmlRootElement(name = "LevenshteinDist")
public class LevenshteinDist extends GeoSource {
  
  private static Logger DST_LOGGER = Logger.getLogger("DSTLogger");
  
  private String attributeName1 = "toponyme";  // toponyme
  private String attributeName2 = "NOM";  // TOPONYME
  
  @Override
  public String getName() {
      return "Distance de Levenshtein";
      // sur les attributs " + attributeName1 + " et " + attributeName2
  }

  @Override
  public double[] evaluate(IFeature ref, GeomHypothesis candidate) {
    
    Object bdcNature = ref.getAttribute(attributeName1).toString();
    Object bdnNature = candidate.getAttribute(attributeName2);
    // System.out.print(ref.getFeatureType().getFeatureAttributes().size() + ", " + candidate.getFeatureType().getFeatureAttributes().size() + " -- ");
    
    if (bdcNature != null && bdnNature != null) {
      
      double distance = compute(bdcNature.toString(), bdnNature.toString());
      // DST_LOGGER.info("        distance = " + distance + "(bdcNature = " + bdcNature.toString() + ", bdnNature = " + bdnNature.toString() + ")");
      DST_LOGGER.info("        distance = " + distance + " (bdc = " + bdcNature.toString() + ", bdn = " + bdnNature.toString() + ")");
      System.out.println(" bdcNature = " + bdcNature.toString() + ", bdnNature = " + bdnNature.toString() + " => " + distance);
      
      return getMasses(distance);
      
    } else {
      double[] masses = new double[3];
      masses[0] = 0;
      masses[1] = 0;
      masses[2] = 1;
      
      System.out.println(ref.getFeatureType().getFeatureAttributes().size() + ", " + candidate.getFeatureType().getFeatureAttributes().size() + " -- ");
      return masses;
      /*for (int i = 0; i < ref.getFeatureType().getFeatureAttributes().size(); i++) {
        System.out.print(ref.getFeatureType().getFeatureAttributes().get(i).getMemberName()+", ");
      }
      System.out.print(" -- ");
      for (int i = 0; i < candidate.getFeatureType().getFeatureAttributes().size(); i++) {
        System.out.print(candidate.getFeatureType().getFeatureAttributes().get(i).getMemberName()+", ");
      }
      System.out.println("");*/
      
    }
     
    
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
  private double compute(String s, String t) {
    if (s == null || t == null) {
      throw new IllegalArgumentException("Strings must not be null");
    }

    double l = StringUtils.getLevenshteinDistance(s, t);
    DST_LOGGER.info("        StringUtils.getLevenshteinDistance : " + l);
    // return 1.0 - l / Math.max (s.length(), t.length());
    return l / Math.max (s.length(), t.length());
  }

}
