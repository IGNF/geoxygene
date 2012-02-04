package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

/**
 * A B-spline curve is a piecewise parametric polynomial or rational curve
 * described in terms of control points and basis functions. If the control
 * points are not homogeneous form or they are but the weights are all equal to
 * one another, then it a piecewise polynomial function. Otherwise, then it is a
 * rational function spline. A B-spline curve is a piecewise Bézier curve if it
 * is quasi-uniform except that the interior knots have multiplicity “degree”
 * rather than having multiplicity one. In this subtype the knot spacing shall
 * be 1.0, starting at 0.0. A piecewise Bézier curve that has only two knots,
 * 0.0, and 1.0, each of multiplicity (degree+1), is equivalent to a simple
 * Bézier curve.
 * @author Julien Perret
 */
public interface IBSplineCurve extends ISplineCurve {
}
