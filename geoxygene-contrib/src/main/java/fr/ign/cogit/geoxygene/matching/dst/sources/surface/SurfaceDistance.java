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

package fr.ign.cogit.geoxygene.matching.dst.sources.surface;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.EvidenceCodec;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeoSource;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.operators.CombinationAlgos;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

/**
 * Critère d'appariement distance surfacique pour l'appariement d'objets
 * geographiques.
 * @author Julien Perret
 */
public class SurfaceDistance extends GeoSource {

  private float threshold = 50;

  public float getThreshold() {
    return this.threshold;
  }

  public void setThreshold(float t) {
    this.threshold = t;
  }

  public SurfaceDistance() throws Exception {
  }

  /**
   * Evaluation.
   */
  @Override
  public List<Pair<byte[], Float>> evaluate(GeomHypothesis reference, final List<GeomHypothesis> candidates,
      EvidenceCodec<GeomHypothesis> codec) {
    List<Pair<byte[], Float>> weightedfocalset = new ArrayList<Pair<byte[], Float>>();
//    IFeature reference = GeoMatching.getInstance().getReference();
    float sum = 0;
    for (GeomHypothesis h : candidates) {
      // On ne traite que les objets qui intersectent la référence. La distance
      // surfacique est donc sensible aux problèmes de calage.
      if (reference.getGeom().intersects(h.getGeom())) {
        float distance = (float) this.compute(reference.getGeom(), h.getGeom());
        if (distance < this.threshold) {
          byte[] encoded = codec.encode(new GeomHypothesis[] { h });
          weightedfocalset.add(new Pair<byte[], Float>(encoded, distance));
          sum += distance;
        }
      }
    }
    for (Pair<byte[], Float> st : weightedfocalset) {
      st.setSecond(st.getSecond() / sum);
    }
    CombinationAlgos.sortKernel(weightedfocalset);
    return weightedfocalset;
  }

  @Override
  public String getName() {
    return "Distance Surfacique";//TODO using I18N
  }

  private double compute(IGeometry geo1, IGeometry geo2) {
    double value = geo1.intersection(geo2).area() / geo1.union(geo2).area();
    return Math.max(Math.min(value, 1d), 0d);
  }
}
