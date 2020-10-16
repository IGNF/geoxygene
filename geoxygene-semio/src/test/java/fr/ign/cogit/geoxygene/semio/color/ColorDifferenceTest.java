package fr.ign.cogit.geoxygene.semio.color;

import org.apache.commons.logging.impl.Log4JLogger;
import org.junit.Assert;
import org.junit.Test;

public class ColorDifferenceTest {

	/**
	 * L*a*b* taken from http://www.brucelindbloom.com/ (D50 sRGB
	 * Bradford-adapted). Lambda = 0, Gamma = sRGB
	 */
	static final float[] black = new float[] { 0f, 0f, 0f };
	static final float[] blue = new float[] { 29.5676f, 68.2986f, -112.0294f };
	static final float[] green = new float[] { 87.8181f, -79.2873f, 80.9902f };
	static final float[] red = new float[] { 54.2917f, 80.8125f, 69.8851f };
	static final float[] cyan = new float[] { 90.6655f, -50.6654f, -14.9620f };
	static final float[] magenta = new float[] { 60.1697f, 93.5500f, -60.4986f };
	static final float[] yellow = new float[] { 97.6071f, -15.7529f, 93.3885f };
	static final float[] white = new float[] { 100f, 0f, 0f };

	private static final float eqDelta = 0.001f;

//	private final Log4JLogger logger = new Log4JLogger("Default");

	@Test
	public void deltaETest() {
//		logger.info("### Color difference tests");

		double de76, de94, de2000;

		float[][] colors = new float[][] { black, blue, green, red, cyan, magenta, yellow, white };
		double[][] expdE = new double[colors.length * colors.length][3];
		expdE[0] = new double[] { 134.4973, 134.4973, 38.452 }; // black-blue
		expdE[1] = new double[] { 143.3802, 143.3802, 87.8053 };// black - green
		expdE[2] = new double[] { 119.84229, 119.84229, 51.3411 };// black - red
		expdE[3] = new double[] { 104.933, 104.933, 89.9176 };// black - cyan
		expdE[4] = new double[] { 126.6178, 126.6178, 56.39465 };// black -
																// magenta
		expdE[5] = new double[] { 136.0026, 136.0026, 101.7567 };// black -
																	// yellow
		expdE[6] = new double[] { 100, 100, 100 };// black - white
		expdE[7] = new double[] { 249.8625, 100.3250, 86.2377 };// blue - green
		expdE[8] = new double[] { 184.0129, 65.8068, 55.7975 };// blue - red
		expdE[9] = new double[] { 165.249711, 76.423064, 68.394632 };// blue -
																		// cyan
		expdE[10] = new double[] { 65.034953, 35.693348, 36.381306 };// blue -
																		// magenta
		expdE[11] = new double[] { 232.1433, 100.4879, 101.7251 };// blue -
																	// yellow
		expdE[12] = new double[] { 148.916111, 72.95108, 66.916642 };// blue -
																		// white
		expdE[13] = new double[] { 163.949043, 68.206066, 84.315595 };// green -
																		// red
		expdE[14] = new double[] { 100.170582, 31.296575, 34.746495 };// green -
																		// cyan
		expdE[15] = new double[] { 225.069427, 87.220456, 108.591186 };// green
																		// -
																		// magenta
		expdE[16] = new double[] { 65.468789, 25.145621, 24.766593 };// green -
																		// yellow
		expdE[17] = new double[] { 113.9924, 22.216953, 32.766165 };// green -
																	// white
		expdE[18] = new double[] { 160.650309, 67.777436, 71.170128 };// red -
																		// cyan
		expdE[19] = new double[] { 131.1362, 50.653977, 43.251949 };// red -
																	// magenta
		expdE[20] = new double[] { 108.413607, 57.594223, 60.988315 };// red -
																		// yellow
		expdE[21] = new double[] { 116.2060, 49.271276, 45.263973 };// red -
																	// white
		expdE[22] = new double[] { 154.277858, 85.33285, 58.73103 };// cyan -
																	// magenta
		expdE[23] = new double[] { 114.047794, 60.741430, 43.1763 };// cyan -
																	// yellow
		expdE[24] = new double[] { 53.646781, 18.215779, 25.845217 };// cyan -
																		// white
		expdE[25] = new double[] { 192.431605, 79.773082, 89.816011 };// magenta
																		// -
																		// yellow
		expdE[26] = new double[] { 118.31371, 43.92827, 42.048382 };// magenta -
																	// white
		expdE[27] = new double[] { 94.738016, 18.157318, 30.281539 };// yellow -
																		// white

		int k = 0;
		for (int i = 0; i < colors.length - 1; i++) {
			for (int j = i + 1; j < colors.length; j++) {
				float[] a = colors[i];
				float[] b = colors[j];
				de76 = ColorDifference.deltaE(a, b, ColorDifference.CIE76);
				de94 = ColorDifference.deltaE(a, b, ColorDifference.CIE94);
				de2000 = ColorDifference.deltaE(a, b, ColorDifference.CIE2000);
//				logger.info("(calculated - expected) = (dE76=" + (de76 - expdE[k][0]) + ", dE94=" + (de94 - expdE[k][1])
//						+ ", dE2000=" + (de2000 - expdE[k][2]) + ")");
				Assert.assertEquals(expdE[k][0], de76, eqDelta);
				Assert.assertEquals(expdE[k][1], de94, eqDelta);
				Assert.assertEquals(expdE[k][2], de2000, eqDelta);
				k++;
			}
		}

	}

}
