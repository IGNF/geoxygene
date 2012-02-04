package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

/**
 * IKnot is used to control the constructive parameter space for splines, curves, surfaces and solids.
 * @author Julien Perret
 */
public interface IKnot {
  /**
   * The attribute “value” is the value of the parameter at the knot of the
   * spline. The sequence of knots shall be a non-decreasing sequence. That is,
   * each knot's value in the sequence shall be equal to or greater than the
   * previous knot's value. The use of equal consecutive knots is normally
   * handled using the multiplicity.
   * @return the the value of the parameter at the knot of the spline
   */
  public double getValue();
  /**
   * The attribute “multiplicity” is the multiplicity of this knot used in the
   * definition of the spline.
   * @return the multiplicity of this knot used in the definition of the spline
   */
  public int getMultiplicity();
}
