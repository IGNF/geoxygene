package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

public interface ISplineCurve extends ICurveSegment {
  /** NON IMPLEMENTE. */
  public abstract IDirectPositionList coord();

  /** NON IMPLEMENTE. */
  public abstract ICurveSegment reverse();
}
