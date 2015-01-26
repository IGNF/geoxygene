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
  
  private String attributeNameRef = "";
  private String attributeNameCandidat = "";
  
  
  public LevenshteinDist(String att1, String att2) {
    this.attributeNameRef = att1;
    this.attributeNameCandidat = att2;
  }
  
  @Override
  public String getName() {
      return "Distance de Levenshtein";
  }

  @Override
  public double[] evaluate(IFeature ref, GeomHypothesis candidate) {
    
    Object valueRef = ref.getAttribute(attributeNameRef);
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
   * Mesure de Levenshtein.
   * 
   * @param s
   * @param t
   * @return int
   *    as coefficient de similarité
   */
  public double compute(String s, String t) {
    if (s == null || t == null) {
      throw new IllegalArgumentException("Strings must not be null");
    }

    double l = StringUtils.getLevenshteinDistance(s.toLowerCase(), t.toLowerCase());
    DST_LOGGER.info("        StringUtils.getLevenshteinDistance : " + l);
    
    // return 1.0 - l / Math.max (s.length(), t.length());
    return l / Math.max (s.length(), t.length());
  }

}
