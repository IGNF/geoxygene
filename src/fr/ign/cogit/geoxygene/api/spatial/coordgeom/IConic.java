package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

public interface IConic extends ICurveSegment {
  /** NON IMPLEMENTE. */
  @Override
  public abstract IDirectPositionList coord();

  /** NON IMPLEMENTE. */
  @Override
  public abstract ICurveSegment reverse();
}
