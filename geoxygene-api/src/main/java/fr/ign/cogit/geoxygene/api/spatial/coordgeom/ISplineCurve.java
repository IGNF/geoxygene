package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

import java.util.List;

/**
 * ISplineCurve acts as a root for subtypes of ICurveSegment using some version
 * of spline, either polynomial or rational functions.
 * @author Julien Perret
 */
public interface ISplineCurve extends ICurveSegment {
  @Override
  public IDirectPositionList coord();
  @Override
  public ICurveSegment reverse();
  /**
   * The attribute “degree” shall be the degree of the polynomials used for
   * defining the interpolation in this GM_SplineCurve. Rational splines will
   * have this degree for both the numerator and denominator of the rational
   * functions being used for the interpolation.
   * @return the degree of the curve.
   */
  public int getDegree();
  @Override
  public String getInterpolation();
  /**
   * The attribute “knot” shall be the array of distinct knots, each of which
   * will define a value in the parameter space of the spline, and will be used
   * to define the spline basis functions. The knot data type holds information
   * on knot multiplicity. Repetitions in the knot values will be distinguished
   * through use of this multiplicity, and so the parameter values in this array
   * will be strictly increasing.
   * @return the knots of the spline.
   */
  public List<? extends IKnot> getKnot();
}
