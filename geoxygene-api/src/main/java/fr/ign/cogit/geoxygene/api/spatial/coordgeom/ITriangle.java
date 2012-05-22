package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

public interface ITriangle extends IPolygon {
  public abstract IPosition getCorners(int i);

  public abstract IPosition[] getCorners();
  /*
   * public int cardCorners () { return this.corners.length; }
   */
}
