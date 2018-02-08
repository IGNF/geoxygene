package fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.pattern;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.Profile;

public class Pattern implements Comparable<Pattern> {

	private int length;
	private int indexBegin;
	private int repeat;
	private double correlationScore;
	private IFeatureCollection<IFeature> generator = null;
	private IFeatureCollection<IFeature> signal = null;
	private Profile profile;
	private Profile.SIDE side;

	public Pattern(Profile profile, Profile.SIDE side, int length, double correlationScore, int indexBegin,
			int repeat) {
		super();

		this.length = length;
		this.correlationScore = correlationScore;
		this.indexBegin = indexBegin;
		this.repeat = repeat;
		this.profile = profile;
		this.side = side;
	}



	public int getIndexBegin() {
		return indexBegin;
	}

	public int getRepeat() {
		return repeat;
	}

	public int getLength() {
		return length;
	}

	public double getCorrelationScore() {
		return correlationScore;
	}

	public IFeatureCollection<IFeature> getGenerator() {
		if (generator == null) {
			generator = generateGenerator();
		}
		return generator;
	}

	private IFeatureCollection<IFeature> generateGenerator() {
		// Extraction of points corresponding to the generator
		IFeatureCollection<IFeature> signalPoints = new FT_FeatureCollection<>();

		for (int i = indexBegin; i < indexBegin + length; i++) {
			signalPoints.addAll(profile.getPointAtXstep(i, side));
		}

		return signalPoints;
	}

	public IFeatureCollection<IFeature> getSignal() {
		if (signal == null) {
			signal = generateSignal();
		}
		return signal;
	}

	private IFeatureCollection<IFeature> generateSignal() {
		// Extraction of points corresponding to the generator
		IFeatureCollection<IFeature> signalPoints = new FT_FeatureCollection<>();

		for (int i = indexBegin + length; i < indexBegin + length + length * repeat; i++) {
			signalPoints.addAll(profile.getPointAtXstep(i, side));
		}

		return signalPoints;
	}

	@Override
	public int compareTo(Pattern arg0) {

		return Double.compare(this.getCorrelationScore(), arg0.getCorrelationScore());
	}

	@Override
	public String toString() {
		return "Pattern [length=" + length + ", indexBegin=" + indexBegin + ", repeat=" + repeat + ", correlationScore="
				+ correlationScore + "]";
	}

}
