package fr.ign.cogit.geoxygene.api.spatial.coordgeom;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public interface IArc extends IArcString {
  public IPosition getStartPoint();

  public void setStartPoint(IPosition startPoint);

  public IPosition getMidPoint();

  public void setMidPoint(IPosition midPoint);

  public IPosition getEndPoint();

  public void setEndPoint(IPosition endPoint);

  public IDirectPosition getCenter();

  public double getRadius();

  public double startOfArc();

  public double endOfArc();

  public double delta();

  @Override
  public IGeometry intersection(IGeometry geom);

  @Override
  public boolean contains(IGeometry geom);
}
