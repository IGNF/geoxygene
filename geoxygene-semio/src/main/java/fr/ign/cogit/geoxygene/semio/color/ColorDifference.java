package fr.ign.cogit.geoxygene.semio.color;

/**
 * Color difference in CIEL*a*b*
 * 
 * @author Bertrand Duménieu
 *
 */
public class ColorDifference {

	/*
	 * Constants, etc
	 */
	private static final double[] CIE76_K_GRAPHICS = new double[] { 1d, 0.045d, 0.015d };

	public final static int CIE76 = 0;
	public final static int CIE94 = 1;
	public final static int CIE2000 = 2;

	static final double pow_25_7 = Math.pow(25, 7);

	/**
	 * COmpute the color difference DeltaE between two L*a*b* colors.
	 * 
	 * @param lab1
	 *            : a L*a*b color
	 * @param lab2
	 *            : another L*a*b color
	 * @param type
	 *            : delta e formula to use
	 * @return delta e between lab1 and lab2
	 */
	public static double deltaE(float[] lab1, float[] lab2, int type) {
		if (type == CIE76) {
			return de_CIE76(lab1, lab2);
		}
		if (type == CIE94) {
			return de_CIE94(lab1, lab2);
		}
		if (type == CIE2000) {
			return de_CIE2000(lab1, lab2);
		}
		return Double.NaN;
	}

	public static double deltaL(float[] lab1, float[] lab2, int type) {
		if (type == CIE76) {
			return dL_CIE76(lab1, lab2);
		}
		if (type == CIE94) {
			return dL_CIE94(lab1, lab2);
		}
		if (type == CIE2000) {
			return dL_CIE2000(lab1, lab2);
		}
		return Double.NaN;
	}

	public static double deltaC(float[] lab1, float[] lab2, int type) {
		if (type == CIE76) {
			return dC_CIE76(lab1, lab2);
		}
		if (type == CIE94) {
			return dC_CIE94(lab1, lab2);
		}
		if (type == CIE2000) {
			return dC_CIE2000(lab1, lab2);
		}
		return Double.NaN;
	}

	public static double deltaH(float[] lab1, float[] lab2, int type) {
		if (type == CIE76) {
			return dH_CIE76(lab1, lab2);
		}
		if (type == CIE94) {
			return dH_CIE94(lab1, lab2);
		}
		if (type == CIE2000) {
			return dH_CIE2000(lab1, lab2);
		}
		return Double.NaN;
	}

	private static double dL_CIE2000(float[] x, float[] y) {
		return y[0] - x[0];
	}

	private static double dC_CIE2000(float[] x, float[] y) {
		double a1 = x[1];
		double b1 = x[2];

		double a2 = y[1];
		double b2 = y[2];

		final double pow_25_7 = Math.pow(25, 7);

		double C1 = Math.sqrt(a1 * a1 + b1 * b1);
		double C2 = Math.sqrt(a2 * a2 + b2 * b2);

		double Cbar = 0.5 * (C1 + C2);
		double Cbarpow7 = Math.pow(Cbar, 7);

		double G = 0.5 * (1 - Math.sqrt(Cbarpow7 / (Cbarpow7 + pow_25_7)));

		double a1p = (1 + G) * a1;
		double a2p = (1 + G) * a2;

		double C1p = Math.sqrt(a1p * a1p + b1 * b1);
		double C2p = Math.sqrt(a2p * a2p + b2 * b2);

		return C2p - C1p;
	}

	private static double dH_CIE2000(float[] x, float[] y) {
		double a1 = x[1];
		double b1 = x[2];

		double a2 = y[1];
		double b2 = y[2];

		double C1 = Math.sqrt(a1 * a1 + b1 * b1);
		double C2 = Math.sqrt(a2 * a2 + b2 * b2);

		double Cbar = 0.5 * (C1 + C2);
		double Cbarpow7 = Math.pow(Cbar, 7);

		double G = 0.5 * (1 - Math.sqrt(Cbarpow7 / (Cbarpow7 + pow_25_7)));

		double a1p = (1 + G) * a1;
		double a2p = (1 + G) * a2;

		double h1p = Math.atan2(b1, a1p);
		h1p += (h1p < 0) ? 2 * Math.PI : 0;
		double h2p = Math.atan2(b2, a2p);
		h2p += (h2p < 0) ? 2 * Math.PI : 0;

		double C1p = Math.sqrt(a1p * a1p + b1 * b1);
		double C2p = Math.sqrt(a2p * a2p + b2 * b2);

		double dhp = h2p - h1p;
		dhp += (dhp > Math.PI) ? -2 * Math.PI : (dhp < -Math.PI) ? 2 * Math.PI : 0;
		dhp = (C1p * C2p) == 0 ? 0 : dhp;

		return 2 * Math.sqrt(C1p * C2p) * Math.sin(dhp / 2);
	}

	private static double de_CIE2000(float[] x, float[] y) {
		// Implementation of the DE2000 color difference defined in "The
		// CIEDE2000 Color-Difference Formula: Implementation Notes,
		// Supplementary Test Data, and Mathematical Observations" from Gaurav
		// Sharma, Wencheng Wu and Edul N. Dalal
		// Pdf available at :
		// http://www.ece.rochester.edu/~gsharma/ciede2000/ciede2000noteCRNA.pdf
		// (last checked 17/07/2016)
		double L1 = x[0];
		double a1 = x[1];
		double b1 = x[2];

		double L2 = y[0];
		double a2 = y[1];
		double b2 = y[2];

		double kl = 1, kc = 1, kh = 1;

		double C1 = Math.sqrt(a1 * a1 + b1 * b1);
		double C2 = Math.sqrt(a2 * a2 + b2 * b2);

		double Lpbar = (L1 + L2) / 2;

		double Cbar = 0.5 * (C1 + C2);
		double Cbarpow7 = Math.pow(Cbar, 7);

		double G = 0.5 * (1 - Math.sqrt(Cbarpow7 / (Cbarpow7 + pow_25_7)));

		double a1p = (1 + G) * a1;
		double a2p = (1 + G) * a2;

		double C1p = Math.sqrt(a1p * a1p + b1 * b1);
		double C2p = Math.sqrt(a2p * a2p + b2 * b2);

		double Cpbar = (C1p + C2p) / 2;

		double h1p = Math.atan2(b1, a1p);
		h1p += (h1p < 0) ? 2 * Math.PI : 0;
		double h2p = Math.atan2(b2, a2p);
		h2p += (h2p < 0) ? 2 * Math.PI : 0;

		double dhp = h2p - h1p;
		dhp += (dhp > Math.PI) ? -2 * Math.PI : (dhp < -Math.PI) ? 2 * Math.PI : 0;
		dhp = (C1p * C2p) == 0 ? 0 : dhp;

		double dLp = dL_CIE2000(x, y);
		double dCp = C2p - C1p;

		double dHp = 2 * Math.sqrt(C1p * C2p) * Math.sin(dhp / 2);

		double Hp = 0.5 * (h1p + h2p);
		Hp += Math.abs(h1p - h2p) > Math.PI ? Math.PI : 0;
		if (C1p * C2p == 0) {
			Hp = h1p + h2p;
		}
		double T = 1 - 0.17 * Math.cos(Hp - 0.523599) + 0.24 * Math.cos(2 * Hp) + 0.32 * Math.cos(3 * Hp + 0.10472)
				- 0.20 * Math.cos(4 * Hp - 1.09956);
		double Lpbarpow502 = (Lpbar - 50) * (Lpbar - 50);
		double Sl = 1 + 0.015 * Lpbarpow502 / Math.sqrt(20 + Lpbarpow502);
		double Sc = 1 + 0.045 * Cpbar;
		double Sh = 1 + 0.015 * Cpbar * T;
		double f = (Math.toDegrees(Hp) - 275) / 25;
		double dOmega = (30 * Math.PI / 180) * Math.exp(-(f * f));
		double Rc = 2 * Math.sqrt(Math.pow(Cpbar, 7) / (Math.pow(Cpbar, 7) + Math.pow(25, 7)));
		double RT = -1 * Math.sin(2 * dOmega) * Rc;

		dLp = dLp / (kl * Sl);
		dCp = dCp / (kc * Sc);
		dHp = dHp / (kh * Sh);

		return Math.sqrt(dLp * dLp + dCp * dCp + dHp * dHp + RT * dCp * dHp);
	}

	private static double dL_CIE94(float[] x, float[] y) {
		return x[0] - y[0];
	}

	private static double dC_CIE94(float[] x, float[] y) {
		double a1 = x[1];
		double b1 = x[2];
		double a2 = y[1];
		double b2 = y[2];

		double C1 = Math.sqrt(a1 * a1 + b1 * b1);
		double C2 = Math.sqrt(a2 * a2 + b2 * b2);

		return C1 - C2;

	}

	private static double dH_CIE94(float[] x, float[] y) {
		double a1 = x[1];
		double b1 = x[2];
		double a2 = y[1];
		double b2 = y[2];
		double dCab = dC_CIE94(x, y);
		double da = a1 - a2;
		double db = b1 - b2;
		return da * da + db * db - dCab * dCab;
	}

	private static double de_CIE94(float[] x, float[] y) {
		double a1 = x[1];
		double b1 = x[2];
		double dL = dL_CIE94(x, y);
		double C1 = Math.sqrt(a1 * a1 + b1 * b1);
		double dCab = dC_CIE94(x, y);
		double dHab2 = dH_CIE94(x, y);
		double SC = 1d + CIE76_K_GRAPHICS[1] * C1;
		double SH = 1d + CIE76_K_GRAPHICS[2] * C1;
		double f1 = dL / (CIE76_K_GRAPHICS[0]);
		double f2 = dCab / (SC);
		double f32 = dHab2 / (SH * SH);
		return Math.sqrt(f1 * f1 + f2 * f2 + f32);
	}

	private static double dL_CIE76(float[] x, float[] y) {
		return x[0] - y[0];
	}

	private static double dC_CIE76(float[] x, float[] y) {
		double a1 = x[1];
		double b1 = x[2];
		double a2 = y[1];
		double b2 = y[2];
		double C1 = Math.sqrt(a1 * a1 + b1 * b1);
		double C2 = Math.sqrt(a2 * a2 + b2 * b2);
		return C1 - C2;
	}

	private static double dH_CIE76(float[] x, float[] y) {
		double a1 = x[1];
		double b1 = x[2];
		double a2 = y[1];
		double b2 = y[2];

		double H1 = Math.toDegrees(Math.atan2(b1, a1));
		H1 += H1 < 0 ? 360 : 0;
		H1 -= H1 >= 360 ? 360 : 0;

		double H2 = Math.toDegrees(Math.atan2(b2, a2));
		H2 += H2 < 0 ? 360 : 0;
		H2 -= H2 >= 360 ? 360 : 0;

		return H1 - H2;
	}

	private static double de_CIE76(float[] x, float[] y) {
		double dl = y[0] - x[0];
		double da = y[1] - x[1];
		double db = y[2] - x[2];
		return Math.sqrt(dl * dl + da * da + db * db);
	}

}
