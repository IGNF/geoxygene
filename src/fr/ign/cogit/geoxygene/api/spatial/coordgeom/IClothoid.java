package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

public interface IClothoid extends ICurveSegment {
  /** NON IMPLEMENTE. */
  @Override
  public abstract IDirectPositionList coord();

  /** NON IMPLEMENTE. */
  @Override
  public abstract ICurveSegment reverse();
}
