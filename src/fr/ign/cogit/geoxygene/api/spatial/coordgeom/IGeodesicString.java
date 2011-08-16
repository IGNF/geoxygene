package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

public interface IGeodesicString extends ICurveSegment {
  /** NON IMPLEMENTE. */
  public abstract IDirectPositionList coord();

  /** NON IMPLEMENTE. */
  public abstract ICurveSegment reverse();
}
