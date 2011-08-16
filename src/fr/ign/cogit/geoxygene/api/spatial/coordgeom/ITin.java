package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

public interface ITin extends ITriangulatedSurface {
  public abstract ILineString getStopLines(int i);

  public abstract int cardStopLines();

  public abstract ILineString getBreakLines(int i);

  public abstract int cardBreakLines();

  public abstract double getMaxLength();

  public abstract IPosition getControlPoint(int i);

  public abstract int sizeControlPoint();
}
