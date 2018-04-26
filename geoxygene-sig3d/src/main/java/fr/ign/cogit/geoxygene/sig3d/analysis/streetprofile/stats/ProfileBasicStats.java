package fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.stats;

import java.util.Arrays;
import java.util.List;

public class ProfileBasicStats {

	private double min;
	private double max;
	private double moy;
	private double med;
	private double std;

	public void calculate(List<Double> heights) {

		min = Double.POSITIVE_INFINITY;
		max = Double.NEGATIVE_INFINITY;

		for (Double d : heights) {
			min = Math.min(min, d);
			max = Math.max(max, d);
		}

		double sum = 0;
		for (double h : heights) {
			sum = sum + h;
		}
		moy = sum / heights.size();

		Double[] heightsTab = new Double[heights.size()];
		heightsTab = heights.toArray(heightsTab);
		Arrays.sort(heightsTab);
		
		if (heightsTab.length % 2 == 0)
			med = ((double) heightsTab[heightsTab.length / 2] + (double) heightsTab[heightsTab.length / 2 - 1]) / 2;
		else
			med = (double) heightsTab[heightsTab.length / 2];
		
		
		for(Double d: heightsTab) {
			std = Math.pow(d - moy, 2) / heightsTab.length;
		}
		
		std = Math.sqrt(std);

	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public double getMoy() {
		return moy;
	}

	public double getMed() {
		return med;
	}

	public double getStd() {
		return std;
	}

}
