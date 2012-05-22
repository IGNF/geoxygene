package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

public interface IArcString extends ICurveSegment {

  public abstract int getNumArc();

  @Override
  public abstract IDirectPositionList coord();

  @Override
  public abstract ICurveSegment reverse();
}
