package fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.stats;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.BuildingProfileParameters;
import fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.Profile;
import fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.Profile.SIDE;

/**
 * Multi-dimensionnal correlation method using classical ACF method by using
 * both height and depth information
 * 
 * 
 * @author mbrasebin
 *
 */
public class ProfileMultiDimensionnalCorrelation {

	public double[] getTabACF() {
		return tabACF;
	}

	private double[] tabACF;

	public void calculate(Profile p, Profile.SIDE s, double defaultValue, double maxHeight) {

		int nbPoints = p.getNumberOfPoints();

		int nbEstimations = nbPoints / 2;

		tabACF = new double[nbEstimations];
		
		double moyenne = evaluateMyenne(p,s,nbPoints, defaultValue);
		double variance = evaluateVariance(p,s,nbPoints, defaultValue, moyenne);
		
		
		

		for (int i = 0; i < nbEstimations; i++) {
			tabACF[i] = calculateACFCorrelation(p, s, i, nbPoints, defaultValue, moyenne, maxHeight)/ variance;

		}

	}
	
	
	private double evaluateMyenne(Profile p, SIDE s,int nbPoints, double defaultValue) {
		
		
		double value = 0;
		double count = 0;
		
		for (int i = 0; i < nbPoints; i++) {
			IFeatureCollection<IFeature> feat1 = p.getPointAtXstep(i, s);


		
			
			

			for (int j = 0; j < feat1.size(); j++) {
				
				value = value + getValue(feat1, j, defaultValue);
				
				count ++;
			}
			
		}
		
		return value/count;
		
	}
	
	
	private double evaluateVariance(Profile p, SIDE s,int nbPoints, double defaultValue, double moyenne) {
		
		double value = 0;
		double count = 0;
		
		for (int i = 0; i < nbPoints; i++) {
			IFeatureCollection<IFeature> feat1 = p.getPointAtXstep(i, s);

			

			for (int j = 0; j < feat1.size(); j++) {
				
				value = value + Math.pow(getValue(feat1, j, defaultValue)-moyenne,2);
				
				count ++;
			}
			
		}
		
		return value/count;
		
		
	}

	private double calculateACFCorrelation(Profile p, SIDE s, int frequency, int nbPoints, double defaultValue, double moyenne, double maxHeight) {

		int nbElem = nbPoints - frequency;
		
		int count =0;
		
		double value = 0;
		for (int i = 0; i < nbElem; i++) {

			IFeatureCollection<IFeature> feat1 = p.getPointAtXstep(i, s);
			IFeatureCollection<IFeature> feat2 = p.getPointAtXstep(i + frequency, s);


			for (int j = 0; j < maxHeight; j++) {
				
				double value1 = getValue(feat1, j, defaultValue);
				double value2 = getValue(feat2, j, defaultValue);
				
				
				value  = value + (value1 - moyenne) * (value2 - moyenne);
				count++;
			}

		}
		
		return value/count;
	}

	private double getValue(IFeatureCollection<IFeature> feat2, int j, double defaultValue) {
		
		if(!feat2.isEmpty() && feat2.size() > j){
			IFeature feat = feat2.get(j);
			
			
			return Double.parseDouble(feat.getAttribute( BuildingProfileParameters.NAM_ATT_DISTANCE).toString());
		}

		return defaultValue;
	}

}
