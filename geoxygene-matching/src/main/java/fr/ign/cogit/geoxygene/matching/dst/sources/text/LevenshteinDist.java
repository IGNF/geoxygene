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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.EvidenceCodec;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeoSource;
import fr.ign.cogit.geoxygene.matching.dst.geomatching.GeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.operators.CombinationAlgos;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

public class LevenshteinDist extends GeoSource {
	
	private String attributeName = "nature";
	
	/** Seuil de distance en m. */
	private float threshold = 100f;

	public float getThreshold() {
		return this.threshold;
	}

	public void setThreshold(float t) {
		this.threshold = t;
	}
	
	@Override
	public String getName() {
		return "Distance de Levenshtein";
	}

	@Override
	public double evaluate(IFeature ref, GeomHypothesis candidate) {
		return 0;
	}
	
	@Override
	public List<Pair<byte[], Float>> evaluate(IFeature reference,
	      final List<GeomHypothesis> candidates, EvidenceCodec<GeomHypothesis> codec) {
	
		List<Pair<byte[], Float>> weightedfocalset = new ArrayList<Pair<byte[], Float>>();
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
	    return weightedfocalset;
	}

}