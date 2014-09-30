/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.sources.punctual;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.EvidenceCodec;
import fr.ign.cogit.geoxygene.matching.dst.function.Function;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeoSource;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.operators.CombinationAlgos;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

/**
 * 
 * @author Julien Perret
 */
public class EuclidianDist extends GeoSource {
	
	private Function[] f1x;
	private Function[] f1y;
	
	private Function[] f2x;
	private Function[] f2y;
	
	private Function[] f3x;
	private Function[] f3y;
	
	private double bornemax;
	
	public void setBornemax(double bornemax) {
	  this.bornemax = bornemax;
	}
	
	public void setF1x(Function... f1x) {
      this.f1x = f1x;
    }
	
	public void setF1y(Function... f1y) {
      this.f1y = f1y;
    }
	
	public void setF2x(Function... f2x) {
      this.f2x = f2x;
    }
	
	public void setF2y(Function... f2y) {
      this.f2y = f2y;
    }
	
	public void setF3x(Function... f3x) {
      this.f3x = f3x;
    }
	
	public void setF3y(Function... f3y) {
      this.f3y = f3y;
    }
	
	@Override
	public String getName() {
		return "Distance Euclidienne";
	}

	@Override
	public double evaluate(IFeature ref, GeomHypothesis candidate) {
		return 0;
	}
	
	@Override
	public List<Pair<byte[], Float>> evaluate(IFeature reference,
	      final List<GeomHypothesis> candidates, EvidenceCodec<GeomHypothesis> codec) {
	
	  List<Pair<byte[], Float>> weightedfocalset = new ArrayList<Pair<byte[], Float>>();
      for (GeomHypothesis h : candidates) {
        
        byte[] encoded = codec.encode(new GeomHypothesis[] { h });  
        float distance = (float) this.compute(reference.getGeom(), h.getGeom());
          
        // On cherche les masses de croyance associées
          
        // F1
        float masse1 = 0.0f;
        for (Function f : f1x) {
          masse1 = 1.0f;
        }
          
        weightedfocalset.add(new Pair<byte[], Float>(encoded, masse1));
          
      }
      CombinationAlgos.sortKernel(weightedfocalset);
      return weightedfocalset;
	
	}
	
	/**
	 * @param geom
	 * @param geom2
	 * @return
	 */
	private float compute(IGeometry geom, IGeometry geom2) {
	    return (float) geom.distance(geom2);
	}

}
