package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

public interface IConic extends ICurveSegment {
  /** NON IMPLEMENTE. */
  public abstract IDirectPositionList coord();

  /** NON IMPLEMENTE. */
  public abstract ICurveSegment reverse();
}
