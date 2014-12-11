package fr.ign.cogit.geoxygene.util.math;

/**
 * 
 * @author nmellado
 * 
 * @brief Static functions only, to compute the value of Quadratic or Cubic
 *        Bernstein polynomials.
 *
 */
public class BernsteinPolynomial {
    private BernsteinPolynomial() {
    }

    /**
     * Cubic bernstein polynom
     * 
     * @param i
     *            Coefficient id
     * @param t
     *            Evaluated value, must be in [0/1]
     * @return
     */
    static private float bernstein3(int i, float t) {
        switch (i) {
        case 0:
            return (1 - t) * (1 - t) * (1 - t);
        case 1:
            return 3 * t * (1 - t) * (1 - t);
        case 2:
            return 3 * t * t * (1 - t);
        case 3:
            return t * t * t;
        }
        return 0; // we only get here if an invalid i is specified
    }

    /**
     * Quadratic bernstein polynom
     * 
     * @param i
     *            Coefficient id
     * @param t
     *            Evaluated value, must be in [0/1]
     * @return
     */
    static private float bernstein2(int i, float t) {
        switch (i) {
        case 0:
            return (1 - t) * (1 - t);
        case 1:
            return 2 * t * (1 - t);
        case 2:
            return t * t;
        }
        return 0; // we only get here if an invalid i is specified
    }

    // evaluate a point on the B spline
    public static double evalQuadratic(double v0, double v1, double v2, float t) {
        return bernstein2(0, t) * v0 + bernstein2(1, t) * v1 + bernstein2(2, t)
                * v2;
    }

    // evaluate a point on the B spline
    public static double evalCubic(double v0, double v1, double v2, double v3,
            float t) {
        return bernstein3(0, t) * v0 + bernstein3(1, t) * v1 + bernstein3(2, t)
                * v2 + bernstein3(3, t) * v3;
    }
}
