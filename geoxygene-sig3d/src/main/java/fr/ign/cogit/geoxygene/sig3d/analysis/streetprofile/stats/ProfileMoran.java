package fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.stats;

import java.util.ArrayList;
import java.util.List;

public class ProfileMoran {

	double moranProfileFinal = -2.0;

	public static void main(String[] args) {

		int n = 1000;

		ArrayList<Double> test = new ArrayList<Double>();
		System.out.println("liste taille" + test.size());

		for (int i = 0; i < n; i++) {

			// if (randomnum.nextInt(2)>0) {
			// //if(i<25 || i >= 75 ){
			// test.add(10.);
			// }
			// else{
			// test.add(0.);
			// }
			test.add(0.0);

		}
		System.out.println(test.toString());
		ProfileMoran p = new ProfileMoran();
		p.calculate(test);
		System.out.println(p.getMoranProfileFinal());

	}// main

	// distance between i and j on curve of length dmax (discrete)

	public int distanceCurvi(int i, int j, int dmax) {
		return Math.min(Math.abs(j - i), i + (dmax - j));

	}

	public void calculate(List<Double> heights) {

		int nbpts = heights.size();

		double sum_weights = 0;
		for (int i = 0; i < nbpts; i++) {
			for (int j = 0; j < nbpts; j++) {
				if (i != j) {
					sum_weights += (1.0 / distanceCurvi(i, j, nbpts));
				}
			}
		}

		double htot = 0;
		for (int i = 0; i < heights.size(); i++) {
			htot = htot + heights.get(i);
		}

		double hmean = htot / nbpts;

		double numer = 0;
		double denom = 0;
		double moran = 0;
		for (int i = 0; i < nbpts; i++) {
			for (int j = 0; j < nbpts; j++) {
				if (i != j) {
					numer += (heights.get(i) - hmean) * (heights.get(j) - hmean) / distanceCurvi(i, j, nbpts);
					// denom += ((energy_parcels.get(i) - mean_energy) *
					// (energy_parcels.get(i) - mean_energy));

				}
			}
			denom += ((heights.get(i) - hmean) * (heights.get(i) - hmean));
		}
		moran = numer / denom;

		moran *= (nbpts / sum_weights);

		this.moranProfileFinal = moran;

		// moran should be between -1 and 1
		if (moranProfileFinal > 1 || moranProfileFinal < -1) {
			System.out.println("# erreur dans le calcul de moran :" + this.moranProfileFinal);
		}

	}
	// System.out.println("moran de la zone " + moran);

	public double getMoranProfileFinal() {
		return moranProfileFinal;
	}

}
