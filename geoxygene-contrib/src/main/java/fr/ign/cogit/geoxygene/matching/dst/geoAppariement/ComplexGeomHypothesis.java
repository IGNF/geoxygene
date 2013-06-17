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
 */
public class ComplexGeomHypothesis extends SimpleGeomHypothesis {
  /**
   * The logger.
   */
  Logger logger = Logger.getLogger(ComplexGeomHypothesis.class);

  /**
   * Features of the hypothesis.
   */
  List<IFeature> fromfeatures;

  /**
   * @param feature
   *        a single feature
   */
  public ComplexGeomHypothesis(IFeature feature) {
    super(feature);
  }

  /**
   * @param features
   *        a list of features
   */
  public ComplexGeomHypothesis(IFeature... features) {
    super();
    try {
      this.fromfeatures = Arrays.asList(features);
      if (this.fromfeatures.size() > 1) {
        IFeature feat = this.fromfeatures.get(0);
        boolean ok = true;
        // Check class coherence
        for (int i = 1; i < this.fromfeatures.size(); i++) {
          if (!feat.getClass().isAssignableFrom(this.fromfeatures.get(i).getClass())) {
            ok = false;
            break;
          }
        }
        if (!ok) {
          logger.error("Complex Geometrical Hypothesis with several Feature types!");
          return;
        }
        // create the merged feature with the union of the geometries of its features
        IFeature merged = feat.getClass().newInstance();
        List<IGeometry> geoms = new ArrayList<IGeometry>(this.fromfeatures.size());
        for (IFeature f : this.fromfeatures) {
          geoms.add(f.getGeom());
        }
        merged.setGeom(JtsAlgorithms.union(geoms));
        this.decoratedFeature = merged;
      } else {
        logger.debug("Creating a complex geometry hypothesis with only 1 object");
        this.decoratedFeature = fromfeatures.get(0);
        this.fromfeatures.add(fromfeatures.get(0));
      }
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }
}
