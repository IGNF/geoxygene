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

package fr.ign.cogit.geoxygene.matching.dst.sources.linear;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.EvidenceCodec;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeoSource;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.operators.CombinationAlgos;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

/**
 * Critère d'appariement distance linéaire pour l'appariement d'objets
 * geographiques.
 * @author Julien Perret
 */
public class LineOrientation extends GeoSource {

  private double threshold = Math.PI / 2;

  public double getThreshold() {
    return this.threshold;
  }

  public void setThreshold(double t) {
    this.threshold = t;
  }

  public LineOrientation() throws Exception {
  }

  /**
   * Evaluation.
   */
  @Override
  public List<Pair<byte[], Float>> evaluate(IFeature reference,
      final List<GeomHypothesis> candidates, EvidenceCodec<GeomHypothesis> codec) {
	  
    List<Pair<byte[], Float>> weightedfocalset = new ArrayList<Pair<byte[], Float>>();
    // IFeature reference = GeoMatching.getInstance().getReference();
    float sum = 0;
    for (GeomHypothesis h : candidates) {
      double distance = this.compute(reference.getGeom(), h.getGeom());
      if (distance >= this.threshold) {
        distance = 0.5 * (distance - this.threshold) / (Math.PI - this.threshold);
      } else {
        distance = 0.5 * (this.threshold - distance) / this.threshold;
      }
      if (distance > 0) {
        byte[] encoded = codec.encode(new GeomHypothesis[] { h });
        weightedfocalset.add(new Pair<byte[], Float>(encoded, (float) distance));
        sum += distance;
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
    return "Orientation Générale";// TODO using I18N
  }

  private double compute(IGeometry geo1, IGeometry geo2) {
    // TODO add subsampling?
    Angle a1 = Operateurs.directionPrincipale(geo1.coord());
    Angle a2 = Operateurs.directionPrincipale(geo2.coord());
    double result = Angle.ecart(a1, a2).getValeur();
//    System.out.println("l1 (" + a1.getValeur() + " ) = " + geo1);
//    System.out.println("l2 (" + a2.getValeur() + " ) = " + geo2);
//    System.out.println("result = " + result);
    return result;
  }

  @Override
  public double evaluate(IFeature ref, GeomHypothesis candidate) {
    double distance = compute(ref.getGeom(), candidate.getGeom());
    if (distance >= this.threshold) {
      distance = 0.5 * (distance - this.threshold) / (Math.PI - this.threshold);
    } else {
      distance = 0.5 * (this.threshold - distance) / this.threshold;
    }
    return distance;
  }
}
