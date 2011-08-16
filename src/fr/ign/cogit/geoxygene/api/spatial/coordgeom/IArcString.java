package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

public interface IArcString extends ICurveSegment {

  public abstract int getNumArc();

  public abstract IDirectPositionList coord();

  public abstract ICurveSegment reverse();
}
