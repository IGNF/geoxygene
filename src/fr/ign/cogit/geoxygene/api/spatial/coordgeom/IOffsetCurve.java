package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

public interface IOffsetCurve extends ICurveSegment {
  public abstract ICurveSegment getBaseCurve();

  public abstract int cardBaseCurve();

  public abstract double getDistance();

  /**
   * Inutile en 2D.
   */
  /*
   * protected Vecteur refDirection; public Vecteur getRefDirection () { return
   * this.refDirection; }
   */
  /** NON IMPLEMENTE. */
  @Override
  public abstract IDirectPositionList coord();

  /** NON IMPLEMENTE. */
  @Override
  public abstract ICurveSegment reverse();
}
