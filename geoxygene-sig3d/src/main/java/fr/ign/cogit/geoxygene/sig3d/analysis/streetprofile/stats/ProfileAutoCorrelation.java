package fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.stats;

import java.util.List;


/**
 * Calculation of mono-dimensionnal (just using height) autocorrelation to dectect periodicity in Profile.
 * 
 * Two methods implemented (with variation of considered period from 1 to signal.lengt / 2:
 * - Classical ACF : https://fr.wikipedia.org/wiki/Autocorr%C3%A9lation
 * - Yin method : http://audition.ens.fr/adc/pdf/2002_JASA_YIN.pdf
 * 
 * @author mbrasebin
 *
 */
public class ProfileAutoCorrelation {

	private double[] tabACF;
	private double[] tabYIN;

	public void calculateACF(List<Double> heights) {

		int nbEstimations = (heights.size()) / 2;

		tabACF = new double[nbEstimations];
		
		
		ProfileBasicStats pb = new ProfileBasicStats();
		pb.calculate(heights);
		
		double variance = calculateVariance(heights, pb.getMoy());
		

		for (int i = 0; i < nbEstimations; i++) {
			tabACF[i] = calculateACFCorrelation(heights, i, pb.getMoy()) / variance;
		}

	}
	
	
	public static double calculateVariance(List<Double> heights, double moyenne){
		double value = 0 ;
		
		for(Double h: heights){
			value = value + Math.pow(h-moyenne, 2);
		}
		return value / heights.size();
	}

	private double calculateACFCorrelation(List<Double> heights, int frequency, double moy) {
		double value = 0;
		

		
		
		int nbElem = heights.size() - frequency;
		for (int i = 0; i < nbElem; i++) {
			value = value + (heights.get(i) -  moy) * (heights.get(i + frequency) - moy);
		}

		return value / nbElem;
	}
	
	
	

	public void calculateMethodYin(List<Double> heights) {

		int nbEstimations = (heights.size())/2;

		tabYIN = new double[nbEstimations];
		
		for (int i = 0; i < nbEstimations; i++) {
			tabYIN[i] = calculateACFDistancen(heights, i);
			
			double sum = 0;
			
			for(int j = 1; j <= i; j++){
				sum = sum+ calculateACFDistancen(heights, j);
			}
			
			
			if(i !=0){
				tabYIN[i]  = 	tabYIN[i]  / (sum / i);
			}


		}
	}
	
	
	

	private double calculateACFDistancen(List<Double> heights, int frequency) {
		double value = 0;
		int nbElem = heights.size() - frequency;
		for (int i = 0; i < nbElem; i++) {
			value = value + Math.pow(heights.get(i) - heights.get(i + frequency), 2);
		}

		return value / nbElem;
	}

	public double[] getTabACF() {
		return tabACF;
	}

	public double[] getTabYIN() {
		return tabYIN;
	}
	
	
	
	
}
