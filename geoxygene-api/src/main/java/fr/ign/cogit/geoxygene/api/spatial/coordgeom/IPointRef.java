package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;

public interface IPointRef {
  public abstract IPoint getPoint();

  public abstract int cardPoint();
}
