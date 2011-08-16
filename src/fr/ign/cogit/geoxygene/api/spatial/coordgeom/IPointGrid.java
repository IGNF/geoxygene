package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

public interface IPointGrid {
  public abstract IDirectPositionList getRow(int i);

  public abstract int cardRow();
}
