package fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.Profile;
import fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile.stats.ProfileAutoCorrelation;

public class ProfilePatternDetector {

	private static Logger logger = LogManager.getLogger(ProfilePatternDetector.class);

	private int patternMinLength;

	public ProfilePatternDetector(int patternMinLength) {
		this.patternMinLength = patternMinLength;
	}

	public int getPatternMinLength() {
		return patternMinLength;
	}

	public HashMap<Integer, List<Pattern>> patternDetector(Profile p, Profile.SIDE s) {
		return patternDetector(p, s, 2, 1, 500, 10);
	}

	public HashMap<Integer, List<Pattern>> patternDetector(Profile p, Profile.SIDE s, double threshold,
			int numberOfMinimalRepeat, int maxPatternLength, int maxRepeat) {

		// Integer = repeatLength; ListPattern contains best correlation
		// (positive and negatiive and correlation other provided threshol in positive
		// and negative)
		HashMap<Integer, List<Pattern>> patternsMap = new HashMap<>();

		if (this.patternMinLength < 1) {
			return patternsMap;
		}

		// Longeur du signal
		int profileLength = p.getNumberOfPoints();
		logger.debug("Profile length" + profileLength);

		// Parcourt de la liste de points
		for (int i = 0; i < profileLength - this.getPatternMinLength(); i++) {

			// Parcourt de la longueur de pattern
			int longueurMaxPattern = Math.min(maxPatternLength, (profileLength - i) / 2);

			logger.debug("Max pattern length" + longueurMaxPattern);

			for (int patternLength = patternMinLength; patternLength < longueurMaxPattern; patternLength++) {

				// Parcourt de la longueur max du pattern à considérer
				int numberOfMaxReapeat = Math.min(maxRepeat, (profileLength - i) / patternLength);

				logger.debug("Max pattern repeat" + numberOfMaxReapeat);

				for (int repeat = numberOfMinimalRepeat; repeat < numberOfMaxReapeat; repeat++) {
					// Calcul du pattern
					Pattern patternCalculated = calculateCorrelation(p, i, repeat, patternLength, s);
					if (patternCalculated != null) {

						int repeatCalculated = patternCalculated.getRepeat();

						List<Pattern> lP = patternsMap.get(repeatCalculated);

						if (lP == null) {
							lP = new ArrayList<>();
							patternsMap.put(repeatCalculated, lP);
						}

						// 0 or 1 element we add it
						if (lP.size() == 1 || lP.isEmpty()) {
							lP.add(patternCalculated);
							Collections.sort(lP);
							continue;
						}

						// List with more than 2 elements
						double calculatedCorrelationValue = patternCalculated.getCorrelationScore();
						// IS it the smallest ?
						if (calculatedCorrelationValue < lP.get(0).getCorrelationScore()) {
							lP.add(0, patternCalculated);
							if (Math.abs(lP.get(1).getCorrelationScore()) < threshold) {
								lP.remove(1);
							}
						}

						// IS it the biggest ?
						if (calculatedCorrelationValue > lP.get(lP.size() - 1).getCorrelationScore()) {
							lP.add(patternCalculated);
							if (Math.abs(lP.get(lP.size() - 2).getCorrelationScore()) < threshold) {
								lP.remove(lP.size() - 2);
							}
						}

						if (Math.abs(calculatedCorrelationValue) > threshold) {
							lP.add(patternCalculated);
							Collections.sort(lP);
						}

					}

				}

			}

		}

		return patternsMap;

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
