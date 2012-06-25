/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.geoAppariement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;

/**
 * @author Bertrand Dumenieu
 * 
 */
public class ComplexGeomHypothesis extends SimpleGeomHypothesis {
  Logger logger = Logger.getLogger(ComplexGeomHypothesis.class);

  List<IFeature> fromfeatures;

  /**
   * @param feature
   */
  public ComplexGeomHypothesis(IFeature feature) {
    super(feature);
  }

  public ComplexGeomHypothesis(IFeature... features) {
    super();
    try {
      fromfeatures = Arrays.asList(features);
      if (fromfeatures.size() > 1) {
        IFeature feat = fromfeatures.get(0);
        boolean ok = true;
        // Check class coherence
        for(int i = 1; i < fromfeatures.size(); i++){
          if(!feat.getClass().isAssignableFrom(fromfeatures.get(i).getClass())){
            ok = false;
            break;
          }
        }
        if (!ok) {
          logger
              .error("Complex Geometrical Hypothesis with several Feature types!");
          return;
        }
        IFeature merged;
        merged = feat.getClass().newInstance();
        List<IGeometry> geoms = new ArrayList<IGeometry>();
        for(IFeature f : fromfeatures){
          geoms.add(f.getGeom());
        }
        merged.setGeom(JtsAlgorithms.union(geoms));
        this.decoratedFeature = merged;
      } else {
        logger
            .debug("Creating a complex geometry hypothesis with only 1 object");
        this.decoratedFeature = fromfeatures.get(0);
        this.fromfeatures.add(fromfeatures.get(0));
      }
    } catch (InstantiationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
