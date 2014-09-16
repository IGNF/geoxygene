package fr.ign.cogit.geoxygene.matching.dst.sources.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
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
	    	// System.out.println(h.getFeatureType().getFeatureAttributes().size());
	    	
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
