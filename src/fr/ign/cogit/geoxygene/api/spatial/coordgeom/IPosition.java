package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

public interface IPosition {
  public abstract IDirectPosition getDirect();

  public abstract IPointRef getIndirect();
}
