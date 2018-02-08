package fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.Profile;
import fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.stats.ProfileAutoCorrelation;

public class ProfilePatternDetector {

	private static Logger logger = Logger.getLogger(ProfilePatternDetector.class);

	private int patternMinLength;

	public ProfilePatternDetector(int patternMinLength) {
		this.patternMinLength = patternMinLength;
	}

	public int getPatternMinLength() {
		return patternMinLength;
	}

	public List<Pattern> patternDetector(Profile p, Profile.SIDE s) {

		List<Pattern> lPatterns = new ArrayList<>();

		if (this.patternMinLength < 1) {
			return lPatterns;
		}

		// Longeur du signal
		int profileLength = p.getNumberOfPoints();
		logger.debug("Profile length" + profileLength);

		// Parcourt de la liste de points
		for (int i = 0; i < profileLength - this.getPatternMinLength(); i++) {

			// Parcourt de la longueur de pattern
			int longueurMaxPattern = (profileLength - i) / 2;
			logger.debug("Max pattern length" + longueurMaxPattern);

			for (int patternLength = patternMinLength; patternLength < longueurMaxPattern; patternLength++) {

				// Parcourt de la longueur max du pattern à considérer
				int numberOfMaxReapeat = (profileLength - i) / patternLength;

				logger.debug("Max pattern repeat" + numberOfMaxReapeat);

				for (int repeat = 1; repeat < numberOfMaxReapeat; repeat++) {
					// Calcul du pattern
					Pattern patternCalculated = calculateCorrelation(p, i, repeat, patternLength, s);
					if (patternCalculated != null) {
						lPatterns.add(patternCalculated);
					}

				}

			}

		}

		Collections.sort(lPatterns);
		return lPatterns;

	}

	private Pattern calculateCorrelation(Profile p, int posIni, int repeat, int patternLength, Profile.SIDE s) {
		// Extract the generator and the signal to test
		List<Double> generatorHeights = p.getHeightAlongRoad(s, posIni, posIni + patternLength);
		List<Double> signalHeights = p.getHeightAlongRoad(s, posIni + patternLength,
				posIni + patternLength + patternLength * repeat);

		// Assessing corrrelation

		// Determining the average
		double moyGenerator = moyenne(generatorHeights);
		double moySignal = moyenne(signalHeights);

		// Determining the variance
		double varianceGenerator = ProfileAutoCorrelation.calculateVariance(generatorHeights, moyGenerator);
		double varianceSignal = ProfileAutoCorrelation.calculateVariance(signalHeights, moySignal);

		// The number of samples corrersponds to the longest signal
		int nbValue = signalHeights.size();

		// Initializing correlatino value
		double valueCorrelation = 0;

		// For each sampling
		for (int i = 0; i < nbValue; i++) {
			// Comparaison to average (a modulo is used for the generator to
			// simulate the repetition of the signal)
			double distMoy1 = generatorHeights.get(i % patternLength) - moyGenerator;
			double distMoy2 = signalHeights.get(i) - moySignal;

			// multiplying the difference between the signal
			valueCorrelation = valueCorrelation + distMoy1 * distMoy2;

		}

		// correlation as the sum of the signal difference normalized by the
		// number of values and the variance of the signals
		double correlation = valueCorrelation / (Math.pow(varianceGenerator * varianceSignal, 0.5) * nbValue);

		if (Double.isNaN(correlation)) {
			return null;
		}

		// Pattern(Profile profile, Profile.SIDE side, int length, double
		// correlationScore, int indexBegin,
		// int repeat)
		Pattern pattern = new Pattern(p, s, patternLength, correlation, posIni, repeat);

		return pattern;

	}

	private static double moyenne(List<Double> lDouble) {
		double val = 0;
		for (Double d : lDouble) {
			val = val + d;
		}
		return val / lDouble.size();
	}

}
